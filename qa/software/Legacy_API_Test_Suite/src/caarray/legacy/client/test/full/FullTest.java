/**
 * 
 */
package caarray.legacy.client.test.full;

import caarray.legacy.client.test.TestMain;

/**
 * @author vaughng
 * Jul 31, 2009
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