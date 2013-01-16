//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.external.v1_0;

import java.io.Serializable;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * AbstractCaArrayEntity is the base class for objects in the caArray external model that can be dereferenced.
 * An instance of AbstractCaArrayEntity has an id which can be used to retrieve that instance later. It can be
 * thought of similarly to a URL or EPR.
 * 
 * @author dkokotov
 */
public abstract class AbstractCaArrayEntity implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String id;

    /**
     * @return the id for this entity. The id uniquely identifies both the entity type and the specific instance
     * of the entity, such that the external API can provide a method to retrieve an entity given an id.
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
     * @return a reference to this entity
     */
    public CaArrayEntityReference getReference() {
        return new CaArrayEntityReference(this.id);
    }
    
    /**
     * {@inheritDoc}
     */
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    /**
     * Id properties are used to test for euality, if they are not null; otherwise, the other properties are used.
     * @param obj object to compare this with.
     * @return true if both ids are non-null and equal, or all properties are equal when id is null.
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        final AbstractCaArrayEntity other = (AbstractCaArrayEntity) obj;
        if (this.id != null) {
            return this.id.equals(other.id);
        } else {
            return EqualsBuilder.reflectionEquals(this, other);
        }
    }

    /**
     * Hashcode, based on id when not null; otherwise, the other properties are used to comute the hash code.
     * @return hashcode computed from id when not null, other other properties.
     */
    @Override
    public int hashCode() {
        if (this.id != null) {
            return this.id.hashCode();
        } else {
            return HashCodeBuilder.reflectionHashCode(this);
        }
    }
}
