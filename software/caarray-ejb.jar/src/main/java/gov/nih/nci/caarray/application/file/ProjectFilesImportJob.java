//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package gov.nih.nci.caarray.application.file;

import gov.nih.nci.caarray.application.arraydata.DataImportOptions;
import gov.nih.nci.caarray.application.fileaccess.FileAccessService;
import gov.nih.nci.caarray.dao.ProjectDao;
import gov.nih.nci.caarray.dao.SearchDao;
import gov.nih.nci.caarray.domain.file.CaArrayFileSet;
import gov.nih.nci.caarray.domain.file.FileStatus;
import gov.nih.nci.caarray.domain.project.JobType;
import gov.nih.nci.caarray.domain.project.ParentJob;
import gov.nih.nci.caarray.domain.project.Project;
import gov.nih.nci.caarray.magetab.MageTabDocumentSet;
import gov.nih.nci.caarray.magetab.MageTabParsingException;

import org.apache.log4j.Logger;

import com.google.inject.Inject;

/**
 * Encapsulates the functionality necessary for importing a set of files into a project.
 */
class ProjectFilesImportJob extends AbstractProjectFilesJob {

    private static final long serialVersionUID = 1L;
    private static final Logger LOG = Logger.getLogger(ProjectFilesImportJob.class);

    private final DataImportOptions dataImportOptions;

    // CHECKSTYLE:OFF more than 7 parameters are okay for injected constructor
    @SuppressWarnings("PMD.ExcessiveParameterList")
    @Inject
    ProjectFilesImportJob(String username, Project targetProject,
            CaArrayFileSet fileSet, DataImportOptions dataImportOptions, ArrayDataImporter arrayDataImporter,
            MageTabImporter mageTabImporter, FileAccessService fileAccessService, ProjectDao projectDao,
            SearchDao searchDao) {
    // CHECKSTYLE:ON
        this(username, targetProject, fileSet, dataImportOptions, arrayDataImporter,
                mageTabImporter, fileAccessService, projectDao, searchDao, null);
    }

    // CHECKSTYLE:OFF more than 7 parameters are okay for injected constructor
    @SuppressWarnings("PMD.ExcessiveParameterList")
    @Inject
    ProjectFilesImportJob(String username, Project targetProject,
            CaArrayFileSet fileSet, DataImportOptions dataImportOptions, ArrayDataImporter arrayDataImporter,
            MageTabImporter mageTabImporter, FileAccessService fileAccessService, ProjectDao projectDao,
            SearchDao searchDao, ParentJob parent) {
    // CHECKSTYLE:ON
        super(username, targetProject, fileSet, arrayDataImporter,
                mageTabImporter, fileAccessService, projectDao, searchDao, parent);
        this.dataImportOptions = dataImportOptions;
    }

    /**
     * {@inheritDoc}
     */
    public JobType getJobType() {
        return JobType.DATA_FILE_IMPORT;
    }

    @Override
    protected void executeProjectFilesJob() {
        doValidate(getFileSet());
        if (getFileSet().isValidated()) {
            importAnnotationAndData(getFileSet());
        }
    }

    private void importAnnotationAndData(CaArrayFileSet fileSet) {
        MageTabDocumentSet mageTabDocSet = importAnnotation(fileSet);
        importArrayData(fileSet, mageTabDocSet);
        // ARRAY-2684: The data loaded from the import is referenced from mageTabDocSet,
        // but for some reason is not getting released for garbage collection. Clearing it
        // manually for now, until we figure out why it is not released automatically.
        mageTabDocSet.getNativeDataFiles().clear();
    }

    private MageTabDocumentSet importAnnotation(CaArrayFileSet fileSet) {
        MageTabDocumentSet mageTabDocSet = null;
        try {
            Project p = getProject();
            if (dataImportOptions != null) {
                p.setImportDescription(dataImportOptions.getImportDescription());
            }
            mageTabDocSet = getMageTabImporter().importFiles(p, fileSet);
        } catch (final MageTabParsingException e) {
            LOG.error(e.getMessage(), e);
        }
        getProjectDao().flushSession();
        getProjectDao().clearSession();
        return mageTabDocSet;
    }

    private void importArrayData(CaArrayFileSet fileSet, MageTabDocumentSet mTabSet) {
        final ArrayDataImporter arrayDataImporter = getArrayDataImporter();
        arrayDataImporter.importFiles(fileSet, this.dataImportOptions, mTabSet);
    }

    @Override
    protected FileStatus getInProgressStatus() {
        return FileStatus.IMPORTING;
    }
}
