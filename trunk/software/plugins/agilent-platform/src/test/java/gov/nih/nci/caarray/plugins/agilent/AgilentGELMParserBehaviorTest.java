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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyDouble;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import gov.nih.nci.caarray.plugins.agilent.AgilentGELMToken.Token;

import org.junit.Test;

public class AgilentGELMParserBehaviorTest {
    final private TokenizerStub tokenizer = new TokenizerStub();
    final private AgilentGELMParser parser = new AgilentGELMParser(tokenizer);
    final private AbstractBuilder builder = createMockBuilder();

    @Test
    public void reporterCreatesPhysicalProbe() {
        final String reporterName = "reporter name";
        final String geneName = "gene name";
        final int featureNumber = 123;

        add(Token.DOCUMENT_START);
        add(Token.PROJECT_START);
        add(Token.ATTRIBUTE_END);
        add(Token.PATTERN_START);
        add(Token.ATTRIBUTE_END);
        add(Token.REPORTER_START);
        add(Token.NAME, reporterName);
        add(Token.SYSTEMATIC_NAME);
        add(Token.ATTRIBUTE_END);
        add(Token.FEATURE_START);
        add(Token.NUMBER, featureNumber);
        add(Token.ATTRIBUTE_END);
        add(Token.POSITION_START);
        add(Token.UNITS, "mm");
        add(Token.X, 0.00000000000000001);
        add(Token.Y, 0.00000000000000001);
        add(Token.ATTRIBUTE_END);
        repeat(2, Token.ELEMENT_END);
        add(Token.GENE_START);
        add(Token.PRIMARY_NAME, geneName);
        add(Token.ATTRIBUTE_END);
        repeat(4, Token.ELEMENT_END);
        add(Token.DOCUMENT_END);
        add(Token.EOF);
        
        assertTrue(parser.parse(builder));

        verify(builder).findOrCreatePhysicalProbeBuilder(reporterName);
        verify(builder).createFeatureBuilder(featureNumber);
        verify(builder).setCoordinates(0.00000000000000001, 0.00000000000000001, "mm");
        verify(builder).createGeneBuilder(geneName);
    }

    @Test
    public void physicalProbeSetsBioSequenceRef() {
        final String reporterName = "reporter name";
        final String databaseName = "database name";
        final String identifier = "identifier";
        final String species = "species";
        final int featureNumber = 123;

        add(Token.DOCUMENT_START);
        add(Token.PROJECT_START);
        add(Token.ATTRIBUTE_END);
        add(Token.PATTERN_START);
        add(Token.ATTRIBUTE_END);
        add(Token.REPORTER_START);
        add(Token.NAME, reporterName);
        add(Token.SYSTEMATIC_NAME);
        add(Token.ATTRIBUTE_END);
        add(Token.FEATURE_START);
        add(Token.NUMBER, featureNumber);
        add(Token.ATTRIBUTE_END);
        add(Token.POSITION_START);
        add(Token.UNITS, "mm");
        add(Token.X, 0.00000000000000001);
        add(Token.Y, 0.00000000000000001);
        add(Token.ATTRIBUTE_END);
        repeat(2, Token.ELEMENT_END);
        add(Token.BIOSEQUENCE_REF_START);
        add(Token.DATABASE, databaseName);
        add(Token.IDENTIFIER, identifier);
        add(Token.SPECIES, species);
        add(Token.ATTRIBUTE_END);
        repeat(4, Token.ELEMENT_END);
        add(Token.DOCUMENT_END);
        add(Token.EOF);
        
        assertTrue(parser.parse(builder));

        verify(builder).findOrCreatePhysicalProbeBuilder(reporterName);
        verify(builder).createFeatureBuilder(featureNumber);
        verify(builder).setCoordinates(0.00000000000000001, 0.00000000000000001, "mm");
        verify(builder).setBiosequenceRef(databaseName, species, identifier);
    }
    
    @Test
    public void biosequenceBuilderCreatesAccessions() {
        final String controlType = null;
        final String species = "species name";
        final String probeId = "probe id";
        
        add(Token.DOCUMENT_START);
        add(Token.PROJECT_START);
        add(Token.ATTRIBUTE_END);
        add(Token.BIOSEQUENCE_START);
        add(Token.SPECIES, species);
        add(Token.ATTRIBUTE_END);
        add(Token.ACCESSION_START);
        add(Token.DATABASE, "agp");
        add(Token.ID, probeId);
        add(Token.ATTRIBUTE_END);
        add(Token.ELEMENT_END);
        add(Token.ACCESSION_START);
        add(Token.DATABASE, "gb");
        add(Token.ID, "genbank accession");
        add(Token.ATTRIBUTE_END);
        add(Token.ELEMENT_END);
        add(Token.ACCESSION_START);
        add(Token.DATABASE, "ens");
        add(Token.ID, "ensembl accession");
        add(Token.ATTRIBUTE_END);
        add(Token.ELEMENT_END);
        add(Token.ACCESSION_START);
        add(Token.DATABASE, "ref");
        add(Token.ID, "ref accession");
        add(Token.ATTRIBUTE_END);
        add(Token.ELEMENT_END);
        add(Token.ACCESSION_START);
        add(Token.DATABASE, "thc");
        add(Token.ID, "thc accession");
        add(Token.ATTRIBUTE_END);
        repeat(3, Token.ELEMENT_END);
        add(Token.DOCUMENT_END);
        add(Token.EOF);

        assertTrue(parser.parse(builder));

        verify(builder).createBiosequenceBuilder(controlType, species);
        verify(builder).agpAccession(probeId);
        verify(builder).createNewGBAccession("genbank accession");
        verify(builder).createNewEnsemblAccession("ensembl accession");
        verify(builder).createNewRefSeqAccession("ref accession");
        verify(builder).createNewTHCAccession("thc accession");
        verify(builder).finish();
    }
    
    @Test
    public void biosequenceBuilderCreatesMiRnaAccessions() {
        final String controlType = null;
        final String species = "species name";
        final String probeId = "probe id";
        final String mirAccession1 = "mir accession 1";
        final String mirAccession2 = "mir accession 2";
        
        add(Token.DOCUMENT_START);
        add(Token.PROJECT_START);
        add(Token.ATTRIBUTE_END);
        add(Token.BIOSEQUENCE_START);
        add(Token.SPECIES, species);
        add(Token.ATTRIBUTE_END);
        add(Token.ACCESSION_START);
        add(Token.DATABASE, "agp");
        add(Token.ID, probeId);
        add(Token.ATTRIBUTE_END);
        add(Token.ELEMENT_END);
        add(Token.ACCESSION_START);
        add(Token.DATABASE, "mir");
        add(Token.ID, mirAccession1);
        add(Token.ATTRIBUTE_END);
        add(Token.ELEMENT_END);
        add(Token.ACCESSION_START);
        add(Token.DATABASE, "mir");
        add(Token.ID, mirAccession2);
        add(Token.ATTRIBUTE_END);
        repeat(3, Token.ELEMENT_END);
        add(Token.DOCUMENT_END);
        add(Token.EOF);

        assertTrue(parser.parse(builder));

        verify(builder).createBiosequenceBuilder(controlType, species);
        verify(builder).agpAccession(probeId);
        verify(builder).createNewMirAccession(mirAccession1);
        verify(builder).createNewMirAccession(mirAccession2);
        verify(builder).finish();
    }

    @Test
    public void geneBuilderCreatesAccessions() {
        final String reporterName = "reporter name";
        final String geneName = "gene name";
        final int featureNumber = 123;

        add(Token.DOCUMENT_START);
        add(Token.PROJECT_START);
        add(Token.ATTRIBUTE_END);
        add(Token.PATTERN_START);
        add(Token.ATTRIBUTE_END);
        add(Token.REPORTER_START);
        add(Token.NAME, reporterName);
        add(Token.SYSTEMATIC_NAME);
        add(Token.ATTRIBUTE_END);
        add(Token.FEATURE_START);
        add(Token.NUMBER, featureNumber);
        add(Token.ATTRIBUTE_END);
        add(Token.POSITION_START);
        add(Token.UNITS, "mm");
        add(Token.X, 0.00000000000000001);
        add(Token.Y, 0.00000000000000001);
        add(Token.ATTRIBUTE_END);
        repeat(2, Token.ELEMENT_END);
        add(Token.GENE_START);
        add(Token.PRIMARY_NAME, geneName);
        add(Token.ATTRIBUTE_END);
        add(Token.ACCESSION_START);
        add(Token.DATABASE, "gb");
        add(Token.ID, "genbank accession");
        add(Token.ATTRIBUTE_END);
        add(Token.ELEMENT_END);
        add(Token.ACCESSION_START);
        add(Token.DATABASE, "ens");
        add(Token.ID, "ensembl accession");
        add(Token.ATTRIBUTE_END);
        add(Token.ELEMENT_END);
        add(Token.ACCESSION_START);
        add(Token.DATABASE, "ref");
        add(Token.ID, "ref accession");
        add(Token.ATTRIBUTE_END);
        add(Token.ELEMENT_END);
        add(Token.ACCESSION_START);
        add(Token.DATABASE, "thc");
        add(Token.ID, "thc accession");
        add(Token.ATTRIBUTE_END);
        repeat(5, Token.ELEMENT_END);
        add(Token.DOCUMENT_END);
        add(Token.EOF);

        assertTrue(parser.parse(builder));

        verify(builder).findOrCreatePhysicalProbeBuilder(reporterName);
        verify(builder).createFeatureBuilder(featureNumber);
        verify(builder).setCoordinates(0.00000000000000001, 0.00000000000000001, "mm");
        verify(builder).createGeneBuilder(geneName);
        verify(builder).createNewGBAccession("genbank accession");
        verify(builder).createNewEnsemblAccession("ensembl accession");
        verify(builder).createNewRefSeqAccession("ref accession");
        verify(builder).createNewTHCAccession("thc accession");
    }


    @Test
    public void setsChromosomeLocation() {
        final String reporterName = "reporter name";
        final String geneName = "gene name";
        final int featureNumber = 123;

        add(Token.DOCUMENT_START);
        add(Token.PROJECT_START);
        add(Token.ATTRIBUTE_END);
        add(Token.PATTERN_START);
        add(Token.ATTRIBUTE_END);
        add(Token.REPORTER_START);
        add(Token.NAME, reporterName);
        add(Token.SYSTEMATIC_NAME);
        add(Token.ATTRIBUTE_END);
        add(Token.FEATURE_START);
        add(Token.NUMBER, featureNumber);
        add(Token.ATTRIBUTE_END);
        add(Token.POSITION_START);
        add(Token.UNITS, "mm");
        add(Token.X, 0.00000000000000001);
        add(Token.Y, 0.00000000000000001);
        add(Token.ATTRIBUTE_END);
        repeat(2, Token.ELEMENT_END);
        add(Token.GENE_START);
        add(Token.PRIMARY_NAME, geneName);
        add(Token.ATTRIBUTE_END);
        add(Token.ACCESSION_START);
        add(Token.DATABASE, "gb");
        add(Token.ID, "genbank accession");
        add(Token.ATTRIBUTE_END);
        add(Token.ELEMENT_END);
        add(Token.OTHER_START);
        add(Token.NAME, "probe_mappings");
        add(Token.VALUE, "chr2:111111111-999999999");
        add(Token.ATTRIBUTE_END);
        repeat(5, Token.ELEMENT_END);
        add(Token.DOCUMENT_END);
        add(Token.EOF);

        assertTrue(parser.parse(builder));

        verify(builder).findOrCreatePhysicalProbeBuilder(reporterName);
        verify(builder).createFeatureBuilder(featureNumber);
        verify(builder).setCoordinates(0.00000000000000001, 0.00000000000000001, "mm");
        verify(builder).createGeneBuilder(geneName);
        verify(builder).createNewGBAccession("genbank accession");
        verify(builder).setChromosomeLocation("2",111111111,999999999);
   }

    @Test
    public void probeWithEmptyNameAndNoControlTypeIsRejected() {
        final String reporterName = "";
        final int featureNumber = 123;

        add(Token.DOCUMENT_START);
        add(Token.PROJECT_START);
        add(Token.ATTRIBUTE_END);
        add(Token.PATTERN_START);
        add(Token.ATTRIBUTE_END);
        add(Token.REPORTER_START);
        add(Token.NAME, reporterName);
        add(Token.SYSTEMATIC_NAME);
        add(Token.ATTRIBUTE_END);
        add(Token.FEATURE_START);
        add(Token.NUMBER, featureNumber);
        add(Token.ATTRIBUTE_END);
        add(Token.POSITION_START);
        add(Token.UNITS);
        add(Token.X);
        add(Token.Y);
        add(Token.ATTRIBUTE_END);
        repeat(5, Token.ELEMENT_END);
        add(Token.DOCUMENT_END);
        add(Token.EOF);

        assertParserRejects();
    }

    @Test
    public void probeWithEmptyNameAndPosControlTypeIsRejected() {
        final String reporterName = "";
        final String controlType = "pos";
        final int featureNumber = 123;

        setupTokenStream(reporterName, controlType, featureNumber);

        assertParserRejects();
    }

    @Test
    public void probeWithEmptyNameAndNegControlTypeIsRejected() {
        final String reporterName = "";
        final String controlType = "neg";
        final int featureNumber = 123;

        setupTokenStream(reporterName, controlType, featureNumber);

        assertParserRejects();
    }

    @Test
    public void probeWithControlTypeIgnoreAndEmptyNameIsPlacedInIgnoreProbeGroup() {
        final String reporterName = "";
        final String controlType = "ignore";
        final int featureNumber = 123;

        setupTokenStream(reporterName, controlType, featureNumber);
        assertTrue(parser.parse(builder));
        verifyCalls(reporterName, controlType, featureNumber, builder);
    }

    @Test
    public void probeWithControlTypeIgnoreAndNameNAIsPlacedInIgnoreProbeGroup() {
        final String reporterName = "NA";
        final String controlType = "ignore";
        final int featureNumber = 123;

        setupTokenStream(reporterName, controlType, featureNumber);
        assertTrue(parser.parse(builder));
        verifyCalls(reporterName, controlType, featureNumber, builder);
    }

    @Test
    public void probeWithControlTypeIgnoreAndNameStartingWithNAIsPlacedInIgnoreProbeGroup() {
        final String reporterName = "NA12345";
        final String controlType = "ignore";
        final int featureNumber = 123;

        setupTokenStream(reporterName, controlType, featureNumber);
        assertTrue(parser.parse(builder));
        verifyCalls(reporterName, controlType, featureNumber, builder);
    }

    @Test
    public void probeWithControlTypeIgnoreAndAnyOtherNameIsRejected() {
        final String reporterName = "not empty or 'NA'";
        final String controlType = "ignore";
        final int featureNumber = 123;

        setupTokenStream(reporterName, controlType, featureNumber);

        assertParserRejects();
    }

    @Test
    public void probeWithControlTypePosIsPlacedInPosProbeGroup() {
        final String reporterName = "reporter name";
        final String controlType = "pos";
        final int featureNumber = 123;

        setupTokenStream(reporterName, controlType, featureNumber);
        assertTrue(parser.parse(builder));
        verifyCalls(reporterName, controlType, featureNumber, builder);
    }

    @Test
    public void probeWithControlTypeNegIsPlacedInNegProbeGroup() {
        final String reporterName = "reporter name";
        final String controlType = "neg";
        final int featureNumber = 123;

        setupTokenStream(reporterName, controlType, featureNumber);
        assertTrue(parser.parse(builder));
        verifyCalls(reporterName, controlType, featureNumber, builder);
    }

    private void verifyCalls(final String reporterName, final String controlType, final int featureNumber,
            final AbstractBuilder arrayDesignBuilder) {
        final String negativeControlsControlGroup = "negative controls";
        final String positiveControlsControlGroup = "positive controls";
        final String ignoreControlGroup = "ignore";
        
        verify(arrayDesignBuilder).findOrCreatePhysicalProbeBuilder(reporterName);
        verify(arrayDesignBuilder).createFeatureBuilder(featureNumber);

        if ("ignore".equalsIgnoreCase(controlType)) {
            verify(arrayDesignBuilder).addToProbeGroup(ignoreControlGroup);
        } else if ("pos".equalsIgnoreCase(controlType)) {
            verify(arrayDesignBuilder).addToProbeGroup(positiveControlsControlGroup);
        } else if ("neg".equalsIgnoreCase(controlType)) {
            verify(arrayDesignBuilder).addToProbeGroup(negativeControlsControlGroup);
        }

        verify(arrayDesignBuilder).setCoordinates(0.00000000000000001, 0.00000000000000001, "mm");
    }

    private void setupTokenStream(String reporterName, String controlType, int featureNumber) {
        add(Token.DOCUMENT_START);
        add(Token.PROJECT_START);
        add(Token.ATTRIBUTE_END);
        add(Token.PATTERN_START);
        add(Token.ATTRIBUTE_END);
        add(Token.REPORTER_START);
        add(Token.CONTROL_TYPE, controlType);
        add(Token.NAME, reporterName);
        add(Token.SYSTEMATIC_NAME);
        add(Token.ATTRIBUTE_END);
        add(Token.FEATURE_START);
        add(Token.NUMBER, featureNumber);
        add(Token.ATTRIBUTE_END);
        add(Token.POSITION_START);
        add(Token.UNITS, "mm");
        add(Token.X, 0.00000000000000001);
        add(Token.Y, 0.00000000000000001);
        add(Token.ATTRIBUTE_END);
        repeat(5, Token.ELEMENT_END);
        add(Token.DOCUMENT_END);
        add(Token.EOF);
    }

    private void assertParserRejects() {
        assertFalse(parser.validate());
    }

    private void add(Token token) {
        tokenizer.add(token);
    }

    private void add(Token token, Object value) {
        tokenizer.add(token, value);
    }

    private void repeat(int count, Token token) {
        tokenizer.repeat(count, token);
    }
    
    private abstract class AbstractBuilder implements ArrayDesignBuilder, PhysicalProbeBuilder,
            FeatureBuilder, GeneBuilder, AccessionBuilder, BiosequenceBuilder {
    }

    private AbstractBuilder createMockBuilder() {
        AbstractBuilder newBuilder = mock(AbstractBuilder.class);
        
        when(newBuilder.createFeatureBuilder(anyInt())).thenReturn(newBuilder);
        when(newBuilder.setCoordinates(anyDouble(), anyDouble(), anyString())).thenReturn(newBuilder);
        when(newBuilder.createGeneBuilder(anyString())).thenReturn(newBuilder);
        when(newBuilder.createNewEnsemblAccession(anyString())).thenReturn(newBuilder);
        when(newBuilder.createNewGBAccession(anyString())).thenReturn(newBuilder);
        when(newBuilder.setChromosomeLocation(anyString(), anyLong(), anyLong())).thenReturn(newBuilder);
        when(newBuilder.addToProbeGroup(anyString())).thenReturn(newBuilder);
        when(newBuilder.findOrCreatePhysicalProbeBuilder(anyString())).thenReturn(newBuilder);
        when(newBuilder.createBiosequenceBuilder(anyString(), anyString())).thenReturn(newBuilder);
        when(newBuilder.finish()).thenReturn(newBuilder);
        
        return newBuilder;
    }
}
