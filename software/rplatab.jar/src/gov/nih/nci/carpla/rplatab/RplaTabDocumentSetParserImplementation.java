package gov.nih.nci.carpla.rplatab;

import gov.nih.nci.caarray.magetab.OntologyTerm;
import gov.nih.nci.caarray.magetab.idf.ExperimentalFactor;
import gov.nih.nci.caarray.validation.ValidationMessage;
import gov.nih.nci.caarray.validation.ValidationResult;
import gov.nih.nci.caarray.validation.ValidationMessage.Type;

import gov.nih.nci.carpla.rplatab.RplaConstants.SradfSectionType;
import gov.nih.nci.carpla.rplatab.files.ArrayDataFile;
import gov.nih.nci.carpla.rplatab.files.DerivedArrayDataFile;
import gov.nih.nci.carpla.rplatab.files.ImageFile;
import gov.nih.nci.carpla.rplatab.files.RplaIdfFile;
import gov.nih.nci.carpla.rplatab.files.SradfFile;
import gov.nih.nci.carpla.rplatab.model.Antibody;
import gov.nih.nci.carpla.rplatab.model.Assay;
import gov.nih.nci.carpla.rplatab.model.Characteristic;
import gov.nih.nci.carpla.rplatab.model.Comment;
import gov.nih.nci.carpla.rplatab.model.Dilution;

import gov.nih.nci.carpla.rplatab.model.FactorValue;
import gov.nih.nci.carpla.rplatab.model.HasAttribute;
import gov.nih.nci.carpla.rplatab.model.HasCharacteristics;
import gov.nih.nci.carpla.rplatab.model.HasComment;
import gov.nih.nci.carpla.rplatab.model.HasProvider;
import gov.nih.nci.carpla.rplatab.model.Protocol;
import gov.nih.nci.carpla.rplatab.model.Provider;
import gov.nih.nci.carpla.rplatab.model.RPLArray;
import gov.nih.nci.carpla.rplatab.model.Sample;
import gov.nih.nci.carpla.rplatab.model.Source;
import gov.nih.nci.carpla.rplatab.rplaidf.RplaIdfReader;
import gov.nih.nci.carpla.rplatab.sradf.ReplaceME;
import gov.nih.nci.carpla.rplatab.sradf.SradfHeader;
import gov.nih.nci.carpla.rplatab.sradf.SradfHeaderReader;
import gov.nih.nci.carpla.rplatab.sradf.SradfHeaders;
import gov.nih.nci.carpla.rplatab.sradf.SradfSectionHeaders;
import gov.nih.nci.carpla.rplatab.sradf.SradfSectionRowReader;
import gov.nih.nci.carpla.rplatab.sradf.javacc.generated.ParseException;

import java.io.PrintStream;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.SortedMap;
import java.util.Vector;

import org.apache.log4j.Logger;

public class RplaTabDocumentSetParserImplementation
													implements
													RplaTabDocumentSetParser {

	private static final Logger	LOG	= Logger.getLogger(RplaTabDocumentSetParserImplementation.class);

	// ####################################################################
	// ####################################################################
	public RplaTabDocumentSetParserImplementation() {

	}

	// ####################################################################
	// ####################################################################
	public ValidationResult validate ( RplaTabInputFileSet inputSet) {
		return parse(inputSet).getValidationResult();
	}

	// ####################################################################
	// ####################################################################
	public RplaTabDocumentSet parse ( RplaTabInputFileSet rplaTabInputFileSet)

	{
		RplaTabDocumentSet rplaTabDocumentSet = new RplaTabDocumentSet();

		rplaTabDocumentSet.setRplaTabInputFileSet(rplaTabInputFileSet);
		readRplaIdf(rplaTabDocumentSet, rplaTabInputFileSet);

		readSradfHeaders(rplaTabDocumentSet, rplaTabInputFileSet);

		parseSradfRows(rplaTabDocumentSet, rplaTabInputFileSet);

		return rplaTabDocumentSet;
	}

	// ####################################################################
	// ####################################################################
	private void readRplaIdf (	RplaTabDocumentSet RplaTabDocumentSet,
								RplaTabInputFileSet rplaTabInputFileSet)

	{

		RplaIdfReader rplaIdfReader = new RplaIdfReader();

		RplaIdfReader.readRplaIdfFile(RplaTabDocumentSet, rplaTabInputFileSet);

	}

	// ####################################################################
	// ####################################################################
	private void readSradfHeaders ( RplaTabDocumentSet RplaTabDocumentSet,
									RplaTabInputFileSet rplaTabInputFileSet)

	{

		// Only one SRADF file is supported, document this upstream

		try {
			SradfHeaderReader.readSradfHeaders(	RplaTabDocumentSet,
												RplaTabDocumentSet.getSradfFile());

		} catch (RplaTabParsingException rpe) {
			// rpe.printStackTrace();
			return;
		}

	}

	// ####################################################################
	// ####################################################################
	private void parseSradfRows (	RplaTabDocumentSet RplaTabDocumentSet,
									RplaTabInputFileSet rplaTabInputFileSet)

	{

		SradfHeaders sradfHeaders = RplaTabDocumentSet.getSradfHeaders();

		if (sradfHeaders == null) {
			return;
		}

		SradfFile sradfFile = RplaTabDocumentSet.getSradfFile();

		processSectionRows(	sradfHeaders.getSamplesSectionHeaders(),
							sradfFile,
							RplaTabDocumentSet);

		processSectionRows(	sradfHeaders.getArraySectionHeaders(),
							sradfFile,
							RplaTabDocumentSet);
		processSectionRows(	sradfHeaders.getArrayDataSectionHeaders(),
							sradfFile,
							RplaTabDocumentSet);

	}

	// ####################################################################
	// ####################################################################
	private void processSectionRows (	SradfSectionHeaders sectionPrincipalHeaders,
										SradfFile sradfFileHolder,
										RplaTabDocumentSet RplaTabDocumentSet)

	{

		int principalNodeSize = sectionPrincipalHeaders.getHeaders().size();

		SradfSectionRowReader rowReader = new SradfSectionRowReader(sradfFileHolder.getFile(),
																	sectionPrincipalHeaders.getSectionType());

		int correctNumberOfColumns = sectionPrincipalHeaders.getTotalNumberOfColumns();

		String[] values;

		int row_number_in_section = 1;
		while ((values = rowReader.nextRow()) != null) {
			// checkNumberOfColumns(values.length, correctNumberOfColumns);

			if (values[0].startsWith("#"))
				break;

			for (int current_index_in_principal_headers = 0; current_index_in_principal_headers < principalNodeSize; current_index_in_principal_headers++) {

				SradfHeader header = sectionPrincipalHeaders.getHeaders()
															.get(current_index_in_principal_headers);

				handlePrincipal(sectionPrincipalHeaders.getSectionType(),
								header,
								values,
								row_number_in_section,
								RplaTabDocumentSet);

			}

			row_number_in_section++;

			// establishLinks(RplaTabDocumentSet.getSamplesSectionPrincipalObjects());

		}
	}

	// ####################################################################
	// ####################################################################################################
	private void handlePrincipal (	RplaConstants.SradfSectionType sectionType,
									SradfHeader header,
									String[] rowValues,
									int row_number_in_section,
									RplaTabDocumentSet RplaTabDocumentSet)
	{

		if (rowValues.length < (header.getCol())) {

			// RplaTabDocumentSet.getMessages().add(new ValidationMessage(
			// "sectionrow=" + row_number_in_section
			// + "\t incorrect number of columns"));

		}

		switch (ReplaceME.getType(header.getValue())) {

		case SOURCENAME:

			handleSourceName(	sectionType,
								header,
								rowValues,
								row_number_in_section,
								RplaTabDocumentSet);
			break;

		case SAMPLENAME:

			handleSampleName(	sectionType,
								header,
								rowValues,
								row_number_in_section,
								RplaTabDocumentSet);
			break;

		case PROTOCOLREF:

			handleProtocolRef(	sectionType,
								header,
								rowValues,
								row_number_in_section,
								RplaTabDocumentSet);

			break;

		case FACTORVALUE:

			handleFactorValue(	sectionType,
								header,
								rowValues,
								row_number_in_section,
								RplaTabDocumentSet);
			break;

		case BLOCKROW:
			handleBlockRow(	sectionType,
							header,
							rowValues,
							row_number_in_section,
							RplaTabDocumentSet);

			break;

		case BLOCKCOLUMN:

			handleBlockColumn(	sectionType,
								header,
								rowValues,
								row_number_in_section,
								RplaTabDocumentSet);

			break;
		case ROW:
			handleRow(	sectionType,
						header,
						rowValues,
						row_number_in_section,
						RplaTabDocumentSet);

			break;
		case COLUMN:

			handleColumn(	sectionType,
							header,
							rowValues,
							row_number_in_section,
							RplaTabDocumentSet);

			break;

		case SAMPLEREF:

			handleSampleRef(sectionType,
							header,
							rowValues,
							row_number_in_section,
							RplaTabDocumentSet);

			break;

		case DILUTIONREF:

			handleDilutionRef(	sectionType,
								header,
								rowValues,
								row_number_in_section,
								RplaTabDocumentSet);

			break;

		case ARRAYREF:
			handleArrayRef(	sectionType,
							header,
							rowValues,
							row_number_in_section,
							RplaTabDocumentSet);

			break;
		case ANTIBODYREF:
			handleAntibodyRef(	sectionType,
								header,
								rowValues,
								row_number_in_section,
								RplaTabDocumentSet);

			break;

		case ASSAYNAME:

			handleAssayName(sectionType,
							header,
							rowValues,
							row_number_in_section,
							RplaTabDocumentSet);
			break;

		case IMAGEFILE:
			handleImageFile(sectionType,
							header,
							rowValues,
							row_number_in_section,
							RplaTabDocumentSet);

			break;
		case ARRAYDATAFILE:

			handleArrayDataFile(sectionType,
								header,
								rowValues,
								row_number_in_section,
								RplaTabDocumentSet);

			break;
		case DERIVEDARRAYDATAFILE:

			handleDerivedArrayDataFile(	sectionType,
										header,
										rowValues,
										row_number_in_section,
										RplaTabDocumentSet);

			break;

		default:
			break;

		}

	}

	// #############################################################################
	// #############################################################################

	private void handleAttribute (	HasAttribute obj,
									SradfHeader header,
									String[] rowValues,
									RplaConstants.SradfSectionType sectionType,
									int row_number_in_section,
									RplaTabDocumentSet RplaTabDocumentSet)
	{

		switch (ReplaceME.getType(header.getValue())) {

		case CHARACTERISTICS:

			handleCharacteristics(	(HasCharacteristics) obj,
									header,
									rowValues,
									sectionType,
									row_number_in_section,
									RplaTabDocumentSet);
			break;

		case PROVIDER:

			handleProvider(	(HasProvider) obj,
							header,
							rowValues,
							sectionType,
							row_number_in_section,
							RplaTabDocumentSet);
			break;

		case COMMENT:

			handleComment(	(HasComment) obj,
							header,
							rowValues,
							sectionType,
							row_number_in_section,
							RplaTabDocumentSet);
			break;

		default:

			break;

		}

	}

	// #############################################################################
	// #############################################################################

	private void handleDerivedArrayDataFile (	SradfSectionType sectionType,
												SradfHeader header,
												String[] rowValues,
												int row_number_in_section,
												RplaTabDocumentSet RplaTabDocumentSet)
	{
		String name = rowValues[header.getCol() - 1];

		if (checkEmpty(	name,
						header,
						rowValues,
						sectionType,
						row_number_in_section,
						RplaTabDocumentSet)) {

			return;

		}

		DerivedArrayDataFile dadfile = RplaTabDocumentSet.getOrCreateDerivedArrayDataFile(name);

		RplaTabDocumentSet	.getPrincipalObjectsBySectionAndRow(sectionType,
																row_number_in_section)
							.add(dadfile);

	}

	// #############################################################################
	// #############################################################################

	private void handleArrayDataFile (	SradfSectionType sectionType,
										SradfHeader header,
										String[] rowValues,
										int row_number_in_section,
										RplaTabDocumentSet RplaTabDocumentSet)
	{
		String name = rowValues[header.getCol() - 1];

		if (checkEmpty(	name,
						header,
						rowValues,
						sectionType,
						row_number_in_section,
						RplaTabDocumentSet)) {

			return;

		}

		ArrayDataFile adfile = RplaTabDocumentSet.getOrCreateArrayDataFile(name);

		RplaTabDocumentSet	.getPrincipalObjectsBySectionAndRow(sectionType,
																row_number_in_section)
							.add(adfile);

	}

	// #############################################################################
	// #############################################################################

	private void handleImageFile (	SradfSectionType sectionType,
									SradfHeader header,
									String[] rowValues,
									int row_number_in_section,
									RplaTabDocumentSet RplaTabDocumentSet)
	{

		String name = rowValues[header.getCol() - 1];
		if (checkEmpty(	name,
						header,
						rowValues,
						sectionType,
						row_number_in_section,
						RplaTabDocumentSet)) {

			return;

		}

		ImageFile ifile = RplaTabDocumentSet.getOrCreateImageFile(name);

		RplaTabDocumentSet	.getPrincipalObjectsBySectionAndRow(sectionType,
																row_number_in_section)
							.add(ifile);

	}

	// #############################################################################
	// #############################################################################

	private void handleAntibodyRef (	SradfSectionType sectionType,
										SradfHeader header,
										String[] rowValues,
										int row_number_in_section,
										RplaTabDocumentSet RplaTabDocumentSet)
	{
		String name = rowValues[header.getCol() - 1];
		if (checkEmpty(	name,
						header,
						rowValues,
						sectionType,
						row_number_in_section,
						RplaTabDocumentSet)) {

			return;

		}

		Antibody antibody = RplaTabDocumentSet.getAntibody(name);

		if (antibody == null) {

			// RplaTabDocumentSet.getMessages().add(new ValidationMessage(
			// "sectionrow=" + row_number_in_section
			// + "\tantibody with name="
			// + name
			// + "dne"));
		}

		RplaTabDocumentSet	.getPrincipalObjectsBySectionAndRow(sectionType,
																row_number_in_section)
							.add(antibody);

	}

	// #############################################################################
	// #############################################################################

	private void handleArrayRef (	SradfSectionType sectionType,
									SradfHeader header,
									String[] rowValues,
									int row_number_in_section,
									RplaTabDocumentSet RplaTabDocumentSet)
	{
		String name = rowValues[header.getCol() - 1];

		// this doesn;t have attributes here....
		if (checkEmpty(	name,
						header,
						rowValues,
						sectionType,
						row_number_in_section,
						RplaTabDocumentSet)) {

			return;

		}

		RPLArray rplarray = RplaTabDocumentSet.getRPLArray(name);

		if (rplarray == null) {

			// RplaTabDocumentSet.getMessages().add(new ValidationMessage(
			// "sectionrow=" + row_number_in_section
			// + "\trplarray with name="
			// + name
			// + "dne"));
		}

		RplaTabDocumentSet	.getPrincipalObjectsBySectionAndRow(sectionType,
																row_number_in_section)
							.add(rplarray);
	}

	// #############################################################################
	// #############################################################################

	private void handleDilutionRef (	SradfSectionType sectionType,
										SradfHeader header,
										String[] rowValues,
										int row_number_in_section,
										RplaTabDocumentSet RplaTabDocumentSet)
	{

		String name = rowValues[header.getCol() - 1];

		if (name.compareTo(RplaConstants.EMPTYFIELDSTRING) == 0) {
			return;
		}

		Dilution dilution = RplaTabDocumentSet.getDilution(name);

		if (dilution == null) {

			// RplaTabDocumentSet.getMessages().add(new ValidationMessage(
			// "sectionrow=" + row_number_in_section
			// + "\tdilutionref with name="
			// + name
			// + "dne"));
		}

		RplaTabDocumentSet	.getPrincipalObjectsBySectionAndRow(sectionType,
																row_number_in_section)
							.add(dilution);

	}

	// #############################################################################
	// #############################################################################

	private void handleSampleRef (	SradfSectionType sectionType,
									SradfHeader header,
									String[] rowValues,
									int row_number_in_section,
									RplaTabDocumentSet RplaTabDocumentSet)
	{

		String name = rowValues[header.getCol() - 1];

		if (name.compareTo(RplaConstants.EMPTYFIELDSTRING) == 0) {
			return;
		}

		Sample sample = RplaTabDocumentSet.getSample(name);

		if (sample == null) {

			RplaTabDocumentSet	.getValidationResult()
								.addMessage(RplaTabDocumentSet	.getSradfFile()
																.getFile(),
											Type.ERROR,
											"Cannot find sample with name=" + name);

		}

		RplaTabDocumentSet	.getPrincipalObjectsBySectionAndRow(sectionType,
																row_number_in_section)
							.add(sample);

	}

	// #############################################################################
	// #############################################################################

	private void handleColumn ( SradfSectionType sectionType,
								SradfHeader header,
								String[] rowValues,
								int row_number_in_section,
								RplaTabDocumentSet RplaTabDocumentSet)
	{

	}

	// #############################################################################
	// #############################################################################

	private void handleRow (	SradfSectionType sectionType,
								SradfHeader header,
								String[] rowValues,
								int row_number_in_section,
								RplaTabDocumentSet RplaTabDocumentSet)
	{

	}

	// #############################################################################
	// #############################################################################

	private void handleBlockColumn (	SradfSectionType sectionType,
										SradfHeader header,
										String[] rowValues,
										int row_number_in_section,
										RplaTabDocumentSet RplaTabDocumentSet)
	{

	}

	// #############################################################################
	// #############################################################################

	private void handleBlockRow (	SradfSectionType sectionType,
									SradfHeader header,
									String[] rowValues,
									int row_number_in_section,
									RplaTabDocumentSet RplaTabDocumentSet)
	{

	}

	// ####################################################################
	// #############################################################################

	private void handleComment (	HasComment obj,
									SradfHeader header,
									String[] rowValues,
									RplaConstants.SradfSectionType sectionType,
									int row_number_in_section,
									RplaTabDocumentSet RplaTabDocumentSet)
	{
		Comment comment = RplaTabDocumentSet.createComment();
		comment.setValue(rowValues[header.getCol() - 1]);

		obj.addComment(comment);

	}

	// ####################################################################
	// #############################################################################
	private void handleSourceName ( SradfSectionType sectionType,
									SradfHeader header,
									String[] rowValues,

									int row_number_in_section,
									RplaTabDocumentSet RplaTabDocumentSet)

	{

		String name = rowValues[header.getCol() - 1];

		if (checkEmpty(	name,
						header,
						rowValues,
						sectionType,
						row_number_in_section,
						RplaTabDocumentSet)) {

			return;

		}

		Source source = RplaTabDocumentSet.getOrCreateSource(name);

		RplaTabDocumentSet	.getPrincipalObjectsBySectionAndRow(sectionType,
																row_number_in_section)
							.add(source);

		List<SradfHeader> subheaders = header.getSubHeaders();

		for (int ii = 0; ii < subheaders.size(); ii++) {

			SradfHeader subheader = subheaders.get(ii);

			handleAttribute((HasAttribute) source,
							subheader,
							rowValues,
							sectionType,
							row_number_in_section,
							RplaTabDocumentSet);

		}

	}

	// ####################################################################
	// #############################################################################
	private void handleSampleName ( SradfSectionType sectionType,
									SradfHeader header,
									String[] rowValues,
									int row_number_in_section,

									RplaTabDocumentSet RplaTabDocumentSet)
	{

		String name = rowValues[header.getCol() - 1];

		if (checkEmpty(	name,
						header,
						rowValues,
						sectionType,
						row_number_in_section,
						RplaTabDocumentSet))
			return;

		Sample sample = RplaTabDocumentSet.getOrCreateSample(name);

		RplaTabDocumentSet	.getPrincipalObjectsBySectionAndRow(sectionType,
																row_number_in_section)
							.add(sample);

		List<SradfHeader> subheaders = header.getSubHeaders();

		for (int ii = 0; ii < subheaders.size(); ii++) {

			SradfHeader subheader = subheaders.get(ii);

			handleAttribute((HasAttribute) sample,
							subheader,
							rowValues,
							sectionType,
							row_number_in_section,
							RplaTabDocumentSet);

		}

	}

	// ####################################################################
	// #############################################################################
	private void handleCharacteristics (	HasCharacteristics hasCharacteristics,
											SradfHeader header,
											String[] rowValues,
											RplaConstants.SradfSectionType sectionType,
											int row_number_in_section,
											RplaTabDocumentSet rplaTabDocumentSet)
	{

		LOG.info("remember to fix Characteristic and address case where characteristic is measurement (???) ");

		String name = rowValues[header.getCol() - 1];

		if (checkEmpty(	name,
						header,
						rowValues,
						sectionType,
						row_number_in_section,
						rplaTabDocumentSet)) {
			return;
		}
		Characteristic characteristic = rplaTabDocumentSet.createCharacteristic();
		String qualifier = header.getTerm();

		OntologyTerm ot = rplaTabDocumentSet.createOntologyTerm(qualifier, name);
		characteristic.setTerm(ot);

		hasCharacteristics.getCharacteristics().add(characteristic);

		List<SradfHeader> subheaders = header.getSubHeaders();

		for (int ii = 0; ii < subheaders.size(); ii++) {

			SradfHeader subheader = subheaders.get(ii);

			handleAttribute((HasAttribute) characteristic,
							subheader,
							rowValues,
							sectionType,
							row_number_in_section,
							rplaTabDocumentSet);

		}

	}

	// #############################################################################
	private void handleAssayName (	SradfSectionType sectionType,
									SradfHeader header,
									String[] rowValues,
									int row_number_in_section,

									RplaTabDocumentSet rplaTabDocumentSet)
	{

		String name = rowValues[header.getCol() - 1];

		checkEmpty(	name,
					header,
					rowValues,
					sectionType,
					row_number_in_section,
					rplaTabDocumentSet);

		Assay assay = rplaTabDocumentSet.getOrCreateAssay(name);

		rplaTabDocumentSet	.getPrincipalObjectsBySectionAndRow(sectionType,
																row_number_in_section)
							.add(assay);

		List<SradfHeader> subheaders = header.getSubHeaders();

		for (int ii = 0; ii < subheaders.size(); ii++) {

			SradfHeader subheader = subheaders.get(ii);

			handleAttribute((HasAttribute) assay,
							subheader,
							rowValues,
							sectionType,
							row_number_in_section,
							rplaTabDocumentSet);

		}

	}

	// #############################################################################
	private void handleFactorValue (	SradfSectionType sectionType,
										SradfHeader header,
										String[] rowValues,
										int row_number_in_section,

										RplaTabDocumentSet rplaTabDocumentSet)
	{
		ExperimentalFactor ef = rplaTabDocumentSet	.getExperimentalFactors()
													.get(header.getTerm());
		if (ef == null) {

			rplaTabDocumentSet	.getValidationResult()
								.addMessage(rplaTabDocumentSet	.getSradfFile()
																.getFile(),
											Type.ERROR,
											"Cannot find experimental factor with name=" + header.getTerm());

		}

		FactorValue factorValue = rplaTabDocumentSet.createFactorValue();
		factorValue.setFactor(ef);

		rplaTabDocumentSet	.getPrincipalObjectsBySectionAndRow(sectionType,
																row_number_in_section)
							.add(factorValue);

		List<SradfHeader> subheaders = header.getSubHeaders();

		for (int ii = 0; ii < subheaders.size(); ii++) {

			SradfHeader subheader = subheaders.get(ii);

			handleAttribute((HasAttribute) factorValue,
							subheader,
							rowValues,
							sectionType,
							row_number_in_section,
							rplaTabDocumentSet);

		}

	}

	// #############################################################################
	private void handleProtocolRef (	SradfSectionType sectionType,
										SradfHeader header,
										String[] rowValues,
										int row_number_in_section,

										RplaTabDocumentSet rplaTabDocumentSet)
	{

		String name = rowValues[header.getCol() - 1];

		if (checkEmpty(	name,
						header,
						rowValues,
						sectionType,
						row_number_in_section,
						rplaTabDocumentSet)) {
			return;
		}

		Protocol protocol = rplaTabDocumentSet.getProtocol(name);

		if (protocol == null) {

			rplaTabDocumentSet	.getValidationResult()
								.addMessage(rplaTabDocumentSet	.getSradfFile()
																.getFile(),
											Type.ERROR,
											"Cannot find protocol with name=" + name);

			return;

		}

		rplaTabDocumentSet	.getPrincipalObjectsBySectionAndRow(sectionType,
																row_number_in_section)
							.add(protocol);

		List<SradfHeader> subheaders = header.getSubHeaders();

		for (int ii = 0; ii < subheaders.size(); ii++) {

			SradfHeader subheader = subheaders.get(ii);

			handleAttribute((HasAttribute) protocol,
							subheader,
							rowValues,
							sectionType,
							row_number_in_section,
							rplaTabDocumentSet);

		}
	}

	// #############################################################################
	private void handleProvider (	HasProvider hasprovider,
									SradfHeader header,
									String[] rowValues,
									RplaConstants.SradfSectionType sectionType,
									int row_number_in_section,
									RplaTabDocumentSet rplaTabDocumentSet)
	{

		String name = rowValues[header.getCol() - 1];

		checkEmpty(name, header, rowValues, sectionType,

		row_number_in_section, rplaTabDocumentSet);

		// todo : use Comparable below
		if (hasprovider.getProvider().getName().compareTo(name) != 0) {
			Provider provider = rplaTabDocumentSet.getOrCreateProvider(name);

			hasprovider.setProvider(provider);

			List<SradfHeader> subheaders = header.getSubHeaders();

			for (int ii = 0; ii < subheaders.size(); ii++) {

				SradfHeader subheader = subheaders.get(ii);

				handleAttribute((HasAttribute) provider,
								subheader,
								rowValues,
								sectionType,
								row_number_in_section,
								rplaTabDocumentSet);

			}

		}

		else {
			// Why change provider ? Specifying different providers for the same
			// source doesn;t make sense
			// RplaTabDocumentSet.getMessages().add(new ValidationMessage(
			// " why change provider midstream?"));

			// should stop here ?

		}

	}

	// #############################################################################
	// #############################################################################

	private boolean checkEmpty (	String name,
									SradfHeader header,
									String[] rowValues,
									SradfSectionType sectionType,
									int row_number_in_section,
									RplaTabDocumentSet RplaTabDocumentSet)
	{
		if (name.compareTo(RplaConstants.EMPTYFIELDSTRING) == 0) {

			return verifyAllSubHeadersAreEmpty(	header,
												rowValues,
												row_number_in_section,
												sectionType,
												RplaTabDocumentSet);

		}
		return false;
	}

	// #############################################################################
	// #############################################################################
	// carplatodo show correct column and header value
	private boolean verifyAllSubHeadersAreEmpty (	SradfHeader header,
													String[] rowValues,
													int row_number_in_section,
													SradfSectionType sectionType,
													RplaTabDocumentSet rplaTabDocumentSet)
	{

		List<SradfHeader> list = header.getSubHeaders();

		if (header.getSubHeaders().size() == 0) {

			if (rowValues[header.getCol() - 1].compareTo(RplaConstants.EMPTYFIELDSTRING) != 0) {

				rplaTabDocumentSet	.getValidationResult()
									.addMessage(rplaTabDocumentSet	.getSradfFile()
																	.getFile(),
												Type.ERROR,
												"sradf: section = " + sectionType
														+ " section row number = "
														+ row_number_in_section
														+ " colnum= "
														+ (header.getCol() - 1)

														+ " Non-empty attribute for an empty field");

				return false;
			}

		}

		for (SradfHeader subheader : list) {

			return (verifyAllSubHeadersAreEmpty(subheader,
												rowValues,
												row_number_in_section,
												sectionType,
												rplaTabDocumentSet));

		}

		return true;
	}
}
