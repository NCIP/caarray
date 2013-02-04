//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package gov.nih.nci.caarray.dao;

import gov.nih.nci.caarray.domain.vocabulary.Category;
import gov.nih.nci.caarray.domain.vocabulary.Term;
import gov.nih.nci.caarray.domain.vocabulary.TermSource;
import gov.nih.nci.caarray.security.SecurityUtils;
import gov.nih.nci.caarray.util.CaArrayHibernateHelper;
import gov.nih.nci.caarray.util.CaArrayHibernateHelperFactory;
import gov.nih.nci.security.authorization.domainobjects.Application;
import gov.nih.nci.security.authorization.domainobjects.FilterClause;
import gov.nih.nci.security.authorization.domainobjects.Group;
import gov.nih.nci.security.authorization.domainobjects.InstanceLevelMappingElement;
import gov.nih.nci.security.authorization.domainobjects.Privilege;
import gov.nih.nci.security.authorization.domainobjects.ProtectionElement;
import gov.nih.nci.security.authorization.domainobjects.Role;
import gov.nih.nci.security.authorization.domainobjects.User;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.ArrayUtils;
import org.apache.log4j.Logger;
import org.hibernate.EmptyInterceptor;
import org.hibernate.EntityMode;
import org.hibernate.FlushMode;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.metadata.ClassMetadata;

/**
 * Provides methods to remove objects created during Hibernate integration tests.
 */
public final class HibernateIntegrationTestCleanUpUtility {
    private static final CaArrayHibernateHelper hibernateHelper = CaArrayHibernateHelperFactory
            .getCaArrayHibernateHelper();

    private static final Logger LOG = Logger.getLogger(HibernateIntegrationTestCleanUpUtility.class);
    private static List<Class<?>> classesToRemove;
    private static List<Class<?>> collsToRemove;
    private static final String SELF_GROUP_PATTERN = "'" + SecurityUtils.SELF_GROUP_PREFIX + "%'";

    // this sets up constraints that prevent some instances from being removed. these would be configuration
    // data that are expected to always be populated and are needed for proper operation of caArray
    // if a persistence class is found in this map, its instances will only be deleted if they match the
    // constraint in the map
    private static final Map<Class<?>, String> CLASS_DELETE_CONSTRAINTS = new HashMap<Class<?>, String>();
    static {
        CLASS_DELETE_CONSTRAINTS.put(TermSource.class, "id > 0");
        CLASS_DELETE_CONSTRAINTS.put(Category.class, "id > 0");
        CLASS_DELETE_CONSTRAINTS.put(Term.class, "id > 0");
        CLASS_DELETE_CONSTRAINTS.put(User.class, "id > 11");
        CLASS_DELETE_CONSTRAINTS.put(Group.class, "id > 8 and not (groupName like " + SELF_GROUP_PATTERN + ")");
        CLASS_DELETE_CONSTRAINTS.put(ProtectionElement.class, "id > 2");
        CLASS_DELETE_CONSTRAINTS.put(Application.class, "0 = 1");
        CLASS_DELETE_CONSTRAINTS.put(Privilege.class, "0 = 1");
        CLASS_DELETE_CONSTRAINTS.put(Role.class, "0 = 1");
        CLASS_DELETE_CONSTRAINTS.put(FilterClause.class, "0 = 1");
        CLASS_DELETE_CONSTRAINTS.put(InstanceLevelMappingElement.class, "0 = 1");
    }

    private HibernateIntegrationTestCleanUpUtility() {
        super();
    }

    /**
     * Delete all instances of CSM and caArray classes from the databases, except for a few
     */
    @SuppressWarnings("PMD")
    public static void cleanUp() {
        boolean cleanupComplete = doCleanUp();
        if (!cleanupComplete) {
            // This means we saw an object again, and that's a problem
            throw new IllegalStateException("Last unit test didn't fully clean up after itself");
        }
    }

    private static boolean doCleanUp() {
        if (classesToRemove == null) {
            retrieveClassMetadata();
        }
        int numIterations = classesToRemove.size() + 1;
        boolean done = false;
        for (int i = 0; i < numIterations && !done; i++) {
            done = true;
            for (Class<?> c : classesToRemove) {
                boolean removed = doCleanUp(c);
                done &= !removed;
            }
        }
        return done;
    }

    private static boolean doCleanUp(Class<?> c) {
        StringBuilder sb = new StringBuilder("DELETE FROM " + c.getName());
        String condition = CLASS_DELETE_CONSTRAINTS.get(c);
        if (condition != null) {
            sb.append(" where ").append(condition);
        }
        return doCleanUp(sb.toString());
    }

    private static boolean doCleanUp(String deleteSql) {
        Transaction tx = null;
        boolean removed = false;
        Session s = null;
        try {
            s = getSession();
            s.setFlushMode(FlushMode.MANUAL);
            tx = s.beginTransaction();
            disableForeignKeyChecks(s);
            int deletedObjs = s.createQuery(deleteSql).executeUpdate();
            if (deletedObjs > 0) {
                removed = true;
            }
            s.flush();
            tx.commit();
        } catch (DAOException deleteException) {
            hibernateHelper.rollbackTransaction(tx);
            LOG.warn("Error cleaning up test objects.", deleteException);
        } catch (HibernateException he) {
            hibernateHelper.rollbackTransaction(tx);
            LOG.warn("Error cleaning up test objects.", he);
        } finally {
            s.close();
        }
        return removed;
    }

    @SuppressWarnings("unchecked")
    private static void retrieveClassMetadata() {
        Map<String, ClassMetadata> classMetadataMap = hibernateHelper.getSessionFactory().getAllClassMetadata();

        classesToRemove = new LinkedList<Class<?>>();
        for (ClassMetadata classMetadata : classMetadataMap.values()) {
            classesToRemove.add(classMetadata.getMappedClass(EntityMode.POJO));
        }
    }

    private static Session getSession() {
        // we need a session that bypasses security, so override the security interceptor here
        return hibernateHelper.getSessionFactory().openSession(new EmptyInterceptor() {

            private static final long serialVersionUID = 1L;
        });
    }

    private static void disableForeignKeyChecks(Session s) {
        // this may be database-specific. for now, we know it works in mysql
        s.createSQLQuery("set foreign_key_checks = 0").executeUpdate();
    }
}
