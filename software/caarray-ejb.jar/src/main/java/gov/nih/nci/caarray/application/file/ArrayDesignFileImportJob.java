//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.application.file;

import gov.nih.nci.caarray.application.ServiceLocatorFactory;
import gov.nih.nci.caarray.application.fileaccess.FileAccessService;
import gov.nih.nci.caarray.dao.ArrayDao;
import gov.nih.nci.caarray.domain.array.ArrayDesign;
import gov.nih.nci.caarray.domain.file.CaArrayFileSet;
import gov.nih.nci.caarray.domain.file.FileStatus;
import gov.nih.nci.caarray.domain.project.JobType;
import gov.nih.nci.security.authorization.domainobjects.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.google.inject.Inject;

/**
 * Encapsulates the data to import array design details from a design file.
 */
final class ArrayDesignFileImportJob extends AbstractFileManagementJob {

    private static final long serialVersionUID = 1L;

    private final long arrayDesignId;
    private final ArrayDao arrayDao;
    private final String arrayDesignName;

    @Inject
    ArrayDesignFileImportJob(String username, ArrayDesign arrayDesign, ArrayDao arrayDao,
            FileAccessService fileAccessService) {
        super(username, fileAccessService);
        this.arrayDesignId = arrayDesign.getId();
        this.arrayDesignName = arrayDesign.getName();
        this.arrayDao = arrayDao;
    }

    /**
     * {@inheritDoc}
     */
    public String getJobEntityName() {
        return arrayDesignName;
    }

    /**
     * {@inheritDoc}
     */
    public long getJobEntityId() {
        return arrayDesignId;
    }

    /**
     * {@inheritDoc}
     */
    public JobType getJobType() {
        return JobType.DESIGN_FILE_IMPORT;
    }

    /**
     * {@inheritDoc}
     */
    public String getExperimentName() {
        return arrayDesignName;
    }

    long getArrayDesignId() {
        return this.arrayDesignId;
    }

    @Override
    protected void doExecute() {
        ArrayDesign arrayDesign = getArrayDesign();
        ArrayDesignImporter importer = new ArrayDesignImporter(ServiceLocatorFactory.getArrayDesignService());
        importer.importArrayDesign(arrayDesign);
    }

    private ArrayDesign getArrayDesign() {
        return arrayDao.getArrayDesign(getArrayDesignId());
    }

    @Override
    protected FileStatus getInProgressStatus() {
        return FileStatus.IMPORTING;
    };

    @Override
    public CaArrayFileSet getFileSet() {
        return getArrayDesign().getDesignFileSet();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PreparedStatement getUnexpectedErrorPreparedStatement(Connection con) throws SQLException {
        final PreparedStatement s = con.prepareStatement("update caarrayfile set status = ? where id in "
                + "(select design_file from array_design_design_file where array_design = ?)");
        s.setString(1, FileStatus.IMPORT_FAILED.toString());
        s.setLong(2, getArrayDesignId());
        return s;
    }

    /**
     * {@inheritDoc}
     */
    public boolean userHasReadAccess(User user) {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    public boolean userHasWriteAccess(User user) {
        return user.getLoginName().equalsIgnoreCase(this.getOwnerName());
    }
}
