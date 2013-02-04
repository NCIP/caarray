//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package caarray.client.examples.java;

import gov.nih.nci.caarray.external.v1_0.CaArrayEntityReference;
import gov.nih.nci.caarray.external.v1_0.data.File;
import gov.nih.nci.caarray.external.v1_0.data.FileCategory;
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

import java.io.FileOutputStream;
import java.io.IOException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

/**
 * A client downloading a zip of files from an experiment using the caArray Java API.
 *
 * @author Rashmi Srinivasa
 */
public class DownloadFileZipFromExperiment {
    private static SearchService searchService = null;
    private static DataService dataService = null;
    private static SearchApiUtils searchServiceHelper = null;
    private static DataApiUtils dataServiceHelper = null;
    private static final String EXPERIMENT_TITLE = BaseProperties.AFFYMETRIX_EXPERIMENT;

    public static void main(String[] args) {
        DownloadFileZipFromExperiment downloader = new DownloadFileZipFromExperiment();
        try {
            CaArrayServer server = new CaArrayServer(BaseProperties.getServerHostname(), BaseProperties
                    .getServerJndiPort());
            server.connect();
            searchService = server.getSearchService();
            dataService = server.getDataService();
            searchServiceHelper = new JavaSearchApiUtils(searchService);
            dataServiceHelper = new JavaDataApiUtils(dataService);
            System.out.println("Downloading file zip from " + EXPERIMENT_TITLE + "...");
            downloader.download();
        } catch (Throwable t) {
            System.out.println("Error while downloading file zip.");
            t.printStackTrace();
        }
    }

    private void download() throws InvalidInputException, DataTransferException, IOException {
        CaArrayEntityReference experimentRef = searchForExperiment();
        if (experimentRef == null) {
            System.err.println("Could not find experiment with the requested title or public identifier.");
            return;
        }
        List<CaArrayEntityReference> fileRefs = searchForFiles(experimentRef);
        if (fileRefs == null) {
            System.err.println("Could not find any files that match the search criteria.");
            return;
        }
        downloadZipOfFiles(fileRefs);
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

    /**
     * Search for a certain type or category of files in an experiment.
     */
    private List<CaArrayEntityReference> searchForFiles(CaArrayEntityReference experimentRef) throws RemoteException,
            InvalidInputException {
        // Search for all raw data files in the experiment. (Experiment ref is a mandatory parameter.)
        FileSearchCriteria fileSearchCriteria = new FileSearchCriteria();
        fileSearchCriteria.setExperiment(experimentRef);
        fileSearchCriteria.getCategories().add(FileCategory.RAW_DATA);

        // Alternatively, search for all AFFYMETRIX_CEL data files)
        // CaArrayEntityReference celFileTypeRef = getCelFileType();
        // fileSearchCriteria.getTypes().add(celFileTypeRef);

        // Alternatively, search for all derived data files with extension .CHP)
        // fileSearchCriteria.getCategories().add(FileTypeCategory.DERIVED);
        // fileSearchCriteria.setExtension("CHP");

        List<File> files = searchServiceHelper.filesByCriteria(fileSearchCriteria).list();
        if (files.size() <= 0) {
            return null;
        }
        System.out.println("Matching files: " + files);

        // Return references to the files.
        List<CaArrayEntityReference> fileRefs = new ArrayList<CaArrayEntityReference>();
        for (File file : files) {
            fileRefs.add(file.getReference());
        }
        return fileRefs;
    }

    /**
     * Download a zip of the given files.
     */
    private void downloadZipOfFiles(List<CaArrayEntityReference> fileRefs) throws InvalidReferenceException, DataTransferException, IOException {
        java.io.File tempOutFile = new java.io.File("downloadedFiles.zip");
        FileOutputStream outStream = new FileOutputStream(tempOutFile);
        long startTime = System.currentTimeMillis();
        dataServiceHelper.copyFileContentsZipToOutputStream(fileRefs, outStream);
        long totalTime = System.currentTimeMillis() - startTime;
        outStream.close();
        System.out.println("Retrieved file zip in " + totalTime + " ms.");
    }
}
