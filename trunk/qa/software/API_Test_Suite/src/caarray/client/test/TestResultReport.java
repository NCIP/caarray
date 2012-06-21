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
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * Encapsulates the results of one or more individual tests,
 * and produces a report file detailing the test results.
 * 
 * @author vaughng
 * Jun 25, 2009
 */
public class TestResultReport {

	private Set<TestResult> results = new TreeSet<TestResult>(new TestResultComparator());
	private List<String> errorMessages = new ArrayList<String>();
	
	public static final String TEST_CASE = "Test Case";
	public static final String THREAD = "Thread";
	public static final String STATUS = "Test Status";
	public static final String TIME = "Elapsed Time";
	public static final String DETAILS = "Details";
	public static final String[] COLUMN_HEADERS = new String[]{TEST_CASE,THREAD,STATUS,TIME,DETAILS};
	private static final String DELIMITER = ",";
	
	public TestResultReport()
	{
		
	}
	
	/**
	 * Adds a TestResult to this report.
	 * 
	 * @param testResult The TestResult to be added.
	 */
	public void addTestResult(TestResult testResult)
	{
		results.add(testResult);
	}
	
	/**
	 * Adds the results of the given TestResultReport to this TestResultReport.
	 * 
	 * @param report TestResultReport to be merged with this report.
	 */
	public void merge(TestResultReport report)
	{
	    results.addAll(report.results);
	    errorMessages.addAll(report.errorMessages);
	}
	
	/**
	 * Adds an error message that does not correspond to an
	 * individual test, such as an error encountered in a configuration
	 * file. Error messages will be included in the report file.
	 * 
	 * @param errorMessage An error message that does not correspond to an individual test.
	 */
	public void addErrorMessage(String errorMessage)
	{
		errorMessages.add(errorMessage);
	}
	
	/**
	 * Sets the threadId for every TestResult in this TestResultReport.
	 * 
	 * @param threadId The threadId to be set for every TestResult in this report.
	 */
	public void setThreadId(int threadId)
	{
	    for (TestResult result : results)
	    {
	        result.setThreadId(threadId);
	    }
	}
	
	/**
	 * Produces a CSV file detailing the TestResults contained
	 * in this TestResultReport. Error messages not specific to
	 * individual tests will also be included in the report file.
	 * The name and location of the file is determined by properties
	 * obtained from the TestProperties class, with the addition of
	 * a time stamp included in the file name to prevent the overwriting
	 * of previous report files.
	 * 
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public void writeReport() throws FileNotFoundException, IOException
	{
		String reportDirName = TestProperties.getReportDir();
		File reportDir = new File(reportDirName);
		if (!reportDir.exists())
		{
			reportDir.mkdirs();
		}
		SimpleDateFormat df = new SimpleDateFormat("-yyyy-MM-dd-HH.mm.ss.SSS");
		String reportFilename = TestProperties.getReportFile() + df.format(Calendar.getInstance().getTime()) + ".csv";
		File reportFile = new File(reportFilename);
		if (!reportFile.exists())
		{
			reportFile.createNewFile();
		}
		
		Writer writer = new OutputStreamWriter(new FileOutputStream(reportFile));
		
		String columnHeaders = TestUtils.delimit(COLUMN_HEADERS, DELIMITER);
		writer.write(columnHeaders);
		writer.write('\n');
		for (String errorMessage : errorMessages)
		{
			String error = TestUtils.delimit(new String[]{"Unexpected Error",errorMessage}, DELIMITER);
			writer.write(error);
			writer.write('\n');
		}
		for (TestResult result : results)
		{
			String[] resultArray = new String[]{Float.toString(result.getTestCase()), Integer.toString(result.getThreadId()),
					(result.isPassed() ? "passed" : "failed"), Long.toString(result.getElapsedTime()) + " ms",result.getDetails()};
			String results = TestUtils.delimit(resultArray, DELIMITER);
			writer.write(results);
			writer.write('\n');
		}
		
		writer.close();
		System.out.println("Test result report written to: " + reportFilename);
		
	}
	
	/**
	 * Produces two CSV files, one detailing the TestResults contained
     * in this TestResultReport and the other detailing pass/fail and execution
     * time discrepancies between threads for individual test cases.
     * 
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public void writeLoadTestReports() throws FileNotFoundException, IOException
	{
	    writeLoadTestAnalysis();
	    writeReport();
	}
	
	private void writeLoadTestAnalysis() throws FileNotFoundException, IOException
	{
	    String[] columnHeaders = new String[]{"Test Case", "Pass/Fail Discrepancy","Execution Time Discrepancy"};
	    List<String[]> resultRows = new ArrayList<String[]>();
	    
	    List<TestResult> resultList = new ArrayList<TestResult>();
	    resultList.addAll(results);
	    
	    // Create a mapping of test case -> a list of the test results for that case
	    Map<Float, List<Integer>> testCaseIndexMap = new TreeMap<Float, List<Integer>>();
	    for (int i = 0; i < resultList.size(); i++)
	    {
	        TestResult result = resultList.get(i);
	        float testCase = result.getTestCase();
	        if (!testCaseIndexMap.containsKey(testCase))
	        {
	            testCaseIndexMap.put(testCase, new ArrayList<Integer>());
	        }
	        testCaseIndexMap.get(testCase).add(i);
	    }
	     
	    // For each test case, compare the results for each thread and record any discrepancies
	    for (float testCase : testCaseIndexMap.keySet())
	    {
	        String[] resultRow = null;
	        List<Integer> indices = testCaseIndexMap.get(testCase);
	        
	        //Look for inconsistencies for pass/fail for a single case
	        List<Integer> passed = new ArrayList<Integer>();
	        List<Integer> failed = new ArrayList<Integer>();
	        
	        //Look for elapsed time inconsistencies (1 minute or more)
	        long minTime = Long.MAX_VALUE;
            long maxTime = Long.MIN_VALUE;
            int shortThread = -1;
            int longThread = -1;
            long maxDiff = 60000;
            
	        for (int i : indices)
	        {
	            TestResult result = resultList.get(i);
	            if (result.isPassed())
	            {
	                passed.add(result.getThreadId());
	            }
	            else
	            {
	                failed.add(result.getThreadId());
	            }
	            long time = result.getElapsedTime();
	            if (time < minTime)
	            {
	                minTime = time;
	                shortThread = result.getThreadId();
	            }
	            if (time > maxTime)
	            {
	                maxTime = time;
	                longThread = result.getThreadId();
	            }
	        }
	        if (passed.size() > 0 && failed.size() > 0)
	        {
	            resultRow = new String[3];
	            resultRow[0] = Float.toString(testCase);
	            String detail = "Passing threads: ";
	            for (int pass : passed)
	            {
	                detail += pass + " ";
	            }
	            detail += ";";
	            detail += " Failing threads: ";
	            for (int fail: failed)
	            {
	                detail += fail + " ";
	            }
	            detail += ".";
	            resultRow[1] = detail;
	            resultRow[2] = "";
	        }
	        if (maxTime - minTime > maxDiff)
	        {
	            long diff = maxTime - minTime;
	           if (resultRow == null)
	           {
	               resultRow = new String[3];
	               resultRow[0] = Float.toString(testCase);
	               resultRow[1] = "";
	           }
	           String detail = "Difference between shortest and longest execution time: " + (double)diff/(double)1000 + " seconds. ";
	           detail += "Shortest thread: " + shortThread + ", longest thread: " + longThread;
	           resultRow[2] = detail;
	        }
	        if (resultRow != null)
	        {
	            resultRows.add(resultRow);
	        }
	    }
	    
	    String reportDirName = TestProperties.getReportDir();
        File reportDir = new File(reportDirName);
        if (!reportDir.exists())
        {
            reportDir.mkdirs();
        }
        SimpleDateFormat df = new SimpleDateFormat("-yyyy-MM-dd-HH.mm.ss.SSS");
        String reportFilename = TestProperties.getLoadAnalysisFile() + df.format(Calendar.getInstance().getTime()) + ".csv";
        File reportFile = new File(reportFilename);
        if (!reportFile.exists())
        {
            reportFile.createNewFile();
        }
        
        Writer writer = new OutputStreamWriter(new FileOutputStream(reportFile));
        String headers = TestUtils.delimit(columnHeaders, DELIMITER);
        writer.write(headers);
        writer.write('\n');
        if (resultRows.isEmpty())
        {
            String detail = "No pass/fail or execution time discrepancies were found.";
            writer.write(detail);
            writer.write('\n');
        }
        else
        {
            for (String[] result : resultRows)
            {
                String results = TestUtils.delimit(result, DELIMITER);
                writer.write(results);
                writer.write('\n');
            } 
        }
        
        
        writer.close();
        System.out.println("Load test analysis written to: " + reportFilename);
	}
}
