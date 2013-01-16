//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.application.permissions;

import static org.junit.Assert.assertEquals;
import gov.nih.nci.caarray.application.AbstractServiceIntegrationTest;
import gov.nih.nci.caarray.application.GenericDataService;
import gov.nih.nci.caarray.application.GenericDataServiceStub;
import gov.nih.nci.caarray.dao.CaArrayDaoFactory;
import gov.nih.nci.caarray.domain.permissions.CollaboratorGroup;
import gov.nih.nci.caarray.security.SecurityUtils;
import gov.nih.nci.caarray.util.CaArrayUsernameHolder;
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

    private PermissionsManagementService createPermissionsManagementService(GenericDataService genericDataServiceStub) {
        final CaArrayDaoFactory daoFactory = CaArrayDaoFactory.INSTANCE;
        final PermissionsManagementServiceBean bean = new PermissionsManagementServiceBean();
        bean.setHibernateHelper(this.hibernateHelper);
        bean.setCollaboratorGroupDao(daoFactory.getCollaboratorGroupDao());
        bean.setSearchDao(daoFactory.getSearchDao());

        final ServiceLocatorStub locatorStub = ServiceLocatorStub.registerEmptyLocator();
        locatorStub.addLookup(GenericDataService.JNDI_NAME, genericDataServiceStub);
        final MysqlDataSource ds = new MysqlDataSource();
        final Configuration config = this.hibernateHelper.getConfiguration();
        ds.setUrl(config.getProperty("hibernate.connection.url"));
        ds.setUser(config.getProperty("hibernate.connection.username"));
        ds.setPassword(config.getProperty("hibernate.connection.password"));
        locatorStub.addLookup("java:jdbc/CaArrayDataSource", ds);
        bean.setGenericDataService(genericDataServiceStub);

        return bean;
    }

    @Test
    public void testChangeOwner() throws Exception {
        CaArrayUsernameHolder.setUser(STANDARD_USER);
        Transaction tx = this.hibernateHelper.beginTransaction();
        final CollaboratorGroup created = this.permissionsManagementService.create(TEST);
        tx.commit();
        assertEquals(STANDARD_USER, created.getOwner().getLoginName());
        tx = this.hibernateHelper.beginTransaction();
        List<CollaboratorGroup> groups =
                this.permissionsManagementService.getCollaboratorGroupsForOwner(created.getOwner().getUserId());
        tx.commit();
        assertEquals(1, groups.size());

        CaArrayUsernameHolder.setUser("systemadministrator");
        final User caarrayuser = SecurityUtils.getAuthorizationManager().getUser("caarrayuser");
        tx = this.hibernateHelper.beginTransaction();
        groups = this.permissionsManagementService.getCollaboratorGroupsForOwner(caarrayuser.getUserId());
        assertEquals(0, groups.size());
        this.permissionsManagementService.changeOwner(created.getId(), "caarrayuser");
        tx.commit();
        tx = this.hibernateHelper.beginTransaction();
        groups = this.permissionsManagementService.getCollaboratorGroupsForOwner(caarrayuser.getUserId());
        tx.commit();
        assertEquals(1, groups.size());
    }
}
