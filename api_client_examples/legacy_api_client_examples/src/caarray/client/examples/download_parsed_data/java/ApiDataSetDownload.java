//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
package caarray.client.examples.download_parsed_data.java;

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
import gov.nih.nci.caarray.services.CaArrayServer;
import gov.nih.nci.caarray.services.ServerConnectionException;
import gov.nih.nci.caarray.services.data.DataRetrievalService;
import gov.nih.nci.caarray.services.search.CaArraySearchService;
import caarray.client.examples.BaseProperties;

import java.util.List;
import java.util.Set;

/**
 * A client downloading a DataSet through CaArray's Remote Java API. The DataSet can contain data from one or more
 * experiments, one or more hybridizations, a subset of quantitation types, and a subset of design elements (e.g.,
 * probes). Each hybridization requested must have the same set of design elements.
 *
 * @author Rashmi Srinivasa
 */
public class ApiDataSetDownload {
    private static final String EXPERIMENT_NAME = BaseProperties.AFFYMETRIX_EXPERIMENT;
    private static final String QUANTITATION_TYPES_CSV_STRING = BaseProperties.AFFYMETRIX_CEL_QUANTITATION_TYPES;

    public static void main(String[] args) {
        ApiDataSetDownload client = new ApiDataSetDownload();
        try {
            CaArrayServer server = new CaArrayServer(BaseProperties.getServerHostname(), BaseProperties
                    .getServerJndiPort());
            server.connect();
            CaArraySearchService searchService = server.getSearchService();
            DataRetrievalService dataService = server.getDataRetrievalService();
            System.out.println("Downloading a DataSet from experiment " + EXPERIMENT_NAME + "...");
            client.getDataSetFromExperiment(searchService, dataService, EXPERIMENT_NAME, QUANTITATION_TYPES_CSV_STRING);
        } catch (ServerConnectionException e) {
            System.out.println("Could not connect to server.");
            e.printStackTrace();
        } catch (Throwable t) {
            System.out.println("Generic error.");
            t.printStackTrace();
        }
    }

    private void getDataSetFromExperiment(CaArraySearchService searchService, DataRetrievalService dataService,
            String experimentName, String quantitationTypesCsv) {
        DataRetrievalRequest request = new DataRetrievalRequest();
        lookupExperiment(searchService, request, experimentName);
        lookupQuantitationTypes(searchService, request, quantitationTypesCsv);
        if (request.getHybridizations().size() == 0) {
            System.out.println("No hybridizations found in request.");
            return;
        }
        if (request.getQuantitationTypes().size() == 0) {
            System.out.println("No quantitation types found in request.");
            return;
        }

        long startTime = System.currentTimeMillis();
        DataSet dataSet = dataService.getDataSet(request);
        long totalTime = System.currentTimeMillis() - startTime;
        System.out.println("Time to retrieve DataSet = " + totalTime + " ms.");

        int numValuesRetrieved = 0;

        if (dataSet == null) {
            System.out.println("Retrieved null DataSet.");
            return;
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

    private void lookupExperiment(CaArraySearchService service, DataRetrievalRequest request, String experimentName) {
        // Locate the experiment and add its hybridizations to the request.
        Experiment exampleExperiment = new Experiment();
        exampleExperiment.setTitle(experimentName);
        Experiment experiment = service.search(exampleExperiment).get(0);
        Set<Hybridization> allHybs = experiment.getHybridizations();
        request.getHybridizations().addAll(allHybs);
    }

    private void lookupQuantitationTypes(CaArraySearchService service, DataRetrievalRequest request,
            String quantitationTypesCsv) {
        String[] quantitationTypeNames = quantitationTypesCsv.split(",");
        if (quantitationTypeNames == null) {
            return;
        }

        // Locate each quantitation type and add it to the request.
        QuantitationType exampleQuantitationType = new QuantitationType();
        for (int i = 0; i < quantitationTypeNames.length; i++) {
            String quantitationTypeName = quantitationTypeNames[i];
            exampleQuantitationType.setName(quantitationTypeName);
            List<QuantitationType> quantitationTypeList = service.search(exampleQuantitationType);
            request.getQuantitationTypes().addAll(quantitationTypeList);
        }
    }
}
