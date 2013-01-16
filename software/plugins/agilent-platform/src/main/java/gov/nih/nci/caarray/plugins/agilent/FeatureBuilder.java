//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.plugins.agilent;

/**
 * Used during the parsing of an array design definition file to create the object graph
 * associated with a feature.
 * 
 * @author jscott
 */
public interface FeatureBuilder {
    /**
     * Sets the physical coordinates on the microarray of the feature .
     * @param x the X coordinate
     * @param y the Y coordinate
     * @param units the units used for the coordinate (e.g., "mm", millimeters)
     * @return a reference to itself
    */
    FeatureBuilder setCoordinates(double x, double y, String units);
}
