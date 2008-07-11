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

package gov.nih.nci.caarray.domain.project;

import edu.georgetown.pir.Organism;
import gov.nih.nci.caarray.domain.AbstractCaArrayEntity;
import gov.nih.nci.caarray.domain.AbstractCaArrayObject;
import gov.nih.nci.caarray.domain.array.Array;
import gov.nih.nci.caarray.domain.array.ArrayDesign;
import gov.nih.nci.caarray.domain.contact.Organization;
import gov.nih.nci.caarray.domain.hybridization.Hybridization;
import gov.nih.nci.caarray.domain.publication.Publication;
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

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
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
import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.IndexColumn;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
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
        + "ad.data_file left join project p on f.project = p.id left join hybridization h on ad.hybridization = h.id "
        + "left join derivedarraydata_hybridizations dadh on ad.id = dadh.derivedarraydata_id left join "
        + "hybridization h2 on dadh.hybridization_id = h2.id left join labeledextracthybridization leh on h.id = "
        + "leh.hybridization_id left join extractlabeledextract ele on leh.labeledextract_id = ele.labeledextract_id "
        + "left join sampleextract se on ele.extract_id = se.extract_id left join biomaterial s on se.sample_id = s.id "
        + "left join labeledextracthybridization leh2 on h2.id = leh2.hybridization_id left join extractlabeledextract "
        + "ele2 on leh2.labeledextract_id = ele2.labeledextract_id left join sampleextract se2 on ele2.extract_id = "
        + "se2.extract_id left join biomaterial s2 on se2.sample_id = s2.id where s.id is not null and s.id in "
        + READABLE_SAMPLE_ALIAS_CLAUSE + " or s2.id is not null and s2.id in " + READABLE_SAMPLE_ALIAS_CLAUSE 
        + " or (f.status = " + "'SUPPLEMENTAL' or f.status = 'IMPORTED') and s.id is null and s2.id is null and "
        + "p.id in " + READABLE_PROJECT_CLAUSE + " or p.id in " + PROJECT_OWNER_CLAUSE + ")";
    private static final long serialVersionUID = 1234567890L;
    private static final int PAYMENT_NUMBER_FIELD_LENGTH = 100;

    private String title;
    private String description;
    private Date dateOfExperiment;
    private Date publicReleaseDate;
    private PaymentMechanism paymentMechanism;
    private String paymentNumber;
    private ServiceType serviceType = ServiceType.FULL;
    private String assayType;
    private Organization manufacturer;
    private Organism organism;
    private Set<Factor> factors = new HashSet<Factor>();
    private List<ExperimentContact> experimentContacts = new ArrayList<ExperimentContact>();
    private Set<Term> experimentDesignTypes = new HashSet<Term>();
    private String experimentDesignDescription;
    private String qualityControlDescription;
    private Set<Term> qualityControlTypes = new HashSet<Term>();
    private String replicateDescription;
    private Set<Term> replicateTypes = new HashSet<Term>();
    private Set<Term> normalizationTypes = new HashSet<Term>();
    private Set<Publication> publications = new HashSet<Publication>();
    private Set<Array> arrays = new HashSet<Array>();
    private Set<ArrayDesign> arrayDesigns = new HashSet<ArrayDesign>();
    private Set<Source> sources = new HashSet<Source>();
    private Set<Sample> samples = new HashSet<Sample>();
    private Set<Extract> extracts = new HashSet<Extract>();
    private Set<LabeledExtract> labeledExtracts = new HashSet<LabeledExtract>();
    private Set<Hybridization> hybridizations = new HashSet<Hybridization>();
    private String publicIdentifier;
    private Project project;

    /**
     * Gets the dateOfExperiment.
     *
     * @return the dateOfExperiment
     */
    @Temporal(TemporalType.TIMESTAMP)
    @AttributePolicy(allow = SecurityPolicy.BROWSE_POLICY_NAME)
    public Date getDateOfExperiment() {
        return this.dateOfExperiment;
    }

    /**
     * Sets the dateOfExperiment.
     *
     * @param dateOfExperimentVal the dateOfExperiment
     */
    public void setDateOfExperiment(final Date dateOfExperimentVal) {
        this.dateOfExperiment = dateOfExperimentVal;
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
     * Gets the payment mechanism for this experiment.
     *
     * @return the payment mechanism
     */
    @ManyToOne(cascade = {CascadeType.ALL })
    @ForeignKey(name = "experiment_payment_mechanism_fk")
    public PaymentMechanism getPaymentMechanism() {
        return this.paymentMechanism;
    }

    /**
     * Set the payment mechanism for this experiment.
     *
     * @param paymentMechanism the payment mechanism to set
     */
    public void setPaymentMechanism(PaymentMechanism paymentMechanism) {
        this.paymentMechanism = paymentMechanism;
    }

    /**
     * Gets the payment number, i.e. invoice number.
     *
     * @return the payment number
     */
    @Column(length = PAYMENT_NUMBER_FIELD_LENGTH)
    public String getPaymentNumber() {
        return this.paymentNumber;
    }

    /**
     * Sets the payment number, ie invoice number.
     *
     * @param paymentNumber the payment number to set
     */
    public void setPaymentNumber(String paymentNumber) {
        this.paymentNumber = paymentNumber;
    }

    /**
     * Gets the service type requested for this experiment.
     *
     * @return the service type
     */
    @Enumerated(EnumType.STRING)
    @AttributePolicy(allow = SecurityPolicy.BROWSE_POLICY_NAME)
    @NotNull
    public ServiceType getServiceType() {
        return this.serviceType;
    }

    /**
     * Sets the service type requested for this experiment.
     *
     * @param serviceType the service type to set
     */
    public void setServiceType(ServiceType serviceType) {
        this.serviceType = serviceType;
    }

    /**
     * Gets the assay type for this Experiment.
     *
     * @return the assay type
     */
    @NotNull
    @Column(length = DEFAULT_STRING_COLUMN_SIZE)
    @AttributePolicy(allow = SecurityPolicy.BROWSE_POLICY_NAME)
    public String getAssayType() {
        return this.assayType;
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
     * Gets the manufacturer for this Experiment.
     *
     * @return the manufacturer
     */
    @ManyToOne
    @Cascade(org.hibernate.annotations.CascadeType.SAVE_UPDATE)
    @ForeignKey(name = "experiment_manufacturer_fk")
    @NotNull
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
    @SuppressWarnings({"unused", "PMD.UnusedPrivateMethod" })
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
    @Cascade({org.hibernate.annotations.CascadeType.SAVE_UPDATE, org.hibernate.annotations.CascadeType.DELETE_ORPHAN })
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
    @SuppressWarnings({"unused", "PMD.UnusedPrivateMethod" })
    public void setReplicateTypes(final Set<Term> replicateTypesVal) {
        this.replicateTypes = replicateTypesVal;
    }

    /**
     * Gets the sources.
     *
     * @return the sources
     */
    @OneToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "experimentsource",
            joinColumns = {@JoinColumn(name = FK_COLUMN_NAME) },
            inverseJoinColumns = {@JoinColumn(name = "source_id") })
    @ForeignKey(name = "experimentsource_invest_fk", inverseName = "experimentsource_source_fk")
    @Cascade({org.hibernate.annotations.CascadeType.SAVE_UPDATE, org.hibernate.annotations.CascadeType.DELETE_ORPHAN })
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
    @OneToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "experimentsample",
            joinColumns = {@JoinColumn(name = FK_COLUMN_NAME) },
            inverseJoinColumns = {@JoinColumn(name = "sample_id") })
    @LazyCollection(LazyCollectionOption.EXTRA)
    @ForeignKey(name = "experimentsample_invest_fk", inverseName = "experimentsample_sample_fk")
    @Cascade({org.hibernate.annotations.CascadeType.SAVE_UPDATE, org.hibernate.annotations.CascadeType.DELETE_ORPHAN })
    @Filter(name = "Project1", condition = SAMPLES_ALIAS_FILTER)
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
    @OneToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "experimentextract",
            joinColumns = {@JoinColumn(name = FK_COLUMN_NAME) },
            inverseJoinColumns = {@JoinColumn(name = "extract_id") })
    @ForeignKey(name = "experimentextract_invest_fk", inverseName = "experimentextract_extract_fk")
    @Cascade({org.hibernate.annotations.CascadeType.SAVE_UPDATE, org.hibernate.annotations.CascadeType.DELETE_ORPHAN })
    @Filter(name = "Project1", condition = EXTRACTS_ALIAS_FILTER)
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
    @OneToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "experimentlabeledextract",
            joinColumns = {@JoinColumn(name = FK_COLUMN_NAME) },
            inverseJoinColumns = {@JoinColumn(name = "labeled_extract_id") })
    @ForeignKey(name = "experimentle_invest_fk", inverseName = "experimentle_le_fk")
    @Cascade({org.hibernate.annotations.CascadeType.SAVE_UPDATE, org.hibernate.annotations.CascadeType.DELETE_ORPHAN })
    @Filter(name = "Project1", condition = LABELED_EXTRACTS_ALIAS_FILTER)
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
    @SuppressWarnings({"unused", "PMD.UnusedPrivateMethod" })
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
    @Cascade({org.hibernate.annotations.CascadeType.SAVE_UPDATE, org.hibernate.annotations.CascadeType.DELETE_ORPHAN })
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
     * @return the experimentDesignDescription
     */
    @Length(min = 1, max = LARGE_TEXT_FIELD_LENGTH)
    public String getExperimentDesignDescription() {
        return this.experimentDesignDescription;
    }

    /**
     * @param experimentDesignDescription the experimentDesignDescription to set
     */
    public void setExperimentDesignDescription(String experimentDesignDescription) {
        this.experimentDesignDescription = experimentDesignDescription;
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
    @Cascade({org.hibernate.annotations.CascadeType.SAVE_UPDATE, org.hibernate.annotations.CascadeType.DELETE_ORPHAN })
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
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "experimentarray",
            joinColumns = {@JoinColumn(name = FK_COLUMN_NAME) },
            inverseJoinColumns = {@JoinColumn(name = "array_id") })
    @ForeignKey(name = "exprarray_invest_fk", inverseName = "exprarray_array_fk")
    @Cascade(org.hibernate.annotations.CascadeType.SAVE_UPDATE)
    public Set<Array> getArrays() {
        return this.arrays;
    }

    /**
     * Sets the arrays.
     *
     * @param arraysVal the arrays
     */
    @SuppressWarnings({"unused", "PMD.UnusedPrivateMethod" })
    private void setArrays(final Set<Array> arraysVal) {
        this.arrays = arraysVal;
    }

    /**
     * @return hybridizations
     */
    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = EXPERIMENT_REF)
    @ForeignKey(name = "hybridization_expr_fk")
    @Cascade({org.hibernate.annotations.CascadeType.SAVE_UPDATE, org.hibernate.annotations.CascadeType.DELETE_ORPHAN })
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
     * Return the hybridization with given name in this experiment. if there is more than one hybridization
     * with this name, return a random one from among them. if there are none, return null.
     * @param hybridizationName name of hybridization to find in this experiment
     * @return the hybridization with given name (if multiple matches, then some random one from among the matches),
     * or null if there are none.
     */
    public Hybridization getHybridizationByName(final String hybridizationName) {
       return (Hybridization) CollectionUtils.find(getHybridizations(), new Predicate() {
            /**
             * {@inheritDoc}
             */
            public boolean evaluate(Object o) {
                Hybridization hyb = (Hybridization) o;
                return hybridizationName.equalsIgnoreCase(hyb.getName());
            }
        });
    }
}
