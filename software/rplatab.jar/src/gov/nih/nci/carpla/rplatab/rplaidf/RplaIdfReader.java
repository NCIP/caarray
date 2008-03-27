package gov.nih.nci.carpla.rplatab.rplaidf;



import gov.nih.nci.caarray.magetab.TermSource;
import gov.nih.nci.carpla.rplatab.RplaTabDocumentSet;
import gov.nih.nci.carpla.rplatab.RplaTabInputFileSet;


import gov.nih.nci.carpla.rplatab.model.Antibody;
import gov.nih.nci.carpla.rplatab.model.Dilution;
import gov.nih.nci.carpla.rplatab.model.ExperimentalFactor;
import gov.nih.nci.carpla.rplatab.model.OntologyTerm;
import gov.nih.nci.carpla.rplatab.model.Protocol;
import gov.nih.nci.carpla.rplatab.model.RPLArray;

import gov.nih.nci.carpla.rplatab.files.RplaIdfFile;
import gov.nih.nci.carpla.rplatab.files.SradfFile;

import gov.nih.nci.carpla.rplatab.rplaidf.javacc.generated.ParseException;
import gov.nih.nci.carpla.rplatab.rplaidf.javacc.generated.RplaDatasetRplaIdfGrammar;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.Reader;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

public class RplaIdfReader {

	public static void readRplaIdfFile (	RplaTabDocumentSet RplaTabDocumentSet,
	                                    	RplaTabInputFileSet inputFileSet
											)

	{

		java.io.Reader r = getReader(inputFileSet.getRplaIdfFile().getFile());
		RplaDatasetRplaIdfGrammar parser = new RplaDatasetRplaIdfGrammar(r);
		RplaIdfHelper helper = null;

		try {
			helper = parser.parse();
		} catch (ParseException pe) {

			pe.printStackTrace();

			// throw new InvalidRplaTabException(new ValidationResult());
		}

		RplaTabDocumentSet.setRplaIdfFile(inputFileSet.getRplaIdfFile());

		// Load Term Sources first since they get referred to...
		handleTermSources(helper, RplaTabDocumentSet);

		handleInvestigationTitle(helper, RplaTabDocumentSet);
		handleExperimentalDesigns(helper, RplaTabDocumentSet);
		handleExperimentalFactors(helper, RplaTabDocumentSet);
		handleAntibodies(helper, RplaTabDocumentSet);
		handleRPLArrayNames(helper, RplaTabDocumentSet);
		handleDilutions(helper, RplaTabDocumentSet);
		handlePeople(helper, RplaTabDocumentSet);
		handleQualityControls(helper, RplaTabDocumentSet);
		handleReplicates(helper, RplaTabDocumentSet);
		handleNormalizations(helper, RplaTabDocumentSet);
		handleDateOfExperiment(helper, RplaTabDocumentSet);
		handlePublicReleaseDate(helper, RplaTabDocumentSet);
		handlePublications(helper, RplaTabDocumentSet);
		handleExperimentDescription(helper, RplaTabDocumentSet);
		handleProtocols(helper, RplaTabDocumentSet);
		handleSradfFile(helper,
		                inputFileSet,
						RplaTabDocumentSet);

		
		
		
		
	}

	private static void handleDilutions (	RplaIdfHelper helper,
											RplaTabDocumentSet RplaTabDocumentSet)
	{
		Vector<String> dilutionNames = helper.getColumnStrings("Dilution");
		Vector<String> dilutionValues = helper
				.getColumnStrings("Dilution Value");

		Vector<String> dilutionUnits = helper.getColumnStrings("Dilution Unit");

		Vector<String> dilutionUnitTermSourceRefs = helper
				.getColumnStrings("Dilution Unit Term Source Ref");
		
		
		for (int ii = 0; ii < dilutionNames.size(); ii++) {

			String dilutionName = dilutionNames.get(ii);
			//verifyRPLArrayNameIsValid(Name);
		Dilution dil= 	RplaTabDocumentSet.createDilution(dilutionName);

		}
		
		
		
		
		
		
		
		
		
		
		

	}

	private static void handleRPLArrayNames (	RplaIdfHelper helper,
												RplaTabDocumentSet RplaTabDocumentSet)
	{

		Vector<String> arrayNames = helper.getColumnStrings("RPLArray Name");

		verifyNoDuplicates(arrayNames);

		for (int ii = 0; ii < arrayNames.size(); ii++) {

			String arrayName = arrayNames.get(ii);
			verifyRPLArrayNameIsValid(arrayName);
			RPLArray rarray = RplaTabDocumentSet.createRPLArray(arrayName);

		}

	}

	private static void verifyRPLArrayNameIsValid ( String arrayName) {
	// TODO Auto-generated method stub

	}

	// ###############################################################
	private static void handleTermSources ( RplaIdfHelper helper,
											RplaTabDocumentSet RplaTabDocumentSet)

	{

		Vector<String> termSourceNames = helper
				.getColumnStrings("Term Source Name");
		Vector<String> termSourceFiles = helper
				.getColumnStrings("Term Source File");
		Vector<String> termSourceVersions = helper
				.getColumnStrings("Term Source Version");

		// Term Sources are only referenced by name in the SRADF document,
		// so names need to be unique.
		verifyNoDuplicates(termSourceNames);

		verifySameNumberOfColumnsIfRowIsNotEmpty(	RplaTabDocumentSet,
													"Term Source",
													termSourceNames,
													termSourceFiles,
													termSourceVersions);

		for (int ii = 0; ii < termSourceNames.size(); ii++) {

			String termSourceName = termSourceNames.get(ii);
			verifyTermSourceNameIsValid(termSourceName);
			TermSource termSource = RplaTabDocumentSet
					.createTermSource(termSourceName);

			String termSourceFile = termSourceFiles.get(ii);

			verifyTermSourceFileNameIsValid(termSourceFile);

			//termSource.setFileName(termSourceFile);
			termSource.setFile(termSourceFile);
		}

	}

	// ###############################################################
	private static void handleInvestigationTitle (	RplaIdfHelper helper,
													RplaTabDocumentSet RplaTabDocumentSet)

	{

		Vector<String> strings = helper.getColumnStrings("Investigation Title");
		String title = strings.get(0);
		verifyInvestigationTitleIsValid(title);
		RplaTabDocumentSet.setInvestigationTitle(title);

	}

	// ###############################################################
	private static void handleExperimentalDesigns ( RplaIdfHelper helper,
													RplaTabDocumentSet RplaTabDocumentSet)

	{

	// todo

	}

	// ###############################################################
	private static void handleExperimentalFactors ( RplaIdfHelper helper,
													RplaTabDocumentSet RplaTabDocumentSet)

	{

		Vector<String> factorNames = helper
				.getColumnStrings("Experimental Factor Name");
		Vector<String> factorTypes = helper
				.getColumnStrings("Experimental Factor Type");
		Vector<String> factorTermSourceRefs = helper
				.getColumnStrings("Experimental Factor Term Source REF");

		verifyNoDuplicates(factorNames);

		verifySameNumberOfColumnsIfRowIsNotEmpty(	RplaTabDocumentSet,
													"Experimental Factor",
													factorNames,
													factorTypes,
													factorTermSourceRefs);

		for (int ii = 0; ii < factorNames.size(); ii++) {

			String name = factorNames.get(ii);
			verifyFactorNameIsValid(name);

			ExperimentalFactor factor = RplaTabDocumentSet
					.createExperimentalFactor(name);

			String type = factorTypes.get(ii);
			String termsourceref = factorTermSourceRefs.get(ii);

			TermSource termSource = RplaTabDocumentSet.getTermSource(termsourceref);

			OntologyTerm factorTypeOntologyTerm = new OntologyTerm();
			factorTypeOntologyTerm.setValue(type);

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

		Vector<String> protocolNames = helper.getColumnStrings("Protocol Name");
		Vector<String> protocolTypes = helper.getColumnStrings("Protocol Type");
		Vector<String> protocolDescriptions = helper
				.getColumnStrings("Protocol Description");
		Vector<String> protocolParameters = helper
				.getColumnStrings("Protocol Parameters");
		Vector<String> protocolHardware = helper
				.getColumnStrings("Protocol Hardware");
		Vector<String> protocolSoftware = helper
				.getColumnStrings("Protocol Software");
		Vector<String> protocolContact = helper
				.getColumnStrings("Protocol Contact");
		Vector<String> protocolTermSourceRefs = helper
				.getColumnStrings("Protocol Term Source REF");

		verifyNoDuplicates(protocolNames);

		verifySameNumberOfColumnsIfRowIsNotEmpty(	RplaTabDocumentSet,
													"Protocol",
													protocolNames,
													protocolTypes,
													protocolDescriptions,
													protocolParameters,
													protocolHardware,
													protocolSoftware,
													protocolContact,
													protocolTermSourceRefs);

		for (int ii = 0; ii < protocolNames.size(); ii++) {

			// protocols should not be duplicated and we're reading them in
			// order
			String name = protocolNames.get(ii);
			if (RplaTabDocumentSet.protocolExists(name)) {

//				RplaTabDocumentSet.getMessages().add(new ValidationMessage(
//						" protocol already declared"));
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
			// protocol.addParameter

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
											RplaTabDocumentSet RplaTabDocumentSet)

	{

		Vector<String> antibodyNames = helper.getColumnStrings("Antibody Name");
		Vector<String> targetGeneNames = helper
				.getColumnStrings("Target Gene Name");
		Vector<String> targetGeneNameTermSourceRefs = helper
				.getColumnStrings("Target Gene Name Term Source REF");
		Vector<String> antibodySpecificities = helper
				.getColumnStrings("Antibody Specificity");

		Vector<String> antibodyEpitopes = helper
				.getColumnStrings("Antibody Epitope");

		Vector<String> antibodyImmunogens = helper
				.getColumnStrings("Antibody Immunogen");

		Vector<String> antibodyProviders = helper
				.getColumnStrings("Antibody Provider");
		Vector<String> antibodyCatalogIDs = helper
				.getColumnStrings("Antibody Catalog ID");
		Vector<String> antibodyLotIDs = helper
				.getColumnStrings("Antibody Lot ID");
		Vector<String> antibodyComments = helper
				.getColumnStrings("Antibody Comment");

		verifyNoDuplicates(antibodyNames);

		verifySameNumberOfColumnsIfRowIsNotEmpty(	RplaTabDocumentSet,
													"Antibody",
													antibodyNames,
													targetGeneNames,
													targetGeneNameTermSourceRefs,
													antibodySpecificities,
													antibodyEpitopes,
													antibodyImmunogens,
													antibodyProviders,
													antibodyCatalogIDs,
													antibodyLotIDs,
													antibodyComments);

		for (int ii = 0; ii < antibodyNames.size(); ii++) {
			String name = antibodyNames.get(ii);
			Antibody antibody = RplaTabDocumentSet.createAntibody(name);

			// verifyAntibodyNameIsValid(name);
			antibody.setName(name);

			// finish loading all antibody data

		}
		// finish loading all antibody data

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
												RplaTabDocumentSet RplaTabDocumentSet)

	{
	// TODO Auto-generated method stub

	}

	// ###############################################################
	private static void handleExperimentDescription (	RplaIdfHelper helper,
														RplaTabDocumentSet RplaTabDocumentSet)

	{
	// TODO Auto-generated method stub

	}

	// ###############################################################
	// only look for it in current directory...
	private static void handleSradfFile (	RplaIdfHelper helper,
	                                     	RplaTabInputFileSet inputFileSet,
											RplaTabDocumentSet RplaTabDocumentSet)

	{

		// filehandling todo do for windows.
		try {
			Vector<String> sradfFiles = helper.getColumnStrings("SRADF File");

			// SradfFile sradfFile = new SradfFile();
			// String filename = sradfFiles.get(0);
			//
			// File file = new File(rplaIdffileDirectory.getAbsolutePath() + "/"
			// + filename);

		
		//	sradfFile.setFile(file);

			
			RplaTabDocumentSet.setSradfFile(inputFileSet.getSradfFile());

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
	private static void verifyNoDuplicates (	Vector<String> termSourceNames,
												Vector<String> termSourceVersions)

	{
	// TODO Auto-generated method stub

	}

	// ###############################################################
	private static void verifyInvestigationTitleIsValid ( String title) {
	// TODO Auto-generated method stub

	}

	// ###############################################################
	private static void verifySameNumberOfColumnsIfRowIsNotEmpty (	RplaTabDocumentSet rdataset,
																	String sectionName,
																	List<String>... listOfStrings)

	{

		// RplaTabDocumentSet.getMessages().add(new ValidationMessage(
		// "Parsing sradf file: \t" + " Protocol with name =\""
		// + name
		// + "\" in section row="
		// + row_number_in_section
		// + " and col="
		// + (header.getCol() - 1)
		// + " does not exist"));
		//		

		int firstrowsize = listOfStrings[0].size();

		for (List<String> list : listOfStrings) {

			if (list.size() != 0 && firstrowsize != list.size()) {
//				rdataset
//						.getMessages()
//						.add(new ValidationMessage(
//								"Parsing rplaidf:  not same number of columns for : " + sectionName));
			}

		}

	}

	// ###############################################################
	private static void verifyNoDuplicates ( Vector<String> strings)

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
			BufferedReader in = new BufferedReader(new FileReader(rplaidffile
					.getAbsolutePath()));

			return in;

		} catch (Exception e) {
			e.printStackTrace();

		}

		// TODO proper exception handling
		return null;

	}

}
