//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package gov.nih.nci.caarray.domain.project;

import gov.nih.nci.caarray.domain.file.CaArrayFileSet;
import gov.nih.nci.security.authorization.domainobjects.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.UUID;

/**
 * Adds functionality to Job for execution purposes.
 * @author jscott
 *
 */
public interface ExecutableJob extends BaseJob {
    /**
     * Perform the job.
     */
    void execute();

    /**
     * Set an uuid for the job.
     * @param id the uuid to set.
     */
    void setJobId(UUID id);

    /**
     * Set the appropriate fileset and job status values, indicating that the job is in the queue.
     */
    void markAsInQueue();

    /**
     * Set the appropriate fileset and job status values, indicating that the job has been cancelled.
     */
    void markAsCancelled();

    /**
     * Set the appropriate fileset and job status values, indicating that the job is in progress.
     */
    void markAsInProgress();

    /**
     * Set the appropriate job status values, indicating that the job has been processed.
     */
    void markAsProcessed();

    /**
     * @param user the given user
     * @return true if the given user has read access to this job
     */
    boolean userHasReadAccess(User user);

    /**
     * @param user the given user
     * @return true if the given user has write access to this job
     */
    boolean userHasWriteAccess(User user);

    /**
     * Returns a prepared statement for the executer of the job to run if the job fails
     * with an unexpected error.
     *
     * This is ugly and should be replaced with something that runs at a higher level of
     * abstraction. We should not be doing SQL at this level.
     *
     * @param connection the connection to prepare the statement on
     * @return the prepared statement
     * @throws SQLException if a SQL error occurs
     */
    PreparedStatement getUnexpectedErrorPreparedStatement(Connection connection) throws SQLException;

    /**
     * @return the file set this job is operating on
     */
    CaArrayFileSet getFileSet();
}
