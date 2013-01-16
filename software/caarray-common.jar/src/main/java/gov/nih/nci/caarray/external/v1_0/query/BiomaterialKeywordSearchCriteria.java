//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.external.v1_0.query;

import gov.nih.nci.caarray.external.v1_0.sample.BiomaterialType;

import java.util.HashSet;
import java.util.Set;

/**
 * @author dkokotov
 * 
 */
public class BiomaterialKeywordSearchCriteria extends KeywordSearchCriteria {
    private static final long serialVersionUID = 1L;
    
    private Set<BiomaterialType> types = new HashSet<BiomaterialType>();
    

    /**
     * @return the types
     */
    public Set<BiomaterialType> getTypes() {
        return types;
    }

    /**
     * @param types the types to set
     */
    public void setTypes(Set<BiomaterialType> types) {
        this.types = types;
    }
}
