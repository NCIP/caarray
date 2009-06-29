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
import gov.nih.nci.caarray.external.v1_0.CaArrayEntityReference;
import gov.nih.nci.caarray.external.v1_0.experiment.Organism;
import gov.nih.nci.caarray.external.v1_0.experiment.Person;
import gov.nih.nci.caarray.external.v1_0.query.ExampleSearchCriteria;
import gov.nih.nci.caarray.external.v1_0.query.SearchResult;
import gov.nih.nci.caarray.external.v1_0.vocabulary.Category;
import gov.nih.nci.caarray.external.v1_0.vocabulary.Term;
import gov.nih.nci.caarray.services.external.v1_0.grid.client.CaArraySvc_v1_0Client;
import gov.nih.nci.caarray.services.external.v1_0.search.SearchService;

import java.io.File;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

import caarray.client.test.TestConfigurationException;
import caarray.client.test.TestProperties;
import caarray.client.test.TestResult;
import caarray.client.test.TestResultReport;
import caarray.client.test.TestUtils;

/**
 * @author vaughng
 * Jun 28, 2009
 */
public class LookupEntitiesTestSuite extends ConfigurableTestSuite
{
    private static final String CONFIG_FILE = TestProperties.CONFIG_DIR
    + File.separator + "LookupEntities.csv";
    
    private static final String TEST_CASE = "Test Case";
    private static final String API = "API";
    private static final String PI_EXPECTED_RESULTS = "Principal Investigators Expected Results";
    private static final String CC_EXPECTED_RESULTS = "Characteristic Categories Expected Results";
    private static final String TERMS_NAME = "Terms for Category Name";
    private static final String TERMS_MIN_RESULTS = "Terms for Category Min Results";
    private static final String REFERENCE = "Reference";
    private static final String REFERENCE_EXPECTED_RESULTS = "Reference Expected Results";
    private static final String REFERENCES = "References";
    private static final String REFERENCES_EXPECTED_RESULTS = "References Expected Results";
    
    private static final String[] COLUMN_HEADERS = new String[]{TEST_CASE,API,PI_EXPECTED_RESULTS,CC_EXPECTED_RESULTS,
        TERMS_NAME, TERMS_MIN_RESULTS, REFERENCE, REFERENCE_EXPECTED_RESULTS, REFERENCES, REFERENCES_EXPECTED_RESULTS};
    
    private List<ConfigurableTest> configuredTests = new ArrayList<ConfigurableTest>();
    
    /**
     * @param gridClient
     * @param javaSearchService
     */
    public LookupEntitiesTestSuite(CaArraySvc_v1_0Client gridClient,
            SearchService javaSearchService)
    {
        super(gridClient, javaSearchService);
    }

    /* (non-Javadoc)
     * @see caarray.client.test.ConfigurableTestSuite#constructSearches(java.util.List)
     */
    @Override
    protected void constructSearches(List<String> spreadsheetRows)
            throws TestConfigurationException
    {
        int index = 1;
        String row = spreadsheetRows.get(index);
        ConfigurableTest test = null;

        // Convert spreadsheet input to test objects
        while (row != null)
        {
            String[] input = TestUtils.split(row, DELIMITER);
            int testCase = 0;
            if (isNewSearch(input))
            {
                String api = null;
                if (headerIndexMap.get(API) < input.length
                        && !input[headerIndexMap.get(API)].equals(""))
                {
                    api = input[headerIndexMap.get(API)].trim();
                }
                if (headerIndexMap.get(TEST_CASE) < input.length
                        && !input[headerIndexMap.get(TEST_CASE)].equals(""))
                {
                    testCase = Integer.parseInt(input[headerIndexMap.get(TEST_CASE)]);
                }
                if (headerIndexMap.get(PI_EXPECTED_RESULTS) < input.length
                        && !input[headerIndexMap.get(PI_EXPECTED_RESULTS)].equals(""))
                {
                    int expectedResults = Integer.parseInt(input[headerIndexMap.get(PI_EXPECTED_RESULTS)]);
                    test = new PrincipalInvestigatorsTest(api,expectedResults,testCase);
                }
                else if (headerIndexMap.get(TERMS_NAME) < input.length
                        && !input[headerIndexMap.get(TERMS_NAME)].equals(""))
                {
                    int minResults = Integer.parseInt(input[headerIndexMap.get(TERMS_MIN_RESULTS)]);
                    String name = input[headerIndexMap.get(TERMS_NAME)];
                    test = new TermsForCategoryTest(api,testCase,name,minResults);
                }
                else if (headerIndexMap.get(REFERENCE) < input.length
                        && !input[headerIndexMap.get(REFERENCE)].equals(""))
                {
                    int expectedResults = Integer.parseInt(input[headerIndexMap.get(REFERENCE_EXPECTED_RESULTS)]);
                    String reference = input[headerIndexMap.get(REFERENCE)];
                    test = new GetByReferenceTest(api,testCase,reference,expectedResults);
                }
                else if (headerIndexMap.get(REFERENCES) < input.length
                        && !input[headerIndexMap.get(REFERENCES)].equals(""))
                {
                    int expectedResults = Integer.parseInt(input[headerIndexMap.get(REFERENCES_EXPECTED_RESULTS)]);
                    List<String> references = new ArrayList<String>();
                    references.add(input[headerIndexMap.get(REFERENCES)]);
                    int refIndex = index + 1;
                    while (refIndex < spreadsheetRows.size())
                    {
                        String refRow = spreadsheetRows.get(refIndex);
                        String[] inp = TestUtils.split(refRow, DELIMITER);
                        if (!isNewSearch(inp))
                        {
                            references.add(inp[headerIndexMap.get(REFERENCES)]);
                            refIndex++;
                        }
                        else
                        {
                            index = refIndex-1;
                            break;
                        }
                    }
                    test = new GetByReferencesTest(api,testCase,references,expectedResults);
                }
            }
            if (test != null)
            {
                configuredTests.add(test);
                test = null;
            }
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
    }

    /* (non-Javadoc)
     * @see caarray.client.test.ConfigurableTestSuite#executeConfiguredTests(caarray.client.test.TestResultReport)
     */
    @Override
    protected void executeConfiguredTests(TestResultReport resultReport)
    {
        for (ConfigurableTest test : configuredTests)
        {
            TestResult result = test.runTest();
            resultReport.addTestResult(result);
        }
        System.out.println("LookupEntities tests complete ...");
    }
    
    private boolean isNewSearch(String[] input)
    {
        int testCaseIndex = headerIndexMap.get(TEST_CASE);
        return (testCaseIndex < input.length && !input[testCaseIndex]
                .equals(""));
    }

    @Override
    protected String[] getColumnHeaders()
    {
        return COLUMN_HEADERS;
    }

   @Override
    protected String getConfigFilename()
    {
        return CONFIG_FILE;
    }

    @Override
    protected String getType()
    {
        return "LookupEntities";
    }
    
    class PrincipalInvestigatorsTest implements ConfigurableTest
    {
        private String api;
        private int expectedResults;
        private int testCase;
        public PrincipalInvestigatorsTest(String api, int expectedResults, int testCase)
        {
            
            this.api = api;
            this.expectedResults = expectedResults;
            this.testCase = testCase;
        }
        
        @Override
        public TestResult runTest()
        {
            TestResult testResult = new TestResult();
            testResult.setTestCase(testCase);
            if (api == null)
            {
                testResult.setPassed(false);
                testResult.setTestCase(testCase);
                String detail = "No API set for principal investigator search.";
                testResult.addDetail(detail);
                return testResult;
            }
               
            try
            {
                long start = System.currentTimeMillis();
                List<Person> results = getInvestigators();
                long elapsedTime = System.currentTimeMillis() - start;
                testResult.setElapsedTime(elapsedTime);
                if (results.size() < expectedResults)
                {
                    testResult.setPassed(false);
                    String detail = "Failed with unexpected number of results, expected: " + expectedResults + 
                        ", found: " + results.size();
                    testResult.addDetail(detail);
                }
                else
                {
                    testResult.setPassed(true);
                    String detail = "Found " + results.size() + " results.";
                    testResult.addDetail(detail);
                }
            }
            catch (Throwable t)
            {
                testResult.setPassed(false);
                String detail = "Unexpected error occurred: " + t.getLocalizedMessage();
                testResult.addDetail(detail);
            }
            
            return testResult;
        }
        
        private List<Person> getInvestigators() throws RemoteException
        {
            List<Person> resultList = new ArrayList<Person>();
            if (api.equalsIgnoreCase("java"))
                resultList.addAll(javaSearchService.getAllPrincipalInvestigators());
            else if (api.equalsIgnoreCase("grid"))
            {
                Person[] results = gridClient.getAllPrincipalInvestigators();
                if (results != null)
                {
                    for (Person person : results)
                        resultList.add(person);
                }
            }
            return resultList;    
        }
        
    }
    
    class TermsForCategoryTest implements ConfigurableTest
    {
        private String api, name;
        private int minResults, testCase;
        
        public TermsForCategoryTest(String api, int testCase, String name, int minResults)
        {
            this.api = api;
            this.testCase = testCase;
            this.name = name;
            this.minResults = minResults;
        }

        @Override
        public TestResult runTest()
        {
            TestResult testResult = new TestResult();
            testResult.setTestCase(testCase);
            if (api == null)
            {
                testResult.setPassed(false);
                testResult.setTestCase(testCase);
                String detail = "No API set for category terms search.";
                testResult.addDetail(detail);
                return testResult;
            }
            try
            {
                List<Term> results = getTerms(testResult);               
                if (results.size() < minResults)
                {
                    testResult.setPassed(false);
                    String detail = "Failed with unexpected number of results, expected: " + minResults + 
                        ", found: " + results.size();
                    testResult.addDetail(detail);
                }
                else
                {
                    testResult.setPassed(true);
                    String detail = "Found " + results.size() + " results.";
                    testResult.addDetail(detail);
                }
            }
            catch (Throwable t)
            {
                testResult.setPassed(false);
                String detail = "Unexpected error occurred: " + t.getLocalizedMessage();
                testResult.addDetail(detail);
            }
            
            return testResult;
        }
        
        private List<Term> getTerms(TestResult testResult) throws Exception
        {
            List<Term> resultsList = new ArrayList<Term>();
            ExampleSearchCriteria<Category> criteria = new ExampleSearchCriteria<Category>();
            Category exampleCategory = new Category();
            exampleCategory.setName(name);
            criteria.setExample(exampleCategory);
            SearchResult<Category> results = (SearchResult<Category>) getSearchResults(
                    api, criteria, null);
            List<Category> categories = results.getResults();
            CaArrayEntityReference categoryRef = categories.get(0)
                    .getReference();

            long startTime = System.currentTimeMillis();
            if (api.equalsIgnoreCase("java"))
            {
                resultsList.addAll(javaSearchService.getTermsForCategory(
                        categoryRef, null));
            }
            else if (api.equalsIgnoreCase("grid"))
            {
                Term[] result = gridClient.getTermsForCategory(categoryRef, null);
                if (result != null)
                {
                    for (Term term : result)
                    {
                        resultsList.add(term);  
                    }
                }              
            }
            long elapsedTime = System.currentTimeMillis() - startTime;
            testResult.setElapsedTime(elapsedTime);
            return resultsList;
        }
    }
    
    class GetByReferenceTest implements ConfigurableTest
    {
        private String reference,api;
        private int testCase, expectedResults;
        
        public GetByReferenceTest(String api, int testCase, String reference, int expectedResults)
        {
            this.api = api;
            this.testCase = testCase;
            this.reference = reference;
            this.expectedResults = expectedResults;
        }

        @Override
        public TestResult runTest()
        {
            TestResult testResult = new TestResult();
            testResult.setTestCase(testCase);
            if (api == null)
            {
                testResult.setPassed(false);
                testResult.setTestCase(testCase);
                String detail = "No API set for getByReference search.";
                testResult.addDetail(detail);
                return testResult;
            }
               
            try
            {
                long start = System.currentTimeMillis();
                AbstractCaArrayEntity result = getByReference();
                long elapsedTime = System.currentTimeMillis() - start;
                testResult.setElapsedTime(elapsedTime);
                if (result == null && expectedResults > 0)
                {
                    testResult.setPassed(false);
                    String detail = "Failed with unexpected number of results, expected: " + expectedResults + 
                        ", no result returned.";
                    testResult.addDetail(detail);
                }
                else
                {
                    if (result != null)
                    {
                        if (result instanceof Organism)
                        {
                            if (((Organism)result).getScientificName() == null)
                            {
                                testResult.setPassed(false);
                            }
                        }
                        else
                        {
                            testResult.setPassed(true);
                        }
                    }
                    if ( result == null || testResult.isPassed())
                    {
                        String detail = "Found " + expectedResults + " results.";
                        testResult.addDetail(detail);
                    }
                    else
                    {
                        String detail = "Found invalid result: " + result.getId();
                        testResult.addDetail(detail);
                    }
                    
                }
            }
            catch (Throwable t)
            {
                testResult.setPassed(false);
                String detail = "Unexpected error occurred: " + t.getLocalizedMessage();
                testResult.addDetail(detail);
            }
            
            return testResult;
        }
        
        private AbstractCaArrayEntity getByReference() throws Exception
        {
            CaArrayEntityReference ref = new CaArrayEntityReference(reference);
            if (api.equalsIgnoreCase("java"))
            {
                return javaSearchService.getByReference(ref);
            }
            if (api.equalsIgnoreCase("grid"))
            {
                return gridClient.getByReference(ref);
            }
            return null;
        }
    }
    
    class GetByReferencesTest implements ConfigurableTest
    {
        private String api;
        private int testCase, expectedResults;
        private List<String> references;
        
        public GetByReferencesTest(String api, int testCase, List<String> references, int expectedResults)
        {
            this.api = api;
            this.testCase = testCase;
            this.references = references;
            this.expectedResults = expectedResults;
        }

        @Override
        public TestResult runTest()
        {
            TestResult testResult = new TestResult();
            testResult.setTestCase(testCase);
            if (api == null)
            {
                testResult.setPassed(false);
                testResult.setTestCase(testCase);
                String detail = "No API set for getByReference search.";
                testResult.addDetail(detail);
                return testResult;
            }
               
            try
            {
                long start = System.currentTimeMillis();
                List<AbstractCaArrayEntity> results = getByReferences();
                long elapsedTime = System.currentTimeMillis() - start;
                testResult.setElapsedTime(elapsedTime);
                if (results.size() != expectedResults)
                {
                    testResult.setPassed(false);
                    String detail = "Failed with unexpected number of results, expected: " + expectedResults + 
                        ", found: " + results.size();
                    testResult.addDetail(detail);
                }
                else
                {
                     
                    if (!results.isEmpty() && results.get(0) instanceof Organism)
                    {
                        int namedResults = 0;
                        for (AbstractCaArrayEntity result : results)
                        {
                            if (((Organism)result).getScientificName() != null)
                            {
                                namedResults++;
                            }
                        }
                        if (namedResults == expectedResults)
                        {
                            testResult.setPassed(true);
                            String detail = "Found " + expectedResults + " results.";
                            testResult.addDetail(detail);
                        }
                        else
                        {
                            testResult.setPassed(false);
                            String detail = "Failed with unexpected number of named results: " + namedResults +
                                 ", expected: " + expectedResults;
                            testResult.addDetail(detail);
                        }
                    }
                    
                    else 
                    {
                        testResult.setPassed(true);
                        String detail = "Found " + expectedResults + " results.";
                        testResult.addDetail(detail);
                    }                 
                }
            }
            catch (Throwable t)
            {
                testResult.setPassed(false);
                String detail = "Unexpected error occurred: " + t.getLocalizedMessage();
                testResult.addDetail(detail);
            }
            
            return testResult;
        }
        
        private List<AbstractCaArrayEntity> getByReferences() throws Exception
        {
            
            if (api.equalsIgnoreCase("java"))
            {
                List<CaArrayEntityReference> refs = new ArrayList<CaArrayEntityReference>();
                for (String reference : references)
                {
                    refs.add(new CaArrayEntityReference(reference));
                }
                return javaSearchService.getByReferences(refs);
            }
            if (api.equalsIgnoreCase("grid"))
            {
                CaArrayEntityReference refs[] = new CaArrayEntityReference[references.size()];
                for (int i = 0; i < references.size(); i++)
                {
                    refs[i] = new CaArrayEntityReference(references.get(i));
                }
                AbstractCaArrayEntity[] results = gridClient.getByReferences(refs);
                List<AbstractCaArrayEntity> resultsList = new ArrayList<AbstractCaArrayEntity>();
                for (AbstractCaArrayEntity result : results)
                {
                    resultsList.add(result);
                }
                return resultsList;
            }
            
            return new ArrayList<AbstractCaArrayEntity>();
        }
    }

}
