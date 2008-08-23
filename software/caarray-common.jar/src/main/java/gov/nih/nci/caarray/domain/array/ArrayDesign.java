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

package gov.nih.nci.caarray.domain.array;

import edu.georgetown.pir.Organism;
import gov.nih.nci.caarray.domain.AbstractCaArrayEntity;
import gov.nih.nci.caarray.domain.contact.Organization;
import gov.nih.nci.caarray.domain.file.CaArrayFile;
import gov.nih.nci.caarray.domain.file.CaArrayFileSet;
import gov.nih.nci.caarray.domain.project.AssayType;
import gov.nih.nci.caarray.domain.protocol.ProtocolApplication;
import gov.nih.nci.caarray.domain.vocabulary.Term;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.validator.Length;
import org.hibernate.validator.NotNull;
import org.hibernate.validator.Size;

/**
 * The design details for a type of microarray.
 */
@Entity
@Table(uniqueConstraints = { @UniqueConstraint(columnNames = { "name", "provider" }) })
@SuppressWarnings("PMD.TooManyFields")
public class ArrayDesign extends AbstractCaArrayEntity {

    private static final long serialVersionUID = 1234567890L;

    private String name;
    private String description;
    private String assayType;
    private ProtocolApplication printing;
    private Term polymerType;
    private Integer numberOfFeatures;
    private Term substrateType;
    private Term surfaceType;
    private Term technologyType;
    private Organism organism;
    private String version;
    private CaArrayFile annotationFile;
    private Set<CaArrayFile> designFiles = new HashSet<CaArrayFile>();
    private Organization provider;
    private ArrayDesignDetails designDetails;

    /**
     * Default constructor.
     */
    public ArrayDesign() {
        // nothing to do here
    }

    /**
     * Copy constructor.  The newly created array design will be a shallow copy of the given design.
     * @param arrayDesign existing array design on which to base the new array design.
     */
    public ArrayDesign(ArrayDesign arrayDesign) {
        this.setLsidForEntity(arrayDesign.getLsid());

        this.name = arrayDesign.name;
        this.description = arrayDesign.description;
        this.assayType = arrayDesign.assayType;
        this.printing = arrayDesign.printing;
        this.polymerType = arrayDesign.polymerType;
        this.numberOfFeatures = arrayDesign.numberOfFeatures;
        this.substrateType = arrayDesign.substrateType;
        this.surfaceType = arrayDesign.surfaceType;
        this.technologyType = arrayDesign.technologyType;
        this.organism = arrayDesign.organism;
        this.version = arrayDesign.version;
        this.annotationFile = arrayDesign.annotationFile;
        this.designFiles.addAll(arrayDesign.designFiles);
        this.provider = arrayDesign.provider;
        this.designDetails = arrayDesign.designDetails;
    }

    /**
     * Gets the name.
     *
     * @return the name
     */
    @NotNull
    @Length(min = 1, max = DEFAULT_STRING_COLUMN_SIZE)
    public String getName() {
        return name;
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
     * Gets the numberOfFeatures.
     *
     * @return the numberOfFeatures
     */
    public Integer getNumberOfFeatures() {
        return numberOfFeatures;
    }

    /**
     * Sets the numberOfFeatures.
     *
     * @param numberOfFeaturesVal the numberOfFeatures
     */
    public void setNumberOfFeatures(final Integer numberOfFeaturesVal) {
        this.numberOfFeatures = numberOfFeaturesVal;
    }
    /**
     * Gets the polymerType.
     *
     * @return the polymerType
     */
    @ManyToOne
    @Cascade(CascadeType.SAVE_UPDATE)
    @ForeignKey(name = "arraydesign_polymer_fk")
    public Term getPolymerType() {
        return polymerType;
    }

    /**
     * Sets the polymerType.
     *
     * @param polymerTypeVal the polymerType
     */
    public void setPolymerType(final Term polymerTypeVal) {
        this.polymerType = polymerTypeVal;
    }
    /**
     * Gets the substrateType.
     *
     * @return the substrateType
     */
    @ManyToOne
    @Cascade(CascadeType.SAVE_UPDATE)
    @ForeignKey(name = "arraydesign_substrae_fk")
    public Term getSubstrateType() {
        return substrateType;
    }

    /**
     * Sets the substrateType.
     *
     * @param substrateTypeVal the substrateType
     */
    public void setSubstrateType(final Term substrateTypeVal) {
        this.substrateType = substrateTypeVal;
    }
    /**
     * Gets the surfaceType.
     *
     * @return the surfaceType
     */
    @ManyToOne
    @Cascade(CascadeType.SAVE_UPDATE)
    @ForeignKey(name = "arraydesign_surface_fk")
    public Term getSurfaceType() {
        return surfaceType;
    }

    /**
     * Sets the surfaceType.
     *
     * @param surfaceTypeVal the surfaceType
     */
    public void setSurfaceType(final Term surfaceTypeVal) {
        this.surfaceType = surfaceTypeVal;
    }
    /**
     * Gets the technologyType.
     *
     * @return the technologyType
     */
    @NotNull
    @ManyToOne
    @Cascade(CascadeType.SAVE_UPDATE)
    @ForeignKey(name = "arraydesign_technology_fk")
    public Term getTechnologyType() {
        return technologyType;
    }

    /**
     * Sets the technologyType.
     *
     * @param technologyTypeVal the technologyType
     */
    public void setTechnologyType(final Term technologyTypeVal) {
        this.technologyType = technologyTypeVal;
    }
    /**
     * Gets the version.
     *
     * @return the version
     */
    @NotNull
    @Column(length = DEFAULT_STRING_COLUMN_SIZE)
    public String getVersion() {
        return version;
    }

    /**
     * Sets the version.
     *
     * @param versionVal the version
     */
    public void setVersion(final String versionVal) {
        this.version = versionVal;
    }

    /**
     * Gets the provider.
     *
     * @return the provider
     */
    @NotNull
    @ManyToOne
    @Cascade(CascadeType.SAVE_UPDATE)
    @ForeignKey(name = "arraydesign_provider_fk")
    public Organization getProvider() {
        return provider;
    }

    /**
     * Sets the provider.
     *
     * @param providerVal the provider
     */
    public void setProvider(final Organization providerVal) {
        this.provider = providerVal;
    }

    /**
     * Gets the printing.
     *
     * @return the printing
     */
    @ManyToOne
    @Cascade(org.hibernate.annotations.CascadeType.SAVE_UPDATE)
    @ForeignKey(name = "arraydesign_printing_fk")
    public ProtocolApplication getPrinting() {
        return printing;
    }

    /**
     * Sets the printing.
     *
     * @param printingVal the printing
     */
    public void setPrinting(final ProtocolApplication printingVal) {
        this.printing = printingVal;
    }

    /**
     * @return the designFiles
     */
    @NotNull
    @Size(min = 1)
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "array_design_design_file",
               joinColumns = @JoinColumn(name = "array_design"),
               inverseJoinColumns = @JoinColumn(name = "design_file"))
    @ForeignKey(name = "array_design_fk",
                inverseName = "design_file_fk")
    @Cascade(org.hibernate.annotations.CascadeType.ALL)
    public Set<CaArrayFile> getDesignFiles() {
        return designFiles;
    }

    /**
     * @return the design files as a set.
     */
    @Transient
    public CaArrayFileSet getDesignFileSet() {
        CaArrayFileSet fileSet = new CaArrayFileSet();
        fileSet.addAll(this.designFiles);
        return fileSet;
    }

    /**
     * Set the design files of this array design to the ones contained in the given set.
     * @param designFileSet files to set
     */
    public void setDesignFileSet(CaArrayFileSet designFileSet) {
        this.designFiles.clear();
        this.designFiles.addAll(designFileSet.getFiles());
    }

    /**
     * @param designFiles the designFiles to set
     */
    @SuppressWarnings({"PMD.UnusedPrivateMethod", "unused" })
    private void setDesignFiles(Set<CaArrayFile> designFiles) {
        this.designFiles = designFiles;
    }

    /**
     * Add a new design file to this design.
     * @param designFile design file to add
     */
    public void addDesignFile(CaArrayFile designFile) {
        this.designFiles.add(designFile);
    }

    /**
     * Gets the first (or only) design file.
     * @return the design file
     */
    @Transient
    public CaArrayFile getFirstDesignFile() {
        if (designFiles.isEmpty()) {
            return null;
        } else {
            return designFiles.iterator().next();
        }
    }

    /**
     * @param annotationFile the annotationFile to set
     */
    public void setAnnotationFile(CaArrayFile annotationFile) {
        this.annotationFile = annotationFile;
    }

    /**
     * @return the annotationFile
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @Cascade(org.hibernate.annotations.CascadeType.SAVE_UPDATE)
    @ForeignKey(name = "annotation_file_fk")
    public CaArrayFile getAnnotationFile() {
        return annotationFile;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return name;
    }

    /**
     * @return the type
     */
    @NotNull
    @Column(length = DEFAULT_STRING_COLUMN_SIZE)
    public String getAssayType() {
        return assayType;
    }

    /**
     * @param assayType the assayType to set
     */
    public void setAssayType(String assayType) {
        AssayType.checkType(assayType);
        this.assayType = assayType;
    }

    /**
     * @return the assayType enum
     */
    @Transient
    public AssayType getAssayTypeEnum() {
        return AssayType.getByValue(getAssayType());
    }

    /**
     * @param assayTypeEnum the assayTypeEnum to set
     */
    public void setAssayTypeEnum(AssayType assayTypeEnum) {
        if (assayTypeEnum == null) {
            setAssayType(null);
        } else {
            setAssayType(assayTypeEnum.getValue());
        }
    }

    /**
     * @return the organism
     */
    @NotNull
    @ManyToOne
    @Cascade(CascadeType.SAVE_UPDATE)
    @ForeignKey(name = "arraydesign_organism_fk")
    public Organism getOrganism() {
        return organism;
    }

    /**
     * @param organism the organism to set
     */
    public void setOrganism(Organism organism) {
        this.organism = organism;
    }

    /**
     * @return the designDetails
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @Cascade(CascadeType.ALL)
    @ForeignKey(name = "arraydesign_details_fk")
    public ArrayDesignDetails getDesignDetails() {
        return designDetails;
    }

    /**
     * @param designDetails the designDetails to set
     */
    public void setDesignDetails(ArrayDesignDetails designDetails) {
        this.designDetails = designDetails;
    }

    /**
     * @return the description
     */
    @Length(max = LARGE_TEXT_FIELD_LENGTH)
    public String getDescription() {
        return description;
    }

    /**
     * @param description the description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }
}
