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

import gov.nih.nci.caarray.platforms.agilent.AgilentGELMToken.Token;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Implements a parser which recognizes a grammar representing an Agilent GELM file. Rather than dealing with XML
 * directly, this parser makes use of an XMLTokenizer object, which reduces the XML file to a series of tokens. This
 * simplifies unit testing this class.
 * 
 * @author jscott
 * 
 */
@SuppressWarnings("PMD.ExcessiveClassLength")
class AgilentGELMParser {
    private static final String NEGATIVE_CONTROLS_CONTROL_GROUP_NAME = "negative controls";
    private static final String POSITIVE_CONTROLS_CONTROL_GROUP_NAME = "positive controls";
    private static final String IGNORE_CONTROL_GROUP_NAME = "ignore";

    private final XMLTokenizer<Token> tokenizer;
    private final Pattern chromosomePattern = Pattern.compile("chr(\\d{1,2}|X|Y|x|y):(\\d+)-(\\d+)(?:\\|.+){0,1}");
    private int featureCount;
    private int geneCount;
    private int positionCount;
    private int penCount;

    /**
     * @param tokenizer provides the stream of tokens to be parsed.
     */
    AgilentGELMParser(XMLTokenizer<Token> tokenizer) {
        this.tokenizer = tokenizer;
    }

    /**
     * Validates the token stream by doing a dry run parse.
     * 
     * @return true if the validation parse succeeds; false otherwise
     */
    boolean validate() {
        ValidatingArrayDesignBuilder arrayDesignBuilder = new ValidatingArrayDesignBuilder();
        return parse(arrayDesignBuilder);
    }

    /**
     * Parses the token stream, calling the arrayDesignBuilder as it goes.
     * 
     * @param arrayDesignBuilder object to call as parse proceeds
     * @return true if the parse succeeds; false otherwise
     */
    boolean parse(ArrayDesignBuilder arrayDesignBuilder) {
        try {
            parseDocument(arrayDesignBuilder);
            expect(Token.EOF);
        } catch (ParseException e) {
            return false;
        }

        return true;
    }

    private void parseDocument(ArrayDesignBuilder arrayDesignBuilder) {
        expect(Token.DOCUMENT_START);
        parseProject(arrayDesignBuilder);
        expect(Token.DOCUMENT_END);
    }

    private void parseProject(ArrayDesignBuilder arrayDesignBuilder) {
        expect(Token.PROJECT_START);

        accept(Token.BY);
        accept(Token.COMPANY);
        accept(Token.DATE);
        accept(Token.ID);
        accept(Token.NAME);
        accept(Token.ORGANIZATION);

        while (Token.END != tokenizer.getCurrentToken()) {
            switch (tokenizer.getCurrentToken()) {

            case BIOSEQUENCE_START:
                parseBiosequence(arrayDesignBuilder);
                break;

            case PRINTING_START:
                parsePrinting();
                break;

            case PATTERN_START:
                parsePattern(arrayDesignBuilder);
                break;

            case OTHER_START:
                parseOther();
                break;

            default:
                throwTokenError();
            }
        }

        expect(Token.END);
    }

    private void parseBiosequence(ArrayDesignBuilder arrayDesignBuilder) {
        expect(Token.BIOSEQUENCE_START);

        accept(Token.ACCESS);
        accept(Token.CHROMOSOME);
        final String controlType = acceptTokenWithStringValue(Token.CONTROL_TYPE, null);
        accept(Token.DESCRIPTION);
        accept(Token.EC_NUMBER);
        accept(Token.MAP_POSITION);
        accept(Token.PRIMARY_NAME);
        accept(Token.SEQUENCEDB);
        final String species = expectTokenWithStringValue(Token.SPECIES);
        accept(Token.TYPE);
        
        BiosequenceBuilder biosequenceBuilder = arrayDesignBuilder.createBiosequenceBuilder(controlType, species);

        while (Token.END != tokenizer.getCurrentToken()) {
            switch (tokenizer.getCurrentToken()) {

            case ACCESSION_START:
                 parseAccession(biosequenceBuilder);
                break;

            case ALIAS_START:
                parseAlias();
                break;

            case OTHER_START:
                parseOther();
                break;

            default:
                throwTokenError();
            }
        }

        expect(Token.END);
        
        biosequenceBuilder.finish();
    }

    private void parseBiosequenceRef(PhysicalProbeBuilder physicalProbeBuilder) {
        expect(Token.BIOSEQUENCE_REF_START);

        final String database = acceptTokenWithStringValue(Token.DATABASE, "");
        final String identifier = expectTokenWithStringValue(Token.IDENTIFIER);
        final String species = expectTokenWithStringValue(Token.SPECIES);
        
        physicalProbeBuilder.setBiosequenceRef(database, species, identifier);

        expect(Token.END);
    }

    private void parsePrinting() {
        int chipCount = 0;

        expect(Token.PRINTING_START);

        accept(Token.DATE);
        accept(Token.PATTERN_NAME);
        accept(Token.PREPARED_AT_SITE);
        accept(Token.PREPARED_BY);
        accept(Token.PREPARED_BY_ORG);
        accept(Token.PRINTER);
        accept(Token.RUN_DESCRIPTION);
        accept(Token.TYPE);

        while (Token.END != tokenizer.getCurrentToken()) {
            switch (tokenizer.getCurrentToken()) {

            case CHIP_START:
                chipCount++;
                parseChip();
                break;

            case OTHER_START:
                parseOther();
                break;

            default:
                throwTokenError();
            }
        }

        expect(Token.END);

        if (chipCount == 0) {
            throw new ParseException("Expected at least one \"chip\" element.");
        }
    }

    private void parseChip() {
        expect(Token.CHIP_START);

        expect(Token.BARCODE);
        accept(Token.PREPARED_FOR);
        accept(Token.PREPARED_FOR_ORG);

        while (Token.END != tokenizer.getCurrentToken()) {
            switch (tokenizer.getCurrentToken()) {

            case OTHER_START:
                parseOther();
                break;

            default:
                throwTokenError();
            }
        }

        expect(Token.END);
    }

    private void parsePattern(ArrayDesignBuilder arrayDesignBuilder) {
        int reporterCount = 0;

        expect(Token.PATTERN_START);

        accept(Token.ACCESS);
        accept(Token.DESCRIPTION);
        accept(Token.NAME);
        accept(Token.OWNER);
        accept(Token.SPECIES_DATABASE);
        accept(Token.TYPE);
        accept(Token.TYPE_ID);

        while (Token.END != tokenizer.getCurrentToken()) {
            switch (tokenizer.getCurrentToken()) {

            case GRID_LAYOUT_START:
                parseGridLayout();
                break;

            case REPORTER_START:
                reporterCount++;
                parseReporter(arrayDesignBuilder);
                break;

            case OTHER_START:
                parseOther();
                break;

            default:
                throwTokenError();
            }
        }

        expect(Token.END);

        if (reporterCount == 0) {
            throw new ParseException("Expected at least one \"reporter\" element.");
        }
    }

    /**
     * 
     */
    private void parseGridLayout() {
        expect(Token.GRID_LAYOUT_START);

        accept(Token.FEATURE_COUNT_X);
        accept(Token.FEATURE_COUNT_Y);
        accept(Token.FEATURE_SPACING_X);
        accept(Token.FEATURE_SPACING_Y);

        expect(Token.END);
    }

    private void parseReporter(ArrayDesignBuilder arrayDesignBuilder) {
        featureCount = 0;
        geneCount = 0;

        expect(Token.REPORTER_START);

        parseReportAttributes1();
        final String controlTypeValue = acceptTokenWithStringValue(Token.CONTROL_TYPE, null);
        parseReporterAttributes2();
        final String name = expectTokenWithStringValue(Token.NAME);
        parseReporterAttributes3();

        final ControlType controlType = validateReporterControlType(controlTypeValue, name);

        PhysicalProbeBuilder physicalProbeBuilder = arrayDesignBuilder.findOrCreatePhysicalProbeBuilder(name);
        addPhysicalProbeToProbeGroup(physicalProbeBuilder, controlType);

        parseReporterContents(physicalProbeBuilder);

        expect(Token.END);

        if (featureCount == 0) {
            throw new ParseException("Expected at least one \"feature\" element.");
        }

        if (geneCount > 1) {
            throw new ParseException("Expected no more than one \"gene\" element.");
        }
    }

    private void parseReporterContents(PhysicalProbeBuilder physicalProbeBuilder) {
        while (Token.END != tokenizer.getCurrentToken()) {
            switch (tokenizer.getCurrentToken()) {

            case BIOSEQUENCE_REF_START:
                parseBiosequenceRef(physicalProbeBuilder);
                break;

            case FEATURE_START:
                featureCount++;
                parseFeature(physicalProbeBuilder);
                break;

            case GENE_START:
                geneCount++;
                parseGene(physicalProbeBuilder);
                break;

            case OTHER_START:
                parseOther();
                break;

            default:
                throwTokenError();
            }
        }
    }

    private void addPhysicalProbeToProbeGroup(PhysicalProbeBuilder physicalProbeBuilder,
            final ControlType controlType) {
        switch (controlType) {
        case IGNORE:
            physicalProbeBuilder.addToProbeGroup(IGNORE_CONTROL_GROUP_NAME);
            break;
        case POSITIVE:
            physicalProbeBuilder.addToProbeGroup(POSITIVE_CONTROLS_CONTROL_GROUP_NAME);
            break;
        case NEGATIVE:
            physicalProbeBuilder.addToProbeGroup(NEGATIVE_CONTROLS_CONTROL_GROUP_NAME);
            break;
        default:
            // Empty default case.
            break;
        }
    }

    private ControlType validateReporterControlType(final String controlTypeValue, final String name) {
        final ControlType controlType = ControlType.fromString(controlTypeValue);
        final boolean nameIsEmpty = "".equals(name);
        final boolean nameIsEmptyOrNA = nameIsEmpty || name.toLowerCase(Locale.ENGLISH).startsWith("na");

        if (!nameIsEmptyOrNA && controlType == ControlType.IGNORE) {
            throw new ParseException("Reporters with controlType=\"ignore\", should not have names");
        } else if (nameIsEmpty && controlType != ControlType.IGNORE) {
            throw new ParseException("Reporters with controlType<>\"ignore\", should have names");
        }
        return controlType;
    }

    private void parseReportAttributes1() {
        accept(Token.ACCESSION);
        accept(Token.ACTIVE_SEQUENCE);
    }

    private void parseReporterAttributes2() {
        accept(Token.DELETION);
        accept(Token.DESCRIPTION);
        accept(Token.FAIL_TYPE);
        accept(Token.LINKER_SEQUENCE);
        accept(Token.MISMATCH_COUNT);
    }

    private void parseReporterAttributes3() {
        accept(Token.PRIMER1_SEQUENCE);
        accept(Token.PRIMER2_SEQUENCE);
        accept(Token.START_COORD);

        accept(Token.SYSTEMATIC_NAME);
    }

    private void parseFeature(PhysicalProbeBuilder physicalProbeBuilder) {
        positionCount = 0;
        penCount = 0;

        expect(Token.FEATURE_START);

        accept(Token.CTRL_FOR_FEAT_NUM);
        int featureNumber = acceptTokenWithIntValue(Token.NUMBER, 0);

        FeatureBuilder featureBuilder = physicalProbeBuilder.createFeatureBuilder(featureNumber);

        parseFeatureContents(featureBuilder);

        expect(Token.END);

        if (positionCount != 1) {
            throw new ParseException("Expected no more than one \"position\" element.");
        }

        if (penCount > 1) {
            throw new ParseException("Expected no more than one \"pen\" element.");
        }
    }

    private void parseFeatureContents(FeatureBuilder featureBuilder) {
        while (Token.END != tokenizer.getCurrentToken()) {
            switch (tokenizer.getCurrentToken()) {

            case POSITION_START:
                positionCount++;
                parsePosition(featureBuilder);
                break;

            case PEN_START:
                penCount++;
                parsePen();
                break;

            case OTHER_START:
                parseOther();
                break;

            default:
                throwTokenError();
           }
        }
    }

    private void parseGene(PhysicalProbeBuilder physicalProbeBuilder) {
        expect(Token.GENE_START);

        accept(Token.CHROMOSOME);
        accept(Token.DESCRIPTION);
        accept(Token.MAP_POSITION);

        String name = expectTokenWithStringValue(Token.PRIMARY_NAME);

        accept(Token.SPECIES);
        accept(Token.SYSTEMATIC_NAME);

        GeneBuilder geneBuilder = physicalProbeBuilder.createGeneBuilder(name);
 
        while (Token.END != tokenizer.getCurrentToken()) {
            switch (tokenizer.getCurrentToken()) {

            case ACCESSION_START:
                parseAccession(geneBuilder);
                break;

            case ALIAS_START:
                parseAlias();
                break;

            case OTHER_START:
                parseProbeMappingsOrOther(geneBuilder);
                break;

            default:
                throwTokenError();
            }
        }

        expect(Token.END);
    }

    private void parsePosition(FeatureBuilder featureBuilder) {
        expect(Token.POSITION_START);

        String units = expectTokenWithStringValue(Token.UNITS);
        double x = expectTokenWithDoubleValue(Token.X);
        double y = expectTokenWithDoubleValue(Token.Y);

        while (Token.END != tokenizer.getCurrentToken()) {
            switch (tokenizer.getCurrentToken()) {

            case OTHER_START:
                parseOther();
                break;

            default:
                throwTokenError();
            }
        }

        featureBuilder.setCoordinates(x, y, units);

        expect(Token.END);
    }

    private void parsePen() {
        expect(Token.PEN_START);
        expect(Token.UNITS);
        expect(Token.X);
        expect(Token.Y);

        while (Token.END != tokenizer.getCurrentToken()) {
            switch (tokenizer.getCurrentToken()) {

            case OTHER_START:
                parseOther();
                break;

            default:
                throwTokenError();
            }
        }

        expect(Token.END);
    }

    private void parseAccession(AccessionBuilder accessionBuilder) {
        expect(Token.ACCESSION_START);
        
        final String database = expectTokenWithStringValue(Token.DATABASE);       
        final String id = parseAccessionName();

        while (Token.END != tokenizer.getCurrentToken()) {
            switch (tokenizer.getCurrentToken()) {

            case OTHER_START:
                parseOther();
                break;

            default:
                throwTokenError();
            }
        }

        if ("agp".equalsIgnoreCase(database)) {
            accessionBuilder.agpAccession(id);
        } else if ("gb".equalsIgnoreCase(database)) {
            accessionBuilder.createNewGBAccession(id);
        } else if ("ens".equalsIgnoreCase(database)) {
            accessionBuilder.createNewEnsemblAccession(id);
        } else if ("ref".equalsIgnoreCase(database)) {
            accessionBuilder.createNewRefSeqAccession(id);
        } else if ("thc".equalsIgnoreCase(database)) {
            accessionBuilder.createNewTHCAccession(id);
        } else if ("mir".equalsIgnoreCase(database)) {
            accessionBuilder.createNewMirAccession(id);
        }

        expect(Token.END);
    }

    private String parseAccessionName() {      
        final String defaultValue = "default value 9238BE1D-CA35-40F7-631264F9DCFB7ABA";
        String id = acceptTokenWithStringValue(Token.ID, defaultValue);
        
        if (defaultValue.equals(id)) {
            id = acceptTokenWithStringValue(Token.IDENTIFIER, defaultValue);
        }
        
        if (defaultValue.equals(id)) {
            throw new ParseException("Missing accession.");
        }
        
        return id;
    }

    private void parseAlias() {
        expect(Token.ALIAS_START);
        expect(Token.NAME);

        while (Token.END != tokenizer.getCurrentToken()) {
            switch (tokenizer.getCurrentToken()) {

            case OTHER_START:
                parseOther();
                break;

            default:
                throwTokenError();
            }
        }

        expect(Token.END);
    }

    private void parseProbeMappingsOrOther(GeneBuilder geneBuilder) {
        expect(Token.OTHER_START);
        String name = expectTokenWithStringValue(Token.NAME);

        if (!"probe_mappings".equalsIgnoreCase(name)) {
            parseOtherContents();
        } else {
            String value = expectTokenWithStringValue(Token.VALUE);
            Matcher m = chromosomePattern.matcher(value);
            if (!m.matches()) {
                throw new ParseException(String.format("Unrecognized chromosome name \"%s\".", value));
            }

            final String chromosome = m.group(1);
            final long startPosition = Long.parseLong(m.group(2));
            final long endPosition = Long.parseLong(m.group(3));

            geneBuilder.setChromosomeLocation(chromosome, startPosition, endPosition);
            expect(Token.END);
        }
    }

    private void parseOther() {
        expect(Token.OTHER_START);
        expect(Token.NAME);
        parseOtherContents();
    }

    private void parseOtherContents() {
        expect(Token.VALUE);

        while (Token.END != tokenizer.getCurrentToken()) {
            switch (tokenizer.getCurrentToken()) {

            case OTHER_START:
                parseOther();
                break;

            default:
                throwTokenError();
            }
        }

        expect(Token.END);
    }

    private void throwTokenError() {
        throw new ParseException(String.format("Unexpected token \"%s\".", tokenizer.getCurrentToken().toString()));
    }

    private void accept(Token token) {
        if (tokenizer.getCurrentToken() == token) {
            tokenizer.advance();
        }
    }

    private int acceptTokenWithIntValue(Token token, int defaultValue) {
        if (tokenizer.getCurrentToken() != token) {
            return defaultValue;
        }

        int value = tokenizer.getIntValue();
        tokenizer.advance();
        return value;
    }

    private String acceptTokenWithStringValue(Token token, String defaultValue) {
        if (tokenizer.getCurrentToken() != token) {
            return defaultValue;
        }

        String value = tokenizer.getStringValue();
        tokenizer.advance();
        return value;
    }

    private void expect(Token token) {
        if (tokenizer.getCurrentToken() != token) {
            throwTokenError(token);
        }

        tokenizer.advance();
    }

    private double expectTokenWithDoubleValue(Token token) {
        if (tokenizer.getCurrentToken() != token) {
            throwTokenError(token);
        }

        double value = tokenizer.getDoubleValue();
        tokenizer.advance();
        return value;
    }

    private String expectTokenWithStringValue(Token token) {
        if (tokenizer.getCurrentToken() != token) {
            throwTokenError(token);
        }

        String value = tokenizer.getStringValue();
        tokenizer.advance();
        return value;
    }

    private void throwTokenError(Token token) {
        throw new ParseException(
                String.format("Unexepected token \"%s\".  Expected \"%s\"",
                        tokenizer.getCurrentToken(), token.toString()));
    }

    /***
     * Control types for probes in GELM files.
     * 
     * @author jscott
     */
    private enum ControlType {
        NONE, POSITIVE, NEGATIVE, IGNORE;

        static ControlType fromString(String s) {
            if ("ignore".equalsIgnoreCase(s)) {
                return IGNORE;
            }
            if ("pos".equalsIgnoreCase(s)) {
                return POSITIVE;
            }
            if ("neg".equalsIgnoreCase(s)) {
                return NEGATIVE;
            }
            return NONE;
        }
    }

    /***
     * Exception thrown on unexpected exceptions during parsing.
     * 
     * @author jscott
     * 
     */
    private class ParseException extends RuntimeException {
        private static final long serialVersionUID = 1L;
        
       /**
         * @param message the exception message.
         */
        public ParseException(String message) {
            super(message);
        }
    }
}
