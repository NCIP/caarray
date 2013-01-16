//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.util;

import java.util.ArrayList;
import java.util.List;

import javax.mail.Address;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.naming.InitialContext;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.apache.log4j.Logger;

/**
 * Utility class that sends emails.
 *
 * Email configuration is retrieved from the application server settings
 * using a JNDI lookup.
 *
 * @author John Hedden (Amentra, Inc.)
 * @author Akhil Bhaskar (Amentra, Inc.)
 */
public final class EmailUtil {

    private static final Logger LOG = Logger.getLogger(EmailUtil.class);
    private static final Session MAILSESSION;
    private static final String JNDI_NAME = "java:/Mail";

    static {
        try {
            InitialContext ctx = new InitialContext();
            MAILSESSION = (Session) ctx.lookup(JNDI_NAME);
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            throw new ExceptionInInitializerError(e);
        }
    }

    /**
     * No instantiation is allowed, hence the private constructor.
     */
    private EmailUtil() {
        // nothing here
    }

    /**
     * Returns a handle on the populated mail session object.
     *
     * @return the populate mail session object.
     */
    public Session getMailSession() {
        return MAILSESSION;
    }

    /**
     * Sends mail based upon input parameters.
     *
     * @param mailRecipients List of strings that are the recipient email addresses
     * @param from the from of the email
     * @param mailSubject the subject of the email
     * @param mailBody the body of the email
     * @throws MessagingException thrown if there is a problem sending the message
     */
    public static void sendMail(List<String> mailRecipients, String from, String mailSubject, String mailBody)
    throws MessagingException {
        MimeMessage message = constructMessage(mailRecipients, from, mailSubject);

        if (StringUtils.isEmpty(mailBody)) {
            LOG.info("No email body specified");
        }
        message.setText(mailBody);

        LOG.debug("sending email");
        Transport.send(message);
        LOG.debug("email successfully sent");
    }

    /**
     * Sends a multipart email with both HTML and plain-text bodies based upon input parameters.
     *
     * @param mailRecipients List of strings that are the recipient email addresses
     * @param from the from of the email
     * @param mailSubject the subject of the email
     * @param htmlMailBody the HTML version of the body of the email
     * @param plainMailBody the plain-text version of the body of the email
     * @throws MessagingException thrown if there is a problem sending the message
     */
    public static void sendMultipartMail(List<String> mailRecipients, String from, String mailSubject,
            String htmlMailBody, String plainMailBody) throws MessagingException {
        MimeMessage message = constructMessage(mailRecipients, from, mailSubject);

        Multipart mp = new MimeMultipart("alternative");
        addBodyPart(mp, htmlMailBody, "text/html");
        addBodyPart(mp, plainMailBody, "text/plain");
        message.setContent(mp);

        LOG.debug("sending email");
        Transport.send(message);
        LOG.debug("email successfully sent");
    }

    private static MimeMessage constructMessage(List<String> mailRecipients, String from, String mailSubject)
            throws MessagingException {
        Validate.notEmpty(mailRecipients, "No email recipients are specified");
        if (StringUtils.isEmpty(mailSubject)) {
            LOG.info("No email subject specified");
        }

        List<Address> addresses = new ArrayList<Address>();
        for (String recipient : mailRecipients) {
            addresses.add(new InternetAddress(recipient));
        }

        MimeMessage message = new MimeMessage(MAILSESSION);
        message.setRecipients(Message.RecipientType.TO, addresses.toArray(new Address[addresses.size()]));
        message.setFrom(new InternetAddress(from));
        message.setSubject(mailSubject);

        return message;
    }

    private static void addBodyPart(Multipart mp, String content, String contentType) throws MessagingException {
        BodyPart bp = new MimeBodyPart();
        bp.setContent(content, contentType);
        mp.addBodyPart(bp);
    }
}
