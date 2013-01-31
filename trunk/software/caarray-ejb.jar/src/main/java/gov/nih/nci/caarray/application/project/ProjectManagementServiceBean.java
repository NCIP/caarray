//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.application.project;

import gov.nih.nci.caarray.application.ExceptionLoggingInterceptor;
import gov.nih.nci.caarray.application.GenericDataService;
import gov.nih.nci.caarray.application.ServiceLocatorFactory;
import gov.nih.nci.caarray.application.fileaccess.FileAccessService;
import gov.nih.nci.caarray.application.project.InconsistentProjectStateException.Reason;
import gov.nih.nci.caarray.dao.CaArrayDaoFactory;
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
import gov.nih.nci.caarray.security.PermissionDeniedException;
import gov.nih.nci.caarray.security.SecurityUtils;
import gov.nih.nci.caarray.util.HibernateUtil;
import gov.nih.nci.caarray.util.UsernameHolder;
import gov.nih.nci.caarray.util.io.logging.LogUtil;
import gov.nih.nci.security.AuthorizationManager;
import gov.nih.nci.security.authorization.domainobjects.User;
import gov.nih.nci.security.exceptions.CSException;

import java.io.File;
import java.io.InputStream;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;
import javax.ejb.Local;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.interceptor.Interceptors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.jboss.annotation.ejb.TransactionTimeout;

import com.fiveamsolutions.nci.commons.data.persistent.PersistentObject;
import com.fiveamsolutions.nci.commons.data.search.PageSortParams;

/**
 * Implementation entry point for the ProjectManagement subsystem.
 */
@Local(ProjectManagementService.class)
@Stateless
@Interceptors(ExceptionLoggingInterceptor.class)
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
@SuppressWarnings({"PMD.ExcessiveClassLength", "PMD.TooManyMethods", "PMD.CyclomaticComplexity" })
public class ProjectManagementServiceBean implements ProjectManagementService {
    private static final Logger LOG = Logger.getLogger(ProjectManagementServiceBean.class);
    private static final int UPLOAD_TIMEOUT = 7200;
    private CaArrayDaoFactory daoFactory = CaArrayDaoFactory.INSTANCE;
    private static final int DELETE_TIMEOUT = 3600;

    @Resource
    private SessionContext sessionContext;

    private ProjectDao getProjectDao() {
        return this.daoFactory.getProjectDao();
    }

    private FileDao getFileDao() {
        return this.daoFactory.getFileDao();
    }

    private SampleDao getSampleDao() {
        return this.daoFactory.getSampleDao();
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
    public Project getProjectByPublicId(String publicId) {
        LogUtil.logSubsystemEntry(LOG, publicId);
        Project project = getProjectDao().getProjectByPublicId(publicId);
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
        HibernateUtil.getCurrentSession().flush();
        caArrayFile.clearAndEvictContents();
    }

    /**
     * {@inheritDoc}
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void changeProjectLockStatus(long projectId, boolean newStatus) throws ProposalWorkflowException {
        LogUtil.logSubsystemEntry(LOG, projectId);
        Project project = getDaoFactory().getSearchDao().retrieve(Project.class, projectId);
        if (!project.isOwner(UsernameHolder.getCsmUser())) {
            LogUtil.logSubsystemExit(LOG);
            throw new PermissionDeniedException(project, "WORKFLOW_CHANGE", UsernameHolder.getUser());
        }
        if (project.isLocked() == newStatus) {
            LogUtil.logSubsystemExit(LOG);
            throw new ProposalWorkflowException("project already " + (newStatus ? " locked" : "unlocked"));
        }
        project.setLocked(newStatus);

        getProjectDao().save(project);
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
        if (project.getId() == null && UsernameHolder.getUser().equals(SecurityUtils.ANONYMOUS_USERNAME)) {
            throw new PermissionDeniedException(project, SecurityUtils.WRITE_PRIVILEGE, UsernameHolder.getUser());
        }

        if (project.getId() == null) {
            // for the initial save, we will need to save experiment first since we need to assign a public
            // identifier, which requires the id to be set
            getProjectDao().save(project.getExperiment());
        }
        // need to save twice, since we need to update the public
        project.recalculatePublicId();
        getProjectDao().save(project);
        for (PersistentObject obj : orphansToDelete) {
            if (obj != null) {
                getProjectDao().remove(obj);
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
        if (!project.isOwner(UsernameHolder.getCsmUser())) {
            LogUtil.logSubsystemExit(LOG);
            throw new PermissionDeniedException(project, SecurityUtils.WRITE_PRIVILEGE, UsernameHolder.getUser());
        }
        if (project.isLocked()) {
            LogUtil.logSubsystemExit(LOG);
            throw new ProposalWorkflowException("Cannot delete a locked project");
        }
        // remove the blobs
        getFileDao().deleteHqlBlobsByProjectId(project.getId());

        // refresh project
        Project freshProject = this.getProject(project.getId());
        // both hybridizations and project are trying to delete the save files via cascades, so explicitly
        //delete hybridizations (and their files) first

        for (Hybridization hyb : freshProject.getExperiment().getHybridizations()) {
            getProjectDao().remove(hyb);
        }

        getProjectDao().remove(freshProject);

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
     * @param project project to check for being able to save
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
        List<Project> result = getProjectDao().getProjectsForCurrentUser(pageSortParams);
        LogUtil.logSubsystemExit(LOG);
        return result;
    }

    /**
     * {@inheritDoc}
     */
    public int getMyProjectCount() {
        LogUtil.logSubsystemEntry(LOG);
        int result = getProjectDao().getProjectCountForCurrentUser();
        LogUtil.logSubsystemExit(LOG);
        return result;
    }

    /**
     * {@inheritDoc}
     */
    public List<Project> getProjectsForOwner(User user) {
        LogUtil.logSubsystemEntry(LOG, user);
        List<Project> result = getProjectDao().getProjectsForOwner(user);
        LogUtil.logSubsystemExit(LOG);
        return result;
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
        AccessProfile profile = project.addGroupProfile(group);
        getProjectDao().save(project);
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
        Sample sample = getDaoFactory().getSearchDao().retrieve(Sample.class, sampleId);
        Sample copy = new Sample();
        copyInto(Sample.class, copy, sample);
        for (Source source : sample.getSources()) {
            source.getSamples().add(copy);
            copy.getSources().add(source);
        }
        project.getExperiment().getSamples().add(copy);
        copy.setExperiment(project.getExperiment());
        getDaoFactory().getProjectDao().save(project);
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
        Extract extract = getDaoFactory().getSearchDao().retrieve(Extract.class, extractId);
        Extract copy = new Extract();
        copyInto(Extract.class, copy, extract);
        project.getExperiment().getExtracts().add(copy);
        copy.setExperiment(project.getExperiment());
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
    public LabeledExtract copyLabeledExtract(Project project, long extractId) throws ProposalWorkflowException,
            InconsistentProjectStateException {
        LogUtil.logSubsystemEntry(LOG, project, extractId);
        checkIfProjectSaveAllowed(project);
        LabeledExtract le = getDaoFactory().getSearchDao().retrieve(LabeledExtract.class, extractId);
        LabeledExtract copy = new LabeledExtract();
        copyInto(LabeledExtract.class, copy, le);
        copy.setLabel(le.getLabel());
        project.getExperiment().getLabeledExtracts().add(copy);
        copy.setExperiment(project.getExperiment());
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
    public Source copySource(Project project, long sourceId) throws ProposalWorkflowException,
            InconsistentProjectStateException {
        checkIfProjectSaveAllowed(project);
        Source source = getDaoFactory().getSearchDao().retrieve(Source.class, sourceId);
        Source copy = new Source();
        copyInto(Source.class, copy, source);
        project.getExperiment().getSources().add(copy);
        copy.setExperiment(project.getExperiment());
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
    public List<Experiment> searchByCriteria(PageSortParams<Experiment> params, ExperimentSearchCriteria criteria) {
        return getProjectDao().searchByCriteria(params, criteria);
    }
    /**
     * {@inheritDoc}
     */
    public List<Sample> searchSamplesByExperimentAndCategory(String keyword, Experiment e, SearchSampleCategory... c) {
        return getSampleDao().searchSamplesByExperimentAndCategory(keyword, e, c);
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
    public List<AssayType> getAssayTypes() {
        return getProjectDao().getAssayTypes();
    }

    /**
     * {@inheritDoc}
     */
    public List<Term> getTissueSitesForExperiment(Experiment experiment) {
        return getProjectDao().getTissueSitesForExperiment(experiment);
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
            throw new IllegalArgumentException("Could not create new instance of class " + biomaterialClass);
        } catch (IllegalAccessException e) {
            throw new IllegalArgumentException("Could not create new instance of class " + biomaterialClass);
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
        return getSampleDao().searchByCategory(params, keyword, biomaterialClass, categories);
    }

    /**
     * {@inheritDoc}
     */
    public List<Sample> searchSamplesByCharacteristicCategory(PageSortParams<Sample> params,
            Category c, String keyword) {
        return getSampleDao().searchSamplesByCharacteristicCategory(params, c, keyword);
    }

    /**
     * {@inheritDoc}
     */
    public List<Source> searchSourcesByCharacteristicCategory(PageSortParams<Source> params,
            Category c, String keyword) {
        return getSampleDao().searchSourcesByCharacteristicCategory(params, c, keyword);
    }

    /**
     * {@inheritDoc}
     */
    public int countSamplesByCharacteristicCategory(Category c, String keyword) {
        return getSampleDao().countSamplesByCharacteristicCategory(c, keyword);
    }

    /**
     * {@inheritDoc}
     */
    public int countSourcesByCharacteristicCategory(Category c, String keyword) {
        return getSampleDao().countSourcesByCharacteristicCategory(c, keyword);
    }

    /**
     * {@inheritDoc}
     */
    public int searchCount(String keyword, Class<? extends AbstractBioMaterial> biomaterialClass,
            BiomaterialSearchCategory... categories) {
        return getSampleDao().searchCount(keyword, biomaterialClass, categories);
    }
    
    /**
     * {@inheritDoc}
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void changeOwner(Long projectId, String newOwner) throws CSException {
        Project project = getDaoFactory().getSearchDao().retrieve(Project.class, projectId);
        AuthorizationManager am = SecurityUtils.getAuthorizationManager();
        User newOwnerUser = am.getUser(newOwner);
        SecurityUtils.changeOwner(project, newOwnerUser);
    }

    private SearchDao getSearchDao() {
        return this.daoFactory.getSearchDao();
    }
}
