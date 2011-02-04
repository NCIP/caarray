package gov.nih.nci.caarray.util;

import org.apache.log4j.Logger;

/**
 * Delegates to the static UsernameHolder class. This needs to be factored away so that no
 * access remains to the static class.
 * @see gov.nih.nci.caarray.util.CaArrayUsernameHolder
 * @author jscott
 *
 */
public class UsernameHolderImpl implements UsernameHolder {
    private static final Logger LOG = Logger.getLogger(UsernameHolder.class);

    /**
     * {@inheritDoc}
     */
    public void setUser(String user) {
    	LOG.error(String.format(">>>>>>> setUser(\"%s\")", user));
        CaArrayUsernameHolder.setUser(user);
    }

    /**
     * {@inheritDoc}
     */
    public String getUser() {
        String user = CaArrayUsernameHolder.getUser();
       	LOG.error(String.format("<<<<<<< getUser() == \"%s\"", user));
		return user;
    }
    
}