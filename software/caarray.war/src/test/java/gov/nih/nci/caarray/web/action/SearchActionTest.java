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

import static org.junit.Assert.assertEquals;
import gov.nih.nci.caarray.application.project.ProjectManagementService;
import gov.nih.nci.caarray.application.project.ProjectManagementServiceStub;
import gov.nih.nci.caarray.application.vocabulary.VocabularyService;
import gov.nih.nci.caarray.application.vocabulary.VocabularyServiceStub;
import gov.nih.nci.caarray.domain.project.Project;
import gov.nih.nci.caarray.domain.sample.Sample;
import gov.nih.nci.caarray.domain.sample.Source;
import gov.nih.nci.caarray.domain.search.BiomaterialSearchCategory;
import gov.nih.nci.caarray.domain.search.ProjectSortCriterion;
import gov.nih.nci.caarray.domain.search.SampleJoinableSortCriterion;
import gov.nih.nci.caarray.domain.search.SearchCategory;
import gov.nih.nci.caarray.domain.search.SearchSampleCategory;
import gov.nih.nci.caarray.domain.search.SearchSourceCategory;
import gov.nih.nci.caarray.domain.search.SearchTypeSelection;
import gov.nih.nci.caarray.domain.search.SourceJoinableSortCriterion;
import gov.nih.nci.caarray.util.j2ee.ServiceLocatorStub;
import gov.nih.nci.caarray.web.AbstractBaseStrutsTest;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.fiveamsolutions.nci.commons.data.search.PageSortParams;
import com.fiveamsolutions.nci.commons.web.displaytag.SortablePaginatedList;
import com.opensymphony.xwork2.Action;

/**
 * @author Winston Cheng
 */
@SuppressWarnings("PMD")
public class SearchActionTest extends AbstractBaseStrutsTest {
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
        assertEquals(NUM_PROJECTS, searchAction.getTabs().get(SearchAction.EXPERIMENTS_TAB).intValue());
        assertEquals(Action.SUCCESS, result);

        this.searchAction.setSearchType(SearchAction.COMBINATION_SEARCH);
        searchAction.setCategoryCombo(SearchCategory.ORGANISM.name());
        result = this.searchAction.basicSearch();
        assertEquals(NUM_PROJECTS_BY_ORGANISM, searchAction.getTabs().get(SearchAction.EXPERIMENTS_TAB).intValue());
        assertEquals(Action.SUCCESS, result);

        this.searchAction.setSearchType(SearchAction.COMBINATION_SEARCH);
        searchAction.setCategoryCombo(SearchSampleCategory.SAMPLE_ORGANISM.name());
        result = this.searchAction.basicSearch();
        assertEquals(NUM_SAMPLES_BY_ORGANISM, searchAction.getTabs().get(SearchAction.SAMPLES_TAB).intValue());
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
        SortablePaginatedList<Sample, SampleJoinableSortCriterion> results = searchAction.getSampleResults();
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
        SortablePaginatedList<Source, SourceJoinableSortCriterion> results = searchAction.getSourceResults();
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
        assertEquals(SearchSampleCategory.values().length-1 + 2, this.searchAction.getSearchBiometricCategories().size());
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
