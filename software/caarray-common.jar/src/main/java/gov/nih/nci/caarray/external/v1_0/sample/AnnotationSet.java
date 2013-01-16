//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.external.v1_0.sample;

import gov.nih.nci.caarray.external.v1_0.vocabulary.Category;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * An AnnotationSet represents a table of annotation values for a particular list of characteristics across a list
 * of experiment graph nodes. the columns in this table correspond to experiment graph nodes and the rows correspond
 * to categories. Each cell then contains an AnnotationValueSet which has the set of values for the category and 
 * graph node for its column and row.
 * 
 * @author dkokotov
 */
public class AnnotationSet implements Serializable {
    private static final long serialVersionUID = 1L;

    private List<Category> categories = new ArrayList<Category>();
    private List<AnnotationColumn> columns = new ArrayList<AnnotationColumn>();

    /**
     * @return the categories for which this AnnotationSet contains AnnotationValueSets.
     */
    public List<Category> getCategories() {
        return categories;
    }

    /**
     * @param categories the categories to set
     */
    public void setCategories(List<Category> categories) {
        this.categories = categories;
    }

    /**
     * @return the list of AnnotationColumns, where each AnnotationColumn contains a list of AnnotationValueSets 
     *         for a particular experiment graph node.
     */
    public List<AnnotationColumn> getColumns() {
        return columns;
    }

    /**
     * @param columns the columns to set
     */
    public void setColumns(List<AnnotationColumn> columns) {
        this.columns = columns;
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
