//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.external.v1_0.data;

import gov.nih.nci.caarray.external.v1_0.AbstractCaArrayEntity;

/**
 * File represents a file that has been uploaded to caarray. 
 * 
 * @author dkokotov
 */
public class File extends AbstractCaArrayEntity {
    private static final long serialVersionUID = 1L;

    private FileMetadata metadata;

    /**
     * @return the metadata describing this file
     */
    public FileMetadata getMetadata() {
        return metadata;
    }

    /**
     * @param metadata the metadata describing this file
     */
    public void setMetadata(FileMetadata metadata) {
        this.metadata = metadata;
    }
}
