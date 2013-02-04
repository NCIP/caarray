//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package gov.nih.nci.caarray.domain.protocol;

import gov.nih.nci.caarray.domain.TermBasedValue;
import gov.nih.nci.caarray.domain.vocabulary.Term;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.validator.NotNull;

/**
 * A parameter value with a controlled vocabulary term value.
 * 
 * @author dkokotov
 */
@Entity
@Table(name = "term_parameter_value")
@PrimaryKeyJoinColumn(name = "parameter_value_id")
public class TermBasedParameterValue extends AbstractParameterValue implements TermBasedValue {
    private static final long serialVersionUID = 1L;

    private Term term;

    /**
     * Hibernate-only constructor.
     */
    public TermBasedParameterValue() {
        //empty
    }

    /**
     * Copy constructor.
     * @param other the value to copy from
     */
    public TermBasedParameterValue(TermBasedParameterValue other) {
        super(other);
        term = other.term;
    }
    
    /**
     * Create a new protocol value with given term and unit.
     *
     * @param term the term
     * @param unit the unit
     */
    public TermBasedParameterValue(Term term, Term unit) {
        super(unit);
        this.term = term;
    }

    /**
     * Gets the term.
     *
     * @return the term
     */
    @ManyToOne(optional = false)
    @Cascade(org.hibernate.annotations.CascadeType.SAVE_UPDATE)
    @ForeignKey(name = "term_pv_term_fk")
    @NotNull
    public Term getTerm() {
        return term;
    }

    /**
     * Sets the term.
     *
     * @param termVal the term
     */
    public void setTerm(final Term termVal) {
        this.term = termVal;
    }

    /**
     * {@inheritDoc}
     */
    @Transient
    public String getDisplayValueWithoutUnit() {
        return this.term != null ? this.term.getValue() : null;
    }

    /**
     * {@inheritDoc}
     */
    public boolean matches(final AbstractParameterValue other) {
        if (!(other instanceof TermBasedParameterValue)) {
            return false;
        }
        TermBasedParameterValue termOther = (TermBasedParameterValue) other;
        return super.matches(other) && new EqualsBuilder().append(term, termOther.term).isEquals();
    }
}
