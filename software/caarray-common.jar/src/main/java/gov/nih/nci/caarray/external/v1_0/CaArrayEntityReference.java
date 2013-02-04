//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package gov.nih.nci.caarray.external.v1_0;

import java.io.Serializable;

import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * CaArrayEntityReference encapsulates an external id identifying an entity that can be retrieved via the API.
 * 
 * @author dkokotov
 */
public class CaArrayEntityReference implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String id;

    /**
     * Empty constructor, for use by tooling.
     */
    public CaArrayEntityReference() {
        // noop
    }
    
    /**
     * Create a new CaArrayEntityReference with given id.
     * @param id the id of the entity this is referencing. 
     */
    public CaArrayEntityReference(String id) {
        this.id = id;
    }

    /**
     * @return the id of the entity this is referencing
     */
    public String getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(String id) {
        this.id = id;
    }    
    
    /**
     * {@inheritDoc}
     */
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        final CaArrayEntityReference other = (CaArrayEntityReference) obj;
        return ((this.id == null) ? (other.id == null) : this.id.equals(other.id));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        // CHECKSTYLE:OFF
        int hash = 3;
        hash = 59 * hash + (this.id != null ? this.id.hashCode() : 0);
        return hash;
        // CHECKSTYLE:ON
    }


}
