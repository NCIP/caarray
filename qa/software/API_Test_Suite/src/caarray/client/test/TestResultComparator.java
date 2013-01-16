//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
package caarray.client.test;

import java.util.Comparator;

/**
 * Compares TestResults numerically by test case.
 * @author vaughng
 * Jun 26, 2009
 */
public class TestResultComparator implements Comparator<TestResult> 
{

	
	public int compare(TestResult o1, TestResult o2) {
	    if (new Float(o1.getTestCase()).compareTo(o2.getTestCase()) == 0)
	    {
	        //Allow multiple instances of a single test case.
	        if (new Integer(o1.getThreadId()).compareTo(o2.getThreadId()) == 0)
	        {
	            return 1;
	        }
	        return new Integer(o1.getThreadId()).compareTo(o2.getThreadId());
	    }
	    
		return new Float(o1.getTestCase()).compareTo(o2.getTestCase());
	}

	
}
