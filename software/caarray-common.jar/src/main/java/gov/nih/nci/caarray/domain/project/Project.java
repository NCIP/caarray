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
import java.util.Arrays;
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
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
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
@SuppressWarnings({"PMD.AvoidDuplicateLiterals", "PMD.TooManyFields", "PMD.ExcessiveClassLength" })
public class Project extends AbstractCaArrayEntity implements Comparable<Project>, Protectable {
    private static final long serialVersionUID = 1234567890L;

    private static final int PUBLIC_ID_COMPONENT_LENGTH = 5;

    private ProposalStatus status = ProposalStatus.DRAFT;
    private Experiment experiment = new Experiment();
    private SortedSet<CaArrayFile> files = new TreeSet<CaArrayFile>();
    private SortedSet<CaArrayFile> importedFiles = new TreeSet<CaArrayFile>();
    private SortedSet<CaArrayFile> supplementalFiles = new TreeSet<CaArrayFile>();
    private SortedSet<CaArrayFile> unImportedFiles = new TreeSet<CaArrayFile>();
    private AccessProfile publicProfile = new AccessProfile(SecurityLevel.NO_VISIBILITY);
    private AccessProfile hostProfile = new AccessProfile(SecurityLevel.READ_WRITE_SELECTIVE);
    private Map<CollaboratorGroup, AccessProfile> groupProfiles = new HashMap<CollaboratorGroup, AccessProfile>();
    private boolean useTcgaPolicy = false;
    private transient Set<User> owners;
    private Date lastUpdated = new Date();
    private boolean publicIdLocked = false;

    /**
     * Hibernate and castor constructor.
     */
    public Project() {
        // hibernate & castor-only constructor
        this.publicProfile.setProjectForPublicProfile(this);
        this.hostProfile.setProjectForHostProfile(this);
    }

    /**
     * Gets the workflow status of this project. Hibernate use only
     *
     * @return the status
     */
    @Enumerated(EnumType.STRING)
    @NotNull
    @Column(name = "status")
    @AttributePolicy(allow = SecurityPolicy.BROWSE_POLICY_NAME)
    private ProposalStatus getStatusInternal() {
        return this.status;
    }

    /**
     * Sets the workflow status of this project. Hibernate use only
     *
     * @param status the status to set
     */
    private void setStatusInternal(ProposalStatus statusInternal) {
        this.status = statusInternal;
    }

    /**
     * Gets the workflow status of this project.
     *
     * @return the status
     */
    @Transient
    public ProposalStatus getStatus() {
        return getStatusInternal();
    }

    /**
     * Sets the workflow status of this project.
     *
     * @param status the status to set
     */
    public void setStatus(ProposalStatus status) {
        setStatusInternal(status);
        // in progress projects get automatically set to browsable, if they weren't before
        if (status == ProposalStatus.IN_PROGRESS) {
            setPublicIdLocked(true);
            // this is correct for both submitting a draft project and retracting a project
            // from public availability. in the former, we need to make sure the project is visible
            // if it wasn't before; in the latter we need to remove the public read access
            getPublicProfile().setSecurityLevel(SecurityLevel.VISIBLE);
        }

        // public projects are effectively read rights to all and write rights to no one
        // we leave the host and profile access profiles alone, however, in case they revert
        // to in progress status. prohibition against writing is enforced in the application
        // layer based on workflow status
        if (status == ProposalStatus.PUBLIC) {
            getPublicProfile().setSecurityLevel(SecurityLevel.READ);
        }
    }

    /**
     * @return whether the project can be saved in its current state
     */
    @Transient
    public boolean isSaveAllowed() {
        return !isPublic();
    }

    /**
     * @return whether this project currently has any files that are being actively imported.
     */
    public boolean hasImportingData() {
        return CollectionUtils.exists(getFiles(), new Predicate() {
            public boolean evaluate(Object o) {
                CaArrayFile file = (CaArrayFile) o;
                return file.getFileStatus().equals(FileStatus.IMPORTING);
            }
        });
    }

    /**
     * @return whether the project can be submitted in its current state
     */
    @Transient
    public boolean isSubmissionAllowed() {
        return getStatus() != ProposalStatus.PUBLIC && getStatus().canTransitionTo(ProposalStatus.IN_PROGRESS);
    }

    /**
     * @return whether the project can be made public in its current state
     */
    @Transient
    public boolean isMakingPublicAllowed() {
        return getStatus().canTransitionTo(ProposalStatus.PUBLIC);
    }

    /**
     * @return whether the project's permissions can be edited in its current state
     */
    @Transient
    public boolean isPermissionsEditingAllowed() {
        return !isPublic();
    }

    /**
     * @return whether the project is currently public
     */
    @Transient
    public boolean isPublic() {
        return getStatus() == ProposalStatus.PUBLIC;
    }

    /**
     * @return whether the project is currently in draft status
     */
    @Transient
    public boolean isDraft() {
        return getStatus() == ProposalStatus.DRAFT;
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
     * @return host institution access profile
     */
    @ManyToOne(cascade = {CascadeType.ALL }, fetch = FetchType.LAZY)
    @JoinColumn(unique = true)
    @ForeignKey(name = "project_hostaccess_fk")
    public AccessProfile getHostProfile() {
        return this.hostProfile;
    }

    @SuppressWarnings({"unused", "PMD.UnusedPrivateMethod" })
    private void setHostProfile(AccessProfile profile) {
        this.hostProfile = profile;
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
        List<AccessProfile> profiles =
                new ArrayList<AccessProfile>(Arrays.asList(this.publicProfile, this.hostProfile));
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
        if (isPublicIdLocked()) {
            return;
        }
        if (getExperiment().getId() == null) {
            return;
        }
        Person pi = (Person) getExperiment().getPrimaryInvestigator().getContact();
        String piComponent = StringUtils.substring(
                CharSetUtils.keep(StringUtils.lowerCase(pi.getLastName()), "A-Za-z"), 0, PUBLIC_ID_COMPONENT_LENGTH);
        String idComponent = StringUtils.leftPad(getExperiment().getId().toString(), PUBLIC_ID_COMPONENT_LENGTH, "0");
        getExperiment().setPublicIdentifier(piComponent + "-" + idComponent);
    }

    /**
     * @return whether the public identifier has been locked from further editing
     */
    @AttributePolicy(allow = SecurityPolicy.BROWSE_POLICY_NAME)
    public boolean isPublicIdLocked() {
        return publicIdLocked;
    }

    /**
     * For hibernate use only.
     * @param publicIdLocked whether the public identifier has been locked from further editing
     */
    @SuppressWarnings({"unused", "PMD.unused" })
    private void setPublicIdLocked(boolean publicIdLocked) {
        this.publicIdLocked = publicIdLocked;
    }

    /**
     * @return the useTcgaPolicy
     */
    @Column(nullable = false)
    @AttributePolicy(allow = SecurityPolicy.BROWSE_POLICY_NAME)
    public boolean isUseTcgaPolicy() {
        return useTcgaPolicy;
    }

    /**
     * @param useTcgaPolicy the useTcgaPolicy to set
     */
    public void setUseTcgaPolicy(boolean useTcgaPolicy) {
        this.useTcgaPolicy = useTcgaPolicy;
    }

    /**
     * Returns the set of SecurityPolicies that apply to this project for the given user.
     *
     * @param user the user for whom to check the policies
     * @return the set of policies that apply for that user for this project
     */
    @SuppressWarnings("PMD.CyclomaticComplexity")
    public Set<SecurityPolicy> getApplicablePolicies(User user) {
        Set<SecurityPolicy> policies = new HashSet<SecurityPolicy>();
        if (!SecurityUtils.canRead(this, user)) {
            policies.add(SecurityPolicy.BROWSE);
        }
        if (useTcgaPolicy && !isOwner(user) && !isCollaborator(user)) {
            policies.add(SecurityPolicy.TCGA);
        }
        return policies;
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
