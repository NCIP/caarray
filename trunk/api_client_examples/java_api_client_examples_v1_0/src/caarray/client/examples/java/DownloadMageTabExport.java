//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package caarray.client.examples.java;

import gov.nih.nci.caarray.external.v1_0.CaArrayEntityReference;
import gov.nih.nci.caarray.external.v1_0.data.File;
import gov.nih.nci.caarray.external.v1_0.data.MageTabFileSet;
import gov.nih.nci.caarray.external.v1_0.experiment.Experiment;
import gov.nih.nci.caarray.external.v1_0.query.ExperimentSearchCriteria;
import gov.nih.nci.caarray.services.external.v1_0.CaArrayServer;
import gov.nih.nci.caarray.services.external.v1_0.InvalidInputException;
import gov.nih.nci.caarray.services.external.v1_0.data.DataService;
import gov.nih.nci.caarray.services.external.v1_0.data.DataTransferException;
import gov.nih.nci.caarray.services.external.v1_0.search.SearchService;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.IOUtils;

/**
 * A client exporting an experiment into MAGE-TAB and downloading the IDF, SDRF and references
 * to data files using the caArray Java API.
 *
 * @author Rashmi Srinivasa
 */
public class DownloadMageTabExport {
    private static SearchService searchService = null;
    private static DataService dataService = null;
    private static final String EXPERIMENT_TITLE = BaseProperties.AFFYMETRIX_EXPERIMENT;

    public static void main(String[] args) {
        DownloadMageTabExport downloader = new DownloadMageTabExport();
        try {
            CaArrayServer server = new CaArrayServer(BaseProperties.getServerHostname(), BaseProperties
                    .getServerJndiPort());
            server.connect();
            searchService = server.getSearchService();
            dataService = server.getDataService();
            System.out.println("Exporting MAGE-TAB of experiment: " + EXPERIMENT_TITLE + "...");
            downloader.download();
        } catch (Throwable t) {
            System.out.println("Error while downloading MAGE-TAB export.");
            t.printStackTrace();
        }
    }

    private void download() throws InvalidInputException, DataTransferException, IOException  {
        CaArrayEntityReference experimentRef = searchForExperiment();
        if (experimentRef == null) {
            System.err.println("Could not find experiment with the requested title.");
            return;
        }
        long startTime = System.currentTimeMillis();
        MageTabFileSet fileSet = dataService.exportMageTab(experimentRef);
        long totalTime = System.currentTimeMillis() - startTime;
        byte[] idfContents = fileSet.getIdf().getContents();
        byte[] sdrfContents = fileSet.getSdrf().getContents();
        Set<File> dataFiles = fileSet.getDataFiles();
        int bytesRetrieved = idfContents.length + sdrfContents.length;
        int numDataFileRefs = dataFiles == null || dataFiles.size() <= 0 ? 0 : dataFiles.size();

        System.out.println("Retrieved " + bytesRetrieved + " bytes and " + numDataFileRefs + " data file references in " + totalTime + " ms.");
        System.out.println("IDF:");
        IOUtils.write(idfContents, System.out);
        System.out.println("SDRF:");
        IOUtils.write(sdrfContents, System.out);
        
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

        // Multiple experiments with the same name can exist. Here, we're picking the first result.
        Experiment experiment = experiments.iterator().next();
        return experiment.getReference();
    }
}
