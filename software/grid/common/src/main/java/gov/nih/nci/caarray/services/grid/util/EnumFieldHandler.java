//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.services.grid.util;

import org.exolab.castor.mapping.GeneralizedFieldHandler;

/**
 * @param <T> type of enum
 * @author dkokotov
 */
public abstract class EnumFieldHandler<T extends Enum<T>> extends GeneralizedFieldHandler {
    private Class<T> enumClass;
    
    /**
     * @param enumClass the enum class
     */
    public EnumFieldHandler(Class<T> enumClass) {
        this.enumClass = enumClass;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public Object convertUponGet(Object value) {
        return value == null ? null : value.toString();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object convertUponSet(Object value) {
        return Enum.valueOf(enumClass, (String) value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Class getFieldType() {
        return enumClass;
    }

    /**
     * {@inheritDoc}
     */    
    @Override
    public Object newInstance(Object parent) {
       return null;
    }
}
