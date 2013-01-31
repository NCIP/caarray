//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.web.action;

import static org.junit.Assert.assertEquals;
import gov.nih.nci.caarray.AbstractCaarrayTest;
import gov.nih.nci.caarray.application.project.ProjectManagementService;
import gov.nih.nci.caarray.application.project.ProjectManagementServiceStub;
import gov.nih.nci.caarray.business.vocabulary.VocabularyService;
import gov.nih.nci.caarray.business.vocabulary.VocabularyServiceStub;
import gov.nih.nci.caarray.domain.project.Project;
import gov.nih.nci.caarray.domain.sample.Sample;
import gov.nih.nci.caarray.domain.sample.Source;
import gov.nih.nci.caarray.domain.search.BiomaterialSearchCategory;
import gov.nih.nci.caarray.domain.search.ProjectSortCriterion;
import gov.nih.nci.caarray.domain.search.SampleSortCriterion;
import gov.nih.nci.caarray.domain.search.SearchCategory;
import gov.nih.nci.caarray.domain.search.SearchSampleCategory;
import gov.nih.nci.caarray.domain.search.SearchSourceCategory;
import gov.nih.nci.caarray.domain.search.SearchTypeSelection;
import gov.nih.nci.caarray.domain.search.SourceSortCriterion;
import gov.nih.nci.caarray.util.j2ee.ServiceLocatorStub;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.fiveamsolutions.nci.commons.data.search.PageSortParams;
import com.fiveamsolutions.nci.commons.web.displaytag.SortablePaginatedList;
import com.opensymphony.xwork2.Action;

/**
 * @author Winston Cheng
 *
 */
@SuppressWarnings("PMD")
public class SearchActionTest extends AbstractCaarrayTest {
    private final SearchAction searchAction = new SearchAction();
    private final LocalProjectManagementServiceStub projectServiceStub = new LocalProjectManagementServiceStub();
    private final VocabularyService vocabServiceStub = new VocabularyServiceStub();
    private static final int NUM_PROJECTS = 4;
    private static final int NUM_PROJECTS_BY_ORGANISM = 2;
    private static final int NUM_SAMPLES_BY_ORGANISM = 4;

    @Before
    public void setUp() throws Exception {
        ServiceLocatorStub locatorStub = ServiceLocatorStub.registerEmptyLocator();
        locatorStub.addLookup(ProjectManagementService.JNDI_NAME, this.projectServiceStub);
        locatorStub.addLookup(VocabularyService.JNDI_NAME, this.vocabServiceStub);
    }

    @Test
    public void testBasicSearch() throws Exception {
        this.searchAction.setSearchType(SearchAction.COMBINATION_SEARCH);
        this.searchAction.setCategoryCombo(SearchAction.SEARCH_EXPERIMENT_CATEGORY_ALL);
        String result = this.searchAction.basicSearch();
        assertEquals(NUM_PROJECTS, searchAction.getTabs().get(SearchAction.EXPERIMENTS_TAB));
        assertEquals(Action.SUCCESS, result);

        this.searchAction.setSearchType(SearchAction.COMBINATION_SEARCH);
        searchAction.setCategoryCombo(SearchCategory.ORGANISM.name());
        result = this.searchAction.basicSearch();
        assertEquals(NUM_PROJECTS_BY_ORGANISM, searchAction.getTabs().get(SearchAction.EXPERIMENTS_TAB));
        assertEquals(Action.SUCCESS, result);

        this.searchAction.setSearchType(SearchAction.COMBINATION_SEARCH);
        searchAction.setCategoryCombo(SearchSampleCategory.SAMPLE_ORGANISM.name());
        result = this.searchAction.basicSearch();
        assertEquals(NUM_SAMPLES_BY_ORGANISM, searchAction.getTabs().get(SearchAction.SAMPLES_TAB));
        assertEquals(Action.SUCCESS, result);

    }

    @Test
    public void testExperiments() throws Exception {
        this.searchAction.setSearchType(SearchTypeSelection.SEARCH_BY_EXPERIMENT.name());
        this.searchAction.setCategoryExp(SearchAction.SEARCH_EXPERIMENT_CATEGORY_ALL);
        searchAction.setKeyword("keyword");
        this.searchAction.setResultExpCount(NUM_PROJECTS);
        String result = this.searchAction.experiments();
        SortablePaginatedList<Project, ProjectSortCriterion> results = searchAction.getResults();
        assertEquals(SearchAction.EXPERIMENTS_TAB, searchAction.getCurrentTab());
        assertEquals(NUM_PROJECTS, results.getFullListSize());
        assertEquals(NUM_PROJECTS, results.getList().size());
        assertEquals("tab", result);

        searchAction.setCategoryExp(SearchCategory.ORGANISM.name());
        this.searchAction.setResultExpCount(NUM_PROJECTS_BY_ORGANISM);
        result = this.searchAction.experiments();
        results = searchAction.getResults();
        assertEquals(SearchAction.EXPERIMENTS_TAB, searchAction.getCurrentTab());
        assertEquals(NUM_PROJECTS_BY_ORGANISM, results.getFullListSize());
        assertEquals(NUM_PROJECTS_BY_ORGANISM, results.getList().size());
        assertEquals("tab", result);
    }

    @Test
    public void testSamples() throws Exception {
        this.searchAction.setSearchType(SearchTypeSelection.SEARCH_BY_SAMPLE.name());
        this.searchAction.setCategorySample(SearchAction.SEARCH_SAMPLE_CATEGORY_ALL);
        searchAction.setKeyword("keyword");
        this.searchAction.setResultSampleCount(NUM_PROJECTS);
        String result = this.searchAction.samples();
        SortablePaginatedList<Sample, SampleSortCriterion> results = searchAction.getSampleResults();
        assertEquals(SearchAction.SAMPLES_TAB, searchAction.getCurrentTab());
        assertEquals(NUM_PROJECTS, results.getFullListSize());
        assertEquals(NUM_PROJECTS, results.getList().size());
        assertEquals("tab", result);

        searchAction.setCategorySample(SearchSampleCategory.SAMPLE_ORGANISM.name());
        this.searchAction.setResultSampleCount(NUM_SAMPLES_BY_ORGANISM);
        result = this.searchAction.samples();
        results = searchAction.getSampleResults();
        assertEquals(SearchAction.SAMPLES_TAB, searchAction.getCurrentTab());
        assertEquals(NUM_SAMPLES_BY_ORGANISM, results.getFullListSize());
        assertEquals(NUM_SAMPLES_BY_ORGANISM, results.getList().size());
        assertEquals("tab", result);
    }

    @Test
    public void testSources() throws Exception {
        this.searchAction.setSearchType(SearchTypeSelection.SEARCH_BY_SAMPLE.name());
        this.searchAction.setCategorySample(SearchAction.SEARCH_SAMPLE_CATEGORY_ALL);
        searchAction.setKeyword("keyword");
        this.searchAction.setResultSourceCount(NUM_PROJECTS);
        String result = this.searchAction.sources();
        SortablePaginatedList<Source, SourceSortCriterion> results = searchAction.getSourceResults();
        assertEquals(SearchAction.SOURCES_TAB, searchAction.getCurrentTab());
        assertEquals(NUM_PROJECTS, results.getFullListSize());
        assertEquals(NUM_PROJECTS, results.getList().size());
        assertEquals("tab", result);

        searchAction.setCategorySample(SearchSampleCategory.SAMPLE_ORGANISM.name());
        this.searchAction.setResultSourceCount(NUM_SAMPLES_BY_ORGANISM);
        result = this.searchAction.sources();
        results = searchAction.getSourceResults();
        assertEquals(SearchAction.SOURCES_TAB, searchAction.getCurrentTab());
        assertEquals(NUM_SAMPLES_BY_ORGANISM, results.getFullListSize());
        assertEquals(NUM_SAMPLES_BY_ORGANISM, results.getList().size());
        assertEquals("tab", result);
    }


    @Test
    public void testGetSearchCategories() {
        assertEquals(SearchCategory.values().length + 1, this.searchAction.getSearchCategories().size());
    }

    @Test
    public void testGetSearchSampleCategories() {
        assertEquals(SearchSampleCategory.values().length + 1, this.searchAction.getSearchSimpleBiometricCategories().size());
    }

    @Test
    public void testGetSearchAdvSampleCategories() {
        assertEquals(SearchSampleCategory.values().length + 2, this.searchAction.getSearchBiometricCategories().size());
    }

    @Test
    public void testGetSearchTypeSelection() {
        assertEquals(SearchTypeSelection.values().length, this.searchAction.getSearchTypeSelection().size());
    }

    @Test
    public void testUseOfBiomaterialSearchCategory() {
        BiomaterialSearchCategory[] bc = SearchSampleCategory.values();
        BiomaterialSearchCategory[] bc2 = SearchSourceCategory.values();
    }

    @Test
    public void testFields() {
        this.searchAction.getKeyword();
        this.searchAction.setKeyword("////~~~~~");
        this.searchAction.getSearchType();
        this.searchAction.setSearchType("////~~~~~");
        this.searchAction.getCategoryExp();
        this.searchAction.getCategorySample();
        this.searchAction.getLocation();
        this.searchAction.setLocation("nowhere");
        this.searchAction.getResultSampleCount();
        this.searchAction.getResultExpCount();
        this.searchAction.getResultSourceCount();

    }

    private static class LocalProjectManagementServiceStub extends ProjectManagementServiceStub {
        @Override
        public List<Project> searchByCategory(PageSortParams<Project> params, String keyword, SearchCategory... categories) {
            List<Project> projects= new ArrayList<Project>();
            int count = (categories[0] == SearchCategory.ORGANISM) ? NUM_PROJECTS_BY_ORGANISM : NUM_PROJECTS;
            for (int i = 0; i < count; i++) {
                projects.add(new Project());
            }
            return projects;
        }

        @Override
        public int searchCount(String keyword, SearchCategory... categories) {
            if (categories[0] == SearchCategory.ORGANISM) {
                return NUM_PROJECTS_BY_ORGANISM;
            } else {
                return NUM_PROJECTS;
            }
        }
    }

}
