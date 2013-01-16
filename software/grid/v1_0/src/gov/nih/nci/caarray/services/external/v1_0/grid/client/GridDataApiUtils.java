//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.services.external.v1_0.grid.client;

import gov.nih.nci.caarray.external.v1_0.CaArrayEntityReference;
import gov.nih.nci.caarray.external.v1_0.data.MageTabFileSet;
import gov.nih.nci.caarray.services.external.v1_0.IncorrectEntityTypeException;
import gov.nih.nci.caarray.services.external.v1_0.InvalidReferenceException;
import gov.nih.nci.caarray.services.external.v1_0.NoEntityMatchingReferenceException;
import gov.nih.nci.caarray.services.external.v1_0.data.AbstractDataApiUtils;
import gov.nih.nci.caarray.services.external.v1_0.data.DataTransferException;
import gov.nih.nci.caarray.services.external.v1_0.grid.stubs.types.DataStagingFault;
import gov.nih.nci.caarray.services.external.v1_0.grid.stubs.types.IncorrectEntityTypeFault;
import gov.nih.nci.caarray.services.external.v1_0.grid.stubs.types.NoEntityMatchingReferenceFault;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.rmi.RemoteException;
import java.util.zip.GZIPInputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.cagrid.transfer.context.client.TransferServiceContextClient;
import org.cagrid.transfer.context.client.helper.TransferClientHelper;
import org.cagrid.transfer.context.stubs.types.TransferServiceContextReference;
import org.cagrid.transfer.descriptor.DataTransferDescriptor;

/**
 * DataUtils is a helper class for working with the caArray Data API. It makes it easier to perform a variety of common
 * tasks.
 * 
 * @author dkokotov
 */
public class GridDataApiUtils extends AbstractDataApiUtils {
    private final CaArraySvc_v1_0Client client;

    /**
     * @param client the CaArraySvc_v1_0 client proxy to use for API calls
     */
    public GridDataApiUtils(CaArraySvc_v1_0Client client) {
        this.client = client;
    }

    /**
     * {@inheritDoc}
     */
    public void copyFileContentsToOutputStream(CaArrayEntityReference fileRef, boolean compressed, OutputStream os)
            throws InvalidReferenceException, DataTransferException, IOException {
        readFully(getFileContentsTransfer(fileRef, true), os, !compressed);
    }

    /**
     * {@inheritDoc}
     */
    public void copyFileContentsZipToOutputStream(Iterable<CaArrayEntityReference> fileRefs, OutputStream ostream)
            throws InvalidReferenceException, DataTransferException, IOException {
        ZipOutputStream zos = new ZipOutputStream(ostream);
        for (CaArrayEntityReference fileRef : fileRefs) {
            TransferServiceContextReference transferRef = getFileContentsTransfer(fileRef, true);
            addToZip(transferRef, zos);
        }
        zos.finish();
    }
    
    /**
     * {@inheritDoc}
     */
    public void downloadFileContentsToDir(Iterable<CaArrayEntityReference> fileRefs, File dir)
            throws InvalidReferenceException, DataTransferException, IOException {
        if (!dir.exists() && !dir.mkdirs()) {
            throw new IOException("Could not create temporary directory " + dir.getAbsolutePath());
        }
        for (CaArrayEntityReference fileRef : fileRefs) {
            TransferServiceContextReference transferRef = getFileContentsTransfer(fileRef, true);
            downloadToDir(transferRef, dir);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected MageTabFileSet exportMageTab(CaArrayEntityReference experimentRef) throws InvalidReferenceException,
            DataTransferException {
        try {
            return client.getMageTabExport(experimentRef);
        } catch (IncorrectEntityTypeFault f) {
            throw new IncorrectEntityTypeException(f.getCaArrayEntityReference(), GridApiUtils.getMessage(f));
        } catch (NoEntityMatchingReferenceFault f) {
            throw new NoEntityMatchingReferenceException(f.getCaArrayEntityReference(), GridApiUtils.getMessage(f));
        } catch (DataStagingFault f) {
            throw new DataTransferException(GridApiUtils.getMessage(f));
        } catch (RemoteException e) {
            throw new DataTransferException(e.getMessage());            
        }
    }    
    
    private TransferServiceContextReference getFileContentsTransfer(CaArrayEntityReference fileRef,
            boolean compressed) throws InvalidReferenceException, DataTransferException, IOException {
        try {
            return client.getFileContentsTransfer(fileRef, compressed);
        } catch (IncorrectEntityTypeFault f) {
            throw new IncorrectEntityTypeException(f.getCaArrayEntityReference(), GridApiUtils.getMessage(f));
        } catch (NoEntityMatchingReferenceFault f) {
            throw new NoEntityMatchingReferenceException(f.getCaArrayEntityReference(), GridApiUtils.getMessage(f));
        } catch (DataStagingFault f) {
            throw new DataTransferException(GridApiUtils.getMessage(f));
        } catch (RemoteException e) {
            throw new DataTransferException(e.getMessage());            
        }
    }

    /**
     * Read data fully from the given Grid Transfer resource and write it to the given OutputStream. The Grid Transfer
     * resource is always destroyed at the end of this method, regardless of whether an error occurs.
     * 
     * @param transferRef the reference to the Grid Transfer resource from which the data can be obtained
     * @param ostream the OutputStream to write the data to.
     * @param decompress if true, then the data is expected to be compressed with GZip and will be decompressed before
     *            being written to the OutputStream
     * @throws IOException if there is an error writing to the OutputStream
     * @throws DataTransferException if there is an error communicating with the Grid Transfer resource.
     */
    public static void readFully(TransferServiceContextReference transferRef, OutputStream ostream, boolean decompress)
            throws IOException, DataTransferException {
        TransferServiceContextClient tclient = null;
        try {
            tclient = new TransferServiceContextClient(transferRef.getEndpointReference());
            readFully(tclient.getDataTransferDescriptor(), ostream, decompress);
        } finally {
            if (tclient != null) {
                tclient.destroy();
            }
        }
    }

    private static void addToZip(TransferServiceContextReference transferRef, ZipOutputStream zostream)
            throws IOException, DataTransferException {
        TransferServiceContextClient tclient = null;
        try {
            tclient = new TransferServiceContextClient(transferRef.getEndpointReference());
            DataTransferDescriptor dd = tclient.getDataTransferDescriptor();
            zostream.putNextEntry(new ZipEntry(dd.getDataDescriptor().getName()));
            readFully(dd, zostream, true);
        } finally {
            if (tclient != null) {
                tclient.destroy();
            }
        }
    }

    private static void downloadToDir(TransferServiceContextReference transferRef, File dir)
            throws IOException, DataTransferException {
        TransferServiceContextClient tclient = null;
        try {
            tclient = new TransferServiceContextClient(transferRef.getEndpointReference());
            DataTransferDescriptor dd = tclient.getDataTransferDescriptor();
            FileOutputStream fos = FileUtils.openOutputStream(new File(dir, dd.getDataDescriptor().getName()));
            readFully(dd, fos, true);
            IOUtils.closeQuietly(fos);
        } finally {
            if (tclient != null) {
                tclient.destroy();
            }
        }
    }

    private static void readFully(DataTransferDescriptor dataDescriptor, OutputStream ostream, boolean decompress)
            throws IOException, DataTransferException {
        try {
            InputStream istream = TransferClientHelper.getData(dataDescriptor);
            if (decompress) {
                istream = new GZIPInputStream(istream);
            }
            IOUtils.copy(istream, ostream);
        } catch (Exception e) {
            if (e instanceof IOException) {
                throw (IOException) e;
            } else {
                throw new DataTransferException("Could not retrieve file data via Grid Transfer");
            }
        } 
    }
}
