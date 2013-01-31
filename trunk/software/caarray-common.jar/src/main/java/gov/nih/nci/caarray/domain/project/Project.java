//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================

package gov.nih.nci.caarray.domain.project;

import gov.nih.nci.caarray.domain.AbstractCaArrayEntity;
import gov.nih.nci.caarray.domain.AbstractCaArrayObject;
import gov.nih.nci.caarray.domain.contact.Person;
import gov.nih.nci.caarray.domain.file.CaArrayFile;
import gov.nih.nci.caarray.domain.file.CaArrayFileSet;
import gov.nih.nci.caarray.domain.file.FileStatus;
import gov.nih.nci.caarray.domain.permissions.AccessProfile;
import gov.nih.nci.caarray.domain.permissions.CollaboratorGroup;
import gov.nih.nci.caarray.domain.permissions.SecurityLevel;
import gov.nih.nci.caarray.security.AttributePolicy;
import gov.nih.nci.caarray.security.Protectable;
import gov.nih.nci.caarray.security.SecurityPolicy;
import gov.nih.nci.caarray.security.SecurityUtils;
import gov.nih.nci.caarray.util.UsernameHolder;
import gov.nih.nci.security.authorization.domainobjects.User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.lang.CharSetUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.CompareToBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.MapKeyManyToMany;
import org.hibernate.annotations.Sort;
import org.hibernate.annotations.SortType;
import org.hibernate.validator.NotNull;
import org.hibernate.validator.Valid;

/**
 * A microarray project.
 */
@Entity
@BatchSize(size = AbstractCaArrayObject.DEFAULT_BATCH_SIZE)
@SuppressWarnings({ "PMD.AvoidDuplicateLiterals", "PMD.TooManyFields", "PMD.TooManyMethods",
        "PMD.ExcessiveClassLength" })
public class Project extends AbstractCaArrayEntity implements Comparable<Project>, Protectable {
    private static final long serialVersionUID = 1234567890L;

    private static final int PUBLIC_ID_COMPONENT_LENGTH = 5;

    private boolean locked;
    private Experiment experiment = new Experiment();
    private SortedSet<CaArrayFile> files = new TreeSet<CaArrayFile>();
    private SortedSet<CaArrayFile> importedFiles = new TreeSet<CaArrayFile>();
    private SortedSet<CaArrayFile> supplementalFiles = new TreeSet<CaArrayFile>();
    private SortedSet<CaArrayFile> unImportedFiles = new TreeSet<CaArrayFile>();
    private AccessProfile publicProfile = new AccessProfile(SecurityLevel.NO_VISIBILITY);
    private Map<CollaboratorGroup, AccessProfile> groupProfiles = new HashMap<CollaboratorGroup, AccessProfile>();
    private transient Set<User> owners;
    private Date lastUpdated = new Date();

    /**
     * Hibernate and caster constructor.
     */
    public Project() {
        // hibernate & caster-only constructor
        this.publicProfile.setProjectForPublicProfile(this);
    }

    /**
     * Gets the workflow status of this project. Hibernate use only
     *
     * @return the true if this project is in a lock box.
     */
    @NotNull
    @Column(name = "locked")
    @AttributePolicy(allow = SecurityPolicy.BROWSE_POLICY_NAME)
    public boolean isLocked() {
        return this.locked;
    }

    /**
     * Sets the workflow status of this project. Hibernate use only
     *
     * @param locked the locked status to set.
     */
    public void setLocked(boolean locked) {
        this.locked = locked;
    }

    /**
     * @return whether this project currently has any files that are being actively imported.
     */
    @Transient
    public boolean isImportingData() {
        return CollectionUtils.exists(getFiles(), new Predicate() {
            public boolean evaluate(Object o) {
                CaArrayFile file = (CaArrayFile) o;
                return file.getFileStatus().equals(FileStatus.IMPORTING);
            }
        });
    }

    /**
     * Gets the experiment.
     *
     * @return the experiment
     */
    @ManyToOne
    @JoinColumn(unique = true)
    @Cascade({ org.hibernate.annotations.CascadeType.SAVE_UPDATE, org.hibernate.annotations.CascadeType.DELETE })
    @ForeignKey(name = "project_experiment_fk")
    @AttributePolicy(allow = SecurityPolicy.BROWSE_POLICY_NAME)
    @Valid
    public Experiment getExperiment() {
        return this.experiment;
    }

    /**
     * Sets the experiment.
     *
     * @param experimentVal the experiment
     */
    public void setExperiment(final Experiment experimentVal) {
        this.experiment = experimentVal;
    }

    /**
     * Gets the files.
     *
     * @return the files
     */
    @OneToMany(mappedBy = "project", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @Sort(type = SortType.NATURAL)
    @Filter(name = "Project1", condition = Experiment.FILES_FILTER)
    public SortedSet<CaArrayFile> getFiles() {
        return this.files;
    }

    @SuppressWarnings({"unused", "PMD.UnusedPrivateMethod" })
    private void setFiles(final SortedSet<CaArrayFile> filesVal) {
        this.files = filesVal;
    }

    /**
     * Get the files.
     *
     * @return the files.
     */
    @Transient
    public SortedSet<CaArrayFile> getImportedFiles() {
        return Collections.unmodifiableSortedSet(getImportedFileSet());
    }

    /**
     * Gets the files.
     *
     * @return the files
     */
    @OneToMany(mappedBy = "project", fetch = FetchType.LAZY)
    @Sort(type = SortType.NATURAL)
    @Filter(name = "Project1", condition = "(status = 'IMPORTED' or status = 'IMPORTED_NOT_PARSED') and "
        + Experiment.FILES_FILTER)
    private SortedSet<CaArrayFile> getImportedFileSet() {
        return this.importedFiles;
    }

    /**
     * Get the files.
     *
     * @return the files.
     */
    @Transient
    public SortedSet<CaArrayFile> getSupplementalFiles() {
        return Collections.unmodifiableSortedSet(getSupplementalFileSet());
    }

    /**
     * Gets the files.
     *
     * @return the files
     */
    @OneToMany(mappedBy = "project", fetch = FetchType.LAZY)
    @Sort(type = SortType.NATURAL)
    @Filter(name = "Project1", condition = "status = 'SUPPLEMENTAL' and " + Experiment.FILES_FILTER)
    private SortedSet<CaArrayFile> getSupplementalFileSet() {
        return this.supplementalFiles;
    }

    @SuppressWarnings({"unused", "PMD.UnusedPrivateMethod" })
    private void setSupplementalFileSet(final SortedSet<CaArrayFile> filesVal) {
        this.supplementalFiles = filesVal;
    }

    @SuppressWarnings({"unused", "PMD.UnusedPrivateMethod" })
    private void setImportedFileSet(final SortedSet<CaArrayFile> filesVal) {
        this.importedFiles = filesVal;
    }

    /**
     * Get the files.
     *
     * @return the files.
     */
    @Transient
    public SortedSet<CaArrayFile> getUnImportedFiles() {
        return Collections.unmodifiableSortedSet(getUnImportedFileSet());
    }

    /**
     * Gets the files.
     *
     * @return the files
     */
    @OneToMany(mappedBy = "project", fetch = FetchType.LAZY)
    @Sort(type = SortType.NATURAL)
    @Filter(name = "Project1", condition = "status != 'IMPORTED' and status != 'IMPORTED_NOT_PARSED' "
        + "and status != 'SUPPLEMENTAL' and " + Experiment.FILES_FILTER)
    private SortedSet<CaArrayFile> getUnImportedFileSet() {
        return this.unImportedFiles;
    }

    @SuppressWarnings({"unused", "PMD.UnusedPrivateMethod" })
    private void setUnImportedFileSet(final SortedSet<CaArrayFile> filesVal) {
        this.unImportedFiles = filesVal;
    }

    /**
     * @return the files contained in the project as a set.
     */
    @Transient
    public CaArrayFileSet getFileSet() {
        CaArrayFileSet fileSet = new CaArrayFileSet(this);
        fileSet.addAll(this.files);
        return fileSet;
    }

    /**
     * @return the names of the files contained in this project.
     */
    @Transient
    public Set<String> getFileNames() {
        // create set of existing files
        Set<String> existingFileNameSet = new HashSet<String>();
        for (CaArrayFile file : getFiles()) {
            existingFileNameSet.add(file.getName());
        }
        return existingFileNameSet;
    }


    /**
     * @return public access profile
     */
    @ManyToOne(cascade = {CascadeType.ALL }, fetch = FetchType.LAZY)
    @JoinColumn(unique = true)
    @ForeignKey(name = "project_publicaccess_fk")
    @AttributePolicy(allow = SecurityPolicy.BROWSE_POLICY_NAME)
    public AccessProfile getPublicProfile() {
        return this.publicProfile;
    }

    @SuppressWarnings({"unused", "PMD.UnusedPrivateMethod" })
    private void setPublicProfile(AccessProfile profile) {
        this.publicProfile = profile;
    }

    /**
     * @return collaborator access profiles
     */
    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id")
    @MapKeyManyToMany(joinColumns = @JoinColumn(name = "group_id"))
    @Cascade(org.hibernate.annotations.CascadeType.SAVE_UPDATE)
    private Map<CollaboratorGroup, AccessProfile> getGroupProfilesMap() {
        return this.groupProfiles;
    }

    @SuppressWarnings({"unused", "PMD.UnusedPrivateMethod" })
    private void setGroupProfilesMap(Map<CollaboratorGroup, AccessProfile> profiles) {
        this.groupProfiles = profiles;
    }

    /**
     * @return collaborator access profiles
     */
    @Transient
    public Map<CollaboratorGroup, AccessProfile> getGroupProfiles() {
        return Collections.unmodifiableMap(getGroupProfilesMap());
    }

    /**
     * Add a new AccessProfile for a collaborator group unless the project already has a profile for this group.
     *
     * @param group to add profile for
     * @return if there already existed a profile for that group, then it is returned, otherwise a
     * new profile is added and returned
     */
    public AccessProfile addGroupProfile(CollaboratorGroup group) {
        AccessProfile profile = new AccessProfile(SecurityLevel.NONE);
        this.groupProfiles.put(group, profile);
        profile.setProjectForGroupProfile(this);
        profile.setGroup(group);
        return profile;
    }

    /**
     * remove the access profile for given collaborator group from this project.
     *
     * @param group to remove profile for
     * @return the profile previously associated with given group, or null if there weren't any
     */
    public AccessProfile removeGroupProfile(CollaboratorGroup group) {
        return this.groupProfiles.remove(group);
    }

    /**
     * @return all the access profiles associated with this project (public, host, and the various group ones)
     */
    @Transient
    public Collection<AccessProfile> getAllAccessProfiles() {
        List<AccessProfile> profiles = new ArrayList<AccessProfile>(this.groupProfiles.size() + 1);
        profiles.add(this.publicProfile);
        profiles.addAll(this.groupProfiles.values());
        return profiles;
    }

    /**
     * @return the data owner of this project
     */
    @Transient
    public Set<User> getOwners() {
        if (getId() == null) {
            return Collections.singleton(UsernameHolder.getCsmUser());
        }
        if (this.owners == null) {
            this.owners = SecurityUtils.getOwners(this);
        }
        return Collections.unmodifiableSet(this.owners);
    }

    /**
     * Checks whether a given user is an owner of this project.
     *
     * @param user user to check
     * @return whether the given user is an owner of this project
     */
    public boolean isOwner(User user) {
        return getOwners().contains(user);
    }

    /**
     * Returns whether the given user has read permissions to this project.
     *
     * @param user the user (can be the synthetic "anonymous" permission)
     * @return whether the user has read permissions to this project
     */
    public boolean hasReadPermission(User user) {
        return SecurityUtils.canRead(this, user);
    }

    /**
     * Returns whether the given user has write permissions to this project.
     *
     * @param user the user (can be the synthetic "anonymous" permission)
     * @return whether the user has write permissions to this project
     */
    public boolean hasWritePermission(User user) {
        return SecurityUtils.canWrite(this, user);
    }

    /**
     * Returns whether the given user has permission to modify permissions for this project.
     *
     * @param user the user (can be the synthetic "anonymous" permission)
     * @return whether the user has permissions to modify permissions for this project
     */
    public boolean canModifyPermissions(User user) {
        return SecurityUtils.canModifyPermissions(this, user);
    }

    /**
     * Returns whether the given user is a collaborator on this project, ie whether he is in any of the collaborator
     * groups that have an access profile with a security level greater than "None" configured for this project.
     *
     * @param user the user (can be the synthetic "anonymous" user)
     * @return whether the user is a collaborator for this project
     */
    public boolean isCollaborator(User user) {
        for (Map.Entry<CollaboratorGroup, AccessProfile> cgEntry : groupProfiles.entrySet()) {
            if (cgEntry.getValue().getSecurityLevel() != SecurityLevel.NONE
                    && cgEntry.getKey().getGroup().getUsers().contains(user)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Gets the last updated date.
     *
     * @return the last date this experiment was updated
     */
    @Temporal(TemporalType.TIMESTAMP)
    @AttributePolicy(allow = SecurityPolicy.BROWSE_POLICY_NAME)
    public Date getLastUpdated() {
        return this.lastUpdated;
    }

    /**
     * Sets the last updated date.
     *
     * @param lastUpdated the last date this experiment was updated
     */
    public void setLastUpdated(final Date lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    /**
     * If the public id has not been locked, recalculate the experiment's public id value based on the
     * algorithm specified in @see getPublicId from the current values of the PI and persistent identifier.
     * If the public id has been locked, then this method is a no-op.
     */
    public void recalculatePublicId() {
        if (isLocked()) {
            return;
        }
        if (getExperiment().getId() == null) {
            return;
        }
        String lastName = "anonymous";
        final ExperimentContact primaryInvestigator = getExperiment().getPrimaryInvestigator();
        if (primaryInvestigator != null) {
            Person pi = primaryInvestigator.getContact();
            lastName = StringUtils.lowerCase(pi.getLastName());
        }        
        String piComponent = StringUtils.substring(
                CharSetUtils.keep(lastName, "A-Za-z"), 0, PUBLIC_ID_COMPONENT_LENGTH);
        String idComponent = StringUtils.leftPad(getExperiment().getId().toString(), PUBLIC_ID_COMPONENT_LENGTH, "0");
        getExperiment().setPublicIdentifier(piComponent + "-" + idComponent);
    }

    /**
     * {@inheritDoc}
     */
    public Set<SecurityPolicy> getRemoteApiSecurityPolicies(User user) {
        if (!SecurityUtils.canRead(this, user)) {
            return Collections.singleton(SecurityPolicy.BROWSE);
        }
        return Collections.emptySet();
    }

    /**
     * {@inheritDoc}
     */
    public int compareTo(Project o) {
        return new CompareToBuilder().append(getId(), o.getId()).toComparison();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
