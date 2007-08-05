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

import gov.nih.nci.caarray.domain.AbstractCaArrayEntity;
import gov.nih.nci.caarray.domain.array.Array;
import gov.nih.nci.caarray.domain.array.ArrayDesign;
import gov.nih.nci.caarray.domain.publication.Publication;
import gov.nih.nci.caarray.domain.sample.Sample;
import gov.nih.nci.caarray.domain.sample.Source;
import gov.nih.nci.caarray.domain.vocabulary.Term;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.ForeignKey;

/**
 *
 */
@Entity
public class Investigation extends AbstractCaArrayEntity {
    private static final String UNUSED = "unused";
    private static final String FK_COLUMN_NAME = "INVESTIGATION_ID";
    private static final String TERM_FK_NAME = "TERM_ID";

    /**
     * The serial version UID for serialization.
     */
    private static final long serialVersionUID = 1234567890L;

    private Date dateOfExperiment;

    /**
     * Creates a new, empty <code>Investigation</code>.
     *
     * @return the initialized <code>Investigation</code>.
     */
    public static Investigation createNew() {
        return new Investigation();
    }

    /**
     * Gets the dateOfExperiment.
     *
     * @return the dateOfExperiment
     */
    @Temporal(TemporalType.TIMESTAMP)
    public Date getDateOfExperiment() {
        return dateOfExperiment;
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
     * The description String.
     */
    private String description;

    /**
     * Gets the description.
     *
     * @return the description
     */
    @Column(length = DEFAULT_STRING_COLUMN_SIZE)
    public String getDescription() {
        return description;
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
     * The publicReleaseDate java.util.Date.
     */
    private Date publicReleaseDate;

    /**
     * Gets the publicReleaseDate.
     *
     * @return the publicReleaseDate
     */
    @Temporal(TemporalType.TIMESTAMP)
    public Date getPublicReleaseDate() {
        return publicReleaseDate;
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
     * The title String.
     */
    private String title;

    /**
     * Gets the title.
     *
     * @return the title
     */
    @Column(length = DEFAULT_STRING_COLUMN_SIZE)
    public String getTitle() {
        return title;
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
     * The qualityControlTypes set.
     */
    private Set<Term> qualityControlTypes = new HashSet<Term>();

    /**
     * Gets the qualityControlTypes.
     *
     * @return the qualityControlTypes
     */
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "QUALITYCONTROLTYPE",
            joinColumns = { @JoinColumn(name = FK_COLUMN_NAME) },
            inverseJoinColumns = { @JoinColumn(name = TERM_FK_NAME) }
    )
    @ForeignKey(name = "QUALCTRLTYPE_INVEST_IDX", inverseName = "QUALCTRLTYPE_TERM_IDX")
    @Cascade(org.hibernate.annotations.CascadeType.SAVE_UPDATE)
    public Set<Term> getQualityControlTypes() {
        return qualityControlTypes;
    }

    /**
     * Sets the qualityControlTypes.
     *
     * @param qualityControlTypesVal the qualityControlTypes
     */
    @SuppressWarnings(UNUSED)
    private void setQualityControlTypes(final Set<Term> qualityControlTypesVal) { // NOPMD
        this.qualityControlTypes = qualityControlTypesVal;
    }

    /**
     * The publications set.
     */
    private Set<Publication> publications = new HashSet<Publication>();

    /**
     * Gets the publications.
     *
     * @return the publications
     */
    @OneToMany(mappedBy = "investigation", fetch = FetchType.EAGER)
    public Set<Publication> getPublications() {
        return publications;
    }

    /**
     * Sets the publications.
     *
     * @param publicationsVal the publications
     */
    @SuppressWarnings(UNUSED)
    private void setPublications(final Set<Publication> publicationsVal) {  // NOPMD
        this.publications = publicationsVal;
    }

    /**
     * The replicateTypes set.
     */
    private Set<Term> replicateTypes = new HashSet<Term>();

    /**
     * Gets the replicateTypes.
     *
     * @return the replicateTypes
     */
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "REPLICATETYPE",
            joinColumns = { @JoinColumn(name = FK_COLUMN_NAME) },
            inverseJoinColumns = { @JoinColumn(name = TERM_FK_NAME) }
    )
    @ForeignKey(name = "REPLTYPE_INVEST_IDX", inverseName = "REPLTYPE_TERM_IDX")
    @Cascade(org.hibernate.annotations.CascadeType.SAVE_UPDATE)
    public Set<Term> getReplicateTypes() {
        return replicateTypes;
    }

    /**
     * Sets the replicateTypes.
     *
     * @param replicateTypesVal the replicateTypes
     */
    @SuppressWarnings(UNUSED)
    private void setReplicateTypes(final Set<Term> replicateTypesVal) {  // NOPMD
        this.replicateTypes = replicateTypesVal;
    }

    /**
     * The sources set.
     */
    private Set<Source> sources = new HashSet<Source>();

    /**
     * Gets the sources.
     *
     * @return the sources
     */
    @Transient
    public Set<Source> getSources() {
        return sources;
    }

    /**
     * Sets the sources.
     *
     * @param sourcesVal the sources
     */
    @SuppressWarnings(UNUSED)
    private void setSources(final Set<Source> sourcesVal) {  // NOPMD
        this.sources = sourcesVal;
    }

    /**
     * The samples set.
     */
    private Set<Sample> samples = new HashSet<Sample>();

    /**
     * Gets the samples.
     *
     * @return the samples
     */
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "INVESTIGATIONSAMPLE",
            joinColumns = { @JoinColumn(name = FK_COLUMN_NAME) },
            inverseJoinColumns = { @JoinColumn(name = "SAMPLE_ID") }
    )
    @ForeignKey(name = "INVESTIGATIONSAMPLE_INVEST_IDX", inverseName = "INVESTIGATIONSAMPLE_SAMPLE_IDX")
    @Cascade(org.hibernate.annotations.CascadeType.SAVE_UPDATE)
    public Set<Sample> getSamples() {
        return samples;
    }

    /**
     * Sets the samples.
     *
     * @param samplesVal the sources
     */
    @SuppressWarnings(UNUSED)
    private void setSamples(final Set<Sample> samplesVal) {  // NOPMD
        this.samples = samplesVal;
    }

    /**
     * The arrayDesigns set.
     */
    private Set<ArrayDesign> arrayDesigns = new HashSet<ArrayDesign>();

    /**
     * Gets the arrayDesigns.
     *
     * @return the arrayDesigns
     */
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "INVESTIGATIONARRAYDESIGN",
            joinColumns = { @JoinColumn(name = FK_COLUMN_NAME) },
            inverseJoinColumns = { @JoinColumn(name = "ARRAYDESIGN_ID") }
    )
    @ForeignKey(name = "INVESTARRAYDESIGN_INVEST_IDX", inverseName = "INVESTARRAYDESIGN_ARRAYDESIGN_IDX")
    @Cascade(org.hibernate.annotations.CascadeType.SAVE_UPDATE)
    public Set<ArrayDesign> getArrayDesigns() {
        return arrayDesigns;
    }

    /**
     * Sets the arrayDesigns.
     *
     * @param arrayDesignsVal the arrayDesigns
     */
    @SuppressWarnings(UNUSED)
    private void setArrayDesigns(final Set<ArrayDesign> arrayDesignsVal) {  // NOPMD
        this.arrayDesigns = arrayDesignsVal;
    }

    /**
     * The investigationContacts set.
     */
    private Set<InvestigationContact> investigationContacts = new HashSet<InvestigationContact>();

    /**
     * Gets the investigationContacts.
     *
     * @return the investigationContacts
     */
    @OneToMany(mappedBy = "investigation", fetch = FetchType.EAGER)
    @Cascade(org.hibernate.annotations.CascadeType.SAVE_UPDATE)
    public Set<InvestigationContact> getInvestigationContacts() {
        return investigationContacts;
    }

    /**
     * Sets the investigationContacts.
     *
     * @param investigationContactsVal the investigationContacts
     */
    @SuppressWarnings(UNUSED)
    private void setInvestigationContacts(final Set<InvestigationContact> investigationContactsVal) {  // NOPMD
        this.investigationContacts = investigationContactsVal;
    }

    /**
     * The factors set.
     */
    private Set<Factor> factors = new HashSet<Factor>();

    /**
     * Gets the factors.
     *
     * @return the factors
     */
    @OneToMany(mappedBy = "investigation", fetch = FetchType.EAGER)
    @Cascade(org.hibernate.annotations.CascadeType.SAVE_UPDATE)
    public Set<Factor> getFactors() {
        return factors;
    }

    /**
     * Sets the factors.
     *
     * @param factorsVal the factors
     */
    @SuppressWarnings(UNUSED)
    private void setFactors(final Set<Factor> factorsVal) {  // NOPMD
        this.factors = factorsVal;
    }

    /**
     * The normalizationTypes set.
     */
    private Set<Term> normalizationTypes = new HashSet<Term>();

    /**
     * Gets the normalizationTypes.
     *
     * @return the normalizationTypes
     */
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "NORMALIZATIONTYPE",
            joinColumns = { @JoinColumn(name = FK_COLUMN_NAME) },
            inverseJoinColumns = { @JoinColumn(name = TERM_FK_NAME) }
    )
    @ForeignKey(name = "NORMTYPE_INVEST_IDX", inverseName = "NORMTYPE_TERM_IDX")
    @Cascade(org.hibernate.annotations.CascadeType.SAVE_UPDATE)
    public Set<Term> getNormalizationTypes() {
        return normalizationTypes;
    }

    /**
     * Sets the normalizationTypes.
     *
     * @param normalizationTypesVal the normalizationTypes
     */
    @SuppressWarnings(UNUSED)
    private void setNormalizationTypes(final Set<Term> normalizationTypesVal) {  // NOPMD
        this.normalizationTypes = normalizationTypesVal;
    }

    /**
     * The arrays set.
     */
    private Set<Array> arrays = new HashSet<Array>();

    /**
     * Gets the arrays.
     *
     * @return the arrays
     */
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "INVESTIGATIONARRAY",
            joinColumns = { @JoinColumn(name = FK_COLUMN_NAME) },
            inverseJoinColumns = { @JoinColumn(name = "ARRAY_ID") }
    )
    @ForeignKey(name = "INVESTARRAY_INVEST_IDX", inverseName = "INVESTARRAY_ARRAY_IDX")
    @Cascade(org.hibernate.annotations.CascadeType.SAVE_UPDATE)
    public Set<Array> getArrays() {
        return arrays;
    }

    /**
     * Sets the arrays.
     *
     * @param arraysVal the arrays
     */
    @SuppressWarnings(UNUSED)
    private void setArrays(final Set<Array> arraysVal) {  // NOPMD
        this.arrays = arraysVal;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
