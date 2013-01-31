//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.test.api.java.search;

import static org.junit.Assert.assertTrue;
import gov.nih.nci.caarray.domain.project.Experiment;
import gov.nih.nci.caarray.services.CaArrayServer;
import gov.nih.nci.caarray.services.ServerConnectionException;
import gov.nih.nci.caarray.services.search.CaArraySearchService;
import gov.nih.nci.caarray.test.api.AbstractApiTest;
import gov.nih.nci.caarray.test.base.TestProperties;
import gov.nih.nci.cagrid.cqlquery.Association;
import gov.nih.nci.cagrid.cqlquery.Attribute;
import gov.nih.nci.cagrid.cqlquery.CQLQuery;
import gov.nih.nci.cagrid.cqlquery.Group;
import gov.nih.nci.cagrid.cqlquery.LogicalOperator;
import gov.nih.nci.cagrid.cqlquery.Object;
import gov.nih.nci.cagrid.cqlquery.Predicate;

import java.util.Iterator;
import java.util.List;

import org.junit.Test;

/**
 * A client searching for experiments using CQL through CaArray's Remote Java API.
 *
 * @author Rashmi Srinivasa
 */
public class ApiCqlSearchExperiment extends AbstractApiTest {
    private static final String[] MANUFACTURER_NAMES = { "Affymetrix", "Illumina" };
    private static final String[] ORGANISM_NAMES = { "human", "black rat" };

    @Test
    public void testCqlSearchExperiment() {
        try {
            CaArrayServer server = new CaArrayServer(TestProperties.getServerHostname(), TestProperties
                    .getServerJndiPort());
            server.connect();
            CaArraySearchService searchService = server.getSearchService();
            logForSilverCompatibility(TEST_NAME, "CQL-Searching for Experiments...");
            int i = 0;
            for (String manufacturerName : MANUFACTURER_NAMES) {
                String organismName = ORGANISM_NAMES[i++];
                boolean resultIsOkay = searchExperiments(searchService, manufacturerName, organismName);
                assertTrue("Error: Response did not match request.", resultIsOkay);
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

    private boolean searchExperiments(CaArraySearchService searchService, String manufacturerName, String organismName) {
        CQLQuery cqlQuery = createCqlQuery(manufacturerName, organismName);
        List experimentList = searchService.search(cqlQuery);
        logForSilverCompatibility(API_CALL, "CaArraySearchService.search(CQLQuery)");
        boolean resultIsOkay = isResultOkay(experimentList, manufacturerName, organismName);
        if (resultIsOkay) {
            logForSilverCompatibility(TEST_OUTPUT, "Retrieved " + experimentList.size()
                    + " experiments with array provider " + manufacturerName + " and organism " + organismName + ".");
        } else {
            logForSilverCompatibility(TEST_OUTPUT, "Error: Response did not match request. Retrieved "
                    + experimentList.size() + " experiments.");
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

    private boolean isResultOkay(List experimentList, String manufacturerName, String organismName) {
        if (experimentList.isEmpty()) {
            return false;
        }

        Iterator i = experimentList.iterator();
        while (i.hasNext()) {
            Experiment retrievedExperiment = (Experiment) i.next();
            // Check if retrieved experiment matches requested search criteria.
            logForSilverCompatibility(TRAVERSE_OBJECT_GRAPH, "Experiment.getManufacturer().getName(): "
                    + retrievedExperiment.getManufacturer().getName());
            logForSilverCompatibility(TRAVERSE_OBJECT_GRAPH, "Experiment.getOrganism().getCommonName(): "
                    + retrievedExperiment.getOrganism().getCommonName());
            if ((!manufacturerName.equals(retrievedExperiment.getManufacturer().getName()))
                    || (!organismName.equals(retrievedExperiment.getOrganism().getCommonName()))) {
                return false;
            }
            // Check if retrieved experiment has mandatory fields.
            if ((retrievedExperiment.getTitle() == null) || (retrievedExperiment.getServiceType() == null)
                    || (retrievedExperiment.getAssayTypes() == null && retrievedExperiment.getManufacturer() == null)) {
                return false;
            }
            logForSilverCompatibility(TRAVERSE_OBJECT_GRAPH, "Experiment.getServiceType().getResourceKey(): "
                    + retrievedExperiment.getServiceType().getResourceKey());
            logForSilverCompatibility(TRAVERSE_OBJECT_GRAPH, "Experiment.getAssayTypes(): "
                    + retrievedExperiment.getAssayTypes());
        }
        return true;
    }
}
