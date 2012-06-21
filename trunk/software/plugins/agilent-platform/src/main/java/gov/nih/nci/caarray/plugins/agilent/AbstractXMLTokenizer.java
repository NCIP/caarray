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
