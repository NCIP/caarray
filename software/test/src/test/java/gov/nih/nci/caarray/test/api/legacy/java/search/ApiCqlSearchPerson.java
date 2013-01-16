//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.test.api.legacy.java.search;

import static org.junit.Assert.assertTrue;
import gov.nih.nci.caarray.domain.contact.Person;
import gov.nih.nci.caarray.services.CaArrayServer;
import gov.nih.nci.caarray.services.ServerConnectionException;
import gov.nih.nci.caarray.services.search.CaArraySearchService;
import gov.nih.nci.caarray.test.api.AbstractApiTest;
import gov.nih.nci.caarray.test.base.TestProperties;
import gov.nih.nci.cagrid.cqlquery.Attribute;
import gov.nih.nci.cagrid.cqlquery.CQLQuery;
import gov.nih.nci.cagrid.cqlquery.Object;
import gov.nih.nci.cagrid.cqlquery.Predicate;

import java.util.Iterator;
import java.util.List;

import org.junit.Test;

/**
 * A client searching for persons using CQL through CaArray's Remote Java API.
 *
 * @author Rashmi Srinivasa
 */
public class ApiCqlSearchPerson extends AbstractApiTest {
    private static final String[] LAST_NAMES = {
        "Laufs",
        "Freuhauf",
        "Wenz",
        "Li",
        "Zeller",
        "Fleckenstein"
    };

    @Test
    public void testCqlSearchPerson() {
        try {
            CaArrayServer server = new CaArrayServer(TestProperties.getServerHostname(), TestProperties
                    .getServerJndiPort());
            server.connect();
            CaArraySearchService searchService = server.getSearchService();
            logForSilverCompatibility(TEST_NAME, "CQL-Searching for Persons...");
            for (String lastName : LAST_NAMES) {
                boolean resultIsOkay = searchPersons(searchService, lastName);
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

    private boolean searchPersons(CaArraySearchService searchService, String lastName) {
        CQLQuery cqlQuery = createCqlQuery(lastName);
        List personList = searchService.search(cqlQuery);
        logForSilverCompatibility(API_CALL, "CaArraySearchService.search(CQLQuery)");
        boolean resultIsOkay = isResultOkay(personList, lastName);
        if (resultIsOkay) {
            logForSilverCompatibility(TEST_OUTPUT, "Retrieved " + personList.size()
                    + " persons with last name " + lastName + ".");
        } else {
            logForSilverCompatibility(TEST_OUTPUT, "Error: Response did not match request. Retrieved " + personList.size()
                    + " persons.");
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

    private boolean isResultOkay(List personList, String lastName) {
        if (personList.isEmpty()) {
            return false;
        }

        Iterator i = personList.iterator();
        while (i.hasNext()) {
            Person retrievedPerson = (Person) i.next();
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
