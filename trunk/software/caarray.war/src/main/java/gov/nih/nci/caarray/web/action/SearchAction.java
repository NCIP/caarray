//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.web.action;

import edu.georgetown.pir.Organism;
import gov.nih.nci.caarray.application.ServiceLocatorFactory;
import gov.nih.nci.caarray.application.project.ProjectManagementService;
import gov.nih.nci.caarray.domain.project.Project;
import gov.nih.nci.caarray.domain.sample.Sample;
import gov.nih.nci.caarray.domain.sample.Source;
import gov.nih.nci.caarray.domain.search.ProjectSortCriterion;
import gov.nih.nci.caarray.domain.search.SampleJoinableSortCriterion;
import gov.nih.nci.caarray.domain.search.SearchCategory;
import gov.nih.nci.caarray.domain.search.SearchSampleCategory;
import gov.nih.nci.caarray.domain.search.SearchSourceCategory;
import gov.nih.nci.caarray.domain.search.SearchTypeSelection;
import gov.nih.nci.caarray.domain.search.SourceJoinableSortCriterion;
import gov.nih.nci.caarray.domain.vocabulary.Category;
import gov.nih.nci.caarray.web.util.LabelValue;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.fiveamsolutions.nci.commons.web.displaytag.SortablePaginatedList;
import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.validator.annotations.Validation;

/**
 * @author Winston Cheng
 *
 */
@Validation
@SuppressWarnings({"PMD.ExcessiveClassLength", "PMD.TooManyFields", "PMD.CyclomaticComplexity" })
public class SearchAction extends ActionSupport {
    private static final long serialVersionUID = -6250359716714235444L;
    private static final int SEARCH_PAGE_SIZE = 20;
    /**
     * name of the experiments tab, should have a method of the same name.
     */
    protected static final String EXPERIMENTS_TAB = "experiments";
    /**
     * name of the samples tab, should have a method of the same name.
     */
    protected static final String SAMPLES_TAB = "samples";

    /**
     * name of the samples tab, should have a method of the same name.
     */
    protected static final String SOURCES_TAB = "sources";

    /**
     * name of the search all category.
     */
    public static final String SEARCH_EXPERIMENT_CATEGORY_ALL = "EXPERIMENT_ALL_CATEGORIES";

    /**
     * name of the search all category.
     */
    public static final String SEARCH_SAMPLE_CATEGORY_ALL = "SAMPLE_ALL_CATEGORIES";

    /**
     * name of search for other characteristics.
     */
    public static final String SEARCH_CATEGORY_OTHER_CHAR = "OTHER_CHARACTERISTICS";

    /**
     * name of search for other characteristics.
     */
    public static final String COMBINATION_SEARCH = "COMBINATION_SEARCH";

    private static final String TAB_FWD = "tab";
    /**
     * regexp for checking keyword in action.
     */
    public static final String KEYWORD_REGEX = "^[\\w\\s\\-\\.]*$";

    /**
     * regexp for checking keyword in javascript.
     */
    public static final String KEYWORD_REGEX_WEB = "^[\\\\w\\\\s\\\\-\\\\.]*$";


    // search parameters
    private String keyword;
    private String categoryExp;
    private String categorySample;
    private int resultExpCount;
    private int resultSourceCount;
    private int resultSampleCount;
    private String location;
    private String searchType;
    private String categoryCombo;
    private String filterInput;
    private String filterKeyword;
    private Category selectedCategory;
    private Organism selectedOrganism;
    private String searchField;



    // fields for displaying search results
    private String currentTab;
    private final SortablePaginatedList<Project, ProjectSortCriterion> results =
        new SortablePaginatedList<Project, ProjectSortCriterion>(
            SEARCH_PAGE_SIZE, ProjectSortCriterion.PUBLIC_ID.name(), ProjectSortCriterion.class);
    private final SortablePaginatedList<Sample, SampleJoinableSortCriterion> sampleResults =
        new SortablePaginatedList<Sample, SampleJoinableSortCriterion>(
                SEARCH_PAGE_SIZE, SampleJoinableSortCriterion.NAME.name(), SampleJoinableSortCriterion.class);
    private final SortablePaginatedList<Source, SourceJoinableSortCriterion> sourceResults =
        new SortablePaginatedList<Source, SourceJoinableSortCriterion>(
                SEARCH_PAGE_SIZE, SourceJoinableSortCriterion.NAME.name(), SourceJoinableSortCriterion.class);

    private Map<String, Integer> tabs;


    /**
     * @return the keyword
     */
    public String getKeyword() {
        return keyword;
    }

    /**
     * @return the search type
     */
    public String getSearchType() {
        return searchType;
    }

    /**
     * @param type the search type to set
     */
    public void setSearchType(String type) {
        try {
            this.searchType = URLDecoder.decode(type, "ISO-8859-1");
        } catch (UnsupportedEncodingException e) {
            this.searchType = type;
        }
    }

    /**
     * @param keyword the keyword to set
     */
    public void setKeyword(String keyword) {
        try {
            if (keyword != null) {
                this.keyword = URLDecoder.decode(keyword, "ISO-8859-1").trim();
            }
        } catch (UnsupportedEncodingException e) {
            this.keyword = keyword;
        }
    }

    /**
     * @return the categoryExp
     */
    public String getCategoryExp() {
        return categoryExp;
    }
    /**
     * @param categoryExp the categoryExp to set
     */
    public void setCategoryExp(String categoryExp) {
        this.categoryExp = categoryExp;
    }

    /**
     * @return the categorySample
     */
    public String getCategorySample() {
        return categorySample;
    }
    /**
     * @param categorySample the categorySample to set
     */
    public void setCategorySample(String categorySample) {
        this.categorySample = categorySample;
    }

    /**
     * @return the location
     */
    public String getLocation() {
        return location;
    }
    /**
     * @param location the location to set
     */
    public void setLocation(String location) {
        this.location = location;
    }
    /**
     * @return the current tab
     */
    public String getCurrentTab() {
        return currentTab;
    }
    /**
     * Returns a map of tabs to #results in that tab.
     * @return the tabs
     */
    public Map<String, Integer> getTabs() {
        return tabs;
    }
    /**
     * @return the experiments
     */
    public SortablePaginatedList<Project, ProjectSortCriterion> getResults() {
        return results;
    }

    /**
     * @return the samples/sources
     */
    public SortablePaginatedList<Sample, SampleJoinableSortCriterion> getSampleResults() {
        return sampleResults;
    }

    /**
     * @return the samples/sources
     */
    public SortablePaginatedList<Source, SourceJoinableSortCriterion> getSourceResults() {
        return sourceResults;
    }

    /**
     * @return the resultSampleCount
     */
    public int getResultSampleCount() {
        return resultSourceCount;
    }

    /**
     * @return the resultExpCount
     */
    public int getResultExpCount() {
        return resultExpCount;
    }

    /**
     * @return the resultSourceCount
     */
    public int getResultSourceCount() {
        return resultSourceCount;
    }

    /**
     * @param resultSourceCount the resultSourceCount to set
     */
    public void setResultSourceCount(int resultSourceCount) {
        this.resultSourceCount = resultSourceCount;
    }

    /**
     * @param resultSampleCount the resultSampleCount to set
     */
    public void setResultSampleCount(int resultSampleCount) {
        this.resultSampleCount = resultSampleCount;
    }

    /**
     * @param resultExpCount the resultExpCount to set
     */
    public void setResultExpCount(int resultExpCount) {
        this.resultExpCount = resultExpCount;
    }

    /**
     * This action queries for the result counts of each tab.
     * The tabs call other methods to return the actual data.
     * @return success
     */
    public String basicSearch() {

        if (this.keyword != null && !checkKeyword()) {
            return Action.INPUT;
        }

        if (COMBINATION_SEARCH.equals(searchType)) {
           return comboSearch();
        } else if (SearchTypeSelection.SEARCH_BY_EXPERIMENT.name().equals(searchType)) {
            return basicExpSearch();
        } else if (SearchTypeSelection.SEARCH_BY_SAMPLE.name().equals(searchType)) {
            return SEARCH_CATEGORY_OTHER_CHAR.equals(categorySample)
                ? advBiometricSearch() : basicBiometricSearch();
        } else {
            return Action.INPUT;
        }
    }

    private boolean checkKeyword() {
        Pattern p = Pattern.compile(KEYWORD_REGEX);
        Matcher m = p.matcher(this.keyword);
        if (!m.matches()) {
            addActionError(getText("search.error.keyword"));
            return false;
        } else {
            return true;
        }
    }

    private String comboSearch() {
        // must figure out search type based on category.
        try {
            if (SEARCH_EXPERIMENT_CATEGORY_ALL.equals(categoryCombo)
                    || SearchCategory.valueOf(categoryCombo) != null) {
                categoryExp = categoryCombo;
                searchType = SearchTypeSelection.SEARCH_BY_EXPERIMENT.name();
                return basicExpSearch();
            }
        } catch (IllegalArgumentException eie) {
            // throw away exception.
            LOG.info(categoryCombo + "is not a Experiment category.");
        }

        try {
            if (SEARCH_SAMPLE_CATEGORY_ALL.equals(categoryCombo)
                    || SearchSampleCategory.valueOf(categoryCombo) != null) {
                categorySample = categoryCombo;
                searchType = SearchTypeSelection.SEARCH_BY_SAMPLE.name();
                return basicBiometricSearch();
            }
        } catch (IllegalArgumentException eie) {
            // throw away exception.
            LOG.info(categoryCombo + "is not a Sample category.");
        }

        return Action.INPUT;
    }

    /**
     * This action queries for the result counts of each tab.
     * The tabs call other methods to return the actual data.
     * @return success
     */
    public String basicExpSearch() {

        if (!checkExpFields()) {
            return Action.INPUT;
        }

        checkExpOrganism();

        SearchCategory[] categories = (categoryExp.equals(SEARCH_EXPERIMENT_CATEGORY_ALL)) ? SearchCategory.values()
                : new SearchCategory[] {SearchCategory.valueOf(categoryExp) };
        ProjectManagementService pms = ServiceLocatorFactory.getProjectManagementService();
        int projectCount = pms.searchCount(keyword, categories);
        tabs = new LinkedHashMap<String, Integer>();
        tabs.put(EXPERIMENTS_TAB, projectCount);
        setResultExpCount(projectCount);
        return Action.SUCCESS;
    }

    private void checkExpOrganism() {
        if (!categoryExp.equals(categoryCombo)
                && SearchCategory.ORGANISM.toString().equals(categoryExp)) {
            keyword = selectedOrganism.getScientificName();
        }
    }

    private boolean checkExpFields() {
        if (!categoryExp.equals(categoryCombo)
                && SearchCategory.ORGANISM.toString().equals(categoryExp)
                && selectedOrganism == null) {
            addActionError(getText("search.error.organism"));
            return false;
        } else {
            return true;
        }
    }

    /**
     * This action queries for the result counts of each tab.
     * The tabs call other methods to return the actual data.
     * @return success
     */
    public String basicBiometricSearch() {
        if (!checkSampleFields()) {
            return Action.INPUT;
        }
        tabs = new LinkedHashMap<String, Integer>();
        ProjectManagementService pms = ServiceLocatorFactory.getProjectManagementService();

        checkSampleOrganism();

        SearchSampleCategory[] categories = (categorySample
                .equals(SEARCH_SAMPLE_CATEGORY_ALL)) ? SearchSampleCategory.values()
                : new SearchSampleCategory[] {SearchSampleCategory.valueOf(categorySample) };

        int sampleCount = pms.searchCount(keyword, Sample.class, categories);
        tabs.put(SAMPLES_TAB, sampleCount);
        setResultSampleCount(sampleCount);

        try {
            SearchSourceCategory[] scategories = (categorySample
                    .equals(SEARCH_SAMPLE_CATEGORY_ALL)) ? SearchSourceCategory.values()
                : new SearchSourceCategory[] {SearchSourceCategory.valueOf(categorySample) };
            int sourceCount = pms.searchCount(keyword, Source.class, scategories);
            tabs.put(SOURCES_TAB, sourceCount);
            setResultSourceCount(sourceCount);
        } catch (IllegalArgumentException eie) {
            // throw away exception.
            LOG.info(categorySample + "is not a Source category.");
            tabs.put(SOURCES_TAB, 0);
        }

        return Action.SUCCESS;
    }

    private void checkSampleOrganism() {
        if (!categorySample.equals(categoryCombo)
                && SearchSampleCategory.SAMPLE_ORGANISM.toString().equals(categorySample)
                && selectedOrganism != null) {
            keyword = selectedOrganism.getScientificName();
        }
    }

    private boolean checkSampleFields() {
        if (!categorySample.equals(categoryCombo)
                && SearchSampleCategory.SAMPLE_ORGANISM.toString().equals(categorySample)
                && selectedOrganism == null) {
            addActionError(getText("search.error.organism"));
        } else if (SearchSampleCategory.SAMPLE_EXTERNAL_ID.toString().equals(categorySample)
                && (keyword == null || keyword.length() < 1)) {
            addActionError(getText("search.error.extSampleIdTooShort"));
        } else if (!SearchSampleCategory.SAMPLE_ORGANISM.toString().equals(categorySample)
                && !SearchSampleCategory.SAMPLE_EXTERNAL_ID.toString().equals(categorySample)
                && (keyword == null || keyword.length() < 3)) {
            addActionError(getText("search.error.keywordTooShort"));
        } else {
            return true;
        }

        return false;
    }

    /**
     * This action queries for the result counts of each tab.
     * The tabs call other methods to return the actual data.
     * @return success
     */
    public String advBiometricSearch() {
        if (!checkAdvSampleFields()) {
            return Action.INPUT;
        }
        tabs = new LinkedHashMap<String, Integer>();
        ProjectManagementService pms = ServiceLocatorFactory.getProjectManagementService();
        if (selectedCategory != null) {
            int sampleCount = pms.countSamplesByCharacteristicCategory(selectedCategory, keyword);
            tabs.put(SAMPLES_TAB, sampleCount);
            setResultSampleCount(sampleCount);

            int sourceCount = pms.countSourcesByCharacteristicCategory(selectedCategory, keyword);
            tabs.put(SOURCES_TAB, sourceCount);
            setResultSourceCount(sourceCount);

            return Action.SUCCESS;
        } else {
            addFieldError("selectedCategory",
                    getText("search.category.other.characteristics.required"));

            return Action.INPUT;
        }
    }

    private boolean checkAdvSampleFields() {
        if (SEARCH_CATEGORY_OTHER_CHAR.equals(categorySample) && selectedCategory == null) {
            addActionError(getText("search.error.otherchars"));
            return false;
        } else {
            return true;
        }


    }

    /**
     * Search action for the experiments tab.
     * @return tab
     */
    public String experiments() {
        currentTab = EXPERIMENTS_TAB;
        ProjectManagementService pms = ServiceLocatorFactory.getProjectManagementService();
        SearchCategory[] categories = (categoryExp.equals(SEARCH_EXPERIMENT_CATEGORY_ALL)) ? SearchCategory.values()
                : new SearchCategory[] {SearchCategory.valueOf(categoryExp) };
        List<Project> projects = pms.searchByCategory(this.results.getPageSortParams(), keyword, categories);
        results.setFullListSize(this.resultExpCount);
        results.setList(projects);
        return TAB_FWD;
    }

    /**
     * Search action for the samples tab.
     * @return tab
     */
    public String samples() {
        currentTab = SAMPLES_TAB;
        if (this.resultSampleCount > 0) {
            ProjectManagementService pms = ServiceLocatorFactory.getProjectManagementService();
            List<Sample> bioMats = null;
            if (SEARCH_CATEGORY_OTHER_CHAR.equals(categorySample)) {
                bioMats = pms.searchSamplesByCharacteristicCategory(
                        this.sampleResults.getPageSortParams(), selectedCategory, keyword);
            } else {
                SearchSampleCategory[] categories = (categorySample
                        .equals(SEARCH_SAMPLE_CATEGORY_ALL)) ? SearchSampleCategory.values()
                        : new SearchSampleCategory[] {SearchSampleCategory.valueOf(categorySample) };
                bioMats = pms.searchByCategory(this.sampleResults.getPageSortParams(), keyword, Sample.class,
                        categories);
            }
            sampleResults.setFullListSize(this.resultSampleCount);
            sampleResults.setList(bioMats);
        }

        return TAB_FWD;

    }

    /**
     * Search action for the samples tab.
     * @return tab
     */
    public String sources() {
        currentTab = SOURCES_TAB;
        if (this.resultSourceCount > 0) {
            ProjectManagementService pms = ServiceLocatorFactory.getProjectManagementService();
            List<Source> sources = null;
            if (SEARCH_CATEGORY_OTHER_CHAR.equals(categorySample)) {
                sources = pms.searchSourcesByCharacteristicCategory(
                        this.sourceResults.getPageSortParams(), selectedCategory, keyword);
            } else {
                SearchSourceCategory[] scategories = (categorySample
                        .equals(SEARCH_SAMPLE_CATEGORY_ALL)) ? SearchSourceCategory.values()
                        : new SearchSourceCategory[] {SearchSourceCategory.valueOf(categorySample) };
                sources = pms.searchByCategory(this.sourceResults.getPageSortParams(), keyword, Source.class,
                        scategories);
            }
            sourceResults.setFullListSize(this.resultSourceCount);
            sourceResults.setList(sources);
        }
        return TAB_FWD;
    }



    /**
     * @return list of label value beans.
     */
    public static List<LabelValue> getSearchCategories() {
        List<LabelValue> searchCats = new ArrayList<LabelValue>();
        for (SearchCategory cat : SearchCategory.values()) {
            searchCats.add(new LabelValue(cat.getResourceKey(), cat.name()));
        }

        searchCats.add(new LabelValue("search.category.all", SEARCH_EXPERIMENT_CATEGORY_ALL));
        return searchCats;
    }

    /**
     * @return list of label value beans.
     */
    public static List<LabelValue> getSearchTypeSelection() {
        List<LabelValue> searchCats = new ArrayList<LabelValue>();

        for (SearchTypeSelection cat : SearchTypeSelection.values()) {
            searchCats.add(new LabelValue(cat.getResourceKey(), cat.name()));
        }

        return searchCats;
    }

    /**
     * @return list of label value beans.
     */
    public static List<LabelValue> getSearchBiometricCategories() {
        List<LabelValue> searchCats = new ArrayList<LabelValue>();
        for (SearchSampleCategory cat : SearchSampleCategory.values()) {
            searchCats.add(new LabelValue(cat.getResourceKey(), cat.name()));
        }
        searchCats.add(new LabelValue("search.category.other.characteristics", SEARCH_CATEGORY_OTHER_CHAR));
        searchCats.add(new LabelValue("search.category.all", SEARCH_SAMPLE_CATEGORY_ALL));
        return searchCats;
    }

    /**
     * @return list of label value beans.
     */
    public static List<LabelValue> getSearchSimpleBiometricCategories() {
        List<LabelValue> searchCats = new ArrayList<LabelValue>();
        for (SearchSampleCategory cat : SearchSampleCategory.values()) {
            searchCats.add(new LabelValue(cat.getResourceKey(), cat.name()));
        }
        searchCats.add(new LabelValue("search.category.all", SEARCH_SAMPLE_CATEGORY_ALL));
        return searchCats;
    }

    /**
     * @return the categoryCombo
     */
    public String getCategoryCombo() {
        return categoryCombo;
    }

    /**
     * @param categoryCombo the categoryCombo to set
     */
    public void setCategoryCombo(String categoryCombo) {
        this.categoryCombo = categoryCombo;
    }

    /**
     * @return the filterInput
     */
    public String getFilterInput() {
        return filterInput;
    }

    /**
     * @param filterInput the filterInput to set
     */
    public void setFilterInput(String filterInput) {
        this.filterInput = filterInput;
    }

    /**
     * @return the selectedCategory
     */
    public Category getSelectedCategory() {
        return selectedCategory;
    }

    /**
     * @param selectedCategory the selectedCategory to set
     */
    public void setSelectedCategory(Category selectedCategory) {
        this.selectedCategory = selectedCategory;
    }

    /**
     * @return the selectedOrganism
     */
    public Organism getSelectedOrganism() {
        return selectedOrganism;
    }

    /**
     * @param selectedOrganism the selectedOrganism to set
     */
    public void setSelectedOrganism(Organism selectedOrganism) {
        this.selectedOrganism = selectedOrganism;
    }

    /**
     * @return the filterKeyword
     */
    public String getFilterKeyword() {
        return filterKeyword;
    }

    /**
     * @param filterKeyword the filterKeyword to set
     */
    public void setFilterKeyword(String filterKeyword) {
        this.filterKeyword = filterKeyword;
    }

    /**
     * @return the searchField
     */
    public String getSearchField() {
        return searchField;
    }

    /**
     * @param searchField the searchField to set
     */
    public void setSearchField(String searchField) {
        this.searchField = searchField;
    }
}
