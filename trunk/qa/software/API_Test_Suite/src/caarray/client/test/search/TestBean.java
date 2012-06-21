/**
 * 
 */
package caarray.client.test.search;

/**
 * Base class for beans encapsulating the details of an individual test case.
 * 
 * @author vaughng
 *
 */
public abstract class TestBean
{

    protected Float testCase = null;
    protected String api = null;
    protected Integer expectedResults = null;
    protected Integer minResults = null;

    public Float getTestCase()
    {
        return testCase;
    }

    public void setTestCase(Float testCase)
    {
        this.testCase = testCase;
    }

    public String getApi()
    {
        return api;
    }

    public void setApi(String api)
    {
        this.api = api;
    }

    public Integer getExpectedResults()
    {
        return expectedResults;
    }

    public void setExpectedResults(Integer expectedResults)
    {
        this.expectedResults = expectedResults;
    }

    public Integer getMinResults()
    {
        return minResults;
    }

    public void setMinResults(Integer minResults)
    {
        this.minResults = minResults;
    }

}
