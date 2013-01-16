//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.util;

/**
 * Utility class holding a set of PropertyAcessors for the properties of a class.
 * @author dkokotov
 */
@SuppressWarnings("PMD")
public final class ReflectionHelper {
    private final PropertyAccessor[] accessors;

    /**
     * @param accessors the set of accessors for the properties of a class
     */
    public ReflectionHelper(PropertyAccessor[] accessors) { 
        this.accessors = accessors;
    }

    /**
     * @return the accessors
     */
    public PropertyAccessor[] getAccessors() {
        return accessors;
    }
}
