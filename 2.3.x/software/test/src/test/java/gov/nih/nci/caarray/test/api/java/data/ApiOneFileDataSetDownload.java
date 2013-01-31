//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.test.api.java.data;

import static org.junit.Assert.assertTrue;
import gov.nih.nci.caarray.domain.data.AbstractDataColumn;
import gov.nih.nci.caarray.domain.data.BooleanColumn;
import gov.nih.nci.caarray.domain.data.DataSet;
import gov.nih.nci.caarray.domain.data.DerivedArrayData;
import gov.nih.nci.caarray.domain.data.DesignElementList;
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
import gov.nih.nci.caarray.test.api.AbstractApiTest;
import gov.nih.nci.caarray.test.base.TestProperties;

import java.util.List;
import java.util.Set;

import org.junit.Test;

/**
 * A client downloading a full data set corresponding to one data file through CaArray's Remote Java API.
 *
 * @author Rashmi Srinivasa
 */
public class ApiOneFileDataSetDownload extends AbstractApiTest {
    private static final String[] EXPERIMENT_NAMES = {
        TestProperties.getAffymetricSpecificationName(),
        TestProperties.getAffymetricChpName(),
        TestProperties.getIlluminaRatName(),
        TestProperties.getGenepixCowName()
    };

    @Test
    public void testDownloadOneFileDataSet() {
        try {
            CaArrayServer server = new CaArrayServer(TestProperties.getServerHostname(), TestProperties
                    .getServerJndiPort());
            server.connect();
            CaArraySearchService searchService = server.getSearchService();
            logForSilverCompatibility(TEST_NAME, "Downloading a full DataSet corresponding to one data file...");
            for (String experimentName : EXPERIMENT_NAMES) {
                logForSilverCompatibility(TEST_OUTPUT, "from Experiment: " + experimentName);
                getDataSetFromExperiment(searchService, experimentName);
            }
        } catch (ServerConnectionException e) {
            StringBuilder trace = buildStackTrace(e);
            logForSilverCompatibility(TEST_OUTPUT, "Server connection exception: " + e + "\nTrace: " + trace);
            assertTrue("Server connection exception: " + e, false);
        } catch (RuntimeException e) {
            StringBuilder trace = buildStackTrace(e);
            logForSilverCompatibility(TEST_OUTPUT, "Runtime exception: " + e + "\nTrace: " + trace);
            assertTrue("Runtime exception: " + e, false);
        } catch (Throwable t) {
            // Catches things like out-of-memory errors and logs them.
            StringBuilder trace = buildStackTrace(t);
            logForSilverCompatibility(TEST_OUTPUT, "Throwable: " + t + "\nTrace: " + trace);
            assertTrue("Throwable: " + t, false);
        }
    }

    private void getDataSetFromExperiment(CaArraySearchService searchService, String experimentName) {
        Experiment experiment = lookupExperiment(searchService, experimentName);
        if (experiment != null) {
            Hybridization hybridization = getFirstHybridization(searchService, experiment);
            if (hybridization != null) {
                DataSet dataSet = getDataSet(searchService, hybridization);
                if (dataSet != null) {
                    int numValuesRetrieved = 0;
                    DesignElementList designElementList = dataSet.getDesignElementList();
                    DesignElementList populatedDesignElementList = searchService.search(designElementList).get(0);
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
                    logForSilverCompatibility(TEST_OUTPUT, "Retrieved " + dataSet.getHybridizationDataList().size()
                            + " hybridization data elements, "
                            + populatedDesignElementList.getDesignElements().size() + " design elements of type "
                            + populatedDesignElementList.getDesignElementType() + ","
                            + dataSet.getQuantitationTypes().size() + " quantitation types and "
                            + numValuesRetrieved + " values.");
                    assertTrue((dataSet.getQuantitationTypes().size() > 0) && (numValuesRetrieved > 0));
                } else {
                    logForSilverCompatibility(TEST_OUTPUT, "Error: Retrieved null data set.");
                    assertTrue("Error: Retrieved null data set.", false);
                }
            } else {
                logForSilverCompatibility(TEST_OUTPUT, "Error: Retrieved null hybridization.");
                assertTrue("Error: Retrieved null hybridization.", false);
            }
        } else {
            logForSilverCompatibility(TEST_OUTPUT, "Error: Could not find experiment.");
            assertTrue("Error: Could not find experiment.", false);
        }
    }

    private Experiment lookupExperiment(CaArraySearchService service, String experimentName) {
        Experiment exampleExperiment = new Experiment();
        exampleExperiment.setTitle(experimentName);

        List<Experiment> experimentList = service.search(exampleExperiment);
        logForSilverCompatibility(API_CALL, "CaArraySearchService.search(Experiment)");
        if (experimentList.size() == 0) {
            return null;
        }
        Experiment experiment = experimentList.get(0);
        return experiment;
    }

    private Hybridization getFirstHybridization(CaArraySearchService service, Experiment experiment) {
        Set<Hybridization> allHybridizations = experiment.getHybridizations();
        logForSilverCompatibility(TRAVERSE_OBJECT_GRAPH, "Experiment.getHybridizations().size(): "
                + experiment.getHybridizations().size());
        for (Hybridization hybridization : allHybridizations) {
            Hybridization populatedHybridization = service.search(hybridization).get(0);
            logForSilverCompatibility(API_CALL, "CaArraySearchService.search(Hybridization)");
            // Yes, we're returning only the first hybridization.
            return populatedHybridization;
        }
        return null;
    }

    private DataSet getDataSet(CaArraySearchService service, Hybridization hybridization) {
        DataSet dataSet = null;
        // Try to find raw data
        Set<RawArrayData> rawArrayDataSet = hybridization.getRawDataCollection();
        logForSilverCompatibility(TRAVERSE_OBJECT_GRAPH, "Hybridization.getArrayData().");
            for (RawArrayData rawArrayData : rawArrayDataSet) {
                // Return the data set associated with the first raw data.
                RawArrayData populatedArrayData = service.search(rawArrayData).get(0);
                logForSilverCompatibility(API_CALL, "CaArraySearchService.search(RawArrayData)");
                dataSet = populatedArrayData.getDataSet();
                logForSilverCompatibility(TRAVERSE_OBJECT_GRAPH, "RawArrayData.getDataSet().");
                break;
            }
        if (dataSet == null) {
            // If raw data doesn't exist, try to find derived data
            Set<DerivedArrayData> derivedArrayDataSet = hybridization.getDerivedDataCollection();
            for (DerivedArrayData derivedArrayData : derivedArrayDataSet) {
                // Return the data set associated with the first derived data.
                DerivedArrayData populatedArrayData = service.search(derivedArrayData).get(0);
                logForSilverCompatibility(API_CALL, "CaArraySearchService.search(DerivedArrayData)");
                dataSet = populatedArrayData.getDataSet();
                logForSilverCompatibility(TRAVERSE_OBJECT_GRAPH, "DerivedArrayData.getDataSet().");
                break;
            }
        }
        if (dataSet == null) {
            return null;
        } else {
            logForSilverCompatibility(API_CALL, "CaArraySearchService.search(DataSet)");
            return service.search(dataSet).get(0);
        }
    }
}
