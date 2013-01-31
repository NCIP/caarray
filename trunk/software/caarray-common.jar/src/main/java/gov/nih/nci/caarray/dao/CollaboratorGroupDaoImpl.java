//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.dao;

import gov.nih.nci.caarray.domain.permissions.CollaboratorGroup;
import gov.nih.nci.caarray.util.UsernameHolder;
import gov.nih.nci.security.authorization.domainobjects.Group;

import java.util.List;

import org.hibernate.Query;

/**
 * Dao impl.
 */
public class CollaboratorGroupDaoImpl extends AbstractCaArrayDaoImpl implements CollaboratorGroupDao {
    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public List<CollaboratorGroup> getAll() {
        return getCurrentSession().createQuery("SELECT cg FROM " + CollaboratorGroup.class.getName() + " cg, "
                + Group.class.getName() + " g  WHERE cg.groupId = g.groupId order by g.groupName").list();
    }

    /**
     * {@inheritDoc}
     */
    public List<CollaboratorGroup> getAllForCurrentUser() {
        Long userId = UsernameHolder.getCsmUser().getUserId();
        return getAllForUser(userId);
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public List<CollaboratorGroup> getAllForUser(long userId) {
        Query query = getCurrentSession().createQuery("SELECT cg FROM " + CollaboratorGroup.class.getName() + " cg, "
                + Group.class.getName() + " g"
                + " WHERE cg.groupId = g.groupId AND cg.ownerId = :ownerId ORDER BY g.groupName");
        return query.setLong("ownerId", userId).list();
    }
}
