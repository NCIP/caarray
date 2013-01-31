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
 * Contains a column of <code>int</code> values.
 */
@Entity
@DiscriminatorValue("INT")
public class IntegerColumn extends AbstractDataColumn {

    private static final long serialVersionUID = 1L;

    /**
     * @return the values
     */
    @Transient
    public int[] getValues() {
        return (int[]) getValuesAsSerializable();
    }

    /**
     * @param values the values to set
     */
    public void setValues(int[] values) {
        setSerializableValues(values);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void initializeArray(int numberOfValues) {
        setValues(new int[numberOfValues]);
    }

}
