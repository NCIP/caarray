//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package gov.nih.nci.caarray.external.v1_0.data;


/**
 * @author dkokotov
 */
public enum FileCategory {
    /**
     * file type category for raw data array data files.
     */
    RAW_DATA,
    
    /**
     * file type category for derived data array data files.
     */
    DERIVED_DATA,
 
    /**
     * file type category for array design files.
     */
    ARRAY_DESIGN,
    
    /**
     * file type category for mage tab files.
     */
    MAGE_TAB,
    
    /**
     * file type category for all other types of files.
     */
    OTHER;
    
    private static final long serialVersionUID = 1L;
    
    /**
     * @return the name
     */
    public String getName() {
        return name();
    }
}
