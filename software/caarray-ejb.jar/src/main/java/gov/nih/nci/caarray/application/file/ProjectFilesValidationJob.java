//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.application.file;

import gov.nih.nci.caarray.application.fileaccess.FileAccessService;
import gov.nih.nci.caarray.dao.ProjectDao;
import gov.nih.nci.caarray.dao.SearchDao;
import gov.nih.nci.caarray.domain.file.CaArrayFileSet;
import gov.nih.nci.caarray.domain.file.FileStatus;
import gov.nih.nci.caarray.domain.project.JobType;
import gov.nih.nci.caarray.domain.project.Project;

import com.google.inject.Inject;

/**
 * Encapsulates the functionality necessary for validating a set of files in a project.
 */
final class ProjectFilesValidationJob extends AbstractProjectFilesJob {

    private static final long serialVersionUID = 1L;

    @SuppressWarnings("PMD.ExcessiveParameterList")
    @Inject
    // CHECKSTYLE:OFF more than 7 parameters are okay for injected constructor
    ProjectFilesValidationJob(String username, Project project,
            CaArrayFileSet fileSet, ArrayDataImporter arrayDataImporter, MageTabImporter mageTabImporter,
            FileAccessService fileAccessService, ProjectDao projectDao, SearchDao searchDao) {
    // CHECKSTYLE:ON
        super(username, project, fileSet, arrayDataImporter, mageTabImporter,
                fileAccessService, projectDao, searchDao);
    }

    /**
     * {@inheritDoc}
     */
    public JobType getJobType() {
        return JobType.DATA_FILE_VALIDATION;
    }

    @Override
    protected void executeProjectFilesJob() {
        doValidate(getFileSet());
    }

    @Override
    protected FileStatus getInProgressStatus() {
        return FileStatus.VALIDATING;
    }

}
