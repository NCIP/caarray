//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.domain.project;

import gov.nih.nci.caarray.domain.AbstractCaArrayEntity;

import javax.persistence.Column;
import javax.persistence.Entity;

import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * A payment mechanism for a project.
 */
@Entity
public class PaymentMechanism extends AbstractCaArrayEntity {

    private static final long serialVersionUID = 6882932219345651741L;

    private String name;

    /**
     * @param name the name
     */
    public PaymentMechanism(String name) {
        this.name = name;
    }

    /**
     * @deprecated hibernate & castor only
     */
    @Deprecated
    public PaymentMechanism() {
        // hibernate & castor only
    }

    /**
     * Gets the name.
     * @return the name
     */
    @Column(length = DEFAULT_STRING_COLUMN_SIZE)
    public String getName() {
        return name;
    }

    /**
     * Sets the name.
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
