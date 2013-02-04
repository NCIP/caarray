//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package gov.nih.nci.caarray.domain.project;


/**
 * @author jscott
 *
 */
public interface Job extends BaseJob {
    /**
     * @return true if the user has read access to this job
     */
    boolean getUserHasReadAccess();

    /**
     * @return true if the user has write access to this job
     */
    boolean getUserHasWriteAccess();

    /**
     * @return true if the user is the owner of the job
     */
    boolean getUserHasOwnership();

    /**
     * @return the position in the queue
     */
    int getPosition();
}
