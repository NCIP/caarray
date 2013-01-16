//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.test.api.legacy.grid.search;

import static org.junit.Assert.assertTrue;
import gov.nih.nci.caarray.domain.sample.Extract;
import gov.nih.nci.caarray.domain.sample.Sample;
import gov.nih.nci.caarray.test.api.legacy.grid.AbstractLegacyGridApiTest;
import gov.nih.nci.cagrid.caarray.client.CaArraySvcClient;
import gov.nih.nci.cagrid.cqlquery.Attribute;
import gov.nih.nci.cagrid.cqlquery.CQLQuery;
import gov.nih.nci.cagrid.cqlquery.Object;
import gov.nih.nci.cagrid.cqlquery.Predicate;
import gov.nih.nci.cagrid.cqlresultset.CQLQueryResults;
import gov.nih.nci.cagrid.data.utilities.CQLQueryResultsIterator;

import java.rmi.RemoteException;
import java.util.Iterator;
import java.util.Set;

import org.junit.Test;

/**
 * A client searching for samples using CQL through the CaArray Grid service.
 *
 * @author Rashmi Srinivasa
 */
public class GridCqlSearchSample extends AbstractLegacyGridApiTest {
    private static final String[] NAMES = { "TK6 replicate 1" };

    @Test
    public void testCqlSearchSample() {
        try {
            logForSilverCompatibility(TEST_NAME, "Grid-CQL-Searching for Samples...");
            for (String sampleName : NAMES) {
                boolean resultIsOkay = searchSamples(sampleName);
                assertTrue("Error: Response did not match request.", resultIsOkay);
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

    private boolean searchSamples(String sampleName) throws RemoteException {
        CQLQuery cqlQuery = createCqlQuery(sampleName);
        CQLQueryResults cqlResults = gridClient.query(cqlQuery);
        logForSilverCompatibility(API_CALL, "Grid search(CQLQuery)");
        boolean resultIsOkay = isResultOkay(cqlResults, sampleName);
        if (resultIsOkay) {
            logForSilverCompatibility(TEST_OUTPUT, "Retrieved " + cqlResults.getObjectResult().length
                    + " samples with name " + sampleName + ".");
        } else {
            logForSilverCompatibility(TEST_OUTPUT, "Error: Response did not match request.");
        }
        return resultIsOkay;
    }

    private CQLQuery createCqlQuery(String sampleName) {
        CQLQuery cqlQuery = new CQLQuery();
        Object target = new Object();
        target.setName("gov.nih.nci.caarray.domain.sample.Sample");

        Attribute nameAttribute = new Attribute();
        nameAttribute.setName("name");
        nameAttribute.setValue(sampleName);
        nameAttribute.setPredicate(Predicate.EQUAL_TO);

        target.setAttribute(nameAttribute);

        cqlQuery.setTarget(target);
        return cqlQuery;
    }

    private boolean isResultOkay(CQLQueryResults cqlResults, String sampleName) {
        if (cqlResults.getObjectResult() == null) {
            return false;
        }
        Iterator iter = new CQLQueryResultsIterator(cqlResults, CaArraySvcClient.class
                .getResourceAsStream("client-config.wsdd"));
        if (!(iter.hasNext())) {
            return false;
        }
        while (iter.hasNext()) {
            Sample retrievedSample = (Sample) (iter.next());
            // Check if retrieved sample matches requested search criteria.
            logForSilverCompatibility(TRAVERSE_OBJECT_GRAPH, "Sample.getName(): " + retrievedSample.getName());
            if (!sampleName.equalsIgnoreCase(retrievedSample.getName())) {
                return false;
            }
            Set<Extract> extracts = retrievedSample.getExtracts();
            if (extracts == null) {
                logForSilverCompatibility(TRAVERSE_OBJECT_GRAPH, "Sample.getExtracts(): null");
            } else {
                logForSilverCompatibility(TRAVERSE_OBJECT_GRAPH, "Sample.getExtracts(): size = " + extracts.size());
            }
        }
        return true;
    }
}
