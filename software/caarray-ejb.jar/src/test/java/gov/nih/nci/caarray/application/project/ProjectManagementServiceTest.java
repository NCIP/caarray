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
package gov.nih.nci.caarray.application.project;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import gov.nih.nci.caarray.application.GenericDataService;
import gov.nih.nci.caarray.application.GenericDataServiceStub;
import gov.nih.nci.caarray.application.SessionContextStub;
import gov.nih.nci.caarray.application.fileaccess.FileAccessService;
import gov.nih.nci.caarray.application.fileaccess.FileAccessServiceStub;
import gov.nih.nci.caarray.application.fileaccess.TemporaryFileCacheLocator;
import gov.nih.nci.caarray.application.fileaccess.TemporaryFileCacheStubFactory;
import gov.nih.nci.caarray.application.project.InconsistentProjectStateException.Reason;
import gov.nih.nci.caarray.dao.ProjectDao;
import gov.nih.nci.caarray.dao.SearchDao;
import gov.nih.nci.caarray.dao.stub.DaoFactoryStub;
import gov.nih.nci.caarray.dao.stub.ProjectDaoStub;
import gov.nih.nci.caarray.dao.stub.SearchDaoStub;
import gov.nih.nci.caarray.domain.AbstractCaArrayEntity;
import gov.nih.nci.caarray.domain.PersistentObject;
import gov.nih.nci.caarray.domain.array.Array;
import gov.nih.nci.caarray.domain.array.ArrayDesign;
import gov.nih.nci.caarray.domain.data.RawArrayData;
import gov.nih.nci.caarray.domain.file.CaArrayFile;
import gov.nih.nci.caarray.domain.file.FileStatus;
import gov.nih.nci.caarray.domain.hybridization.Hybridization;
import gov.nih.nci.caarray.domain.project.Factor;
import gov.nih.nci.caarray.domain.project.Project;
import gov.nih.nci.caarray.domain.sample.AbstractBioMaterial;
import gov.nih.nci.caarray.domain.sample.Extract;
import gov.nih.nci.caarray.domain.sample.Sample;
import gov.nih.nci.caarray.domain.sample.Source;
import gov.nih.nci.caarray.domain.search.PageSortParams;
import gov.nih.nci.caarray.domain.search.ProjectSortCriterion;
import gov.nih.nci.caarray.security.PermissionDeniedException;
import gov.nih.nci.caarray.security.Protectable;
import gov.nih.nci.caarray.security.SecurityUtils;
import gov.nih.nci.caarray.test.data.magetab.MageTabDataFiles;
import gov.nih.nci.caarray.util.HibernateUtil;
import gov.nih.nci.caarray.util.UsernameHolder;
import gov.nih.nci.caarray.util.j2ee.ServiceLocatorStub;
import gov.nih.nci.security.authorization.domainobjects.User;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.hibernate.Transaction;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

@SuppressWarnings("PMD")
public class ProjectManagementServiceTest {

    private ProjectManagementService projectManagementService;
    private final LocalDaoFactoryStub daoFactoryStub = new LocalDaoFactoryStub();
    private final FileAccessServiceStub fileAccessService = new FileAccessServiceStub();
    private final GenericDataService genericDataService = new GenericDataServiceStub();
    private final LocalSessionContextStub sessionContextStub = new LocalSessionContextStub();
    private Transaction transaction;

    @Before
    public void setUpService() {
        UsernameHolder.setUser("caarrayadmin");
        ProjectManagementServiceBean projectManagementServiceBean = new ProjectManagementServiceBean();
        projectManagementServiceBean.setDaoFactory(this.daoFactoryStub);
        ServiceLocatorStub locatorStub = ServiceLocatorStub.registerEmptyLocator();
        locatorStub.addLookup(FileAccessService.JNDI_NAME, this.fileAccessService);
        locatorStub.addLookup(GenericDataService.JNDI_NAME, this.genericDataService);
        projectManagementServiceBean.setSessionContext(this.sessionContextStub);
        this.projectManagementService = projectManagementServiceBean;
        HibernateUtil.enableFilters(false);
        transaction = HibernateUtil.beginTransaction();
        TemporaryFileCacheLocator.setTemporaryFileCacheFactory(new TemporaryFileCacheStubFactory(this.fileAccessService));
        TemporaryFileCacheLocator.resetTemporaryFileCache();
    }

    @After
    public void tearDown() {
        transaction.rollback();
    }

    @Test
    public void testGetWorkspaceProjects() {
        List<Project> projects =
                this.projectManagementService.getMyProjects(false, new PageSortParams<Project>(10000, 1,
                        ProjectSortCriterion.PUBLIC_ID, false));
        assertNotNull(projects);
    }

    @Test
    public void testGetProject() {
        Project project = this.projectManagementService.getProject(123L);
        assertNotNull(project);
        assertEquals(123L, project.getId());
    }

    @Test
    public void testAddFile() {
        Project project = this.projectManagementService.getProject(123L);
        try {
            CaArrayFile file = this.projectManagementService.addFile(project, MageTabDataFiles.SPECIFICATION_EXAMPLE_IDF);
            assertEquals(MageTabDataFiles.SPECIFICATION_EXAMPLE_IDF.getName(), file.getName());
            assertEquals(1, project.getFiles().size());
            assertNotNull(project.getFiles().iterator().next().getProject());
            assertContains(project.getFiles(), MageTabDataFiles.SPECIFICATION_EXAMPLE_IDF.getName());
        } catch (ProposalWorkflowException e) {
            fail("Should not have gotten a workflow exception adding files");
        } catch (InconsistentProjectStateException e) {
            fail("Unexpected inconstient state exception: " + e);
        }
    }

    @Test
    public void testUploadFiles() throws Exception {
        Project project = this.projectManagementService.getProject(123L);
        try {
            List<String> conflicts = new ArrayList<String>();
            List<String> fileNames = new ArrayList<String>();
            List<File> files = new ArrayList<File>();
            files.add(MageTabDataFiles.SPECIFICATION_ZIP_WITH_NEXTED_ZIP);
            fileNames.add("specification.zip");

            files.add(MageTabDataFiles.SPECIFICATION_EXAMPLE_IDF);
            fileNames.add("  ");

            files.add(MageTabDataFiles.SPECIFICATION_ZIP_WITH_NEXTED_ZIP_TXT_FILE);
            fileNames.add(MageTabDataFiles.SPECIFICATION_ZIP_WITH_NEXTED_ZIP_TXT_FILE.getName());

            files.add(MageTabDataFiles.SPECIFICATION_EXAMPLE_IDF);
            fileNames.add(MageTabDataFiles.SPECIFICATION_EXAMPLE_IDF.getName());
            int count = this.projectManagementService.uploadFiles(project, files, fileNames, conflicts);
            assertEquals(13, count);
            assertEquals(13, project.getFiles().size());
            assertNotNull(project.getFiles().iterator().next().getProject());
            assertContains(project.getFiles(), "Test1.zip");
            assertContains(project.getFiles(), MageTabDataFiles.SPECIFICATION_ZIP_WITH_NEXTED_ZIP_TXT_FILE.getName());
            assertContains(project.getFiles(), "folder1/Test1.txt");
            assertEquals(1, conflicts.size());
            assertTrue(conflicts.contains(MageTabDataFiles.SPECIFICATION_EXAMPLE_IDF.getName()));

            conflicts = new ArrayList<String>();
            count = this.projectManagementService.uploadFiles(project, files, fileNames, conflicts);
            assertEquals(0, count);
            assertEquals(13, project.getFiles().size());
            assertNotNull(project.getFiles().iterator().next().getProject());
            assertContains(project.getFiles(), "Test1.zip");
            assertContains(project.getFiles(), "folder1/Test1.txt");
            assertContains(project.getFiles(), MageTabDataFiles.SPECIFICATION_ZIP_WITH_NEXTED_ZIP_TXT_FILE.getName());
            assertEquals(14, conflicts.size());
            assertTrue(conflicts.contains(MageTabDataFiles.SPECIFICATION_EXAMPLE_IDF.getName()));
        } catch (ProposalWorkflowException e) {
            fail("Should not have gotten a workflow exception adding files");
        }
    }

    private void assertContains(Set<CaArrayFile> caArrayFiles, String file) {
        for (CaArrayFile caArrayFile : caArrayFiles) {
            if (file.equals(caArrayFile.getName())) {
                return;
            }
        }
        fail("CaArrayFileSet did not contain " + file);
    }

    /**
     * Test method for {@link gov.nih.nci.caarray.application.project.ProjectManagementService#submitProject(Project)}.
     */
    @Test
    public void testSaveProject() {
        Project project = new Project();
        try {
            AbstractCaArrayEntity e = new Sample();
            this.projectManagementService.saveProject(project, e);
            assertEquals(project, this.daoFactoryStub.projectDao.lastSaved);
            assertEquals(e, this.daoFactoryStub.projectDao.lastDeleted);
        } catch (ProposalWorkflowException e) {
            fail("Unexpected exception: " + e);
        } catch (InconsistentProjectStateException e) {
            fail("Unexpected exception: " + e);
        }

        project = new Project();
        UsernameHolder.setUser(SecurityUtils.ANONYMOUS_USERNAME);
        try {
            this.projectManagementService.saveProject(project);
            fail("anonymous user should not have been allowed to save a project");
        } catch (ProposalWorkflowException e) {
            fail("Unexpected exception: " + e);
        } catch (InconsistentProjectStateException e) {
            fail("Unexpected exception: " + e);
        } catch (PermissionDeniedException e) {
            // expected exception
        }
    }
    
    /**
     * Test method for {@link gov.nih.nci.caarray.application.project.ProjectManagementService#submitProject(Project)}.
     */
    @Test
    public void testSaveProjectWithInconsistentArrayDesigns() {
        Project project = new Project();
        ArrayDesign ad1 = new ArrayDesign();
        ad1.setName("Test1");
        ArrayDesign ad2 = new ArrayDesign();
        ad2.setName("Test2");
        project.getExperiment().getArrayDesigns().add(ad1);
        project.getExperiment().getArrayDesigns().add(ad2);
        
        Hybridization h1 = new Hybridization();
        Array a1 = new Array();
        a1.setDesign(ad2);
        h1.setArray(a1);
        project.getExperiment().getHybridizations().add(h1);

        try {
            this.projectManagementService.saveProject(project);
            // should be ok
        } catch (InconsistentProjectStateException e) {
            fail("Unexpected inconstient state exception: " + e);
        } catch (ProposalWorkflowException e) {
            fail("Unexpected workflow exception: " + e);
        }

        project.getExperiment().getArrayDesigns().remove(ad2);
        try {
            this.projectManagementService.saveProject(project);
            fail("Expected to throw inconsistent state exception");
        } catch (InconsistentProjectStateException e) {
            assertEquals(Reason.INCONSISTENT_ARRAY_DESIGNS, e.getReason());
            assertEquals(1, e.getArguments().length);
            assertEquals("Test2", e.getArguments()[0]);
        } catch (ProposalWorkflowException e) {
            fail("Unexpected workflow exception: " + e);
        }

    }    

    /**
     * Test method for {@link gov.nih.nci.caarray.application.project.ProjectManagementService#submitProject(Project)}.
     */
    @Test
    public void testSaveProjectWithImportingFiles() {
        Project project = new Project();
        CaArrayFile file1 = new CaArrayFile();
        file1.setProject(project);
        project.getFiles().add(file1);
        CaArrayFile file2 = new CaArrayFile();
        file2.setProject(project);
        project.getFiles().add(file2);
        Source source = new Source();
        source.setId(1L);
        source.setExperiment(project.getExperiment());
        project.getExperiment().getSources().add(source);

        try {
            this.projectManagementService.saveProject(project);
            // should be ok
        } catch (InconsistentProjectStateException e) {
            fail("Unexpected inconstient state exception: " + e);
        } catch (ProposalWorkflowException e) {
            fail("Unexpected workflow exception: " + e);
        }

        file1.setFileStatus(FileStatus.IMPORTING);
        try {
            this.projectManagementService.saveProject(project);
            fail("Expected to throw inconsistent state exception");
        } catch (InconsistentProjectStateException e) {
            assertEquals(Reason.IMPORTING_FILES, e.getReason());
        } catch (ProposalWorkflowException e) {
            fail("Unexpected workflow exception: " + e);
        }
        try {
            this.projectManagementService.copySource(project, 1L);
            fail("Expected to throw inconsistent state exception");
        } catch (InconsistentProjectStateException e) {
            assertEquals(Reason.IMPORTING_FILES, e.getReason());
        } catch (ProposalWorkflowException e) {
            fail("Unexpected workflow exception: " + e);
        }
        
        file1.setFileStatus(FileStatus.IMPORTED);
        try {
            this.projectManagementService.saveProject(project);
            // should be ok
        } catch (InconsistentProjectStateException e) {
            fail("Unexpected inconstient state exception: " + e);
        } catch (ProposalWorkflowException e) {
            fail("Unexpected workflow exception: " + e);
        }
        try {
            this.projectManagementService.copySource(project, 1L);
            // should be ok
        } catch (InconsistentProjectStateException e) {
            fail("Unexpected inconstient state exception: " + e);
        } catch (ProposalWorkflowException e) {
            fail("Unexpected workflow exception: " + e);
        }

    }    

    /**
     * Test method for {@link gov.nih.nci.caarray.application.project.ProjectManagementService#submitProject(Project)}.
     */
    @Test
    public void testCopyFactor() {
        Project project = new Project();
        try {
            this.projectManagementService.saveProject(project);
            Factor factor = this.projectManagementService.copyFactor(project, 1);
            assertNotNull(factor);
            assertEquals("Test2", factor.getName());
            assertEquals(1, project.getExperiment().getFactors().size());
        } catch (ProposalWorkflowException e) {
            fail("Unexpected exception: " + e);
        } catch (InconsistentProjectStateException e) {
            fail("Unexpected exception: " + e);
        }
    }

    /**
     * Test method for {@link gov.nih.nci.caarray.application.project.ProjectManagementService#submitProject(Project)}.
     */
    @Test
    public void testCopySource() {
        Project project = new Project();
        try {
            this.projectManagementService.saveProject(project);
            Source source = this.projectManagementService.copySource(project, 1);
            assertNotNull(source);
            assertEquals("Test2", source.getName());
            assertEquals("Test", source.getDescription());
            assertEquals(1, project.getExperiment().getSources().size());
        } catch (ProposalWorkflowException e) {
            fail("Unexpected exception: " + e);
        } catch (InconsistentProjectStateException e) {
            fail("Unexpected exception: " + e);
        }
    }

    /**
     * Test method for {@link gov.nih.nci.caarray.application.project.ProjectManagementService#submitProject(Project)}.
     */
    @Test
    public void testCopySample() {
        Project project = new Project();
        try {
            this.projectManagementService.saveProject(project);
            Sample sample = this.projectManagementService.copySample(project, 1);
            assertOnAbstractBioMaterialCopy(sample);
            assertEquals(1, project.getExperiment().getSamples().size());
            assertTrue(!sample.getSources().isEmpty());
        } catch (ProposalWorkflowException e) {
            fail("Unexpected exception: " + e);
        } catch (InconsistentProjectStateException e) {
            fail("Unexpected exception: " + e);
        }
    }

    @Test
    public void testCopyExtract() throws ProposalWorkflowException, InconsistentProjectStateException {
        Project project = new Project();
        this.projectManagementService.saveProject(project);
        Extract e = this.projectManagementService.copyExtract(project, 1);
        assertOnAbstractBioMaterialCopy(e);
        assertEquals(1, project.getExperiment().getExtracts().size());
        assertTrue(!e.getSamples().isEmpty());
    } 

    private void assertOnAbstractBioMaterialCopy(AbstractBioMaterial abm) {
        assertNotNull(abm);
        assertEquals("Test2", abm.getName());
        assertEquals("Test", abm.getDescription());
    }

    /**
     * @param project
     */
    private void createProtectionGroup(Project project) {
        // Perform voodoo magic
        try {
            Method m = SecurityUtils.class.getDeclaredMethod("createProtectionGroup", Protectable.class, User.class);
            m.setAccessible(true);
            m.invoke(null, project, UsernameHolder.getCsmUser());
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }

    }

    @Test
    public void testSetUseTcgaPolicy() {
        Project project = this.projectManagementService.getProject(123L);
        UsernameHolder.setUser("caarrayadmin");
        createProtectionGroup(project);
        assertFalse(project.isUseTcgaPolicy());
        try {
            this.projectManagementService.setUseTcgaPolicy(123L, true);
        } catch (ProposalWorkflowException e) {
            fail("Should have been able to set tcga policy status");
        }
        project = this.projectManagementService.getProject(123L);
        assertTrue(project.isUseTcgaPolicy());
    }

    @Test
    public void testDownload() throws IOException, ProposalWorkflowException, InconsistentProjectStateException {
        try {
            this.projectManagementService.prepareForDownload(null);
            fail();
        } catch (IllegalArgumentException iae) {
            // expected
        }

        try {
            this.projectManagementService.prepareForDownload(new ArrayList<CaArrayFile>());
            fail();
        } catch (IllegalArgumentException iae) {
            // expected
        }

        Project project = this.projectManagementService.getProject(123L);
        CaArrayFile file = null;
        try {
            file = this.projectManagementService.addFile(project, MageTabDataFiles.SPECIFICATION_EXAMPLE_IDF);
        } catch (ProposalWorkflowException e) {
            fail("Should not have gotten a workflow exception adding files");
        } catch (InconsistentProjectStateException e) {
            fail("Unexpected inconstient state exception: " + e);
        }

        File f = this.projectManagementService.prepareForDownload(Collections.singleton(file));
        checkFile(file, f);

        try {
            f = this.projectManagementService.prepareHybsForDownload(null, null);
            assertNull(f);
        } catch (IllegalArgumentException e) {
            fail();
        }

        try {
            this.projectManagementService.prepareHybsForDownload(null, new ArrayList<Hybridization>());
            assertNull(f);
        } catch (IllegalArgumentException e) {
            fail();
        }

        Hybridization h = new Hybridization();
        h.setArrayData(new RawArrayData());
        h.getArrayData().setDataFile(file);
        f = this.projectManagementService.prepareHybsForDownload(null, Collections.singleton(h));
        checkFile(file, f);

        f = this.projectManagementService.prepareHybsForDownload(project, Collections.singleton(h));
        checkFile(file, f);

        Hybridization h2 = new Hybridization();
        h2.setArrayData(new RawArrayData());
        CaArrayFile file2 = this.projectManagementService.addFile(new Project(), MageTabDataFiles.SPECIFICATION_EXAMPLE_ADF);
        h2.getArrayData().setDataFile(file2);
        f = this.projectManagementService.prepareHybsForDownload(project, Arrays.asList(h, h2));
        checkFile(file, f);
    }

    private void checkFile(CaArrayFile file, File f) throws FileNotFoundException, IOException {
        assertNotNull(f);

        // make sure it's a zip file
        ZipInputStream zis = new ZipInputStream(new BufferedInputStream(new FileInputStream(f)));
        ZipEntry ze = zis.getNextEntry();
        assertNotNull(ze);
        assertEquals(file.getName(), ze.getName());
        int size = 0;
        int curRead = 0;
        InputStream is = new FileInputStream(MageTabDataFiles.SPECIFICATION_EXAMPLE_IDF);
        while ((curRead = is.read(new byte[is.available()])) > 0) {
            size += curRead;
        }

        while ((curRead = zis.read(new byte[zis.available()])) > 0) {
            size -= curRead;
        }
        assertEquals(0, size);
        is.close();
        zis.close();
    }

    private static class LocalDaoFactoryStub extends DaoFactoryStub {

        LocalProjectDaoStub projectDao = new LocalProjectDaoStub();

        @Override
        public ProjectDao getProjectDao() {
            return this.projectDao;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public SearchDao getSearchDao() {
            return new LocalSearchDaoStub(this.projectDao);
        }
    }

    private static class LocalProjectDaoStub extends ProjectDaoStub {

        final HashMap<Long, PersistentObject> savedObjects = new HashMap<Long, PersistentObject>();
        PersistentObject lastSaved;
        PersistentObject lastDeleted;

        @Override
        public void save(PersistentObject caArrayObject) {
            this.lastSaved = caArrayObject;
            this.savedObjects.put(caArrayObject.getId(), caArrayObject);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public List<Project> getProjectsForCurrentUser(boolean showPublic, PageSortParams<Project> pageSortParams) {
            return new ArrayList<Project>();
        }

        @Override
        public void remove(PersistentObject caArrayEntity) {
            this.lastDeleted = caArrayEntity;
        }

        public PersistentObject getLastDeleted() {
            return this.lastDeleted;
        }

    }

    private static class LocalSearchDaoStub extends SearchDaoStub {
        private final LocalProjectDaoStub projectDao;

        /**
         * @param projectDao
         */
        public LocalSearchDaoStub(LocalProjectDaoStub projectDao) {
            this.projectDao = projectDao;
        }

        /**
         * {@inheritDoc}
         */
        @SuppressWarnings({ "unchecked", "deprecation" })
        @Override
        public <T extends PersistentObject> T retrieve(Class<T> entityClass, Long entityId) {
            if (Sample.class.equals(entityClass)) {
                Sample s = getSample(entityId);
                return (T) s;
            }
            else if (Source.class.equals(entityClass)) {
                Source s = getSource(entityId);
                return (T) s;
            }
            else if (Factor.class.equals(entityClass)) {
                Factor f = getFactor(entityId);
                return (T) f;
            } else if (Extract.class.equals(entityClass)) {
                Extract e = getExtract(entityId);
                return (T) e;
            } else if (Project.class.equals(entityClass)) {
                return (T) getProject(entityId);
            }
            return null;
        }

        /**
         * {@inheritDoc}
         */
        private Project getProject(Long id) {
            if (this.projectDao.savedObjects.containsKey(id)) {
                return (Project) this.projectDao.savedObjects.get(id);
            }
            Project project = new Project();
            // Perform voodoo magic
            try {
                Method m = project.getClass().getSuperclass().getSuperclass().getDeclaredMethod("setId", Long.class);
                m.setAccessible(true);
                m.invoke(project, id);
            } catch (SecurityException e) {
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
            this.projectDao.save(project);
            return project;
        }

        private Extract getExtract(Long entityId) {
            Extract e = new Extract();
            setABM(e, entityId);
            Sample s = getSample(entityId++);
            e.getSamples().add(s);
            s.getExtracts().add(e);
            return e;
        }

        private Sample getSample(Long entityId) {
            Sample s = new Sample();
            setABM(s, entityId);
            Source source = getSource(entityId++);
            s.getSources().add(source);
            source.getSamples().add(s);
            return s;
        }

        private Source getSource(Long entityId) {
            Source s = new Source();
            setABM(s, entityId);
            return s;
        }

        @SuppressWarnings("deprecation")
        private Factor getFactor(Long entityId) {
            Factor f = new Factor();
            f.setName("Test");
            f.setId(entityId);
            return f;
        }

        @SuppressWarnings("deprecation")
        private void setABM(AbstractBioMaterial abm, Long entityId) {
            abm.setName("Test");
            abm.setDescription("Test");
            abm.setId(entityId);
        }
    }


    private static class LocalSessionContextStub extends SessionContextStub {

        @Override
        public Principal getCallerPrincipal() {
            return new Principal() {
                public String getName() {
                    return "testusername";
                }
            };

        }

    }
}
