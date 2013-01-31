//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.application.translation.magetab;

import gov.nih.nci.caarray.domain.vocabulary.TermSource;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

/**
 * @author Scott Miller
 *
 */
public class ProtocolKey {

    private final String name;
    private final TermSource termSource;

    /**
     * Constructor taking required fields.
     * @param name the name of the key.
     * @param termSource the source.
     */
    public ProtocolKey(String name, TermSource termSource) {
        this.name = name;
        this.termSource = termSource;
    }

    /**
     * @return the name
     */
    public String getName() {
        return this.name;
    }

    /**
     * @return the termSource
     */
    public TermSource getTermSource() {
        return this.termSource;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }

        if (!(o instanceof ProtocolKey)) {
            return false;
        }

        if (o == this) {
            return true;
        }

        ProtocolKey rhs = (ProtocolKey) o;
        return new EqualsBuilder().append(getName(), rhs.getName()).append(getTermSource(), rhs.getTermSource()).
            isEquals();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(getName()).append(getTermSource()).toHashCode();
    }
}
