//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package gov.nih.nci.caarray.test.api.legacy.grid.file;

import static org.junit.Assert.assertTrue;
import gov.nih.nci.caarray.domain.file.CaArrayFile;
import gov.nih.nci.caarray.test.api.legacy.grid.AbstractLegacyGridApiTest;
import gov.nih.nci.cagrid.caarray.client.CaArraySvcClient;
import gov.nih.nci.cagrid.caarray.util.GridTransferResultHandler;
import gov.nih.nci.cagrid.cqlquery.Attribute;
import gov.nih.nci.cagrid.cqlquery.CQLQuery;
import gov.nih.nci.cagrid.cqlquery.Predicate;
import gov.nih.nci.cagrid.cqlresultset.CQLQueryResults;
import gov.nih.nci.cagrid.data.utilities.CQLQueryResultsIterator;

import java.io.IOException;
import java.io.InputStream;
import java.rmi.RemoteException;

import org.apache.commons.io.IOUtils;
import org.cagrid.transfer.context.client.TransferServiceContextClient;
import org.cagrid.transfer.context.client.helper.TransferClientHelper;
import org.cagrid.transfer.context.stubs.types.TransferServiceContextReference;
import org.junit.Test;

/**
 * A client downloading file contents through the CaArray Grid service using "Grid Transfer":
 * a faster way to download bytes.
 *
 * @author Rashmi Srinivasa
 */
public class GridTransferFileDownload extends AbstractLegacyGridApiTest {
    private static final String[] FILE_NAMES = {
        "H_TK6 MDR1 replicate 1.cel",
        "Test3-1-121502.CHP",
        //"Human_WG-6_data.csv", // Causes Grid server to run out of memory
        //"4_1_1_x.gpr"
    };

    @Test
    public void testDownloadFileContents() {
        try {
            logForSilverCompatibility(TEST_NAME, "Grid-Downloading file contents...");
            for (String fileName : FILE_NAMES) {
                downloadContents(fileName);
            }
        } catch (RemoteException e) {
            StringBuilder trace = buildStackTrace(e);
            logForSilverCompatibility(TEST_OUTPUT, "Remote exception: " + e + "\nTrace: " + trace);
            assertTrue("Remote exception: " + e, false);
        } catch (Throwable t) {
            // Catches things like out-of-memory errors and logs them.
            StringBuilder trace = buildStackTrace(t);
            logForSilverCompatibility(TEST_OUTPUT, "Throwable: " + t + "\nTrace: " + trace);
            assertTrue("Throwable: " + t, false);
        }
    }

    private void downloadContents(String fileName) throws Exception {
        logForSilverCompatibility(TEST_OUTPUT, "... from file " + fileName);
        CaArrayFile caArrayFile = lookupFile(fileName);
        if (caArrayFile != null) {
            // Create a result handler that converts the input stream to bytes.
            GridTransferResultHandler resultHandler = new GridTransferResultHandler() {
                public java.lang.Object processRetrievedData(InputStream stream) throws IOException {
                    return IOUtils.toByteArray(stream);
                }
            };
            byte[] byteArray = readFileUsingGridTransfer(caArrayFile, resultHandler);
            logForSilverCompatibility(API_CALL, "Grid readFileUsingGridTransfer(CaArrayFile)");
            if (byteArray != null) {
                logForSilverCompatibility(TEST_OUTPUT, "Retrieved " + byteArray.length + " bytes.");
                assertTrue(byteArray.length > 0);
            } else {
                logForSilverCompatibility(TEST_OUTPUT, "Error: Retrieved null byte array.");
                assertTrue("Error: Retrieved null byte array.", false);
            }
        } else {
            logForSilverCompatibility(TEST_OUTPUT, "Error: Could not find file " + fileName);
            assertTrue("Error: Could not find file.", false);
        }
    }

    private CaArrayFile lookupFile(String fileName) throws RemoteException {
        CQLQuery cqlQuery = new CQLQuery();
        gov.nih.nci.cagrid.cqlquery.Object target = new gov.nih.nci.cagrid.cqlquery.Object();
        target.setName("gov.nih.nci.caarray.domain.file.CaArrayFile");
        Attribute fileNameAttribute = new Attribute();
        fileNameAttribute.setName("name");
        fileNameAttribute.setValue(fileName);
        fileNameAttribute.setPredicate(Predicate.EQUAL_TO);
        target.setAttribute(fileNameAttribute);
        cqlQuery.setTarget(target);
        CQLQueryResults cqlResults = gridClient.query(cqlQuery);
        logForSilverCompatibility(API_CALL, "Grid query(CQLQuery)");
        CQLQueryResultsIterator iter = new CQLQueryResultsIterator(cqlResults, CaArraySvcClient.class
                .getResourceAsStream("client-config.wsdd"));
        return (CaArrayFile) iter.next();
    }

    private byte[] readFileUsingGridTransfer(CaArrayFile file, GridTransferResultHandler resultHandler) throws Exception {
        TransferServiceContextReference fileRef = gridClient.createFileTransfer(file);
        TransferServiceContextClient transferClient = new TransferServiceContextClient(fileRef.getEndpointReference());
        InputStream stream = TransferClientHelper.getData(transferClient.getDataTransferDescriptor());
        return (byte[]) resultHandler.processRetrievedData(stream);
    }
}
