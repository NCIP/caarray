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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Index;

import gov.nih.nci.caarray.domain.AbstractCaArrayEntity;
import gov.nih.nci.caarray.domain.contact.Organization;
import gov.nih.nci.caarray.domain.file.CaArrayFile;
import gov.nih.nci.caarray.domain.protocol.ProtocolApplication;
import gov.nih.nci.caarray.domain.vocabulary.Term;

/**
 * The design details for a type of microarray.
 */
@Entity
@Table(name = "ARRAYDESIGN")
public class ArrayDesign extends AbstractCaArrayEntity {

    private static final long serialVersionUID = 1234567890L;

    private String name;
    private ProtocolApplication printing;
    private Term polymerType;
    private Integer numberOfFeatures;
    private Term substrateType;
    private Term surfaceType;
    private Term technologyType;
    private String version;
    private CaArrayFile designFile;
    private Organization provider;

    /**
     * Gets the name.
     *
     * @return the name
     */
    @Column(name = "NAME", length = DEFAULT_STRING_COLUMN_SIZE)
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
    @Transient
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
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "POLYMERTYPE_ID")
    @Cascade(CascadeType.SAVE_UPDATE)
    @Index(name = "ARRAYDESIGN_POLYMER_IDX")
    @ForeignKey(name = "POLYMERTYPE_ID")
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
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "SUBSTRATETYPE_ID")
    @Cascade(CascadeType.SAVE_UPDATE)
    @Index(name = "ARRAYDESIGN_SUBSTRAE_IDX")
    @ForeignKey(name = "SUBSTRATETYPE_ID")
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
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "SURFACETYPE_ID")
    @Cascade(CascadeType.SAVE_UPDATE)
    @Index(name = "ARRAYDESIGN_SURFACE_IDX")
    @ForeignKey(name = "SURFACETYPE_ID")
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
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "TECHNOLOGYTYPE_ID")
    @Cascade(CascadeType.SAVE_UPDATE)
    @Index(name = "ARRAYDESIGN_TECHNOLOGY_IDX")
    @ForeignKey(name = "TECHNOLOGIYTYPE_ID")
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
    @Column(name = "VERSION", length = DEFAULT_STRING_COLUMN_SIZE)
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
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "PROVIDER_ID")
    @Cascade(CascadeType.SAVE_UPDATE)
    @Index(name = "ARRAYDESIGN_PROVIDER_IDX")
    @ForeignKey(name = "PROVIDER_ID")
    public Organization getProvider() {
        return provider;
    }

    /**
     * Sets the provider.
     *
     * @param providerVal the provider
     */
    public void setProvider(final
      Organization providerVal) {
        this.provider = providerVal;
    }

    /**
     * Gets the printing.
     *
     * @return the printing
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "PRINTING_ID")
    @Index(name = "ARRAYDESIGN_PRINTING_IDX")
    @ForeignKey(name = "PRINTING_ID")
    public ProtocolApplication getPrinting() {
        return printing;
    }

    /**
     * Sets the printing.
     *
     * @param printingVal the printing
     */
    public void setPrinting(final
      ProtocolApplication printingVal) {
        this.printing = printingVal;
    }

    /**
     * @return the designFile
     */
    @Transient
    public CaArrayFile getDesignFile() {
        return designFile;
    }

    /**
     * @param designFile the designFile to set
     */
    public void setDesignFile(CaArrayFile designFile) {
        this.designFile = designFile;
    }

    /**
     * Checks if given object is equal to this object.
     *
     * @param obj the object to compare to this object
     * @return true if they are equal, false if they are not
     */
    @Override
    public boolean equals(final Object obj) {
        boolean theyAreEqual = false;
        if (obj instanceof gov.nih.nci.caarray.domain.array.ArrayDesign) {
            final gov.nih.nci.caarray.domain.array.ArrayDesign castObject =
                (gov.nih.nci.caarray.domain.array.ArrayDesign) obj;
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
    @Override
    public int hashCode() {
        int theHashCode = 0;
        if (getId() != null) {
            theHashCode += getId().hashCode();
        }
        return theHashCode;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
