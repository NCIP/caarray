package gov.nih.nci.caarray.platforms.agilent;

/**
 * @author jscott
 * Used during the parsing of an array design definition file to create the object graph
 * associated with an array design.
 */
public interface ArrayDesignDetailerInterface {
    /**
     * Sets the current physical probe object being operated on.  If the probe named
     * does not exist, it is created.  Otherwise, the previously created probe is looked
     * up by name and used.
     * @param name the name of the probe
     */
    void findOrCreateCurrentPhysicalProbe(String name);

    /**
     * Creates a new feature object and associates it with the current physical probe object.  This
     * new feature is set as the current feature for subsequent operations.
     * @param featureNumber the number of the feature
     */
    void createFeatureForCurrentPhysicalProbe(int featureNumber);

    /**
     * Sets the physical coordinates of the current feature on the microarray.
     * @param x the X coordinate
     * @param y the Y coordinate
     * @param units the units used for the coordinate (e.g., "mm", millimeters)
     */
    void setCoordinatesOnCurrentFeature(double x, double y, String units);

    /**
     * Associates the current physical probe to the "ignore" probe group.
     */
    void addCurrentPhysicalProbeToIgnoreProbeGroup();

    /**
     * Associates the current physical probe to the "positive control" probe group.
     */
    void addCurrentPhysicalProbeToPositiveControlProbeGroup();

    /**
     * Associates the current physical probe to the "negative control" probe group.
     */
    void addCurrentPhysicalProbeToNegativeControlProbeGroup();

    /**
     * Associates a new annotation object and related gene object to the current physical probe.
     * @param geneName the name of the gene
     */
    void createNewAnnotationOnCurrentPhysicalProbe(String geneName);

    /**
     * Associates a new Genbank accession to the gene object related to the current physical probe.
     * @param accessionNumber the Genbank accession number
     */
    void createNewGBAccessionOnCurrentGene(String accessionNumber);

    /**
     * Associates a new Ensembl accession to the gene object related to the current physical probe.
     * @param accessionNumber the Ensembl accession number
     */
    void createNewEnsemblAccessionOnCurrentGene(String accessionNumber);

    /**
     * Sets the chromosome location for the gene related to the current physical probe.
     * @param chromosome the number (1-22) or name ("x" or "y") of the chromosome
     * @param startPosition the position on the chromosome where the gene begins
     * @param endPosition the position on the chromosome where the gene ends
     */
    void setChromosomeLocationForCurrentGene(String chromosome, long startPosition, long endPosition);
}

