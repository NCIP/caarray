//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package gov.nih.nci.caarray.plugins.agilent;

import java.io.Reader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;

import org.codehaus.stax2.XMLInputFactory2;
import org.codehaus.stax2.XMLStreamReader2;

import com.ctc.wstx.exc.WstxEOFException;

/**
 * Reads an XML file and reduces it to a stream of tokens. The token stream can be further processed in a parser. The
 * rationale is to abstract away the world of XML. An implementation of this class can be unit tested in isolation from
 * parsing code. The classes implementing the follow-on parser can be unit tested in terms of tokens instead in the more
 * cumbersome terms of XML elements and attributes.
 *
 * @author jscott
 *
 * @param <TokenT> the enumeration of tokens to be used
 */
abstract class AbstractXMLTokenizer<TokenT extends Enum<TokenT>> implements XMLTokenizer<TokenT> {
    /**
     * Indicates whether or not a stream is at the end of input.
     */
    private enum FileStatus {
        OKAY, END_OF_FILE
    }

    private TokenT currentToken;
    private boolean attribtesArePresentForCurrentElement = false;
    private XMLStreamReader2 reader;
    private boolean currentTokenIsAttribute;
    private int currentAttributeIndex;
    private final List<AttributeNameAndIndexWrapper> attributesAndIndexesList =
        new ArrayList<AttributeNameAndIndexWrapper>();
    private Iterator<AttributeNameAndIndexWrapper> attributeIterator = attributesAndIndexesList.iterator();

    private static final String END_OF_ATTRIBUTES_TOKEN_NAME = AbstractXMLTokenizer.class.getName()
        + ".END_OF_ATTRIBUTES_TOKEN_NAME";
    private static final int END_OF_ATTRIBUTES_INDEX_PLACEHOLDER = -1;

    /**
     * @param inputReader a reader on the XML source to tokenized
     */
    AbstractXMLTokenizer(Reader inputReader) {
        XMLInputFactory2 inputFactory = (XMLInputFactory2) XMLInputFactory2.newInstance();
        inputFactory.configureForConvenience();
        inputFactory.setProperty(XMLInputFactory2.SUPPORT_DTD, false);
        inputFactory.setProperty(XMLInputFactory2.IS_COALESCING, false);

        try {
            reader = (XMLStreamReader2) inputFactory.createXMLStreamReader(inputReader);
        } catch (XMLStreamException e) {
            throw new AgilentParseException("Cannot create StAX reader: " + e.getMessage(), e);
        }

        currentToken = getNextToken();
    }

    /**
     * {@inheritDoc}
     */
    public void close() {
        if (null != reader) {
            try {
                reader.close();
            } catch (XMLStreamException e) {
                throw new AgilentParseException("Cannot close StAX reader: " + e.getMessage(), e);
            }
        }
    }

    private TokenT getNextToken() {
        currentTokenIsAttribute = attributeIterator.hasNext();
        if (currentTokenIsAttribute) {
            currentAttributeIndex = attributeIterator.next().getAttributeIndex();
            if (END_OF_ATTRIBUTES_INDEX_PLACEHOLDER == currentAttributeIndex) {
                return getEndAttributesToken();
            } else {
                String attributeName = reader.getAttributeLocalName(currentAttributeIndex);
                return findAttributeToken(attributeName);
            }
        }
        while (true) {
            TokenT token = getToken();
            if (null != token) {
                return token;
            }

            FileStatus status = tryToAdvance();

            if (FileStatus.END_OF_FILE == status) {
                return getEOFToken();
            }
        }
    }

    @SuppressWarnings("CyclomaticComplexity")
    private TokenT getToken() {
        TokenT token;

        switch (reader.getEventType()) {
        case XMLEvent.START_DOCUMENT:
            token = getDocumentStartToken();
            break;

        case XMLEvent.START_ELEMENT:
            setUpAttributes();
            token = findStartToken(reader.getLocalName());
            break;

        case XMLEvent.END_ELEMENT:
            token = getEndToken();
            break;

        case XMLEvent.END_DOCUMENT:
            token = getDocumentEndToken();
            break;

        case XMLEvent.ATTRIBUTE:
        case XMLEvent.CDATA:
        case XMLEvent.ENTITY_DECLARATION:
        case XMLEvent.ENTITY_REFERENCE:
        case XMLEvent.NAMESPACE:
        case XMLEvent.NOTATION_DECLARATION:
        case XMLEvent.SPACE:
            token = getErrorToken();
            break;

        case XMLEvent.CHARACTERS:
        case XMLEvent.COMMENT:
        case XMLEvent.DTD:
        case XMLEvent.PROCESSING_INSTRUCTION:
            token = null;
            break;

        default:
            throw new AgilentParseException("Encountered an unexpected case in switch statement.");
        }

        return token;
    }

    private void setUpAttributes() {
        int numberOfAttributes = reader.getAttributeCount();
        if (numberOfAttributes > 0) {
            attribtesArePresentForCurrentElement = true;
        } else {
            attribtesArePresentForCurrentElement = false;
        }
        attributesAndIndexesList.clear();
        for (int i = 0; i < numberOfAttributes; i++) {
            attributesAndIndexesList.add(new AttributeNameAndIndexWrapper(reader.getAttributeLocalName(i), i));
        }
        attributesAndIndexesList.add(
                new AttributeNameAndIndexWrapper(END_OF_ATTRIBUTES_TOKEN_NAME, END_OF_ATTRIBUTES_INDEX_PLACEHOLDER));
        attributeIterator = attributesAndIndexesList.iterator();
    }

    /**
     * @return the token to return first, at the beginning of the XML document
     */
    protected abstract TokenT getDocumentStartToken();

    /**
     * @return the token to return at the end of the XML document
     */
    protected abstract TokenT getDocumentEndToken();

    /**
     * Finds the token to return at the start of a given XML element.
     *
     * @param elementName the name of the XML element
     * @return the token
     */
    protected abstract TokenT findStartToken(String elementName);

    /**
     * @return the token to return at the end of each XML element
     */
    protected abstract TokenT getEndToken();

    /**
     * Finds the token associated with a given XML attribute.
     *
     * @param attributeName the name of the XML attribute
     * @return the token
     */
    protected abstract TokenT findAttributeToken(String attributeName);

    /**
     * @return the token to return when an error occurs
     */
    protected abstract TokenT getErrorToken();

    /**
     * @return the token to return when there is no more XML input
     */
    protected abstract TokenT getEOFToken();

    /**
     * @return the token to return when there is are no more attributes for this element
     */
    protected abstract TokenT getEndAttributesToken();

    /**
     * @return {@inheritDoc}
     */
    public TokenT getCurrentToken() {
        return currentToken;
    }

    /**
     * {@inheritDoc}
     */
    public void advance() {
        if (attributeIterator.hasNext()) {
            currentToken = getNextToken();
        } else {
            switch (tryToAdvance()) {
            case OKAY:
                currentToken = getNextToken();
                break;
            case END_OF_FILE:
                currentToken = getEOFToken();
                break;
            default:
                throw new AgilentParseException(
                        "Encountered an unexpected case in switch statement while advancing.");
            }
        }
    }

    /**
     * {@inheritDoc}
     *
     * @return {@inheritDoc}
     */
    public String getStringValue() {
        try {
            if (currentTokenIsAttribute) {
                return reader.getAttributeValue(currentAttributeIndex);
            } else {
                return reader.getElementText();
            }
        } catch (XMLStreamException e) {
            throw new AgilentParseException("StAX library error", e);
        }
    }

    /**
     * {@inheritDoc}
     *
     * @return {@inheritDoc}
     */
    public double getDoubleValue() {
        try {
            Double val = Double.parseDouble(getStringValue());
            return val.doubleValue();
        } catch (NumberFormatException e) {
            throw new AgilentParseException("Parsing Error: Unable to parse the value to a double", e);
        }
    }

    /**
     * {@inheritDoc}
     *
     * @return {@inheritDoc}
     */
    public int getIntValue() {
        try {
            Integer val = Integer.parseInt(getStringValue());
            return val.intValue();
        } catch (NumberFormatException e) {
            throw new AgilentParseException("Parsing Error: Unable to parse the value to an int", e);
        }
    }

    private FileStatus tryToAdvance() {
        try {
            if (reader.hasNext()) {
                reader.next();
                return FileStatus.OKAY;
            }

            return FileStatus.END_OF_FILE;
        } catch (WstxEOFException e) {
            return FileStatus.END_OF_FILE;
        } catch (XMLStreamException e) {
            throw new AgilentParseException("StAX library error", e);
        }
    }

    /**
     * wraps the name and index of an xml attribute.
     * @author dharley
     *
     */
    private static class AttributeNameAndIndexWrapper {

        private final String attributeName;
        private final int attributeIndex;

        AttributeNameAndIndexWrapper(String attributeName, int attributeIndex) {
            this.attributeName = attributeName;
            this.attributeIndex = attributeIndex;
        }

        String getAttributeName() {
            return attributeName;
        }

        int getAttributeIndex() {
            return attributeIndex;
        }
    }
}
