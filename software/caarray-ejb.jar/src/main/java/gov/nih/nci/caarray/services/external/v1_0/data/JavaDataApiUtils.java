//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.services.external.v1_0.data;

import gov.nih.nci.caarray.external.v1_0.CaArrayEntityReference;
import gov.nih.nci.caarray.external.v1_0.data.FileStreamableContents;
import gov.nih.nci.caarray.external.v1_0.data.MageTabFileSet;
import gov.nih.nci.caarray.services.external.v1_0.InvalidReferenceException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import com.healthmarketscience.rmiio.RemoteInputStream;
import com.healthmarketscience.rmiio.RemoteInputStreamClient;

/**
 * Implementation of DataApiUtils for the Java API.
 * 
 * @author dkokotov
 */
public class JavaDataApiUtils extends AbstractDataApiUtils {
    private final DataService dataService;

    /**
     * @param dataService the DataService EJB to use to do the API calls.
     */
    public JavaDataApiUtils(DataService dataService) {
        this.dataService = dataService;
    }

    /**
     * {@inheritDoc}
     */
    public void copyFileContentsToOutputStream(CaArrayEntityReference fileRef, boolean compressed, OutputStream os)
            throws InvalidReferenceException, DataTransferException, IOException {
        FileStreamableContents fsContents = dataService.streamFileContents(fileRef, true);
        readFully(fsContents.getContentStream(), os, !compressed);
    }
    
    /**
     * {@inheritDoc}
     */
    public void copyFileContentsZipToOutputStream(Iterable<CaArrayEntityReference> fileRefs, OutputStream ostream)
            throws InvalidReferenceException, DataTransferException, IOException {
        ZipOutputStream zos = new ZipOutputStream(ostream);
        for (CaArrayEntityReference fileRef : fileRefs) {
            FileStreamableContents fsContents = dataService.streamFileContents(fileRef, true);
            zos.putNextEntry(new ZipEntry(fsContents.getMetadata().getName()));
            readFully(fsContents.getContentStream(), zos, true);
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
            FileStreamableContents fsContents = dataService.streamFileContents(fileRef, true);
            FileOutputStream fos = FileUtils.openOutputStream(new File(dir, fsContents.getMetadata().getName()));
            readFully(fsContents.getContentStream(), fos, true);
            IOUtils.closeQuietly(fos);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected MageTabFileSet exportMageTab(CaArrayEntityReference experimentRef) throws InvalidReferenceException,
            DataTransferException {
        return dataService.exportMageTab(experimentRef);
    }

    /**
     * Read data fully from the given RemoteInputStream and write it to the given OutputStream. The RemoteInputStream is
     * always closed at the end of this method, regardless of whether an exception occurs.
     * 
     * @param ris the RemoteInputStream to read from
     * @param ostream the OutputStream to write to
     * @param decompress if true, then the data is expected to be compressed with GZip and will be decompressed before
     *            being written to the OutputStream
     * @throws IOException if there is an error reading from the RemoteInputStream or writing to the OutputStream
     */
    public static void readFully(RemoteInputStream ris, OutputStream ostream, boolean decompress) throws IOException {
        InputStream istream = null;
        try {
            istream = RemoteInputStreamClient.wrap(ris);
            if (decompress) {
                istream = new GZIPInputStream(istream);
            }
            IOUtils.copy(istream, ostream);
        } finally {
            if (istream != null) {
                istream.close();
            }
        }
    }
}
