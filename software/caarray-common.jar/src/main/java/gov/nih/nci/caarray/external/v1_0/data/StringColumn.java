//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package gov.nih.nci.caarray.external.v1_0.data;

import java.io.Serializable;

import gov.nih.nci.caarray.util.CaArrayUtils;

/**
 * A StringColumn represents a data column with string values.
 * 
 * @author dkokotov
 */
@SuppressWarnings({"PMD.MethodReturnsInternalArray", "PMD.ArrayIsStoredDirectly" })
public final class StringColumn extends AbstractDataColumn {
    private static final long serialVersionUID = 1L;

    private String[] values;

    /**
     * @return the values
     */
    public String[] getValues() {
        return values;
    }

    /**
     * @param values the values to set
     */
    public void setValues(String[] values) {
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
        this.values = (String[]) serValues;        
    }
    
    /**
     * @return the values of this column as a comma-separated string. Each value will be encoded in this string
     * by escaping any commas in the value with a backslash.
     */
    public String getValuesAsString() {
        return CaArrayUtils.joinAsCsv(this.values);
    }
 
    /**
     * Sets the values of this column from a string, which must contain a comma-separated list of strings. Each
     * such string will be unescaped by converting any backslash-comma combinations back to commas.
     * @param s string containing a comma-separated list of strings.
     */
    public void setValuesAsString(String s) {
        this.values = CaArrayUtils.splitFromCsv(s);
    }
}
