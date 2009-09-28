/**
 * The software subject to this notice and license includes both human readable
 * source code form and machine readable, binary, object code form. The caarrayGridClientExamples_v1_0
 * Software was developed in conjunction with the National Cancer Institute
 * (NCI) by NCI employees and 5AM Solutions, Inc. (5AM). To the extent
 * government employees are authors, any rights in such works shall be subject
 * to Title 17 of the United States Code, section 105.
 *
 * This caarrayGridClientExamples_v1_0 Software License (the License) is between NCI and You. You (or
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
 * its rights in the caarrayGridClientExamples_v1_0 Software to (i) use, install, access, operate,
 * execute, copy, modify, translate, market, publicly display, publicly perform,
 * and prepare derivative works of the caarrayGridClientExamples_v1_0 Software; (ii) distribute and
 * have distributed to and by third parties the caarrayGridClientExamples_v1_0 Software and any
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
package caarray.client.test.suite;

import gov.nih.nci.caarray.external.v1_0.AbstractCaArrayEntity;
import gov.nih.nci.caarray.external.v1_0.query.ExampleSearchCriteria;
import gov.nih.nci.caarray.external.v1_0.query.LimitOffset;
import gov.nih.nci.caarray.external.v1_0.query.SearchResult;

import java.util.ArrayList;
import java.util.List;

import caarray.client.test.ApiFacade;
import caarray.client.test.TestConfigurationException;
import caarray.client.test.TestProperties;
import caarray.client.test.TestResult;
import caarray.client.test.TestResultReport;
import caarray.client.test.TestUtils;
import caarray.client.test.search.ExampleSearch;

/**
 * Encapsulates a collection of search-by-example test searches, some
 * or all of which may be parameterized via a configuration spreadsheet.
 * 
 * @author vaughng 
 * Jun 26, 2009
 */
public abstract class SearchByExampleTestSuite extends ConfigurableTestSuite
{
    protected static final String MIN_RESULTS = "Min Results";
    protected static final String PAGES = "Pages";
    protected List<ExampleSearch> configuredSearches = new ArrayList<ExampleSearch>();
    protected SearchByExampleTestSuite(ApiFacade apiFacade)
    {
        super(apiFacade);
    }

    @Override
    protected void constructSearches(List<String> spreadsheetRows)
            throws TestConfigurationException
    {
        int index = 1;
        String row = spreadsheetRows.get(index);
        List<Float> excludeTests = TestProperties.getExcludedTests();
        List<Float> includeTests = TestProperties.getIncludeOnlyTests();
        ExampleSearch search = null;
    
        // Iterate each row of spreadsheet input and construct individual search objects
        while (row != null)
        {
            
            String[] input = TestUtils.split(row, DELIMITER);
            //If the input row begins a new search, create a new object
            //otherwise, continue adding parameters to the existing object
            if (isNewSearch(input))
            {
                search = getExampleSearch();
                boolean skip = false;
                if (headerIndexMap.get(TEST_CASE) < input.length
                && !input[headerIndexMap.get(TEST_CASE)].equals(""))
                {
                    search.setTestCase(Float.parseFloat(input[headerIndexMap.get(TEST_CASE)]
                                                              .trim()));
                    if (!excludeTests.isEmpty() && (excludeTests.contains(search.getTestCase()) || excludeTests.contains((float)Math.floor(search.getTestCase()))))
                        skip = true;
                    if (!includeTests.isEmpty() && (!includeTests.contains(search.getTestCase())&& !includeTests.contains((float)Math.floor(search.getTestCase()))))
                        skip = true;
                }
                if (!skip)
                {
                    
                    populateSearch(input, search);  
                }              
            }
            else
            {
                boolean skip = false;
                if (search == null)
                    throw new TestConfigurationException(
                            "No test case indicated for row: " + index);
                if (!excludeTests.isEmpty() && (excludeTests.contains(search.getTestCase()) || excludeTests.contains((float)Math.floor(search.getTestCase()))))
                    skip = true;
                if (!includeTests.isEmpty() && (!includeTests.contains(search.getTestCase())&& !includeTests.contains((float)Math.floor(search.getTestCase()))))
                    skip = true;
                if (!skip)
                {
                    populateAdditionalSearchValues(input, search);
                }
                
            }
    
            if (search != null)
                configuredSearches.add(search);
    
            index++;
            if (index < spreadsheetRows.size())
            {
                row = spreadsheetRows.get(index);
            }
            else
            {
                row = null;
            }
        }
        filterSearches();
    }

    protected void executeTests(TestResultReport resultReport)
    {
        for (ExampleSearch search : configuredSearches)
        {
            TestResult testResult = new TestResult();
            try
            {
                if (search.getApi() == null)
                {
                    setTestResultFailure(testResult, search,
                            "No API indicated for test case: "
                                    + search.getTestCase());
                    resultReport.addTestResult(testResult);
                    continue;
                }

                
                ExampleSearchCriteria<AbstractCaArrayEntity> criteria = new ExampleSearchCriteria<AbstractCaArrayEntity>();
                criteria.setExample(search.getExample());
                if (search.getMatchMode() != null)
                {
                   criteria.setMatchMode(search.getMatchMode()); 
                }
                criteria.setExcludeZeroes(search.isExcludeZeros());
                List<AbstractCaArrayEntity> resultsList = new ArrayList<AbstractCaArrayEntity>();
                long startTime = System.currentTimeMillis();
                try
                {
                    if (search.isEnumerate())
                    {
                        resultsList.addAll(apiFacade.enumerateByExample(search.getApi(), criteria, search.getExample().getClass()));
                    }
                    else if (search.isApiUtil())
                    {
                        resultsList.addAll(apiFacade.searchByExampleUtils(search.getApi(), criteria));
                    }
                    else
                    {
                        LimitOffset offset = null;
                        if (search.getPages() != null)
                        {
                            offset = new LimitOffset(search.getPages(),0);        
                        }
                        else if (search.getStopResults() != null)
                        {
                            offset = new LimitOffset(search.getStopResults(),0);
                        }
                        SearchResult<? extends AbstractCaArrayEntity> results = getSearchResults(search.getApi(), criteria, offset);
                        search.addPageReturned(results.getResults().size());
                        resultsList.addAll(results.getResults());
                        boolean fullResults = results.isFullResult();
                        boolean stopResults = search.getStopResults() != null && results.getResults().size() >= search.getStopResults();
                        if (!stopResults)
                        {
                            while ((!fullResults && results.getResults().size() > 0) || (offset != null && search.getPages() != null 
                                    && results.getResults().size() == search.getPages()))
                            {
                                offset = new LimitOffset();
                                if (search.getPages() != null)
                                    offset.setLimit(search.getPages());
                                offset.setOffset(results.getResults().size()
                                        + results.getFirstResultOffset());
                                
                                results = getSearchResults(search.getApi(), criteria, offset);
                                resultsList.addAll(results.getResults());
                                search.addPageReturned(results.getResults().size());                         
                            }
                        }
                        
                    }
                    
                }
                catch (Throwable t)
                {
                    String detail = "An exception occured executing " + getType() + " search-by-example: "
                    + t.getClass() + " " + t.getLocalizedMessage();
                    testResult.addDetail(detail);
                    t.printStackTrace();
                }
                long elapsedTime = System.currentTimeMillis() - startTime;

                testResult.setElapsedTime(elapsedTime);
                if (search.getTestCase() != null)
                    testResult.setTestCase(search.getTestCase());

                evaluateResults(resultsList, search, testResult);
            }
            catch (Throwable t)
            {

                setTestResultFailure(testResult, search,
                        "An exception occured executing an " + getType() + " search-by-example: "
                                + t.getClass() + " " + t.getLocalizedMessage());
                t.printStackTrace();
            }

            resultReport.addTestResult(testResult);
        }
        
        System.out.println(getType() + " tests complete ...");
    }
    
    private void filterSearches()
    {
        String api = TestProperties.getTargetApi();
        if (!api.equalsIgnoreCase(TestProperties.API_ALL))
        {
            List<ExampleSearch> filteredSearches = new ArrayList<ExampleSearch>();
            for (ExampleSearch search : configuredSearches)
            {
                if (search.getApi().equalsIgnoreCase(api))
                    filteredSearches.add(search);
            }
            configuredSearches = filteredSearches;
        }
        List<Float> excludedTests = TestProperties.getExcludedTests();
        if (!excludedTests.isEmpty())
        {
            List<ExampleSearch> filteredSearches = new ArrayList<ExampleSearch>();
            for (ExampleSearch search : configuredSearches)
            {
                if (!excludedTests.contains(search.getTestCase()) && !excludedTests.contains(search.getTestCase()))
                    filteredSearches.add(search);
            }
            configuredSearches = filteredSearches;
        }
        List<Float> includeTests = TestProperties.getIncludeOnlyTests();
        if (!includeTests.isEmpty())
        {
            List<ExampleSearch> filteredSearches = new ArrayList<ExampleSearch>();
            for (ExampleSearch search : configuredSearches)
            {
                if (includeTests.contains(search.getTestCase()) || includeTests.contains((float)Math.floor(search.getTestCase())))
                    filteredSearches.add(search);
            }
            configuredSearches = filteredSearches; 
        }
    }

    /**
     * Convenience method for adding an unexpected error message to a test result.
     * 
     * @param testResult
     * @param search
     * @param errorMessage
     */
    protected void setTestResultFailure(TestResult testResult,
            ExampleSearch search, String errorMessage)
    {
        testResult.setPassed(false);
        if (search.getTestCase() != null)
            testResult.setTestCase(search.getTestCase());
        testResult.addDetail(errorMessage);
    }
    
    /**
     * Populates an ExampleSearch bean with values taken from a configuration spreadsheet.
     * 
     * @param input Input row taken from a configuration spreadsheet.
     * @param exampleSearch ExampleSearch bean to be populated.
     */
    protected abstract void populateSearch(String[] input, ExampleSearch exampleSearch);
    
    /**
     * For test cases configured in multiple rows of a spreadsheet, populates search
     * with the values entered in continuation rows.
     * @param input Input row taken from a configuration spreadsheet.
     * @param exampleSearch ExampleSearch bean to be populated.
     */
    protected abstract void populateAdditionalSearchValues(String[] input, ExampleSearch exampleSearch);
    
    /**
     * Determines the pass/fail status of a test based on the given search results.
     * 
     * @param resultsList The results of a test search.
     * @param search ExampleSearch specifying the expected results of the test search.
     * @param testResult TestResult to which a status will be added.
     */
    protected abstract void evaluateResults(List<? extends AbstractCaArrayEntity> resultsList, ExampleSearch search, TestResult testResult);

    /**
     * Returns a new, type-specific ExampleSearch object to be populated.
     * @return a new, type-specific ExampleSearch object to be populated.
     */
    protected abstract ExampleSearch getExampleSearch();
}
