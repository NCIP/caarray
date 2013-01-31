//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.application.fileaccess;

/**
 * @author dkokotov
 *
 */
public class TemporaryFileCacheStubFactory implements TemporaryFileCacheFactory {
    private final FileAccessServiceStub fileAccessServiceStub;


    /**
     * @param fileAccessServiceStub
     */
    public TemporaryFileCacheStubFactory(FileAccessServiceStub fileAccessServiceStub) {
        this.fileAccessServiceStub = fileAccessServiceStub;
    }

    /**
     * {@inheritDoc}
     */
    public TemporaryFileCache createTempFileCache() {
        return this.fileAccessServiceStub;
    }
}
