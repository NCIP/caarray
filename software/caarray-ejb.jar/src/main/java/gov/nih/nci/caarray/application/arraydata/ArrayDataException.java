//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.application.arraydata;


/**
 * Thrown to indicate that an IO exception occurred in the process of handling array data.
 *
 * @author jscott
 *
 */
public class ArrayDataException extends RuntimeException {


    /**
     *
     */
    private static final long serialVersionUID = -1393123523692533699L;

    /**
     * Wraps an Exception.
     *
     * @param cause the Exception being wrapped
     */
    public ArrayDataException(final Exception cause) {
        super(cause);
    }
}

