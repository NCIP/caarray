//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package gov.nih.nci.caarray.security;

import com.fiveamsolutions.nci.commons.data.persistent.PersistentObject;

/**
 * Indicates that an operation was not allowed due to the current user not having the required permissions.
 */
public class PermissionDeniedException extends RuntimeException {
    private static final long serialVersionUID = 3582622697786140397L;
    private final PersistentObject entity;
    private final String privilege;
    private final String userName;

    /**
     * Create a new PermissionDeniedException for a given user not having the given privilege to the
     * given entity.
     * 
     * @param entity the instance on which an operation was requested
     * @param privilege the privilege that was needed to perform the operation
     * @param userName the user attempting the operation
     */
    public PermissionDeniedException(PersistentObject entity, String privilege, String userName) {
        super(String.format("User %s does not have privilege %s for entity of type %s with id %s", userName,
                privilege, entity.getClass().getName(), entity.getId()));
        this.privilege = privilege;
        this.entity = entity;
        this.userName = userName;
    }
    
    /**
     * Create a new PermissionDeniedException for a given user not having the given privilege to the
     * given entity.
     * 
     * @param entity the instance on which an operation was requested
     * @param privilege the privilege that was needed to perform the operation
     * @param userName the user attempting the operation
     * @param message the custom message
     */
    protected PermissionDeniedException(PersistentObject entity, String privilege, String userName, String message) {
        super(message);
        this.entity = entity;
        this.privilege = privilege;
        this.userName = userName;
    }
    
    /**
     * @return the privilege
     */
    public String getPrivilege() {
        return privilege;
    }

    /**
     * @return the userName
     */
    public String getUserName() {
        return userName;
    }

    /**
     * @return the entity
     */
    public PersistentObject getEntity() {
        return entity;
    }
}
