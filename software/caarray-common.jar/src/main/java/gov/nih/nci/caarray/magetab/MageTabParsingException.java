//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package gov.nih.nci.caarray.magetab;

import java.io.IOException;

/**
 * Indicates an I/O problem parsing a MAGE-TAB file.
 */
public class MageTabParsingException extends Exception {

    private static final long serialVersionUID = 3909907717560839118L;

    /**
     * New parsing exception with given cause.
     * @param cause the underlying cause
     */
    public MageTabParsingException(IOException cause) {
        super(cause);
    }

    /**
     * New parsing exception with given message and cause.
     * @param message the message
     * @param cause the underlying cause
     */
    public MageTabParsingException(String message, IOException cause) {
        super(message, cause);
    }
    
}
