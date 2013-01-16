//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.external.v1_0.query;

import gov.nih.nci.caarray.external.v1_0.CaArrayEntityReference;

import java.io.Serializable;

/**
 * An AnnotationCriterion specifies a criterion that matches Characteristics having
 * the referenced category and given value.
 * 
 * @author dkokotov
 */
public class AnnotationCriterion implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String value;
    private CaArrayEntityReference category;

    /**
     * No-arg constructor.
     */
    public AnnotationCriterion() {
        // empty
    }

    /**
     * Constructor for AnnotationCriterion with given category and value.
     * 
     * @param category the reference to the category
     * @param value the value to match against
     */
    public AnnotationCriterion(CaArrayEntityReference category, String value) {
        this.category = category;
        this.value = value;
    }

    /**
     * @return the value
     */
    public String getValue() {
        return value;
    }

    /**
     * @param value the value to set
     */
    public void setValue(String value) {
        this.value = value;
    }

    /**
     * @return the category
     */
    public CaArrayEntityReference getCategory() {
        return category;
    }

    /**
     * @param category the category to set
     */
    public void setCategory(CaArrayEntityReference category) {
        this.category = category;
    }

}
