//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================

package gov.nih.nci.caarray.domain.vocabulary;

import gov.nih.nci.caarray.domain.AbstractCaArrayEntity;
import gov.nih.nci.caarray.domain.AbstractCaArrayObject;
import gov.nih.nci.caarray.validation.UniqueConstraint;
import gov.nih.nci.caarray.validation.UniqueConstraintField;
import gov.nih.nci.caarray.validation.UniqueConstraints;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.Cascade;
import org.hibernate.validator.Length;
import org.hibernate.validator.NotNull;

/**
 *
 */
@Entity
@BatchSize(size = AbstractCaArrayObject.DEFAULT_BATCH_SIZE)
@UniqueConstraints(constraints = {
        @UniqueConstraint(fields = { @UniqueConstraintField(name = "name"),
                @UniqueConstraintField(name = "source") }),
        @UniqueConstraint(fields = { @UniqueConstraintField(name = "accession"),
                @UniqueConstraintField(name = "source") }) }, message = "{category.uniqueConstraint}")
public class Category extends AbstractCaArrayEntity implements Cloneable {
    /**
     * The serial version UID for serialization.
     */
    private static final long serialVersionUID = 1234567890L;

    private String name;
    private String accession;
    private String url;
    private TermSource source;
    private Set<Category> parents = new HashSet<Category>();
    private Set<Category> children = new HashSet<Category>();

    /**
     * Gets the name.
     *
     * @return the name
     */
    @Length(min = 1, max = DEFAULT_STRING_COLUMN_SIZE)
    @NotNull
    public String getName() {
        return name;

    }

    /**
     * Sets the name.
     *
     * @param nameVal the name
     */
    public void setName(final String nameVal) {
        this.name = nameVal;
    }

    /**
     * @return the accession number for this category
     */
    public String getAccession() {
        return accession;
    }

    /**
     * @param accession the accession to set
     */
    public void setAccession(String accession) {
        this.accession = accession;
    }

    /**
     * @return the url for this category
     */
    public String getUrl() {
        return url;
    }

    /**
     * @param url the url to set
     */
    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * Gets the parent.
     *
     * @return the parent
     */
    @ManyToMany
    @JoinTable(name = "category_parents",
            joinColumns = @JoinColumn(name = "category_id"),
            inverseJoinColumns = @JoinColumn(name = "parent_category_id"))
    @Cascade(org.hibernate.annotations.CascadeType.SAVE_UPDATE)
    public Set<Category> getParents() {
        return parents;
    }

    /**
     * Sets the parent categories.
     *
     * @param parents the parent categories
     */
    @SuppressWarnings({ "unused", "PMD.UnusedPrivateMethod" })
    private void setParents(final Set<Category> parents) {
        this.parents = parents;
    }

    /**
     * Gets the children.
     *
     * @return the children
     */
    @ManyToMany(mappedBy = "parents")
    public Set<Category> getChildren() {
        return children;
    }

    /**
     * Sets the children.
     *
     * @param childrenVal the children
     */
    @SuppressWarnings({ "unused", "PMD.UnusedPrivateMethod" })
    private void setChildren(final Set<Category> childrenVal) {
        this.children = childrenVal;
    }

    /**
     * @return the term source to which this category belongs
     */
    @ManyToOne(optional = false)
    @Cascade(org.hibernate.annotations.CascadeType.SAVE_UPDATE)
    @NotNull
    public TermSource getSource() {
        return source;
    }

    /**
     * Set the termSource.
     *
     * @param termSource The termSource to set
     */
    public void setSource(TermSource termSource) {
        this.source = termSource;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Category)) {
            return false;
        }
        if (o == this) {
            return true;
        }
        Category other = (Category) o;
        return new EqualsBuilder().append(this.getName(), other.getName())
                .append(this.getSource(), other.getSource()).isEquals();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(this.getName()).append(this.getSource()).toHashCode();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return new ToStringBuilder(this).append("name", name).toString();
    }
}
