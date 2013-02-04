//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package gov.nih.nci.caarray.services.external.v1_0;

import gov.nih.nci.caarray.external.v1_0.CaArrayEntityReference;

/**
 * Exception thrown to indicate that a passed in CaArrayEntityReference was
 * invalid in some general way. Use of one of the subclasses of this may be more
 * appropriate for certain specific types of invalid references. 
 * 
 * @author dkokotov
 */
public class InvalidReferenceException extends InvalidInputException {
    private static final long serialVersionUID = 1L;
    
    private final CaArrayEntityReference reference;
    
    /**
     * Constructor for no cause.
     * @param reference the problematic reference
     */
    public InvalidReferenceException(CaArrayEntityReference reference) {
        super(reference == null ? null : reference.getId());
        this.reference = reference;
    }

    /**
     * Constructor with message.
     * @param reference the problematic reference
     * @param message message.
     */
    public InvalidReferenceException(CaArrayEntityReference reference, String message) {
        super(reference.getId() + " : " + message);
        this.reference = reference;
    }

    /**
     * @return the reference
     */
    public CaArrayEntityReference getReference() {
        return reference;
    }
}
