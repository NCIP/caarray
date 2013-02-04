//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================

package gov.nih.nci.caarray.domain.data;

import gov.nih.nci.caarray.util.CaArrayUtils;

import java.io.Serializable;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Transient;

import com.google.common.base.Preconditions;

/**
 * Contains a column of <code>short</code> values.
 */
@Entity
@DiscriminatorValue("SHORT")
public class ShortColumn extends AbstractDataColumn {
    private static final long serialVersionUID = 1L;

    private short[] values;

    /**
     * @return the values
     */
    @Transient
    public short[] getValues() {
        Preconditions.checkNotNull(values, ERROR_NOT_INITIALIZED);
        return this.values;
    }

    /**
     * @param values the values to set
     */
    public void setValues(short[] values) {
        this.values = values;
    }

    /**
     * @return the values of this column, in a space-separated representation, where each value is encoded using the
     *         literal representation of the xs:short type defined in the XML Schema standard.
     */
    @Override
    @Transient
    public String getValuesAsString() {
        return CaArrayUtils.join(getValues(), SEPARATOR);
    }

    /**
     * Set values from a String representation. The string should contain a list of space-separated values, with each
     * value encoded using the literal representation of the xs:short type defined in XML Schema.
     * 
     * @param s the string containing the space-separated values
     */
    @Override
    public void setValuesAsString(String s) {
        setValues(CaArrayUtils.splitIntoShorts(s, SEPARATOR));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setValuesFromArray(Serializable array) {
        Preconditions.checkArgument(array instanceof short[], "Invalid array value passed");
        this.values = (short[]) array;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transient
    public Serializable getValuesAsArray() {
        Preconditions.checkNotNull(values, ERROR_NOT_INITIALIZED);
        return this.values;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void initializeArray(int numberOfValues) {
        setValues(new short[numberOfValues]);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transient
    public boolean isLoaded() {
        return values != null;
    }
}
