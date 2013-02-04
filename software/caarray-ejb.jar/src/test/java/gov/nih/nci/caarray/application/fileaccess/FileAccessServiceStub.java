//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package gov.nih.nci.caarray.application.fileaccess;

import gov.nih.nci.caarray.dataStorage.DataStorageFacade;
import gov.nih.nci.caarray.dataStorage.DataStoreException;
import gov.nih.nci.caarray.dataStorage.StorageMetadata;
import gov.nih.nci.caarray.dataStorage.UnsupportedSchemeException;
import gov.nih.nci.caarray.dataStorage.spi.DataStorage;
import gov.nih.nci.caarray.domain.file.CaArrayFile;
import gov.nih.nci.caarray.domain.file.FileStatus;
import gov.nih.nci.caarray.domain.file.FileType;
import gov.nih.nci.caarray.domain.file.FileTypeRegistry;
import gov.nih.nci.caarray.domain.file.FileTypeRegistryImpl;
import gov.nih.nci.caarray.platforms.spi.DataFileHandler;
import gov.nih.nci.caarray.platforms.spi.DesignFileHandler;
import gov.nih.nci.caarray.util.CaArrayUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.input.AutoCloseInputStream;
import org.apache.commons.lang.BooleanUtils;

import com.google.inject.Inject;
import com.google.inject.internal.Lists;
import com.google.inject.internal.Maps;

/**
 * Stub implementation for testing.
 */
public class FileAccessServiceStub implements FileAccessService, DataStorage {
    public static final String SCHEME = "fsstub";

    private final Map<String, File> nameToFile = new HashMap<String, File>();
    private final Map<String, Boolean> deletables = new HashMap<String, Boolean>();
    private int savedFileCount = 0;
    private int removedFileCount = 0;
    private FileTypeRegistry typeRegistry;

    public FileAccessServiceStub() {
        this(new FileTypeRegistryImpl(Collections.<DataFileHandler> emptySet(),
                Collections.<DesignFileHandler> emptySet()));
    }

    @Inject
    public FileAccessServiceStub(FileTypeRegistry typeRegistry) {
        this.typeRegistry = typeRegistry;
    }

    @Override
    public CaArrayFile add(File file) {
        CaArrayFile parent = null;
        return add(file, parent);
    }

    public CaArrayFile add(File file, CaArrayFile parent) {
        final CaArrayFile caArrayFile = new CaArrayFile(parent);
        caArrayFile.setName(file.getName());
        caArrayFile.setFileStatus(FileStatus.UPLOADED);
        if (parent != null) {
            caArrayFile.setProject(parent.getProject());
            parent.addChild(caArrayFile);
        }
        setTypeFromExtension(caArrayFile, file.getName());
        this.nameToFile.put(caArrayFile.getName(), file);
        caArrayFile.setDataHandle(CaArrayUtils.makeUriQuietly(SCHEME, file.getName()));
        return caArrayFile;
    }

    private void setTypeFromExtension(CaArrayFile caArrayFile, String filename) {
        final FileType type = this.typeRegistry.getTypeFromExtension(filename);
        if (type != null) {
            caArrayFile.setFileType(type);
        }
    }

    public File createFile(String fileName) {
        this.savedFileCount++;
        return new File(fileName);
    }

    @Override
    public CaArrayFile add(File file, String filename) {
        return add(file, filename, null);
    }

    public CaArrayFile add(File file, String filename, CaArrayFile parent) {
        final CaArrayFile caArrayFile = new CaArrayFile(parent);
        caArrayFile.setName(filename);
        caArrayFile.setFileStatus(FileStatus.UPLOADED);
        if (parent != null) {
            caArrayFile.setProject(parent.getProject());
            parent.addChild(caArrayFile);
        }
        this.nameToFile.put(caArrayFile.getName(), file);
        return caArrayFile;
    }
    
    public CaArrayFile addChunk(File file, String fileName, Long fileSize, CaArrayFile caArrayFile) {
        return null; // unused.  Trying to get eventually rid of stub classes.
    }

    @Override
    public boolean remove(CaArrayFile caArrayFile) {
        if (BooleanUtils.isTrue(this.deletables.get(caArrayFile.getName()))) {
            this.removedFileCount++;
        }
        if (caArrayFile.getProject() != null) {
            caArrayFile.getProject().getFiles().remove(caArrayFile);
        }
        return false;
    }

    public void save(CaArrayFile caArrayFile) {
        this.savedFileCount++;
        if (caArrayFile.getId() == null) {
            caArrayFile.setId((long) this.savedFileCount);
        }
    }

    public void reset() {
        this.nameToFile.clear();
        this.deletables.clear();
        this.savedFileCount = 0;
        this.removedFileCount = 0;
    }

    public void setDeletableStatus(CaArrayFile file, boolean deletable) {
        this.deletables.put(file.getName(), deletable);
    }

    /**
     * @return the savedFileCount
     */
    public int getSavedFileCount() {
        return this.savedFileCount;
    }

    /**
     * @return the removedFileCount
     */
    public int getRemovedFileCount() {
        return this.removedFileCount;
    }

    /**
     * @return the nameToFile
     */
    public Map<String, File> getNameToFile() {
        return this.nameToFile;
    }

    /**
     * {@inheritDoc}
     */
    public void closeFile(CaArrayFile caarrayFile) {
        closeFile(caarrayFile, true);
    }

    public void closeFile(CaArrayFile caarrayFile, boolean uncompressed) {
        // nothing to do
    }

    /**
     * {@inheritDoc}
     */
    public void deleteFiles() {
        // nothing to do
    }

    /**
     * {@inheritDoc}
     */
    public void delete(File file) {
        this.removedFileCount++;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CaArrayFile add(InputStream stream, String filename) {
        return add(stream, filename, null);
    }

    public CaArrayFile add(InputStream stream, String filename,
            CaArrayFile parent) {
        final CaArrayFile caArrayFile = new CaArrayFile();
        caArrayFile.setName(filename);
        caArrayFile.setFileStatus(FileStatus.UPLOADED);
        this.nameToFile.put(caArrayFile.getName(), new File(filename));
        return caArrayFile;
    }

    public StorageMetadata addChunk(URI handle, InputStream stream) throws DataStoreException {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public StorageMetadata finalizeChunkedFile(URI handle) {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void synchronizeDataStorage() {
        // no-op
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void cleanupUnreferencedChildren() {
        // no-op
    }


    @Override
    public StorageMetadata add(InputStream stream, boolean compressed) throws DataStoreException {
        return null;
    }

    @Override
    public Iterable<StorageMetadata> list() {
        final List<StorageMetadata> metadatas = Lists.newArrayList();
        for (final Map.Entry<String, File> fileEntry : this.nameToFile.entrySet()) {
            final StorageMetadata metadata = new StorageMetadata();
            metadata.setHandle(CaArrayUtils.makeUriQuietly(SCHEME, fileEntry.getKey()));
            metadata.setCreationTimestamp(new Date(fileEntry.getValue().lastModified()));
            metadata.setCompressedSize(fileEntry.getValue().length());
            metadata.setUncompressedSize(fileEntry.getValue().length());
            metadatas.add(metadata);
        }
        return metadatas;
    }

    @Override
    public File openFile(URI handle, boolean compressed) throws DataStoreException {
        checkScheme(handle);
        return this.nameToFile.get(handle.getSchemeSpecificPart());
    }

    @Override
    public InputStream openInputStream(URI handle, boolean compressed) throws DataStoreException {
        checkScheme(handle);
        try {
            return new AutoCloseInputStream(FileUtils.openInputStream(openFile(handle, compressed)));
        } catch (final IOException e) {
            throw new DataStoreException("Could not open input stream for data " + handle + ":", e);
        }
    }

    @Override
    public void releaseFile(URI handle, boolean compressed) {
        // no-op
    }

    @Override
    public void remove(Collection<URI> handles) throws DataStoreException {
        // no-op
    }

    @Override
    public void remove(URI handle) throws DataStoreException {
        // no-op
    }

    private void checkScheme(URI handle) {
        if (!SCHEME.equals(handle.getScheme())) {
            throw new UnsupportedSchemeException("Unsupported scheme: " + handle.getScheme());
        }
    }

    /**
     * @param typeRegistry the typeRegistry to set
     */
    public void setTypeRegistry(FileTypeRegistry typeRegistry) {
        this.typeRegistry = typeRegistry;
    }

    /**
     * @return the typeRegistry
     */
    public FileTypeRegistry getTypeRegistry() {
        return this.typeRegistry;
    }

    /**
     * @return a DataStorageFacade that knows about a single storage engine - this stub class
     */
    public DataStorageFacade createStorageFacade() {
        final Map<String, DataStorage> engines = Maps.newHashMap();
        engines.put(FileAccessServiceStub.SCHEME, this);
        return new DataStorageFacade(engines, FileAccessServiceStub.SCHEME, FileAccessServiceStub.SCHEME);
    }
}
