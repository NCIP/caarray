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

import gov.nih.nci.caarray.validation.ValidationMessage.Type;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.builder.ToStringBuilder;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * The result of validating a set of files.
 */
public final class ValidationResult implements Serializable {

    private static final long serialVersionUID = -5781574225752015910L;

    private final Map<String, FileValidationResult> fileValidationResults = Maps.newTreeMap();

    /**
     * Instantiates a new, empty result.
     */
    public ValidationResult() {
        super();
    }

    /**
     * Returns true if all the documents in the set were valid.
     * 
     * @return true if set was valid.
     */
    public boolean isValid() {
        for (final FileValidationResult fileValidationResult : this.fileValidationResults.values()) {
            if (!fileValidationResult.isValid()) {
                return false;
            }
        }
        return true;
    }

    /**
     * Returns the messages ordered by file, type and location.
     * 
     * @return the messages.
     */
    public List<ValidationMessage> getMessages() {
        final List<ValidationMessage> messages = new ArrayList<ValidationMessage>();
        for (final FileValidationResult fileValidationResult : getFileValidationResults()) {
            messages.addAll(fileValidationResult.getMessages());
        }
        return Collections.unmodifiableList(messages);
    }

    /**
     * Returns the messages of given type, ordered by file and location.
     * 
     * @param type type of messages to return
     * @return the messages.
     */
    public List<ValidationMessage> getMessages(ValidationMessage.Type type) {
        final List<ValidationMessage> messages = new ArrayList<ValidationMessage>();
        for (final FileValidationResult fileValidationResult : getFileValidationResults()) {
            messages.addAll(fileValidationResult.getMessages(type));
        }
        return Collections.unmodifiableList(messages);
    }

    /**
     * Returns all the file validation results in order by file.
     * 
     * @return the file validation results.
     */
    public List<FileValidationResult> getFileValidationResults() {
        return Lists.newArrayList(this.fileValidationResults.values());
    }

    /**
     * Returns the names of all the files for which this has validation results.
     * 
     * @return set of names of the files for which this contains validation results
     */
    public Set<String> getFileNames() {
        return Collections.unmodifiableSet(this.fileValidationResults.keySet());
    }

    /**
     * Adds a new validation message to the result.
     * 
     * @param fileName validation message is associated with the file of this name
     * @param type the type/level of the message
     * @param message the actual message content
     * @return the newly added message, if additional configuration of the message is required.
     */
    public ValidationMessage addMessage(String fileName, Type type, String message) {
        return getOrCreateFileValidationResult(fileName).addMessage(type, message);
    }

    /**
     * Adds a new validation message to the result.
     * 
     * @param fileName validation message is associated with the file of this name
     * @param message the validation message
     */
    public void addMessage(String fileName, ValidationMessage message) {
        getOrCreateFileValidationResult(fileName).addMessage(message);
    }

    /**
     * Returns the <code>FileValidationResult</code> corresponding to the file with given name. If one does not exist
     * yet, creates a new one and adds it to this.
     * 
     * @param fileName get or create validation results for the file of this name
     * @return an existing or new validation result for the file
     */
    public FileValidationResult getOrCreateFileValidationResult(String fileName) {
        if (!this.fileValidationResults.containsKey(fileName)) {
            this.fileValidationResults.put(fileName, new FileValidationResult());
        }
        return getFileValidationResult(fileName);
    }

    /**
     * Add the results for a file, replacing any previous results for that file.
     * 
     * @param fileName name of file to add
     * @param fileResult results to add
     */
    public void addFile(String fileName, FileValidationResult fileResult) {
        this.fileValidationResults.put(fileName, fileResult);
    }

    /**
     * Returns the <code>FileValidationResult</code> corresponding to the given file, or null if non exists.
     * 
     * @param fileName get validation results for the file with this name
     * @return the validation result.
     */
    public FileValidationResult getFileValidationResult(String fileName) {
        return this.fileValidationResults.get(fileName);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

}
