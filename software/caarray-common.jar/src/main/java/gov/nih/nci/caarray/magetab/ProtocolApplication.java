//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.magetab;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * The use of a protocol with the requisite parameters.
 */
public final class ProtocolApplication implements Serializable {

    private static final long serialVersionUID = -3273047341277478014L;

    private Protocol protocol;
    private final List<ParameterValue> parameterValues = new ArrayList<ParameterValue>();
    private String performer;
    private Date date;

    /**
     * @return the date
     */
    public Date getDate() {
        return date;
    }

    /**
     * @param date the date to set
     */
    public void setDate(Date date) {
        this.date = date;
    }

    /**
     * @return the performer
     */
    public String getPerformer() {
        return performer;
    }

    /**
     * @param performer the performer to set
     */
    public void setPerformer(String performer) {
        this.performer = performer;
    }

    /**
     * @return the protocol
     */
    public Protocol getProtocol() {
        return protocol;
    }

    /**
     * @param protocol the protocol to set
     */
    public void setProtocol(Protocol protocol) {
        this.protocol = protocol;
    }

    /**
     * @return the parameterValues
     */
    public List<ParameterValue> getParameterValues() {
        return parameterValues;
    }

}
