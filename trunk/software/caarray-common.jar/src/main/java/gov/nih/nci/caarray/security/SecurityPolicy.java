/**
 * The software subject to this notice and license includes both human readable
 * source code form and machine readable, binary, object code form. The caarray-common-jar
 * Software was developed in conjunction with the National Cancer Institute 
 * (NCI) by NCI employees and 5AM Solutions, Inc. (5AM). To the extent 
 * government employees are authors, any rights in such works shall be subject 
 * to Title 17 of the United States Code, section 105. 
 *
 * This caarray-common-jar Software License (the License) is between NCI and You. You (or 
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
 * its rights in the caarray-common-jar Software to (i) use, install, access, operate, 
 * execute, copy, modify, translate, market, publicly display, publicly perform,
 * and prepare derivative works of the caarray-common-jar Software; (ii) distribute and 
 * have distributed to and by third parties the caarray-common-jar Software and any 
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
