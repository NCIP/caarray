//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
package caarray.client.examples.grid;

 import gov.nih.nci.caarray.external.v1_0.CaArrayEntityReference;
import gov.nih.nci.caarray.external.v1_0.array.ArrayDesign;
import gov.nih.nci.caarray.external.v1_0.data.File;
import gov.nih.nci.caarray.external.v1_0.experiment.Experiment;
import gov.nih.nci.caarray.external.v1_0.query.ExperimentSearchCriteria;
import gov.nih.nci.caarray.external.v1_0.query.HybridizationSearchCriteria;
import gov.nih.nci.caarray.external.v1_0.sample.Hybridization;
import gov.nih.nci.caarray.services.external.v1_0.grid.client.CaArraySvc_v1_0Client;

import java.io.IOException;
import java.io.InputStream;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Set;

import org.apache.axis.types.URI.MalformedURIException;
import org.apache.commons.io.IOUtils;
import org.cagrid.transfer.context.client.TransferServiceContextClient;
import org.cagrid.transfer.context.client.helper.TransferClientHelper;
import org.cagrid.transfer.context.stubs.types.TransferServiceContextReference;

/**
 * A client downloading the array design file associated with a hybridization using the caArray Grid service API.
 *
 * @author Rashmi Srinivasa
 */
public class DownloadArrayDesignForHybridization {
    private static CaArraySvc_v1_0Client client = null;
    private static final String EXPERIMENT_TITLE = BaseProperties.AFFYMETRIX_EXPERIMENT;
    private static final String HYBRIDIZATION_NAME = BaseProperties.HYBRIDIZATION_NAME_01;

    public static void main(String[] args) {
        DownloadArrayDesignForHybridization downloader = new DownloadArrayDesignForHybridization();
        try {
            client = new CaArraySvc_v1_0Client(BaseProperties.getGridServiceUrl());
            System.out.println("Downloading array design file for hybridization " + HYBRIDIZATION_NAME + " in "
                    + EXPERIMENT_TITLE + "...");
            downloader.download();
        } catch (Throwable t) {
            System.out.println("Error while downloading array design file.");
            t.printStackTrace();
        }
    }

    private void download() throws RemoteException, MalformedURIException, IOException, Exception {
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
     */
    private CaArrayEntityReference selectExperiment() throws RemoteException {
        // Search for experiment with the given title.
        ExperimentSearchCriteria experimentSearchCriteria = new ExperimentSearchCriteria();
        experimentSearchCriteria.setTitle(EXPERIMENT_TITLE);

        // ... OR Search for experiment with the given public identifier.
        // ExperimentSearchCriteria experimentSearchCriteria = new ExperimentSearchCriteria();
        // experimentSearchCriteria.setPublicIdentifier(EXPERIMENT_PUBLIC_IDENTIFIER);

        List<Experiment> experiments = (client.searchForExperiments(experimentSearchCriteria, null)).getResults();
        if (experiments == null || experiments.size() <= 0) {
            return null;
        }

        // Assuming that only one experiment was found, pick the first result.
        // This will always be true for a search by public identifier, but may not be true for a search by title.
        Experiment experiment = experiments.get(0);
        return experiment.getReference();
    }

    /**
     * Select hybridization with given name in the experiment.
     */
    private Hybridization selectHybridization(CaArrayEntityReference experimentRef) throws RemoteException {
        HybridizationSearchCriteria searchCriteria = new HybridizationSearchCriteria();
        searchCriteria.setExperiment(experimentRef);
        searchCriteria.getNames().add(HYBRIDIZATION_NAME);
        List<Hybridization> hybridizations = (client.searchForHybridizations(searchCriteria, null)).getResults();
        if (hybridizations == null || hybridizations.size() <= 0) {
            return null;
        }

        return hybridizations.get(0);
    }

    private void downloadContents(CaArrayEntityReference fileRef) throws RemoteException, MalformedURIException,
            IOException, Exception {
        boolean compressFile = false;
        long startTime = System.currentTimeMillis();
        TransferServiceContextReference serviceContextRef = client.getFileContentsTransfer(fileRef, compressFile);
        TransferServiceContextClient transferClient = new TransferServiceContextClient(serviceContextRef
                .getEndpointReference());
        InputStream stream = TransferClientHelper.getData(transferClient.getDataTransferDescriptor());
        long totalTime = System.currentTimeMillis() - startTime;
        byte[] byteArray = IOUtils.toByteArray(stream);

        if (byteArray != null) {
            System.out.println("Retrieved " + byteArray.length + " bytes in " + totalTime + " ms.");
        } else {
            System.out.println("Error: Retrieved null byte array.");
        }
    }
}
