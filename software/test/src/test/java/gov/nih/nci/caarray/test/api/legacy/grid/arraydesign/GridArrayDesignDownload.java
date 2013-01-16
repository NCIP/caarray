//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.test.api.legacy.grid.arraydesign;

import static org.junit.Assert.assertTrue;
import gov.nih.nci.caarray.domain.array.ArrayDesign;
import gov.nih.nci.caarray.domain.array.ArrayDesignDetails;
import gov.nih.nci.caarray.test.api.legacy.grid.AbstractLegacyGridApiTest;
import gov.nih.nci.caarray.test.base.TestProperties;
import gov.nih.nci.cagrid.caarray.client.CaArraySvcClient;
import gov.nih.nci.cagrid.cqlquery.Attribute;
import gov.nih.nci.cagrid.cqlquery.CQLQuery;
import gov.nih.nci.cagrid.cqlquery.Predicate;
import gov.nih.nci.cagrid.cqlresultset.CQLQueryResults;
import gov.nih.nci.cagrid.data.utilities.CQLQueryResultsIterator;

import java.rmi.RemoteException;

import org.junit.Test;

/**
 * A client downloading array design details through the CaArray Grid service.
 *
 * @author Rashmi Srinivasa
 */
public class GridArrayDesignDownload extends AbstractLegacyGridApiTest {
    private static final String[] ARRAY_DESIGN_NAMES = {
        TestProperties.getAffymetrixSpecificationDesignName(),
        TestProperties.getIlluminaDesignName(),
        //TestProperties.getAffymetrixHumanDesignName(), // Causes Grid server to run out of memory
        TestProperties.getGenepixDesignName()
    };

    @Test
    public void testDownloadArrayDesignDetails() {
        try {
            logForSilverCompatibility(TEST_NAME, "Grid-Downloading array design details...");
            for (String arrayDesignName : ARRAY_DESIGN_NAMES) {
                downloadDetails(arrayDesignName);
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

    private void downloadDetails(String arrayDesignName) throws RemoteException {
        logForSilverCompatibility(TEST_OUTPUT, "... from array design " + arrayDesignName);
        ArrayDesign arrayDesign = lookupArrayDesign(arrayDesignName);
        if (arrayDesign != null) {
            ArrayDesignDetails details = gridClient.getDesignDetails(arrayDesign);
            logForSilverCompatibility(API_CALL, "Grid getDesignDetails(ArrayDesign)");
            if (details != null) {
                int numFeatures = details.getFeatures().size();
                int numProbeGroups = details.getProbeGroups().size();
                int numProbes = details.getProbes().size();
                int numLogicalProbes = details.getLogicalProbes().size();
                logForSilverCompatibility(TEST_OUTPUT, "Retrieved " + numFeatures + " features, " + numProbeGroups
                        + " probe groups, " + numProbes + " probes and " + numLogicalProbes + " logical probes.");
                assertTrue((numFeatures > 0) || (numProbeGroups > 0) || (numProbes > 0) || (numLogicalProbes > 0));
            } else {
                logForSilverCompatibility(TEST_OUTPUT, "Error: Retrieved zero features, probe groups, probes and logical probes.");
                assertTrue("Error: Retrieved zero features, probe groups, probes and logical probes.", false);
            }
        } else {
            logForSilverCompatibility(TEST_OUTPUT, "Error: Could not find array design " + arrayDesignName);
            assertTrue("Error: Could not find array design.", false);
        }
    }

    private ArrayDesign lookupArrayDesign(String arrayDesignName) throws RemoteException {
        CQLQuery cqlQuery = new CQLQuery();
        gov.nih.nci.cagrid.cqlquery.Object target = new gov.nih.nci.cagrid.cqlquery.Object();
        target.setName("gov.nih.nci.caarray.domain.array.ArrayDesign");
        Attribute arrayDesignNameAttribute = new Attribute();
        arrayDesignNameAttribute.setName("name");
        arrayDesignNameAttribute.setValue(arrayDesignName);
        arrayDesignNameAttribute.setPredicate(Predicate.EQUAL_TO);
        target.setAttribute(arrayDesignNameAttribute);
        cqlQuery.setTarget(target);
        CQLQueryResults cqlResults = gridClient.query(cqlQuery);
        logForSilverCompatibility(API_CALL, "Grid query(CQLQuery)");
        CQLQueryResultsIterator iter = new CQLQueryResultsIterator(cqlResults, CaArraySvcClient.class
                .getResourceAsStream("client-config.wsdd"));
        return (ArrayDesign) iter.next();
    }
}
