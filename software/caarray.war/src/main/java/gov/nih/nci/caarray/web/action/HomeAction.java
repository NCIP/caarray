//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.web.action;

import edu.georgetown.pir.Organism;
import gov.nih.nci.caarray.application.ServiceLocatorFactory;
import gov.nih.nci.caarray.application.browse.BrowseService;
import gov.nih.nci.caarray.application.vocabulary.VocabularyService;
import gov.nih.nci.caarray.domain.sample.TermBasedCharacteristic;
import gov.nih.nci.caarray.domain.search.BrowseCategory;
import gov.nih.nci.caarray.domain.vocabulary.Category;

import java.util.ArrayList;
import java.util.List;

import com.opensymphony.xwork2.Action;

/**
 * Action for displaying the home page.
 * 
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
    private List<Category> categories = new ArrayList<Category>();
    private List<Organism> organisms = new ArrayList<Organism>();
    
    /**
     * @return the browse items
     */
    public List<BrowseItems> getBrowseItems() {
        return browseItems;
    }

    /**
     * @return the categories
     */
    public List<Category> getCategories() {
        return categories;
    }

    /**
     * @return the filterOrganisms
     */
    public List<Organism> getOrganisms() {
        return organisms;
    }

    /**
     * @return input
     */
    public String execute() {
        BrowseService bs = ServiceLocatorFactory.getBrowseService();
        browseItems = new ArrayList<BrowseItems>();
        for (BrowseCategory cat : BrowseCategory.values()) {
            browseItems.add(new BrowseItems(cat, bs.countByBrowseCategory(cat)));
        }
        browseItems.add(new BrowseItems("browse.report.hybridizations", bs.hybridizationCount()));
        browseItems.add(new BrowseItems("browse.report.users", bs.userCount()));
        
        VocabularyService voc = ServiceLocatorFactory.getVocabularyService();
        this.categories = voc.searchForCharacteristicCategory(TermBasedCharacteristic.class, null);
        this.organisms = voc.getOrganisms();

        return Action.INPUT;
    }
}
