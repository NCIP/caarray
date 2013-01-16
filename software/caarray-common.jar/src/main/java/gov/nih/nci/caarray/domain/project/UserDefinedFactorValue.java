//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.domain.project;

import gov.nih.nci.caarray.domain.UserDefinedValue;
import gov.nih.nci.caarray.domain.vocabulary.Term;

import javax.persistence.Entity;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.validator.Length;

/**
 * A FactorValue with a free text value.
 */
@Entity
@Table(name = "userdef_factor_value")
@PrimaryKeyJoinColumn(name = "factor_value_id")
public class UserDefinedFactorValue extends AbstractFactorValue implements UserDefinedValue {
    private static final long serialVersionUID = 1L;

    private String value;
    
    /**
     * Hibernate-only constructor.
     */
    public UserDefinedFactorValue() {
        // empty
    }

    /**
     * Create a new factor value with given fields.
     * @param value the value 
     * @param unit the unit for the value
     */
    public UserDefinedFactorValue(String value, Term unit) {
        super(unit);
        this.value = value;
    }

    /**
     * @return the value
     */
    @Length(max = DEFAULT_STRING_COLUMN_SIZE)
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
        return this.value;
    }
}
