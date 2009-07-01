/**
 * 
 */
package caarray.client.test.java;

import caarray.client.test.TestMain;

/**
 * Runs test suites against the caArray java API.
 * 
 * @author vaughng
 *
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
            test.runTests(new JavaApiFacade());
        }
        catch (Throwable t)
        {
            System.out.println("An unexpected error occurred during test execution.");
            t.printStackTrace();
        }
    }

}