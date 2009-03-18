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
import gov.nih.nci.caarray.domain.MultiPartBlob;
import gov.nih.nci.caarray.domain.project.Project;
import gov.nih.nci.caarray.security.Protectable;
import gov.nih.nci.caarray.security.ProtectableDescendent;
import gov.nih.nci.caarray.util.HibernateUtil;
import gov.nih.nci.caarray.validation.FileValidationResult;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Collections;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.builder.CompareToBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.hibernate.annotations.ForeignKey;

/**
 */
@Entity
@Table(name = "caarrayfile")
public class CaArrayFile extends AbstractCaArrayEntity implements Comparable<CaArrayFile>, ProtectableDescendent {
    private static final long serialVersionUID = 1234567890L;

    private String name;
    private String type;
    private String status = FileStatus.UPLOADED.name();
    private Project project;
    private FileValidationResult validationResult;
    private int uncompressedSize;
    private int compressedSize;

    // transient properties
    private transient MultiPartBlob multiPartBlob;
    private transient InputStream inputStreamToClose;
    private transient File fileToDelete;

    /**
     * Gets the name.
     *
     * @return the name
     */
    @Column(length = DEFAULT_STRING_COLUMN_SIZE)
    public String getName() {
        return this.name;
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
     * @return the fileType
     */
    @Transient
    public FileType getFileType() {
        return getType() != null ? FileType.valueOf(getType()) : null;
    }

    /**
     * @param fileType the fileType to set
     */
    public void setFileType(FileType fileType) {
        if (fileType != null) {
            setType(fileType.name());
        } else {
            setType(null);
        }
    }

    /**
     * @return the project
     */
    @ManyToOne
    @JoinColumn(updatable = false)
    @ForeignKey(name = "caarrayfile_project_fk")
    public Project getProject() {
        return this.project;
    }

    /**
     * @param project the project to set
     */
    public void setProject(Project project) {
        this.project = project;
    }

    /**
     * @return the uncompressed size, in bytes
     */
    public int getUncompressedSize() {
        return this.uncompressedSize;
    }

    @SuppressWarnings({"unused", "PMD.UnusedPrivateMethod" })
    private void setUncompressedSize(int uncompressedSize) {
        this.uncompressedSize = uncompressedSize;
    }

    /**
     * @return the compressed size, in bytes
     */
    public int getCompressedSize() {
        return this.compressedSize;
    }

    @SuppressWarnings({"unused", "PMD.UnusedPrivateMethod" })
    private void setCompressedSize(int compressedSize) {
        this.compressedSize = compressedSize;
    }

    /**
     * {@inheritDoc}
     */
    public int compareTo(CaArrayFile o) {
        return new CompareToBuilder()
            .append(getProject(), o.getProject())
            .append(getFileStatus(), o.getFileStatus())
            .append(getFileType(), o.getFileType())
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
            .append("name", this.name)
            .append("fileStatus", this.status)
            .append("type", this.type)
            .toString();
    }

    /**
     * @return the status
     */
    @Column(length = DEFAULT_STRING_COLUMN_SIZE)
    public String getStatus() {
        return this.status;
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
     * @return the type
     */
    @Column(length = DEFAULT_STRING_COLUMN_SIZE)
    public String getType() {
        return this.type;
    }

    /**
     * @param type the type to set
     */
    public void setType(String type) {
        checkForLegalTypeValue(type);
        this.type = type;
    }

    private void checkForLegalTypeValue(String checkType) {
        if (checkType != null) {
            FileType.valueOf(checkType);
        }
    }

    /**
     * @return the validationResult
     */
    @OneToOne(cascade = CascadeType.ALL)
    @ForeignKey(name = "caarrayfile_validation_result_fk")
    public FileValidationResult getValidationResult() {
        return this.validationResult;
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
     * Writes the contents of a stream to the storage for this file's contents. Note that the
     * contents may only be set once (i.e. no overwrites are allowed).
     *
     * @param inputStream read the file contents from this input stream.
     * @throws IOException if there is a problem writing the contents.
     */
    public void writeContents(InputStream inputStream) throws IOException {
        if (this.multiPartBlob == null) {
            File tempFile = File.createTempFile("compressed", "tmp");
            FileOutputStream fileOutputStream = new FileOutputStream(tempFile);
            GZIPOutputStream gzipOutputStream = new GZIPOutputStream(fileOutputStream);
            int uncompressedDataSize = IOUtils.copy(inputStream, gzipOutputStream);
            IOUtils.closeQuietly(gzipOutputStream);
            IOUtils.closeQuietly(fileOutputStream);
            FileInputStream compressedFile = new FileInputStream(tempFile);
            setMultiPartBlob(new MultiPartBlob());
            getMultiPartBlob().writeData(compressedFile);
            setUncompressedSize(uncompressedDataSize);
            setCompressedSize((int) tempFile.length());
            this.inputStreamToClose = compressedFile;
            this.fileToDelete = tempFile;
        } else {
            throw new IllegalStateException("Can't reset the contents of an existing CaArrayFile");
        }
    }

    /**
     * Returns an input stream to access the contents of the file.
     *
     * @return the input stream to read.
     * @throws IOException if the contents couldn't be accessed.
     */
    public InputStream readContents() throws IOException {
        try {
            return new GZIPInputStream(getMultiPartBlob().readData());
        } catch (SQLException e) {
            throw new IllegalStateException("Couldn't access file conteents", e);
        }
    }

    /**
     * @return the multiPartBlob
     */
    @Embedded
    private MultiPartBlob getMultiPartBlob() {
        return this.multiPartBlob;
    }

    /**
     * @param multiPartBlob the multiPartBlob to set
     */
    private void setMultiPartBlob(MultiPartBlob multiPartBlob) {
        this.multiPartBlob = multiPartBlob;
    }

    /**
     * {@inheritDoc}
     */
    public Collection<? extends Protectable> relatedProtectables() {
        // we cheat a little bit here - we know the only modification operations allowed on files
        // are deleting and changing file type, and those are only allowed in the unimported status
        // therefore it is sufficient to only rely on the Project to which it belongs as the related
        // Protectable. If it does not belong to a project, then protections do not apply to it at all
        // (ie it is an array design file or some such)
        return getProject() != null ? Collections.singleton(getProject()) : null;
    }

    /**
     * Clear the file contents from memory.
     */
    public void clearAndEvictContents() {
        HibernateUtil.getCurrentSession().evict(this);
        if (getMultiPartBlob() != null) {
            getMultiPartBlob().clearAndEvictData();
        }
    }

    /**
     * Get the input stream that needs to be closed in postflush.
     * @return the input stream
     */
    @Transient
    public InputStream getInputStreamToClose() {
        return this.inputStreamToClose;
    }

    /**
     * Get the file the needs to be deleted in post flush.
     * @return the file
     */
    @Transient
    public File getFileToDelete() {
        return this.fileToDelete;
    }
}