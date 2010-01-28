/**
 * 
 */
package caarray.client.test.java;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import caarray.client.test.TestMain;
import caarray.client.test.TestProperties;
import caarray.client.test.full.FullTest;

/**
 * Runs test suites against the caArray java API.
 * 
 * @author vaughng
 *
 */
public class JavaTest
{
    private static final Log log = LogFactory.getLog(JavaTest.class);
    
    /**
     * @param args
     */
    public static void main(String[] args)
    {
        TestMain test = new TestMain();
        try
        {
            System.setProperty(TestProperties.API_KEY, TestProperties.API_JAVA);
            test.runTests(new JavaApiFacade());
        }
        catch (Throwable t)
        {
            System.out.println("An unexpected error occurred during test execution.");
            t.printStackTrace();
            log.error("Exception encountered:",t);
        }
    }

}