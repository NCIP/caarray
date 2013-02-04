//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package gov.nih.nci.caarray.domain;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.SequenceInputStream;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Vector;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.ArrayUtils;
import org.hibernate.Hibernate;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.IndexColumn;
import org.hibernate.validator.NotNull;

import com.fiveamsolutions.nci.commons.data.persistent.PersistentObject;

/**
 * This class is used to get around a mysql issue where the mysql server uses memory extremely poorly on a connection
 * where a blob is being used. In order to get around this memory issue we are breaking a single blob up in to many
 * smaller blobs.
 * 
 * @author Scott Miller
 */
@Entity
@Table(name = "multipart_blob")
public class MultiPartBlob implements PersistentObject {
    private static final long serialVersionUID = -2527332971292994350L;

    private Long id;
    private Date creationTimestamp;
    private long uncompressedSize;
    private long compressedSize;

    /**
     * Returns the id.
     * 
     * @return the id
     */
    @Override
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Long getId() {
        return this.id;
    }

    /**
     * Sets the id.
     * 
     * @param id the id to set
     * @deprecated should only be used by castor and hibernate
     */
    @Deprecated
    public void setId(Long id) {
        this.id = id;
    }

    private List<BlobHolder> blobParts = new ArrayList<BlobHolder>();

    /**
     * The blobParts as stored by hibernate.
     * 
     * @return the blobParts the blobParts.
     */
    @OneToMany(fetch = FetchType.LAZY)
    @IndexColumn(name = "contents_index")
    @Cascade(value = CascadeType.ALL)
    public List<BlobHolder> getBlobParts() {
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
     * Method that takes an input stream and breaks it up in to multiple blobs. Note that this method loads each chunk
     * in to a byte[], while this is not ideal, this will be done by the mysql driver anyway, so we are not adding a new
     * inefficiency.
     * 
     * @param data the input stream to store.
     * @param compress true to compress the data, false to leave it uncompressed
     * @param blobPartSize the maximum size of a single blob
     * @throws IOException on error reading from the stream.
     */
    public void writeData(InputStream data, boolean compress, int blobPartSize) throws IOException {
        final ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        OutputStream writeStream;
        if (compress) {
            writeStream = new GZIPOutputStream(byteStream);
        } else {
            writeStream = byteStream;
        }
        byte[] unwritten = new byte[0];
        final byte[] uncompressed = new byte[blobPartSize];
        int len = 0;
        while ((len = data.read(uncompressed)) > 0) {
            uncompressedSize += len;
            writeStream.write(uncompressed, 0, len);
            if (byteStream.size() + unwritten.length >= blobPartSize) {
                compressedSize += byteStream.size();
                unwritten = writeData(ArrayUtils.addAll(unwritten, byteStream.toByteArray()), blobPartSize, false);
                byteStream.reset();
            }
        }
        IOUtils.closeQuietly(writeStream);
        compressedSize += byteStream.size();
        writeData(ArrayUtils.addAll(unwritten, byteStream.toByteArray()), blobPartSize, true);
    }

    /**
     * Writes data to the blob. If writeAll is false, this method only writes out data that fills the max blob size. Any
     * remaining unwritten data is returned.
     * 
     * @param data the data to write the blob
     * @param writeAll whether to write out all the data
     * @param blobPartSize the maximum size of a single blob
     * @return array of any unwritten data
     */
    private byte[] writeData(byte[] data, int blobPartSize, boolean writeAll) {
        if (data == null) {
            return new byte[0];
        }
        int index = 0;
        while (data.length - index >= blobPartSize) {
            addBlob(ArrayUtils.subarray(data, index, index + blobPartSize));
            index += blobPartSize;
        }
        final byte[] unwritten = ArrayUtils.subarray(data, index, data.length);
        if (writeAll && !ArrayUtils.isEmpty(unwritten)) {
            addBlob(unwritten);
            return new byte[0];
        } else {
            return unwritten;
        }
    }

    /**
     * Add a blob part.
     * 
     * @param buffer blob data
     */
    private void addBlob(byte[] buffer) {
        final BlobHolder bh = new BlobHolder();
        bh.setContents(Hibernate.createBlob(buffer));
        getBlobParts().add(bh);
    }

    /**
     * Add a blob part.
     * 
     * @param blob blob data
     */
    public void addBlob(Blob blob) {
        final BlobHolder bh = new BlobHolder();
        bh.setContents(blob);
        getBlobParts().add(bh);
    }

    /**
     * Returns an input stream to access the contents of this MultiPartBlob. The contents will <em>not</em> be
     * uncompressed when read.
     * 
     * @return the raw (gzip) input stream to read.
     * @throws IOException if the contents couldn't be accessed.
     */
    public InputStream readCompressedContents() throws IOException {
        return readContents(false);
    }

    /**
     * Returns an input stream to access the contents of this MultiPartBlob. The contents will be uncompressed when
     * read.
     * 
     * @return the inflated input stream to read.
     * @throws IOException if the contents couldn't be accessed.
     */
    public InputStream readUncompressedContents() throws IOException {
        return readContents(true);
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
            final Vector<InputStream> isVector = new Vector<InputStream>(); // NOPMD
            for (final BlobHolder currentBlobHolder : getBlobParts()) {
                isVector.add(currentBlobHolder.getContents().getBinaryStream());
            }
            final SequenceInputStream sequenceInputStream = new SequenceInputStream(isVector.elements());
            if (uncompress) {
                return new GZIPInputStream(sequenceInputStream);
            } else {
                return sequenceInputStream;
            }
        } catch (final SQLException e) {
            throw new IllegalStateException("Couldn't access file contents", e);
        }
    }

    /**
     * @return the timestamp
     */
    @Temporal(TemporalType.TIMESTAMP)
    @NotNull
    public Date getCreationTimestamp() {
        return this.creationTimestamp;
    }

    /**
     * @param timestamp the timestamp to set
     */
    public void setCreationTimestamp(Date timestamp) {
        this.creationTimestamp = timestamp;
    }

    /**
     * @return the uncompressed size, in bytes
     */
    public long getUncompressedSize() {
        return this.uncompressedSize;
    }

    /**
     * This method should generally not be called directly, as file size is calculated when data is written to the file.
     * It is left public to support use in query by example and tooling relying on JavaBean property conventions
     * 
     * @param uncompressedSize the uncompressed size of the file, in bytes
     */
    public void setUncompressedSize(long uncompressedSize) {
        this.uncompressedSize = uncompressedSize;
    }

    /**
     * @return the compressed size, in bytes
     */
    public long getCompressedSize() {
        return this.compressedSize;
    }

    /**
     * This method should generally not be called directly, as file size is calculated when data is written to the file.
     * It is left public to support use in query by example and tooling relying on JavaBean property conventions
     * 
     * @param compressedSize the compressed size of the file, in bytes
     */
    public void setCompressedSize(long compressedSize) {
        this.compressedSize = compressedSize;
    }
}
