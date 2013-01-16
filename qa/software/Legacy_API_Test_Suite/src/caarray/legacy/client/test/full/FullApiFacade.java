//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
package caarray.legacy.client.test.full;

import gov.nih.nci.caarray.domain.AbstractCaArrayObject;
import gov.nih.nci.caarray.domain.array.ArrayDesign;
import gov.nih.nci.caarray.domain.array.ArrayDesignDetails;
import gov.nih.nci.caarray.domain.data.DataRetrievalRequest;
import gov.nih.nci.caarray.domain.data.DataSet;
import gov.nih.nci.caarray.domain.file.CaArrayFile;
import gov.nih.nci.cagrid.cqlquery.CQLQuery;

import java.util.List;

import caarray.legacy.client.test.ApiFacade;
import caarray.legacy.client.test.TestProperties;
import caarray.legacy.client.test.grid.GridApiFacade;
import caarray.legacy.client.test.java.JavaApiFacade;

/**
 * @author vaughng
 * Jul 31, 2009
 */
public class FullApiFacade implements ApiFacade
{

    private JavaApiFacade javaApiFacade;
    private GridApiFacade gridApiFacade;
    
    
    public FullApiFacade()
    {
        javaApiFacade = new JavaApiFacade();
        gridApiFacade = new GridApiFacade();
    }
    
    public void connect() throws Exception
    {
        javaApiFacade.connect();
        gridApiFacade.connect();
    }
    
    private ApiFacade getFacade(String api)
    {
        if (api.equalsIgnoreCase(TestProperties.API_JAVA))
            return javaApiFacade;
        if (api.equalsIgnoreCase(TestProperties.API_GRID))
            return gridApiFacade;
        
        System.out.println("No api specified for FullApiFacade ...");
        return null;
    }

    /* (non-Javadoc)
     * @see caarray.legacy.client.test.ApiFacade#searchByExample(gov.nih.nci.caarray.domain.AbstractCaArrayObject)
     */
    public <T extends AbstractCaArrayObject> List<T> searchByExample(String api, T example, boolean login) throws Exception
    {
        return getFacade(api).searchByExample(api, example, login);
    }

    /* (non-Javadoc)
     * @see caarray.legacy.client.test.ApiFacade#query(java.lang.String, gov.nih.nci.cagrid.cqlquery.CQLQuery)
     */
    public Object query(String api, CQLQuery cqlQuery)
            throws Exception
    {
        return getFacade(api).query(api, cqlQuery);
    }

    /* (non-Javadoc)
     * @see caarray.legacy.client.test.ApiFacade#getDataSet(java.lang.String, gov.nih.nci.caarray.domain.data.DataRetrievalRequest)
     */
    public DataSet getDataSet(String api, DataRetrievalRequest request)
            throws Exception
    {
        return getFacade(api).getDataSet(api, request);
    }

    /* (non-Javadoc)
     * @see caarray.legacy.client.test.ApiFacade#getArrayDesignDetails(java.lang.String, gov.nih.nci.caarray.domain.array.ArrayDesign)
     */
    public ArrayDesignDetails getArrayDesignDetails(String api,
            ArrayDesign arrayDesign) throws Exception
    {
        return getFacade(api).getArrayDesignDetails(api, arrayDesign);
    }

    /* (non-Javadoc)
     * @see caarray.legacy.client.test.ApiFacade#readFile(java.lang.String, gov.nih.nci.caarray.domain.file.CaArrayFile)
     */
    public byte[] readFile(String api, CaArrayFile file) throws Exception
    {
        return getFacade(api).readFile(api, file);
    }
    
    
}
