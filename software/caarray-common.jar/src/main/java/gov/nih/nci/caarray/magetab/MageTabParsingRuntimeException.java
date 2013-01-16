//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.magetab;

/**
 * Indicates a programming error or otherwise unexpected runtime exception in MAGE-TAB parsing.
 */
public class MageTabParsingRuntimeException extends RuntimeException {

    private static final long serialVersionUID = -8373094799214834514L;

    /**
     * Creates a parsing exception from an originating <code>Throwable</code>.
     * 
     * @param cause the cause
     */
    public MageTabParsingRuntimeException(Throwable cause) {
        super(cause);
    }

}
