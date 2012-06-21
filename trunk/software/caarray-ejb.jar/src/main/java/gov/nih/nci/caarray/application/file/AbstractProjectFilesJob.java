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
package gov.nih.nci.caarray.application.file;

import gov.nih.nci.caarray.application.fileaccess.FileAccessService;
import gov.nih.nci.caarray.dao.ProjectDao;
import gov.nih.nci.caarray.dao.SearchDao;
import gov.nih.nci.caarray.domain.file.CaArrayFile;
import gov.nih.nci.caarray.domain.file.CaArrayFileSet;
import gov.nih.nci.caarray.domain.file.FileStatus;
import gov.nih.nci.caarray.domain.project.ParentJob;
import gov.nih.nci.caarray.domain.project.Project;
import gov.nih.nci.caarray.magetab.MageTabDocumentSet;
import gov.nih.nci.security.authorization.domainobjects.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.google.inject.Inject;

/**
 * Encapsulates the data necessary for a project file management job.
 */
abstract class AbstractProjectFilesJob extends AbstractFileManagementJob {

    private static final long serialVersionUID = 1L;

    private long projectId;
    private Set<Long> fileIds;
    private ArrayDataImporter arrayDataImporter;
    private MageTabImporter mageTabImporter;
    private ProjectDao projectDao;
    private SearchDao searchDao;
    private String experimentName;

    @SuppressWarnings("PMD.ExcessiveParameterList")
    @Inject
    // CHECKSTYLE:OFF more than 7 parameters are okay for injected constructor
    AbstractProjectFilesJob(String username, Project targetProject,
            CaArrayFileSet fileSet, ArrayDataImporter arrayDataImporter, MageTabImporter mageTabImporter,
            FileAccessService fileAccessService, ProjectDao projectDao, SearchDao searchDao) {
        // CHECKSTYLE:ON
        this(username, targetProject, fileSet, arrayDataImporter, mageTabImporter,
                fileAccessService, projectDao, searchDao, null);
    }

    @SuppressWarnings("PMD.ExcessiveParameterList")
    @Inject
    // CHECKSTYLE:OFF more than 7 parameters are okay for injected constructor
    AbstractProjectFilesJob(String username, Project targetProject,
            CaArrayFileSet fileSet, ArrayDataImporter arrayDataImporter, MageTabImporter mageTabImporter,
            FileAccessService fileAccessService, ProjectDao projectDao, SearchDao searchDao, ParentJob parent) {
    // CHECKSTYLE:ON
        super(username, parent, fileAccessService);
        init(username, targetProject, fileSet, arrayDataImporter, mageTabImporter, projectDao, searchDao);
    }

    @SuppressWarnings("PMD.ExcessiveParameterList")
    // CHECKSTYLE:OFF more than 7 parameters are okay for injected constructor
    final void init(String username, Project targetProject, CaArrayFileSet fileSet, ArrayDataImporter arrayDataImptr,
            MageTabImporter mageTabImptr, ProjectDao pDao, SearchDao sDao) {
        // CHECKSTYLE:ON
        setOwnerName(username);
        this.projectId = targetProject.getId();
        this.experimentName = targetProject.getExperiment().getTitle();
        this.arrayDataImporter = arrayDataImptr;
        this.mageTabImporter = mageTabImptr;
        this.projectDao = pDao;
        this.searchDao = sDao;
        this.fileIds = new HashSet<Long>();
        for (final CaArrayFile file : fileSet.getFiles()) {
            this.fileIds.add(file.getId());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected final void doExecute() {
        executeProjectFilesJob();
        getFileSet().pullUpValidationMessages();
        for (CaArrayFile file : getFileSet().getFiles()) {
            if (file.getParent() != null) {
                getFileAccessService().remove(file);
            }
        }
    }

    /**
     * Primary execution method subclasses must implement.
     */
    protected abstract void executeProjectFilesJob();

    /**
     * {@inheritDoc}
     */
    public String getJobEntityName() {
        return experimentName;
    }

    /**
     * {@inheritDoc}
     */
    public long getJobEntityId() {
        return projectId;
    }

    @Override
    public CaArrayFileSet getFileSet() {
        CaArrayFileSet fileSet = new CaArrayFileSet(getProject());
        List<CaArrayFile> files = searchDao.retrieveByIds(CaArrayFile.class,
                new ArrayList<Long>(this.fileIds));
        fileSet.addAll(files);
        return fileSet;
    }

    Project getProject() {
        return searchDao.retrieve(Project.class, this.projectId);
    }

    void doValidate(CaArrayFileSet fileSet) {
        final MageTabDocumentSet mTabSet = validateAnnotation(fileSet);
        validateArrayData(fileSet, mTabSet);
    }

    private MageTabDocumentSet validateAnnotation(CaArrayFileSet fileSet) {
        return getMageTabImporter().validateFiles(getProject(), fileSet);
    }

    private void validateArrayData(CaArrayFileSet fileSet, MageTabDocumentSet mTabSet) {
        getArrayDataImporter().validateFiles(fileSet, mTabSet, false);
    }

    MageTabImporter getMageTabImporter() {
        return this.mageTabImporter;
    }

    /**
     * @param mageTabImporter the mageTabImporter to set
     */
    @Inject
    public void setMageTabImporter(MageTabImporter mageTabImporter) {
        this.mageTabImporter = mageTabImporter;
    }

    protected ArrayDataImporter getArrayDataImporter() {
        return arrayDataImporter;
    }

    protected ProjectDao getProjectDao() {
        return projectDao;
    }

    /**
     * @return the searchDao
     */
    protected SearchDao getSearchDao() {
        return searchDao;
    }

    /**
     * {@inheritDoc}
     */
    public boolean userHasReadAccess(User user) {
       return userCanAccessProject(user, false);
    }

    /**
     * {@inheritDoc}
     */
    public boolean userHasWriteAccess(User user) {
        return userCanAccessProject(user, true);
    }

    private boolean userCanAccessProject(User user, boolean checkForWriteAccess) {
        boolean hasAccess = false;
        Project p = getProject();
        if (p != null) {
            hasAccess = checkForWriteAccess ? p.hasWritePermission(user) : p.hasReadPermission(user);
        }
        return hasAccess;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PreparedStatement getUnexpectedErrorPreparedStatement(Connection con) throws SQLException {
        final PreparedStatement s = con
                .prepareStatement("update caarrayfile set status = ? where project = ? and status = ?");
        FileStatus newStatus;
        switch (getInProgressStatus()) {
        case IMPORTING:
            newStatus = FileStatus.IMPORT_FAILED;
            break;
        case VALIDATING:
            newStatus = FileStatus.VALIDATION_ERRORS;
            break;
        default:
            newStatus = FileStatus.IMPORT_FAILED;
        }
        int i = 1;
        s.setString(i++, newStatus.toString());
        s.setLong(i++, this.projectId);
        s.setString(i++, getInProgressStatus().toString());
        return s;
    }
}
