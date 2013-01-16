//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.platforms;

import gov.nih.nci.caarray.domain.data.AbstractDataColumn;

/**
 * Interface for classes capable of parsing values in data files into numbers and booleans.
 * 
 * @author dkokotov
 */
public interface ValueParser {
    /**
     * Parse the given string from a data file, and set value at given row of the given column to its value. 
     * 
     * @param column the column 
     * @param rowIndex the index of the row in the column
     * @param value the value 
     */
    void setValue(AbstractDataColumn column, int rowIndex, String value);
}
