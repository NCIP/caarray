//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.util;

import gov.nih.nci.caarray.security.SecurityUtils;
import gov.nih.nci.security.authorization.domainobjects.User;

/**
 * Holds the name of the currently-logged in user in a ThreadLocal.  If the
 * value is unset, return the username for the 'anonymous' user.
 *
 * @see gov.nih.nci.caarray.security.SecurityUtils#ANONYMOUS_USERNAME
 */
public final class UsernameHolder extends com.fiveamsolutions.nci.commons.util.UsernameHolder {

    private static ThreadLocal<User> userThreadLocal = new ThreadLocal<User>();

    private UsernameHolder() {
        // No constructor for util class
    }

    /**
     * @return the User instance for the logged in user for this thread, or the anonymous user
     *         if no user is logged in
     */
    public static User getCsmUser() {
        User csmUser = userThreadLocal.get();
        String userName = getUser();
        if (csmUser == null || !csmUser.getLoginName().equals(userName)) {
            csmUser = SecurityUtils.getAuthorizationManager().getUser(getUser());
            userThreadLocal.set(csmUser);
        }
        return csmUser;
    }
}
