//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.web.helper;

import gov.nih.nci.caarray.domain.ConfigParamEnum;
import gov.nih.nci.caarray.domain.contact.Person;
import gov.nih.nci.caarray.domain.project.Project;
import gov.nih.nci.caarray.domain.register.RegistrationRequest;
import gov.nih.nci.caarray.util.ConfigurationHelper;
import gov.nih.nci.caarray.util.EmailUtil;

import java.text.MessageFormat;
import java.util.Collections;

import javax.mail.MessagingException;

import org.apache.commons.configuration.DataConfiguration;
import org.apache.commons.lang.StringUtils;

/**
 * @author John Hedden (Amentra, Inc.)
 *
 */
public final class EmailHelper {

    private EmailHelper() {
        // nothing here.
    }

    /**
     * @param registrationRequest request
     * @throws MessagingException on other error
     */
    public static void registerEmail(RegistrationRequest registrationRequest) throws MessagingException {
        DataConfiguration config = ConfigurationHelper.getConfiguration();

        if (!config.getBoolean(ConfigParamEnum.SEND_CONFIRM_EMAIL.name())) {
            return;
        }

        String subject = config.getString(ConfigParamEnum.CONFIRM_EMAIL_SUBJECT.name());
        String from = config.getString(ConfigParamEnum.EMAIL_FROM.name());
        String mailBodyPattern = config.getString(ConfigParamEnum.CONFIRM_EMAIL_CONTENT.name());
        String mailBody = MessageFormat.format(mailBodyPattern, registrationRequest.getId());

        EmailUtil.sendMail(Collections.singletonList(registrationRequest.getEmail()), from, subject, mailBody);
    }

    /**
     * @param registrationRequest request
     * @throws MessagingException on error
     */
    public static void registerEmailAdmin(RegistrationRequest registrationRequest) throws MessagingException {
        DataConfiguration config = ConfigurationHelper.getConfiguration();
        if (!config.getBoolean(ConfigParamEnum.SEND_ADMIN_EMAIL.name())) {
            return;
        }

        String subject = config.getString(ConfigParamEnum.REG_EMAIL_SUBJECT.name());
        String from = registrationRequest.getEmail();
        String admin = config.getString(ConfigParamEnum.REG_EMAIL_TO.name());

        String mailBody = "Registration Request:\n"
            + "First Name: " + registrationRequest.getFirstName() + "\n"
            + "Middle Initial: " + registrationRequest.getMiddleInitial() + "\n"
            + "Last Name: " + registrationRequest.getLastName() + "\n"
            + "Email: " + registrationRequest.getEmail() + "\n"
            + "Phone: " + registrationRequest.getPhone() + "\n"
            + "Fax: " + registrationRequest.getFax() + "\n"
            + "Organization: " + registrationRequest.getOrganization() + "\n"
            + "Address1: " + registrationRequest.getAddress1() + "\n"
            + "Address2: " + registrationRequest.getAddress2() + "\n"
            +  "City: " + registrationRequest.getCity() + "\n"
            + "State: " + registrationRequest.getState() + "\n"
            + "Province: " + registrationRequest.getProvince() + "\n"
            + "Country: " + registrationRequest.getCountry().getPrintableName() + "\n"
            + "Zip: " + registrationRequest.getZip() + "\n"
            + "Role: " + registrationRequest.getRole();

        EmailUtil.sendMail(Collections.singletonList(admin), from, subject, mailBody);
    }

    /**
     * @param project the newly created project
     * @param projectLink the link to view the details of the project
     * @throws MessagingException on other error
     */
    public static void sendSubmitExperimentEmail(Project project, String projectLink) throws MessagingException {
        DataConfiguration config = ConfigurationHelper.getConfiguration();

        String subject = config.getString(ConfigParamEnum.SUBMIT_EXPERIMENT_EMAIL_SUBJECT.name());
        String from = config.getString(ConfigParamEnum.EMAIL_FROM.name());
        String plainMailBodyPattern = config.getString(ConfigParamEnum.SUBMIT_EXPERIMENT_EMAIL_PLAIN_CONTENT.name());
        String htmlMailBodyPattern = config.getString(ConfigParamEnum.SUBMIT_EXPERIMENT_EMAIL_HTML_CONTENT.name());

        Person pi = (Person) project.getExperiment().getPrimaryInvestigator().getContact();
        String plainMailBody =
                MessageFormat.format(plainMailBodyPattern, pi.getName(), project.getExperiment().getTitle(),
                        projectLink);
        String htmlMailBody =
                MessageFormat.format(htmlMailBodyPattern, pi.getName(), project.getExperiment().getTitle(),
                        projectLink);

        if (!StringUtils.isEmpty(pi.getEmail())) {
            EmailUtil.sendMultipartMail(Collections.singletonList(pi.getEmail()), from, subject, htmlMailBody,
                    plainMailBody);
        }
    }
}
