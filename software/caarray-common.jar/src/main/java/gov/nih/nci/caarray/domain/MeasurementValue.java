//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.domain;

/**
 * A UnitableValue with a numerical value.
 * 
 * @author dkokotov
 */
public interface MeasurementValue extends UnitableValue {
    /**
     * @return the value
     */
    Float getValue();

    /**
     * @param value the value to set
     */
    void setValue(Float value);
}
