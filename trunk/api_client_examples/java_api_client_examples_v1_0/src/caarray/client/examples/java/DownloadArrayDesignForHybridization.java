//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package caarray.client.examples.java;

import gov.nih.nci.caarray.external.v1_0.CaArrayEntityReference;
import gov.nih.nci.caarray.external.v1_0.array.ArrayDesign;
import gov.nih.nci.caarray.external.v1_0.data.File;
import gov.nih.nci.caarray.external.v1_0.experiment.Experiment;
import gov.nih.nci.caarray.external.v1_0.query.ExperimentSearchCriteria;
import gov.nih.nci.caarray.external.v1_0.query.HybridizationSearchCriteria;
import gov.nih.nci.caarray.external.v1_0.sample.Hybridization;
import gov.nih.nci.caarray.services.external.v1_0.CaArrayServer;
import gov.nih.nci.caarray.services.external.v1_0.InvalidReferenceException;
import gov.nih.nci.caarray.services.external.v1_0.UnsupportedCategoryException;
import gov.nih.nci.caarray.services.external.v1_0.data.DataApiUtils;
import gov.nih.nci.caarray.services.external.v1_0.data.DataService;
import gov.nih.nci.caarray.services.external.v1_0.data.DataTransferException;
import gov.nih.nci.caarray.services.external.v1_0.data.JavaDataApiUtils;
import gov.nih.nci.caarray.services.external.v1_0.search.SearchService;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Set;

/**
 * A client downloading the array design file associated with a hybridization using the caArray Java API.
 *
 * @author Rashmi Srinivasa
 */
public class DownloadArrayDesignForHybridization {
    private static SearchService searchService = null;
    private static DataService dataService = null;
    private static DataApiUtils dataServiceHelper = null;
    private static final String EXPERIMENT_TITLE = BaseProperties.AFFYMETRIX_EXPERIMENT;
    private static final String HYBRIDIZATION_NAME = BaseProperties.HYBRIDIZATION_NAME_01;

    public static void main(String[] args) {
        DownloadArrayDesignForHybridization downloader = new DownloadArrayDesignForHybridization();
        try {
            CaArrayServer server = new CaArrayServer(BaseProperties.getServerHostname(), BaseProperties
                    .getServerJndiPort());
            server.connect();
            searchService = server.getSearchService();
            dataService = server.getDataService();
            dataServiceHelper = new JavaDataApiUtils(dataService);
            System.out.println("Downloading array design file for hybridization " + HYBRIDIZATION_NAME + " in "
                    + EXPERIMENT_TITLE + "...");
            downloader.download();
        } catch (Throwable t) {
            System.out.println("Error while downloading array design file.");
            t.printStackTrace();
        }
    }

    private void download() throws InvalidReferenceException, DataTransferException, IOException, UnsupportedCategoryException {
        // Select an experiment of interest.
        CaArrayEntityReference experimentRef = selectExperiment();
        if (experimentRef == null) {
            System.err.println("Could not find experiment with the requested title.");
            return;
        }

        // Select hybridization of interest in the experiment.
        Hybridization hybridization = selectHybridization(experimentRef);
        if (hybridization == null) {
            System.err.println("Could not find hybridization with requested name in the selected experiment.");
            return;
        }

        // Get array design associated with the hybridization.
        ArrayDesign arrayDesign = hybridization.getArrayDesign();
        if (arrayDesign == null) {
            System.err.println("No array design associated with the hybridization.");
            return;
        }
        Set<File> arrayDesignFiles = arrayDesign.getFiles();

        for (File arrayDesignFile : arrayDesignFiles) {
            System.out.println("Downloading array design file " + arrayDesignFile.getMetadata().getName());
            downloadContents(arrayDesignFile.getReference());
        }
    }

    /**
     * Search for experiments and select one.
     * @throws UnsupportedCategoryException 
     * @throws InvalidReferenceException 
     */
    private CaArrayEntityReference selectExperiment() throws InvalidReferenceException, UnsupportedCategoryException  {
        // Search for experiment with the given title.
        ExperimentSearchCriteria experimentSearchCriteria = new ExperimentSearchCriteria();
        experimentSearchCriteria.setTitle(EXPERIMENT_TITLE);

        // ... OR Search for experiment with the given public identifier.
        // ExperimentSearchCriteria experimentSearchCriteria = new ExperimentSearchCriteria();
        // experimentSearchCriteria.setPublicIdentifier(EXPERIMENT_PUBLIC_IDENTIFIER);

        List<Experiment> experiments = searchService.searchForExperiments(experimentSearchCriteria, null).getResults();
        if (experiments == null || experiments.size() <= 0) {
            return null;
        }

        // Assuming that only one experiment was found, pick the first result.
        // This will always be true for a search by public identifier, but may not be true for a search by title.
        Experiment experiment = experiments.iterator().next();
        return experiment.getReference();
    }

    /**
     * Select hybridization with given name in the experiment.
     * @throws InvalidReferenceException 
     */
    private Hybridization selectHybridization(CaArrayEntityReference experimentRef) throws InvalidReferenceException  {
        HybridizationSearchCriteria searchCriteria = new HybridizationSearchCriteria();
        searchCriteria.setExperiment(experimentRef);
        searchCriteria.getNames().add(HYBRIDIZATION_NAME);
        List<Hybridization> hybridizations = searchService.searchForHybridizations(searchCriteria, null).getResults();
        if (hybridizations == null || hybridizations.size() <= 0) {
            return null;
        }

        return hybridizations.iterator().next();
    }

    private void downloadContents(CaArrayEntityReference fileRef) throws InvalidReferenceException, DataTransferException, IOException  {
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
