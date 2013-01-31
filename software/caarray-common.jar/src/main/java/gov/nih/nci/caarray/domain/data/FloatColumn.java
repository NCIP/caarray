//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================

package gov.nih.nci.caarray.domain.data;

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
     * {@inheritDoc}
     */
    @Override
    public void initializeArray(int numberOfValues) {
        setValues(new float[numberOfValues]);
    }

}
