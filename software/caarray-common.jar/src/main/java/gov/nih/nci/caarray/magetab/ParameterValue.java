//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.magetab;

import gov.nih.nci.caarray.magetab.sdrf.AbstractCommentable;

/**
 * The value supplied for a parameter to a protocol.
 */
public final class ParameterValue extends AbstractCommentable implements Unitable {
    private static final long serialVersionUID = -7063719478977989712L;

    private Parameter parameter;
    private String value;
    private OntologyTerm term;
    private OntologyTerm unit;

    /**
     * @return the parameter
     */
    public Parameter getParameter() {
        return parameter;
    }

    /**
     * @param parameter the parameter to set
     */
    public void setParameter(Parameter parameter) {
        this.parameter = parameter;
    }

    /**
     * @return the unit (optional)
     */
    public OntologyTerm getUnit() {
        return unit;
    }

    /**
     * @param unit the unit to set
     */
    public void setUnit(OntologyTerm unit) {
        this.unit = unit;
    }

    /**
     * @return the explicit value of this parameter. this is used if no term source is specified for
     * a parameter value. otherwise, the value will be contained in the term property
     */
    public String getValue() {
        return value;
    }

    /**
     * @param value the value to set
     */
    public void setValue(String value) {
        this.value = value;
    }

    /**
     * @return the term containing the value of this parameter value
     */
    public OntologyTerm getTerm() {
        return term;
    }

    /**
     * @param term the term to set
     */
    public void setTerm(OntologyTerm term) {
        this.term = term;
    }

}
