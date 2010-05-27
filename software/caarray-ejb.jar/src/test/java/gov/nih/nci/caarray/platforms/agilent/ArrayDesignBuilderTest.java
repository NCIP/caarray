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

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.refEq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
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

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.hibernate.criterion.Order;
import org.junit.Before;
import org.junit.Test;

public class ArrayDesignBuilderTest {
    private ArrayDesignBuilderImpl arrayDesignBuilder;
    private ArrayDesignDetails arrayDesignDetails;
    private static final Term EXPECTED_MILLIMETER_TERM = new Term();
    
    @Test
    public void createsNewPhysicalProbe() {
        final String probeName = "probe1";
        arrayDesignBuilder.findOrCreatePhysicalProbeBuilder(probeName);

        PhysicalProbe physicalProbe = arrayDesignBuilder.getPhysicalProbes().values().iterator().next();
        assertSame(arrayDesignDetails, physicalProbe.getArrayDesignDetails());
        assertEquals(probeName, physicalProbe.getName());
     }

    @Test
    public void findsExistingPhysicalProbe() {
        final String probe1Name = "probe1";
        arrayDesignBuilder.findOrCreatePhysicalProbeBuilder(probe1Name);

        final String probe2Name = "probe2";
        arrayDesignBuilder.findOrCreatePhysicalProbeBuilder(probe2Name);

        assertEquals(2, arrayDesignBuilder.getPhysicalProbes().values().size());

        arrayDesignBuilder.findOrCreatePhysicalProbeBuilder(probe1Name);

        assertEquals(2, arrayDesignBuilder.getPhysicalProbes().values().size());        
     }

    @Test
    public void putsProbeInIgnoreProbeGroup() {
        final String probeGroupName = "ignore";
        putsProbeInProbeGroup(probeGroupName);
     }

    @Test
    public void putsProbeInPositiveControlProbeGroup() {
        final String probeGroupName = "positive controls";
        putsProbeInProbeGroup(probeGroupName);
     }

    @Test
    public void putsProbeInNegativeControlProbeGroup() {
        final String probeGroupName = "negative controls";
        putsProbeInProbeGroup(probeGroupName);
     }

    private void putsProbeInProbeGroup(final String probeGroupName) {
        final String probeName = "probe";
        arrayDesignBuilder.findOrCreatePhysicalProbeBuilder(probeName);
        arrayDesignBuilder.addToProbeGroup(probeGroupName);
        
        assertProbeGroup(probeGroupName);
    }

    @Test
    public void createsAnnotationOnCurrentPhysicalProbe() {
        final String probe1Name = "probe1";
        final String geneName = "gene1";

        arrayDesignBuilder.findOrCreatePhysicalProbeBuilder(probe1Name);
        PhysicalProbe probe = arrayDesignBuilder.getPhysicalProbes().values().iterator().next();
        assertNull(probe.getAnnotation());

        arrayDesignBuilder.createGeneBuilder(geneName);

        ExpressionProbeAnnotation annotation = (ExpressionProbeAnnotation) probe.getAnnotation();
        assertNotNull(annotation);

        Gene gene = annotation.getGene();
        assertNotNull(gene);

        assertEquals(geneName, gene.getFullName());
    }

    @Test
    public void createsAnnotationForGeneExpressionAssay() {
        final String probe1Name = "probe1";
        final String database = "database name";
        final String species = "species name";
        final String controlType = null;
        
        arrayDesignBuilder.findOrCreatePhysicalProbeBuilder(probe1Name);
        PhysicalProbe probe = arrayDesignBuilder.getPhysicalProbes().values().iterator().next();
        assertNull(probe.getAnnotation());

        arrayDesignBuilder.setBiosequenceRef(database, species, probe1Name);
        BiosequenceBuilder biosequenceBuilder = arrayDesignBuilder.createBiosequenceBuilder(controlType, species);
        biosequenceBuilder.agpAccession(probe1Name);
        biosequenceBuilder.finish();

        ExpressionProbeAnnotation annotation = (ExpressionProbeAnnotation) probe.getAnnotation();
        assertNotNull(annotation);

        Gene gene = annotation.getGene();
        assertNotNull(gene);
    }

    @Test
    public void createsAccessionsForGeneExpressionAssay() {
        final String probe1Name = "probe1";
        final String database = "database name";
        final String species = "species name";
        final String controlType = null;
        
        final String ensemblAccession = "ensembl accession";
        final String genbankAccession = "genebank accession";
        final String refSeqAccession = "refseq accession";
        final String thcAccession = "thc accession";
        
        arrayDesignBuilder.findOrCreatePhysicalProbeBuilder(probe1Name);
        PhysicalProbe probe = arrayDesignBuilder.getPhysicalProbes().values().iterator().next();

        arrayDesignBuilder.setBiosequenceRef(database, species, probe1Name);
        BiosequenceBuilder biosequenceBuilder = arrayDesignBuilder.createBiosequenceBuilder(controlType, species);
        biosequenceBuilder.agpAccession(probe1Name);
        
        biosequenceBuilder.createNewEnsemblAccession(ensemblAccession);
        biosequenceBuilder.createNewGBAccession(genbankAccession);
        biosequenceBuilder.createNewRefSeqAccession(refSeqAccession);
        biosequenceBuilder.createNewTHCAccession(thcAccession);
        biosequenceBuilder.finish();
        
        ExpressionProbeAnnotation annotation = (ExpressionProbeAnnotation) probe.getAnnotation();
        Gene gene = annotation.getGene();
        
        assertEquals(ensemblAccession, gene.getAccessionNumbers(Gene.ENSEMBLE).get(0));
        assertEquals(genbankAccession, gene.getAccessionNumbers(Gene.GENBANK).get(0));
        assertEquals(refSeqAccession, gene.getAccessionNumbers(Gene.REF_SEQ).get(0));
        assertEquals(thcAccession, gene.getAccessionNumbers(Gene.THC).get(0));
    }

    @Test
    public void createsAccessionsForMiRNAAssay() {
        final String probe1Name = "probe1";
        final String database = "database name";
        final String species = "species name";
        final String controlType = null;
        
        final String accession1 = "mir 1";
        final String accession2 = "mir 2";
        final List<String> accessions = Arrays.asList(accession1, accession2);
       
        arrayDesignBuilder.findOrCreatePhysicalProbeBuilder(probe1Name);
        PhysicalProbe probe = arrayDesignBuilder.getPhysicalProbes().values().iterator().next();

        arrayDesignBuilder.setBiosequenceRef(database, species, probe1Name);
        BiosequenceBuilder biosequenceBuilder = arrayDesignBuilder.createBiosequenceBuilder(controlType, species);
        biosequenceBuilder.agpAccession(probe1Name);
        
        biosequenceBuilder.createNewMirAccession(accession1);
        biosequenceBuilder.createNewMirAccession(accession2);
        biosequenceBuilder.finish();
        
        MiRNAProbeAnnotation annotation = (MiRNAProbeAnnotation) probe.getAnnotation();
        
        assertThat(annotation.getAccessionNumbers(MiRNAProbeAnnotation.MIR), is(accessions));
    }

    @Test
    public void ignoresBiosequenceForPositiveControl() {
        final String controlType = "pos";
        
        assertIgnoresBiosquence(controlType);
    }

    @Test
    public void ignoresBiosequenceForNegativeControl() {
        final String controlType = "neg";
        
        assertIgnoresBiosquence(controlType);
    }

    private void assertIgnoresBiosquence(final String controlType) {
        final String probe1Name = "probe1";
        final String database = "database name";
        final String species = "species name";
        
        arrayDesignBuilder.findOrCreatePhysicalProbeBuilder(probe1Name);
        PhysicalProbe probe = arrayDesignBuilder.getPhysicalProbes().values().iterator().next();

        arrayDesignBuilder.setBiosequenceRef(database, species, probe1Name);
        BiosequenceBuilder biosequenceBuilder = arrayDesignBuilder.createBiosequenceBuilder(controlType, species);
        biosequenceBuilder.agpAccession(probe1Name);

        ExpressionProbeAnnotation annotation = (ExpressionProbeAnnotation) probe.getAnnotation();
        
        final String ensemblAccession = "ensembl accession";
        final String genbankAccession = "genebank accession";
        final String refSeqAccession = "refseq accession";
        final String thcAccession = "thc accession";
        
        biosequenceBuilder.createNewEnsemblAccession(ensemblAccession);
        biosequenceBuilder.createNewGBAccession(genbankAccession);
        biosequenceBuilder.createNewRefSeqAccession(refSeqAccession);
        biosequenceBuilder.createNewTHCAccession(thcAccession);
        
        assertNull(annotation);
    }

    @Test
    public void createsGBAccessionOnCurrentGene() {
        final String probe1Name = "probe1";
        final String geneName = "gene1";
        final String accession = "accession1";

        arrayDesignBuilder.findOrCreatePhysicalProbeBuilder(probe1Name);
        PhysicalProbe probe = arrayDesignBuilder.getPhysicalProbes().values().iterator().next();

        arrayDesignBuilder.createGeneBuilder(geneName);
        ExpressionProbeAnnotation annotation = (ExpressionProbeAnnotation) probe.getAnnotation();
        Gene gene = annotation.getGene();

        arrayDesignBuilder.createNewGBAccession(accession);
        assertEquals(accession, gene.getAccessionNumbers(Gene.GENBANK).get(0));
    }

    @Test
    public void createsEnsemblAccessionOnCurrentGene() {
        final String probe1Name = "probe1";
        final String geneName = "gene1";
        final String accession = "accession1";

        arrayDesignBuilder.findOrCreatePhysicalProbeBuilder(probe1Name);
        PhysicalProbe probe = arrayDesignBuilder.getPhysicalProbes().values().iterator().next();

        arrayDesignBuilder.createGeneBuilder(geneName);
        ExpressionProbeAnnotation annotation = (ExpressionProbeAnnotation) probe.getAnnotation();
        Gene gene = annotation.getGene();

        arrayDesignBuilder.createNewEnsemblAccession(accession);
        assertEquals(accession, gene.getAccessionNumbers(Gene.ENSEMBLE).get(0));
    }

    @Test
    public void setsChromosomeLocationForCurrentGene() {
        final String probe1Name = "probe1";
        final String geneName = "gene1";
        final String chromosomeName = "x";
        final Long chromosomeStart = 111111111l;
        final Long chromosomeEnd = 999999999l;

        arrayDesignBuilder.findOrCreatePhysicalProbeBuilder(probe1Name);
        PhysicalProbe probe = arrayDesignBuilder.getPhysicalProbes().values().iterator().next();

        arrayDesignBuilder.createGeneBuilder(geneName);
        ExpressionProbeAnnotation annotation = (ExpressionProbeAnnotation) probe.getAnnotation();

        arrayDesignBuilder.setChromosomeLocation(chromosomeName, chromosomeStart, chromosomeEnd);
        assertEquals(chromosomeName, annotation.getChromosomeName());
        assertEquals(chromosomeStart, annotation.getChromosomeStartPosition());
        assertEquals(chromosomeEnd, annotation.getChromosomeEndPosition());
    }

    @Test
    public void createsFeatureForCurrentPhysicalProbe() {
        final String probe1Name = "probe1";
        final Integer featureNumber = 987654321;

        arrayDesignBuilder.findOrCreatePhysicalProbeBuilder(probe1Name);
        PhysicalProbe probe = arrayDesignBuilder.getPhysicalProbes().values().iterator().next();

        arrayDesignBuilder.createFeatureBuilder(featureNumber);

        assertEquals(1, arrayDesignBuilder.getFeatures().size());
        assertEquals(1, probe.getFeatures().size());

        Feature feature = arrayDesignBuilder.getFeatures().iterator().next();
        assertSame(arrayDesignDetails, feature.getArrayDesignDetails());
        assertEquals(featureNumber, feature.getFeatureNumber());
    }

    @Test
    public void setsCoordinatesOnCurrentFeature() {
        final String probe1Name = "probe1";
        final int featureNumber = 987654321;
        final double x = 1.111111111;
        final double y = 0.999999999;
        final String units = "mm";

        arrayDesignBuilder.findOrCreatePhysicalProbeBuilder(probe1Name);
        PhysicalProbe probe = arrayDesignBuilder.getPhysicalProbes().values().iterator().next();

        arrayDesignBuilder.createFeatureBuilder(featureNumber);
        Feature feature = probe.getFeatures().iterator().next();

        arrayDesignBuilder.setCoordinates(x, y, units);

        assertEquals(x, feature.getX_Coordinate(), 0);
        assertEquals(y, feature.getY_Coordinate(), 0);
        assertSame(EXPECTED_MILLIMETER_TERM, feature.getCoordinateUnits());
    }

    private void assertProbeGroup(String probeGroupName) {

        assertEquals(1, arrayDesignBuilder.getProbeGroups().values().size());

        ProbeGroup probeGroup = arrayDesignBuilder.getProbeGroups().values().iterator().next();
        assertSame(arrayDesignDetails, probeGroup.getArrayDesignDetails());
        assertEquals(probeGroupName, probeGroup.getName());

        PhysicalProbe probe = arrayDesignBuilder.getPhysicalProbes().values().iterator().next();
        assertSame(probeGroup, probe.getProbeGroup());
    }

    @SuppressWarnings("unchecked")
    @Before
    public void setUp() throws Exception {
        final TermSource termSource = new TermSource();
        List<TermSource> termSources = Collections.singletonList(termSource);
        
        VocabularyDao vocabularyDaoMock = mock(VocabularyDao.class);
        when(vocabularyDaoMock.queryEntityByExample((ExampleSearchCriteria<TermSource>)anyObject(), any(Order.class))).thenReturn(termSources);
        when(vocabularyDaoMock.getTerm(refEq(termSource), eq("mm"))).thenReturn(EXPECTED_MILLIMETER_TERM);
        
        arrayDesignBuilder = new ArrayDesignBuilderImpl(vocabularyDaoMock);
        arrayDesignDetails = arrayDesignBuilder.getArrayDesignDetails();
    }
}
