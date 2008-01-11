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

import gov.nih.nci.caarray.application.ExceptionLoggingInterceptor;
import gov.nih.nci.caarray.application.GenericDataService;
import gov.nih.nci.caarray.application.fileaccess.FileAccessService;
import gov.nih.nci.caarray.application.fileaccess.TemporaryFileCacheLocator;
import gov.nih.nci.caarray.dao.CaArrayDaoFactory;
import gov.nih.nci.caarray.dao.ProjectDao;
import gov.nih.nci.caarray.domain.PersistentObject;
import gov.nih.nci.caarray.domain.file.CaArrayFile;
import gov.nih.nci.caarray.domain.hybridization.Hybridization;
import gov.nih.nci.caarray.domain.permissions.AccessProfile;
import gov.nih.nci.caarray.domain.permissions.CollaboratorGroup;
import gov.nih.nci.caarray.domain.project.Experiment;
import gov.nih.nci.caarray.domain.project.Factor;
import gov.nih.nci.caarray.domain.project.Project;
import gov.nih.nci.caarray.domain.project.ProposalStatus;
import gov.nih.nci.caarray.domain.protocol.ProtocolApplication;
import gov.nih.nci.caarray.domain.sample.AbstractBioMaterial;
import gov.nih.nci.caarray.domain.sample.Extract;
import gov.nih.nci.caarray.domain.sample.LabeledExtract;
import gov.nih.nci.caarray.domain.sample.Sample;
import gov.nih.nci.caarray.domain.sample.Source;
import gov.nih.nci.caarray.domain.search.PageSortParams;
import gov.nih.nci.caarray.domain.search.SearchCategory;
import gov.nih.nci.caarray.domain.vocabulary.Term;
import gov.nih.nci.caarray.security.PermissionDeniedException;
import gov.nih.nci.caarray.security.SecurityUtils;
import gov.nih.nci.caarray.util.HibernateUtil;
import gov.nih.nci.caarray.util.UsernameHolder;
import gov.nih.nci.caarray.util.io.logging.LogUtil;
import gov.nih.nci.caarray.util.j2ee.ServiceLocatorFactory;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.annotation.Resource;
import javax.ejb.Local;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.interceptor.Interceptors;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

/**
 * Implementation entry point for the ProjectManagement subsystem.
 */
@Local
@Stateless
@Interceptors(ExceptionLoggingInterceptor.class)
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class ProjectManagementServiceBean implements ProjectManagementService {
    private static final Logger LOG = Logger.getLogger(ProjectManagementServiceBean.class);
    private CaArrayDaoFactory daoFactory = CaArrayDaoFactory.INSTANCE;

    @Resource
    private SessionContext sessionContext;

    private ProjectDao getProjectDao() {
        return this.daoFactory.getProjectDao();
    }

    /**
     * {@inheritDoc}
     */
    public Project getProject(long id) {
        LogUtil.logSubsystemEntry(LOG, id);
        Project project = getDaoFactory().getSearchDao().retrieve(Project.class, id);
        LogUtil.logSubsystemExit(LOG);
        return project;
    }

    /**
     * {@inheritDoc}
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public CaArrayFile addFile(Project project, File file) throws ProposalWorkflowException {
        LogUtil.logSubsystemEntry(LOG, project, file);
        checkIfProjectSaveAllowed(project);
        CaArrayFile caArrayFile = doAddFile(project, file, file.getName());
        LogUtil.logSubsystemExit(LOG);
        return caArrayFile;
    }

    /**
     * {@inheritDoc}
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public CaArrayFile addFile(Project project, File file, String filename) throws ProposalWorkflowException {
        LogUtil.logSubsystemEntry(LOG, project, file);
        checkIfProjectSaveAllowed(project);
        CaArrayFile caArrayFile = doAddFile(project, file, filename);
        LogUtil.logSubsystemExit(LOG);
        return caArrayFile;
    }

    private CaArrayFile doAddFile(Project project, File file, String filename) {
        CaArrayFile caArrayFile = null;
        caArrayFile = getFileAccessService().add(file, filename);
        project.getFiles().add(caArrayFile);
        caArrayFile.setProject(project);
        getProjectDao().save(caArrayFile);
        getProjectDao().save(project);
        HibernateUtil.getCurrentSession().flush();
        HibernateUtil.getCurrentSession().evict(caArrayFile);
        caArrayFile.clearContents();
        return caArrayFile;
    }

    /**
     * {@inheritDoc}
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void changeProjectStatus(long projectId, ProposalStatus newStatus) throws ProposalWorkflowException {
        LogUtil.logSubsystemEntry(LOG, projectId);
        Project project = getDaoFactory().getSearchDao().retrieve(Project.class, projectId);
        if (!project.isOwner(UsernameHolder.getCsmUser())) {
            LogUtil.logSubsystemExit(LOG);
            throw new PermissionDeniedException(project, "WORKFLOW_CHANGE", UsernameHolder.getUser());
        }
        if (!project.getStatus().canTransitionTo(newStatus)) {
            LogUtil.logSubsystemExit(LOG);
            throw new ProposalWorkflowException("Cannot transition project to status " + newStatus);
        }
        project.setStatus(newStatus);

        getProjectDao().save(project);
        LogUtil.logSubsystemExit(LOG);
    }

    /**
     * {@inheritDoc}
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void saveProject(Project project, PersistentObject... orphansToDelete)
        throws ProposalWorkflowException {

        LogUtil.logSubsystemEntry(LOG, project);
        checkIfProjectSaveAllowed(project);
        // make sure that an anonymous user cannot create a new project
        if (project.getId() == null && UsernameHolder.getUser().equals(SecurityUtils.ANONYMOUS_USERNAME)) {
            throw new PermissionDeniedException(project, SecurityUtils.WRITE_PRIVILEGE, UsernameHolder.getUser());
        }
        getProjectDao().save(project);
        for (PersistentObject obj : orphansToDelete) {
            if (obj != null) {
                getProjectDao().remove(obj);
            }
        }
        LogUtil.logSubsystemExit(LOG);
    }

    /**
     * Checks whether the project can be saved. if it can, does nothing, otherwise throws an exception
     *
     * @param project project to check for being able to save
     * @throws ProposalWorkflowException if the project can't be saved due to workflow state
     */
    private void checkIfProjectSaveAllowed(Project project) throws ProposalWorkflowException {
        if (!project.isSaveAllowed()) {
            LogUtil.logSubsystemExit(LOG);
            throw new ProposalWorkflowException("Cannot save project in current state");
        }
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public List<Project> getMyProjects(boolean showPublic, PageSortParams<Project> pageSortParams) {
        LogUtil.logSubsystemEntry(LOG, showPublic);
        List<Project> result = getProjectDao().getProjectsForCurrentUser(showPublic, pageSortParams);
        LogUtil.logSubsystemExit(LOG);
        return result;
    }

    /**
     * {@inheritDoc}
     */
    public int getMyProjectCount(boolean showPublic) {
        LogUtil.logSubsystemEntry(LOG, showPublic);
        int result = getProjectDao().getProjectCountForCurrentUser(showPublic);
        LogUtil.logSubsystemExit(LOG);
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public Project setUseTcgaPolicy(long projectId, boolean useTcgaPolicy) {
        LogUtil.logSubsystemEntry(LOG, projectId);
        Project project = getProject(projectId);
        if (!project.canModifyPermissions(UsernameHolder.getCsmUser())) {
            LogUtil.logSubsystemExit(LOG);
            throw new PermissionDeniedException(project, SecurityUtils.PERMISSIONS_PRIVILEGE, UsernameHolder
                    .getUser());
        }
        project.setUseTcgaPolicy(useTcgaPolicy);
        getProjectDao().save(project);
        LogUtil.logSubsystemExit(LOG);
        return project;
    }

    /**
     * {@inheritDoc}
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public AccessProfile addGroupProfile(Project project, CollaboratorGroup group) throws ProposalWorkflowException {
        LogUtil.logSubsystemEntry(LOG, project, group);
        if (!project.canModifyPermissions(UsernameHolder.getCsmUser())) {
            LogUtil.logSubsystemExit(LOG);
            throw new PermissionDeniedException(project, SecurityUtils.PERMISSIONS_PRIVILEGE, UsernameHolder
                    .getUser());
        }
        if (!project.isPermissionsEditingAllowed()) {
            LogUtil.logSubsystemExit(LOG);
            throw new ProposalWorkflowException("Cannot edit project permissions in current state");
        }
        AccessProfile profile = project.addGroupProfile(group);
        getProjectDao().save(project);
        LogUtil.logSubsystemExit(LOG);
        return profile;
    }

    /**
     * {@inheritDoc}
     */
    public File prepareForDownload(Collection<CaArrayFile> files) throws IOException {
        LogUtil.logSubsystemEntry(LOG, files);
        File result = prepareForDownload(null, files);
        LogUtil.logSubsystemExit(LOG);

        return result;
    }

    private File prepareForDownload(Project p, Collection<CaArrayFile> files) throws IOException {
        if (files == null || files.isEmpty()) {
            throw new IllegalArgumentException("Files cannot be null or empty!");
        }
        File result = File.createTempFile("data", ".zip");
        result.deleteOnExit();

        ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(result));
        for (CaArrayFile caf : files) {
            if (p == null || p.equals(caf.getProject())) {
                File f = TemporaryFileCacheLocator.getTemporaryFileCache().getFile(caf);
                InputStream is = new BufferedInputStream(new FileInputStream(f));

                ZipEntry ze = new ZipEntry(f.getName());
                zos.putNextEntry(ze);
                IOUtils.copy(is, zos);
                zos.closeEntry();
                is.close();
            }
        }
        zos.close();
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public Sample copySample(Project project, long sampleId) throws ProposalWorkflowException {
        LogUtil.logSubsystemEntry(LOG, project, sampleId);
        checkIfProjectSaveAllowed(project);
        Sample sample = getDaoFactory().getSearchDao().retrieve(Sample.class, sampleId);
        Sample copy = new Sample();
        copyInto(Sample.class, copy, sample);
        copy.setSpecimen(sample.getSpecimen());
        for (Source source : sample.getSources()) {
            source.getSamples().add(copy);
            copy.getSources().add(source);
        }
        project.getExperiment().getSamples().add(copy);
        getDaoFactory().getProjectDao().save(project);
        LogUtil.logSubsystemExit(LOG);
        return copy;
    }

    /**
     * {@inheritDoc}
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public Extract copyExtract(Project project, long extractId) throws ProposalWorkflowException {
        LogUtil.logSubsystemEntry(LOG, project, extractId);
        checkIfProjectSaveAllowed(project);
        Extract extract = getDaoFactory().getSearchDao().retrieve(Extract.class, extractId);
        Extract copy = new Extract();
        copyInto(Extract.class, copy, extract);
        copy.setMolecularSpecimen(extract.getMolecularSpecimen());
        project.getExperiment().getExtracts().add(copy);
        for (Sample sample : extract.getSamples()) {
            sample.getExtracts().add(copy);
            copy.getSamples().add(sample);
        }
        getDaoFactory().getProjectDao().save(project);
        LogUtil.logSubsystemExit(LOG);
        return copy;
    }

    /**
     * {@inheritDoc}
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public LabeledExtract copyLabeledExtract(Project project, long extractId) throws ProposalWorkflowException {
        LogUtil.logSubsystemEntry(LOG, project, extractId);
        checkIfProjectSaveAllowed(project);
        LabeledExtract le = getDaoFactory().getSearchDao().retrieve(LabeledExtract.class, extractId);
        LabeledExtract copy = new LabeledExtract();
        copyInto(LabeledExtract.class, copy, le);
        copy.setLabel(le.getLabel());
        project.getExperiment().getLabeledExtracts().add(copy);
        for (Extract e : le.getExtracts()) {
            e.getLabeledExtracts().add(copy);
            copy.getExtracts().add(e);
        }
        getDaoFactory().getProjectDao().save(project);
        LogUtil.logSubsystemExit(LOG);
        return copy;
    }

    private <T extends AbstractBioMaterial> void copyInto(Class<T> clazz, T to, T from) {
        String copyName = getGenericDataService().getIncrementingCopyName(clazz, "name", from.getName());
        to.setName(copyName);
        to.setDescription(from.getDescription());
        to.setMaterialType(from.getMaterialType());
        to.setTissueSite(from.getTissueSite());
        to.setCellType(from.getCellType());
        to.setDiseaseState(from.getDiseaseState());
        to.setOrganism(from.getOrganism());
        for (ProtocolApplication pa : from.getProtocolApplications()) {
            ProtocolApplication newPa = new ProtocolApplication();
            newPa.setBioMaterial(to);
            newPa.setProtocol(pa.getProtocol());
            to.getProtocolApplications().add(newPa);
        }
    }

    /**
     * {@inheritDoc}
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public Factor copyFactor(Project project, long factorId) throws ProposalWorkflowException {
        checkIfProjectSaveAllowed(project);
        Factor factor = getDaoFactory().getSearchDao().retrieve(Factor.class, factorId);
        Factor copy = new Factor();
        String copyName = getGenericDataService().getIncrementingCopyName(Factor.class, "name", factor.getName());
        copy.setName(copyName);
        copy.setType(factor.getType());
        project.getExperiment().getFactors().add(copy);
        getDaoFactory().getProjectDao().save(project);
        return copy;
    }

    /**
     * {@inheritDoc}
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public Source copySource(Project project, long sourceId) throws ProposalWorkflowException {
        checkIfProjectSaveAllowed(project);
        Source source = getDaoFactory().getSearchDao().retrieve(Source.class, sourceId);
        Source copy = new Source();
        copyInto(Source.class, copy, source);
        project.getExperiment().getSources().add(copy);
        getDaoFactory().getProjectDao().save(project);
        return copy;
    }

    FileAccessService getFileAccessService() {
        return (FileAccessService) ServiceLocatorFactory.getLocator().lookup(FileAccessService.JNDI_NAME);
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

    private GenericDataService getGenericDataService() {
        return (GenericDataService) ServiceLocatorFactory.getLocator().lookup(GenericDataService.JNDI_NAME);
    }

    /**
     * {@inheritDoc}
     */
    public List<Project> searchByCategory(PageSortParams<Project> params, String keyword,
            SearchCategory... categories) {
        return getProjectDao().searchByCategory(params, keyword, categories);
    }

    /**
     * {@inheritDoc}
     */
    public int searchCount(String keyword, SearchCategory... categories) {
        return getProjectDao().searchCount(keyword, categories);
    }

    /**
     * {@inheritDoc}
     */
    public File prepareHybsForDownload(Project p, Collection<Hybridization> hybridizations) throws IOException {
        LogUtil.logSubsystemEntry(LOG, p, hybridizations);
        Collection<CaArrayFile> files = new HashSet<CaArrayFile>();
        if (hybridizations != null && !hybridizations.isEmpty()) {
            for (Hybridization h : hybridizations) {
                files.addAll(h.getAllDataFiles());
            }
        }

        if (files.isEmpty()) {
            LogUtil.logSubsystemExit(LOG);
            return null;
        }

        File result = prepareForDownload(p, files);
        LogUtil.logSubsystemExit(LOG);
        return result;
    }

    /**
     * {@inheritDoc}
     */
    public List<Term> getCellTypesForExperiment(Experiment experiment) {
        return getProjectDao().getCellTypesForExperiment(experiment);
    }

    /**
     * {@inheritDoc}
     */
    public List<Term> getDiseaseStatesForExperiment(Experiment experiment) {
        return getProjectDao().getDiseaseStatesForExperiment(experiment);
    }

    /**
     * {@inheritDoc}
     */
    public List<Term> getMaterialTypesForExperiment(Experiment experiment) {
        return getProjectDao().getMaterialTypesForExperiment(experiment);
    }

    /**
     * {@inheritDoc}
     */
    public List<Term> getTissueSitesForExperiment(Experiment experiment) {
        return getProjectDao().getTissueSitesForExperiment(experiment);
    }
}
