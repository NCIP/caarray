//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.external.v1_0.value;

import gov.nih.nci.caarray.external.v1_0.vocabulary.Term;

import java.io.Serializable;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * Base class for values of characteristics and factor values.
 * 
 * @author dkokotov
 */
public abstract class AbstractValue implements Serializable {
    private static final long serialVersionUID = 1L; // NOPMD
    
    private Term unit;

    /**
     * @return the unit for this value
     */
    public Term getUnit() {
        return unit;
    }

    /**
     * @param unit the unit to set
     */
    public void setUnit(Term unit) {
        this.unit = unit;
    }
    
    /**
     * {@inheritDoc}
     */
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }
}
