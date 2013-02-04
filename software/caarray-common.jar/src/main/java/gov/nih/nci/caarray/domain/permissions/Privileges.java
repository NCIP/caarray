//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package gov.nih.nci.caarray.domain.permissions;

import gov.nih.nci.caarray.security.SecurityUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple class to hold the privileges a user may have for a Protectable object. Has methods
 * to make it convenient to use in the view layer
 * 
 * @author dkokotov
 */
public class Privileges {
    private final List<String> privilegeNames = new ArrayList<String>();    
    
    /**
     * Create a new Privileges with given privilege names.
     * @param privilegeNames privilege names
     */
    public Privileges(List<String> privilegeNames) {
        this.privilegeNames.addAll(privilegeNames);
    }

    /**
     * Create a new Privileges with no privilege names.
     */
    public Privileges() {
        // empty
    }
    
    /**
     * @return the names of the privileges in this object
     */
    public List<String> getPrivilegeNames() {
        return privilegeNames;
    }

    /**
     * @return whether this includes the READ privilege
     */
    public boolean isRead() {
        return privilegeNames.contains(SecurityUtils.READ_PRIVILEGE);
    }
    
    /**
     * @return whether this includes the BROWSE privilege
     */
    public boolean isBrowse() {
        return privilegeNames.contains(SecurityUtils.BROWSE_PRIVILEGE);
    }

    /**
     * @return whether this includes the WRITE privilege
     */
    public boolean isWrite() {
        return privilegeNames.contains(SecurityUtils.WRITE_PRIVILEGE);
    }

    /**
     * @return whether this includes the PERMISSIONS privilege
     */
    public boolean isPermissions() {
        return privilegeNames.contains(SecurityUtils.PERMISSIONS_PRIVILEGE);
    }

    /**
     * @return whether this includes the PARTIAL_READ privilege
     */
    public boolean isPartialRead() {
        return privilegeNames.contains(SecurityUtils.PARTIAL_READ_PRIVILEGE);
    }

    /**
     * @return whether this includes the PARTIAL_WRITE privilege
     */
    public boolean isPartialWrite() {
        return privilegeNames.contains(SecurityUtils.PARTIAL_WRITE_PRIVILEGE);
    }
}
