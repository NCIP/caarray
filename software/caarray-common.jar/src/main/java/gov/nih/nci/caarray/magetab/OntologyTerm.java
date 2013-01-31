//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.magetab;

import java.io.Serializable;

import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * A term from a controlled vocabulary.
 */
public final class OntologyTerm implements Serializable, TermSourceable {

    private static final long serialVersionUID = -6171998480909384500L;

    private TermSource termSource;
    private String category;
    private String value;

    /**
     * @return the termSource
     */
    public TermSource getTermSource() {
        return termSource;
    }

    /**
     * @param termSource the termSource to set
     */
    public void setTermSource(TermSource termSource) {
        this.termSource = termSource;
    }

    /**
     * @return the value
     */
    public String getValue() {
        return this.value;
    }

    /**
     * @param value the value to set
     */
    public void setValue(String value) {
        this.value = value;
    }

    /**
     * @return the category
     */
    public String getCategory() {
        return category;
    }

    /**
     * @param category the category to set
     */
    public void setCategory(String category) {
        this.category = category;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
