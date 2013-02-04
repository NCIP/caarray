//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package gov.nih.nci.caarray.services.external;

import java.lang.reflect.Array;

import net.sf.dozer.util.mapping.converters.CustomConverter;

/**
 * Converter for array to array of the same type. More efficient than the default dozer handling.
 * 
 * @author dkokotov
 *
 */
@SuppressWarnings("unchecked")
public class DataValuesConverter implements CustomConverter {
    /**
     * {@inheritDoc}
     */
    public Object convert(Object dest, Object src, Class destClass, Class srcClass) {
        if (srcClass.isArray()) {
            int length = Array.getLength(src);
            Object newArray = Array.newInstance(srcClass.getComponentType(), length);
            System.arraycopy(src, 0, newArray, 0, length);            
            return newArray;
        } else {
            throw new IllegalArgumentException();
        }        
    }
}
