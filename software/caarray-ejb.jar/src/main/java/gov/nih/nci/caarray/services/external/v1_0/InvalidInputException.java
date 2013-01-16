//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.services.external.v1_0;

/**
 * Exception thrown to indicate that a passed in query parameter was
 * invalid.
 * 
 * @author gax
 */
public class InvalidInputException extends ApiException {
    private static final long serialVersionUID = 1L;
    
    /**
     * Constructor with message.
     * @param message message.
     */
    public InvalidInputException(String message) {
        super(message);
    }

    /**
     * Default constructor.
     */
    public InvalidInputException() {
        super();
    }
}
