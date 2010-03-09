/**
 * The software subject to this notice and license includes both human readable
 * source code form and machine readable, binary, object code form. The caarray-war
 * Software was developed in conjunction with the National Cancer Institute
 * (NCI) by NCI employees and 5AM Solutions, Inc. (5AM). To the extent
 * government employees are authors, any rights in such works shall be subject
 * to Title 17 of the United States Code, section 105.
 *
 * This caarray-war Software License (the License) is between NCI and You. You (or
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
 * its rights in the caarray-war Software to (i) use, install, access, operate,
 * execute, copy, modify, translate, market, publicly display, publicly perform,
 * and prepare derivative works of the caarray-war Software; (ii) distribute and
 * have distributed to and by third parties the caarray-war Software and any
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
package gov.nih.nci.caarray.web.action.project;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import gov.nih.nci.caarray.AbstractCaarrayTest;
import gov.nih.nci.caarray.application.GenericDataService;
import gov.nih.nci.caarray.application.GenericDataServiceStub;
import gov.nih.nci.caarray.application.project.ProjectManagementService;
import gov.nih.nci.caarray.application.project.ProjectManagementServiceStub;
import gov.nih.nci.caarray.application.project.ProposalWorkflowException;
import gov.nih.nci.caarray.domain.project.Project;
import gov.nih.nci.caarray.security.PermissionDeniedException;
import gov.nih.nci.caarray.security.SecurityUtils;
import gov.nih.nci.caarray.util.UsernameHolder;
import gov.nih.nci.caarray.util.j2ee.ServiceLocatorStub;
import gov.nih.nci.security.authorization.domainobjects.User;

import org.apache.struts2.ServletActionContext;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;

import com.fiveamsolutions.nci.commons.data.persistent.PersistentObject;
import com.fiveamsolutions.nci.commons.web.struts2.action.ActionHelper;
import com.opensymphony.xwork2.Action;

/**
 * @author Scott Miller
 */
public class ProjectActionTest extends AbstractCaarrayTest {

    private static final String WORKSPACE = "workspace";
    ProjectAction action = new ProjectAction();
    private static final ProjectManagementServiceStub projectManagementServiceStub = new LocalProjectManagementServiceStub();
    private static final GenericDataServiceStub genericDataServiceStub = new LocalGenericDataServiceStub();

    @BeforeClass
    @SuppressWarnings("PMD")
    public static void beforeClass() {
        ServiceLocatorStub stub = ServiceLocatorStub.registerEmptyLocator();
        stub.addLookup(ProjectManagementService.JNDI_NAME, projectManagementServiceStub);
        stub.addLookup(GenericDataService.JNDI_NAME, genericDataServiceStub);
    }

    @Before
    public void before() {
        this.action = new ProjectAction() {
            private static final long serialVersionUID = 1L;

            @Override
            protected User getCsmUser() {
                return null;
            }
        };
        projectManagementServiceStub.reset();
        ServletActionContext.setRequest(new MockHttpServletRequest());
    }

    @SuppressWarnings("deprecation")
    private Project getTestProject(Long id) {
        Project p = new Project() {
            private static final long serialVersionUID = 1L;

            @Override
            public boolean hasReadPermission(User user) {
                return (this.getId() == 1l);
            }

            @Override
            public boolean hasWritePermission(User user) {
                return (this.getId() == 1l);
            }

        };
        p.setId(id);
        return p;
    }

    @Test
    public void testPrepare() throws Exception {
        this.action.setProject(this.getTestProject(1l));
        this.action.prepare();
        assertNotNull(this.action.getExperiment());
    }

    @Test
    public void testCreate() throws Exception {
        assertFalse(this.action.isEditMode());
        assertEquals(Action.INPUT, this.action.create());
        assertTrue(this.action.isEditMode());
    }

    @Test
    public void testEdit() {
        this.action.setProject(this.getTestProject(null));
        assertEquals(WORKSPACE, this.action.edit());
        this.action.setProject(this.getTestProject((2l)));
        assertEquals("login-details-id", this.action.edit());
        this.action.setProject(this.getTestProject((1l)));
        assertEquals(Action.INPUT, this.action.edit());
        assertTrue(this.action.isEditMode());
    }

    @Test
    public void testDetails() {
        this.action.setProject(this.getTestProject(null));
        assertEquals(WORKSPACE, this.action.details());
        this.action.setProject(this.getTestProject((2l)));
        assertEquals("login-details-id", this.action.details());
        this.action.setProject(this.getTestProject((1l)));
        assertEquals(Action.INPUT, this.action.details());
        assertFalse(this.action.isEditMode());
    }

    @Test
    public void testChangeStatus() {
        this.action.setWorkflowStatus(Boolean.FALSE);
        this.action.setProject(this.getTestProject(1l));
        assertEquals(WORKSPACE, this.action.changeWorkflowStatus());
        assertEquals(1, projectManagementServiceStub.getChangeWorkflowStatusCount());
        this.action.setProject(this.getTestProject(999l));
        assertEquals(Action.INPUT, this.action.changeWorkflowStatus());
        assertEquals(2, projectManagementServiceStub.getChangeWorkflowStatusCount());
    }

    @Test
    public void testDelete() {
        this.action.setProject(this.getTestProject(1l));
        assertEquals(WORKSPACE, this.action.delete());
        assertTrue(ActionHelper.getMessages().get(0).contains("project.deleted"));
    }

    @Test
    public void testDeleteNonDraft() {
        this.action.setProject(this.getTestProject(1l));
        this.action.getProject().setLocked(true);
        assertEquals(WORKSPACE, this.action.delete());
        assertTrue(ActionHelper.getMessages().get(0).contains("project.deleteOnlyDrafts"));
    }

    @Test
    public void testDeleteNotFound() {
        this.action.setProject(new Project());
        UsernameHolder.setUser("someuser");
        assertEquals(WORKSPACE, this.action.delete());
        assertTrue(ActionHelper.getMessages().get(0).contains("project.notFound"));
    }

    @Test(expected = PermissionDeniedException.class)
    public void testDeleteNoWritePermission() {
        this.action.setProject(this.getTestProject(2l));
        UsernameHolder.setUser("unauthorizeduser");
        this.action.delete();
    }

    private static class LocalProjectManagementServiceStub extends ProjectManagementServiceStub {
        /**
         * {@inheritDoc}
         */
        public void deleteProject(Project project) throws ProposalWorkflowException {
            if (UsernameHolder.getUser().equals("unauthorizeduser")) {
                throw new PermissionDeniedException(project, SecurityUtils.WRITE_PRIVILEGE, UsernameHolder.getUser());
            }
            if (project.isLocked()) {
                throw new ProposalWorkflowException("Cannot delete unlocked project");
            }
            // assume success if the checks pass
        }
    }
    
    private static class LocalGenericDataServiceStub extends GenericDataServiceStub {
        @Override
        public <T extends PersistentObject> T getPersistentObject(Class<T> entityClass, Long entityId) {
            if (Project.class.equals(entityClass)) {
                Project project = new Project();
                project.setId(entityId);
                return (T) project;
            } else {
                return super.getPersistentObject(entityClass, entityId);
            }
        }
    }
}
