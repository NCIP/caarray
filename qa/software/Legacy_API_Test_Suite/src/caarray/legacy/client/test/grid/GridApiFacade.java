/**
 * 
 */
package caarray.legacy.client.test.grid;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.cagrid.transfer.context.client.TransferServiceContextClient;
import org.cagrid.transfer.context.client.helper.TransferClientHelper;
import org.cagrid.transfer.context.stubs.types.TransferServiceContextReference;

import gov.nih.nci.caarray.domain.AbstractCaArrayObject;
import gov.nih.nci.caarray.domain.array.ArrayDesign;
import gov.nih.nci.caarray.domain.array.ArrayDesignDetails;
import gov.nih.nci.caarray.domain.data.DataRetrievalRequest;
import gov.nih.nci.caarray.domain.data.DataSet;
import gov.nih.nci.caarray.domain.file.CaArrayFile;
import gov.nih.nci.cagrid.caarray.client.CaArraySvcClient;
import gov.nih.nci.cagrid.cqlquery.CQLQuery;
import caarray.legacy.client.test.ApiFacade;
import caarray.legacy.client.test.TestProperties;

/**
 * @author vaughng
 * Jul 31, 2009
 */
public class GridApiFacade implements ApiFacade
{

    private CaArraySvcClient gridClient = null;
    
    public GridApiFacade(){}
    
    public void connect() throws Exception
    {
        String gridUrl = TestProperties.getGridServiceUrl();
        System.out.println("Connecting to grid service: " + gridUrl);
        gridClient = new CaArraySvcClient(gridUrl);   
    }

    /* (non-Javadoc)
     * @see caarray.legacy.client.test.ApiFacade#searchByExample(java.lang.String, gov.nih.nci.caarray.domain.AbstractCaArrayObject)
     */
    public <T extends AbstractCaArrayObject> List<T> searchByExample(
            String api, T example)
    {
        //TODO: handle this for grid api?
        return new ArrayList<T>();
    }

    /* (non-Javadoc)
     * @see caarray.legacy.client.test.ApiFacade#query(java.lang.String, gov.nih.nci.cagrid.cqlquery.CQLQuery)
     */
    public Object query(String api, CQLQuery cqlQuery)
            throws Exception
    {
        return gridClient.query(cqlQuery);
    }

    /* (non-Javadoc)
     * @see caarray.legacy.client.test.ApiFacade#getDataSet(java.lang.String, gov.nih.nci.caarray.domain.data.DataRetrievalRequest)
     */
    public DataSet getDataSet(String api, DataRetrievalRequest request)
            throws Exception
    {
        return gridClient.getDataSet(request);
    }

    /* (non-Javadoc)
     * @see caarray.legacy.client.test.ApiFacade#getArrayDesignDetails(java.lang.String, gov.nih.nci.caarray.domain.array.ArrayDesign)
     */
    public ArrayDesignDetails getArrayDesignDetails(String api,
            ArrayDesign arrayDesign) throws Exception
    {
        return gridClient.getDesignDetails(arrayDesign);
    }

    /* (non-Javadoc)
     * @see caarray.legacy.client.test.ApiFacade#readFile(java.lang.String, gov.nih.nci.caarray.domain.file.CaArrayFile)
     */
    public byte[] readFile(String api, CaArrayFile file) throws Exception
    {
        TransferServiceContextReference serviceContextRef = gridClient.createFileTransfer(file);
        TransferServiceContextClient transferClient = new TransferServiceContextClient(serviceContextRef.getEndpointReference());
        InputStream stream = TransferClientHelper.getData(transferClient.getDataTransferDescriptor());
        return IOUtils.toByteArray(stream);
     }
}
