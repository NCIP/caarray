//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.web.upgrade;

/**
 * @author Winston Cheng
 *
 */
public enum MigrationStatus {
    /**
     * Migration pending.
     */
    PENDING,
    /**
     * Migration in progress.
     */
    UPDATING,
    /**
     * Migration complete.
     */
    COMPLETE,
    /**
     * Migration error.
     */
    ERROR;
}
