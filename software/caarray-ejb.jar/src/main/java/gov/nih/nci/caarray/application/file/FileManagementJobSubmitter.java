//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package gov.nih.nci.caarray.application.file;

/**
 * Passes the job provided to the FileManagementMDB for handling. Implemented as an interface so that
 * unit tests may substitute a non-JMS based approach.
 */
public interface FileManagementJobSubmitter {

    /**
     * Submits the job to the FilemanagementMDB.
     *
     * @param job the job to submit.
     */
    void submitJob(AbstractFileManagementJob job);

}
