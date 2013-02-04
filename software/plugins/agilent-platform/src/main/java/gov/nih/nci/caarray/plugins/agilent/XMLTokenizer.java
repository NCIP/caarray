//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package gov.nih.nci.caarray.plugins.agilent;

/**
 * Reads an XML file and reduces it to a stream of tokens.  The token stream can be further processed in a parser.
 * The rationale is to abstract away the world of XML.  An implementation of this interface can be unit tested in
 * isolation from parsing code.  The classes implementing the follow-on parser can be unit tested in terms of tokens
 * instead in the more cumbersome terms of XML elements and attributes.
 * 
 * @author jscott
 *
 * @param <TokenT> the enumeration of tokens to be used
 */
interface XMLTokenizer<TokenT extends Enum<TokenT>> {
    /**
     * @return the token currently at the head of the token stream
     */
    TokenT getCurrentToken();

    /**
     * Moves to the next token in the token stream.
     */
    void advance();

    /**
     * @return the string value associated with the current token.
     */
    String getStringValue();

    /**
     * @return the double value associated with the current token.
     */
    double getDoubleValue();

    /**
     * @return the integer value associated with the current token.
     */
    int getIntValue();
    
    /**
     * Close the tokenizer.
     */
    void close();
}
