//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.domain.protocol;

import gov.nih.nci.caarray.domain.UserDefinedValue;
import gov.nih.nci.caarray.domain.vocabulary.Term;

import javax.persistence.Entity;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.lang.builder.EqualsBuilder;

/**
 * a parameter value with a free text value.
 * @author dkokotov
 */
@Entity
@Table(name = "userdef_parameter_value")
@PrimaryKeyJoinColumn(name = "parameter_value_id")
public class UserDefinedParameterValue extends AbstractParameterValue implements UserDefinedValue {
    private static final long serialVersionUID = 1L;

    private String value;
    
    /**
     * Hibernate-only constructor.
     */
    public UserDefinedParameterValue() {
        // empty
    }

    /**
     * Copy constructor.
     * @param other the value to copy from
     */
    public UserDefinedParameterValue(UserDefinedParameterValue other) {
        super(other);
        value = other.value;
    }

    /**
     * Create a new parameter value with given fields.
     * @param value the value 
     * @param unit the unit for the value
     */
    public UserDefinedParameterValue(String value, Term unit) {
        super(unit);
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
    
    /**
     * {@inheritDoc}
     */
    public boolean matches(final AbstractParameterValue other) {
        if (!(other instanceof UserDefinedParameterValue)) {
            return false;
        }
        UserDefinedParameterValue othermp = (UserDefinedParameterValue) other;
        return super.matches(other) && new EqualsBuilder().append(value, othermp.value).isEquals();
    }
}
