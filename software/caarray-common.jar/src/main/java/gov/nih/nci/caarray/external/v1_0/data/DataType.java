//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.external.v1_0.data;


/**
 * DataType is an enumeration of the possible value types that a single value of a data column may have.
 * 
 * @author dkokotov
 */
public enum DataType {
    /**
     * data type for integer values.
     */
    INTEGER,
    
    /**
     * data type for short integer values.
     */
    SHORT,
 
    /**
     * data type for long integer values.
     */
    LONG,
 
    /**
     * data type for float values.
     */
    FLOAT, 
    
    /**
     * data type for double precision float values.
     */
    DOUBLE,
    
    /**
     * data type for boolean values.
     */
    BOOLEAN,
    
    /**
     * data type for string values.
     */
    STRING;

    private static final long serialVersionUID = 1L;
    
    /**
     * @return the name
     */
    public String getName() {
        return name();
    }
}
