//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package gov.nih.nci.caarray.web.action.project;

import edu.georgetown.pir.Organism;
import gov.nih.nci.caarray.application.ServiceLocatorFactory;
import gov.nih.nci.caarray.application.arraydesign.ArrayDesignService;
import gov.nih.nci.caarray.application.vocabulary.VocabularyService;
import gov.nih.nci.caarray.domain.array.ArrayDesign;
import gov.nih.nci.caarray.domain.contact.Organization;
import gov.nih.nci.caarray.domain.project.AssayType;
import gov.nih.nci.caarray.domain.vocabulary.Term;
import gov.nih.nci.caarray.util.LabelValue;
import gov.nih.nci.caarray.util.CaArrayUsernameHolder;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.ajaxtags.xml.AjaxXmlBuilder;
import org.apache.struts2.interceptor.validation.SkipValidation;

import com.opensymphony.xwork2.validator.annotations.FieldExpressionValidator;
import com.opensymphony.xwork2.validator.annotations.Validations;

/**
 * Action implementing the overview tab.
 * @author Dan Kokotov
 */
public class ProjectOverviewAction extends ProjectTabAction {
    private static final long serialVersionUID = 1L;

    private Long manufacturerId;
    private Set<AssayType> assayTypeValues;

    private List<Organism> organisms = new ArrayList<Organism>();
    private List<Organization> manufacturers = new ArrayList<Organization>();
    private List<ArrayDesign> arrayDesigns = new ArrayList<ArrayDesign>();
    private List<Term> tissueSites;
    private List<Term> materialTypes;
    private List<Term> cellTypes;
    private List<Term> diseaseState;
    private Set<AssayType> assayTypes;

    /**
     * {@inheritDoc}
     */
    @Override
    public void prepare() {
        super.prepare();
        VocabularyService vocabService = ServiceLocatorFactory.getVocabularyService();
        this.organisms = vocabService.getOrganisms();

        ArrayDesignService arrayDesignService = ServiceLocatorFactory.getArrayDesignService();
        this.manufacturers = arrayDesignService.getAllProviders();
        prepareArrayDesigns();
    }
    /**
     * Method sets array designs to the list provided by the array design service
     * if either assay types or the provider is set.
     */
    private void prepareArrayDesigns() {
        boolean containsAssayTypes = true;
        if (getExperiment().getAssayTypes() == null  || getExperiment().getAssayTypes().isEmpty()
                || getExperiment().getAssayTypes().toArray()[0] == null) {
            containsAssayTypes = false;
        }
        if (containsAssayTypes) {
            this.arrayDesigns =  ServiceLocatorFactory.getArrayDesignService().getImportedArrayDesigns(
                    getExperiment().getManufacturer(), getExperiment().getAssayTypes());
        } else if (getExperiment().getManufacturer() != null) {
            //Struts puts a null into the list if no assay types are selected.  Pass
            //null instead of a list with a null value
            this.arrayDesigns =  ServiceLocatorFactory.getArrayDesignService().getImportedArrayDesigns(
                    getExperiment().getManufacturer(), null);
        } else {
            this.arrayDesigns.clear();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SkipValidation
    public String load() {
        if (isEditMode() && !getProject().hasWritePermission(CaArrayUsernameHolder.getCsmUser())) {
            setEditMode(false);
        }
        if (!isEditMode() && getExperiment() != null && getExperiment().getId() != null) {
            setTissueSites(ServiceLocatorFactory.getProjectManagementService().getTissueSitesForExperiment(
                    getExperiment()));
            setCellTypes(ServiceLocatorFactory.getProjectManagementService().getCellTypesForExperiment(
                    getExperiment()));
            setDiseaseState(ServiceLocatorFactory.getProjectManagementService().getDiseaseStatesForExperiment(
                    getExperiment()));
            setMaterialTypes(ServiceLocatorFactory.getProjectManagementService().getMaterialTypesForExperiment(
                    getExperiment()));
        }
        return super.load();
    }

    /**
     * {@inheritDoc}
     */
    /**
     * save a project.
     *
     * @return path String
     */
    @Override
    @Validations(
            fieldExpressions = {
                @FieldExpressionValidator(fieldName = "project.experiment.assayTypes",
                    message = "You must select at least one assay type or provider.",
                    expression = "project.experiment.manufacturer != null ||"
                            + " !project.experiment.assayTypes.isEmpty")
            }
        )
    @SuppressWarnings("PMD.UselessOverridingMethod")
    public String save() {
        prepareArrayDesigns();
        return super.save();
    }

    /**
     * @return xmlArrayDesigns
     */
    @SkipValidation
    public String retrieveArrayDesigns() {
        if (this.assayTypeValues != null || this.manufacturerId != null) {
            Organization provider = null;
            if (this.manufacturerId != null) {
                provider = ServiceLocatorFactory.getGenericDataService().getPersistentObject(Organization.class,
                        this.manufacturerId);
            }
            this.arrayDesigns = ServiceLocatorFactory.getArrayDesignService().getImportedArrayDesigns(provider,
                    this.assayTypeValues);
        } else {
            this.arrayDesigns.clear();
        }
        return "xmlArrayDesigns";
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
     * @return the manufacturerId
     */
    public Long getManufacturerId() {
        return this.manufacturerId;
    }

    /**
     * @param manufacturerId the manufacturerId to set
     */
    public void setManufacturerId(Long manufacturerId) {
        this.manufacturerId = manufacturerId;
    }

    /**
     * @return the assayTypeValue
     */
    public Set <AssayType>getAssayTypeValues() {
        return this.assayTypeValues;
    }

    /**
     * @param assayTypeValues the assayTypes to set
     */
    public void setAssayTypeValues(Set <AssayType>assayTypeValues) {
        this.assayTypeValues = assayTypeValues;
    }

    /**
     * @return the arrayDesigns
     */
    public List<ArrayDesign> getArrayDesigns() {
        return this.arrayDesigns;
    }

    /**
     * @param arrayDesigns the arrayDesigns to set
     */
    public void setArrayDesigns(List<ArrayDesign> arrayDesigns) {
        this.arrayDesigns = arrayDesigns;
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
     * @return the materialTypes
     */
    public List<Term> getMaterialTypes() {
        return this.materialTypes;
    }

    /**
     * @param materialTypes the materialTypes to set
     */
    public void setMaterialTypes(List<Term> materialTypes) {
        this.materialTypes = materialTypes;
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
     * @return the diseaseState
     */
    public List<Term> getDiseaseState() {
        return this.diseaseState;
    }

    /**
     * @param diseaseState the diseaseState to set
     */
    public void setDiseaseState(List<Term> diseaseState) {
        this.diseaseState = diseaseState;
    }
    /**
     * Action is used for an ajax call by the list generator.
     *
     * @return the string indicating which result to follow.
     */
    @SkipValidation
    public String generateAssayList() {
        setAssayTypes(new TreeSet<AssayType>(ServiceLocatorFactory.getProjectManagementService().getAssayTypes()));
        return "generateAssayList";
    }
    /**
     * @return the assayTypes
     */
    public Set<AssayType> getAssayTypes() {
        return assayTypes;
    }

    /**
     * @param assayTypes the assayTypes to set
     */
    public void setAssayTypes(Set<AssayType> assayTypes) {
        this.assayTypes = assayTypes;
    }

    /**
     * Get the set of retrieved array designs encoded in XML for use by AjaxTags.
     * @return the stream containing the XML encoding the set of array designs
     * @throws IllegalAccessException on error
     * @throws NoSuchMethodException on error
     * @throws InvocationTargetException on error
     * @throws UnsupportedEncodingException on error
     */
    public InputStream getArrayDesignsAsXml() throws IllegalAccessException, NoSuchMethodException,
        InvocationTargetException, UnsupportedEncodingException {
        AjaxXmlBuilder xmlBuilder = new AjaxXmlBuilder();
        if (this.arrayDesigns.isEmpty()) {
            xmlBuilder.addItems(Arrays.asList(new LabelValue(getText("experiment.overview.noArrayDesigns"), "")),
                    "label", "value");
        } else {
            xmlBuilder.addItems(this.arrayDesigns, "name", "id");
        }
        return new ByteArrayInputStream(xmlBuilder.toString().getBytes("UTF-8"));
    }
}
