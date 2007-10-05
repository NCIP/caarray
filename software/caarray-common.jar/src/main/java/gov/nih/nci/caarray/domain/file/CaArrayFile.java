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

package gov.nih.nci.caarray.domain.file;

import gov.nih.nci.caarray.domain.AbstractCaArrayEntity;
import gov.nih.nci.caarray.domain.project.Project;
import gov.nih.nci.caarray.validation.FileValidationResult;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.builder.CompareToBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Type;

/**
 */
@Entity
@Table(name = "CAARRAYFILE")
public class CaArrayFile extends AbstractCaArrayEntity implements Comparable<CaArrayFile> {

    private static final long serialVersionUID = 1234567890L;

    private String name;
    private FileType type;
    private String status = FileStatus.UPLOADED.name();
    private Project project;
    private FileValidationResult validationResult;
    private byte[] contents;

    /**
     * Gets the name.
     *
     * @return the name
     */
    @Column(length = DEFAULT_STRING_COLUMN_SIZE)
    public String getName() {
        return name;
    }

    /**
     * Sets the name.
     *
     * @param name the name
     */
    public void setName(final String name) {
        this.name = name;
    }

    /**
     * Gets the type.
     *
     * @return the type
     */
    @Type(type = "gov.nih.nci.caarray.domain.file.FileTypeUserType")
    public FileType getType() {
        return type;
    }

    /**
     * Sets the type.
     *
     * @param typeVal the type
     */
    public void setType(final FileType typeVal) {
        this.type = typeVal;
    }

    /**
     * @return the fileStatus
     */
    @Transient
    public FileStatus getFileStatus() {
        return getStatus() != null ? FileStatus.valueOf(getStatus()) : null;
    }

    /**
     * @param fileStatus the fileStatus to set
     */
    public void setFileStatus(FileStatus fileStatus) {
        if (fileStatus != null) {
            setStatus(fileStatus.name());
        } else {
            setStatus(null);
        }
    }

    /**
     * @return the project
     */
    @ManyToOne
    @JoinColumn(updatable = false)
    @ForeignKey(name = "CAARRAYFILE_PROJECT_FK")
    public Project getProject() {
        return project;
    }

    /**
     * @param project the project to set
     */
    public void setProject(Project project) {
        this.project = project;
    }

    /**
     * {@inheritDoc}
     */
    public int compareTo(CaArrayFile o) {
        return new CompareToBuilder()
            .append(getProject(), o.getProject())
            .append(getFileStatus(), o.getFileStatus())
            .append(getType(), o.getType())
            .append(getName(), o.getName())
            .append(getId(), o.getId())
            .toComparison();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return new ToStringBuilder(this)
            .appendSuper(super.toString())
            .append("name", name)
            .append("fileStatus", status)
            .append("type", type)
            .toString();
    }

    /**
     * @return the status
     */
    @Column(length = DEFAULT_STRING_COLUMN_SIZE)
    public String getStatus() {
        return status;
    }

    /**
     * @param status the status to set
     */
    public void setStatus(String status) {
        checkForLegalStatusValue(status);
        this.status = status;
    }

    private void checkForLegalStatusValue(String checkStatus) {
        if (checkStatus != null) {
            FileStatus.valueOf(checkStatus);
        }
    }

    /**
     * @return the validationResult
     */
    @OneToOne(cascade = CascadeType.ALL)
    @ForeignKey(name = "CAARRAYFILE_VALIDATION_RESULT_FK")
    public FileValidationResult getValidationResult() {
        return validationResult;
    }

    /**
     * @param validationResult the validationResult to set
     */
    public void setValidationResult(FileValidationResult validationResult) {
        this.validationResult = validationResult;
    }

    /**
     * Returns true if this <code>CaArrayFile</code> wraps access to the given physical
     * file.
     *
     * @param file check if this is the wrapped file
     * @return true if this is the wrapped file.
     */
    boolean isMatch(File file) {
        return file != null && getName() != null && getName().equals(file.getName());
    }

    /**
     * Writes the contents of a stream to the storage for this file's contents.
     *
     * @param inputStream read the file contents from this input stream.
     * @throws IOException if there is a problem writing the contents.
     */
    public void writeContents(InputStream inputStream) throws IOException {
        setContents(IOUtils.toByteArray(inputStream));
    }

    /**
     * Returns an input stream to access the contents of the file.
     *
     * @return the input stream to read.
     * @throws IOException if the contents couldn't be accessed.
     */
    public InputStream readContents() throws IOException {
        return new ByteArrayInputStream(getContents());
    }

    /**
     * @return the contents
     */
    @Basic(fetch = FetchType.LAZY)
    @Column(columnDefinition = "LONGBLOB")
    @SuppressWarnings({ "unused", "PMD.UnusedPrivateMethod" })
    public byte[] getContents() {
        return contents;
    }

    /**
     * @param contents the contents to set
     */
    public void setContents(byte[] contents) {
        this.contents = contents;
    }

}
