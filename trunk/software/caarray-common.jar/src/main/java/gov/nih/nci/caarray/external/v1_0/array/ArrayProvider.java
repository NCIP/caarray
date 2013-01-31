//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.external.v1_0.array;

import gov.nih.nci.caarray.external.v1_0.AbstractCaArrayEntity;


/**
 * An ArrayProvider represents a provider (manufacturer) of microarrays.
 * 
 * @author dkokotov
 */
public class ArrayProvider extends AbstractCaArrayEntity {
    private static final long serialVersionUID = 1L;
    
    private String name;
    
    /**
     * Default constructor, for use by tooling.
     */
    public ArrayProvider() {
        // NO-OP
    }
    
    /**
     * Create a new array provider with given name.
     * @param name the name of the provider
     */
    public ArrayProvider(String name) {
        this.name = name;
    }

    /**
     * @return the name of the provider.
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
