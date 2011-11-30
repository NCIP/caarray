/**
 * 
 */
package caarray.legacy.client.test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import caarray.legacy.client.test.suite.AssayTypeTestSuite;
import caarray.legacy.client.test.suite.CQLTestSuite;
import caarray.legacy.client.test.suite.CaArrayFileTestSuite;
import caarray.legacy.client.test.suite.CategoryTestSuite;
import caarray.legacy.client.test.suite.ConfigurableTestSuite;
import caarray.legacy.client.test.suite.DataSetTestSuite;
import caarray.legacy.client.test.suite.ExperimentTestSuite;
import caarray.legacy.client.test.suite.FactorTestSuite;
import caarray.legacy.client.test.suite.FileDownloadTestSuite;
import caarray.legacy.client.test.suite.PropertyTestSuit;
import caarray.legacy.client.test.suite.QuantitationTypeTestSuite;
import caarray.legacy.client.test.suite.SampleTestSuite;

/**
 * @author vaughng
 * Jul 31, 2009
 */
public class TestMain
{

    private TestResultReport resultReport = new TestResultReport();
    
    public void runTests(ApiFacade apiFacade) throws Exception
    {
        apiFacade.connect();
        List<ConfigurableTestSuite> testSuites = getTestSuites(apiFacade);
        
        System.out.println("Executing test suites ...");
        for (ConfigurableTestSuite testSuite : testSuites)
        {
            testSuite.runTests(resultReport);
        }
        resultReport.writeReport();
        System.out.println("Tests completed.");
    }
    
    private static List<ConfigurableTestSuite> getTestSuites(ApiFacade apiFacade)
    {
        
        String version = TestProperties.getTestVersion();
        if (version.equals(TestProperties.TEST_VERSION_SHORT))
            return getShortTestSuites(apiFacade);
        if (version.equals(TestProperties.TEST_VERSION_LONG))
            return getLongTestSuites(apiFacade);
        
        return getAllTestSuites(apiFacade);
    }
    
    public static List<ConfigurableTestSuite> getShortTestSuites(ApiFacade apiFacade)
    {
        ConfigurableTestSuite[] shortSuites = new ConfigurableTestSuite[]{
                new CaArrayFileTestSuite(apiFacade), new SampleTestSuite(apiFacade), new ExperimentTestSuite(apiFacade), new QuantitationTypeTestSuite(apiFacade),
                new CQLTestSuite(apiFacade),
                new PropertyTestSuit(apiFacade),
                new DataSetTestSuite(apiFacade), new FileDownloadTestSuite(apiFacade),
                new CategoryTestSuite(apiFacade), new AssayTypeTestSuite(apiFacade),new FactorTestSuite(apiFacade)
        };
        
        
        return Arrays.asList(shortSuites);
    }
    
    public static List<ConfigurableTestSuite> getLongTestSuites(ApiFacade apiFacade)
    {
        ConfigurableTestSuite[] longSuites = new ConfigurableTestSuite[]{};
        return Arrays.asList(longSuites);
    }
    
    public static List<ConfigurableTestSuite> getAllTestSuites(ApiFacade apiFacade)
    {
        List<ConfigurableTestSuite> all = new ArrayList<ConfigurableTestSuite>();
        all.addAll(getShortTestSuites(apiFacade));
        all.addAll(getLongTestSuites(apiFacade));
        return all;
    }
}
