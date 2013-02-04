//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package gov.nih.nci.caarray.domain;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * The presence of this annotation indicates to the entity pruning algorithm used to prepare
 * return results for remote API queries a maximum size for a collection that can be serialized.
 * The entity pruning algorithm should test the size of the collection, and if it exceeds the size
 * indicated by this annotation, throw a RuntimeException indicating that the result cannot be retrieved.
 * 
 * This annotation should be placed on getter methods for collection-typed properties of persistent objects.
 * It is only effective if the ExtraLazy option is used for the collection, preventing the collection from being 
 * faulted when its size is obtained.
 * 
 * @author dkokotov
 *
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME) 
public @interface MaxSerializableSize {
    /**
     * the maximum number of elements in collection that will result in successful serialization.
     */
    int value();
}
