//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.external.v1_0.array;

import gov.nih.nci.caarray.external.v1_0.AbstractCaArrayEntity;

/**
 * AssayType represents kinds of assays (measurements) that can be run on biomaterials hybridized to microarrays.
 * 
 * @author dkokotov
 */
public class AssayType extends AbstractCaArrayEntity {
    private static final long serialVersionUID = 1L;
    
    private String name;
    
    /**
     * Default constructor, for tooling use.
     */
    public AssayType() {
        // no-op
    }
    
    /**
     * Create a new assay type with given name.
     * 
     * @param name the name of the assay type
     */
    public AssayType(String name) {
        this.name = name;
    }
    
    /**
     * @return the name of the assay type.
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
}
