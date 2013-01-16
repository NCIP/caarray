//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.dataStorage.fileSystem;

import gov.nih.nci.caarray.dataStorage.DataStoreException;
import gov.nih.nci.caarray.dataStorage.StorageMetadata;
import gov.nih.nci.caarray.dataStorage.UnsupportedSchemeException;
import gov.nih.nci.caarray.dataStorage.spi.DataStorage;
import gov.nih.nci.caarray.util.CaArrayUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.util.Collection;
import java.util.Date;
import java.util.Set;
import java.util.UUID;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.input.AutoCloseInputStream;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.google.common.collect.Sets;
import com.google.common.io.ByteStreams;
import com.google.common.io.Files;
import com.google.common.io.PatternFilenameFilter;
import com.google.inject.Inject;
import com.google.inject.name.Named;

/**
 * An implementation of data storage based on storing as files in the file system.
 * 
 * @author dkokotov
 */
public class FilesystemDataStorage implements DataStorage {
    private static final Logger LOG = Logger.getLogger(FilesystemDataStorage.class);

    static final String SCHEME = "file-system";

    private final String baseDir;

    /**
     * Create a FilesystemDataStorage that uses the given directory to store the data.
     * 
     * @param baseDir the directory where where data will be stored as files
     */
    @Inject
    public FilesystemDataStorage(@Named(FileSystemStorageModule.BASE_DIR_KEY) String baseDir) {
        this.baseDir = baseDir;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public StorageMetadata add(InputStream stream, boolean compressed) throws DataStoreException {
        final String fileHandle = UUID.randomUUID().toString();
        final StorageMetadata metadata = new StorageMetadata();
        metadata.setHandle(makeHandle(fileHandle));

        final File compressedFile = compressedFile(fileHandle);
        final File uncompressedFile = uncompressedFile(fileHandle);

        try {
            if (compressed) {
                writeFileData(stream, new FileOutputStream(compressedFile));
                final FileInputStream fis = Files.newInputStreamSupplier(compressedFile).getInput();
                uncompressAndWriteFile(fis, uncompressedFile);
                IOUtils.closeQuietly(fis);
            } else {
                LOG.info("Write file");
                writeFileData(stream, new FileOutputStream(uncompressedFile));
                final FileInputStream fis = Files.newInputStreamSupplier(uncompressedFile).getInput();
                compressAndWriteFile(fis, compressedFile);
                IOUtils.closeQuietly(fis);
                LOG.info("Completed writing file");
            }
            metadata.setUncompressedSize(uncompressedFile.length());
            metadata.setCompressedSize(compressedFile.length());
            return metadata;
        } catch (final IOException e) {
            throw new DataStoreException("Could not add data", e);
        }
    }

    /**
     * {@inheritDoc}
     */
    public StorageMetadata addChunk(URI handle, InputStream stream) throws DataStoreException {
        final StorageMetadata metadata = new StorageMetadata();
        try {
            File file;
            if (handle == null) {
                final String fileHandle = UUID.randomUUID().toString();
                file = uncompressedFile(fileHandle);
                writeFileData(stream, new FileOutputStream(file));
                metadata.setHandle(makeHandle(fileHandle));
            } else {
                file = openFile(handle, false);
                writeFileData(stream, new FileOutputStream(file, true));
                metadata.setHandle(handle);
            }
            metadata.setPartialSize(file.length());
        } catch (final IOException e) {
            throw new DataStoreException("Could not add data", e);
        }
        return metadata;
    }

    /**
     * {@inheritDoc}
     */
    public StorageMetadata finalizeChunkedFile(URI handle) {
        String fileHandle = handle.getSchemeSpecificPart();
        File uncompressedFile = openFile(handle, false);
        File compressedFile = compressedFile(fileHandle);

        try {
            final FileInputStream fis = Files.newInputStreamSupplier(uncompressedFile).getInput();
            compressAndWriteFile(fis, compressedFile(handle.getSchemeSpecificPart()));
            IOUtils.closeQuietly(fis);

            final StorageMetadata metadata = new StorageMetadata();
            metadata.setHandle(handle);
            metadata.setUncompressedSize(uncompressedFile.length());
            metadata.setCompressedSize(compressedFile.length());
            return metadata;
        } catch (final IOException e) {
            throw new DataStoreException("Could not add data", e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Iterable<StorageMetadata> list() {
        final Set<StorageMetadata> metadatas = Sets.newHashSet();
        final File[] files = baseStorageDir().listFiles(new PatternFilenameFilter(".*\\.unc"));
        if (files != null) {
            for (final File file : files) {
                final StorageMetadata sm = new StorageMetadata();
                sm.setCreationTimestamp(new Date(file.lastModified()));
                sm.setHandle(handleFromFile(file));
                sm.setUncompressedSize(file.length());
                metadatas.add(sm);
            }
        }
        return metadatas;
    }

    private URI handleFromFile(File file) {
        final String fileName = file.getName();
        if (fileName.endsWith(".comp")) {
            return makeHandle(StringUtils.substringBeforeLast(fileName, ".comp"));
        } else if (fileName.endsWith(".unc")) {
            return makeHandle(StringUtils.substringBeforeLast(fileName, ".unc"));
        } else {
            throw new IllegalArgumentException("File not managed by filestorage engine: " + fileName);
        }
    }

    private URI makeHandle(String id) {
        return CaArrayUtils.makeUriQuietly(SCHEME, id);
    }

    private File baseStorageDir() {
        return new File(this.baseDir);
    }

    private File compressedFile(String fileHandle) {
        return new File(baseStorageDir(), fileHandle + ".comp");
    }

    private File uncompressedFile(String fileHandle) {
        return new File(baseStorageDir(), fileHandle + ".unc");
    }

    private File file(String fileHandle, boolean compressed) {
        return compressed ? compressedFile(fileHandle) : uncompressedFile(fileHandle);
    }

    private void writeFileData(InputStream in, OutputStream out) throws IOException {
        ByteStreams.copy(in, out);
        out.flush();
        out.close();
    }

    private void compressAndWriteFile(InputStream in, File file) throws IOException {
        writeFileData(in, new GZIPOutputStream(new FileOutputStream(file)));
    }

    private void uncompressAndWriteFile(InputStream in, File file) throws IOException {
        writeFileData(new GZIPInputStream(in), new FileOutputStream(file));
    }

    private void checkScheme(URI handle) {
        if (!SCHEME.equals(handle.getScheme())) {
            throw new UnsupportedSchemeException("Unsupported scheme: " + handle.getScheme());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void remove(URI handle) {
        checkScheme(handle);
        FileUtils.deleteQuietly(compressedFile(handle.getSchemeSpecificPart()));
        FileUtils.deleteQuietly(uncompressedFile(handle.getSchemeSpecificPart()));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void remove(Collection<URI> handles) {
        for (final URI handle : handles) {
            remove(handle);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public File openFile(URI handle, boolean compressed) throws DataStoreException {
        checkScheme(handle);
        final File file = file(handle.getSchemeSpecificPart(), compressed);
        if (!file.exists()) {
            throw new DataStoreException("No data available for handle " + handle);
        }
        return file;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public InputStream openInputStream(URI handle, boolean compressed) throws DataStoreException {
        checkScheme(handle);
        try {
            return new AutoCloseInputStream(FileUtils.openInputStream(openFile(handle, compressed)));
        } catch (final IOException e) {
            throw new DataStoreException("Could not open input stream for data: ", e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void releaseFile(URI handle, boolean compressed) {
        checkScheme(handle);
        // no-op - the file is just there
    }
}
