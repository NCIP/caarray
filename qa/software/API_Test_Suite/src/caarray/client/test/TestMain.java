//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
package caarray.client.test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import caarray.client.test.suite.AnnotationSetTestSuite;
import caarray.client.test.suite.ArrayDataTypeTestSuite;
import caarray.client.test.suite.ArrayDesignTestSuite;
import caarray.client.test.suite.AssayTypeTestSuite;
import caarray.client.test.suite.BiomaterialCriteriaTestSuite;
import caarray.client.test.suite.BiomaterialKeywordTestSuite;
import caarray.client.test.suite.BiomaterialTestSuite;
import caarray.client.test.suite.CategoryTestSuite;
import caarray.client.test.suite.ConfigurableTestSuite;
import caarray.client.test.suite.CriteriaPerformanceTestSuite;
import caarray.client.test.suite.DataSetTestSuite;
import caarray.client.test.suite.ExperimentCriteriaTestSuite;
import caarray.client.test.suite.ExperimentKeywordTestSuite;
import caarray.client.test.suite.ExperimentTestSuite;
import caarray.client.test.suite.ExperimentalContactTestSuite;
import caarray.client.test.suite.FactorTestSuite;
import caarray.client.test.suite.FileContentsTestSuite;
import caarray.client.test.suite.FileCriteriaTestSuite;
import caarray.client.test.suite.FileTestSuite;
import caarray.client.test.suite.FileTypeTestSuite;
import caarray.client.test.suite.HybridizationCriteriaTestSuite;
import caarray.client.test.suite.HybridizationTestSuite;
import caarray.client.test.suite.LookupEntitiesTestSuite;
import caarray.client.test.suite.OrganismTestSuite;
import caarray.client.test.suite.PersonTestSuite;
import caarray.client.test.suite.QuantitationTypeCriteriaTestSuite;
import caarray.client.test.suite.QuantitationTypeTestSuite;
import caarray.client.test.suite.TermSourceTestSuite;
import caarray.client.test.suite.TermTestSuite;

/**
 * Main class for executing a collection of tests against the caArray
 * external API. Executed by build script test targets.
 * 
 * @author vaughng
 *
 */
public class TestMain {

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
	    ConfigurableTestSuite[] shortSuites = new ConfigurableTestSuite[]{new ArrayDataTypeTestSuite(apiFacade),
                new ArrayDesignTestSuite(apiFacade), new AssayTypeTestSuite(apiFacade), new CategoryTestSuite(apiFacade), 
                new FileTypeTestSuite(apiFacade), new OrganismTestSuite(apiFacade), new TermSourceTestSuite(apiFacade),
                new QuantitationTypeTestSuite(apiFacade), new ExperimentTestSuite(apiFacade), new ExperimentalContactTestSuite(apiFacade),
                new FactorTestSuite(apiFacade), new PersonTestSuite(apiFacade), new TermTestSuite(apiFacade), new FileTestSuite(apiFacade),
                new LookupEntitiesTestSuite(apiFacade), new ExperimentCriteriaTestSuite(apiFacade),
                new ExperimentKeywordTestSuite(apiFacade),                            
                new FileCriteriaTestSuite(apiFacade), new HybridizationCriteriaTestSuite(apiFacade),
                new QuantitationTypeCriteriaTestSuite(apiFacade),
                new AnnotationSetTestSuite(apiFacade), new DataSetTestSuite(apiFacade),new FileContentsTestSuite(apiFacade),
                new CriteriaPerformanceTestSuite(apiFacade)};
	    
	    
	    return Arrays.asList(shortSuites);
	}
	
	public static List<ConfigurableTestSuite> getLongTestSuites(ApiFacade apiFacade)
	{
	    ConfigurableTestSuite[] longSuites = new ConfigurableTestSuite[]{new BiomaterialCriteriaTestSuite(apiFacade),
                new BiomaterialKeywordTestSuite(apiFacade), new HybridizationTestSuite(apiFacade), new BiomaterialTestSuite(apiFacade)};
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
