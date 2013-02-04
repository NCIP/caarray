//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package gov.nih.nci.caarray.dataStorage;

import gov.nih.nci.caarray.dataStorage.spi.DataStorage;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.util.Date;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;
import com.google.inject.Inject;
import com.google.inject.name.Named;

/**
 * Facade class to the data storage subsystem. Application code should inject and use this class to interact with the
 * data storage subsystem. In turn, this will delegate to appropriate storage engines as necessary.
 * 
 * @author dkokotov
 */
public class DataStorageFacade {
    private static final Logger LOG = Logger.getLogger(DataStorageFacade.class);

    private final Map<String, DataStorage> dataStorageEngines;
    private final String parsedDataStorage;
    private final String fileDataStorage;

    /**
     * Constructor.
     * 
     * @param dataStorageEngines a map of storage scheme name to the DataStorage implementations which can handle
     *            storing data using that scheme. The storage scheme name is matched against the scheme part of the data
     *            handle URI to locate the storage engine that can retrieve the data for that handle.
     * @param fileDataStorage the name of the storage scheme to use for storing raw file data. This is the scheme used
     *            when new file data needs to be stored; existing file data can use other schemes, as long as they have
     *            corresponding storage engine implementations. Must be present as a key in <i>dataStorageEngines</i>
     * @param parsedDataStorage the name of the storage scheme to use for storing parsed array data. This is the scheme
     *            used when new parsed data needs to be stored; existing parsed data can use other schemes, as long as
     *            they have corresponding storage engine implementations. Must be present as a key in
     *            <i>dataStorageEngines</i>
     */
    @Inject
    public DataStorageFacade(Map<String, DataStorage> dataStorageEngines,
            @Named(DataStorageModule.FILE_DATA_ENGINE) String fileDataStorage,
            @Named(DataStorageModule.PARSED_DATA_ENGINE) String parsedDataStorage) {
        this.dataStorageEngines = dataStorageEngines;
        this.fileDataStorage = fileDataStorage;
        this.parsedDataStorage = parsedDataStorage;
        Preconditions.checkArgument(dataStorageEngines.containsKey(fileDataStorage), "No data storage engine named "
                + fileDataStorage + " defined");
        Preconditions.checkArgument(dataStorageEngines.containsKey(parsedDataStorage), "No data storage engine named "
                + parsedDataStorage + " defined");
    }

    private DataStorage fileDataStorageEngine() {
        return this.dataStorageEngines.get(this.fileDataStorage);
    }

    private DataStorage parsedDataStorageEngine() {
        return this.dataStorageEngines.get(this.parsedDataStorage);
    }

    private DataStorage dataStorageEngine(URI handle) {
        return this.dataStorageEngines.get(handle.getScheme());
    }

    /**
     * Synchronize state of storate engines with state of caArray by removing from storage engines any data blocks that
     * are no longer referenced from any caArray entities. Only data blocks of a minimum threshold age are removed; this
     * is to handle data blocks which are referenced from entities created in an ongoing transaction that has not yet
     * been committed.
     * 
     * @param references the complete set of data handles that are currently held by entities in caArray. Any data
     *            blocks whose handles are not in this set and that are older than <i>minAge</i> will be removed.
     * @param minAge the minimum age for a data block to be eligible for removal, if it is unreferenced, in
     *            milliseconds. This should be at least as long as the maximum transaction time.
     */
    public void removeUnreferencedData(Set<URI> references, long minAge) {
        final Date creationThreshold = new Date(System.currentTimeMillis() - minAge);
        for (final Map.Entry<String, DataStorage> dsEntry : this.dataStorageEngines.entrySet()) {
            final Set<URI> dsReferences = Sets.newHashSet(Iterables.filter(references, new Predicate<URI>() {
                @Override
                public boolean apply(URI uri) {
                    return dsEntry.getKey().equals(uri.getScheme());
                }
            }));
            removeUnreferencedData(dsReferences, creationThreshold, dsEntry.getValue());
        }
    }

    private void removeUnreferencedData(Set<URI> references, Date creationThreshold, DataStorage ds) {
        final Set<URI> urisToRemove = Sets.newHashSet();
        final Iterable<StorageMetadata> existingMetadatas = ds.list();
        for (final StorageMetadata metadata : existingMetadatas) {
            if (metadata.getCreationTimestamp().before(creationThreshold) 
                    && !references.contains(metadata.getHandle())) {
                urisToRemove.add(metadata.getHandle());
            }
        }
        ds.remove(urisToRemove);
    }

    /**
     * Return a <code>java.io.File</code> which will hold the data identified by the given handle. The client should
     * eventually call releaseFile() for this <code>handle</code>.
     * 
     * @param handle the handle referencing the data to be obtained.
     * @param compressed whether the file should hold the compressed (if true) or uncompressed (if false) view of the
     *            data. Compressed data uses GZip compression.
     * @return the <code>java.io.File</code> with the referenced data, possibly compressed.
     */
    public File openFile(URI handle, boolean compressed) {
        final DataStorage ds = dataStorageEngine(handle);
        if (ds == null) {
            throw new DataStoreException("No storage engine found for handle: " + handle);
        }

        return ds.openFile(handle, compressed);
    }

    /**
     * Releases the file that was previously obtained from the data storage subsystem for the data block with given
     * handle.
     * 
     * @param handle the handle referencing the data block that was obtained as a file via a call to openFile.
     * @param compressed whether the openFile call was to get compressed or uncompressed data.
     */
    public void releaseFile(URI handle, boolean compressed) {
        final DataStorage ds = dataStorageEngine(handle);
        if (ds == null) {
            throw new DataStoreException("No storage engine found for handle: " + handle);
        }
        ds.releaseFile(handle, compressed);
    }

    /**
     * Return a <code>java.io.InputStream</code> which can be used to read the data identified by the given handle. The
     * client should make sure to close the stream once it is done with it. Implementors may use a temporary file to
     * hold the data, but may also provide a stream that accesses the data from its original location.
     * 
     * @param handle the handle referencing the data to be obtained.
     * @param compressed whether the stream should hold the compressed (if true) or uncompressed (if false) view of the
     *            data. Compressed data uses GZip compression.
     * @return the <code>java.io.InputStream</code> with the referenced data, possibly compressed.
     */
    public InputStream openInputStream(URI handle, boolean compressed) {
        final DataStorage ds = dataStorageEngine(handle);
        if (ds == null) {
            throw new DataStoreException("No storage engine found for handle: " + handle);
        }

        return ds.openInputStream(handle, compressed);
    }

    /**
     * Write the uncompressed data identified by the given handle to the given OutputStream. The OutputStream is not
     * closed at the end.
     * 
     * @param handle the handle referencing the data to be obtained.
     * @param os the OutputStream to write the data to.
     * 
     * @throws IOException if there is an error writing to the stream or reading the data.
     */
    public void copyDataToStream(URI handle, OutputStream os) throws IOException {
        final InputStream in = openInputStream(handle, false);
        try {
            IOUtils.copy(in, os);
        } finally {
            IOUtils.closeQuietly(in);
        }
    }

    /**
     * Add a new block of raw file data to the storage system. This should correspond to the contents of a CaArrayFile.
     * 
     * @param in a stream from which data will be read
     * @param compressed whether the data in the stream is compressed or not. If the data is compressed, GZip
     *            compression should be used.
     * @return a StorageMetadata object describing the added block of data, including a handle for later access to the
     *         data
     */
    public StorageMetadata addFile(InputStream in, boolean compressed) {
        return fileDataStorageEngine().add(in, compressed);
    }

    /**
     * Add a new block of parsed data to the storage system. This should correspond to the contents of a
     * AbstractDataColumn.
     * 
     * @param in a stream from which data will be read
     * @param compressed whether the data in the stream is compressed or not. If the data is compressed, GZip
     *            compression should be used.
     * @return a StorageMetadata object describing the added block of data, including a handle for later access to the
     *         data
     */
    public StorageMetadata addParsed(InputStream in, boolean compressed) {
        return parsedDataStorageEngine().add(in, compressed);
    }
    
    /**
     * Appends file data to an existing block in the storage system. If no handle is given, a new block is created.
     * The client must call completeFile() after the last chunk has been added.
     * 
     * @param handle the handle referencing the data to be appended to.
     * @param in a stream from which data will be read.
     * @return a StorageMetadata object describing the added block of data, must include a handle for later access to
     *         the data and the size of the partially uploaded file.
     */
    public StorageMetadata addFileChunk(URI handle, InputStream in) {
        return fileDataStorageEngine().addChunk(handle, in);
    }
    
    /**
     * When a file is added in chunks, this method must be called after the final chunk is added.  This will perform
     * any necessary processing after all the chunks have been put together.
     * 
     * @param handle the handle referencing the data.
     * @return a StorageMetadata object describing the block of data, must include a handle for later access to the
     *         data and the compressed/uncompressed sizes of the file.
     */
    public StorageMetadata finalizeChunkedFile(URI handle) {
        return fileDataStorageEngine().finalizeChunkedFile(handle);
    }
}
