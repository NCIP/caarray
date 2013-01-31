//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.domain.data;

import gov.nih.nci.caarray.domain.AbstractCaArrayObject;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Transient;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * Represents the heading for a "column" of array data, indicating the name and primitive typeClass
 * of the data.
 */
@Entity
@org.hibernate.annotations.Entity(mutable = false)
@Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
public final class QuantitationType extends AbstractCaArrayObject {

    private static final long serialVersionUID = 7207891070185665511L;
    private static final Set<Class<?>> SUPPORTED_TYPES = new HashSet<Class<?>>();

    static {
        SUPPORTED_TYPES.add(Boolean.class);
        SUPPORTED_TYPES.add(Short.class);
        SUPPORTED_TYPES.add(Integer.class);
        SUPPORTED_TYPES.add(Long.class);
        SUPPORTED_TYPES.add(Float.class);
        SUPPORTED_TYPES.add(Double.class);
        SUPPORTED_TYPES.add(String.class);
    }

    private String name;
    private String type;

    /**
     * @return the name
     */
    @Column(length = DEFAULT_STRING_COLUMN_SIZE)
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the typeClass
     */
    @Transient
    public Class<?> getTypeClass() {
        try {
            return getType() != null ? Class.forName(getType()) : null;
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException("Couldn't locate class for QuantitationType", e);
        }
    }

    /**
     * @param typeClass the typeClass to set
     */
    public void setTypeClass(Class<?> typeClass) {
        if (!SUPPORTED_TYPES.contains(typeClass)) {
            throw new IllegalArgumentException("Unsupported data typeClass; allowed types are " + SUPPORTED_TYPES);
        }
        this.type = typeClass.getName();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return name;
    }

    /**
     * @return the type
     */
    @Column(length = DEFAULT_STRING_COLUMN_SIZE)
    public String getType() {
        return type;
    }

    /**
     * @param type the type to set
     */
    public void setType(String type) {
        this.type = type;
    }

}
