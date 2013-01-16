//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.plugins.agilent;

import gov.nih.nci.caarray.dao.ArrayDao;
import gov.nih.nci.caarray.dao.SearchDao;
import gov.nih.nci.caarray.dao.VocabularyDao;
import gov.nih.nci.caarray.domain.array.ArrayDesignDetails;
import gov.nih.nci.caarray.domain.array.ExpressionProbeAnnotation;
import gov.nih.nci.caarray.domain.array.Feature;
import gov.nih.nci.caarray.domain.array.Gene;
import gov.nih.nci.caarray.domain.array.MiRNAProbeAnnotation;
import gov.nih.nci.caarray.domain.array.PhysicalProbe;
import gov.nih.nci.caarray.domain.array.ProbeGroup;
import gov.nih.nci.caarray.domain.project.ExperimentOntology;
import gov.nih.nci.caarray.domain.search.ExampleSearchCriteria;
import gov.nih.nci.caarray.domain.vocabulary.Term;
import gov.nih.nci.caarray.domain.vocabulary.TermSource;
import gov.nih.nci.caarray.util.CaArrayUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.hibernate.criterion.Order;

/**
 * {@inheritDoc}
 * 
 * @author jscott
 * 
 */
@SuppressWarnings("PMD.ExcessiveClassLength")
class ArrayDesignBuilderImpl implements ArrayDesignBuilder, PhysicalProbeBuilder,
FeatureBuilder, GeneBuilder, AccessionBuilder {
    
    private static final Logger LOG = Logger.getLogger(ArrayDesignBuilderImpl.class);

    private final VocabularyDao vocabularyDao;
    private final ArrayDao arrayDao;
    private final SearchDao searchDao;
    private final Map<String, Long> createdProbeGroupIds = new HashMap<String, Long>();
    private final Map<String, Long> createdPhysicalProbeIds = new HashMap<String, Long>();
    private final ArrayDesignDetails arrayDesignDetails;
    private PhysicalProbe currentPhysicalProbe;
    private Feature currentFeature;
    private Term millimeterTerm;
    private final Map<String, Long> biosequenceReIdsfMap = new HashMap<String, Long>();
    private static final int BATCH_SIZE = 1000;
    private static final String POSITIVE_CONTROL_LABEL = "pos";
    private static final String NEGATIVE_CONTROL_LABEL = "neg";
    private int batchCount = 0;

    /**
     * 
     * @param arrayDesignDetails
     * @param vocabularyDao
     * @param arrayDao
     * @param searchDao
     */
    ArrayDesignBuilderImpl(ArrayDesignDetails arrayDesignDetails, VocabularyDao vocabularyDao, ArrayDao arrayDao,
            SearchDao searchDao) {
        this.arrayDesignDetails = arrayDesignDetails;
        this.vocabularyDao = vocabularyDao;
        this.arrayDao = arrayDao;
        this.searchDao = searchDao;
        lookupMillimeterTerm();
    }


    private TermSource getMOSource() {
        final TermSource querySource = new TermSource();
        querySource.setName(ExperimentOntology.MGED_ONTOLOGY.getOntologyName());
        querySource.setVersion(ExperimentOntology.MGED_ONTOLOGY.getVersion());
        return CaArrayUtils.uniqueResult(this.vocabularyDao.queryEntityByExample(
                ExampleSearchCriteria.forEntity(querySource).includeNulls().excludeProperties("url"),
                Order.desc("version")));
    }

    private void lookupMillimeterTerm() {
        final TermSource source = getMOSource();
        if (null == source) {
            throw new AgilentParseException("Could not find the MGED Ontology term source");
        }

        this.millimeterTerm = this.vocabularyDao.getTerm(source, "mm");
        if (null == this.millimeterTerm) {
            throw new AgilentParseException("Could not look up the term for \"mm\".");
        }
    }

    /**
     * {@inheritDoc}
     */
    public PhysicalProbeBuilder findOrCreatePhysicalProbeBuilder(String name) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("1 findOrCreatePhysicalProbeBuilder(String) called: name=" + name + "=");
        }
        Long existingPhysicalProbeId = createdPhysicalProbeIds.get(name);
        PhysicalProbe probe = null;
        if (null != existingPhysicalProbeId) {
            probe = searchDao.retrieve(PhysicalProbe.class, existingPhysicalProbeId);
        } else {
            probe = new PhysicalProbe(arrayDesignDetails, null);
            probe.setName(name);
            arrayDao.save(probe);
            createdPhysicalProbeIds.put(name, probe.getId());
        }
        currentPhysicalProbe = probe;
        return this;
    }

    /**
     * {@inheritDoc}
     */
    public FeatureBuilder createFeatureBuilder(int featureNumber) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("2 createFeatureBuilder(int) called: featureNumber=" + featureNumber + "=");
        }
        Feature feature = new Feature(arrayDesignDetails);
        feature.setFeatureNumber(featureNumber);
        arrayDao.save(feature);
        currentPhysicalProbe.addFeature(feature);
        currentFeature = feature;
        return this;
    }

    /**
     * {@inheritDoc}
     */
    public FeatureBuilder setCoordinates(double x, double y, String units) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("3 setCoordinates(double, double, String) called: x=" + x + "=, y =" + y + "=, units =" + units
                    + "=");
        }
        currentFeature.setX_Coordinate(x);
        currentFeature.setY_Coordinate(y);
        if ("mm".equalsIgnoreCase(units)) {
            currentFeature.setCoordinateUnits(millimeterTerm);
        } else {
            throw new AgilentParseException(String.format("Unexpected units \"%s\"", units));
        }
        return this;
    }

    /**
     * {@inheritDoc}
     */
    public PhysicalProbeBuilder addToProbeGroup(String probeGroupName) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("4 addToProbeGroup(String) called: probeGroupName=" + probeGroupName + "=");
        }
        ProbeGroup ignoreProbeGroup = getOrCreateProbeGroup(probeGroupName);
        addCurrentPhysicalProbeToProbeGroup(ignoreProbeGroup);
        return this;
    }

    private ProbeGroup getOrCreateProbeGroup(String probeGroupName) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("5 getOrCreateProbeGroup(String) called: probeGroupName=" + probeGroupName + "=");
        }
        Long probeGroupId = createdProbeGroupIds.get(probeGroupName);
        ProbeGroup probeGroup = null;
        if (null != probeGroupId) {
            probeGroup = searchDao.retrieve(ProbeGroup.class, probeGroupId);
        } else {
            probeGroup = createProbeGroup(probeGroupName);
        }
        return probeGroup;
    }

    private ProbeGroup createProbeGroup(String name) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("6 createProbeGroup(String) called: name=" + name + "=");
        }
        ProbeGroup probeGroup = new ProbeGroup(arrayDesignDetails);
        probeGroup.setName(name);
        arrayDao.save(probeGroup);
        createdProbeGroupIds.put(name, probeGroup.getId());
        return probeGroup;
    }

    private void addCurrentPhysicalProbeToProbeGroup(final ProbeGroup probeGroup) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("7 addCurrentPhysicalProbeToProbeGroup(ProbeGroup) called: probeGroup=" + probeGroup + "=");
        }
        currentPhysicalProbe.setProbeGroup(probeGroup);
    }

    /**
     * {@inheritDoc}
     */
    public PhysicalProbeBuilder setBiosequenceRef(String database, String species, String identifier) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("8 setBiosequenceRef(String, String, String) called: database=" + database + "=, species ="
                    + species + "=, identifier =" + identifier + "=");
        }
        String key = buildBiosequenceKey(species, identifier);
        getBiosequenceReIdsfMap().put(key, currentPhysicalProbe.getId());
        return this;
    }

    private String buildBiosequenceKey(String species, String identifier) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("9 buildBiosequenceKey(String, String) called: species=" + species + "=, identifier ="
                    + identifier + "=");
        }
        return String.format("`%s`%s`", species, identifier);
    }

    /**
     * {@inheritDoc}
     */
    public GeneBuilder createGeneBuilder(String geneName) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("10 createGeneBuilder(String) called: geneName=" + geneName + "=");
        }
        createExpressionProbeAnnotation(currentPhysicalProbe, geneName);
        return this;
    }

    private ExpressionProbeAnnotation createExpressionProbeAnnotation(PhysicalProbe physicalProbe, String geneName) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("11 createExpressionProbeAnnotation(PhysicalProbe, String) called: physicalProbe ="
                    + physicalProbe + ", geneName=" + geneName + "=");
        }
        Gene gene = new Gene();
        gene.setFullName(geneName);

        ExpressionProbeAnnotation annotation = new ExpressionProbeAnnotation();
        annotation.setGene(gene);
        
        physicalProbe.setAnnotation(annotation);

        return annotation;
    }

    private ExpressionProbeAnnotation createExpressionProbeAnnotation(PhysicalProbe physicalProbe) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("12 createExpressionProbeAnnotation(PhysicalProbe) called: physicalProbe=" + physicalProbe + "=");
        }
        String geneName = null;
        return createExpressionProbeAnnotation(physicalProbe, geneName);
    }

    /**
     * {@inheritDoc}
     */
    public GeneBuilder setChromosomeLocation(String chromosomeName,
            long startPosition, long endPosition) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("13 setChromosomeLocation(String, long, long) called: chromosomeName=" + chromosomeName
                    + "=, startPosition =" + startPosition + "=, endPosition =" + endPosition + "=");
        }
        ExpressionProbeAnnotation annotation = (ExpressionProbeAnnotation) currentPhysicalProbe.getAnnotation();
        annotation.setChromosome(chromosomeName, startPosition, endPosition);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    public AccessionBuilder createNewGBAccession(String accessionNumber) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("14 createNewGBAccession(String) called: accessionNumber=" + accessionNumber + "=");
        }
        ExpressionProbeAnnotation annotation = (ExpressionProbeAnnotation) currentPhysicalProbe.getAnnotation();
        Gene gene = annotation.getGene();
        gene.addAccessionNumber(Gene.GENBANK, accessionNumber);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    public AccessionBuilder createNewEnsemblAccession(String accessionNumber) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("15 createNewEnsemblAccession(String) called: accessionNumber=" + accessionNumber + "=");
        }
        ExpressionProbeAnnotation annotation = (ExpressionProbeAnnotation) currentPhysicalProbe.getAnnotation();
        Gene gene = annotation.getGene();
        gene.addAccessionNumber(Gene.ENSEMBLE, accessionNumber);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    public AccessionBuilder createNewRefSeqAccession(String accessionNumber) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("16 createNewRefSeqAccession(String) called: accessionNumber=" + accessionNumber + "=");
        }
        ExpressionProbeAnnotation annotation = (ExpressionProbeAnnotation) currentPhysicalProbe.getAnnotation();
        Gene gene = annotation.getGene();
        gene.addAccessionNumber(Gene.REF_SEQ, accessionNumber);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    public AccessionBuilder createNewTHCAccession(String accessionNumber) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("17 createNewTHCAccession(String) called: accessionNumber=" + accessionNumber + "=");
        }
        ExpressionProbeAnnotation annotation = (ExpressionProbeAnnotation) currentPhysicalProbe.getAnnotation();
        Gene gene = annotation.getGene();
        gene.addAccessionNumber(Gene.THC, accessionNumber);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    public AccessionBuilder agpAccession(String probeId) {
        return this;
    }

    /**
     * {@inheritDoc}
     */
    public AccessionBuilder createNewMirAccession(String accessionNumber) {
        return this;
    }

    /**
     * {@inheritDoc}
     */
    public BiosequenceBuilder createBiosequenceBuilder(String controlType, String species) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("18 createBiosequenceBuilder(String, String) called: controlType=" + controlType + "=, species ="
                    + species + "=");
        }
        if (POSITIVE_CONTROL_LABEL.equalsIgnoreCase(controlType)
                || NEGATIVE_CONTROL_LABEL.equalsIgnoreCase(controlType)) {
            return new NullBiosequenceBuilder();
        } else {
            return new BiosequenceBuilderImpl(this, species);
        }
    }

    /**
     * @return the array design details object constructed by this class.
     */
    ArrayDesignDetails getArrayDesignDetails() {
        return arrayDesignDetails;
    }

    /**
     * @return biosequenceReIdsfMap
     */
    Map<String, Long> getBiosequenceReIdsfMap() {
        return biosequenceReIdsfMap;
    }

    /**
     * {@inheritDoc}
     */
    private class BiosequenceBuilderImpl implements BiosequenceBuilder {
        private final ArrayDesignBuilderImpl parentBuilder;
        private final String species;
        private final List<AccessionNumber> accessionNumbers = new ArrayList<AccessionNumber>();
        
        private String key;
        private boolean isMiRNA;

        BiosequenceBuilderImpl(ArrayDesignBuilderImpl parentBuilder, String species) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("19 BiosequenceBuilderImpl(ArrayDesignBuilderImpl, String) called: parentBuilder="
                        + parentBuilder + "=, species =" + species + "=");
            }
            this.parentBuilder = parentBuilder;
            this.species = species;
        }

        /**
         * {@inheritDoc}
         */
        public BiosequenceBuilder agpAccession(String accessionNumber) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("20 agpAccession(String) called: accessionNumber=" + accessionNumber + "=");
            }
            key = parentBuilder.buildBiosequenceKey(species, accessionNumber);
            return this;
        }

        /**
         * {@inheritDoc}
         */
        public BiosequenceBuilder createNewEnsemblAccession(String accessionNumber) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("21 createNewEnsemblAccession(String) called: accessionNumber=" + accessionNumber + "=");
            }
            addAccession(Gene.ENSEMBLE, accessionNumber);           
            return this;
        }

        /**
         * {@inheritDoc}
         */
        public BiosequenceBuilder createNewGBAccession(String accessionNumber) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("22 createNewGBAccession(String) called: accessionNumber=" + accessionNumber + "=");
            }
            addAccession(Gene.GENBANK, accessionNumber);           
            return this;
        }

        /**
         * {@inheritDoc}
         */
        public BiosequenceBuilder createNewMirAccession(String accessionNumber) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("23 createNewMirAccession(String) called: accessionNumber=" + accessionNumber + "=");
            }
            isMiRNA = true;
            addAccession(MiRNAProbeAnnotation.MIR, accessionNumber);           
            return this;
        }

        /**
         * {@inheritDoc}
         */
        public BiosequenceBuilder createNewRefSeqAccession(String accessionNumber) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("24 createNewRefSeqAccession(String) called: accessionNumber=" + accessionNumber + "=");
            }
            addAccession(Gene.REF_SEQ, accessionNumber);           
            return this;
        }

        /**
         * {@inheritDoc}
         */
        public BiosequenceBuilder createNewTHCAccession(String accessionNumber) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("25 createNewTHCAccession(String) called: accessionNumber=" + accessionNumber + "=");
            }
            addAccession(Gene.THC, accessionNumber);           
            return this;
        }
        
        /**
         * {@inheritDoc}
         */
        public BiosequenceBuilder finish() {
            if (LOG.isDebugEnabled()) {
                LOG.debug("26 finish() called");
            }
            Long physicalProbeId = parentBuilder.getBiosequenceReIdsfMap().get(key);
            PhysicalProbe physicalProbe = null;
            if (null != physicalProbeId) {
                physicalProbe = searchDao.retrieve(PhysicalProbe.class, physicalProbeId);
                createAnnotationWithAccessions(physicalProbe);
            }
            return this;
        }

        private void createAnnotationWithAccessions(PhysicalProbe physicalProbe) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("27 createAnnotationWithAccessions(PhysicalProbe) called: physicalProbe=" + physicalProbe
                        + "=");
            }
            if (isMiRNA) {
                createMiRNAAnnotationWithAccessions(physicalProbe);
            } else {
                createExpressionAnnotationWithAccessions(physicalProbe);
            }
        }

        private void createExpressionAnnotationWithAccessions(PhysicalProbe physicalProbe) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("28 createExpressionAnnotationWithAccessions(PhysicalProbe) called: physicalProbe="
                        + physicalProbe + "=");
            }
            ExpressionProbeAnnotation annotation = createExpressionProbeAnnotation(physicalProbe);             
            Gene gene = annotation.getGene();
   
            for (AccessionNumber accessionNumber : accessionNumbers) {
                gene.addAccessionNumber(accessionNumber.getDatabase(), accessionNumber.getAccession());
            }
        }

        private void createMiRNAAnnotationWithAccessions(PhysicalProbe physicalProbe) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("29 createMiRNAAnnotationWithAccessions(PhysicalProbe) called: physicalProbe="
                        + physicalProbe + "=");
            }
            MiRNAProbeAnnotation annotation = new MiRNAProbeAnnotation();
            physicalProbe.setAnnotation(annotation);
            
            for (AccessionNumber accessionNumber : accessionNumbers) {
                annotation.addAccessionNumber(accessionNumber.getDatabase(), accessionNumber.getAccession());
            }
        }

        private void addAccession(String database, String accessionNumber) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("30 addAccession(String, String) called: database=" + database + "=, accessionNumber ="
                        + accessionNumber + "=");
            }
            this.accessionNumbers.add(new AccessionNumber(database, accessionNumber));          
        }
        
        /**
         * Stores accession numbers for association with an annotation.
         * @author jscott
         */
        private class AccessionNumber {
            private final String database;
            private final String accession; 
            
            AccessionNumber(String database, String accessionNumber) {
                this.database = database;
                this.accession = accessionNumber;
            }

            public String getDatabase() {
                return database;
            }

            public String getAccession() {
                return accession;
            }
        }
    }
    
    /**
     * Used for controls, this biosequenceBuilder does nothing.
     * @author jscott
     */
    private class NullBiosequenceBuilder implements BiosequenceBuilder {

        /**
         * {@inheritDoc}
         */
        public BiosequenceBuilder agpAccession(String accessionNumber) {
            // Do nothing
            return this;
        }

        /**
         * {@inheritDoc}
         */
        public BiosequenceBuilder createNewEnsemblAccession(String accessionNumber) {
            // Do nothing
            return this;
        }

        /**
         * {@inheritDoc}
         */
        public BiosequenceBuilder createNewGBAccession(String accessionNumber) {
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
        public BiosequenceBuilder createNewRefSeqAccession(String accessionNumber) {
            // Do nothing
            return this;
        }

        /**
         * {@inheritDoc}
         */
        public BiosequenceBuilder createNewTHCAccession(String accessionNumber) {
            // Do nothing
            return this;
        }
              
        /**
         * {@inheritDoc}
         */
        public BiosequenceBuilder finish() {
            return this;
        }

    }

    /**
     * {@inheritDoc}
     */
    public void processChunk(boolean forceFlush) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("31 processChunk() called");
        }
        arrayDao.save(currentFeature);
        arrayDao.save(currentPhysicalProbe);
        if (forceFlush || ++batchCount % BATCH_SIZE == 0) {
            arrayDao.flushSession();
            arrayDao.clearSession();
        }
    }
}
