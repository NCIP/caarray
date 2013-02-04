//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package gov.nih.nci.caarray.external.v1_0.data;

import gov.nih.nci.caarray.external.v1_0.AbstractCaArrayEntity;

/**
 * A QuantitationType represents a quantity that can be measured by an array. A quantity is defined by its name
 * and its data type, which identifies the value domain for the values of that quantity. 
 * The supported data types are boolean, short, integer, long, float, double, and string. 
 * 
 * @author dkokotov
 */
@SuppressWarnings({"PMD.MethodReturnsInternalArray", "PMD.ArrayIsStoredDirectly" })
public class QuantitationType extends AbstractCaArrayEntity {
    private static final long serialVersionUID = 1L;
    
    private String name;
    private DataType dataType;

    /**
     * @return the name of this quantity
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name for this quanity.
     * 
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the data type of this quantity
     */
    public DataType getDataType() {
        return dataType;
    }

    /**
     * Sets the data type for the quantity.
     * 
     * @param dataType the data type to set
     */
    public void setDataType(DataType dataType) {
        this.dataType = dataType;
    }
}
