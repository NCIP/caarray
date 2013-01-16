//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
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
