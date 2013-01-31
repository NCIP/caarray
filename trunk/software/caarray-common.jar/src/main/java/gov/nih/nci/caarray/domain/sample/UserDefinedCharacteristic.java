//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.domain.sample;

import gov.nih.nci.caarray.domain.UserDefinedValue;
import gov.nih.nci.caarray.domain.vocabulary.Category;
import gov.nih.nci.caarray.domain.vocabulary.Term;

import javax.persistence.Entity;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 * A Characteristic with a free text value.
 */
@Entity
@Table(name = "characteristic_userdef")
@PrimaryKeyJoinColumn(name = "characteristic_id")
public class UserDefinedCharacteristic extends AbstractCharacteristic implements UserDefinedValue {
    private static final long serialVersionUID = 1L;

    private String value;

    /**
     * Hibernate-only constructor.
     */
    public UserDefinedCharacteristic() {
        // empty
    }

    /**
     * Create a new characteristic with given fields.
     * @param category the category of the characteristic
     * @param value the value of the characteristic
     * @param unit the unit for the value
     */
    public UserDefinedCharacteristic(Category category, String value, Term unit) {
        super(category, unit);
        this.value = value;
    }

    /**
     * @return the value
     */
    public String getValue() {
        return value;
    }

    /**
     * @param value the value to set
     */
    public void setValue(String value) {
        this.value = value;
    }

    /**
     * {@inheritDoc}
     */
    @Transient
    public String getDisplayValueWithoutUnit() {
        return getValue();
    }
}
