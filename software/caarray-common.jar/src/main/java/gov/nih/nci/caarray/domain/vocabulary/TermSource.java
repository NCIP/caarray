//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.domain.vocabulary;

import gov.nih.nci.caarray.domain.AbstractCaArrayEntity;
import gov.nih.nci.caarray.domain.AbstractCaArrayObject;
import gov.nih.nci.caarray.validation.UniqueConstraint;
import gov.nih.nci.caarray.validation.UniqueConstraintField;
import gov.nih.nci.caarray.validation.UniqueConstraints;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Transient;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.hibernate.annotations.BatchSize;
import org.hibernate.validator.Length;
import org.hibernate.validator.NotNull;

  /**
   */
@Entity
@BatchSize(size = AbstractCaArrayObject.DEFAULT_BATCH_SIZE)
@UniqueConstraints(constraints = {
        @UniqueConstraint(fields = { @UniqueConstraintField(name = "name"),
                @UniqueConstraintField(name = "version", nullsEqual = true) }),
        @UniqueConstraint(fields = { @UniqueConstraintField(name = "url"),
                @UniqueConstraintField(name = "version", nullsEqual = true) }) },
                message = "{termSource.uniqueConstraint}")
public class TermSource extends AbstractCaArrayEntity {

    private static final long serialVersionUID = 1234567890L;

    private String name;
    private String url;
    private String version;

    /**
     * Gets the name.
     *
     * @return the name
     */
    @NotNull
    @Length(min = 1, max = DEFAULT_STRING_COLUMN_SIZE)
    public String getName() {
        return this.name;
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
     * Gets the url.
     *
     * @return the url
     */
    @Length(max = DEFAULT_STRING_COLUMN_SIZE)
    public String getUrl() {
        return this.url;
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
     * Gets the version.
     *
     * @return the version
     */
    @Column(length = DEFAULT_STRING_COLUMN_SIZE)
    public String getVersion() {
        return this.version;
    }

    /**
     * Sets the version.
     *
     * @param versionVal the version
     */
    public void setVersion(final String versionVal) {
        this.version = versionVal;
    }

    /**
     * @return the name and the version, if any, of this term source
     */
    @Transient
    public String getNameAndVersion() {
        StringBuilder str = new StringBuilder(this.name);
        if (this.version != null) {
            str.append(" ").append(this.version);
        }
        return str.toString();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof TermSource)) {
            return false;
        }
        if (o == this) {
            return true;
        }
        TermSource other = (TermSource) o;
        return new EqualsBuilder().append(this.getId(), other.getId()).append(this.getName(), other.getName()).append(
                this.getVersion(), other.getVersion()).isEquals();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(this.getId()).append(this.getName()).append(this.getVersion()).toHashCode();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return super.toString() + ", name=" + getName();
    }
}
