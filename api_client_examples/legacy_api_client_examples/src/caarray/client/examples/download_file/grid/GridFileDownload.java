//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package caarray.client.examples.download_file.grid;

import gov.nih.nci.caarray.domain.file.CaArrayFile;
import gov.nih.nci.cagrid.caarray.client.CaArraySvcClient;
import gov.nih.nci.cagrid.cqlquery.Attribute;
import gov.nih.nci.cagrid.cqlquery.CQLQuery;
import gov.nih.nci.cagrid.cqlquery.Predicate;
import gov.nih.nci.cagrid.cqlresultset.CQLQueryResults;
import gov.nih.nci.cagrid.data.utilities.CQLQueryResultsIterator;
import gov.nih.nci.cagrid.caarray.util.GridTransferResultHandler;
import caarray.client.examples.BaseProperties;

import java.io.IOException;
import java.io.InputStream;
import java.rmi.RemoteException;

import org.apache.commons.io.IOUtils;
import org.cagrid.transfer.context.client.TransferServiceContextClient;
import org.cagrid.transfer.context.client.helper.TransferClientHelper;
import org.cagrid.transfer.context.stubs.types.TransferServiceContextReference;

/**
 * A client downloading file contents through the CaArray Grid service.
 *
 * @author Rashmi Srinivasa
 */
public class GridFileDownload {
    private static final String FILE_NAME = "Test3-1-121502.CEL";

    public static void main(String[] args) {
        GridFileDownload gridClient = new GridFileDownload();
        try {
            CaArraySvcClient client = new CaArraySvcClient(BaseProperties.getGridServiceUrl());
            System.out.println("Grid-Downloading file contents from " + FILE_NAME + "...");
            gridClient.downloadContents(client, FILE_NAME);
        } catch (RemoteException e) {
            System.out.println("Remote server threw an exception.");
            e.printStackTrace();
        } catch (Throwable t) {
            // Catches things like out-of-memory errors and logs them.
            System.out.println("Generic error.");
            t.printStackTrace();
        }
    }

    private void downloadContents(CaArraySvcClient client, String fileName) throws Exception {
        CaArrayFile caArrayFile = lookupFile(client, fileName);
        if (caArrayFile == null) {
            System.out.println("Error: Could not find file " + fileName);
            return;
        }
        // Create a result handler that converts the input stream to bytes.
        GridTransferResultHandler resultHandler = new GridTransferResultHandler() {
            public java.lang.Object processRetrievedData(InputStream stream) throws IOException {
                return IOUtils.toByteArray(stream);
            }
        };
        byte[] byteArray = readFileUsingGridTransfer(client, caArrayFile, resultHandler);

        if (byteArray != null) {
            System.out.println("Retrieved " + byteArray.length + " bytes.");
        } else {
            System.out.println("Error: Retrieved null byte array.");
        }
    }

    private CaArrayFile lookupFile(CaArraySvcClient client, String fileName) throws RemoteException {
        CQLQuery cqlQuery = new CQLQuery();
        gov.nih.nci.cagrid.cqlquery.Object target = new gov.nih.nci.cagrid.cqlquery.Object();
        target.setName("gov.nih.nci.caarray.domain.file.CaArrayFile");
        Attribute fileNameAttribute = new Attribute();
        fileNameAttribute.setName("name");
        fileNameAttribute.setValue(fileName);
        fileNameAttribute.setPredicate(Predicate.EQUAL_TO);
        target.setAttribute(fileNameAttribute);
        cqlQuery.setTarget(target);
        CQLQueryResults cqlResults = client.query(cqlQuery);
        CQLQueryResultsIterator iter = new CQLQueryResultsIterator(cqlResults, CaArraySvcClient.class
                .getResourceAsStream("client-config.wsdd"));
        if (!(iter.hasNext())) {
            return null;
        }
        return (CaArrayFile) iter.next();
    }

    private byte[] readFileUsingGridTransfer(CaArraySvcClient client, CaArrayFile file, GridTransferResultHandler resultHandler) throws Exception {
        TransferServiceContextReference serviceContextRef = client.createFileTransfer(file);
        TransferServiceContextClient transferClient = new TransferServiceContextClient(serviceContextRef.getEndpointReference());
        InputStream stream = TransferClientHelper.getData(transferClient.getDataTransferDescriptor());
        return (byte[]) resultHandler.processRetrievedData(stream);
    }
}
