//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.external.v1_0.data;

import java.io.Serializable;

import gov.nih.nci.caarray.util.CaArrayUtils;


/**
 * A BooleanColumn represents a data column with boolean values.
 * 
 * @author dkokotov
 */
@SuppressWarnings({"PMD.MethodReturnsInternalArray", "PMD.ArrayIsStoredDirectly" })
public final class BooleanColumn extends AbstractDataColumn {
    private static final long serialVersionUID = 1L;

    private boolean[] values;

    /**
     * @return the values
     */
    public boolean[] getValues() {
        return values;
    }

    /**
     * @param values the values to set
     */
    public void setValues(boolean[] values) {
        this.values = values;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override    
    protected Serializable getSerializableValues() {
        return values;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void setSerializableValues(Serializable serValues) {
        this.values = (boolean[]) serValues;        
    }
    
    /**
     * @return the values of this column, in a space-separated representation, where each value is 
     * encoded using the literal representation of the xs:short type defined in the XML Schema standard.
     */
    public String getValuesAsString() {
        return CaArrayUtils.join(this.values, SEPARATOR);
    }
    
    /**
     * Set values from a String representation. The string should contain a list of space-separated
     * values, with each value encoded using the literal representation of the xs:boolean type defined in XML Schema.
     * @param s the string containing the space-separated values
     */
    public void setValuesAsString(String s) {
        this.values = CaArrayUtils.splitIntoBooleans(s, SEPARATOR);
    }
}
