//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.services.external.v1_0;


/**
 * Base class for exceptions for the External API.
 * 
 * @author dkokotov
 */
public class ApiException extends Exception {
    private static final long serialVersionUID = 1L;

    /**
     * Constructor for no message.
     */
    public ApiException() {
        super();
    }

    /**
     * Constructor with message.
     * 
     * @param msg provides detailed description of the exception.
     */
    public ApiException(String msg) {
        super(msg);
    }
}
