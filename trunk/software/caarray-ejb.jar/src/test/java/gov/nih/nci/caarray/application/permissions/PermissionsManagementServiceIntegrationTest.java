//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.application.permissions;

import static org.junit.Assert.assertEquals;
import gov.nih.nci.caarray.application.AbstractServiceIntegrationTest;
import gov.nih.nci.caarray.application.GenericDataService;
import gov.nih.nci.caarray.application.GenericDataServiceStub;
import gov.nih.nci.caarray.domain.permissions.CollaboratorGroup;
import gov.nih.nci.caarray.security.SecurityUtils;
import gov.nih.nci.caarray.util.HibernateUtil;
import gov.nih.nci.caarray.util.UsernameHolder;
import gov.nih.nci.caarray.util.j2ee.ServiceLocatorStub;
import gov.nih.nci.security.authorization.domainobjects.User;

import java.util.List;

import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.junit.Before;
import org.junit.Test;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;

/**
 * Test cases for service.
 */
@SuppressWarnings("PMD")
public class PermissionsManagementServiceIntegrationTest extends AbstractServiceIntegrationTest {

    private static final String TEST = "test";
    private PermissionsManagementService permissionsManagementService;
    private final GenericDataServiceStub genericDataServiceStub = new GenericDataServiceStub();

    @Before
    public void setup() {
        this.permissionsManagementService = createPermissionsManagementService(this.genericDataServiceStub);
    }
    
    private static PermissionsManagementService createPermissionsManagementService(GenericDataService genericDataServiceStub) {
        PermissionsManagementServiceBean bean = new PermissionsManagementServiceBean();
        ServiceLocatorStub locatorStub = ServiceLocatorStub.registerEmptyLocator();
        locatorStub.addLookup(GenericDataService.JNDI_NAME, genericDataServiceStub);
        MysqlDataSource ds = new MysqlDataSource();
        Configuration config = HibernateUtil.getConfiguration();
        ds.setUrl(config.getProperty("hibernate.connection.url"));
        ds.setUser(config.getProperty("hibernate.connection.username"));
        ds.setPassword(config.getProperty("hibernate.connection.password"));
        locatorStub.addLookup("java:jdbc/CaArrayDataSource", ds);
        bean.setGenericDataService(genericDataServiceStub);

        return bean;
    }

    @Test
    public void testChangeOwner() throws Exception {
        UsernameHolder.setUser(STANDARD_USER);
        Transaction tx = HibernateUtil.beginTransaction();
        CollaboratorGroup created = this.permissionsManagementService.create(TEST);
        tx.commit();
        assertEquals(STANDARD_USER, created.getOwner().getLoginName());
        tx = HibernateUtil.beginTransaction();
        List<CollaboratorGroup> groups = this.permissionsManagementService.getCollaboratorGroupsForOwner(created
                .getOwner().getUserId());
        tx.commit();
        assertEquals(1, groups.size());

        UsernameHolder.setUser("systemadministrator");
        User caarrayuser = SecurityUtils.getAuthorizationManager().getUser("caarrayuser");
        tx = HibernateUtil.beginTransaction();
        groups = this.permissionsManagementService.getCollaboratorGroupsForOwner(caarrayuser.getUserId());
        assertEquals(0, groups.size());
        this.permissionsManagementService.changeOwner(created.getId(), "caarrayuser");
        tx.commit();
        tx = HibernateUtil.beginTransaction();
        groups = this.permissionsManagementService.getCollaboratorGroupsForOwner(caarrayuser.getUserId());
        tx.commit();
        assertEquals(1, groups.size());
    }
}
