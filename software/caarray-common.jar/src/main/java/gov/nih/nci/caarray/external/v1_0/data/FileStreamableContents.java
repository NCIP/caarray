//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.external.v1_0.data;

import java.io.Serializable;

import com.healthmarketscience.rmiio.RemoteInputStream;

/**
 * FileStreamableContents combines a file's metadata with a RemoteInputStream for reading its contents.
 * 
 * @author dkokotov
 */
public class FileStreamableContents implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private FileMetadata metadata;
    private RemoteInputStream contentStream;
    private boolean compressed;

    /**
     * @return whether the contents that the contentStream will expose have been compressed using the GZIP algorithm.
     */
    public boolean isCompressed() {
        return compressed;
    }

    /**
     * @param compressed whether the contents that the contentCtream will expose have been compressed using the GZIP
     *            algorithm.
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
     * @return the RemoteInputStream via which the contents for this file can be read. The stream will contain either
     *         the raw contents, or the contents compressed using GZIP, depending on the compressed property.
     */
    public RemoteInputStream getContentStream() {
        return contentStream;
    }

    /**
     * @param contentStream the RemoteInputStream via which the contents for this file can be read.
     */
    public void setContentStream(RemoteInputStream contentStream) {
        this.contentStream = contentStream;
    }
}
