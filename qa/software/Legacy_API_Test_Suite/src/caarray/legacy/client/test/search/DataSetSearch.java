//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
package caarray.legacy.client.test.search;

import gov.nih.nci.caarray.domain.data.DataRetrievalRequest;
import gov.nih.nci.caarray.domain.data.DataType;

/**
 * @author vaughng
 * Aug 14, 2009
 */
public class DataSetSearch
{

    private DataRetrievalRequest request;
    private Float testCase;
    private String api;
    private Integer expectedProbeIds, expectedDataResults;
    private DataType expectedDataType;
    
    public DataSetSearch(){}

    /**
     * @return the request
     */
    public DataRetrievalRequest getRequest()
    {
        return request;
    }

    /**
     * @param request the request to set
     */
    public void setRequest(DataRetrievalRequest request)
    {
        this.request = request;
    }

    /**
     * @return the testCase
     */
    public Float getTestCase()
    {
        return testCase;
    }

    /**
     * @param testCase the testCase to set
     */
    public void setTestCase(Float testCase)
    {
        this.testCase = testCase;
    }

    /**
     * @return the api
     */
    public String getApi()
    {
        return api;
    }

    /**
     * @param api the api to set
     */
    public void setApi(String api)
    {
        this.api = api;
    }

    /**
     * @return the expectedProbeIds
     */
    public Integer getExpectedProbeIds()
    {
        return expectedProbeIds;
    }

    /**
     * @param expectedProbeIds the expectedProbeIds to set
     */
    public void setExpectedProbeIds(Integer expectedProbeIds)
    {
        this.expectedProbeIds = expectedProbeIds;
    }

    /**
     * @return the expectedValues
     */
    public Integer getExpectedDataResults()
    {
        return expectedDataResults;
    }

    /**
     * @param expectedValues the expectedValues to set
     */
    public void setExpectedDataResults(Integer expectedValues)
    {
        this.expectedDataResults = expectedValues;
    }

    /**
     * @return the expectedDataType
     */
    public DataType getExpectedDataType()
    {
        return expectedDataType;
    }

    /**
     * @param expectedDataType the expectedDataType to set
     */
    public void setExpectedDataType(DataType expectedDataType)
    {
        this.expectedDataType = expectedDataType;
    }
    
    
}
