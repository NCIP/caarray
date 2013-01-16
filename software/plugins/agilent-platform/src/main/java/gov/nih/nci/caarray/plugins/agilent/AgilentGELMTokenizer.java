//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.plugins.agilent;

import gov.nih.nci.caarray.plugins.agilent.AgilentGELMToken.Token;

import java.io.Reader;

/**
 * Implements XMLTokenizer, using AgilentGELMToken.Token as the token enumeration.
 * @author jscott
 *
 */
class AgilentGELMTokenizer extends AbstractXMLTokenizer<Token> {
    /**
     * @param inputReader a reader on the XML source to tokenized
     */
   AgilentGELMTokenizer(Reader inputReader) {
        super(inputReader);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Token getDocumentStartToken() {
        return Token.getDocumentStartToken();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Token getDocumentEndToken() {
        return Token.getDocumentEndToken();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Token getEndToken() {
        return Token.getEndToken();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Token findStartToken(String elementName) {
        return Token.findStartToken(elementName);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Token findAttributeToken(String attributeName) {
        return Token.findAttributeToken(attributeName);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Token getErrorToken() {
        return Token.getErrorToken();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Token getEOFToken() {
        return Token.getEOFToken();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Token getEndAttributesToken() {
        return Token.getEndAttributeEndToken();
    }
}
