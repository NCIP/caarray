//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.domain.sample;

import gov.nih.nci.caarray.domain.MeasurementValue;
import gov.nih.nci.caarray.domain.vocabulary.Category;
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
@Table(name = "characteristic_measurement")
@PrimaryKeyJoinColumn(name = "characteristic_id")
public class MeasurementCharacteristic extends AbstractCharacteristic implements MeasurementValue {
    private static final long serialVersionUID = 1L;

    // this should probably be Locale-dependent, but there is no mechanism for user-specific
    // Locales right now
    private static final String NUMBER_FORMAT = "0.00";

    private Float value;

    /**
     * Hibernate-only constructor.
     */
    public MeasurementCharacteristic() {
        // empty
    }

    /**
     * Create a new characteristic with given fields.
     * @param category the category of the characteristic
     * @param value the value of the characteristic
     * @param unit the unit for the value
     */
    public MeasurementCharacteristic(Category category, Float value, Term unit) {
        super(category, unit);
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
