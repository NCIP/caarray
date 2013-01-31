//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.security;

import java.util.Collection;

import com.fiveamsolutions.nci.commons.data.persistent.PersistentObject;

/**
 * Marker interface for entities that, while they themselves are not directly Protectable,
 * are related to Protectable elements and are secured based on their relationships to those Protectables.
 */
public interface ProtectableDescendent extends PersistentObject {
    /**
     * Retrieve the set of protectables which are related to this. Any privileges for any of those
     * Protectables flow down to this entity. This method should return an empty Collection to indicate 
     * that it does not inherit privileges at all, and null to indicate that privilege checking
     * based on Protectables does not apply to this instance (ie it is effectively not a ProtectableDescendent).
     * @return the Protectable entities from which this entity is descended. A null value indicates
     * that this instance should be treated as though it did not implement ProtectableDescendent
     */
    Collection<? extends Protectable> relatedProtectables();
}
