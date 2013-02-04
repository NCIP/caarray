//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
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
