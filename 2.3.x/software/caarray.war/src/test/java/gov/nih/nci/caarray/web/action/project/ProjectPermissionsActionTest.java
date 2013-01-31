//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.web.action.project;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import gov.nih.nci.caarray.AbstractCaarrayTest;
import gov.nih.nci.caarray.application.GenericDataService;
import gov.nih.nci.caarray.application.GenericDataServiceStub;
import gov.nih.nci.caarray.application.permissions.PermissionsManagementService;
import gov.nih.nci.caarray.application.permissions.PermissionsManagementServiceStub;
import gov.nih.nci.caarray.application.project.ProjectManagementService;
import gov.nih.nci.caarray.application.project.ProjectManagementServiceStub;
import gov.nih.nci.caarray.domain.permissions.AccessProfile;
import gov.nih.nci.caarray.domain.permissions.CollaboratorGroup;
import gov.nih.nci.caarray.domain.permissions.SampleSecurityLevel;
import gov.nih.nci.caarray.domain.permissions.SecurityLevel;
import gov.nih.nci.caarray.domain.project.Project;
import gov.nih.nci.caarray.domain.sample.Sample;
import gov.nih.nci.caarray.security.PermissionDeniedException;
import gov.nih.nci.caarray.util.j2ee.ServiceLocatorStub;

import org.apache.struts2.ServletActionContext;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;

import com.fiveamsolutions.nci.commons.data.persistent.PersistentObject;
import com.fiveamsolutions.nci.commons.web.struts2.action.ActionHelper;
import com.opensymphony.xwork2.Action;

/**
 * @author Winston Cheng
 *
 */
public class ProjectPermissionsActionTest extends AbstractCaarrayTest {
    private final ProjectPermissionsAction action = new ProjectPermissionsAction();
    private static Project DUMMY_PROJECT = new Project();
    private static CollaboratorGroup DUMMY_GROUP = new CollaboratorGroup();
    private static AccessProfile DUMMY_PROFILE = new AccessProfile(SecurityLevel.READ_SELECTIVE);
    private static Sample DUMMY_SAMPLE = new Sample();

    @SuppressWarnings("deprecation")
    @Before
    public void setUp() throws Exception {
        ServiceLocatorStub locatorStub = ServiceLocatorStub.registerEmptyLocator();
        locatorStub.addLookup(GenericDataService.JNDI_NAME, new LocalGenericDataService());
        locatorStub.addLookup(PermissionsManagementService.JNDI_NAME, new PermissionsManagementServiceStub());
        locatorStub.addLookup(ProjectManagementService.JNDI_NAME, new LocalProjectManagementService());
        DUMMY_PROJECT.setId(1L);
        DUMMY_GROUP.setId(1L);
        DUMMY_PROFILE.setId(1L);
        DUMMY_SAMPLE.setId(1L);
        action.setProject(DUMMY_PROJECT);
        ServletActionContext.setRequest(new MockHttpServletRequest());
    }

    @SuppressWarnings("deprecation")
    @Test
    public void testPrepare() throws Exception {
        // no collab group or access profile
        action.prepare();
        assertNull(action.getCollaboratorGroup().getId());
        assertNull(action.getAccessProfile().getId());

        // valid collab group and access profile
        CollaboratorGroup group = new CollaboratorGroup();
        group.setId(1L);
        action.setCollaboratorGroup(group);
        AccessProfile profile = new AccessProfile(SecurityLevel.NONE);
        profile.setId(1L);
        action.setAccessProfile(profile);
        action.prepare();
        assertEquals(DUMMY_GROUP, action.getCollaboratorGroup());
        assertEquals(DUMMY_PROFILE, action.getAccessProfile());

        // invalid group id
        group = new CollaboratorGroup();
        group.setId(2L);
        action.setCollaboratorGroup(group);
        try {
            action.prepare();
            fail("Expected PermissionDeniedException");
        } catch (PermissionDeniedException pde) {}

        // invalid profile id
        profile = new AccessProfile(SecurityLevel.NONE);
        profile.setId(2L);
        action.setAccessProfile(profile);
        action.setCollaboratorGroup(DUMMY_GROUP);
        try {
            action.prepare();
            fail("Expected PermissionDeniedException");
        } catch (PermissionDeniedException pde) {}
    }

    @Test
    public void testEditPermissions() {
        assertEquals(Action.SUCCESS, action.editPermissions());
    }

    @Test
    public void testSetTcgaPolicy() {
        assertEquals(Action.SUCCESS, action.setTcgaPolicy());
        assertTrue(ActionHelper.getMessages().contains("project.tcgaPolicyUpdated"));
    }

    @Test
    public void testLoadPublicProfile() {
        action.setAccessProfile(DUMMY_PROFILE);
        action.getAccessProfile().getSampleSecurityLevels().put(DUMMY_SAMPLE, SampleSecurityLevel.NONE);
        assertEquals("accessProfile", action.loadPublicProfile());
    }

    @Test
    public void testLoadGroupProfile() {
        assertEquals("accessProfile", action.loadGroupProfile());
    }

    @Test
    public void testSaveAccessProfile() {
        action.setAccessProfile(DUMMY_PROFILE);
        action.getSampleSecurityLevels().put(1L, SampleSecurityLevel.NONE);
        assertEquals(Action.SUCCESS, action.saveAccessProfile());
    }

    private static class LocalGenericDataService extends GenericDataServiceStub {
        @SuppressWarnings("unchecked")
        @Override
        public <T extends PersistentObject> T getPersistentObject(Class<T> entityClass, Long entityId) {
            if (entityClass.equals(CollaboratorGroup.class) && entityId.equals(1L)) {
                return (T)DUMMY_GROUP;
            }
            if (entityClass.equals(AccessProfile.class) && entityId.equals(1L)) {
                return (T)DUMMY_PROFILE;
            }
            if (entityClass.equals(Sample.class) && entityId.equals(1L)) {
                return (T)DUMMY_SAMPLE;
            }
            return null;
        }
    }
    private static class LocalProjectManagementService extends ProjectManagementServiceStub {
        @Override
        public AccessProfile addGroupProfile(Project project, CollaboratorGroup group) {
            return new AccessProfile(SecurityLevel.NONE);
        }
    }
}
