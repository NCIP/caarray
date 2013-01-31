//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.external.v1_0.data;

import java.io.Serializable;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * FileMetadata describes the metadata for a file in caarray. 
 * 
 * @author dkokotov
 */
public class FileMetadata implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private long compressedSize;
    private long uncompressedSize;
    private String name;
    private FileType fileType;

    /**
     * @return the compressedSize
     */
    public long getCompressedSize() {
        return compressedSize;
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
        return uncompressedSize;
    }

    /**
     * @param uncompressedSize the uncompressedSize to set
     */
    public void setUncompressedSize(long uncompressedSize) {
        this.uncompressedSize = uncompressedSize;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the fileType
     */
    public FileType getFileType() {
        return fileType;
    }

    /**
     * @param fileType the fileType to set
     */
    public void setFileType(FileType fileType) {
        this.fileType = fileType;
    }
    
    /**
     * {@inheritDoc}
     */
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
}
