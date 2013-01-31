//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.cagrid.caarray.client;

import gov.nih.nci.caarray.domain.SerializationHelperUtility;
import gov.nih.nci.caarray.domain.array.AbstractDesignElement;
import gov.nih.nci.caarray.domain.array.ArrayDesign;
import gov.nih.nci.caarray.domain.array.ArrayDesignDetails;
import gov.nih.nci.caarray.domain.data.AbstractDataColumn;
import gov.nih.nci.caarray.domain.data.BooleanColumn;
import gov.nih.nci.caarray.domain.data.DataRetrievalRequest;
import gov.nih.nci.caarray.domain.data.DataSet;
import gov.nih.nci.caarray.domain.data.DesignElementList;
import gov.nih.nci.caarray.domain.data.DoubleColumn;
import gov.nih.nci.caarray.domain.data.FloatColumn;
import gov.nih.nci.caarray.domain.data.HybridizationData;
import gov.nih.nci.caarray.domain.data.IntegerColumn;
import gov.nih.nci.caarray.domain.data.LongColumn;
import gov.nih.nci.caarray.domain.data.QuantitationType;
import gov.nih.nci.caarray.domain.data.ShortColumn;
import gov.nih.nci.caarray.domain.data.StringColumn;
import gov.nih.nci.caarray.domain.file.CaArrayFile;
import gov.nih.nci.caarray.domain.hybridization.Hybridization;
import gov.nih.nci.caarray.domain.project.Experiment;
import gov.nih.nci.cagrid.caarray.util.GridTransferResultHandler;
import gov.nih.nci.cagrid.cqlquery.Attribute;
import gov.nih.nci.cagrid.cqlquery.CQLQuery;
import gov.nih.nci.cagrid.cqlquery.Object;
import gov.nih.nci.cagrid.cqlquery.Predicate;
import gov.nih.nci.cagrid.cqlresultset.CQLQueryResults;
import gov.nih.nci.cagrid.data.faults.MalformedQueryExceptionType;
import gov.nih.nci.cagrid.data.faults.QueryProcessingExceptionType;
import gov.nih.nci.cagrid.data.utilities.CQLQueryResultsIterator;

import java.io.IOException;
import java.io.InputStream;
import java.rmi.RemoteException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.apache.axis.client.Stub;
import org.apache.axis.message.addressing.EndpointReferenceType;
import org.apache.axis.types.URI.MalformedURIException;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.time.StopWatch;
import org.cagrid.transfer.context.client.TransferServiceContextClient;
import org.cagrid.transfer.context.client.helper.TransferClientHelper;
import org.cagrid.transfer.context.stubs.types.TransferServiceContextReference;
import org.globus.gsi.GlobusCredential;

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
public class CaArraySvcClient extends CaArraySvcClientBase {
    private static final String EXPERIMENT_NAME = "hg133plus2";
    private static final String QUANTITATION_TYPES_CSV_STRING = "CELX,CELY,CELintensity,CELintensityStdev,CELMask,CELOutlier,CELPixels";
    
    private static GridTransferResultHandler DATA_COLUMN_HANDLER = new GridTransferResultHandler() {
        public java.lang.Object processRetrievedData(InputStream stream) throws IOException {
            byte[] dataBytes = IOUtils.toByteArray(stream);
            return SerializationHelperUtility.deserialize(dataBytes);
        }
    };

    public CaArraySvcClient(String url) throws MalformedURIException, RemoteException {
        this(url,null);
    }

    public CaArraySvcClient(String url, GlobusCredential proxy) throws MalformedURIException, RemoteException {
           super(url,proxy);
    }

    public CaArraySvcClient(EndpointReferenceType epr) throws MalformedURIException, RemoteException {
           this(epr,null);
    }

    public CaArraySvcClient(EndpointReferenceType epr, GlobusCredential proxy) throws MalformedURIException, RemoteException {
           super(epr,proxy);
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

                StopWatch sw = new StopWatch();
                sw.start();

                ArrayDesign arrayDesign = new ArrayDesign();
                arrayDesign.setId(1L);
                ArrayDesignDetails designDetails = client.getDesignDetails(arrayDesign);
                System.out.println("DesignDetails: " + designDetails);
                sw.stop();
                System.out.println("Time for design details retrieval: " + sw.toString());

                sw = new StopWatch();
                sw.start();
                QuantitationType qt = (QuantitationType) get(client, "gov.nih.nci.caarray.domain.data.QuantitationType",
                      "name", "CELX");
                sw.stop();
                System.out.println("Time for simple data service retrieval: " + sw.toString());
              
              DataRetrievalRequest drr = new DataRetrievalRequest();
              lookupExperiment(client, drr, EXPERIMENT_NAME);
              lookupQuantitationTypes(client, drr, QUANTITATION_TYPES_CSV_STRING);
              //drr.getHybridizations().add(hyb);
              //drr.addQuantitationType(qt);
              sw = new StopWatch();
              sw.start();
              System.out.println("Beginning data retrieval at " + SimpleDateFormat.getDateTimeInstance().format(new Date()));
              DataSet dataSet = client.getDataSet(drr);
              sw.stop();
              System.out.println("Time to retrieve dataset: " + sw.toString());
              introspectDataSet(client, dataSet);

              sw = new StopWatch();
              sw.start();
              CaArrayFile file = new CaArrayFile();
              file.setId(3L);
              byte[] bytes = client.readFileUsingGridTransfer(file);
              System.out.println("Bytes: " + IOUtils.toString(bytes));
              sw.stop();
              System.out.println("Time to retrieve file: " + sw.toString());

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
    
    private static void introspectDataSet(CaArraySvcClient client, DataSet dataSet) throws QueryProcessingExceptionType, MalformedQueryExceptionType, RemoteException {
        int numValuesRetrieved = 0;
        int numColumns = 0;

        if (dataSet == null) {
            System.out.println("Retrieved null DataSet.");
            return;
        }
        DesignElementList designElementList = dataSet.getDesignElementList();
        List<AbstractDesignElement> designElements = designElementList.getDesignElements();

        // Get each HybridizationData in the DataSet.
        for (HybridizationData oneHybData : dataSet.getHybridizationDataList()) {
            for (AbstractDataColumn column : oneHybData.getColumns()) {
                numColumns++;
                QuantitationType qType = column.getQuantitationType();
                Class typeClass = qType.getTypeClass();
                // Retrieve the appropriate data depending on the type of the column.
                if (typeClass == String.class) {
                    String[] values = ((StringColumn) column).getValues();
                    numValuesRetrieved += values.length;
                    System.out.println("Found String Column");
                } else if (typeClass == Float.class) {
                    float[] values = ((FloatColumn) column).getValues();
                    numValuesRetrieved += values.length;
                    System.out.println("Found float Column");
                } else if (typeClass == Short.class) {
                    short[] values = ((ShortColumn) column).getValues();
                    numValuesRetrieved += values.length;
                    System.out.println("Found short Column");
                } else if (typeClass == Boolean.class) {
                    boolean[] values = ((BooleanColumn) column).getValues();
                    numValuesRetrieved += values.length;
                    System.out.println("Found boolean Column");
                } else if (typeClass == Double.class) {
                    double[] values = ((DoubleColumn) column).getValues();
                    numValuesRetrieved += values.length;
                    System.out.println("Found double Column");
                } else if (typeClass == Integer.class) {
                    int[] values = ((IntegerColumn) column).getValues();
                    numValuesRetrieved += values.length;
                    System.out.println("Found integer Column");
                } else if (typeClass == Long.class) {
                    long[] values = ((LongColumn) column).getValues();
                    numValuesRetrieved += values.length;
                    System.out.println("Found long Column");
                } else {
                    // Should never get here.
                }
            }
        }
        System.out.println("Retrieved " + dataSet.getHybridizationDataList().size() + " hybridization data elements, "
                + designElements.size() + " design elements of type "
                + designElementList.getDesignElementType() + "," + dataSet.getQuantitationTypes().size()
                + " quantitation types, " + numColumns + " columns and " + numValuesRetrieved + " values.");

    }
    
    private static void lookupExperiment(CaArraySvcClient client, DataRetrievalRequest request, String experimentName)
            throws QueryProcessingExceptionType, MalformedQueryExceptionType, RemoteException {
        // Locate the experiment and add its hybridizations to the request.
        Experiment experiment = (Experiment) get(client, "gov.nih.nci.caarray.domain.project.Experiment", "title",
                experimentName);
        Set<Hybridization> allHybs = experiment.getHybridizations();
        request.getHybridizations().addAll(allHybs);
    }

    private static void lookupQuantitationTypes(CaArraySvcClient client, DataRetrievalRequest request,
            String quantitationTypesCsv) throws QueryProcessingExceptionType, MalformedQueryExceptionType, RemoteException {
        String[] quantitationTypeNames = quantitationTypesCsv.split(",");
        if (quantitationTypeNames == null) {
            return;
        }

        // Locate each quantitation type and add it to the request.
        for (int i = 0; i < quantitationTypeNames.length; i++) {
            String quantitationTypeName = quantitationTypeNames[i];
            QuantitationType qt = (QuantitationType) get(client, "gov.nih.nci.caarray.domain.data.QuantitationType", "name", quantitationTypeName);
            request.getQuantitationTypes().add(qt);
        }
    }
    
    public byte[] readFileUsingGridTransfer(CaArrayFile file) throws Exception {
        GridTransferResultHandler handler = new GridTransferResultHandler() {
            public java.lang.Object processRetrievedData(InputStream stream) throws IOException {
                return IOUtils.toByteArray(stream);
            }
        };
        return (byte[]) readFileUsingGridTransfer(file, handler);
    }

    public java.lang.Object readFileUsingGridTransfer(CaArrayFile file, GridTransferResultHandler handler) throws Exception {
        TransferServiceContextReference fileRef = createFileTransfer(file);
        TransferServiceContextClient tclient = null;
        try {
            tclient = new TransferServiceContextClient(fileRef.getEndpointReference());
            InputStream stream = TransferClientHelper.getData(tclient.getDataTransferDescriptor());
            return handler.processRetrievedData(stream);
        } finally {
            if (tclient != null) {
                tclient.destroy();
            }
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

  public org.cagrid.transfer.context.stubs.types.TransferServiceContextReference createFileTransfer(gov.nih.nci.caarray.domain.file.CaArrayFile caArrayFile) throws RemoteException {
    synchronized(portTypeMutex){
      configureStubSecurity((Stub)portType,"createFileTransfer");
    gov.nih.nci.cagrid.caarray.stubs.CreateFileTransferRequest params = new gov.nih.nci.cagrid.caarray.stubs.CreateFileTransferRequest();
    gov.nih.nci.cagrid.caarray.stubs.CreateFileTransferRequestCaArrayFile caArrayFileContainer = new gov.nih.nci.cagrid.caarray.stubs.CreateFileTransferRequestCaArrayFile();
    caArrayFileContainer.setCaArrayFile(caArrayFile);
    params.setCaArrayFile(caArrayFileContainer);
    gov.nih.nci.cagrid.caarray.stubs.CreateFileTransferResponse boxedResult = portType.createFileTransfer(params);
    return boxedResult.getTransferServiceContextReference();
    }
  }

  public org.oasis.wsrf.properties.GetMultipleResourcePropertiesResponse getMultipleResourceProperties(org.oasis.wsrf.properties.GetMultipleResourceProperties_Element params) throws RemoteException {
    synchronized(portTypeMutex){
      configureStubSecurity((Stub)portType,"getMultipleResourceProperties");
    return portType.getMultipleResourceProperties(params);
    }
  }

  public org.oasis.wsrf.properties.GetResourcePropertyResponse getResourceProperty(javax.xml.namespace.QName params) throws RemoteException {
    synchronized(portTypeMutex){
      configureStubSecurity((Stub)portType,"getResourceProperty");
    return portType.getResourceProperty(params);
    }
  }

  public org.oasis.wsrf.properties.QueryResourcePropertiesResponse queryResourceProperties(org.oasis.wsrf.properties.QueryResourceProperties_Element params) throws RemoteException {
    synchronized(portTypeMutex){
      configureStubSecurity((Stub)portType,"queryResourceProperties");
    return portType.queryResourceProperties(params);
    }
  }

}
