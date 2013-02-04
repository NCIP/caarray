//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package gov.nih.nci.caarray.domain;

import gov.nih.nci.caarray.domain.vocabulary.Term;

/**
 * Interface for classes that store a value with an optional unit.
 * 
 * @author dkokotov
 */
public interface UnitableValue {
    /**
     * Gets the unit.
     *
     * @return the unit
     */
    Term getUnit();

    /**
     * Sets the unit.
     *
     * @param unitVal the unit
     */
    void setUnit(final Term unitVal);

    /**
     * @return the value (with the unit) as a string displayable in the ui 
     */
    String getDisplayValue();
    
    /**
     * @return the value (without unit) as a string displayable in the ui
     */
    String getDisplayValueWithoutUnit();
}
