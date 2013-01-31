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
import gov.nih.nci.caarray.test.api.AbstractApiTest;
import gov.nih.nci.caarray.test.base.TestProperties;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Test;

/**
 * A client downloading a DataSet through CaArray's Remote Java API. The DataSet can contain data from one or more
 * experiments, one or more hybridizations, a subset of quantitation types, and a subset of design elements (e.g.,
 * probes).
 *
 * @author Rashmi Srinivasa
 */
public class ApiDataSetDownload extends AbstractApiTest {
    private static final String[] EXPERIMENT_NAMES = {
        TestProperties.getAffymetricSpecificationName(),
        TestProperties.getAffymetricChpName(),
        TestProperties.getIlluminaRatName(),
        TestProperties.getGenepixCowName()
    };
    private static final String[] QUANTITATION_TYPES_CSV_STRINGS = {
        TestProperties.AFFYMETRIX_CEL_QUANTITATION_TYPES,
        TestProperties.AFFYMETRIX_CHP_QUANTITATION_TYPES,
        TestProperties.ILLUMINA_QUANTITATION_TYPES,
        TestProperties.GENEPIX_QUANTITATION_TYPES
    };

    @Test
    public void testDownloadDataSet() {
        try {
            CaArrayServer server = new CaArrayServer(TestProperties.getServerHostname(), TestProperties
                    .getServerJndiPort());
            server.connect();
            CaArraySearchService searchService = server.getSearchService();
            logForSilverCompatibility(TEST_NAME, "Downloading a DataSet...");
            int i = 0;
            for (String experimentName : EXPERIMENT_NAMES) {
                logForSilverCompatibility(TEST_OUTPUT, "from Experiment: " + experimentName);
                String quantitationTypesCsv = QUANTITATION_TYPES_CSV_STRINGS[i++];
                getDataSetFromExperiment(server, searchService, experimentName, quantitationTypesCsv);
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

    private void getDataSetFromExperiment(CaArrayServer server, CaArraySearchService searchService, String experimentName, String quantitationTypesCsv) {
        DataRetrievalRequest request = new DataRetrievalRequest();
        lookupExperiments(searchService, request, experimentName);
        lookupQuantitationTypes(searchService, request, quantitationTypesCsv);
        DataRetrievalService dataService = server.getDataRetrievalService();
        DataSet dataSet = dataService.getDataSet(request);
        logForSilverCompatibility(API_CALL, "DataRetrievalService.getDataSet(DataRetrievalRequest)");
        int numValuesRetrieved = 0;

        // Check if the retrieved hybridizations and quantitation types are as requested.
        if (dataSet != null) {
            // Get each HybridizationData in the DataSet.
            logForSilverCompatibility(TRAVERSE_OBJECT_GRAPH, "DataSet.getHybridizationDataList().size(): "
                    + dataSet.getHybridizationDataList().size());
            for (HybridizationData oneHybData : dataSet.getHybridizationDataList()) {
                // Get each column in the HybridizationData.
                logForSilverCompatibility(TRAVERSE_OBJECT_GRAPH, "HybridizationData.getColumns().size(): "
                        + oneHybData.getColumns().size());
                for (AbstractDataColumn column : oneHybData.getColumns()) {
                    // Find the type of the column.
                    logForSilverCompatibility(TRAVERSE_OBJECT_GRAPH,
                            "AbstractDataColumn.getQuantitationType().getName(): "
                            + column.getQuantitationType().getName());
                    QuantitationType qType = column.getQuantitationType();
                    Class typeClass = qType.getTypeClass();
                    // Retrieve the appropriate data depending on the type of the column.
                    logForSilverCompatibility(TRAVERSE_OBJECT_GRAPH, "AbstractDataColumn.getValues().");
                    if (typeClass == String.class) {
                        String[] values = ((StringColumn) column).getValues();
                        numValuesRetrieved += values.length;
                    } else if (typeClass == Float.class) {
                        float[] values = ((FloatColumn) column).getValues();
                        numValuesRetrieved += values.length;
                    } else if (typeClass == Short.class) {
                        short[] values = ((ShortColumn) column).getValues();
                        numValuesRetrieved += values.length;
                    } else if (typeClass == Boolean.class) {
                        boolean[] values = ((BooleanColumn) column).getValues();
                        numValuesRetrieved += values.length;
                    } else if (typeClass == Double.class) {
                        double[] values = ((DoubleColumn) column).getValues();
                        numValuesRetrieved += values.length;
                    } else if (typeClass == Integer.class) {
                        int[] values = ((IntegerColumn) column).getValues();
                        numValuesRetrieved += values.length;
                    } else if (typeClass == Long.class) {
                        long[] values = ((LongColumn) column).getValues();
                        numValuesRetrieved += values.length;
                    } else {
                        // Should never get here.
                    }
                }
            }
            logForSilverCompatibility(TEST_OUTPUT, "Retrieved " + dataSet.getHybridizationDataList().size()
                    + " hybridization data elements, " + dataSet.getQuantitationTypes().size()
                    + " quantitation types and " + numValuesRetrieved + " values.");
            assertTrue((dataSet.getHybridizationDataList().size() > 0) && (dataSet.getQuantitationTypes().size() > 0)
                    && (numValuesRetrieved > 0));
        } else {
            logForSilverCompatibility(TEST_OUTPUT, "Retrieved null DataSet.");
            assertTrue("Error: Retrieved null DataSet.", false);
        }
    }

    private void lookupExperiments(CaArraySearchService service, DataRetrievalRequest request, String experimentName) {
        String[] experimentTitles = experimentName.split(",");
        if (experimentTitles == null) {
            return;
        }

        // Locate each experiment, and add its hybridizations to the request.
        Experiment exampleExperiment = new Experiment();
        for (int i = 0; i < experimentTitles.length; i++) {
            String experimentTitle = experimentTitles[i];
            exampleExperiment.setTitle(experimentTitle);
            List<Experiment> experimentList = service.search(exampleExperiment);
            logForSilverCompatibility(API_CALL, "CaArraySearchService.search(Experiment)");
            Set<Hybridization> allHybs = getAllHybridizations(experimentList);
            request.getHybridizations().addAll(allHybs);
        }
    }

    private void lookupQuantitationTypes(CaArraySearchService service, DataRetrievalRequest request, String quantitationTypesCsv) {
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
            logForSilverCompatibility(API_CALL, "CaArraySearchService.search(QuantitationType)");
            request.getQuantitationTypes().addAll(quantitationTypeList);
        }
    }

    private Set<Hybridization> getAllHybridizations(List<Experiment> experimentList) {
        Set<Hybridization> hybridizations = new HashSet<Hybridization>();
        for (Experiment experiment : experimentList) {
            hybridizations.addAll(getAllHybridizations(experiment));
        }
        return hybridizations;
    }

    private Set<Hybridization> getAllHybridizations(Experiment experiment) {
        Set<Hybridization> hybridizations = new HashSet<Hybridization>();
        hybridizations.addAll(experiment.getHybridizations());
        logForSilverCompatibility(TRAVERSE_OBJECT_GRAPH, "Experiment.getHybridizations().size(): "
                + experiment.getHybridizations().size());
        return hybridizations;
    }
}
