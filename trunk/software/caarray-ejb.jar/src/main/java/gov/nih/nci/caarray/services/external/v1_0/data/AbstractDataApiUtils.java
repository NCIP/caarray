//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.services.external.v1_0.data;

import gov.nih.nci.caarray.external.v1_0.CaArrayEntityReference;
import gov.nih.nci.caarray.external.v1_0.data.MageTabFileSet;
import gov.nih.nci.caarray.services.external.v1_0.InvalidReferenceException;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.rmi.server.UID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

/**
 * Base implementation of the DataApiUtils interface.
 * 
 * @author dkokotov
 */
public abstract class AbstractDataApiUtils implements DataApiUtils {
    /**
     * {@inheritDoc}
     */
    public File downloadFileContentsToTempFile(CaArrayEntityReference fileRef, boolean compressed)
            throws InvalidReferenceException, DataTransferException, IOException {
        File tempFile = File.createTempFile("retrievedFile", null);
        downloadFileContentsToFile(fileRef, compressed, tempFile);
        return tempFile;
    }

    /**
     * {@inheritDoc}
     */
    public void downloadFileContentsToFile(CaArrayEntityReference fileRef, boolean compressed, File toFile)
            throws InvalidReferenceException, DataTransferException, IOException {
        OutputStream ostream = FileUtils.openOutputStream(toFile);
        try {
            copyFileContentsToOutputStream(fileRef, compressed, ostream);
        } finally {
            if (ostream != null) {
                ostream.close();
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public byte[] getFileContents(CaArrayEntityReference fileRef, boolean compressed) throws InvalidReferenceException,
            DataTransferException, IOException {
        ByteArrayOutputStream ostream = new ByteArrayOutputStream();
        try {
            copyFileContentsToOutputStream(fileRef, compressed, ostream);
            return ostream.toByteArray();
        } finally {
            if (ostream != null) {
                ostream.close();
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public File downloadFileContentsZipToTempFile(Iterable<CaArrayEntityReference> fileRefs)
            throws InvalidReferenceException, DataTransferException, IOException {
        File tempFile = File.createTempFile("retrievedFile", null);
        downloadFileContentsZipToFile(fileRefs, tempFile);
        return tempFile;
    }

    /**
     * {@inheritDoc}
     */
    public void downloadFileContentsZipToFile(Iterable<CaArrayEntityReference> fileRefs, File toFile)
            throws InvalidReferenceException, DataTransferException, IOException {
        OutputStream ostream = FileUtils.openOutputStream(toFile);
        try {
            copyFileContentsZipToOutputStream(fileRefs, ostream);
        } finally {
            if (ostream != null) {
                ostream.close();
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public File downloadFileContentsToTempDir(Iterable<CaArrayEntityReference> fileRefs)
            throws InvalidReferenceException, DataTransferException, IOException {
        String tempDirName = new UID().toString().replace(':', '_');
        File tempDir = new File(System.getProperty("java.io.tmpdir"), tempDirName);
        downloadFileContentsToDir(fileRefs, tempDir);
        return tempDir;
    }

    /**
     * {@inheritDoc}
     */
    public File downloadMageTabZipToTempFile(CaArrayEntityReference experimentRef) throws InvalidReferenceException,
            DataTransferException, IOException {
        File tempFile = File.createTempFile("retrievedFile", null);
        downloadMageTabZipToFile(experimentRef, tempFile);
        return tempFile;
    }

    /**
     * {@inheritDoc}
     */
    public void downloadMageTabZipToFile(CaArrayEntityReference experimentRef, File toFile)
            throws InvalidReferenceException, DataTransferException, IOException {
        OutputStream ostream = FileUtils.openOutputStream(toFile);
        try {
            copyMageTabZipToOutputStream(experimentRef, ostream);
        } finally {
            if (ostream != null) {
                ostream.close();
            }
        }
    }
    
    /**
     * {@inheritDoc}
     */
    public void copyMageTabZipToOutputStream(CaArrayEntityReference experimentRef, OutputStream ostream)
            throws InvalidReferenceException, DataTransferException, IOException {
        MageTabFileSet mtset = exportMageTab(experimentRef);
        ZipOutputStream zos = new ZipOutputStream(ostream);
        zos.putNextEntry(new ZipEntry(mtset.getIdf().getMetadata().getName()));
        IOUtils.write(mtset.getIdf().getContents(), zos);
        zos.putNextEntry(new ZipEntry(mtset.getSdrf().getMetadata().getName()));
        IOUtils.write(mtset.getSdrf().getContents(), zos);
        for (gov.nih.nci.caarray.external.v1_0.data.File dataFile : mtset.getDataFiles()) {
            zos.putNextEntry(new ZipEntry(dataFile.getMetadata().getName()));
            copyFileContentsToOutputStream(dataFile.getReference(), false, zos);
        }
        zos.finish();
    }


    /**
     * {@inheritDoc}
     */
    public File downloadMageTabFilesetToTempDir(CaArrayEntityReference experimentRef)
            throws InvalidReferenceException, DataTransferException, IOException {
        String tempDirName = new UID().toString().replace(':', '_');
        File tempDir = new File(System.getProperty("java.io.tmpdir"), tempDirName);
        downloadMageTabFileSetToDir(experimentRef, tempDir);
        return tempDir;
    }

    /**
     * {@inheritDoc}
     */
    public void downloadMageTabFileSetToDir(CaArrayEntityReference experimentRef, File dir)
            throws InvalidReferenceException, DataTransferException, IOException {
        if (!dir.exists() && !dir.mkdirs()) {
            throw new IOException("Could not create temporary directory " + dir.getAbsolutePath());
        }

        MageTabFileSet mtset = exportMageTab(experimentRef);
        File idf = new File(dir, mtset.getIdf().getMetadata().getName());
        FileUtils.writeByteArrayToFile(idf, mtset.getIdf().getContents());
        File sdrf = new File(dir, mtset.getSdrf().getMetadata().getName());
        FileUtils.writeByteArrayToFile(sdrf, mtset.getSdrf().getContents());
        for (gov.nih.nci.caarray.external.v1_0.data.File dataFile : mtset.getDataFiles()) {
            File dataFileOnDisk = new File(dir, dataFile.getMetadata().getName());
            downloadFileContentsToFile(dataFile.getReference(), false, dataFileOnDisk);
        }
    }
    
    /**
     * Retrieves a set of files containing the mage-tab IDF and SDRF for the experiment identified by the given
     * reference. The IDF and SDRF are generated dynamically. The file set also contains references to the data files
     * referenced by the mage-tab SDRF. This method just reflects the corresponding method from DataService, which
     * subclasses must route to appropriate service implementation.
     * 
     * @param experimentRef reference identifying the experiment
     * @return the set of IDF and SDRF files, and references to corresponding data files.
     * @throws InvalidReferenceException if the experimentRef is not a valid experiment reference.
     * @throws DataTransferException if there is an error generating the mage-tab file data
     */    
    protected abstract MageTabFileSet exportMageTab(CaArrayEntityReference experimentRef)
            throws InvalidReferenceException, DataTransferException;
}
