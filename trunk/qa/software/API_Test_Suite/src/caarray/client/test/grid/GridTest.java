//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package caarray.client.test.grid;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import caarray.client.test.TestMain;
import caarray.client.test.TestProperties;
import caarray.client.test.full.FullTest;

/**
 * Runs test suites against the caArray grid API.
 * 
 * @author vaughng
 *
 */
public class GridTest
{
    private static final Log log = LogFactory.getLog(GridTest.class);
    
    /**
     * @param args
     */
    public static void main(String[] args)
    {
        TestMain test = new TestMain();
        try
        {
            System.setProperty(TestProperties.API_KEY, TestProperties.API_GRID);
            test.runTests(new GridApiFacade()); 
        }
        catch (Throwable t)
        {
            System.out.println("An unexpected error occurred during test execution.");
            t.printStackTrace();
            log.error("Exception encountered:",t);
        }
        
    }

}
