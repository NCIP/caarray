//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.security;

import com.fiveamsolutions.nci.commons.data.persistent.PersistentObject;

/**
 * Marker interface to indicate entities that should have CSM protection elements
 * created upon save to the db.
 */
public interface Protectable extends PersistentObject {
}
