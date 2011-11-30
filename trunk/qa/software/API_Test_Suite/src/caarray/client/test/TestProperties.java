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
