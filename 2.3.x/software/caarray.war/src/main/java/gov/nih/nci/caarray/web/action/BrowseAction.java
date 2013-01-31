//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.web.action;

import gov.nih.nci.caarray.application.browse.BrowseService;
import gov.nih.nci.caarray.domain.project.Project;
import gov.nih.nci.caarray.domain.search.BrowseCategory;
import gov.nih.nci.caarray.domain.search.ProjectSortCriterion;
import gov.nih.nci.caarray.web.ui.BrowseTab;

import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import com.fiveamsolutions.nci.commons.web.displaytag.SortablePaginatedList;
import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionSupport;

/**
 * @author Winston Cheng
 *
 */
public class BrowseAction extends ActionSupport {

    private static final long serialVersionUID = 1L;

    private static final int BROWSE_PAGE_SIZE = 20;

    // browse parameters
    private BrowseCategory category;
    private Long id;

    // browse results
    private SortedSet<BrowseTab> tabs;
    private final SortablePaginatedList<Project, ProjectSortCriterion> results =
            new SortablePaginatedList<Project, ProjectSortCriterion>(BROWSE_PAGE_SIZE, ProjectSortCriterion.PUBLIC_ID
                    .name(), ProjectSortCriterion.class);

    /**
     * @return the category
     */
    public BrowseCategory getCategory() {
        return this.category;
    }

    /**
     * @param category the category to set
     */
    public void setCategory(BrowseCategory category) {
        this.category = category;
    }

    /**
     * @return the id
     */
    public Long getId() {
        return this.id;
    }

    /**
     * @param id the id to set
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * @return the tabs
     */
    public SortedSet<BrowseTab> getTabs() {
        return this.tabs;
    }

    /**
     * @return the results
     */
    public SortablePaginatedList<Project, ProjectSortCriterion> getResults() {
        return this.results;
    }

    /**
     * Retrieves tab headers for the given category.
     * @return success
     */
    @Override
    public String execute() {
        BrowseService bs = CaArrayActionHelper.getBrowseService();
        List<Object[]> resultList = bs.tabList(this.category);
        this.tabs = new TreeSet<BrowseTab>();
        for (Object[] tab : resultList) {
           this.tabs.add(new BrowseTab((String) tab[0], (Number) tab[1], ((Number) tab[2]).intValue()));
        }
        return Action.SUCCESS;
    }

    /**
     * Retrieves the result list for an individual tab.
     * @return tab
     */
    public String list() {
        BrowseService bs = CaArrayActionHelper.getBrowseService();
        this.results.setFullListSize(bs.browseCount(this.category, this.id));
        this.results.setList(bs.browseList(this.results.getPageSortParams(), this.category, this.id));
        return "tab";
    }
}
