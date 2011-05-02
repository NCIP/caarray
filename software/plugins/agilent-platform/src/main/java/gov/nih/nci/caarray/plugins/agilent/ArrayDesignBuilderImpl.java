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
package gov.nih.nci.caarray.plugins.agilent;

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

import org.hibernate.criterion.Order;

/**
 * {@inheritDoc}
 * 
 * @author jscott
 * 
 */
class ArrayDesignBuilderImpl implements ArrayDesignBuilder, PhysicalProbeBuilder, FeatureBuilder, GeneBuilder,
        AccessionBuilder {

    private final VocabularyDao vocabularyDao;
    private final ArrayDesignDetails arrayDesignDetails = new ArrayDesignDetails();
    private final Map<String, ProbeGroup> probeGroups = new HashMap<String, ProbeGroup>();;
    private final Map<String, PhysicalProbe> physicalProbes = new HashMap<String, PhysicalProbe>();
    private final List<Feature> features = new ArrayList<Feature>();
    private PhysicalProbe currentPhysicalProbe;
    private Feature currentFeature;
    private Term millimeterTerm;
    private final Map<String, PhysicalProbe> biosequenceRefMap = new HashMap<String, PhysicalProbe>();

    /**
     * @param vocabularyDao a vocabulary DAO
     */
    ArrayDesignBuilderImpl(VocabularyDao vocabularyDao) {
        this.vocabularyDao = vocabularyDao;
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
    @Override
    public PhysicalProbeBuilder findOrCreatePhysicalProbeBuilder(String name) {
        PhysicalProbe probe = getPhysicalProbes().get(name);

        if (null == probe) {
            probe = new PhysicalProbe(this.arrayDesignDetails, null);

            probe.setName(name);
            getPhysicalProbes().put(name, probe);
        }

        this.currentPhysicalProbe = probe;
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public FeatureBuilder createFeatureBuilder(int featureNumber) {
        final Feature feature = new Feature(this.arrayDesignDetails);
        this.currentPhysicalProbe.addFeature(feature);

        feature.setFeatureNumber(featureNumber);

        this.features.add(feature);
        this.currentFeature = feature;
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public FeatureBuilder setCoordinates(double x, double y, String units) {
        this.currentFeature.setX_Coordinate(x);
        this.currentFeature.setY_Coordinate(y);
        if ("mm".equalsIgnoreCase(units)) {
            this.currentFeature.setCoordinateUnits(this.millimeterTerm);
        } else {
            throw new AgilentParseException(String.format("Unexpected units \"%s\"", units));
        }
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PhysicalProbeBuilder addToProbeGroup(String probeGroupName) {
        final ProbeGroup ignoreProbeGroup = getOrCreateProbeGroup(probeGroupName);
        addCurrentPhysicalProbeToProbeGroup(ignoreProbeGroup);
        return this;
    }

    private ProbeGroup getOrCreateProbeGroup(String probeGroupName) {
        ProbeGroup probeGroup = this.probeGroups.get(probeGroupName);
        if (null == probeGroup) {
            probeGroup = createProbeGroup(probeGroupName);
        }
        return probeGroup;
    }

    private ProbeGroup createProbeGroup(String name) {
        final ProbeGroup probeGroup = new ProbeGroup(this.arrayDesignDetails);
        probeGroup.setName(name);
        this.probeGroups.put(name, probeGroup);
        return probeGroup;
    }

    private void addCurrentPhysicalProbeToProbeGroup(final ProbeGroup probeGroup) {
        this.currentPhysicalProbe.setProbeGroup(probeGroup);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PhysicalProbeBuilder setBiosequenceRef(String database, String species, String identifier) {
        final String key = buildBiosequenceKey(species, identifier);
        getBiosequenceRefMap().put(key, this.currentPhysicalProbe);
        return this;
    }

    private String buildBiosequenceKey(String species, String identifier) {
        return String.format("`%s`%s`", species, identifier);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GeneBuilder createGeneBuilder(String geneName) {
        createExpressionProbeAnnotation(this.currentPhysicalProbe, geneName);
        return this;
    }

    private ExpressionProbeAnnotation createExpressionProbeAnnotation(PhysicalProbe physicalProbe, String geneName) {
        final Gene gene = new Gene();
        gene.setFullName(geneName);

        final ExpressionProbeAnnotation annotation = new ExpressionProbeAnnotation();
        annotation.setGene(gene);

        physicalProbe.setAnnotation(annotation);

        return annotation;
    }

    private ExpressionProbeAnnotation createExpressionProbeAnnotation(PhysicalProbe physicalProbe) {
        final String geneName = null;
        return createExpressionProbeAnnotation(physicalProbe, geneName);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GeneBuilder setChromosomeLocation(String chromosomeName, long startPosition, long endPosition) {
        final ExpressionProbeAnnotation annotation = (ExpressionProbeAnnotation) this.currentPhysicalProbe
                .getAnnotation();
        annotation.setChromosome(chromosomeName, startPosition, endPosition);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AccessionBuilder createNewGBAccession(String accessionNumber) {
        final ExpressionProbeAnnotation annotation = (ExpressionProbeAnnotation) this.currentPhysicalProbe
                .getAnnotation();
        final Gene gene = annotation.getGene();
        gene.addAccessionNumber(Gene.GENBANK, accessionNumber);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AccessionBuilder createNewEnsemblAccession(String accessionNumber) {
        final ExpressionProbeAnnotation annotation = (ExpressionProbeAnnotation) this.currentPhysicalProbe
                .getAnnotation();
        final Gene gene = annotation.getGene();
        gene.addAccessionNumber(Gene.ENSEMBLE, accessionNumber);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AccessionBuilder createNewRefSeqAccession(String accessionNumber) {
        final ExpressionProbeAnnotation annotation = (ExpressionProbeAnnotation) this.currentPhysicalProbe
                .getAnnotation();
        final Gene gene = annotation.getGene();
        gene.addAccessionNumber(Gene.REF_SEQ, accessionNumber);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AccessionBuilder createNewTHCAccession(String accessionNumber) {
        final ExpressionProbeAnnotation annotation = (ExpressionProbeAnnotation) this.currentPhysicalProbe
                .getAnnotation();
        final Gene gene = annotation.getGene();
        gene.addAccessionNumber(Gene.THC, accessionNumber);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AccessionBuilder agpAccession(String probeId) {
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AccessionBuilder createNewMirAccession(String accessionNumber) {
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BiosequenceBuilder createBiosequenceBuilder(String controlType, String species) {
        final String positiveControlLabel = "pos";

        final String negativeControlLabel = "neg";
        if (positiveControlLabel.equalsIgnoreCase(controlType) || negativeControlLabel.equalsIgnoreCase(controlType)) {
            return new NullBiosequenceBuilder();
        } else {
            return new BiosequenceBuilderImpl(this, species);
        }
    }

    /**
     * @return the array design details object constructed by this class.
     */
    ArrayDesignDetails getArrayDesignDetails() {
        return this.arrayDesignDetails;
    }

    /**
     * @return the features
     */
    List<Feature> getFeatures() {
        return this.features;
    }

    /**
     * @return the physicalProbes
     */
    Map<String, PhysicalProbe> getPhysicalProbes() {
        return this.physicalProbes;
    }

    /**
     * @return the probeGroups
     */
    Map<String, ProbeGroup> getProbeGroups() {
        return this.probeGroups;
    }

    private Map<String, PhysicalProbe> getBiosequenceRefMap() {
        return this.biosequenceRefMap;
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
            this.parentBuilder = parentBuilder;
            this.species = species;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public BiosequenceBuilder agpAccession(String accessionNumber) {
            this.key = this.parentBuilder.buildBiosequenceKey(this.species, accessionNumber);
            return this;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public BiosequenceBuilder createNewEnsemblAccession(String accessionNumber) {
            addAccession(Gene.ENSEMBLE, accessionNumber);
            return this;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public BiosequenceBuilder createNewGBAccession(String accessionNumber) {
            addAccession(Gene.GENBANK, accessionNumber);
            return this;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public BiosequenceBuilder createNewMirAccession(String accessionNumber) {
            this.isMiRNA = true;
            addAccession(MiRNAProbeAnnotation.MIR, accessionNumber);
            return this;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public BiosequenceBuilder createNewRefSeqAccession(String accessionNumber) {
            addAccession(Gene.REF_SEQ, accessionNumber);
            return this;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public BiosequenceBuilder createNewTHCAccession(String accessionNumber) {
            addAccession(Gene.THC, accessionNumber);
            return this;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public BiosequenceBuilder finish() {
            final PhysicalProbe physicalProbe = this.parentBuilder.getBiosequenceRefMap().get(this.key);

            if (null != physicalProbe) {
                createAnnotationWithAccessions(physicalProbe);
            }

            return this;
        }

        private void createAnnotationWithAccessions(PhysicalProbe physicalProbe) {
            if (this.isMiRNA) {
                createMiRNAAnnotationWithAccessions(physicalProbe);
            } else {
                createExpressionAnnotationWithAccessions(physicalProbe);
            }
        }

        private void createExpressionAnnotationWithAccessions(PhysicalProbe physicalProbe) {
            final ExpressionProbeAnnotation annotation = createExpressionProbeAnnotation(physicalProbe);
            final Gene gene = annotation.getGene();

            for (final AccessionNumber accessionNumber : this.accessionNumbers) {
                gene.addAccessionNumber(accessionNumber.getDatabase(), accessionNumber.getAccession());
            }
        }

        private void createMiRNAAnnotationWithAccessions(PhysicalProbe physicalProbe) {
            final MiRNAProbeAnnotation annotation = new MiRNAProbeAnnotation();
            physicalProbe.setAnnotation(annotation);

            for (final AccessionNumber accessionNumber : this.accessionNumbers) {
                annotation.addAccessionNumber(accessionNumber.getDatabase(), accessionNumber.getAccession());
            }
        }

        private void addAccession(String database, String accessionNumber) {
            this.accessionNumbers.add(new AccessionNumber(database, accessionNumber));
        }

        /**
         * Stores accession numbers for association with an annotation.
         * 
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
                return this.database;
            }

            public String getAccession() {
                return this.accession;
            }
        }
    }

    /**
     * Used for controls, this biosequenceBuilder does nothing.
     * 
     * @author jscott
     */
    private class NullBiosequenceBuilder implements BiosequenceBuilder {

        /**
         * {@inheritDoc}
         */
        @Override
        public BiosequenceBuilder agpAccession(String accessionNumber) {
            // Do nothing
            return this;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public BiosequenceBuilder createNewEnsemblAccession(String accessionNumber) {
            // Do nothing
            return this;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public BiosequenceBuilder createNewGBAccession(String accessionNumber) {
            // Do nothing
            return this;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public BiosequenceBuilder createNewMirAccession(String accessionNumber) {
            // Do nothing
            return this;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public BiosequenceBuilder createNewRefSeqAccession(String accessionNumber) {
            // Do nothing
            return this;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public BiosequenceBuilder createNewTHCAccession(String accessionNumber) {
            // Do nothing
            return this;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public BiosequenceBuilder finish() {
            return this;
        }

    }
}
