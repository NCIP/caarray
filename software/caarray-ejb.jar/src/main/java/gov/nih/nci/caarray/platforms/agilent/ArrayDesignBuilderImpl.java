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

import gov.nih.nci.caarray.dao.VocabularyDao;
import gov.nih.nci.caarray.domain.array.ArrayDesignDetails;
import gov.nih.nci.caarray.domain.array.ExpressionProbeAnnotation;
import gov.nih.nci.caarray.domain.array.Feature;
import gov.nih.nci.caarray.domain.array.Gene;
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

import org.hibernate.criterion.Order;

/**
 * {@inheritDoc}
 * @author jscott
 *
 */
class ArrayDesignBuilderImpl implements ArrayDesignBuilder, ArrayDesignBuilder.PhysicalProbeBuilder,
        ArrayDesignBuilder.FeatureBuilder, ArrayDesignBuilder.GeneBuilder {
    
    private final VocabularyDao vocabularyDao;
    private final ArrayDesignDetails arrayDesignDetails = new ArrayDesignDetails();
    private final Map<String, ProbeGroup> probeGroups = new HashMap<String, ProbeGroup>();;
    private final Map<String, PhysicalProbe> physicalProbes = new HashMap<String, PhysicalProbe>();
    private final List<Feature> features = new ArrayList<Feature>();
    private PhysicalProbe currentPhysicalProbe;
    private Feature currentFeature;
    private Term millimeterTerm;

    /**
     * @param vocabularyDao a vocabulary DAO
     */
    ArrayDesignBuilderImpl(VocabularyDao vocabularyDao) {
        this.vocabularyDao = vocabularyDao;
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
        PhysicalProbe probe = getPhysicalProbes().get(name);

        if (null == probe) {
            probe = new PhysicalProbe(arrayDesignDetails, null);

            probe.setName(name);
            getPhysicalProbes().put(name, probe);
        }

        currentPhysicalProbe = probe;
        return this;
    }

    /**
     * {@inheritDoc}
     */
    public FeatureBuilder createFeatureBuilder(int featureNumber) {
        Feature feature = new Feature(arrayDesignDetails);
        currentPhysicalProbe.addFeature(feature);

        feature.setFeatureNumber(featureNumber);

        features.add(feature);
        currentFeature = feature;
        return this;
    }

    /**
     * {@inheritDoc}
     */
    public FeatureBuilder setCoordinates(double x, double y, String units) {
        currentFeature.setXCoordinate(x);
        currentFeature.setYCoordinate(y);
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
        ProbeGroup ignoreProbeGroup = getOrCreateProbeGroup(probeGroupName);
        addCurrentPhysicalProbeToProbeGroup(ignoreProbeGroup);
        return this;
    }

    private ProbeGroup getOrCreateProbeGroup(String probeGroupName) {
        ProbeGroup probeGroup = probeGroups.get(probeGroupName);
        if (null == probeGroup) {
            probeGroup = createProbeGroup(probeGroupName);
        }
        return probeGroup;
    }

    private ProbeGroup createProbeGroup(String name) {
        ProbeGroup probeGroup = new ProbeGroup(arrayDesignDetails);
        probeGroup.setName(name);
        probeGroups.put(name, probeGroup);
        return probeGroup;
    }

    private void addCurrentPhysicalProbeToProbeGroup(final ProbeGroup probeGroup) {
        currentPhysicalProbe.setProbeGroup(probeGroup);
    }

    /**
     * {@inheritDoc}
     */
    public GeneBuilder createGeneBuilder(String geneName) {
        Gene gene = new Gene();
        gene.setFullName(geneName);

        ExpressionProbeAnnotation annotation = new ExpressionProbeAnnotation();
        annotation.setGene(gene);

        currentPhysicalProbe.setAnnotation(annotation);
        return this;
    }

    /**
     * {@inheritDoc}
     */
   public GeneBuilder createNewGBAccession(String accessionNumber) {
        ExpressionProbeAnnotation annotation = (ExpressionProbeAnnotation) currentPhysicalProbe.getAnnotation();
        Gene gene = annotation.getGene();
        gene.setGenbankAccession(accessionNumber);
        return this;
    }

   /**
    * {@inheritDoc}
    */
   public GeneBuilder createNewEnsemblAccession(String accessionNumber) {
        ExpressionProbeAnnotation annotation = (ExpressionProbeAnnotation) currentPhysicalProbe.getAnnotation();
        Gene gene = annotation.getGene();
        gene.setEnsemblgeneID(accessionNumber);
        return this;
    }

   /**
    * {@inheritDoc}
    */
   public GeneBuilder setChromosomeLocation(String chromosomeName,
           long startPosition, long endPosition) {
        ExpressionProbeAnnotation annotation = (ExpressionProbeAnnotation) currentPhysicalProbe.getAnnotation();
        annotation.setChromosome(chromosomeName, startPosition, endPosition);
        return this;
    }

    /**
     * @return the array design details object constructed by this class.
     */
    ArrayDesignDetails getArrayDesignDetails() {
        return arrayDesignDetails;
    }

    /**
     * @return the features
     */
    List<Feature> getFeatures() {
        return features;
    }

    /**
     * @return the physicalProbes
     */
    Map<String, PhysicalProbe> getPhysicalProbes() {
        return physicalProbes;
    }

    /**
     * @return the probeGroups
     */
    Map<String, ProbeGroup> getProbeGroups() {
        return probeGroups;
    }
}
