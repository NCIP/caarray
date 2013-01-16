//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.plugins.agilent;


/**
 * @author jscott
 * Used during the parsing of an array design definition file for the purposes of file validation.
 */
class ValidatingArrayDesignBuilder implements ArrayDesignBuilder, PhysicalProbeBuilder,
FeatureBuilder, GeneBuilder, AccessionBuilder, BiosequenceBuilder {
    /**
     * {@inheritDoc}
     */
    public PhysicalProbeBuilder findOrCreatePhysicalProbeBuilder(String name) {
        // Do nothing
        return this;
    }

    /**
     * {@inheritDoc}
     */
    public FeatureBuilder createFeatureBuilder(int featureNumber) {
        // Do nothing
        return this;
    }

    /**
     * {@inheritDoc}
     */
    public FeatureBuilder setCoordinates(double x, double y, String units) {
        // Do nothing
        return this;
    }

    /**
     * {@inheritDoc}
     */
    public PhysicalProbeBuilder addToProbeGroup(String probeGroupName) {
        // Do nothing
        return this;
    }

    /**
     * {@inheritDoc}
     */
    ArrayDesignBuilder addCurrentPhysicalProbeToPositiveControlProbeGroup() {
        // Do nothing
        return this;
    }

    /**
     * {@inheritDoc}
     */
    public AccessionBuilder createNewGBAccession(String accessionNumber) {
        // Do nothing
        return this;
    }

    /**
     * {@inheritDoc}
     */
    public AccessionBuilder createNewEnsemblAccession(String accessionNumber) {
        // Do nothing
        return this;
    }

    /**
     * {@inheritDoc}
     */
    public AccessionBuilder createNewRefSeqAccession(String accessionNumber) {
        // Do nothing
        return this;
    }

    /**
     * {@inheritDoc}
     */
    public AccessionBuilder createNewTHCAccession(String accessionNumber) {
        // Do nothing
        return this;
    }

    /**
     * {@inheritDoc}
     */
    public BiosequenceBuilder agpAccession(String probeId) {
        // Do nothing
        return this;
    }
    
    /**
     * {@inheritDoc}
     */
    public BiosequenceBuilder createNewMirAccession(String accessionNumber) {
        // Do nothing
        return this;
    }

    /**
     * {@inheritDoc}
     */
    public GeneBuilder setChromosomeLocation(String chromosome,
            long startPosition, long endPosition) {
        // Do nothing
        return this;
    }

    /**
     * {@inheritDoc}
     */
    public GeneBuilder createGeneBuilder(String geneName) {
        // Do nothing
        return this;
    }

    /**
     * {@inheritDoc}
     */
    public PhysicalProbeBuilder setBiosequenceRef(String database, String species, String identifier) {
        // Do nothing
        return this;
    }

    /**
     * {@inheritDoc}
     */
    public BiosequenceBuilder createBiosequenceBuilder(String controlType, String species) {
        // Do nothing
        return this;
    }

    /**
     * {@inheritDoc}
     */
    public BiosequenceBuilder finish() {
        // Do nothing
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void processChunk(boolean forceFlush) {
        // Do nothing
    }
}

