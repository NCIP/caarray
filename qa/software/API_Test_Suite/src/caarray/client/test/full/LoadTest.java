//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package caarray.client.test.full;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import caarray.client.test.ApiFacade;
import caarray.client.test.TestMain;
import caarray.client.test.TestProperties;
import caarray.client.test.TestResultReport;
import caarray.client.test.suite.ConfigurableTestSuite;

/**
 * Main class executed when load testing is initiated via the build script.
 * 
 * @author vaughng
 * Aug 26, 2009
 */
public class LoadTest
{
    private static final Log log = LogFactory.getLog(LoadTest.class);
    
    private static final int DEFAULT_THREADS = 5;
    /**
     * @param args
     */
    public static void main(String[] args)
    {
        List<ApiFacade> apiFacades = new ArrayList<ApiFacade>();
        List<List<ConfigurableTestSuite>> testSuiteCollection = new ArrayList<List<ConfigurableTestSuite>>();
        int numThreads = TestProperties.getNumThreads();
        if (numThreads <= 1)
        {
            System.out.println("Thread count for load test set to 1 - setting to default count of " + DEFAULT_THREADS);
            numThreads = DEFAULT_THREADS;
        }
        try
        {
            for (int i = apiFacades.size(); i < numThreads; i++)
            {
                apiFacades.add(new FullApiFacade());
            }
            for (int i = testSuiteCollection.size(); i < numThreads; i++)
            {
                List<ConfigurableTestSuite> shortTests = TestMain.getShortTestSuites(apiFacades.get(i));
                List<ConfigurableTestSuite> longTests = TestMain.getLongTestSuites(apiFacades.get(i));
                List<ConfigurableTestSuite> testSuites = new ArrayList<ConfigurableTestSuite>();
                testSuites.addAll(shortTests);
                testSuites.addAll(longTests);
                testSuiteCollection.add(testSuites);
            }
            TestResultReport[] threadReports = new TestResultReport[numThreads];
            for (int i = 0; i < numThreads; i++)
            {
                threadReports[i] = new TestResultReport();
            }
            Thread[] loadTestThreads = new Thread[numThreads];
            for (int i = 0; i < numThreads; i++)
            {
                LoadTestThread thread = new LoadTestThread(apiFacades.get(i),testSuiteCollection.get(i),
                        threadReports[i],i);
                loadTestThreads[i] = new Thread(thread);
            }
            System.out.println("Executing load tests for " + numThreads + " threads ...");
            long start = System.currentTimeMillis();
            for (int i = 0; i < numThreads; i++)
            {
                loadTestThreads[i].start();
            }
            for (int i = 0; i < numThreads; i++)
            {
                loadTestThreads[i].join();
            }
            long time = System.currentTimeMillis() - start;
            System.out.println("Load tests completed in " + (double)time/(double)1000 + " seconds.");
            
            TestResultReport finalReport = new TestResultReport();
            for (TestResultReport report : threadReports)
            {
                finalReport.merge(report);
            }
            System.out.println("Analyzing load test results ...");
            finalReport.writeLoadTestReports();
        }
        catch (Throwable t)
        {
            System.out.println("An exception occured execuitng the load tests: " + t.getClass());
            System.out.println("Test suite aborted.");
            t.printStackTrace();
            log.error("Exception encountered:",t);
        }
        
        
    }
    
    static class LoadTestThread implements Runnable
    {
        private ApiFacade apiFacade;
        private List<ConfigurableTestSuite> testSuites;
        private TestResultReport resultReport;
        private int thread;
        public LoadTestThread(ApiFacade apiFacade, List<ConfigurableTestSuite> testSuites, TestResultReport resultReport, int thread)
        {
            this.apiFacade = apiFacade;
            this.testSuites = testSuites;
            this.resultReport = resultReport;
            this.thread = thread;
        }
        /* (non-Javadoc)
         * @see java.lang.Runnable#run()
         */
        public void run()
        {
            
            try
            {
                apiFacade.connect();
                for (int i = 0; i < testSuites.size(); i++)
                {
                    testSuites.get(i).runTests(resultReport);
                    
                }
                resultReport.setThreadId(thread);
                
            }
            catch (Throwable t)
            {
                System.out.println("An exception occured in thread " + thread + ": " + t.getClass());
                System.out.println("Test suite aborted.");
                t.printStackTrace();
                log.error("Exception encountered:",t);
            }
            
        }
        
        
    }
    

}
