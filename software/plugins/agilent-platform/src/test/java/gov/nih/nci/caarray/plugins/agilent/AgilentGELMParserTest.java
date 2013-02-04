//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package gov.nih.nci.caarray.plugins.agilent;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import gov.nih.nci.caarray.plugins.agilent.AgilentGELMToken.Token;

import org.junit.Ignore;
import org.junit.Test;

public class AgilentGELMParserTest {
    final private TokenizerStub tokenizer = new TokenizerStub();
    final private AgilentGELMParser parser = new AgilentGELMParser(tokenizer);

    @Test
    public void acceptsDocumentWithProject() {
        add(Token.DOCUMENT_START);
        add(Token.PROJECT_START);
        add(Token.ATTRIBUTE_END);
        add(Token.ELEMENT_END);
        add(Token.DOCUMENT_END);
        add(Token.EOF);

        assertParserAccepts();
    }

    @Test
    public void acceptsProjectWithOptionalAttributes() {
        add(Token.DOCUMENT_START);
        add(Token.PROJECT_START);
        add(Token.BY);
        add(Token.COMPANY);
        add(Token.DATE);
        add(Token.ID);
        add(Token.NAME);
        add(Token.ORGANIZATION);
        add(Token.ATTRIBUTE_END);
        add(Token.ELEMENT_END);
        add(Token.DOCUMENT_END);
        add(Token.EOF);

        assertParserAccepts();
    }

    @Test
    public void acceptsProjectWithPrinting() {
        add(Token.DOCUMENT_START);
        add(Token.PROJECT_START);
        add(Token.ATTRIBUTE_END);
        add(Token.PRINTING_START);
        add(Token.ATTRIBUTE_END);
        add(Token.CHIP_START);
        add(Token.BARCODE);
        add(Token.ATTRIBUTE_END);
        repeat(3, Token.ELEMENT_END);
        add(Token.DOCUMENT_END);
        add(Token.EOF);

        assertParserAccepts();
    }

    @Test
    public void acceptsProjectWith2Printings() {

        add(Token.DOCUMENT_START);
        add(Token.PROJECT_START);
        add(Token.ATTRIBUTE_END);
        add(Token.PRINTING_START);
        add(Token.ATTRIBUTE_END);
        add(Token.CHIP_START);
        add(Token.BARCODE);
        add(Token.ATTRIBUTE_END);
        repeat(2, Token.ELEMENT_END);
        add(Token.PRINTING_START);
        add(Token.ATTRIBUTE_END);
        add(Token.CHIP_START);
        add(Token.BARCODE);
        add(Token.ATTRIBUTE_END);
        repeat(3, Token.ELEMENT_END);
        add(Token.DOCUMENT_END);
        add(Token.EOF);

        assertParserAccepts();
    }

    @Test
    public void acceptsProjectWithPattern() {
        add(Token.DOCUMENT_START);
        add(Token.PROJECT_START);
        add(Token.ATTRIBUTE_END);
        add(Token.PATTERN_START);
        add(Token.ATTRIBUTE_END);
        add(Token.REPORTER_START);
        add(Token.NAME, "reporter name");
        add(Token.SYSTEMATIC_NAME);
        add(Token.ATTRIBUTE_END);
        add(Token.FEATURE_START);
        add(Token.ATTRIBUTE_END);
        add(Token.POSITION_START);
        add(Token.UNITS, "mm");
        add(Token.X, 1.1);
        add(Token.Y, 2.2);
        add(Token.ATTRIBUTE_END);
        repeat(5, Token.ELEMENT_END);
        add(Token.DOCUMENT_END);
        add(Token.EOF);

        assertParserAccepts();
    }

    @Test
    public void acceptsProjectWith2Patterns() {
        add(Token.DOCUMENT_START);
        add(Token.PROJECT_START);
        add(Token.ATTRIBUTE_END);
        add(Token.PATTERN_START);
        add(Token.ATTRIBUTE_END);
        add(Token.REPORTER_START);
        add(Token.NAME, "reporter name");
        add(Token.SYSTEMATIC_NAME);
        add(Token.ATTRIBUTE_END);
        add(Token.FEATURE_START);
        add(Token.ATTRIBUTE_END);
        add(Token.POSITION_START);
        add(Token.UNITS, "mm");
        add(Token.X, 1.1);
        add(Token.Y, 2.2);
        add(Token.ATTRIBUTE_END);
        repeat(4, Token.ELEMENT_END);
        add(Token.PATTERN_START);
        add(Token.ATTRIBUTE_END);
        add(Token.REPORTER_START);
        add(Token.NAME, "reporter name");
        add(Token.SYSTEMATIC_NAME);
        add(Token.ATTRIBUTE_END);
        add(Token.FEATURE_START);
        add(Token.ATTRIBUTE_END);
        add(Token.POSITION_START);
        add(Token.UNITS, "mm");
        add(Token.X, 1.1);
        add(Token.Y, 2.2);
        add(Token.ATTRIBUTE_END);
        repeat(5, Token.ELEMENT_END);
        add(Token.DOCUMENT_END);
        add(Token.EOF);

        assertParserAccepts();
    }

    @Test
    public void acceptsProjectWithOther() {
        add(Token.DOCUMENT_START);
        add(Token.PROJECT_START);
        add(Token.ATTRIBUTE_END);
        add(Token.OTHER_START);
        add(Token.NAME);
        add(Token.VALUE);
        add(Token.ATTRIBUTE_END);
        repeat(2, Token.ELEMENT_END);
        add(Token.DOCUMENT_END);
        add(Token.EOF);

        assertParserAccepts();
    }

    @Test
    public void acceptsProjectWith2Others() {
        add(Token.DOCUMENT_START);
        add(Token.PROJECT_START);
        add(Token.ATTRIBUTE_END);
        add(Token.OTHER_START);
        add(Token.NAME);
        add(Token.VALUE);
        add(Token.ATTRIBUTE_END);
        add(Token.ELEMENT_END);
        add(Token.OTHER_START);
        add(Token.NAME);
        add(Token.VALUE);
        add(Token.ATTRIBUTE_END);
        repeat(2, Token.ELEMENT_END);
        add(Token.DOCUMENT_END);
        add(Token.EOF);

        assertParserAccepts();
    }

    @Test
    public void rejectsPrintingWithNoChip() {
        add(Token.DOCUMENT_START);
        add(Token.PROJECT_START);
        add(Token.ATTRIBUTE_END);
        add(Token.PRINTING_START);
        add(Token.ATTRIBUTE_END);
        repeat(2, Token.ELEMENT_END);
        add(Token.DOCUMENT_END);
        add(Token.EOF);

        assertParserRejects();
    }

    @Test
    public void acceptsPrintingWithOneChip() {
        add(Token.DOCUMENT_START);
        add(Token.PROJECT_START);
        add(Token.ATTRIBUTE_END);
        add(Token.PRINTING_START);
        add(Token.ATTRIBUTE_END);
        add(Token.CHIP_START);
        add(Token.BARCODE);
        add(Token.ATTRIBUTE_END);
        repeat(3, Token.ELEMENT_END);
        add(Token.DOCUMENT_END);
        add(Token.EOF);

        assertParserAccepts();
    }

    @Test
    public void acceptsPrintingWithTwoChips() {
        add(Token.DOCUMENT_START);
        add(Token.PROJECT_START);
        add(Token.ATTRIBUTE_END);
        add(Token.PRINTING_START);
        add(Token.ATTRIBUTE_END);
        add(Token.CHIP_START);
        add(Token.BARCODE);
        add(Token.ATTRIBUTE_END);
        add(Token.ELEMENT_END);
        add(Token.CHIP_START);
        add(Token.BARCODE);
        add(Token.ATTRIBUTE_END);
        repeat(3, Token.ELEMENT_END);
        add(Token.DOCUMENT_END);
        add(Token.EOF);

        assertParserAccepts();
    }

    @Test
    public void acceptsPrintingWithOptionalAttributes() {
        add(Token.DOCUMENT_START);
        add(Token.PROJECT_START);
        add(Token.ATTRIBUTE_END);
        add(Token.PRINTING_START);        
        add(Token.DATE);
        add(Token.PATTERN_NAME);
        add(Token.PREPARED_AT_SITE);
        add(Token.PREPARED_BY);
        add(Token.PREPARED_BY_ORG);
        add(Token.PRINTER);
        add(Token.RUN_DESCRIPTION);
        add(Token.TYPE);
        add(Token.ATTRIBUTE_END);
        add(Token.CHIP_START);
        add(Token.BARCODE);
        add(Token.ATTRIBUTE_END);
        repeat(3, Token.ELEMENT_END);
        add(Token.DOCUMENT_END);
        add(Token.EOF);

        assertParserAccepts();
    }

    @Test
    public void acceptsPrintingWithOneOther() {
        add(Token.DOCUMENT_START);
        add(Token.PROJECT_START);
        add(Token.ATTRIBUTE_END);
        add(Token.PRINTING_START);
        add(Token.ATTRIBUTE_END);
        add(Token.CHIP_START);
        add(Token.BARCODE);
        add(Token.ATTRIBUTE_END);
        add(Token.ELEMENT_END);
        add(Token.OTHER_START);
        add(Token.NAME);
        add(Token.VALUE);
        add(Token.ATTRIBUTE_END);
        repeat(3, Token.ELEMENT_END);
        add(Token.DOCUMENT_END);
        add(Token.EOF);

        assertParserAccepts();
    }

    @Test
    public void acceptsPrintingWithTwoOthers() {
        add(Token.DOCUMENT_START);
        add(Token.PROJECT_START);
        add(Token.ATTRIBUTE_END);
        add(Token.PRINTING_START);
        add(Token.ATTRIBUTE_END);
        add(Token.CHIP_START);
        add(Token.BARCODE);
        add(Token.ATTRIBUTE_END);
        add(Token.ELEMENT_END);
        add(Token.OTHER_START);
        add(Token.NAME);
        add(Token.VALUE);
        add(Token.ATTRIBUTE_END);
        add(Token.ELEMENT_END);
        add(Token.OTHER_START);
        add(Token.NAME);
        add(Token.VALUE);
        add(Token.ATTRIBUTE_END);
        repeat(3, Token.ELEMENT_END);
        add(Token.DOCUMENT_END);
        add(Token.EOF);

        assertParserAccepts();
    }

    @Test
    public void rejectsChipWithNoBarcode() {
        add(Token.DOCUMENT_START);
        add(Token.PROJECT_START);
        add(Token.ATTRIBUTE_END);
        add(Token.PRINTING_START);
        add(Token.ATTRIBUTE_END);
        add(Token.CHIP_START);
        add(Token.ATTRIBUTE_END);
        repeat(3, Token.ELEMENT_END);
        add(Token.DOCUMENT_END);
        add(Token.EOF);

        assertParserRejects();
    }

    @Test
    public void acceptsChipWithOptionalAttributes() {
        add(Token.DOCUMENT_START);
        add(Token.PROJECT_START);
        add(Token.ATTRIBUTE_END);
        add(Token.PRINTING_START);
        add(Token.ATTRIBUTE_END);
        add(Token.CHIP_START);
        add(Token.BARCODE);
        add(Token.PREPARED_FOR);
        add(Token.PREPARED_FOR_ORG);
        add(Token.ATTRIBUTE_END);
        repeat(3, Token.ELEMENT_END);
        add(Token.DOCUMENT_END);
        add(Token.EOF);

        assertParserAccepts();
    }

    @Test
    public void acceptsChipWithOneOther() {
        add(Token.DOCUMENT_START);
        add(Token.PROJECT_START);
        add(Token.ATTRIBUTE_END);
        add(Token.PRINTING_START);
        add(Token.ATTRIBUTE_END);
        add(Token.CHIP_START);
        add(Token.BARCODE);
        add(Token.ATTRIBUTE_END);
        add(Token.OTHER_START);
        add(Token.NAME);
        add(Token.VALUE);
        add(Token.ATTRIBUTE_END);
        repeat(4, Token.ELEMENT_END);
        add(Token.DOCUMENT_END);
        add(Token.EOF);

        assertParserAccepts();
    }

    @Test
    public void acceptsChipWithTwoOthers() {
        add(Token.DOCUMENT_START);
        add(Token.PROJECT_START);
        add(Token.ATTRIBUTE_END);
        add(Token.PRINTING_START);
        add(Token.ATTRIBUTE_END);
        add(Token.CHIP_START);
        add(Token.BARCODE);
        add(Token.ATTRIBUTE_END);
        add(Token.OTHER_START);
        add(Token.NAME);
        add(Token.VALUE);
        add(Token.ATTRIBUTE_END);
        add(Token.ELEMENT_END);
        add(Token.OTHER_START);
        add(Token.NAME);
        add(Token.VALUE);
        add(Token.ATTRIBUTE_END);
        repeat(4, Token.ELEMENT_END);
        add(Token.DOCUMENT_END);
        add(Token.EOF);

        assertParserAccepts();
    }

    @Test
    public void acceptsPatternWithOneReporter() {
        add(Token.DOCUMENT_START);
        add(Token.PROJECT_START);
        add(Token.ATTRIBUTE_END);
        add(Token.PATTERN_START);
        add(Token.ATTRIBUTE_END);
        add(Token.REPORTER_START);
        add(Token.NAME, "reporter name");
        add(Token.SYSTEMATIC_NAME);
        add(Token.ATTRIBUTE_END);
        add(Token.FEATURE_START);
        add(Token.ATTRIBUTE_END);
        add(Token.POSITION_START);
        add(Token.UNITS, "mm");
        add(Token.X, 1.1);
        add(Token.Y, 2.2);
        add(Token.ATTRIBUTE_END);
        repeat(5, Token.ELEMENT_END);
        add(Token.DOCUMENT_END);
        add(Token.EOF);

        assertParserAccepts();
    }

    @Test
    public void acceptsPatternWithTwoReporters() {
        add(Token.DOCUMENT_START);
        add(Token.PROJECT_START);
        add(Token.ATTRIBUTE_END);
        add(Token.PATTERN_START);
        add(Token.ATTRIBUTE_END);
        add(Token.REPORTER_START);
        add(Token.NAME, "reporter name");
        add(Token.SYSTEMATIC_NAME);
        add(Token.ATTRIBUTE_END);
        add(Token.FEATURE_START);
        add(Token.ATTRIBUTE_END);
        add(Token.POSITION_START);
        add(Token.UNITS, "mm");
        add(Token.X, 1.1);
        add(Token.Y, 2.2);
        add(Token.ATTRIBUTE_END);
        repeat(3, Token.ELEMENT_END);
        add(Token.REPORTER_START);
        add(Token.NAME, "reporter name");
        add(Token.SYSTEMATIC_NAME);
        add(Token.ATTRIBUTE_END);
        add(Token.FEATURE_START);
        add(Token.ATTRIBUTE_END);
        add(Token.POSITION_START);
        add(Token.UNITS, "mm");
        add(Token.X, 1.1);
        add(Token.Y, 2.2);
        add(Token.ATTRIBUTE_END);
        repeat(5, Token.ELEMENT_END);
        add(Token.DOCUMENT_END);
        add(Token.EOF);

        assertParserAccepts();
    }

    @Test
    public void rejectsPatternWithNoReporter() {
        add(Token.DOCUMENT_START);
        add(Token.PROJECT_START);
        add(Token.ATTRIBUTE_END);
        add(Token.PATTERN_START);
        add(Token.ATTRIBUTE_END);
        repeat(2, Token.ELEMENT_END);
        add(Token.DOCUMENT_END);
        add(Token.EOF);

        assertParserRejects();
    }

    @Test
    public void acceptsPatternWithOptionalAttributes() {
        add(Token.DOCUMENT_START);
        add(Token.PROJECT_START);
        add(Token.ATTRIBUTE_END);
        add(Token.PATTERN_START);
        add(Token.ACCESS);
        add(Token.DESCRIPTION);
        add(Token.NAME);
        add(Token.OWNER);
        add(Token.SPECIES_DATABASE);
        add(Token.TYPE);
        add(Token.TYPE_ID);
        add(Token.ATTRIBUTE_END);
        add(Token.REPORTER_START);
        add(Token.NAME, "reporter name");
        add(Token.SYSTEMATIC_NAME);
        add(Token.ATTRIBUTE_END);
        add(Token.FEATURE_START);
        add(Token.ATTRIBUTE_END);
        add(Token.POSITION_START);
        add(Token.UNITS, "mm");
        add(Token.X, 1.1);
        add(Token.Y, 2.2);
        add(Token.ATTRIBUTE_END);
        repeat(5, Token.ELEMENT_END);
        add(Token.DOCUMENT_END);
        add(Token.EOF);

        assertParserAccepts();
    }

    @Test
    public void acceptsPatternWithOneOther() {
        add(Token.DOCUMENT_START);
        add(Token.PROJECT_START);
        add(Token.ATTRIBUTE_END);
        add(Token.PATTERN_START);
        add(Token.ATTRIBUTE_END);
        add(Token.REPORTER_START);
        add(Token.NAME, "reporter name");
        add(Token.SYSTEMATIC_NAME);
        add(Token.ATTRIBUTE_END);
        add(Token.FEATURE_START);
        add(Token.ATTRIBUTE_END);
        add(Token.POSITION_START);
        add(Token.UNITS, "mm");
        add(Token.X, 1.1);
        add(Token.Y, 2.2);
        add(Token.ATTRIBUTE_END);
        repeat(3, Token.ELEMENT_END);
        add(Token.OTHER_START);
        add(Token.NAME);
        add(Token.VALUE);
        add(Token.ATTRIBUTE_END);
        repeat(3, Token.ELEMENT_END);
        add(Token.DOCUMENT_END);
        add(Token.EOF);

        assertParserAccepts();
    }

    @Test
    public void acceptsPatternWithTwoOthers() {
        add(Token.DOCUMENT_START);
        add(Token.PROJECT_START);
        add(Token.ATTRIBUTE_END);
        add(Token.PATTERN_START);
        add(Token.ATTRIBUTE_END);
        add(Token.REPORTER_START);
        add(Token.NAME, "reporter name");
        add(Token.SYSTEMATIC_NAME);
        add(Token.ATTRIBUTE_END);
        add(Token.FEATURE_START);
        add(Token.ATTRIBUTE_END);
        add(Token.POSITION_START);
        add(Token.UNITS, "mm");
        add(Token.X, 1.1);
        add(Token.Y, 2.2);
        add(Token.ATTRIBUTE_END);
        repeat(3, Token.ELEMENT_END);
        add(Token.OTHER_START);
        add(Token.NAME);
        add(Token.VALUE);
        add(Token.ATTRIBUTE_END);
        add(Token.ELEMENT_END);
        add(Token.OTHER_START);
        add(Token.NAME);
        add(Token.VALUE);
        add(Token.ATTRIBUTE_END);
        repeat(3, Token.ELEMENT_END);
        add(Token.DOCUMENT_END);
        add(Token.EOF);

        assertParserAccepts();
    }

    @Test
    public void acceptsReporterWithOneFeature() {
        add(Token.DOCUMENT_START);
        add(Token.PROJECT_START);
        add(Token.ATTRIBUTE_END);
        add(Token.PATTERN_START);
        add(Token.ATTRIBUTE_END);
        add(Token.REPORTER_START);
        add(Token.NAME, "reporter name");
        add(Token.SYSTEMATIC_NAME);
        add(Token.ATTRIBUTE_END);
        add(Token.FEATURE_START);
        add(Token.ATTRIBUTE_END);
        add(Token.POSITION_START);
        add(Token.UNITS, "mm");
        add(Token.X, 1.1);
        add(Token.Y, 2.2);
        add(Token.ATTRIBUTE_END);
        repeat(5, Token.ELEMENT_END);
        add(Token.DOCUMENT_END);
        add(Token.EOF);

        assertParserAccepts();
    }

    @Test
    public void acceptsReporterWithTwoFeatures() {
        add(Token.DOCUMENT_START);
        add(Token.PROJECT_START);
        add(Token.ATTRIBUTE_END);
        add(Token.PATTERN_START);
        add(Token.ATTRIBUTE_END);
        add(Token.REPORTER_START);
        add(Token.NAME, "reporter name");
        add(Token.SYSTEMATIC_NAME);
        add(Token.ATTRIBUTE_END);
        add(Token.FEATURE_START);
        add(Token.ATTRIBUTE_END);
        add(Token.POSITION_START);
        add(Token.UNITS, "mm");
        add(Token.X, 1.1);
        add(Token.Y, 2.2);
        add(Token.ATTRIBUTE_END);
        repeat(2, Token.ELEMENT_END);
        add(Token.FEATURE_START);
        add(Token.ATTRIBUTE_END);
        add(Token.POSITION_START);
        add(Token.UNITS, "mm");
        add(Token.X, 1.1);
        add(Token.Y, 2.2);
        add(Token.ATTRIBUTE_END);
        repeat(5, Token.ELEMENT_END);
        add(Token.DOCUMENT_END);
        add(Token.EOF);

        assertParserAccepts();
    }

    @Test
    public void rejectsReporterWithNoFeature() {
        add(Token.DOCUMENT_START);
        add(Token.PROJECT_START);
        add(Token.ATTRIBUTE_END);
        add(Token.PATTERN_START);
        add(Token.ATTRIBUTE_END);
        add(Token.REPORTER_START);
        add(Token.NAME, "reporter name");
        add(Token.SYSTEMATIC_NAME);
        add(Token.ATTRIBUTE_END);
        repeat(3, Token.ELEMENT_END);
        add(Token.DOCUMENT_END);
        add(Token.EOF);

        assertParserRejects();
    }

    @Test
    public void rejectsReporterWithNoName() {
        add(Token.DOCUMENT_START);
        add(Token.PROJECT_START);
        add(Token.ATTRIBUTE_END);
        add(Token.PATTERN_START);
        add(Token.ATTRIBUTE_END);
        add(Token.REPORTER_START);
        add(Token.ATTRIBUTE_END);
        add(Token.FEATURE_START);
        add(Token.ATTRIBUTE_END);
        add(Token.POSITION_START);
        add(Token.UNITS, "mm");
        add(Token.X, 1.1);
        add(Token.Y, 2.2);
        add(Token.ATTRIBUTE_END);
        repeat(5, Token.ELEMENT_END);
        add(Token.DOCUMENT_END);
        add(Token.EOF);

        assertParserRejects();
    }

    @Test
    public void acceptsReporterWithOptionalAttributes() {
        add(Token.DOCUMENT_START);
        add(Token.PROJECT_START);
        add(Token.ATTRIBUTE_END);
        add(Token.PATTERN_START);
        add(Token.ATTRIBUTE_END);
        add(Token.REPORTER_START);
        add(Token.ACCESSION);
        add(Token.ACTIVE_SEQUENCE);
        add(Token.CONTROL_TYPE);
        add(Token.DELETION);
        add(Token.DESCRIPTION);
        add(Token.FAIL_TYPE);
        add(Token.LINKER_SEQUENCE);
        add(Token.MISMATCH_COUNT);
        add(Token.NAME, "reporter name");
        add(Token.PRIMER1_SEQUENCE);
        add(Token.PRIMER2_SEQUENCE);
        add(Token.START_COORD);
        add(Token.SYSTEMATIC_NAME);
        add(Token.ATTRIBUTE_END);
        add(Token.FEATURE_START);
        add(Token.ATTRIBUTE_END);
        add(Token.POSITION_START);
        add(Token.UNITS, "mm");
        add(Token.X, 1.1);
        add(Token.Y, 2.2);
        add(Token.ATTRIBUTE_END);
        repeat(5, Token.ELEMENT_END);
        add(Token.DOCUMENT_END);
        add(Token.EOF);

        assertParserAccepts();
    }

    @Test
    public void acceptsReporterWithOneOther() {
        add(Token.DOCUMENT_START);
        add(Token.PROJECT_START);
        add(Token.ATTRIBUTE_END);
        add(Token.PATTERN_START);
        add(Token.ATTRIBUTE_END);
        add(Token.REPORTER_START);
        add(Token.NAME, "reporter name");
        add(Token.SYSTEMATIC_NAME);
        add(Token.ATTRIBUTE_END);
        add(Token.FEATURE_START);
        add(Token.ATTRIBUTE_END);
        add(Token.POSITION_START);
        add(Token.UNITS, "mm");
        add(Token.X, 1.1);
        add(Token.Y, 2.2);
        add(Token.ATTRIBUTE_END);
        repeat(2, Token.ELEMENT_END);
        add(Token.OTHER_START);
        add(Token.NAME);
        add(Token.VALUE);
        add(Token.ATTRIBUTE_END);
        repeat(4, Token.ELEMENT_END);
        add(Token.DOCUMENT_END);
        add(Token.EOF);

        assertParserAccepts();
    }

    @Test
    public void acceptsReporterWithTwoOthers() {
        add(Token.DOCUMENT_START);
        add(Token.PROJECT_START);
        add(Token.ATTRIBUTE_END);
        add(Token.PATTERN_START);
        add(Token.ATTRIBUTE_END);
        add(Token.REPORTER_START);
        add(Token.NAME, "reporter name");
        add(Token.SYSTEMATIC_NAME);
        add(Token.ATTRIBUTE_END);
        add(Token.FEATURE_START);
        add(Token.ATTRIBUTE_END);
        add(Token.POSITION_START);
        add(Token.UNITS, "mm");
        add(Token.X, 1.1);
        add(Token.Y, 2.2);
        add(Token.ATTRIBUTE_END);
        repeat(2, Token.ELEMENT_END);
        add(Token.OTHER_START);
        add(Token.NAME);
        add(Token.VALUE);
        add(Token.ATTRIBUTE_END);
        add(Token.ELEMENT_END);
        add(Token.OTHER_START);
        add(Token.NAME);
        add(Token.VALUE);
        add(Token.ATTRIBUTE_END);
        repeat(4, Token.ELEMENT_END);
        add(Token.DOCUMENT_END);
        add(Token.EOF);

        assertParserAccepts();
    }

    @Test
    public void acceptsReporterWithOneGene() {
        add(Token.DOCUMENT_START);
        add(Token.PROJECT_START);
        add(Token.ATTRIBUTE_END);
        add(Token.PATTERN_START);
        add(Token.ATTRIBUTE_END);
        add(Token.REPORTER_START);
        add(Token.NAME, "reporter name");
        add(Token.SYSTEMATIC_NAME);
        add(Token.ATTRIBUTE_END);
        add(Token.FEATURE_START);
        add(Token.ATTRIBUTE_END);
        add(Token.POSITION_START);
        add(Token.UNITS, "mm");
        add(Token.X, 1.1);
        add(Token.Y, 2.2);
        add(Token.ATTRIBUTE_END);
        repeat(2, Token.ELEMENT_END);
        add(Token.GENE_START);
        add(Token.PRIMARY_NAME);
        add(Token.ATTRIBUTE_END);
        repeat(4, Token.ELEMENT_END);
        add(Token.DOCUMENT_END);
        add(Token.EOF);

        assertParserAccepts();
    }

    @Test
    public void rejectsReporterWithTwoGenes() {
        add(Token.DOCUMENT_START);
        add(Token.PROJECT_START);
        add(Token.ATTRIBUTE_END);
        add(Token.PATTERN_START);
        add(Token.ATTRIBUTE_END);
        add(Token.REPORTER_START);
        add(Token.NAME, "reporter name");
        add(Token.SYSTEMATIC_NAME);
        add(Token.ATTRIBUTE_END);
        add(Token.FEATURE_START);
        add(Token.ATTRIBUTE_END);
        add(Token.ELEMENT_END);
        add(Token.GENE_START);
        add(Token.PRIMARY_NAME);
        add(Token.ATTRIBUTE_END);
        add(Token.ELEMENT_END);
        add(Token.GENE_START);
        add(Token.PRIMARY_NAME);
        add(Token.ATTRIBUTE_END);
        repeat(4, Token.ELEMENT_END);
        add(Token.DOCUMENT_END);
        add(Token.EOF);

        assertParserRejects();
    }

    @Test
    public void acceptsFeatureWithOptionalAttributes() {
        add(Token.DOCUMENT_START);
        add(Token.PROJECT_START);
        add(Token.ATTRIBUTE_END);
        add(Token.PATTERN_START);
        add(Token.ATTRIBUTE_END);
        add(Token.REPORTER_START);
        add(Token.NAME, "reporter name");
        add(Token.SYSTEMATIC_NAME);
        add(Token.ATTRIBUTE_END);
        add(Token.FEATURE_START);
        add(Token.CTRL_FOR_FEAT_NUM);
        add(Token.NUMBER, 123456);
        add(Token.ATTRIBUTE_END);
        add(Token.POSITION_START);
        add(Token.UNITS, "mm");
        add(Token.X, 1.1);
        add(Token.Y, 2.2);
        add(Token.ATTRIBUTE_END);
        repeat(5, Token.ELEMENT_END);
        add(Token.DOCUMENT_END);
        add(Token.EOF);

        assertParserAccepts();
    }

    @Test
    public void acceptsFeatureWithOneOther() {
        add(Token.DOCUMENT_START);
        add(Token.PROJECT_START);
        add(Token.ATTRIBUTE_END);
        add(Token.PATTERN_START);
        add(Token.ATTRIBUTE_END);
        add(Token.REPORTER_START);
        add(Token.NAME, "reporter name");
        add(Token.SYSTEMATIC_NAME);
        add(Token.ATTRIBUTE_END);
        add(Token.FEATURE_START);
        add(Token.ATTRIBUTE_END);
        add(Token.POSITION_START);
        add(Token.UNITS, "mm");
        add(Token.X, 1.1);
        add(Token.Y, 2.2);
        add(Token.ATTRIBUTE_END);
        add(Token.ELEMENT_END);
        add(Token.OTHER_START);
        add(Token.NAME);
        add(Token.VALUE);
        add(Token.ATTRIBUTE_END);
        repeat(5, Token.ELEMENT_END);
        add(Token.DOCUMENT_END);
        add(Token.EOF);

        assertParserAccepts();
    }

    @Test
    public void acceptsFeatureWithTwoOthers() {
        add(Token.DOCUMENT_START);
        add(Token.PROJECT_START);
        add(Token.ATTRIBUTE_END);
        add(Token.PATTERN_START);
        add(Token.ATTRIBUTE_END);
        add(Token.REPORTER_START);
        add(Token.NAME, "reporter name");
        add(Token.SYSTEMATIC_NAME);
        add(Token.ATTRIBUTE_END);
        add(Token.FEATURE_START);
        add(Token.ATTRIBUTE_END);
        add(Token.POSITION_START);
        add(Token.UNITS, "mm");
        add(Token.X, 1.1);
        add(Token.Y, 2.2);
        add(Token.ATTRIBUTE_END);
        add(Token.ELEMENT_END);
        add(Token.OTHER_START);
        add(Token.NAME);
        add(Token.VALUE);
        add(Token.ATTRIBUTE_END);
        add(Token.ELEMENT_END);
        add(Token.OTHER_START);
        add(Token.NAME);
        add(Token.VALUE);
        add(Token.ATTRIBUTE_END);
        repeat(5, Token.ELEMENT_END);
        add(Token.DOCUMENT_END);
        add(Token.EOF);

        assertParserAccepts();
    }

    @Test
    public void acceptsFeatureWithOnePen() {
        add(Token.DOCUMENT_START);
        add(Token.PROJECT_START);
        add(Token.ATTRIBUTE_END);
        add(Token.PATTERN_START);
        add(Token.ATTRIBUTE_END);
        add(Token.REPORTER_START);
        add(Token.NAME, "reporter name");
        add(Token.SYSTEMATIC_NAME);
        add(Token.ATTRIBUTE_END);
        add(Token.FEATURE_START);
        add(Token.ATTRIBUTE_END);
        add(Token.POSITION_START);
        add(Token.UNITS, "mm");
        add(Token.X, 1.1);
        add(Token.Y, 2.2);
        add(Token.ATTRIBUTE_END);
        add(Token.ELEMENT_END);
        add(Token.PEN_START);
        add(Token.UNITS, "mm");
        add(Token.X, 1.1);
        add(Token.Y, 2.2);
        add(Token.ATTRIBUTE_END);
        repeat(5, Token.ELEMENT_END);
        add(Token.DOCUMENT_END);
        add(Token.EOF);

        assertParserAccepts();
    }

    @Test
    public void rejectsFeatureWithTwoPens() {
        add(Token.DOCUMENT_START);
        add(Token.PROJECT_START);
        add(Token.ATTRIBUTE_END);
        add(Token.PATTERN_START);
        add(Token.ATTRIBUTE_END);
        add(Token.REPORTER_START);
        add(Token.NAME, "reporter name");
        add(Token.SYSTEMATIC_NAME);
        add(Token.ATTRIBUTE_END);
        add(Token.FEATURE_START);
        add(Token.ATTRIBUTE_END);
        add(Token.PEN_START);
        add(Token.UNITS, "mm");
        add(Token.X, 1.1);
        add(Token.Y, 2.2);
        add(Token.ATTRIBUTE_END);
        add(Token.ELEMENT_END);
        add(Token.PEN_START);
        add(Token.UNITS, "mm");
        add(Token.X, 1.1);
        add(Token.Y, 2.2);
        add(Token.ATTRIBUTE_END);
        repeat(5, Token.ELEMENT_END);
        add(Token.DOCUMENT_END);
        add(Token.EOF);

        assertParserRejects();
    }

    @Test
    public void rejectsFeatureWithNoPosition() {
        add(Token.DOCUMENT_START);
        add(Token.PROJECT_START);
        add(Token.ATTRIBUTE_END);
        add(Token.PATTERN_START);
        add(Token.ATTRIBUTE_END);
        add(Token.REPORTER_START);
        add(Token.NAME, "reporter name");
        add(Token.SYSTEMATIC_NAME);
        add(Token.ATTRIBUTE_END);
        add(Token.FEATURE_START);
        add(Token.ATTRIBUTE_END);
        repeat(4, Token.ELEMENT_END);
        add(Token.DOCUMENT_END);
        add(Token.EOF);

        assertParserRejects();
    }

    @Test
    public void rejectsFeatureWithTwoPositions() {
        add(Token.DOCUMENT_START);
        add(Token.PROJECT_START);
        add(Token.ATTRIBUTE_END);
        add(Token.PATTERN_START);
        add(Token.ATTRIBUTE_END);
        add(Token.REPORTER_START);
        add(Token.NAME, "reporter name");
        add(Token.SYSTEMATIC_NAME);
        add(Token.ATTRIBUTE_END);
        add(Token.FEATURE_START);
        add(Token.ATTRIBUTE_END);
        add(Token.POSITION_START);
        add(Token.UNITS, "mm");
        add(Token.X, 1.1);
        add(Token.Y, 2.2);
        add(Token.ATTRIBUTE_END);
        add(Token.ELEMENT_END);
        add(Token.POSITION_START);
        add(Token.UNITS, "mm");
        add(Token.X, 1.1);
        add(Token.Y, 2.2);
        add(Token.ATTRIBUTE_END);
        repeat(5, Token.ELEMENT_END);
        add(Token.DOCUMENT_END);
        add(Token.EOF);

        assertParserRejects();
    }

    @Test
    public void rejectsPenWithNoUnits() {
        add(Token.DOCUMENT_START);
        add(Token.PROJECT_START);
        add(Token.ATTRIBUTE_END);
        add(Token.PATTERN_START);
        add(Token.ATTRIBUTE_END);
        add(Token.REPORTER_START);
        add(Token.NAME, "reporter name");
        add(Token.SYSTEMATIC_NAME);
        add(Token.ATTRIBUTE_END);
        add(Token.FEATURE_START);
        add(Token.ATTRIBUTE_END);
        add(Token.POSITION_START);
        add(Token.UNITS, "mm");
        add(Token.X, 1.1);
        add(Token.Y, 2.2);
        add(Token.ATTRIBUTE_END);
        add(Token.ELEMENT_END);
        add(Token.PEN_START);
        add(Token.X, 1.1);
        add(Token.Y, 2.2);
        add(Token.ATTRIBUTE_END);
        repeat(5, Token.ELEMENT_END);
        add(Token.DOCUMENT_END);
        add(Token.EOF);

        assertParserRejects();
    }

    @Test
    public void rejectsPenWithNoX() {
        add(Token.DOCUMENT_START);
        add(Token.PROJECT_START);
        add(Token.ATTRIBUTE_END);
        add(Token.PATTERN_START);
        add(Token.ATTRIBUTE_END);
        add(Token.REPORTER_START);
        add(Token.NAME, "reporter name");
        add(Token.SYSTEMATIC_NAME);
        add(Token.ATTRIBUTE_END);
        add(Token.FEATURE_START);
        add(Token.ATTRIBUTE_END);
        add(Token.POSITION_START);
        add(Token.UNITS, "mm");
        add(Token.X, 1.1);
        add(Token.Y, 2.2);
        add(Token.ATTRIBUTE_END);
        add(Token.ELEMENT_END);
        add(Token.PEN_START);
        add(Token.UNITS, "mm");
        add(Token.ATTRIBUTE_END);
        repeat(5, Token.ELEMENT_END);
        add(Token.DOCUMENT_END);
        add(Token.EOF);

        assertParserRejects();
    }

    @Test
    public void rejectsPenWithNoY() {
        add(Token.DOCUMENT_START);
        add(Token.PROJECT_START);
        add(Token.ATTRIBUTE_END);
        add(Token.PATTERN_START);
        add(Token.ATTRIBUTE_END);
        add(Token.REPORTER_START);
        add(Token.NAME, "reporter name");
        add(Token.SYSTEMATIC_NAME);
        add(Token.ATTRIBUTE_END);
        add(Token.FEATURE_START);
        add(Token.ATTRIBUTE_END);
        add(Token.POSITION_START);
        add(Token.UNITS, "mm");
        add(Token.X, 1.1);
        add(Token.Y, 2.2);
        add(Token.ATTRIBUTE_END);
        add(Token.ELEMENT_END);
        add(Token.PEN_START);
        add(Token.UNITS, "mm");
        add(Token.X, 1.1);
        add(Token.ATTRIBUTE_END);
        repeat(5, Token.ELEMENT_END);
        add(Token.DOCUMENT_END);
        add(Token.EOF);

        assertParserRejects();
    }

    @Test
    public void acceptsPenWithOneOther() {
        add(Token.DOCUMENT_START);
        add(Token.PROJECT_START);
        add(Token.ATTRIBUTE_END);
        add(Token.PATTERN_START);
        add(Token.ATTRIBUTE_END);
        add(Token.REPORTER_START);
        add(Token.NAME, "reporter name");
        add(Token.SYSTEMATIC_NAME);
        add(Token.ATTRIBUTE_END);
        add(Token.FEATURE_START);
        add(Token.ATTRIBUTE_END);
        add(Token.POSITION_START);
        add(Token.UNITS, "mm");
        add(Token.X, 1.1);
        add(Token.Y, 2.2);
        add(Token.ATTRIBUTE_END);
        add(Token.ELEMENT_END);
        add(Token.PEN_START);
        add(Token.UNITS, "mm");
        add(Token.X, 1.1);
        add(Token.Y, 2.2);
        add(Token.ATTRIBUTE_END);
        add(Token.OTHER_START);
        add(Token.NAME);
        add(Token.VALUE);
        add(Token.ATTRIBUTE_END);
        repeat(6, Token.ELEMENT_END);
        add(Token.DOCUMENT_END);
        add(Token.EOF);

        assertParserAccepts();
    }

    @Test
    public void acceptsPenWithTwoOthers() {
        add(Token.DOCUMENT_START);
        add(Token.PROJECT_START);
        add(Token.ATTRIBUTE_END);
        add(Token.PATTERN_START);
        add(Token.ATTRIBUTE_END);
        add(Token.REPORTER_START);
        add(Token.NAME, "reporter name");
        add(Token.SYSTEMATIC_NAME);
        add(Token.ATTRIBUTE_END);
        add(Token.FEATURE_START);
        add(Token.ATTRIBUTE_END);
        add(Token.POSITION_START);
        add(Token.UNITS, "mm");
        add(Token.X, 1.1);
        add(Token.Y, 2.2);
        add(Token.ATTRIBUTE_END);
        add(Token.ELEMENT_END);
        add(Token.PEN_START);
        add(Token.UNITS, "mm");
        add(Token.X, 1.1);
        add(Token.Y, 2.2);
        add(Token.ATTRIBUTE_END);
        add(Token.OTHER_START);
        add(Token.NAME);
        add(Token.VALUE);
        add(Token.ATTRIBUTE_END);
        add(Token.ELEMENT_END);
        add(Token.OTHER_START);
        add(Token.NAME);
        add(Token.VALUE);
        add(Token.ATTRIBUTE_END);
        repeat(6, Token.ELEMENT_END);
        add(Token.DOCUMENT_END);
        add(Token.EOF);

        assertParserAccepts();
    }

    @Test
    public void rejectsPositionWithNoUnits() {
        add(Token.DOCUMENT_START);
        add(Token.PROJECT_START);
        add(Token.ATTRIBUTE_END);
        add(Token.PATTERN_START);
        add(Token.ATTRIBUTE_END);
        add(Token.REPORTER_START);
        add(Token.NAME, "reporter name");
        add(Token.SYSTEMATIC_NAME);
        add(Token.ATTRIBUTE_END);
        add(Token.FEATURE_START);
        add(Token.ATTRIBUTE_END);
        add(Token.POSITION_START);
        add(Token.X, 1.1);
        add(Token.Y, 2.2);
        add(Token.ATTRIBUTE_END);
        repeat(5, Token.ELEMENT_END);
        add(Token.DOCUMENT_END);
        add(Token.EOF);

        assertParserRejects();
    }

    @Test
    public void rejectsPositionWithNoX() {
        add(Token.DOCUMENT_START);
        add(Token.PROJECT_START);
        add(Token.ATTRIBUTE_END);
        add(Token.PATTERN_START);
        add(Token.ATTRIBUTE_END);
        add(Token.REPORTER_START);
        add(Token.NAME, "reporter name");
        add(Token.SYSTEMATIC_NAME);
        add(Token.ATTRIBUTE_END);
        add(Token.FEATURE_START);
        add(Token.ATTRIBUTE_END);
        add(Token.POSITION_START);
        add(Token.UNITS, "mm");
        add(Token.Y, 2.2);
        add(Token.ATTRIBUTE_END);
        repeat(5, Token.ELEMENT_END);
        add(Token.DOCUMENT_END);
        add(Token.EOF);

        assertParserRejects();
    }

    @Test
    public void rejectsPositionWithNoY() {
        add(Token.DOCUMENT_START);
        add(Token.PROJECT_START);
        add(Token.ATTRIBUTE_END);
        add(Token.PATTERN_START);
        add(Token.ATTRIBUTE_END);
        add(Token.REPORTER_START);
        add(Token.NAME, "reporter name");
        add(Token.SYSTEMATIC_NAME);
        add(Token.ATTRIBUTE_END);
        add(Token.FEATURE_START);
        add(Token.ATTRIBUTE_END);
        add(Token.POSITION_START);
        add(Token.UNITS, "mm");
        add(Token.X, 1.1);
        add(Token.ATTRIBUTE_END);
        repeat(5, Token.ELEMENT_END);
        add(Token.DOCUMENT_END);
        add(Token.EOF);

        assertParserRejects();
    }

    @Test
    public void acceptsPositionWithOneOther() {
        add(Token.DOCUMENT_START);
        add(Token.PROJECT_START);
        add(Token.ATTRIBUTE_END);
        add(Token.PATTERN_START);
        add(Token.ATTRIBUTE_END);
        add(Token.REPORTER_START);
        add(Token.NAME, "reporter name");
        add(Token.SYSTEMATIC_NAME);
        add(Token.ATTRIBUTE_END);
        add(Token.FEATURE_START);
        add(Token.ATTRIBUTE_END);
        add(Token.POSITION_START);
        add(Token.UNITS, "mm");
        add(Token.X, 1.1);
        add(Token.Y, 2.2);
        add(Token.ATTRIBUTE_END);
        add(Token.OTHER_START);
        add(Token.NAME);
        add(Token.VALUE);
        add(Token.ATTRIBUTE_END);
        repeat(2, Token.ELEMENT_END);
        add(Token.PEN_START);
        add(Token.UNITS, "mm");
        add(Token.X, 1.1);
        add(Token.Y, 2.2);
        add(Token.ATTRIBUTE_END);
        repeat(5, Token.ELEMENT_END);
        add(Token.DOCUMENT_END);
        add(Token.EOF);

        assertParserAccepts();
    }

    @Test
    public void acceptsPositionWithTwoOthers() {
        add(Token.DOCUMENT_START);
        add(Token.PROJECT_START);
        add(Token.ATTRIBUTE_END);
        add(Token.PATTERN_START);
        add(Token.ATTRIBUTE_END);
        add(Token.REPORTER_START);
        add(Token.NAME, "reporter name");
        add(Token.SYSTEMATIC_NAME);
        add(Token.ATTRIBUTE_END);
        add(Token.FEATURE_START);
        add(Token.ATTRIBUTE_END);
        add(Token.POSITION_START);
        add(Token.UNITS, "mm");
        add(Token.X, 1.1);
        add(Token.Y, 2.2);
        add(Token.ATTRIBUTE_END);
        add(Token.OTHER_START);
        add(Token.NAME);
        add(Token.VALUE);
        add(Token.ATTRIBUTE_END);
        add(Token.ELEMENT_END);
        add(Token.OTHER_START);
        add(Token.NAME);
        add(Token.VALUE);
        add(Token.ATTRIBUTE_END);
        repeat(2, Token.ELEMENT_END);
        add(Token.PEN_START);
        add(Token.UNITS, "mm");
        add(Token.X, 1.1);
        add(Token.Y, 2.2);
        add(Token.ATTRIBUTE_END);
        repeat(5, Token.ELEMENT_END);
        add(Token.DOCUMENT_END);
        add(Token.EOF);

        assertParserAccepts();
    }

    @Test
    public void acceptsGeneWithOneAccession() {
        add(Token.DOCUMENT_START);
        add(Token.PROJECT_START);
        add(Token.ATTRIBUTE_END);
        add(Token.PATTERN_START);
        add(Token.ATTRIBUTE_END);
        add(Token.REPORTER_START);
        add(Token.NAME, "reporter name");
        add(Token.SYSTEMATIC_NAME);
        add(Token.ATTRIBUTE_END);
        add(Token.FEATURE_START);
        add(Token.ATTRIBUTE_END);
        add(Token.POSITION_START);
        add(Token.UNITS, "mm");
        add(Token.X, 1.1);
        add(Token.Y, 2.2);
        add(Token.ATTRIBUTE_END);
        repeat(2, Token.ELEMENT_END);
        add(Token.GENE_START);
        add(Token.PRIMARY_NAME);
        add(Token.ATTRIBUTE_END);
        add(Token.ACCESSION_START);
        add(Token.DATABASE);
        add(Token.ID);
        add(Token.ATTRIBUTE_END);
        repeat(5, Token.ELEMENT_END);
        add(Token.DOCUMENT_END);
        add(Token.EOF);

        assertParserAccepts();
    }

    @Test
    public void acceptsGeneWithTwoAccessions() {
        add(Token.DOCUMENT_START);
        add(Token.PROJECT_START);
        add(Token.ATTRIBUTE_END);
        add(Token.PATTERN_START);
        add(Token.ATTRIBUTE_END);
        add(Token.REPORTER_START);
        add(Token.NAME, "reporter name");
        add(Token.SYSTEMATIC_NAME);
        add(Token.ATTRIBUTE_END);
        add(Token.FEATURE_START);
        add(Token.ATTRIBUTE_END);
        add(Token.POSITION_START);
        add(Token.UNITS, "mm");
        add(Token.X, 1.1);
        add(Token.Y, 2.2);
        add(Token.ATTRIBUTE_END);
        repeat(2, Token.ELEMENT_END);
        add(Token.GENE_START);
        add(Token.PRIMARY_NAME);
        add(Token.ATTRIBUTE_END);
        add(Token.ACCESSION_START);
        add(Token.DATABASE);
        add(Token.ID);
        add(Token.ATTRIBUTE_END);
        add(Token.ELEMENT_END);
        add(Token.ACCESSION_START);
        add(Token.DATABASE);
        add(Token.ID);
        add(Token.ATTRIBUTE_END);
        repeat(5, Token.ELEMENT_END);
        add(Token.DOCUMENT_END);
        add(Token.EOF);

        assertParserAccepts();
    }

    @Test
    public void acceptsGeneWithOneAlias() {
        add(Token.DOCUMENT_START);
        add(Token.PROJECT_START);
        add(Token.ATTRIBUTE_END);
        add(Token.PATTERN_START);
        add(Token.ATTRIBUTE_END);
        add(Token.REPORTER_START);
        add(Token.NAME, "reporter name");
        add(Token.SYSTEMATIC_NAME);
        add(Token.ATTRIBUTE_END);
        add(Token.FEATURE_START);
        add(Token.ATTRIBUTE_END);
        add(Token.POSITION_START);
        add(Token.UNITS, "mm");
        add(Token.X, 1.1);
        add(Token.Y, 2.2);
        add(Token.ATTRIBUTE_END);
        repeat(2, Token.ELEMENT_END);
        add(Token.GENE_START);
        add(Token.PRIMARY_NAME);
        add(Token.ATTRIBUTE_END);
        add(Token.ALIAS_START);
        add(Token.NAME);
        add(Token.ATTRIBUTE_END);
        repeat(5, Token.ELEMENT_END);
        add(Token.DOCUMENT_END);
        add(Token.EOF);

        assertParserAccepts();
    }

    @Test
    public void acceptsGeneWithTwoAliases() {
        add(Token.DOCUMENT_START);
        add(Token.PROJECT_START);
        add(Token.ATTRIBUTE_END);
        add(Token.PATTERN_START);
        add(Token.ATTRIBUTE_END);
        add(Token.REPORTER_START);
        add(Token.NAME, "reporter name");
        add(Token.SYSTEMATIC_NAME);
        add(Token.ATTRIBUTE_END);
        add(Token.FEATURE_START);
        add(Token.ATTRIBUTE_END);
        add(Token.POSITION_START);
        add(Token.UNITS, "mm");
        add(Token.X, 1.1);
        add(Token.Y, 2.2);
        add(Token.ATTRIBUTE_END);
        repeat(2, Token.ELEMENT_END);
        add(Token.GENE_START);
        add(Token.PRIMARY_NAME);
        add(Token.ATTRIBUTE_END);
        add(Token.ALIAS_START);
        add(Token.NAME);
        add(Token.ATTRIBUTE_END);
        add(Token.ELEMENT_END);
        add(Token.ALIAS_START);
        add(Token.NAME);
        add(Token.ATTRIBUTE_END);
        repeat(5, Token.ELEMENT_END);
        add(Token.DOCUMENT_END);
        add(Token.EOF);

        assertParserAccepts();
    }

    @Test
    public void rejectsGeneWithNoPrimaryName() {
        add(Token.DOCUMENT_START);
        add(Token.PROJECT_START);
        add(Token.ATTRIBUTE_END);
        add(Token.PATTERN_START);
        add(Token.ATTRIBUTE_END);
        add(Token.REPORTER_START);
        add(Token.NAME, "reporter name");
        add(Token.SYSTEMATIC_NAME);
        add(Token.ATTRIBUTE_END);
        add(Token.FEATURE_START);
        add(Token.ATTRIBUTE_END);
        add(Token.POSITION_START);
        add(Token.UNITS, "mm");
        add(Token.X, 1.1);
        add(Token.Y, 2.2);
        add(Token.ATTRIBUTE_END);
        repeat(2, Token.ELEMENT_END);
        add(Token.GENE_START);
        add(Token.ATTRIBUTE_END);
        repeat(4, Token.ELEMENT_END);
        add(Token.DOCUMENT_END);
        add(Token.EOF);

        assertParserRejects();
    }

    @Test
    public void acceptsGeneWithOptionalAttributes() {
        add(Token.DOCUMENT_START);
        add(Token.PROJECT_START);
        add(Token.ATTRIBUTE_END);
        add(Token.PATTERN_START);
        add(Token.ATTRIBUTE_END);
        add(Token.REPORTER_START);
        add(Token.NAME, "reporter name");
        add(Token.SYSTEMATIC_NAME);
        add(Token.ATTRIBUTE_END);
        add(Token.FEATURE_START);
        add(Token.ATTRIBUTE_END);
        add(Token.POSITION_START);
        add(Token.UNITS, "mm");
        add(Token.X, 1.1);
        add(Token.Y, 2.2);
        add(Token.ATTRIBUTE_END);
        repeat(2, Token.ELEMENT_END);
        add(Token.GENE_START);
        add(Token.CHROMOSOME);
        add(Token.DESCRIPTION);
        add(Token.MAP_POSITION);
        add(Token.PRIMARY_NAME);
        add(Token.SPECIES);
        add(Token.SYSTEMATIC_NAME);
        add(Token.ATTRIBUTE_END);
        repeat(4, Token.ELEMENT_END);
        add(Token.DOCUMENT_END);
        add(Token.EOF);

        assertParserAccepts();
    }

    @Test
    public void acceptsGeneWithOneOther() {
        add(Token.DOCUMENT_START);
        add(Token.PROJECT_START);
        add(Token.ATTRIBUTE_END);
        add(Token.PATTERN_START);
        add(Token.ATTRIBUTE_END);
        add(Token.REPORTER_START);
        add(Token.NAME, "reporter name");
        add(Token.SYSTEMATIC_NAME);
        add(Token.ATTRIBUTE_END);
        add(Token.FEATURE_START);
        add(Token.ATTRIBUTE_END);
        add(Token.POSITION_START);
        add(Token.UNITS, "mm");
        add(Token.X, 1.1);
        add(Token.Y, 2.2);
        add(Token.ATTRIBUTE_END);
        repeat(2, Token.ELEMENT_END);
        add(Token.GENE_START);
        add(Token.PRIMARY_NAME);
        add(Token.ATTRIBUTE_END);
        add(Token.OTHER_START);
        add(Token.NAME);
        add(Token.VALUE);
        add(Token.ATTRIBUTE_END);
        repeat(5, Token.ELEMENT_END);
        add(Token.DOCUMENT_END);
        add(Token.EOF);

        assertParserAccepts();
    }

    @Test
    public void acceptsGeneWithTwoOthers() {
        add(Token.DOCUMENT_START);
        add(Token.PROJECT_START);
        add(Token.ATTRIBUTE_END);
        add(Token.PATTERN_START);
        add(Token.ATTRIBUTE_END);
        add(Token.REPORTER_START);
        add(Token.NAME, "reporter name");
        add(Token.SYSTEMATIC_NAME);
        add(Token.ATTRIBUTE_END);
        add(Token.FEATURE_START);
        add(Token.ATTRIBUTE_END);
        add(Token.POSITION_START);
        add(Token.UNITS, "mm");
        add(Token.X, 1.1);
        add(Token.Y, 2.2);
        add(Token.ATTRIBUTE_END);
        repeat(2, Token.ELEMENT_END);
        add(Token.GENE_START);
        add(Token.PRIMARY_NAME);
        add(Token.ATTRIBUTE_END);
        add(Token.OTHER_START);
        add(Token.NAME);
        add(Token.VALUE);
        add(Token.ATTRIBUTE_END);
        add(Token.ELEMENT_END);
        add(Token.OTHER_START);
        add(Token.NAME);
        add(Token.VALUE);
        add(Token.ATTRIBUTE_END);
        repeat(5, Token.ELEMENT_END);
        add(Token.DOCUMENT_END);
        add(Token.EOF);

        assertParserAccepts();
    }

    @Test
    public void rejectsAccessionNoDatabase() {
        add(Token.DOCUMENT_START);
        add(Token.PROJECT_START);
        add(Token.ATTRIBUTE_END);
        add(Token.PATTERN_START);
        add(Token.ATTRIBUTE_END);
        add(Token.REPORTER_START);
        add(Token.NAME, "reporter name");
        add(Token.SYSTEMATIC_NAME);
        add(Token.ATTRIBUTE_END);
        add(Token.FEATURE_START);
        add(Token.ATTRIBUTE_END);
        add(Token.POSITION_START);
        add(Token.UNITS, "mm");
        add(Token.X, 1.1);
        add(Token.Y, 2.2);
        add(Token.ATTRIBUTE_END);
        repeat(2, Token.ELEMENT_END);
        add(Token.GENE_START);
        add(Token.PRIMARY_NAME);
        add(Token.ACCESSION_START);
        add(Token.ID);
        add(Token.ATTRIBUTE_END);
        repeat(5, Token.ELEMENT_END);
        add(Token.DOCUMENT_END);
        add(Token.EOF);

        assertParserRejects();
    }

    @Test
    public void rejectsAccessionNoIdOrIdentifier() {
        add(Token.DOCUMENT_START);
        add(Token.PROJECT_START);
        add(Token.ATTRIBUTE_END);
        add(Token.PATTERN_START);
        add(Token.ATTRIBUTE_END);
        add(Token.REPORTER_START);
        add(Token.NAME, "reporter name");
        add(Token.SYSTEMATIC_NAME);
        add(Token.ATTRIBUTE_END);
        add(Token.FEATURE_START);
        add(Token.ATTRIBUTE_END);
        add(Token.POSITION_START);
        add(Token.UNITS, "mm");
        add(Token.X, 1.1);
        add(Token.Y, 2.2);
        add(Token.ATTRIBUTE_END);
        repeat(2, Token.ELEMENT_END);
        add(Token.GENE_START);
        add(Token.PRIMARY_NAME);
        add(Token.ACCESSION_START);
        add(Token.DATABASE);
        add(Token.ATTRIBUTE_END);
        repeat(5, Token.ELEMENT_END);
        add(Token.DOCUMENT_END);
        add(Token.EOF);

        assertParserRejects();
    }

    @Test
    public void acceptsAccessionWithId() {
        add(Token.DOCUMENT_START);
        add(Token.PROJECT_START);
        add(Token.ATTRIBUTE_END);
        add(Token.PATTERN_START);
        add(Token.ATTRIBUTE_END);
        add(Token.REPORTER_START);
        add(Token.NAME, "reporter name");
        add(Token.SYSTEMATIC_NAME);
        add(Token.ATTRIBUTE_END);
        add(Token.FEATURE_START);
        add(Token.ATTRIBUTE_END);
        add(Token.POSITION_START);
        add(Token.UNITS, "mm");
        add(Token.X, 1.1);
        add(Token.Y, 2.2);
        add(Token.ATTRIBUTE_END);
        repeat(2, Token.ELEMENT_END);
        add(Token.GENE_START);
        add(Token.PRIMARY_NAME);
        add(Token.ATTRIBUTE_END);
        add(Token.ACCESSION_START);
        add(Token.DATABASE);
        add(Token.ID);
        add(Token.ATTRIBUTE_END);
        repeat(5, Token.ELEMENT_END);
        add(Token.DOCUMENT_END);
        add(Token.EOF);

        assertParserAccepts();
    }

    @Test
    public void acceptsAccessionWithIdentifier() {
        add(Token.DOCUMENT_START);
        add(Token.PROJECT_START);
        add(Token.ATTRIBUTE_END);
        add(Token.PATTERN_START);
        add(Token.ATTRIBUTE_END);
        add(Token.REPORTER_START);
        add(Token.NAME, "reporter name");
        add(Token.SYSTEMATIC_NAME);
        add(Token.ATTRIBUTE_END);
        add(Token.FEATURE_START);
        add(Token.ATTRIBUTE_END);
        add(Token.POSITION_START);
        add(Token.UNITS, "mm");
        add(Token.X, 1.1);
        add(Token.Y, 2.2);
        add(Token.ATTRIBUTE_END);
        repeat(2, Token.ELEMENT_END);
        add(Token.GENE_START);
        add(Token.PRIMARY_NAME);
        add(Token.ATTRIBUTE_END);
        add(Token.ACCESSION_START);
        add(Token.DATABASE);
        add(Token.IDENTIFIER);
        add(Token.ATTRIBUTE_END);
        repeat(5, Token.ELEMENT_END);
        add(Token.DOCUMENT_END);
        add(Token.EOF);

        assertParserAccepts();
    }

    @Test
    public void acceptsAccessionWithOneOther() {
        add(Token.DOCUMENT_START);
        add(Token.PROJECT_START);
        add(Token.ATTRIBUTE_END);
        add(Token.PATTERN_START);
        add(Token.ATTRIBUTE_END);
        add(Token.REPORTER_START);
        add(Token.NAME, "reporter name");
        add(Token.SYSTEMATIC_NAME);
        add(Token.ATTRIBUTE_END);
        add(Token.FEATURE_START);
        add(Token.ATTRIBUTE_END);
        add(Token.POSITION_START);
        add(Token.UNITS, "mm");
        add(Token.X, 1.1);
        add(Token.Y, 2.2);
        add(Token.ATTRIBUTE_END);
        repeat(2, Token.ELEMENT_END);
        add(Token.GENE_START);
        add(Token.PRIMARY_NAME);
        add(Token.ATTRIBUTE_END);
        add(Token.ACCESSION_START);
        add(Token.DATABASE);
        add(Token.ID);
        add(Token.ATTRIBUTE_END);
        add(Token.OTHER_START);
        add(Token.NAME);
        add(Token.VALUE);
        add(Token.ATTRIBUTE_END);
        repeat(6, Token.ELEMENT_END);
        add(Token.DOCUMENT_END);
        add(Token.EOF);

        assertParserAccepts();
    }

    @Test
    public void acceptsAccessionWithTwoOthers() {
        add(Token.DOCUMENT_START);
        add(Token.PROJECT_START);
        add(Token.ATTRIBUTE_END);
        add(Token.PATTERN_START);
        add(Token.ATTRIBUTE_END);
        add(Token.REPORTER_START);
        add(Token.NAME, "reporter name");
        add(Token.SYSTEMATIC_NAME);
        add(Token.ATTRIBUTE_END);
        add(Token.FEATURE_START);
        add(Token.ATTRIBUTE_END);
        add(Token.POSITION_START);
        add(Token.UNITS, "mm");
        add(Token.X, 1.1);
        add(Token.Y, 2.2);
        add(Token.ATTRIBUTE_END);
        repeat(2, Token.ELEMENT_END);
        add(Token.GENE_START);
        add(Token.PRIMARY_NAME);
        add(Token.ATTRIBUTE_END);
        add(Token.ACCESSION_START);
        add(Token.DATABASE);
        add(Token.ID);
        add(Token.ATTRIBUTE_END);
        add(Token.OTHER_START);
        add(Token.NAME);
        add(Token.VALUE);
        add(Token.ATTRIBUTE_END);
        add(Token.ELEMENT_END);
        add(Token.OTHER_START);
        add(Token.NAME);
        add(Token.VALUE);
        add(Token.ATTRIBUTE_END);
        repeat(6, Token.ELEMENT_END);
        add(Token.DOCUMENT_END);
        add(Token.EOF);

        assertParserAccepts();
    }

    @Test
    public void rejectsAliasWithNoName() {
        add(Token.DOCUMENT_START);
        add(Token.PROJECT_START);
        add(Token.ATTRIBUTE_END);
        add(Token.PATTERN_START);
        add(Token.ATTRIBUTE_END);
        add(Token.REPORTER_START);
        add(Token.NAME, "reporter name");
        add(Token.SYSTEMATIC_NAME);
        add(Token.ATTRIBUTE_END);
        add(Token.FEATURE_START);
        add(Token.ATTRIBUTE_END);
        add(Token.POSITION_START);
        add(Token.UNITS, "mm");
        add(Token.X, 1.1);
        add(Token.Y, 2.2);
        add(Token.ATTRIBUTE_END);
        repeat(2, Token.ELEMENT_END);
        add(Token.GENE_START);
        add(Token.PRIMARY_NAME);
        add(Token.ATTRIBUTE_END);
        add(Token.ALIAS_START);
        add(Token.ATTRIBUTE_END);
        repeat(5, Token.ELEMENT_END);
        add(Token.DOCUMENT_END);
        add(Token.EOF);

        assertParserRejects();
    }

    @Test
    public void acceptsAliasWithOneOther() {
        add(Token.DOCUMENT_START);
        add(Token.PROJECT_START);
        add(Token.ATTRIBUTE_END);
        add(Token.PATTERN_START);
        add(Token.ATTRIBUTE_END);
        add(Token.REPORTER_START);
        add(Token.NAME, "reporter name");
        add(Token.SYSTEMATIC_NAME);
        add(Token.ATTRIBUTE_END);
        add(Token.FEATURE_START);
        add(Token.ATTRIBUTE_END);
        add(Token.POSITION_START);
        add(Token.UNITS, "mm");
        add(Token.X, 1.1);
        add(Token.Y, 2.2);
        add(Token.ATTRIBUTE_END);
        repeat(2, Token.ELEMENT_END);
        add(Token.GENE_START);
        add(Token.PRIMARY_NAME);
        add(Token.ATTRIBUTE_END);
        add(Token.ALIAS_START);
        add(Token.NAME);
        add(Token.ATTRIBUTE_END);
        add(Token.OTHER_START);
        add(Token.NAME);
        add(Token.VALUE);
        add(Token.ATTRIBUTE_END);
        repeat(6, Token.ELEMENT_END);
        add(Token.DOCUMENT_END);
        add(Token.EOF);

        assertParserAccepts();
    }

    @Test
    public void acceptsAliasWithTwoOthers() {
        add(Token.DOCUMENT_START);
        add(Token.PROJECT_START);
        add(Token.ATTRIBUTE_END);
        add(Token.PATTERN_START);
        add(Token.ATTRIBUTE_END);
        add(Token.REPORTER_START);
        add(Token.NAME, "reporter name");
        add(Token.SYSTEMATIC_NAME);
        add(Token.ATTRIBUTE_END);
        add(Token.FEATURE_START);
        add(Token.ATTRIBUTE_END);
        add(Token.POSITION_START);
        add(Token.UNITS, "mm");
        add(Token.X, 1.1);
        add(Token.Y, 2.2);
        add(Token.ATTRIBUTE_END);
        repeat(2, Token.ELEMENT_END);
        add(Token.GENE_START);
        add(Token.PRIMARY_NAME);
        add(Token.ATTRIBUTE_END);
        add(Token.ALIAS_START);
        add(Token.NAME);
        add(Token.ATTRIBUTE_END);
        add(Token.OTHER_START);
        add(Token.NAME);
        add(Token.VALUE);
        add(Token.ATTRIBUTE_END);
        add(Token.ELEMENT_END);
        add(Token.OTHER_START);
        add(Token.NAME);
        add(Token.VALUE);
        add(Token.ATTRIBUTE_END);
        repeat(6, Token.ELEMENT_END);
        add(Token.DOCUMENT_END);
        add(Token.EOF);

        assertParserAccepts();
    }

    @Test
    public void rejectsOtherNoName() {
        add(Token.DOCUMENT_START);
        add(Token.PROJECT_START);
        add(Token.ATTRIBUTE_END);
        add(Token.OTHER_START);
        add(Token.VALUE);
        add(Token.ATTRIBUTE_END);
        repeat(2, Token.ELEMENT_END);
        add(Token.DOCUMENT_END);
        add(Token.EOF);

        assertParserRejects();
    }

    @Test
    public void rejectsOtherNoValue() {
        add(Token.DOCUMENT_START);
        add(Token.PROJECT_START);
        add(Token.ATTRIBUTE_END);
        add(Token.OTHER_START);
        add(Token.NAME);
        add(Token.ATTRIBUTE_END);
        repeat(2, Token.ELEMENT_END);
        add(Token.DOCUMENT_END);
        add(Token.EOF);

        assertParserRejects();
    }

    @Test
    public void acceptsOtherWithOneNestedOther() {
        add(Token.DOCUMENT_START);
        add(Token.PROJECT_START);
        add(Token.ATTRIBUTE_END);
        add(Token.OTHER_START);
        add(Token.NAME);
        add(Token.VALUE);
        add(Token.ATTRIBUTE_END);
        add(Token.OTHER_START);
        add(Token.NAME);
        add(Token.VALUE);
        add(Token.ATTRIBUTE_END);
        repeat(3, Token.ELEMENT_END);
        add(Token.DOCUMENT_END);
        add(Token.EOF);

        assertParserAccepts();
    }

    @Test
    public void acceptsOtherWithTwoNestedOthers() {
        add(Token.DOCUMENT_START);
        add(Token.PROJECT_START);
        add(Token.ATTRIBUTE_END);
        add(Token.OTHER_START);
        add(Token.NAME);
        add(Token.VALUE);
        add(Token.ATTRIBUTE_END);
        add(Token.OTHER_START);
        add(Token.NAME);
        add(Token.VALUE);
        add(Token.ATTRIBUTE_END);
        add(Token.ELEMENT_END);
        add(Token.OTHER_START);
        add(Token.NAME);
        add(Token.VALUE);
        add(Token.ATTRIBUTE_END);
        repeat(3, Token.ELEMENT_END);
        add(Token.DOCUMENT_END);
        add(Token.EOF);

        assertParserAccepts();
    }

    @Test
    public void acceptsOtherWithDoublyNestedOthers() {
        add(Token.DOCUMENT_START);
        add(Token.PROJECT_START);
        add(Token.ATTRIBUTE_END);
        add(Token.OTHER_START);
        add(Token.NAME);
        add(Token.VALUE);
        add(Token.ATTRIBUTE_END);
        add(Token.OTHER_START);
        add(Token.NAME);
        add(Token.VALUE);
        add(Token.ATTRIBUTE_END);
        add(Token.OTHER_START);
        add(Token.NAME);
        add(Token.VALUE);
        add(Token.ATTRIBUTE_END);
        repeat(4, Token.ELEMENT_END);
        add(Token.DOCUMENT_END);
        add(Token.EOF);

        assertParserAccepts();
    }

    @Test
    public void acceptsGridLayout() {
        add(Token.DOCUMENT_START);
        add(Token.PROJECT_START);
        add(Token.ATTRIBUTE_END);
        add(Token.PATTERN_START);
        add(Token.ATTRIBUTE_END);
        add(Token.GRID_LAYOUT_START);
        add(Token.ATTRIBUTE_END);
        add(Token.ELEMENT_END);
        add(Token.REPORTER_START);
        add(Token.NAME, "reporter name");
        add(Token.SYSTEMATIC_NAME);
        add(Token.ATTRIBUTE_END);
        add(Token.FEATURE_START);
        add(Token.ATTRIBUTE_END);
        add(Token.POSITION_START);
        add(Token.UNITS, "mm");
        add(Token.X, 1.1);
        add(Token.Y, 2.2);
        add(Token.ATTRIBUTE_END);
        repeat(5, Token.ELEMENT_END);
        add(Token.DOCUMENT_END);
        add(Token.EOF);

        assertParserAccepts();
    }

    @Test
    public void acceptsGridLayoutWithOptionalAttributes() {
        add(Token.DOCUMENT_START);
        add(Token.PROJECT_START);
        add(Token.ATTRIBUTE_END);
        add(Token.PATTERN_START);
        add(Token.ATTRIBUTE_END);
        add(Token.GRID_LAYOUT_START);
        add(Token.FEATURE_COUNT_X);
        add(Token.FEATURE_COUNT_Y);
        add(Token.FEATURE_SPACING_X);
        add(Token.FEATURE_SPACING_Y);
        add(Token.ATTRIBUTE_END);
        add(Token.ELEMENT_END);
        add(Token.REPORTER_START);
        add(Token.NAME, "reporter name");
        add(Token.SYSTEMATIC_NAME);
        add(Token.ATTRIBUTE_END);
        add(Token.FEATURE_START);
        add(Token.ATTRIBUTE_END);
        add(Token.POSITION_START);
        add(Token.UNITS, "mm");
        add(Token.X, 1.1);
        add(Token.Y, 2.2);
        add(Token.ATTRIBUTE_END);
        repeat(5, Token.ELEMENT_END);
        add(Token.DOCUMENT_END);
        add(Token.EOF);

        assertParserAccepts();
    }

    @Test
    public void acceptsBiosequenceRef() {
        add(Token.DOCUMENT_START);
        add(Token.PROJECT_START);
        add(Token.ATTRIBUTE_END);
        add(Token.PATTERN_START);
        add(Token.ATTRIBUTE_END);
        add(Token.REPORTER_START);
        add(Token.NAME, "reporter name");
        add(Token.SYSTEMATIC_NAME);
        add(Token.ATTRIBUTE_END);
        add(Token.FEATURE_START);
        add(Token.ATTRIBUTE_END);
        add(Token.POSITION_START);
        add(Token.UNITS, "mm");
        add(Token.X, 1.1);
        add(Token.Y, 2.2);
        add(Token.ATTRIBUTE_END);
        repeat(2, Token.ELEMENT_END);
        add(Token.BIOSEQUENCE_REF_START);
        add(Token.IDENTIFIER);
        add(Token.SPECIES);
        add(Token.ATTRIBUTE_END);
        repeat(4, Token.ELEMENT_END);
        add(Token.DOCUMENT_END);
        add(Token.EOF);

        assertParserAccepts();
    }

    @Test
    public void rejectsBiosequenceRefWithNoIdentifier() {
        add(Token.DOCUMENT_START);
        add(Token.PROJECT_START);
        add(Token.ATTRIBUTE_END);
        add(Token.PATTERN_START);
        add(Token.ATTRIBUTE_END);
        add(Token.REPORTER_START);
        add(Token.NAME, "reporter name");
        add(Token.SYSTEMATIC_NAME);
        add(Token.ATTRIBUTE_END);
        add(Token.FEATURE_START);
        add(Token.ATTRIBUTE_END);
        add(Token.POSITION_START);
        add(Token.UNITS, "mm");
        add(Token.X, 1.1);
        add(Token.Y, 2.2);
        add(Token.ATTRIBUTE_END);
        repeat(2, Token.ELEMENT_END);
        add(Token.BIOSEQUENCE_REF_START);
        add(Token.SPECIES);
        add(Token.ATTRIBUTE_END);
        repeat(4, Token.ELEMENT_END);
        add(Token.DOCUMENT_END);
        add(Token.EOF);

        assertParserRejects();
    }

    @Test
    public void rejectsBiosequenceRefWithNoSpecies() {
        add(Token.DOCUMENT_START);
        add(Token.PROJECT_START);
        add(Token.ATTRIBUTE_END);
        add(Token.PATTERN_START);
        add(Token.ATTRIBUTE_END);
        add(Token.REPORTER_START);
        add(Token.NAME, "reporter name");
        add(Token.SYSTEMATIC_NAME);
        add(Token.ATTRIBUTE_END);
        add(Token.FEATURE_START);
        add(Token.ATTRIBUTE_END);
        add(Token.POSITION_START);
        add(Token.UNITS, "mm");
        add(Token.X, 1.1);
        add(Token.Y, 2.2);
        add(Token.ATTRIBUTE_END);
        repeat(2, Token.ELEMENT_END);
        add(Token.BIOSEQUENCE_REF_START);
        add(Token.IDENTIFIER);
        add(Token.ATTRIBUTE_END);
        repeat(4, Token.ELEMENT_END);
        add(Token.DOCUMENT_END);
        add(Token.EOF);

        assertParserRejects();
    }

    @Test
    public void acceptsBiosequenceRefWithOptionalAttributes() {
        add(Token.DOCUMENT_START);
        add(Token.PROJECT_START);
        add(Token.ATTRIBUTE_END);
        add(Token.PATTERN_START);
        add(Token.ATTRIBUTE_END);
        add(Token.REPORTER_START);
        add(Token.NAME, "reporter name");
        add(Token.SYSTEMATIC_NAME);
        add(Token.ATTRIBUTE_END);
        add(Token.FEATURE_START);
        add(Token.ATTRIBUTE_END);
        add(Token.POSITION_START);
        add(Token.UNITS, "mm");
        add(Token.X, 1.1);
        add(Token.Y, 2.2);
        add(Token.ATTRIBUTE_END);
        repeat(2, Token.ELEMENT_END);
        add(Token.BIOSEQUENCE_REF_START);
        add(Token.DATABASE);
        add(Token.IDENTIFIER);
        add(Token.SPECIES);
        add(Token.ATTRIBUTE_END);
        repeat(4, Token.ELEMENT_END);
        add(Token.DOCUMENT_END);
        add(Token.EOF);

        assertParserAccepts ();
    }

    @Test
    public void acceptsBiosequence() {
        add(Token.DOCUMENT_START);
        add(Token.PROJECT_START);
        add(Token.ATTRIBUTE_END);
        add(Token.BIOSEQUENCE_START);
        add(Token.SPECIES);
        add(Token.ATTRIBUTE_END);
        repeat(2, Token.ELEMENT_END);
        add(Token.DOCUMENT_END);
        add(Token.EOF);

        assertParserAccepts();
    }

    @Test
    public void rejectsBiosequenceWithOutSpecies() {
        add(Token.DOCUMENT_START);
        add(Token.PROJECT_START);
        add(Token.ATTRIBUTE_END);
        add(Token.BIOSEQUENCE_START);
        add(Token.ATTRIBUTE_END);
        repeat(2, Token.ELEMENT_END);
        add(Token.DOCUMENT_END);
        add(Token.EOF);

        assertParserRejects();
    }

    @Test
    public void acceptsBiosequenceWithOptionalAttributes() {
        add(Token.DOCUMENT_START);
        add(Token.PROJECT_START);
        add(Token.ATTRIBUTE_END);
        add(Token.BIOSEQUENCE_START);       
        add(Token.ACCESS);
        add(Token.CHROMOSOME);
        add(Token.CONTROL_TYPE);
        add(Token.DESCRIPTION);
        add(Token.EC_NUMBER);
        add(Token.MAP_POSITION);
        add(Token.PRIMARY_NAME);
        add(Token.SEQUENCEDB);
        add(Token.SPECIES);
        add(Token.TYPE);
        add(Token.ATTRIBUTE_END);
        repeat(2, Token.ELEMENT_END);
        add(Token.DOCUMENT_END);
        add(Token.EOF);

        assertParserAccepts();
    }

    @Test
    public void acceptsBiosequenceWithOneOther() {
        add(Token.DOCUMENT_START);
        add(Token.PROJECT_START);
        add(Token.ATTRIBUTE_END);
        add(Token.BIOSEQUENCE_START);
        add(Token.SPECIES);
        add(Token.ATTRIBUTE_END);
        add(Token.OTHER_START);
        add(Token.NAME);
        add(Token.VALUE);
        add(Token.ATTRIBUTE_END);
        repeat(3, Token.ELEMENT_END);
        add(Token.DOCUMENT_END);
        add(Token.EOF);

        assertParserAccepts();
    }

    @Test
    public void acceptsBiosequenceWithTwoOthers() {
        add(Token.DOCUMENT_START);
        add(Token.PROJECT_START);
        add(Token.ATTRIBUTE_END);
        add(Token.BIOSEQUENCE_START);
        add(Token.SPECIES);
        add(Token.ATTRIBUTE_END);
        add(Token.OTHER_START);
        add(Token.NAME);
        add(Token.VALUE);
        add(Token.ATTRIBUTE_END);
        add(Token.ELEMENT_END);
        add(Token.OTHER_START);
        add(Token.NAME);
        add(Token.VALUE);
        add(Token.ATTRIBUTE_END);
        repeat(3, Token.ELEMENT_END);
        add(Token.DOCUMENT_END);
        add(Token.EOF);

        assertParserAccepts();
    }

    @Test
    public void acceptsBiosequenceWithOneAccession() {
        add(Token.DOCUMENT_START);
        add(Token.PROJECT_START);
        add(Token.ATTRIBUTE_END);
        add(Token.BIOSEQUENCE_START);
        add(Token.SPECIES);
        add(Token.ATTRIBUTE_END);
        add(Token.ACCESSION_START);
        add(Token.DATABASE);
        add(Token.ID);
        add(Token.ATTRIBUTE_END);
        repeat(3, Token.ELEMENT_END);
        add(Token.DOCUMENT_END);
        add(Token.EOF);

        assertParserAccepts();
    }

    @Test
    public void acceptsBiosequenceWithTwoAccessions() {
        add(Token.DOCUMENT_START);
        add(Token.PROJECT_START);
        add(Token.ATTRIBUTE_END);
        add(Token.BIOSEQUENCE_START);
        add(Token.SPECIES);
        add(Token.ATTRIBUTE_END);
        add(Token.ACCESSION_START);
        add(Token.DATABASE);
        add(Token.ID);
        add(Token.ATTRIBUTE_END);
        add(Token.ELEMENT_END);
        add(Token.ACCESSION_START);
        add(Token.DATABASE);
        add(Token.ID);
        add(Token.ATTRIBUTE_END);
        repeat(3, Token.ELEMENT_END);
        add(Token.DOCUMENT_END);
        add(Token.EOF);

        assertParserAccepts();
    }

    @Test
    public void acceptsBiosequenceWithOneAlias() {
        add(Token.DOCUMENT_START);
        add(Token.PROJECT_START);
        add(Token.ATTRIBUTE_END);
        add(Token.BIOSEQUENCE_START);
        add(Token.SPECIES);
        add(Token.ATTRIBUTE_END);
        add(Token.ALIAS_START);
        add(Token.NAME);
        add(Token.ATTRIBUTE_END);
        repeat(3, Token.ELEMENT_END);
        add(Token.DOCUMENT_END);
        add(Token.EOF);

        assertParserAccepts();
    }

    @Test
    public void acceptsBiosequenceWithTwoAliases() {
        add(Token.DOCUMENT_START);
        add(Token.PROJECT_START);
        add(Token.ATTRIBUTE_END);
        add(Token.BIOSEQUENCE_START);
        add(Token.SPECIES);
        add(Token.ATTRIBUTE_END);
        add(Token.ALIAS_START);
        add(Token.NAME);
        add(Token.ATTRIBUTE_END);
        add(Token.ELEMENT_END);
        add(Token.ALIAS_START);
        add(Token.NAME);
        add(Token.ATTRIBUTE_END);
        repeat(3, Token.ELEMENT_END);
        add(Token.DOCUMENT_END);
        add(Token.EOF);

        assertParserAccepts();
    }

    private void assertParserAccepts() {
        assertTrue(parser.validate());
    }

    private void assertParserRejects() {
        assertFalse(parser.validate());
    }

    private void add(Token token) {
        tokenizer.add(token);
    }

    private void repeat(int count, Token token) {
        tokenizer.repeat(count, token);
    }

    private void add(Token token, Object value) {
        tokenizer.add(token, value);
    }

}
