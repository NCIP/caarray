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

import org.apache.commons.collections.Closure;

/**
 * Annotation allowing a property to specifying a mutator to be applied when the property
 * is allowed by the policy.
 * @author dkokotov
 */
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface AttributeMutator {
    /**
     * the list of names of policies under which the mutator is active. The mutator is only
     * applied when the property is allowed by the policy.
     */
    String[] policies() default { };
    
    /**
     * The class of a Closure that will be applied to the value
     * of the property if it is allowed by the policy. A new instance of this
     * class (which must have a no-arg public constructor) will be instantiated, and its execute method
     * will be called on the attribute value. Note that the setter method will not be called - 
     * use a @AttributeTransformer annotation instead if that is what you need.
     */
    Class<? extends Closure> mutator();        
 }
