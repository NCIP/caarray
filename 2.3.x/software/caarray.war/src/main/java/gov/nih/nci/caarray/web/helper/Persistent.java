//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.web.helper;

/**
 * Interface for persistent classes.
 *
 * @author Scott Miller
 */
public interface Persistent {
    /**
     * Get the id of the object.
     *
     * @return the id
     */
    Long getId();
}

