//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package gov.nih.nci.caarray.application.file;

import gov.nih.nci.caarray.application.fileaccess.FileAccessService;
import gov.nih.nci.caarray.dao.ProjectDao;
import gov.nih.nci.caarray.dao.SearchDao;
import gov.nih.nci.caarray.domain.file.CaArrayFile;
import gov.nih.nci.caarray.domain.file.CaArrayFileSet;
import gov.nih.nci.caarray.domain.file.FileStatus;
import gov.nih.nci.caarray.domain.project.ParentJob;
import gov.nih.nci.caarray.domain.project.Project;
import gov.nih.nci.caarray.magetab.MageTabDocumentSet;
import gov.nih.nci.security.authorization.domainobjects.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.google.inject.Inject;

/**
 * Encapsulates the data necessary for a project file management job.
 */
abstract class AbstractProjectFilesJob extends AbstractFileManagementJob {

    private static final long serialVersionUID = 1L;

    private long projectId;
    private Set<Long> fileIds;
    private ArrayDataImporter arrayDataImporter;
    private MageTabImporter mageTabImporter;
    private ProjectDao projectDao;
    private SearchDao searchDao;
    private String experimentName;

    @SuppressWarnings("PMD.ExcessiveParameterList")
    @Inject
    // CHECKSTYLE:OFF more than 7 parameters are okay for injected constructor
    AbstractProjectFilesJob(String username, Project targetProject,
            CaArrayFileSet fileSet, ArrayDataImporter arrayDataImporter, MageTabImporter mageTabImporter,
            FileAccessService fileAccessService, ProjectDao projectDao, SearchDao searchDao) {
        // CHECKSTYLE:ON
        this(username, targetProject, fileSet, arrayDataImporter, mageTabImporter,
                fileAccessService, projectDao, searchDao, null);
    }

    @SuppressWarnings("PMD.ExcessiveParameterList")
    @Inject
    // CHECKSTYLE:OFF more than 7 parameters are okay for injected constructor
    AbstractProjectFilesJob(String username, Project targetProject,
            CaArrayFileSet fileSet, ArrayDataImporter arrayDataImporter, MageTabImporter mageTabImporter,
            FileAccessService fileAccessService, ProjectDao projectDao, SearchDao searchDao, ParentJob parent) {
    // CHECKSTYLE:ON
        super(username, parent, fileAccessService);
        init(username, targetProject, fileSet, arrayDataImporter, mageTabImporter, projectDao, searchDao);
    }

    @SuppressWarnings("PMD.ExcessiveParameterList")
    // CHECKSTYLE:OFF more than 7 parameters are okay for injected constructor
    final void init(String username, Project targetProject, CaArrayFileSet fileSet, ArrayDataImporter arrayDataImptr,
            MageTabImporter mageTabImptr, ProjectDao pDao, SearchDao sDao) {
        // CHECKSTYLE:ON
        setOwnerName(username);
        this.projectId = targetProject.getId();
        this.experimentName = targetProject.getExperiment().getTitle();
        this.arrayDataImporter = arrayDataImptr;
        this.mageTabImporter = mageTabImptr;
        this.projectDao = pDao;
        this.searchDao = sDao;
        this.fileIds = new HashSet<Long>();
        for (final CaArrayFile file : fileSet.getFiles()) {
            this.fileIds.add(file.getId());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected final void doExecute() {
        executeProjectFilesJob();
        getFileSet().pullUpValidationMessages();
        for (CaArrayFile file : getFileSet().getFiles()) {
            if (file.getParent() != null) {
                getFileAccessService().remove(file);
            }
        }
    }

    /**
     * Primary execution method subclasses must implement.
     */
    protected abstract void executeProjectFilesJob();

    /**
     * {@inheritDoc}
     */
    public String getJobEntityName() {
        return experimentName;
    }

    /**
     * {@inheritDoc}
     */
    public long getJobEntityId() {
        return projectId;
    }

    @Override
    public CaArrayFileSet getFileSet() {
        CaArrayFileSet fileSet = new CaArrayFileSet(getProject());
        List<CaArrayFile> files = searchDao.retrieveByIds(CaArrayFile.class,
                new ArrayList<Long>(this.fileIds));
        fileSet.addAll(files);
        return fileSet;
    }

    Project getProject() {
        return searchDao.retrieve(Project.class, this.projectId);
    }

    void doValidate(CaArrayFileSet fileSet) {
        final MageTabDocumentSet mTabSet = validateAnnotation(fileSet);
        validateArrayData(fileSet, mTabSet);
    }

    private MageTabDocumentSet validateAnnotation(CaArrayFileSet fileSet) {
        return getMageTabImporter().validateFiles(getProject(), fileSet);
    }

    private void validateArrayData(CaArrayFileSet fileSet, MageTabDocumentSet mTabSet) {
        getArrayDataImporter().validateFiles(fileSet, mTabSet, false);
    }

    MageTabImporter getMageTabImporter() {
        return this.mageTabImporter;
    }

    /**
     * @param mageTabImporter the mageTabImporter to set
     */
    @Inject
    public void setMageTabImporter(MageTabImporter mageTabImporter) {
        this.mageTabImporter = mageTabImporter;
    }

    protected ArrayDataImporter getArrayDataImporter() {
        return arrayDataImporter;
    }

    protected ProjectDao getProjectDao() {
        return projectDao;
    }

    /**
     * @return the searchDao
     */
    protected SearchDao getSearchDao() {
        return searchDao;
    }

    /**
     * {@inheritDoc}
     */
    public boolean userHasReadAccess(User user) {
       return userCanAccessProject(user, false);
    }

    /**
     * {@inheritDoc}
     */
    public boolean userHasWriteAccess(User user) {
        return userCanAccessProject(user, true);
    }

    private boolean userCanAccessProject(User user, boolean checkForWriteAccess) {
        boolean hasAccess = false;
        Project p = getProject();
        if (p != null) {
            hasAccess = checkForWriteAccess ? p.hasWritePermission(user) : p.hasReadPermission(user);
        }
        return hasAccess;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PreparedStatement getUnexpectedErrorPreparedStatement(Connection con) throws SQLException {
        final PreparedStatement s = con
                .prepareStatement("update caarrayfile set status = ? where project = ? and status = ?");
        FileStatus newStatus;
        switch (getInProgressStatus()) {
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
        s.setLong(i++, this.projectId);
        s.setString(i++, getInProgressStatus().toString());
        return s;
    }
}
