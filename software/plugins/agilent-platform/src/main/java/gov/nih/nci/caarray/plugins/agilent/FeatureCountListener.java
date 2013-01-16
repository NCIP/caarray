//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.plugins.agilent;

/**
 * Is interested in the feature count of an array design.
 * @author dharley
 *
 */
interface FeatureCountListener {
    
    /**
     * Increment feature count.
     */
    void incrementFeatureCount();

}
