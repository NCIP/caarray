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
import gov.nih.nci.caarray.application.ServiceLocatorFactory;
import gov.nih.nci.caarray.application.fileaccess.FileAccessService;
import gov.nih.nci.caarray.application.project.InconsistentProjectStateException.Reason;
import gov.nih.nci.caarray.dao.FileDao;
import gov.nih.nci.caarray.dao.ProjectDao;
import gov.nih.nci.caarray.dao.SampleDao;
import gov.nih.nci.caarray.dao.SearchDao;
import gov.nih.nci.caarray.domain.array.ArrayDesign;
import gov.nih.nci.caarray.domain.file.CaArrayFile;
import gov.nih.nci.caarray.domain.hybridization.Hybridization;
import gov.nih.nci.caarray.domain.permissions.AccessProfile;
import gov.nih.nci.caarray.domain.permissions.CollaboratorGroup;
import gov.nih.nci.caarray.domain.project.AssayType;
import gov.nih.nci.caarray.domain.project.Experiment;
import gov.nih.nci.caarray.domain.project.Factor;
import gov.nih.nci.caarray.domain.project.Project;
import gov.nih.nci.caarray.domain.protocol.ProtocolApplication;
import gov.nih.nci.caarray.domain.sample.AbstractBioMaterial;
import gov.nih.nci.caarray.domain.sample.Extract;
import gov.nih.nci.caarray.domain.sample.LabeledExtract;
import gov.nih.nci.caarray.domain.sample.Sample;
import gov.nih.nci.caarray.domain.sample.Source;
import gov.nih.nci.caarray.domain.search.BiomaterialSearchCategory;
import gov.nih.nci.caarray.domain.search.ExperimentSearchCriteria;
import gov.nih.nci.caarray.domain.search.SearchCategory;
import gov.nih.nci.caarray.domain.search.SearchSampleCategory;
import gov.nih.nci.caarray.domain.vocabulary.Category;
import gov.nih.nci.caarray.domain.vocabulary.Term;
import gov.nih.nci.caarray.injection.InjectionInterceptor;
import gov.nih.nci.caarray.security.PermissionDeniedException;
import gov.nih.nci.caarray.security.SecurityUtils;
import gov.nih.nci.caarray.util.CaArrayUsernameHolder;
import gov.nih.nci.caarray.util.io.logging.LogUtil;
import gov.nih.nci.security.AuthorizationManager;
import gov.nih.nci.security.authorization.domainobjects.User;
import gov.nih.nci.security.exceptions.CSException;

import java.io.File;
import java.io.InputStream;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.interceptor.Interceptors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.jboss.ejb3.annotation.TransactionTimeout;

import com.fiveamsolutions.nci.commons.data.persistent.PersistentObject;
import com.fiveamsolutions.nci.commons.data.search.PageSortParams;
import com.google.inject.Inject;

/**
 * Implementation entry point for the ProjectManagement subsystem.
 */
@Local(ProjectManagementService.class)
@Stateless
@Interceptors({ ExceptionLoggingInterceptor.class, InjectionInterceptor.class })
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
@SuppressWarnings({"PMD.ExcessiveClassLength", "PMD.TooManyMethods", "PMD.CyclomaticComplexity" })
public class ProjectManagementServiceBean implements ProjectManagementService {
    private static final Logger LOG = Logger.getLogger(ProjectManagementServiceBean.class);
    private static final int UPLOAD_TIMEOUT = 7200;
    private static final int DELETE_TIMEOUT = 3600;
    /**
     * public id prefix {@value}.
     */
    static final String PUBLIC_ID_PREFIX = "EXP-";
    
    private ProjectDao projectDao;
    private FileDao fileDao;
    private SampleDao sampleDao;
    private SearchDao searchDao;
    
    /**
     * 
     * @param projectDao the ProjectDao dependency
     * @param fileDao the FileDao dependency
     * @param sampleDao the SampleDao dependency
     * @param searchDao the SearchDao dependency
     */
    @Inject
    public void setDependencies(ProjectDao projectDao, FileDao fileDao, SampleDao sampleDao,
            SearchDao searchDao) {
        this.projectDao = projectDao;
        this.fileDao = fileDao;
        this.sampleDao = sampleDao;
        this.searchDao = searchDao;
    }
    
    /**
     * {@inheritDoc}
     */
    public Project getProjectByPublicId(String publicId) {
        LogUtil.logSubsystemEntry(LOG, publicId);
        Project project = projectDao.getProjectByPublicId(publicId);
        LogUtil.logSubsystemExit(LOG);
        return project;
    }

    /**
     * {@inheritDoc}
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    @TransactionTimeout(UPLOAD_TIMEOUT)
    public CaArrayFile addFile(Project project, File file) throws ProposalWorkflowException,
            InconsistentProjectStateException {
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
    @TransactionTimeout(UPLOAD_TIMEOUT)
    public CaArrayFile addFile(Project project, File file, String filename) throws ProposalWorkflowException,
            InconsistentProjectStateException {
        LogUtil.logSubsystemEntry(LOG, project, file);
        checkIfProjectSaveAllowed(project);
        CaArrayFile caArrayFile = doAddFile(project, file, filename);
        LogUtil.logSubsystemExit(LOG);
        return caArrayFile;
    }

    /**
     * {@inheritDoc}
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    @TransactionTimeout(UPLOAD_TIMEOUT)
    public CaArrayFile addFile(Project project, InputStream data, String filename) throws ProposalWorkflowException,
            InconsistentProjectStateException {
        LogUtil.logSubsystemEntry(LOG, project);
        checkIfProjectSaveAllowed(project);
        CaArrayFile caArrayFile = doAddStream(project, data, filename);

        LogUtil.logSubsystemExit(LOG);
        return caArrayFile;
    }

    private CaArrayFile doAddStream(Project project, InputStream stream, String filename)
            throws ProposalWorkflowException, InconsistentProjectStateException  {
        checkIfProjectSaveAllowed(project);
        CaArrayFile caArrayFile = getFileAccessService().add(stream, filename);
        addCaArrayFileToProject(project, caArrayFile);
        return caArrayFile;
    }

    private CaArrayFile doAddFile(Project project, File file, String filename) {
        CaArrayFile caArrayFile = getFileAccessService().add(file, filename);
        addCaArrayFileToProject(project, caArrayFile);
        return caArrayFile;
    }

    private void addCaArrayFileToProject(Project project, CaArrayFile caArrayFile) {
        project.getFiles().add(caArrayFile);
        caArrayFile.setProject(project);
        getFileAccessService().save(caArrayFile);
    }

    /**
     * {@inheritDoc}
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void changeProjectLockStatus(long projectId, boolean newStatus) throws ProposalWorkflowException {
        LogUtil.logSubsystemEntry(LOG, projectId);
        Project project = searchDao.retrieve(Project.class, projectId);
        if (!project.isOwner(CaArrayUsernameHolder.getCsmUser())) {
            LogUtil.logSubsystemExit(LOG);
            throw new PermissionDeniedException(project, "WORKFLOW_CHANGE", CaArrayUsernameHolder.getUser());
        }
        if (project.isLocked() == newStatus) {
            LogUtil.logSubsystemExit(LOG);
            throw new ProposalWorkflowException("project already " + (newStatus ? " locked" : "unlocked"));
        }
        project.setLocked(newStatus);

        projectDao.save(project);
        LogUtil.logSubsystemExit(LOG);
    }

    /**
     * {@inheritDoc}
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void saveProject(Project project, PersistentObject... orphansToDelete)
        throws ProposalWorkflowException, InconsistentProjectStateException {

        LogUtil.logSubsystemEntry(LOG, project);
        checkIfProjectSaveAllowed(project);
        // make sure that an anonymous user cannot create a new project
        if (project.getId() == null && CaArrayUsernameHolder.getUser().equals(SecurityUtils.ANONYMOUS_USERNAME)) {
            throw new PermissionDeniedException(project, SecurityUtils.WRITE_PRIVILEGE,
                    CaArrayUsernameHolder.getUser());
        }

        if (project.getId() == null) {
            // for the initial save, we will need to save experiment first since we need to assign a public
            // identifier, which requires the id to be set
            Experiment exp = project.getExperiment();
            projectDao.save(exp);
            exp.setPublicIdentifier(PUBLIC_ID_PREFIX + String.valueOf(exp.getId()));
        }
        projectDao.save(project);
        for (PersistentObject obj : orphansToDelete) {
            if (obj != null) {
                projectDao.remove(obj);
            }
        }
        LogUtil.logSubsystemExit(LOG);
    }

    /**
     * {@inheritDoc}
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    @TransactionTimeout(DELETE_TIMEOUT)
    public void deleteProject(Project project) throws ProposalWorkflowException {
        LogUtil.logSubsystemEntry(LOG, project);
        if (!project.isOwner(CaArrayUsernameHolder.getCsmUser())) {
            LogUtil.logSubsystemExit(LOG);
            throw new PermissionDeniedException(project, SecurityUtils.WRITE_PRIVILEGE,
                    CaArrayUsernameHolder.getUser());
        }
        if (project.isLocked()) {
            LogUtil.logSubsystemExit(LOG);
            throw new ProposalWorkflowException("Cannot delete a locked project");
        }
        // remove the blobs
        fileDao.deleteHqlBlobsByProjectId(project.getId());

        // refresh project
        Project freshProject = getSearchDao().retrieve(Project.class, project.getId());
        // both hybridizations and project are trying to delete the saveAndEvict files via cascades, so explicitly
        //delete hybridizations (and their files) first

        for (Hybridization hyb : freshProject.getExperiment().getHybridizations()) {
            projectDao.remove(hyb);
        }

        projectDao.remove(freshProject);

        LogUtil.logSubsystemExit(LOG);
    }

    /**
     * Checks whether the project has files that are currently importing. if not, does nothing,
     * otherwise throws an exception because you cannot edit a project while it has
     * files being imported
     *
     * @param project project to check
     * @throws InconsistentProjectStateException if the project state is not consistent
     */
    private void checkImportInProgress(Project project) throws InconsistentProjectStateException {
        if (project.isImportingData()) {
            throw new InconsistentProjectStateException(Reason.IMPORTING_FILES);
        }
    }

    /**
     * Checks whether the user-specified array designs in the given project are consistent with ones
     * inferred from actual hybridization data. if they are, does nothing, otherwise throws an exception.
     *
     * @param project project to check
     * @throws InconsistentProjectStateException if the project state is not consistent
     */
    private void checkArrayDesignsConsistent(Project project) throws InconsistentProjectStateException {
        Set<ArrayDesign> declaredDesigns = project.getExperiment().getArrayDesigns();
        Set<ArrayDesign> usedDesigns = project.getExperiment().getArrayDesignsFromHybs();
        Set<String> missingDesignNames = new HashSet<String>();
        for (ArrayDesign ad : usedDesigns) {
            if (!declaredDesigns.contains(ad)) {
                missingDesignNames.add(ad.getName());
            }
        }
        if (!missingDesignNames.isEmpty()) {
            throw new InconsistentProjectStateException(Reason.INCONSISTENT_ARRAY_DESIGNS,
                    missingDesignNames.toArray());
        }
    }

    private void checkArrayDesignManufacturer(Project project) throws InconsistentProjectStateException {
        Set<ArrayDesign> designs = project.getExperiment().getArrayDesigns();
        for (ArrayDesign ad : designs) {
            if ((project.getExperiment().getManufacturer() != null
                    && project.getExperiment().getManufacturer() != ad.getProvider())
                    || (!project.getExperiment().getAssayTypes().isEmpty()
                            && !CollectionUtils.containsAny(ad.getAssayTypes(),
                                    project.getExperiment().getAssayTypes()))) {
                throw new InconsistentProjectStateException(Reason.ARRAY_DESIGNS_DONT_MATCH_MANUF_OR_TYPE,
                        new Object[] {});
            }

        }
    }

    /**
     * Checks whether the project can be saved. if it can, does nothing, otherwise throws an exception
     *
     * @param project project to check for being able to saveAndEvict
     * @throws ProposalWorkflowException if the project can't be saved due to workflow state
     * @throws InconsistentProjectStateException if the project can't be saved because its state
     * is inconsistent
     */
    private void checkIfProjectSaveAllowed(Project project) throws ProposalWorkflowException,
            InconsistentProjectStateException {
        if (project.isLocked()) {
            LogUtil.logSubsystemExit(LOG);
            throw new ProposalWorkflowException("Cannot save a locked project");
        }
        checkArrayDesignManufacturer(project);
        checkArrayDesignsConsistent(project);
        checkImportInProgress(project);
    }

    /**
     * {@inheritDoc}
     */
    public List<Project> getMyProjects(PageSortParams<Project> pageSortParams) {
        LogUtil.logSubsystemEntry(LOG);
        List<Project> result = projectDao.getProjectsForCurrentUser(pageSortParams);
        LogUtil.logSubsystemExit(LOG);
        return result;
    }

    /**
     * {@inheritDoc}
     */
    public int getMyProjectCount() {
        LogUtil.logSubsystemEntry(LOG);
        int result = projectDao.getProjectCountForCurrentUser();
        LogUtil.logSubsystemExit(LOG);
        return result;
    }

    /**
     * {@inheritDoc}
     */
    public List<Project> getProjectsForOwner(User user) {
        LogUtil.logSubsystemEntry(LOG, user);
        List<Project> result = projectDao.getProjectsForOwner(user);
        LogUtil.logSubsystemExit(LOG);
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public AccessProfile addGroupProfile(Project project, CollaboratorGroup group) throws ProposalWorkflowException {
        LogUtil.logSubsystemEntry(LOG, project, group);
        if (!project.canModifyPermissions(CaArrayUsernameHolder.getCsmUser())) {
            LogUtil.logSubsystemExit(LOG);
            throw new PermissionDeniedException(project, SecurityUtils.PERMISSIONS_PRIVILEGE, CaArrayUsernameHolder
                    .getUser());
        }
        AccessProfile profile = project.addGroupProfile(group);
        projectDao.save(project);
        LogUtil.logSubsystemExit(LOG);
        return profile;
    }

    /**
     * {@inheritDoc}
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public Sample copySample(Project project, long sampleId) throws ProposalWorkflowException,
            InconsistentProjectStateException {
        LogUtil.logSubsystemEntry(LOG, project, sampleId);
        checkIfProjectSaveAllowed(project);
        Sample sample = searchDao.retrieve(Sample.class, sampleId);
        Sample copy = new Sample();
        copyInto(Sample.class, copy, sample);
        for (Source source : sample.getSources()) {
            source.getSamples().add(copy);
            copy.getSources().add(source);
        }
        project.getExperiment().getSamples().add(copy);
        copy.setExperiment(project.getExperiment());
        projectDao.save(project);
        LogUtil.logSubsystemExit(LOG);
        return copy;
    }

    /**
     * {@inheritDoc}
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public Extract copyExtract(Project project, long extractId) throws ProposalWorkflowException,
            InconsistentProjectStateException {
        LogUtil.logSubsystemEntry(LOG, project, extractId);
        checkIfProjectSaveAllowed(project);
        Extract extract = searchDao.retrieve(Extract.class, extractId);
        Extract copy = new Extract();
        copyInto(Extract.class, copy, extract);
        project.getExperiment().getExtracts().add(copy);
        copy.setExperiment(project.getExperiment());
        for (Sample sample : extract.getSamples()) {
            sample.getExtracts().add(copy);
            copy.getSamples().add(sample);
        }
        projectDao.save(project);
        LogUtil.logSubsystemExit(LOG);
        return copy;
    }

    /**
     * {@inheritDoc}
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public LabeledExtract copyLabeledExtract(Project project, long extractId) throws ProposalWorkflowException,
            InconsistentProjectStateException {
        LogUtil.logSubsystemEntry(LOG, project, extractId);
        checkIfProjectSaveAllowed(project);
        LabeledExtract le = searchDao.retrieve(LabeledExtract.class, extractId);
        LabeledExtract copy = new LabeledExtract();
        copyInto(LabeledExtract.class, copy, le);
        copy.setLabel(le.getLabel());
        project.getExperiment().getLabeledExtracts().add(copy);
        copy.setExperiment(project.getExperiment());
        for (Extract e : le.getExtracts()) {
            e.getLabeledExtracts().add(copy);
            copy.getExtracts().add(e);
        }
        projectDao.save(project);
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
            newPa.setProtocol(pa.getProtocol());
            to.addProtocolApplication(newPa);
        }
    }

    /**
     * {@inheritDoc}
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public Factor copyFactor(Project project, long factorId) throws ProposalWorkflowException,
            InconsistentProjectStateException {
        checkIfProjectSaveAllowed(project);
        Factor factor = searchDao.retrieve(Factor.class, factorId);
        Factor copy = new Factor();
        String copyName = getGenericDataService().getIncrementingCopyName(Factor.class, "name", factor.getName());
        copy.setName(copyName);
        copy.setType(factor.getType());
        project.getExperiment().getFactors().add(copy);
        projectDao.save(project);
        return copy;
    }

    /**
     * {@inheritDoc}
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public Source copySource(Project project, long sourceId) throws ProposalWorkflowException,
            InconsistentProjectStateException {
        checkIfProjectSaveAllowed(project);
        Source source = searchDao.retrieve(Source.class, sourceId);
        Source copy = new Source();
        copyInto(Source.class, copy, source);
        project.getExperiment().getSources().add(copy);
        copy.setExperiment(project.getExperiment());
        projectDao.save(project);
        return copy;
    }

    private FileAccessService getFileAccessService() {
        return ServiceLocatorFactory.getFileAccessService();
    }

    private GenericDataService getGenericDataService() {
        return (GenericDataService) ServiceLocatorFactory.getLocator().lookup(GenericDataService.JNDI_NAME);
    }

    /**
     * {@inheritDoc}
     */
    public List<Project> searchByCategory(PageSortParams<Project> params, String keyword,
            SearchCategory... categories) {
        return projectDao.searchByCategory(params, keyword, categories);
    }

    /**
     * {@inheritDoc}
     */
    public List<Experiment> searchByCriteria(PageSortParams<Experiment> params, ExperimentSearchCriteria criteria) {
        return projectDao.searchByCriteria(params, criteria);
    }
    /**
     * {@inheritDoc}
     */
    public List<Sample> searchSamplesByExperimentAndCategory(String keyword, Experiment e, SearchSampleCategory... c) {
        return sampleDao.searchSamplesByExperimentAndCategory(keyword, e, c);
    }

    /**
     * {@inheritDoc}
     */
    public int searchCount(String keyword, SearchCategory... categories) {
        return projectDao.searchCount(keyword, categories);
    }

    /**
     * {@inheritDoc}
     */
    public List<Term> getCellTypesForExperiment(Experiment experiment) {
        return projectDao.getCellTypesForExperiment(experiment);
    }

    /**
     * {@inheritDoc}
     */
    public List<Term> getDiseaseStatesForExperiment(Experiment experiment) {
        return projectDao.getDiseaseStatesForExperiment(experiment);
    }

    /**
     * {@inheritDoc}
     */
    public List<Term> getMaterialTypesForExperiment(Experiment experiment) {
        return projectDao.getMaterialTypesForExperiment(experiment);
    }
    /**
     * {@inheritDoc}
     */
    public List<AssayType> getAssayTypes() {
        return projectDao.getAssayTypes();
    }

    /**
     * {@inheritDoc}
     */
    public List<Term> getTissueSitesForExperiment(Experiment experiment) {
        return projectDao.getTissueSitesForExperiment(experiment);
    }

    /**
     * {@inheritDoc}
     */
    public List<CaArrayFile> getDeletableFiles(Long projectId) {
        return fileDao.getDeletableFiles(projectId);
    }

    /**
     * {@inheritDoc}
     */
    public <T extends AbstractBioMaterial> T getBiomaterialByExternalId(Project project, String externalId,
            Class<T> biomaterialClass) {
        T bm;
        try {
            bm = biomaterialClass.newInstance();
        } catch (InstantiationException e) {
            throw new IllegalArgumentException("Could not create new instance of class " + biomaterialClass, e);
        } catch (IllegalAccessException e) {
            throw new IllegalArgumentException("Could not create new instance of class " + biomaterialClass, e);
        }
        bm.setExternalId(externalId);
        bm.setExperiment(project.getExperiment());
        List<T> bms = getSearchDao().query(bm);
        if (bms == null || bms.isEmpty()) {
            return null;
        } else if (bms.size() > 1) {
            throw new IllegalStateException("Too many biomaterials found matching external id");
        } 
        return bms.get(0);
    }

    /**
     * {@inheritDoc}
     */
    public <T extends AbstractBioMaterial>List<T>  searchByCategory(PageSortParams<T> params, String keyword,
            Class<T> biomaterialClass, BiomaterialSearchCategory... categories) {
        return sampleDao.searchByCategory(params, keyword, biomaterialClass, categories);
    }

    /**
     * {@inheritDoc}
     */
    public List<Sample> searchSamplesByCharacteristicCategory(PageSortParams<Sample> params,
            Category c, String keyword) {
        return sampleDao.searchSamplesByCharacteristicCategory(params, c, keyword);
    }

    /**
     * {@inheritDoc}
     */
    public List<Source> searchSourcesByCharacteristicCategory(PageSortParams<Source> params,
            Category c, String keyword) {
        return sampleDao.searchSourcesByCharacteristicCategory(params, c, keyword);
    }

    /**
     * {@inheritDoc}
     */
    public int countSamplesByCharacteristicCategory(Category c, String keyword) {
        return sampleDao.countSamplesByCharacteristicCategory(c, keyword);
    }

    /**
     * {@inheritDoc}
     */
    public int countSourcesByCharacteristicCategory(Category c, String keyword) {
        return sampleDao.countSourcesByCharacteristicCategory(c, keyword);
    }

    /**
     * {@inheritDoc}
     */
    public int searchCount(String keyword, Class<? extends AbstractBioMaterial> biomaterialClass,
            BiomaterialSearchCategory... categories) {
        return sampleDao.searchCount(keyword, biomaterialClass, categories);
    }
    
    /**
     * {@inheritDoc}
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void changeOwner(Long projectId, String newOwner) throws CSException {
        Project project = searchDao.retrieve(Project.class, projectId);
        AuthorizationManager am = SecurityUtils.getAuthorizationManager();
        User newOwnerUser = am.getUser(newOwner);
        SecurityUtils.changeOwner(project, newOwnerUser);
    }

    private SearchDao getSearchDao() {
        return searchDao;
    }

    /**
     * {@inheritDoc}
     */
    public List<Project> getProjectsWithReImportableFiles() {
        return projectDao.getProjectsWithReImportable();
    }

    
}
