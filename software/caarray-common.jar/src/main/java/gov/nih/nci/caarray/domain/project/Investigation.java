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

  /**

   */

public class Investigation extends AbstractCaArrayEntity {
    /**
     * The serial version UID for serialization.
     */
    private static final long serialVersionUID = 1234567890L;
    private static final String UNCHECKED = "unchecked";

    /**
     * The dateOfExperiment java.util.Date.
     */
    private java.util.Date dateOfExperiment;

    /**
     * Gets the dateOfExperiment.
     *
     * @return the dateOfExperiment
     */
    public java.util.Date getDateOfExperiment() {
        return dateOfExperiment;
    }

    /**
     * Sets the dateOfExperiment.
     *
     * @param dateOfExperimentVal the dateOfExperiment
     */
    public void setDateOfExperiment(final java.util.Date dateOfExperimentVal) {
        this.dateOfExperiment = dateOfExperimentVal;
    }
    /**
     * The description java.lang.String.
     */
    private java.lang.String description;

    /**
     * Gets the description.
     *
     * @return the description
     */
    public java.lang.String getDescription() {
        return description;
    }

    /**
     * Sets the description.
     *
     * @param descriptionVal the description
     */
    public void setDescription(final java.lang.String descriptionVal) {
        this.description = descriptionVal;
    }
    /**
     * The publicReleaseDate java.util.Date.
     */
    private java.util.Date publicReleaseDate;

    /**
     * Gets the publicReleaseDate.
     *
     * @return the publicReleaseDate
     */
    public java.util.Date getPublicReleaseDate() {
        return publicReleaseDate;
    }

    /**
     * Sets the publicReleaseDate.
     *
     * @param publicReleaseDateVal the publicReleaseDate
     */
    public void setPublicReleaseDate(final java.util.Date publicReleaseDateVal) {
        this.publicReleaseDate = publicReleaseDateVal;
    }
    /**
     * The title java.lang.String.
     */
    private java.lang.String title;

    /**
     * Gets the title.
     *
     * @return the title
     */
    public java.lang.String getTitle() {
        return title;
    }

    /**
     * Sets the title.
     *
     * @param titleVal the title
     */
    public void setTitle(final java.lang.String titleVal) {
        this.title = titleVal;
    }

    /**
     * The qualityControlTypes set.
     */
    private final java.util.Collection qualityControlTypes = new java.util.HashSet();

    /**
     * Gets the qualityControlTypes.
     *
     * @return the qualityControlTypes
     */
    public java.util.Collection getQualityControlTypes() {
        return qualityControlTypes;
    }

    /**
     * Sets the qualityControlTypes.
     *
     * @param qualityControlTypesVal the qualityControlTypes
     */
    @SuppressWarnings(UNCHECKED)
    public void setQualityControlTypes(final java.util.Collection qualityControlTypesVal) {
        this.qualityControlTypes.addAll(qualityControlTypesVal);
    }

    /**
     * The publications set.
     */
    private final java.util.Collection publications = new java.util.HashSet();

    /**
     * Gets the publications.
     *
     * @return the publications
     */
    public java.util.Collection getPublications() {
        return publications;
    }

    /**
     * Sets the publications.
     *
     * @param publicationsVal the publications
     */
    @SuppressWarnings(UNCHECKED)
    public void setPublications(final java.util.Collection publicationsVal) {
        this.publications.addAll(publicationsVal);
    }

    /**
     * The replicateTypes set.
     */
    private final java.util.Collection replicateTypes = new java.util.HashSet();

    /**
     * Gets the replicateTypes.
     *
     * @return the replicateTypes
     */
    public java.util.Collection getReplicateTypes() {
        return replicateTypes;
    }

    /**
     * Sets the replicateTypes.
     *
     * @param replicateTypesVal the replicateTypes
     */
    @SuppressWarnings(UNCHECKED)
    public void setReplicateTypes(final java.util.Collection replicateTypesVal) {
        this.replicateTypes.addAll(replicateTypesVal);
    }

    /**
     * The arrayDesigns set.
     */
    private final java.util.Collection arrayDesigns = new java.util.HashSet();

    /**
     * Gets the arrayDesigns.
     *
     * @return the arrayDesigns
     */
    public java.util.Collection getArrayDesigns() {
        return arrayDesigns;
    }

    /**
     * Sets the arrayDesigns.
     *
     * @param arrayDesignsVal the arrayDesigns
     */
    @SuppressWarnings(UNCHECKED)
    public void setArrayDesigns(final java.util.Collection arrayDesignsVal) {
        this.arrayDesigns.addAll(arrayDesignsVal);
    }

    /**
     * The investigationContacts set.
     */
    private final java.util.Collection investigationContacts = new java.util.HashSet();

    /**
     * Gets the investigationContacts.
     *
     * @return the investigationContacts
     */
    public java.util.Collection getInvestigationContacts() {
        return investigationContacts;
    }

    /**
     * Sets the investigationContacts.
     *
     * @param investigationContactsVal the investigationContacts
     */
    @SuppressWarnings(UNCHECKED)
    public void setInvestigationContacts(final java.util.Collection investigationContactsVal) {
        this.investigationContacts.addAll(investigationContactsVal);
    }

    /**
     * The factors set.
     */
    private final java.util.Collection factors = new java.util.HashSet();

    /**
     * Gets the factors.
     *
     * @return the factors
     */
    public java.util.Collection getFactors() {
        return factors;
    }

    /**
     * Sets the factors.
     *
     * @param factorsVal the factors
     */
    @SuppressWarnings(UNCHECKED)
    public void setFactors(final java.util.Collection factorsVal) {
        this.factors.addAll(factorsVal);
    }

    /**
     * The normalizationTypes set.
     */
    private final java.util.Collection normalizationTypes = new java.util.HashSet();

    /**
     * Gets the normalizationTypes.
     *
     * @return the normalizationTypes
     */
    public java.util.Collection getNormalizationTypes() {
        return normalizationTypes;
    }

    /**
     * Sets the normalizationTypes.
     *
     * @param normalizationTypesVal the normalizationTypes
     */
    @SuppressWarnings(UNCHECKED)
    public void setNormalizationTypes(final java.util.Collection normalizationTypesVal) {
        this.normalizationTypes.addAll(normalizationTypesVal);
    }

    /**
     * The arrays set.
     */
    private final java.util.Collection arrays = new java.util.HashSet();

    /**
     * Gets the arrays.
     *
     * @return the arrays
     */
    public java.util.Collection getArrays() {
        return arrays;
    }

    /**
     * Sets the arrays.
     *
     * @param arraysVal the arrays
     */
    @SuppressWarnings(UNCHECKED)
    public void setArrays(final java.util.Collection arraysVal) {
        this.arrays.addAll(arraysVal);
    }

    /**
     * Checks if given object is equal to this object.
     *
     * @param obj the object to compare to this object
     * @return true if they are equal, false if they are not
     */
    public boolean equals(final Object obj) {
        boolean theyAreEqual = false;
        if (obj instanceof gov.nih.nci.caarray.domain.project.Investigation) {
            final gov.nih.nci.caarray.domain.project.Investigation castObject =
                (gov.nih.nci.caarray.domain.project.Investigation) obj;
            Long thisId = getId();
            if (thisId != null && thisId.equals(castObject.getId())) {
                theyAreEqual = true;
            }
        }
        return theyAreEqual;
    }

    /**
     * Returns the hashcode for the object.
     *
     * @return the int hashcode
     */
    public int hashCode() {
        int theHashCode = 0;
        if (getId() != null) {
            theHashCode += getId().hashCode();
        }
        return theHashCode;
    }
}
