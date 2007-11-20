package gov.nih.nci.caarray.web.util;

import java.security.Security;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.log4j.Logger;

/**
 * @author John Hedden
 *
 */
public final class LDAPUtil {

    private static final Logger LOG = Logger.getLogger(LDAPUtil.class);
    private static final String INITCTX = "com.sun.jndi.ldap.LdapCtxFactory";
    private static Map<String, String> configMap = new HashMap<String, String>();
    @SuppressWarnings("PMD.ReplaceHashtableWithMap")
    private static Hashtable<String, String> env = new Hashtable<String, String>();

    private LDAPUtil() {
     //do nothing
    }

    static {
        setLDAPConfigPara();
        setLDAPEnv();
    }

    private static void setLDAPConfigPara() {
        try {
            PropertiesConfiguration config = new PropertiesConfiguration();
            config.setDelimiterParsingDisabled(true);
            config.load("default.properties");
            configMap.put("ldapHost", config.getString("ldap.host"));
            configMap.put("ldapSearchbase", config.getString("ldap.searchbase"));
        } catch (ConfigurationException ce) {
            LOG.error("An IO error occured. Please check the path or filename.");
        }
    }


    private static void setLDAPEnv() {
        Security.addProvider(new com.sun.net.ssl.internal.ssl.Provider());
        env.clear();
        env.put(Context.INITIAL_CONTEXT_FACTORY, INITCTX);
        env.put(Context.PROVIDER_URL, configMap.get("ldapHost"));
        env.put(Context.SECURITY_AUTHENTICATION, "simple");
        env.put(Context.SECURITY_PROTOCOL, "ssl");
    }


    private static String getFDN(String loginName) {
        String[] attrIDs = {"cn" };
        String searchFilter = "(cn=" + loginName + "*)";

        try {
            DirContext ctx = new InitialDirContext(env);

            SearchControls ctls = new SearchControls();
            ctls.setReturningAttributes(attrIDs);
            ctls.setSearchScope(SearchControls.SUBTREE_SCOPE);

            String fdn = null;
            NamingEnumeration<SearchResult> searchEnum = ctx.search(configMap.get("ldapSearchbase"),
                                                                    searchFilter,
                                                                    ctls);
            ctx.close();

            while (searchEnum.hasMore()) {
                SearchResult sr = searchEnum.next();
                fdn = sr.getName() + "," + configMap.get("ldapSearchbase");
                LOG.debug("sr.getName() = " + sr.getName() + " " + "Dn = " + fdn);
                return fdn;
            }
        } catch (Exception ex) {
            LOG.error("Connect ldap attempt failed");
            return null;
        }
        return null;
    }


    /**
     * Return the result of user authentication with LDAP server.
     *
     * @param loginName the login name of the user
     * @param passwd the password of the user
     * @return true for successful authentication <br>
     *         false for failed authentication
     */
    public static boolean ldapAuthenticateUser(String loginName, String passwd) {
        String fdn = getFDN(loginName);

        if (null == fdn) {
            return false;
        }

        try {
            env.put(Context.SECURITY_PRINCIPAL, fdn);
            env.put(Context.SECURITY_CREDENTIALS, passwd);
            DirContext ctx = new InitialDirContext(env);
            LOG.debug("User authentication successful");
            ctx.close();
            setLDAPEnv();
            return true;
        } catch (Exception ex) {
            setLDAPEnv();
            LOG.error("User authentication failed", ex);
            return false;
        }
    }

    /**
     * @return the env
     */
    @SuppressWarnings("PMD.ReplaceHashtableWithMap")
    public Hashtable<String, String> getEnv() {
        return env;
    }

    /**
     * @param env the env to set
     */
    @SuppressWarnings("PMD.ReplaceHashtableWithMap")
    public void setEnv(Hashtable<String, String> env) {
        LDAPUtil.env = env;
    }

    /**
     * @return the configMap
     */
    public Map<String, String> getConfigMap() {
        return configMap;
    }

    /**
     * @param configMap the configMap to set
     */
    public void setConfigMap(Map<String, String> configMap) {
        LDAPUtil.configMap = configMap;
    }
}