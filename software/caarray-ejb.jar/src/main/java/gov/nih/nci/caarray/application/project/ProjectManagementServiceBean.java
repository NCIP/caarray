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
package gov.nih.nci.caarray.application.project;

import gov.nih.nci.caarray.application.fileaccess.FileAccessService;
import gov.nih.nci.caarray.dao.CaArrayDaoFactory;
import gov.nih.nci.caarray.dao.ContactDao;
import gov.nih.nci.caarray.dao.ProjectDao;
import gov.nih.nci.caarray.domain.contact.Organization;
import gov.nih.nci.caarray.domain.file.CaArrayFile;
import gov.nih.nci.caarray.domain.project.Project;
import gov.nih.nci.caarray.domain.project.ProposalStatus;
import gov.nih.nci.caarray.util.io.logging.LogUtil;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.Local;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Implementation entry point for the ProjectManagement subsystem.
 */
@Local
@Stateless
public class ProjectManagementServiceBean implements ProjectManagementService {

    private static final Log LOG = LogFactory.getLog(ProjectManagementServiceBean.class);
    private CaArrayDaoFactory daoFactory = CaArrayDaoFactory.INSTANCE;

    @Resource private SessionContext sessionContext;
    @EJB private FileAccessService fileAccessService;

    private ProjectDao getProjectDao() {
        return this.daoFactory.getProjectDao();
    }

    private ContactDao getContactDao() {
        return this.daoFactory.getContactDao();
    }

    /**
     * {@inheritDoc}
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public Project getProject(long id) {
        LogUtil.logSubsystemEntry(LOG, id);
        Project project = getProjectDao().getProject(id);
        LogUtil.logSubsystemExit(LOG);
        return project;
    }

    /**
     * {@inheritDoc}
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public Organization getOrganization(long id) {
        LogUtil.logSubsystemEntry(LOG, id);
        Organization organization = (Organization) getContactDao().getContact(id);
        LogUtil.logSubsystemExit(LOG);
        return organization;
    }

    /**
     * {@inheritDoc}
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public CaArrayFile addFile(Project project, File file) {
        LogUtil.logSubsystemEntry(LOG, project, file);
        CaArrayFile caArrayFile = doAddFile(project, file, file.getName());
        this.fileAccessService.closeFiles();
        LogUtil.logSubsystemExit(LOG);
        return caArrayFile;
    }

    /**
     * {@inheritDoc}
     */
    public CaArrayFile addFile(Project project, File file, String filename) {
        LogUtil.logSubsystemEntry(LOG, project, file);
        CaArrayFile caArrayFile = doAddFile(project, file, filename);
        this.fileAccessService.closeFiles();
        LogUtil.logSubsystemExit(LOG);
        return caArrayFile;
    }

    private CaArrayFile doAddFile(Project project, File file, String filename) {
        CaArrayFile caArrayFile = null;
        caArrayFile = this.fileAccessService.add(file, filename);
        project.getFiles().add(caArrayFile);
        caArrayFile.setProject(project);
        getProjectDao().save(caArrayFile);
        getProjectDao().save(project);
        return caArrayFile;
    }

    /**
     * {@inheritDoc}
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void submitProject(Project project) throws ProposalWorkflowException {
        LogUtil.logSubsystemEntry(LOG, project);
        if (!project.isSubmissionAllowed()) {
            LogUtil.logSubsystemExit(LOG);
            throw new ProposalWorkflowException("Cannot submit project in current state");
        }
        project.setStatus(ProposalStatus.SUBMITTED_FOR_REVIEW);
        getProjectDao().save(project);
        LogUtil.logSubsystemExit(LOG);
    }

    /**
     * {@inheritDoc}
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void saveDraftProject(Project project) throws ProposalWorkflowException {
        LogUtil.logSubsystemEntry(LOG, project);
        if (!project.isSaveDraftAllowed()) {
            LogUtil.logSubsystemExit(LOG);
            throw new ProposalWorkflowException("Cannot save project draft in current state");
        }
        project.setStatus(ProposalStatus.DRAFT);
        getProjectDao().save(project);
        LogUtil.logSubsystemExit(LOG);
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public List<Project> getWorkspaceProjects() {
        String username = getSessionContext().getCallerPrincipal().getName();
        return getProjectDao().getProjectsForUser(username);
    }

    /**
     * {@inheritDoc}
     */
    public Project toggleBrowsableStatus(long projectId) {
        LogUtil.logSubsystemEntry(LOG, projectId);
        Project p = getProject(projectId);
        p.setBrowsable(!p.isBrowsable());
        getProjectDao().save(p);
        LogUtil.logSubsystemExit(LOG);
        return p;
    }

    /**
     * {@inheritDoc}
     */
    public File prepareForDownload(Collection<Long> ids) throws IOException {
        LogUtil.logSubsystemEntry(LOG, ids);

        if (ids == null || ids.isEmpty()) {
            throw new IllegalArgumentException("Ids cannot be null or empty!");
        }
        File result = File.createTempFile("data", ".zip");
        result.deleteOnExit();

        ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(result));
        FileAccessService svc = getFileAccessService();
        for (Long l : ids) {
            CaArrayFile caf = svc.getCaArrayFile(l);
            File f = svc.getFile(caf);
            InputStream is = new BufferedInputStream(new FileInputStream(f));

            ZipEntry ze = new ZipEntry(f.getName());
            zos.putNextEntry(ze);
            IOUtils.copy(is, zos);
            zos.closeEntry();
            is.close();
        }
        zos.close();

        svc.closeFiles();
        LogUtil.logSubsystemExit(LOG);
        return result;
    }

    FileAccessService getFileAccessService() {
        return this.fileAccessService;
    }

    void setFileAccessService(FileAccessService fileAccessService) {
        this.fileAccessService = fileAccessService;
    }

    CaArrayDaoFactory getDaoFactory() {
        return this.daoFactory;
    }

    void setDaoFactory(CaArrayDaoFactory daoFactory) {
        this.daoFactory = daoFactory;
    }
    SessionContext getSessionContext() {
        return this.sessionContext;
    }

    void setSessionContext(SessionContext sessionContext) {
        this.sessionContext = sessionContext;
    }
}
