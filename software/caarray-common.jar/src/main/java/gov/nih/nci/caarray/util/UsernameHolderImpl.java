//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.util;

/**
 * Delegates to the static UsernameHolder class. This needs to be factored away so that no
 * access remains to the static class.
 * @see gov.nih.nci.caarray.util.CaArrayUsernameHolder
 * @author jscott
 *
 */
public class UsernameHolderImpl implements UsernameHolder {
    /**
     * {@inheritDoc}
     */
    public void setUser(String user) {
        gov.nih.nci.caarray.util.CaArrayUsernameHolder.setUser(user);      
    }

    /**
     * {@inheritDoc}
     */
    public String getUser() {
        return gov.nih.nci.caarray.util.CaArrayUsernameHolder.getUser();
    }
    
}
