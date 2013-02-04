//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package gov.nih.nci.caarray.security;


/**
 * Enum of modes in which a security policy can operate.
 */
public enum SecurityPolicyMode {
    /**
     * Whitelist mode - attributes must explicitly list a policy in their whitelist for them
     * to be allowed by that policies.
     */
    WHITELIST,

    /**
     * Blacklist mode - attributes are allowed by a policy unless they explicitly list that policy
     * in their blacklist.
     */
    BLACKLIST;
}
