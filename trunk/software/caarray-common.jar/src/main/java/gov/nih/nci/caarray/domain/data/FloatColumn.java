//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================

package gov.nih.nci.caarray.domain.data;

import gov.nih.nci.caarray.util.CaArrayUtils;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Transient;

/**
 * Contains a column of <code>float</code> values.
 */
@Entity
@DiscriminatorValue("FLOAT")
public class FloatColumn extends AbstractDataColumn {
    private static final long serialVersionUID = 1L;

    /**
     * @return the values
     */
    @Transient
    public float[] getValues() {
        return (float[]) getValuesAsSerializable();
    }

    /**
     * @param values the values to set
     */
    public void setValues(float[] values) {
        setSerializableValues(values);
    }

    /**
     * @return the values of this column, in a space-separated representation, where each value is 
     * encoded using the literal representation of the xs:float type defined in the XML Schema standard.
     */
    @Transient
    public String getValuesAsString() {
        return CaArrayUtils.join(getValues(), SEPARATOR);
    }
    
    /**
     * Set values from a String representation. The string should contain a list of space-separated
     * values, with each value encoded using the literal representation of the xs:double type defined in XML Schema.
     * @param s the string containing the space-separated values
     */
    public void setValuesAsString(String s) {
        setValues(CaArrayUtils.splitIntoFloats(s, SEPARATOR));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void initializeArray(int numberOfValues) {
        setValues(new float[numberOfValues]);
    }

}
