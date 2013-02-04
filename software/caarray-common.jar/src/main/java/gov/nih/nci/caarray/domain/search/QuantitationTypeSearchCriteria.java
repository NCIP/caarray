//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package gov.nih.nci.caarray.domain.search;

import gov.nih.nci.caarray.domain.data.ArrayDataType;
import gov.nih.nci.caarray.domain.file.FileCategory;
import gov.nih.nci.caarray.domain.file.FileType;
import gov.nih.nci.caarray.domain.hybridization.Hybridization;

import java.util.HashSet;
import java.util.Set;

/**
 * Simple bean to hold search criteria for a quantitation types.
 * 
 * @author dkokotov
 */
public class QuantitationTypeSearchCriteria {
    private Hybridization hybridization;
    private Set<FileType> fileTypes = new HashSet<FileType>();
    private Set<ArrayDataType> arrayDataTypes = new HashSet<ArrayDataType>();
    private Set<FileCategory> fileCategories = new HashSet<FileCategory>();

    /**
     * @return the hybridization
     */
    public Hybridization getHybridization() {
        return hybridization;
    }

    /**
     * @param hybridization the hybridization to set
     */
    public void setHybridization(Hybridization hybridization) {
        this.hybridization = hybridization;
    }

    /**
     * @return the fileTypes
     */
    public Set<FileType> getFileTypes() {
        return fileTypes;
    }

    /**
     * @param fileTypes the fileTypes to set
     */
    public void setFileTypes(Set<FileType> fileTypes) {
        this.fileTypes = fileTypes;
    }

    /**
     * @return the arrayDataType
     */
    public Set<ArrayDataType> getArrayDataTypes() {
        return arrayDataTypes;
    }

    /**
     * @param arrayDataTypes the arrayDataTypes to set
     */
    public void setArrayDataTypes(Set<ArrayDataType> arrayDataTypes) {
        this.arrayDataTypes = arrayDataTypes;
    }
    
    /**
     * @return the fileCategories
     */
    public Set<FileCategory> getFileCategories() {
        return fileCategories;
    }

    /**
     * @param fileCategories the fileCategories to set
     */
    public void setFileCategories(Set<FileCategory> fileCategories) {
        this.fileCategories = fileCategories;
    }
}
