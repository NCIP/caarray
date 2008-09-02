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

import gov.nih.nci.caarray.domain.project.Project;

import java.io.File;
import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * Provides functionality to handle multiple <code>CaArrayFiles</code> as a single set.
 */
@SuppressWarnings("PMD.CyclomaticComplexity") // Complex rules for checking aggregate file status
public final class CaArrayFileSet implements Serializable {

    private static final long serialVersionUID = -831461553674445009L;
    private final Set<CaArrayFile> files = new HashSet<CaArrayFile>();
    private final Long projectId;

    /**
     * @param p project this file set is associated with
     */
    public CaArrayFileSet(Project p) {
        if (p == null) {
            throw new IllegalArgumentException("Project must be non-null");
        }
        projectId = p.getId();
    }

    /**
     * Construct a new file set, with the same files and associated project.
     * @param baseFileSet file set on which to base the new file set
     */
    public CaArrayFileSet(CaArrayFileSet baseFileSet) {
        projectId = baseFileSet.projectId;
        files.addAll(baseFileSet.getFiles());
    }

    /**
     * @return related project system identifier
     */
    public Long getProjectId() {
        return projectId;
    }

    /**
     * File set.  Only files <em>not</em> associated with a project can be added.
     */
    public CaArrayFileSet() {
        projectId = null;
    }

    /**
     * Adds a file to the set.
     *
     * @param file the file to add
     */
    public void add(CaArrayFile file) {
        Long fileProjectId = file.getProject() == null ? null : file.getProject().getId();
        if (!ObjectUtils.equals(fileProjectId, projectId)) {
            throw new IllegalArgumentException("file's project and fileset project not the same");
        }
        files.add(file);
    }

    /**
     * Returns the contained files.
     *
     * @return the files.
     */
    public Set<CaArrayFile> getFiles() {
        return files;
    }

    /**
     * Returns the aggregate status of the file set.
     *
     * @return status of the set.
     */
    public FileStatus getStatus() {
        if (!files.isEmpty() && allStatusesEqual()) {
            return files.iterator().next().getFileStatus();
        } else if (allStatusesEqual(FileStatus.IMPORTED, FileStatus.IMPORTED_NOT_PARSED)) {
            return FileStatus.IMPORTED;
        } else if (allStatusesEqual(FileStatus.VALIDATED, FileStatus.VALIDATED_NOT_PARSED)) {
            return FileStatus.VALIDATED;
        } else if (statusesContains(FileStatus.IMPORTING)) {
            return FileStatus.IMPORTING;
        } else if (statusesContains(FileStatus.VALIDATING)) {
            return FileStatus.VALIDATING;
        } else if (statusesContains(FileStatus.VALIDATION_ERRORS)) {
            return FileStatus.VALIDATION_ERRORS;
        } else if (statusesContains(FileStatus.IMPORT_FAILED)) {
            return FileStatus.IMPORT_FAILED;
        } else {
            return FileStatus.UPLOADED;
        }
    }

    /**
     * @return true if the status of all the files is one of the given statuses
     */
    @SuppressWarnings("PMD.UnusedPrivateMethod")
    private boolean allStatusesEqual(FileStatus... statuses) {
        Set<FileStatus> fileSetStatuses = new HashSet<FileStatus>();
        for (CaArrayFile file : files) {
            fileSetStatuses.add(file.getFileStatus());
        }
        for (FileStatus status : statuses) {
            fileSetStatuses.remove(status);
        }
        return fileSetStatuses.isEmpty();
    }

    /**
     * @return true if the status of all the files is the same
     */
    private boolean allStatusesEqual() {
        Set<FileStatus> fileSetStatuses = new HashSet<FileStatus>();
        for (CaArrayFile file : files) {
            fileSetStatuses.add(file.getFileStatus());
        }
        return fileSetStatuses.size() == 1;
    }

    /**
     * tells if the status is in the set.
     * @param status the status.
     * @return true if it is in the set.
     */
    public boolean statusesContains(FileStatus status) {
        for (CaArrayFile file : files) {
            if (status.equals(file.getFileStatus())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Adds a collection of files to this set.
     *
     * @param fileCollection files to add.
     */
    public void addAll(Collection<CaArrayFile> fileCollection) {
        for (CaArrayFile file : fileCollection) {
            files.add(file);
        }
    }

    /**
     * Update the status of each file in this file set to the given status.
     * @param status the new status which each file in this set should have.
     */
    public void updateStatus(FileStatus status) {
        for (CaArrayFile file : files) {
            file.setFileStatus(status);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    /**
     * Returns the <code>CaArrayFile</code> in the set corresponding to the
     * given file object or null if no match.
     *
     * @param file get <code>CaArrayFile</code> for this file.
     * @return the matching <code>CaArrayFile</code>.
     */
    public CaArrayFile getFile(File file) {
        for (CaArrayFile caArrayFile : files) {
            if (caArrayFile.isMatch(file)) {
                return caArrayFile;
            }
        }
        return null;
    }
}
