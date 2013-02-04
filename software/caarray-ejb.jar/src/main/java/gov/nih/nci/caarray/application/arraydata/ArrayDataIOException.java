//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package gov.nih.nci.caarray.application.arraydata;

import java.io.IOException;

/**
 * Thrown to indicate that an IO exception occurred in the process of handling array data.
 *
 * @author jscott
 *
 */
public class ArrayDataIOException extends RuntimeException {

    /**
     *
     */
    private static final long serialVersionUID = 8446325181742297980L;

    /**
     * Wraps an IOException.
     *
     * @param cause the IOException being wrapped
     */
    public ArrayDataIOException(final IOException cause) {
        super(cause);
    }

}
