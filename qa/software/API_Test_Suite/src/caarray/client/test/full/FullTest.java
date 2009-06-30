/**
 * 
 */
package caarray.client.test.full;

import caarray.client.test.TestMain;

/**
 * Runs the test suite against both the grid and java APIs.
 * 
 * @author vaughng
 *
 */
public class FullTest
{

    /**
     * @param args
     */
    public static void main(String[] args)
    {
        TestMain test = new TestMain();
        try
        {
            test.runTests(new FullApiFacade());
        }
        catch (Throwable t)
        {
            System.out.println("An unexpected error occurred during test execution.");
            t.printStackTrace();
        }

    }

}
