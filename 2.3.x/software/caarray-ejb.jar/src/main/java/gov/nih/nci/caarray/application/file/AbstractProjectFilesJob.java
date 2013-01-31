//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.application.file;

import gov.nih.nci.caarray.application.arraydata.ArrayDataService;
import gov.nih.nci.caarray.application.translation.magetab.MageTabTranslator;
import gov.nih.nci.caarray.domain.file.CaArrayFile;
import gov.nih.nci.caarray.domain.file.CaArrayFileSet;
import gov.nih.nci.caarray.domain.file.FileStatus;
import gov.nih.nci.caarray.domain.project.Project;
import gov.nih.nci.caarray.magetab.MageTabDocumentSet;
import gov.nih.nci.caarray.util.j2ee.ServiceLocatorFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Encapsulates the data necessary for a project file management job.
 */
abstract class AbstractProjectFilesJob extends AbstractFileManagementJob {

    private static final long serialVersionUID = 1L;

    private final long projectId;
    private final Set<Long> fileIds = new HashSet<Long>();

    AbstractProjectFilesJob(String username, Project targetProject, CaArrayFileSet fileSet) {
        super(username);
        this.projectId = targetProject.getId();
        for (CaArrayFile file : fileSet.getFiles()) {
            this.fileIds.add(file.getId());
        }
    }

    Set<Long> getFileIds() {
        return this.fileIds;
    }

    long getProjectId() {
        return this.projectId;
    }

    MageTabTranslator getMageTabTranslator() {
        return (MageTabTranslator) ServiceLocatorFactory.getLocator().lookup(MageTabTranslator.JNDI_NAME);
    }

    ArrayDataService getArrayDataService() {
        return (ArrayDataService) ServiceLocatorFactory.getLocator().lookup(ArrayDataService.JNDI_NAME);
    }

    CaArrayFileSet getFileSet(Project project) {
        CaArrayFileSet fileSet = new CaArrayFileSet(project);
        List<CaArrayFile> files = getDaoFactory().getSearchDao().retrieveByIds(CaArrayFile.class,
                new ArrayList<Long>(this.fileIds));
        fileSet.addAll(files);
        return fileSet;
    }

    Project getProject() {
        return getDaoFactory().getSearchDao().retrieve(Project.class, this.projectId);
    }

    void doValidate(CaArrayFileSet fileSet) {
        MageTabDocumentSet mTabSet = validateAnnotation(fileSet);
        validateArrayData(fileSet, mTabSet);
    }

    MageTabDocumentSet validateAnnotation(CaArrayFileSet fileSet) {
        return getMageTabImporter().validateFiles(fileSet);
    }

    private void validateArrayData(CaArrayFileSet fileSet, MageTabDocumentSet mTabSet) {
        getArrayDataImporter().validateFiles(fileSet, mTabSet);
    }

    ArrayDataImporter getArrayDataImporter() {
        return new ArrayDataImporter(getArrayDataService(), getDaoFactory());
    }

    MageTabImporter getMageTabImporter() {
        return new MageTabImporter(getMageTabTranslator(), getDaoFactory());
    }

    @Override
    void setInProgressStatus() {
        setStatus(getInProgressStatus());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    PreparedStatement getUnexpectedErrorPreparedStatement(Connection con) throws SQLException {
        PreparedStatement s = con.prepareStatement(
                "update caarrayfile set status = ? where project = ? and status = ?");
        FileStatus newStatus;
        switch(getInProgressStatus()) {
        case IMPORTING:
            newStatus = FileStatus.IMPORT_FAILED;
            break;
        case VALIDATING:
            newStatus = FileStatus.VALIDATION_ERRORS;
            break;
        default:
            newStatus = FileStatus.IMPORT_FAILED;
        }
        int i = 1;
        s.setString(i++, newStatus.toString());
        s.setLong(i++, getProjectId());
        s.setString(i++, getInProgressStatus().toString());
        return s;
    }

    private void setStatus(FileStatus status) {
        CaArrayFileSet fileSet = getFileSet(getProject());
        fileSet.updateStatus(status);
    }

    abstract FileStatus getInProgressStatus();
}
