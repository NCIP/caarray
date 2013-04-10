//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package gov.nih.nci.caarray.application.file;

import gov.nih.nci.caarray.application.arraydata.DataImportOptions;
import gov.nih.nci.caarray.application.fileaccess.FileAccessService;
import gov.nih.nci.caarray.application.util.CaArrayFileSetSplitter;
import gov.nih.nci.caarray.dao.ProjectDao;
import gov.nih.nci.caarray.dao.SearchDao;
import gov.nih.nci.caarray.domain.file.CaArrayFileSet;
import gov.nih.nci.caarray.domain.file.FileStatus;
import gov.nih.nci.caarray.domain.project.JobType;
import gov.nih.nci.caarray.domain.project.Project;

import java.io.IOException;
import java.util.Set;

import org.apache.log4j.Logger;

import com.google.common.collect.ImmutableSet;

/**
 * Splits large Mage-Tab File sets into smaller chunks.  Dispatches to ProjectFilesImportJob
 * after splitting.
 */
class ProjectFilesSplitJob extends AbstractProjectFilesJob {

    private static final long serialVersionUID = -6505339669676465113L;
    private static final Logger LOG = Logger.getLogger(ProjectFilesSplitJob.class);

    private final CaArrayFileSetSplitter splitter;
    private final DataImportOptions dataImportOptions;
    private final FileManagementJobSubmitter jobSubmitter;

    /**
     * Injected constructor.
     *
     * @param username user requesting job
     * @param targetProject project
     * @param fileSet set to split
     * @param arrayDataImporter not used
     * @param mageTabImporter not used
     * @param fileAccessService for creating new files
     * @param projectDao dao
     * @param searchDao dao
     * @param dataImportOptions import options for new sets
     * @param splitter file set splitter
     */
    // CHECKSTYLE:OFF more than 7 parameters are okay for injected constructor
    @SuppressWarnings("PMD.ExcessiveParameterList")
    ProjectFilesSplitJob(String username, Project targetProject,
            CaArrayFileSet fileSet, ArrayDataImporter arrayDataImporter,
            MageTabImporter mageTabImporter,
            FileAccessService fileAccessService, ProjectDao projectDao,
            SearchDao searchDao, DataImportOptions dataImportOptions, CaArrayFileSetSplitter splitter,
            FileManagementJobSubmitter jobSubmitter) {
        // CHECKSTYLE:ON
        super(username, targetProject, fileSet, arrayDataImporter, mageTabImporter,
                fileAccessService, projectDao, searchDao);
        this.dataImportOptions = dataImportOptions;
        this.splitter = splitter;
        this.jobSubmitter = jobSubmitter;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void executeProjectFilesJob() {
        CaArrayFileSet fileSet = getFileSet();
        doValidate(fileSet);

        if (fileSet.isValidated()) {
            importSplits(fileSet);
        }
    }

    private void importSplits(CaArrayFileSet origFileSet) {
        Set<CaArrayFileSet> splits = getSplitsToImport(origFileSet);
        handleSessionMess(); // new job needs the new split sdrf to have an id
        for (CaArrayFileSet curSplit : splits) {
            curSplit.updateStatus(FileStatus.VALIDATED);
            ProjectFilesImportJob job = new ProjectFilesImportJob(getOwnerName(), getProject(), curSplit,
                    dataImportOptions, getArrayDataImporter(), getMageTabImporter(), getFileAccessService(),
                    getProjectDao(), getSearchDao(), this);
            getChildren().add(job);
            jobSubmitter.submitJob(job);
        }
    }

    private Set<CaArrayFileSet> getSplitsToImport(CaArrayFileSet origFileSet) {
        try {
            return splitter.split(origFileSet);
        } catch (IOException e) {
            LOG.warn("Unable to split file set.  Falling back to non-split import.", e);
            return ImmutableSet.of(origFileSet);
        }
    }

    /**
     * This method is necessary because the Hibernate Session FlushMode is set to
     * FlushMode.COMMIT.  This means that queries will not flush prior to running.
     * Call this method only when a dependency needs database state to be correct.
     *
     * This is a compromise - it's misplaced responsibility to do session management
     * at this level.  We'd be better off not exposing CaArrayDao.flushSession(), but
     * since we have it and use it in many places, this is just one of many.
     */
    private void handleSessionMess() {
        getProjectDao().flushSession();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected FileStatus getInProgressStatus() {
        return FileStatus.IMPORTING;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JobType getJobType() {
        return JobType.DATA_FILE_SPLIT;
    }
}
