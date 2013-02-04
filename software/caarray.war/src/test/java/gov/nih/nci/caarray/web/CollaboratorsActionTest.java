//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package gov.nih.nci.caarray.web;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import gov.nih.nci.caarray.application.permissions.PermissionsManagementService;
import gov.nih.nci.caarray.application.permissions.PermissionsManagementServiceStub;
import gov.nih.nci.caarray.domain.permissions.CollaboratorGroup;
import gov.nih.nci.caarray.util.j2ee.ServiceLocatorStub;
import gov.nih.nci.caarray.web.action.CollaboratorsAction;
import gov.nih.nci.security.authorization.domainobjects.Group;
import gov.nih.nci.security.authorization.domainobjects.User;
import gov.nih.nci.security.exceptions.CSException;
import gov.nih.nci.security.exceptions.CSObjectNotFoundException;
import gov.nih.nci.security.exceptions.CSTransactionException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Test cases for struts action.
 */
public class CollaboratorsActionTest extends AbstractBaseStrutsTest {

    private final CollaboratorsAction action = new CollaboratorsAction();
    private static final PermissionsManagementServiceStub pstub = new PermissionsManagementServiceStub();

    @BeforeClass
    public static void beforeClass() {
        ServiceLocatorStub stub = ServiceLocatorStub.registerEmptyLocator();
        stub.addLookup(PermissionsManagementService.JNDI_NAME, pstub);
    }

    @Before
    public void before() {
        pstub.reset();
    }

    @Test
    public void testList() {
        action.listGroups();
        assertTrue(pstub.isGetForUserCalled());
        assertNotNull(action.getGroups());
    }

    @Test
    public void testDelete() throws CSTransactionException {
        User owner = new User();
        Group group = new Group();
        CollaboratorGroup cg = new CollaboratorGroup(group, owner);
        action.setTargetGroup(cg);
        action.delete();
        assertTrue(pstub.isGetForUserCalled());
        assertEquals(cg, pstub.getCurrentGroup());
    }

    
    @Test
    public void testName() throws CSException {
        action.setGroupName("test");
        action.name();
        assertEquals("test", pstub.getName());
        assertNull(pstub.getCurrentGroup());

        User owner = new User();
        Group group = new Group();
        CollaboratorGroup cg = new CollaboratorGroup(group, owner);
        action.setTargetGroup(cg);
        action.setGroupName("test2");
        action.name();

        assertEquals("test2", pstub.getName());
        assertNotNull(pstub.getCurrentGroup());
    }

    @Test
    public void testAddUsers() throws CSTransactionException, CSObjectNotFoundException {
        User owner = new User();
        Group group = new Group();
        group.setUsers(Collections.emptySet());
        CollaboratorGroup cg = new CollaboratorGroup(group, owner);
        List<Long> toAdd = new ArrayList<Long>();
        toAdd.add(1L);

        action.setTargetGroup(cg);
        action.setUsers(toAdd);
        action.addUsers();

        List<Long> stubAdd = pstub.getAddedUsers();
        CollaboratorGroup stubGroup = pstub.getCurrentGroup();
        assertEquals(toAdd.size(), stubAdd.size());
        assertEquals(cg, stubGroup);
    }

    @Test
    public void testRemoveUsers() throws CSTransactionException, CSObjectNotFoundException {
        User owner = new User();
        Group group = new Group();
        CollaboratorGroup cg = new CollaboratorGroup(group, owner);
        List<Long> toAdd = new ArrayList<Long>();
        toAdd.add(1L);

        action.setTargetGroup(cg);
        action.setUsers(toAdd);
        action.removeUsers();

        List<Long> stubRemove = pstub.getRemovedUsers();
        CollaboratorGroup stubGroup = pstub.getCurrentGroup();
        assertEquals(toAdd.size(), stubRemove.size());
        assertEquals(cg, stubGroup);
    }

    @Test
    public void testEdit() {
        action.edit();
        assertTrue(!pstub.isGetUsersCalled());

        action.setTargetGroup(new CollaboratorGroup(new Group(), new User()));
        action.edit();
        assertTrue(!pstub.isGetUsersCalled());
    }

    @After
    public void reset() {
        pstub.reset();
    }
}
