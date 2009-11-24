/**
 * The software subject to this notice and license includes both human readable
 * source code form and machine readable, binary, object code form. The caarray-war
 * Software was developed in conjunction with the National Cancer Institute
 * (NCI) by NCI employees and 5AM Solutions, Inc. (5AM). To the extent
 * government employees are authors, any rights in such works shall be subject
 * to Title 17 of the United States Code, section 105.
 *
 * This caarray-war Software License (the License) is between NCI and You. You (or
 * Your) shall mean a person or an entity, and all other entities that control,
 * are controlled by, or are under common control with the entity. Control for
 * purposes of this definition means (i) the direct or indirect power to cause
 * the direction or management of such entity, whether by contract or otherwise,
 * or (ii) ownership of fifty percent (50%) or more of the outstanding shares,
 * or (iii) beneficial ownership of such entity.
 *
 * This License is granted provided that You agree to the conditions described
 * below. NCI grants You a non-exclusive, worldwide, perpetual, fully-paid-up,
 * no-charge, irrevocable, transferable and royalty-free right and license in
 * its rights in the caarray-war Software to (i) use, install, access, operate,
 * execute, copy, modify, translate, market, publicly display, publicly perform,
 * and prepare derivative works of the caarray-war Software; (ii) distribute and
 * have distributed to and by third parties the caarray-war Software and any
 * modifications and derivative works thereof; and (iii) sublicense the
 * foregoing rights set out in (i) and (ii) to third parties, including the
 * right to license such rights to further third parties. For sake of clarity,
 * and not by way of limitation, NCI shall have no right of accounting or right
 * of payment from You or Your sub-licensees for the rights granted under this
 * License. This License is granted at no charge to You.
 *
 * Your redistributions of the source code for the Software must retain the
 * above copyright notice, this list of conditions and the disclaimer and
 * limitation of liability of Article 6, below. Your redistributions in object
 * code form must reproduce the above copyright notice, this list of conditions
 * and the disclaimer of Article 6 in the documentation and/or other materials
 * provided with the distribution, if any.
 *
 * Your end-user documentation included with the redistribution, if any, must
 * include the following acknowledgment: This product includes software
 * developed by 5AM and the National Cancer Institute. If You do not include
 * such end-user documentation, You shall include this acknowledgment in the
 * Software itself, wherever such third-party acknowledgments normally appear.
 *
 * You may not use the names "The National Cancer Institute", "NCI", or "5AM"
 * to endorse or promote products derived from this Software. This License does
 * not authorize You to use any trademarks, service marks, trade names, logos or
 * product names of either NCI or 5AM, except as required to comply with the
 * terms of this License.
 *
 * For sake of clarity, and not by way of limitation, You may incorporate this
 * Software into Your proprietary programs and into any third party proprietary
 * programs. However, if You incorporate the Software into third party
 * proprietary programs, You agree that You are solely responsible for obtaining
 * any permission from such third parties required to incorporate the Software
 * into such third party proprietary programs and for informing Your
 * sub-licensees, including without limitation Your end-users, of their
 * obligation to secure any required permissions from such third parties before
 * incorporating the Software into such third party proprietary software
 * programs. In the event that You fail to obtain such permissions, You agree
 * to indemnify NCI for any claims against NCI by such third parties, except to
 * the extent prohibited by law, resulting from Your failure to obtain such
 * permissions.
 *
 * For sake of clarity, and not by way of limitation, You may add Your own
 * copyright statement to Your modifications and to the derivative works, and
 * You may provide additional or different license terms and conditions in Your
 * sublicenses of modifications of the Software, or any derivative works of the
 * Software as a whole, provided Your use, reproduction, and distribution of the
 * Work otherwise complies with the conditions stated in this License.
 *
 * THIS SOFTWARE IS PROVIDED "AS IS," AND ANY EXPRESSED OR IMPLIED WARRANTIES,
 * (INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY,
 * NON-INFRINGEMENT AND FITNESS FOR A PARTICULAR PURPOSE) ARE DISCLAIMED. IN NO
 * EVENT SHALL THE NATIONAL CANCER INSTITUTE, 5AM SOLUTIONS, INC. OR THEIR
 * AFFILIATES BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS;
 * OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR
 * OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package gov.nih.nci.caarray.web.action;

import edu.georgetown.pir.Organism;
import gov.nih.nci.caarray.application.project.ProjectManagementService;
import gov.nih.nci.caarray.business.vocabulary.VocabularyService;
import gov.nih.nci.caarray.domain.project.Project;
import gov.nih.nci.caarray.domain.sample.Sample;
import gov.nih.nci.caarray.domain.sample.Source;
import gov.nih.nci.caarray.domain.search.ProjectSortCriterion;
import gov.nih.nci.caarray.domain.search.SampleSortCriterion;
import gov.nih.nci.caarray.domain.search.SearchCategory;
import gov.nih.nci.caarray.domain.search.SearchSampleCategory;
import gov.nih.nci.caarray.domain.search.SearchSourceCategory;
import gov.nih.nci.caarray.domain.search.SearchTypeSelection;
import gov.nih.nci.caarray.domain.search.SourceSortCriterion;
import gov.nih.nci.caarray.domain.vocabulary.Category;
import gov.nih.nci.caarray.web.util.LabelValue;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.fiveamsolutions.nci.commons.web.displaytag.SortablePaginatedList;
import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.validator.annotations.RegexFieldValidator;
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
    private List<Category> filterCategories = new ArrayList<Category>();
    private List<Organism> filterOrganisms = new ArrayList<Organism>();
    private Category selectedCategory;
    private Organism selectedOrganism;

    // fields for displaying search results
    private String currentTab;
    private final SortablePaginatedList<Project, ProjectSortCriterion> results =
        new SortablePaginatedList<Project, ProjectSortCriterion>(
            SEARCH_PAGE_SIZE, ProjectSortCriterion.PUBLIC_ID.name(), ProjectSortCriterion.class);
    private final SortablePaginatedList<Sample, SampleSortCriterion> sampleResults =
        new SortablePaginatedList<Sample, SampleSortCriterion>(
                SEARCH_PAGE_SIZE, SampleSortCriterion.NAME.name(), SampleSortCriterion.class);
    private final SortablePaginatedList<Source, SourceSortCriterion> sourceResults =
        new SortablePaginatedList<Source, SourceSortCriterion>(
                SEARCH_PAGE_SIZE, SourceSortCriterion.NAME.name(), SourceSortCriterion.class);

    private Map<String, Integer> tabs;



    /**
     * @return the keyword
     */
    @RegexFieldValidator(expression = "[\\w \\-\\.]*", key = "search.error.keyword", message = "")
    public String getKeyword() {
        return keyword;
    }

    /**
     * @return the search type
     */
    @RegexFieldValidator(expression = "[\\w \\-\\.]*", key = "search.error.type", message = "")
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
            this.keyword = URLDecoder.decode(keyword, "ISO-8859-1");
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
    public SortablePaginatedList<Sample, SampleSortCriterion> getSampleResults() {
        return sampleResults;
    }

    /**
     * @return the samples/sources
     */
    public SortablePaginatedList<Source, SourceSortCriterion> getSourceResults() {
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
        if (SearchCategory.ORGANISM.toString().equals(categoryExp) && selectedOrganism != null) {
            keyword = selectedOrganism.getScientificName();
        }

        SearchCategory[] categories = (categoryExp.equals(SEARCH_EXPERIMENT_CATEGORY_ALL)) ? SearchCategory.values()
                : new SearchCategory[] {SearchCategory.valueOf(categoryExp) };
        ProjectManagementService pms = CaArrayActionHelper.getProjectManagementService();
        int projectCount = pms.searchCount(keyword, categories);
        tabs = new LinkedHashMap<String, Integer>();
        tabs.put(EXPERIMENTS_TAB, projectCount);
        setResultExpCount(projectCount);
        return Action.SUCCESS;
    }

    /**
     * This action queries for the result counts of each tab.
     * The tabs call other methods to return the actual data.
     * @return success
     */
    public String basicBiometricSearch() {
        tabs = new LinkedHashMap<String, Integer>();
        ProjectManagementService pms = CaArrayActionHelper.getProjectManagementService();
        if (SearchSampleCategory.SAMPLE_ORGANISM.toString().equals(categorySample)
                && selectedOrganism != null) {
            keyword = selectedOrganism.getScientificName();
        }
        SearchSampleCategory[] categories = (categorySample
                .equals(SEARCH_SAMPLE_CATEGORY_ALL)) ? SearchSampleCategory.values()
                : new SearchSampleCategory[] {SearchSampleCategory.valueOf(categorySample) };

        int sampleCount = pms.searchCount(keyword, categories);
        tabs.put(SAMPLES_TAB, sampleCount);
        setResultSampleCount(sampleCount);

        try {
            SearchSourceCategory[] scategories = (categorySample
                    .equals(SEARCH_SAMPLE_CATEGORY_ALL)) ? SearchSourceCategory.values()
                : new SearchSourceCategory[] {SearchSourceCategory.valueOf(categorySample) };
            int sourceCount = pms.searchCount(keyword, scategories);
            tabs.put(SOURCES_TAB, sourceCount);
            setResultSourceCount(sourceCount);
        } catch (IllegalArgumentException eie) {
            // throw away exception.
            LOG.info(categorySample + "is not a Source category.");
            tabs.put(SOURCES_TAB, 0);
        }

        return Action.SUCCESS;
    }

    /**
     * This action queries for the result counts of each tab.
     * The tabs call other methods to return the actual data.
     * @return success
     */
    public String advBiometricSearch() {

        tabs = new LinkedHashMap<String, Integer>();
        ProjectManagementService pms = CaArrayActionHelper.getProjectManagementService();
        int sampleCount = pms.countSamplesByCharacteristicCategory(selectedCategory, keyword);
        tabs.put(SAMPLES_TAB, sampleCount);
        setResultSampleCount(sampleCount);

        int sourceCount = pms.countSourcesByCharacteristicCategory(selectedCategory, keyword);
        tabs.put(SOURCES_TAB, sourceCount);
        setResultSourceCount(sourceCount);

        return Action.SUCCESS;
    }

    /**
     * Search action for the experiments tab.
     * @return tab
     */
    public String experiments() {
        currentTab = EXPERIMENTS_TAB;
        ProjectManagementService pms = CaArrayActionHelper.getProjectManagementService();
        SearchCategory[] categories = (categoryExp.equals(SEARCH_EXPERIMENT_CATEGORY_ALL)) ? SearchCategory.values()
                : new SearchCategory[] {SearchCategory.valueOf(categoryExp) };
        List<Project> projects = pms.searchByCategory(this.results.getPageSortParams(), keyword, categories);
        results.setFullListSize(this.resultExpCount);
        results.setList(projects);
        return "tab";
    }

    /**
     * Search action for the samples tab.
     * @return tab
     */
    public String samples() {
        currentTab = SAMPLES_TAB;
        if (this.resultSampleCount > 0) {
            ProjectManagementService pms = CaArrayActionHelper.getProjectManagementService();
            List<Sample> bioMats = null;
            if (SEARCH_CATEGORY_OTHER_CHAR.equals(categorySample)) {
                bioMats = pms.searchSamplesByCharacteristicCategory(selectedCategory, keyword);
            } else {
                SearchSampleCategory[] categories = (categorySample
                        .equals(SEARCH_SAMPLE_CATEGORY_ALL)) ? SearchSampleCategory.values()
                        : new SearchSampleCategory[] {SearchSampleCategory.valueOf(categorySample) };
                bioMats = pms.searchByCategory(this.sampleResults.getPageSortParams(), keyword, categories);
            }
            sampleResults.setFullListSize(this.resultSampleCount);
            sampleResults.setList(bioMats);
        }

        return "tab";

    }

    /**
     * Search action for the samples tab.
     * @return tab
     */
    public String sources() {
        currentTab = SOURCES_TAB;
        if (this.resultSourceCount > 0) {
            ProjectManagementService pms = CaArrayActionHelper.getProjectManagementService();
            List<Source> sources = null;
            if (SEARCH_CATEGORY_OTHER_CHAR.equals(categorySample)) {
                sources = pms.searchSourcesByCharacteristicCategory(selectedCategory, keyword);
            } else {
                SearchSourceCategory[] scategories = (categorySample
                        .equals(SEARCH_SAMPLE_CATEGORY_ALL)) ? SearchSourceCategory.values()
                        : new SearchSourceCategory[] {SearchSourceCategory.valueOf(categorySample) };
                sources = pms.searchByCategory(this.sourceResults.getPageSortParams(), keyword, scategories);
            }
            sourceResults.setFullListSize(this.resultSourceCount);
            sourceResults.setList(sources);
        }
        return "tab";
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
     * @return list of label value beans.
     */
    public String searchForCharacteristicCategories() {
        VocabularyService voc = CaArrayActionHelper.getVocabularyService();
        this.setFilterCategories(voc.searchForCharacteristicCategory(this.filterInput));
        return "categoryAutoCompleterValues";
    }

    /**
     * @return list of strings.
     */
    public String searchForOrganismNames() {
        VocabularyService voc = CaArrayActionHelper.getVocabularyService();
        this.setFilterOrganisms(voc.searchForOrganismNames(this.filterKeyword));
        return "organismAutoCompleterValues";
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
     * @return the categories
     */
    public List<Category> getFilterCategories() {
        return filterCategories;
    }

    /**
     * @param categories the categories to set
     */
    public void setFilterCategories(List<Category> categories) {
        this.filterCategories = categories;
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
     * @return the filterOrganisms
     */
    public List<Organism> getFilterOrganisms() {
        return filterOrganisms;
    }

    /**
     * @param filterOrganisms the filterOrganisms to set
     */
    public void setFilterOrganisms(List<Organism> filterOrganisms) {
        this.filterOrganisms = filterOrganisms;
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
}
