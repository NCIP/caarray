//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.dao.stub;

import gov.nih.nci.caarray.dao.FileDao;
import gov.nih.nci.caarray.domain.file.CaArrayFile;
import gov.nih.nci.caarray.domain.search.FileSearchCriteria;

import java.net.URI;
import java.util.Collections;
import java.util.List;

import com.fiveamsolutions.nci.commons.data.search.PageSortParams;

/**
 *
 */
public class FileDaoStub extends AbstractDaoStub implements FileDao {
    @Override
    public List<CaArrayFile> searchFiles(PageSortParams<CaArrayFile> params, FileSearchCriteria criteria) {
        return Collections.emptyList();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<CaArrayFile> getDeletableFiles(Long projectId) {
        return Collections.emptyList();
    }

    @Override
    public List<URI> getAllFileHandles() {
        return Collections.emptyList();
    }

    @Override
    public void cleanupUnreferencedChildren() {
        // no op
    }

    @Override
    public CaArrayFile getPartialFile(Long projectId, String fileName, long fileSize) {
        return null; // unused. Trying to get rid of stub classes.
    }
}
