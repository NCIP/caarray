/**
 * The software subject to this notice and license includes both human readable
 * source code form and machine readable, binary, object code form. The caarray-app
 * Software was developed in conjunction with the National Cancer Institute
 * (NCI) by NCI employees and 5AM Solutions, Inc. (5AM). To the extent
 * government employees are authors, any rights in such works shall be subject
 * to Title 17 of the United States Code, section 105.
 *
 * This caarray-app Software License (the License) is between NCI and You. You (or
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
 * its rights in the caarray-app Software to (i) use, install, access, operate,
 * execute, copy, modify, translate, market, publicly display, publicly perform,
 * and prepare derivative works of the caarray-app Software; (ii) distribute and
 * have distributed to and by third parties the caarray-app Software and any
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
