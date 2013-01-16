//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.domain.project;

import gov.nih.nci.caarray.domain.TermBasedValue;
import gov.nih.nci.caarray.domain.vocabulary.Term;

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
@Table(name = "term_factor_value")
@PrimaryKeyJoinColumn(name = "factor_value_id")
public class TermBasedFactorValue extends AbstractFactorValue implements TermBasedValue {
    private static final long serialVersionUID = 1L;

    private Term term;

    /**
     * Hibernate-only constructor.
     */
    public TermBasedFactorValue() {
        //empty
    }

    /**
     * Create a new factor value with given term and unit.
     *
     * @param term the term
     * @param unit the unit
     */
    public TermBasedFactorValue(Term term, Term unit) {
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
    @ForeignKey(name = "term_fv_term_fk")
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
}
