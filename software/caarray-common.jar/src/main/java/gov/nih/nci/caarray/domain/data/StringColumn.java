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
 * Contains a column of <code>String</code> values.
 */
@Entity
@DiscriminatorValue("STRING")
public class StringColumn extends AbstractDataColumn {
    private static final long serialVersionUID = 1L;

    private String[] values;

    /**
     * @return the values
     */
    @Transient
    public String[] getValues() {
        Preconditions.checkNotNull(values, ERROR_NOT_INITIALIZED);
        return this.values;
    }

    /**
     * @param values the values to set
     */
    public void setValues(String[] values) {
        this.values = values;
    }

    /**
     * @return the values of this column as a comma-separated string. Each value will be encoded in this string by
     *         escaping any commas in the value with a backslash.
     */
    @Override
    @Transient
    public String getValuesAsString() {
        return CaArrayUtils.joinAsCsv(getValues());
    }

    /**
     * Sets the values of this column from a string, which must contain a comma-separated list of strings. Each such
     * string will be unescaped by converting any backslash-comma combinations back to commas.
     * 
     * @param s string containing a comma-separated list of strings.
     */
    @Override
    public void setValuesAsString(String s) {
        setValues(CaArrayUtils.splitFromCsv(s));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setValuesFromArray(Serializable array) {
        Preconditions.checkArgument(array instanceof String[], "Invalid array value passed");
        this.values = (String[]) array;
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
        setValues(new String[numberOfValues]);
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
