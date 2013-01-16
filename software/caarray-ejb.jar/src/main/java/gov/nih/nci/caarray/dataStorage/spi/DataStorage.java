//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.dataStorage.spi;

import gov.nih.nci.caarray.dataStorage.DataStoreException;
import gov.nih.nci.caarray.dataStorage.StorageMetadata;

import java.io.File;
import java.io.InputStream;
import java.net.URI;
import java.util.Collection;

/**
 * Primary interface for storage engine modules that provide binary data storage and retrieval services to caArray.
 * <p>
 * A Data Storage engine is able to store and load opaque blocks of binary data. Data Storages support handling the data
 * either as-is or compressed using GZip, and can accept or produce either representation (it is up to the storage
 * engine to decide whether to compress/decompress on the fly or store both representations).
 * <p>
 * A block of data is identified by a URI, initially assigned by the storage engine that first stored it. The URI
 * consists of a scheme and a scheme-specific part. The scheme is used to identify the storage engine which is capable
 * of handling it. A storage engine may be capable of handling multiple schemes, but a given scheme should only be
 * handled by a single storage engine.
 */
public interface DataStorage {
    /**
     * Add a new block of data to the storage system.
     * 
     * @param stream a stream from which data will be read
     * @param compressed whether the data in the stream is compressed or not. If the data is compressed, GZip
     *            compression should be used.
     * @return a StorageMetadata object describing the added block of data, including a handle for later access to the
     *         data
     * @throws DataStoreException if there is an error storing the data
     */
    StorageMetadata add(InputStream stream, boolean compressed) throws DataStoreException;

    /**
     * Appends file data to an existing block in the storage system. If no handle is given, a new block is created.
     * 
     * @param handle the handle referencing the data to be appended to.
     * @param stream a stream from which data will be read.
     * @return a StorageMetadata object describing the added block of data, must include a handle for later access to
     *         the data and the size of the partially uploaded file.
     * @throws DataStoreException if there is an error storing the data
     */
    StorageMetadata addChunk(URI handle, InputStream stream) throws DataStoreException;

    /**
     * When a file is added in chunks, this method must be called after the final chunk is added.  This will perform
     * any necessary processing after all the chunks have been put together.
     * 
     * @param handle the handle referencing the data.
     * @return a StorageMetadata object describing the block of data, must include a handle for later access to the
     *         data and the compressed/uncompressed sizes of the file.
     */
    StorageMetadata finalizeChunkedFile(URI handle);

    /**
     * Get a listing of all data blocks this storage engine is currently managing.
     * 
     * @return an Iterable of StorageMetadata, where each item describes one block of data managed by this storage
     *         engine
     */
    Iterable<StorageMetadata> list();

    /**
     * Remove a block of data from storage.
     * 
     * @param handle the handle referencing the data to be removed.
     * @throws DataStoreException if there is an error removing the data
     */
    void remove(URI handle) throws DataStoreException;

    /**
     * Remove multiple data blocks from storage. Some storage engines may be more efficient at removing multiple data
     * blocks at a time, and thus may have an optimized implementation of this method.
     * 
     * @param handles the handles referencing the datas to be removed.
     * @throws DataStoreException if there is an error removing the data
     */
    void remove(Collection<URI> handles) throws DataStoreException;

    /**
     * Return a <code>java.io.File</code> which will hold the data identified by the given handle. The client should
     * eventually call releaseFile() for this <code>handle</code>.
     * 
     * @param handle the handle referencing the data to be obtained.
     * @param compressed whether the file should hold the compressed (if true) or uncompressed (if false) view of the
     *            data. Compressed data uses GZip compression.
     * @return the <code>java.io.File</code> with the referenced data, possibly compressed.
     * @throws DataStoreException if there is an error retrieving the data
     */
    File openFile(URI handle, boolean compressed) throws DataStoreException;

    /**
     * Return a <code>java.io.InputStream</code> which can be used to read the data identified by the given handle. The
     * client should make sure to close the stream once it is done with it. Implementors may use a temporary file to
     * hold the data, but may also provide a stream that accesses the data from its original location.
     * 
     * @param handle the handle referencing the data to be obtained.
     * @param compressed whether the stream should hold the compressed (if true) or uncompressed (if false) view of the
     *            data. Compressed data uses GZip compression.
     * @return the <code>java.io.InputStream</code> with the referenced data, possibly compressed.
     * @throws DataStoreException if there is an error retrieving the data
     */
    InputStream openInputStream(URI handle, boolean compressed) throws DataStoreException;

    /**
     * Releases the file that was previously obtained from this storage engine for the data block with given handle.
     * 
     * @param handle the handle referencing the data block that was obtained as a file via a call to openFile.
     * @param compressed whether the openFile call was to get compressed or uncompressed data.
     */
    void releaseFile(URI handle, boolean compressed);
}
