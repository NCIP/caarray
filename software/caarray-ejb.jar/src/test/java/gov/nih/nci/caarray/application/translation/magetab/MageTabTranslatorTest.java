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
package gov.nih.nci.caarray.application.translation.magetab;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
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
import gov.nih.nci.caarray.domain.file.CaArrayFileSet;
import gov.nih.nci.caarray.domain.hybridization.Hybridization;
import gov.nih.nci.caarray.domain.project.Experiment;
import gov.nih.nci.caarray.domain.project.ExperimentContact;
import gov.nih.nci.caarray.domain.sample.LabeledExtract;
import gov.nih.nci.caarray.domain.sample.Sample;
import gov.nih.nci.caarray.domain.vocabulary.Term;
import gov.nih.nci.caarray.magetab.MageTabDocumentSet;
import gov.nih.nci.caarray.magetab.TestMageTabSets;
import gov.nih.nci.caarray.magetab.idf.IdfDocument;
import gov.nih.nci.caarray.magetab.sdrf.AbstractBioMaterial;
import gov.nih.nci.caarray.magetab.sdrf.SdrfDocument;
import gov.nih.nci.caarray.util.j2ee.ServiceLocatorStub;
import gov.nih.nci.caarray.validation.ValidationMessage;
import gov.nih.nci.caarray.validation.ValidationResult;

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
public class MageTabTranslatorTest {

    private MageTabTranslator translator;
    private final LocalDaoFactoryStub daoFactoryStub = new LocalDaoFactoryStub();
    private final VocabularyServiceStub vocabularyServiceStub = new VocabularyServiceStub();

    /**
     * Prepares the translator implementation, stubbing out dependencies.
     */
    @Before
    public void setupTranslator() {
        MageTabTranslatorBean mageTabTranslatorBean = new MageTabTranslatorBean();
        mageTabTranslatorBean.setDaoFactory(this.daoFactoryStub);
        ServiceLocatorStub locatorStub = ServiceLocatorStub.registerEmptyLocator();
        locatorStub.addLookup(VocabularyService.JNDI_NAME, this.vocabularyServiceStub);
        this.translator = mageTabTranslatorBean;
    }

    /**
     * Test method for
     * {@link gov.nih.nci.caarray.application.translation.magetab.MageTabTranslator#translate(gov.nih.nci.caarray.magetab.MageTabDocumentSet)}
     * .
     */
    @Test
    public void testTranslate() {
        testSpecificationDocuments();
        testSpecificationTermCaseSensitivityDocuments();
        testSpecificationDocumentsNoArrayDesignRef();
        testSpecificationDocumentsNoExpDesc();
        testTcgaBroadDocuments();
        testGskTestDocuments();
        testSpecificationDocumentsWithDerivedData();
    }

    private void testSpecificationDocuments() {
        CaArrayFileSet fileSet = TestMageTabSets.getFileSet(TestMageTabSets.MAGE_TAB_SPECIFICATION_SET);
        CaArrayTranslationResult result = this.translator
                .translate(TestMageTabSets.MAGE_TAB_SPECIFICATION_SET, fileSet);
        Experiment experiment = result.getInvestigations().iterator().next();
        assertNotNull(experiment.getDescription());
        assertTrue(experiment.getDescription().startsWith("Gene expression of TK6"));
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
    private void testSpecificationTermCaseSensitivityDocuments() {
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

    private void testSpecificationDocumentsNoExpDesc() {
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

    private void testSpecificationDocumentsNoArrayDesignRef() {
        CaArrayFileSet fileSet = TestMageTabSets.getFileSet(TestMageTabSets.MAGE_TAB_SPECIFICATION_NO_ARRAY_DESIGN_SET);
        CaArrayTranslationResult result = this.translator.translate(
                TestMageTabSets.MAGE_TAB_SPECIFICATION_NO_ARRAY_DESIGN_SET, fileSet);
        Experiment experiment = result.getInvestigations().iterator().next();
        assertNotNull(experiment.getDescription());
        assertTrue(experiment.getDescription().startsWith("Gene expression of TK6"));
        assertEquals(8, experiment.getExperimentContacts().size());
        assertEquals(1, experiment.getExperimentDesignTypes().size());
        assertEquals("genetic_modification_design", experiment.getExperimentDesignTypes().iterator().next().getValue());
        assertEquals(6, experiment.getSources().size());
        assertEquals(6, experiment.getSamples().size());
        assertEquals(6, experiment.getExtracts().size());
        assertEquals(6, experiment.getLabeledExtracts().size());
        assertEquals(6, experiment.getHybridizations().size());
        for (Hybridization hyb : experiment.getHybridizations()) {
            assertNull(hyb.getArray().getDesign());
        }
    }

    private void testSpecificationDocumentsWithDerivedData() {
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

    private void testTcgaBroadDocuments() {
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

    private void testGskTestDocuments() {
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
