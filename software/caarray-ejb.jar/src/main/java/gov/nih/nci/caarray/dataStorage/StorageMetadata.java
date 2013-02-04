//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package gov.nih.nci.caarray.dataStorage;

import java.io.Serializable;
import java.net.URI;
import java.util.Date;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * StorageMetadata describes the metadata for a binary data block stored in the data storage subsystem.
 * 
 * @author dkokotov
 */
public class StorageMetadata implements Serializable {
    private static final long serialVersionUID = 1L;

    private long compressedSize;
    private long uncompressedSize;
    private long partialSize;
    private URI handle;
    private Date creationTimestamp;

    /**
     * @return the compressedSize
     */
    public long getCompressedSize() {
        return this.compressedSize;
    }

    /**
     * @param compressedSize the compressedSize to set
     */
    public void setCompressedSize(long compressedSize) {
        this.compressedSize = compressedSize;
    }

    /**
     * @return the uncompressedSize
     */
    public long getUncompressedSize() {
        return this.uncompressedSize;
    }

    /**
     * @param uncompressedSize the uncompressedSize to set
     */
    public void setUncompressedSize(long uncompressedSize) {
        this.uncompressedSize = uncompressedSize;
    }

    /**
     * @return the partialSize
     */
    public long getPartialSize() {
        return partialSize;
    }

    /**
     * @param partialSize the partialSize to set
     */
    public void setPartialSize(long partialSize) {
        this.partialSize = partialSize;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    /**
     * @return the handle
     */
    public URI getHandle() {
        return this.handle;
    }

    /**
     * @param handle the handle to set
     */
    public void setHandle(URI handle) {
        this.handle = handle;
    }

    /**
     * @return the creationTimestamp
     */
    public Date getCreationTimestamp() {
        return this.creationTimestamp;
    }

    /**
     * @param creationTimestamp the creationTimestamp to set
     */
    public void setCreationTimestamp(Date creationTimestamp) {
        this.creationTimestamp = creationTimestamp;
    }
}
