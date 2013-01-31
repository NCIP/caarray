//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.domain.search;

import gov.nih.nci.caarray.domain.vocabulary.Category;

/**
 * An AnnotationCriterion specifies a criterion that matches Characteristics having
 * the given category and value.
 * 
 * @author dkokotov
 */
public class AnnotationCriterion {
    private String value;
    private Category category;

    /**
     * Empty constructor.
     */
    public AnnotationCriterion() {
        // empty
    }
    
    /**
     * Constructor for AnnotationCriterion with given category and value.
     * @param category the category
     * @param value the value
     */
    public AnnotationCriterion(Category category, String value) {
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
    public Category getCategory() {
        return category;
    }

    /**
     * @param category the category to set
     */
    public void setCategory(Category category) {
        this.category = category;
    }
}
