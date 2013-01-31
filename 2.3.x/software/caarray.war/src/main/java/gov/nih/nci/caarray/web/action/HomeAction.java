//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.web.action;

import gov.nih.nci.caarray.application.browse.BrowseService;
import gov.nih.nci.caarray.domain.search.BrowseCategory;

import java.util.ArrayList;
import java.util.List;

import com.opensymphony.xwork2.Action;

/**
 * Action for displaying the home page.
 * @author Winston Cheng
 */
public class HomeAction {
    /**
     * This holds the name and count for each item in the browse box.
     */
    public class BrowseItems {
        private final BrowseCategory category;
        private final String resourceKey;
        private final int count;

        /**
         * Constructor for a browse option.
         * @param resourceKey label for this item
         * @param count number of groups in this category
         */
        public BrowseItems(String resourceKey, int count) {
            this.category = null;
            this.resourceKey = resourceKey;
            this.count = count;
        }
        /**
         * Constructor for a browse option.
         * @param category the browse category
         * @param count number of groups in this category
         */
        public BrowseItems(BrowseCategory category, int count) {
            this.category = category;
            this.resourceKey = category.getResourceKey();
            this.count = count;
        }
        /**
         * @return the browse category
         */
        public BrowseCategory getCategory() {
            return category;
        }
        /**
         * @return the resourceKey
         */
        public String getResourceKey() {
            return resourceKey;
        }
        /**
         * @return the count
         */
        public int getCount() {
            return count;
        }
    }

    private List<BrowseItems> browseItems;
    /**
     * @return the browse items
     */
    public List<BrowseItems> getBrowseItems() {
        return browseItems;
    }

    /**
     * @return input
     */
    public String execute() {
        BrowseService bs = CaArrayActionHelper.getBrowseService();
        browseItems = new ArrayList<BrowseItems>();
        for (BrowseCategory cat : BrowseCategory.values()) {
            browseItems.add(new BrowseItems(cat, bs.countByBrowseCategory(cat)));
        }
        browseItems.add(new BrowseItems("browse.report.hybridizations", bs.hybridizationCount()));
        browseItems.add(new BrowseItems("browse.report.users", bs.userCount()));
        return Action.INPUT;
    }
}
