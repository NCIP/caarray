//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.domain.sample;

import gov.nih.nci.caarray.domain.vocabulary.Category;
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
@Table(name = "characteristic_term")
@PrimaryKeyJoinColumn(name = "characteristic_id")
public class TermBasedCharacteristic extends AbstractCharacteristic {

    private static final long serialVersionUID = 1L;

    private Term term;


    /**
     * Hibernate-only constructor.
     */
    public TermBasedCharacteristic() {
        //empty
    }

    /**
     * Create a new characteristic with given category and term.
     *
     * @param category the category.
     * @param term the term
     */
    public TermBasedCharacteristic(Category category, Term term) {
        super(category);
        this.term = term;
    }

    /**
     * Gets the term.
     *
     * @return the term
     */
    @ManyToOne(optional = false)
    @Cascade(org.hibernate.annotations.CascadeType.SAVE_UPDATE)
    @ForeignKey(name = "characteristic_term_term_fk")
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
    @Override
    @Transient
    public String getDisplayValue() {
        return this.term != null ? this.term.getValue() : null;
    }
}
