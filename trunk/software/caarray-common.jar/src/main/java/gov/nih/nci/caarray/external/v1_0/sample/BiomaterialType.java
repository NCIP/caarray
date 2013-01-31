//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.external.v1_0.sample;


/**
 * BiomaterialType contains constants representing different types of Biomaterials. These correspond to types defined
 * in MAGE-TAB, and represent different stages in the treatment of a biomaterial prior to hybridization.
 *  
 * @author dkokotov
 */
public enum BiomaterialType {
    /**
     * biomaterial type corresponding to a MAGE-TAB Source.
     */
    SOURCE,
    
    /**
     * biomaterial type corresponding to a MAGE-TAB Sample.
     */
    SAMPLE,
 
    /**
     * biomaterial type corresponding to a MAGE-TAB Extract.
     */
    EXTRACT,
 
    /**
     * biomaterial type corresponding to a MAGE-TAB Labeled Extract.
     */
    LABELED_EXTRACT;

    private static final long serialVersionUID = 1L;
    
    /**
     * @return the name
     */
    public String getName() {
        return name();
    }
}
