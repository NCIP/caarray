//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.security;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation allowing properties to specify a list of policies which either allow or deny 
 * that property to be displayed. 
 * @author dkokotov
 */
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface AttributePolicy {
    /**
     * the list of whitelisted policies. If a policy uses whitelist mode, then a property must include
     * that policy's name in this list in order to be allowed through.
     */
    String[] allow() default { };

    /**
     * the list of blacklisted policies. If a policy uses blacklist mode, then a property will be allowed through
     * unless it includes the name of that policy in this list
     */
    String[] deny() default { };
    
    /**
     * An optional element specifying Transformers that can be applied to the property under various 
     * policies. If this array has multiple AttributeTransformers matching a policy, then the corresponding
     * Transformer instances' transform methods will be chained in the order in which they appear in the array,
     * with the result of the final one being used to set the final value for the property     
     */
    AttributeTransformer[] transformers() default { };

    /**
     * An optional element specifying Closures that can be applied to the property under various 
     * policies. If this array has multiple AttributeMutators matching a policy, then the the corresponding
     * Closure instances execute methods will be called in the order in which they appear in the array,
     */
    AttributeMutator[] mutators() default { };
 }
