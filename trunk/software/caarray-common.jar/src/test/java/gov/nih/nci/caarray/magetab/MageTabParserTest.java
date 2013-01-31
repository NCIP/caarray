//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.magetab;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import gov.nih.nci.caarray.AbstractCaarrayTest;
import gov.nih.nci.caarray.domain.project.ExperimentOntologyCategory;
import gov.nih.nci.caarray.magetab.idf.ExperimentalFactor;
import gov.nih.nci.caarray.magetab.idf.IdfDocument;
import gov.nih.nci.caarray.magetab.idf.Investigation;
import gov.nih.nci.caarray.magetab.idf.Person;
import gov.nih.nci.caarray.magetab.idf.Publication;
import gov.nih.nci.caarray.magetab.sdrf.AbstractBioMaterial;
import gov.nih.nci.caarray.magetab.sdrf.ArrayDataFile;
import gov.nih.nci.caarray.magetab.sdrf.ArrayDesign;
import gov.nih.nci.caarray.magetab.sdrf.DerivedArrayDataFile;
import gov.nih.nci.caarray.magetab.sdrf.Hybridization;
import gov.nih.nci.caarray.magetab.sdrf.LabeledExtract;
import gov.nih.nci.caarray.magetab.sdrf.Sample;
import gov.nih.nci.caarray.magetab.sdrf.SdrfDocument;
import gov.nih.nci.caarray.magetab.sdrf.Source;
import gov.nih.nci.caarray.test.data.magetab.MageTabDataFiles;
import gov.nih.nci.caarray.validation.FileValidationResult;
import gov.nih.nci.caarray.validation.InvalidDataException;
import gov.nih.nci.caarray.validation.ValidationMessage;
import gov.nih.nci.caarray.validation.ValidationResult;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.junit.Test;

/**
 * Tests for the MageTabParser subsystem.
 */
@SuppressWarnings("PMD")
public class MageTabParserTest extends AbstractCaarrayTest {

    private static final int ONE = 1;
    private static final int SIX = 6;
    private MageTabParser parser = MageTabParser.INSTANCE;

    private static final Map<String, Protocol> CAARRAY1X_EXPECTED_PROTOCOLS = new HashMap<String, Protocol>();
    private static final Map<String, TermSource> CAARRAY1X_EXPECTED_TERM_SOURCES = new HashMap<String, TermSource>();
    private static final Map<String, ExperimentalFactor> CAARRAY1X_EXPECTED_FACTORS = new HashMap<String, ExperimentalFactor>();
    private static final Map<String, Person> CAARRAY1X_EXPECTED_PERSONS = new HashMap<String, Person>();
    private static final Map<String, Publication> CAARRAY1X_EXPECTED_PUBLICATIONS = new HashMap<String, Publication>();
    private static final Map<String, OntologyTerm> CAARRAY1X_EXPECTED_DESIGNS = new HashMap<String, OntologyTerm>();
    private static final Map<String, OntologyTerm> CAARRAY1X_EXPECTED_QUALITY_CONTROL_TYPES = new HashMap<String, OntologyTerm>();
    private static final TermSource CAARRAY1X_MO;
    private static final Map<String, TermSource> SPEC_EXPECTED_TERM_SOURCES = new HashMap<String, TermSource>();

    static {
        TermSource mo = new TermSource("MO", "http://mged.sourceforge.net/ontologies/MGEDontology.php", "1.3.0.1");
        TermSource cto = new TermSource("CTO", "http://obo.sourceforge.net/cgi-bin/detail.cgi?cell", null);
        TermSource ncbitax = new TermSource("ncbitax", "http://www.ncbi.nlm.nih.gov/Taxonomy/taxonomyhome.html/", null);
        TermSource arrayExpress = new TermSource("ArrayExpress", "http://www.ebi.ac.uk/arrayexpress/", null);
        SPEC_EXPECTED_TERM_SOURCES.put(mo.getName(), mo);
        SPEC_EXPECTED_TERM_SOURCES.put(cto.getName(), cto);
        SPEC_EXPECTED_TERM_SOURCES.put(ncbitax.getName(), ncbitax);
        SPEC_EXPECTED_TERM_SOURCES.put(arrayExpress.getName(), arrayExpress);

        CAARRAY1X_MO = mo = new TermSource("MO", "http://mged.sourceforge.net/ontologies/MGEDOntology1.1.8.daml",
                "1.1.8");
        TermSource adultMouseAnatomy = new TermSource("DB:Adult_Mouse_Anatomy");
        TermSource nciThesaurus = new TermSource("DB:NCI_Thesaurus");
        TermSource nciOrganismPartsDb = new TermSource("DB:NCI_organism_part_database");
        CAARRAY1X_EXPECTED_TERM_SOURCES.put(mo.getName(), mo);
        CAARRAY1X_EXPECTED_TERM_SOURCES.put(adultMouseAnatomy.getName(), adultMouseAnatomy);
        CAARRAY1X_EXPECTED_TERM_SOURCES.put(nciOrganismPartsDb.getName(), nciOrganismPartsDb);
        CAARRAY1X_EXPECTED_TERM_SOURCES.put(nciThesaurus.getName(), nciThesaurus);

        addExpectedProtocol(
                CAARRAY1X_EXPECTED_PROTOCOLS,
                "Calvo's RNA Extraction",
                "Total RNA was extracted from frozen tissues using the RNeasy mini kit (Qiagen, Valencia, CA) according to the manufacturer¿s protocol. The quality of the RNA was assessed by running aliquots on agarose gels.",
                "nucleic_acid_extraction", mo);
        addExpectedProtocol(
                CAARRAY1X_EXPECTED_PROTOCOLS,
                "Calvo's Reverse Transcription",
                "Briefly, 10 ¿g of RNA from both the reference sample (Pr111) and the experimental samples were reverse transcribed for 1 h at 42¿C using Cyanine 3-dUTP for the reference cell line Pr111 or Cyanine 5-dUTP for the experimental samples.",
                "reverse_transcription", mo);
        addExpectedProtocol(
                CAARRAY1X_EXPECTED_PROTOCOLS,
                "Calvo's labeling",
                "Preparation of the cDNA-labeled probes was performed using the Micromax system (NEN Life Science Products), according to the manufacture¿s protocol. Briefly, 10 ¿g of RNA from both the reference sample (Pr111) and the experimental samples were reverse transcribed for 1 h at 42¿C using Cyanine 3-dUTP for the reference cell line Pr111 or Cyanine 5-dUTP for the experimental samples.",
                "labeling", mo);
        addExpectedProtocol(
                CAARRAY1X_EXPECTED_PROTOCOLS,
                "Calvo's Hybridization",
                "An equal volume of 2x hybridization solution (50% formamide, 10x SSC, and 0.2% SDS) was added to the cDNA mixture and placed onto the microarray slide for hybridization at 42¿C for 16 h.",
                "hybridization", mo);
        addExpectedProtocol(
                CAARRAY1X_EXPECTED_PROTOCOLS,
                "GenePix feature Extraction",
                "The array slides were scanned with an Axon 4000 scanner (Axon Instruments, Foster City, CA) at a resolution of 10 ¿m.",
                "feature_extraction", mo);

        addExpectedFactor(CAARRAY1X_EXPECTED_FACTORS, "Pr117", "cell_line", mo);
        addExpectedFactor(CAARRAY1X_EXPECTED_FACTORS, "Pr14", "cell_line", mo);
        addExpectedFactor(CAARRAY1X_EXPECTED_FACTORS, "Pr14C2", "cell_line", mo);
        addExpectedFactor(CAARRAY1X_EXPECTED_FACTORS, "Pr14C1", "cell_line", mo);
        addExpectedFactor(CAARRAY1X_EXPECTED_FACTORS, "Pr111 reference", "cell_line", mo);
        addExpectedFactor(CAARRAY1X_EXPECTED_FACTORS, "Cell Line", "cell_line", mo);

        addExpectedDesignType(CAARRAY1X_EXPECTED_DESIGNS, "development_or_differentiation_design", null);
        addExpectedDesignType(CAARRAY1X_EXPECTED_DESIGNS, "disease_state_design", null);
        addExpectedDesignType(CAARRAY1X_EXPECTED_DESIGNS, "cell_type_comparison_design", null);
        addExpectedDesignType(CAARRAY1X_EXPECTED_DESIGNS, "cellular_process_design", null);
        addExpectedDesignType(CAARRAY1X_EXPECTED_DESIGNS, "reference_design", null);

        addExpectedQualityControlType(CAARRAY1X_EXPECTED_QUALITY_CONTROL_TYPES, "technical_replicate", mo);

        addExpectedPerson(CAARRAY1X_EXPECTED_PERSONS, "Jeffrey", "Green", "E", "JEGreen@nih.gov", null, null,
                "NCI/NIH, Behtesda, MD 20892", "Laboratory of cell regulation and carcinogenesis", mo, "investigator");
        addExpectedPerson(CAARRAY1X_EXPECTED_PERSONS, "Alfonso", "Calvo", null, "jegreen@nih.gov", "(301) 435-5193",
                "301) 496-8395", "41 Library Drive, Bethesda, MD 20892",
                "Laboratory of cell regulation and carcinogenesis", mo, "submitter");

        addExpectedPublication(CAARRAY1X_EXPECTED_PUBLICATIONS,
                "Alterations in gene expression profiles during prostate cancer progression", "12235003", null,
                "Calvo A, Xiao N, Kang J, Best CJ, Leiva I, Emmert-Buck MR, Jorcyk C, Green JE", "published", null);
    }

    private static void addExpectedPerson(Map<String, Person> expectedMap, String firstName, String lastName,
            String mi, String email, String phone, String fax, String address, String affiliation,
            TermSource roleTermSource, String... roles) {
        Person p = new Person();
        p.setFirstName(firstName);
        p.setLastName(lastName);
        p.setMidInitials(mi);
        p.setEmail(email);
        p.setPhone(phone);
        p.setFax(fax);
        p.setAffiliation(affiliation);
        p.setAddress(address);
        for (String role : roles) {
            p.addToRoles(createTerm(MageTabOntologyCategory.ROLES.getCategoryName(), role, roleTermSource));
        }
        expectedMap.put(personKey(p), p);
    }

    private static String personKey(Person person) {
        return person.getFirstName() + " " + person.getLastName();
    }

    private static void addExpectedPublication(Map<String, Publication> expectedMap, String title, String pubmedId,
            String doi, String authorList, String status, TermSource statusTermSource) {
        Publication p = new Publication();
        p.setAuthorList(authorList);
        p.setDoi(doi);
        p.setPubMedId(pubmedId);
        p.setTitle(title);
        p.setStatus(createTerm(MageTabOntologyCategory.PUBLICATION_STATUS.getCategoryName(), status, statusTermSource));
        expectedMap.put(publicationKey(p), p);
    }

    private static String publicationKey(Publication pub) {
        return pub.getTitle() + " " + pub.getPubMedId();
    }

    private static void addExpectedProtocol(Map<String, Protocol> expectedMap, String name, String description,
            String type, TermSource typeTermSource) {
        Protocol p = new Protocol();
        p.setName(name);
        p.setDescription(description);
        p.setType(createProtocolType(type, typeTermSource));
        CAARRAY1X_EXPECTED_PROTOCOLS.put(p.getName(), p);
    }

    private static void addExpectedFactor(Map<String, ExperimentalFactor> expectedMap, String name, String type,
            TermSource typeTermSource) {
        ExperimentalFactor ef = new ExperimentalFactor();
        ef.setName(name);
        ef.setType(createFactorType(type, typeTermSource));
        expectedMap.put(ef.getName(), ef);
    }

    private static void addExpectedDesignType(Map<String, OntologyTerm> expectedMap, String typeValue,
            TermSource typeTermSource) {
        addExpectedTerm(expectedMap, MageTabOntologyCategory.EXPERIMENTAL_DESIGN_TYPE.getCategoryName(), typeValue,
                typeTermSource);
    }

    private static void addExpectedQualityControlType(Map<String, OntologyTerm> expectedMap, String typeValue,
            TermSource typeTermSource) {
        addExpectedTerm(expectedMap, MageTabOntologyCategory.QUALITY_CONTROL_TYPE.getCategoryName(), typeValue,
                typeTermSource);
    }

    private static void addExpectedTerm(Map<String, OntologyTerm> expectedMap, String termCategory, String termValue,
            TermSource termSource) {
        OntologyTerm term = createTerm(termCategory, termValue, termSource);
        expectedMap.put(term.getValue(), term);
    }

    private static OntologyTerm createProtocolType(String typeValue, TermSource ts) {
        return createTerm(MageTabOntologyCategory.PROTOCOL_TYPE.getCategoryName(), typeValue, ts);
    }

    private static OntologyTerm createFactorType(String typeValue, TermSource ts) {
        return createTerm(MageTabOntologyCategory.EXPERIMENTAL_FACTOR_CATEGORY.getCategoryName(), typeValue, ts);
    }

    private static OntologyTerm createTerm(String category, String value, TermSource ts) {
        OntologyTerm type = new OntologyTerm();
        type.setCategory(category);
        type.setValue(value);
        type.setTermSource(ts);
        return type;
    }

    private ValidationResult validate(MageTabFileSet fileSet)
            throws MageTabParsingException {
        try {
            MageTabDocumentSet docSet = parser.parse(fileSet);
            return docSet.getValidationResult();
        } catch (InvalidDataException e) {
            return e.getValidationResult();
        }
    }

    @Test
    public void testValidate() throws MageTabParsingException {
        MageTabFileSet fileSet = TestMageTabSets.MAGE_TAB_ERROR_SPECIFICATION_INPUT_SET;
        ValidationResult result = validate(fileSet);
        System.out.println("testValidate result: " + result);
        assertFalse(result.isValid());
        assertEquals(100, result.getMessages().size());
        // check for the fix to gforge defect 12541
        assertTrue(result.getMessages().toString().contains("ERROR: Referenced Factor Name EF1 was not found in the IDF"));
        assertTrue(result.getMessages().toString().contains("ERROR: Experimental Factors must have a non-empty name"));
        assertTrue(result.getMessages().toString().contains("Array Design File not allowed via experiment data import."));
    }

    @Test
    public void testSelectRefFiles() throws MageTabParsingException, InvalidDataException{
        MageTabFileSet fileSet = TestMageTabSets.MAGE_TAB_SPECIFICATION_INPUT_SET;
        MageTabDocumentSet mTabDocSet = parser.parseDataFileNames(fileSet);
        assertNotNull(mTabDocSet);
        assertEquals(1, mTabDocSet.getSdrfDocuments().size());
    }


    @Test
    public void testValidateIllegalUnitPlacement() throws MageTabParsingException {
        MageTabFileSet fileSet = TestMageTabSets.MAGE_TAB_GEDP_INPUT_SET;
        ValidationResult result = validate(fileSet);
        assertFalse(result.isValid());
        assertTrue(result.getMessages().toString().contains("ERROR: Illegal Unit column"));
    }

    @Test
    public void testParseEmptySet() throws InvalidDataException, MageTabParsingException {
        MageTabFileSet inputFileSet = new MageTabFileSet();
        MageTabDocumentSet documentSet = parser.parse(inputFileSet);
        assertNotNull(documentSet);
    }

    @Test
    public void testValidateMissingSdrf() throws MageTabParsingException {
        MageTabFileSet inputFileSet = new MageTabFileSet();
        inputFileSet.addIdf(MageTabDataFiles.SPECIFICATION_EXAMPLE_IDF);
        ValidationResult result = validate(inputFileSet);
        assertNotNull(result);
        assertFalse(result.isValid());
        assertEquals(1, result.getFileValidationResults().size());
        FileValidationResult fileValidationResult = result.getFileValidationResults().get(0);
        assertEquals(MageTabDataFiles.SPECIFICATION_EXAMPLE_IDF, fileValidationResult.getFile());
        assertEquals(1, fileValidationResult.getMessages().size());
        ValidationMessage message = fileValidationResult.getMessages().get(0);
        assertEquals(33, message.getLine());
        assertEquals(2, message.getColumn());
        assertTrue(message.getMessage().startsWith("Referenced SDRF file "));
        assertTrue(message.getMessage().endsWith(" was not included in the MAGE-TAB document set"));
    }

    @Test
    public void testValidateMissingIdf() throws MageTabParsingException {
        MageTabFileSet inputFileSet = new MageTabFileSet();
        inputFileSet.addSdrf(MageTabDataFiles.SPECIFICATION_EXAMPLE_SDRF);
        ValidationResult result = validate(inputFileSet);
        assertNotNull(result);
        assertFalse(result.isValid());
        assertEquals(1, result.getFileValidationResults().size());
        FileValidationResult fileValidationResult = result.getFileValidationResults().get(0);
        assertEquals(MageTabDataFiles.SPECIFICATION_EXAMPLE_SDRF, fileValidationResult.getFile());
        assertEquals(1, fileValidationResult.getMessages().size());
        ValidationMessage message = fileValidationResult.getMessages().get(0);
        assertTrue(message.getMessage().equals("This SDRF file is not referenced by an IDF file."));
    }

    @Test
    public void testValidateMissingTermSources() throws MageTabParsingException {
        MageTabFileSet inputFileSet = new MageTabFileSet();
        inputFileSet.addIdf(MageTabDataFiles.MISSING_TERMSOURCE_IDF);
        inputFileSet.addSdrf(MageTabDataFiles.MISSING_TERMSOURCE_SDRF);
        ValidationResult result = validate(inputFileSet);
        System.out.println(result);
        assertTrue(result.toString().contains("Term Source not-in-IDF is not defined in the IDF document"));
    }

    @Test
    public void testValidateMultipleIdfs() throws MageTabParsingException {
        MageTabFileSet inputFileSet = new MageTabFileSet();
        inputFileSet.addIdf(MageTabDataFiles.MISSING_TERMSOURCE_IDF);
        inputFileSet.addSdrf(MageTabDataFiles.MISSING_TERMSOURCE_SDRF);
        inputFileSet.addIdf(MageTabDataFiles.SPECIFICATION_EXAMPLE_IDF);
        ValidationResult result = validate(inputFileSet);
        System.out.println(result);
        assertTrue(result.toString().contains("At most one IDF document can be present in an import"));
    }

    @Test
    public void testValidateMissingDataFiles() throws MageTabParsingException {
        MageTabFileSet inputFileSet = new MageTabFileSet();
        inputFileSet.addIdf(MageTabDataFiles.SPECIFICATION_EXAMPLE_IDF);
        inputFileSet.addSdrf(MageTabDataFiles.SPECIFICATION_EXAMPLE_SDRF);
        ValidationResult result = validate(inputFileSet);
        assertNotNull(result);
        assertFalse(result.isValid());
        inputFileSet = TestMageTabSets.MAGE_TAB_SPECIFICATION_NO_DATA_MATRIX_INPUT_SET;
        result = validate(inputFileSet);
        assertNotNull(result);
        assertFalse(result.isValid());
        inputFileSet = TestMageTabSets.MAGE_TAB_SPECIFICATION_INPUT_SET;
        result = validate(inputFileSet);
        assertNotNull(result);
        assertTrue(result.isValid());
    }

    @Test
    public void testValidateMisplacedFactorValues() throws MageTabParsingException {
        MageTabFileSet inputFileSet = TestMageTabSets.MAGE_TAB_MISPLACED_FACTOR_VALUES_INPUT_SET;
        ValidationResult result = validate(inputFileSet);
        assertNotNull(result);
        assertFalse(result.isValid());
        assertEquals(3, StringUtils.countMatches(result.toString(), "Factor Value columns must come after (to the right of) a Hybridization column"));
        assertFalse(result.toString().contains("Exception"));
    }

    @Test
    public void testParseTcgaBroadDocuments() throws MageTabParsingException, InvalidDataException {
        MageTabFileSet fileSet = TestMageTabSets.TCGA_BROAD_INPUT_SET;
        MageTabDocumentSet documentSet = parser.parse(fileSet);
        assertNotNull(documentSet);
        assertEquals(1, documentSet.getIdfDocuments().size());
        assertEquals(-1, documentSet.getIdfDocuments().iterator().next().getInvestigation().getDescription().indexOf("<"));
        assertEquals(-1, documentSet.getIdfDocuments().iterator().next().getInvestigation().getDescription().indexOf(">"));
        assertEquals(1, documentSet.getSdrfDocuments().size());
        assertEquals(1, documentSet.getDataMatrixes().size());
        assertEquals(26, documentSet.getNativeDataFiles().size());
        checkSdrfTranslation(documentSet.getSdrfDocuments().iterator().next());
        checkArrayDesigns(documentSet);
        assertTrue(documentSet.getValidationResult().isValid());
    }

    @Test
    public void testDefect12537() throws MageTabParsingException, InvalidDataException {
        MageTabFileSet fileSet = TestMageTabSets.DEFECT_12537_ERROR_INPUT_SET;
        ValidationResult validationResult = validate(fileSet);
        assertFalse(validationResult.isValid());
        List<ValidationMessage> errors = validationResult.getMessages(ValidationMessage.Type.ERROR);
        assertEquals(3, errors.size());
        for (int i = 0; i < 3; i++) {
            assertEquals(i + 2, errors.get(i).getLine());
            assertEquals(38, errors.get(i).getColumn());
            assertTrue(errors.get(i).getMessage().contains("Duplicate Normalization Name"));
        }

        fileSet = TestMageTabSets.DEFECT_12537_INPUT_SET;
        MageTabDocumentSet documentSet = parser.parse(fileSet);
        assertNotNull(documentSet);
        assertTrue(documentSet.getValidationResult().isValid());
        assertEquals(2, documentSet.getDataMatrixes().size());
        assertEquals(3, documentSet.getNativeDataFiles().size());
        assertEquals(1, documentSet.getIdfDocuments().size());
        assertEquals(1, documentSet.getSdrfDocuments().size());

        SdrfDocument sdrfDocument = documentSet.getSdrfDocuments().iterator().next();
        assertEquals(2, sdrfDocument.getAllDerivedArrayDataMatrixFiles().size());
        assertEquals(3, sdrfDocument.getAllHybridizations().size());
        for (Hybridization hyb : sdrfDocument.getAllHybridizations()) {
            assertEquals(2, hyb.getSuccessorDerivedArrayDataMatrixFiles().size());
            assertEquals(1, hyb.getSuccessorArrayDataFiles().size());
        }
    }

    @Test
    public void testParseCarray1xDocuments() throws MageTabParsingException, InvalidDataException {
        MageTabFileSet fileSet = TestMageTabSets.CAARRAY1X_INPUT_SET;
        MageTabDocumentSet documentSet = parser.parse(fileSet);
        assertNotNull(documentSet);
        assertTrue(documentSet.getValidationResult().isValid());
        assertEquals(1, documentSet.getIdfDocuments().size());
        assertEquals(1, documentSet.getSdrfDocuments().size());
        assertEquals(0, documentSet.getAdfDocuments().size());
        assertEquals(0, documentSet.getDataMatrixes().size());
        assertEquals(19, documentSet.getNativeDataFiles().size());
        assertEquals(4, documentSet.getTermSources().size());
        assertEquals(5, documentSet.getProtocols().size());
        IdfDocument idfDocument = documentSet.getIdfDocuments().iterator().next();
        SdrfDocument sdrfDocument = documentSet.getSdrfDocuments().iterator().next();
        assertTrue(idfDocument.getDocumentSet().getSdrfDocuments().contains(sdrfDocument));
        assertEquals(idfDocument, sdrfDocument.getIdfDocument());
        checkCarray1xIdfDocument(idfDocument);
        checkCarray1xSdrfDocument(sdrfDocument);
        checkTermSources(CAARRAY1X_EXPECTED_TERM_SOURCES, documentSet.getTermSources());
        checkTerms(documentSet);
        checkProtocols(CAARRAY1X_EXPECTED_PROTOCOLS, documentSet.getProtocols());
    }

    private void checkProtocols(Map<String, Protocol> expectedProtocols, Collection<Protocol> protocols) {
        assertEquals(expectedProtocols.size(), protocols.size());
        for (Protocol protocol : protocols) {
            Protocol expectedProtocol = expectedProtocols.get(protocol.getName());
            checkProtocolsMatch(expectedProtocol, protocol);
        }
    }

    private void checkProtocolsMatch(Protocol expectedProtocol, Protocol protocol) {
        checkTermsMatch(expectedProtocol.getType(), protocol.getType());
        assertTrue("Expected protocol " + ToStringBuilder.reflectionToString(expectedProtocol)
                + " differs from actual protocol " + ToStringBuilder.reflectionToString(protocol), EqualsBuilder
                .reflectionEquals(expectedProtocol, protocol, new String[] { "type" }));
    }

    private void checkTermSources(Map<String, TermSource> expectedTermSources, Collection<TermSource> termSources) {
        assertEquals(expectedTermSources.size(), termSources.size());
        for (TermSource termSource : termSources) {
            TermSource expectedTermSource = expectedTermSources.get(termSource.getName());
            checkTermSourcesMatch(expectedTermSource, termSource);
        }
    }

    private void checkTermSourcesMatch(TermSource expectedTermSource, TermSource termSource) {
        assertTrue("Expected term source " + ToStringBuilder.reflectionToString(expectedTermSource)
                + " differs from actual term source" + ToStringBuilder.reflectionToString(termSource), EqualsBuilder
                .reflectionEquals(expectedTermSource, termSource));
    }

    private void checkFactors(Map<String, ExperimentalFactor> expectedFactors, Collection<ExperimentalFactor> factors) {
        assertEquals(expectedFactors.size(), factors.size());
        for (ExperimentalFactor factor : factors) {
            ExperimentalFactor expectedFactor = expectedFactors.get(factor.getName());
            checkFactorsMatch(expectedFactor, factor);
        }
    }

    private void checkFactorsMatch(ExperimentalFactor expectedFactor, ExperimentalFactor factor) {
        checkTermsMatch(expectedFactor.getType(), factor.getType());
        assertTrue("Expected factor " + ToStringBuilder.reflectionToString(expectedFactor)
                + " differs from actual factor" + ToStringBuilder.reflectionToString(factor), EqualsBuilder
                .reflectionEquals(expectedFactor, factor, new String[] { "type" }));
    }

    private void checkPersons(Map<String, Person> expectedPersons, Collection<Person> persons) {
        assertEquals(expectedPersons.size(), persons.size());
        for (Person person : persons) {
            Person expectedPerson = expectedPersons.get(personKey(person));
            checkPersonsMatch(expectedPerson, person);
        }
    }

    private void checkPersonsMatch(Person expectedPerson, Person person) {
        assertEquals(expectedPerson.getRoles().size(), person.getRoles().size());
        assertTrue("Expected person " + ToStringBuilder.reflectionToString(expectedPerson)
                + " differs from actual person" + ToStringBuilder.reflectionToString(person), EqualsBuilder
                .reflectionEquals(expectedPerson, person, new String[] { "roles" }));
    }

    private void checkPublications(Map<String, Publication> expectedPublications, Collection<Publication> publications) {
        assertEquals(expectedPublications.size(), publications.size());
        for (Publication publication : publications) {
            Publication expectedPublication = expectedPublications.get(publicationKey(publication));
            checkPublicationsMatch(expectedPublication, publication);
        }
    }

    private void checkPublicationsMatch(Publication expectedPublication, Publication publication) {
        checkTermsMatch(expectedPublication.getStatus(), publication.getStatus());
        assertTrue("Expected publication " + ToStringBuilder.reflectionToString(expectedPublication)
                + " differs from actual publication" + ToStringBuilder.reflectionToString(publication), EqualsBuilder
                .reflectionEquals(expectedPublication, publication, new String[] { "status" }));
    }

    private void checkTerms(Map<String, OntologyTerm> expectedTerms, Collection<OntologyTerm> terms) {
        assertEquals(expectedTerms.size(), terms.size());
        for (OntologyTerm term : terms) {
            OntologyTerm expectedTerm = expectedTerms.get(term.getValue());
            checkTermsMatch(expectedTerm, term);
        }
    }

    private void checkTermsMatch(OntologyTerm expectedTerm, OntologyTerm term) {
        assertTrue("Expected term  " + ToStringBuilder.reflectionToString(expectedTerm) + " differs from actual term"
                + ToStringBuilder.reflectionToString(term), EqualsBuilder.reflectionEquals(expectedTerm, term));
    }

    private void checkCarray1xSdrfDocument(SdrfDocument sdrfDocument) {
        assertEquals(37, sdrfDocument.getLeftmostNodes().size());
        assertEquals(37, sdrfDocument.getAllSources().size());
        Source source = sdrfDocument.getAllSources().get(0);
        assertEquals("8kNew111_14_4601_m84", source.getName());
        assertEquals(1, source.getProviders().size());
        assertEquals("Laboratory of cell regulation and carcinogenesis", source.getProviders().get(0).getName());
        assertEquals(7, source.getCharacteristics().size());
        checkTermsMatch(createTerm(MageTabOntologyCategory.MATERIAL_TYPE.getCategoryName(), "organism_part",
                CAARRAY1X_MO), source.getMaterialType());
        assertEquals(2, source.getProtocolApplications().size());
        assertEquals(36, sdrfDocument.getAllSamples().size());
        Sample sample = sdrfDocument.getAllSamples().get(0);
        assertEquals("8kNew111_14_4601_m84 RNA", sample.getName());
        assertEquals(1, sample.getPredecessors().size());
        assertEquals(1, sample.getProtocolApplications().size());
        assertEquals("Calvo's labeling", sample.getProtocolApplications().get(0).getProtocol().getName());
        assertEquals("Calvo, Alfonso", sample.getProtocolApplications().get(0).getPerformer());
        checkTermsMatch(createTerm(MageTabOntologyCategory.MATERIAL_TYPE.getCategoryName(), "synthetic_RNA",
                CAARRAY1X_MO), sample.getMaterialType());
        assertEquals(38, sdrfDocument.getAllLabeledExtracts().size());
        LabeledExtract le = sdrfDocument.getAllLabeledExtracts().get(0);
        assertEquals("Cy3 labeled Pr111 reference_8kNew111_14_4601_m84", le.getName());
        checkTermsMatch(createTerm(MageTabOntologyCategory.MATERIAL_TYPE.getCategoryName(), "synthetic_RNA",
                CAARRAY1X_MO), le.getMaterialType());
        checkTermsMatch(createTerm(MageTabOntologyCategory.LABEL_COMPOUND.getCategoryName(), "Cy3", null), le
                .getLabel());
        assertEquals(1, le.getProtocolApplications().size());
        assertEquals("Calvo's Hybridization", le.getProtocolApplications().get(0).getProtocol().getName());
        assertEquals("Calvo, Alfonso", le.getProtocolApplications().get(0).getPerformer());
        assertEquals(19, sdrfDocument.getAllHybridizations().size());
        Hybridization hyb = sdrfDocument.getAllHybridizations().get(0);
        assertEquals("GenePix feature Extraction", hyb.getProtocolApplications().get(0).getProtocol().getName());
        assertEquals("Calvo, Alfonso", hyb.getProtocolApplications().get(0).getPerformer());
        assertEquals(1, hyb.getProtocolApplications().size());
        assertEquals(2, hyb.getPredecessors().size());
        assertEquals(0, hyb.getSuccessorArrayDataFiles().size());
        assertEquals(0, hyb.getSuccessorArrayDataMatrixFiles().size());
        assertEquals(0, hyb.getSuccessorDerivedArrayDataMatrixFiles().size());
        assertEquals(1, hyb.getSuccessorDerivedArrayDataFiles().size());
        DerivedArrayDataFile arrayDataFile = hyb.getSuccessorDerivedArrayDataFiles().iterator().next();
        assertNotNull(arrayDataFile);
        assertEquals("8kNew111_14_4601_m84.gpr", arrayDataFile.getName());
        assertEquals(12, hyb.getFactorValues().size());
        assertEquals("URN:LSID:caarray.nci.nih.gov:domain:Mm-Incyte-v1px_16Bx24Cx23R", hyb.getArrayDesign().getName());
    }

    private void checkCarray1xIdfDocument(IdfDocument idfDoc) {
        assertEquals("Alterations in Gene Expression Profiles during Prostate Cancer Progression", idfDoc
                .getInvestigation().getTitle());
        assertEquals(
                "To identify molecular changes that occur during prostate tumor progression, we have characterized a series of prostate cancer cell lines isolated at different stages of tumorigenesis from C3(1)/Tag transgenic mice.",
                idfDoc.getInvestigation().getDescription());
        assertEquals(6, idfDoc.getInvestigation().getFactors().size());
        checkFactors(CAARRAY1X_EXPECTED_FACTORS, idfDoc.getInvestigation().getFactors());
        assertEquals(5, idfDoc.getInvestigation().getDesigns().size());
        checkTerms(CAARRAY1X_EXPECTED_DESIGNS, idfDoc.getInvestigation().getDesigns());
        assertEquals(2, idfDoc.getInvestigation().getPersons().size());
        checkPersons(CAARRAY1X_EXPECTED_PERSONS, idfDoc.getInvestigation().getPersons());
        assertEquals(1, idfDoc.getInvestigation().getQualityControlTypes().size());
        checkTerms(CAARRAY1X_EXPECTED_QUALITY_CONTROL_TYPES, idfDoc.getInvestigation().getQualityControlTypes());
        assertEquals(0, idfDoc.getInvestigation().getNormalizationTypes().size());
        assertEquals(0, idfDoc.getInvestigation().getReplicateTypes().size());
        assertEquals(1, idfDoc.getInvestigation().getPublications().size());
        checkPublications(CAARRAY1X_EXPECTED_PUBLICATIONS, idfDoc.getInvestigation().getPublications());
    }

    private void checkSdrfTranslation(SdrfDocument document) {
        List<Hybridization> hybridizations = document.getAllHybridizations();
        assertEquals(26, hybridizations.size());
        for (Hybridization hybridization : hybridizations) {
            assertEquals(1, hybridization.getSuccessorArrayDataFiles().size());
        }
        List<ArrayDataFile> arrayDataFiles = document.getAllArrayDataFiles();
        assertEquals(26, arrayDataFiles.size());
    }

    private void checkArrayDesigns(MageTabDocumentSet documentSet) {
        SdrfDocument sdrfDocument = documentSet.getSdrfDocuments().iterator().next();
        assertEquals(1, sdrfDocument.getAllArrayDesigns().size());
        ArrayDesign arrayDesign = sdrfDocument.getAllArrayDesigns().get(0);
        for (Hybridization hybridization : sdrfDocument.getAllHybridizations()) {
            assertEquals(arrayDesign, hybridization.getArrayDesign());
        }
    }

    @Test
    public void testSourceDescriptionTranslation() throws InvalidDataException, MageTabParsingException {
        MageTabFileSet fileSet = TestMageTabSets.MAGE_TAB_UNSUPPORTED_DATA_INPUT_SET;
        MageTabDocumentSet documentSet = parser.parse(fileSet);
        SdrfDocument sdrfDocument = documentSet.getSdrfDocuments().iterator().next();
        checkBioMaterialDescriptions(sdrfDocument.getAllSources(), "Source description ");
        checkBioMaterialDescriptions(sdrfDocument.getAllSamples(), "Sample description ");
        checkBioMaterialDescriptions(sdrfDocument.getAllExtracts(), "Extract description ");
        checkBioMaterialDescriptions(sdrfDocument.getAllLabeledExtracts(), "LabeledExtract description ");
    }

    private void checkBioMaterialDescriptions(List<? extends AbstractBioMaterial> materials, String description) {
        for (int i = 0; i < materials.size(); i++) {
            assertEquals(description + (i + 1), materials.get(i).getDescription());
        }
    }

    @Test
    public void testParseSpecificationDocuments() throws MageTabParsingException, InvalidDataException {
        MageTabFileSet fileSet = TestMageTabSets.MAGE_TAB_SPECIFICATION_INPUT_SET;
        MageTabDocumentSet documentSet = parser.parse(fileSet);
        assertNotNull(documentSet);
        checkTermSources(SPEC_EXPECTED_TERM_SOURCES, documentSet.getTermSources());
        checkTerms(documentSet);
        assertEquals(1, documentSet.getIdfDocuments().size());
        IdfDocument idfDocument = documentSet.getIdfDocument(MageTabDataFiles.SPECIFICATION_EXAMPLE_IDF.getName());
        Investigation investigation = idfDocument.getInvestigation();
        assertEquals(3, investigation.getProtocols().size());
        assertEquals("submitter", investigation.getPersons().get(0).getRoles().get(0).getValue());
        assertEquals(2, investigation.getPersons().get(0).getRoles().size());
        assertEquals("http://mged.sourceforge.net/ontologies/MGEDontology.php", investigation.getProtocols().get(0)
                .getType().getTermSource().getFile());
        assertEquals("University of Heidelberg H sapiens TK6", investigation.getTitle());
        SdrfDocument sdrfDocument = documentSet.getSdrfDocuments().iterator().next();
        assertNotNull(sdrfDocument);
        assertEquals(ONE, documentSet.getSdrfDocuments().size());
        assertTrue(idfDocument.getDocumentSet().getSdrfDocuments().contains(sdrfDocument));
        assertEquals(idfDocument, sdrfDocument.getIdfDocument());
        assertEquals(6, sdrfDocument.getLeftmostNodes().size());
        assertEquals(SIX, sdrfDocument.getAllSources().size());
        Source firstSource = sdrfDocument.getAllSources().get(0);
        assertEquals(-1, firstSource.getName().indexOf("<"));
        assertEquals(-1, firstSource.getName().indexOf(">"));

        // check issue 16421 - skip blank characteristics
        assertNull(firstSource.getCharacteristic("CellType"));
        assertEquals("B_lymphoblast", sdrfDocument.getAllSources().get(1).getCharacteristic("CellType").getTerm()
                .getValue());

        assertEquals(SIX, sdrfDocument.getAllSamples().size());
        assertEquals(SIX, sdrfDocument.getAllExtracts().size());
        assertEquals(SIX, sdrfDocument.getAllHybridizations().size());
        assertEquals(SIX, sdrfDocument.getAllLabeledExtracts().size());
        Hybridization firstHybridization = sdrfDocument.getAllHybridizations().get(0);
        Set<ArrayDataFile> rawDataFiles = firstHybridization.getSuccessorArrayDataFiles();
        assertEquals(2, rawDataFiles.size());
        Iterator<ArrayDataFile> rawDataIterator = rawDataFiles.iterator();
        ArrayDataFile arrayDataFile = rawDataIterator.next();
        assertNotNull(arrayDataFile);
        if ("H_TK6 replicate 3.CEL".equals(arrayDataFile.getName())) {
            assertEquals(1, arrayDataFile.getSuccessorDerivedArrayDataMatrixFiles().size());
            arrayDataFile = rawDataIterator.next();
            assertEquals("H_TK6 replicate 1.CEL", arrayDataFile.getName());
            assertEquals(0, arrayDataFile.getSuccessorDerivedArrayDataMatrixFiles().size());
        } else {
            assertEquals(0, arrayDataFile.getSuccessorDerivedArrayDataMatrixFiles().size());
            arrayDataFile = rawDataIterator.next();
            assertEquals("H_TK6 replicate 3.CEL", arrayDataFile.getName());
            assertEquals(1, arrayDataFile.getSuccessorDerivedArrayDataMatrixFiles().size());
        }
    }

    /**
     * Tests to make sure derived data is properly associated with the source of that data (defect 13010).
     */
    @Test
    public void testParseDerivedData() throws Exception {
        MageTabFileSet fileSet = TestMageTabSets.DERIVED_DATA_INPUT_SET;
        MageTabDocumentSet documentSet = parser.parse(fileSet);
        assertNotNull(documentSet);
        SdrfDocument sdrfDocument = documentSet.getSdrfDocuments().iterator().next();
        assertEquals(1, sdrfDocument.getAllArrayDataFiles().size());
        ArrayDataFile arrayDataFile = sdrfDocument.getAllArrayDataFiles().get(0);
        List<DerivedArrayDataFile> derivedDataFiles = sdrfDocument.getAllDerivedArrayDataFiles();
        assertEquals(2, derivedDataFiles.size());

        // make sure the first derived data file is derived from the raw data
        DerivedArrayDataFile derivedArrayDataFile = derivedDataFiles.get(0);
        Set<ArrayDataFile> predecessorArrayDataFiles = derivedArrayDataFile.getPredecessorArrayDataFiles();
        assertEquals(1, predecessorArrayDataFiles.size());
        assertTrue(predecessorArrayDataFiles.contains(arrayDataFile));
        assertEquals(0, derivedArrayDataFile.getPredecessorDerivedArrayDataFiles().size());

        // make sure the second derived data file is derived from the first derived data
        DerivedArrayDataFile derivedArrayDataFile2 = derivedDataFiles.get(1);
        Set<DerivedArrayDataFile> predecessorDerivedArrayDataFiles =
            derivedArrayDataFile2.getPredecessorDerivedArrayDataFiles();
        assertEquals(0, derivedArrayDataFile2.getPredecessorArrayDataFiles().size());
        assertEquals(1, predecessorDerivedArrayDataFiles.size());
        assertTrue(predecessorDerivedArrayDataFiles.contains(derivedArrayDataFile));
    }

    private void checkTerms(MageTabDocumentSet documentSet) {
        for (OntologyTerm term : documentSet.getTerms()) {
            assertNotNull("null value for term", term.getValue());
        }
    }

    @Test
    public void testFeature13141() throws Exception {
        MageTabFileSet fileSet = TestMageTabSets.VALID_FEATURE_13141_INPUT_SET;
        MageTabDocumentSet documentSet = parser.parse(fileSet);
        assertNotNull(documentSet);
        assertEquals(2, documentSet.getSdrfDocuments().size());
        SdrfDocument sdrfDocument = documentSet.getSdrfDocument(MageTabDataFiles.FEATURE_13141_SDRF.getName());
        assertNotNull(sdrfDocument);
        verifyFeature13141Sample(sdrfDocument.getAllSamples().get(0), "Sample A", "123");
        verifyFeature13141Sample(sdrfDocument.getAllSamples().get(1), "Sample B", "234");
        verifyFeature13141Sample(sdrfDocument.getAllSamples().get(2), "Sample C", "345");
        sdrfDocument = documentSet.getSdrfDocument(MageTabDataFiles.FEATURE_13141_SDRF2.getName());
        assertNotNull(sdrfDocument);
        verifyFeature13141Source(sdrfDocument.getAllSources().get(0), "Source D", "ext1");
        verifyFeature13141Source(sdrfDocument.getAllSources().get(1), "Source E", "ext2");
        verifyFeature13141Source(sdrfDocument.getAllSources().get(2), "Source F", "ext3");
        verifyFeature13141Sample(sdrfDocument.getAllSamples().get(0), "Sample D", "456");
        verifyFeature13141Sample(sdrfDocument.getAllSamples().get(1), "Sample E", "567");
        verifyFeature13141Sample(sdrfDocument.getAllSamples().get(2), "Sample F", "678");
    }

    private void verifyFeature13141Sample(Sample s1, String expectedSampleName, String expectedTermValue) {
        assertEquals(expectedSampleName, s1.getName());
        assertEquals(1, s1.getCharacteristics().size());
        assertEquals(ExperimentOntologyCategory.EXTERNAL_SAMPLE_ID.getCategoryName(), s1.getCharacteristics().get(0).getCategory());
        assertEquals(expectedTermValue, s1.getCharacteristics().get(0).getValue());
        assertEquals(null, s1.getCharacteristics().get(0).getTerm());
    }

    private void verifyFeature13141Source(Source s1, String expectedName, String expectedValue) {
        assertEquals(expectedName, s1.getName());
        assertEquals(1, s1.getCharacteristics().size());
        assertEquals(ExperimentOntologyCategory.EXTERNAL_ID.getCategoryName(), s1.getCharacteristics().get(0).getCategory());
        assertEquals(expectedValue, s1.getCharacteristics().get(0).getValue());
        assertEquals(null, s1.getCharacteristics().get(0).getTerm());
    }

    @Test
    public void testFeature13141VerifyCanParsesInvalidSdrf() throws Exception {
        MageTabFileSet fileSet = TestMageTabSets.INVALID_FEATURE_13141_INPUT_SET;
        MageTabDocumentSet documentSet = parser.parse(fileSet);
        assertNotNull(documentSet);
        assertEquals(2, documentSet.getSdrfDocuments().size());
        SdrfDocument sdrfDocument = documentSet.getSdrfDocument(MageTabDataFiles.FEATURE_13141_INVALID_SDRF.getName());
        assertNotNull(sdrfDocument);
        verifyFeature13141Sample(sdrfDocument.getAllSamples().get(0), "Sample A", "123");
        verifyFeature13141Sample(sdrfDocument.getAllSamples().get(1), "Sample B", "234");
        verifyFeature13141Sample(sdrfDocument.getAllSamples().get(2), "Sample C", "345");
        sdrfDocument = documentSet.getSdrfDocument(MageTabDataFiles.FEATURE_13141_INVALID_SDRF2.getName());
        assertNotNull(sdrfDocument);
        verifyFeature13141Sample(sdrfDocument.getAllSamples().get(0), "Sample D", "123");
        verifyFeature13141Sample(sdrfDocument.getAllSamples().get(1), "Sample E", "234");
        verifyFeature13141Sample(sdrfDocument.getAllSamples().get(2), "Sample F", "345");
    }

    @Test
    public void testDefect16421() throws Exception {
        MageTabFileSet fileSet = TestMageTabSets.DEFECT_16421;
        ValidationResult validationResult = validate(fileSet);
        assertTrue(validationResult.isValid());

        MageTabFileSet fileSet2 = TestMageTabSets.DEFECT_16421_2;
        ValidationResult validationResult2 = validate(fileSet2);
        List<ValidationMessage> errors = validationResult2.getMessages(ValidationMessage.Type.ERROR);
        for (ValidationMessage error : errors) {
            assertTrue(error.getMessage().contains("Term Source Ref is not preceded by valid data type"));
        }
    }
    
    /**
     * Tests for the refactored factor value / parameter value / characteristic data model.
     */
    @Test
    public void testExtendedFactorValues() throws Exception {
        MageTabFileSet fileSet = TestMageTabSets.EXTENDED_FACTOR_VALUES_INPUT_SET;
        
        try {
            MageTabDocumentSet documentSet = parser.parse(fileSet);
            System.out.println("Parse result for extended values: \n" + documentSet.getValidationResult());
            assertNotNull(documentSet);

            assertEquals(1, documentSet.getSdrfDocuments().size());
            SdrfDocument sdrfDocument = documentSet.getSdrfDocuments().iterator().next();
            assertEquals(3, sdrfDocument.getAllSamples().size());
            verifyExtendedFactorValuesSampleChars(sdrfDocument.getAllSamples().get(0), "Sample A", "123", "5", "years",
                    "tissue", "MO");
            verifyExtendedFactorValuesSampleChars(sdrfDocument.getAllSamples().get(1), "Sample B", "234", "a lot",
                    "months", "tissue", "MO");
            verifyExtendedFactorValuesSampleChars(sdrfDocument.getAllSamples().get(2), "Sample C", "345", "2.2", "days",
                    "unknown", null);
            verifyExtendedFactorValuesSampleParams(sdrfDocument.getAllSamples().get(0), "Sample A", "foo", "planting", "MO",
                    "4", "kg");
            verifyExtendedFactorValuesSampleParams(sdrfDocument.getAllSamples().get(1), "Sample B", "baz", "planting",
                    "MO", "4", null);
            verifyExtendedFactorValuesSampleParams(sdrfDocument.getAllSamples().get(2), "Sample C", "foo", "nothing", null,
                    "less", "mg");
            assertEquals(3, sdrfDocument.getAllHybridizations().size());
            verifyExtendedFactorValuesHyb(sdrfDocument.getAllHybridizations().get(0), "Hyb A", "123", "5", "years",
                    "tissue", "MO");
            verifyExtendedFactorValuesHyb(sdrfDocument.getAllHybridizations().get(1), "Hyb B", "234", "a lot", "months",
                    "tissue", "MO");
            verifyExtendedFactorValuesHyb(sdrfDocument.getAllHybridizations().get(2), "Hyb C", "345", "2.2", null,
                    "unknown", null);
        } catch (InvalidDataException e) {
            fail("Could not parse magetab: " + e.getValidationResult().toString());
        }
    }

    private void verifyExtendedFactorValuesHyb(Hybridization hyb, String hybName, String fv1Value, String fv2Value,
            String fv2Unit, String fv3Value, String fv3ts) {
        assertEquals(hybName, hyb.getName());
        assertEquals(3, hyb.getFactorValues().size());
        assertEquals("ExternalSampleId", hyb.getFactorValues().get(0).getFactor().getName());
        assertEquals(fv1Value, hyb.getFactorValues().get(0).getValue());
        assertNull(hyb.getFactorValues().get(0).getTerm());
        assertNull(hyb.getFactorValues().get(0).getUnit());
        assertEquals("Age", hyb.getFactorValues().get(1).getFactor().getName());
        assertEquals(fv2Value, hyb.getFactorValues().get(1).getValue());
        assertNull(hyb.getFactorValues().get(1).getTerm());
        if (fv2Unit == null)
            assertNull(hyb.getFactorValues().get(1).getUnit());
        else {
            assertEquals(fv2Unit, hyb.getFactorValues().get(1).getUnit().getValue());
            assertEquals("MO", hyb.getFactorValues().get(1).getUnit().getTermSource().getName());
            assertEquals("time", hyb.getFactorValues().get(1).getUnit().getCategory());
        }
        assertEquals("MaterialType", hyb.getFactorValues().get(2).getFactor().getName());
        if (fv3ts != null) {
            assertEquals(fv3ts, hyb.getFactorValues().get(2).getTerm().getTermSource().getName());            
            assertEquals(fv3Value, hyb.getFactorValues().get(2).getTerm().getValue());
            assertNull(hyb.getFactorValues().get(2).getValue());
        } else {
            assertEquals(fv3Value, hyb.getFactorValues().get(2).getValue());
            assertNull(hyb.getFactorValues().get(2).getTerm());
        }
        assertNull(hyb.getFactorValues().get(2).getUnit());
    }

    private void verifyExtendedFactorValuesSampleChars(Sample sample, String sampleName, String c1Value,
            String c2Value, String c2Unit, String c3Value, String c3ts) {
        assertEquals(sampleName, sample.getName());
        assertEquals(3, sample.getCharacteristics().size());
        assertEquals(ExperimentOntologyCategory.EXTERNAL_SAMPLE_ID.getCategoryName(), sample.getCharacteristics()
                .get(0).getCategory());
        assertEquals(c1Value, sample.getCharacteristics().get(0).getValue());
        assertNull(sample.getCharacteristics().get(0).getTerm());
        assertNull(sample.getCharacteristics().get(0).getUnit());
        assertEquals("Age", sample.getCharacteristics().get(1).getCategory());
        assertEquals(c2Value, sample.getCharacteristics().get(1).getValue());
        assertNull(sample.getCharacteristics().get(1).getTerm());
        assertEquals(c2Unit, sample.getCharacteristics().get(1).getUnit().getValue());
        assertEquals("MO", sample.getCharacteristics().get(1).getUnit().getTermSource().getName());
        assertEquals("time", sample.getCharacteristics().get(1).getUnit().getCategory());
        assertEquals("MaterialType", sample.getCharacteristics().get(2).getCategory());
        if (c3ts != null) {
            assertEquals(c3Value, sample.getCharacteristics().get(2).getTerm().getValue());
            assertEquals(c3ts, sample.getCharacteristics().get(2).getTerm().getTermSource().getName());            
            assertNull(sample.getCharacteristics().get(2).getValue());
        } else {
            assertEquals(c3Value, sample.getCharacteristics().get(2).getValue());
            assertNull(sample.getCharacteristics().get(2).getTerm());
        }
        assertNull(sample.getCharacteristics().get(2).getUnit());

    }

    private void verifyExtendedFactorValuesSampleParams(Sample sample, String sampleName, String pv1Value, String pv2Value,
            String pv2ts, String pv3Value, String pv3Unit) {
        assertEquals(sampleName, sample.getName());
        assertEquals(1, sample.getProtocolApplications().size());
        ProtocolApplication pa = sample.getProtocolApplications().get(0);
        assertEquals("Dan1", pa.getProtocol().getName());
        assertEquals(3, pa.getParameterValues().size());
        assertEquals("p1", pa.getParameterValues().get(0).getParameter().getName());
        assertEquals(pv1Value, pa.getParameterValues().get(0).getValue());
        assertNull(pa.getParameterValues().get(0).getTerm());
        assertNull(pa.getParameterValues().get(0).getUnit());
        assertEquals("p2", pa.getParameterValues().get(1).getParameter().getName());
        if (pv2ts != null) {
            assertEquals(pv2ts, pa.getParameterValues().get(1).getTerm().getTermSource().getName());            
            assertEquals(pv2Value, pa.getParameterValues().get(1).getTerm().getValue());
            assertNull(pa.getParameterValues().get(1).getValue());
        } else {
            assertEquals(pv2Value, pa.getParameterValues().get(1).getValue());
            assertNull(pa.getParameterValues().get(1).getTerm());
        }
        assertNull(pa.getParameterValues().get(1).getUnit());
        assertEquals("p3", pa.getParameterValues().get(2).getParameter().getName());
        assertEquals(pv3Value, pa.getParameterValues().get(2).getValue());
        assertNull(pa.getParameterValues().get(2).getTerm());
        assertEquals(pv3Unit, pv3Unit == null ? pa.getParameterValues().get(2).getUnit() : pa.getParameterValues().get(2).getUnit().getValue());
        if (pv3Unit != null){
            assertEquals("MO", pa.getParameterValues().get(2).getUnit().getTermSource().getName());
            assertEquals("weight", pa.getParameterValues().get(2).getUnit().getCategory());
        }
    }

}
