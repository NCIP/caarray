//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package gov.nih.nci.caarray.application.browse;

import gov.nih.nci.caarray.domain.project.Project;
import gov.nih.nci.caarray.domain.search.BrowseCategory;

import java.util.List;

import com.fiveamsolutions.nci.commons.data.search.PageSortParams;

/**
 * @author Winston Cheng
 *
 */
public interface BrowseService {
    /**
     * The default JNDI name to use to lookup <code>BrowseService</code>.
     */
    String JNDI_NAME = "caarray/BrowseServiceBean/local";

    /**
     * @param cat browse category
     * @return number of items in the given category
     */
    int countByBrowseCategory(BrowseCategory cat);

    /**
     * @return the total number of hybridizations
     */
    int hybridizationCount();

    /**
     * @return the total number of registered users
     */
    int userCount();

    /**
     * Finds the breakdown of groups within a given category and returns a list of [name, id, projectCount] objects,
     * which can be used to populate browse tabs.
     * 
     * @param cat category
     * @return tabs
     */
    List<Object[]> tabList(BrowseCategory cat);

    /**
     * Returns a list of projects constrained by a browse category and id. Note that this method currently only supports
     * 
     * SortCriterions that are either simple properties of the target class or required single-valued associations from
     * it. If a non-required association is used in the sort criterion, then any instances for which that association is
     * null will not be included in the results (as an inner join is used)
     * 
     * @param params paging and sorting parameters
     * @param cat browse category
     * @param fieldId id for the field specified by the category
     * @return a list of matching projects
     */
    List<Project> browseList(PageSortParams<Project> params, BrowseCategory cat, Number fieldId);

    /**
     * Returns the count of projects constrained by a browse category and id.
     * 
     * @param cat browse category
     * @param fieldId id for the field specified by the category
     * @return number of results
     */
    int browseCount(BrowseCategory cat, Number fieldId);
}
