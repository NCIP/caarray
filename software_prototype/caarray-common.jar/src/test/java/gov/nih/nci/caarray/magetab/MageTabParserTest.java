/**
 * The software subject to this notice and license includes both human readable
 * source code form and machine readable, binary, object code form. The caArray
 * Software was developed in conjunction with the National Cancer Institute
 * (NCI) by NCI employees and 5AM Solutions, Inc. (5AM). To the extent
 * government employees are authors, any rights in such works shall be subject
 * to Title 17 of the United States Code, section 105.
 *
 * This caArray Software License (the License) is between NCI and You. You (or
 * Your) shall mean a person or an entity, and all other entities that control,
 * are controlled by, or are under common control with the entity. Control for
 * purposes of this definition means (i) the direct or indirect power to cause
 * the direction or management of such entity, whether by contract or otherwise,
 * or (ii) ownership of fifty percent (50%) or more of the outstanding shares,
 * or (iii) beneficial ownership of such entity.
 *
 * This License is granted provided that You agree to the conditions described
 * below. NCI grants You a non-exclusive, worldwide, perpetual, fully-paid-up,
 * no-charge, irrevocable, transferable and royalty-free right and license in
 * its rights in the caArray Software to (i) use, install, access, operate,
 * execute, copy, modify, translate, market, publicly display, publicly perform,
 * and prepare derivative works of the caArray Software; (ii) distribute and
 * have distributed to and by third parties the caArray Software and any
 * modifications and derivative works thereof; and (iii) sublicense the
 * foregoing rights set out in (i) and (ii) to third parties, including the
 * right to license such rights to further third parties. For sake of clarity,
 * and not by way of limitation, NCI shall have no right of accounting or right
 * of payment from You or Your sub-licensees for the rights granted under this
 * License. This License is granted at no charge to You.
 *
 * Your redistributions of the source code for the Software must retain the
 * above copyright notice, this list of conditions and the disclaimer and
 * limitation of liability of Article 6, below. Your redistributions in object
 * code form must reproduce the above copyright notice, this list of conditions
 * and the disclaimer of Article 6 in the documentation and/or other materials
 * provided with the distribution, if any.
 *
 * Your end-user documentation included with the redistribution, if any, must
 * include the following acknowledgment: This product includes software
 * developed by 5AM and the National Cancer Institute. If You do not include
 * such end-user documentation, You shall include this acknowledgment in the
 * Software itself, wherever such third-party acknowledgments normally appear.
 *
 * You may not use the names "The National Cancer Institute", "NCI", or "5AM"
 * to endorse or promote products derived from this Software. This License does
 * not authorize You to use any trademarks, service marks, trade names, logos or
 * product names of either NCI or 5AM, except as required to comply with the
 * terms of this License.
 *
 * For sake of clarity, and not by way of limitation, You may incorporate this
 * Software into Your proprietary programs and into any third party proprietary
 * programs. However, if You incorporate the Software into third party
 * proprietary programs, You agree that You are solely responsible for obtaining
 * any permission from such third parties required to incorporate the Software
 * into such third party proprietary programs and for informing Your
 * sub-licensees, including without limitation Your end-users, of their
 * obligation to secure any required permissions from such third parties before
 * incorporating the Software into such third party proprietary software
 * programs. In the event that You fail to obtain such permissions, You agree
 * to indemnify NCI for any claims against NCI by such third parties, except to
 * the extent prohibited by law, resulting from Your failure to obtain such
 * permissions.
 *
 * For sake of clarity, and not by way of limitation, You may add Your own
 * copyright statement to Your modifications and to the derivative works, and
 * You may provide additional or different license terms and conditions in Your
 * sublicenses of modifications of the Software, or any derivative works of the
 * Software as a whole, provided Your use, reproduction, and distribution of the
 * Work otherwise complies with the conditions stated in this License.
 *
 * THIS SOFTWARE IS PROVIDED "AS IS," AND ANY EXPRESSED OR IMPLIED WARRANTIES,
 * (INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY,
 * NON-INFRINGEMENT AND FITNESS FOR A PARTICULAR PURPOSE) ARE DISCLAIMED. IN NO
 * EVENT SHALL THE NATIONAL CANCER INSTITUTE, 5AM SOLUTIONS, INC. OR THEIR
 * AFFILIATES BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS;
 * OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR
 * OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package gov.nih.nci.caarray.magetab;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
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
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.junit.Test;

/**
 * Tests for the MageTabParser subsystem.
 */
@SuppressWarnings("PMD")
public class MageTabParserTest {

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
                "Total RNA was extracted from frozen tissues using the RNeasy mini kit (Qiagen, Valencia, CA) according to the manufacturer�s protocol. The quality of the RNA was assessed by running aliquots on agarose gels.",
                "nucleic_acid_extraction", mo);
        addExpectedProtocol(
                CAARRAY1X_EXPECTED_PROTOCOLS,
                "Calvo's Reverse Transcription",
                "Briefly, 10 �g of RNA from both the reference sample (Pr111) and the experimental samples were reverse transcribed for 1 h at 42�C using Cyanine 3-dUTP for the reference cell line Pr111 or Cyanine 5-dUTP for the experimental samples.",
                "reverse_transcription", mo);
        addExpectedProtocol(
                CAARRAY1X_EXPECTED_PROTOCOLS,
                "Calvo's labeling",
                "Preparation of the cDNA-labeled probes was performed using the Micromax system (NEN Life Science Products), according to the manufacture�s protocol. Briefly, 10 �g of RNA from both the reference sample (Pr111) and the experimental samples were reverse transcribed for 1 h at 42�C using Cyanine 3-dUTP for the reference cell line Pr111 or Cyanine 5-dUTP for the experimental samples.",
                "labeling", mo);
        addExpectedProtocol(
                CAARRAY1X_EXPECTED_PROTOCOLS,
                "Calvo's Hybridization",
                "An equal volume of 2x hybridization solution (50% formamide, 10x SSC, and 0.2% SDS) was added to the cDNA mixture and placed onto the microarray slide for hybridization at 42�C for 16 h.",
                "hybridization", mo);
        addExpectedProtocol(
                CAARRAY1X_EXPECTED_PROTOCOLS,
                "GenePix feature Extraction",
                "The array slides were scanned with an Axon 4000 scanner (Axon Instruments, Foster City, CA) at a resolution of 10 �m.",
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

    @Test
    public void testValidate() throws MageTabParsingException {
        MageTabInputFileSet fileSet = TestMageTabSets.MAGE_TAB_ERROR_SPECIFICATION_INPUT_SET;
        ValidationResult result;
        result = parser.validate(fileSet);
        System.out.println("testValidate result: " + result);
        assertFalse(result.isValid());
        assertEquals(92, result.getMessages().size());
        // check for the fix to gforge defect 12541
        assertTrue(result.getMessages().toString().contains("ERROR: Referenced Factor Name EF1 was not found in the IDF"));
    }

    @Test
    public void testValidateIllegalUnitPlacement() throws MageTabParsingException {
        MageTabInputFileSet fileSet = TestMageTabSets.MAGE_TAB_GEDP_INPUT_SET;
        ValidationResult result;
        result = parser.validate(fileSet);
        assertFalse(result.isValid());
        assertTrue(result.getMessages().toString().contains("ERROR: Illegal Unit column"));
    }

    @Test
    public void testParseEmptySet() throws InvalidDataException, MageTabParsingException {
        MageTabInputFileSet inputFileSet = new MageTabInputFileSet();
        MageTabDocumentSet documentSet = parser.parse(inputFileSet);
        assertNotNull(documentSet);
    }

    @Test
    public void testValidateMissingSdrf() throws MageTabParsingException {
        MageTabInputFileSet inputFileSet = new MageTabInputFileSet();
        inputFileSet.addIdf(MageTabDataFiles.SPECIFICATION_EXAMPLE_IDF);
        ValidationResult result = parser.validate(inputFileSet);
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
    public void testValidateProtocolWithoutType() throws MageTabParsingException {
        MageTabInputFileSet inputFileSet = new MageTabInputFileSet();
        inputFileSet.addIdf(MageTabDataFiles.MISSING_TERMSOURCE_IDF);
        inputFileSet.addSdrf(MageTabDataFiles.MISSING_TERMSOURCE_SDRF);
        ValidationResult result = parser.validate(inputFileSet);
        assertNotNull(result);
    }

    @Test
    public void testValidateMissingTermSources() throws MageTabParsingException {
        MageTabInputFileSet inputFileSet = new MageTabInputFileSet();
        inputFileSet.addIdf(MageTabDataFiles.MISSING_TERMSOURCE_IDF);
        inputFileSet.addSdrf(MageTabDataFiles.MISSING_TERMSOURCE_SDRF);
        ValidationResult result = parser.validate(inputFileSet);
        System.out.println(result);
        assertTrue(result.toString().contains("Term Source not-in-IDF is not defined in the IDF document"));
    }

    @Test
    public void testValidateMultipleIdfs() throws MageTabParsingException {
        MageTabInputFileSet inputFileSet = new MageTabInputFileSet();
        inputFileSet.addIdf(MageTabDataFiles.MISSING_TERMSOURCE_IDF);
        inputFileSet.addSdrf(MageTabDataFiles.MISSING_TERMSOURCE_SDRF);
        inputFileSet.addIdf(MageTabDataFiles.SPECIFICATION_EXAMPLE_IDF);
        ValidationResult result = parser.validate(inputFileSet);
        System.out.println(result);
        assertTrue(result.toString().contains("At most one IDF document can be present in an import"));
    }

    @Test
    public void testValidateMissingDataFiles() throws MageTabParsingException {
        MageTabInputFileSet inputFileSet = new MageTabInputFileSet();
        inputFileSet.addIdf(MageTabDataFiles.SPECIFICATION_EXAMPLE_IDF);
        inputFileSet.addSdrf(MageTabDataFiles.SPECIFICATION_EXAMPLE_SDRF);
        ValidationResult result = parser.validate(inputFileSet);
        assertNotNull(result);
        assertFalse(result.isValid());
        inputFileSet = TestMageTabSets.MAGE_TAB_SPECIFICATION_NO_DATA_MATRIX_INPUT_SET;
        result = parser.validate(inputFileSet);
        assertNotNull(result);
        assertFalse(result.isValid());
        inputFileSet = TestMageTabSets.MAGE_TAB_SPECIFICATION_INPUT_SET;
        result = parser.validate(inputFileSet);
        assertNotNull(result);
        assertTrue(result.isValid());
    }

    @Test
    public void testValidateMisplacedFactorValues() throws MageTabParsingException {
        MageTabInputFileSet inputFileSet = TestMageTabSets.MAGE_TAB_MISPLACED_FACTOR_VALUES_INPUT_SET;
        ValidationResult result = parser.validate(inputFileSet);
        assertNotNull(result);
        assertFalse(result.isValid());
        assertEquals(3, StringUtils.countMatches(result.toString(), "Factor Value columns must come after (to the right of) a Hybridization column"));
        assertFalse(result.toString().contains("Exception"));
    }

    @Test
    public void testParseTcgaBroadDocuments() throws MageTabParsingException, InvalidDataException {
        MageTabInputFileSet fileSet = TestMageTabSets.TCGA_BROAD_INPUT_SET;
        MageTabDocumentSet documentSet = parser.parse(fileSet);
        assertNotNull(documentSet);
        assertEquals(1, documentSet.getIdfDocuments().size());
        assertEquals(1, documentSet.getSdrfDocuments().size());
        assertEquals(1, documentSet.getDataMatrixes().size());
        assertEquals(26, documentSet.getNativeDataFiles().size());
        checkSdrfTranslation(documentSet.getSdrfDocuments().iterator().next());
        checkArrayDesigns(documentSet);
        assertTrue(documentSet.getValidationResult().isValid());
    }
    
    @Test
    public void testDefect12537() throws MageTabParsingException, InvalidDataException {
        MageTabInputFileSet fileSet = TestMageTabSets.DEFECT_12537_ERROR_INPUT_SET;
        ValidationResult validationResult = parser.validate(fileSet);
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
        MageTabInputFileSet fileSet = TestMageTabSets.CAARRAY1X_INPUT_SET;
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
        MageTabInputFileSet fileSet = TestMageTabSets.MAGE_TAB_UNSUPPORTED_DATA_INPUT_SET;
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
        MageTabInputFileSet fileSet = TestMageTabSets.MAGE_TAB_SPECIFICATION_INPUT_SET;
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
        assertEquals(SIX, sdrfDocument.getAllSamples().size());
        assertEquals(SIX, sdrfDocument.getAllExtracts().size());
        assertEquals(SIX, sdrfDocument.getAllHybridizations().size());
        assertEquals(SIX, sdrfDocument.getAllLabeledExtracts().size());
        assertEquals(1, sdrfDocument.getAllHybridizations().get(0).getSuccessorArrayDataFiles().size());
        ArrayDataFile arrayDataFile = sdrfDocument.getAllHybridizations().get(0).getSuccessorArrayDataFiles()
                .iterator().next();
        assertNotNull(arrayDataFile);
        assertEquals("H_TK6 replicate 1.CEL", arrayDataFile.getName());
    }

    private void checkTerms(MageTabDocumentSet documentSet) {
        for (OntologyTerm term : documentSet.getTerms()) {
            assertNotNull("null category for term " + term.getValue(), term.getCategory());
            assertNotNull("null value for term", term.getValue());
        }
    }

}