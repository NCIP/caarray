//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package caarray.client.examples.java;

import gov.nih.nci.caarray.external.v1_0.CaArrayEntityReference;
import gov.nih.nci.caarray.external.v1_0.data.File;
import gov.nih.nci.caarray.external.v1_0.experiment.Experiment;
import gov.nih.nci.caarray.external.v1_0.query.ExperimentSearchCriteria;
import gov.nih.nci.caarray.external.v1_0.query.FileSearchCriteria;
import gov.nih.nci.caarray.services.external.v1_0.CaArrayServer;
import gov.nih.nci.caarray.services.external.v1_0.InvalidInputException;
import gov.nih.nci.caarray.services.external.v1_0.InvalidReferenceException;
import gov.nih.nci.caarray.services.external.v1_0.data.DataApiUtils;
import gov.nih.nci.caarray.services.external.v1_0.data.DataService;
import gov.nih.nci.caarray.services.external.v1_0.data.DataTransferException;
import gov.nih.nci.caarray.services.external.v1_0.data.JavaDataApiUtils;
import gov.nih.nci.caarray.services.external.v1_0.search.JavaSearchApiUtils;
import gov.nih.nci.caarray.services.external.v1_0.search.SearchApiUtils;
import gov.nih.nci.caarray.services.external.v1_0.search.SearchService;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

/**
 * A client downloading contents of a single file using the caArray Java API.
 *
 * @author Rashmi Srinivasa
 */
public class DownloadFile {
    private static SearchService searchService = null;
    private static DataService dataService = null;
    private static SearchApiUtils searchServiceHelper = null;
    private static DataApiUtils dataServiceHelper = null;
    private static final String EXPERIMENT_TITLE = BaseProperties.AFFYMETRIX_EXPERIMENT;
    private static final String DATA_FILE_NAME = BaseProperties.CEL_DATA_FILE_NAME;

    public static void main(String[] args) {
        DownloadFile fileDownloader = new DownloadFile();
        try {
            CaArrayServer server = new CaArrayServer(BaseProperties.getServerHostname(), BaseProperties
                    .getServerJndiPort());
            server.connect();
            searchService = server.getSearchService();
            dataService = server.getDataService();
            searchServiceHelper = new JavaSearchApiUtils(searchService);
            dataServiceHelper = new JavaDataApiUtils(dataService);
            System.out.println("Downloading file contents from " + DATA_FILE_NAME + " in experiment: "
                    + EXPERIMENT_TITLE + "...");
            fileDownloader.download();
        } catch (Throwable t) {
            System.err.println("Error while downloading file.");
            t.printStackTrace();
        }
    }

    private void download() throws InvalidInputException, DataTransferException, IOException {
        CaArrayEntityReference experimentRef = searchForExperiment();
        if (experimentRef == null) {
            System.err.println("Could not find experiment with the requested title.");
            return;
        }
        CaArrayEntityReference fileRef = searchForFile(experimentRef);
        if (fileRef == null) {
            System.err.println("Could not find file with the requested name.");
            return;
        }
        downloadContents(fileRef);
    }

    /**
     * Search for an experiment based on its title.
     */
    private CaArrayEntityReference searchForExperiment() throws InvalidInputException {
        // Search for experiment with the given title.
        ExperimentSearchCriteria experimentSearchCriteria = new ExperimentSearchCriteria();
        experimentSearchCriteria.setTitle(EXPERIMENT_TITLE);

        List<Experiment> experiments = (searchService.searchForExperiments(experimentSearchCriteria, null)).getResults();
        if (experiments == null || experiments.size() <= 0) {
            return null;
        }

        // Assuming that only one experiment was found, pick the first result.
        // This assumption will not always be true.
        Experiment experiment = experiments.iterator().next();
        return experiment.getReference();
    }

    /**
     * Search for a file with the given name.
     */
    private CaArrayEntityReference searchForFile(CaArrayEntityReference experimentRef) throws InvalidInputException {
        FileSearchCriteria fileSearchCriteria = new FileSearchCriteria();
        fileSearchCriteria.setExperiment(experimentRef);

        List<File> files = searchServiceHelper.filesByCriteria(fileSearchCriteria).list();
        if (files == null || files.size() <= 0) {
            return null;
        }

        for (File file : files) {
            if (DATA_FILE_NAME.equals(file.getMetadata().getName())) {
                return file.getReference();
            }
        }
        return null;
    }

    private void downloadContents(CaArrayEntityReference fileRef) throws DataTransferException, InvalidReferenceException, IOException {
        boolean compressFile = false;
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        long startTime = System.currentTimeMillis();
        dataServiceHelper.copyFileContentsToOutputStream(fileRef, compressFile, outStream);
        long totalTime = System.currentTimeMillis() - startTime;
        byte[] byteArray = outStream.toByteArray();

        if (byteArray != null) {
            System.out.println("Retrieved " + byteArray.length + " bytes in " + totalTime + " ms.");
        } else {
            System.out.println("Error: Retrieved null byte array.");
        }
    }
}
