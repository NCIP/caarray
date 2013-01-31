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
import gov.nih.nci.caarray.AbstractCaarrayTest;
import gov.nih.nci.caarray.application.fileaccess.FileAccessServiceStub;
import gov.nih.nci.caarray.application.fileaccess.TemporaryFileCacheLocator;
import gov.nih.nci.caarray.application.fileaccess.TemporaryFileCacheStubFactory;
import gov.nih.nci.caarray.application.translation.CaArrayTranslationResult;
import gov.nih.nci.caarray.business.vocabulary.VocabularyService;
import gov.nih.nci.caarray.business.vocabulary.VocabularyServiceStub;
import gov.nih.nci.caarray.dao.ArrayDao;
import gov.nih.nci.caarray.dao.VocabularyDao;
import gov.nih.nci.caarray.dao.stub.ArrayDaoStub;
import gov.nih.nci.caarray.dao.stub.DaoFactoryStub;
import gov.nih.nci.caarray.dao.stub.VocabularyDaoStub;
import gov.nih.nci.caarray.domain.array.ArrayDesign;
import gov.nih.nci.caarray.domain.contact.Organization;
import gov.nih.nci.caarray.domain.contact.Person;
import gov.nih.nci.caarray.domain.data.AbstractArrayData;
import gov.nih.nci.caarray.domain.data.DerivedArrayData;
import gov.nih.nci.caarray.domain.data.RawArrayData;
import gov.nih.nci.caarray.domain.file.CaArrayFile;
import gov.nih.nci.caarray.domain.file.CaArrayFileSet;
import gov.nih.nci.caarray.domain.file.FileStatus;
import gov.nih.nci.caarray.domain.hybridization.Hybridization;
import gov.nih.nci.caarray.domain.project.Experiment;
import gov.nih.nci.caarray.domain.project.ExperimentContact;
import gov.nih.nci.caarray.domain.sample.LabeledExtract;
import gov.nih.nci.caarray.domain.sample.Sample;
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
 *
 */
@SuppressWarnings("PMD")
public class MageTabTranslatorTest extends AbstractCaarrayTest {

    private MageTabTranslator translator;
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
        MageTabDocumentSet docSet = MageTabParser.INSTANCE.parse(mageTabSet, false);
        assertTrue(docSet.getValidationResult().isValid());        
        CaArrayFileSet fileSet = new CaArrayFileSet();
        for (File file : mageTabSet.getAllFiles()) {
            CaArrayFile caArrayFile = fileAccessServiceStub.add(file);
            caArrayFile.setFileStatus(FileStatus.UPLOADED);
            fileSet.add(caArrayFile);
        }
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
        docSet = MageTabParser.INSTANCE.parse(mageTabSet, false);
        assertTrue(docSet.getValidationResult().isValid());        
        result = this.translator.validate(docSet, fileSet);
        assertTrue(result.isValid());        
    }

    @Test
    public void testSpecificationDocuments() {
        CaArrayFileSet fileSet = TestMageTabSets.getFileSet(TestMageTabSets.MAGE_TAB_SPECIFICATION_SET);
        CaArrayTranslationResult result = this.translator
                .translate(TestMageTabSets.MAGE_TAB_SPECIFICATION_SET, fileSet);
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

    @SuppressWarnings("unchecked")
    @Test
    public void testSpecificationTermCaseSensitivityDocuments() {
        CaArrayFileSet fileSet = TestMageTabSets
                .getFileSet(TestMageTabSets.MAGE_TAB_SPECIFICATION_CASE_SENSITIVITY_SET);
        MageTabTranslationResult result = (MageTabTranslationResult) this.translator.translate(
                TestMageTabSets.MAGE_TAB_SPECIFICATION_CASE_SENSITIVITY_SET, fileSet);
        Collection<Term> terms = result.getTerms();
        Collection<Term> matchingTerms = CollectionUtils.select(terms, new Predicate() {
            public boolean evaluate(Object o) {
                Term t = (Term) o;
                return t.getValue().equalsIgnoreCase("wild_type");
            }
        });
        assertTrue(matchingTerms.size() >= 1);
        Term oneMatch = matchingTerms.iterator().next();
        for (Term eachMatch : matchingTerms) {
            assertTrue(oneMatch == eachMatch);
        }
    }

    @Test
    public void testSpecificationDocumentsNoExpDesc() {
        CaArrayFileSet fileSet = TestMageTabSets.getFileSet(TestMageTabSets.MAGE_TAB_SPECIFICATION_NO_EXP_DESC_SET);
        CaArrayTranslationResult result = this.translator.translate(
                TestMageTabSets.MAGE_TAB_SPECIFICATION_NO_EXP_DESC_SET, fileSet);
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
    public void testSpecificationDocumentsNoArrayDesignRef() {
        CaArrayFileSet fileSet = TestMageTabSets.getFileSet(TestMageTabSets.MAGE_TAB_SPECIFICATION_NO_ARRAY_DESIGN_SET);
        CaArrayTranslationResult result = this.translator.translate(
                TestMageTabSets.MAGE_TAB_SPECIFICATION_NO_ARRAY_DESIGN_SET, fileSet);
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
    public void testSpecificationDocumentsWithDerivedData() {
        CaArrayFileSet fileSet = TestMageTabSets.getFileSet(TestMageTabSets.DERIVED_DATA_SET);
        CaArrayTranslationResult result = this.translator.translate(TestMageTabSets.DERIVED_DATA_SET, fileSet);
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
    public void testTcgaBroadDocuments() {
        CaArrayFileSet fileSet = TestMageTabSets.getFileSet(TestMageTabSets.TCGA_BROAD_SET);
        CaArrayTranslationResult result = this.translator.translate(TestMageTabSets.TCGA_BROAD_SET, fileSet);
        Set<Term> terms = new HashSet<Term>();
        terms.addAll(result.getTerms());
        assertEquals(10, terms.size());
        assertEquals(1, result.getInvestigations().size());
        Experiment investigation = result.getInvestigations().iterator().next();
        checkTcgaBroadInvestigation(investigation);
    }

    private void checkTcgaBroadInvestigation(Experiment investigation) {
        IdfDocument idf = TestMageTabSets.TCGA_BROAD_SET.getIdfDocuments().iterator().next();
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
    public void testGskTestDocuments() {
        CaArrayFileSet fileSet = TestMageTabSets.getFileSet(TestMageTabSets.GSK_TEST_SET);
        CaArrayTranslationResult result = this.translator.translate(TestMageTabSets.GSK_TEST_SET, fileSet);
        Experiment experiment = result.getInvestigations().iterator().next();
        assertEquals(6, experiment.getSources().size());
        assertEquals(6, experiment.getSamples().size());
        assertEquals(6, experiment.getExtracts().size());
        assertEquals(6, experiment.getLabeledExtracts().size());
        assertEquals(6, experiment.getHybridizations().size());
    }

    @Test
    public void testTranslateRawArrayDataWithoutDerivedData() {
        CaArrayFileSet fileSet = TestMageTabSets.getFileSet(TestMageTabSets.PERFORMANCE_TEST_10_SET);
        CaArrayTranslationResult result = this.translator.translate(TestMageTabSets.PERFORMANCE_TEST_10_SET, fileSet);
        assertEquals(1, result.getInvestigations().size());
        Experiment investigation = result.getInvestigations().iterator().next();
        assertEquals(10, investigation.getHybridizations().size());
        checkHybridizationsHaveRawDataAndFiles(investigation.getHybridizations());
    }

    @Test
    public void testTranslatePersonsWithNullAffiliation() {
        CaArrayFileSet fileSet = TestMageTabSets.getFileSet(TestMageTabSets.MAGE_TAB_SPECIFICATION_SET);
        MageTabDocumentSet documentSet = TestMageTabSets.MAGE_TAB_SPECIFICATION_SET;
        documentSet.getIdfDocuments().iterator().next().getInvestigation().getPersons().iterator().next()
                .setAffiliation(null);
        CaArrayTranslationResult result = this.translator.translate(documentSet, fileSet);
        Experiment experiment = result.getInvestigations().iterator().next();
        for (ExperimentContact contact : experiment.getExperimentContacts()) {
            Person person = (Person) contact.getContact();
            for (Organization organization : person.getAffiliations()) {
                assertNotNull(organization);
            }
        }
    }

    @Test
    public void testTranslateBioMaterialSourceDescriptions() {
        CaArrayFileSet fileSet = TestMageTabSets.getFileSet(TestMageTabSets.MAGE_TAB_SPECIFICATION_SET);
        MageTabDocumentSet documentSet = TestMageTabSets.MAGE_TAB_SPECIFICATION_SET;
        SdrfDocument sdrfDocument = documentSet.getSdrfDocuments().iterator().next();
        addDescriptionToBioMaterials(sdrfDocument);
        CaArrayTranslationResult result = this.translator.translate(documentSet, fileSet);
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
    public void testTranslateValid_Feature13141() {
        CaArrayFileSet fileSet = TestMageTabSets.getFileSet(TestMageTabSets.VALID_FEATURE_13141_DATA_SET);
        ValidationResult vResult = this.translator.validate(TestMageTabSets.VALID_FEATURE_13141_DATA_SET, fileSet);
        assertTrue(vResult.getMessages().isEmpty());
        CaArrayTranslationResult tResult = this.translator.translate(TestMageTabSets.VALID_FEATURE_13141_DATA_SET, fileSet);
        assertEquals(1,tResult.getInvestigations().size());
        Experiment e = tResult.getInvestigations().iterator().next();
        assertEquals(6, e.getSamples().size());
        for (Sample s : e.getSamples()) {
            assertNotNull(s.getExternalSampleId());
        }
    }

    @Test
    public void testTranslateInvalid_Feature13141() {
        CaArrayFileSet fileSet = TestMageTabSets.getFileSet(TestMageTabSets.INVALID_FEATURE_13141_DATA_SET);
        ValidationResult vResult = this.translator.validate(TestMageTabSets.INVALID_FEATURE_13141_DATA_SET, fileSet);
        assertFalse(vResult.getMessages().isEmpty());
        assertEquals(3, vResult.getMessages(ValidationMessage.Type.ERROR).size());
        String string = "[ExternalSampleId] value '%s"
                                            + "' is referenced multiple times (ExternalSampleId must be unique). "
                                            + "Please correct and try again.";
        assertEquals(String.format(string, "345"), vResult.getMessages(ValidationMessage.Type.ERROR).get(0).getMessage());
        assertEquals(String.format(string, "234"), vResult.getMessages(ValidationMessage.Type.ERROR).get(1).getMessage());
        assertEquals(String.format(string, "123"), vResult.getMessages(ValidationMessage.Type.ERROR).get(2).getMessage());
        CaArrayTranslationResult result = this.translator.translate(TestMageTabSets.INVALID_FEATURE_13141_DATA_SET, fileSet);

        assertEquals(1,result.getInvestigations().size());
        Experiment e = result.getInvestigations().iterator().next();
        assertEquals(6, e.getSamples().size());
        for (Sample s : e.getSamples()) {
            assertNotNull(s.getExternalSampleId());
        }
    }

    private static class LocalDaoFactoryStub extends DaoFactoryStub {

        @Override
        public VocabularyDao getVocabularyDao() {
            return new LocalVocabularyDaoStub();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public ArrayDao getArrayDao() {
            return new LocalArrayDaoStub();
        }

    }

    private static class LocalVocabularyDaoStub extends VocabularyDaoStub {
        @Override
        public <T> List<T> queryEntityByExample(T entityToMatch, Order... order) {
            return new ArrayList<T>();
        }
    }

    private static class LocalArrayDaoStub extends ArrayDaoStub {
        @SuppressWarnings("unchecked")
        @Override
        public <T extends PersistentObject> List<T> queryEntityAndAssociationsByExample(T entityToMatch,
                Order... orders) {
            if (entityToMatch instanceof ArrayDesign) {
                ArrayDesign arrayDesign = (ArrayDesign) entityToMatch;
                if ("URN:LSID:Affymetrix.com:PhysicalArrayDesign:Test3".equals(arrayDesign.getLsid())) {
                    ArrayList list = new ArrayList();
                    list.add(arrayDesign);
                    return list;
                }
            }
            return super.queryEntityAndAssociationsByExample(entityToMatch, orders);
        }
    }
}
