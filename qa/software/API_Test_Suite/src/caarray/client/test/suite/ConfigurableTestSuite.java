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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import caarray.client.test.ApiFacade;
import caarray.client.test.TestConfigurationException;
import caarray.client.test.TestResultReport;
import caarray.client.test.TestUtils;

/**
 * Encapsulates a collection of test cases, some or all of which may be configured via
 * an external spreadsheet. When executed, the results of the test will be added to a TestResultReport.
 * @author vaughng
 * Jun 28, 2009
 */
public abstract class ConfigurableTestSuite
{

    protected static final String DELIMITER = ",";
    protected static final String TEST_CASE = "Test Case";
    protected static final String ID = "Id";
    protected static final String API = "API";
    protected static final String EXPECTED_RESULTS = "Expected Results";
    protected static final String MIN_RESULTS = "Min Results";
    protected static final String VAR_START = "${";
    protected static final String EMPTY_STRING_VAR = "${empty}";
    protected static final String EMPTY_STRING_SPACE_VAR = "${empty_s}";
    protected static final String NULL_VAR = "${null}";
    protected static final String ENUMERATE = "Enumerate";
    protected static final String EXCLUDE_ZERO = "Exclude Zero";
    protected static final String LOGIN= "Login";
    protected static final String API_UTILS_SEARCH = "API Utils";
    private static Map<String, String> varMap = new HashMap<String, String>();
    
    protected ApiFacade apiFacade;
    protected Map<String, Integer> headerIndexMap = new HashMap<String, Integer>();
    
    static
    {
        varMap.put(EMPTY_STRING_VAR, "");
        varMap.put(EMPTY_STRING_SPACE_VAR, " ");
        varMap.put(NULL_VAR, null);
    }

    protected ConfigurableTestSuite(ApiFacade apiFacade)
    {
        this.apiFacade = apiFacade;
    }
    
    /**
     * Loads and executes all of the test cases encapsulated in this suite, and
     * adds the test results to the given TestResultReport. The tests may be created
     * via input from a configuration file, but may also include tests determined
     * by other means.
     * 
     * @param resultReport The TestResultReport to which test results will be added.
     */
    public void runTests(TestResultReport resultReport)
    {
    
        try
        {
            loadTestsFromFile();
            executeTests(resultReport);
        }
        catch (FileNotFoundException e)
        {
            String errorMessage = "An error occured reading an " + getType()
                    + " configuration file: " + e.getMessage();
            resultReport.addErrorMessage(errorMessage);
            e.printStackTrace();
        }
        catch (IOException e)
        {
            String errorMessage = "An error occured executing the " + getType()
                    + " test suite: " + e.getMessage();
            resultReport.addErrorMessage(errorMessage);
            e.printStackTrace();
        }
    
        catch (TestConfigurationException e)
        {
            String errorMessage = "An error occured executing the "
                    + getType()
                    + " test suite, probably due to an error in the configuration "
                    + "file: " + e.getMessage();
            resultReport.addErrorMessage(errorMessage);
            e.printStackTrace();
        }
        catch (Throwable t)
        {
            String errorMessage = "An unexpected error occured executing the " + getType()
                + " : " + t.getLocalizedMessage();
            resultReport.addErrorMessage(errorMessage);
            t.printStackTrace();
        }
    
    }

    /**
     * Generic method for loading tests via input from an external CSV file.
     * Subclasses must provide type-specific helper methods, and classes
     * may override this method if necessary.
     * 
     * @throws FileNotFoundException
     * @throws IOException
     * @throws TestConfigurationException
     */
    protected void loadTestsFromFile() throws FileNotFoundException,
            IOException, TestConfigurationException
    {
        File configFile = new File(getConfigFilename());
        BufferedReader reader = new BufferedReader(new FileReader(configFile));
    
        List<String> spreadsheetRows = new ArrayList<String>();
        String row;
        while ((row = reader.readLine()) != null)
        {
            spreadsheetRows.add(row);
        }
        if (spreadsheetRows.size() > 1)
        {
            // Map column headers to column indexes
            String[] columnHeaders = TestUtils.split(spreadsheetRows.get(0),
                    DELIMITER);
            populateHeaderIndexMap(columnHeaders);
            constructSearches(spreadsheetRows);
        }
    }

    /**
     * Reads the first row of a CSV file and maps column headers to
     * column indexes, to facilitate the reading of configuration file input.
     * 
     * @param columnHeaders The values read from the first row of a CSV configuration file;
     * @throws TestConfigurationException
     */
    protected void populateHeaderIndexMap(String[] columnHeaders)
            throws TestConfigurationException
    {
        if (columnHeaders != null)
        {
            for (int i = 0; i < columnHeaders.length; i++)
            {
                for (String header : getColumnHeaders())
                {
                    if (columnHeaders[i].trim().equalsIgnoreCase(header))
                    {
                        headerIndexMap.put(header, i);
                        break;
                    }
                }
            }
            for (String header : getColumnHeaders())
            {
                if (!headerIndexMap.containsKey(header))
                    throw new TestConfigurationException(
                            "No column found in the spreadsheet for: " + header);
            }
        }
    }

    /**
     * General method for retrieving search-by-example search results via the java
     * or grid searchByExample method.
     * 
     * @param api Either 'java' or 'grid', indicating the API to be used.
     * @param criteria ExampleSearchCriteria to be used for the search.
     * @param offset LimitOffset to be used with the search.
     * @return The search results obtained.
     * @throws RemoteException
     */
    protected SearchResult<? extends AbstractCaArrayEntity> getSearchResults(String api, ExampleSearchCriteria<? extends AbstractCaArrayEntity> criteria, LimitOffset offset)
            throws Exception
    {
        return apiFacade.searchByExample(api, criteria, offset);
    }

    /**
     * Executes the tests configured from CSV input files.
     * 
     * @param resultReport TestResultReport to which test results will be added.
     */
    protected abstract void executeTests(TestResultReport resultReport);
    
    /**
     * Type-specific method for constructing the searches to be used in this test suite.
     * 
     * @param spreadsheetRows The input read from a CSV file, as a list of individual rows of comma-separated values.
     * @throws TestConfigurationException
     */
    protected abstract void constructSearches(List<String> spreadsheetRows)
            throws TestConfigurationException;

    /**
     * Returns the name of the configuration file from which input will be read.
     * 
     * @return The name of the configuration file from which input will be read.
     */
    protected abstract String getConfigFilename();

    /**
     * Returns the column headers expected to be found in an input file.
     * 
     * @return The column headers expected to be found in an input file.
     */
    protected abstract String[] getColumnHeaders();

    /**
     * Returns a string representation of the type of this test suite, to be used
     * in result output.
     * 
     * @return a string representation of the type of this test suite.
     */
    protected abstract String getType();

    /**
     * Returns true if a row of spreadsheet input is the start of a new test case, false otherwise.
     * 
     * @param input Input row taken from a configuration spreadsheet.
     * @return true if a row of spreadsheet input is the start of a new test case, false otherwise.
     */
    protected boolean isNewSearch(String[] input)
    {
        int testCaseIndex = headerIndexMap.get(TEST_CASE);
        return (testCaseIndex < input.length && !input[testCaseIndex]
                .equals(""));
    }

    protected String getVariableValue(String var)
    {
        if (varMap.containsKey(var))
            return varMap.get(var);
        return var;
    }
    
    public String getDisplayName()
    {
        return getType();
    }
}
