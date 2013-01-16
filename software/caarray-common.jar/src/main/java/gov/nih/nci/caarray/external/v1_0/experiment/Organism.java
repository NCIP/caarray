//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.external.v1_0.experiment;

import gov.nih.nci.caarray.external.v1_0.vocabulary.Term;

/**
 * An Organism is a special type of Term which represents an organism from which biomaterials for a microarray
 * experiment can be drawn.
 * 
 * @author dkokotov
 */
public class Organism extends Term {
    private static final long serialVersionUID = 1L;
    
    private String commonName;
    private String scientificName;
    
    /**
     * @return the common (layman) name for this organism.
     */
    public String getCommonName() {
        return commonName;
    }

    /**
     * @param commonName the commonName to set
     */
    public void setCommonName(String commonName) {
        this.commonName = commonName;
    }

    /**
     * @return the scientific name for this organism.
     */
    public String getScientificName() {
        return scientificName;
    }

    /**
     * @param scientificName the scientificName to set
     */
    public void setScientificName(String scientificName) {
        this.scientificName = scientificName;
    }
}
