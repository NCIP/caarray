//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package caarray.client.examples.download_parsed_data.java;

import gov.nih.nci.caarray.domain.array.AbstractDesignElement;
import gov.nih.nci.caarray.domain.data.DesignElementList;
import gov.nih.nci.caarray.domain.data.AbstractDataColumn;
import gov.nih.nci.caarray.domain.data.BooleanColumn;
import gov.nih.nci.caarray.domain.data.DataSet;
import gov.nih.nci.caarray.domain.data.DerivedArrayData;
import gov.nih.nci.caarray.domain.data.DoubleColumn;
import gov.nih.nci.caarray.domain.data.FloatColumn;
import gov.nih.nci.caarray.domain.data.HybridizationData;
import gov.nih.nci.caarray.domain.data.IntegerColumn;
import gov.nih.nci.caarray.domain.data.LongColumn;
import gov.nih.nci.caarray.domain.data.QuantitationType;
import gov.nih.nci.caarray.domain.data.RawArrayData;
import gov.nih.nci.caarray.domain.data.ShortColumn;
import gov.nih.nci.caarray.domain.data.StringColumn;
import gov.nih.nci.caarray.domain.hybridization.Hybridization;
import gov.nih.nci.caarray.domain.project.Experiment;
import gov.nih.nci.caarray.services.CaArrayServer;
import gov.nih.nci.caarray.services.ServerConnectionException;
import gov.nih.nci.caarray.services.search.CaArraySearchService;
import caarray.client.examples.BaseProperties;

import java.util.List;
import java.util.Set;

/**
 * A client downloading a full data set corresponding to one data file through CaArray's Remote Java API.
 * This is a different way to get a DataSet without using the DataRetrievalService.
 * Since the DataRetrievalService is not used, the resulting DataSet is "cut" according to the
 * usual object-graph-cutting rules, and a search-by-ID must be performed at each stage to get
 * to the next level of associations.
 *
 * @author Rashmi Srinivasa
 */
public class ApiOneFileDataSetDownload {
    private static final String EXPERIMENT_NAME = BaseProperties.AFFYMETRIX_EXPERIMENT;

    public static void main(String[] args) {
        ApiOneFileDataSetDownload client = new ApiOneFileDataSetDownload();
        try {
            CaArrayServer server = new CaArrayServer(BaseProperties.getServerHostname(), BaseProperties
                    .getServerJndiPort());
            server.connect();
            CaArraySearchService searchService = server.getSearchService();
            System.out.println("Downloading a data set corresponding to one data file from Experiment "
                    + EXPERIMENT_NAME);
            client.getDataSetFromExperiment(searchService, EXPERIMENT_NAME);
        } catch (ServerConnectionException e) {
            System.out.println("Could not connect to server.");
            e.printStackTrace();
        } catch (Throwable t) {
            System.out.println("Generic error.");
            t.printStackTrace();
        }
    }

    private void getDataSetFromExperiment(CaArraySearchService searchService, String experimentName) {
        Experiment experiment = lookupExperiment(searchService, experimentName);
        if (experiment == null) {
            System.out.println("Error: Could not find experiment.");
            return;
        }
        Hybridization hybridization = getFirstHybridization(searchService, experiment);
        if (hybridization == null) {
            System.out.println("Error: Retrieved null hybridization.");
            return;
        }
        DataSet dataSet = getDataSet(searchService, hybridization);
        if (dataSet == null) {
            System.out.println("Error: Retrieved null data set.");
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

        int numValuesRetrieved = 0;
        // Get each HybridizationData in the DataSet.
        for (HybridizationData oneHybData : dataSet.getHybridizationDataList()) {
            HybridizationData populatedHybData = searchService.search(oneHybData).get(0);
            // Get each column in the HybridizationData.
            for (AbstractDataColumn column : populatedHybData.getColumns()) {
                AbstractDataColumn populatedColumn = searchService.search(column).get(0);
                // Find the type of the column.
                QuantitationType qType = populatedColumn.getQuantitationType();
                Class typeClass = qType.getTypeClass();
                // Retrieve the appropriate data depending on the type of the column.
                if (typeClass == String.class) {
                    String[] values = ((StringColumn) populatedColumn).getValues();
                    numValuesRetrieved += values.length;
                } else if (typeClass == Float.class) {
                    float[] values = ((FloatColumn) populatedColumn).getValues();
                    numValuesRetrieved += values.length;
                } else if (typeClass == Short.class) {
                    short[] values = ((ShortColumn) populatedColumn).getValues();
                    numValuesRetrieved += values.length;
                } else if (typeClass == Boolean.class) {
                    boolean[] values = ((BooleanColumn) populatedColumn).getValues();
                    numValuesRetrieved += values.length;
                } else if (typeClass == Double.class) {
                    double[] values = ((DoubleColumn) populatedColumn).getValues();
                    numValuesRetrieved += values.length;
                } else if (typeClass == Integer.class) {
                    int[] values = ((IntegerColumn) populatedColumn).getValues();
                    numValuesRetrieved += values.length;
                } else if (typeClass == Long.class) {
                    long[] values = ((LongColumn) populatedColumn).getValues();
                    numValuesRetrieved += values.length;
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

    private Experiment lookupExperiment(CaArraySearchService service, String experimentName) {
        Experiment exampleExperiment = new Experiment();
        exampleExperiment.setTitle(experimentName);

        List<Experiment> experimentList = service.search(exampleExperiment);
        if (experimentList.size() == 0) {
            return null;
        }
        Experiment experiment = experimentList.get(0);
        return experiment;
    }

    private Hybridization getFirstHybridization(CaArraySearchService service, Experiment experiment) {
        Set<Hybridization> allHybridizations = experiment.getHybridizations();
        for (Hybridization hybridization : allHybridizations) {
            Hybridization populatedHybridization = service.search(hybridization).get(0);
            // Yes, we're returning only the first hybridization.
            return populatedHybridization;
        }
        return null;
    }

    private DataSet getDataSet(CaArraySearchService service, Hybridization hybridization) {
        DataSet dataSet = null;
        // Try to find raw data
        Set<RawArrayData> rawArrayDataSet = hybridization.getRawDataCollection();
        for (RawArrayData rawArrayData : rawArrayDataSet) {
            // Return the data set associated with the first raw data.
            RawArrayData populatedArrayData = service.search(rawArrayData).get(0);
            System.out.println("Data set corresponds to file: " + populatedArrayData.getDataFile().getName());
            dataSet = populatedArrayData.getDataSet();
            break;
        }
        if (dataSet == null) {
            // If raw data doesn't exist, try to find derived data
            Set<DerivedArrayData> derivedArrayDataSet = hybridization.getDerivedDataCollection();
            for (DerivedArrayData derivedArrayData : derivedArrayDataSet) {
                // Return the data set associated with the first derived data.
                DerivedArrayData populatedArrayData = service.search(derivedArrayData).get(0);
                System.out.println("Data set corresponds to file: " + populatedArrayData.getDataFile().getName());
                dataSet = populatedArrayData.getDataSet();
                break;
            }
        }
        if (dataSet == null) {
            return null;
        } else {
            // Search for the fully-populated dataset object.
            return service.search(dataSet).get(0);
        }
    }
}
