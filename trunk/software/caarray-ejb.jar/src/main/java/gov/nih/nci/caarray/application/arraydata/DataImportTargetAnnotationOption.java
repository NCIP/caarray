//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.application.arraydata;

/**
 * This is an enum of possible options for which annotations are created and/or linked to when a data-only 
 * (ie not MAGE-TAB) import occurs. 
 * @author dkokotov
 */
public enum DataImportTargetAnnotationOption {
    /**
     * This option indicates that annotations should be auto-created, with one annotation chain for each 
     * imported file name. File name here means the base file name, without the extension. Thus multiple
     * files with the same name will be linked to a single auto-created chain.
     */
    AUTOCREATE_PER_FILE,
    /**
     * This option indicates that annotations should be auto-created, with a single annotation chain for 
     * all of the imported files.
     */    
    AUTOCREATE_SINGLE,
    /**
     * This option indicates that the files will be linked to specified bio-materials, with intermediate
     * annotations auto-created.
     */
    ASSOCIATE_TO_NODES
}
