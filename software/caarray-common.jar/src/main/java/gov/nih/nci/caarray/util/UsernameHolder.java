//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.util;

/**
 * Holds the name of the currently-logged in user.
 */
public interface UsernameHolder {
    /**
     * @param user the user to set for the current thread
     */
    void setUser(String user);

    /**
     * @return the currently logged in user for this thread, or the anonymous user
     *         if no user is logged in
     */
    String getUser();
}
