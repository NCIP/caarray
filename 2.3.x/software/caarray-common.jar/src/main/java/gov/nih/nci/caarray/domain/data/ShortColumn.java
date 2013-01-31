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
 * Contains a column of <code>short</code> values.
 */
@Entity
@DiscriminatorValue("SHORT")
public class ShortColumn extends AbstractDataColumn {

    private static final long serialVersionUID = 1L;

    /**
     * @return the values
     */
    @Transient
    public short[] getValues() {
        return (short[]) getValuesAsSerializable();
    }

    /**
     * @param values the values to set
     */
    public void setValues(short[] values) {
        setSerializableValues(values);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void initializeArray(int numberOfValues) {
        setValues(new short[numberOfValues]);
    }

}
