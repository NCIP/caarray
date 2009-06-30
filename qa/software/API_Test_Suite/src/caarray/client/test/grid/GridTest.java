/**
 * 
 */
package caarray.client.test.grid;

import caarray.client.test.TestMain;

/**
 * Runs test suites against the caArray grid API.
 * 
 * @author vaughng
 *
 */
public class GridTest
{

    /**
     * @param args
     */
    public static void main(String[] args)
    {
        TestMain test = new TestMain();
        try
        {
            test.runTests(new GridApiFacade()); 
        }
        catch (Throwable t)
        {
            System.out.println("An unexpected error occurred during test execution.");
            t.printStackTrace();
        }
        
    }

}
