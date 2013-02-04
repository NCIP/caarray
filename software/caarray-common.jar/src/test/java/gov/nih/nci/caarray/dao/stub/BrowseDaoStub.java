//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package gov.nih.nci.caarray.dao.stub;

import gov.nih.nci.caarray.dao.BrowseDao;
import gov.nih.nci.caarray.domain.project.Project;
import gov.nih.nci.caarray.domain.search.BrowseCategory;

import java.util.List;

import com.fiveamsolutions.nci.commons.data.search.PageSortParams;

/**
 * @author Winston Cheng
 *
 */
public class BrowseDaoStub extends AbstractDaoStub implements BrowseDao {
    /**
     * {@inheritDoc}
     */
    public int browseCount(BrowseCategory cat, Number fieldId) {
        return 0;
    }

    /**
     * {@inheritDoc}
     */
    public List<Project> browseList(PageSortParams<Project> params, BrowseCategory cat, Number fieldId) {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public int countByBrowseCategory(BrowseCategory cat) {
        return 0;
    }

    /**
     * {@inheritDoc}
     */
    public int hybridizationCount() {
        return 0;
    }

    /**
     * {@inheritDoc}
     */
    public List<Object[]> tabList(BrowseCategory cat) {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public int userCount() {
        return 0;
    }
}
