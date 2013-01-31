//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package caarray.legacy.client.test;

/**
 * @author vaughng
 * Jul 31, 2009
 */
public class TestResult
{

    private float testCase = 0.0f;
    private boolean passed = true;
    private String details;
    private long elapsedTime = 0;
    
    public TestResult()
    {}

    public boolean isPassed() {
        return passed;
    }

    public void setPassed(boolean passed) {
        this.passed = passed;
    }

    public String getDetails() {
        return details;
    }

    public void addDetail(String detail) {
        if (this.details == null)
            this.details = detail;
        else
            this.details += "; " + detail;
    }

    public long getElapsedTime() {
        return elapsedTime;
    }

    public void setElapsedTime(long elapsedTime) {
        this.elapsedTime = elapsedTime;
    }

    public float getTestCase() {
        return testCase;
    }

    public void setTestCase(float testCase) {
        this.testCase = testCase;
    }
    
    
}
