//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================

package gov.nih.nci.caarray.domain.publication;

import gov.nih.nci.caarray.domain.AbstractCaArrayEntity;
import gov.nih.nci.caarray.domain.AbstractCaArrayObject;
import gov.nih.nci.caarray.domain.vocabulary.Term;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.validator.Length;

/**
 *
 */
@Entity
@BatchSize(size = AbstractCaArrayObject.DEFAULT_BATCH_SIZE)
public class Publication extends AbstractCaArrayEntity {

    private static final long serialVersionUID = 1234567890L;

    private String authors;
    private String doi;
    private String editor;
    private String issue;
    private String pages;
    private String publisher;
    private String pubMedId;
    private String title;
    private String uri;
    private String volume;
    private String year;
    private Term status;
    private Term type;
    @SuppressWarnings("PMD.AvoidFieldNameMatchingTypeName")
    private String publication;


    /**
     * Gets the authors.
     *
     * @return the authors
     */
    @Column(length = DEFAULT_STRING_COLUMN_SIZE)
    public String getAuthors() {
        return authors;
    }

    /**
     * Sets the authors.
     *
     * @param authorsVal the authors
     */
    public void setAuthors(final String authorsVal) {
        this.authors = authorsVal;
    }

    /**
     * Gets the doi.
     *
     * @return the doi
     */
    @Column(length = DEFAULT_STRING_COLUMN_SIZE)
    public String getDoi() {
        return doi;
    }

    /**
     * Sets the doi.
     *
     * @param doiVal the doi
     */
    public void setDoi(final String doiVal) {
        this.doi = doiVal;
    }

    /**
     * Gets the pubMedId.
     *
     * @return the pubMedId
     */
    @Column(length = DEFAULT_STRING_COLUMN_SIZE)
    public String getPubMedId() {
        return pubMedId;
    }

    /**
     * Sets the pubMedId.
     *
     * @param pubMedIdVal the pubMedId
     */
    public void setPubMedId(final String pubMedIdVal) {
        this.pubMedId = pubMedIdVal;
    }

    /**
     * Gets the status.
     *
     * @return the status
     */
    @ManyToOne
    @Cascade(org.hibernate.annotations.CascadeType.SAVE_UPDATE)
    @ForeignKey(name = "publication_status_fk")
    public Term getStatus() {
        return status;
    }

    /**
     * Sets the status.
     *
     * @param statusVal the status
     */
    public void setStatus(final Term statusVal) {
        this.status = statusVal;
    }

    /**
     * Gets the type.
     *
     * @return the type
     */
    @ManyToOne
    @Cascade(org.hibernate.annotations.CascadeType.SAVE_UPDATE)
    @ForeignKey(name = "publication_type_fk")
    public Term getType() {
        return type;
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
     * Gets the title.
     *
     * @return the title
     */
    @Length(max = DEFAULT_STRING_COLUMN_SIZE)
    public String getTitle() {
        return title;
    }

    /**
     * Sets the title.
     *
     * @param titleVal the title
     */
    public void setTitle(final String titleVal) {
        this.title = titleVal;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    /**
     * @return the editor
     */
    @Column(length = DEFAULT_STRING_COLUMN_SIZE)
    public String getEditor() {
        return editor;
    }

    /**
     * @param editor the editor to set
     */
    public void setEditor(String editor) {
        this.editor = editor;
    }

    /**
     * @return the issue
     */
    @Column(length = DEFAULT_STRING_COLUMN_SIZE)
    public String getIssue() {
        return issue;
    }

    /**
     * @param issue the issue to set
     */
    public void setIssue(String issue) {
        this.issue = issue;
    }

    /**
     * @return the pages
     */
    @Column(length = DEFAULT_STRING_COLUMN_SIZE)
    public String getPages() {
        return pages;
    }

    /**
     * @param pages the pages to set
     */
    public void setPages(String pages) {
        this.pages = pages;
    }

    /**
     * @return the publisher
     */
    @Column(length = DEFAULT_STRING_COLUMN_SIZE)
    public String getPublisher() {
        return publisher;
    }

    /**
     * @param publisher the publisher to set
     */
    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    /**
     * @return the uri
     */
    @Column(length = DEFAULT_STRING_COLUMN_SIZE)
    public  String getUri() {
        return uri;
    }

    /**
     * @param uri the uri to set
     */
    public void setUri(String uri) {
        this.uri = uri;
    }

    /**
     * @return the volume
     */
    @Column(length = DEFAULT_STRING_COLUMN_SIZE)
    public String getVolume() {
        return volume;
    }

    /**
     * @param volume the volume to set
     */
    public void setVolume(String volume) {
        this.volume = volume;
    }

    /**
     * @return the year
     */
    @Column(length = DEFAULT_STRING_COLUMN_SIZE)
    public String getYear() {
        return year;
    }

    /**
     * @param year the year to set
     */
    public void setYear(String year) {
        this.year = year;
    }

    /**
     * @return the publication
     */
    @Column(length = DEFAULT_STRING_COLUMN_SIZE)
    public String getPublication() {
        return publication;
    }

    /**
     * @param publication the publication to set
     */
    public void setPublication(String publication) {
        this.publication = publication;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object object) {
        if (object == this) {
            return true;
        }
        if (!(object instanceof Publication)) {
            return false;
        }
        Publication rhs = (Publication) object;
        return new EqualsBuilder().append(this.volume, rhs.volume).append(this.publication, rhs.publication).append(
                this.doi, rhs.doi).append(this.title, rhs.title).append(this.type, rhs.type).append(this.publisher,
                rhs.publisher).append(this.status, rhs.status).append(this.authors, rhs.authors).append(this.editor,
                rhs.editor).append(this.issue, rhs.issue).append(this.year, rhs.year).append(this.pages, rhs.pages)
                .append(this.uri, rhs.uri).append(this.pubMedId, rhs.pubMedId).isEquals();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        // CHECKSTYLE:OFF
        return new HashCodeBuilder(-770675283, -1229233691).append(this.volume).append(this.publication).append(
                this.doi).append(this.title).append(this.type).append(this.publisher).append(this.status).append(
                this.authors).append(this.editor).append(this.issue).append(this.year).append(this.pages).append(
                this.uri).append(this.pubMedId).toHashCode();
        // CHECKSTYLE:ON
    }
}
