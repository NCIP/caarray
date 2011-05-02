/**
 * The software subject to this notice and license includes both human readable
 * source code form and machine readable, binary, object code form. The caArray
 * Software was developed in conjunction with the National Cancer Institute
 * (NCI) by NCI employees and 5AM Solutions, Inc. (5AM). To the extent
 * government employees are authors, any rights in such works shall be subject
 * to Title 17 of the United States Code, section 105.
 *
 * This caArray Software License (the License) is between NCI and You. You (or
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
 * its rights in the caArray Software to (i) use, install, access, operate,
 * execute, copy, modify, translate, market, publicly display, publicly perform,
 * and prepare derivative works of the caArray Software; (ii) distribute and
 * have distributed to and by third parties the caArray Software and any
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
package gov.nih.nci.caarray.validation;

import gov.nih.nci.caarray.domain.AbstractCaArrayObject;
import gov.nih.nci.caarray.validation.ValidationMessage.Type;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Transient;

import org.hibernate.annotations.BatchSize;

/**
 * Contains all the validation messages for a single file.
 */
@Entity
@SuppressWarnings("PMD.AvoidDuplicateLiterals")
@BatchSize(size = AbstractCaArrayObject.DEFAULT_BATCH_SIZE)
public class FileValidationResult implements Serializable {
    /**
     * Key for validationProperties for array data file hybridization names.
     */
    public static final String HYB_NAME = "Hybridization(s)";

    private static final long serialVersionUID = -5402207496806890698L;
    private static final String UNUSED = "unused";

    private Long id;
    private Set<ValidationMessage> messageSet = new HashSet<ValidationMessage>();
    private final transient Map<String, Object> validationProperties = new HashMap<String, Object>();

    /**
     * Returns true if all the documents in the set were valid.
     * 
     * @return true if set was valid.
     */
    @Transient
    public boolean isValid() {
        for (final ValidationMessage message : this.messageSet) {
            if (ValidationMessage.Type.ERROR.equals(message.getType())) {
                return false;
            }
        }
        return true;
    }

    /**
     * Returns the messages ordered by type and location.
     * 
     * @return the messages.
     */
    @Transient
    public List<ValidationMessage> getMessages() {
        final List<ValidationMessage> messageList = new ArrayList<ValidationMessage>();
        messageList.addAll(getMessageSet());
        Collections.sort(messageList);
        return Collections.unmodifiableList(messageList);
    }

    /**
     * Returns the messages of given type, ordered by location.
     * 
     * @param type the type of messages to return
     * @return the messages.
     */
    @Transient
    public List<ValidationMessage> getMessages(ValidationMessage.Type type) {
        final List<ValidationMessage> messageList = new ArrayList<ValidationMessage>();
        for (final ValidationMessage message : this.messageSet) {
            if (message.getType() == type) {
                messageList.add(message);
            }
        }
        Collections.sort(messageList);
        return Collections.unmodifiableList(messageList);
    }

    /**
     * Adds a new validation message to the result.
     * 
     * @param type the type/level of the message
     * @param message the actual message content
     * @return the newly added message, if additional configuration of the message is required.
     */
    public ValidationMessage addMessage(Type type, String message) {
        final ValidationMessage validationMessage = new ValidationMessage(type, message);
        addMessage(validationMessage);
        return validationMessage;
    }

    /**
     * Adds a new validation message to the result including line and column information.
     * 
     * @param type the type/level of the message
     * @param message the actual message content
     * @param lineNumber the line number the error occurs on
     * @param columnNumber the column number the error occurs on
     * @return the newly added message, if additional configuration of the message is required.
     */
    public ValidationMessage addMessage(Type type, String message, int lineNumber, int columnNumber) {
        final ValidationMessage validationMessage = addMessage(type, message);
        validationMessage.setLine(lineNumber);
        validationMessage.setColumn(columnNumber);
        return validationMessage;
    }

    /**
     * Adds a new validation message to the result.
     * 
     * @param message the message to add
     */
    public void addMessage(ValidationMessage message) {
        this.messageSet.add(message);
    }

    /**
     * @return validation messages
     */
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @BatchSize(size = AbstractCaArrayObject.DEFAULT_BATCH_SIZE)
    public Set<ValidationMessage> getMessageSet() {
        return this.messageSet;
    }

    @SuppressWarnings({UNUSED, "PMD.UnusedPrivateMethod" })
    private void setMessageSet(Set<ValidationMessage> messageSet) {
        this.messageSet = messageSet;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @SuppressWarnings({UNUSED, "PMD.UnusedPrivateMethod" })
    private Long getId() {
        return this.id;
    }

    @SuppressWarnings({UNUSED, "PMD.UnusedPrivateMethod" })
    private void setId(Long id) {
        this.id = id;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        final StringBuffer stringBuffer = new StringBuffer();
        for (final ValidationMessage message : getMessages()) {
            stringBuffer.append(message.toString());
            stringBuffer.append('\n');
        }
        return stringBuffer.toString();
    }

    /**
     * The default comparison uses the id.
     * 
     * @param o other object
     * @return equal or not
     */
    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }
        if (!(o instanceof FileValidationResult)) {
            return false;
        }
        if (o == this) {
            return true;
        }
        if (this.id == null) {
            // by default, two transient instances cannot ever be equal
            return false;
        }

        final FileValidationResult e = (FileValidationResult) o;
        return this.id.equals(e.id);
    }

    /**
     * Default hashCode goes off of id.
     * 
     * @return hashCode
     */
    @Override
    public int hashCode() {
        if (this.id == null) {
            return System.identityHashCode(this);
        }
        return this.id.hashCode();
    }

    /**
     * Validation properties based on name.
     * 
     * @param name key for properties
     * @return validation properties
     */
    @Transient
    public Object getValidationProperties(String name) {
        return this.validationProperties.get(name) == null ? new ArrayList<String>() : this.validationProperties
                .get(name);
    }

    /**
     * Add validation properties to general purpose storage for use by specific validators.
     * 
     * @param name type of properties
     * @param props the properties
     */
    @Transient
    public void addValidationProperties(String name, Object props) {
        this.validationProperties.put(name, props);
    }
}
