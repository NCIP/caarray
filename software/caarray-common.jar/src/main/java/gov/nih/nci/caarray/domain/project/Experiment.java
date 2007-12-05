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

import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.persistence.Basic;
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

import org.apache.commons.lang.builder.ToStringBuilder;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Formula;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.hibernate.annotations.Where;
import org.hibernate.validator.Length;
import org.hibernate.validator.NotNull;

/**
 *
 */
@Entity
@SuppressWarnings({"PMD.TooManyFields", "PMD.ExcessiveClassLength", "PMD.ExcessivePublicCount" })
public class Experiment extends AbstractCaArrayEntity {
    // Experiment is central object -- can't reduce set of linked entities

    private static final String FK_COLUMN_NAME = "EXPERIMENT_ID";
    private static final String TERM_FK_NAME = "TERM_ID";
    private static final String EXPERIMENT_REF = "experiment";

    private static final String READABLE_PROJECT_CLAUSE = "(select pe.attribute_value from csm_protection_group pg, "
        + "csm_protection_element pe, csm_pg_pe pgpe, csm_user_group_role_pg ugrpg, csm_user u, csm_role_privilege rp, "
        + "csm_role r, csm_privilege p, csm_group g, csm_user_group ug where "
        + "pe.object_id= 'gov.nih.nci.caarray.domain.project.Project' and pe.attribute='id' and "
        + "u.login_name=:Project1.USER_NAME and pe.application_id=:Project1.APPLICATION_ID and ugrpg.role_id=r.role_id "
        + "and (ugrpg.user_id = u.user_id or ugrpg.group_id = ug.group_id and ug.user_id = u.user_id) and "
        + "ugrpg.protection_group_id = ANY (select pg1.protection_group_id from csm_protection_group pg1 where "
        + "pg1.protection_group_id = pg.protection_group_id  or pg1.protection_group_id = (select "
        + "pg2.parent_protection_group_id from csm_protection_group pg2 where pg2.protection_group_id = "
        + "pg.protection_group_id)) and pg.protection_group_id = pgpe.protection_group_id and "
        + "pgpe.protection_element_id = pe.protection_element_id and r.role_id = rp.role_id and rp.privilege_id = "
        + "p.privilege_id and p.privilege_name='READ')";
    private static final String PROJECT_OWNER_CLAUSE = "(select pe.attribute_value from csm_protection_group pg, "
        + "csm_protection_element pe, csm_pg_pe pgpe, csm_user_group_role_pg ugrpg, csm_user u, csm_role_privilege rp, "
        + "csm_role r, csm_privilege p  where pe.object_id= 'gov.nih.nci.caarray.domain.project.Project' and "
        + "pe.attribute='id' and u.login_name=:Project1.USER_NAME and pe.application_id=:Project1.APPLICATION_ID and "
        + "ugrpg.role_id = r.role_id and ugrpg.user_id = u.user_id and ugrpg.protection_group_id = ANY (select "
        + "pg1.protection_group_id from csm_protection_group pg1 where pg1.protection_group_id = pg.protection_group_id"
        + " or pg1.protection_group_id = (select pg2.parent_protection_group_id from csm_protection_group pg2 where "
        + "pg2.protection_group_id = pg.protection_group_id)) and pg.protection_group_id = pgpe.protection_group_id "
        + "and pgpe.protection_element_id = pe.protection_element_id and r.role_id = rp.role_id and rp.privilege_id = "
        + "p.privilege_id and p.privilege_name='READ')";
    private static final String READABLE_SAMPLE_CLAUSE = "(select pe.attribute_value from csm_protection_group pg, "
        + "csm_protection_element pe, csm_pg_pe pgpe, csm_user_group_role_pg ugrpg, csm_user u, csm_role_privilege rp, "
        + "csm_role r, csm_privilege p, csm_group g, csm_user_group ug where "
        + "pe.object_id= 'gov.nih.nci.caarray.domain.sample.Sample' and pe.attribute='id' and "
        + "u.login_name=:Project1.USER_NAME and pe.application_id=:Project1.APPLICATION_ID and ugrpg.role_id=r.role_id "
        + "and (ugrpg.user_id = u.user_id or ugrpg.group_id = ug.group_id and ug.user_id = u.user_id) and "
        + "ugrpg.protection_group_id = ANY (select pg1.protection_group_id from csm_protection_group pg1 where "
        + "pg1.protection_group_id = pg.protection_group_id  or pg1.protection_group_id = (select "
        + "pg2.parent_protection_group_id from csm_protection_group pg2 where pg2.protection_group_id = "
        + "pg.protection_group_id)) and pg.protection_group_id = pgpe.protection_group_id and "
        + "pgpe.protection_element_id = pe.protection_element_id and r.role_id = rp.role_id and rp.privilege_id = "
        + "p.privilege_id and p.privilege_name='READ')";
    /** @Where filter for samples */
    public static final String SAMPLES_FILTER = "id in (select s.ID from biomaterial s where s.discriminator = 'SA' "
        + "and s.ID in " + READABLE_SAMPLE_CLAUSE + ")";
    /** @Where filter for extracts */
    public static final String EXTRACTS_FILTER = "id in (select e.ID from biomaterial e inner join sampleextract se on "
        + "e.id = se.extract_id inner join biomaterial s on se.sample_id = s.id where e.discriminator = 'EX' and s.ID "
        + "in " + READABLE_SAMPLE_CLAUSE + ")";
    /** @Where filter for labeled extracts */
    public static final String LABELED_EXTRACTS_FILTER = "id in (select le.ID from biomaterial le inner join "
        + "extractlabeledextract ele on le.id = ele.labeledextract_id inner join sampleextract se on ele.extract_id = "
        + "se.extract_id inner join biomaterial s on se.sample_id = s.id where le.discriminator = 'LA' and s.ID in "
        + READABLE_SAMPLE_CLAUSE + ")";
    /** @Where filter for hybs */
    public static final String HYBRIDIZATIONS_FILTER = "ID in (select h.ID from hybridization h inner join "
        + "labeledextracthybridization leh on h.id = leh.hybridization_id inner join extractlabeledextract ele on "
        + "leh.labeledextract_id = ele.labeledextract_id inner join sampleextract se on ele.extract_id = se.extract_id "
        + "inner join biomaterial s on se.sample_id = s.id where s.ID in " + READABLE_SAMPLE_CLAUSE + ")";
    /** @Where filter for files */
    public static final String FILES_FILTER = "ID in (select f.id from caarrayfile f left join arraydata ad on f.id = "
        + "ad.data_file left join project p on f.project = p.id left join hybridization h on ad.hybridization = h.id "
        + "left join derivedarraydata_hybridizations dadh on ad.id = dadh.hybridization_id left join hybridization h2 "
        + "on dadh.derivedarraydata_id = h2.id left join labeledextracthybridization leh on h.id = "
        + "leh.hybridization_id left join extractlabeledextract ele on leh.labeledextract_id = ele.labeledextract_id "
        + "left join sampleextract se on ele.extract_id = se.extract_id left join biomaterial s on se.sample_id = s.id "
        + "left join labeledextracthybridization leh2 on h2.id = leh2.hybridization_id left join extractlabeledextract "
        + "ele2 on leh2.labeledextract_id = ele2.labeledextract_id left join sampleextract se2 on ele2.extract_id = "
        + "se2.extract_id left join biomaterial s2 on se2.sample_id = s2.id where s.id is not null and s.id in "
        + READABLE_SAMPLE_CLAUSE + " or s2.id is not null and s2.id in " + READABLE_SAMPLE_CLAUSE + " or f.type = "
        + "'SUPPLEMENTAL' and p.id in " + READABLE_PROJECT_CLAUSE + " or p.id in " + PROJECT_OWNER_CLAUSE + ")";
    private static final long serialVersionUID = 1234567890L;
    private static final int PAYMENT_NUMBER_FIELD_LENGTH = 100;

    private String title;
    private String description;
    private Date dateOfExperiment;
    private Date publicReleaseDate;
    private PaymentMechanism paymentMechanism;
    private String paymentNumber;
    private ServiceType serviceType;
    private AssayType assayType;
    private Organization manufacturer;
    private Organism organism;
    private Set<Factor> factors = new HashSet<Factor>();
    private Set<ExperimentContact> experimentContacts = new HashSet<ExperimentContact>();
    private Term experimentDesignType;
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
    @Column(length = LARGE_TEXT_FIELD_LENGTH)
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
     * Gets a human readable public identifier for this experiment. This is an autogenerated identifier that can be used
     * to refer to the experiment in publications, and would be expected to be a component of a permalink url pointing
     * to the project.
     *
     * The identifier will be of the form xxxxx-11111, where xxxxx is the first 5 characters of the PI last name, and
     * 11111 is a 5-digit auto number based on the persistent identifier of the experiment
     *
     * Note that this public identifier is available only once the Experiment has been made persistent (ie its id is not
     * null). Before that, this will return null
     *
     * DEVELOPER NOTE: implemented as a hibernate formula field which uses explicit SQL. The concat function below is
     * MySQL-specific
     *
     * @return the public identifier if this Experiment is persistent, null otherwise
     */
    @Basic(fetch = FetchType.LAZY)
    @Formula("(select concat(substr(c.last_name, 1, 5), '-', lpad(id, 5, '0')) from "
            + "contact c join experiment_contact ec on c.id = ec.contact "
            + "join experimentcontactrole ecr on ecr.experimentcontact_id = ec.id "
            + "join term t on ecr.role_id = t.id where t.value='" + ExperimentContact.PI_ROLE
            + "' and ec.experiment = id)")
    @AttributePolicy(allow = SecurityPolicy.BROWSE_POLICY_NAME)
    public String getPublicIdentifier() {
        return this.publicIdentifier;
    }

    /**
     * Hibernate only, as this is a derived property.
     *
     * @param publicIdentifier the publicIdentifier to set
     */
    @SuppressWarnings("unused") // NOPMD
    private void setPublicIdentifier(String publicIdentifier) {
        this.publicIdentifier = publicIdentifier;
    }

    /**
     * Gets the ExperimentContact corresponding to the PI for the experiment.
     *
     * @return the PI ExperimentContact, or null if there isn't one
     */
    @Transient
    public ExperimentContact getPrimaryInvestigator() {
        // find the first contact whose set of roles includes the PI role
        Set<ExperimentContact> contacts = getExperimentContacts();
        for (ExperimentContact contact : contacts) {
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
        Set<ExperimentContact> contacts = getExperimentContacts();
        for (ExperimentContact contact : contacts) {
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
                break;
            }
        }
    }

    /**
     * Gets the payment mechanism for this experiment.
     *
     * @return the payment mechanism
     */
    @ManyToOne(cascade = {CascadeType.ALL })
    @ForeignKey(name = "EXPERIMENT_PAYMENT_MECHANISM_FK")
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
    @Enumerated(EnumType.STRING)
    @NotNull
    @AttributePolicy(allow = SecurityPolicy.BROWSE_POLICY_NAME)
    public AssayType getAssayType() {
        return this.assayType;
    }

    /**
     * Sets the assay type for this experiment.
     *
     * @param assayType the assay type to set
     */
    public void setAssayType(AssayType assayType) {
        this.assayType = assayType;
    }

    /**
     * Gets the manufacturer for this Experiment.
     *
     * @return the manufacturer
     */
    @ManyToOne
    @Cascade(org.hibernate.annotations.CascadeType.SAVE_UPDATE)
    @ForeignKey(name = "EXPERIMENT_MANUFACTURER_FK")
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
    @ForeignKey(name = "EXPERIMENT_ORGANISM_FK")
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
    @JoinTable(name = "QUALITYCONTROLTYPE",
            joinColumns = {@JoinColumn(name = FK_COLUMN_NAME) },
            inverseJoinColumns = {@JoinColumn(name = TERM_FK_NAME) })
    @ForeignKey(name = "QUALCTRLTYPE_INVEST_FK", inverseName = "QUALCTRLTYPE_TERM_FK")
    public Set<Term> getQualityControlTypes() {
        return this.qualityControlTypes;
    }

    /**
     * Sets the qualityControlTypes.
     *
     * @param qualityControlTypesVal the qualityControlTypes
     */
    @SuppressWarnings("unused")
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
    @ForeignKey(name = "PUBLICATION_EXPR_FK")
    @Cascade({org.hibernate.annotations.CascadeType.SAVE_UPDATE, org.hibernate.annotations.CascadeType.DELETE_ORPHAN })
    public Set<Publication> getPublications() {
        return this.publications;
    }

    /**
     * Sets the publications.
     *
     * @param publicationsVal the publications
     */
    @SuppressWarnings("unused")
    private void setPublications(final Set<Publication> publicationsVal) {
        this.publications = publicationsVal;
    }

    /**
     * Gets the replicateTypes.
     *
     * @return the replicateTypes
     */
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "REPLICATETYPE",
            joinColumns = {@JoinColumn(name = FK_COLUMN_NAME) },
            inverseJoinColumns = {@JoinColumn(name = TERM_FK_NAME) })
    @ForeignKey(name = "REPLTYPE_INVEST_FK", inverseName = "REPLTYPE_TERM_FK")
    public Set<Term> getReplicateTypes() {
        return this.replicateTypes;
    }

    /**
     * Sets the replicateTypes.
     *
     * @param replicateTypesVal the replicateTypes
     */
    @SuppressWarnings("unused")
    public void setReplicateTypes(final Set<Term> replicateTypesVal) {
        this.replicateTypes = replicateTypesVal;
    }

    /**
     * Gets the sources.
     *
     * @return the sources
     */
    @OneToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "EXPERIMENTSOURCE",
            joinColumns = {@JoinColumn(name = FK_COLUMN_NAME) },
            inverseJoinColumns = {@JoinColumn(name = "SOURCE_ID") })
    @ForeignKey(name = "EXPERIMENTSOURCE_INVEST_FK", inverseName = "EXPERIMENTSOURCE_SOURCE_FK")
    @Cascade({org.hibernate.annotations.CascadeType.SAVE_UPDATE, org.hibernate.annotations.CascadeType.DELETE_ORPHAN })
    public Set<Source> getSources() {
        return this.sources;
    }

    /**
     * Sets the sources.
     *
     * @param sourcesVal the sources
     */
    @SuppressWarnings("unused")
    private void setSources(final Set<Source> sourcesVal) {
        this.sources = sourcesVal;
    }

    /**
     * Gets the samples.
     *
     * DEVELOPER NOTE: because of the extra laziness options, operations which do not initialize
     * the Samples collection (such as size) do not take into account the security restrictions
     * in the @Where annotation and therefore return values which are not consistent with those
     * that would be returned by the collection once it were initialized. Ie, if the experiment
     * has 3 samples but a user can only access 2, then getSamples().size() will return 3 prior
     * to being initialized by hibernate, and 2 afterwards
     * @return the samples
     */
    @OneToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "EXPERIMENTSAMPLE",
            joinColumns = {@JoinColumn(name = FK_COLUMN_NAME) },
            inverseJoinColumns = {@JoinColumn(name = "SAMPLE_ID") })
    @LazyCollection(LazyCollectionOption.EXTRA)
    @ForeignKey(name = "EXPERIMENTSAMPLE_INVEST_FK", inverseName = "EXPERIMENTSAMPLE_SAMPLE_FK")
    @Cascade({org.hibernate.annotations.CascadeType.SAVE_UPDATE, org.hibernate.annotations.CascadeType.DELETE_ORPHAN })
    @Where(clause = SAMPLES_FILTER)
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
    @SuppressWarnings("unused")
    private void setSamples(final Set<Sample> samplesVal) {
        this.samples = samplesVal;
    }

    /**
     * Gets the extracts.
     *
     * @return the extracts
     */
    @OneToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "EXPERIMENTEXTRACT",
            joinColumns = {@JoinColumn(name = FK_COLUMN_NAME) },
            inverseJoinColumns = {@JoinColumn(name = "EXTRACT_ID") })
    @ForeignKey(name = "EXPERIMENTEXTRACT_INVEST_FK", inverseName = "EXPERIMENTEXTRACT_EXTRACT_FK")
    @Cascade({org.hibernate.annotations.CascadeType.SAVE_UPDATE, org.hibernate.annotations.CascadeType.DELETE_ORPHAN })
    @Where(clause = EXTRACTS_FILTER)
    public Set<Extract> getExtracts() {
        return this.extracts;
    }

    /**
     * Sets the extracts.
     *
     * @param extractsVal the sources
     */
    @SuppressWarnings("unused")
    private void setExtracts(final Set<Extract> extractsVal) {
        this.extracts = extractsVal;
    }

    /**
     * Gets the labeledExtracts.
     *
     * @return the labeledExtracts
     */
    @OneToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "EXPERIMENTLABELEDEXTRACT",
            joinColumns = {@JoinColumn(name = FK_COLUMN_NAME) },
            inverseJoinColumns = {@JoinColumn(name = "LABELED_EXTRACT_ID") })
    @ForeignKey(name = "EXPERIMENTLE_INVEST_FK", inverseName = "EXPERIMENTLE_LE_FK")
    @Cascade({org.hibernate.annotations.CascadeType.SAVE_UPDATE, org.hibernate.annotations.CascadeType.DELETE_ORPHAN })
    @Where(clause = LABELED_EXTRACTS_FILTER)
    public Set<LabeledExtract> getLabeledExtracts() {
        return this.labeledExtracts;
    }

    /**
     * Sets the labeledExtracts.
     *
     * @param labeledExtractsVal the sources
     */
    @SuppressWarnings("unused")
    private void setLabeledExtracts(final Set<LabeledExtract> labeledExtractsVal) {
        this.labeledExtracts = labeledExtractsVal;
    }

    /**
     * Gets the arrayDesigns.
     *
     * @return the arrayDesigns
     */
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "EXPERIMENTARRAYDESIGN",
            joinColumns = {@JoinColumn(name = FK_COLUMN_NAME) },
            inverseJoinColumns = {@JoinColumn(name = "ARRAYDESIGN_ID") })
    @ForeignKey(name = "INVESTARRAYDESIGN_INVEST_FK", inverseName = "INVESTARRAYDESIGN_ARRAYDESIGN_FK")
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
    @SuppressWarnings("unused")
    public void setArrayDesigns(final Set<ArrayDesign> arrayDesignsVal) {
        this.arrayDesigns = arrayDesignsVal;
    }

    /**
     * Gets the experimentContacts.
     *
     * @return the experimentContacts
     */
    @OneToMany(mappedBy = EXPERIMENT_REF, fetch = FetchType.LAZY)
    @Cascade({org.hibernate.annotations.CascadeType.SAVE_UPDATE, org.hibernate.annotations.CascadeType.DELETE_ORPHAN })
    @AttributePolicy(allow = SecurityPolicy.BROWSE_POLICY_NAME)
    public Set<ExperimentContact> getExperimentContacts() {
        return this.experimentContacts;
    }

    /**
     * Sets the experimentContacts.
     *
     * @param experimentContactsVal the experimentContacts
     */
    @SuppressWarnings("unused")
    private void setExperimentContacts(final Set<ExperimentContact> experimentContactsVal) {
        this.experimentContacts = experimentContactsVal;
    }

    /**
     * @return the experimentDesignType
     */
    @ManyToOne
    @ForeignKey(name = "EXPERIMENT_DESIGN_TYPE_FK")
    @Cascade(org.hibernate.annotations.CascadeType.SAVE_UPDATE)
    public Term getExperimentDesignType() {
        return this.experimentDesignType;
    }

    /**
     * @param experimentDesignType the experimentDesignType to set
     */
    public void setExperimentDesignType(Term experimentDesignType) {
        this.experimentDesignType = experimentDesignType;
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
    @SuppressWarnings("unused")
    private void setFactors(final Set<Factor> factorsVal) {
        this.factors = factorsVal;
    }

    /**
     * Gets the normalizationTypes.
     *
     * @return the normalizationTypes
     */
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "NORMALIZATIONTYPE",
            joinColumns = {@JoinColumn(name = FK_COLUMN_NAME) },
            inverseJoinColumns = {@JoinColumn(name = TERM_FK_NAME) })
    @ForeignKey(name = "NORMTYPE_INVEST_FK", inverseName = "NORMTYPE_TERM_FK")
    @Cascade(org.hibernate.annotations.CascadeType.SAVE_UPDATE)
    public Set<Term> getNormalizationTypes() {
        return this.normalizationTypes;
    }

    /**
     * Sets the normalizationTypes.
     *
     * @param normalizationTypesVal the normalizationTypes
     */
    @SuppressWarnings("unused")
    private void setNormalizationTypes(final Set<Term> normalizationTypesVal) {
        this.normalizationTypes = normalizationTypesVal;
    }

    /**
     * Gets the arrays.
     *
     * @return the arrays
     */
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "EXPERIMENTARRAY",
            joinColumns = {@JoinColumn(name = FK_COLUMN_NAME) },
            inverseJoinColumns = {@JoinColumn(name = "ARRAY_ID") })
    @ForeignKey(name = "EXPRARRAY_INVEST_FK", inverseName = "EXPRARRAY_ARRAY_FK")
    @Cascade(org.hibernate.annotations.CascadeType.SAVE_UPDATE)
    public Set<Array> getArrays() {
        return this.arrays;
    }

    /**
     * Sets the arrays.
     *
     * @param arraysVal the arrays
     */
    @SuppressWarnings("unused")
    private void setArrays(final Set<Array> arraysVal) {
        this.arrays = arraysVal;
    }

    /**
     * @return hybridizations
     */
    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = EXPERIMENT_REF)
    @ForeignKey(name = "HYBRIDIZATION_EXPR_FK")
    @Cascade({org.hibernate.annotations.CascadeType.SAVE_UPDATE, org.hibernate.annotations.CascadeType.DELETE_ORPHAN })
    @Where(clause = HYBRIDIZATIONS_FILTER)
    public Set<Hybridization> getHybridizations() {
        return this.hybridizations;
    }

    @SuppressWarnings("unused")
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
    @SuppressWarnings("unused")
    private void setProject(Project project) {
        this.project = project;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
