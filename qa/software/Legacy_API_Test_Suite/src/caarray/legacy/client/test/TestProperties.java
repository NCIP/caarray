/**
 * 
 */
package caarray.legacy.client.test;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author vaughng
 * Jul 31, 2009
 */
public class TestProperties
{

    public static final String SERVER_HOSTNAME_KEY = "server.hostname";
    public static final String SERVER_JNDI_PORT_KEY = "server.jndi.port";
    public static final String GRID_SERVER_HOSTNAME_KEY = "grid.server.hostname";
    public static final String GRID_SERVER_PORT_KEY = "grid.server.http.port";

    public static final String SERVER_HOSTNAME_DEFAULT = "array-qa.nci.nih.gov";
    public static final String SERVER_JNDI_PORT_DEFAULT = "8080";

    public static final String GRID_SERVER_HOSTNAME_DEFAULT = "array-qa.nci.nih.gov";
    public static final String GRID_SERVER_PORT_DEFAULT = "59580";

    public static final String REPORT_DIR_KEY = "report.dir";
    public static final String REPORT_FILE_KEY = "report.file";
    public static final String DEFAULT_REPORT_DIR = "report";
    public static final String DEFAULT_REPORT_FILE = "Legacy_API_Test_Results";
    
    public static final String API_ALL = "all";
    public static final String API_GRID = "grid";
    public static final String API_JAVA = "java";
    public static final String API_KEY = "api.use";
    public static final String DEFAULT_API = API_ALL;
    
    public static final String TEST_VERSION_KEY = "test.version";
    public static final String TEST_VERSION_SHORT = "short";
    public static final String TEST_VERSION_LONG = "long";
    public static final String TEST_VERSION_ALL = "all";
    
    public static final String CONFIG_DIR = "config";
    
    private static List<Float> excludedTests = Collections.synchronizedList(new ArrayList<Float>());
    private static List<Float> includeOnlyTests = Collections.synchronizedList(new ArrayList<Float>());
    
    
    
    public static String getJavaServerHostname() {
        return System.getProperty(SERVER_HOSTNAME_KEY, SERVER_HOSTNAME_DEFAULT);
    }

    public static int getJavaServerJndiPort() {
        return Integer.parseInt(System.getProperty(SERVER_JNDI_PORT_KEY, SERVER_JNDI_PORT_DEFAULT));
    }

    public static String getGridServerHostname() {
        return System.getProperty(GRID_SERVER_HOSTNAME_KEY, GRID_SERVER_HOSTNAME_DEFAULT);
    }

    public static int getGridServerPort() {
        return Integer.parseInt(System.getProperty(GRID_SERVER_PORT_KEY, GRID_SERVER_PORT_DEFAULT));
    }

    public static String getGridServiceUrl() {
        return ("http://" + getGridServerHostname() + ":" + getGridServerPort() + "/wsrf/services/cagrid/CaArraySvc");
    }
    
    public static String getReportDir()
    {
        return System.getProperty(REPORT_DIR_KEY,DEFAULT_REPORT_DIR);
    }
    
    public static String getReportFile()
    {
        return System.getProperty(REPORT_DIR_KEY,DEFAULT_REPORT_DIR) + File.separator + System.getProperty(REPORT_FILE_KEY,DEFAULT_REPORT_FILE);
    }

    public static String getTargetApi()
    {
        return System.getProperty(API_KEY,DEFAULT_API);
    }
    
    public static String getTestVersion()
    {
        return System.getProperty(TEST_VERSION_KEY,TEST_VERSION_SHORT);
    }
    
    public static void addExcludedTests(List<Float> tests)
    {
        excludedTests.addAll(tests);
    }
    
    public static void setJavaServerHostname(String hostname)
    {
        System.setProperty(SERVER_HOSTNAME_KEY, hostname);
    }
    
    public static void setJavaServerJndiPort(int port)
    {
        System.setProperty(SERVER_JNDI_PORT_KEY, Integer.toString(port));
    }
    
    public static void setGridServerHostname(String hostname)
    {
        System.setProperty(GRID_SERVER_HOSTNAME_KEY, hostname);
    }
    
    public static void setGridServerPort(int port)
    {
        System.setProperty(GRID_SERVER_PORT_KEY, Integer.toString(port));
    }
    
    public static void setExcludedTests(List<Float> tests)
    {
        excludedTests.clear();
        excludedTests.addAll(tests);
    }
    
    public static List<Float> getExcludedTests()
    {
        return new ArrayList<Float>(excludedTests);
    }
    
    public static void setIncludeOnlyTests(List<Float> tests)
    {
        includeOnlyTests.clear();
        includeOnlyTests.addAll(tests);
    }
    
    public static List<Float> getIncludeOnlyTests()
    {
        return new ArrayList<Float>(includeOnlyTests);
    }
}
