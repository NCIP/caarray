//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.test.api.legacy.grid.data;

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
import gov.nih.nci.caarray.test.api.legacy.grid.AbstractLegacyGridApiTest;
import gov.nih.nci.caarray.test.base.TestProperties;
import gov.nih.nci.cagrid.caarray.client.CaArraySvcClient;
import gov.nih.nci.cagrid.cqlquery.Attribute;
import gov.nih.nci.cagrid.cqlquery.CQLQuery;
import gov.nih.nci.cagrid.cqlquery.Predicate;
import gov.nih.nci.cagrid.cqlresultset.CQLQueryResults;
import gov.nih.nci.cagrid.data.utilities.CQLQueryResultsIterator;

import java.rmi.RemoteException;
import java.util.Set;

import org.junit.Test;

/**
 * A client downloading a data set through the CaArray Grid service.
 *
 * @author Rashmi Srinivasa
 */
public class GridDataSetDownload extends AbstractLegacyGridApiTest {
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
            logForSilverCompatibility(TEST_NAME, "Grid-Downloading a DataSet...");
            int i = 0;
            for (String experimentName : EXPERIMENT_NAMES) {
                String quantitationTypesCsv = QUANTITATION_TYPES_CSV_STRINGS[i++];

                downloadDataSet(experimentName, quantitationTypesCsv);
            }
        } catch (RemoteException e) {
            StringBuilder trace = buildStackTrace(e);
            logForSilverCompatibility(TEST_OUTPUT, "Remote exception: " + e + "\nTrace: " + trace);
            assertTrue("Remote exception: " + e, false);
        } catch (Throwable t) {
            // Catches things like out-of-memory errors and logs them.
            StringBuilder trace = buildStackTrace(t);
            logForSilverCompatibility(TEST_OUTPUT, "Throwable: " + t + "\nTrace: " + trace);
            assertTrue("Throwable: " + t, false);
        }
    }

    private void downloadDataSet(String experimentName, String quantitationTypesCsv)
    throws RemoteException {
        logForSilverCompatibility(TEST_OUTPUT, "from Experiment: " + experimentName);
        DataRetrievalRequest request = new DataRetrievalRequest();
        lookupExperiment(request, experimentName);
        lookupQuantitationTypes(request, quantitationTypesCsv);

        DataSet dataSet = gridClient.getDataSet(request);
        logForSilverCompatibility(API_CALL, "Grid getDataSet(DataRetrievalRequest)");
        int numValuesRetrieved = 0;
        if (dataSet == null) {
            logForSilverCompatibility(TEST_OUTPUT, "Error: Retrieved null DataSet.");
            assertTrue("Error: Retrieved null DataSet.", false);
        }

        // Get each HybridizationData in the DataSet.
        logForSilverCompatibility(TRAVERSE_OBJECT_GRAPH, "DataSet.getHybridizationDataList().size(): "
                + dataSet.getHybridizationDataList().size());
        for (HybridizationData oneHybData : dataSet.getHybridizationDataList()) {
            // Get each column in the HybridizationData.
            logForSilverCompatibility(TRAVERSE_OBJECT_GRAPH, "HybridizationData.getColumns().size(): "
                    + oneHybData.getColumns().size());
            for (AbstractDataColumn column : oneHybData.getColumns()) {
                // Find the type of the column.
                logForSilverCompatibility(TRAVERSE_OBJECT_GRAPH, "AbstractDataColumn.getQuantitationType().getName(): "
                        + column.getQuantitationType().getName());
                QuantitationType qType = column.getQuantitationType();
                Class<?> typeClass = qType.getTypeClass();
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
                + " hybridization data elements, " + dataSet.getQuantitationTypes().size() + " quantitation types and "
                + numValuesRetrieved + " values.");
        assertTrue((dataSet.getHybridizationDataList().size() > 0) && (dataSet.getQuantitationTypes().size() > 0)
                && (numValuesRetrieved > 0));
        assertTrue((dataSet.getHybridizationDataList().size() > 0) && (dataSet.getQuantitationTypes().size() > 0));

    }

    private void lookupExperiment(DataRetrievalRequest request, String experimentName)
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
        CQLQueryResults cqlResults = gridClient.query(cqlQuery);
        logForSilverCompatibility(API_CALL, "Grid query(CQLQuery)");
        CQLQueryResultsIterator iter = new CQLQueryResultsIterator(cqlResults, CaArraySvcClient.class
                .getResourceAsStream("client-config.wsdd"));
        if (!(iter.hasNext())) {
            return;
        }
        Experiment experiment = (Experiment) iter.next();
        Set<Hybridization> allHybs = experiment.getHybridizations();
        logForSilverCompatibility(TRAVERSE_OBJECT_GRAPH, "Experiment.getHybridizations().size(): "
                + experiment.getHybridizations().size());
        if (allHybs.isEmpty()) {
            return;
        }
        request.getHybridizations().addAll(allHybs);
    }

    private void lookupQuantitationTypes(DataRetrievalRequest request, String quantitationTypesCsv)
            throws RemoteException {
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
            CQLQueryResults cqlResults = gridClient.query(cqlQuery);
            logForSilverCompatibility(API_CALL, "Grid query(CQLQuery)");
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
