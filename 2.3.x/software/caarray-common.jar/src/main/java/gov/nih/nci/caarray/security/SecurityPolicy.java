//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.security;

import java.lang.reflect.Method;

import org.apache.commons.lang.ArrayUtils;
import org.hibernate.util.ReflectHelper;

/**
 * This class models a security policy. For now, security policies are limited to applying attribute-level security to
 * entities. Policies can use either whitelist or blacklist mode - in whitelist mode attributes are only allowed if the
 * have the policy in their whitelist, whereas in blacklist mode attributes are allowed unless they have the policy in
 * their blacklist.
 * 
 * If an attribute is disallowed by policy, then it is nulled out (or, if it is a collection, set to an empty
 * collection) via a Hibernate Interceptor.
 * 
 * @author dkokotov
 */
public class SecurityPolicy {
    private final String name;
    private final SecurityPolicyMode mode;

    /**
     * Name of the "Browse" SecurityPolicy.
     */
    public static final String BROWSE_POLICY_NAME = "Browse";
    
    /**
     * The "Browse" SecurityPolicy applies to browsable projects to which a user does not
     * also have READ or WRITE access.
     */
    public static final SecurityPolicy BROWSE = new SecurityPolicy(BROWSE_POLICY_NAME, SecurityPolicyMode.WHITELIST);
    
    /**
     * Name of the TCGA SecurityPolicy.
     */
    public static final String TCGA_POLICY_NAME = "TCGA";
    
    /**
     * The "TCGA" SecurityPolicy applies to TCGA human projects and restricts public access to certain
     * fields.
     */
    public static final SecurityPolicy TCGA = new SecurityPolicy(TCGA_POLICY_NAME, SecurityPolicyMode.BLACKLIST);

    /**
     * Constructor For SecurityPolicy with given name and mode of operation.
     * @param name the name for the policy
     * @param mode the mode of operation (blacklist or whitelist)
     */
    public SecurityPolicy(String name, SecurityPolicyMode mode) {
        super();
        this.name = name;
        this.mode = mode;
    }

    /**
     * Returns whether the given property on the given entity object is allowed by this policy.
     * @param entity the object in question
     * @param propertyName the name of the property on the object
     * @return whether this policy allows the specified property on the specified object to be seen
     */
    public boolean allowProperty(Object entity, String propertyName) {
        AttributePolicy attributePolicy = getAttributePolicy(entity, propertyName);
        String[] policyNames = ArrayUtils.EMPTY_STRING_ARRAY;
        if (attributePolicy != null) {
            policyNames = (mode == SecurityPolicyMode.WHITELIST) ? attributePolicy.allow() : attributePolicy.deny();
        }
        boolean containsPolicy = ArrayUtils.contains(policyNames, name);
        return (mode == SecurityPolicyMode.WHITELIST) ? containsPolicy : !containsPolicy;
    }


    static AttributePolicy getAttributePolicy(Object entity, String propertyName) {
        Method getterMethod = ReflectHelper.getGetter(entity.getClass(), propertyName).getMethod();
        return getterMethod.getAnnotation(AttributePolicy.class);        
    }

    /**
     * @return the name of this policy
     */
    public String getName() {
        return name;
    }

    /**
     * @return the mode for this policy
     */
    public SecurityPolicyMode getMode() {
        return mode;
    }
}
