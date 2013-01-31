//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.application.translation.magetab;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import gov.nih.nci.caarray.application.AbstractServiceTest;
import gov.nih.nci.caarray.application.fileaccess.FileAccessServiceStub;
import gov.nih.nci.caarray.application.fileaccess.TemporaryFileCacheLocator;
import gov.nih.nci.caarray.application.fileaccess.TemporaryFileCacheStubFactory;
import gov.nih.nci.caarray.application.translation.CaArrayTranslationResult;
import gov.nih.nci.caarray.application.vocabulary.VocabularyService;
import gov.nih.nci.caarray.application.vocabulary.VocabularyServiceStub;
import gov.nih.nci.caarray.dao.ArrayDao;
import gov.nih.nci.caarray.dao.stub.ArrayDaoStub;
import gov.nih.nci.caarray.dao.stub.DaoFactoryStub;
import gov.nih.nci.caarray.domain.array.ArrayDesign;
import gov.nih.nci.caarray.domain.contact.Organization;
import gov.nih.nci.caarray.domain.data.AbstractArrayData;
import gov.nih.nci.caarray.domain.data.DerivedArrayData;
import gov.nih.nci.caarray.domain.data.RawArrayData;
import gov.nih.nci.caarray.domain.file.CaArrayFileSet;
import gov.nih.nci.caarray.domain.hybridization.Hybridization;
import gov.nih.nci.caarray.domain.project.Experiment;
import gov.nih.nci.caarray.domain.project.ExperimentContact;
import gov.nih.nci.caarray.domain.project.MeasurementFactorValue;
import gov.nih.nci.caarray.domain.project.TermBasedFactorValue;
import gov.nih.nci.caarray.domain.project.UserDefinedFactorValue;
import gov.nih.nci.caarray.domain.protocol.MeasurementParameterValue;
import gov.nih.nci.caarray.domain.protocol.ProtocolApplication;
import gov.nih.nci.caarray.domain.protocol.TermBasedParameterValue;
import gov.nih.nci.caarray.domain.protocol.UserDefinedParameterValue;
import gov.nih.nci.caarray.domain.sample.LabeledExtract;
import gov.nih.nci.caarray.domain.sample.MeasurementCharacteristic;
import gov.nih.nci.caarray.domain.sample.Sample;
import gov.nih.nci.caarray.domain.sample.TermBasedCharacteristic;
import gov.nih.nci.caarray.domain.sample.UserDefinedCharacteristic;
import gov.nih.nci.caarray.domain.search.ExampleSearchCriteria;
import gov.nih.nci.caarray.domain.vocabulary.Term;
import gov.nih.nci.caarray.magetab.MageTabDocumentSet;
import gov.nih.nci.caarray.magetab.MageTabFileSet;
import gov.nih.nci.caarray.magetab.MageTabParser;
import gov.nih.nci.caarray.magetab.MageTabParsingException;
import gov.nih.nci.caarray.magetab.TestMageTabSets;
import gov.nih.nci.caarray.magetab.idf.IdfDocument;
import gov.nih.nci.caarray.magetab.sdrf.AbstractBioMaterial;
import gov.nih.nci.caarray.magetab.sdrf.SdrfDocument;
import gov.nih.nci.caarray.test.data.arraydata.GenepixArrayDataFiles;
import gov.nih.nci.caarray.test.data.magetab.MageTabDataFiles;
import gov.nih.nci.caarray.util.j2ee.ServiceLocatorStub;
import gov.nih.nci.caarray.validation.FileValidationResult;
import gov.nih.nci.caarray.validation.InvalidDataException;
import gov.nih.nci.caarray.validation.ValidationMessage;
import gov.nih.nci.caarray.validation.ValidationResult;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.hibernate.criterion.Order;
import org.junit.Before;
import org.junit.Test;

import com.fiveamsolutions.nci.commons.data.persistent.PersistentObject;

/**
 * Test for MAGE tab translator
 */
@SuppressWarnings("PMD")
public class MageTabTranslatorTest extends AbstractServiceTest {

    MageTabTranslator translator;
    private final LocalDaoFactoryStub daoFactoryStub = new LocalDaoFactoryStub();
    private final VocabularyServiceStub vocabularyServiceStub = new VocabularyServiceStub();
    private FileAccessServiceStub fileAccessServiceStub;

    /**
     * Prepares the translator implementation, stubbing out dependencies.
     */
    @Before
    public void setupTranslator() {
        MageTabTranslatorBean mageTabTranslatorBean = new MageTabTranslatorBean();
        mageTabTranslatorBean.setDaoFactory(this.daoFactoryStub);
        ServiceLocatorStub locatorStub = ServiceLocatorStub.registerEmptyLocator();
        fileAccessServiceStub = new FileAccessServiceStub();
        TemporaryFileCacheLocator
                .setTemporaryFileCacheFactory(new TemporaryFileCacheStubFactory(fileAccessServiceStub));
        locatorStub.addLookup(VocabularyService.JNDI_NAME, this.vocabularyServiceStub);
        this.translator = mageTabTranslatorBean;
    }

    @Test
    public void testDefect17200() throws InvalidDataException, MageTabParsingException {
        MageTabFileSet mageTabSet = TestMageTabSets.DEFECT_17200;
        for (File f : mageTabSet.getAllFiles()) {
            fileAccessServiceStub.add(f);
        }
        MageTabDocumentSet docSet = MageTabParser.INSTANCE.parse(mageTabSet);
        assertTrue(docSet.getValidationResult().isValid());
        CaArrayFileSet fileSet = TestMageTabSets.getFileSet(TestMageTabSets.DEFECT_17200);
        ValidationResult result = this.translator.validate(docSet, fileSet);
        assertFalse(result.isValid());
        FileValidationResult fileResult = result.getFileValidationResult(MageTabDataFiles.DEFECT_17200_GPR);
        assertNotNull(fileResult);
        assertFalse(result.isValid());
        assertEquals(1, fileResult.getMessages().size());
        assertTrue(fileResult.getMessages().get(0).getMessage().startsWith("This file is not correctly referenced"));
        fileResult = result.getFileValidationResult(GenepixArrayDataFiles.GPR_3_0_6);
        assertNotNull(fileResult);
        assertFalse(result.isValid());
        assertEquals(1, fileResult.getMessages().size());
        assertTrue(fileResult.getMessages().get(0).getMessage().startsWith("This data file is not referenced from "));

        mageTabSet = new MageTabFileSet();
        mageTabSet.addNativeData(MageTabDataFiles.DEFECT_17200_GPR);
        fileSet = new CaArrayFileSet();
        fileSet.add(fileAccessServiceStub.add(MageTabDataFiles.DEFECT_17200_GPR));
        docSet = MageTabParser.INSTANCE.parse(mageTabSet);
        assertTrue(docSet.getValidationResult().isValid());
        result = this.translator.validate(docSet, fileSet);
        assertTrue(result.isValid());
    }

    @Test
    public void testSpecificationDocuments() throws Exception {
        CaArrayFileSet fileSet = TestMageTabSets.getFileSet(TestMageTabSets.MAGE_TAB_SPECIFICATION_INPUT_SET);
        MageTabDocumentSet docSet = MageTabParser.INSTANCE.parse(TestMageTabSets.MAGE_TAB_SPECIFICATION_INPUT_SET);
        CaArrayTranslationResult result = this.translator.translate(docSet, fileSet);
        Experiment experiment = result.getInvestigations().iterator().next();
        assertNotNull(experiment.getDescription());
        assertTrue(experiment.getDescription().startsWith("&lt;&gt;Gene expression of TK6"));
        assertEquals(8, experiment.getExperimentContacts().size());
        assertEquals(1, experiment.getExperimentDesignTypes().size());
        assertEquals("genetic_modification_design", experiment.getExperimentDesignTypes().iterator().next().getValue());
        assertEquals(6, experiment.getSources().size());
        assertEquals(6, experiment.getSamples().size());
        assertEquals(6, experiment.getExtracts().size());
        assertEquals(6, experiment.getLabeledExtracts().size());
        assertEquals(6, experiment.getHybridizations().size());
        for (Hybridization hyb : experiment.getHybridizations()) {
            assertNotNull(hyb.getArray().getDesign());
            assertEquals(6, hyb.getDerivedDataCollection().iterator().next().getDerivedFromArrayDataCollection().size());
        }
    }

    @Test
    public void testSpecificationTermCaseSensitivityDocuments() throws Exception {
        CaArrayFileSet fileSet = TestMageTabSets
                .getFileSet(TestMageTabSets.MAGE_TAB_SPECIFICATION_CASE_SENSITIVITY_INPUT_SET);
        MageTabDocumentSet docSet = MageTabParser.INSTANCE
                .parse(TestMageTabSets.MAGE_TAB_SPECIFICATION_CASE_SENSITIVITY_INPUT_SET);
        CaArrayTranslationResult result = this.translator.translate(docSet, fileSet);
        Collection<Term> terms = result.getTerms();
        @SuppressWarnings("unchecked")
        Collection<Term> matchingTerms = CollectionUtils.select(terms, new Predicate() {
            public boolean evaluate(Object o) {
                Term t = (Term) o;
                return t.getValue().equalsIgnoreCase("fresh_sample");
            }
        });
        assertTrue(matchingTerms.size() >= 1);
        Term oneMatch = matchingTerms.iterator().next();
        for (Term eachMatch : matchingTerms) {
            assertTrue(oneMatch == eachMatch);
        }
    }

    @Test
    public void testSpecificationDocumentsNoExpDesc() throws Exception {
        CaArrayFileSet fileSet = TestMageTabSets
                .getFileSet(TestMageTabSets.MAGE_TAB_SPECIFICATION_NO_EXP_DESC_INPUT_SET);
        MageTabDocumentSet docSet = MageTabParser.INSTANCE
                .parse(TestMageTabSets.MAGE_TAB_SPECIFICATION_NO_EXP_DESC_INPUT_SET);
        CaArrayTranslationResult result = this.translator.translate(docSet, fileSet);
        Experiment experiment = result.getInvestigations().iterator().next();
        assertNull(experiment.getDescription());
        assertEquals(8, experiment.getExperimentContacts().size());
        assertEquals(1, experiment.getExperimentDesignTypes().size());
        assertEquals("genetic_modification_design", experiment.getExperimentDesignTypes().iterator().next().getValue());
        assertEquals(6, experiment.getSources().size());
        assertEquals(6, experiment.getSamples().size());
        assertEquals(6, experiment.getExtracts().size());
        assertEquals(6, experiment.getLabeledExtracts().size());
        assertEquals(6, experiment.getHybridizations().size());
        for (Hybridization hyb : experiment.getHybridizations()) {
            assertNotNull(hyb.getArray().getDesign());
        }
    }

    @Test
    public void testSpecificationDocumentsNoArrayDesignRef() throws Exception {
        CaArrayFileSet fileSet = TestMageTabSets
                .getFileSet(TestMageTabSets.MAGE_TAB_SPECIFICATION_NO_ARRAY_DESIGN_INPUT_SET);
        MageTabDocumentSet docSet = MageTabParser.INSTANCE
                .parse(TestMageTabSets.MAGE_TAB_SPECIFICATION_NO_ARRAY_DESIGN_INPUT_SET);
        CaArrayTranslationResult result = this.translator.translate(docSet, fileSet);
        Experiment experiment = result.getInvestigations().iterator().next();
        assertNotNull(experiment.getDescription());
        assertTrue(experiment.getDescription().startsWith("&lt;&gt;Gene expression of TK6"));
        assertFalse(experiment.getDescription().contains("<"));
        assertEquals(8, experiment.getExperimentContacts().size());
        assertEquals(1, experiment.getExperimentDesignTypes().size());
        assertEquals("genetic_modification_design", experiment.getExperimentDesignTypes().iterator().next().getValue());
        assertEquals(6, experiment.getSources().size());
        assertEquals(6, experiment.getSamples().size());
        assertEquals(6, experiment.getExtracts().size());
        assertEquals(6, experiment.getLabeledExtracts().size());
        assertEquals(6, experiment.getHybridizations().size());
        for (Hybridization hyb : experiment.getHybridizations()) {
            assertNull(hyb.getArray().getDesign().getName());
        }
    }

    @Test
    public void testSpecificationDocumentsWithDerivedData() throws Exception {
        CaArrayFileSet fileSet = TestMageTabSets.getFileSet(TestMageTabSets.DERIVED_DATA_INPUT_SET);
        MageTabDocumentSet docSet = MageTabParser.INSTANCE.parse(TestMageTabSets.DERIVED_DATA_INPUT_SET);
        CaArrayTranslationResult result = this.translator.translate(docSet, fileSet);
        Experiment experiment = result.getInvestigations().iterator().next();
        assertNotNull(experiment.getDescription());
        assertTrue(experiment.getDescription().startsWith("Gene expression of TK6"));
        assertEquals(8, experiment.getExperimentContacts().size());
        assertEquals(1, experiment.getExperimentDesignTypes().size());
        assertEquals("genetic_modification_design", experiment.getExperimentDesignTypes().iterator().next().getValue());
        assertEquals(1, experiment.getSources().size());
        assertEquals(1, experiment.getSamples().size());
        assertEquals(1, experiment.getExtracts().size());
        assertEquals(1, experiment.getLabeledExtracts().size());
        assertEquals(1, experiment.getHybridizations().size());
        Hybridization hyb = experiment.getHybridizations().iterator().next();
        assertNotNull(hyb.getArray().getDesign());
        assertEquals(2, hyb.getDerivedDataCollection().size());
        Iterator<DerivedArrayData> derivedArrayDataIterator = hyb.getDerivedDataCollection().iterator();
        DerivedArrayData derivedData = derivedArrayDataIterator.next();
        DerivedArrayData derivedData2 = derivedArrayDataIterator.next();
        assertEquals(1, derivedData.getDerivedFromArrayDataCollection().size());
        assertEquals(1, derivedData2.getDerivedFromArrayDataCollection().size());
        AbstractArrayData derivedFrom = derivedData.getDerivedFromArrayDataCollection().iterator().next();
        AbstractArrayData derivedFrom2 = derivedData2.getDerivedFromArrayDataCollection().iterator().next();

        // not sure which order the derived from objects are returned - one derived data is derived from
        // raw data and the other is derived from the other derived data, but we don't know which derived data
        // is first
        if (derivedFrom instanceof RawArrayData) {
            assertEquals(hyb.getRawDataCollection().iterator().next(), derivedFrom);
            assertEquals(derivedData, derivedFrom2);
        } else {
            assertEquals(hyb.getRawDataCollection().iterator().next(), derivedFrom2);
            assertEquals(derivedData2, derivedFrom);
        }

    }

    @Test
    public void testTcgaBroadDocuments() throws Exception {
        CaArrayFileSet fileSet = TestMageTabSets.getFileSet(TestMageTabSets.TCGA_BROAD_INPUT_SET);
        MageTabDocumentSet docSet = MageTabParser.INSTANCE.parse(TestMageTabSets.TCGA_BROAD_INPUT_SET);
        CaArrayTranslationResult result = this.translator.translate(docSet, fileSet);

        Set<Term> terms = new HashSet<Term>();
        terms.addAll(result.getTerms());
        assertEquals(10, terms.size());

        assertEquals(1, result.getInvestigations().size());
        Experiment investigation = result.getInvestigations().iterator().next();
        IdfDocument idf = docSet.getIdfDocuments().iterator().next();
        assertEquals(idf.getInvestigation().getTitle(), investigation.getTitle());

        checkTcgaBroadBioMaterials(investigation);
        checkTcgaBroadHybridizations(investigation);
    }

    private void checkTcgaBroadHybridizations(Experiment investigation) {
        assertEquals(1, investigation.getArrayDesigns().size());
        ArrayDesign arrayDesign = investigation.getArrayDesigns().iterator().next();
        for (LabeledExtract labeledExtract : investigation.getLabeledExtracts()) {
            Hybridization hybridization = labeledExtract.getHybridizations().iterator().next();
            assertEquals(arrayDesign, hybridization.getArray().getDesign());
            RawArrayData celData = hybridization.getRawDataCollection().iterator().next();
            assertEquals(celData.getDataFile().getName(), celData.getName());
        }
        Set<Hybridization> hybridizations = investigation.getHybridizations();
        assertEquals(26, hybridizations.size());
        checkHybridizationsHaveRawDataAndFiles(hybridizations);
    }

    private void checkTcgaBroadBioMaterials(Experiment investigation) {
        assertEquals(26, investigation.getSources().size());
        assertEquals(26, investigation.getSamples().size());
        assertEquals(26, investigation.getExtracts().size());
        assertEquals(26, investigation.getLabeledExtracts().size());
    }

    @Test
    public void testGskTestDocuments() throws Exception {
        CaArrayFileSet fileSet = TestMageTabSets.getFileSet(TestMageTabSets.GSK_TEST_INPUT_SET);
        MageTabDocumentSet docSet = MageTabParser.INSTANCE.parse(TestMageTabSets.GSK_TEST_INPUT_SET);
        CaArrayTranslationResult result = this.translator.translate(docSet, fileSet);
        Experiment experiment = result.getInvestigations().iterator().next();
        assertEquals(6, experiment.getSources().size());
        assertEquals(6, experiment.getSamples().size());
        assertEquals(6, experiment.getExtracts().size());
        assertEquals(6, experiment.getLabeledExtracts().size());
        assertEquals(6, experiment.getHybridizations().size());
    }

    @Test
    public void testTranslateRawArrayDataWithoutDerivedData() throws Exception {
        CaArrayFileSet fileSet = TestMageTabSets.getFileSet(TestMageTabSets.PERFORMANCE_TEST_10_INPUT_SET);
        MageTabDocumentSet docSet = MageTabParser.INSTANCE.parse(TestMageTabSets.PERFORMANCE_TEST_10_INPUT_SET);
        CaArrayTranslationResult result = this.translator.translate(docSet, fileSet);
        assertEquals(1, result.getInvestigations().size());
        Experiment investigation = result.getInvestigations().iterator().next();
        assertEquals(10, investigation.getHybridizations().size());
        checkHybridizationsHaveRawDataAndFiles(investigation.getHybridizations());
    }

    @Test
    public void testTranslatePersonsWithNullAffiliation() throws Exception {
        CaArrayFileSet fileSet = TestMageTabSets.getFileSet(TestMageTabSets.MAGE_TAB_SPECIFICATION_INPUT_SET);
        MageTabDocumentSet docSet = MageTabParser.INSTANCE.parse(TestMageTabSets.MAGE_TAB_SPECIFICATION_INPUT_SET);
        docSet.getIdfDocuments().iterator().next().getInvestigation().getPersons().iterator().next().setAffiliation(
                null);
        CaArrayTranslationResult result = this.translator.translate(docSet, fileSet);
        Experiment experiment = result.getInvestigations().iterator().next();
        for (ExperimentContact contact : experiment.getExperimentContacts()) {
            for (Organization organization : contact.getContact().getAffiliations()) {
                assertNotNull(organization);
            }
        }
    }

    @Test
    public void testTranslateBioMaterialSourceDescriptions() throws Exception {
        CaArrayFileSet fileSet = TestMageTabSets.getFileSet(TestMageTabSets.MAGE_TAB_SPECIFICATION_INPUT_SET);
        MageTabDocumentSet docSet = MageTabParser.INSTANCE.parse(TestMageTabSets.MAGE_TAB_SPECIFICATION_INPUT_SET);
        SdrfDocument sdrfDocument = docSet.getSdrfDocuments().iterator().next();
        addDescriptionToBioMaterials(sdrfDocument);
        CaArrayTranslationResult result = this.translator.translate(docSet, fileSet);
        Experiment experiment = result.getInvestigations().iterator().next();
        checkDescription(experiment.getSources(), "Source description");
        checkDescription(experiment.getSamples(), "Sample description");
        checkDescription(experiment.getExtracts(), "Extract description");
        checkDescription(experiment.getLabeledExtracts(), "LabeledExtract description");
    }

    private void checkDescription(Set<? extends gov.nih.nci.caarray.domain.sample.AbstractBioMaterial> materials,
            String description) {
        for (gov.nih.nci.caarray.domain.sample.AbstractBioMaterial material : materials) {
            assertEquals(description, material.getDescription());
        }
    }

    private void addDescriptionToBioMaterials(SdrfDocument sdrfDocument) {
        addDescriptionToBioMaterials(sdrfDocument.getAllSources(), "Source description");
        addDescriptionToBioMaterials(sdrfDocument.getAllSamples(), "Sample description");
        addDescriptionToBioMaterials(sdrfDocument.getAllExtracts(), "Extract description");
        addDescriptionToBioMaterials(sdrfDocument.getAllLabeledExtracts(), "LabeledExtract description");
    }

    private void addDescriptionToBioMaterials(List<? extends AbstractBioMaterial> materials, String description) {
        for (AbstractBioMaterial material : materials) {
            material.setDescription(description);
        }
    }

    private void checkHybridizationsHaveRawDataAndFiles(Set<Hybridization> hybridizations) {
        for (Hybridization hybridization : hybridizations) {
            assertFalse(hybridization.getRawDataCollection().isEmpty());
            assertNotNull(hybridization.getRawDataCollection().iterator().next().getDataFile());
        }
    }

    @Test
    public void testTranslateValid_Feature13141() throws Exception {
        CaArrayFileSet fileSet = TestMageTabSets.getFileSet(TestMageTabSets.VALID_FEATURE_13141_INPUT_SET);
        MageTabDocumentSet docSet = MageTabParser.INSTANCE.parse(TestMageTabSets.VALID_FEATURE_13141_INPUT_SET);
        ValidationResult vResult = this.translator.validate(docSet, fileSet);
        assertTrue(vResult.getMessages().isEmpty());
        CaArrayTranslationResult tResult = this.translator.translate(docSet, fileSet);
        assertEquals(1,tResult.getInvestigations().size());
        Experiment e = tResult.getInvestigations().iterator().next();
        assertEquals(6, e.getSamples().size());
        for (Sample s : e.getSamples()) {
            assertNotNull(s.getExternalId());
        }
    }

    @Test
    public void testTranslateInvalid_Feature13141() throws Exception {
        CaArrayFileSet fileSet = TestMageTabSets.getFileSet(TestMageTabSets.INVALID_FEATURE_13141_INPUT_SET);
        MageTabDocumentSet docSet = MageTabParser.INSTANCE.parse(TestMageTabSets.INVALID_FEATURE_13141_INPUT_SET);
        ValidationResult vResult = this.translator.validate(docSet, fileSet);
        assertFalse(vResult.getMessages().isEmpty());
        assertEquals(3, vResult.getMessages(ValidationMessage.Type.ERROR).size());
        String string = "[ExternalSampleId] value '%s"
                                            + "' is referenced multiple times (ExternalSampleId must be unique). "
                                            + "Please correct and try again.";
        assertEquals(String.format(string, "345"), vResult.getMessages(ValidationMessage.Type.ERROR).get(0).getMessage());
        assertEquals(String.format(string, "234"), vResult.getMessages(ValidationMessage.Type.ERROR).get(1).getMessage());
        assertEquals(String.format(string, "123"), vResult.getMessages(ValidationMessage.Type.ERROR).get(2).getMessage());
        CaArrayTranslationResult result = this.translator.translate(docSet, fileSet);

        assertEquals(1,result.getInvestigations().size());
        Experiment e = result.getInvestigations().iterator().next();
        assertEquals(6, e.getSamples().size());
        for (Sample s : e.getSamples()) {
            assertNotNull(s.getExternalId());
        }
    }

    @Test
    public void testValidateInvalidDuplicateTermSources() throws Exception {
        CaArrayFileSet fileSet = TestMageTabSets.getFileSet(TestMageTabSets.INVALID_DUPLICATE_TERM_SOURCES_INPUT_SET);
        MageTabDocumentSet docSet = MageTabParser.INSTANCE
                .parse(TestMageTabSets.INVALID_DUPLICATE_TERM_SOURCES_INPUT_SET);
        ValidationResult validationResult = this.translator.validate(docSet, fileSet);
        assertFalse(validationResult.getMessages().isEmpty());
        assertEquals(7, validationResult.getMessages(ValidationMessage.Type.ERROR).size());
        String prototypeString =
            "Redundant term source named '%s'. Term sources cannot have "
            + "the same URL unless they have different versions, even if "
            + "their names are different.";
        assertEquals(String.format(prototypeString, "LM_3"), validationResult.getMessages(ValidationMessage.Type.ERROR).get(0).getMessage());
        assertEquals(String.format(prototypeString, "LM_2"), validationResult.getMessages(ValidationMessage.Type.ERROR).get(1).getMessage());
        assertEquals(String.format(prototypeString, "LM_1"), validationResult.getMessages(ValidationMessage.Type.ERROR).get(2).getMessage());
        assertEquals(String.format(prototypeString, "LM"), validationResult.getMessages(ValidationMessage.Type.ERROR).get(3).getMessage());
        assertEquals(String.format(prototypeString, "FOO2"), validationResult.getMessages(ValidationMessage.Type.ERROR).get(4).getMessage());
        assertEquals(String.format(prototypeString, "FOO"), validationResult.getMessages(ValidationMessage.Type.ERROR).get(5).getMessage());
        assertEquals("Duplicate term source name 'FOO'.", validationResult.getMessages(ValidationMessage.Type.ERROR).get(6).getMessage());
    }
    
    @Test
    public void testExtendedFactorValues() throws Exception {
        CaArrayFileSet fileSet = TestMageTabSets.getFileSet(TestMageTabSets.EXTENDED_FACTOR_VALUES_INPUT_SET);
        MageTabDocumentSet docSet = MageTabParser.INSTANCE.parse(TestMageTabSets.EXTENDED_FACTOR_VALUES_INPUT_SET);
        CaArrayTranslationResult result = this.translator.translate(docSet, fileSet);
        Experiment experiment = result.getInvestigations().iterator().next();
        assertEquals(3, experiment.getFactors().size());
        assertEquals(3, experiment.getSamples().size());
        assertEquals(3, experiment.getHybridizations().size());
        verifyExtendedFactorValuesSampleChars(experiment.getSampleByName("Sample A"), "123", 5f, null, "years",
                "tissue", "MO");
        verifyExtendedFactorValuesSampleChars(experiment.getSampleByName("Sample B"), "234", null, "a lot", "months",
                "tissue", "MO");
        verifyExtendedFactorValuesSampleChars(experiment.getSampleByName("Sample C"), "345", 2.2f, null, "days",
                "unknown", null);
        verifyExtendedFactorValuesSampleParams(experiment.getSampleByName("Sample A"), "foo", 4f, null, "kg",
                "planting", "MO");
        verifyExtendedFactorValuesSampleParams(experiment.getSampleByName("Sample B"), "baz", null, "4", null,
                "planting", "MO");
        verifyExtendedFactorValuesSampleParams(experiment.getSampleByName("Sample C"), "foo", null, "less", "mg",
                "nothing", null);
        verifyExtendedFactorValuesHyb(experiment.getHybridizationByName("Hyb A"), "123", 5f, null, "years", "tissue",
                "MO");
        verifyExtendedFactorValuesHyb(experiment.getHybridizationByName("Hyb B"), "234", null, "a lot", "months",
                "tissue", "MO");
        verifyExtendedFactorValuesHyb(experiment.getHybridizationByName("Hyb C"), "345", null, "2.2", null, "unknown",
                null);
    }

    private void verifyExtendedFactorValuesHyb(Hybridization hyb, String fv1Value, Float fv2Num,
            String fv2String, String fv2Unit, String fv3Value, String fv3ts) {
        assertEquals(3, hyb.getFactorValues().size());
        UserDefinedFactorValue fv1 = (UserDefinedFactorValue) hyb.getFactorValue("ExternalSampleId");
        assertEquals(fv1Value, fv1.getValue());
        assertNull(fv1.getUnit());
        if (fv2Num != null) {
            MeasurementFactorValue fv2 = (MeasurementFactorValue) hyb.getFactorValue("Age");            
            assertEquals(fv2Num, fv2.getValue());
            assertEquals(fv2Unit, fv2Unit == null ? fv2.getUnit() : fv2.getUnit().getValue());
            if (fv2Unit != null)
                assertEquals("MO", fv2.getUnit().getSource().getName());
        } else {
            UserDefinedFactorValue fv2 = (UserDefinedFactorValue) hyb.getFactorValue("Age");
            assertEquals(fv2String, fv2.getValue());
            assertEquals(fv2Unit, fv2Unit == null ? fv2.getUnit() : fv2.getUnit().getValue());
            if (fv2Unit != null)
                assertEquals("MO", fv2.getUnit().getSource().getName());
        }
        if (fv3ts != null) {
            TermBasedFactorValue fv3 = (TermBasedFactorValue) hyb.getFactorValue("MaterialType");
            assertEquals(fv3Value, fv3.getTerm().getValue());
            assertEquals(fv3ts, fv3.getTerm().getSource().getName());
            assertNull(fv3.getUnit());
        } else {
            UserDefinedFactorValue fv3 = (UserDefinedFactorValue) hyb.getFactorValue("MaterialType");
            assertEquals(fv3Value, fv3.getValue());
            assertNull(fv3.getUnit());
        }
    }

    private void verifyExtendedFactorValuesSampleChars(Sample s, String c1Value, Float c2Num,
            String c2String, String c2Unit, String c3Value, String c3ts) {
        assertEquals(2, s.getCharacteristics().size());
        assertEquals(c1Value, s.getExternalId());
        if (c2Num != null) {
            MeasurementCharacteristic c2 = (MeasurementCharacteristic) s.getCharacteristic("Age");            
            assertEquals(c2Num, c2.getValue());
            assertEquals(c2Unit, c2.getUnit().getValue());
            assertEquals("MO", c2.getUnit().getSource().getName());
        } else {
            UserDefinedCharacteristic c2 = (UserDefinedCharacteristic) s.getCharacteristic("Age");
            assertEquals(c2String, c2.getValue());
            assertEquals(c2Unit, c2.getUnit().getValue());
            assertEquals("MO", c2.getUnit().getSource().getName());
        }
        if (c3ts != null) {            
            TermBasedCharacteristic c3 = (TermBasedCharacteristic) s.getCharacteristic("MaterialType");
            assertEquals(c3Value, c3.getTerm().getValue());
            assertEquals(c3ts, c3.getTerm().getSource().getName());
            assertNull(c3.getUnit());
        } else {
            UserDefinedCharacteristic c3 = (UserDefinedCharacteristic) s.getCharacteristic("MaterialType");
            assertEquals(c3Value, c3.getValue());
            assertNull(c3.getUnit());
        }
    }

    private void verifyExtendedFactorValuesSampleParams(Sample s, String pv1Value, Float pv2Num,
            String pv2String, String pv2Unit, String pv3Value, String pv3ts) {        
        assertEquals(1, s.getProtocolApplications().size());
        ProtocolApplication pa = s.getProtocolApplications().get(0);
        assertEquals("Dan1", pa.getProtocol().getName());
        assertEquals(3, pa.getValues().size());
        UserDefinedParameterValue pv1 = (UserDefinedParameterValue) pa.getValue("p1");
        assertNotNull(pv1);
        assertEquals(pv1Value, pv1.getValue());
        assertNull(pv1.getUnit());
        if (pv2Num != null) {
            MeasurementParameterValue pv2 = (MeasurementParameterValue) pa.getValue("p3");            
            assertEquals(pv2Num, pv2.getValue());
            assertEquals(pv2Unit, pv2Unit == null ? pv2.getUnit() : pv2.getUnit().getValue());
            if (pv2Unit != null)
                assertEquals("MO", pv2.getUnit().getSource().getName());
        } else {
            UserDefinedParameterValue pv2 = (UserDefinedParameterValue) pa.getValue("p3");
            assertEquals(pv2String, pv2.getValue());
            assertEquals(pv2Unit, pv2Unit == null ? pv2.getUnit() : pv2.getUnit().getValue());
            if (pv2Unit != null)
                assertEquals("MO", pv2.getUnit().getSource().getName());
        }
        if (pv3ts != null) {            
            TermBasedParameterValue pv3 = (TermBasedParameterValue) pa.getValue("p2");
            assertEquals(pv3Value, pv3.getTerm().getValue());
            assertEquals(pv3ts, pv3.getTerm().getSource().getName());
            assertNull(pv3.getUnit());
        } else {
            UserDefinedParameterValue pv3 = (UserDefinedParameterValue) pa.getValue("p2");
            assertEquals(pv3Value, pv3.getValue());
            assertNull(pv3.getUnit());
        }
    }

    private static class LocalDaoFactoryStub extends DaoFactoryStub {
        /**
         * {@inheritDoc}
         */
        @Override
        public ArrayDao getArrayDao() {
            return new LocalArrayDaoStub();
        }

    }

    private static class LocalArrayDaoStub extends ArrayDaoStub {
        @SuppressWarnings("unchecked")
        @Override
        public <T extends PersistentObject> List<T> queryEntityByExample(ExampleSearchCriteria<T> criteria,
                Order... orders) {
            T entityToMatch = criteria.getExample();
            if (entityToMatch instanceof ArrayDesign) {
                ArrayDesign arrayDesign = (ArrayDesign) entityToMatch;
                if ("URN:LSID:Affymetrix.com:PhysicalArrayDesign:Test3".equals(arrayDesign.getLsid())) {
                    ArrayList list = new ArrayList();
                    list.add(arrayDesign);
                    return list;
                }
            }
            return super.queryEntityByExample(criteria, orders);
        }
    }
}
