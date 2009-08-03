/**
 * 
 */
package caarray.legacy.client.test.suite;

import java.util.List;

import caarray.legacy.client.test.ApiFacade;
import caarray.legacy.client.test.TestConfigurationException;
import caarray.legacy.client.test.TestResultReport;

/**
 * @author vaughng
 * Jul 31, 2009
 */
public class CQLTestSuite extends ConfigurableTestSuite
{

    /**
     * @param apiFacade
     */
    public CQLTestSuite(ApiFacade apiFacade)
    {
        super(apiFacade);
        // TODO Auto-generated constructor stub
    }

    /* (non-Javadoc)
     * @see caarray.legacy.client.test.suite.ConfigurableTestSuite#constructSearches(java.util.List)
     */
    @Override
    protected void constructSearches(List<String> spreadsheetRows)
            throws TestConfigurationException
    {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see caarray.legacy.client.test.suite.ConfigurableTestSuite#executeConfiguredTests(caarray.legacy.client.test.TestResultReport)
     */
    @Override
    protected void executeConfiguredTests(TestResultReport resultReport)
    {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see caarray.legacy.client.test.suite.ConfigurableTestSuite#getColumnHeaders()
     */
    @Override
    protected String[] getColumnHeaders()
    {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see caarray.legacy.client.test.suite.ConfigurableTestSuite#getConfigFilename()
     */
    @Override
    protected String getConfigFilename()
    {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see caarray.legacy.client.test.suite.ConfigurableTestSuite#getType()
     */
    @Override
    protected String getType()
    {
        // TODO Auto-generated method stub
        return null;
    }

}
