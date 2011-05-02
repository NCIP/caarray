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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import gov.nih.nci.caarray.plugins.agilent.AgilentGELMTokenizer;
import gov.nih.nci.caarray.plugins.agilent.AgilentGELMToken.Token;
import gov.nih.nci.caarray.test.data.arraydesign.AgilentArrayDesignFiles;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Arrays;
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

        assertEquals(Token.END, tokenizer.getCurrentToken());
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

        assertEquals(Token.END, tokenizer.getCurrentToken());
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

        assertEquals(Token.END, tokenizer.getCurrentToken());
        tokenizer.advance();

        assertEquals(Token.DOCUMENT_END, tokenizer.getCurrentToken());
        tokenizer.advance();

        assertEquals(Token.EOF, tokenizer.getCurrentToken());
    }

    @Test
    public void recognizesAccessionElementWithAttributes() {
        handlesElementAndAttributes("accession", Token.ACCESSION_START, Token.END, new String[]{"database", "id",}, new Token[]{Token.DATABASE, Token.ID,});
    }

    @Test
    public void recognizesAliasElementWithAttributes() {
        handlesElementAndAttributes("alias", Token.ALIAS_START, Token.END, new String[]{"name",}, new Token[]{Token.NAME,});
    }

    @Test
    public void recognizesChipElementWithAttributes() {
        handlesElementAndAttributes("chip", Token.CHIP_START, Token.END, new String[]{"barcode", "prepared_for", "prepared_for_org"}, new Token[]{Token.BARCODE, Token.PREPARED_FOR, Token.PREPARED_FOR_ORG,});
    }

    @Test
    public void recognizesFeatureElementWithAttributes() {
        handlesElementAndAttributes("feature", Token.FEATURE_START, Token.END, new String[]{"number", "ctrl_for_feat_num",}, new Token[]{Token.NUMBER, Token.CTRL_FOR_FEAT_NUM,});
    }

    @Test
    public void recognizesGeneElementWithAttributes() {
        handlesElementAndAttributes("gene", Token.GENE_START, Token.END, new String[]{"primary_name", "systematic_name", "species", "chromosome", "map_position", "description",}, new Token[]{Token.PRIMARY_NAME, Token.SYSTEMATIC_NAME, Token.SPECIES, Token.CHROMOSOME, Token.MAP_POSITION, Token.DESCRIPTION,});
    }

    @Test
    public void recognizesOtherElementWithAttributes() {
        handlesElementAndAttributes("other", Token.OTHER_START, Token.END, new String[]{"name", "value",}, new Token[]{Token.NAME, Token.VALUE,});
    }

    @Test
    public void recognizesPatternElementWithAttributes() {
        handlesElementAndAttributes("pattern", Token.PATTERN_START, Token.END, new String[]{"name", "type_id", "species_database", "description", "access", "owner",}, new Token[]{Token.NAME, Token.TYPE_ID, Token.SPECIES_DATABASE, Token.DESCRIPTION, Token.ACCESS, Token.OWNER,});
    }

    @Test
    public void recognizesPenElementWithAttributes() {
        handlesElementAndAttributes("pen", Token.PEN_START, Token.END, new String[]{"x", "y", "units",}, new Token[]{Token.X, Token.Y, Token.UNITS,});
    }

    @Test
    public void recognizesPositionElementWithAttributes() {
        handlesElementAndAttributes("position", Token.POSITION_START, Token.END, new String[]{"x", "y", "units",}, new Token[]{Token.X, Token.Y, Token.UNITS,});
    }

    @Test
    public void recognizesPrintingElementWithAttributes() {
        handlesElementAndAttributes("printing", Token.PRINTING_START, Token.END, new String[]{"date", "printer", "type", "pattern_name", "run_description", "prepared_by", "prepared_at_site", "prepared_by_org",}, new Token[]{Token.DATE, Token.PRINTER, Token.TYPE, Token.PATTERN_NAME, Token.RUN_DESCRIPTION, Token.PREPARED_BY, Token.PREPARED_AT_SITE, Token.PREPARED_BY_ORG,});
    }

    @Test
    public void recognizesProjectElementWithGelmPatternDtdAttributes() {
        handlesElementAndAttributes("project", Token.PROJECT_START, Token.END, new String[]{"name", "id", "date", "by", "company",}, new Token[]{Token.NAME, Token.ID, Token.DATE, Token.BY, Token.COMPANY,});
    }

    @Test
    public void recognizesProjectElementWithGelmDtdAttributes() {
        handlesElementAndAttributes("project", Token.PROJECT_START, Token.END, new String[]{"name", "id", "date", "by", "organization",}, new Token[]{Token.NAME, Token.ID, Token.DATE, Token.BY, Token.ORGANIZATION,});
    }

    @Test
    public void recognizesReporterElementWithAttributes() {
        handlesElementAndAttributes("reporter", Token.REPORTER_START, Token.END, new String[]{"name", "systematic_name", "accession", "deletion", "control_type", "fail_type", "active_sequence", "linker_sequence", "primer1_sequence", "primer2_sequence", "start_coord", "mismatch_count",}, new Token[]{Token.NAME, Token.SYSTEMATIC_NAME, Token.ACCESSION, Token.DELETION, Token.CONTROL_TYPE, Token.FAIL_TYPE, Token.ACTIVE_SEQUENCE, Token.LINKER_SEQUENCE, Token.PRIMER1_SEQUENCE, Token.PRIMER2_SEQUENCE, Token.START_COORD, Token.MISMATCH_COUNT,});
    }

    @Test
    public void recognizesBiosequenceElementWithAttributes() {
        handlesElementAndAttributes("biosequence", Token.BIOSEQUENCE_START, Token.END, new String[]{"access", "chromosome", "control_type", "description", "ec_number", "map_position", "primary_name", "sequenceDB", "species", "type",}, new Token[]{Token.ACCESS, Token.CHROMOSOME, Token.CONTROL_TYPE, Token.DESCRIPTION, Token.EC_NUMBER, Token.MAP_POSITION, Token.PRIMARY_NAME, Token.SEQUENCEDB, Token.SPECIES, Token.TYPE,});
    }
    
    @Test
    public void recognizesBiosequenceRefElementWithAttributes() {
        handlesElementAndAttributes("biosequence_ref", Token.BIOSEQUENCE_REF_START, Token.END, new String[]{"database", "identifier", "species",}, new Token[]{Token.DATABASE, Token.IDENTIFIER, Token.SPECIES,});
    }
    
    @Test
    public void recognizesGridLayouElementWithAttributes() {
        handlesElementAndAttributes("grid_layout", Token.GRID_LAYOUT_START, Token.END, new String[]{"feature_count_x", "feature_count_y", "feature_spacing_x  ","feature_spacing_y ",}, new Token[]{Token.FEATURE_COUNT_X, Token.FEATURE_COUNT_Y, Token.FEATURE_SPACING_X, Token.FEATURE_SPACING_Y,});
    }
    
    @Test
    public void recognizesAllTokensInTestAcghXmlFile() throws FileNotFoundException {
        recognizesAllTokensInFile(AgilentArrayDesignFiles.TEST_ACGH_XML);       
    }
    
    @Test
    public void recognizesAllTokensInTestGeneExpressionOneXmlFile() throws FileNotFoundException {
        recognizesAllTokensInFile(AgilentArrayDesignFiles.TEST_GENE_EXPRESSION_1_XML);       
    }
    
    @Test
    public void recognizesAllTokensInTestGeneExpressionTwoXmlFile() throws FileNotFoundException {
        recognizesAllTokensInFile(AgilentArrayDesignFiles.TEST_GENE_EXPRESSION_2_XML);       
    }
    
    @Test
    public void recognizesAllTokensInTestGeneExpressionThreeXmlFile() throws FileNotFoundException {
        recognizesAllTokensInFile(AgilentArrayDesignFiles.TEST_GENE_EXPRESSION_3_XML);       
    }
    
    @Test
    public void recognizesAllTokensInTestMiRnaOneXmlFile() throws FileNotFoundException {
        recognizesAllTokensInFile(AgilentArrayDesignFiles.TEST_MIRNA_1_XML);       
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

    private void handlesElementAndAttributes(String elementName, Token elementStartToken, Token elementEndToken, String[] attributeNames, Token[] attributeTokens) {
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
        String[] sortedAttributeNames = attributeNames.clone();
        Arrays.sort(sortedAttributeNames);

        for (String attributeName : sortedAttributeNames) {
            Token expectedAttributeToken = attributeTokenMap.get(attributeName);
            assertEquals(expectedAttributeToken, tokenizer.getCurrentToken());
            tokenizer.advance();
        }

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
