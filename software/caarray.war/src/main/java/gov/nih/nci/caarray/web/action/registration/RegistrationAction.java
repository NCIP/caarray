//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.web.action.registration;

import gov.nih.nci.caarray.application.ConfigurationHelper;
import gov.nih.nci.caarray.application.ServiceLocatorFactory;
import gov.nih.nci.caarray.domain.ConfigParamEnum;
import gov.nih.nci.caarray.domain.country.Country;
import gov.nih.nci.caarray.domain.register.RegistrationRequest;
import gov.nih.nci.caarray.domain.state.State;
import gov.nih.nci.caarray.security.SecurityUtils;
import gov.nih.nci.caarray.web.helper.EmailHelper;
import gov.nih.nci.security.authentication.helper.LDAPHelper;
import gov.nih.nci.security.authorization.domainobjects.User;
import gov.nih.nci.security.dao.UserSearchCriteria;
import gov.nih.nci.security.exceptions.internal.CSInternalConfigurationException;
import gov.nih.nci.security.exceptions.internal.CSInternalInsufficientAttributesException;
import gov.nih.nci.security.exceptions.internal.CSInternalLoginException;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;

import javax.mail.MessagingException;
import javax.servlet.ServletContext;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.struts2.ServletActionContext;
import org.hibernate.criterion.Order;

import com.fiveamsolutions.nci.commons.web.struts2.action.ActionHelper;
import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.Preparable;
import com.opensymphony.xwork2.validator.annotations.CustomValidator;

/**
 * Registration action.  Handles saving and email sending.
 */
// CSM requires Hashtable, servletcontext untyped
@SuppressWarnings({ "PMD.ReplaceHashtableWithMap", "unchecked", "PMD.CyclomaticComplexity" })
public class RegistrationAction extends ActionSupport implements Preparable {

    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(RegistrationAction.class);

    private RegistrationRequest registrationRequest;
    private String password;
    private String passwordConfirm;
    private Boolean ldapAuthenticate;
    private List<Country> countryList;
    private List<State> stateList;
    private final Hashtable<String, String> ldapContextParams = new Hashtable<String, String>();
    private String successMessage;

    /**
     * {@inheritDoc}
     */
    public void prepare() {
        setCountryList(ServiceLocatorFactory.getGenericDataService().retrieveAll(Country.class, Order.asc("name")));
        setStateList(ServiceLocatorFactory.getGenericDataService().retrieveAll(State.class, Order.asc("name")));
        ServletContext context = ServletActionContext.getServletContext();
        Enumeration<String> e = context.getInitParameterNames();
        while (e.hasMoreElements()) {
            String param = e.nextElement();
            if (param.startsWith("ldap")) {
                ldapContextParams.put(param, context.getInitParameter(param));
            }
        }
        registrationRequest = new RegistrationRequest();
        ldapAuthenticate = Boolean.TRUE;
    }

    /**
     * Action to actually save the registration with authentication.
     * @return the directive for the next action / page to be directed to
     */
    public String save() {
        try {
            // We call the service to save, then send email.  This is non-transactional behavior,
            // but it's okay in this case.  The request gets logged to our db, but if email doesn't
            // send, we tell the user to retry.  (We don't send email in service because Email helper
            // makes assumptions about the environment that are inappropriate for the service tier.)
            ServiceLocatorFactory.getRegistrationService().register(getRegistrationRequest());
            LOGGER.debug("done saving registration request; sending email");
            EmailHelper.registerEmail(getRegistrationRequest());
            EmailHelper.registerEmailAdmin(getRegistrationRequest());
            setSuccessMessage(ConfigurationHelper.getConfiguration().getString(ConfigParamEnum.THANKS_MESSAGE.name()));

            return Action.SUCCESS;
        } catch (MessagingException me) {
            LOGGER.error("Failed to send an email", me);
            ActionHelper.saveMessage(getText("registration.emailFailure"));
            return Action.INPUT;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void validate() {
        super.validate();
        if (isLdapInstall()) {
            validateLdap();
        } else {
            validateNonLdap();
        }
    }

    private void validateLdap() {
        try {
            if (ldapAuthenticate
                    && (StringUtils.isBlank(getPassword())
                            || !LDAPHelper.authenticate(ldapContextParams , registrationRequest.getLoginName(),
                                                        getPassword().toCharArray(), null))) {
                addActionError(getText("registration.ldapLookupFailure"));
            }
        } catch (CSInternalLoginException e) {
            // CSM throws this exception on valid user / wrong pass
            addActionError(getText("registration.ldapLookupFailure"));
        } catch (CSInternalConfigurationException e) {
            addActionError(e.getMessage());
            LOGGER.error("Unable to validate", e);
        } catch (CSInternalInsufficientAttributesException e) {
            addActionError(e.getMessage());
            LOGGER.error("Unable to validate", e);
        }
    }

    private void validateNonLdap() {
        if (StringUtils.isNotBlank(getRegistrationRequest().getLoginName())
                && (SecurityUtils.getAuthorizationManager().getUser(getRegistrationRequest().getLoginName()) != null)) {
            addActionError(getText("registration.usernameInUse"));
        }
        if (StringUtils.isNotBlank(getRegistrationRequest().getEmail())) {
            User searchUser = new User();
            searchUser.setEmailId(getRegistrationRequest().getEmail());
            if (!SecurityUtils.getAuthorizationManager().getObjects(new UserSearchCriteria(searchUser)).isEmpty()) {
                addActionError(getText("registration.emailAddressInUse"));
            }
        }
    }

    /*
     *
     * Getters / setters below here
     *
     */

    /**
     * @return the user
     */
    @CustomValidator(type = "hibernate")
    public RegistrationRequest getRegistrationRequest() {
        return this.registrationRequest;
    }

    /**
     * @param registrationRequest the request to set
     */
    public void setRegistrationRequest(RegistrationRequest registrationRequest) {
        this.registrationRequest = registrationRequest;
    }

    /**
     * @return the countryList
     */
    public List<Country> getCountryList() {
        return countryList;
    }

    /**
     * @param countryList the countryList to set
     */
    public void setCountryList(List<Country> countryList) {
        this.countryList = countryList;
    }

    /**
     * @return the stateList
     */
    public List<State> getStateList() {
        return stateList;
    }

    /**
     * @param stateList the stateList to set
     */
    public void setStateList(List<State> stateList) {
        this.stateList = stateList;
    }

    /**
     * @return the password
     */
    public String getPassword() {
        return password;
    }

    /**
     * @param password the password to set
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * @return the passwordConfirmation
     */
    public String getPasswordConfirm() {
        return passwordConfirm;
    }

    /**
     * @param passwordConfirm the passwordConfirmation to set
     */
    public void setPasswordConfirm(String passwordConfirm) {
        this.passwordConfirm = passwordConfirm;
    }

    /**
     * @return the ldapAuthenticate
     */
    public Boolean getLdapAuthenticate() {
        return ldapAuthenticate;
    }

    /**
     * @param ldapAuthenticate the ldapAuthenticate to set
     */
    public void setLdapAuthenticate(Boolean ldapAuthenticate) {
        this.ldapAuthenticate = ldapAuthenticate;
    }

    /**
     * @return is ldap install?
     */
    public boolean isLdapInstall() {
        return Boolean.parseBoolean(ldapContextParams.get("ldap.install"));
    }

    /**
     * @return the successMessage
     */
    public String getSuccessMessage() {
        return successMessage;
    }

    /**
     * @param successMessage the successMessage to set
     */
    public void setSuccessMessage(String successMessage) {
        this.successMessage = successMessage;
    }
}

