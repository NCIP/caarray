//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.plugins.agilent;

/**
 * Alerts clients to growing feature counts in array designs.
 * @author dharley
 *
 */
interface FeatureCountPublisher {
    
    /**
     * Adds an interested client.
     * @param featureCountListener
     */
    void addFeatureCountListener(FeatureCountListener featureCountListener);

}
