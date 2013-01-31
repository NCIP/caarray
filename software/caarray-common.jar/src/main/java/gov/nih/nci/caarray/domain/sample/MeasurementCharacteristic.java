//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.domain.sample;

import gov.nih.nci.caarray.domain.vocabulary.Category;
import gov.nih.nci.caarray.domain.vocabulary.Term;

import java.text.DecimalFormat;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.validator.NotNull;

/**
 *
 */
@Entity
@Table(name = "characteristic_measurement")
@PrimaryKeyJoinColumn(name = "characteristic_id")
public class MeasurementCharacteristic extends AbstractCharacteristic {
    private static final long serialVersionUID = 1L;

    // this should probably be Locale-dependent, but there is no mechanism for user-specific
    // Locales right now
    private static final String NUMBER_FORMAT = "0.00";

    private Float value;
    private Term unit;

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
        super(category);
        this.value = value;
        this.unit = unit;
    }

    /**
     * Gets the unit.
     *
     * @return the unit
     */
    @ManyToOne
    @NotNull
    @Cascade(org.hibernate.annotations.CascadeType.SAVE_UPDATE)
    @ForeignKey(name = "characteristic_measurement_unit_fk")
    public Term getUnit() {
        return unit;
    }

    /**
     * Sets the unit.
     *
     * @param unitVal the unit
     */
    public void setUnit(final Term unitVal) {
        this.unit = unitVal;
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
    @Override
    @Transient
    public String getDisplayValue() {
        if (getValue() == null) {
            return null;
        }
        return new DecimalFormat(NUMBER_FORMAT).format(this.value.doubleValue()) + " " + this.unit.getValue();
    }

}
