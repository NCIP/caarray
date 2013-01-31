//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================

package gov.nih.nci.caarray.domain.project;

import edu.georgetown.pir.Organism;
import gov.nih.nci.caarray.domain.AbstractCaArrayEntity;
import gov.nih.nci.caarray.domain.AbstractCaArrayObject;
import gov.nih.nci.caarray.domain.array.Array;
import gov.nih.nci.caarray.domain.array.ArrayDesign;
import gov.nih.nci.caarray.domain.contact.Organization;
import gov.nih.nci.caarray.domain.hybridization.Hybridization;
import gov.nih.nci.caarray.domain.publication.Publication;
import gov.nih.nci.caarray.domain.sample.AbstractBioMaterial;
import gov.nih.nci.caarray.domain.sample.Extract;
import gov.nih.nci.caarray.domain.sample.LabeledExtract;
import gov.nih.nci.caarray.domain.sample.Sample;
import gov.nih.nci.caarray.domain.sample.Source;
import gov.nih.nci.caarray.domain.vocabulary.Term;
import gov.nih.nci.caarray.security.AttributePolicy;
import gov.nih.nci.caarray.security.SecurityPolicy;
import gov.nih.nci.caarray.util.CaarrayInnoDBDialect;
import gov.nih.nci.caarray.validation.UniqueConstraint;
import gov.nih.nci.caarray.validation.UniqueConstraintField;
import gov.nih.nci.security.authorization.domainobjects.User;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
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
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.Filters;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Index;
import org.hibernate.annotations.IndexColumn;
import org.hibernate.annotations.Sort;
import org.hibernate.annotations.SortType;
import org.hibernate.annotations.Where;
import org.hibernate.validator.Length;
import org.hibernate.validator.NotNull;

/**
 *
 */
@Entity
@BatchSize(size = AbstractCaArrayObject.DEFAULT_BATCH_SIZE)
@UniqueConstraint(fields = @UniqueConstraintField(name = "publicIdentifier"))
@SuppressWarnings({"PMD.TooManyFields", "PMD.ExcessiveClassLength",
                   "PMD.ExcessivePublicCount", "PMD.AvoidDuplicateLiterals" })
@FilterDef(name = "BiomaterialFilter")                   
public class Experiment extends AbstractCaArrayEntity {
    // Experiment is central object -- can't reduce set of linked entities

    private static final String FK_COLUMN_NAME = "experiment_id";
    private static final String TERM_FK_NAME = "term_id";
    private static final String EXPERIMENT_REF = "experiment";

    private static final String READABLE_PROJECT_CLAUSE = "(select pe.attribute_value from csm_protection_group pg, "
        + "csm_protection_element pe, csm_pg_pe pgpe, csm_user_group_role_pg ugrpg, csm_user u, csm_role_privilege rp, "
        + "csm_role r, csm_privilege p, csm_user_group ug where "
        + "pe.object_id= 'gov.nih.nci.caarray.domain.project.Project' and pe.attribute='id' and "
        + "u.login_name=:USER_NAME and pe.application_id=:APPLICATION_ID and ugrpg.role_id=r.role_id "
        + "and ugrpg.group_id = ug.group_id and ug.user_id = u.user_id and "
        + "ugrpg.protection_group_id = pg.protection_group_id and pg.protection_group_id = pgpe.protection_group_id "
        + "and pgpe.protection_element_id = pe.protection_element_id and r.role_id = rp.role_id and rp.privilege_id = "
        + "p.privilege_id and p.privilege_name='READ')";

    private static final String PROJECT_OWNER_CLAUSE = "(select " + CaarrayInnoDBDialect.FILTER_ALIAS
        + ".attribute_value from (select pe.attribute_value from csm_user_pe upe, "
        + "csm_protection_element pe, csm_user u "
        + "where pe.object_id= 'gov.nih.nci.caarray.domain.project.Project' and "
        + "pe.attribute='id' and u.login_name=:USER_NAME and pe.application_id=:APPLICATION_ID and "
        + "upe.protection_element_id = pe.protection_element_id and upe.user_id = u.user_id) "
        + CaarrayInnoDBDialect.FILTER_ALIAS + ")";

    private static final String READABLE_SAMPLE_CLAUSE = "(select pe.attribute_value from csm_protection_group pg, "
        + "csm_protection_element pe, csm_pg_pe pgpe, csm_user_group_role_pg ugrpg, csm_user u, csm_role_privilege rp, "
        + "csm_role r, csm_privilege p, csm_user_group ug where "
        + "pe.object_id= 'gov.nih.nci.caarray.domain.sample.Sample' and pe.attribute='id' and "
        + "u.login_name=:USER_NAME and pe.application_id=:APPLICATION_ID and ugrpg.role_id=r.role_id "
        + "and ugrpg.group_id = ug.group_id and ug.user_id = u.user_id and "
        + "ugrpg.protection_group_id = pg.protection_group_id and pg.protection_group_id = pgpe.protection_group_id "
        + "and pgpe.protection_element_id = pe.protection_element_id and r.role_id = rp.role_id and rp.privilege_id = "
        + "p.privilege_id and p.privilege_name='READ')";

    private static final String READABLE_SAMPLE_ALIAS_CLAUSE = "(select " + CaarrayInnoDBDialect.FILTER_ALIAS
        + ".attribute_value from " + READABLE_SAMPLE_CLAUSE + " " + CaarrayInnoDBDialect.FILTER_ALIAS + ")";

    /** @Where filter for biomaterials */
    public static final String BIOMATERIALS_ALIAS_FILTER = "id in (select b.ID from biomaterial b "
        + "left join experiment ex on b.experiment_id = ex.id left join project p on ex.id = p.experiment "
        + "left join sampleextract se on b.id = se.extract_id left join biomaterial b2 on b2.id = se.sample_id "
        + "left join extractlabeledextract ele on b.id = ele.labeledextract_id "
        + "left join sampleextract se2 on ele.extract_id = se2.extract_id "
        + "left join biomaterial b3 on b3.id = se2.sample_id "
        + "where b.discriminator='SO' and p.id in " + READABLE_PROJECT_CLAUSE
        + " or b.discriminator = 'SA' and b.ID in " + READABLE_SAMPLE_ALIAS_CLAUSE
        + " or b2.discriminator = 'SA' and b2.ID in " + READABLE_SAMPLE_ALIAS_CLAUSE
        + " or b3.discriminator = 'SA' and b3.ID in " + READABLE_SAMPLE_ALIAS_CLAUSE + ")";
    /** @Where filter for samples */
    public static final String SAMPLES_FILTER = "id in (select s.ID from biomaterial s where s.discriminator = 'SA' "
        + "and s.ID in " + READABLE_SAMPLE_CLAUSE + ")";
    /** @Where filter for samples - with mysql wrapping table for subselect*/
    public static final String SAMPLES_ALIAS_FILTER = "id in (select s.ID from biomaterial s "
        + "where s.discriminator = 'SA' and s.ID in " + READABLE_SAMPLE_ALIAS_CLAUSE + ")";
    /** @Where filter for extracts */
    public static final String EXTRACTS_FILTER = "id in (select e.ID from biomaterial e inner join sampleextract se "
        + " on e.id = se.extract_id inner join biomaterial s on se.sample_id = s.id where e.discriminator = 'EX' and "
        + "s.ID in " + READABLE_SAMPLE_CLAUSE + ")";
    /** @Where filter for extracts - with mysql wrapping table for subselect */
    public static final String EXTRACTS_ALIAS_FILTER = "id in (select e.ID from biomaterial e "
        + "inner join sampleextract se on e.id = se.extract_id inner join biomaterial s on se.sample_id = s.id where "
        + "e.discriminator = 'EX' and s.ID in " + READABLE_SAMPLE_ALIAS_CLAUSE + ")";
    /** @Where filter for labeled extracts */
    public static final String LABELED_EXTRACTS_FILTER = "id in (select le.ID from biomaterial le inner join "
        + "extractlabeledextract ele on le.id = ele.labeledextract_id inner join sampleextract se on ele.extract_id = "
        + "se.extract_id inner join biomaterial s on se.sample_id = s.id where le.discriminator = 'LA' and s.ID in "
        + READABLE_SAMPLE_CLAUSE + ")";
    /** @Where filter for labeled extracts - with mysql wrapping table for subselect */
    public static final String LABELED_EXTRACTS_ALIAS_FILTER = "id in (select le.ID from biomaterial le inner join "
        + "extractlabeledextract ele on le.id = ele.labeledextract_id inner join sampleextract se on ele.extract_id = "
        + "se.extract_id inner join biomaterial s on se.sample_id = s.id where le.discriminator = 'LA' and s.ID in "
        + READABLE_SAMPLE_ALIAS_CLAUSE + ")";
    /** @Where filter for hybs */
    public static final String HYBRIDIZATIONS_FILTER = "ID in (select h.ID from hybridization h inner join "
        + "labeledextracthybridization leh on h.id = leh.hybridization_id inner join extractlabeledextract ele on "
        + "leh.labeledextract_id = ele.labeledextract_id inner join sampleextract se on ele.extract_id = se.extract_id "
        + "inner join biomaterial s on se.sample_id = s.id where s.ID in " + READABLE_SAMPLE_CLAUSE + ")";
    /** @Where filter for hybs - with mysql wrapping table for subselect */
    public static final String HYBRIDIZATIONS_ALIAS_FILTER = "ID in (select h.ID from hybridization h inner join "
        + "labeledextracthybridization leh on h.id = leh.hybridization_id inner join extractlabeledextract ele on "
        + "leh.labeledextract_id = ele.labeledextract_id inner join sampleextract se on ele.extract_id = se.extract_id "
        + "inner join biomaterial s on se.sample_id = s.id where s.ID in " + READABLE_SAMPLE_ALIAS_CLAUSE + ")";
    /** @Where filter for files */
    public static final String FILES_FILTER = "ID in (select f.id from caarrayfile f left join arraydata ad on f.id = "
        + "ad.data_file left join project p on f.project = p.id left join rawarraydata_hybridizations radh "
        + "on ad.id = radh.rawarraydata_id left join hybridization h on radh.hybridization_id = h.id "
        + "left join derivedarraydata_hybridizations dadh on ad.id = dadh.derivedarraydata_id left join "
        + "hybridization h2 on dadh.hybridization_id = h2.id left join labeledextracthybridization leh on h.id = "
        + "leh.hybridization_id left join extractlabeledextract ele on leh.labeledextract_id = ele.labeledextract_id "
        + "left join sampleextract se on ele.extract_id = se.extract_id left join biomaterial s on se.sample_id = s.id "
        + "left join labeledextracthybridization leh2 on h2.id = leh2.hybridization_id left join extractlabeledextract "
        + "ele2 on leh2.labeledextract_id = ele2.labeledextract_id left join sampleextract se2 on ele2.extract_id = "
        + "se2.extract_id left join biomaterial s2 on se2.sample_id = s2.id where s.id is not null and s.id in "
        + READABLE_SAMPLE_ALIAS_CLAUSE + " or s2.id is not null and s2.id in " + READABLE_SAMPLE_ALIAS_CLAUSE
        + " or (f.status = " + "'SUPPLEMENTAL' or f.status = 'IMPORTED' or f.status='IMPORTED_NOT_PARSED') "
        + "and s.id is null and s2.id is null and p.id in " + READABLE_PROJECT_CLAUSE + " or p.id in "
        + PROJECT_OWNER_CLAUSE + ")";
    private static final long serialVersionUID = 1234567890L;

    private String title;
    private String description;
    private Date date;
    private Date publicReleaseDate;
    private SortedSet<AssayType> assayTypes = new TreeSet<AssayType>();
    private Organization manufacturer;
    private Organism organism;
    private Set<Factor> factors = new HashSet<Factor>();
    private List<ExperimentContact> experimentContacts = new ArrayList<ExperimentContact>();
    private Set<Term> experimentDesignTypes = new HashSet<Term>();
    private String designDescription;
    private String qualityControlDescription;
    private Set<Term> qualityControlTypes = new HashSet<Term>();
    private String replicateDescription;
    private Set<Term> replicateTypes = new HashSet<Term>();
    private Set<Term> normalizationTypes = new HashSet<Term>();
    private Set<Publication> publications = new HashSet<Publication>();
    private Set<ArrayDesign> arrayDesigns = new HashSet<ArrayDesign>();
    private Set<AbstractBioMaterial> biomaterials = new HashSet<AbstractBioMaterial>();
    private Set<Source> sources = new HashSet<Source>();
    private Set<Sample> samples = new HashSet<Sample>();
    private Set<Extract> extracts = new HashSet<Extract>();
    private Set<LabeledExtract> labeledExtracts = new HashSet<LabeledExtract>();
    private Set<Hybridization> hybridizations = new HashSet<Hybridization>();
    private String publicIdentifier;
    private Project project;

    /**
     * {@inheritDoc}
     */
    public Set<SecurityPolicy> getRemoteApiSecurityPolicies(User user) {
        return getProject().getRemoteApiSecurityPolicies(user);
    }

    /**
     * Gets the date of this Experiment.
     *
     * @return the date of this Experiment
     */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "date_of_experiment")
    @AttributePolicy(allow = SecurityPolicy.BROWSE_POLICY_NAME)
    public Date getDate() {
        return this.date;
    }

    /**
     * Sets the date of this Experiment.
     *
     * @param date the date of this Experiment
     */
    public void setDate(final Date date) {
        this.date = date;
    }

    /**
     * Gets the description.
     *
     * @return the description
     */
    @Length(max = LARGE_TEXT_FIELD_LENGTH)
    @AttributePolicy(allow = SecurityPolicy.BROWSE_POLICY_NAME)
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
     * Gets the publicReleaseDate.
     *
     * @return the publicReleaseDate
     */
    @Temporal(TemporalType.TIMESTAMP)
    public Date getPublicReleaseDate() {
        return this.publicReleaseDate;
    }

    /**
     * Sets the publicReleaseDate.
     *
     * @param publicReleaseDateVal the publicReleaseDate
     */
    public void setPublicReleaseDate(final Date publicReleaseDateVal) {
        this.publicReleaseDate = publicReleaseDateVal;
    }

    /**
     * Gets the title.
     *
     * @return the title
     */
    @Length(min = 1, max = DEFAULT_STRING_COLUMN_SIZE)
    @NotNull
    @Index(name = "idx_title")
    @AttributePolicy(allow = SecurityPolicy.BROWSE_POLICY_NAME)
    public String getTitle() {
        return this.title;
    }

    /**
     * Sets the title.
     *
     * @param titleVal the title
     */
    public void setTitle(final String titleVal) {
        this.title = titleVal;
    }

    /**
     * Gets a human readable public identifier for this project. This is an autogenerated identifier that can be used
     * to refer to the project in publications, and would be expected to be a component of a permalink url pointing
     * to the project.
     *
     * The identifier will be of the form xxxxx-11111, where xxxxx is the first 5 characters of the PI last name, and
     * 11111 is a 5-digit auto number based on the persistent identifier of the project
     *
     * Note that this public identifier is available only once the project has been made persistent (ie its id is not
     * null). Before that, this will return null
     *
     * @return the public identifier if this project is persistent, null otherwise
     */
    @Column(length = DEFAULT_STRING_COLUMN_SIZE)
    @Index(name = "idx_public_identifier")
    @AttributePolicy(allow = SecurityPolicy.BROWSE_POLICY_NAME)
    public String getPublicIdentifier() {
        return this.publicIdentifier;
    }

    /**
     * @param publicId the publicId to set. should only be used by hibernate and struts
     */
    public void setPublicIdentifier(String publicId) {
        this.publicIdentifier = publicId;
    }

    /**
     * Gets the ExperimentContact corresponding to the PI for the experiment.
     *
     * @return the PI ExperimentContact, or null if there isn't one
     */
    @Transient
    public ExperimentContact getPrimaryInvestigator() {
        // find the first contact whose set of roles includes the PI role
        for (ExperimentContact contact : this.experimentContacts) {
            if (contact.isPrimaryInvestigator()) {
                return contact;
            }
        }
        return null;
    }


    /**
     * Gets the number of PI for the experiment.
     *
     * @return the number of Primary Investigators for Experiment
     */
    @Transient
    public int getPrimaryInvestigatorCount() {
        int count = 0;
        for (ExperimentContact contact : this.experimentContacts) {
            if (contact.isPrimaryInvestigator()) {
                count++;
            }
        }
        return count;
    }

    /**
     * Gets the ExperimentContact corresponding to the Main POC for the experiment.
     *
     * @return the Main POC ExperimentContact, or null if there isn't
     */
    @Transient
    public ExperimentContact getMainPointOfContact() {
        // find the first contact whose set of roles includes the PI role
        for (ExperimentContact contact : this.experimentContacts) {
            if (contact.isMainPointOfContact()) {
                return contact;
            }
        }
        return null;
    }

    /**
     * Checks if the set of contacts for this experiment contains a contact with the POC role that does not also have
     * the PI role, and if such a contact exists, removes it from the set of contacts.
     */
    public void removeSeparateMainPointOfContact() {
        for (Iterator<ExperimentContact> it = this.experimentContacts.iterator(); it.hasNext();) {
            ExperimentContact contact = it.next();
            if (contact.isMainPointOfContact() && !contact.isPrimaryInvestigator()) {
                contact.setExperiment(null);
                it.remove();
            }
        }
    }

    /**
     * Gets the assay type for this Experiment.
     *
     * @return the assay type
     */
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "experiment")
    @ForeignKey(name = "experiment_assaytypes_exp_fk", inverseName = "experiment_assaytypes_at_fk")
    @AttributePolicy(allow = SecurityPolicy.BROWSE_POLICY_NAME)
    @Sort(type = SortType.NATURAL)
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
     * Gets the manufacturer for this Experiment.
     *
     * @return the manufacturer
     */
    @ManyToOne
    @Cascade(org.hibernate.annotations.CascadeType.SAVE_UPDATE)
    @ForeignKey(name = "experiment_manufacturer_fk")
    @AttributePolicy(allow = SecurityPolicy.BROWSE_POLICY_NAME)
    public Organization getManufacturer() {
        return this.manufacturer;
    }

    /**
     * Sets the manufacturer for this Experiment.
     *
     * @param manufacturer the manufacturer to set
     */
    public void setManufacturer(Organization manufacturer) {
        this.manufacturer = manufacturer;
    }

    /**
     * Gets the organism for this Experiment.
     *
     * @return the organism
     */
    @ManyToOne
    @ForeignKey(name = "experiment_organism_fk")
    @Cascade(org.hibernate.annotations.CascadeType.SAVE_UPDATE)
    @NotNull
    @AttributePolicy(allow = SecurityPolicy.BROWSE_POLICY_NAME)
    public Organism getOrganism() {
        return this.organism;
    }

    /**
     * Sets the organism for this Experiment.
     *
     * @param organism the organism to set
     */
    public void setOrganism(Organism organism) {
        this.organism = organism;
    }

    /**
     * Gets the qualityControlTypes.
     *
     * @return the qualityControlTypes
     */
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "qualitycontroltype",
            joinColumns = {@JoinColumn(name = FK_COLUMN_NAME) },
            inverseJoinColumns = {@JoinColumn(name = TERM_FK_NAME) })
    @ForeignKey(name = "qualctrltype_invest_fk", inverseName = "qualctrltype_term_fk")
    public Set<Term> getQualityControlTypes() {
        return this.qualityControlTypes;
    }

    /**
     * Sets the qualityControlTypes.
     *
     * @param qualityControlTypesVal the qualityControlTypes
     */
    public void setQualityControlTypes(final Set<Term> qualityControlTypesVal) {
        this.qualityControlTypes = qualityControlTypesVal;
    }

    /**
     * Gets the publications.
     *
     * @return the publications
     */
    /**
     * Gets the samples.
     *
     * @return the samples
     */
    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "experiment")
    @ForeignKey(name = "publication_expr_fk")
    @Cascade({ org.hibernate.annotations.CascadeType.SAVE_UPDATE, org.hibernate.annotations.CascadeType.DELETE,
            org.hibernate.annotations.CascadeType.DELETE_ORPHAN })
    public Set<Publication> getPublications() {
        return this.publications;
    }

    /**
     * Sets the publications.
     *
     * @param publicationsVal the publications
     */
    @SuppressWarnings({"unused", "PMD.UnusedPrivateMethod" })
    private void setPublications(final Set<Publication> publicationsVal) {
        this.publications = publicationsVal;
    }

    /**
     * Gets the replicateTypes.
     *
     * @return the replicateTypes
     */
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "replicatetype",
            joinColumns = {@JoinColumn(name = FK_COLUMN_NAME) },
            inverseJoinColumns = {@JoinColumn(name = TERM_FK_NAME) })
    @ForeignKey(name = "repltype_invest_fk", inverseName = "repltype_term_fk")
    public Set<Term> getReplicateTypes() {
        return this.replicateTypes;
    }

    /**
     * Sets the replicateTypes.
     *
     * @param replicateTypesVal the replicateTypes
     */
    public void setReplicateTypes(final Set<Term> replicateTypesVal) {
        this.replicateTypes = replicateTypesVal;
    }

    /**
     * Gets all the biomaterials (e.g. union of sources, samples, extracts, labeled extracts).
     * This is currently private, as it is only used in hibernate queries. If in the future
     * client code needs to access it, a wrapper method should be added that wraps this in
     * an unmodifiable set, to assure that modifications are only made to the underlying
     * source, sample, etc, collection.
     *
     * @return the biomaterials
     */
    @OneToMany(mappedBy = "experiment", fetch = FetchType.LAZY)
    @Filter(name = "Project1", condition = BIOMATERIALS_ALIAS_FILTER)
    @SuppressWarnings({"unused", "PMD.UnusedPrivateMethod" })
    private Set<AbstractBioMaterial> getBiomaterials() {
        return this.biomaterials;
    }

    /**
     * Sets the biomaterials. This is only there for javabean compliance and should not be used.
     *
     * @param biomaterials the biomaterials
     */
    @SuppressWarnings({"unused", "PMD.UnusedPrivateMethod" })
    private void setBiomaterials(final Set<AbstractBioMaterial> biomaterials) {
        this.biomaterials = biomaterials;
    }

    
    /**
     * Gets the sources.
     *
     * @return the sources
     */
    @OneToMany(mappedBy = "experiment", targetEntity = AbstractBioMaterial.class, fetch = FetchType.LAZY)
    @Where(clause = "discriminator = '" + Source.DISCRIMINATOR + "'")
    @Cascade({ org.hibernate.annotations.CascadeType.SAVE_UPDATE, org.hibernate.annotations.CascadeType.DELETE,
            org.hibernate.annotations.CascadeType.DELETE_ORPHAN })
    public Set<Source> getSources() {
        return this.sources;
    }

    /**
     * Sets the sources.
     *
     * @param sourcesVal the sources
     */
    @SuppressWarnings({"unused", "PMD.UnusedPrivateMethod" })
    private void setSources(final Set<Source> sourcesVal) {
        this.sources = sourcesVal;
    }

    /**
     * Gets the samples.
     *
     * @return the samples
     */
    @OneToMany(mappedBy = "experiment", targetEntity = AbstractBioMaterial.class, fetch = FetchType.LAZY)
    @Filters({
        @Filter(name = "BiomaterialFilter", condition = "discriminator = '" + Sample.DISCRIMINATOR + "'"),              
        @Filter(name = "Project1", condition = SAMPLES_ALIAS_FILTER)
    }
    )
    @Cascade({ org.hibernate.annotations.CascadeType.SAVE_UPDATE, org.hibernate.annotations.CascadeType.DELETE,
            org.hibernate.annotations.CascadeType.DELETE_ORPHAN })
    public Set<Sample> getSamples() {
        return this.samples;
    }

    /**
     * @return the number of samples in this experiment
     */
    @Transient
    public int getSampleCount() {
        return this.samples.size();
    }

    /**
     * Sets the samples.
     *
     * @param samplesVal the sources
     */
    @SuppressWarnings({"unused", "PMD.UnusedPrivateMethod" })
    private void setSamples(final Set<Sample> samplesVal) {
        this.samples = samplesVal;
    }

    /**
     * Gets the extracts.
     *
     * @return the extracts
     */
    @OneToMany(mappedBy = "experiment", targetEntity = AbstractBioMaterial.class, fetch = FetchType.LAZY)
    @Where(clause = "discriminator = '" + Extract.DISCRIMINATOR + "'")
    @Filter(name = "Project1", condition = EXTRACTS_ALIAS_FILTER)
    @Cascade({ org.hibernate.annotations.CascadeType.SAVE_UPDATE, org.hibernate.annotations.CascadeType.DELETE,
            org.hibernate.annotations.CascadeType.DELETE_ORPHAN })
    public Set<Extract> getExtracts() {
        return this.extracts;
    }

    /**
     * Sets the extracts.
     *
     * @param extractsVal the sources
     */
    @SuppressWarnings({"unused", "PMD.UnusedPrivateMethod" })
    private void setExtracts(final Set<Extract> extractsVal) {
        this.extracts = extractsVal;
    }

    /**
     * Gets the labeledExtracts.
     *
     * @return the labeledExtracts
     */
    @OneToMany(mappedBy = "experiment", targetEntity = AbstractBioMaterial.class, fetch = FetchType.LAZY)
    @Where(clause = "discriminator = '" + LabeledExtract.DISCRIMINATOR + "'")
    @Filter(name = "Project1", condition = LABELED_EXTRACTS_ALIAS_FILTER)
    @Cascade({ org.hibernate.annotations.CascadeType.SAVE_UPDATE, org.hibernate.annotations.CascadeType.DELETE,
            org.hibernate.annotations.CascadeType.DELETE_ORPHAN })
    public Set<LabeledExtract> getLabeledExtracts() {
        return this.labeledExtracts;
    }

    /**
     * Sets the labeledExtracts.
     *
     * @param labeledExtractsVal the sources
     */
    @SuppressWarnings({"unused", "PMD.UnusedPrivateMethod" })
    private void setLabeledExtracts(final Set<LabeledExtract> labeledExtractsVal) {
        this.labeledExtracts = labeledExtractsVal;
    }

    /**
     * Gets the arrayDesigns.
     *
     * @return the arrayDesigns
     */
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "experimentarraydesign",
            joinColumns = {@JoinColumn(name = FK_COLUMN_NAME) },
            inverseJoinColumns = {@JoinColumn(name = "arraydesign_id") })
    @ForeignKey(name = "investarraydesign_invest_fk", inverseName = "investarraydesign_arraydesign_fk")
    @Cascade(org.hibernate.annotations.CascadeType.SAVE_UPDATE)
    @AttributePolicy(allow = SecurityPolicy.BROWSE_POLICY_NAME)
    public Set<ArrayDesign> getArrayDesigns() {
        return this.arrayDesigns;
    }

    /**
     * Sets the arrayDesigns.
     *
     * @param arrayDesignsVal the arrayDesigns
     */
    public void setArrayDesigns(final Set<ArrayDesign> arrayDesignsVal) {
        this.arrayDesigns = arrayDesignsVal;
    }

    /**
     * Gets the experimentContacts.
     *
     * @return the experimentContacts
     */
    @OneToMany
    @IndexColumn(name = "indx")
    @JoinColumn(name = "experiment")
    @BatchSize(size = AbstractCaArrayObject.DEFAULT_BATCH_SIZE)
    @Cascade({ org.hibernate.annotations.CascadeType.SAVE_UPDATE, org.hibernate.annotations.CascadeType.DELETE,
            org.hibernate.annotations.CascadeType.DELETE_ORPHAN })
    @AttributePolicy(allow = SecurityPolicy.BROWSE_POLICY_NAME)
    public List<ExperimentContact> getExperimentContacts() {
        return this.experimentContacts;
    }

    /**
     * Sets the experimentContacts.
     *
     * @param experimentContactsVal the experimentContacts
     */
    @SuppressWarnings({"unused", "PMD.UnusedPrivateMethod" })
    private void setExperimentContacts(final List<ExperimentContact> experimentContactsVal) {
        this.experimentContacts = experimentContactsVal;
    }

    /**
     * @return the experimentDesignType
     */
    @ManyToMany
    @JoinTable(name = "experiment_design_types",
            joinColumns = {@JoinColumn(name = FK_COLUMN_NAME) },
            inverseJoinColumns = {@JoinColumn(name = TERM_FK_NAME) })
    @Cascade(org.hibernate.annotations.CascadeType.SAVE_UPDATE)
    public Set<Term> getExperimentDesignTypes() {
        return this.experimentDesignTypes;
    }

    /**
     * @param experimentDesignTypes the experimentDesignTypes to set
     */
    public void setExperimentDesignTypes(Set<Term> experimentDesignTypes) {
        this.experimentDesignTypes = experimentDesignTypes;
    }

    /**
     * Get the description of the experiment design.
     * @return the designDescription
     */
    @Column(name = "experiment_design_description")
    @Length(min = 1, max = LARGE_TEXT_FIELD_LENGTH)
    public String getDesignDescription() {
        return this.designDescription;
    }

    /**
     * Set the description of the experiment design.
     * @param designDescription the designDescription to set
     */
    public void setDesignDescription(String designDescription) {
        this.designDescription = designDescription;
    }

    /**
     * @return the qualityControlDescription
     */
    @Length(max = LARGE_TEXT_FIELD_LENGTH)
    public String getQualityControlDescription() {
        return this.qualityControlDescription;
    }

    /**
     * @param qualityControlDescription the qualityControlDescription to set
     */
    public void setQualityControlDescription(String qualityControlDescription) {
        this.qualityControlDescription = qualityControlDescription;
    }

    /**
     * @return the replicateDescription
     */
    @Length(max = LARGE_TEXT_FIELD_LENGTH)
    public String getReplicateDescription() {
        return this.replicateDescription;
    }

    /**
     * @param replicateDescription the replicateDescription to set
     */
    public void setReplicateDescription(String replicateDescription) {
        this.replicateDescription = replicateDescription;
    }

    /**
     * Gets the factors.
     *
     * @return the factors
     */
    @OneToMany
    @JoinColumn(name = EXPERIMENT_REF)
    @Cascade({ org.hibernate.annotations.CascadeType.SAVE_UPDATE, org.hibernate.annotations.CascadeType.DELETE,
            org.hibernate.annotations.CascadeType.DELETE_ORPHAN })
    public Set<Factor> getFactors() {
        return this.factors;
    }

    /**
     * Sets the factors.
     *
     * @param factorsVal the factors
     */
    @SuppressWarnings({"unused", "PMD.UnusedPrivateMethod" })
    private void setFactors(final Set<Factor> factorsVal) {
        this.factors = factorsVal;
    }

    /**
     * Gets the normalizationTypes.
     *
     * @return the normalizationTypes
     */
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "normalizationtype",
            joinColumns = {@JoinColumn(name = FK_COLUMN_NAME) },
            inverseJoinColumns = {@JoinColumn(name = TERM_FK_NAME) })
    @ForeignKey(name = "normtype_invest_fk", inverseName = "normtype_term_fk")
    @Cascade(org.hibernate.annotations.CascadeType.SAVE_UPDATE)
    public Set<Term> getNormalizationTypes() {
        return this.normalizationTypes;
    }

    /**
     * Sets the normalizationTypes.
     *
     * @param normalizationTypesVal the normalizationTypes
     */
    @SuppressWarnings({"unused", "PMD.UnusedPrivateMethod" })
    private void setNormalizationTypes(final Set<Term> normalizationTypesVal) {
        this.normalizationTypes = normalizationTypesVal;
    }

    /**
     * Gets the arrays.
     *
     * @return the arrays
     */
    @Transient
    public Set<Array> getArrays() {
        Set<Array> arrays = new HashSet<Array>();
        for (Hybridization hyb : getHybridizations()) {
            arrays.add(hyb.getArray());
        }
        return arrays;
    }

    /**
     * @return hybridizations
     */
    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = EXPERIMENT_REF)
    @ForeignKey(name = "hybridization_expr_fk")
    @Cascade({ org.hibernate.annotations.CascadeType.SAVE_UPDATE, org.hibernate.annotations.CascadeType.DELETE,
            org.hibernate.annotations.CascadeType.DELETE_ORPHAN })
    @Filter(name = "Project1", condition = HYBRIDIZATIONS_ALIAS_FILTER)
    public Set<Hybridization> getHybridizations() {
        return this.hybridizations;
    }

    @SuppressWarnings({"unused", "PMD.UnusedPrivateMethod" })
    private void setHybridizations(final Set<Hybridization> hybridizations) {
        this.hybridizations = hybridizations;
    }

    /**
     * @return the project to which this experiment belongs.
     */
    @OneToOne(mappedBy = "experiment")
    @AttributePolicy(allow = SecurityPolicy.BROWSE_POLICY_NAME)
    public Project getProject() {
        return this.project;
    }

    /**
     * @param project the project to set
     */
    @SuppressWarnings({"unused", "PMD.UnusedPrivateMethod" })
    private void setProject(Project project) {
        this.project = project;
    }

    /**
     * @return the set of ArrayDesigns actually referenced from some hybridization belonging to the experiment.
     * Note that this is distinct and potentially different that what is returned by <code>getArrayDesigns()</code>.
     * The latter is the set of array designs explicitly specified by the user. It is an invariant, however,
     * that the set returned by this method is a subset of that returned by <code>getArrayDesigns()</code>.
     */
    @Transient
    public Set<ArrayDesign> getArrayDesignsFromHybs() {
        Set<ArrayDesign> designs = new HashSet<ArrayDesign>();
        for (Hybridization h : getHybridizations()) {
            if (h.getArray() != null && h.getArray().getDesign() != null) {
                designs.add(h.getArray().getDesign());
            }
        }
        return designs;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    /**
     * Return the source with given name in this experiment. If there is none, return null.
     * @param sourceName name of source to find in this experiment
     * @return the source with given name or null if there is none.
     */
    public Source getSourceByName(final String sourceName) {
       return (Source) CollectionUtils.find(getSources(), new ExperimentDesignNodeNamePredicate(sourceName));
    }

    /**
     * Return the sample with given name in this experiment. If there is none, return null.
     * @param sampleName name of sample to find in this experiment
     * @return the sample with given name or null if there is none.
     */
    public Sample getSampleByName(final String sampleName) {
        return (Sample) CollectionUtils.find(getSamples(), new ExperimentDesignNodeNamePredicate(sampleName));
     }

    /**
     * Return the extract with given name in this experiment. If there is none, return null.
     * @param extractName name of extract to find in this experiment
     * @return the extract with given name or null if there is none.
     */
    public Extract getExtractByName(final String extractName) {
       return (Extract) CollectionUtils.find(getExtracts(), new ExperimentDesignNodeNamePredicate(extractName));
    }

    /**
     * Return the labeled extract with given name in this experiment. If there is none, return null.
     * @param labeledExtractName name of labeled extract to find in this experiment
     * @return the labeledExtract with given name or null if there is none.
     */
    public LabeledExtract getLabeledExtractByName(final String labeledExtractName) {
        return (LabeledExtract) CollectionUtils.find(getLabeledExtracts(), new ExperimentDesignNodeNamePredicate(
                labeledExtractName));
     }

    /**
     * Return the hybridization with given name in this experiment. If there is none, return null.
     * @param hybridizationName name of hybridization to find in this experiment
     * @return the hybridization with given name or null if there is none.
     */
    public Hybridization getHybridizationByName(final String hybridizationName) {
        return (Hybridization) CollectionUtils.find(getHybridizations(), new ExperimentDesignNodeNamePredicate(
                hybridizationName));
     }

    /**
     * Predicate to match biomaterial/hybridization names.
     */
    private class ExperimentDesignNodeNamePredicate implements Predicate {
        private final String nameToMatch;

        /**
         * Constructor.
         */
        public ExperimentDesignNodeNamePredicate(String nameToMatch) {
            this.nameToMatch = nameToMatch;
        }

        /**
         * {@inheritDoc}
         */
        public boolean evaluate(Object o) {
            AbstractExperimentDesignNode node = (AbstractExperimentDesignNode) o;
            return nameToMatch.equalsIgnoreCase(node.getName());
        }
    }

}
