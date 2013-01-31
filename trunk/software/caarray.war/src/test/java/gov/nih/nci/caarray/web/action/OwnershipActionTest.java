//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================

package gov.nih.nci.caarray.web.action;

import com.fiveamsolutions.nci.commons.web.struts2.action.ActionHelper;
import com.opensymphony.xwork2.ActionSupport;
import gov.nih.nci.caarray.AbstractCaarrayTest;
import gov.nih.nci.caarray.application.permissions.PermissionsManagementService;
import gov.nih.nci.caarray.application.permissions.PermissionsManagementServiceStub;
import gov.nih.nci.caarray.application.project.ProjectManagementService;
import gov.nih.nci.caarray.application.project.ProjectManagementServiceStub;
import gov.nih.nci.caarray.util.j2ee.ServiceLocatorStub;
import java.util.Arrays;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import gov.nih.nci.security.exceptions.CSException;
import gov.nih.nci.security.exceptions.CSObjectNotFoundException;
import gov.nih.nci.security.authorization.domainobjects.User;

/**
 *
 * @author gax
 */
public class OwnershipActionTest extends AbstractCaarrayTest {

    @Before
    public void setUp() throws Exception {
        ServiceLocatorStub locatorStub = ServiceLocatorStub.registerEmptyLocator();
        locatorStub.addLookup(ProjectManagementService.JNDI_NAME, new ProjectManagementServiceStub());
        locatorStub.addLookup(PermissionsManagementService.JNDI_NAME, new PermissionsManagementServiceStub());
    }

    @Test
    public void assets() {
        OwnershipAction action  = new OwnershipAction();

        String ret = action.assets();
        assertEquals("listOwners", ret);

        User owner = new User();
        owner.setUserId(1L);
        action.setOwner(owner);
        ret = action.assets();
        assertEquals(OwnershipAction.SUCCESS, ret);
        assertNotNull(action.getGroups());
        assertNotNull(action.getProjects());
    }

    @Test
    public void newOwner() {
        OwnershipAction action  = new OwnershipAction();
        String ret = action.newOwner();
        assertEquals("assets", ret);


        action.setGroupIds(Arrays.asList(1L, 2L, 3L));
        action.setProjectIds(Arrays.asList(1L, 2L, 3L));
        User owner = new User();
        owner.setUserId(1L);
        action.setTargetUser(owner);

        ret = action.newOwner();
        assertEquals(ActionSupport.SUCCESS, ret);
    }

    @Test
    public void reassign() throws CSException, CSObjectNotFoundException {
        OwnershipAction action  = new OwnershipAction();
        String ret = action.reassign();
        assertEquals("newOwner", ret);

        ActionHelper.getMessages().clear();
        action.setOwnerId(1L);
        action.setGroupIds(Arrays.asList(1L, 2L, 3L));
        action.setProjectIds(Arrays.asList(1L, 2L, 3L));
        ret = action.reassign();
        assertEquals("listOwners", ret);
        assertEquals(6, ActionHelper.getMessages().size());
    }

}
