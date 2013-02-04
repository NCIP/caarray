//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package caarray.client.test.full;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import caarray.client.test.TestMain;

/**
 * Runs the test suite against both the grid and java APIs.
 * 
 * @author vaughng
 *
 */
public class FullTest
{
    private static final Log log = LogFactory.getLog(FullTest.class);
    
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
            log.error("Exception encountered:",t);
        }

    }

}
