/**
 * 
 */
package caarray.legacy.client.test.full;

import gov.nih.nci.caarray.domain.AbstractCaArrayObject;
import gov.nih.nci.caarray.services.ServerConnectionException;

import java.rmi.RemoteException;
import java.util.List;

import org.apache.axis.types.URI.MalformedURIException;

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
    
    
    public FullApiFacade() throws ServerConnectionException, RemoteException, MalformedURIException
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
    public <T extends AbstractCaArrayObject> List<T> searchByExample(String api, T example)
    {
        return getFacade(api).searchByExample(api, example);
    }
}
