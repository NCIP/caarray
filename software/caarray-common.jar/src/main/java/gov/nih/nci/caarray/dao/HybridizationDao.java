//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.dao;

import gov.nih.nci.caarray.domain.hybridization.Hybridization;
import gov.nih.nci.caarray.domain.search.HybridizationSearchCriteria;

import java.util.List;

import com.fiveamsolutions.nci.commons.data.search.PageSortParams;

/**
 * DAO for entities in the <code>gov.nih.nci.caarray.domain.hybridization</code> package.
 * 
 * @author dkokotov
 * 
 */
public interface HybridizationDao extends CaArrayDao {
    /**
     * Performs a query for hybridizations based on given criteria.
     * 
     * @param params paging and sorting parameters
     * @param criteria the criteria for the search
     * @return a list of matching hybridizations
     */
    List<Hybridization> searchByCriteria(PageSortParams<Hybridization> params, HybridizationSearchCriteria criteria);
}
