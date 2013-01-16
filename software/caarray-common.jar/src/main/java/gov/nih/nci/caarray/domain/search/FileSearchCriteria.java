//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.domain.search;

import gov.nih.nci.caarray.domain.file.FileCategory;
import gov.nih.nci.caarray.domain.file.FileType;
import gov.nih.nci.caarray.domain.project.AbstractExperimentDesignNode;
import gov.nih.nci.caarray.domain.project.Experiment;

import java.util.HashSet;
import java.util.Set;

/**
 * Simple bean to hold search criteria for files.
 * 
 * @author dkokotov
 */
public class FileSearchCriteria {
    private Experiment experiment;
    private String extension;
    private Set<FileType> types = new HashSet<FileType>();
    private Set<FileCategory> categories = new HashSet<FileCategory>();
    private Set<AbstractExperimentDesignNode> experimentNodes = new HashSet<AbstractExperimentDesignNode>();
    
    /**
     * @return the experiment nodes
     */
    public Set<AbstractExperimentDesignNode> getExperimentNodes() {
        return experimentNodes;
    }

    /**
     * @param nodes the nodes to set
     */
    public void setExperimentNodes(Set<AbstractExperimentDesignNode> nodes) {
        this.experimentNodes = nodes;
    }

    /**
     * @return the experiment
     */
    public Experiment getExperiment() {
        return experiment;
    }

    /**
     * @param experiment the experiment to set
     */
    public void setExperiment(Experiment experiment) {
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
    public Set<FileType> getTypes() {
        return types;
    }

    /**
     * @param types the types to set
     */
    public void setTypes(Set<FileType> types) {
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
}
