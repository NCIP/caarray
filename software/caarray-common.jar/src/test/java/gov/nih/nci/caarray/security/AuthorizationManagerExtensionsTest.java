//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.security;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import edu.georgetown.pir.Organism;
import gov.nih.nci.caarray.dao.HibernateIntegrationTestCleanUpUtility;
import gov.nih.nci.caarray.domain.project.Project;
import gov.nih.nci.caarray.domain.vocabulary.TermSource;
import gov.nih.nci.caarray.util.CaArrayHibernateHelper;
import gov.nih.nci.caarray.util.CaArrayHibernateHelperModule;

import java.math.BigInteger;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;

/**
 * @author wcheng
 *
 */
public class AuthorizationManagerExtensionsTest {
    private static Project testProject;
    
    private static Injector injector;
    private static CaArrayHibernateHelper hibernateHelper;

    /**
     * post-construct lifecycle method; intializes the Guice injector that will provide dependencies.
     */
    @BeforeClass
    public static void init() {
        injector = createInjector();
        hibernateHelper = injector.getInstance(CaArrayHibernateHelper.class);
        SecurityUtils.init();
    }

    /**
     * @return a Guice injector from which this will obtain dependencies.
     */
    protected static Injector createInjector() {
        return Guice.createInjector(new CaArrayHibernateHelperModule(), new AbstractModule() {
            @Override
            protected void configure() {
                requestStaticInjection(gov.nih.nci.caarray.security.AuthorizationManagerExtensions.class);
                requestStaticInjection(gov.nih.nci.caarray.security.SecurityUtils.class);
                requestStaticInjection(gov.nih.nci.caarray.validation.UniqueConstraintValidator.class);
            }
        });
    }

    @After
    public void after() {
        try {
            final Transaction tx = hibernateHelper.getCurrentSession().getTransaction();
            if (tx.isActive()) {
                tx.rollback();
            }
        } catch (final HibernateException e) {
            // ok - there was no active transaction
        }
        hibernateHelper.unbindAndCleanupSession();
        HibernateIntegrationTestCleanUpUtility.cleanUp();
    }

    @Test
    public void testRefreshDelete() throws Exception {
        saveDummyProject();

        // delete protection element
        Transaction tx = hibernateHelper.beginTransaction();
        String deletePe = String.format(
                "delete from csm_protection_element where object_id = '%s' and attribute = 'id' and attribute_value = %s",
                Project.class.getName(), testProject.getId());
        hibernateHelper.getCurrentSession().createSQLQuery(deletePe).executeUpdate();
        tx.commit();

        // refreshTables
        tx = hibernateHelper.beginTransaction();
        int peiCount1 = getRowCount("csm_pei_project_id");
        int groupCount1 = getRowCount("csm_project_id_group");
        AuthorizationManagerExtensions.refreshInstanceTables(SecurityUtils.getApplication());
        tx.commit();
   
        // pei and group privilege entries should have been deleted
        tx = hibernateHelper.beginTransaction();
        int peiCount2 = getRowCount("csm_pei_project_id");
        int groupCount2 = getRowCount("csm_project_id_group");
        assertTrue(peiCount1 > peiCount2);
        assertTrue(groupCount1 > groupCount2);
        tx.commit();
    }
    
    @Test
    public void testRefreshInsert() throws Exception {
        saveDummyProject();

        // delete pei, group privilege entries
        Transaction tx = hibernateHelper.beginTransaction();
        int peiCount1 = getRowCount("csm_pei_project_id");
        int groupCount1 = getRowCount("csm_project_id_group");
        String deletePei =
                String.format("delete from csm_pei_project_id where attribute_value = %s", testProject.getId());
        String deleteGroupPrivilege =
                String.format("delete from csm_project_id_group where attribute_value = %s", testProject.getId());
        hibernateHelper.getCurrentSession().createSQLQuery(deletePei).executeUpdate();
        hibernateHelper.getCurrentSession().createSQLQuery(deleteGroupPrivilege).executeUpdate();
        tx.commit();

        // refreshTables
        tx = hibernateHelper.beginTransaction();
        int peiCount2 = getRowCount("csm_pei_project_id");
        int groupCount2 = getRowCount("csm_project_id_group");
        AuthorizationManagerExtensions.refreshInstanceTables(SecurityUtils.getApplication());
        tx.commit();
   
        // pei and group privilege entries should have been readded
        tx = hibernateHelper.beginTransaction();
        int peiCount3 = getRowCount("csm_pei_project_id");
        int groupCount3 = getRowCount("csm_project_id_group");
        assertTrue(peiCount1 > peiCount2);
        assertTrue(groupCount1 > groupCount2);
        assertEquals(peiCount1, peiCount3);
        assertEquals(groupCount1, groupCount3);
    }
    
    private void saveDummyProject() {
        Transaction tx = hibernateHelper.beginTransaction();
        TermSource ts = new TermSource();
        ts.setName("TS");
        ts.setUrl("http://ts");
        Organism o = new Organism();
        o.setScientificName("baz");
        o.setTermSource(ts);
        testProject = new Project();
        testProject.getExperiment().setTitle("Foo");
        testProject.getExperiment().setOrganism(o);
        hibernateHelper.getCurrentSession().save(testProject);
        tx.commit();
    }
    
    private int getRowCount(String tableName) {
        Session s = hibernateHelper.getCurrentSession();
        BigInteger count = (BigInteger)s.createSQLQuery("select count(*) from " + tableName).uniqueResult();
        return count.intValue();
    }
}
