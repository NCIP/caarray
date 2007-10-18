package gov.nih.nci.caarray.web.action;

import edu.georgetown.pir.Organism;
import gov.nih.nci.caarray.application.arraydesign.ArrayDesignService;
import gov.nih.nci.caarray.application.project.ProjectManagementService;
import gov.nih.nci.caarray.application.project.ProposalWorkflowException;
import gov.nih.nci.caarray.business.vocabulary.VocabularyService;
import gov.nih.nci.caarray.business.vocabulary.VocabularyServiceException;
import gov.nih.nci.caarray.domain.PersistentObject;
import gov.nih.nci.caarray.domain.array.ArrayDesign;
import gov.nih.nci.caarray.domain.contact.Organization;
import gov.nih.nci.caarray.domain.contact.Person;
import gov.nih.nci.caarray.domain.project.ExperimentContact;
import gov.nih.nci.caarray.domain.project.ExperimentOntology;
import gov.nih.nci.caarray.domain.project.ExperimentOntologyCategory;
import gov.nih.nci.caarray.domain.project.Factor;
import gov.nih.nci.caarray.domain.project.PaymentMechanism;
import gov.nih.nci.caarray.domain.project.Project;
import gov.nih.nci.caarray.domain.project.Proposal;
import gov.nih.nci.caarray.domain.sample.Sample;
import gov.nih.nci.caarray.domain.sample.Source;
import gov.nih.nci.caarray.domain.vocabulary.Category;
import gov.nih.nci.caarray.domain.vocabulary.Term;
import gov.nih.nci.caarray.domain.vocabulary.TermSource;
import gov.nih.nci.caarray.util.SecurityInterceptor;
import gov.nih.nci.caarray.util.UsernameHolder;
import gov.nih.nci.caarray.util.j2ee.ServiceLocator;
import gov.nih.nci.caarray.web.delegate.DelegateFactory;
import gov.nih.nci.caarray.web.delegate.ProjectDelegate;
import gov.nih.nci.security.AuthorizationManager;
import gov.nih.nci.security.authorization.domainobjects.User;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.struts2.interceptor.validation.SkipValidation;

import com.opensymphony.xwork2.Preparable;
import com.opensymphony.xwork2.validator.annotations.Validation;

/**
 * ProjectAction.
 *
 * @author John Hedden
 *
 */
@Validation
public class ProjectAction extends BaseAction implements Preparable {
    private static final long serialVersionUID = 1L;

    public static final String LIST_RESULT = "list";
    public static final String WORKSPACE_RESULT = "workspace";
    public static final String DETAILS_RESULT = "details";

    private static final String SAVE_MODE_DRAFT = "save_draft";
    private static final String SAVE_MODE_SUBMIT = "save_submit";

    private String menu = null;
    private Project project = Project.createNew();

    private List<Project> projects = new ArrayList<Project>();
    private List<Organism> organisms = new ArrayList<Organism>();
    private List<Organization> manufacturers = new ArrayList<Organization>();
    private List<PaymentMechanism> paymentMechanisms = new ArrayList<PaymentMechanism>();
    private Map<Organization, List<ArrayDesign>> arrayDesignsByManufacturer;
    private List<Term> tissueSites;
    private List<Term> tissueTypes;
    private List<Term> cellTypes;
    private List<Term> conditions;
    private List<Term> experimentDesignTypes;
    private List<Term> qualityControlTypes;
    private List<Term> replicateTypes;

    private User user;
    private Proposal proposal;
    private String proposalKey;
    private Long proposalId;
    private String saveMode;

    private List<Long> selectedTissueSites = new ArrayList<Long>();
    private List<Long> selectedTissueTypes = new ArrayList<Long>();
    private List<Long> selectedCellTypes = new ArrayList<Long>();
    private List<Long> selectedConditions = new ArrayList<Long>();
    private List<Long> selectedQualityControlTypes = new ArrayList<Long>();
    private List<Long> selectedReplicateTypes = new ArrayList<Long>();
    private List<Long> selectedArrayDesigns = new ArrayList<Long>();

    private boolean piIsMainPoc;
    private Person primaryInvestigator;
    private Person mainPointOfContact;

    private Long selectedManufacturer;
    private Long selectedOrganism;
    private Long selectedExperimentalDesignType;

    private Source currentSource;
    private Integer currentSourceIndex;

    private Sample currentSample;
    private Integer currentSampleIndex;

    private Factor currentFactor;
    private Integer currentFactorIndex;

    /**
     * {@inheritDoc}
     */
    public void prepare() throws Exception {
        if (this.proposalKey != null) {
            this.proposal = (Proposal) getSession().getAttribute(this.proposalKey);
        }

        if (getCurrentSourceIndex() != null) {
            setCurrentSource(this.proposal.getProject().getExperiment().getSources().get(getCurrentSourceIndex()));
        }

        if (getCurrentSampleIndex() != null) {
            setCurrentSample(this.proposal.getProject().getExperiment().getSamples().get(getCurrentSampleIndex()));
        }

        if (getCurrentFactorIndex() != null) {
            setCurrentFactor(this.proposal.getProject().getExperiment().getFactors().get(getCurrentFactorIndex()));
        }

        Organism o1 = new Organism();
        o1.setCommonName("Mouse");
        Organism o2 = new Organism();
        o2.setCommonName("Homo Sapiens");
        this.organisms.add(o1);
        this.organisms.add(o2);
    }

    /**
     * create new project.
     *
     * @return path String
     */
    @SkipValidation
    public String list() {
        setMenu("ProjectListLinks");
        setProjects(getDelegate().getProjectManagementService().getWorkspaceProjects());
        return LIST_RESULT;
    }

    /**
     * Get the entity id
     *
     * @param entity the entity
     * @return the entity id or null if entity is null
     */
    private Long getIdFromEntity(PersistentObject entity) {
        return (entity == null) ? null : entity.getId();
    }

    /**
     * Get the list of ids from a Collection of entities
     *
     * @param entities the Collection of entities
     * @return the List<Long> of the ids of those entities
     */
    private List<Long> getIdsFromEntities(Collection<? extends PersistentObject> entities) {
        List<Long> ids = new ArrayList<Long>();
        for (PersistentObject entity : entities) {
            ids.add(entity.getId());
        }
        return ids;
    }

    /**
     * load a given tab in the submit experiment workflow
     *
     * @return name of result to forward to
     * @throws Exception Exception
     */
    public String overviewLoadTab() throws VocabularyServiceException {
        VocabularyService vocabService = getVocabularyService();
        this.tissueSites = vocabService.getTerms(ExperimentOntologyCategory.ORGANISM_PART.getCategoryName());
        this.tissueTypes = vocabService.getTerms(ExperimentOntologyCategory.MATERIAL_TYPE.getCategoryName());
        this.cellTypes = vocabService.getTerms(ExperimentOntologyCategory.CELL_TYPE.getCategoryName());
        this.conditions = vocabService.getTerms(ExperimentOntologyCategory.DISEASE_STATE.getCategoryName());
        this.organisms = vocabService.getOrganisms();


        this.selectedTissueSites = getIdsFromEntities(this.proposal.getProject().getExperiment().getTissueSites());
        this.selectedTissueTypes= getIdsFromEntities(this.proposal.getProject().getExperiment().getTissueTypes());
        this.selectedCellTypes = getIdsFromEntities(this.proposal.getProject().getExperiment().getCellTypes());
        this.selectedConditions = getIdsFromEntities(this.proposal.getProject().getExperiment().getConditions());
        this.selectedOrganism = getIdFromEntity(this.proposal.getProject().getExperiment().getOrganism());

        ArrayDesignService arrayDesignService = getArrayDesignService();
        this.arrayDesignsByManufacturer = arrayDesignService.getArrayDesignsByOrganization();
        this.selectedArrayDesigns = getIdsFromEntities(this.proposal.getProject().getExperiment().getArrayDesigns());
        this.selectedManufacturer = getIdFromEntity(this.proposal.getProject().getExperiment().getManufacturer());

        this.selectedArrayDesigns = getIdsFromEntities(this.proposal.getProject().getExperiment().getArrayDesigns());
        this.selectedManufacturer = getIdFromEntity(this.proposal.getProject().getExperiment().getManufacturer());

        return loadTab();
    }

    /**
     * load a given tab in the submit experiment workflow
     *
     * @return name of result to forward to
     * @throws Exception Exception
     */
    public String contactsLoadTab() {
        AuthorizationManager am = SecurityInterceptor.getAuthorizationManager();
        String username = UsernameHolder.getUser();
        this.user = am.getUser(username);

        ExperimentContact pi = this.proposal.getProject().getExperiment().getPrimaryInvestigator();
        if (pi != null) {
            this.primaryInvestigator = (Person) pi.getContact();
            if (pi.isMainPointOfContact()) {
                this.piIsMainPoc = true;
                this.mainPointOfContact = new Person();
            } else {
                this.piIsMainPoc = false;
                ExperimentContact mainPoc = this.proposal.getProject().getExperiment().getMainPointOfContact();
                if (mainPoc != null) {
                    this.mainPointOfContact = (Person) mainPoc.getContact();
                } else {
                    this.mainPointOfContact = new Person();
                }
            }
        } else {
            this.primaryInvestigator = new Person();
            this.primaryInvestigator.setFirstName(this.user.getFirstName());
            this.primaryInvestigator.setLastName(this.user.getLastName());
            this.primaryInvestigator.setEmail(this.user.getEmailId());
            this.piIsMainPoc = true;
            this.mainPointOfContact = new Person();
        }
        return loadTab();
    }

    /**
     * load a given tab in the submit experiment workflow
     *
     * @return name of result to forward to
     * @throws Exception Exception
     */
    public String experimentalDesignLoadTab() throws VocabularyServiceException {
        VocabularyService vocabService = getVocabularyService();
        this.experimentDesignTypes = vocabService.getTerms(ExperimentOntologyCategory.EXPERIMENT_DESIGN_TYPE.getCategoryName());
        this.qualityControlTypes = vocabService.getTerms(ExperimentOntologyCategory.QUALITY_CONTROL_TYPE.getCategoryName());
        this.replicateTypes = vocabService.getTerms(ExperimentOntologyCategory.REPLICATE_TYPE.getCategoryName());

        this.selectedQualityControlTypes = getIdsFromEntities(this.proposal.getProject().getExperiment().getQualityControlTypes());
        this.selectedReplicateTypes = getIdsFromEntities(this.proposal.getProject().getExperiment().getReplicateTypes());
        this.selectedExperimentalDesignType = getIdFromEntity(this.proposal.getProject().getExperiment().getExperimentDesignType());

        return loadTab();
    }

    /**
     * Loads the manage data tab.
     *
     * @return name of result to forward to
     */
    public String manageDataLoadTab() {
        return loadTab();
    }

    /**
     * Loads the download data tab.
     *
     * @return name of result to forward to
     */
    public String downloadDataLoadTab() {
        return loadTab();
    }

    /**
     * load a given tab in the submit experiment workflow
     *
     * @return name of result to forward to
     * @throws Exception Exception
     */
    public String loadTab() {
        return INPUT;
    }

    /**
     * save a project.
     *
     * @return path String
     * @throws Exception Exception
     */
    public String overviewSaveTab() {
        this.proposal.getProject().getExperiment().setOrganism(getOrganismFromId(this.selectedOrganism));
        this.proposal.getProject().getExperiment().getTissueTypes().clear();
        this.proposal.getProject().getExperiment().getTissueTypes().addAll(getTermsFromIds(this.selectedTissueTypes));
        this.proposal.getProject().getExperiment().getTissueSites().clear();
        this.proposal.getProject().getExperiment().getTissueSites().addAll(getTermsFromIds(this.selectedTissueSites));
        this.proposal.getProject().getExperiment().getCellTypes().clear();
        this.proposal.getProject().getExperiment().getCellTypes().addAll(getTermsFromIds(this.selectedCellTypes));
        this.proposal.getProject().getExperiment().getConditions().clear();
        this.proposal.getProject().getExperiment().getConditions().addAll(getTermsFromIds(this.selectedConditions));

        this.proposal.getProject().getExperiment().setManufacturer(getOrganizationFromId(this.selectedManufacturer));
        this.proposal.getProject().getExperiment().getArrayDesigns().clear();
        this.proposal.getProject().getExperiment().getArrayDesigns().addAll(getArrayDesignsFromIds(this.selectedArrayDesigns));


        return saveTab();
    }

    /**
     * save a project.
     *
     * @return path String
     * @throws Exception Exception
     */
    @SuppressWarnings("PMD")
    public String contactsSaveTab() {
        VocabularyService vocabService = getVocabularyService();
        TermSource mged = vocabService.getSource(ExperimentOntology.MGED.getOntologyName());
        Category roleCat = vocabService.getCategory(mged, ExperimentOntologyCategory.ROLES.getCategoryName());
        Term piRole = vocabService.getTerm(mged, roleCat, ExperimentContact.PI_ROLE);
        Term mainPocRole = vocabService.getTerm(mged, roleCat, ExperimentContact.MAIN_POC_ROLE);

        ExperimentContact pi = this.proposal.getProject().getExperiment().getPrimaryInvestigator();
        if (pi != null) {
            try {
                PropertyUtils.copyProperties(pi.getContact(), this.primaryInvestigator);
            } catch (IllegalAccessException e) {
                // cannot happen
            } catch (InvocationTargetException e) {
                // cannot happen
            } catch (NoSuchMethodException e) {
                // cannot happen
            }
            if (this.piIsMainPoc && !pi.isMainPointOfContact()) {
                this.proposal.getProject().getExperiment().removeSeparateMainPointOfContact();
                pi.getRoles().add(mainPocRole);
            }
            if (!this.piIsMainPoc) {
                ExperimentContact mainPoc = this.proposal.getProject().getExperiment().getMainPointOfContact();
                if (pi.isMainPointOfContact()) {
                    pi.removeMainPointOfContactRole();
                    mainPoc = new ExperimentContact();
                    mainPoc.getRoles().add(mainPocRole);
                    mainPoc.setContact(new Person());
                    this.proposal.getProject().getExperiment().getExperimentContacts().add(mainPoc);
                    mainPoc.setExperiment(this.proposal.getProject().getExperiment());
                }
                try {
                    PropertyUtils.copyProperties(mainPoc.getContact(), this.mainPointOfContact);
                } catch (IllegalAccessException e) {
                    // cannot happen
                } catch (InvocationTargetException e) {
                    // cannot happen
                } catch (NoSuchMethodException e) {
                    // cannot happen
                }
            }
        } else {
            pi = new ExperimentContact();
            pi.setContact(this.primaryInvestigator);
            pi.getRoles().add(piRole);
            this.proposal.getProject().getExperiment().getExperimentContacts().add(pi);
            pi.setExperiment(this.proposal.getProject().getExperiment());
            if (this.piIsMainPoc) {
                pi.getRoles().add(mainPocRole);
            } else {
                ExperimentContact mainPoc = new ExperimentContact();
                mainPoc.setContact(this.mainPointOfContact);
                mainPoc.getRoles().add(mainPocRole);
                this.proposal.getProject().getExperiment().getExperimentContacts().add(mainPoc);
                mainPoc.setExperiment(this.proposal.getProject().getExperiment());
            }
        }


        return saveTab();
    }

    /**
     * save a project.
     *
     * @return path String
     * @throws Exception Exception
     */
    public String experimentalDesignSaveTab() {
        this.proposal.getProject().getExperiment().getQualityControlTypes().clear();
        this.proposal.getProject().getExperiment().getQualityControlTypes().addAll(getTermsFromIds(this.selectedQualityControlTypes));
        this.proposal.getProject().getExperiment().getReplicateTypes().clear();
        this.proposal.getProject().getExperiment().getReplicateTypes().addAll(getTermsFromIds(this.selectedReplicateTypes));
        this.proposal.getProject().getExperiment().setExperimentDesignType(getTermFromId(this.selectedExperimentalDesignType));
        return saveTab();
    }

    /**
     * Get the Term with given id
     *
     * @param id the id of Term to lookup (could be null)
     * @return the Term with that id or null if id is null or Term could not be found
     */
    private Term getTermFromId(Long id) {
        VocabularyService vocabService = getVocabularyService();
        return (id == null) ? null : vocabService.getTerm(id);
    }

    /**
     * Get the Organism with given id
     *
     * @param id the id of Organism to lookup (could be null)
     * @return the Organism with that id or null if id is null or Organism could not be found
     */
    private Organism getOrganismFromId(Long id) {
        VocabularyService vocabService = getVocabularyService();
        return (id == null) ? null : vocabService.getOrganism(id);
    }

    /**
     * Get the Organization with given id
     *
     * @param id the id of Organizations to lookup (could be null)
     * @return the Organization with that id or null if id is null or Organization could not be found
     */
    private Organization getOrganizationFromId(Long id) {
        ProjectManagementService projectService = getDelegate().getProjectManagementService();
        return (id == null) ? null : projectService.getOrganization(id);
    }

    /**
     * Get the list of Terms corresponding to a List of Term ids
     *
     * @param ids the ids of Terms to lookup
     * @return the List<Terms> with those ids
     */
    private Set<Term> getTermsFromIds(List<Long> ids) {
        VocabularyService vocabService = getVocabularyService();
        Set<Term> terms = new HashSet<Term>();
        for (Long id : ids) {
            if (id == null) {
                continue;
            }
            Term term = vocabService.getTerm(id);
            terms.add(term);
        }
        return terms;
    }

    /**
     * Get the list of ArrayDesigns corresponding to a List of ArrayDesign ids
     *
     * @param ids the ids of ArrayDesigns to lookup
     * @return the List<ArrayDesigns> with those ids
     */
    private Set<ArrayDesign> getArrayDesignsFromIds(List<Long> ids) {
        ArrayDesignService arrayDesignService = getArrayDesignService();
        Set<ArrayDesign> arrayDesigns = new HashSet<ArrayDesign>();
        for (Long id : ids) {
            if (id == null) {
                continue;
            }
            ArrayDesign arrayDesign = arrayDesignService.getArrayDesign(id);
            arrayDesigns.add(arrayDesign);
        }
        return arrayDesigns;
    }

    /**
     * save a project.
     *
     * @return path String
     * @throws Exception Exception
     */
    public String saveTab() {
        String result = SUCCESS;
        if (SAVE_MODE_DRAFT.equals(this.saveMode) || SAVE_MODE_SUBMIT.equals(this.saveMode)) {
            try {
                if (SAVE_MODE_DRAFT.equals(this.saveMode)) {
                    getDelegate().getProjectManagementService().saveDraftProposal((getProposal()));
                    List<String> args = new ArrayList<String>();
                    args.add(getProposal().getProject().getExperiment().getTitle());
                    saveMessage(getText("project.saved", args));
                } else {
                    getDelegate().getProjectManagementService().submitProposal((getProposal()));
                    List<String> args = new ArrayList<String>();
                    args.add(getProposal().getProject().getExperiment().getTitle());
                    saveMessage(getText("project.submitted", args));
                    getSession().removeAttribute(this.proposalKey);
                    result = WORKSPACE_RESULT;
                }
            } catch (ProposalWorkflowException e) {
                List<String> args = new ArrayList<String>();
                args.add(e.getMessage());
                saveMessage(getText("project.workflowProblem", args));
            }
        } else {
            saveMessage(getText("project.updated"));
        }
        return result;
    }

    /**
     * create new proposal
     *
     * @return path String
     */
    @SkipValidation
    public String create() {
        setMenu("ProjectCreateLinks");
        this.proposal = Proposal.createNew();
        this.proposalKey = createSessionKey();
        getSession().setAttribute(this.proposalKey, this.proposal);
        return INPUT;
    }

    /**
     * edit existing proposal
     *
     * @return path String
     */
    @SkipValidation
    public String edit() {
        setMenu("ProjectEditLinks");
        this.proposal = getDelegate().getProjectManagementService().getProposal(this.proposalId);
        this.proposalKey = createSessionKey();
        getSession().setAttribute(this.proposalKey, this.proposal);
        return INPUT;
    }

    /**
     * show details for a proposal
     *
     * @return path String
     */
    @SkipValidation
    public String details() {
        setMenu("ProjectEditLinks");
        setProject(getDelegate().getProjectManagementService().getProject(getProject().getId()));
        return DETAILS_RESULT;
    }

    /**
     * cancel the editing of a proposal
     *
     * @return path String
     */
    @SkipValidation
    public String cancel() {
        getSession().removeAttribute(this.proposalKey);
        return WORKSPACE_RESULT;
    }

    /**
     * Toggles the browsability status.
     *
     * @return success
     */
    @SkipValidation
    public String toggle() {
        setMenu("ProjectEditLinks");
        getDelegate().getProjectManagementService().toggleBrowsableStatus(getProject().getId());
        return SUCCESS;
    }

    /**
     * Saves the source editing tab.
     * @return the string indicating the result to use.
     */
    public String sourceEditSaveTab() {
        if (getCurrentSource() != null && getCurrentSourceIndex() == null) {
            getProposal().getProject().getExperiment().getSources().add(getCurrentSource());
            saveMessage(getText("experiment.sources.created"));
        } else {
            saveMessage(getText("experiment.sources.updated"));
        }
        saveTab();
        return "sourcesTab";
    }

    /**
     * remove the source with the given id from the set of sources.
     * @return the string indicating which result to forward to.
     */
    @SkipValidation
    public String sourceRemoval() {
        if (getCurrentSourceIndex() != null) {
            getProposal().getProject().getExperiment().getSources().remove(getCurrentSourceIndex().intValue());
            saveMessage(getText("experiment.sources.deleted"));
        }
        return SUCCESS;
    }

    /**
     * Copy the soucre to a new object and forward to the edit screen.
     * @return the string indicating the result to forward to.
     */
    @SkipValidation
    public String sourceCopy() {
        if (getCurrentSource() != null) {
            Source source = new Source();
            source.setDescription(getCurrentSource().getDescription());
            source.setMaterialType(getCurrentSource().getMaterialType());
            source.setName(getText("experiment.sources.copy.of") + " " +getCurrentSource().getName());
            source.setOrganism(getCurrentSource().getOrganism());
            setCurrentSource(source);
            setCurrentSourceIndex(null);
            saveMessage(getText("experiment.sources.copied"));
        }
        return SUCCESS;
    }

    /**
     * Saves the samples editing tab.
     * @return the string indicating the result to use.
     */
    public String sampleEditSaveTab() {
        if (getCurrentSample() != null && getCurrentSampleIndex() == null) {
            getProposal().getProject().getExperiment().getSamples().add(getCurrentSample());
            saveMessage(getText("experiment.samples.created"));
        } else {
            saveMessage(getText("experiment.samples.updated"));
        }
        saveTab();
        return "samplesTab";
    }

    /**
     * remove the sample with the given id from the set of samples.
     * @return the string indicating which result to forward to.
     */
    @SkipValidation
    public String sampleRemoval() {
        if (getCurrentSampleIndex() != null) {
            getProposal().getProject().getExperiment().getSamples().remove(getCurrentSampleIndex().intValue());
            saveMessage(getText("experiment.samples.deleted"));
        }
        return SUCCESS;
    }

    /**
     * Copy the sample to a new object and forward to the edit screen.
     * @return the string indicating the result to forward to.
     */
    @SkipValidation
    public String sampleCopy() {
        if (getCurrentSample() != null) {
            Sample sample = new Sample();
            sample.setDescription(getCurrentSample().getDescription());
            sample.setMaterialType(getCurrentSample().getMaterialType());
            sample.setName(getText("experiment.samples.copy.of") + " " + getCurrentSample().getName());
            sample.setOrganism(getCurrentSample().getOrganism());
            sample.setSpecimen(getCurrentSample().getSpecimen());
            setCurrentSample(sample);
            setCurrentSampleIndex(null);
            saveMessage(getText("experiment.samples.copied"));
        }
        return SUCCESS;
    }

    /**
     * Saves the experimentalFactor editing tab.
     * @return the string indicating the result to use.
     */
    public String factorEditSaveTab() {
        if (getCurrentFactor() != null && getCurrentFactorIndex() == null) {
            getProposal().getProject().getExperiment().getFactors().add(getCurrentFactor());
            getCurrentFactor().setExperiment(getProposal().getProject().getExperiment());
            saveMessage(getText("experiment.factors.created"));
        } else {
            saveMessage(getText("experiment.factors.updated"));
        }
        saveTab();
        return "factorsTab";
    }

    /**
     * remove the factor with the given id from the set of factors.
     * @return the string indicating which result to forward to.
     */
    @SkipValidation
    public String factorRemoval() {
        if (getCurrentFactorIndex() != null) {
            getProposal().getProject().getExperiment().getFactors().remove(getCurrentFactorIndex().intValue());
            saveMessage(getText("experiment.factors.deleted"));
        }
        return SUCCESS;
    }

    /**
     * Copy the factor to a new object and forward to the edit screen.
     * @return the string indicating the result to forward to.
     */
    @SkipValidation
    public String factorCopy() {
        if (getCurrentFactor() != null) {
            Factor factor = new Factor();
            factor.setName(getText("experiment.factors.copy.of") + " " + getCurrentFactor().getName());
            factor.setExperiment(getCurrentFactor().getExperiment());
            factor.setType(getCurrentFactor().getType());
            setCurrentFactor(factor);
            setCurrentFactorIndex(null);
            saveMessage(getText("experiment.factors.copied"));
        }
        return SUCCESS;
    }

    /**
     * gets the delegate from factory.
     *
     * @return Delegate ProjectDelegate
     */
    public ProjectDelegate getDelegate() {
        return (ProjectDelegate) DelegateFactory.getDelegate(DelegateFactory.PROJECT);
    }

    /**
     * @return the projects
     */
    public List<Project> getProjects() {
        return this.projects;
    }

    /**
     * @param projects the projects to set
     */
    public void setProjects(List<Project> projects) {
        this.projects = projects;
    }

    /**
     * @return the proposal
     */
    public Proposal getProposal() {
        return this.proposal;
    }

    /**
     * @param proposal the proposal to set
     */
    public void setProposal(Proposal proposal) {
        this.proposal = proposal;
    }

    /**
     * @return the menu
     */
    public String getMenu() {
        return this.menu;
    }

    /**
     * @param menu the menu to set
     */
    public void setMenu(String menu) {
        this.menu = menu;
    }

    /**
     * @return the project
     */
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
     * @return the VocabularyService
     */
    private VocabularyService getVocabularyService() {
        return (VocabularyService) ServiceLocator.INSTANCE.lookup(VocabularyService.JNDI_NAME);
    }

    /**
     * @return the VocabularyService
     */
    private ArrayDesignService getArrayDesignService() {
        return (ArrayDesignService) ServiceLocator.INSTANCE.lookup(ArrayDesignService.JNDI_NAME);
    }

    private String createSessionKey() {
        return "experiment" + System.currentTimeMillis();
    }

    /**
     * @return the organisms
     */
    public List<Organism> getOrganisms() {
        return this.organisms;
    }

    /**
     * @param organisms the organisms to set
     */
    public void setOrganisms(List<Organism> organisms) {
        this.organisms = organisms;
    }

    /**
     * @return the tissueSites
     */
    public List<Term> getTissueSites() {
        return this.tissueSites;
    }

    /**
     * @param tissueSites the tissueSites to set
     */
    public void setTissueSites(List<Term> tissueSites) {
        this.tissueSites = tissueSites;
    }

    /**
     * @return the tissueTypes
     */
    public List<Term> getTissueTypes() {
        return this.tissueTypes;
    }

    /**
     * @param tissueTypes the tissueTypes to set
     */
    public void setTissueTypes(List<Term> tissueTypes) {
        this.tissueTypes = tissueTypes;
    }

    /**
     * @return the cellTypes
     */
    public List<Term> getCellTypes() {
        return this.cellTypes;
    }

    /**
     * @param cellTypes the cellTypes to set
     */
    public void setCellTypes(List<Term> cellTypes) {
        this.cellTypes = cellTypes;
    }

    /**
     * @return the conditions
     */
    public List<Term> getConditions() {
        return this.conditions;
    }

    /**
     * @param conditions the conditions to set
     */
    public void setConditions(List<Term> conditions) {
        this.conditions = conditions;
    }

    /**
     * @return the manufacturers
     */
    public List<Organization> getManufacturers() {
        return this.manufacturers;
    }

    /**
     * @param manufacturers the manufacturers to set
     */
    public void setManufacturers(List<Organization> manufacturers) {
        this.manufacturers = manufacturers;
    }

    /**
     * @return the paymentMechanisms
     */
    public List<PaymentMechanism> getPaymentMechanisms() {
        return this.paymentMechanisms;
    }

    /**
     * @param paymentMechanisms the paymentMechanisms to set
     */
    public void setPaymentMechanisms(List<PaymentMechanism> paymentMechanisms) {
        this.paymentMechanisms = paymentMechanisms;
    }

    /**
     * @return the experimentDesignTypes
     */
    public List<Term> getExperimentDesignTypes() {
        return this.experimentDesignTypes;
    }

    /**
     * @param experimentDesignTypes the experimentDesignTypes to set
     */
    public void setExperimentDesignTypes(List<Term> experimentDesignTypes) {
        this.experimentDesignTypes = experimentDesignTypes;
    }

    /**
     * @return the qualityControlTypes
     */
    public List<Term> getQualityControlTypes() {
        return this.qualityControlTypes;
    }

    /**
     * @param qualityControlTypes the qualityControlTypes to set
     */
    public void setQualityControlTypes(List<Term> qualityControlTypes) {
        this.qualityControlTypes = qualityControlTypes;
    }

    /**
     * @return the replicateTypes
     */
    public List<Term> getReplicateTypes() {
        return this.replicateTypes;
    }

    /**
     * @param replicateTypes the replicateTypes to set
     */
    public void setReplicateTypes(List<Term> replicateTypes) {
        this.replicateTypes = replicateTypes;
    }

    /**
     * @return the user
     */
    public User getUser() {
        return this.user;
    }

    /**
     * @param user the user to set
     */
    public void setUser(User user) {
        this.user = user;
    }

    /**
     * @return the proposalKey
     */
    public String getProposalKey() {
        return this.proposalKey;
    }

    /**
     * @param proposalKey the proposalKey to set
     */
    public void setProposalKey(String proposalKey) {
        this.proposalKey = proposalKey;
    }

    /**
     * @return the saveMode
     */
    public String getSaveMode() {
        return this.saveMode;
    }

    /**
     * @param saveMode the saveMode to set
     */
    public void setSaveMode(String saveMode) {
        this.saveMode = saveMode;
    }

    /**
     * @return the selectedTissueSites
     */
    public List<Long> getSelectedTissueSites() {
        return this.selectedTissueSites;
    }

    /**
     * @param selectedTissueSites the selectedTissueSites to set
     */
    public void setSelectedTissueSites(List<Long> selectedTissueSites) {
        this.selectedTissueSites = selectedTissueSites;
    }

    /**
     * @return the selectedTissueTypes
     */
    public List<Long> getSelectedTissueTypes() {
        return this.selectedTissueTypes;
    }

    /**
     * @param selectedTissueTypes the selectedTissueTypes to set
     */
    public void setSelectedTissueTypes(List<Long> selectedTissueTypes) {
        this.selectedTissueTypes = selectedTissueTypes;
    }

    /**
     * @return the selectedCellTypes
     */
    public List<Long> getSelectedCellTypes() {
        return this.selectedCellTypes;
    }

    /**
     * @param selectedCellTypes the selectedCellTypes to set
     */
    public void setSelectedCellTypes(List<Long> selectedCellTypes) {
        this.selectedCellTypes = selectedCellTypes;
    }

    /**
     * @return the selectedConditions
     */
    public List<Long> getSelectedConditions() {
        return this.selectedConditions;
    }

    /**
     * @param selectedConditions the selectedConditions to set
     */
    public void setSelectedConditions(List<Long> selectedConditions) {
        this.selectedConditions = selectedConditions;
    }

    /**
     * @return the selectedQualityControlTypes
     */
    public List<Long> getSelectedQualityControlTypes() {
        return this.selectedQualityControlTypes;
    }

    /**
     * @param selectedQualityControlTypes the selectedQualityControlTypes to set
     */
    public void setSelectedQualityControlTypes(List<Long> selectedQualityControlTypes) {
        this.selectedQualityControlTypes = selectedQualityControlTypes;
    }

    /**
     * @return the selectedReplicateTypes
     */
    public List<Long> getSelectedReplicateTypes() {
        return this.selectedReplicateTypes;
    }

    /**
     * @param selectedReplicateTypes the selectedReplicateTypes to set
     */
    public void setSelectedReplicateTypes(List<Long> selectedReplicateTypes) {
        this.selectedReplicateTypes = selectedReplicateTypes;
    }

    /**
     * @return the arrayDesignsByManufacturer
     */
    public Map<Organization, List<ArrayDesign>> getArrayDesignsByManufacturer() {
        return this.arrayDesignsByManufacturer;
    }

    /**
     * @param arrayDesignsByManufacturer the arrayDesignsByManufacturer to set
     */
    public void setArrayDesignsByManufacturer(Map<Organization, List<ArrayDesign>> arrayDesignsByManufacturer) {
        this.arrayDesignsByManufacturer = arrayDesignsByManufacturer;
    }

    /**
     * @return the selectedArrayDesigns
     */
    public List<Long> getSelectedArrayDesigns() {
        return this.selectedArrayDesigns;
    }

    /**
     * @param selectedArrayDesigns the selectedArrayDesigns to set
     */
    public void setSelectedArrayDesigns(List<Long> selectedArrayDesigns) {
        this.selectedArrayDesigns = selectedArrayDesigns;
    }

    /**
     * @return the selectedManufacturer
     */
    public Long getSelectedManufacturer() {
        return this.selectedManufacturer;
    }

    /**
     * @param selectedManufacturer the selectedManufacturer to set
     */
    public void setSelectedManufacturer(Long selectedManufacturer) {
        this.selectedManufacturer = selectedManufacturer;
    }

    /**
     * @return the selectedOrganism
     */
    public Long getSelectedOrganism() {
        return this.selectedOrganism;
    }

    /**
     * @param selectedOrganism the selectedOrganism to set
     */
    public void setSelectedOrganism(Long selectedOrganism) {
        this.selectedOrganism = selectedOrganism;
    }

    /**
     * @return the selectedExperimentalDesignType
     */
    public Long getSelectedExperimentalDesignType() {
        return this.selectedExperimentalDesignType;
    }

    /**
     * @param selectedExperimentalDesignType the selectedExperimentalDesignType to set
     */
    public void setSelectedExperimentalDesignType(Long selectedExperimentalDesignType) {
        this.selectedExperimentalDesignType = selectedExperimentalDesignType;
    }

    /**
     * @return the proposalId
     */
    public Long getProposalId() {
        return this.proposalId;
    }

    /**
     * @param proposalId the proposalId to set
     */
    public void setProposalId(Long proposalId) {
        this.proposalId = proposalId;
    }

    /**
     * @return the currentSource
     */
    public Source getCurrentSource() {
        return this.currentSource;
    }

    /**
     * @param currentSource the currentSource to set
     */
    public void setCurrentSource(Source currentSource) {
        this.currentSource = currentSource;
    }

    /**
     * @return the currentSourceIndex
     */
    public Integer getCurrentSourceIndex() {
        return this.currentSourceIndex;
    }

    /**
     * @param currentSourceIndex the currentSourceIndex to set
     */
    public void setCurrentSourceIndex(Integer currentSourceIndex) {
        this.currentSourceIndex = currentSourceIndex;
    }

    /**
     * @return the currentSample
     */
    public Sample getCurrentSample() {
        return this.currentSample;
    }

    /**
     * @param currentSample the currentSample to set
     */
    public void setCurrentSample(Sample currentSample) {
        this.currentSample = currentSample;
    }

    /**
     * @return the currentSampleIndex
     */
    public Integer getCurrentSampleIndex() {
        return this.currentSampleIndex;
    }

    /**
     * @param currentSampleIndex the currentSampleIndex to set
     */
    public void setCurrentSampleIndex(Integer currentSampleIndex) {
        this.currentSampleIndex = currentSampleIndex;
    }

    /**
     * @return the currentFactor
     */
    public Factor getCurrentFactor() {
        return this.currentFactor;
    }

    /**
     * @param currentFactor the currentFactor to set
     */
    public void setCurrentFactor(Factor currentFactor) {
        this.currentFactor = currentFactor;
    }

    /**
     * @return the currentFactorIndex
     */
    public Integer getCurrentFactorIndex() {
        return this.currentFactorIndex;
    }

    /**
     * @param currentFactorIndex the currentFactorIndex to set
     */
    public void setCurrentFactorIndex(Integer currentFactorIndex) {
        this.currentFactorIndex = currentFactorIndex;
    }

    /**
     * @return the piIsMainPoc
     */
    public boolean isPiIsMainPoc() {
        return piIsMainPoc;
    }

    /**
     * @param piIsMainPoc the piIsMainPoc to set
     */
    public void setPiIsMainPoc(boolean piIsMainPoc) {
        this.piIsMainPoc = piIsMainPoc;
    }

    /**
     * @return the primaryInvestigator
     */
    public Person getPrimaryInvestigator() {
        return primaryInvestigator;
    }

    /**
     * @param primaryInvestigator the primaryInvestigator to set
     */
    public void setPrimaryInvestigator(Person primaryInvestigator) {
        this.primaryInvestigator = primaryInvestigator;
    }

    /**
     * @return the mainPointOfContact
     */
    public Person getMainPointOfContact() {
        return mainPointOfContact;
    }

    /**
     * @param mainPointOfContact the mainPointOfContact to set
     */
    public void setMainPointOfContact(Person mainPointOfContact) {
        this.mainPointOfContact = mainPointOfContact;
    }
}
