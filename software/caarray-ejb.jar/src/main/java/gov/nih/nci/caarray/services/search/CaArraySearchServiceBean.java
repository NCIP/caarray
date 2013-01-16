//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.services.search;

import gov.nih.nci.caarray.dao.DAOException;
import gov.nih.nci.caarray.dao.SearchDao;
import gov.nih.nci.caarray.domain.AbstractCaArrayObject;
import gov.nih.nci.caarray.domain.search.ExampleSearchCriteria;
import gov.nih.nci.caarray.injection.InjectionInterceptor;
import gov.nih.nci.caarray.services.AuthorizationInterceptor;
import gov.nih.nci.caarray.services.EntityConfiguringInterceptor;
import gov.nih.nci.caarray.services.HibernateSessionInterceptor;
import gov.nih.nci.cagrid.cqlquery.CQLQuery;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.security.PermitAll;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.interceptor.Interceptors;

import org.apache.log4j.Logger;
import org.jboss.ejb3.annotation.TransactionTimeout;

import com.google.inject.Inject;

/**
 * Session bean that searches for caArray entities based on various types of criteria.
 * 
 * @author Rashmi Srinivasa
 */
@Stateless
@Remote(CaArraySearchService.class)
@PermitAll
@Interceptors({AuthorizationInterceptor.class, HibernateSessionInterceptor.class, EntityConfiguringInterceptor.class,
        InjectionInterceptor.class })
@TransactionTimeout(CaArraySearchServiceBean.TIMEOUT_SECONDS)
@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
public class CaArraySearchServiceBean implements CaArraySearchService {

    private static final Logger LOG = Logger.getLogger(CaArraySearchServiceBean.class);
    static final int TIMEOUT_SECONDS = 1800;

    private SearchDao searchDao;

    /**
     * 
     * @param searchDao the SearchDao dependency
     */
    @Inject
    public void setSearchDao(SearchDao searchDao) {
        this.searchDao = searchDao;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T extends AbstractCaArrayObject> List<T> search(final T entityExample) {
        return search(entityExample, true, false);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T extends AbstractCaArrayObject> List<T> search(final T entityExample, boolean excludeNulls,
            boolean excludeZeroes) {
        List<T> retrievedList = new ArrayList<T>();
        if (entityExample == null) {
            LOG.error("Search was called with null example entity.");
            return retrievedList;
        }

        try {
            final ExampleSearchCriteria<T> ex = new ExampleSearchCriteria<T>(entityExample);
            ex.setExcludeNulls(excludeNulls);
            ex.setExcludeZeroes(excludeZeroes);
            retrievedList = this.searchDao.queryEntityByExample(ex);
        } catch (final DAOException e) {
            LOG.error("DAO exception while querying by example: ", e);
        } catch (final Exception e) {
            LOG.error("Exception while querying by example: ", e);
        }

        return retrievedList;
    }

    /**
     * Searches for entities based on the given CQL query.
     * 
     * @param cqlQuery the HQL (Hibernate Query Language) string to use as search criteria.
     * 
     * @return the matching entities.
     */
    @Override
    public List<?> search(final CQLQuery cqlQuery) {
        List<?> retrievedList = new ArrayList<Object>();
        if (cqlQuery == null) {
            LOG.error("Search was called with null CQL query.");
            return retrievedList;
        }

        try {
            retrievedList = this.searchDao.query(cqlQuery);
        } catch (final DAOException e) {
            LOG.error("DAO exception while querying by CQL: ", e);
        } catch (final Exception e) {
            LOG.error("Exception while querying by CQL: ", e);
        }

        return retrievedList;
    }
}
