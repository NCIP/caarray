//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package gov.nih.nci.caarray.test.api.legacy.grid.search;

import static org.junit.Assert.assertTrue;
import gov.nih.nci.caarray.domain.contact.Person;
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

import org.junit.Test;

/**
 * A client searching for persons using CQL through the CaArray Grid service.
 *
 * @author Rashmi Srinivasa
 */
public class GridCqlSearchPerson extends AbstractLegacyGridApiTest {
    private static final String[] LAST_NAMES = { "Laufs", "Freuhauf", "Wenz", "Li", "Zeller", "Fleckenstein" };

    @Test
    public void testCqlSearchPerson() {
        try {
            logForSilverCompatibility(TEST_NAME, "Grid-CQL-Searching for Persons...");
            for (String lastName : LAST_NAMES) {
                boolean resultIsOkay = searchPersons(lastName);
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

    private boolean searchPersons(String lastName) throws RemoteException {
        CQLQuery cqlQuery = createCqlQuery(lastName);
        CQLQueryResults cqlResults = gridClient.query(cqlQuery);
        logForSilverCompatibility(API_CALL, "Grid search(CQLQuery)");
        // printCqlResultsAsXml(cqlResults, "GridCqlSearchPerson.xml");
        boolean resultIsOkay = isResultOkay(cqlResults, lastName);
        if (resultIsOkay) {
            logForSilverCompatibility(TEST_OUTPUT, "Retrieved " + cqlResults.getObjectResult().length
                    + " persons with last name " + lastName + ".");
        } else {
            logForSilverCompatibility(TEST_OUTPUT, "Error: Response did not match request.");
        }
        return resultIsOkay;
    }

    private CQLQuery createCqlQuery(String lastName) {
        CQLQuery cqlQuery = new CQLQuery();
        Object target = new Object();
        target.setName("gov.nih.nci.caarray.domain.contact.Person");

        Attribute affiliationAttribute = new Attribute();
        affiliationAttribute.setName("lastName");
        affiliationAttribute.setValue(lastName);
        affiliationAttribute.setPredicate(Predicate.EQUAL_TO);

        target.setAttribute(affiliationAttribute);

        cqlQuery.setTarget(target);
        return cqlQuery;
    }

    private boolean isResultOkay(CQLQueryResults cqlResults, String lastName) {
        if (cqlResults.getObjectResult() == null) {
            return false;
        }
        Iterator iter = new CQLQueryResultsIterator(cqlResults, CaArraySvcClient.class
                .getResourceAsStream("client-config.wsdd"));
        if (!(iter.hasNext())) {
            return false;
        }
        while (iter.hasNext()) {
            Person retrievedPerson = (Person) (iter.next());
            logForSilverCompatibility(TRAVERSE_OBJECT_GRAPH, "Person.getFirstName(): " + retrievedPerson.getFirstName());
            logForSilverCompatibility(TRAVERSE_OBJECT_GRAPH, "Person.getLastName(): " + retrievedPerson.getLastName());
            // Check if retrieved person matches requested search criteria.
            if (!lastName.equalsIgnoreCase(retrievedPerson.getLastName())) {
                return false;
            }
        }
        return true;
    }
}
