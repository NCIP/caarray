//======================================================================================
// Copyright 5AM Solutions Inc, Yale University, Lawrence Berkeley National Laboratory
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/caRPLA/LICENSE for details.
//======================================================================================
package gov.nih.nci.caarray.magetab;

import java.io.Serializable;

/**
 * The value supplied for a parameter to a protocol.
 */

//carpla
//public final class ParameterValue implements Serializable, Unitable {
public  class ParameterValue implements Serializable, Unitable {

	
	private static final long serialVersionUID = -7063719478977989712L;

    private Parameter parameter;
    private String value;
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
     * @return the unit
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
     * @return the value
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

}
