/**
 * 
 */
package caarray.legacy.client.test.java;

import caarray.legacy.client.test.TestMain;
import caarray.legacy.client.test.TestProperties;

/**
 * @author vaughng
 * Jul 31, 2009
 */
public class JavaTest
{

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
        }
    }

}
