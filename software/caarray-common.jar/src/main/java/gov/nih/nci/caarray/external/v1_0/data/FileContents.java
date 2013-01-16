//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.external.v1_0.data;

import java.io.Serializable;

/**
 * FileContents combines a file's metadata with its contents.
 * 
 * @author dkokotov
 */
public class FileContents implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private FileMetadata metadata;
    private byte[] contents;
    private boolean compressed;

    /**
     * @return whether the contents have been compressed using the GZIP algorithm.
     */
    public boolean isCompressed() {
        return compressed;
    }

    /**
     * @param compressed whether the contents have been compressed using the GZIP algorithm.
     */
    public void setCompressed(boolean compressed) {
        this.compressed = compressed;
    }

    /**
     * @return the metadata for this file
     */
    public FileMetadata getMetadata() {
        return metadata;
    }

    /**
     * @param metadata the metadata for this file
     */
    public void setMetadata(FileMetadata metadata) {
        this.metadata = metadata;
    }

    /**
     * @return the contents. These will be either the raw byte contents, or the byte contents compressed using GZIP,
     *         depending on the compressed property.
     */
    public byte[] getContents() {
        return contents; // NOPMD
    }

    /**
     * @param contents the byte content of this file. This should be either the raw byte contents, or the byte contents
     *            compressed using GZIP, depending on the compressed property.
     */
    public void setContents(byte[] contents) { // NOPMD
        this.contents = contents;
    }
}
