//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================

package gov.nih.nci.caarray.domain.array;

import edu.georgetown.pir.Organism;
import gov.nih.nci.caarray.domain.AbstractCaArrayEntity;
import gov.nih.nci.caarray.domain.AbstractCaArrayObject;
import gov.nih.nci.caarray.domain.contact.Organization;
import gov.nih.nci.caarray.domain.file.CaArrayFile;
import gov.nih.nci.caarray.domain.file.CaArrayFileSet;
import gov.nih.nci.caarray.domain.file.FileStatus;
import gov.nih.nci.caarray.domain.project.AssayType;
import gov.nih.nci.caarray.domain.protocol.ProtocolApplication;
import gov.nih.nci.caarray.domain.vocabulary.Term;
import gov.nih.nci.caarray.security.AttributePolicy;
import gov.nih.nci.caarray.security.SecurityPolicy;

import java.util.HashSet;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

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

import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Sort;
import org.hibernate.annotations.SortType;
import org.hibernate.validator.Length;
import org.hibernate.validator.NotNull;
import org.hibernate.validator.Size;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;

/**
 * The design details for a type of microarray.
 */
@Entity
@Table(uniqueConstraints = {@UniqueConstraint(columnNames = {"name", "provider" }) })
@SuppressWarnings("PMD.TooManyFields")
public class ArrayDesign extends AbstractCaArrayEntity {

    private static final long serialVersionUID = 1234567890L;

    private String name;
    private String description;
    private SortedSet<AssayType> assayTypes = new TreeSet<AssayType>();
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
    private String geoAccession;

    /**
     * Default constructor.
     */
    public ArrayDesign() {
        // nothing to do here
    }

    /**
     * Copy constructor. The newly created array design will be a shallow copy of the given design.
     * 
     * @param arrayDesign existing array design on which to base the new array design.
     */
    public ArrayDesign(ArrayDesign arrayDesign) {
        this.setLsidForEntity(arrayDesign.getLsid());

        this.name = arrayDesign.name;
        this.description = arrayDesign.description;
        this.assayTypes.addAll(arrayDesign.assayTypes);
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
     * Gets the numberOfFeatures.
     * 
     * @return the numberOfFeatures
     */
    public Integer getNumberOfFeatures() {
        return this.numberOfFeatures;
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
        return this.polymerType;
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
        return this.substrateType;
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
        return this.surfaceType;
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
        return this.technologyType;
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
        return this.version;
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
        return this.provider;
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
        return this.printing;
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
    @JoinTable(name = "array_design_design_file", joinColumns = @JoinColumn(name = "array_design"),
        inverseJoinColumns = @JoinColumn(name = "design_file"))
    @ForeignKey(name = "array_design_fk", inverseName = "design_file_fk")
    @Cascade(org.hibernate.annotations.CascadeType.ALL)
    public Set<CaArrayFile> getDesignFiles() {
        return this.designFiles;
    }

    /**
     * @return the design files as a set.
     */
    @Transient
    public CaArrayFileSet getDesignFileSet() {
        final CaArrayFileSet fileSet = new CaArrayFileSet();
        fileSet.addAll(this.designFiles);
        return fileSet;
    }

    /**
     * Set the design files of this array design to the ones contained in the given set.
     * 
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
     * 
     * @param designFile design file to add
     */
    public void addDesignFile(CaArrayFile designFile) {
        this.designFiles.add(designFile);
    }

    /**
     * Gets the first (or only) design file.
     * 
     * @return the design file
     */
    @Transient
    public CaArrayFile getFirstDesignFile() {
        if (this.designFiles.isEmpty()) {
            return null;
        } else {
            return this.designFiles.iterator().next();
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
        return this.annotationFile;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return getName();
    }

    /**
     * Gets the assay type for this Experiment.
     * 
     * @return the assay type
     */
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "array_design")
    @ForeignKey(name = "array_design_assaytypes_ad_fk", inverseName = "array_design_assaytypes_at_fk")
    @AttributePolicy(allow = SecurityPolicy.BROWSE_POLICY_NAME)
    @Sort(type = SortType.NATURAL)
    @NotNull
    @Size(min = 1)
    @BatchSize(size = AbstractCaArrayObject.DEFAULT_BATCH_SIZE)
    public SortedSet<AssayType> getAssayTypes() {
        return this.assayTypes;
    }

    /**
     * @param assayTypes the assayTypes to set
     */
    public void setAssayTypes(SortedSet<AssayType> assayTypes) {
        this.assayTypes = assayTypes;
    }

    /**
     * @return the organism
     */
    @NotNull
    @ManyToOne
    @Cascade(CascadeType.SAVE_UPDATE)
    @ForeignKey(name = "arraydesign_organism_fk")
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
     * @return the designDetails
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @Cascade({CascadeType.PERSIST, CascadeType.SAVE_UPDATE, CascadeType.LOCK, CascadeType.EVICT })
    @ForeignKey(name = "arraydesign_details_fk")
    public ArrayDesignDetails getDesignDetails() {
        return this.designDetails;
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
        return this.description;
    }

    /**
     * @param description the description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * GEO accession.
     * 
     * @return the GEO accession.
     */
    @Length(max = DEFAULT_STRING_COLUMN_SIZE)
    public String getGeoAccession() {
        return this.geoAccession;
    }

    /**
     * @param geoAccession GEO accession.
     */
    public void setGeoAccession(String geoAccession) {
        this.geoAccession = geoAccession;
    }

    /**
     * Check whether this is a array design that was previously imported but not parsed, but now can be imported and
     * parsed (due to a new parsing FileHandler being implemented for it, or because it was previously canceled from the
     * job queue). This will be the case if any of the design files associated with the array design meet this
     * condition.
     * 
     * @return true if the design can be re-imported and parsed, false otherwise.
     */
    @Transient
    public boolean isUnparsedAndReimportable() {
        return Iterables.any(getDesignFiles(), new Predicate<CaArrayFile>() {
            @Override
            public boolean apply(CaArrayFile file) {
               return (file.isUnparsedAndReimportable() || file.getFileStatus() == FileStatus.UPLOADED);
            }
        });
    }

    /**
     * Check whether this is a array design that has been imported imported but not parsed. implemented for it). This
     * will be the case if any of the design files associated with the array design meet this condition.
     * 
     * @return true if the design has been imported and parsed, false otherwise.
     */
    @Transient
    public boolean isImportedAndParsed() {
        return Iterables.any(getDesignFiles(), new Predicate<CaArrayFile>() {
            @Override
            public boolean apply(CaArrayFile file) {
                return file.getFileStatus() == FileStatus.IMPORTED;
            }
        });
    }
}
