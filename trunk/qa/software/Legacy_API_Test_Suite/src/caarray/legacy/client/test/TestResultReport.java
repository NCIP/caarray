//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package caarray.legacy.client.test;

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
import java.util.Set;
import java.util.TreeSet;

/**
 * @author vaughng
 * Jul 31, 2009
 */
public class TestResultReport
{

    private Set<TestResult> results = new TreeSet<TestResult>(new TestResultComparator());
    private List<String> errorMessages = new ArrayList<String>();
    
    
    private static final String[] COLUMN_HEADERS = new String[]{"Test Case", "Test Status", "Elapsed Time","Details"};
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
        SimpleDateFormat df = new SimpleDateFormat("-yyyy-MM-dd-HH.mm");
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
            String[] resultArray = new String[]{Float.toString(result.getTestCase()),
                    (result.isPassed() ? "passed" : "failed"), Long.toString(result.getElapsedTime()) + " ms",result.getDetails()};
            String results = TestUtils.delimit(resultArray, DELIMITER);
            writer.write(results);
            writer.write('\n');
        }
        
        writer.close();
        System.out.println("Test result report written to: " + reportFilename);
        
    }
}
