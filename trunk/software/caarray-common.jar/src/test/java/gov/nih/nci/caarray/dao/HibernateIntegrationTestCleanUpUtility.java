//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.dao;

import gov.nih.nci.caarray.domain.vocabulary.Category;
import gov.nih.nci.caarray.domain.vocabulary.Term;
import gov.nih.nci.caarray.domain.vocabulary.TermSource;
import gov.nih.nci.caarray.security.SecurityUtils;
import gov.nih.nci.caarray.util.HibernateUtil;
import gov.nih.nci.security.authorization.domainobjects.Group;
import gov.nih.nci.security.authorization.domainobjects.ProtectionElement;
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

    private static final Logger LOG = Logger.getLogger(HibernateIntegrationTestCleanUpUtility.class);
    private static List<Class<?>> classesToRemove;
    private static final String SELF_GROUP_PATTERN = "'" + SecurityUtils.SELF_GROUP_PREFIX + "%'";
    private static final Map<Class<?>, String> CLASS_DELETE_CONSTRAINTS = new HashMap<Class<?>, String>();
    static {
        CLASS_DELETE_CONSTRAINTS.put(TermSource.class, "id > 0");
        CLASS_DELETE_CONSTRAINTS.put(Category.class, "id > 0");
        CLASS_DELETE_CONSTRAINTS.put(Term.class, "id > 0");
        CLASS_DELETE_CONSTRAINTS.put(User.class, "id > 9");
        CLASS_DELETE_CONSTRAINTS.put(Group.class, "id > 8 and not (groupName like " + SELF_GROUP_PATTERN + ")");
        CLASS_DELETE_CONSTRAINTS.put(ProtectionElement.class, "id > 2");
    }

    private static final Class<?>[] CSM_CLASSES_NOT_TO_REMOVE = {
            gov.nih.nci.security.authorization.domainobjects.Application.class,
            gov.nih.nci.security.authorization.domainobjects.Privilege.class,
            gov.nih.nci.security.authorization.domainobjects.Role.class,
            gov.nih.nci.security.authorization.domainobjects.FilterClause.class };

    private HibernateIntegrationTestCleanUpUtility() {
        super();
    }

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
            HibernateUtil.rollbackTransaction(tx);
            LOG.warn("Error cleaning up test objects.", deleteException);
        } catch (HibernateException he) {
            HibernateUtil.rollbackTransaction(tx);
            LOG.warn("Error cleaning up test objects.", he);
        } finally {
            s.close();
        }
        return removed;
    }

    @SuppressWarnings("unchecked")
    private static void retrieveClassMetadata() {
        Map<String, ClassMetadata> classMetadataMap = HibernateUtil.getSessionFactory().getAllClassMetadata();
        classesToRemove = new LinkedList<Class<?>>();
        for (ClassMetadata classMetadata : classMetadataMap.values()) {
            Class<?> persistentClass = classMetadata.getMappedClass(EntityMode.POJO);
            if (!ArrayUtils.contains(CSM_CLASSES_NOT_TO_REMOVE, persistentClass)) {
                classesToRemove.add(persistentClass);
            }
        }
    }

    private static Session getSession() {
        // we need a session that bypasses security, so override the security interceptor here
        return HibernateUtil.getSessionFactory().openSession(new EmptyInterceptor() {

            private static final long serialVersionUID = 1L;
        });
    }

    private static void disableForeignKeyChecks(Session s) {
        // this may be database-specific. for now, we know it works in mysql
        s.createSQLQuery("set foreign_key_checks = 0").executeUpdate();
    }
}
