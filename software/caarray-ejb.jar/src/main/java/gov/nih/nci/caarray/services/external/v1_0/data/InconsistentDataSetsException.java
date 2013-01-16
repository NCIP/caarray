//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.services.external.v1_0.data;

import gov.nih.nci.caarray.services.external.v1_0.ApiException;


/**
 * Exception thrown to indicate that the data sets requested in a getDataSet call were inconsistent, e.g. did not 
 * correspond to the same design element list.
 * 
 * @author dkokotov
 */
public class InconsistentDataSetsException extends ApiException {
    private static final long serialVersionUID = 1L;

    /**
     * Constructor for no cause.
     * 
     * @param msg provides detailed description of the exception.
     */
    public InconsistentDataSetsException(String msg) {
        super(msg);
    }
}
