//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package gov.nih.nci.caarray.domain;

/**
 * A UnitableValue with a free text value.
 * 
 * @author dkokotov
 */
public interface UserDefinedValue extends UnitableValue {
    /**
     * @return the value
     */
    String getValue();

    /**
     * @param value the value to set
     */
    void setValue(String value);
}
