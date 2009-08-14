/**
 * 
 */
package caarray.legacy.client.test.java;

import gov.nih.nci.caarray.domain.AbstractCaArrayObject;
import gov.nih.nci.caarray.domain.array.ArrayDesign;
import gov.nih.nci.caarray.domain.array.ArrayDesignDetails;
import gov.nih.nci.caarray.domain.data.DataRetrievalRequest;
import gov.nih.nci.caarray.domain.data.DataSet;
import gov.nih.nci.caarray.domain.file.CaArrayFile;
import gov.nih.nci.caarray.services.CaArrayServer;
import gov.nih.nci.caarray.services.arraydesign.ArrayDesignDetailsService;
import gov.nih.nci.caarray.services.data.DataRetrievalService;
import gov.nih.nci.caarray.services.file.FileRetrievalService;
import gov.nih.nci.caarray.services.search.CaArraySearchService;
import gov.nih.nci.cagrid.cqlquery.CQLQuery;

import java.util.List;

import caarray.legacy.client.test.ApiFacade;
import caarray.legacy.client.test.TestProperties;

/**
 * @author vaughng
 * Jul 31, 2009
 */
public class JavaApiFacade implements ApiFacade
{

    private CaArraySearchService javaSearchService = null;
    private DataRetrievalService dataService = null;
    private ArrayDesignDetailsService arrayDesignService = null;
    private FileRetrievalService fileRetrievalService = null;
    
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
        dataService = server.getDataRetrievalService();
        arrayDesignService = server.getArrayDesignDetailsService();
        fileRetrievalService = server.getFileRetrievalService();
    }

    /* (non-Javadoc)
     * @see caarray.legacy.client.test.ApiFacade#searchByExample(gov.nih.nci.caarray.domain.AbstractCaArrayObject)
     */
    public <T extends AbstractCaArrayObject> List<T> searchByExample(String api, T example)
    {
        return javaSearchService.search(example);
    }

    /* (non-Javadoc)
     * @see caarray.legacy.client.test.ApiFacade#query(java.lang.String, gov.nih.nci.cagrid.cqlquery.CQLQuery)
     */
    public Object query(String api, CQLQuery cqlQuery)
            throws Exception
    {
        return javaSearchService.search(cqlQuery);
    }

    /* (non-Javadoc)
     * @see caarray.legacy.client.test.ApiFacade#getDataSet(java.lang.String, gov.nih.nci.caarray.domain.data.DataRetrievalRequest)
     */
    public DataSet getDataSet(String api, DataRetrievalRequest request)
            throws Exception
    {
        return dataService.getDataSet(request);
    }

    /* (non-Javadoc)
     * @see caarray.legacy.client.test.ApiFacade#getArrayDesignDetails(java.lang.String, gov.nih.nci.caarray.domain.array.ArrayDesign)
     */
    public ArrayDesignDetails getArrayDesignDetails(String api,
            ArrayDesign arrayDesign) throws Exception
    {
        return arrayDesignService.getDesignDetails(arrayDesign);
    }

    /* (non-Javadoc)
     * @see caarray.legacy.client.test.ApiFacade#readFile(java.lang.String, gov.nih.nci.caarray.domain.file.CaArrayFile)
     */
    public byte[] readFile(String api, CaArrayFile file) throws Exception
    {
        return fileRetrievalService.readFile(file);
    }

    
    
}
