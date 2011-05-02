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

import java.util.HashMap;
import java.util.Map;

/**
 * A wrapper for an enumeration of the tokens in the grammar describing the Agilent GEML file format.
 * Provides static class fields for use by the wrapped enumeration.
 * @author jscott
 */
//Non-final static variables in the outer class are set by the inner enumeration.
@SuppressWarnings("PMD.AssignmentToNonFinalStatic")
final class AgilentGELMToken {

    // These are added to by static constructors in the wrapped Token enumeration.
    private static Map<String, Token> startTokenMap = new HashMap<String, Token>();
    private static Map<String, Token> attributeMap = new HashMap<String, Token>();
    
    // These are set by static constructors in the wrapped Token enumeration.
    private static Token documentStartToken;
    private static Token documentEndToken;
    private static Token eofToken;
    private static Token endToken;
    private static Token errorToken;
    
    // Utility class should not have a default constructor.
    private AgilentGELMToken() {       
    }
    
    /**
     * @author jscott
     * Enumeration of the tokens in the grammar describing the Agilent GEML file format.
     * 
     * Note: this enumeration is nested in the outer class because its constructors make use
     * of static variables to register enumeration values as different types of tokens.  Unfortunately,
     * constructors in enumerations cannot access static members in the enumeration class, but they
     * can access those in a containing class.
     */
    enum Token {
        /**
         * Start of the XML document.
         */
        DOCUMENT_START(TokenType.DOCUMENT_START),
        
        /**
         * End of the XML document.
         */
        DOCUMENT_END(TokenType.DOCUMENT_END),
        
        /**
         * End of input.
         */
        EOF(TokenType.EOF),
        
        /**
         * An error occurred during lexical analysis.
         */
        TOKENIZER_ERROR(TokenType.ERROR),


        /**
         * Start of an "accession" XML element.
         */
        ACCESSION_START("accession", TokenType.START),

        /**
         * Start of an "alias" XML element.
         */
        ALIAS_START("alias", TokenType.START),
        
        /**
         * Start of a "biosequence" XML element.
         */
        BIOSEQUENCE_START("biosequence", TokenType.START),
        
        /**
         * Start of a "biosequence_ref" XML element.
         */
        BIOSEQUENCE_REF_START("biosequence_ref", TokenType.START),

        /**
         * Start of a "chip" XML element.
         */
        CHIP_START("chip", TokenType.START),

        /**
         * Start of a "feature" XML element.
         */
        FEATURE_START("feature", TokenType.START),

        /**
         * Start of a "gene" XML element.
         */
        GENE_START("gene", TokenType.START),
                
        /**
         * Start of a "grid_layout" XML element.
         */
        GRID_LAYOUT_START("grid_layout", TokenType.START),

        /**
         * Start of an "other" XML element.
         */
        OTHER_START("other", TokenType.START),

        /**
         * Start of a "pattern" XML element.
         */
        PATTERN_START("pattern", TokenType.START),

        /**
         * Start of a "pen" XML element.
         */
        PEN_START("pen", TokenType.START),

        /**
         * Start of a "position" XML element.
         */
        POSITION_START("position", TokenType.START),

        /**
         * Start of a "printing" XML element.
         */
        PRINTING_START("printing", TokenType.START),

        /**
         * Start of a "project" XML element.
         */
        PROJECT_START("project", TokenType.START),

        /**
         * Start of a "reporter" XML element.
         */
        REPORTER_START("reporter", TokenType.START),

        
        /**
         * End of an XML element.
         */
        END(TokenType.END),


        /**
         * An "access" XML attribute.
         */
        ACCESS("access", TokenType.ATTRIBUTE),

        /**
         * An "accession" XML attribute.
         */
        ACCESSION("accession", TokenType.ATTRIBUTE),

        /**
         * An "active_sequence" XML attribute.
         */
        ACTIVE_SEQUENCE("active_sequence", TokenType.ATTRIBUTE),

        /**
         * A "barcode" XML attribute.
         */
        BARCODE("barcode", TokenType.ATTRIBUTE),

        /**
         * A "by" XML attribute.
         */
        BY("by", TokenType.ATTRIBUTE),

        /**
         * A "chromosome" XML attribute.
         */
        CHROMOSOME("chromosome", TokenType.ATTRIBUTE),

        /**
         * A "company" XML attribute.
         */
        COMPANY("company", TokenType.ATTRIBUTE),

        /**
         * A "control_type" XML attribute.
         */
        CONTROL_TYPE("control_type", TokenType.ATTRIBUTE),

        /**
         * A "ctrl_for_feat_num" XML attribute.
         */
        CTRL_FOR_FEAT_NUM("ctrl_for_feat_num", TokenType.ATTRIBUTE),

        /**
         * A "database" XML attribute.
         */
        DATABASE("database", TokenType.ATTRIBUTE),

        /**
         * A "date" XML attribute.
         */
        DATE("date", TokenType.ATTRIBUTE),

        /**
         * A "deletion" XML attribute.
         */
        DELETION("deletion", TokenType.ATTRIBUTE),

        /**
         * A "description" XML attribute.
         */
        DESCRIPTION("description", TokenType.ATTRIBUTE),

        /**
         * An "ec_number" XML attribute.
         */
        EC_NUMBER("ec_number", TokenType.ATTRIBUTE),

        /**
         * A "feature_count_x" XML attribute.
         */
        FEATURE_COUNT_X("feature_count_x", TokenType.ATTRIBUTE),

        /**
         * A "feature_count_y" XML attribute.
         */
        FEATURE_COUNT_Y("feature_count_y", TokenType.ATTRIBUTE),

        /**
         * A "fail_type" XML attribute.
         */
        FAIL_TYPE("fail_type", TokenType.ATTRIBUTE),
        
        /**
         * A "feature_spacing_x" XML attribute.
         */
        FEATURE_SPACING_X("feature_spacing_x", TokenType.ATTRIBUTE),

        /**
         * A "feature_spacing_y" XML attribute.
         */
        FEATURE_SPACING_Y("feature_spacing_y", TokenType.ATTRIBUTE),
        
        /**
         * An "id" XML attribute.
         */
        ID("id", TokenType.ATTRIBUTE),
        
        /**
         * An "identifier" XML attribute.
         */
       IDENTIFIER("identifier", TokenType.ATTRIBUTE),

        /**
         * A "linker_sequence" XML attribute.
         */
        LINKER_SEQUENCE("linker_sequence", TokenType.ATTRIBUTE),

        /**
         * A "map_position" XML attribute.
         */
        MAP_POSITION("map_position", TokenType.ATTRIBUTE),

        /**
         * A "mismatch_count" XML attribute.
         */
        MISMATCH_COUNT("mismatch_count", TokenType.ATTRIBUTE),

        /**
         * A "name" XML attribute.
         */
        NAME("name", TokenType.ATTRIBUTE),

        /**
         * A "number" XML attribute.
         */
        NUMBER("number", TokenType.ATTRIBUTE),
        
        /**
         * An "organization" XML attribute.
         */
        ORGANIZATION("organization", TokenType.ATTRIBUTE),

        /**
         * An "owner" XML attribute.
         */
        OWNER("owner", TokenType.ATTRIBUTE),

        /**
         * A "pattern_name" XML attribute.
         */
        PATTERN_NAME("pattern_name", TokenType.ATTRIBUTE),
        
        /**
         * A "prepared_at_site" XML attribute.
         */
        PREPARED_AT_SITE("prepared_at_site", TokenType.ATTRIBUTE),

        /**
         * A "prepared_by" XML attribute.
         */
        PREPARED_BY("prepared_by", TokenType.ATTRIBUTE),

        /**
         * A "prepared_by_org" XML attribute.
         */
        PREPARED_BY_ORG("prepared_by_org", TokenType.ATTRIBUTE),

        /**
         * A "prepared_for" XML attribute.
         */
        PREPARED_FOR("prepared_for", TokenType.ATTRIBUTE),

        /**
         * A "prepared_for_org" XML attribute.
         */
        PREPARED_FOR_ORG("prepared_for_org", TokenType.ATTRIBUTE),

        /**
         * A "primary_name" XML attribute.
         */
        PRIMARY_NAME("primary_name", TokenType.ATTRIBUTE),

        /**
         * A "primer1_sequence" XML attribute.
         */
        PRIMER1_SEQUENCE("primer1_sequence", TokenType.ATTRIBUTE),

        /**
         * A "primer2_sequence" XML attribute.
         */
        PRIMER2_SEQUENCE("primer2_sequence", TokenType.ATTRIBUTE),

        /**
         * A "printer" XML attribute.
         */
        PRINTER("printer", TokenType.ATTRIBUTE),

        /**
         * A "run_description" XML attribute.
         */
        RUN_DESCRIPTION("run_description", TokenType.ATTRIBUTE),

        /**
         * A "species_database" XML attribute.
         */
        SPECIES_DATABASE("species_database", TokenType.ATTRIBUTE),

        /**
         * A "species" XML attribute.
         */
        SPECIES("species", TokenType.ATTRIBUTE),

        /**
         * A "start_coord" XML attribute.
         */
        START_COORD("start_coord", TokenType.ATTRIBUTE),
        
        /**
         * A "sequenceDB" XML attribute.
         */
        SEQUENCEDB("sequenceDB", TokenType.ATTRIBUTE),

        /**
         * A "systematic_name" XML attribute.
         */
        SYSTEMATIC_NAME("systematic_name", TokenType.ATTRIBUTE),

        /**
         * A "type_id" XML attribute.
         */
        TYPE_ID("type_id", TokenType.ATTRIBUTE),

        /**
         * A "type" XML attribute.
         */
        TYPE("type", TokenType.ATTRIBUTE),

        /**
         * A "units" XML attribute.
         */
        UNITS("units", TokenType.ATTRIBUTE),

        /**
         * A "value" XML attribute.
         */
        VALUE("value", TokenType.ATTRIBUTE),

        /**
         * A "x" XML attribute.
         */
        X("x", TokenType.ATTRIBUTE),

        /**
         * A "y" XML attribute.
         */
        Y("y", TokenType.ATTRIBUTE);
              
        private Token(TokenType tokenType) {
            switch (tokenType) {
                case DOCUMENT_START:
                    documentStartToken = this;
                    break;
                case DOCUMENT_END:
                    documentEndToken = this;
                    break;
                case EOF:
                    eofToken = this;
                    break;
                case END:
                    endToken = this;
                    break;
                case ERROR:
                    errorToken = this;
                    break;
                default:
                    // Do nothing
                    break;
            }
        }

        private Token(String xmlName, TokenType tokenType) {
            switch (tokenType) {
                case START:
                    startTokenMap.put(xmlName, this);
                    break;
                case ATTRIBUTE:
                    attributeMap.put(xmlName, this);
                    break;
                default:
                    // Do nothing
                    break;
            }
        }

        static Token getDocumentStartToken() {
            return documentStartToken;
        }

        static Token getDocumentEndToken() {
            return documentEndToken;
        }

        static Token getEOFToken() {
            return eofToken;
        }

        static Token getEndToken() {
            return endToken;
        }

        static Token getErrorToken() {
            return errorToken;
        }

        static Token findStartToken(String elementName) {
            Token token = startTokenMap.get(elementName);
            return token != null ? token : Token.TOKENIZER_ERROR;
        }

        static Token findAttributeToken(String attributeName) {
            Token token = attributeMap.get(attributeName);
            return token != null ? token : Token.TOKENIZER_ERROR;
        }
        
        /**
         * Categorizes the types of tokens in the grammar.
         */
        private enum TokenType {
            DOCUMENT_START, DOCUMENT_END, START, END, ATTRIBUTE, EOF, ERROR
        }
    }
}


