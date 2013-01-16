//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.plugins.agilent;

import gov.nih.nci.caarray.plugins.agilent.AgilentGELMToken.Token;

import java.util.ArrayList;
import java.util.List;
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
@SuppressWarnings({ "PMD.ExcessiveClassLength", "PMD.CyclomaticComplexity", "PMD.ExcessiveMethodLength",
    "PMD.NPathComplexity" })
class AgilentGELMParser implements FeatureCountPublisher {
    private static final String NEGATIVE_CONTROLS_CONTROL_GROUP_NAME = "negative controls";
    private static final String POSITIVE_CONTROLS_CONTROL_GROUP_NAME = "positive controls";
    private static final String IGNORE_CONTROL_GROUP_NAME = "ignore";
    private static final String PROBE_MAPPINGS = "probe_mappings";

    private final XMLTokenizer<Token> tokenizer;
    private final Pattern chromosomePattern = Pattern.compile("chr(.+):(\\d+)-(\\d+)(?:\\|.+){0,1}");
    private final List<FeatureCountListener> featureCountListeners;
    private int featureCount;
    private int geneCount;
    private int positionCount;
    private int penCount;

    /**
     * @param tokenizer provides the stream of tokens to be parsed.
     */
    AgilentGELMParser(XMLTokenizer<Token> tokenizer) {
        this.tokenizer = tokenizer;
        this.featureCountListeners = new ArrayList<FeatureCountListener>();
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
            arrayDesignBuilder.processChunk(true);
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
        
        while (tokenizer.getCurrentToken() != Token.ATTRIBUTE_END) {
            tokenizer.advance(); // none of these attributes are used
        }
        tokenizer.advance();

        while (Token.ELEMENT_END != tokenizer.getCurrentToken()) {
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

        expect(Token.ELEMENT_END);
    }

    private void parseBiosequence(ArrayDesignBuilder arrayDesignBuilder) {
        expect(Token.BIOSEQUENCE_START);

        String controlType = null;
        String species = null;
        int speciesCounter = 0;
        while (tokenizer.getCurrentToken() != Token.ATTRIBUTE_END) {
            switch (tokenizer.getCurrentToken()) {

            case CONTROL_TYPE:
                controlType = acceptTokenWithStringValue(Token.CONTROL_TYPE, null);
                break;

            case SPECIES:
                species = expectTokenWithStringValue(Token.SPECIES);
                speciesCounter++;
                break;

            default:
                tokenizer.advance();
            }
        }
        tokenizer.advance();
        
        if (1 != speciesCounter) {
            throw new ParseException("Expected exactly one \"species\" attribute.");
        }
        
        BiosequenceBuilder biosequenceBuilder = arrayDesignBuilder.createBiosequenceBuilder(controlType, species);

        while (Token.ELEMENT_END != tokenizer.getCurrentToken()) {
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

        expect(Token.ELEMENT_END);
        
        biosequenceBuilder.finish();
    }

    private void parseBiosequenceRef(PhysicalProbeBuilder physicalProbeBuilder) {
        expect(Token.BIOSEQUENCE_REF_START);
        

        String database = null;
        String identifier = null;
        int identifierCounter = 0;
        String species = null;
        int speciesCounter = 0;
        
        while (tokenizer.getCurrentToken() != Token.ATTRIBUTE_END) {
            switch (tokenizer.getCurrentToken()) {

            case DATABASE:
                database = acceptTokenWithStringValue(Token.DATABASE, "");
                break;

            case IDENTIFIER:
                identifier = expectTokenWithStringValue(Token.IDENTIFIER);
                identifierCounter++;
                break;

            case SPECIES:
                species = expectTokenWithStringValue(Token.SPECIES);
                speciesCounter++;
                break;

            default:
                tokenizer.advance();
            }
        }
        tokenizer.advance();
        
        if (1 != identifierCounter || 1 != speciesCounter) {
            throw new ParseException("Expected exactly one \"identifier\" and one \"species\" attribute.");
        }
        
        physicalProbeBuilder.setBiosequenceRef(database, species, identifier);

        expect(Token.ELEMENT_END);
    }

    private void parsePrinting() {
        int chipCount = 0;

        expect(Token.PRINTING_START);
        
        while (tokenizer.getCurrentToken() != Token.ATTRIBUTE_END) {
            tokenizer.advance(); // none of these attributes are used
        }
        tokenizer.advance();

        while (Token.ELEMENT_END != tokenizer.getCurrentToken()) {
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

        expect(Token.ELEMENT_END);

        if (chipCount == 0) {
            throw new ParseException("Expected at least one \"chip\" element.");
        }
    }

    private void parseChip() {
        expect(Token.CHIP_START);
        
        int barcodeCount = 0;
                
        while (tokenizer.getCurrentToken() != Token.ATTRIBUTE_END) {
            switch (tokenizer.getCurrentToken()) {

            case BARCODE:
                barcodeCount++;
                tokenizer.advance();
                break;

            default:
                tokenizer.advance();
            }
        }
        tokenizer.advance();

        while (Token.ELEMENT_END != tokenizer.getCurrentToken()) {
            switch (tokenizer.getCurrentToken()) {

            case OTHER_START:
                parseOther();
                break;

            default:
                throwTokenError();
            }
        }
        
        if (barcodeCount != 1) {
            throw new ParseException("There must be one \"barcode\" element.");
        }

        expect(Token.ELEMENT_END);
    }

    private void parsePattern(ArrayDesignBuilder arrayDesignBuilder) {
        int reporterCount = 0;

        expect(Token.PATTERN_START);
        
        while (tokenizer.getCurrentToken() != Token.ATTRIBUTE_END) {
            tokenizer.advance(); // none of these attributes are used
        }
        tokenizer.advance();

        while (Token.ELEMENT_END != tokenizer.getCurrentToken()) {
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

        expect(Token.ELEMENT_END);

        if (reporterCount == 0) {
            throw new ParseException("Expected at least one \"reporter\" element.");
        }
    }

    /**
     * 
     */
    private void parseGridLayout() {
        expect(Token.GRID_LAYOUT_START);
        
        while (tokenizer.getCurrentToken() != Token.ATTRIBUTE_END) {
            tokenizer.advance(); // none of these attributes are used
        }
        tokenizer.advance();
        
        expect(Token.ELEMENT_END);
    }

    private void parseReporter(ArrayDesignBuilder arrayDesignBuilder) {
        featureCount = 0;
        geneCount = 0;

        expect(Token.REPORTER_START);
        
        String controlTypeValue = null;
        String name = null;
        int nameCount = 0;
        
        while (tokenizer.getCurrentToken() != Token.ATTRIBUTE_END) {
            switch (tokenizer.getCurrentToken()) {

            case CONTROL_TYPE:
                controlTypeValue = acceptTokenWithStringValue(Token.CONTROL_TYPE, null);
                break;

            case NAME:
                name = expectTokenWithStringValue(Token.NAME);
                nameCount++;
                break;

            default:
                tokenizer.advance();
            }
        }
        tokenizer.advance();
        
        if ((null == name) || (nameCount != 1)) {
            throw new ParseException("Expected exactly one \"name\" attribute.");
        }

        final ControlType controlType = validateReporterControlType(controlTypeValue, name);

        PhysicalProbeBuilder physicalProbeBuilder = arrayDesignBuilder.findOrCreatePhysicalProbeBuilder(name);
        addPhysicalProbeToProbeGroup(physicalProbeBuilder, controlType);

        parseReporterContents(physicalProbeBuilder);

        expect(Token.ELEMENT_END);
        
        arrayDesignBuilder.processChunk(false);

        if (featureCount == 0) {
            throw new ParseException("Expected at least one \"feature\" element.");
        }

        if (geneCount > 1) {
            throw new ParseException("Expected no more than one \"gene\" element.");
        }
    }
    
    private void notifyListeners() {
        for (FeatureCountListener featureCountListener : featureCountListeners) {
            featureCountListener.incrementFeatureCount();
        }
    }

    private void parseReporterContents(PhysicalProbeBuilder physicalProbeBuilder) {
        while (Token.ELEMENT_END != tokenizer.getCurrentToken()) {
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

    private void parseFeature(PhysicalProbeBuilder physicalProbeBuilder) {
        positionCount = 0;
        penCount = 0;

        expect(Token.FEATURE_START);
        
        int featureNumber = Integer.MIN_VALUE;
        
        while (tokenizer.getCurrentToken() != Token.ATTRIBUTE_END) {
            switch (tokenizer.getCurrentToken()) {

            case NUMBER:
                featureNumber = acceptTokenWithIntValue(Token.NUMBER, 0);
                break;

            default:
                tokenizer.advance();
            }
        }
        tokenizer.advance();

        FeatureBuilder featureBuilder = physicalProbeBuilder.createFeatureBuilder(featureNumber);

        parseFeatureContents(featureBuilder);

        expect(Token.ELEMENT_END);
        
        notifyListeners();

        if (positionCount != 1) {
            throw new ParseException("Expected no more than one \"position\" element.");
        }

        if (penCount > 1) {
            throw new ParseException("Expected no more than one \"pen\" element.");
        }
    }

    private void parseFeatureContents(FeatureBuilder featureBuilder) {
        while (Token.ELEMENT_END != tokenizer.getCurrentToken()) {
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
        
        String name = null;
        int primaryNameCounter = 0;
        
        while (tokenizer.getCurrentToken() != Token.ATTRIBUTE_END) {
            switch (tokenizer.getCurrentToken()) {

            case PRIMARY_NAME:
                name = expectTokenWithStringValue(Token.PRIMARY_NAME);
                primaryNameCounter++;
                break;

            default:
                tokenizer.advance();
            }
        }
        tokenizer.advance();
        
        if (1 != primaryNameCounter) {
            throw new ParseException("Expected exactly one \"primary_name\" attribute.");
        }

        GeneBuilder geneBuilder = physicalProbeBuilder.createGeneBuilder(name);
 
        while (Token.ELEMENT_END != tokenizer.getCurrentToken()) {
            switch (tokenizer.getCurrentToken()) {

            case ACCESSION_START:
                parseAccession(geneBuilder);
                break;

            case ALIAS_START:
                parseAlias();
                break;

            case OTHER_START:
                parseOther(geneBuilder);
                break;

            default:
                throwTokenError();
            }
        }

        expect(Token.ELEMENT_END);
    }

    private void parsePosition(FeatureBuilder featureBuilder) {
        expect(Token.POSITION_START);
        
        String units = null;
        int unitsCount = 0;
        double x = Double.MIN_VALUE;
        int xCount = 0;
        double y = Double.MIN_VALUE;
        int yCount = 0;
        
        while (tokenizer.getCurrentToken() != Token.ATTRIBUTE_END) {
            switch (tokenizer.getCurrentToken()) {

            case UNITS:
                units = expectTokenWithStringValue(Token.UNITS);
                unitsCount++;
                break;

            case X:
                x = expectTokenWithDoubleValue(Token.X);
                xCount++;
                break;

            case Y:
                y = expectTokenWithDoubleValue(Token.Y);
                yCount++;
                break;

            default:
                tokenizer.advance();
            }
        }
        tokenizer.advance();
        
        if (1 != unitsCount || 1 != xCount || 1 != yCount) {
            throw new ParseException("Expected exactly one \"units\", one \"x\" and one \"y\" attribute.");
        }

        while (Token.ELEMENT_END != tokenizer.getCurrentToken()) {
            switch (tokenizer.getCurrentToken()) {

            case OTHER_START:
                parseOther();
                break;

            default:
                throwTokenError();
            }
        }

        featureBuilder.setCoordinates(x, y, units);

        expect(Token.ELEMENT_END);
    }

    private void parsePen() {
        expect(Token.PEN_START);
        
        int unitsCount = 0;
        int xCount = 0;
        int yCount = 0;
        
        while (tokenizer.getCurrentToken() != Token.ATTRIBUTE_END) {
            switch (tokenizer.getCurrentToken()) {

            case UNITS:
                unitsCount++;
                tokenizer.advance();
                break;

            case X:
                xCount++;
                tokenizer.advance();
                break;

            case Y:
                yCount++;
                tokenizer.advance();
                break;

            default:
                tokenizer.advance();
            }
        }
        tokenizer.advance();
        
        if ((1 != unitsCount) || (1 != xCount) || (1 != yCount)) {
            throw new ParseException("Expected exactly one \"units\", one \"x\" and one \"y\" attribute.");
        }

        while (Token.ELEMENT_END != tokenizer.getCurrentToken()) {
            switch (tokenizer.getCurrentToken()) {

            case OTHER_START:
                parseOther();
                break;

            default:
                throwTokenError();
            }
        }

        expect(Token.ELEMENT_END);
    }

    private void parseAccession(AccessionBuilder accessionBuilder) {
        expect(Token.ACCESSION_START);
        
        String database = null;    
        final String defaultId = "default value 9238BE1D-CA35-40F7-631264F9DCFB7ABA";
        String id = defaultId;
        
        while (tokenizer.getCurrentToken() != Token.ATTRIBUTE_END) {
            switch (tokenizer.getCurrentToken()) {

            case DATABASE:
                database = expectTokenWithStringValue(Token.DATABASE);
                break;

            case ID:
                id = acceptTokenWithStringValue(Token.ID, defaultId);
                break;

            case IDENTIFIER:
                id = acceptTokenWithStringValue(Token.IDENTIFIER, defaultId);
                break;

            default:
                tokenizer.advance();
            }
        }
        tokenizer.advance();
        
        if (defaultId.equals(id)) {
            throw new ParseException("Missing accession.");
        }

        while (Token.ELEMENT_END != tokenizer.getCurrentToken()) {
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

        expect(Token.ELEMENT_END);
    }

    private void parseAlias() {
        expect(Token.ALIAS_START);
        
        int nameCounter = 0;
        
        while (tokenizer.getCurrentToken() != Token.ATTRIBUTE_END) {
            switch (tokenizer.getCurrentToken()) {

            case NAME:
                nameCounter++;
                tokenizer.advance();
                break;

            default:
                tokenizer.advance();
            }
        }
        tokenizer.advance();
        
        if (1 != nameCounter) {
            throw new ParseException("Expected exactly one \"name\" attribute.");
        }

        while (Token.ELEMENT_END != tokenizer.getCurrentToken()) {
            switch (tokenizer.getCurrentToken()) {

            case OTHER_START:
                parseOther();
                break;

            default:
                throwTokenError();
            }
        }

        expect(Token.ELEMENT_END);
    }
    
    private void parseOther() {
        parseOther(null);
    }

    private void parseOther(GeneBuilder geneBuilder) {
        expect(Token.OTHER_START);
        String name = null;
        int nameCounter = 0;
        String value = null;
        int valueCounter = 0;
        while (tokenizer.getCurrentToken() != Token.ATTRIBUTE_END) {
            switch (tokenizer.getCurrentToken()) {

            case NAME:
                name = expectTokenWithStringValue(Token.NAME);
                nameCounter++;
                break;

            case VALUE:
                value = expectTokenWithStringValue(Token.VALUE);
                valueCounter++;
                break;

            default:
                tokenizer.advance();
            }
        }
        tokenizer.advance();
        
        if (1 != nameCounter || 1 != valueCounter) {
            throw new ParseException("Expected exactly one \"name\" and one \"value\" attribute.");
        }

        if (PROBE_MAPPINGS.equalsIgnoreCase(name)) {
            Matcher m = chromosomePattern.matcher(value);
            if (!m.matches()) {
                throw new ParseException(String.format("Unrecognized chromosome name \"%s\".", value));
            }

            final String chromosome = m.group(1);
            final long startPosition = Long.parseLong(m.group(2));
            final long endPosition = Long.parseLong(m.group(3));

            geneBuilder.setChromosomeLocation(chromosome, startPosition, endPosition);
        }
        
        while (Token.ELEMENT_END != tokenizer.getCurrentToken()) {
            switch (tokenizer.getCurrentToken()) {

            case OTHER_START:
                parseOther(geneBuilder);
                break;

            default:
                tokenizer.advance();
            }
        }
        
        expect(Token.ELEMENT_END);
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

    /**
     * {@inheritDoc}
     */
    public void addFeatureCountListener(FeatureCountListener featureCountListener) {
        featureCountListeners.add(featureCountListener);
    }
}
