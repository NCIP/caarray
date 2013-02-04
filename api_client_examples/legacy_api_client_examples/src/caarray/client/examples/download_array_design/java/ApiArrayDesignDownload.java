//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package caarray.client.examples.download_array_design.java;

import gov.nih.nci.caarray.domain.array.ArrayDesign;
import gov.nih.nci.caarray.domain.array.ArrayDesignDetails;
import gov.nih.nci.caarray.services.CaArrayServer;
import gov.nih.nci.caarray.services.ServerConnectionException;
import gov.nih.nci.caarray.services.arraydesign.ArrayDesignDetailsService;
import gov.nih.nci.caarray.services.search.CaArraySearchService;
import caarray.client.examples.BaseProperties;

import java.util.List;

/**
 * A client downloading the details of an array design through CaArray's Remote Java API.
 *
 * @author Rashmi Srinivasa
 */
public class ApiArrayDesignDownload {
    private static final String ARRAY_DESIGN_NAME = BaseProperties.AFFYMETRIX_DESIGN;

    public static void main(String[] args) {
        ApiArrayDesignDownload client = new ApiArrayDesignDownload();
        try {
            CaArrayServer server = new CaArrayServer(BaseProperties.getServerHostname(), BaseProperties
                    .getServerJndiPort());
            server.connect();
            CaArraySearchService searchService = server.getSearchService();
            System.out.println("Downloading array design details for " + ARRAY_DESIGN_NAME);
            client.downloadDetailsFromArrayDesign(server, searchService, ARRAY_DESIGN_NAME);
        } catch (ServerConnectionException e) {
            System.out.println("Could not connect to server.");
            e.printStackTrace();
        } catch (Throwable t) {
            System.out.println("Generic error.");
            t.printStackTrace();
        }
    }

    private void downloadDetailsFromArrayDesign(CaArrayServer server, CaArraySearchService searchService,
            String arrayDesignName) {
        ArrayDesign arrayDesign = lookupArrayDesign(searchService, arrayDesignName);
        if (arrayDesign == null) {
            System.out.println("Error: Could not find array design.");
            return;
        }
        ArrayDesignDetailsService arrayDesignDetailsService = server.getArrayDesignDetailsService();
        ArrayDesignDetails details = arrayDesignDetailsService.getDesignDetails(arrayDesign);
        if (details != null) {
            System.out.println("Retrieved " + details.getFeatures().size() + " features, "
                    + details.getProbeGroups().size() + " probe group(s), " + details.getProbes().size()
                    + " physical probes and " + details.getLogicalProbes().size() + " logical probes.");
        } else {
            System.out.println("Error: Retrieved null array design details.");
        }
    }

    private ArrayDesign lookupArrayDesign(CaArraySearchService service, String arrayDesignName) {
        ArrayDesign exampleArrayDesign = new ArrayDesign();
        exampleArrayDesign.setName(arrayDesignName);

        List<ArrayDesign> arrayDesignList = service.search(exampleArrayDesign);
        if (arrayDesignList.size() == 0) {
            return null;
        }
        return arrayDesignList.get(0);
    }
}
