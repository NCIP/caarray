//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
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
