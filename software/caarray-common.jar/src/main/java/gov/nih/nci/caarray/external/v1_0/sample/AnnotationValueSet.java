//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package gov.nih.nci.caarray.external.v1_0.sample;

import gov.nih.nci.caarray.external.v1_0.value.AbstractValue;
import gov.nih.nci.caarray.external.v1_0.vocabulary.Category;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * An AnnotationValueSet contains the set of values for a given category. An AnnotationValueSet always belongs to an
 * AnnotationColumn, and the set of values is for the node corresponding to that column.
 * 
 * A node's set of values for a given category is calculated as follows:
 * <ul>
 * <li>if the node directly has a characteristic of the given category, then the set of values is a singleton containing
 * that characteristic's values</li>
 * <li>otherwise, the set of values consists of the union of the set of values for the given categories across the nodes
 * that are the direct predecessors of this node in the experiment graph.</li>
 * </ul>
 * 
 * @author dkokotov
 */
public class AnnotationValueSet implements Serializable {
    private static final long serialVersionUID = 1L;

    private Set<AbstractValue> values = new HashSet<AbstractValue>();
    private Category category;

    /**
     * @return the values
     */
    public Set<AbstractValue> getValues() {
        return values;
    }

    /**
     * @param values the values to set
     */
    public void setValues(Set<AbstractValue> values) {
        this.values = values;
    }

    /**
     * @return the category
     */
    public Category getCategory() {
        return category;
    }

    /**
     * @param category the category to set
     */
    public void setCategory(Category category) {
        this.category = category;
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
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }
}
