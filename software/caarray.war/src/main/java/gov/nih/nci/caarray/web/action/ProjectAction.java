package gov.nih.nci.caarray.web.action;

import edu.georgetown.pir.Organism;
import gov.nih.nci.caarray.application.arraydesign.ArrayDesignService;
import gov.nih.nci.caarray.business.vocabulary.VocabularyService;
import gov.nih.nci.caarray.domain.PersistentObject;
import gov.nih.nci.caarray.domain.array.ArrayDesign;
import gov.nih.nci.caarray.domain.contact.Organization;
import gov.nih.nci.caarray.domain.project.ExperimentOntologyCategory;
import gov.nih.nci.caarray.domain.project.PaymentMechanism;
import gov.nih.nci.caarray.domain.project.Project;
import gov.nih.nci.caarray.domain.project.Proposal;
import gov.nih.nci.caarray.domain.project.ProposalStatus;
import gov.nih.nci.caarray.domain.sample.Sample;
import gov.nih.nci.caarray.domain.sample.Source;
import gov.nih.nci.caarray.domain.vocabulary.Term;
import gov.nih.nci.caarray.util.UsernameHolder;
import gov.nih.nci.caarray.util.j2ee.ServiceLocator;
import gov.nih.nci.caarray.web.delegate.DelegateFactory;
import gov.nih.nci.caarray.web.delegate.ProjectDelegate;
import gov.nih.nci.security.AuthorizationManager;
import gov.nih.nci.security.SecurityServiceProvider;
import gov.nih.nci.security.authorization.domainobjects.User;
import gov.nih.nci.security.exceptions.CSConfigurationException;
import gov.nih.nci.security.exceptions.CSException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.apache.struts2.interceptor.validation.SkipValidation;

import com.opensymphony.xwork2.Preparable;
import com.opensymphony.xwork2.validator.annotations.Validation;

/**
 * ProjectAction.
 * @author John Hedden
 *
 */
@Validation
public class ProjectAction extends BaseAction implements Preparable {
    private static final long serialVersionUID = 1L;
    
    public static final String LIST_RESULT = "list";
    public static final String WORKSPACE_RESULT = "workspace";
    public static final String DETAILS_RESULT = "details";

    private static final String SAVE_MODE_SESSION = "save_session";
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
    private String saveMode;

    private List<Source> sources = new ArrayList<Source>();
    private List<Sample> samples = new ArrayList<Sample>();
    
    private List<Long> selectedTissueSites = new ArrayList<Long>();
    private List<Long> selectedTissueTypes = new ArrayList<Long>();
    private List<Long> selectedCellTypes = new ArrayList<Long>();
    private List<Long> selectedConditions = new ArrayList<Long>();
    private List<Long> selectedQualityControlTypes = new ArrayList<Long>();
    private List<Long> selectedReplicateTypes = new ArrayList<Long>();
    private List<Long> selectedArrayDesigns = new ArrayList<Long>();
    private Long selectedManufacturer;
    private Long selectedOrganism;
    private Long selectedExperimentalDesignType;


    /**
     * {@inheritDoc}
     */
    public void prepare() throws Exception {        
        if (proposalKey != null) {
            proposal = (Proposal) getSession().getAttribute(proposalKey);
        }
        
        Organism o1 = new Organism();
        o1.setCommonName("Mouse");
        Organism o2 = new Organism();
        o2.setCommonName("Homo Sapiens");
        organisms.add(o1);
        organisms.add(o2);
        
        Source s1 = new Source();
        s1.setName("mouse-11111");
        s1.setDescription("White Mouse");
        s1.setOrganism(o1);
        Source s2 = new Source();
        s2.setName("homo-99999");
        s2.setDescription("Purple Man");
        s2.setOrganism(o2);
        sources.add(s1);
        sources.add(s2);        
        
        Sample sm1 = new Sample();
        sm1.setName("G96-1");
        sm1.setDescription("Tumor sample");
        sm1.setOrganism(o1);
        Sample sm2 = new Sample();
        sm2.setName("G96-2");
        sm2.setDescription("Tumor sample");
        sm2.setOrganism(o1);
        samples.add(sm1);
        samples.add(sm2);                
    }
    
    /**
     * create new project.
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
     * @param entity the entity
     * @return the entity id or null if entity is null
     */
    private Long getIdFromEntity(PersistentObject entity) {
        return (entity == null) ? null : entity.getId();
    }

    
    /**
     * Get the list of ids from a Collection of entities
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
     * @return name of result to forward to
     * @throws Exception Exception
     */
    @SuppressWarnings("PMD")
    public String overviewLoadTab() throws Exception {
        VocabularyService vocabService = getVocabularyService();
        tissueSites = vocabService.getTerms(ExperimentOntologyCategory.ORGANISM_PART.getCategoryName());
        tissueTypes = vocabService.getTerms(ExperimentOntologyCategory.MATERIAL_TYPE.getCategoryName());
        cellTypes = vocabService.getTerms(ExperimentOntologyCategory.CELL_TYPE.getCategoryName());
        conditions = vocabService.getTerms(ExperimentOntologyCategory.DISEASE_STATE.getCategoryName());      

        selectedTissueSites = getIdsFromEntities(proposal.getProject().getExperiment().getTissueSites());
        selectedTissueTypes= getIdsFromEntities(proposal.getProject().getExperiment().getTissueTypes());
        selectedCellTypes = getIdsFromEntities(proposal.getProject().getExperiment().getCellTypes());
        selectedConditions = getIdsFromEntities(proposal.getProject().getExperiment().getConditions());
        selectedOrganism = getIdFromEntity(proposal.getProject().getExperiment().getOrganism());
        
        ArrayDesignService arrayDesignService = getArrayDesignService();
        arrayDesignsByManufacturer = arrayDesignService.getArrayDesignsByOrganization();
        
        selectedArrayDesigns = getIdsFromEntities(proposal.getProject().getExperiment().getArrayDesigns());
        selectedManufacturer = getIdFromEntity(proposal.getProject().getExperiment().getManufacturer());

        return loadTab();
    }

    /**
     * load a given tab in the submit experiment workflow
     * @return name of result to forward to
     * @throws Exception Exception
     */
    @SuppressWarnings("PMD")
    public String contactsLoadTab() throws Exception {
        AuthorizationManager am = getAuthorizationManager();
        String username = UsernameHolder.getUser();
        user = am.getUser(username);

        return loadTab();
    }

    /**
     * load a given tab in the submit experiment workflow
     * @return name of result to forward to
     * @throws Exception Exception
     */
    @SuppressWarnings("PMD")
    public String experimentalDesignLoadTab() throws Exception {
        VocabularyService vocabService = getVocabularyService();
        experimentDesignTypes = vocabService.getTerms(ExperimentOntologyCategory.EXPERIMENT_DESIGN_TYPE.getCategoryName());        
        qualityControlTypes = vocabService.getTerms(ExperimentOntologyCategory.QUALITY_CONTROL_TYPE.getCategoryName());        
        replicateTypes = vocabService.getTerms(ExperimentOntologyCategory.REPLICATE_TYPE.getCategoryName());        

        selectedQualityControlTypes = getIdsFromEntities(proposal.getProject().getExperiment().getQualityControlTypes());
        selectedReplicateTypes = getIdsFromEntities(proposal.getProject().getExperiment().getReplicateTypes());
        selectedExperimentalDesignType = getIdFromEntity(proposal.getProject().getExperiment().getExperimentDesignType());
        
        return loadTab();
    }

    /**
     * load a given tab in the submit experiment workflow
     * @return name of result to forward to
     * @throws Exception Exception
     */
    @SuppressWarnings("PMD")
    public String loadTab() throws Exception {
        return INPUT;
    }

    /**
     * save a project.
     * @return path String
     * @throws Exception Exception
     */
    @SuppressWarnings("PMD")
    public String overviewSaveTab() {
        proposal.getProject().getExperiment().getTissueTypes().clear();
        proposal.getProject().getExperiment().getTissueTypes().addAll(getTermsFromIds(selectedTissueTypes));
        proposal.getProject().getExperiment().getTissueSites().clear();
        proposal.getProject().getExperiment().getTissueSites().addAll(getTermsFromIds(selectedTissueSites));
        proposal.getProject().getExperiment().getCellTypes().clear();
        proposal.getProject().getExperiment().getCellTypes().addAll(getTermsFromIds(selectedCellTypes));
        proposal.getProject().getExperiment().getConditions().clear();
        proposal.getProject().getExperiment().getConditions().addAll(getTermsFromIds(selectedConditions));
        
        proposal.getProject().getExperiment().getArrayDesigns().clear();
        proposal.getProject().getExperiment().getArrayDesigns().addAll(getArrayDesignsFromIds(selectedArrayDesigns));
        
               
        return saveTab();
    }

    /**
     * save a project.
     * @return path String
     * @throws Exception Exception
     */
    @SuppressWarnings("PMD")
    public String experimentalDesignSaveTab() {
        proposal.getProject().getExperiment().getQualityControlTypes().clear();
        proposal.getProject().getExperiment().getQualityControlTypes().addAll(getTermsFromIds(selectedQualityControlTypes));
        proposal.getProject().getExperiment().getReplicateTypes().clear();
        proposal.getProject().getExperiment().getReplicateTypes().addAll(getTermsFromIds(selectedReplicateTypes));
        proposal.getProject().getExperiment().setExperimentDesignType(getTermFromId(selectedExperimentalDesignType));
        return saveTab();
    }

    /**
     * Get the Term with given id 
     * @param id the id of Terms to lookup (could be null)
     * @return the Term with that id or null if id is null or Term could not be found
     */
    private Term getTermFromId(Long id) {
        VocabularyService vocabService = getVocabularyService();
        return (id == null) ? null : vocabService.getTerm(id);
    }

    /**
     * Get the list of Terms corresponding to a List of Term ids
     * @param ids the ids of Terms to lookup
     * @return the List<Terms> with those ids
     */
    private Set<Term> getTermsFromIds(List<Long> ids) {
        VocabularyService vocabService = getVocabularyService();
        Set<Term> terms = new HashSet<Term>();
        for (Long id : ids) {
            Term term = vocabService.getTerm(id);
            terms.add(term);
        }
        return terms;
    }

    /**
     * Get the list of ArrayDesigns corresponding to a List of ArrayDesign ids
     * @param ids the ids of ArrayDesigns to lookup
     * @return the List<ArrayDesigns> with those ids
     */
    private Set<ArrayDesign> getArrayDesignsFromIds(List<Long> ids) {
        ArrayDesignService arrayDesignService = getArrayDesignService();
        Set<ArrayDesign> arrayDesigns = new HashSet<ArrayDesign>();
        for (Long id : ids) {
            ArrayDesign arrayDesign = arrayDesignService.getArrayDesign(id);
            arrayDesigns.add(arrayDesign);
        }
        return arrayDesigns;
    }

    /**
     * save a project.
     * @return path String
     * @throws Exception Exception
     */
    @SuppressWarnings("PMD")
    public String saveTab() {
        System.out.println("save mode: " + saveMode);
        System.out.println("proposal being submitted (sop): " + getProposal());

        if (SAVE_MODE_DRAFT.equals(saveMode) || SAVE_MODE_SUBMIT.equals(saveMode)) {
            getProposal().setStatus(SAVE_MODE_DRAFT.equals(saveMode) ? ProposalStatus.DRAFT : ProposalStatus.SUBMITTED_FOR_REVIEW);
            getDelegate().getProjectManagementService().submitProposal(getProposal());            

            List<String> args = new ArrayList<String>();
            args.add(getProposal().getProject().getExperiment().getTitle());
            saveMessage(getText("project.created", args));
        }
        
        saveMessage(getText("project.updated"));
        return SUCCESS;
    }


    /**
     * create new project.
     * @return path String
     */
    @SkipValidation
    public String create() {
        setMenu("ProjectCreateLinks");
        proposal = Proposal.createNew();
        proposalKey = createSessionKey();
        getSession().setAttribute(proposalKey, proposal);
        return INPUT;
    }

    /**
     * save a project.
     * @return path String
     */
    public String save() {
        setMenu("ProjectSaveLinks");
        getDelegate().getProjectManagementService().submitProposal(getProposal());
        List<String> args = new ArrayList<String>();
        args.add(getProposal().getProject().getExperiment().getTitle());
        saveMessage(getText("project.created", args));

        return WORKSPACE_RESULT;
    }

    /**
     * edit files associated with a project.
     * @return path String
     */
    @SkipValidation
    public String details() {
        setMenu("ProjectEditLinks");
        setProject(getDelegate().getProjectManagementService().getProject(getProject().getId()));
        return DETAILS_RESULT;
    }

    /**
     * Toggles the browsability status.
     * @return success
     */
    @SkipValidation
    public String toggle() {
        setMenu("ProjectEditLinks");
        getDelegate().getProjectManagementService().toggleBrowsableStatus(getProject().getId());
        return SUCCESS;
    }

    /**
     * gets the delegate from factory.
     * @return Delegate ProjectDelegate
     */
    public ProjectDelegate getDelegate() {
        return (ProjectDelegate) DelegateFactory.getDelegate(DelegateFactory.PROJECT);
    }

    /**
     * @return the projects
     */
    public List<Project> getProjects() {
        return projects;
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
        return proposal;
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
        return menu;
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
        return project;
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

    /**
     * @return the CSM Authorization Manager
     */
    private AuthorizationManager getAuthorizationManager() {
        AuthorizationManager am = null;
        try {
            am = SecurityServiceProvider.getAuthorizationManager("caarray");
        } catch (CSConfigurationException e) {
            LOG.error("Unable to initialize CSM: " + e.getMessage(), e);
        } catch (CSException e) {
            LOG.error("Unable to initialize CSM: " + e.getMessage(), e);
        }
        return am;
    }
    
    private String createSessionKey() {
        return "experiment" + new Random().nextInt();
    }

    /**
     * @return the organisms
     */
    public List<Organism> getOrganisms() {
        return organisms;
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
        return tissueSites;
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
        return tissueTypes;
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
        return cellTypes;
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
        return conditions;
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
        return manufacturers;
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
        return paymentMechanisms;
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
        return experimentDesignTypes;
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
        return qualityControlTypes;
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
        return replicateTypes;
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
        return user;
    }

    /**
     * @param user the user to set
     */
    public void setUser(User user) {
        this.user = user;
    }

    /**
     * @return the sources
     */
    public List<Source> getSources() {
        return sources;
    }

    /**
     * @param sources the sources to set
     */
    public void setSources(List<Source> sources) {
        this.sources = sources;
    }

    /**
     * @return the samples
     */
    public List<Sample> getSamples() {
        return samples;
    }

    /**
     * @param samples the samples to set
     */
    public void setSamples(List<Sample> samples) {
        this.samples = samples;
    }

    /**
     * @return the proposalKey
     */
    public String getProposalKey() {
        return proposalKey;
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
        return saveMode;
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
        return selectedTissueSites;
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
        return selectedTissueTypes;
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
        return selectedCellTypes;
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
        return selectedConditions;
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
        return selectedQualityControlTypes;
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
        return selectedReplicateTypes;
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
        return arrayDesignsByManufacturer;
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
        return selectedArrayDesigns;
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
        return selectedManufacturer;
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
        return selectedOrganism;
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
        return selectedExperimentalDesignType;
    }

    /**
     * @param selectedExperimentalDesignType the selectedExperimentalDesignType to set
     */
    public void setSelectedExperimentalDesignType(Long selectedExperimentalDesignType) {
        this.selectedExperimentalDesignType = selectedExperimentalDesignType;
    }
}
