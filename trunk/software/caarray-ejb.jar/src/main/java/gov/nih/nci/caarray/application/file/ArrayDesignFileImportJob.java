//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.application.file;

import gov.nih.nci.caarray.application.fileaccess.TemporaryFileCacheLocator;
import gov.nih.nci.caarray.domain.array.ArrayDesign;
import gov.nih.nci.caarray.domain.file.FileStatus;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Encapsulates the data to import array design details from a design file.
 */
final class ArrayDesignFileImportJob extends AbstractFileManagementJob {

    private static final long serialVersionUID = 1L;

    private final long arrayDesignId;

    ArrayDesignFileImportJob(String username, ArrayDesign arrayDesign) {
        super(username);
        this.arrayDesignId = arrayDesign.getId();
    }

    long getArrayDesignId() {
        return this.arrayDesignId;
    }

    @Override
    void execute() {
        ArrayDesign arrayDesign = getDaoFactory().getArrayDao().getArrayDesign(getArrayDesignId());
        try {
            getArrayDesignImporter().importArrayDesign(arrayDesign);
        } finally {
            TemporaryFileCacheLocator.getTemporaryFileCache().closeFiles();
        }
    }

    @Override
    void setInProgressStatus() {
        ArrayDesign arrayDesign = getDaoFactory().getArrayDao().getArrayDesign(getArrayDesignId());
        arrayDesign.getDesignFileSet().updateStatus(FileStatus.IMPORTING);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    PreparedStatement getUnexpectedErrorPreparedStatement(Connection con) throws SQLException {
        PreparedStatement s = con.prepareStatement("update caarrayfile set status = ? where id in "
                + "(select design_file from array_design_design_file where array_design = ?)");
        s.setString(1, FileStatus.IMPORT_FAILED.toString());
        s.setLong(2, getArrayDesignId());
        return s;
    }

}
