//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.dao.stub;

import gov.nih.nci.caarray.dao.CollaboratorGroupDao;
import gov.nih.nci.caarray.domain.permissions.CollaboratorGroup;

import java.util.Collections;
import java.util.List;

import com.fiveamsolutions.nci.commons.data.persistent.PersistentObject;

/**
 * Dao stub.
 */
public class CollaboratorGroupDaoStub extends AbstractDaoStub implements CollaboratorGroupDao {

    private static int numGetAllCalls = 0;
    private static int numGetAllForUserCalls = 0;
    private static PersistentObject savedObject;

    /**
     * {@inheritDoc}
     */
    @Override
    public void save(PersistentObject caArrayEntity) {
        super.save(caArrayEntity);
        CollaboratorGroupDaoStub.savedObject = caArrayEntity;
    }

    /**
     * {@inheritDoc}
     */
    public List<CollaboratorGroup> getAll() {
        ++numGetAllCalls;
        return Collections.emptyList();
    }

    public int getNumGetAllCalls() {
        return numGetAllCalls;
    }

    /**
     * {@inheritDoc}
     */
    public List<CollaboratorGroup> getAllForCurrentUser() {
        ++numGetAllForUserCalls;
        return Collections.emptyList();
    }

    public int getNumGetAllForUserCalls() {
        return numGetAllForUserCalls;
    }

    /**
     * @return the savedObject
     */
    public PersistentObject getSavedObject() {
        return savedObject;
    }

    /**
     * {@inheritDoc}
     */
    public List<CollaboratorGroup> getAllForUser(long userId) {
        return Collections.emptyList();
    }

}
