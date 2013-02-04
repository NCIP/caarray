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
 * Contains a column of <code>boolean</code> values.
 */
@Entity
@DiscriminatorValue("BOOLEAN")
public class BooleanColumn extends AbstractDataColumn {
    private static final long serialVersionUID = 1L;

    private boolean[] values;

    /**
     * @return the values
     */
    @Transient
    public boolean[] getValues() {
        Preconditions.checkNotNull(values, ERROR_NOT_INITIALIZED);
        return this.values;
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
    @Transient
    public String getValuesAsString() {
        return CaArrayUtils.join(this.values, SEPARATOR);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setValuesAsString(String s) {
        setValues(CaArrayUtils.splitIntoBooleans(s, SEPARATOR));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setValuesFromArray(Serializable array) {
        Preconditions.checkArgument(array instanceof boolean[], "Invalid array value passed");
        this.values = (boolean[]) array;
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
        setValues(new boolean[numberOfValues]);
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
