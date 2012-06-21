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