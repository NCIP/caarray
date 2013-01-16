//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
package caarray.client.test;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Provides access to properties required to run the test suite.
 * 
 * @author vaughng
 * Jun 26, 2009
 */
public class TestProperties {

	public static final String SERVER_HOSTNAME_KEY = "server.hostname";
    public static final String SERVER_JNDI_PORT_KEY = "server.jndi.port";
    
    public static final String SERVER_HOSTNAME_DEFAULT = "array-qa.nci.nih.gov";
    public static final String SERVER_JNDI_PORT_DEFAULT = "8080";
    
	public static final String GRID_SERVER_HOSTNAME_KEY = "grid.server.hostname";
    public static final String GRID_SERVER_PORT_KEY = "grid.server.http.port";
    
    public static final String GRID_SERVER_HOSTNAME_DEFAULT = "array-qa.nci.nih.gov";
    public static final String GRID_SERVER_PORT_DEFAULT = "80";
    
    public static final String REPORT_DIR_KEY = "report.dir";
    public static final String REPORT_FILE_KEY = "report.file";
    public static final String LOAD_REPORT_FILE_KEY = "load.report.file";
    public static final String LOAD_ANALYSIS_FILE_KEY = "load.analysis.file";
    public static final String DEFAULT_REPORT_DIR = "report";
    public static final String DEFAULT_REPORT_FILE = "External_API_Test_Results";
    public static final String DEFAULT_LOAD_REPORT_FILE = "Load_Tests_API_Test_Results";
    public static final String DEFAULT_LOAD_ANALYSIS_FILE = "Load_Test_Analysis";
    
    public static final String API_ALL = "all";
    public static final String API_GRID = "grid";
    public static final String API_JAVA = "java";
    public static final String API_KEY = "api.use";
    public static final String DEFAULT_API = API_ALL;
    
    public static final String TEST_VERSION_KEY = "test.version";
    public static final String TEST_VERSION_SHORT = "short";
    public static final String TEST_VERSION_LONG = "long";
    public static final String TEST_VERSION_ALL = "all";
    public static final String TEST_CASE_LONG_KEY = "test.long.exclude";
    public static final String TEST_CASE_LONG = "351,125,133,142,182,183,203,204,294,295,297,299,301,363,365,366,367,368,369,409,410,"
            + "352,104,107,109,112,114,349,115,119,124,125,350,51,52,318,319";
    
    public static final String NUM_THREADS_KEY = "threads.num";
    
    public static final String CONFIG_DIR = "config";
    
    private static List<Float> excludedTests = Collections.synchronizedList(new ArrayList<Float>());
    private static List<Float> includeOnlyTests = Collections.synchronizedList(new ArrayList<Float>());
    
    public static String getJavaServerHostname() {
        return System.getProperty(SERVER_HOSTNAME_KEY, SERVER_HOSTNAME_DEFAULT);
    }
    
    public static int getJavaServerJndiPort() {
        return Integer.parseInt(System.getProperty(SERVER_JNDI_PORT_KEY, SERVER_JNDI_PORT_DEFAULT));
    }
    
    public static void setJavaServerHostname(String hostname)
    {
        System.setProperty(SERVER_HOSTNAME_KEY, hostname);
    }
    
    public static void setJavaServerJndiPort(int port)
    {
        System.setProperty(SERVER_JNDI_PORT_KEY, Integer.toString(port));
    }
    
	public static String getGridServerHostname() {
        return System.getProperty(GRID_SERVER_HOSTNAME_KEY, GRID_SERVER_HOSTNAME_DEFAULT);
    }

	public static int getGridServerPort() {
        return Integer.parseInt(System.getProperty(GRID_SERVER_PORT_KEY, GRID_SERVER_PORT_DEFAULT));
    }
	
	public static void setGridServerHostname(String hostname)
	{
	    System.setProperty(GRID_SERVER_HOSTNAME_KEY, hostname);
	}
	
	public static void setGridServerPort(int port)
	{
	    System.setProperty(GRID_SERVER_PORT_KEY, Integer.toString(port));
	}

    public static String getGridServiceUrl() {
        return ("http://" + getGridServerHostname() + ":" + getGridServerPort() + "/wsrf/services/cagrid/CaArraySvc_v1_0");
    }
    
    public static String getReportDir()
    {
    	return System.getProperty(REPORT_DIR_KEY,DEFAULT_REPORT_DIR);
    }
    
    public static void setReportDir(String dir)
    {
        System.setProperty(REPORT_DIR_KEY, dir);
    }
    
    public static String getReportFile()
    {
        if (getNumThreads() <= 1)
            return System.getProperty(REPORT_DIR_KEY,DEFAULT_REPORT_DIR) + File.separator + System.getProperty(REPORT_FILE_KEY,DEFAULT_REPORT_FILE);
        
        return System.getProperty(REPORT_DIR_KEY,DEFAULT_REPORT_DIR) + File.separator + System.getProperty(LOAD_REPORT_FILE_KEY,
                DEFAULT_LOAD_REPORT_FILE);
    }
    
    public static String getLoadAnalysisFile()
    {
        return System.getProperty(REPORT_DIR_KEY,DEFAULT_REPORT_DIR) + File.separator + System.getProperty(LOAD_ANALYSIS_FILE_KEY,
                DEFAULT_LOAD_ANALYSIS_FILE);    
    }

	public static String getTargetApi()
	{
	    return System.getProperty(API_KEY,DEFAULT_API);
	}
	
	public static String getTestVersion()
	{
	    return System.getProperty(TEST_VERSION_KEY,TEST_VERSION_SHORT);
	}
	
	public static int getNumThreads()
	{
	    return Integer.parseInt(System.getProperty(NUM_THREADS_KEY,"1"));
	}
	
	public static void setNumThreads(int numThreads)
	{
	    System.setProperty(NUM_THREADS_KEY, Integer.toString(numThreads));
	}
	
	public static synchronized void setExcludedTests(List<Float> tests)
	{
	    excludedTests.clear();
	    excludedTests.addAll(tests);
	}
	
	public static List<Float> getExcludedTests()
	{
	    return excludedTests;
	}
	
	public static synchronized void setIncludeOnlyTests(List<Float> tests)
	{
	    includeOnlyTests.clear();
	    includeOnlyTests.addAll(tests);
	}
	
	public static List<Float> getIncludeOnlyTests()
	{
	    return includeOnlyTests;
	}
	
	public static void excludeLongTests()
	{
	    String tests = System.getProperty(TEST_CASE_LONG_KEY, TEST_CASE_LONG);
	    if (tests != null)
	    {
	        String[] testArray = tests.split(",");
	        for (String test : testArray)
	        {
	            getExcludedTests().add(Float.parseFloat(test));
	        }
	        
	    }
	}
	
	public static void removeExcludedLongTests()
	{
	    String tests = System.getProperty(TEST_CASE_LONG_KEY, TEST_CASE_LONG);
        if (tests != null)
        {
            String[] testArray = tests.split(",");
            for (String test : testArray)
            {
                getExcludedTests().remove(Float.parseFloat(test));
            }
            
        }
	}
}
