//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package caarray.client.test.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import caarray.client.test.TestConfigurationException;
import caarray.client.test.TestProperties;
import caarray.client.test.TestResult;
import caarray.client.test.TestResultReport;
import caarray.client.test.TestUtils;

/**
 * [Replace this line with description]
 * @author vaughng
 * @version $Revision$
 * Last depot modification: $DateTime$
 *
 * <!--Revision history:
 * Apr 30, 2010 vaughng: Initial version
 * -->
 */
public class ReportFileMerger
{

    private static final String DEFAULT_DIR = "C:" + File.separator + "caArray" + File.separator + "reports";
    private static Map<String, Integer> headerIndexMap = new HashMap<String, Integer>();
    private static TestResultReport mergedReport = new TestResultReport();
    private static boolean[] includedTests = new boolean[369];
    public static void main(String[] args)
    {
        try
        {
        String dirName = DEFAULT_DIR;
        if (args != null && args.length > 0)
        {
            dirName = args[0];
        }
        
        File dir = new File(dirName);
        if (!dir.exists())
        {
            System.out.println("Invalid directory: " + dir);
            System.exit(0);
        }
        
        File[] reports = dir.listFiles();
        
        if (reports == null || reports.length == 0)
        {
            System.out.println("No reports to merge.");
            System.exit(0);
        }
        
        //SimpleDateFormat df = new SimpleDateFormat("-yyyy-MM-dd-HH.mm.ss.SSS");
        /*String reportFilename = "MergedReports" + df.format(Calendar.getInstance().getTime()) + ".csv";
        File reportFile = new File(reportFilename);
        if (!reportFile.exists())
        {
            reportFile.createNewFile();
        }*/
        
        
        for (File file : reports)
        {
            if (file.isDirectory())
                continue;
            
            BufferedReader reader = new BufferedReader(new FileReader(file));
            
            List<String> spreadsheetRows = new ArrayList<String>();
            String row;
            while ((row = reader.readLine()) != null)
            {
                spreadsheetRows.add(row);
            }
            if (spreadsheetRows.size() > 1)
            {
                
                if (headerIndexMap.isEmpty())
                {
                    String[] columnHeaders = TestUtils.split(spreadsheetRows.get(0),
                    ",");
                    populateHeaderIndexMap(columnHeaders);
                }
                    
                constructReportList(spreadsheetRows);
            }
        }
        
        TestProperties.setReportDir(dirName + File.separator + "merged");
        mergedReport.writeReport();
        System.out.println("Excluded tests: ");
        for (int i = 0; i < includedTests.length; i++)
        {
            if (!includedTests[i])
            {
                System.out.print(i + ",");
            }
        }
        String[] longTests = TestProperties.TEST_CASE_LONG.split(",");
        Set<String> longTestSet = new HashSet<String>();
        for (String t : longTests)
        {
            longTestSet.add(t);
        }
        System.out.println("\nExcluded long tests: ");
        for (int i = 0; i < includedTests.length; i++)
        {
            if (!includedTests[i] && longTestSet.contains(Integer.toString(i)))
            {
                System.out.print(i + ",");
            }
        }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    
    protected static void populateHeaderIndexMap(String[] columnHeaders)
            throws TestConfigurationException
    {
        if (columnHeaders != null)
        {
            for (int i = 0; i < columnHeaders.length; i++)
            {
                for (String header : TestResultReport.COLUMN_HEADERS)
                {
                    if (columnHeaders[i].trim().equalsIgnoreCase(header))
                    {
                        headerIndexMap.put(header, i);
                        break;
                    }
                }
            }
        }
    }
    
    protected static void constructReportList(List<String> spreadsheetRows)
    {
        for (int i = 1; i < spreadsheetRows.size();i++)
        {
            TestResult result = new TestResult();
            String[] input = TestUtils.split(spreadsheetRows.get(i),",");
            int testCase = (int) Math.floor(Float.parseFloat(input[headerIndexMap.get(TestResultReport.TEST_CASE)]));
            if (testCase < includedTests.length)
                includedTests[testCase] = true;
            
            result.setTestCase(Float.parseFloat(input[headerIndexMap.get(TestResultReport.TEST_CASE)]));
            result.setElapsedTime(Long.parseLong(input[headerIndexMap.get(TestResultReport.TIME)].split("\\s")[0]));
            result.setPassed(input[headerIndexMap.get(TestResultReport.STATUS)].equalsIgnoreCase("passed"));
            result.setThreadId(Integer.parseInt(input[headerIndexMap.get(TestResultReport.THREAD)]));
            if (input.length > headerIndexMap.get(TestResultReport.DETAILS))
                result.addDetail(input[headerIndexMap.get(TestResultReport.DETAILS)]);
            mergedReport.addTestResult(result);
        }
        
        
    }
}
