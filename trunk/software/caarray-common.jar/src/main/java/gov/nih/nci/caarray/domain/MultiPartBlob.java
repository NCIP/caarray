//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.domain;

import gov.nih.nci.caarray.util.HibernateUtil;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.SequenceInputStream;
import java.io.Serializable;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.ArrayUtils;
import org.hibernate.Hibernate;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.IndexColumn;

/**
 * This class is used to get around a mysql issue where the mysql server uses memory extremely
 * poorly on a connection where a blob is being used.  In order to get around this memory issue we
 * are breaking a single blob up in to many smaller blobs.
 * @author Scott Miller
 */
@Embeddable
public class MultiPartBlob implements Serializable {
    private static final long serialVersionUID = -2527332971292994350L;

    private static final int DEFAULT_BLOB_SIZE = 50 * 1024 * 1024;

    /**
     * Constant holding the default blob size.  By default it will be 50 MB.
     */
    private static int blobSize = DEFAULT_BLOB_SIZE;

    /**
     * Set the max size of a stored blob.
     * @param size the size in bytes
     */
    public static final void setBlobSize(int size) {
        blobSize = size;
    }
    /**
     * @return blob size
     */
    public static final int getBlobSize() {
        return blobSize;
    }

    private List<BlobHolder> blobParts = new ArrayList<BlobHolder>();

    /**
     * The blobParts as stored by hibernate.
     * @return the blobParts the blobParts.
     */
    @OneToMany(fetch = FetchType.LAZY)
    @IndexColumn(name = "contents_index")
    @Cascade(value = CascadeType.ALL)
    private List<BlobHolder> getBlobParts() {
        return this.blobParts;
    }

    /**
     * @param blobParts the blobParts to set
     */
    @SuppressWarnings({ "unused", "PMD.UnusedPrivateMethod" })
    private void setBlobParts(List<BlobHolder> contents) {
        this.blobParts = contents;
    }


    /**
     * Method that takes an input stream and breaks it up in to multiple blobs.
     * Note that this method loads each chunk in to a byte[], while this is not
     * ideal, this will be done by the mysql driver anyway, so we are not adding
     * a new inefficiency.  The data will be compressed.
     * @param data the input stream to store.
     * @return metadata about the written data (sizes, etc)
     * @throws IOException on error reading from the stream.
     */
    public MetaData writeDataCompressed(InputStream data) throws IOException {
        return writeData(data, true);
    }

    /**
     * Method that takes an input stream and breaks it up in to multiple blobs.
     * Note that this method loads each chunk in to a byte[], while this is not
     * ideal, this will be done by the mysql driver anyway, so we are not adding
     * a new inefficiency.  The data will <em>not</em> be compressed.
     * @param data the input stream to store.
     * @return metadata about the written data (sizes, etc)
     * @throws IOException on error reading from the stream.
     */
    public MetaData writeDataUncompressed(InputStream data) throws IOException {
        return writeData(data, false);
    }

    /**
     * Method that takes an input stream and breaks it up in to multiple blobs.
     * Note that this method loads each chunk in to a byte[], while this is not
     * ideal, this will be done by the mysql driver anyway, so we are not adding
     * a new inefficiency.
     * @param data the input stream to store.
     * @param compress true to compress the data, false to leave it uncompressed
     * @return metadata about the written data (sizes, etc)
     * @throws IOException on error reading from the stream.
     */
    private MetaData writeData(InputStream data, boolean compress) throws IOException {
        int uncompressedBytes = 0;
        int compressedBytes = 0;
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        OutputStream writeStream;
        if (compress) {
            writeStream = new GZIPOutputStream(byteStream);
        } else {
            writeStream = byteStream;
        }
        byte[] unwritten = new byte[0];
        byte[] uncompressed = new byte[MultiPartBlob.getBlobSize()];
        int len = 0;
        while ((len = data.read(uncompressed)) > 0) {
            uncompressedBytes += len;
            writeStream.write(uncompressed, 0, len);
            if (byteStream.size() + unwritten.length >= MultiPartBlob.getBlobSize()) {
                compressedBytes += byteStream.size();
                unwritten = writeData(ArrayUtils.addAll(unwritten, byteStream.toByteArray()), false);
                byteStream.reset();
            }
        }
        IOUtils.closeQuietly(writeStream);
        compressedBytes += byteStream.size();
        writeData(ArrayUtils.addAll(unwritten, byteStream.toByteArray()), true);
        return new MetaData(uncompressedBytes, compressedBytes);
    }

    /**
     * Writes data to the blob. If writeAll is false, this method only writes out data that fills the max blob size. Any
     * remaining unwritten data is returned.
     *
     * @param data the data to write the blob
     * @param writeAll whether to write out all the data
     * @return array of any unwritten data
     */
    private byte[] writeData(byte[] data, boolean writeAll) {
        if (data == null) {
            return new byte[0];
        }
        int index = 0;
        while (data.length - index >= blobSize) {
            addBlob(ArrayUtils.subarray(data, index, index + blobSize));
            index += blobSize;
        }
        byte[] unwritten = ArrayUtils.subarray(data, index, data.length);
        if (writeAll && !ArrayUtils.isEmpty(unwritten)) {
            addBlob(unwritten);
            return new byte[0];
        } else {
            return unwritten;
        }
    }

    /**
     * Add a blob part.
     * @param buffer blob data
     */
    private void addBlob(byte[] buffer) {
        BlobHolder bh = new BlobHolder();
        bh.setContents(Hibernate.createBlob(buffer));
        getBlobParts().add(bh);
    }

    /**
     * Add a blob part.
     * @param blob blob data
     */
    public void addBlob(Blob blob) {
        BlobHolder bh = new BlobHolder();
        bh.setContents(blob);
        getBlobParts().add(bh);
    }

    /**
     * Returns an input stream to access the contents of this MultiPartBlob.  The contents will be uncompressed when
     * read.
     *
     * @return the input stream to read.
     * @throws IOException if the contents couldn't be accessed.
     */
    public InputStream readCompressedContents() throws IOException {
        return readContents(true);
    }

    /**
     * Returns an input stream to access the contents of this MultiPartBlob.  The contents will <em>not</em> be
     * uncompressed when read.
     *
     * @return the input stream to read.
     * @throws IOException if the contents couldn't be accessed.
     */
    public InputStream readUncompressedContents() throws IOException {
        return readContents(false);
    }

    /**
     * Returns an input stream to access the contents of this MultiPartBlob.
     *
     * @param uncompress true if the data should be uncompressed when read, false otherwise
     * @return the input stream to read.
     * @throws IOException if the contents couldn't be accessed.
     */
    private InputStream readContents(boolean uncompress) throws IOException {
        try {
            Vector<InputStream> isVector = new Vector<InputStream>(); // NOPMD
            for (BlobHolder currentBlobHolder : getBlobParts()) {
                isVector.add(currentBlobHolder.getContents().getBinaryStream());
            }
            SequenceInputStream sequenceInputStream = new SequenceInputStream(isVector.elements());
            if (uncompress) {
                return new GZIPInputStream(sequenceInputStream);
            } else {
                return sequenceInputStream;
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Couldn't access file contents", e);
        }
    }

    /**
     * Clear the blobParts.
     */
    public void clearAndEvictData() {
        HibernateUtil.getCurrentSession().evict(this);
        for (BlobHolder bh : getBlobParts()) {
            bh.setContents(null);
        }
    }

    /**
     * This class stores metadata about a MultiPartBlob.
     * @author Steve Lustbader
     */
    public class MetaData {
        private int uncompressedBytes = 0;
        private int compressedBytes = 0;

        /**
         * @param uncompressedBytes uncompressed size of the MultiPartBlob, in bytes
         * @param compressedBytes compressed size of the MultiPartBlob, in bytes
         */
        public MetaData(int uncompressedBytes, int compressedBytes) {
            this.uncompressedBytes = uncompressedBytes;
            this.compressedBytes = compressedBytes;
        }

        /**
         * @return the uncompressedBytes
         */
        public int getUncompressedBytes() {
            return uncompressedBytes;
        }

        /**
         * @param uncompressedBytes the uncompressedBytes to set
         */
        public void setUncompressedBytes(int uncompressedBytes) {
            this.uncompressedBytes = uncompressedBytes;
        }

        /**
         * @return the compressedBytes
         */
        public int getCompressedBytes() {
            return compressedBytes;
        }

        /**
         * @param compressedBytes the compressedBytes to set
         */
        public void setCompressedBytes(int compressedBytes) {
            this.compressedBytes = compressedBytes;
        }
    }
}
