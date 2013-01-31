//======================================================================================
// Copyright 5AM Solutions Inc, Yale University, Lawrence Berkeley National Laboratory
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/caRPLA/LICENSE for details.
//======================================================================================
package gov.nih.nci.carpla.rplatab.rplaidf;

import gov.nih.nci.caarray.magetab.MageTabOntologyCategory;
import gov.nih.nci.caarray.magetab.OntologyTerm;
import gov.nih.nci.caarray.magetab.TermSource;
import gov.nih.nci.caarray.magetab.idf.ExperimentalFactor;
import gov.nih.nci.caarray.magetab.idf.Publication;
import gov.nih.nci.caarray.validation.ValidationMessage.Type;
import gov.nih.nci.carpla.rplatab.RplaConstants;
import gov.nih.nci.carpla.rplatab.RplaTabDocumentSet;
import gov.nih.nci.carpla.rplatab.RplaTabInputFileSet;
import gov.nih.nci.carpla.rplatab.RplaTabParsingException;
import gov.nih.nci.carpla.rplatab.model.Antibody;
import gov.nih.nci.carpla.rplatab.model.Dilution;
import gov.nih.nci.carpla.rplatab.model.Parameter;
import gov.nih.nci.carpla.rplatab.model.Protocol;
import gov.nih.nci.carpla.rplatab.model.RplArray;
import gov.nih.nci.carpla.rplatab.rplaidf.javacc.generated.ParseException;
import gov.nih.nci.carpla.rplatab.rplaidf.javacc.generated.RplaDatasetRplaIdfGrammar;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.Reader;
import java.util.List;

public class RplaIdfReader {

	public static void readRplaIdfFile (	RplaTabDocumentSet rplaTabDocumentSet,
											RplaTabInputFileSet inputFileSet)

	{

		rplaTabDocumentSet.setRplaIdfFile(inputFileSet.getRplaIdfFile());

		java.io.Reader r = getReader(inputFileSet.getRplaIdfFile().getFile());
		RplaDatasetRplaIdfGrammar parser = new RplaDatasetRplaIdfGrammar(r);
		RplaIdfHelper helper = null;

		try {
			helper = parser.parse();
		} catch (ParseException pe) {
			pe.printStackTrace();

			rplaTabDocumentSet	.getValidationResult()
								.addMessage(rplaTabDocumentSet	.getRplaIdfFile()
																.getFile(),
											Type.ERROR,

											org.apache.commons.lang.StringEscapeUtils.escapeXml(pe.getMessage()));
			return;

		}
		try {
			// Load Term Sources first since they get referred to...
			handleTermSources(helper, rplaTabDocumentSet);

			handleInvestigationTitle(helper, rplaTabDocumentSet);
			handleExperimentalDesigns(helper, rplaTabDocumentSet);
			handleExperimentalFactors(helper, rplaTabDocumentSet);
			handleAntibodies(helper, rplaTabDocumentSet);
			handleRPLArrayNames(helper, rplaTabDocumentSet);
			handleDilutions(helper, rplaTabDocumentSet);
			handlePeople(helper, rplaTabDocumentSet);
			handleQualityControls(helper, rplaTabDocumentSet);
			handleReplicates(helper, rplaTabDocumentSet);
			handleNormalizations(helper, rplaTabDocumentSet);
			handleDateOfExperiment(helper, rplaTabDocumentSet);
			handlePublicReleaseDate(helper, rplaTabDocumentSet);
			handlePublications(helper, rplaTabDocumentSet);
			handleExperimentDescription(helper, rplaTabDocumentSet);
			handleProtocols(helper, rplaTabDocumentSet);
			handleSradfFile(helper, inputFileSet, rplaTabDocumentSet);
		} catch (RplaTabParsingException pe) {

			rplaTabDocumentSet	.getValidationResult()
								.addMessage(rplaTabDocumentSet	.getRplaIdfFile()
																.getFile(),
											Type.ERROR,

											pe.getMessage());

		}
	}

	private static void handleDilutions (	RplaIdfHelper helper,
											RplaTabDocumentSet rdataset)
	{
		List<String> dilutionNames = helper.getColumnStrings("Dilution");

		List<String> dilutionValues = helper.getColumnStrings("Dilution Value");

		List<String> dilutionUnits = helper.getColumnStrings("Dilution Unit");

		List<String> dilutionUnitTermSourceRefs = helper.getColumnStrings("Dilution Unit Term Source Ref");

		if (!verifySameNumberOfColumnsIfRowIsNotEmpty(	rdataset,
														"Dilution",
														true,
														dilutionNames,
														dilutionValues,
														dilutionUnits,
														dilutionUnitTermSourceRefs)) {
			return;
		}

		for (int ii = 0; ii < dilutionNames.size(); ii++) {

			String dilutionName = dilutionNames.get(ii);
			// verifyRPLArrayNameIsValid(Name);

			Dilution dil = rdataset.createDilution(dilutionName);

			if (dilutionValues	.get(ii)
								.compareTo(RplaConstants.EMPTYFIELDSTRING) == 0) {
				// dilution value may be unknown
				dil.setValue(RplaConstants.UNKNOWNDILUTIONVALUE);

			}

			else {
				try {

					dil.setValue(Float.valueOf(dilutionValues.get(ii)));

				} catch (NumberFormatException nfe) {

					rdataset.getValidationResult()
							.addMessage(rdataset.getRplaIdfFile().getFile(),
										Type.ERROR,

										"RPLAIDF: " + dilutionValues.get(ii)
												+ " is not valid as a dilution value.");
					return;
				}

			}
			dil.setUnit("x_times");

		}

	}

	private static void handleRPLArrayNames (	RplaIdfHelper helper,
												RplaTabDocumentSet rplaTabDocumentSet)
	{

		List<String> arrayNames = helper.getColumnStrings("RPLArray Name");

		verifyNoDuplicates(arrayNames);

		for (int ii = 0; ii < arrayNames.size(); ii++) {

			String arrayName = arrayNames.get(ii);
			verifyRPLArrayNameIsValid(arrayName);
			RplArray rarray = rplaTabDocumentSet.createRplArray(arrayName);

		}

	}

	private static void verifyRPLArrayNameIsValid ( String arrayName) {
	// TODO Auto-generated method stub

	}

	// ###############################################################
	private static void handleTermSources ( RplaIdfHelper helper,
											RplaTabDocumentSet rplaTabDocumentSet)
																					throws RplaTabParsingException

	{
		try {

			List<String> termSourceNames = helper.getColumnStrings("Term Source Name");
			List<String> termSourceFiles = helper.getColumnStrings("Term Source File");
			List<String> termSourceVersions = helper.getColumnStrings("Term Source Version");

			// Term Sources are only referenced by name in the SRADF document,
			// so names need to be unique.
			verifyNoDuplicates(termSourceNames);

			if (!verifySameNumberOfColumnsIfRowIsNotEmpty(	rplaTabDocumentSet,
															"Term Source",
															false,
															termSourceNames,
															termSourceFiles,
															termSourceVersions)) {
				return;
			}

			for (int ii = 0; ii < termSourceNames.size(); ii++) {

				String termSourceName = termSourceNames.get(ii);
				verifyTermSourceNameIsValid(termSourceName);
				TermSource termSource = rplaTabDocumentSet.createTermSource(termSourceName);

				String termSourceFile = termSourceFiles.get(ii);

				verifyTermSourceFileNameIsValid(termSourceFile);

				// termSource.setFileName(termSourceFile);
				termSource.setFile(termSourceFile);
			}
		} catch (Exception exc) {
			throw new RplaTabParsingException(exc.getMessage());
		}
	}

	// ###############################################################
	private static void handleInvestigationTitle (	RplaIdfHelper helper,
													RplaTabDocumentSet rplaTabDocumentSet)
																							throws RplaTabParsingException

	{

		List<String> strings = helper.getColumnStrings("Investigation Title");
		String title = strings.get(0);
		verifyInvestigationTitleIsValid(title);
		rplaTabDocumentSet.setInvestigationTitle(title);

	}

	// ###############################################################
	private static void handleExperimentalDesigns ( RplaIdfHelper helper,
													RplaTabDocumentSet rplaTabDocumentSet)

	{

		List<String> expDesigns = helper.getColumnStrings("Experimental Design");

		List<String> expDesignTermSourceRefs = helper.getColumnStrings("Experimental Design Term Source REF");

		verifyNoDuplicates(expDesigns);

		if (!verifySameNumberOfColumnsIfRowIsNotEmpty(	rplaTabDocumentSet,
														"Experimental Design",
														false,
														expDesigns,
														expDesignTermSourceRefs)) {
			return;
		}

		for (int ii = 0; ii < expDesigns.size(); ii++) {
			String designName = expDesigns.get(ii);
			String designNameTermSourceRef = expDesignTermSourceRefs.get(ii);
			verifyExperimentalDesignNameIsValid(designName);

			OntologyTerm designTerm = rplaTabDocumentSet.createOntologyTerm(MageTabOntologyCategory.EXPERIMENTAL_DESIGN_TYPE.getCategoryName(),
																			designName);
			designTerm.setValue(designName);

			TermSource termSource = rplaTabDocumentSet.getTermSource(designNameTermSourceRef);

			if (termSource == null) {

				rplaTabDocumentSet	.getValidationResult()
									.addMessage(rplaTabDocumentSet	.getSradfFile()
																	.getFile(),
												Type.ERROR,
												"Cannot find term source ref with name=" + designNameTermSourceRef);
			}

			designTerm.setTermSource(termSource);

			rplaTabDocumentSet.addExperimentalDesign(designTerm);

		}

	}

	private static void verifyExperimentalDesignNameIsValid ( String designName)
	{
	// TODO Auto-generated method stub

	}

	// ###############################################################
	private static void handleExperimentalFactors ( RplaIdfHelper helper,
													RplaTabDocumentSet rplaTabDocumentSet)

	{

		List<String> factorNames = helper.getColumnStrings("Experimental Factor Name");
		List<String> factorTypes = helper.getColumnStrings("Experimental Factor Type");
		List<String> factorTermSourceRefs = helper.getColumnStrings("Experimental Factor Term Source REF");

		verifyNoDuplicates(factorNames);

		if (!verifySameNumberOfColumnsIfRowIsNotEmpty(	rplaTabDocumentSet,
														"Experimental Factor",
														false,
														factorNames,
														factorTypes,
														factorTermSourceRefs)) {
			return;
		}

		for (int ii = 0; ii < factorNames.size(); ii++) {

			String name = factorNames.get(ii);
			verifyFactorNameIsValid(name);

			ExperimentalFactor factor = rplaTabDocumentSet.createExperimentalFactor(name);

			String type = factorTypes.get(ii);
			String termsourceref = factorTermSourceRefs.get(ii);

			TermSource termSource = rplaTabDocumentSet.getTermSource(termsourceref);
			OntologyTerm factorTypeOntologyTerm = rplaTabDocumentSet.createOntologyTerm(MageTabOntologyCategory.EXPERIMENTAL_FACTOR_CATEGORY.getCategoryName(),
																						type);

			factorTypeOntologyTerm.setValue(type);

			factorTypeOntologyTerm.setCategory(MageTabOntologyCategory.EXPERIMENTAL_FACTOR_CATEGORY.name());

			factorTypeOntologyTerm.setTermSource(termSource);

			verifyFactorTypeIsValid(factorTypeOntologyTerm, termSource);

			factor.setType(factorTypeOntologyTerm);

		}

	}

	// ###############################################################
	private static void verifyFactorTypeIsValid (	OntologyTerm factorTypeontologyTerm,
													TermSource termSource)
	{
	// TODO Auto-generated method stub

	}

	// ###############################################################
	private static void verifyFactorNameIsValid ( String name) {
	// TODO Auto-generated method stub

	}

	// ###############################################################
	private static void handleProtocols (	RplaIdfHelper helper,
											RplaTabDocumentSet RplaTabDocumentSet)

	{

		List<String> protocolNames = helper.getColumnStrings("Protocol Name");
		List<String> protocolTypes = helper.getColumnStrings("Protocol Type");
		List<String> protocolDescriptions = helper.getColumnStrings("Protocol Description");
		List<String> protocolParameters = helper.getColumnStrings("Protocol Parameters");
		List<String> protocolHardware = helper.getColumnStrings("Protocol Hardware");
		List<String> protocolSoftware = helper.getColumnStrings("Protocol Software");
		List<String> protocolContact = helper.getColumnStrings("Protocol Contact");
		List<String> protocolTermSourceRefs = helper.getColumnStrings("Protocol Term Source REF");

		verifyNoDuplicates(protocolNames);

		if (!verifySameNumberOfColumnsIfRowIsNotEmpty(	RplaTabDocumentSet,
														"Protocol",
														false,
														protocolNames,
														protocolTypes,
														protocolDescriptions,
														protocolParameters,
														protocolHardware,
														protocolSoftware,
														protocolContact,
														protocolTermSourceRefs)) {
			return;
		}

		for (int ii = 0; ii < protocolNames.size(); ii++) {

			// protocols should not be duplicated and we're reading them in
			// order
			String name = protocolNames.get(ii);
			if (RplaTabDocumentSet.protocolExists(name)) {

				// RplaTabDocumentSet.getMessages().add(new ValidationMessage(
				// " protocol already declared"));
				// shoudl stop here?

			}

			Protocol protocol = RplaTabDocumentSet.createProtocol(name);

			verifyProtocolNameIsValid(name);
			protocol.setName(name);

			String type = protocolTypes.get(ii);
			String termsourceref = protocolTermSourceRefs.get(ii);

			TermSource termSource = RplaTabDocumentSet.getTermSource(termsourceref);

			// protocol.setTermSource(termSource);

			OntologyTerm protocolTypeOntologyTerm = new OntologyTerm();
			protocolTypeOntologyTerm.setValue(type);

			verifyProtocolTypeIsValid(protocolTypeOntologyTerm, termSource);

			protocol.setType(protocolTypeOntologyTerm);

			String description = protocolDescriptions.get(ii);
			verifyProtocolDescriptionIsValid(description);
			protocol.setDescription(description);

			String parameters = protocolParameters.get(ii);
			// parse parameters
			String[] parameterNames = parameters.split(";");
			for (String element : parameterNames) {
				Parameter parameter = new Parameter();
				parameter.setName(element);
				protocol.getParameters().add(parameter);
			}

			String hardware = protocolHardware.get(ii);
			verifyProtocolHardwareIsValid(hardware);
			protocol.setHardware(hardware);

			String software = protocolSoftware.get(ii);
			verifyProtocolSoftwareIsValid(software);
			protocol.setSoftware(software);

			String contacts = protocolContact.get(ii);
			// todo

		}

	}

	// ###############################################################
	private static void verifyProtocolTermSourceRefIsValid ( String termsourceref)
	{
	// TODO Auto-generated method stub

	}

	// ###############################################################
	private static void handleAntibodies (	RplaIdfHelper helper,
											RplaTabDocumentSet rdataset)

	{

		List<String> antibodyNames = helper.getColumnStrings("Antibody Name");

		if (antibodyNames.size() == 0) {
			rdataset.getValidationResult()
					.addMessage(rdataset.getRplaIdfFile().getFile(),
								Type.ERROR,

								"RPLAIDF: Currently, at least one Antibody is required.");
			return;
		}

		List<String> targetGeneNames = helper.getColumnStrings("Target Gene Name");
		List<String> targetGeneNameTermSourceRefs = helper.getColumnStrings("Target Gene Name Term Source REF");
		List<String> antibodySpecificities = helper.getColumnStrings("Antibody Specificity");

		List<String> antibodyEpitopes = helper.getColumnStrings("Antibody Epitope");

		List<String> antibodyImmunogens = helper.getColumnStrings("Antibody Immunogen");

		List<String> antibodyProviders = helper.getColumnStrings("Antibody Provider");
		List<String> antibodyCatalogIDs = helper.getColumnStrings("Antibody Catalog ID");
		List<String> antibodyLotIDs = helper.getColumnStrings("Antibody Lot ID");
		List<String> antibodyComments = helper.getColumnStrings("Antibody Comment");

		verifyNoDuplicates(antibodyNames);

		if (!verifySameNumberOfColumnsIfRowIsNotEmpty(	rdataset,
														"Antibody",
														true,
														antibodyNames,
														targetGeneNames,
														targetGeneNameTermSourceRefs,
														antibodySpecificities,
														antibodyEpitopes,
														antibodyImmunogens,
														antibodyProviders,
														antibodyCatalogIDs,
														antibodyLotIDs,
														antibodyComments)) {
			return;
		}

		for (int ii = 0; ii < antibodyNames.size(); ii++) {
			String name = antibodyNames.get(ii);
			Antibody antibody = rdataset.createAntibody(name);

			// verifyAntibodyNameIsValid(name);
			antibody.setName(name);

			antibody.setLotId(get(antibodyLotIDs, ii));
			antibody.setCatalogId(get(antibodyCatalogIDs, ii));
			antibody.setComment(get(antibodyComments, ii));
			antibody.setEpitope(get(antibodyEpitopes, ii));
			antibody.setImmunogen(get(antibodyImmunogens, ii));
			antibody.setSpecificity(get(antibodySpecificities, ii));
			// carplatodo not setting gene names
			antibody.setProvider(get(antibodyProviders, ii));

		}
		// finish loading all antibody data

	}

	private static String get ( List<String> list_strings, int ii) {
		if (ii >= list_strings.size())
			return "";
		else
			return list_strings.get(ii);
	}

	// ###############################################################
	private static void handlePeople (	RplaIdfHelper helper,
										RplaTabDocumentSet RplaTabDocumentSet)

	{
	// TODO Auto-generated method stub

	}

	// ###############################################################
	private static void handleQualityControls ( RplaIdfHelper helper,
												RplaTabDocumentSet RplaTabDocumentSet)

	{
	// TODO Auto-generated method stub

	}

	// ###############################################################
	private static void handleReplicates (	RplaIdfHelper helper,
											RplaTabDocumentSet RplaTabDocumentSet)

	{
	// TODO Auto-generated method stub

	}

	// ###############################################################
	private static void handleNormalizations (	RplaIdfHelper helper,
												RplaTabDocumentSet RplaTabDocumentSet)

	{
	// TODO Auto-generated method stub

	}

	// ###############################################################
	private static void handleDateOfExperiment (	RplaIdfHelper helper,
													RplaTabDocumentSet RplaTabDocumentSet)

	{
	// TODO Auto-generated method stub

	}

	// ###############################################################
	private static void handlePublicReleaseDate (	RplaIdfHelper helper,
													RplaTabDocumentSet RplaTabDocumentSet)

	{
	// TODO Auto-generated method stub

	}

	// ###############################################################
	private static void handlePublications (	RplaIdfHelper helper,
												RplaTabDocumentSet rplaTabDocumentSet)

	{

		List<String> pubMedIds = helper.getColumnStrings("PubMed ID");

		List<String> pubDOIs = helper.getColumnStrings("Publication DOI");

		List<String> pubAuthors = helper.getColumnStrings("Publication Author List");

		List<String> pubTitles = helper.getColumnStrings("Publication Title");

		List<String> pubStatuses = helper.getColumnStrings("Publication Status");

		List<String> pubStatusTermSourceRefs = helper.getColumnStrings("Publication Status Term Source REF");

		verifyNoDuplicates(pubMedIds);

		if (!verifySameNumberOfColumnsIfRowIsNotEmpty(	rplaTabDocumentSet,
														"Publication",
														false,
														pubMedIds,
														pubDOIs,
														pubAuthors,
														pubTitles,
														pubStatuses,
														pubStatusTermSourceRefs)) {

			return;

		}

		for (int ii = 0; ii < pubMedIds.size(); ii++) {

			String pubMedId = pubMedIds.get(ii);
			String pubDOI = pubDOIs.get(ii);
			String pubAuthor = pubAuthors.get(ii);
			String pubTitle = pubTitles.get(ii);
			String pubStatus = pubStatuses.get(ii);
			String pubStatusTermSourceRef = pubStatusTermSourceRefs.get(ii);

			gov.nih.nci.caarray.magetab.idf.Publication pub = new Publication();

			pub.setPubMedId(pubMedId);
			pub.setDoi(pubDOI);
			// need to parse author list
			pub.setAuthorList(pubAuthor);
			pub.setTitle(pubTitle);
			OntologyTerm pubStatus_ot = new OntologyTerm();
			pubStatus_ot.setValue(pubStatus);

			TermSource termSource = rplaTabDocumentSet.getTermSource(pubStatusTermSourceRef);

			if (termSource == null) {

				rplaTabDocumentSet	.getValidationResult()
									.addMessage(rplaTabDocumentSet	.getSradfFile()
																	.getFile(),
												Type.ERROR,
												"Cannot find term source ref with name=" + pubStatusTermSourceRef);
			}

			pubStatus_ot.setTermSource(termSource);

			rplaTabDocumentSet.addPublication(pub);

		}

	}

	// ###############################################################
	private static void handleExperimentDescription (	RplaIdfHelper helper,
														RplaTabDocumentSet rplaTabDocumentSet)

	{
		rplaTabDocumentSet.setExperimentDescription(helper	.getColumnStrings("Experiment Description")
															.get(0));

	}

	// ###############################################################
	// only look for it in current directory...
	private static void handleSradfFile (	RplaIdfHelper helper,
											RplaTabInputFileSet inputFileSet,
											RplaTabDocumentSet rplaTabDocumentSet)

	{

		// filehandling todo do for windows.
		try {
			List<String> sradfFiles = helper.getColumnStrings("SRADF File");

			// SradfFile sradfFile = new SradfFile();
			// String filename = sradfFiles.get(0);
			//
			// File file = new File(rplaIdffileDirectory.getAbsolutePath() + "/"
			// + filename);

			// sradfFile.setFile(file);

			// huh?
			rplaTabDocumentSet.setSradfFile(inputFileSet.getSradfFile());

		} catch (Exception e) {
			e.printStackTrace();
			// throw new InvalidRplaTabException(new ValidationResult());
		}

	}

	// ###############################################################
	private static void verifyTermSourceFileNameIsValid ( String termSourceFile)
	{
	// TODO Auto-generated method stub

	}

	// ###############################################################
	private static void verifyTermSourceNameIsValid ( String termSourceName)

	{
	// TODO Auto-generated method stub

	}

	// ###############################################################
	private static void verifyNoDuplicates (	List<String> termSourceNames,
												List<String> termSourceVersions)

	{
	// TODO Auto-generated method stub

	}

	// ###############################################################
	private static void verifyInvestigationTitleIsValid ( String title) {
	// TODO Auto-generated method stub

	}

	// ###############################################################
	private static boolean verifySameNumberOfColumnsIfRowIsNotEmpty (	RplaTabDocumentSet rdataset,
																		String sectionName,
																		boolean required,
																		List<String>... listOfStrings)

	{

		int firstrowsize = listOfStrings[0].size();

		if (firstrowsize == 0 && required) {

			rdataset.getValidationResult()
					.addMessage(rdataset.getRplaIdfFile().getFile(),
								Type.ERROR,

								"RPLAIDF: Zero columns for " + sectionName
										+ " is not supported.");
			return false;
		}

		else if (firstrowsize == 0) {
			return false;

		}

		for (List<String> list : listOfStrings) {

			if (firstrowsize != list.size() && list.size() != 0) {

				rdataset.getValidationResult()
						.addMessage(rdataset.getRplaIdfFile().getFile(),
									Type.ERROR,

									"RPLAIDF: Inconsistent number of columns in " + sectionName);

				return false;

			}

		}
		return true;
	}

	// ###############################################################
	private static void verifyNoDuplicates ( List<String> strings)

	{

	// todo

	}

	// ###############################################################
	private static void verifyProtocolSoftwareIsValid ( String software)

	{
	// TODO Auto-generated method stub

	}

	// ###############################################################
	private static void verifyProtocolHardwareIsValid ( String hardware)

	{
	// TODO Auto-generated method stub

	}

	// ###############################################################
	private static void verifyProtocolDescriptionIsValid ( String description)

	{
	// TODO Auto-generated method stub

	}

	// ###############################################################
	private static void verifyProtocolTypeIsValid ( OntologyTerm protocolTypeOntologyTerm,
													TermSource termSource)

	{
	// TODO Auto-generated method stub

	}

	// ###############################################################
	private static void verifyProtocolNameIsValid ( String name)

	{
	// TODO Auto-generated method stub

	}

	// ###############################################################
	private static Reader getReader ( File rplaidffile)

	{

		try {

			String str = null;
			BufferedReader in = new BufferedReader(new FileReader(rplaidffile.getAbsolutePath()));

			return in;

		} catch (Exception e) {
			e.printStackTrace();

		}

		// TODO proper exception handling
		return null;

	}

}
