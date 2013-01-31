//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.magetab;

/**
 * Indicates a problem while exporting the MAGE-TAB object graph.
 *
 * @author Rashmi Srinivasa
 */
public class MageTabExportException extends RuntimeException {
    private static final long serialVersionUID = -975369312995150402L;

    /**
     * Creates a MAGE-TAB export exception from an originating <code>Throwable</code>.
     *
     * @param cause the cause
     */
    public MageTabExportException(Throwable cause) {
        super(cause);
    }

    /**
     * Creates a MAGE-TAB export exception with an error message.
     *
     * @param message the error message
     */
    public MageTabExportException(String message) {
        super(message);
    }
}
