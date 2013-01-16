//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.test.api.legacy.java.arraydesign;

import static org.junit.Assert.assertTrue;
import gov.nih.nci.caarray.domain.array.ArrayDesign;
import gov.nih.nci.caarray.domain.array.ArrayDesignDetails;
import gov.nih.nci.caarray.services.CaArrayServer;
import gov.nih.nci.caarray.services.ServerConnectionException;
import gov.nih.nci.caarray.services.arraydesign.ArrayDesignDetailsService;
import gov.nih.nci.caarray.services.search.CaArraySearchService;
import gov.nih.nci.caarray.test.api.AbstractApiTest;
import gov.nih.nci.caarray.test.base.TestProperties;

import java.util.List;

import org.junit.Test;

/**
 * A client downloading the details of an array design through CaArray's Remote Java API.
 *
 * @author Rashmi Srinivasa
 */
public class ApiArrayDesignDownload extends AbstractApiTest {
    private static final String[] ARRAY_DESIGN_NAMES = {
        TestProperties.getAffymetrixSpecificationDesignName(),
        TestProperties.getIlluminaDesignName(),
        //TestProperties.getAffymetrixHumanDesignName(), // Causes server to run out of memory
        TestProperties.getGenepixDesignName()
    };

    @Test
    public void testDownloadArrayDesignDetails() {
        try {
            CaArrayServer server = new CaArrayServer(TestProperties.getServerHostname(), TestProperties
                    .getServerJndiPort());
            server.connect();
            CaArraySearchService searchService = server.getSearchService();
            logForSilverCompatibility(TEST_NAME, "Downloading Array Design details...");
            for (String arrayDesignName : ARRAY_DESIGN_NAMES) {
                logForSilverCompatibility(TEST_OUTPUT, "from Experiment: " + arrayDesignName);
                downloadDetailsFromArrayDesign(server, searchService, arrayDesignName);
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

    private void downloadDetailsFromArrayDesign(CaArrayServer server, CaArraySearchService searchService, String arrayDesignName) {
        ArrayDesign arrayDesign = lookupArrayDesign(searchService, arrayDesignName);
        if (arrayDesign != null) {
            ArrayDesignDetailsService arrayDesignDetailsService = server.getArrayDesignDetailsService();
            ArrayDesignDetails details = arrayDesignDetailsService.getDesignDetails(arrayDesign);
            logForSilverCompatibility(API_CALL, "ArrayDesignDetailsService.getDesignDetails()");
            if (details != null) {
                logForSilverCompatibility(TEST_OUTPUT, "Retrieved " + arrayDesignName + " with " + details.getFeatures().size() + " features, "
                        + details.getProbeGroups().size() + " probe groups, " + details.getProbes().size()
                        + " probes and " + details.getLogicalProbes().size() + " logical probes.");
                assertTrue(true);
            } else {
                logForSilverCompatibility(TEST_OUTPUT, "Error: Array Design Details was null.");
                assertTrue("Error: Array Design Details was null.", false);
            }
        } else {
            logForSilverCompatibility(TEST_OUTPUT, "Error: Array Design was null.");
            assertTrue("Error: Array Design was null.", false);
        }
    }

    private ArrayDesign lookupArrayDesign(CaArraySearchService service, String arrayDesignName) {
        ArrayDesign exampleArrayDesign = new ArrayDesign();
        exampleArrayDesign.setName(arrayDesignName);

        List<ArrayDesign> arrayDesignList = service.search(exampleArrayDesign);
        logForSilverCompatibility(API_CALL, "CaArraySearchService.search(ArrayDesign)");
        int numArrayDesignsFound = arrayDesignList.size();
        if (numArrayDesignsFound == 0) {
            return null;
        }
        ArrayDesign arrayDesign = arrayDesignList.get(0);
        return arrayDesign;
    }

}
