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
import gov.nih.nci.caarray.application.fileaccess.FileAccessServiceStub;
import gov.nih.nci.caarray.application.translation.CaArrayTranslationResult;
import gov.nih.nci.caarray.business.vocabulary.VocabularyServiceStub;
import gov.nih.nci.caarray.dao.VocabularyDao;
import gov.nih.nci.caarray.dao.stub.DaoFactoryStub;
import gov.nih.nci.caarray.dao.stub.VocabularyDaoStub;
import gov.nih.nci.caarray.domain.AbstractCaArrayObject;
import gov.nih.nci.caarray.domain.data.RawArrayData;
import gov.nih.nci.caarray.domain.file.CaArrayFile;
import gov.nih.nci.caarray.domain.file.CaArrayFileSet;
import gov.nih.nci.caarray.domain.file.FileType;
import gov.nih.nci.caarray.domain.hybridization.Hybridization;
import gov.nih.nci.caarray.domain.project.Investigation;
import gov.nih.nci.caarray.domain.sample.LabeledExtract;
import gov.nih.nci.caarray.domain.vocabulary.Category;
import gov.nih.nci.caarray.domain.vocabulary.Term;
import gov.nih.nci.caarray.domain.vocabulary.TermSource;
import gov.nih.nci.caarray.magetab.AbstractMageTabDocument;
import gov.nih.nci.caarray.magetab.MageTabDocumentSet;
import gov.nih.nci.caarray.magetab.TestMageTabSets;
import gov.nih.nci.caarray.magetab.adf.AdfDocument;
import gov.nih.nci.caarray.magetab.data.DataMatrix;
import gov.nih.nci.caarray.magetab.idf.IdfDocument;
import gov.nih.nci.caarray.magetab.sdrf.SdrfDocument;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.junit.Before;
import org.junit.Test;

/**
 *
 */
@SuppressWarnings("PMD")
public class MageTabTranslatorTest {

    private MageTabTranslator translator;
    private final LocalDaoFactoryStub daoFactoryStub = new LocalDaoFactoryStub();
    private final LocalVocabularyServiceStub vocabularyServiceStub = new LocalVocabularyServiceStub();
    private final FileAccessServiceStub fileAccessServiceStub = new FileAccessServiceStub();

    /**
     * Prepares the translator implementation, stubbing out dependencies.
     */
    @Before
    public void setupTranslator() {
        MageTabTranslatorBean mageTabTranslatorBean = new MageTabTranslatorBean();
        mageTabTranslatorBean.setDaoFactory(daoFactoryStub);
        mageTabTranslatorBean.setVocabularyService(vocabularyServiceStub);
        mageTabTranslatorBean.setFileAccessService(fileAccessServiceStub);
        translator = mageTabTranslatorBean;
    }

    /**
     * Test method for {@link gov.nih.nci.caarray.application.translation.magetab.MageTabTranslator#translate(gov.nih.nci.caarray.magetab.MageTabDocumentSet)}.
     */
    @Test
    public void testTranslate() {
        testTcgaBroadDocuments();
    }

    private void testTcgaBroadDocuments() {
        CaArrayFileSet fileSet = getFileSet(TestMageTabSets.TCGA_BROAD_SET);
        CaArrayTranslationResult result = translator.translate(TestMageTabSets.TCGA_BROAD_SET, fileSet);
        assertEquals(10, result.getTerms().size());
        assertEquals(1, result.getInvestigations().size());
        Investigation investigation = result.getInvestigations().iterator().next();
        checkTcgaBroadInvestigation(investigation);
    }

    private CaArrayFileSet getFileSet(MageTabDocumentSet documentSet) {
        CaArrayFileSet fileSet = new CaArrayFileSet();
        addFiles(fileSet, documentSet.getIdfDocuments());
        addFiles(fileSet, documentSet.getSdrfDocuments());
        addFiles(fileSet, documentSet.getAdfDocuments());
        addFiles(fileSet, documentSet.getDataMatrixes());
        addFiles(fileSet, documentSet.getNativeDataFiles());
        return fileSet;
    }

    private void addFiles(CaArrayFileSet fileSet, Collection<? extends AbstractMageTabDocument> mageTabDocuments) {
        for (AbstractMageTabDocument mageTabDocument : mageTabDocuments) {
            addFile(fileSet, mageTabDocument);
        }
    }

    private void addFile(CaArrayFileSet fileSet, AbstractMageTabDocument mageTabDocument) {
        CaArrayFile caArrayFile = fileAccessServiceStub.add(mageTabDocument.getFile());
        if (mageTabDocument instanceof IdfDocument) {
            caArrayFile.setType(FileType.MAGE_TAB_IDF);
        } else if (mageTabDocument instanceof SdrfDocument) {
            caArrayFile.setType(FileType.MAGE_TAB_SDRF);
        } else if (mageTabDocument instanceof AdfDocument) {
            caArrayFile.setType(FileType.MAGE_TAB_ADF);
        } else if (mageTabDocument instanceof DataMatrix) {
            caArrayFile.setType(FileType.MAGE_TAB_DATA_MATRIX);
        } else if (mageTabDocument.getFile().getName().toLowerCase().endsWith(".cel")) {
            caArrayFile.setType(FileType.AFFYMETRIX_CEL);
        } else {
            throw new IllegalArgumentException("Unrecognized document file " + mageTabDocument.getFile());
        }
        fileSet.add(caArrayFile);
    }

    private void checkTcgaBroadInvestigation(Investigation investigation) {
        IdfDocument idf = TestMageTabSets.TCGA_BROAD_SET.getIdfDocuments().iterator().next();
        assertEquals(idf.getInvestigation().getTitle(), investigation.getTitle());
        checkTcgaBroadBioMaterials(investigation);
        checkTcgaBroadHybridizations(investigation);
    }

    private void checkTcgaBroadHybridizations(Investigation investigation) {
        for (LabeledExtract labeledExtract : investigation.getLabeledExtracts()) {
            Hybridization hybridization = labeledExtract.getHybridization();
            assertNotNull(hybridization);
            RawArrayData celData = hybridization.getArrayData();
            assertNotNull(celData);
            // TODO Eric -- fix this issue
            // assertNotNull(celData.getDataFile());
        }
    }

    private void checkTcgaBroadBioMaterials(Investigation investigation) {
        assertEquals(0, investigation.getSources().size());
        assertEquals(0, investigation.getSamples().size());
        assertEquals(26, investigation.getExtracts().size());
        assertEquals(26, investigation.getLabeledExtracts().size());
    }

    private static class LocalDaoFactoryStub extends DaoFactoryStub {

        @Override
        public VocabularyDao getVocabularyDao() {
            return new LocalVocabularyDaoStub();
        }

    }

    private static class LocalVocabularyDaoStub extends VocabularyDaoStub {

        @Override
        public List<AbstractCaArrayObject> queryEntityByExample(AbstractCaArrayObject entityToMatch) {
            return new ArrayList<AbstractCaArrayObject>();
        }

    }

    private static class LocalVocabularyServiceStub extends VocabularyServiceStub {

        @Override
        public TermSource getSource(String name) {
            assertFalse(StringUtils.isEmpty(name));
            if ("caarray".equals(name)) {
                return null;
            } else {
                return super.getSource(name);
            }
        }

        @Override
        public Category getCategory(TermSource source, String categoryName) {
            assertFalse(StringUtils.isEmpty(source.getName()));
            assertFalse(StringUtils.isEmpty(categoryName));
            if ("MO".equals(source.getName())) {
                return super.getCategory(source, categoryName);
            } else {
                return null;
            }
        }

        @Override
        public Term getTerm(TermSource source, Category category, String value) {
            assertFalse(StringUtils.isEmpty(source.getName()));
            assertFalse(StringUtils.isEmpty(category.getName()));
            assertFalse(StringUtils.isEmpty(value));
            if ("MO".equals(source.getName())) {
                return super.getTerm(source, category, value);
            } else {
                return null;
            }
        }

        @Override
        public TermSource createSource(String name) {
            assertFalse("MO".equals(name));
            return super.createSource(name);
        }

        @Override
        public Category createCategory(TermSource source, String categoryName) {
            assertFalse("MO".equals(source.getName()));
            return super.createCategory(source, categoryName);
        }

        @Override
        public Term createTerm(TermSource source, Category category, String value) {
            assertFalse("MO".equals(source.getName()));
            return super.createTerm(source, category, value);
        }

    }

}
