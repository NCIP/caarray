//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.plugins.agilent;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import gov.nih.nci.caarray.plugins.agilent.AgilentGELMToken.Token;
import gov.nih.nci.caarray.test.data.arraydesign.AgilentArrayDesignFiles;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

public class AgilentGELMTokenizerTest {

    @Test
    public void handlesEmptyDocument() {
        String xmlSnippet = "";

        StringReader inputReader = new StringReader(xmlSnippet);
        AgilentGELMTokenizer tokenizer = new AgilentGELMTokenizer(inputReader);

        assertEquals(Token.DOCUMENT_START, tokenizer.getCurrentToken());
        tokenizer.advance();

        assertEquals(Token.EOF, tokenizer.getCurrentToken());
    }

    @Test
    public void recognizesPatternStartAndEnd() {
        String xmlSnippet =
                "<pattern/>";

        StringReader inputReader = new StringReader(xmlSnippet);
        AgilentGELMTokenizer tokenizer = new AgilentGELMTokenizer(inputReader);

        assertEquals(Token.DOCUMENT_START, tokenizer.getCurrentToken());
        tokenizer.advance();

        assertEquals(Token.PATTERN_START, tokenizer.getCurrentToken());
        tokenizer.advance();

        assertEquals(Token.ATTRIBUTE_END, tokenizer.getCurrentToken());
        tokenizer.advance();

        assertEquals(Token.ELEMENT_END, tokenizer.getCurrentToken());
        tokenizer.advance();

        assertEquals(Token.DOCUMENT_END, tokenizer.getCurrentToken());
        tokenizer.advance();

        assertEquals(Token.EOF, tokenizer.getCurrentToken());
    }

    @Test
    public void producesErrorTokenOnUnknownElement() {
        String xmlSnippet =
                "<madeUpElementName></madeUpElementName>";

        StringReader inputReader = new StringReader(xmlSnippet);
        AgilentGELMTokenizer tokenizer = new AgilentGELMTokenizer(inputReader);

        assertEquals(Token.DOCUMENT_START, tokenizer.getCurrentToken());
        tokenizer.advance();

        assertEquals(Token.TOKENIZER_ERROR, tokenizer.getCurrentToken());
        tokenizer.advance();

        assertEquals(Token.ATTRIBUTE_END, tokenizer.getCurrentToken());
        tokenizer.advance();

        assertEquals(Token.ELEMENT_END, tokenizer.getCurrentToken());
        tokenizer.advance();

        assertEquals(Token.DOCUMENT_END, tokenizer.getCurrentToken());
        tokenizer.advance();

        assertEquals(Token.EOF, tokenizer.getCurrentToken());
    }

    @Test
    public void producesErrorTokenOnUnknownAttribute() {
        String xmlSnippet =
                "<pattern madeUpAttributeName=\"dummy value 1\"></pattern>";

        StringReader inputReader = new StringReader(xmlSnippet);
        AgilentGELMTokenizer tokenizer = new AgilentGELMTokenizer(inputReader);

        assertEquals(Token.DOCUMENT_START, tokenizer.getCurrentToken());
        tokenizer.advance();

        assertEquals(Token.PATTERN_START, tokenizer.getCurrentToken());
        tokenizer.advance();

        assertEquals(Token.TOKENIZER_ERROR, tokenizer.getCurrentToken());
        tokenizer.advance();

        assertEquals(Token.ATTRIBUTE_END, tokenizer.getCurrentToken());
        tokenizer.advance();

        assertEquals(Token.ELEMENT_END, tokenizer.getCurrentToken());
        tokenizer.advance();

        assertEquals(Token.DOCUMENT_END, tokenizer.getCurrentToken());
        tokenizer.advance();

        assertEquals(Token.EOF, tokenizer.getCurrentToken());
    }

    @Test
    public void recognizesAccessionElementWithAttributes() {
        handlesElementAndAttributes("accession", Token.ACCESSION_START, Token.ELEMENT_END, Token.ATTRIBUTE_END, new String[]{"database", "id",}, new Token[]{Token.DATABASE, Token.ID,});
    }

    @Test
    public void recognizesAliasElementWithAttributes() {
        handlesElementAndAttributes("alias", Token.ALIAS_START, Token.ELEMENT_END, Token.ATTRIBUTE_END, new String[]{"name",}, new Token[]{Token.NAME,});
    }

    @Test
    public void recognizesChipElementWithAttributes() {
        handlesElementAndAttributes("chip", Token.CHIP_START, Token.ELEMENT_END, Token.ATTRIBUTE_END, new String[]{"barcode", "prepared_for", "prepared_for_org"}, new Token[]{Token.BARCODE, Token.PREPARED_FOR, Token.PREPARED_FOR_ORG,});
    }

    @Test
    public void recognizesFeatureElementWithAttributes() {
        handlesElementAndAttributes("feature", Token.FEATURE_START, Token.ELEMENT_END, Token.ATTRIBUTE_END, new String[]{"number", "ctrl_for_feat_num",}, new Token[]{Token.NUMBER, Token.CTRL_FOR_FEAT_NUM,});
    }

    @Test
    public void recognizesGeneElementWithAttributes() {
        handlesElementAndAttributes("gene", Token.GENE_START, Token.ELEMENT_END, Token.ATTRIBUTE_END, new String[]{"primary_name", "systematic_name", "species", "chromosome", "map_position", "description",}, new Token[]{Token.PRIMARY_NAME, Token.SYSTEMATIC_NAME, Token.SPECIES, Token.CHROMOSOME, Token.MAP_POSITION, Token.DESCRIPTION,});
    }

    @Test
    public void recognizesOtherElementWithAttributes() {
        handlesElementAndAttributes("other", Token.OTHER_START, Token.ELEMENT_END, Token.ATTRIBUTE_END, new String[]{"name", "value",}, new Token[]{Token.NAME, Token.VALUE,});
    }

    @Test
    public void recognizesPatternElementWithAttributes() {
        handlesElementAndAttributes("pattern", Token.PATTERN_START, Token.ELEMENT_END, Token.ATTRIBUTE_END, new String[]{"name", "type_id", "species_database", "description", "access", "owner",}, new Token[]{Token.NAME, Token.TYPE_ID, Token.SPECIES_DATABASE, Token.DESCRIPTION, Token.ACCESS, Token.OWNER,});
    }

    @Test
    public void recognizesPenElementWithAttributes() {
        handlesElementAndAttributes("pen", Token.PEN_START, Token.ELEMENT_END, Token.ATTRIBUTE_END, new String[]{"x", "y", "units",}, new Token[]{Token.X, Token.Y, Token.UNITS,});
    }

    @Test
    public void recognizesPositionElementWithAttributes() {
        handlesElementAndAttributes("position", Token.POSITION_START, Token.ELEMENT_END, Token.ATTRIBUTE_END, new String[]{"x", "y", "units",}, new Token[]{Token.X, Token.Y, Token.UNITS,});
    }

    @Test
    public void recognizesPrintingElementWithAttributes() {
        handlesElementAndAttributes("printing", Token.PRINTING_START, Token.ELEMENT_END, Token.ATTRIBUTE_END, new String[]{"date", "printer", "type", "pattern_name", "run_description", "prepared_by", "prepared_at_site", "prepared_by_org",}, new Token[]{Token.DATE, Token.PRINTER, Token.TYPE, Token.PATTERN_NAME, Token.RUN_DESCRIPTION, Token.PREPARED_BY, Token.PREPARED_AT_SITE, Token.PREPARED_BY_ORG,});
    }

    @Test
    public void recognizesProjectElementWithGelmPatternDtdAttributes() {
        handlesElementAndAttributes("project", Token.PROJECT_START, Token.ELEMENT_END, Token.ATTRIBUTE_END, new String[]{"name", "id", "date", "by", "company",}, new Token[]{Token.NAME, Token.ID, Token.DATE, Token.BY, Token.COMPANY,});
    }

    @Test
    public void recognizesProjectElementWithGelmDtdAttributes() {
        handlesElementAndAttributes("project", Token.PROJECT_START, Token.ELEMENT_END, Token.ATTRIBUTE_END, new String[]{"name", "id", "date", "by", "organization",}, new Token[]{Token.NAME, Token.ID, Token.DATE, Token.BY, Token.ORGANIZATION,});
    }

    @Test
    public void recognizesReporterElementWithAttributes() {
        handlesElementAndAttributes("reporter", Token.REPORTER_START, Token.ELEMENT_END, Token.ATTRIBUTE_END, new String[]{"name", "systematic_name", "accession", "deletion", "control_type", "fail_type", "active_sequence", "linker_sequence", "primer1_sequence", "primer2_sequence", "start_coord", "mismatch_count",}, new Token[]{Token.NAME, Token.SYSTEMATIC_NAME, Token.ACCESSION, Token.DELETION, Token.CONTROL_TYPE, Token.FAIL_TYPE, Token.ACTIVE_SEQUENCE, Token.LINKER_SEQUENCE, Token.PRIMER1_SEQUENCE, Token.PRIMER2_SEQUENCE, Token.START_COORD, Token.MISMATCH_COUNT,});
    }

    @Test
    public void recognizesBiosequenceElementWithAttributes() {
        handlesElementAndAttributes("biosequence", Token.BIOSEQUENCE_START, Token.ELEMENT_END, Token.ATTRIBUTE_END, new String[]{"access", "chromosome", "control_type", "description", "ec_number", "map_position", "primary_name", "sequenceDB", "species", "type",}, new Token[]{Token.ACCESS, Token.CHROMOSOME, Token.CONTROL_TYPE, Token.DESCRIPTION, Token.EC_NUMBER, Token.MAP_POSITION, Token.PRIMARY_NAME, Token.SEQUENCEDB, Token.SPECIES, Token.TYPE,});
    }
    
    @Test
    public void recognizesBiosequenceRefElementWithAttributes() {
        handlesElementAndAttributes("biosequence_ref", Token.BIOSEQUENCE_REF_START, Token.ELEMENT_END, Token.ATTRIBUTE_END, new String[]{"database", "identifier", "species",}, new Token[]{Token.DATABASE, Token.IDENTIFIER, Token.SPECIES,});
    }
    
    @Test
    public void recognizesGridLayouElementWithAttributes() {
        handlesElementAndAttributes("grid_layout", Token.GRID_LAYOUT_START, Token.ELEMENT_END, Token.ATTRIBUTE_END, new String[]{"feature_count_x", "feature_count_y", "feature_spacing_x  ","feature_spacing_y ",}, new Token[]{Token.FEATURE_COUNT_X, Token.FEATURE_COUNT_Y, Token.FEATURE_SPACING_X, Token.FEATURE_SPACING_Y,});
    }
    
    @Test
    public void recognizesAllTokensInTestGeneExpressionTwoXmlFile() throws FileNotFoundException {
        recognizesAllTokensInFile(AgilentArrayDesignFiles.TEST_GENE_EXPRESSION_2_XML);       
    }
    
    @Test
    public void recognizesAllTokensInTestMiRnaTwoXmlFile() throws FileNotFoundException {
        recognizesAllTokensInFile(AgilentArrayDesignFiles.TEST_MIRNA_2_XML);       
    }
    
    private void recognizesAllTokensInFile(File file) throws FileNotFoundException {
        FileReader reader = new FileReader(file);
        AgilentGELMTokenizer tokenizer = new AgilentGELMTokenizer(reader);
        while (Token.EOF != tokenizer.getCurrentToken()) {
            assertThat(tokenizer.getCurrentToken(), not(is(Token.getErrorToken())));
            tokenizer.advance();
        }
    }

    private void handlesElementAndAttributes(String elementName, Token elementStartToken, Token elementEndToken, Token attributeEndToken, String[] attributeNames, Token[] attributeTokens) {
        String xmlSnippet = buildXmlSnippet(elementName, attributeNames);
        StringReader inputReader = new StringReader(xmlSnippet);

        AgilentGELMTokenizer tokenizer = new AgilentGELMTokenizer(inputReader);

        // Expect a document start token
        assertEquals(Token.DOCUMENT_START, tokenizer.getCurrentToken());
        tokenizer.advance();

        // Followed by the element start token
        assertEquals(elementStartToken, tokenizer.getCurrentToken());
        tokenizer.advance();

        // Followed by all the attribute tokens
        Map<String, Token> attributeTokenMap = new HashMap<String, Token>();
        for (int i = 0; i < attributeNames.length; i++) {
            attributeTokenMap.put(attributeNames[i], attributeTokens[i]);
        }

        for (String attributeName : attributeNames) {
            Token expectedAttributeToken = attributeTokenMap.get(attributeName);
            assertEquals(expectedAttributeToken, tokenizer.getCurrentToken());
            tokenizer.advance();
        }

        // Followed the attribute end token
        assertEquals(attributeEndToken, tokenizer.getCurrentToken());
        tokenizer.advance();

        // Followed the element end token
        assertEquals(elementEndToken, tokenizer.getCurrentToken());
        tokenizer.advance();

        // Followed by the document end token
        assertEquals(Token.DOCUMENT_END, tokenizer.getCurrentToken());
        tokenizer.advance();

        // Followed by the EOF token
        assertEquals(Token.EOF, tokenizer.getCurrentToken());
    }

    private String buildXmlSnippet(String elementName, String[] attributeNames) {
        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);

        writer.printf("<%s", elementName);
        for (String attributeName : attributeNames)
            writer.printf(" %s=\"dummy value\"", attributeName);
        writer.println(">");
        writer.printf("</%s>", elementName);
        writer.close();

        return stringWriter.toString();
    }
}
