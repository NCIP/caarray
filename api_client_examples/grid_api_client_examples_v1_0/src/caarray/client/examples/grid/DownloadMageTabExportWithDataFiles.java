//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
package caarray.client.examples.grid;

import gov.nih.nci.caarray.external.v1_0.CaArrayEntityReference;
import gov.nih.nci.caarray.external.v1_0.experiment.Experiment;
import gov.nih.nci.caarray.external.v1_0.query.ExperimentSearchCriteria;
import gov.nih.nci.caarray.services.external.v1_0.data.DataApiUtils;
import gov.nih.nci.caarray.services.external.v1_0.grid.client.CaArraySvc_v1_0Client;
import gov.nih.nci.caarray.services.external.v1_0.grid.client.GridDataApiUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.rmi.RemoteException;
import java.util.List;

import org.apache.axis.types.URI.MalformedURIException;

/**
 * A client exporting an experiment into MAGE-TAB and downloading a zip of the IDF, SDRF and data files
 * using the caArray Grid service API.
 *
 * @author Rashmi Srinivasa
 */
public class DownloadMageTabExportWithDataFiles {
    private static CaArraySvc_v1_0Client client = null;
    private static final String EXPERIMENT_TITLE = BaseProperties.AFFYMETRIX_EXPERIMENT;
    private static DataApiUtils dataServiceHelper = null;

    public static void main(String[] args) {
        DownloadMageTabExportWithDataFiles downloader = new DownloadMageTabExportWithDataFiles();
        try {
            client = new CaArraySvc_v1_0Client(BaseProperties.getGridServiceUrl());
            dataServiceHelper = new GridDataApiUtils(client);
            System.out.println("Exporting MAGE-TAB plus data files for experiment: " + EXPERIMENT_TITLE + "...");
            downloader.download();
        } catch (Throwable t) {
            System.out.println("Error while downloading MAGE-TAB export.");
            t.printStackTrace();
        }
    }

    private void download() throws RemoteException, MalformedURIException, IOException, Exception {
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
            System.out.println("Error: Retrieved null byte array.");
        }
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
}
