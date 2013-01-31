//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.application.file;

import gov.nih.nci.caarray.application.arraydata.DataImportOptions;
import gov.nih.nci.caarray.application.fileaccess.TemporaryFileCacheLocator;
import gov.nih.nci.caarray.domain.file.CaArrayFileSet;
import gov.nih.nci.caarray.domain.file.FileStatus;
import gov.nih.nci.caarray.domain.project.Project;
import gov.nih.nci.caarray.magetab.MageTabParsingException;

import org.apache.log4j.Logger;

/**
 * Encapsulates the functionality necessary for importing a set of files into a project.
 */
final class ProjectFilesImportJob extends AbstractProjectFilesJob {

    private static final long serialVersionUID = 1L;
    private static final Logger LOG = Logger.getLogger(ProjectFilesImportJob.class);

    private final DataImportOptions dataImportOptions;

    ProjectFilesImportJob(String username, Project targetProject, CaArrayFileSet fileSet,
            DataImportOptions dataImportOptions) {
        super(username, targetProject, fileSet);
        this.dataImportOptions = dataImportOptions;
    }

    @Override
    void execute() {
        CaArrayFileSet fileSet = getFileSet(getProject());
        try {
            doValidate(fileSet);
            if (fileSet.getStatus().equals(FileStatus.VALIDATED)
                    || fileSet.getStatus().equals(FileStatus.VALIDATED_NOT_PARSED)) {
                importAnnotation(fileSet);
                importArrayData(fileSet);
            }
        } finally {
            TemporaryFileCacheLocator.getTemporaryFileCache().closeFiles();
        }
    }

    private void importAnnotation(CaArrayFileSet fileSet) {
        try {
            getMageTabImporter().importFiles(getProject(), fileSet);
        } catch (MageTabParsingException e) {
            LOG.error(e.getMessage(), e);
        }
        getDaoFactory().getProjectDao().flushSession();
        getDaoFactory().getProjectDao().clearSession();
    }

    private void importArrayData(CaArrayFileSet fileSet) {
        ArrayDataImporter arrayDataImporter = getArrayDataImporter();
        arrayDataImporter.importFiles(fileSet, this.dataImportOptions);
    }

    @Override
    FileStatus getInProgressStatus() {
        return FileStatus.IMPORTING;
    }

}
