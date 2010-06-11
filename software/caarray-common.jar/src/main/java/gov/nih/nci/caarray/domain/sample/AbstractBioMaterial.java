/**
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

package gov.nih.nci.caarray.domain.sample;

import edu.georgetown.pir.Organism;
import gov.nih.nci.caarray.domain.AbstractCaArrayObject;
import gov.nih.nci.caarray.domain.file.CaArrayFile;
import gov.nih.nci.caarray.domain.hybridization.Hybridization;
import gov.nih.nci.caarray.domain.project.AbstractExperimentDesignNode;
import gov.nih.nci.caarray.domain.project.Experiment;
import gov.nih.nci.caarray.domain.project.ExperimentDesignNodeType;
import gov.nih.nci.caarray.domain.project.ExperimentOntologyCategory;
import gov.nih.nci.caarray.domain.protocol.ProtocolApplication;
import gov.nih.nci.caarray.domain.vocabulary.Category;
import gov.nih.nci.caarray.domain.vocabulary.Term;
import gov.nih.nci.caarray.validation.UniqueConstraint;
import gov.nih.nci.caarray.validation.UniqueConstraintField;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Index;
import org.hibernate.annotations.IndexColumn;
import org.hibernate.validator.Length;
import org.hibernate.validator.NotNull;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterators;

/**
 * AbstractBiomaterial represents a biomaterial at some stage prior to being hybridized to an array.
 * @author dkokotov 
 */
@Entity
@Table(name = "biomaterial")
@BatchSize(size = AbstractCaArrayObject.DEFAULT_BATCH_SIZE)
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "discriminator", discriminatorType = DiscriminatorType.STRING)
@UniqueConstraint(fields = {
        @UniqueConstraintField(name = "externalId"),
        @UniqueConstraintField(name = "experiment", nullsEqual = false) },
        generateDDLConstraint = false, message = "{biomaterial.externalId.uniqueConstraint}")
public abstract class AbstractBioMaterial extends AbstractExperimentDesignNode {
    private static final long serialVersionUID = 1234567890L;

    private Term tissueSite;
    private Term materialType;
    private Term cellType;
    private Term diseaseState;
    private String name;
    private String description;
    private String externalId;
    private Set<AbstractCharacteristic> characteristics = new HashSet<AbstractCharacteristic>();
    private List<ProtocolApplication> protocolApplications = new ArrayList<ProtocolApplication>();
    private Organism organism;
    private Experiment experiment;

    /**
     * {@inheritDoc}
     */
    @ManyToOne
    @JoinColumn(name = "experiment_id")
    @ForeignKey(name = "biomaterial_experiment_fk")
    public Experiment getExperiment() {
        return experiment;
    }

    /**
     * @param experiment the experiment to set
     */
    public void setExperiment(Experiment experiment) {
        this.experiment = experiment;
    }

    /**
     * @return the tissueSite
     */
    @ManyToOne
    @Cascade(CascadeType.SAVE_UPDATE)
    @ForeignKey(name = "biomaterial_site_fk")
    public Term getTissueSite() {
        return this.tissueSite;
    }

    /**
     * @param tissueSite the tissueSite to set
     */
    public void setTissueSite(Term tissueSite) {
        this.tissueSite = tissueSite;
    }

    /**
     * Gets the materialType.
     *
     * @return the materialType
     */
    @ManyToOne
    @Cascade(CascadeType.SAVE_UPDATE)
    @ForeignKey(name = "biomaterial_mat_type_fk")
    public Term getMaterialType() {
        return this.materialType;
    }

    /**
     * Sets the materialType.
     *
     * @param materialTypeVal the materialType
     */
    public void setMaterialType(final Term materialTypeVal) {
        this.materialType = materialTypeVal;
    }

    /**
     * @return the cellType
     */
    @ManyToOne
    @Cascade(CascadeType.SAVE_UPDATE)
    @ForeignKey(name = "biomaterial_cell_type_fk")
    public Term getCellType() {
        return this.cellType;
    }

    /**
     * @param cellType the cellType to set
     */
    public void setCellType(Term cellType) {
        this.cellType = cellType;
    }

    /**
     * @return the diseaseState
     */
    @ManyToOne
    @Cascade(CascadeType.SAVE_UPDATE)
    @ForeignKey(name = "biomaterial_dis_state_fk")
    public Term getDiseaseState() {
        return this.diseaseState;
    }

    /**
     * @param diseaseState the diseaseState to set
     */
    public void setDiseaseState(Term diseaseState) {
        this.diseaseState = diseaseState;
    }

    /**
     * Gets the name.
     *
     * @return the name
     */
    @NotNull
    @Length(min = 1, max = DEFAULT_STRING_COLUMN_SIZE)
    @Index(name = "idx_name")
    public String getName() {
        return this.name;
    }

    /**
     * Sets the name.
     *
     * @param nameVal the name
     */
    public void setName(final String nameVal) {
        this.name = nameVal;
    }

    /**
     * Gets the description.
     *
     * @return the description
     */
    @Length(max = LARGE_TEXT_FIELD_LENGTH)
    public String getDescription() {
        return this.description;
    }

    /**
     * Sets the description.
     *
     * @param descriptionVal the description
     */
    public void setDescription(final String descriptionVal) {
        this.description = descriptionVal;
    }
    
    /**
     * @return the externalId
     */
    @Length(max = DEFAULT_STRING_COLUMN_SIZE)
    @Index(name = "idx_name")
    public String getExternalId() {
        return this.externalId;
    }

    /**
     * @param externalId the externalId to set
     */
    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }

    /**
     * Gets the characteristics.
     *
     * @return the characteristics
     */
    @OneToMany(mappedBy = "bioMaterial", fetch = FetchType.LAZY)
    @Cascade({CascadeType.SAVE_UPDATE, CascadeType.DELETE })
    public Set<AbstractCharacteristic> getCharacteristics() {
        return this.characteristics;
    }

    /**
     * Sets the characteristics.
     *
     * @param characteristicsVal the characteristics
     */
    @SuppressWarnings({"unused", "PMD.UnusedPrivateMethod" })
    private void setCharacteristics(final Set<AbstractCharacteristic> characteristicsVal) {
        this.characteristics = characteristicsVal;
    }
    
    /**
     * Return the characteristic with given category name in this biomaterial. If multiple characteristics
     * have the same category name, return one at random.
     * If there is none, return null.
     * 
     * @param categoryName name of category for which to find a characteristic.
     * @return the characteristic with given category name or null if there is none.
     */
    public AbstractCharacteristic getCharacteristic(final String categoryName) {
        return Iterators.find(getCharacteristics().iterator(), new Predicate<AbstractCharacteristic>() {
            public boolean apply(AbstractCharacteristic input) {
                return categoryName.equals(input.getCategory().getName());
            }
        });
    }

    /**
     * Return the characteristics with given category in this biomaterial. 
     * 
     * @param category category
     * @return the characteristics with given category.
     */
    public Set<AbstractCharacteristic> getCharacteristics(final Category category) {
        Set<AbstractCharacteristic> chars = new HashSet<AbstractCharacteristic>();
        Iterators.addAll(chars, Iterators.filter(characteristics.iterator(), new Predicate<AbstractCharacteristic>() {
            public boolean apply(AbstractCharacteristic input) {
                return category.equals(input.getCategory());
            }
        }));

        // special handling for the built-in characteristics
        Map<ExperimentOntologyCategory, Term> builtins = getSpecialCharacteristics();        
        for (Map.Entry<ExperimentOntologyCategory, Term> builtin : builtins.entrySet()) {
            if (builtin.getValue() != null && category.getName().equals(builtin.getKey().getCategoryName())) {
                TermBasedCharacteristic c = new TermBasedCharacteristic();
                c.setBioMaterial(this);
                c.setCategory(category);
                c.setTerm(builtin.getValue());
                chars.add(c);
            }
        }
        
        // special handling for the external id
        if (StringUtils.isNotEmpty(this.externalId)
                && category.getName().equals(ExperimentOntologyCategory.EXTERNAL_ID.getCategoryName())) {
            UserDefinedCharacteristic c = new UserDefinedCharacteristic();
            c.setBioMaterial(this);
            c.setCategory(category);
            c.setValue(this.externalId);
            chars.add(c);
        }
        
        return chars;
    }    
    
    /**
     * Returns the characteristics that are handled specially and have their own fields in AbstractBioMaterial or
     * one of its subclasses, rather than being placed in the general characteristics collection. These are returned
     * as a map, with keys being category constants defining the category of the special characteristic, and the 
     * values being the Term values of the characteristic.  
     * For AbstractBioMaterial, this will return a map with four entries:
     * <ul>
     * <li>ExperimentOntologyCategory.DISEASE_STATE -> this.diseaseState
     * <li>ExperimentOntologyCategory.CELL_TYPE -> this.cellType
     * <li>ExperimentOntologyCategory.MATERIAL_TYPE -> this.materialType
     * <li>ExperimentOntologyCategory.ORGANISM_PART -> this.tissueSite
     * </ul>
     * Subclasses can override this method to add additional entries if they have other characteristics that they
     * store directly.
     * @return the map of special characterstics as described above
     */
    @Transient
    protected Map<ExperimentOntologyCategory, Term> getSpecialCharacteristics() {
        Map<ExperimentOntologyCategory, Term> builtins = new HashMap<ExperimentOntologyCategory, Term>();
        builtins.put(ExperimentOntologyCategory.DISEASE_STATE, this.diseaseState);
        builtins.put(ExperimentOntologyCategory.CELL_TYPE, this.cellType);
        builtins.put(ExperimentOntologyCategory.MATERIAL_TYPE, this.materialType);
        builtins.put(ExperimentOntologyCategory.ORGANISM_PART, this.tissueSite);
        return builtins;
    }

    /**
     * {@inheritDoc}
     */
    // should really be one-to-many, but hibernate bug HHH-3160/HHH-1296 prevents reordering or deleting from the list
    // with a unique constraint on protocol_application
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "biomaterial_protocol_application",
            joinColumns = @JoinColumn(name = "bio_material"),
            inverseJoinColumns = @JoinColumn(name = "protocol_application")
    )
    @IndexColumn(name = "protocol_order")
    @ForeignKey(name = "biomaterial_protocol_application_bio_material_fk",
            inverseName = "biomaterial_protocol_application_protocol_application_fk")
    @Cascade({ org.hibernate.annotations.CascadeType.SAVE_UPDATE, org.hibernate.annotations.CascadeType.DELETE,
            org.hibernate.annotations.CascadeType.DELETE_ORPHAN })
    public List<ProtocolApplication> getProtocolApplications() {
        return this.protocolApplications;
    }

    /**
     * Sets the protocolApplications.
     *
     * @param protocolApplicationsVal the protocolApplications
     */
    @SuppressWarnings({"unused", "PMD.UnusedPrivateMethod" })
    private void setProtocolApplications(final List<ProtocolApplication> protocolApplicationsVal) {
        this.protocolApplications = protocolApplicationsVal;
    }

    /**
     * {@inheritDoc}
     */
    public void addProtocolApplication(ProtocolApplication protocolApplication) {
        this.protocolApplications.add(protocolApplication);
    }

    /**
     * {@inheritDoc}
     */
    public void clearProtocolApplications() {
        this.protocolApplications.clear();
    }

    /**
     * @return the organism
     */
    @ManyToOne
    @Cascade(CascadeType.SAVE_UPDATE)
    @ForeignKey(name = "biomaterial_organism_fk")
    public Organism getOrganism() {
        return this.organism;
    }

    /**
     * @param organism the organism to set
     */
    public void setOrganism(Organism organism) {
        this.organism = organism;
    }

    /**
     * @return the set of hybridizations related to this biomaterial (via the biomaterial chain)
     */
    @Transient
    @SuppressWarnings("unchecked")
    public Set<Hybridization> getRelatedHybridizations() {
        return (Set<Hybridization>) getSuccessorsOfType(ExperimentDesignNodeType.HYBRIDIZATION);
    }

    /**
     * {@inheritDoc}
     * The data files related to this biomaterial is the set of files
     * that is related to at least one hybridization that is related to this biomaterial
     * (@see getRelatedHybs()).
     */
    @Transient
    public Set<CaArrayFile> getAllDataFiles() {
        Set<CaArrayFile> files = new HashSet<CaArrayFile>();
        Collection<Hybridization> hybridizations = getRelatedHybridizations();
        if (hybridizations != null && !hybridizations.isEmpty()) {
            for (Hybridization h : hybridizations) {
                files.addAll(h.getAllDataFiles());
            }
        }
        return files;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void merge(AbstractExperimentDesignNode node) {
        AbstractBioMaterial bm = (AbstractBioMaterial) node;
        super.merge(bm);

        mergeBioMaterialTerms(bm);
        if (this.getDescription() == null) {
            this.setDescription(bm.getDescription());
        }
        if (this.getOrganism() == null) {
            this.setOrganism(bm.getOrganism());
        }
        if (this.getExternalId() == null) {
            this.setExternalId(bm.getExternalId());
        }

        mergeCharacteristics(bm);
    }

    private void mergeBioMaterialTerms(AbstractBioMaterial bm) {
        if (this.getTissueSite() == null) {
            this.setTissueSite(bm.getTissueSite());
        }
        if (this.getMaterialType() == null) {
            this.setMaterialType(bm.getMaterialType());
        }
        if (this.getCellType() == null) {
            this.setCellType(bm.getCellType());
        }
        if (this.getDiseaseState() == null) {
            this.setDiseaseState(bm.getDiseaseState());
        }
    }

    private void mergeCharacteristics(AbstractBioMaterial bm) {
        Set<AbstractCharacteristic> newChars = new HashSet<AbstractCharacteristic>();
        for (AbstractCharacteristic characteristic : bm.getCharacteristics()) {
            boolean foundCategory = false;
            for (AbstractCharacteristic currCharacteristic : this.getCharacteristics()) {
                if (characteristic.getCategory().equals(currCharacteristic.getCategory())) {
                    foundCategory = true;
                    break;
                }
            }
            if (!foundCategory) {
                newChars.add(characteristic);
                characteristic.setBioMaterial(this);
            }
        }
        this.getCharacteristics().addAll(newChars);
        bm.getCharacteristics().removeAll(newChars);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return new ToStringBuilder(this)
            .append("name", getName())
            .toString();
    }

}
