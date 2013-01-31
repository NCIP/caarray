//======================================================================================
// Copyright 5AM Solutions Inc, Yale University, Lawrence Berkeley National Laboratory
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/caRPLA/LICENSE for details.
//======================================================================================
package gov.nih.nci.caarray.application.browse;

import gov.nih.nci.caarray.dao.BrowseDao;
import gov.nih.nci.caarray.dao.CaArrayDaoFactory;
import gov.nih.nci.caarray.domain.project.Project;
import gov.nih.nci.caarray.domain.search.BrowseCategory;

import java.util.List;

import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import com.fiveamsolutions.nci.commons.data.search.PageSortParams;

/**
 * @author Winston Cheng
 *
 */
@Local(BrowseService.class)
@Stateless
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class BrowseServiceBean implements BrowseService {
    private CaArrayDaoFactory daoFactory = CaArrayDaoFactory.INSTANCE;

    private BrowseDao getBrowseDao() {
        return this.daoFactory.getBrowseDao();
    }
    CaArrayDaoFactory getDaoFactory() {
        return this.daoFactory;
    }

    void setDaoFactory(CaArrayDaoFactory daoFactory) {
        this.daoFactory = daoFactory;
    }

    /**
     * {@inheritDoc}
     */
    public int countByBrowseCategory(BrowseCategory cat) {
        return getBrowseDao().countByBrowseCategory(cat);
    }

    /**
     * {@inheritDoc}
     */
    public int hybridizationCount() {
        return getBrowseDao().hybridizationCount();
    }
    

    
    //carpla_begin
   public int antibodyCount() {
       return getBrowseDao().antibodyCount();
   }

    //carpla_end
    
    
    
    
    
    
    

    /**
     * {@inheritDoc}
     */
    public int institutionCount() {
        return getBrowseDao().institutionCount();
    }

    /**
     * {@inheritDoc}
     */
    public int userCount() {
        return getBrowseDao().userCount();
    }

    /**
     * {@inheritDoc}
     */
    public List<Object[]> tabList(BrowseCategory cat) {
        return getBrowseDao().tabList(cat);
    }
    /**
     * {@inheritDoc}
     */
    public List<Project> browseList(PageSortParams<Project> params, BrowseCategory cat, Number fieldId) {
        return getBrowseDao().browseList(params, cat, fieldId);
    }

    /**
     * {@inheritDoc}
     */
    public int browseCount(BrowseCategory cat, Number fieldId) {
        return getBrowseDao().browseCount(cat, fieldId);
    }


}
