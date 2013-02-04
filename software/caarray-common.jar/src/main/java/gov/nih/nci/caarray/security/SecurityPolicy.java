//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package gov.nih.nci.caarray.security;

import gov.nih.nci.caarray.util.CaArrayUtils;
import gov.nih.nci.caarray.util.PropertyAccessor;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.Closure;
import org.apache.commons.collections.ClosureUtils;
import org.apache.commons.collections.Transformer;
import org.apache.commons.collections.TransformerUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.log4j.Logger;

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
@SuppressWarnings("PMD.CyclomaticComplexity")
public class SecurityPolicy {
    private static final Logger LOG = Logger.getLogger(SecurityPolicyPostLoadEventListener.class);
    
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
    
    /**
     * Apply the given set of policies to the given entity. 
     * @param entity the entity to apply policy
     * @param policies the set of policies to apply
     */
    public static void applySecurityPolicies(Object entity, Set<SecurityPolicy> policies) {
        PropertyAccessor[] propAccessors = CaArrayUtils.createReflectionHelper(entity.getClass()).getAccessors();
        if (!policies.isEmpty()) {
            for (PropertyAccessor propAccessor : propAccessors) {
                boolean disallowed = false;
                try {
                    for (SecurityPolicy policy : policies) {
                        if (!policy.allowProperty(propAccessor)) {
                            clearDisallowedProperty(entity, propAccessor);
                            disallowed = true;
                            continue;
                        }
                    }
                    if (!disallowed) {
                        applyTransformations(policies, entity, propAccessor);
                    }
                } catch (IllegalAccessException e) {
                    LOG.warn("Could not apply security policies to property " + propAccessor.getter().getName(), e);
                } catch (InvocationTargetException e) {
                    LOG.warn("Could not apply security policies to property " + propAccessor.getter().getName(), e);
                }
            }
        }
    }

    /**
     * Returns whether the given property on the given entity object is allowed by this policy.
     * @param entity the object in question
     * @param propertyName the name of the property on the object
     * @return whether this policy allows the specified property on the specified object to be seen
     */
    private boolean allowProperty(PropertyAccessor propAccessor) { // NOPMD for some reason PMD thinks it's not used
        AttributePolicy attributePolicy = getAttributePolicy(propAccessor);
        String[] policyNames = ArrayUtils.EMPTY_STRING_ARRAY;
        if (attributePolicy != null) {
            policyNames = (mode == SecurityPolicyMode.WHITELIST) ? attributePolicy.allow() : attributePolicy.deny();
        }
        boolean containsPolicy = ArrayUtils.contains(policyNames, name);
        return (mode == SecurityPolicyMode.WHITELIST) ? containsPolicy : !containsPolicy;
    }


    private static AttributePolicy getAttributePolicy(PropertyAccessor propAccessor) {
        return propAccessor.getter().getAnnotation(AttributePolicy.class);        
    }


    private static void applyTransformations(Set<SecurityPolicy> policies, Object entity, PropertyAccessor propAccessor)
            throws IllegalAccessException, InvocationTargetException {
        Transformer transformer = getPropertyTransformer(policies, propAccessor);
        if (transformer != null) {
            Object originalVal = propAccessor.get(entity);
            Object transformedVal = transformer.transform(originalVal);
            propAccessor.set(entity, transformedVal);
        }
        Closure mutator = getPropertyMutator(policies, propAccessor);
        if (mutator != null) {
            mutator.execute(propAccessor.get(entity));
        }
    }

    /**
     * Returns a transformer to be applied to the given property on the given entity, given the set
     * of applicable policies. If multiple Transformers are to be applied, then the returned
     * Transformer represents a chaining of those Transformers. If no Transformers are to be applied,
     * returns null
     *
     * @param policies the active policies
     * @param propertyAccessor accessor for the property on the object
     * @return the Transformer to be applied, or null if none should be applied
     */
    private static Transformer getPropertyTransformer(Set<SecurityPolicy> policies, PropertyAccessor propAccessor) {
        AttributePolicy attributePolicy = getAttributePolicy(propAccessor);
        if (attributePolicy == null) {
            return null;
        }
        List<Transformer> transformers = new ArrayList<Transformer>();
        for (AttributeTransformer attrTransformer : attributePolicy.transformers()) {
            if (policiesMatch(attrTransformer.policies(), policies)) {
                try {
                    transformers.add(attrTransformer.transformer().newInstance());
                } catch (InstantiationException e) {
                    LOG.warn("Could not instantiate transformer of class " + attrTransformer.transformer().getName(),
                            e);
                } catch (IllegalAccessException e) {
                    LOG.warn("Could not instantiate transformer of class " + attrTransformer.transformer().getName(),
                            e);
                }
            }
        }
        return transformers.isEmpty() ? null : TransformerUtils.chainedTransformer(transformers);
    }

    /**
     * Returns a Closure mutator to be applied to the given property on the given entity, given the set
     * of applicable policies. If multiple Closures are to be applied, then the returned Closure
     * represents a chaining of those Closure. If no Closures are to be applied,
     * returns null
     *
     * @param policies the active policies
     * @param propertyAccessor accessor for the property on the object
     * @return the Closure to be applied, or null if none should be applied
     */
    private static Closure getPropertyMutator(Set<SecurityPolicy> policies, PropertyAccessor propAccessor) {
        AttributePolicy attributePolicy = getAttributePolicy(propAccessor);
        if (attributePolicy == null) {
            return null;
        }
        List<Closure> mutators = new ArrayList<Closure>();
        for (AttributeMutator attrMutator : attributePolicy.mutators()) {
            if (policiesMatch(attrMutator.policies(), policies)) {
                try {
                    mutators.add(attrMutator.mutator().newInstance());
                } catch (InstantiationException e) {
                    LOG.warn("Could not instantiate closure of class " + attrMutator.mutator().getName(),
                            e);
                } catch (IllegalAccessException e) {
                    LOG.warn("Could not instantiate closure of class " + attrMutator.mutator().getName(),
                            e);
                }
            }
        }
        return mutators.isEmpty() ? null : ClosureUtils.chainedClosure(mutators);
    }

    private static boolean policiesMatch(String[] policyNames, Set<SecurityPolicy> policies) {
        for (SecurityPolicy policy : policies) {
            if (ArrayUtils.contains(policyNames, policy.getName())) {
                return true;
            }
        }
        return false;
    }

    private static void clearDisallowedProperty(Object entity, PropertyAccessor propAccessor)
            throws IllegalAccessException, InvocationTargetException {
        Class<?> type = propAccessor.getType();
        if (Collection.class.isAssignableFrom(type) || Map.class.isAssignableFrom(type)) {
            propAccessor.set(entity, CaArrayUtils.emptyCollectionOrMapFor(type));
        } else if (!type.isPrimitive()) {
            propAccessor.set(entity, null);
        } else {
            LOG.warn("Could not null out primitive property " + propAccessor.getter().getName());
        }
    }

}
