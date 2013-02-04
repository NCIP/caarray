//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================

package gov.nih.nci.caarray.domain.vocabulary;

import gov.nih.nci.caarray.domain.AbstractCaArrayEntity;
import gov.nih.nci.caarray.domain.AbstractCaArrayObject;
import gov.nih.nci.caarray.validation.UniqueConstraint;
import gov.nih.nci.caarray.validation.UniqueConstraintField;
import gov.nih.nci.caarray.validation.UniqueConstraints;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;

import org.apache.commons.lang.builder.CompareToBuilder;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.Cascade;
import org.hibernate.validator.Length;
import org.hibernate.validator.NotNull;

  /**

   */
@Entity
@BatchSize(size = AbstractCaArrayObject.DEFAULT_BATCH_SIZE)
@UniqueConstraints(constraints = {
        @UniqueConstraint(fields = {@UniqueConstraintField(name = "value"), @UniqueConstraintField(name = "source") }),
        @UniqueConstraint(fields = {@UniqueConstraintField(name = "accession"),
                @UniqueConstraintField(name = "source") }) },
                message = "{term.uniqueConstraint}")
public class Term extends AbstractCaArrayEntity implements Comparable<Term> {
    /**
     * The serial version UID for serialization.
     */
    private static final long serialVersionUID = 1234567890L;

    private String value;
    private String accession;
    private String url;
    private String description;
    private Set<Category> categories = new HashSet<Category>();
    private TermSource source;

    /**
     * Gets the description.
     *
     * @return the description
     */
    @Length(max = DEFAULT_STRING_COLUMN_SIZE)
    public String getDescription() {
        return this.description;
    }

    /**
     * Sets the description.
     *
     * @param descriptionVal the description
     */
    public void setDescription(final String descriptionVal) {
        this.description = descriptionVal;
    }

    /**
     * Gets the value.
     *
     * @return the value
     */
    @NotNull
    @Length(min = 1, max = DEFAULT_STRING_COLUMN_SIZE)
    public String getValue() {
        return this.value;
    }

    /**
     * Sets the value.
     *
     * @param valueVal the value
     */
    public void setValue(final String valueVal) {
        this.value = valueVal;
    }

    /**
     * Gets the url at which this term can be accessed, if available.
     *
     * @return the url
     */
    @Column(length = DEFAULT_STRING_COLUMN_SIZE)
    public String getUrl() {
        return url;
    }

    /**
     * Sets the url.
     *
     * @param urlVal the url
     */
    public void setUrl(final String urlVal) {
        this.url = urlVal;
    }

    /**
     * Gets the accession, which is a unique identifier for this term within its term source.
     *
     * @return the accession
     */
    @Column(length = DEFAULT_STRING_COLUMN_SIZE)
    public String getAccession() {
        return accession;
    }

    /**
     * Sets the accession.
     *
     * @param accession the accession
     */
    public void setAccession(final String accession) {
        this.accession = accession;
    }

    /**
     * @return the categories to which this term belongs
     */
    @ManyToMany
    @JoinTable(
            name = "term_categories",
            joinColumns = @JoinColumn(name = "term_id"),
            inverseJoinColumns = @JoinColumn(name = "category_id")
    )
    @Cascade(org.hibernate.annotations.CascadeType.SAVE_UPDATE)
    public Set<Category> getCategories() {
        return this.categories;
    }

    /**
     * Sets the categories for this term.
     * @param categories the categories
     */
    @SuppressWarnings({"unused", "PMD.UnusedPrivateMethod" })
    private void setCategories(final Set<Category> categories) {
        this.categories = categories;
    }

    /**
     * Sets the categories of this term to be the singleton set with the given category.
     * @param category the category which should be the sole category for this term
     */
    public void setCategory(Category category) {
        this.categories.clear();
        this.categories.add(category);
    }

    /**
     * Gets the source.
     *
     * @return the source
     */
    @ManyToOne(optional = false)
    @Cascade(org.hibernate.annotations.CascadeType.SAVE_UPDATE)
    @NotNull(message = "{term.source.notNull}")
    public TermSource getSource() {
        return this.source;
    }

    /**
     * Sets the source.
     *
     * @param sourceVal the source
     */
    public void setSource(final TermSource sourceVal) {
        this.source = sourceVal;
    }

    /**
     * @return the value and the term source of this term, which identify
     * the term unambiguously
     */
    @Transient
    public String getValueAndSource() {
        return new StringBuilder(getValue()).append(" (").append(getSource().getName()).append(")")
                .toString();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Term)) {
            return false;
        }
        if (o == this) {
            return true;
        }
        Term other = (Term) o;
        return new EqualsBuilder().append(this.getId(), other.getId()).append(this.getValue(), other.getValue())
                .append(this.getSource(), other.getSource()).isEquals();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(this.getId()).append(this.getValue()).append(this.getSource()).toHashCode();
    }

    /**
     * {@inheritDoc}
     */
    public int compareTo(Term o) {
        if (o == null) {
            throw new NullPointerException(); // NOPMD
        }
        return new CompareToBuilder().append(this.getValue(), o.getValue()).append(this.getSource(), o.getSource())
                .toComparison();
    }
}
