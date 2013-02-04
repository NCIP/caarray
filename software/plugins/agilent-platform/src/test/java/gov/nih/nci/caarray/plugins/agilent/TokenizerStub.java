//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package gov.nih.nci.caarray.plugins.agilent;

import gov.nih.nci.caarray.plugins.agilent.AgilentGELMToken;
import gov.nih.nci.caarray.plugins.agilent.XMLTokenizer;

import java.util.LinkedList;
import java.util.Queue;

class TokenizerStub implements XMLTokenizer<AgilentGELMToken.Token> {

    private class TokenValue {
        private AgilentGELMToken.Token token;
        private Object value;

        TokenValue(AgilentGELMToken.Token token) {
            this.token = token;
        }

        TokenValue(AgilentGELMToken.Token token, Object value) {
            this(token);
            this.value = value;
        }

        AgilentGELMToken.Token getToken() {
            return token;
        }

        Object getValue() {
            return value;
        }
    }
    private Queue<TokenValue> tokens = new LinkedList<TokenValue>();

    public AgilentGELMToken.Token getCurrentToken() {
        return tokens.peek().getToken();
    }

    public void advance() {
        tokens.remove();
    }

    public String getStringValue() {
        Object value = tokens.peek().getValue();
        return (String) value;
    }

    public double getDoubleValue() {
        Object value = tokens.peek().getValue();
        return (Double) value;
    }

    public int getIntValue() {
        Object value = tokens.peek().getValue();
        return (Integer) value;
    }

    public void close() {
    }

    void add(AgilentGELMToken.Token token) {
        tokens.add(new TokenValue(token));
    }

    void add(AgilentGELMToken.Token token, Object value) {
        tokens.add(new TokenValue(token, value));
    }

    void repeat(int count, AgilentGELMToken.Token token) {
        for (int i = 0; i < count; i++)
            add(token);
    }
}
