//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package caarray.client.examples.download_file.java;

import gov.nih.nci.caarray.domain.data.RawArrayData;
import gov.nih.nci.caarray.domain.file.CaArrayFile;
import gov.nih.nci.caarray.domain.hybridization.Hybridization;
import gov.nih.nci.caarray.domain.project.Experiment;
import gov.nih.nci.caarray.services.CaArrayServer;
import gov.nih.nci.caarray.services.ServerConnectionException;
import gov.nih.nci.caarray.services.file.FileRetrievalService;
import gov.nih.nci.caarray.services.search.CaArraySearchService;

import java.util.List;
import java.util.Set;

import caarray.client.examples.BaseProperties;

/**
 * A client downloading file contents through CaArray's Remote Java API.
 *
 * @author Rashmi Srinivasa
 */
public class ApiFileDownload {
    private static final String EXPERIMENT_NAME = BaseProperties.AFFYMETRIX_EXPERIMENT;

    public static void main(String[] args) {
        ApiFileDownload client = new ApiFileDownload();
        try {
            CaArrayServer server = new CaArrayServer(BaseProperties.getServerHostname(), BaseProperties
                    .getServerJndiPort());
            server.connect();
            CaArraySearchService searchService = server.getSearchService();
            FileRetrievalService fileRetrievalService = server.getFileRetrievalService();
            System.out.println("Downloading File Contents from Experiment: " + EXPERIMENT_NAME);
            client.downloadFileFromExperiment(searchService, fileRetrievalService, EXPERIMENT_NAME);
        } catch (ServerConnectionException e) {
            System.out.println("Could not connect to server.");
            e.printStackTrace();
        } catch (Throwable t) {
            System.out.println("Generic error.");
            t.printStackTrace();
        }
    }

    private void downloadFileFromExperiment(CaArraySearchService searchService, FileRetrievalService fileRetrievalService,
            String experimentName) {
        Experiment experiment = lookupExperiment(searchService, experimentName);
        if (experiment == null) {
            System.out.println("Error: Could not find experiment " + experimentName);
            return;
        }
        Hybridization hybridization = getFirstHybridization(searchService, experiment);
        if (hybridization == null) {
            System.out.println("Error: Retrieved null hybridization.");
            return;
        }
        CaArrayFile dataFile = getFirstDataFile(searchService, hybridization);
        if (dataFile == null) {
            System.out.println("Error: Retrieved null data file.");
            return;
        }

        System.out.println("Downloading file " + dataFile.getName());
        byte[] byteArray = fileRetrievalService.readFile(dataFile);
        if (byteArray != null) {
            System.out.println("Retrieved " + byteArray.length + " bytes.");
        } else {
            System.out.println("Error: Retrieved null byte array.");
        }
    }

    private Experiment lookupExperiment(CaArraySearchService service, String experimentName) {
        Experiment exampleExperiment = new Experiment();
        exampleExperiment.setTitle(experimentName);

        List<Experiment> experimentList = service.search(exampleExperiment);
        if (experimentList.size() == 0) {
            return null;
        }
        return experimentList.get(0);
    }

    private Hybridization getFirstHybridization(CaArraySearchService service, Experiment experiment) {
        Set<Hybridization> allHybridizations = experiment.getHybridizations();
        for (Hybridization hybridization : allHybridizations) {
            Hybridization populatedHybridization = service.search(hybridization).get(0);
            // Yes, we're returning only the first hybridization.
            return populatedHybridization;
        }
        return null;
    }

    private CaArrayFile getFirstDataFile(CaArraySearchService service, Hybridization hybridization) {
        Set<RawArrayData> rawArrayDataSet = hybridization.getRawDataCollection();
        for (RawArrayData rawArrayData : rawArrayDataSet) {
            // Return the file associated with the first raw data.
            RawArrayData populatedArrayData = service.search(rawArrayData).get(0);
            return populatedArrayData.getDataFile();
        }

        return null;
    }
}
