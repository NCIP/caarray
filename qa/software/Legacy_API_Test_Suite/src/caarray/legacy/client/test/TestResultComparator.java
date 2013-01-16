//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
package caarray.legacy.client.test;

import java.util.Comparator;

/**
 * @author vaughng
 * Jul 31, 2009
 */
public class TestResultComparator implements Comparator<TestResult> 
{

    
    public int compare(TestResult o1, TestResult o2) {
        if (new Float(o1.getTestCase()).compareTo(o2.getTestCase()) == 0)
            return 1;
        
        return new Float(o1.getTestCase()).compareTo(o2.getTestCase());
    }

    
}
