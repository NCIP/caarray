/**
 * 
 */
package gov.nih.nci.caarray.common;

/**
 * Intended to be superclass of all applicatin specific RuntimeExceptions. 
 * @author asy
 *
 */
public class CaArrayRutimeException extends RuntimeException {

    /**
     * @param message the msg
     */
    public CaArrayRutimeException(String message) {
        super(message);
    }

    /**
     * @param message the msg
     * @param cause the cause
     */
    public CaArrayRutimeException(String message, Throwable cause) {
        super(message, cause);
    }

}
