//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.external.v1_0.query;

import gov.nih.nci.caarray.external.v1_0.CaArrayEntityReference;
import gov.nih.nci.caarray.external.v1_0.data.FileCategory;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * @author dkokotov
 * 
 */
public class QuantitationTypeSearchCriteria implements Serializable {
    private static final long serialVersionUID = 1L;

    private Set<CaArrayEntityReference> fileTypes = new HashSet<CaArrayEntityReference>();
    private Set<FileCategory> fileCategories = new HashSet<FileCategory>();
    private Set<CaArrayEntityReference> arrayDataTypes = new HashSet<CaArrayEntityReference>();
    private CaArrayEntityReference hybridization;

    /**
     * @return the fileTypes
     */
    public Set<CaArrayEntityReference> getFileTypes() {
        return fileTypes;
    }

    /**
     * @param fileTypes the fileTypes to set
     */
    public void setFileTypes(Set<CaArrayEntityReference> fileTypes) {
        this.fileTypes = fileTypes;
    }

    /**
     * @return the fileTypeCategories
     */
    public Set<FileCategory> getFileCategories() {
        return fileCategories;
    }

    /**
     * @param fileTypeCategories the fileTypeCategories to set
     */
    public void setFileCategories(Set<FileCategory> fileTypeCategories) {
        this.fileCategories = fileTypeCategories;
    }

    /**
     * @return the arrayDataTypes
     */
    public Set<CaArrayEntityReference> getArrayDataTypes() {
        return arrayDataTypes;
    }

    /**
     * @param arrayDataTypes the arrayDataTypes to set
     */
    public void setArrayDataTypes(Set<CaArrayEntityReference> arrayDataTypes) {
        this.arrayDataTypes = arrayDataTypes;
    }

    /**
     * @return the hybridizations
     */
    public CaArrayEntityReference getHybridization() {
        return hybridization;
    }

    /**
     * @param hybridization the hybridization to set
     */
    public void setHybridization(CaArrayEntityReference hybridization) {
        this.hybridization = hybridization;
    }
}
