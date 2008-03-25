package gov.nih.nci.cagrid.caarray.client;

import gov.nih.nci.caarray.domain.array.ArrayDesign;
import gov.nih.nci.caarray.domain.array.ArrayDesignDetails;
import gov.nih.nci.caarray.domain.data.DataRetrievalRequest;
import gov.nih.nci.caarray.domain.data.DataSet;
import gov.nih.nci.caarray.domain.data.QuantitationType;
import gov.nih.nci.caarray.domain.file.CaArrayFile;
import gov.nih.nci.caarray.domain.hybridization.Hybridization;
import gov.nih.nci.cagrid.caarray.common.CaArraySvcI;
import gov.nih.nci.cagrid.caarray.stubs.CaArraySvcPortType;
import gov.nih.nci.cagrid.caarray.stubs.service.CaArraySvcServiceAddressingLocator;
import gov.nih.nci.cagrid.cqlquery.Attribute;
import gov.nih.nci.cagrid.cqlquery.CQLQuery;
import gov.nih.nci.cagrid.cqlquery.Object;
import gov.nih.nci.cagrid.cqlquery.Predicate;
import gov.nih.nci.cagrid.cqlresultset.CQLQueryResults;
import gov.nih.nci.cagrid.data.faults.MalformedQueryExceptionType;
import gov.nih.nci.cagrid.data.faults.QueryProcessingExceptionType;
import gov.nih.nci.cagrid.data.utilities.CQLQueryResultsIterator;
import gov.nih.nci.cagrid.introduce.security.client.ServiceSecurityClient;

import java.io.InputStream;
import java.rmi.RemoteException;

import javax.xml.namespace.QName;

import org.apache.axis.EngineConfiguration;
import org.apache.axis.client.AxisClient;
import org.apache.axis.client.Stub;
import org.apache.axis.configuration.FileProvider;
import org.apache.axis.message.addressing.EndpointReferenceType;
import org.apache.axis.types.URI.MalformedURIException;
import org.globus.gsi.GlobusCredential;
import org.oasis.wsrf.properties.GetResourcePropertyResponse;

/**
 * This class is autogenerated, DO NOT EDIT GENERATED GRID SERVICE ACCESS METHODS.
 *
 * This client is generated automatically by Introduce to provide a clean unwrapped API to the
 * service.
 *
 * On construction the class instance will contact the remote service and retrieve it's security
 * metadata description which it will use to configure the Stub specifically for each method call.
 *
 * @created by Introduce Toolkit version 1.1
 */
public class CaArraySvcClient extends ServiceSecurityClient implements CaArraySvcI {
    protected CaArraySvcPortType portType;
    private Object portTypeMutex;

    public CaArraySvcClient(String url) throws MalformedURIException, RemoteException {
        this(url,null);
    }

    public CaArraySvcClient(String url, GlobusCredential proxy) throws MalformedURIException, RemoteException {
           super(url,proxy);
           initialize();
    }

    public CaArraySvcClient(EndpointReferenceType epr) throws MalformedURIException, RemoteException {
           this(epr,null);
    }

    public CaArraySvcClient(EndpointReferenceType epr, GlobusCredential proxy) throws MalformedURIException, RemoteException {
           super(epr,proxy);
        initialize();
    }

    private void initialize() throws RemoteException {
        this.portTypeMutex = new Object();
        this.portType = createPortType();
    }

    private CaArraySvcPortType createPortType() throws RemoteException {

        CaArraySvcServiceAddressingLocator locator = new CaArraySvcServiceAddressingLocator();
        // attempt to load our context sensitive wsdd file
        InputStream resourceAsStream = getClass().getResourceAsStream("client-config.wsdd");
        if (resourceAsStream != null) {
            // we found it, so tell axis to configure an engine to use it
            EngineConfiguration engineConfig = new FileProvider(resourceAsStream);
            // set the engine of the locator
            locator.setEngine(new AxisClient(engineConfig));
        }
        CaArraySvcPortType port = null;
        try {
            port = locator.getCaArraySvcPortTypePort(getEndpointReference());
        } catch (Exception e) {
            throw new RemoteException("Unable to locate portType:" + e.getMessage(), e);
        }

        return port;
    }

    public GetResourcePropertyResponse getResourceProperty(QName resourcePropertyQName) throws RemoteException {
        return portType.getResourceProperty(resourcePropertyQName);
    }

    public static void usage(){
        System.out.println(CaArraySvcClient.class.getName() + " -url <service url>");
    }

    public static void main(String [] args){
        System.out.println("Running the Grid Service Client");
        try{
        if(!(args.length < 2)){
            if(args[0].equals("-url")){
              CaArraySvcClient client = new CaArraySvcClient(args[1]);

              ArrayDesign arrayDesign = new ArrayDesign();
              arrayDesign.setId(1L);
              ArrayDesignDetails designDetails = client.getDesignDetails(arrayDesign );
              System.out.println("DesignDetails: " + designDetails);

              QuantitationType qt = (QuantitationType) get(client, "gov.nih.nci.caarray.domain.data.QuantitationType",
                      "name", "CELX");
              Hybridization hyb = (Hybridization) get(client, "gov.nih.nci.caarray.domain.hybridization.Hybridization",
                      "name", "H_TK6MDR1 replicate 1");

              DataRetrievalRequest drr = new DataRetrievalRequest();
              drr.getHybridizations().add(hyb);
              drr.addQuantitationType(qt);
              DataSet dataSet = client.getDataSet(drr);
              System.out.println(dataSet);

              CaArrayFile file = new CaArrayFile();
              file.setId(7L);
              byte[] bytes = client.readFile(file);
              System.out.println("Bytes: " + bytes);

              // place client calls here if you want to use this main as a
              // test....
            } else {
                usage();
                System.exit(1);
            }
        } else {
            usage();
            System.exit(1);
        }
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    public static java.lang.Object get(CaArraySvcClient client, String type, String attr, String value)
    throws QueryProcessingExceptionType, MalformedQueryExceptionType, RemoteException {
        CQLQuery cqlQuery = new CQLQuery();

        Object target = new Object();
        cqlQuery.setTarget(target);

        target.setName(type);
        Attribute attribute = new Attribute();
        attribute.setName(attr);
        attribute.setPredicate(Predicate.EQUAL_TO);
        attribute.setValue(value);
        target.setAttribute(attribute);

        CQLQueryResults results = client.query(cqlQuery);
        CQLQueryResultsIterator iter = new CQLQueryResultsIterator(results, CaArraySvcClient.class.getResourceAsStream("client-config.wsdd"));

        return iter.next();

    }

  public gov.nih.nci.caarray.domain.data.DataSet getDataSet(gov.nih.nci.caarray.domain.data.DataRetrievalRequest dataRetrievalRequest) throws RemoteException {
    synchronized(portTypeMutex){
      configureStubSecurity((Stub)portType,"getDataSet");
    gov.nih.nci.cagrid.caarray.stubs.GetDataSetRequest params = new gov.nih.nci.cagrid.caarray.stubs.GetDataSetRequest();
    gov.nih.nci.cagrid.caarray.stubs.GetDataSetRequestDataRetrievalRequest dataRetrievalRequestContainer = new gov.nih.nci.cagrid.caarray.stubs.GetDataSetRequestDataRetrievalRequest();
    dataRetrievalRequestContainer.setDataRetrievalRequest(dataRetrievalRequest);
    params.setDataRetrievalRequest(dataRetrievalRequestContainer);
    gov.nih.nci.cagrid.caarray.stubs.GetDataSetResponse boxedResult = portType.getDataSet(params);
    return boxedResult.getDataSet();
    }
  }

  public gov.nih.nci.cagrid.cqlresultset.CQLQueryResults query(gov.nih.nci.cagrid.cqlquery.CQLQuery cqlQuery) throws RemoteException, gov.nih.nci.cagrid.data.faults.QueryProcessingExceptionType, gov.nih.nci.cagrid.data.faults.MalformedQueryExceptionType {
    synchronized(portTypeMutex){
      configureStubSecurity((Stub)portType,"query");
    gov.nih.nci.cagrid.data.QueryRequest params = new gov.nih.nci.cagrid.data.QueryRequest();
    gov.nih.nci.cagrid.data.QueryRequestCqlQuery cqlQueryContainer = new gov.nih.nci.cagrid.data.QueryRequestCqlQuery();
    cqlQueryContainer.setCQLQuery(cqlQuery);
    params.setCqlQuery(cqlQueryContainer);
    gov.nih.nci.cagrid.data.QueryResponse boxedResult = portType.query(params);
    return boxedResult.getCQLQueryResultCollection();
    }
  }

  public gov.nih.nci.caarray.domain.array.ArrayDesignDetails getDesignDetails(gov.nih.nci.caarray.domain.array.ArrayDesign arrayDesign) throws RemoteException {
    synchronized(portTypeMutex){
      configureStubSecurity((Stub)portType,"getDesignDetails");
    gov.nih.nci.cagrid.caarray.stubs.GetDesignDetailsRequest params = new gov.nih.nci.cagrid.caarray.stubs.GetDesignDetailsRequest();
    gov.nih.nci.cagrid.caarray.stubs.GetDesignDetailsRequestArrayDesign arrayDesignContainer = new gov.nih.nci.cagrid.caarray.stubs.GetDesignDetailsRequestArrayDesign();
    arrayDesignContainer.setArrayDesign(arrayDesign);
    params.setArrayDesign(arrayDesignContainer);
    gov.nih.nci.cagrid.caarray.stubs.GetDesignDetailsResponse boxedResult = portType.getDesignDetails(params);
    return boxedResult.getArrayDesignDetails();
    }
  }

  public byte[] readFile(gov.nih.nci.caarray.domain.file.CaArrayFile caArrayFile) throws RemoteException {
    synchronized(portTypeMutex){
      configureStubSecurity((Stub)portType,"readFile");
    gov.nih.nci.cagrid.caarray.stubs.ReadFileRequest params = new gov.nih.nci.cagrid.caarray.stubs.ReadFileRequest();
    gov.nih.nci.cagrid.caarray.stubs.ReadFileRequestCaArrayFile caArrayFileContainer = new gov.nih.nci.cagrid.caarray.stubs.ReadFileRequestCaArrayFile();
    caArrayFileContainer.setCaArrayFile(caArrayFile);
    params.setCaArrayFile(caArrayFileContainer);
    gov.nih.nci.cagrid.caarray.stubs.ReadFileResponse boxedResult = portType.readFile(params);
    return boxedResult.getResponse();
    }
  }

}
