//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package gov.nih.nci.caarray.security;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.apache.commons.collections.Transformer;

/**
 * Annotation allowing a property to specifying a transformer to be applied when the property
 * is allowed by the policy.
 * @author dkokotov
 */
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface AttributeTransformer {
    /**
     * the list of names of policies under which the transformer is active. The transformer is only
     * applied when the property is allowed by the policy.
     */
    String[] policies() default { };
    
    /**
     * The class of a Transformer that will be applied to the value
     * of the property if it is allowed by the policy. A new instance of this
     * class (which must have a no-arg public constructor) will be instantiated, its transform method
     * will be called on the attribute value, and the setter for the attribute will be called
     * to set the new value on the owning entity.     
     */
    Class<? extends Transformer> transformer();        
 }
