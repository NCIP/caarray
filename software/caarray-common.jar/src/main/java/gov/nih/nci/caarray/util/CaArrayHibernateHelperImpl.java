//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.util;

import gov.nih.nci.caarray.domain.project.Experiment;
import gov.nih.nci.caarray.security.SecurityInterceptor;
import gov.nih.nci.caarray.security.SecurityUtils;
import gov.nih.nci.security.authorization.domainobjects.Group;
import gov.nih.nci.security.authorization.domainobjects.User;
import gov.nih.nci.security.authorization.instancelevel.InstanceLevelSecurityHelper;
import gov.nih.nci.security.exceptions.CSObjectNotFoundException;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.ArrayUtils;
import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.engine.FilterDefinition;
import org.hibernate.engine.SessionFactoryImplementor;
import org.hibernate.engine.SessionImplementor;
import org.hibernate.proxy.HibernateProxy;

import com.fiveamsolutions.nci.commons.audit.AuditLogInterceptor;
import com.fiveamsolutions.nci.commons.util.CompositeInterceptor;
import com.fiveamsolutions.nci.commons.util.CsmEnabledHibernateHelper;

/**
 * Utility class to create and retrieve Hibernate sessions.  Most methods are pass-throughs to {@link HibernateHelper},
 * except for the methods involving filters.
 *
 * @author Rashmi Srinivasa
 */
@SuppressWarnings("unchecked")
public final class CaArrayHibernateHelperImpl extends CsmEnabledHibernateHelper implements CaArrayHibernateHelper {
    private static final Logger LOG = Logger.getLogger(CaArrayHibernateHelperImpl.class);

    private final AuditLogInterceptor auditLogInterceptor;
    private CaArrayAuditLogProcessor auditLogProcessor;

    private boolean filtersEnabled = true;

    /**
     * @return a new CaArrayHibernateHelperImpl
     */
    public static CaArrayHibernateHelper create() {
        CaArrayHibernateHelperImpl hibernateHelper = new CaArrayHibernateHelperImpl();
        hibernateHelper.initialize();
        return hibernateHelper;
    }

    private CaArrayHibernateHelperImpl() {
        this(new CaArrayAuditLogInterceptor());
    }

    private CaArrayHibernateHelperImpl(AuditLogInterceptor auditLogInterceptor) {
        super(new NamingStrategy(), new CompositeInterceptor(new SecurityInterceptor(), auditLogInterceptor));
        this.auditLogInterceptor = auditLogInterceptor;
    }
    
    /**
     * Because of circular references (for instance, SecurityUtils references this object through the
     * static factory), we must use a two stage construction/initialization process.  The factory
     * will call initialize() after construction, allowing SecurityUtils and others to access this
     * object before it is fully initialized.
     * 
     * This needs to be refactored away.
     */
    public void initialize() {
        auditLogProcessor = new CaArrayAuditLogProcessor();
        setSecurity(SecurityUtils.getAuthorizationManager(), false);
        super.initialize();
        auditLogInterceptor.setHibernateHelper(this);
        auditLogInterceptor.setProcessor(auditLogProcessor);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Session getCurrentSession() {
        Session result = super.getCurrentSession();
        if (filtersEnabled && !securityFiltersEnabledInSession(result)) {
            InstanceLevelSecurityHelper.initializeFiltersForGroups(getGroupNames(), result,
                    SecurityUtils.getAuthorizationManager());
        }
        result.enableFilter("BiomaterialFilter");
        return result;
    }

    private String[] getGroupNames() {
        User user = CaArrayUsernameHolder.getCsmUser();
        try {
            Set<Group> groups = SecurityUtils.getAuthorizationManager().getGroups(user.getUserId().toString());
            String[] groupNames = new String[groups.size()];
            int i = 0;
            for (Group g : groups) {
                groupNames[i++] = String.valueOf(g.getGroupId());
            }
            return groupNames;
        } catch (CSObjectNotFoundException e) {
            LOG.error("Could not retrieve group names", e);
            return ArrayUtils.EMPTY_STRING_ARRAY;
        }
    }

    /**
     * {@inheritDoc}
     */
    public void setFiltersEnabled(boolean enable) {
        filtersEnabled = enable;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void openAndBindSession() {
        SecurityInterceptor.clear();
        super.openAndBindSession();
    }

    /**
     * {@inheritDoc}
     */
    public Connection getNewConnection() throws SQLException {
        return ((SessionFactoryImplementor) getSessionFactory()).getConnectionProvider().getConnection();
    }
    
    /**
     * {@inheritDoc}
     */
    public Object doUnfiltered(UnfilteredCallback uc) {
        Session session = getCurrentSession();
        disableFilters(session);
        try {
            return uc.doUnfiltered(session);
        } finally {
            enableFilters(session);
        }
    }
    
    /**
     * {@inheritDoc}
     */
    public void disableFilters() {
        disableFilters(getCurrentSession());
    }

    private void disableFilters(Session session) {
        Set<String> filters = session.getSessionFactory().getDefinedFilterNames();
        for (String filterName : filters) {
            // we only want to disable the security filters. assume security filters are ones
            // with GROUP_NAMES and APPLICATION_ID parameters
            FilterDefinition fd = session.getSessionFactory().getFilterDefinition(filterName);
            if (fd.getParameterNames().contains("GROUP_NAMES") && fd.getParameterNames().contains("APPLICATION_ID")) {
                session.disableFilter(filterName);                
            }
        }
    }

    private void enableFilters(Session session) {
        if (filtersEnabled && !securityFiltersEnabledInSession(session)) {
            InstanceLevelSecurityHelper.initializeFiltersForGroups(getGroupNames(), session,
                    SecurityUtils.getAuthorizationManager());
        }
    }
    
    private boolean securityFiltersEnabledInSession(Session session) {
        return ((SessionImplementor) session).getEnabledFilters().containsKey(Experiment.SECURITY_FILTER_NAME);
    }
    
    /**
     * {@inheritDoc}
     */
    public void setQueryParams(final Map<String, Object> params, Query q) {
        for (String key : params.keySet()) {
            Object value = params.get(key);
            if (value instanceof Collection<?>) {
                q.setParameterList(key, (Collection<?>) value);
            } else {
                q.setParameter(key, value);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public String buildInClauses(List<? extends Serializable> items, String columnName,
            Map<String, List<? extends Serializable>> blocks) {
        StringBuffer inClause = new StringBuffer();
        int startIndex = blocks.size();
        for (int i = 0; i < items.size(); i += CsmEnabledHibernateHelper.MAX_IN_CLAUSE_LENGTH) {
            List<? extends Serializable> block = items.subList(i, Math.min(items.size(), i
                    + CsmEnabledHibernateHelper.MAX_IN_CLAUSE_LENGTH));
            int paramNameIndex = startIndex + (i / CsmEnabledHibernateHelper.MAX_IN_CLAUSE_LENGTH);
            String paramName = "block" + paramNameIndex;
            if (inClause.length() > 0) {
                inClause.append(" or");
            }
            inClause.append(" " + columnName + " in (:" + paramName + ")");
            blocks.put(paramName, block);
        }
        return inClause.toString();
    }

    /**
     * {@inheritDoc}
     */
    public Object unwrapProxy(Object entity) {
        if (entity instanceof HibernateProxy) {
            return ((HibernateProxy) entity).getHibernateLazyInitializer().getImplementation();
        } else {
            return entity;
        }
    }
}
