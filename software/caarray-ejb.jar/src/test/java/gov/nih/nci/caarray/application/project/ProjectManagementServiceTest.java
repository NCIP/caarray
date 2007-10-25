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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;
import gov.nih.nci.caarray.application.GenericDataService;
import gov.nih.nci.caarray.application.GenericDataServiceStub;
import gov.nih.nci.caarray.application.SessionContextStub;
import gov.nih.nci.caarray.application.fileaccess.FileAccessService;
import gov.nih.nci.caarray.application.fileaccess.FileAccessServiceStub;
import gov.nih.nci.caarray.dao.ProjectDao;
import gov.nih.nci.caarray.dao.SearchDao;
import gov.nih.nci.caarray.dao.stub.DaoFactoryStub;
import gov.nih.nci.caarray.dao.stub.ProjectDaoStub;
import gov.nih.nci.caarray.dao.stub.SearchDaoStub;
import gov.nih.nci.caarray.domain.PersistentObject;
import gov.nih.nci.caarray.domain.file.CaArrayFile;
import gov.nih.nci.caarray.domain.project.Factor;
import gov.nih.nci.caarray.domain.project.Project;
import gov.nih.nci.caarray.domain.sample.Sample;
import gov.nih.nci.caarray.domain.sample.Source;
import gov.nih.nci.caarray.test.data.magetab.MageTabDataFiles;
import gov.nih.nci.caarray.util.j2ee.ServiceLocatorStub;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.junit.Before;
import org.junit.Test;

@SuppressWarnings("PMD")
public class ProjectManagementServiceTest {

    private ProjectManagementService projectManagementService;
    private final LocalDaoFactoryStub daoFactoryStub = new LocalDaoFactoryStub();
    private final FileAccessService fileAccessService = new FileAccessServiceStub();
    private final GenericDataService genericDataService = new GenericDataServiceStub();
    private final LocalSessionContextStub sessionContextStub = new LocalSessionContextStub();

    @Before
    public void setUpService() {
        ProjectManagementServiceBean projectManagementServiceBean = new ProjectManagementServiceBean();
        projectManagementServiceBean.setDaoFactory(this.daoFactoryStub);
        ServiceLocatorStub locatorStub = new ServiceLocatorStub();
        locatorStub.addLookup(FileAccessService.JNDI_NAME, fileAccessService);
        projectManagementServiceBean.setSessionContext(this.sessionContextStub);
        projectManagementServiceBean.setServiceLocator(locatorStub);
        projectManagementServiceBean.setGenericDataService(genericDataService);
        this.projectManagementService = projectManagementServiceBean;
    }

    @Test
    public void testGetWorkspaceProjects() {
        List<Project> projects = this.projectManagementService.getWorkspaceProjects();
        assertNotNull(projects);
        assertEquals("testusername", this.daoFactoryStub.projectDao.username);
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
        CaArrayFile file = this.projectManagementService.addFile(project, MageTabDataFiles.SPECIFICATION_EXAMPLE_IDF);
        assertEquals(MageTabDataFiles.SPECIFICATION_EXAMPLE_IDF.getName(), file.getName());
        assertEquals(1, project.getFiles().size());
        assertNotNull(project.getFiles().iterator().next().getProject());
        assertContains(project.getFiles(), MageTabDataFiles.SPECIFICATION_EXAMPLE_IDF);
    }

    private void assertContains(Set<CaArrayFile> caArrayFiles, File file) {
        for (CaArrayFile caArrayFile : caArrayFiles) {
            if (file.getName().equals(caArrayFile.getName())) {
                return;
            }
        }
        fail("CaArrayFileSet did not contain " + file.getAbsolutePath());
    }

    /**
     * Test method for {@link gov.nih.nci.caarray.application.project.ProjectManagementService#submitProject(Project)}.
     */
    @Test
    public void testSubmitProject() {
        Project project = new Project();
        try {
            this.projectManagementService.submitProject(project);
            assertEquals(project, this.daoFactoryStub.projectDao.lastSaved);
        } catch (ProposalWorkflowException e) {
            fail("Unexpected exception: " + e);
        }
    }

    /**
     * Test method for {@link gov.nih.nci.caarray.application.project.ProjectManagementService#submitProject(Project)}.
     */
    @Test
    public void testCopyFactor() {
        Project project = new Project();
        try {
            this.projectManagementService.saveDraftProject(project);
            Factor factor = this.projectManagementService.copyFactor(project, 1);
            assertNotNull(factor);
            assertEquals("Test2", factor.getName());
            assertEquals(1, project.getExperiment().getFactors().size());
        } catch (ProposalWorkflowException e) {
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
            this.projectManagementService.saveDraftProject(project);
            Source source = this.projectManagementService.copySource(project, 1);
            assertNotNull(source);
            assertEquals("Test2", source.getName());
            assertEquals("Test", source.getDescription());
            assertEquals(1, project.getExperiment().getSources().size());
        } catch (ProposalWorkflowException e) {
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
            this.projectManagementService.saveDraftProject(project);
            Sample sample = this.projectManagementService.copySample(project, 1);
            assertNotNull(sample);
            assertEquals("Test2", sample.getName());
            assertEquals("Test", sample.getDescription());
            assertEquals(1, project.getExperiment().getSamples().size());
        } catch (ProposalWorkflowException e) {
            fail("Unexpected exception: " + e);
        }
    }

    @Test
    public void testDownload() throws IOException {
        try {
            this.projectManagementService.prepareForDownload(null);
            fail();
        } catch (IllegalArgumentException iae) {
            // expected
        }

        try {
            this.projectManagementService.prepareForDownload(new ArrayList<Long>());
            fail();
        } catch (IllegalArgumentException iae) {
            // expected
        }

        Project project = this.projectManagementService.getProject(123L);
        CaArrayFile file = this.projectManagementService.addFile(project, MageTabDataFiles.SPECIFICATION_EXAMPLE_IDF);

        File f = this.projectManagementService.prepareForDownload(Collections.singleton(file.getId()));
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

        LocalProjectDaoStub projectDao;

        @Override
        public ProjectDao getProjectDao() {
            if (this.projectDao == null) {
                this.projectDao = new LocalProjectDaoStub();
            }
            return this.projectDao;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public SearchDao getSearchDao() {
            return new LocalSearchDaoStub();
        }
    }

    private static class LocalProjectDaoStub extends ProjectDaoStub {

        private final HashMap<Long, PersistentObject> savedObjects = new HashMap<Long, PersistentObject>();
        private PersistentObject lastSaved;
        private String username;

        @Override
        public void save(PersistentObject caArrayObject) {
            this.lastSaved = caArrayObject;
            this.savedObjects.put(caArrayObject.getId(), caArrayObject);
        }

        @Override
        public List<Project> getProjectsForUser(String name) {
            this.username = name;
            return new ArrayList<Project>();
        }

        @Override
        @SuppressWarnings("PMD")
        public Project getProject(long id) {
            if (this.savedObjects.containsKey(id)) {
                return (Project) this.savedObjects.get(id);
            }
            Project project = new Project();;
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
            save(project);
            return project;
        }

    }

    private static class LocalSearchDaoStub extends SearchDaoStub {
        /**
         * {@inheritDoc}
         */
        @SuppressWarnings("unchecked")
        @Override
        public <T extends PersistentObject> T retrieve(Class<T> entityClass, Long entityId) {
            if (Sample.class.equals(entityClass)) {
                Sample s = new Sample();
                s.setName("Test");
                s.setDescription("Test");
                s.setId(entityId);
                return (T) s;
            }
            else if (Source.class.equals(entityClass)) {
                Source s = new Source();
                s.setName("Test");
                s.setDescription("Test");
                s.setId(entityId);
                return (T) s;
            }
            else if (Factor.class.equals(entityClass)) {
                Factor f = new Factor();
                f.setName("Test");
                f.setId(entityId);
                return (T) f;
            }
            return null;
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
