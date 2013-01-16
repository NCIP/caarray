//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
package caarray.client.examples.grid;

import gov.nih.nci.caarray.external.v1_0.CaArrayEntityReference;
import gov.nih.nci.caarray.external.v1_0.data.File;
import gov.nih.nci.caarray.external.v1_0.experiment.Experiment;
import gov.nih.nci.caarray.external.v1_0.query.ExperimentSearchCriteria;
import gov.nih.nci.caarray.external.v1_0.query.FileSearchCriteria;
import gov.nih.nci.caarray.services.external.v1_0.InvalidInputException;
import gov.nih.nci.caarray.services.external.v1_0.grid.client.CaArraySvc_v1_0Client;
import gov.nih.nci.caarray.services.external.v1_0.grid.client.GridSearchApiUtils;
import gov.nih.nci.caarray.services.external.v1_0.search.SearchApiUtils;

import java.io.IOException;
import java.io.InputStream;
import java.rmi.RemoteException;
import java.util.List;

import org.apache.axis.types.URI.MalformedURIException;
import org.apache.commons.io.IOUtils;
import org.cagrid.transfer.context.client.TransferServiceContextClient;
import org.cagrid.transfer.context.client.helper.TransferClientHelper;
import org.cagrid.transfer.context.stubs.types.TransferServiceContextReference;

/**
 * A client downloading contents of a single file using the caArray Grid service API.
 *
 * @author Rashmi Srinivasa
 */
public class DownloadFile {
    private static CaArraySvc_v1_0Client client = null;
    private static SearchApiUtils searchServiceHelper = null;
    private static final String EXPERIMENT_TITLE = BaseProperties.AFFYMETRIX_EXPERIMENT;
    private static final String DATA_FILE_NAME = BaseProperties.CEL_DATA_FILE_NAME;

    public static void main(String[] args) {
        DownloadFile fileDownloader = new DownloadFile();
        try {
            client = new CaArraySvc_v1_0Client(BaseProperties.getGridServiceUrl());
            searchServiceHelper = new GridSearchApiUtils(client);
            System.out.println("Downloading file contents from " + DATA_FILE_NAME + " in experiment: " + EXPERIMENT_TITLE + "...");
            fileDownloader.download();
        } catch (Throwable t) {
            System.out.println("Error while downloading file.");
            t.printStackTrace();
        }
    }

    private void download() throws RemoteException, MalformedURIException, IOException, Exception {
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
    private CaArrayEntityReference searchForExperiment() throws RemoteException {
        // Search for experiment with the given title.
        ExperimentSearchCriteria experimentSearchCriteria = new ExperimentSearchCriteria();
        experimentSearchCriteria.setTitle(EXPERIMENT_TITLE);

        List<Experiment> experiments = (client.searchForExperiments(experimentSearchCriteria, null)).getResults();
        if (experiments == null || experiments.size() <= 0) {
            return null;
        }

        // Multiple experiments with the same name can exist. Here, we're picking the first result.
        Experiment experiment = experiments.get(0);
        return experiment.getReference();
    }

    /**
     * Search for a file with the given name.
     */
    private CaArrayEntityReference searchForFile(CaArrayEntityReference experimentRef) throws RemoteException, InvalidInputException {
        FileSearchCriteria fileSearchCriteria = new FileSearchCriteria();
        fileSearchCriteria.setExperiment(experimentRef);

        List<File> files = (searchServiceHelper.filesByCriteria(fileSearchCriteria)).list();
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

    private void downloadContents(CaArrayEntityReference fileRef) throws RemoteException, MalformedURIException, IOException, Exception {
        boolean compressFile = false;
        long startTime = System.currentTimeMillis();
        TransferServiceContextReference serviceContextRef = client.getFileContentsTransfer(fileRef, compressFile);
        TransferServiceContextClient transferClient = new TransferServiceContextClient(serviceContextRef.getEndpointReference());
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
