//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.test.api.legacy.java.file;

import static org.junit.Assert.assertTrue;
import gov.nih.nci.caarray.domain.data.DerivedArrayData;
import gov.nih.nci.caarray.domain.data.RawArrayData;
import gov.nih.nci.caarray.domain.file.CaArrayFile;
import gov.nih.nci.caarray.domain.hybridization.Hybridization;
import gov.nih.nci.caarray.domain.project.Experiment;
import gov.nih.nci.caarray.services.CaArrayServer;
import gov.nih.nci.caarray.services.ServerConnectionException;
import gov.nih.nci.caarray.services.file.FileRetrievalService;
import gov.nih.nci.caarray.services.search.CaArraySearchService;
import gov.nih.nci.caarray.test.api.AbstractApiTest;
import gov.nih.nci.caarray.test.base.TestProperties;

import java.util.List;
import java.util.Set;

import org.junit.Test;

/**
 * A client downloading file contents through CaArray's Remote Java API.
 *
 * @author Rashmi Srinivasa
 */
public class ApiFileDownload extends AbstractApiTest {
    private static final String[] EXPERIMENT_NAMES = {
        TestProperties.getAffymetricSpecificationName(),
        TestProperties.getAffymetricChpName(),
        TestProperties.getIlluminaRatName(),
        TestProperties.getGenepixCowName()
    };

    @Test
    public void testDownloadFileContents() {
        try {
            CaArrayServer server = new CaArrayServer(TestProperties.getServerHostname(), TestProperties
                    .getServerJndiPort());
            server.connect();
            CaArraySearchService searchService = server.getSearchService();
            logForSilverCompatibility(TEST_NAME, "Downloading File Contents...");
            for (String experimentName : EXPERIMENT_NAMES) {
                logForSilverCompatibility(TEST_OUTPUT, "from Experiment: " + experimentName);
                downloadFileFromExperiment(server, searchService, experimentName);
            }
        } catch (ServerConnectionException e) {
            StringBuilder trace = buildStackTrace(e);
            logForSilverCompatibility(TEST_OUTPUT, "Server connection exception: " + e + "\nTrace: " + trace);
            assertTrue("Server connection exception: " + e, false);
        } catch (RuntimeException e) {
            StringBuilder trace = buildStackTrace(e);
            logForSilverCompatibility(TEST_OUTPUT, "Runtime exception: " + e + "\nTrace: " + trace);
            assertTrue("Runtime exception: " + e, false);
        } catch (Throwable t) {
            // Catches things like out-of-memory errors and logs them.
            StringBuilder trace = buildStackTrace(t);
            logForSilverCompatibility(TEST_OUTPUT, "Throwable: " + t + "\nTrace: " + trace);
            assertTrue("Throwable: " + t, false);
        }
    }

    private void downloadFileFromExperiment(CaArrayServer server, CaArraySearchService searchService, String experimentName) {
        Experiment experiment = lookupExperiment(searchService, experimentName);
        if (experiment != null) {
            Hybridization hybridization = getFirstHybridization(searchService, experiment);
            if (hybridization != null) {
                CaArrayFile dataFile = getDataFile(searchService, hybridization);
                if (dataFile != null) {
                    logForSilverCompatibility(TEST_OUTPUT, "Downloading file " + dataFile.getName());
                    FileRetrievalService fileRetrievalService = server.getFileRetrievalService();
                    byte[] byteArray = fileRetrievalService.readFile(dataFile);
                    logForSilverCompatibility(API_CALL, "FileRetrievalService.readFile(CaArrayFile)");
                    if (byteArray != null) {
                        logForSilverCompatibility(TEST_OUTPUT, "Retrieved " + byteArray.length + " bytes.");
                        assertTrue(byteArray.length > 0);
                    } else {
                        logForSilverCompatibility(TEST_OUTPUT, "Error: Retrieved null byte array.");
                        assertTrue("Error: Retrieved null byte array.", false);
                    }
                } else {
                    logForSilverCompatibility(TEST_OUTPUT, "Error: Retrieved null data file.");
                    assertTrue("Error: Retrieved null data file.", false);
                }
            } else {
                logForSilverCompatibility(TEST_OUTPUT,
                        "Error: Retrieved null hybridization for experiment with title " + experimentName);
                assertTrue("Error: Retrieved null hybridization.", false);
            }
        } else {
            logForSilverCompatibility(TEST_OUTPUT, "Error: Could not find experiment " + experimentName);
            assertTrue("Error: Could not find experiment.", false);
        }
    }

    private Experiment lookupExperiment(CaArraySearchService service, String experimentName) {
        Experiment exampleExperiment = new Experiment();
        exampleExperiment.setTitle(experimentName);

        List<Experiment> experimentList = service.search(exampleExperiment);
        logForSilverCompatibility(API_CALL, "CaArraySearchService.search(Experiment)");
        if (experimentList.size() == 0) {
            return null;
        }
        return experimentList.get(0);
    }

    private Hybridization getFirstHybridization(CaArraySearchService service, Experiment experiment) {
        Set<Hybridization> allHybridizations = experiment.getHybridizations();
        logForSilverCompatibility(TRAVERSE_OBJECT_GRAPH, "Experiment.getHybridizations().size(): "
                + experiment.getHybridizations().size());
        for (Hybridization hybridization : allHybridizations) {
            Hybridization populatedHybridization = service.search(hybridization).get(0);
            logForSilverCompatibility(API_CALL, "CaArraySearchService.search(Hybridization)");
            // Yes, we're returning only the first hybridization.
            return populatedHybridization;
        }
        return null;
    }

    private CaArrayFile getDataFile(CaArraySearchService service, Hybridization hybridization) {
        // Try to find raw data
        Set<RawArrayData> rawArrayDataSet = hybridization.getRawDataCollection();
        logForSilverCompatibility(TRAVERSE_OBJECT_GRAPH, "Hybridization.getRawDataCollection().");
        for (RawArrayData rawArrayData : rawArrayDataSet) {
            // Return the file associated with the first raw data.
            RawArrayData populatedArrayData = service.search(rawArrayData).get(0);
            logForSilverCompatibility(API_CALL, "CaArraySearchService.search(RawArrayData)");
            logForSilverCompatibility(TRAVERSE_OBJECT_GRAPH, "RawArrayData.getDataFile().");
            return populatedArrayData.getDataFile();
        }
        // If raw data doesn't exist, try to find derived data
        Set<DerivedArrayData> derivedArrayDataSet = hybridization.getDerivedDataCollection();
        logForSilverCompatibility(TRAVERSE_OBJECT_GRAPH, "Hybridization.getDerivedDataCollection().");
        for (DerivedArrayData derivedArrayData : derivedArrayDataSet) {
            // Return the file associated with the first derived data.
            DerivedArrayData populatedArrayData = service.search(derivedArrayData).get(0);
            logForSilverCompatibility(API_CALL, "CaArraySearchService.search(DerivedArrayData)");
            logForSilverCompatibility(TRAVERSE_OBJECT_GRAPH, "DerivedArrayData.getDataFile().");
            return populatedArrayData.getDataFile();
        }
        return null;
    }
}
