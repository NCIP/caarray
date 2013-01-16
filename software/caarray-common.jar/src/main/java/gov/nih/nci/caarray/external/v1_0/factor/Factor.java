//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.external.v1_0.factor;

import gov.nih.nci.caarray.external.v1_0.AbstractCaArrayEntity;
import gov.nih.nci.caarray.external.v1_0.vocabulary.Term;

/**
 * Factor represents an experimental factor, e.g. an independent variable within an experiment.
 * 
 * @author dkokotov
 */
public class Factor extends AbstractCaArrayEntity {
    private static final long serialVersionUID = 1L;
    
    private String name;
    private String description;
    private Term type;

    /**
     * @return the name for this factor.
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }
    
    /**
     * @return the long-form description of this factor
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param description the description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * @return a term from the MGED ontology defining the type of this factor.
     */
    public Term getType() {
        return type;
    }

    /**
     * @param type the type to set
     */
    public void setType(Term type) {
        this.type = type;
    }
}
