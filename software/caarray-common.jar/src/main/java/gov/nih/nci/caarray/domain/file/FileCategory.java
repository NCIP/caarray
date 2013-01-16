//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.domain.file;

/**
 * FileCategory classifies files according to the kind of data they contain. Currently this is only used in some query
 * APIs.
 * 
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
     * file type category for mage tab annotation files.
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
