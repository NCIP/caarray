//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.services.external.v1_0.data;

import gov.nih.nci.caarray.services.external.v1_0.ApiException;


/**
 * Exception thrown to indicate that there was an issue with transferring data over the remote connection.
 * 
 * @author dkokotov
 */
public class DataTransferException extends ApiException {
    private static final long serialVersionUID = 1L;

    /**
     * Constructor for no cause.
     * 
     * @param msg provides detailed description of the exception.
     */
    public DataTransferException(String msg) {
        super(msg);
    }
}
