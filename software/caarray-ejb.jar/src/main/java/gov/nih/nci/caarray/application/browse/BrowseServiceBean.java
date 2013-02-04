//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package gov.nih.nci.caarray.application.browse;

import gov.nih.nci.caarray.dao.BrowseDao;
import gov.nih.nci.caarray.domain.project.Project;
import gov.nih.nci.caarray.domain.search.BrowseCategory;
import gov.nih.nci.caarray.injection.InjectionInterceptor;

import java.util.List;

import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.interceptor.Interceptors;

import com.fiveamsolutions.nci.commons.data.search.PageSortParams;
import com.google.inject.Inject;

/**
 * @author Winston Cheng
 * 
 */
@Local(BrowseService.class)
@Stateless
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
@Interceptors(InjectionInterceptor.class)
public class BrowseServiceBean implements BrowseService {
    private BrowseDao browseDao;

    /**
     * 
     * @param browseDao the browseDao dependency
     */
    @Inject
    public void setBrowseDao(BrowseDao browseDao) {
        this.browseDao = browseDao;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int countByBrowseCategory(BrowseCategory cat) {
        return this.browseDao.countByBrowseCategory(cat);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hybridizationCount() {
        return this.browseDao.hybridizationCount();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int userCount() {
        return this.browseDao.userCount();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Object[]> tabList(BrowseCategory cat) {
        return this.browseDao.tabList(cat);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Project> browseList(PageSortParams<Project> params, BrowseCategory cat, Number fieldId) {
        return this.browseDao.browseList(params, cat, fieldId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int browseCount(BrowseCategory cat, Number fieldId) {
        return this.browseDao.browseCount(cat, fieldId);
    }
}
