//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.application.fileaccess;

/**
 * Factory interface for creating a TemporaryFileCache implementation.
 * @author dkokotov
 */
public interface TemporaryFileCacheFactory {
    /**
     * @return a new TemporaryFileCache implementation.
     */
    TemporaryFileCache createTempFileCache();
}
