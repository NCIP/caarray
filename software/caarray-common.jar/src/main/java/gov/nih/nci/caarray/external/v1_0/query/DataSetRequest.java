//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package gov.nih.nci.caarray.external.v1_0.query;

import gov.nih.nci.caarray.external.v1_0.CaArrayEntityReference;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * @author dkokotov
 * 
 */
public class DataSetRequest implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private Set<CaArrayEntityReference> quantitationTypes = new HashSet<CaArrayEntityReference>();
    private Set<CaArrayEntityReference> dataFiles = new HashSet<CaArrayEntityReference>();
    private Set<CaArrayEntityReference> hybridizations = new HashSet<CaArrayEntityReference>();

    /**
     * @return the quantitationTypes
     */
    public Set<CaArrayEntityReference> getQuantitationTypes() {
        return quantitationTypes;
    }

    /**
     * @param quantitationTypes the quantitationTypes to set
     */
    public void setQuantitationTypes(Set<CaArrayEntityReference> quantitationTypes) {
        this.quantitationTypes = quantitationTypes;
    }

    /**
     * @return the dataFiles
     */
    public Set<CaArrayEntityReference> getDataFiles() {
        return dataFiles;
    }

    /**
     * @param dataFiles the dataFiles to set
     */
    public void setDataFiles(Set<CaArrayEntityReference> dataFiles) {
        this.dataFiles = dataFiles;
    }

    /**
     * @return the hybridizations
     */
    public Set<CaArrayEntityReference> getHybridizations() {
        return hybridizations;
    }

    /**
     * @param hybridizations the hybridizations to set
     */
    public void setHybridizations(Set<CaArrayEntityReference> hybridizations) {
        this.hybridizations = hybridizations;
    }
}
