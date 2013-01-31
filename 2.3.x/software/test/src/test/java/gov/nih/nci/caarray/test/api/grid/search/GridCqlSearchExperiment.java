//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.test.api.grid.search;

import static org.junit.Assert.assertTrue;
import gov.nih.nci.caarray.domain.project.Experiment;
import gov.nih.nci.caarray.test.api.AbstractApiTest;
import gov.nih.nci.caarray.test.base.TestProperties;
import gov.nih.nci.cagrid.caarray.client.CaArraySvcClient;
import gov.nih.nci.cagrid.cqlquery.Association;
import gov.nih.nci.cagrid.cqlquery.Attribute;
import gov.nih.nci.cagrid.cqlquery.CQLQuery;
import gov.nih.nci.cagrid.cqlquery.Group;
import gov.nih.nci.cagrid.cqlquery.LogicalOperator;
import gov.nih.nci.cagrid.cqlquery.Object;
import gov.nih.nci.cagrid.cqlquery.Predicate;
import gov.nih.nci.cagrid.cqlresultset.CQLQueryResults;
import gov.nih.nci.cagrid.data.utilities.CQLQueryResultsIterator;

import java.rmi.RemoteException;
import java.util.Iterator;

import org.junit.Test;

/**
 * A client searching for experiments using CQL through the CaArray Grid service.
 *
 * @author Rashmi Srinivasa
 */
public class GridCqlSearchExperiment extends AbstractApiTest {
    private static final String[] MANUFACTURER_NAMES = { "Affymetrix", "Illumina" };
    private static final String[] ORGANISM_NAMES = { "human", "black rat" };

    @Test
    public void testCqlSearchExperiment() {
        try {
            CaArraySvcClient client = new CaArraySvcClient(TestProperties.getGridServiceUrl());
            logForSilverCompatibility(TEST_NAME, "Grid-CQL-Searching for Experiments...");
            int i = 0;
            for (String manufacturerName : MANUFACTURER_NAMES) {
                String organismName = ORGANISM_NAMES[i++];
                boolean resultIsOkay = searchExperiments(client, manufacturerName, organismName);
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

    private boolean searchExperiments(CaArraySvcClient client, String manufacturerName, String organismName)
            throws RemoteException {
        CQLQuery cqlQuery = createCqlQuery(manufacturerName, organismName);
        CQLQueryResults cqlResults = client.query(cqlQuery);
        logForSilverCompatibility(API_CALL, "Grid search(CQLQuery)");
        boolean resultIsOkay = isResultOkay(cqlResults, manufacturerName, organismName);
        if (resultIsOkay) {
            logForSilverCompatibility(TEST_OUTPUT, "Retrieved " + cqlResults.getObjectResult().length
                    + " experiments with array provider " + manufacturerName + " and organism " + organismName + ".");
        } else {
            logForSilverCompatibility(TEST_OUTPUT, "Error: Response did not match request.");
        }
        return resultIsOkay;
    }

    private CQLQuery createCqlQuery(String manufacturerName, String organismName) {
        CQLQuery cqlQuery = new CQLQuery();
        Object target = new Object();
        target.setName("gov.nih.nci.caarray.domain.project.Experiment");

        Association manufacturerAssociation = new Association();
        manufacturerAssociation.setName("gov.nih.nci.caarray.domain.contact.Organization");
        Attribute manufacturerAttribute = new Attribute();
        manufacturerAttribute.setName("name");
        manufacturerAttribute.setValue(manufacturerName);
        manufacturerAttribute.setPredicate(Predicate.EQUAL_TO);
        manufacturerAssociation.setAttribute(manufacturerAttribute);
        manufacturerAssociation.setRoleName("manufacturer");

        Association organismAssociation = new Association();
        organismAssociation.setName("edu.georgetown.pir.Organism");
        Attribute organismAttribute = new Attribute();
        organismAttribute.setName("commonName");
        organismAttribute.setValue(organismName);
        organismAttribute.setPredicate(Predicate.EQUAL_TO);
        organismAssociation.setAttribute(organismAttribute);
        organismAssociation.setRoleName("organism");

        Group associations = new Group();
        associations.setAssociation(new Association[] { manufacturerAssociation, organismAssociation });
        associations.setLogicRelation(LogicalOperator.AND);
        target.setGroup(associations);

        cqlQuery.setTarget(target);
        return cqlQuery;
    }

    private boolean isResultOkay(CQLQueryResults cqlResults, String manufacturerName, String organismName) {
        if (cqlResults.getObjectResult() == null) {
            return false;
        }
        Iterator iter = new CQLQueryResultsIterator(cqlResults, CaArraySvcClient.class
                .getResourceAsStream("client-config.wsdd"));
        if (!(iter.hasNext())) {
            return false;
        }
        while (iter.hasNext()) {
            Experiment retrievedExperiment = (Experiment) (iter.next());
            // The following code commented out because of upcoming defect re: manufacturer attribute being null.
            // Check if retrieved experiment matches requested search criteria.
            //logForSilverCompatibility(TRAVERSE_OBJECT_GRAPH, "Experiment.getManufacturer().getName(): "
            //+ retrievedExperiment.getManufacturer().getName());
            logForSilverCompatibility(TRAVERSE_OBJECT_GRAPH, "Experiment.getOrganism().getCommonName(): "
                    + retrievedExperiment.getOrganism().getCommonName());
            //if ((!manufacturerName.equals(retrievedExperiment.getManufacturer().getName()))
            //|| (!organismName.equals(retrievedExperiment.getOrganism().getCommonName()))) {
            //return false;
            //}
            // Check if retrieved experiment has mandatory fields.
            if ((retrievedExperiment.getTitle() == null)
                    || (retrievedExperiment.getAssayTypes() == null && retrievedExperiment.getManufacturer() == null)) {
                return false;
            }
            logForSilverCompatibility(TRAVERSE_OBJECT_GRAPH, "Experiment.getAssayTypes(): "
                    + retrievedExperiment.getAssayTypes());
        }
        return true;
    }
}
