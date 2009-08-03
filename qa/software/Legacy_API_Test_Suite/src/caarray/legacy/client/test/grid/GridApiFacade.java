/**
 * 
 */
package caarray.legacy.client.test.grid;

import java.util.ArrayList;
import java.util.List;

import gov.nih.nci.caarray.domain.AbstractCaArrayObject;
import gov.nih.nci.cagrid.caarray.client.CaArraySvcClient;
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
}
