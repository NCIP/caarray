//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
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
