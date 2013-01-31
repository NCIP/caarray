//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.application.arraydata;

import gov.nih.nci.caarray.domain.project.ExperimentDesignNodeType;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.ArrayUtils;

/**
 * Simple JavaBean class to hold values of various array data import options.
 * @author dkokotov
 */
public final class DataImportOptions implements Serializable {    
    private static final long serialVersionUID = 1L;
    private static final DataImportOptions AUTOCREATE_PER_FILE_INSTANCE = new DataImportOptions(
            DataImportTargetAnnotationOption.AUTOCREATE_PER_FILE, null, null, Arrays
                    .asList(ArrayUtils.EMPTY_LONG_OBJECT_ARRAY));

    private final DataImportTargetAnnotationOption targetAnnotationOption;
    private final String newAnnotationName;
    private final ExperimentDesignNodeType targetNodeType;
    private final List<Long> targetNodeIds = new ArrayList<Long>();
    
    /**
     * Create a new DataImportOptions instance with given options.
     * @param targetAnnotationOption which method for autocreating annotation chains to use
     * @param newAnnotationName the name to use 
     * @param targetBiomaterials the target biomaterials to associate to.
     */
    private DataImportOptions(DataImportTargetAnnotationOption targetAnnotationOption, String newAnnotationName,
            ExperimentDesignNodeType targetNodeType, List<Long> targetNodeIds) {
        this.newAnnotationName = newAnnotationName;
        this.targetAnnotationOption = targetAnnotationOption;
        this.targetNodeType = targetNodeType;
        this.targetNodeIds.addAll(targetNodeIds);
    }

    /**
     * @return a new instance of data import options where the annotation option
     * is to autocreate one chain per file.
     */
    public static DataImportOptions getAutoCreatePerFileOptions() {
        return AUTOCREATE_PER_FILE_INSTANCE;
    }

    /**
     * @param newAnnotationName the name to be used by all auto-created annotations
     * @return a new instance of data import options where the annotation option
     * is to autocreate a single chain shared by all files
     */
    public static DataImportOptions getAutoCreateSingleOptions(String newAnnotationName) {
        return new DataImportOptions(DataImportTargetAnnotationOption.AUTOCREATE_SINGLE,
                newAnnotationName, null, Arrays.asList(ArrayUtils.EMPTY_LONG_OBJECT_ARRAY));
    }
    
    /**
     * @param targetNodeType the node type of the target nodes
     * @param targetNodeIds the list of ids of the target nodes
     * @return a new instance of data import options where the annotation option
     * is to associate the data files with selected nodes, autocreating annotation
     * chains for each file up to each selected node
     */
    public static DataImportOptions getAssociateToBiomaterialsOptions(ExperimentDesignNodeType targetNodeType,
            List<Long> targetNodeIds) {
        return new DataImportOptions(DataImportTargetAnnotationOption.ASSOCIATE_TO_NODES, null, targetNodeType,
                targetNodeIds);
    }

    /**
     * Create a new DataImportOptions instance with given options.
     * @param targetAnnotationOption which method for autocreating annotation chains to use
     * @param newAnnotationName the name to use for new annotations. Only required if targetAnnotationOption is
     * to autocreate a single chain for all files. 
     * @param targetNodeType the node type of the target nodes
     * @param targetNodeIds the ids of the target node to associate to. Only required if 
     * targetAnnotationOption is to associate to selected nodes
     * @return the new DataImportOptions instance.
     */
    public static DataImportOptions getDataImportOptions(DataImportTargetAnnotationOption targetAnnotationOption,
            String newAnnotationName, ExperimentDesignNodeType targetNodeType, List<Long> targetNodeIds) {
        return new DataImportOptions(targetAnnotationOption, newAnnotationName, targetNodeType, targetNodeIds);
    }

    /**
     * @return the option specifying how to create new or link to existing annotations
     */
    protected DataImportTargetAnnotationOption getTargetAnnotationOption() {
        return targetAnnotationOption;
    }

    /**
     * @return the name to use for new annotations
     */
    protected String getNewAnnotationName() {
        return newAnnotationName;
    }

    /**
     * @return the ids of the target nodes
     */
    protected List<Long> getTargetNodeIds() {
        return targetNodeIds;
    }

    /**
     * @return the target node type
     */
    public ExperimentDesignNodeType getTargetNodeType() {
        return targetNodeType;
    }    
}
