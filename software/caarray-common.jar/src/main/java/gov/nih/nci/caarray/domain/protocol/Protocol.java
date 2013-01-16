//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================

package gov.nih.nci.caarray.domain.protocol;

import gov.nih.nci.caarray.domain.AbstractCaArrayEntity;
import gov.nih.nci.caarray.domain.AbstractCaArrayObject;
import gov.nih.nci.caarray.domain.vocabulary.Term;
import gov.nih.nci.caarray.domain.vocabulary.TermSource;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.validator.Length;
import org.hibernate.validator.NotNull;

/**
 * Class representing a protocol.
 *
 * @author Scott Miller
 */
@Entity
@BatchSize(size = AbstractCaArrayObject.DEFAULT_BATCH_SIZE)
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"name", "source" }))
public class Protocol extends AbstractCaArrayEntity {
    private static final long serialVersionUID = 1234567890L;

    private String contact;
    private String description;
    private String hardware;
    private String name;
    private String software;
    private Term type;
    private TermSource source;
    private String url;
    private Set<Parameter> parameters = new HashSet<Parameter>();

    /**
     * Constructor for use by hibernate and struts only.
     */
    @Deprecated
    public Protocol() {
        // do nothing
    }

    /**
     * Constructor taking all required fields.
     *
     * @param name the name.
     * @param type the type.
     * @param source the source.
     */
    public Protocol(String name, Term type, TermSource source) {
        this.name = name;
        this.type = type;
        this.source = source;
    }

    /**
     * Gets the contact.
     *
     * @return the contact
     */
    @Length(max = DEFAULT_STRING_COLUMN_SIZE)
    public String getContact() {
        return this.contact;
    }

    /**
     * Sets the contact.
     *
     * @param contactVal the contact
     */
    public void setContact(final String contactVal) {
        this.contact = contactVal;
    }

    /**
     * Gets the description.
     *
     * @return the description
     */
    @Length(max = LARGE_TEXT_FIELD_LENGTH)
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
     * Gets the hardware.
     *
     * @return the hardware
     */
    @Length(max = DEFAULT_STRING_COLUMN_SIZE)
    public String getHardware() {
        return this.hardware;
    }

    /**
     * Sets the hardware.
     *
     * @param hardwareVal the hardware
     */
    public void setHardware(final String hardwareVal) {
        this.hardware = hardwareVal;
    }

    /**
     * Gets the name.
     *
     * @return the name
     */
    @Length(min = 1, max = DEFAULT_STRING_COLUMN_SIZE)
    @NotNull
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
     * Gets the software.
     *
     * @return the software
     */
    @Length(max = DEFAULT_STRING_COLUMN_SIZE)
    public String getSoftware() {
        return this.software;
    }

    /**
     * Sets the software.
     *
     * @param softwareVal the software
     */
    public void setSoftware(final String softwareVal) {
        this.software = softwareVal;
    }

    /**
     * Gets the type.
     *
     * @return the type
     */
    @ManyToOne
    @Cascade(org.hibernate.annotations.CascadeType.SAVE_UPDATE)
    @ForeignKey(name = "protocol_type_fk")
    @NotNull
    public Term getType() {
        return this.type;
    }

    /**
     * Sets the type.
     *
     * @param typeVal the type
     */
    public void setType(final Term typeVal) {
        this.type = typeVal;
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
     * Gets the parameters.
     *
     * @return the parameters
     */
    @OneToMany(mappedBy = "protocol", fetch = FetchType.LAZY)
    @Cascade(CascadeType.SAVE_UPDATE)
    public Set<Parameter> getParameters() {
        return this.parameters;
    }

    /**
     * Sets the parameters.
     *
     * @param parametersVal the parameters
     */
    @SuppressWarnings({"unused", "PMD.UnusedPrivateMethod" })
    private void setParameters(final Set<Parameter> parametersVal) {
        this.parameters = parametersVal;
    }

    /**
     * @return the source
     */
    @ManyToOne
    @Cascade(org.hibernate.annotations.CascadeType.SAVE_UPDATE)
    @ForeignKey(name = "protocol_term_source_fk")
    @NotNull
    public TermSource getSource() {
        return this.source;
    }

    /**
     * @param source the source to set
     */
    public void setSource(TermSource source) {
        this.source = source;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }

        if (!(o instanceof Protocol)) {
            return false;
        }

        if (o == this) {
            return true;
        }

        if (getId() == null) {
            // by default, two transient instances cannot ever be equal
            return false;
        }

        Protocol rhs = (Protocol) o;
        return new EqualsBuilder().append(getName(), rhs.getName()).append(getSource(), rhs.getSource()).isEquals();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(getName()).append(getSource()).toHashCode();
    }
}
