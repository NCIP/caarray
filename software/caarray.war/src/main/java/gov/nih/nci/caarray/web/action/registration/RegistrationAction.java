/**
 * The software subject to this notice and license includes both human readable
 * source code form and machine readable, binary, object code form. The caarray-war
 * Software was developed in conjunction with the National Cancer Institute
 * (NCI) by NCI employees and 5AM Solutions, Inc. (5AM). To the extent
 * government employees are authors, any rights in such works shall be subject
 * to Title 17 of the United States Code, section 105.
 *
 * This caarray-war Software License (the License) is between NCI and You. You (or
 * Your) shall mean a person or an entity, and all other entities that control,
 * are controlled by, or are under common control with the entity. Control for
 * purposes of this definition means (i) the direct or indirect power to cause
 * the direction or management of such entity, whether by contract or otherwise,
 * or (ii) ownership of fifty percent (50%) or more of the outstanding shares,
 * or (iii) beneficial ownership of such entity.
 *
 * This License is granted provided that You agree to the conditions described
 * below. NCI grants You a non-exclusive, worldwide, perpetual, fully-paid-up,
 * no-charge, irrevocable, transferable and royalty-free right and license in
 * its rights in the caarray-war Software to (i) use, install, access, operate,
 * execute, copy, modify, translate, market, publicly display, publicly perform,
 * and prepare derivative works of the caarray-war Software; (ii) distribute and
 * have distributed to and by third parties the caarray-war Software and any
 * modifications and derivative works thereof; and (iii) sublicense the
 * foregoing rights set out in (i) and (ii) to third parties, including the
 * right to license such rights to further third parties. For sake of clarity,
 * and not by way of limitation, NCI shall have no right of accounting or right
 * of payment from You or Your sub-licensees for the rights granted under this
 * License. This License is granted at no charge to You.
 *
 * Your redistributions of the source code for the Software must retain the
 * above copyright notice, this list of conditions and the disclaimer and
 * limitation of liability of Article 6, below. Your redistributions in object
 * code form must reproduce the above copyright notice, this list of conditions
 * and the disclaimer of Article 6 in the documentation and/or other materials
 * provided with the distribution, if any.
 *
 * Your end-user documentation included with the redistribution, if any, must
 * include the following acknowledgment: This product includes software
 * developed by 5AM and the National Cancer Institute. If You do not include
 * such end-user documentation, You shall include this acknowledgment in the
 * Software itself, wherever such third-party acknowledgments normally appear.
 *
 * You may not use the names "The National Cancer Institute", "NCI", or "5AM"
 * to endorse or promote products derived from this Software. This License does
 * not authorize You to use any trademarks, service marks, trade names, logos or
 * product names of either NCI or 5AM, except as required to comply with the
 * terms of this License.
 *
 * For sake of clarity, and not by way of limitation, You may incorporate this
 * Software into Your proprietary programs and into any third party proprietary
 * programs. However, if You incorporate the Software into third party
 * proprietary programs, You agree that You are solely responsible for obtaining
 * any permission from such third parties required to incorporate the Software
 * into such third party proprietary programs and for informing Your
 * sub-licensees, including without limitation Your end-users, of their
 * obligation to secure any required permissions from such third parties before
 * incorporating the Software into such third party proprietary software
 * programs. In the event that You fail to obtain such permissions, You agree
 * to indemnify NCI for any claims against NCI by such third parties, except to
 * the extent prohibited by law, resulting from Your failure to obtain such
 * permissions.
 *
 * For sake of clarity, and not by way of limitation, You may add Your own
 * copyright statement to Your modifications and to the derivative works, and
 * You may provide additional or different license terms and conditions in Your
 * sublicenses of modifications of the Software, or any derivative works of the
 * Software as a whole, provided Your use, reproduction, and distribution of the
 * Work otherwise complies with the conditions stated in this License.
 *
 * THIS SOFTWARE IS PROVIDED "AS IS," AND ANY EXPRESSED OR IMPLIED WARRANTIES,
 * (INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY,
 * NON-INFRINGEMENT AND FITNESS FOR A PARTICULAR PURPOSE) ARE DISCLAIMED. IN NO
 * EVENT SHALL THE NATIONAL CANCER INSTITUTE, 5AM SOLUTIONS, INC. OR THEIR
 * AFFILIATES BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS;
 * OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR
 * OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package gov.nih.nci.caarray.web.action.registration;

import gov.nih.nci.caarray.domain.contact.Organization;
import gov.nih.nci.caarray.domain.country.Country;
import gov.nih.nci.caarray.domain.register.RegistrationRequest;
import gov.nih.nci.caarray.domain.state.State;
import gov.nih.nci.caarray.web.action.ActionHelper;
import gov.nih.nci.caarray.web.helper.EmailHelper;
import gov.nih.nci.caarray.web.util.CacheManager;
import gov.nih.nci.security.authentication.helper.LDAPHelper;
import gov.nih.nci.security.authorization.domainobjects.User;
import gov.nih.nci.security.dao.UserSearchCriteria;
import gov.nih.nci.security.exceptions.CSException;
import gov.nih.nci.security.exceptions.internal.CSInternalConfigurationException;
import gov.nih.nci.security.exceptions.internal.CSInternalInsufficientAttributesException;
import gov.nih.nci.security.exceptions.internal.CSInternalLoginException;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;

import javax.mail.MessagingException;
import javax.servlet.ServletContext;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.interceptor.validation.SkipValidation;

import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.Preparable;
import com.opensymphony.xwork2.validator.annotations.CustomValidator;
import com.opensymphony.xwork2.validator.annotations.Validation;

/**
 * @author John Hedden
 * @author Akhil Bhaskar (Amentra, Inc.)
 *
 */
// CSM requires Hashtable, servletcontext untyped
@SuppressWarnings({ "PMD.ReplaceHashtableWithMap", "unchecked", "PMD.CyclomaticComplexity" })
@Validation
public class RegistrationAction extends ActionSupport implements Preparable {

    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(RegistrationAction.class);

    private RegistrationRequest registrationRequest;
    private String password;
    private String passwordConfirm;
    private Boolean ldapAuthenticate;
    private List<Organization> organizationList = new ArrayList<Organization>();
    private List<Country> countryList = new ArrayList<Country>();
    private List<State> stateList = new ArrayList<State>();
    private List<UserRole> roleList = new ArrayList<UserRole>();
    private static final Hashtable<String, String> LDAP_CONTEXT_PARAMS = new Hashtable<String, String>();

    /**
     * {@inheritDoc}
     */
    public void prepare() {
        if (getCountryList().isEmpty()) {
            setCountryList(CacheManager.getInstance().getCountries());
        }
        if (getStateList().isEmpty()) {
            setStateList(CacheManager.getInstance().getStates());
        }
        if (getRoleList().isEmpty()) {
            setRoleList(CacheManager.getInstance().getRoles());
        }
        if (LDAP_CONTEXT_PARAMS.isEmpty()) {
            ServletContext context = ServletActionContext.getServletContext();
            Enumeration<String> e = context.getInitParameterNames();
            while (e.hasMoreElements()) {
                String param = e.nextElement();
                if (param.startsWith("ldap")) {
                    LDAP_CONTEXT_PARAMS.put(param, context.getInitParameter(param));
                }
            }
        }
    }

    /**
     * Action to load the registration form.
     *
     * @return the directive for the next action / page to be directed to
     */
    @Override
    @SkipValidation
    public String input() {
        setupForm();
        return Action.INPUT;
    }

    /**
     * Action to cancel.
     *
     * @return the directive for the next action / page to be directed to
     */
    @SkipValidation
    public String cancel() {
        setupForm();
        return Action.INPUT;
    }

    /**
     * Action to actually save the registration.
     * @return the directive for the next action / page to be directed to
     */
    public String save() {
        try {
            persist();
            LOGGER.info("done saving registration request; sending email");
            EmailHelper.registerEmail(getRegistrationRequest());
            EmailHelper.registerEmailAdmin(getRegistrationRequest());

            return Action.SUCCESS;
        } catch (MessagingException me) {
            LOGGER.error("Failed to send an email", me);
            ActionHelper.saveMessage(getText("registration.emailFailure"));
            return Action.INPUT;
        }
    }

    /**
     * Action to actually save the registration with authentication.
     * @return the directive for the next action / page to be directed to
     * @throws CSException on CSM error
     * @throws CSInternalInsufficientAttributesException on CSM error
     * @throws CSInternalConfigurationException on CSM error
     */

    public String saveAuthenticate() throws CSException, CSInternalConfigurationException,
        CSInternalInsufficientAttributesException {

        if (isLdapInstall()) {
            try {
                LDAPHelper.authenticate(LDAP_CONTEXT_PARAMS , registrationRequest.getLoginName(),
                                        getPassword().toCharArray(), null);
            } catch (CSInternalLoginException e) {
                // CSM throws this exception on invalid user/password
                ActionHelper.saveMessage(getText("registration.ldapLookupFailure"));
                return Action.INPUT;
            }
        } else {
            if (!validateDBUniqueFields()) {
                return Action.INPUT;
            }
        }

        return save();
    }


    private boolean validateDBUniqueFields() throws CSException {
        boolean retval = true;
        if (getRegistrationRequest() != null) {
            if (StringUtils.isNotBlank(getRegistrationRequest().getLoginName())
                    && (ActionHelper.getUserProvisioningManager()
                                    .getUser(getRegistrationRequest().getLoginName()) != null)) {
                ActionHelper.saveMessage(getText("registration.usernameInUse"));
                retval = false;
            }
            if (StringUtils.isNotBlank(getRegistrationRequest().getEmail())) {
                User searchUser = new User();
                searchUser.setEmailId(getRegistrationRequest().getEmail());
                if (!ActionHelper.getUserProvisioningManager()
                                 .getObjects(new UserSearchCriteria(searchUser)).isEmpty()) {
                    ActionHelper.saveMessage(getText("registration.emailAddressInUse"));
                    retval = false;
                }
            }
        }
        return retval;
    }

    private void persist() {
        ActionHelper.getRegistrationService().register(getRegistrationRequest());
    }

    /**
     * method to setup drop downs on form.
     */
    private void setupForm() {
        registrationRequest = new RegistrationRequest();

        // populate the initial radio for LDAP
        if (null == getLdapAuthenticate()) {
            setLdapAuthenticate(Boolean.TRUE);
        }
    }

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
     * @return the organizationList
     */
    public List<Organization> getOrganizationList() {
        return organizationList;
    }

    /**
     * @param organizationList the organizationList to set
     */
    public void setOrganizationList(List<Organization> organizationList) {
        this.organizationList = organizationList;
    }

    /**
     * @return the roleList
     */
    public List<UserRole> getRoleList() {
        return roleList;
    }

    /**
     * @param roleList the roleList to set
     */
    public void setRoleList(List<UserRole> roleList) {
        this.roleList = roleList;
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
        return Boolean.parseBoolean(LDAP_CONTEXT_PARAMS.get("ldap.install"));
    }
}

