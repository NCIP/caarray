//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
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
    private String confFile;
    private String api;
    private Throwable t;
    


    public void setThrowable(Throwable t){
    	this.t = t;
    }
    
    public Throwable getThrowable(){
    	return this.t;
    }
    
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
    
    public void setConfFile(String confFile){
    	this.confFile = confFile;
    }
    
    public String getConfFile(){
    	return this.confFile;
    }
    
    public void setType(String type){
    	this.api = type;
    }
    
    public String getType(){
    	return this.api;
    }
}
