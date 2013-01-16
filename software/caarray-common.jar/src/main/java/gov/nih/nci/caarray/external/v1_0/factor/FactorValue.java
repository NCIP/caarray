//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.external.v1_0.factor;

import gov.nih.nci.caarray.external.v1_0.value.AbstractValue;
import gov.nih.nci.caarray.external.v1_0.value.MeasurementValue;
import gov.nih.nci.caarray.external.v1_0.value.TermValue;
import gov.nih.nci.caarray.external.v1_0.value.UserDefinedValue;

import java.io.Serializable;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * FactorValue represents the value of an experimental factor for a particular hybridization chain.
 * 
 * @author dkokotov
 */
public class FactorValue implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private Factor factor;    
    private AbstractValue value;

    /**
     * @return the value
     */
    public AbstractValue getValue() {
        return value;
    }

    /**
     * @param value the value to set
     */
    public void setValue(AbstractValue value) {
        this.value = value;
    }

    /**
     * @return the factor whose value this contains
     */
    public Factor getFactor() {
        return factor;
    }

    /**
     * @param factor the factor to set
     */
    public void setFactor(Factor factor) {
        this.factor = factor;
    }
    
    /**
     * @return a new FactorValue initialized with a blank measurement value;
     */
    public static FactorValue newMeasurementFactorValue() {
        return newFactorValue(new MeasurementValue());
    }

    /**
     * @return a new FactorValue initialized with a blank term value;
     */
    public static FactorValue newTermFactorValue() {
        return newFactorValue(new TermValue());
    }

    /**
     * @return a new FactorValue initialized with a blank user defined value;
     */
    public static FactorValue newUserDefinedFactorValue() {
        return newFactorValue(new UserDefinedValue());
    }
    
    private static FactorValue newFactorValue(AbstractValue initialValue) {
        FactorValue fv = new FactorValue();
        fv.setValue(initialValue);
        return fv;
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
