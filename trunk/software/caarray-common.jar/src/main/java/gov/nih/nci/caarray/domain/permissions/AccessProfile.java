/**
 * The software subject to this notice and license includes both human readable
 * source code form and machine readable, binary, object code form. The caarray-common-jar
 * Software was developed in conjunction with the National Cancer Institute
 * (NCI) by NCI employees and 5AM Solutions, Inc. (5AM). To the extent
 * government employees are authors, any rights in such works shall be subject
 * to Title 17 of the United States Code, section 105.
 *
 * This caarray-common-jar Software License (the License) is between NCI and You. You (or
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
 * its rights in the caarray-common-jar Software to (i) use, install, access, operate,
 * execute, copy, modify, translate, market, publicly display, publicly perform,
 * and prepare derivative works of the caarray-common-jar Software; (ii) distribute and
 * have distributed to and by third parties the caarray-common-jar Software and any
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
package gov.nih.nci.caarray.domain.permissions;

import gov.nih.nci.caarray.domain.AbstractCaArrayObject;
import gov.nih.nci.caarray.domain.project.Project;
import gov.nih.nci.caarray.domain.sample.Sample;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Transient;

import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CollectionOfElements;
import org.hibernate.annotations.MapKeyManyToMany;

import com.fiveamsolutions.nci.commons.data.persistent.PersistentObject;

/**
 * Container class that models the read and write permissions to samples.
 */
@Entity
@BatchSize(size = AbstractCaArrayObject.DEFAULT_BATCH_SIZE)
@SuppressWarnings({"PMD.AvoidDuplicateLiterals", "PMD.CyclomaticComplexity" })
public class AccessProfile implements PersistentObject, Serializable, Comparable<AccessProfile> {

    private static final long serialVersionUID = -7994016784349522735L;

    private Long id;
    private SecurityLevel securityLevel;
    private Map<Sample, SampleSecurityLevel> sampleSecurityLevels = new HashMap<Sample, SampleSecurityLevel>();
    private Project projectForPublicProfile;
    private Project projectForGroupProfile;
    private CollaboratorGroup group;

    /**
     * Hibernate-only constructor.
     */
    public AccessProfile() {
        // no body
    }

    /**
     * Creates a new profile with given initial security level.
     * @param securityLevel initial security level
     */
    public AccessProfile(SecurityLevel securityLevel) {
        this.securityLevel = securityLevel;
    }

    /**
     * @return database identifier
     */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Long getId() {
        return this.id;
    }

    /**
     * Sets the id.
     *
     * @param id the id to set
     * @deprecated should only be used by castor, hibernate and struts
     */
    @Deprecated
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Hibernate use only.
     * @return the securityLevel
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, name = "security_level")
    private SecurityLevel getSecurityLevelInternal() {
        return this.securityLevel;
    }

    /**
     * Hibernate use only.
     * @param securityLevel the securityLevel to set
     */
    private void setSecurityLevelInternal(SecurityLevel securityLevelInternal) {
        this.securityLevel = securityLevelInternal;
    }

    /**
     * @return the securityLevel
     */
    @Transient
    public SecurityLevel getSecurityLevel() {
        return getSecurityLevelInternal();
    }

    /**
     * @param securityLevel the securityLevel to set
     */
    @SuppressWarnings("PMD.CyclomaticComplexity")
    public void setSecurityLevel(SecurityLevel securityLevel) {
        setSecurityLevelInternal(securityLevel);
        if (!securityLevel.isSampleLevelPermissionsAllowed()) {
            this.sampleSecurityLevels.clear();
        } else if (securityLevel == SecurityLevel.READ_SELECTIVE) {
            for (Map.Entry<Sample, SampleSecurityLevel> sampleEntry : this.sampleSecurityLevels.entrySet()) {
                if (sampleEntry.getValue() == SampleSecurityLevel.READ_WRITE) {
                    sampleEntry.setValue(SampleSecurityLevel.READ);
                }
            }
        }
        if (!securityLevel.isAvailableToPublic() && isPublicProfile()) {
            throw new IllegalArgumentException("Security level not legal for a public profile: " + securityLevel);
        }
        if (!securityLevel.isAvailableToGroups() && isGroupProfile()) {
            throw new IllegalArgumentException("Security level not legal for a collaborator group profile: "
                    + securityLevel);
        }
    }

    /**
     * @return Mapping of samples to the security level for each sample
     */
    @CollectionOfElements(fetch = FetchType.LAZY)
    @MapKeyManyToMany(joinColumns = @JoinColumn(name = "sample_id", nullable = false))
    @JoinTable(name = "access_profile_samples", joinColumns = @JoinColumn(name = "access_profile_id"))
    @Column(name = "security_level")
    @Enumerated(EnumType.STRING)
    @Cascade({org.hibernate.annotations.CascadeType.SAVE_UPDATE, org.hibernate.annotations.CascadeType.DELETE })
    public Map<Sample, SampleSecurityLevel> getSampleSecurityLevels() {
        return this.sampleSecurityLevels;
    }

    /**
     * @param sampleSecurityLevels the sampleSecurityLevels to set
     */
    public void setSampleSecurityLevels(Map<Sample, SampleSecurityLevel> sampleSecurityLevels) {
        this.sampleSecurityLevels = sampleSecurityLevels;
    }

    /**
     * @return the projectForPublicProfile
     */
    @OneToOne(mappedBy = "publicProfile", fetch = FetchType.LAZY)
    private Project getProjectForPublicProfile() {
        return projectForPublicProfile;
    }

    /**
     * @param projectForPublicProfile the projectForPublicProfile to set
     * DEVELOPER NOTE:
     * This method should not generally never be called. It needs to remain public
     * as it must be called by Project to establish the symmetric link
     */
    public void setProjectForPublicProfile(Project projectForPublicProfile) {
        this.projectForPublicProfile = projectForPublicProfile;
    }

    /**
     * @return the projectForGroupProfile
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", insertable = false, updatable = false)
    private Project getProjectForGroupProfile() {
        return projectForGroupProfile;
    }

    /**
     * @param projectForGroupProfile the projectForGroupProfile to set
     * DEVELOPER NOTE:
     * This method should not generally never be called. It needs to remain public
     * as it must be called by Project to establish the symmetric link
     */
    public void setProjectForGroupProfile(Project projectForGroupProfile) {
        this.projectForGroupProfile = projectForGroupProfile;
    }

    /**
     * @return whether this is a public profile
     */
    @Transient
    public boolean isPublicProfile() {
        return this.projectForPublicProfile != null;
    }

    /**
     * @return whether this is a group profile
     */
    @Transient
    public boolean isGroupProfile() {
        return this.projectForGroupProfile != null;
    }

    /**
     * @return the project to which this access profile belongs
     */
    @Transient
    public Project getProject() {
        if (isPublicProfile()) {
            return getProjectForPublicProfile();
        } else {
            return getProjectForGroupProfile();
        }
    }

    /**
     * @return the group
     */
    @ManyToOne
    @JoinColumn(name = "group_id", insertable = false, updatable = false)
    public CollaboratorGroup getGroup() {
        return this.group;
    }

    /**
     * @param group for group access profiles only, the group to which this profile corresponds
     * This method should not generally never be called. It needs to remain public
     * as it must be called by Project.addProfile to establish the symmetric link
     */
    public void setGroup(CollaboratorGroup group) {
        this.group = group;
    }
    /**
     * Compares Access Profiles by group name, putting "The Public" after Collaboration groups.
     * @param profile other Access Profile to compare to
     * @return result of comparison
     */
    public int compareTo(AccessProfile profile) {
        if (this.isPublicProfile()) {
            return 1;
        } else if (profile.isPublicProfile()) {
            return -1;
        } else {
            return this.getGroup().getGroup().getGroupName().compareTo(profile.getGroup().getGroup().getGroupName());
        }
    }
}
