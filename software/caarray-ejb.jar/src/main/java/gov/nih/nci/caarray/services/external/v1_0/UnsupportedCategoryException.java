//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.services.external.v1_0;

import gov.nih.nci.caarray.external.v1_0.CaArrayEntityReference;

/**
 * Exception thrown to indicate that a category used in an 
 * AnnotationCriterion is not currently supported. 
 * 
 * @author dkokotov
 */
public class UnsupportedCategoryException extends InvalidInputException {
    private static final long serialVersionUID = 1L;
    
    private final CaArrayEntityReference category;
    
    /**
     * Constructor for no cause.
     * @param category the reference to the category that is not supported
     */
    public UnsupportedCategoryException(CaArrayEntityReference category) {
        super();
        this.category = category;
    }

    /**
     * Constructor for no cause.
     * @param category the reference to the category that is not supported
     * @param msg a message with more information about the exception
     */
    public UnsupportedCategoryException(CaArrayEntityReference category, String msg) {
        super(msg);
        this.category = category;
    }

    /**
     * @return the category that is not supported
     */
    public CaArrayEntityReference getCategory() {
        return category;
    }
}
