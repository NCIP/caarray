//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package caarray.client.test;

/**
 * Encapsulates the results of an individual test.
 * 
 * @author vaughng
 * Jun 25, 2009
 */
public class TestResult {

	private float testCase = 0.0f;
	private boolean passed = true;
	private String details;
	private long elapsedTime = 0;
	private int threadId = 0;
	
	public TestResult()
	{}

	/**
	 * Indicates whether a test passed or failed.
	 * @return true if the test passed, false otherwise.
	 */
	public boolean isPassed() {
		return passed;
	}

	/**
	 * Set the indicator for test pass/fail status.
	 * @param passed true if the test passed, false otherwise.
	 */
	public void setPassed(boolean passed) {
		this.passed = passed;
	}

	/**
	 * Details of the test's status, such as why it passed or failed.
	 * @return Details of the test's status, such as why it passed or failed.
	 */
	public String getDetails() {
		return details;
	}

	/**
	 * Append text to this test's status details.
	 * @param detail Text to be added to this test's status details.
	 */
	public void addDetail(String detail) {
		if (this.details == null)
			this.details = detail;
		else
			this.details += "; " + detail;
	}

	/**
	 * The time it took for this test to execute, in milliseconds.
	 * @return The time it took for this test to execute, in milliseconds.
	 */
	public long getElapsedTime() {
		return elapsedTime;
	}

	/**
	 * Set the time it took for this test to execute, in milliseconds.
	 * @param elapsedTime The time it took for this test to execute, in milliseconds.
	 */
	public void setElapsedTime(long elapsedTime) {
		this.elapsedTime = elapsedTime;
	}
 
	/**
	 * Test case as specified in the API backlog to which this test corresponds.
	 * @return Test case as specified in the API backlog to which this test corresponds.
	 */
	public float getTestCase() {
    	return testCase;
    }

	/**
	 * Set the test case as specified in the API backlog to which this test corresponds.
	 * @param testCase Test case as specified in the API backlog to which this test corresponds.
	 */
    public void setTestCase(float testCase) {
    	this.testCase = testCase;
    }

    /**
     * For load testing, the thread number in which this test was executed.
     * @return the thread number in which this test was executed.
     */
    public int getThreadId()
    {
        return threadId;
    }

    /**
     * Set the thread number in which this test will be executed.
     * @param thread the thread number in which this test will be executed.
     */
    public void setThreadId(int thread)
    {
        this.threadId = thread;
    }
	
	
}
