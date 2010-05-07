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
     * Used during the parsing of an array design definition file to create the object graph
     * associated with a physical probe.
     * 
     * @author jscott
     */
    interface PhysicalProbeBuilder {
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
    }
    
    /**
     * Used during the parsing of an array design definition file to create the object graph
     * associated with a feature.
     * 
     * @author jscott
     */
    interface FeatureBuilder {
        /**
         * Sets the physical coordinates on the microarray of the feature .
         * @param x the X coordinate
         * @param y the Y coordinate
         * @param units the units used for the coordinate (e.g., "mm", millimeters)
         * @return a reference to itself
        */
        FeatureBuilder setCoordinates(double x, double y, String units);
    }

    /**
     * Used during the parsing of an array design definition file to create the object graph
     * associated with a gene.
     * 
     * @author jscott
     */
    interface GeneBuilder {
       /**
         * Associates a new Genbank accession to the gene.
         * @param accessionNumber the Genbank accession number
         * @return a reference to itself
         */
        GeneBuilder createNewGBAccession(String accessionNumber);
    
        /**
         * Associates a new Ensembl accession to the gene object.
         * @param accessionNumber the Ensembl accession number
         * @return a reference to itself
         */
        GeneBuilder createNewEnsemblAccession(String accessionNumber);
    
        /**
         * Sets the chromosome location for the gene.
         * @param chromosome the number (1-22) or name ("x" or "y") of the chromosome
         * @param startPosition the position on the chromosome where the gene begins
         * @param endPosition the position on the chromosome where the gene ends
         * @return a reference to itself
         */
        GeneBuilder setChromosomeLocation(String chromosome, long startPosition, long endPosition);
    }
}

