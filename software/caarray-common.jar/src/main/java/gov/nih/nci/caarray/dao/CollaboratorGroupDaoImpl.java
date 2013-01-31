//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.dao;

import gov.nih.nci.caarray.domain.permissions.CollaboratorGroup;
import gov.nih.nci.security.authorization.domainobjects.Group;

import java.util.List;

import org.apache.log4j.Logger;

/**
 * Dao impl.
 */
public class CollaboratorGroupDaoImpl extends AbstractCaArrayDaoImpl implements CollaboratorGroupDao {

    private static final Logger LOG = Logger.getLogger(CollaboratorGroupDaoImpl.class);

    /**
     * {@inheritDoc}
     */
    @Override
    Logger getLog() {
        return LOG;
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public List<CollaboratorGroup> getAll() {
        return getCurrentSession().createQuery("SELECT cg FROM " + CollaboratorGroup.class.getName() + " cg, "
                + Group.class.getName() + " g" +  " WHERE cg.groupId = g.groupId order by g.groupName").list();
    }
}
