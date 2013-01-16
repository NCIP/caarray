//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.services.external.v1_0;

import gov.nih.nci.caarray.external.v1_0.CaArrayEntityReference;

/**
 * Exception thrown to indicate that a passed in CaArrayEntityReference referred
 * to an entity that was of a different type than expected by the service method.
 * 
 * @author dkokotov
 */
public class IncorrectEntityTypeException extends InvalidReferenceException {
    private static final long serialVersionUID = 1L;
    
    /**
     * Constructor for no cause.
     * @param reference the problematic reference
     */
    public IncorrectEntityTypeException(CaArrayEntityReference reference) {
        super(reference);
    }

    /**
     * Constructor with message.
     * @param reference the problematic reference
     * @param message message.
     */
    public IncorrectEntityTypeException(CaArrayEntityReference reference, String message) {
        super(reference, message);
    }
}
