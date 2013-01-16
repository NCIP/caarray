//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.domain.project;

import gov.nih.nci.caarray.domain.MeasurementValue;
import gov.nih.nci.caarray.domain.vocabulary.Term;

import java.text.DecimalFormat;

import javax.persistence.Entity;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 *
 */
@Entity
@Table(name = "measurement_factor_value")
@PrimaryKeyJoinColumn(name = "factor_value_id")
public class MeasurementFactorValue extends AbstractFactorValue implements MeasurementValue {
    private static final long serialVersionUID = 1L;

    private static final String NUMBER_FORMAT = "0.00";

    private Float value;
    
    /**
     * Hibernate-only constructor.
     */
    public MeasurementFactorValue() {
        // empty
    }

    /**
     * Create a new factor value with given fields.
     * @param value the value 
     * @param unit the unit for the value
     */
    public MeasurementFactorValue(Float value, Term unit) {
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
}
