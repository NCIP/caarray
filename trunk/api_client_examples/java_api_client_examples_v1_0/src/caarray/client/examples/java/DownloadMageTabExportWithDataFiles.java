//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package caarray.client.examples.java;

import gov.nih.nci.caarray.external.v1_0.CaArrayEntityReference;
import gov.nih.nci.caarray.external.v1_0.experiment.Experiment;
import gov.nih.nci.caarray.external.v1_0.query.ExperimentSearchCriteria;
import gov.nih.nci.caarray.services.external.v1_0.CaArrayServer;
import gov.nih.nci.caarray.services.external.v1_0.InvalidInputException;
import gov.nih.nci.caarray.services.external.v1_0.data.DataApiUtils;
import gov.nih.nci.caarray.services.external.v1_0.data.DataService;
import gov.nih.nci.caarray.services.external.v1_0.data.DataTransferException;
import gov.nih.nci.caarray.services.external.v1_0.data.JavaDataApiUtils;
import gov.nih.nci.caarray.services.external.v1_0.search.SearchService;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

/**
 * A client downloading a zip of files from an experiment using the caArray Java API.
 *
 * @author Rashmi Srinivasa
 */
public class DownloadMageTabExportWithDataFiles {
    private static SearchService searchService = null;
    private static DataService dataService = null;
    private static DataApiUtils dataServiceHelper = null;
    private static final String EXPERIMENT_TITLE = BaseProperties.AFFYMETRIX_EXPERIMENT;

    public static void main(String[] args) {
        DownloadMageTabExportWithDataFiles downloader = new DownloadMageTabExportWithDataFiles();
        try {
            CaArrayServer server = new CaArrayServer(BaseProperties.getServerHostname(), BaseProperties
                    .getServerJndiPort());
            server.connect();
            searchService = server.getSearchService();
            dataService = server.getDataService();
            dataServiceHelper = new JavaDataApiUtils(dataService);
            System.out.println("Exporting MAGE-TAB plus data files for experiment: " + EXPERIMENT_TITLE + "...");
            downloader.download();
        } catch (Throwable t) {
            System.out.println("Error while downloading MAGE-TAB export.");
            t.printStackTrace();
        }
    }

    private void download() throws InvalidInputException, DataTransferException, IOException {
        CaArrayEntityReference experimentRef = searchForExperiment();
        if (experimentRef == null) {
            System.err.println("Could not find experiment with the requested title.");
            return;
        }
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        long startTime = System.currentTimeMillis();
        dataServiceHelper.copyMageTabZipToOutputStream(experimentRef, outStream);
        long totalTime = System.currentTimeMillis() - startTime;
        byte[] byteArray = outStream.toByteArray();
        if (byteArray != null) {
            System.out.println("Retrieved " + byteArray.length + " bytes in " + totalTime + " ms.");
        } else {
            System.err.println("Error: Retrieved null byte array.");
        }
    }

    /**
     * Search for an experiment based on its title or public identifier.
     */
    private CaArrayEntityReference searchForExperiment() throws InvalidInputException {
        // Search for experiment with the given title.
        ExperimentSearchCriteria experimentSearchCriteria = new ExperimentSearchCriteria();
        experimentSearchCriteria.setTitle(EXPERIMENT_TITLE);

        // ... OR Search for experiment with the given public identifier.
        // ExperimentSearchCriteria experimentSearchCriteria = new ExperimentSearchCriteria();
        // experimentSearchCriteria.setPublicIdentifier(EXPERIMENT_PUBLIC_IDENTIFIER);

        List<Experiment> experiments = (searchService.searchForExperiments(experimentSearchCriteria, null)).getResults();
        if (experiments == null || experiments.size() <= 0) {
            return null;
        }

        // Assuming that only one experiment was found, pick the first result.
        // This will always be true for a search by public identifier, but may not be true for a search by title.
        Experiment experiment = experiments.iterator().next();
        return experiment.getReference();
    }
}
