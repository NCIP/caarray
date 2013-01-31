//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.business.vocabulary;

/**A specific exception caught in the vocabularyservice component.
 * @author John Pike
 *
 */
public class VocabularyServiceException extends Exception {

    private static final long serialVersionUID = 1901721771982992506L;
    
    /**
    *
    */
    public static final int BAD_EVS_SVC_RESPONSE = 1;
    /**
     *
     */
    public static final int TERM_NOT_FOUND = 2;
    private int errorCode;


    /**
     *
     */
    public VocabularyServiceException() {
        super();
    }

    /**
     * @param message msg
     */
    public VocabularyServiceException(String message) {
        super(message);
    }

    /**
     * @param cause throwable
     */
    public VocabularyServiceException(Throwable cause) {
        super(cause);
    }

    /**
     * @param message msg
     * @param cause throwable
     */
    public VocabularyServiceException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * @return errorCode
     */
    public int getErrorCode() {
        return this.errorCode;
    }

    /**
     * @param errorCode the error
     */
    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }
}
