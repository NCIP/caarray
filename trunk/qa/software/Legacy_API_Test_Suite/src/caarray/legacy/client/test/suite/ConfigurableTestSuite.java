/**
 * 
 */
package caarray.legacy.client.test.suite;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import caarray.legacy.client.test.ApiFacade;
import caarray.legacy.client.test.TestConfigurationException;
import caarray.legacy.client.test.TestResultReport;
import caarray.legacy.client.test.TestUtils;

/**
 * @author vaughng
 * Jul 31, 2009
 */
public abstract class ConfigurableTestSuite
{

    protected static final String DELIMITER = ",";
    protected static final String TEST_CASE = "Test Case";
    protected static final String API = "API";
    protected static final String EXPECTED_RESULTS = "Expected Results";
    protected static final String MIN_RESULTS = "Min Results";
    protected static final String VAR_START = "${";
    protected static final String EMPTY_STRING_VAR = "${empty}";
    protected static final String EMPTY_STRING_SPACE_VAR = "${empty_s}";
    protected static final String NULL_VAR = "${null}";
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
            loadConfiguredTests();
            executeConfiguredTests(resultReport);
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
    protected void loadConfiguredTests() throws FileNotFoundException,
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
     * Executes the tests configured from CSV input files.
     * 
     * @param resultReport TestResultReport to which test results will be added.
     */
    protected abstract void executeConfiguredTests(TestResultReport resultReport);
    
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
