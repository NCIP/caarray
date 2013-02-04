//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package gov.nih.nci.caarray.web.util;

/**
 * @author John Hedden
 * Constant values used throughout the application.
 *
 */
public final class Constants {
    //~ Static fields/initializers =============================================

    /** The name of the ResourceBundle used in this application. */
    public static final String BUNDLE_KEY = "ApplicationResources";

    /** The encryption algorithm key to be used for passwords. */
    public static final String ENC_ALGORITHM = "algorithm";

    /** A flag to indicate if passwords should be encrypted. */
    public static final String ENCRYPT_PASSWORD = "encryptPassword";

    /** File separator from System properties. */
    public static final String FILE_SEP = System.getProperty("file.separator");

    /** User home from System properties. */
    public static final String USER_HOME = System.getProperty("user.home") + FILE_SEP;

    /**
     * Session scope attribute that holds the locale set by the user. By setting this key
     * to the same one that Struts uses, we get synchronization in Struts w/o having
     * to do extra work or have two session-level variables.
     */
    public static final String PREFERRED_LOCALE_KEY = "org.apache.struts2.action.LOCALE";

    private Constants() {
        // nothing to do here
    }
}
