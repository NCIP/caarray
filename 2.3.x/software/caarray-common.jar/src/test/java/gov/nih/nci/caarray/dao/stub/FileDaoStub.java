//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.dao.stub;

import gov.nih.nci.caarray.dao.FileDao;
import gov.nih.nci.caarray.domain.file.CaArrayFile;
import gov.nih.nci.caarray.domain.file.CaArrayFileSet;
import gov.nih.nci.caarray.domain.file.FileStatus;


/**
 *
 */
public class FileDaoStub extends AbstractDaoStub implements FileDao {

    /**
     * {@inheritDoc}
     */
    public void updateFileStatus(CaArrayFileSet fileSet, FileStatus status) {
        for (CaArrayFile file : fileSet.getFiles()) {
            file.setFileStatus(status);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void deleteSqlBlobsByProjectId(Long projectId) {
        //NOOP
    }

    public void deleteHqlBlobsByProjectId(Long projectId) {
        // NOOP

    }

}
