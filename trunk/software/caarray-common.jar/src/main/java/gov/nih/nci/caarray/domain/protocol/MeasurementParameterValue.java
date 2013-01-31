//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.domain.protocol;

import gov.nih.nci.caarray.domain.MeasurementValue;
import gov.nih.nci.caarray.domain.vocabulary.Term;

import java.text.DecimalFormat;

import javax.persistence.Entity;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.lang.builder.EqualsBuilder;

/**
 * a parameter value with a numeric value.
 * @author dkokotov
 */
@Entity
@Table(name = "measurement_parameter_value")
@PrimaryKeyJoinColumn(name = "parameter_value_id")
public class MeasurementParameterValue extends AbstractParameterValue implements MeasurementValue {
    private static final long serialVersionUID = 1L;

    private static final String NUMBER_FORMAT = "0.00";

    private Float value;
    
    /**
     * Hibernate-only constructor.
     */
    public MeasurementParameterValue() {
        // empty
    }

    /**
     * Copy constructor.
     * @param other the value to copy from
     */
    public MeasurementParameterValue(MeasurementParameterValue other) {
        super(other);
        value = other.value;
    }

    /**
     * Create a new parameter value with given fields.
     * @param value the value 
     * @param unit the unit for the value
     */
    public MeasurementParameterValue(Float value, Term unit) {
        super(unit);
        this.value = value;
    }

    /**
     * @return the value
     */
    public Float getValue() {
        return value;
    }

    /**
     * @param value the value to set
     */
    public void setValue(Float value) {
        this.value = value;
    }

    /**
     * {@inheritDoc}
     */
    @Transient
    public String getDisplayValueWithoutUnit() {
        if (getValue() == null) {
            return null;
        }
        return new DecimalFormat(NUMBER_FORMAT).format(this.value.doubleValue());
    }
    
    /**
     * {@inheritDoc}
     */
    public boolean matches(final AbstractParameterValue other) {
        if (!(other instanceof MeasurementParameterValue)) {
            return false;
        }
        MeasurementParameterValue othermp = (MeasurementParameterValue) other;
        return super.matches(other) && new EqualsBuilder().append(value, othermp.value).isEquals();
    }
}
