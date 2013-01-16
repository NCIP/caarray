//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
package caarray.client.examples.download_parsed_data.grid;

import gov.nih.nci.caarray.domain.array.AbstractDesignElement;
import gov.nih.nci.caarray.domain.data.DesignElementList;
import gov.nih.nci.caarray.domain.data.AbstractDataColumn;
import gov.nih.nci.caarray.domain.data.BooleanColumn;
import gov.nih.nci.caarray.domain.data.DataRetrievalRequest;
import gov.nih.nci.caarray.domain.data.DataSet;
import gov.nih.nci.caarray.domain.data.DoubleColumn;
import gov.nih.nci.caarray.domain.data.FloatColumn;
import gov.nih.nci.caarray.domain.data.HybridizationData;
import gov.nih.nci.caarray.domain.data.IntegerColumn;
import gov.nih.nci.caarray.domain.data.LongColumn;
import gov.nih.nci.caarray.domain.data.QuantitationType;
import gov.nih.nci.caarray.domain.data.ShortColumn;
import gov.nih.nci.caarray.domain.data.StringColumn;
import gov.nih.nci.caarray.domain.hybridization.Hybridization;
import gov.nih.nci.caarray.domain.project.Experiment;
import gov.nih.nci.cagrid.caarray.client.CaArraySvcClient;
import gov.nih.nci.cagrid.cqlquery.Attribute;
import gov.nih.nci.cagrid.cqlquery.CQLQuery;
import gov.nih.nci.cagrid.cqlquery.Predicate;
import gov.nih.nci.cagrid.cqlresultset.CQLQueryResults;
import gov.nih.nci.cagrid.data.utilities.CQLQueryResultsIterator;
import caarray.client.examples.BaseProperties;

import java.rmi.RemoteException;
import java.util.Set;
import java.util.List;

/**
 * A client downloading a data set through the CaArray Grid service.
 *
 * @author Rashmi Srinivasa
 */
public class GridDataSetDownload {
    private static final String EXPERIMENT_NAME = BaseProperties.AFFYMETRIX_EXPERIMENT;
    //private static final String QUANTITATION_TYPES_CSV_STRING = BaseProperties.AFFYMETRIX_CHP_QUANTITATION_TYPES;
    private static final String QUANTITATION_TYPES_CSV_STRING = BaseProperties.AFFYMETRIX_CEL_QUANTITATION_TYPES;

    public static void main(String[] args) {
        GridDataSetDownload gridClient = new GridDataSetDownload();
        try {
            CaArraySvcClient client = new CaArraySvcClient(BaseProperties.getGridServiceUrl());
            System.out.println("Grid-Downloading a DataSet from experiment " + EXPERIMENT_NAME + "...");
            gridClient.downloadDataSet(client, EXPERIMENT_NAME, QUANTITATION_TYPES_CSV_STRING);
        } catch (RemoteException e) {
            System.out.println("Remote server threw an exception.");
            e.printStackTrace();
        } catch (Throwable t) {
            // Catches things like out-of-memory errors and logs them.
            System.out.println("Generic error.");
            t.printStackTrace();
        }
    }

    private void downloadDataSet(CaArraySvcClient client, String experimentName, String quantitationTypesCsv)
            throws RemoteException {
        DataRetrievalRequest request = new DataRetrievalRequest();
        lookupExperiment(client, request, experimentName);
        lookupQuantitationTypes(client, request, quantitationTypesCsv);
        if (request.getHybridizations().size() == 0) {
            System.out.println("No hybridizations found in request.");
            return;
        }
        if (request.getQuantitationTypes().size() == 0) {
            System.out.println("No quantitation types found in request.");
            return;
        }

        long startTime = System.currentTimeMillis();
        DataSet dataSet = client.getDataSet(request);
        long totalTime = System.currentTimeMillis() - startTime;
        System.out.println("Time to retrieve DataSet = " + totalTime + " ms.");

        int numValuesRetrieved = 0;
        if (dataSet == null) {
            System.out.println("Error: Retrieved null DataSet.");
        }

        DesignElementList designElementList = dataSet.getDesignElementList();
        List<AbstractDesignElement> designElements = designElementList.getDesignElements();
        String designElementType = designElementList.getDesignElementType();

        // Uncomment the following to print each probe name in the DataSet.
	/*
        System.out.println("Logical Probe Names: ");
        for (AbstractDesignElement oneDesignElement : designElements) {
            if (DesignElementType.LOGICAL_PROBE.getValue().equals(designElementType)) {
                LogicalProbe probe = (LogicalProbe) oneDesignElement;
                System.out.print(probe.getName() + "  ");
            }
        }
        System.out.println();
	*/

        // Get each HybridizationData in the DataSet.
        for (HybridizationData oneHybData : dataSet.getHybridizationDataList()) {
            // Get each column in the HybridizationData.
            for (AbstractDataColumn column : oneHybData.getColumns()) {
                // Find the type of the column.
                QuantitationType qType = column.getQuantitationType();
                System.out.println("Quantitation type = " + qType.getName());
                Class typeClass = qType.getTypeClass();
                // Retrieve the appropriate data depending on the type of the column.
                if (typeClass == String.class) {
                    String[] values = ((StringColumn) column).getValues();
                    numValuesRetrieved += values.length;
                    System.out.println("Retrieved " + values.length + " String values.");
                } else if (typeClass == Float.class) {
                    float[] values = ((FloatColumn) column).getValues();
                    numValuesRetrieved += values.length;
                    System.out.println("Retrieved " + values.length + " float values.");
                } else if (typeClass == Short.class) {
                    short[] values = ((ShortColumn) column).getValues();
                    numValuesRetrieved += values.length;
                    System.out.println("Retrieved " + values.length + " short values.");
                } else if (typeClass == Boolean.class) {
                    boolean[] values = ((BooleanColumn) column).getValues();
                    numValuesRetrieved += values.length;
                    System.out.println("Retrieved " + values.length + " boolean values.");
                } else if (typeClass == Double.class) {
                    double[] values = ((DoubleColumn) column).getValues();
                    numValuesRetrieved += values.length;
                    System.out.println("Retrieved " + values.length + " double values.");
                } else if (typeClass == Integer.class) {
                    int[] values = ((IntegerColumn) column).getValues();
                    numValuesRetrieved += values.length;
                    System.out.println("Retrieved " + values.length + " int values.");
                } else if (typeClass == Long.class) {
                    long[] values = ((LongColumn) column).getValues();
                    numValuesRetrieved += values.length;
                    System.out.println("Retrieved " + values.length + " long values.");
                } else {
                    // Should never get here.
                }
            }
        }
        System.out.println("Retrieved " + dataSet.getHybridizationDataList().size() + " hybridization data elements, "
                + designElements.size() + " design elements of type "
                + designElementType + "," + dataSet.getQuantitationTypes().size()
                + " quantitation types and " + numValuesRetrieved + " values.");

    }

    private void lookupExperiment(CaArraySvcClient client, DataRetrievalRequest request, String experimentName)
            throws RemoteException {
        CQLQuery cqlQuery = new CQLQuery();
        gov.nih.nci.cagrid.cqlquery.Object target = new gov.nih.nci.cagrid.cqlquery.Object();
        target.setName("gov.nih.nci.caarray.domain.project.Experiment");
        Attribute titleAttribute = new Attribute();
        titleAttribute.setName("title");
        titleAttribute.setValue(experimentName);
        titleAttribute.setPredicate(Predicate.EQUAL_TO);
        target.setAttribute(titleAttribute);
        cqlQuery.setTarget(target);
        CQLQueryResults cqlResults = client.query(cqlQuery);
        CQLQueryResultsIterator iter = new CQLQueryResultsIterator(cqlResults, CaArraySvcClient.class
                .getResourceAsStream("client-config.wsdd"));
        if (!(iter.hasNext())) {
            System.out.println("Could not find experiment.");
            return;
        }
        Experiment experiment = (Experiment) iter.next();
        Set<Hybridization> allHybs = experiment.getHybridizations();
        if (allHybs.isEmpty()) {
            return;
        }
        request.getHybridizations().addAll(allHybs);
    }

    private void lookupQuantitationTypes(CaArraySvcClient client, DataRetrievalRequest request,
            String quantitationTypesCsv) throws RemoteException {
        String[] quantitationTypeNames = quantitationTypesCsv.split(",");
        if (quantitationTypeNames == null) {
            return;
        }

        // Locate each quantitation type and add it to the request.
        CQLQuery cqlQuery = new CQLQuery();
        gov.nih.nci.cagrid.cqlquery.Object target = new gov.nih.nci.cagrid.cqlquery.Object();
        target.setName("gov.nih.nci.caarray.domain.data.QuantitationType");
        Attribute nameAttribute = new Attribute();
        nameAttribute.setName("name");
        nameAttribute.setPredicate(Predicate.EQUAL_TO);
        target.setAttribute(nameAttribute);
        cqlQuery.setTarget(target);
        for (int i = 0; i < quantitationTypeNames.length; i++) {
            String quantitationTypeName = quantitationTypeNames[i];
            nameAttribute.setValue(quantitationTypeName);
            CQLQueryResults cqlResults = client.query(cqlQuery);
            CQLQueryResultsIterator iter = new CQLQueryResultsIterator(cqlResults, CaArraySvcClient.class
                    .getResourceAsStream("client-config.wsdd"));
            if (!(iter.hasNext())) {
                continue;
            }
            QuantitationType quantitationType = (QuantitationType) iter.next();
            request.getQuantitationTypes().add(quantitationType);
        }
    }
}
