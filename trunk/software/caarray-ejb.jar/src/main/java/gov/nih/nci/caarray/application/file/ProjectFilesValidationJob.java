//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.application.file;

import gov.nih.nci.caarray.application.fileaccess.TemporaryFileCacheLocator;
import gov.nih.nci.caarray.domain.file.CaArrayFileSet;
import gov.nih.nci.caarray.domain.file.FileStatus;
import gov.nih.nci.caarray.domain.project.Project;

/**
 * Encapsulates the functionality necessary for validating a set of files in a project.
 */
final class ProjectFilesValidationJob extends AbstractProjectFilesJob {

    private static final long serialVersionUID = 1L;

    ProjectFilesValidationJob(String username, Project project, CaArrayFileSet fileSet) {
        super(username, project, fileSet);
    }

    @Override
    void execute() {
        Project project = getProject();
        try {            
            doValidate(getFileSet(project));
        } finally {
            TemporaryFileCacheLocator.getTemporaryFileCache().closeFiles();            
        }
    }


    @Override
    FileStatus getInProgressStatus() {
        return FileStatus.VALIDATING;
    }

}
