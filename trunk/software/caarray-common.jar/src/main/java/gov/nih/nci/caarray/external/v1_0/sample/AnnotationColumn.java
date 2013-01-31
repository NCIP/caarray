//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.external.v1_0.sample;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * An AnnotationColumn represents a column of AnnotationValueSets for a particular experiment graph node, with one
 * AnnotationValueSet for each category in the parent AnnotationSet.
 * 
 * @author dkokotov
 */
public class AnnotationColumn implements Serializable {
    private static final long serialVersionUID = 1L;

    private AbstractExperimentGraphNode node;
    private List<AnnotationValueSet> valueSets = new ArrayList<AnnotationValueSet>();
    
    /**
     * @return the experiment graph node for which this AnnotationColumn has value sets.
     */
    public AbstractExperimentGraphNode getNode() {
        return node;
    }

    /**
     * @param node the experiment graph node for which this AnnotationColumn has value sets.
     */
    public void setNode(AbstractExperimentGraphNode node) {
        this.node = node;
    }

    /**
     * @return the list of value sets for this column's node, one for each category from the parent annotation set
     */
    public List<AnnotationValueSet> getValueSets() {
        return valueSets;
    }

    /**
     * @param valueSets list of value sets for this column's node.
     */
    public void setValueSets(List<AnnotationValueSet> valueSets) {
        this.valueSets = valueSets;
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
