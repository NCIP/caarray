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
import gov.nih.nci.caarray.magetab.MageTabDocumentSet;
import gov.nih.nci.caarray.magetab.MageTabFileSet;
import gov.nih.nci.caarray.magetab.validator.ValidatorSet;

import org.apache.log4j.Logger;

import com.google.inject.Inject;

/**
 * Encapsulates the functionality necessary for re-parsing a set of files that
 * were previously imported-not-parsed but now have an available parser.
 * 
 * This is very similar to ProjectFilesImportJob except the annotation validation
 * and importing component is skipped.
 * @author dkokotov 
 */
final class ProjectFilesReparseJob extends AbstractProjectFilesJob {
    private static final long serialVersionUID = 1L;
    private static final Logger LOG = Logger.getLogger(ProjectFilesReparseJob.class);

    @SuppressWarnings("PMD.ExcessiveParameterList")
    @Inject
    // CHECKSTYLE:OFF more than 7 parameters are okay for injected constructor
    ProjectFilesReparseJob(String username, Project targetProject,
            CaArrayFileSet fileSet, ArrayDataImporter arrayDataImporter, MageTabImporter mageTabImporter,
            FileAccessService fileAccessService, ProjectDao projectDao, SearchDao searchDao) {
    // CHECKSTYLE:ON
        super(username, targetProject, fileSet,
                arrayDataImporter, mageTabImporter, fileAccessService, projectDao, searchDao);
    }

    /**
     * {@inheritDoc}
     */
    public JobType getJobType() {
        return JobType.DATA_FILE_REPARSE;
    }

    @Override
    protected void executeProjectFilesJob() {
        CaArrayFileSet fileSet = getFileSet();
        getArrayDataImporter().validateFiles(fileSet,
                new MageTabDocumentSet(new MageTabFileSet(), new ValidatorSet()), true);
        final FileStatus status = getFileSet().getStatus();
        if (status.equals(FileStatus.VALIDATED) || status.equals(FileStatus.VALIDATED_NOT_PARSED)) {
            getProjectDao().flushSession();
            getProjectDao().clearSession();
            importArrayData(fileSet);
        }
    }

    private void importArrayData(CaArrayFileSet fileSet) {
        final ArrayDataImporter arrayDataImporter = getArrayDataImporter();
        final MageTabDocumentSet mTabSet = null; 
            //TODO isn't parsed MageTabDocumentSet required for certain platforms? 
            //e.g. AgilentRawTextDataHandler needs SDRF that specifies data file to array design mapping,  
            //in case there are more than one array design for a given experiment. 
            // Andrew Sy 2011-08-24
        
        arrayDataImporter.importFiles(fileSet, null, mTabSet);  
            // don't need to specify import options since ArrayData exists.
    }

    @Override
    protected FileStatus getInProgressStatus() {
        return FileStatus.IMPORTING;
    }
}
