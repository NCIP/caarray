//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package gov.nih.nci.caarray.plugins.agilent;


/**
 * Used during the parsing of an array design definition file to create the object graph
 * associated with a physical probe.
 * 
 * @author jscott
 */
public interface PhysicalProbeBuilder {
    /**
     * Creates a builder for a new feature object associated with the physical probe. 
     * @param featureNumber the number of the feature
     * @return the physical probe builder builder
     */
    FeatureBuilder createFeatureBuilder(int featureNumber);

    /**
     * Associates the physical probe to the named probe group.
     * @param probeGroupName the name of the probe group
     * @return a reference to itself
     */
    PhysicalProbeBuilder addToProbeGroup(String probeGroupName);
    
    /**
     * Creates a builder for a new gene object associated with the physical probe.
     * @param geneName the name of the gene
     * @return the gene builder
     */
    GeneBuilder createGeneBuilder(String geneName);

    /**
     * Associates a biosequence reference to the probe.
     * 
     * @param database the name of the public database where this biosequence is found.
     * @param species the species hosting this biosequence.
     * @param identifier the identifier of this biosequence in the public database.
     * @return a reference to itself
     */
    PhysicalProbeBuilder setBiosequenceRef(String database, String species, String identifier);
}
