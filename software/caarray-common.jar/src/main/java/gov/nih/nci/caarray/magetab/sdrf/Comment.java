//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.magetab.sdrf;

/**
 * Represents a MAGE-TAB SDRF Comment column.
 * 
 * @author dkokotov
 */
public class Comment {
    private static final long serialVersionUID = 8963454780738369441L;

    private String value;

    /**
     * Create a new Comment with given value.
     * @param value the value of this Comment column
     */
    public Comment(String value) {
        this.value = value;
    }

    /**
     * 
     * @return String the comment
     */
    public String getValue() {
        return value;
    }

    /**
     * 
     * @param value the comment
     */
    public void setValue(String value) {
        this.value = value;
    }
}
