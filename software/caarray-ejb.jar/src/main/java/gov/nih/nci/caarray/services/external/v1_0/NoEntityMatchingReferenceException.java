//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.services.external.v1_0;

import gov.nih.nci.caarray.external.v1_0.CaArrayEntityReference;

/**
 * Exception thrown to indicate that there was no entity matching a passed in 
 * CaArrayEntityReference.
 * 
 * @author dkokotov
 */
public class NoEntityMatchingReferenceException extends InvalidReferenceException {
    private static final long serialVersionUID = 1L;
    
    /**
     * Constructor for no cause.
     * @param reference the problematic reference
     */
    public NoEntityMatchingReferenceException(CaArrayEntityReference reference) {
        super(reference);
    }
    
    /**
     * Constructor with message.
     * @param reference the problematic reference
     * @param message message.
     */
    public NoEntityMatchingReferenceException(CaArrayEntityReference reference, String message) {
        super(reference, message);
    }
}
