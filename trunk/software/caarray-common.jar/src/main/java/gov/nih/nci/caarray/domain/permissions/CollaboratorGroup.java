//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.domain.permissions;

import gov.nih.nci.caarray.domain.AbstractCaArrayObject;
import gov.nih.nci.caarray.security.Protectable;
import gov.nih.nci.caarray.util.HibernateUtil;
import gov.nih.nci.security.authorization.domainobjects.Group;
import gov.nih.nci.security.authorization.domainobjects.User;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.BatchSize;

import com.fiveamsolutions.nci.commons.data.persistent.PersistentObject;

/**
 * CollaboratorGroups bridge CSM groups with owners.
 */
@Entity
@BatchSize(size = AbstractCaArrayObject.DEFAULT_BATCH_SIZE)
@Table(uniqueConstraints = { @UniqueConstraint(columnNames = { "csm_group", "csm_user" }) })
@SuppressWarnings("PMD.AvoidDuplicateLiterals")
public class CollaboratorGroup implements PersistentObject, Protectable {
    //
    // DEVELOPER NOTE: This class has-a Group, rather than is-a Group,
    // to be fully compatible with whatever CSM changes may come down
    // the pike.  It's somewhat clunky, but then again, so is CSM.
    //

    private static final long serialVersionUID = -7566813289284832301L;

    private Long id;
    private Group group;
    private User owner;
    private Set<AccessProfile> accessProfiles = new HashSet<AccessProfile>();

    /**
     * @param group CSM group
     * @param owner group owner
     */
    public CollaboratorGroup(Group group, User owner) {
        if (group == null || owner == null) {
            throw new IllegalArgumentException("Group and owner must be non-null");
        }
        setGroup(group);
        this.owner = owner;
    }

    /**
     * For UI / Hibernate Usage only.
     */
    public CollaboratorGroup() {
        // intentionally empty
    }

    /**
     * @return database identifier
     */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Long getId() {
        return id;
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
     * @return the group
     */
    @Transient
    public Group getGroup() {
        return group;
    }

    private void setGroup(Group group) {
        this.group = group;
    }

    /**
     * @return the owner
     */
    @Transient
    public User getOwner() {
        return owner;
    }

    /**
     * @param owner owner to set
     */
    public void setOwner(User owner) {
        this.owner = owner;
    }

    @SuppressWarnings({"unused", "PMD.UnusedPrivateMethod" })
    @Column(name = "csm_group", nullable = false)
    private long getGroupId() {
        return group.getGroupId();
    }

    @SuppressWarnings({"unused", "PMD.UnusedPrivateMethod" })
    private void setGroupId(long groupId) {
        group = (Group) HibernateUtil.getCurrentSession().load(Group.class, groupId);
    }

    @SuppressWarnings({"unused", "PMD.UnusedPrivateMethod" })
    @Column(name = "csm_user", nullable = false)
    private long getOwnerId() {
        return owner.getUserId();
    }

    @SuppressWarnings({"unused", "PMD.UnusedPrivateMethod" })
    private void setOwnerId(long ownerId) {
        owner = (User) HibernateUtil.getCurrentSession().load(User.class, ownerId);
    }

    /**
     * @return the access profiles that have been created for this group
     */
    @OneToMany(mappedBy = "group")
    public Set<AccessProfile> getAccessProfiles() {
        return accessProfiles;
    }

    /**
     * @param accessProfiles the accessProfiles to set
     */
    @SuppressWarnings({"unused", "PMD.UnusedPrivateMethod" })
    private void setAccessProfiles(Set<AccessProfile> accessProfiles) {
        this.accessProfiles = accessProfiles;
    }
}
