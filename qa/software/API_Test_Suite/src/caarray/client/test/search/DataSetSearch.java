/**
 * 
 */
package caarray.client.test.search;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import gov.nih.nci.caarray.external.v1_0.data.DataType;
import gov.nih.nci.caarray.external.v1_0.query.DataSetRequest;

/**
 * @author vaughng
 * Jul 13, 2009
 */
public class DataSetSearch extends CriteriaSearch
{

    private DataSetRequest dataSetRequest = null;
    private Integer expectedProbeIds = null;
    private List<String> expectedQuantitationTypes = new ArrayList<String>();
    private Map<String, DataType> expectedDataType = new HashMap<String, DataType>();
    private Map<String,Integer> expectedDataResults = new HashMap<String, Integer>();
    
    /**
     * 
     */
    public DataSetSearch()
    {
        super();
    }

    public DataSetRequest getDataSetRequest()
    {
        return dataSetRequest;
    }

    public void setDataSetRequest(DataSetRequest dataSetRequest)
    {
        this.dataSetRequest = dataSetRequest;
    }

    public Integer getExpectedProbeIds()
    {
        return expectedProbeIds;
    }

    public void setExpectedProbeIds(Integer expectedProbeIds)
    {
        this.expectedProbeIds = expectedProbeIds;
    }

    public List<String> getExpectedQuantitationTypes()
    {
        return expectedQuantitationTypes;
    }
    
    public void addExpectedQuantitationType(String type)
    {
        expectedQuantitationTypes.add(type);
    }

    public Map<String, DataType> getExpectedDataType()
    {
        return expectedDataType;
    }

    public Map<String, Integer> getExpectedDataResults()
    {
        return expectedDataResults;
    }

}