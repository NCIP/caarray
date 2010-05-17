package gov.nih.nci.caarray.platforms.agilent;

/**
 * Used during the parsing of an array design definition file to create the object graph
 * associated with an array design.
 * 
 * @author jscott
 */
interface ArrayDesignBuilder {
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

