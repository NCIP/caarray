//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package gov.nih.nci.caarray.external.v1_0.query;

import gov.nih.nci.caarray.external.v1_0.CaArrayEntityReference;
import gov.nih.nci.caarray.external.v1_0.data.FileCategory;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * @author dkokotov
 */
public class FileSearchCriteria implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private CaArrayEntityReference experiment;
    private String extension;
    private Set<CaArrayEntityReference> types = new HashSet<CaArrayEntityReference>();
    private Set<FileCategory> categories = new HashSet<FileCategory>();
    private Set<CaArrayEntityReference> experimentGraphNodes = new HashSet<CaArrayEntityReference>();
    
    /**
     * @return the experiment
     */
    public CaArrayEntityReference getExperiment() {
        return experiment;
    }

    /**
     * @param experiment the experiment to set
     */
    public void setExperiment(CaArrayEntityReference experiment) {
        this.experiment = experiment;
    }

    /**
     * @return the extension
     */
    public String getExtension() {
        return extension;
    }

    /**
     * @param extension the extension to set
     */
    public void setExtension(String extension) {
        this.extension = extension;
    }

    /**
     * @return the types
     */
    public Set<CaArrayEntityReference> getTypes() {
        return types;
    }

    /**
     * @param types the types to set
     */
    public void setTypes(Set<CaArrayEntityReference> types) {
        this.types = types;
    }

    /**
     * @return the categories
     */
    public Set<FileCategory> getCategories() {
        return categories;
    }

    /**
     * @param categories the categories to set
     */
    public void setCategories(Set<FileCategory> categories) {
        this.categories = categories;
    }

    /**
     * @return the biomaterials
     */
    public Set<CaArrayEntityReference> getExperimentGraphNodes() {
        return experimentGraphNodes;
    }

    /**
     * @param biomaterials the biomaterials to set
     */
    public void setExperimentGraphNodes(Set<CaArrayEntityReference> biomaterials) {
        this.experimentGraphNodes = biomaterials;
    }
}
