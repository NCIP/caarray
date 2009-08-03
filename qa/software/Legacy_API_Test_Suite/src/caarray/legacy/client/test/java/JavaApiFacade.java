/**
 * 
 */
package caarray.legacy.client.test.java;

import java.util.List;

import gov.nih.nci.caarray.domain.AbstractCaArrayObject;
import gov.nih.nci.caarray.services.CaArrayServer;
import gov.nih.nci.caarray.services.search.CaArraySearchService;
import caarray.legacy.client.test.ApiFacade;
import caarray.legacy.client.test.TestProperties;

/**
 * @author vaughng
 * Jul 31, 2009
 */
public class JavaApiFacade implements ApiFacade
{

    private CaArraySearchService javaSearchService = null;
    
    public JavaApiFacade()
    {}
    
    public void connect() throws Exception
    {

        String hostName = TestProperties.getJavaServerHostname();
        int port = TestProperties.getJavaServerJndiPort();
        System.out.println("Connecting to java server: " + hostName + ":" + port);
        CaArrayServer server = new CaArrayServer(hostName, port);
        server.connect();
        javaSearchService = server.getSearchService();   
    }

    /* (non-Javadoc)
     * @see caarray.legacy.client.test.ApiFacade#searchByExample(gov.nih.nci.caarray.domain.AbstractCaArrayObject)
     */
    public <T extends AbstractCaArrayObject> List<T> searchByExample(String api, T example)
    {
        return javaSearchService.search(example);
    }

    
    
}
