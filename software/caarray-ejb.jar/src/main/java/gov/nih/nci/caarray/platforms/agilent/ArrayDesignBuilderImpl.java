/**
 * The software subject to this notice and license includes both human readable
 * source code form and machine readable, binary, object code form. The caArray
 * Software was developed in conjunction with the National Cancer Institute
 * (NCI) by NCI employees and 5AM Solutions, Inc. (5AM). To the extent
 * government employees are authors, any rights in such works shall be subject
 * to Title 17 of the United States Code, section 105.
 *
 * This caArray Software License (the License) is between NCI and You. You (or
 * Your) shall mean a person or an entity, and all other entities that control,
 * are controlled by, or are under common control with the entity. Control for
 * purposes of this definition means (i) the direct or indirect power to cause
 * the direction or management of such entity, whether by contract or otherwise,
 * or (ii) ownership of fifty percent (50%) or more of the outstanding shares,
 * or (iii) beneficial ownership of such entity.
 *
 * This License is granted provided that You agree to the conditions described
 * below. NCI grants You a non-exclusive, worldwide, perpetual, fully-paid-up,
 * no-charge, irrevocable, transferable and royalty-free right and license in
 * its rights in the caArray Software to (i) use, install, access, operate,
 * execute, copy, modify, translate, market, publicly display, publicly perform,
 * and prepare derivative works of the caArray Software; (ii) distribute and
 * have distributed to and by third parties the caArray Software and any
 * modifications and derivative works thereof; and (iii) sublicense the
 * foregoing rights set out in (i) and (ii) to third parties, including the
 * right to license such rights to further third parties. For sake of clarity,
 * and not by way of limitation, NCI shall have no right of accounting or right
 * of payment from You or Your sub-licensees for the rights granted under this
 * License. This License is granted at no charge to You.
 *
 * Your redistributions of the source code for the Software must retain the
 * above copyright notice, this list of conditions and the disclaimer and
 * limitation of liability of Article 6, below. Your redistributions in object
 * code form must reproduce the above copyright notice, this list of conditions
 * and the disclaimer of Article 6 in the documentation and/or other materials
 * provided with the distribution, if any.
 *
 * Your end-user documentation included with the redistribution, if any, must
 * include the following acknowledgment: This product includes software
 * developed by 5AM and the National Cancer Institute. If You do not include
 * such end-user documentation, You shall include this acknowledgment in the
 * Software itself, wherever such third-party acknowledgments normally appear.
 *
 * You may not use the names "The National Cancer Institute", "NCI", or "5AM"
 * to endorse or promote products derived from this Software. This License does
 * not authorize You to use any trademarks, service marks, trade names, logos or
 * product names of either NCI or 5AM, except as required to comply with the
 * terms of this License.
 *
 * For sake of clarity, and not by way of limitation, You may incorporate this
 * Software into Your proprietary programs and into any third party proprietary
 * programs. However, if You incorporate the Software into third party
 * proprietary programs, You agree that You are solely responsible for obtaining
 * any permission from such third parties required to incorporate the Software
 * into such third party proprietary programs and for informing Your
 * sub-licensees, including without limitation Your end-users, of their
 * obligation to secure any required permissions from such third parties before
 * incorporating the Software into such third party proprietary software
 * programs. In the event that You fail to obtain such permissions, You agree
 * to indemnify NCI for any claims against NCI by such third parties, except to
 * the extent prohibited by law, resulting from Your failure to obtain such
 * permissions.
 *
 * For sake of clarity, and not by way of limitation, You may add Your own
 * copyright statement to Your modifications and to the derivative works, and
 * You may provide additional or different license terms and conditions in Your
 * sublicenses of modifications of the Software, or any derivative works of the
 * Software as a whole, provided Your use, reproduction, and distribution of the
 * Work otherwise complies with the conditions stated in this License.
 *
 * THIS SOFTWARE IS PROVIDED "AS IS," AND ANY EXPRESSED OR IMPLIED WARRANTIES,
 * (INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY,
 * NON-INFRINGEMENT AND FITNESS FOR A PARTICULAR PURPOSE) ARE DISCLAIMED. IN NO
 * EVENT SHALL THE NATIONAL CANCER INSTITUTE, 5AM SOLUTIONS, INC. OR THEIR
 * AFFILIATES BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS;
 * OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR
 * OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package gov.nih.nci.caarray.platforms.agilent;

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
 * @author jscott
 * @author dharley
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
     * @param vocabularyDao a vocabulary DAO
     */
    ArrayDesignBuilderImpl(ArrayDesignDetails arrayDesignDetails, VocabularyDao vocabularyDao, ArrayDao arrayDao,
            SearchDao searchDao) {
        this.arrayDesignDetails = arrayDesignDetails;
        this.vocabularyDao = vocabularyDao;
        this.arrayDao = arrayDao;
        this.searchDao = searchDao;
        lookupMillimeterTerm();
    }

    private TermSource getSource(String name, String version) {
        final String versionField = "version";

        TermSource querySource = new TermSource();
        querySource.setName(name);
        querySource.setVersion(version);
        return CaArrayUtils.uniqueResult(vocabularyDao.queryEntityByExample(
                ExampleSearchCriteria.forEntity(querySource).includeNulls().excludeProperties("url"),
                Order.desc(versionField)));
    }

    private void lookupMillimeterTerm() {
        final String termSourceName = "MO";
        final String termSourceVersion = "1.3.1.1";
        final String millimeterString = "mm";

        TermSource source = getSource(termSourceName, termSourceVersion);
        if (null == source) {
            throw new AgilentParseException(String.format(
                    "Could not find the \"%s\" term source, version %s", termSourceName, termSourceVersion)); 
        }

        millimeterTerm = vocabularyDao.getTerm(source, millimeterString);        
        if (null == millimeterTerm) {
            throw new AgilentParseException(String.format(
                    "Could not look up the term for \"%s\".", millimeterString)); 
        }
    }

    /**
     * {@inheritDoc}
     */
    public PhysicalProbeBuilder findOrCreatePhysicalProbeBuilder(String name) {
        if (LOG.isTraceEnabled()) {
            LOG.trace("1 findOrCreatePhysicalProbeBuilder(String) called: name=" + name + "=");
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
        if (LOG.isTraceEnabled()) {
            LOG.trace("2 createFeatureBuilder(int) called: featureNumber=" + featureNumber + "=");
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
        if (LOG.isTraceEnabled()) {
            LOG.trace("3 setCoordinates(double, double, String) called: x=" + x + "=, y =" + y + "=, units =" + units
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
        if (LOG.isTraceEnabled()) {
            LOG.trace("4 addToProbeGroup(String) called: probeGroupName=" + probeGroupName + "=");
        }
        ProbeGroup ignoreProbeGroup = getOrCreateProbeGroup(probeGroupName);
        addCurrentPhysicalProbeToProbeGroup(ignoreProbeGroup);
        return this;
    }

    private ProbeGroup getOrCreateProbeGroup(String probeGroupName) {
        if (LOG.isTraceEnabled()) {
            LOG.trace("5 getOrCreateProbeGroup(String) called: probeGroupName=" + probeGroupName + "=");
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
        if (LOG.isTraceEnabled()) {
            LOG.trace("6 createProbeGroup(String) called: name=" + name + "=");
        }
        ProbeGroup probeGroup = new ProbeGroup(arrayDesignDetails);
        probeGroup.setName(name);
        arrayDao.save(probeGroup);
        createdProbeGroupIds.put(name, probeGroup.getId());
        return probeGroup;
    }

    private void addCurrentPhysicalProbeToProbeGroup(final ProbeGroup probeGroup) {
        if (LOG.isTraceEnabled()) {
            LOG.trace("7 addCurrentPhysicalProbeToProbeGroup(ProbeGroup) called: probeGroup=" + probeGroup + "=");
        }
        currentPhysicalProbe.setProbeGroup(probeGroup);
    }

    /**
     * {@inheritDoc}
     */
    public PhysicalProbeBuilder setBiosequenceRef(String database, String species, String identifier) {
        if (LOG.isTraceEnabled()) {
            LOG.trace("8 setBiosequenceRef(String, String, String) called: database=" + database + "=, species ="
                    + species + "=, identifier =" + identifier + "=");
        }
        String key = buildBiosequenceKey(species, identifier);
        getBiosequenceReIdsfMap().put(key, currentPhysicalProbe.getId());
        return this;
    }

    private String buildBiosequenceKey(String species, String identifier) {
        if (LOG.isTraceEnabled()) {
            LOG.trace("9 buildBiosequenceKey(String, String) called: species=" + species + "=, identifier ="
                    + identifier + "=");
        }
        return String.format("`%s`%s`", species, identifier);
    }

    /**
     * {@inheritDoc}
     */
    public GeneBuilder createGeneBuilder(String geneName) {
        if (LOG.isTraceEnabled()) {
            LOG.trace("10 createGeneBuilder(String) called: geneName=" + geneName + "=");
        }
        createExpressionProbeAnnotation(currentPhysicalProbe, geneName);
        return this;
    }

    private ExpressionProbeAnnotation createExpressionProbeAnnotation(PhysicalProbe physicalProbe, String geneName) {
        if (LOG.isTraceEnabled()) {
            LOG.trace("11 createExpressionProbeAnnotation(PhysicalProbe, String) called: physicalProbe ="
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
        if (LOG.isTraceEnabled()) {
            LOG.trace("12 createExpressionProbeAnnotation(PhysicalProbe) called: physicalProbe=" + physicalProbe + "=");
        }
        String geneName = null;
        return createExpressionProbeAnnotation(physicalProbe, geneName);
    }

    /**
     * {@inheritDoc}
     */
    public GeneBuilder setChromosomeLocation(String chromosomeName,
            long startPosition, long endPosition) {
        if (LOG.isTraceEnabled()) {
            LOG.trace("13 setChromosomeLocation(String, long, long) called: chromosomeName=" + chromosomeName
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
        if (LOG.isTraceEnabled()) {
            LOG.trace("14 createNewGBAccession(String) called: accessionNumber=" + accessionNumber + "=");
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
        if (LOG.isTraceEnabled()) {
            LOG.trace("15 createNewEnsemblAccession(String) called: accessionNumber=" + accessionNumber + "=");
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
        if (LOG.isTraceEnabled()) {
            LOG.trace("16 createNewRefSeqAccession(String) called: accessionNumber=" + accessionNumber + "=");
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
        if (LOG.isTraceEnabled()) {
            LOG.trace("17 createNewTHCAccession(String) called: accessionNumber=" + accessionNumber + "=");
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
        if (LOG.isTraceEnabled()) {
            LOG.trace("18 createBiosequenceBuilder(String, String) called: controlType=" + controlType + "=, species ="
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
            if (LOG.isTraceEnabled()) {
                LOG.trace("19 BiosequenceBuilderImpl(ArrayDesignBuilderImpl, String) called: parentBuilder="
                        + parentBuilder + "=, species =" + species + "=");
            }
            this.parentBuilder = parentBuilder;
            this.species = species;
        }

        /**
         * {@inheritDoc}
         */
        public BiosequenceBuilder agpAccession(String accessionNumber) {
            if (LOG.isTraceEnabled()) {
                LOG.trace("20 agpAccession(String) called: accessionNumber=" + accessionNumber + "=");
            }
            key = parentBuilder.buildBiosequenceKey(species, accessionNumber);
            return this;
        }

        /**
         * {@inheritDoc}
         */
        public BiosequenceBuilder createNewEnsemblAccession(String accessionNumber) {
            if (LOG.isTraceEnabled()) {
                LOG.trace("21 createNewEnsemblAccession(String) called: accessionNumber=" + accessionNumber + "=");
            }
            addAccession(Gene.ENSEMBLE, accessionNumber);           
            return this;
        }

        /**
         * {@inheritDoc}
         */
        public BiosequenceBuilder createNewGBAccession(String accessionNumber) {
            if (LOG.isTraceEnabled()) {
                LOG.trace("22 createNewGBAccession(String) called: accessionNumber=" + accessionNumber + "=");
            }
            addAccession(Gene.GENBANK, accessionNumber);           
            return this;
        }

        /**
         * {@inheritDoc}
         */
        public BiosequenceBuilder createNewMirAccession(String accessionNumber) {
            if (LOG.isTraceEnabled()) {
                LOG.trace("23 createNewMirAccession(String) called: accessionNumber=" + accessionNumber + "=");
            }
            isMiRNA = true;
            addAccession(MiRNAProbeAnnotation.MIR, accessionNumber);           
            return this;
        }

        /**
         * {@inheritDoc}
         */
        public BiosequenceBuilder createNewRefSeqAccession(String accessionNumber) {
            if (LOG.isTraceEnabled()) {
                LOG.trace("24 createNewRefSeqAccession(String) called: accessionNumber=" + accessionNumber + "=");
            }
            addAccession(Gene.REF_SEQ, accessionNumber);           
            return this;
        }

        /**
         * {@inheritDoc}
         */
        public BiosequenceBuilder createNewTHCAccession(String accessionNumber) {
            if (LOG.isTraceEnabled()) {
                LOG.trace("25 createNewTHCAccession(String) called: accessionNumber=" + accessionNumber + "=");
            }
            addAccession(Gene.THC, accessionNumber);           
            return this;
        }
        
        /**
         * {@inheritDoc}
         */
        public BiosequenceBuilder finish() {
            if (LOG.isTraceEnabled()) {
                LOG.trace("26 finish() called");
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
            if (LOG.isTraceEnabled()) {
                LOG.trace("27 createAnnotationWithAccessions(PhysicalProbe) called: physicalProbe=" + physicalProbe
                        + "=");
            }
            if (isMiRNA) {
                createMiRNAAnnotationWithAccessions(physicalProbe);
            } else {
                createExpressionAnnotationWithAccessions(physicalProbe);
            }
        }

        private void createExpressionAnnotationWithAccessions(PhysicalProbe physicalProbe) {
            if (LOG.isTraceEnabled()) {
                LOG.trace("28 createExpressionAnnotationWithAccessions(PhysicalProbe) called: physicalProbe="
                        + physicalProbe + "=");
            }
            ExpressionProbeAnnotation annotation = createExpressionProbeAnnotation(physicalProbe);             
            Gene gene = annotation.getGene();
   
            for (AccessionNumber accessionNumber : accessionNumbers) {
                gene.addAccessionNumber(accessionNumber.getDatabase(), accessionNumber.getAccession());
            }
        }

        private void createMiRNAAnnotationWithAccessions(PhysicalProbe physicalProbe) {
            if (LOG.isTraceEnabled()) {
                LOG.trace("29 createMiRNAAnnotationWithAccessions(PhysicalProbe) called: physicalProbe="
                        + physicalProbe + "=");
            }
            MiRNAProbeAnnotation annotation = new MiRNAProbeAnnotation();
            physicalProbe.setAnnotation(annotation);
            
            for (AccessionNumber accessionNumber : accessionNumbers) {
                annotation.addAccessionNumber(accessionNumber.getDatabase(), accessionNumber.getAccession());
            }
        }

        private void addAccession(String database, String accessionNumber) {
            if (LOG.isTraceEnabled()) {
                LOG.trace("30 addAccession(String, String) called: database=" + database + "=, accessionNumber ="
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
        if (LOG.isTraceEnabled()) {
            LOG.trace("31 processChunk() called");
        }
        arrayDao.save(currentFeature);
        arrayDao.save(currentPhysicalProbe);
        if (forceFlush || ++batchCount % BATCH_SIZE == 0) {
            arrayDao.flushSession();
            arrayDao.clearSession();
        }
    }
}
