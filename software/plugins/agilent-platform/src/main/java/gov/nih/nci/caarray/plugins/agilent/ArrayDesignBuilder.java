//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package gov.nih.nci.caarray.plugins.agilent;

/**
 * Used during the parsing of an array design definition file to create the object graph
 * associated with an array design.
 * 
 * @author jscott
 */
interface ArrayDesignBuilder extends PersistenceChunkProcessor {
    /**
     * Gets a builder for a probe to be associated with the array design.  If the probe named
     * does not exist, it is created.  Otherwise, the previously created probe is looked
     * up by name and used.
     * @param name the name of the probe
     * @return the physical probe builder
     */
    PhysicalProbeBuilder findOrCreatePhysicalProbeBuilder(String name);


    /**
     * @param controlType
     * @param species
     * @return
     */
    BiosequenceBuilder createBiosequenceBuilder(String controlType, String species);
}

