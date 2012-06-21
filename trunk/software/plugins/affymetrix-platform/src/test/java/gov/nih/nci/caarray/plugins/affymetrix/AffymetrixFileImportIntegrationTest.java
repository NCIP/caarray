/**
 * The software subject to this notice and license includes both human readable
 * source code form and machine readable, binary, object code form. The caarray-ejb-jar
 * Software was developed in conjunction with the National Cancer Institute
 * (NCI) by NCI employees and 5AM Solutions, Inc. (5AM). To the extent
 * government employees are authors, any rights in such works shall be subject
 * to Title 17 of the United States Code, section 105.
 *
 * This caarray-ejb-jar Software License (the License) is between NCI and You. You (or
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
 * its rights in the caarray-ejb-jar Software to (i) use, install, access, operate,
 * execute, copy, modify, translate, market, publicly display, publicly perform,
 * and prepare derivative works of the caarray-ejb-jar Software; (ii) distribute and
 * have distributed to and by third parties the caarray-ejb-jar Software and any
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
package gov.nih.nci.caarray.plugins.affymetrix;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import gov.nih.nci.caarray.application.arraydata.DataImportOptions;
import gov.nih.nci.caarray.application.file.AbstractFileManagementServiceIntegrationTest;
import gov.nih.nci.caarray.domain.array.ArrayDesign;
import gov.nih.nci.caarray.domain.data.DerivedArrayData;
import gov.nih.nci.caarray.domain.data.RawArrayData;
import gov.nih.nci.caarray.domain.file.CaArrayFile;
import gov.nih.nci.caarray.domain.file.CaArrayFileSet;
import gov.nih.nci.caarray.domain.file.FileStatus;
import gov.nih.nci.caarray.domain.hybridization.Hybridization;
import gov.nih.nci.caarray.domain.project.Project;
import gov.nih.nci.caarray.domain.sample.Extract;
import gov.nih.nci.caarray.domain.sample.Source;
import gov.nih.nci.caarray.domain.vocabulary.TermSource;
import gov.nih.nci.caarray.injection.InjectorFactory;
import gov.nih.nci.caarray.magetab.MageTabDocumentSet;
import gov.nih.nci.caarray.magetab.MageTabFileSet;
import gov.nih.nci.caarray.magetab.MageTabParser;
import gov.nih.nci.caarray.magetab.TestMageTabSets;
import gov.nih.nci.caarray.magetab.io.FileRef;
import gov.nih.nci.caarray.platforms.unparsed.UnparsedDataHandler;
import gov.nih.nci.caarray.test.data.arraydesign.AffymetrixArrayDesignFiles;

import java.util.Arrays;
import java.util.Set;

import org.hibernate.Query;
import org.hibernate.Transaction;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Integration test for the FileManagementService.
 * 
 * @author Steve Lustbader
 */
@SuppressWarnings("PMD")
public class AffymetrixFileImportIntegrationTest extends AbstractFileManagementServiceIntegrationTest {
    @BeforeClass
    public static void configurePlatforms() {
        InjectorFactory.addPlatform(new AffymetrixModule());
    }

    @Test
    public void testImportMageTabSpecificationAndUpdateCharacteristics() throws Exception {
        final ArrayDesign design = importArrayDesign(AffymetrixArrayDesignFiles.TEST3_CDF, CdfHandler.CDF_FILE_TYPE);
        addDesignToExperiment(design);

        final ArrayDesign design2 =
                importArrayDesign(AffymetrixArrayDesignFiles.HG_FOCUS_CDF, CdfHandler.CDF_FILE_TYPE);
        addDesignToExperiment(design2);

        importFiles(TestMageTabSets.MAGE_TAB_SPECIFICATION_INPUT_SET);

        Transaction tx = this.hibernateHelper.beginTransaction();
        Project project = getTestProject();
        assertEquals(FileStatus.IMPORTED, project.getFileSet().getStatus());
        assertTrue(project.getExperiment().getDescription().endsWith("MDR1 overexpression."));
        assertEquals(1, project.getExperiment().getFactors().size());
        assertEquals(8, project.getExperiment().getExperimentContacts().size());
        assertEquals(6, project.getExperiment().getSources().size());
        assertEquals(6, project.getExperiment().getSamples().size());
        assertEquals(6, project.getExperiment().getExtracts().size());
        assertEquals(6, project.getExperiment().getLabeledExtracts().size());
        assertEquals(6, project.getExperiment().getHybridizations().size());
        Source testSource = null;
        testSource = findSource(project, "TK6 replicate 2");
        assertEquals("cell", testSource.getMaterialType().getValue());
        assertEquals("B_lymphoblast", testSource.getCellType().getValue());
        assertNull(testSource.getTissueSite());
        assertEquals("Test3", project.getExperiment().getHybridizationByName("H_TK6 replicate 1").getArray()
                .getDesign().getName());
        tx.commit();

        // now try to update annotations of existing biomaterials
        importFiles(TestMageTabSets.MAGE_TAB_SPECIFICATION_UPDATE_ANNOTATIONS_INPUT_SET);

        tx = this.hibernateHelper.beginTransaction();
        project = getTestProject();
        assertEquals(FileStatus.IMPORTED, project.getFileSet().getStatus());
        assertTrue(project.getExperiment().getDescription().endsWith("This sentence is added to the description."));
        assertEquals(2, project.getExperiment().getFactors().size());
        assertEquals(10, project.getExperiment().getExperimentContacts().size());
        assertEquals(6, project.getExperiment().getSources().size());
        assertEquals(6, project.getExperiment().getSamples().size());
        assertEquals(6, project.getExperiment().getExtracts().size());
        assertEquals(6, project.getExperiment().getLabeledExtracts().size());
        assertEquals(6, project.getExperiment().getHybridizations().size());
        testSource = findSource(project, "TK6 replicate 2");
        assertEquals("cell2", testSource.getMaterialType().getValue());
        assertEquals("B_lymphoblast2", testSource.getCellType().getValue());
        assertEquals("Pancreas", testSource.getTissueSite().getValue());
        testSource = findSource(project, "TK6neo replicate 2");
        assertEquals("B_lymphoblast", testSource.getCellType().getValue());
        assertNull(findSource(project, "TK6neo replicate 3"));
        assertEquals("Test3", project.getExperiment().getHybridizationByName("H_TK6 replicate 1").getArray()
                .getDesign().getName());
        tx.commit();

        // now try to add a new biomaterial while update existing biomaterials
        importFiles(TestMageTabSets.MAGE_TAB_SPECIFICATION_UPDATE_ANNOTATIONS_ADD_BM_INPUT_SET);

        tx = this.hibernateHelper.beginTransaction();
        project = getTestProject();
        assertEquals(FileStatus.IMPORTED, project.getFileSet().getStatus());
        assertTrue(project.getExperiment().getDescription().endsWith("This sentence is added to the description."));
        assertEquals(2, project.getExperiment().getFactors().size());
        assertEquals(10, project.getExperiment().getExperimentContacts().size());
        assertEquals(7, project.getExperiment().getSources().size());
        assertEquals(7, project.getExperiment().getSamples().size());
        assertEquals(7, project.getExperiment().getExtracts().size());
        assertEquals(7, project.getExperiment().getLabeledExtracts().size());
        assertEquals(7, project.getExperiment().getHybridizations().size());
        testSource = findSource(project, "TK6 replicate 2");
        assertEquals("cell2", testSource.getMaterialType().getValue());
        assertEquals("B_lymphoblast2", testSource.getCellType().getValue());
        assertEquals("Pancreas", testSource.getTissueSite().getValue());
        testSource = findSource(project, "TK6neo replicate 2");
        assertEquals("B_lymphoblast3", testSource.getCellType().getValue());
        testSource = findSource(project, "TK6neo replicate 3");
        assertEquals("cell", testSource.getMaterialType().getValue());
        assertEquals("HG-Focus", project.getExperiment().getHybridizationByName("H_TK6 replicate 1").getArray()
                .getDesign().getName());
        tx.commit();
    }

    @Test
    public void testImportMageTabWithoutArrayDesignRef() throws Exception {
        final ArrayDesign design = importArrayDesign(AffymetrixArrayDesignFiles.TEST3_CDF, CdfHandler.CDF_FILE_TYPE);
        addDesignToExperiment(design);

        importFiles(TestMageTabSets.EXTENDED_FACTOR_VALUES_INPUT_SET);

        final Transaction tx = this.hibernateHelper.beginTransaction();
        final Project project = getTestProject();
        assertEquals(5, project.getImportedFiles().size());
        assertEquals(3, project.getExperiment().getHybridizations().size());
        for (final Hybridization h : project.getExperiment().getHybridizations()) {
            assertEquals("Test3", h.getArray().getDesign().getName());
        }
        tx.commit();
    }

    @Test
    public void testImportMageTabWithoutArrayDesignRef2() throws Exception {
        final ArrayDesign design = importArrayDesign(AffymetrixArrayDesignFiles.TEST3_CDF, CdfHandler.CDF_FILE_TYPE);
        addDesignToExperiment(design);

        final CaArrayFileSet fileSet = uploadFiles(TestMageTabSets.EXTENDED_FACTOR_VALUES_INPUT_SET);

        Transaction tx = this.hibernateHelper.beginTransaction();
        for (final CaArrayFile file : fileSet.getFilesByType(CelHandler.CEL_FILE_TYPE)) {
            file.setFileType(UnparsedDataHandler.FILE_TYPE_AFFYMETRIX_DAT);
        }
        tx.commit();

        importFiles(fileSet, null);

        tx = this.hibernateHelper.beginTransaction();
        final Project project = getTestProject();
        assertEquals(5, project.getImportedFiles().size());
        assertEquals(3, project.getExperiment().getHybridizations().size());
        for (final Hybridization h : project.getExperiment().getHybridizations()) {
            assertEquals("Test3", h.getArray().getDesign().getName());
        }
        tx.commit();
    }

    @Test
    public void testImportNonMageTabWithoutArrayDesign() throws Exception {
        final ArrayDesign design = importArrayDesign(AffymetrixArrayDesignFiles.TEST3_CDF, CdfHandler.CDF_FILE_TYPE);
        addDesignToExperiment(design);

        final MageTabFileSet inputFiles = new MageTabFileSet();
        for (final FileRef f : TestMageTabSets.EXTENDED_FACTOR_VALUES_INPUT_SET.getAllFiles()) {
            if (f.getName().endsWith("CEL")) {
                inputFiles.addNativeData(f);
            }
        }
        final MageTabDocumentSet docSet =
                MageTabParser.INSTANCE.parse(TestMageTabSets.EXTENDED_FACTOR_VALUES_INPUT_SET);
        docSet.getIdfDocuments().clear();
        docSet.getSdrfDocuments().clear();

        final CaArrayFileSet fileSet = uploadFiles(inputFiles);

        Transaction tx = this.hibernateHelper.beginTransaction();
        for (final CaArrayFile file : fileSet.getFilesByType(CelHandler.CEL_FILE_TYPE)) {
            file.setFileType(UnparsedDataHandler.FILE_TYPE_AFFYMETRIX_DAT);
        }
        tx.commit();

        importFiles(fileSet, DataImportOptions.getAutoCreatePerFileOptions());

        tx = this.hibernateHelper.beginTransaction();
        final Project project = getTestProject();
        assertEquals(3, project.getImportedFiles().size());
        assertEquals(3, project.getExperiment().getHybridizations().size());
        for (final Hybridization h : project.getExperiment().getHybridizations()) {
            assertEquals("Test3", h.getArray().getDesign().getName());
        }
        tx.commit();
    }

    @Test
    public void testUpdateBioMaterialChain() throws Exception {
        final ArrayDesign design = importArrayDesign(AffymetrixArrayDesignFiles.TEST3_CDF, CdfHandler.CDF_FILE_TYPE);
        addDesignToExperiment(design);

        importFiles(TestMageTabSets.UPDATE_BIO_MATERIAL_CHAIN_BASELINE_INPUT_SET);

        Transaction tx = this.hibernateHelper.beginTransaction();
        Project project = getTestProject();
        assertEquals(FileStatus.IMPORTED, project.getFileSet().getStatus());
        assertEquals(1, project.getExperiment().getSources().size());
        assertEquals(1, project.getExperiment().getSamples().size());
        assertEquals(2, project.getExperiment().getExtracts().size());
        assertEquals(2, project.getExperiment().getLabeledExtracts().size());
        assertEquals(2, project.getExperiment().getHybridizations().size());
        assertNotNull(findSource(project, "Source A"));
        assertNull(findSource(project, "Source B"));
        assertEquals(1, project.getExperiment().getHybridizationByName("Hyb 1").getAllDataFiles().size());
        tx.commit();

        // now try to add new biomaterials in the middle of the existing chains
        importFiles(TestMageTabSets.UPDATE_BIO_MATERIAL_CHAIN_NEW_BIO_MATERIALS_INPUT_SET);

        tx = this.hibernateHelper.beginTransaction();
        project = getTestProject();
        assertEquals(FileStatus.IMPORTED, project.getFileSet().getStatus());
        assertEquals(2, project.getExperiment().getSources().size());
        assertEquals(2, project.getExperiment().getSamples().size());
        Set<Extract> extracts = project.getExperiment().getExtracts();
        System.out.println("Updated extracts: " + extracts);
        assertEquals(4, extracts.size());
        assertEquals(4, project.getExperiment().getLabeledExtracts().size());
        assertEquals(4, project.getExperiment().getHybridizations().size());
        assertNotNull(findSource(project, "Source A"));
        assertNotNull(findSource(project, "Source B"));
        assertEquals(1, project.getExperiment().getHybridizationByName("Hyb 1").getAllDataFiles().size());
        tx.commit();

        // now try to add a data files to existing hybs
        importFiles(TestMageTabSets.UPDATE_BIO_MATERIAL_CHAIN_NEW_DATA_FILES_INPUT_SET);

        tx = this.hibernateHelper.beginTransaction();
        project = getTestProject();
        assertEquals(FileStatus.IMPORTED, project.getFileSet().getStatus());
        assertEquals(2, project.getExperiment().getSources().size());
        assertEquals(2, project.getExperiment().getSamples().size());
        extracts = project.getExperiment().getExtracts();
        System.out.println("Updated extracts: " + extracts);
        assertEquals(4, extracts.size());
        assertEquals(4, project.getExperiment().getLabeledExtracts().size());
        assertEquals(4, project.getExperiment().getHybridizations().size());
        assertNotNull(findSource(project, "Source A"));
        assertNotNull(findSource(project, "Source B"));
        assertEquals(2, project.getExperiment().getHybridizationByName("Hyb 1").getAllDataFiles().size());
        tx.commit();
    }

    @Test
    public void testUpdateFiles() throws Exception {
        final ArrayDesign design = importArrayDesign(AffymetrixArrayDesignFiles.TEST3_CDF, CdfHandler.CDF_FILE_TYPE);
        addDesignToExperiment(design);

        importFiles(TestMageTabSets.UPDATE_FILES_BASELINE_INPUT_SET);

        Transaction tx = this.hibernateHelper.beginTransaction();
        Project project = getTestProject();
        assertEquals(FileStatus.IMPORTED, project.getFileSet().getStatus());
        assertEquals(1, project.getExperiment().getSources().size());
        assertEquals(1, project.getExperiment().getSamples().size());
        assertEquals(1, project.getExperiment().getExtracts().size());
        assertEquals(1, project.getExperiment().getLabeledExtracts().size());
        assertEquals(2, project.getExperiment().getHybridizations().size());
        assertNotNull(findSource(project, "Source A"));
        assertNull(findSource(project, "Source B"));
        assertEquals(2, project.getExperiment().getHybridizationByName("Hyb 1").getAllDataFiles().size());
        assertEquals(1, project.getExperiment().getHybridizationByName("Hyb 1").getRawDataCollection().size());
        assertEquals(1, project.getExperiment().getHybridizationByName("Hyb 1").getDerivedDataCollection().size());
        RawArrayData raw =
                project.getExperiment().getHybridizationByName("Hyb 1").getRawDataCollection().iterator().next();
        DerivedArrayData derived =
                project.getExperiment().getHybridizationByName("Hyb 1").getDerivedDataCollection().iterator().next();
        assertEquals(1, derived.getDerivedFromArrayDataCollection().size());
        assertEquals(raw, derived.getDerivedFromArrayDataCollection().iterator().next());
        tx.commit();

        // now try to add data files to existing hybs, which also reference existing data files
        importFiles(TestMageTabSets.UPDATE_FILES_NEW_INPUT_SET);

        tx = this.hibernateHelper.beginTransaction();
        project = getTestProject();
        assertEquals(FileStatus.IMPORTED, project.getFileSet().getStatus());
        assertEquals(1, project.getExperiment().getSources().size());
        assertEquals(1, project.getExperiment().getSamples().size());
        assertEquals(2, project.getExperiment().getHybridizations().size());

        assertEquals(3, project.getExperiment().getHybridizationByName("Hyb 1").getAllDataFiles().size());
        assertEquals(2, project.getExperiment().getHybridizationByName("Hyb 1").getRawDataCollection().size());
        assertEquals(1, project.getExperiment().getHybridizationByName("Hyb 1").getDerivedDataCollection().size());
        derived = project.getExperiment().getHybridizationByName("Hyb 1").getDerivedDataCollection().iterator().next();
        assertEquals("BM1.EXP", derived.getDataFile().getName());
        assertEquals(2, derived.getDerivedFromArrayDataCollection().size());
        for (final RawArrayData oneRaw : project.getExperiment().getHybridizationByName("Hyb 1").getRawDataCollection()) {
            assertTrue(Arrays.asList("BM1.CEL", "BM1a.CEL").contains(oneRaw.getDataFile().getName()));
            assertTrue("BM1.EXP is not derived from " + oneRaw.getName(), derived.getDerivedFromArrayDataCollection()
                    .contains(oneRaw));
        }

        assertEquals(3, project.getExperiment().getHybridizationByName("Hyb 2").getAllDataFiles().size());
        assertEquals(1, project.getExperiment().getHybridizationByName("Hyb 2").getRawDataCollection().size());
        assertEquals(2, project.getExperiment().getHybridizationByName("Hyb 2").getDerivedDataCollection().size());
        raw = project.getExperiment().getHybridizationByName("Hyb 2").getRawDataCollection().iterator().next();
        assertEquals("BM2.CEL", raw.getDataFile().getName());
        for (final DerivedArrayData oneDerived : project.getExperiment().getHybridizationByName("Hyb 2")
                .getDerivedDataCollection()) {
            assertTrue(Arrays.asList("BM2.EXP", "BM2a.EXP").contains(oneDerived.getDataFile().getName()));
            assertEquals(1, oneDerived.getDerivedFromArrayDataCollection().size());
            assertEquals(raw, oneDerived.getDerivedFromArrayDataCollection().iterator().next());
        }

        tx.commit();
    }

    @Test
    public void testImportMageTabCausesNewTermSourceCreationForSameNameDifferentUrl() throws Exception {
        final ArrayDesign design = importArrayDesign(AffymetrixArrayDesignFiles.TEST3_CDF);
        addDesignToExperiment(design);

        importFiles(TestMageTabSets.GETS_NEW_TERM_SOURCE_INPUT_SET);

        final Transaction tx = this.hibernateHelper.beginTransaction();
        final Project project = getTestProject();
        assertEquals(FileStatus.IMPORTED, project.getFileSet().getStatus());
        final Set<Extract> extracts = project.getExperiment().getExtracts();
        assertEquals("this number of extracts is incorrect.", 1, extracts.size());
        final Extract extract = extracts.iterator().next();
        assertTrue("The term source URL is incorrect.", extract.getMaterialType().getSource().getName().equals("MO"));
        assertTrue("The term source URL is incorrect.",
                extract.getMaterialType().getSource().getUrl().equals("http://foobar.com"));
        assertTrue("The term source URL is incorrect.",
                extract.getMaterialType().getSource().getVersion().equals("9001"));

        // verify both MO term sources are now present
        final Query query =
                this.hibernateHelper.getCurrentSession().createQuery(
                        "from " + TermSource.class.getName() + " where name = 'MO'");
        assertEquals("The number of term sources is incorrect.", 2, query.list().size());
        tx.commit();
    }

}
