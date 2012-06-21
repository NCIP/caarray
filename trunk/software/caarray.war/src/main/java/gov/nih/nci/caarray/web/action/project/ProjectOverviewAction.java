/**
 * The software subject to this notice and license includes both human readable
 * source code form and machine readable, binary, object code form. The caarray-war
 * Software was developed in conjunction with the National Cancer Institute
 * (NCI) by NCI employees and 5AM Solutions, Inc. (5AM). To the extent
 * government employees are authors, any rights in such works shall be subject
 * to Title 17 of the United States Code, section 105.
 *
 * This caarray-war Software License (the License) is between NCI and You. You (or
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
 * its rights in the caarray-war Software to (i) use, install, access, operate,
 * execute, copy, modify, translate, market, publicly display, publicly perform,
 * and prepare derivative works of the caarray-war Software; (ii) distribute and
 * have distributed to and by third parties the caarray-war Software and any
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
