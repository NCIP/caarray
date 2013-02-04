//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package gov.nih.nci.caarray.domain.search;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;

import org.hibernate.criterion.MatchMode;

import com.fiveamsolutions.nci.commons.data.persistent.PersistentObject;

/**
 * Simple bean to hold example search criteria.
 * 
 * @param <T> class of the example entity
 * @author dkokotov
 */
public class ExampleSearchCriteria<T extends PersistentObject> {
    private T example;
    private MatchMode matchMode = MatchMode.EXACT;
    private boolean excludeNulls = true;
    private boolean excludeZeroes = false;
    private Collection<String> excludeProperties = new HashSet<String>();

    /**
     * Create a new example search criteria with given example, using exact matching, excluding null properties.
     * 
     * @param example the example
     */
    public ExampleSearchCriteria(T example) {
        this.example = example;
    }
    
    /**
     * Create a new example search criteria with given example, using the given match mode, excluding null properties.
     * 
     * @param example the example
     * @param matchMode how string properties in the example should be compared against candidate matches.
     */
    public ExampleSearchCriteria(T example, MatchMode matchMode) {
        this.example = example;
        this.matchMode = matchMode;
    }
    

    /**
     * Create a new example search criteria with given example, using the given match mode and given null property
     * handling.
     * 
     * @param example the example
     * @param matchMode how string properties in the example should be compared against candidate matches.
     * @param excludeNulls if true, properties in the example with a null value will be excluded from comparison; if
     *            false, they will be excluded, so candidate matches must also have null values for those properties.
     */
    public ExampleSearchCriteria(T example, MatchMode matchMode, boolean excludeNulls) {
        this.example = example;
        this.matchMode = matchMode;
        this.excludeNulls = excludeNulls;
    }


    /**
     * Create a new example search criteria with given example, using the given match mode and given null property
     * handling.
     * 
     * @param example the example
     * @param matchMode how string properties in the example should be compared against candidate matches.
     * @param excludeNulls if true, properties in the example with a null value will be excluded from comparison; if
     *            false, they will be excluded, so candidate matches must also have null values for those properties.
     * @param excludeProperties exclude the given properties from comparison.
     */
    public ExampleSearchCriteria(T example, MatchMode matchMode, boolean excludeNulls,
            Collection<String> excludeProperties) {
        this.example = example;
        this.matchMode = matchMode;
        this.excludeNulls = excludeNulls;
        this.excludeProperties = excludeProperties;
    }

    /**
     * Create a new example search criteria with given example, using exact matching, excluding null properties. 
     * Intended for builder-style creation of the criteria.
     * 
     * @param entity the example
     * @param <T> class of example entity
     * @return the new example criteria
     */
    public static <T extends PersistentObject> ExampleSearchCriteria<T> forEntity(T entity) {
        return new ExampleSearchCriteria<T>(entity);
    }

    /**
     * Set the match mode of this criteria to given match mode.
     * 
     * @param mode the new match mode
     * @return this
     */
    public ExampleSearchCriteria<T> matchUsing(MatchMode mode) {
        setMatchMode(mode);
        return this;
    }
    
    /**
     * Set this criteria to exclude null properties from comparison.
     * 
     * @return this
     */
    public ExampleSearchCriteria<T> excludeNulls() {
        setExcludeNulls(true);
        return this;
    }    

    /**
     * Set this criteria to include null properties in the comparison.
     * 
     * @return this
     */
    public ExampleSearchCriteria<T> includeNulls() {
        setExcludeNulls(false);
        return this;
    }    
    
    /**
     * Set this criteria to exclude given properties from comparison.
     * 
     * @param properties the given properties to exclude.
     * @return this
     */
    public ExampleSearchCriteria<T> excludeProperties(String... properties) {
        excludeProperties.addAll(Arrays.asList(properties));
        return this;
    }    

    /**
     * Set this criteria to include given properties from comparison.
     * 
     * @param properties the given properties to include.
     * @return this
     */
    public ExampleSearchCriteria<T> includeProperties(String... properties) {
        excludeProperties.removeAll(Arrays.asList(properties));
        return this;
    }    
    
    /**
     * Set this criteria to exclude zero-valued properties from comparison.
     * 
     * @return this
     */
    public ExampleSearchCriteria<T> excludeZeroes() {
        setExcludeZeroes(true);
        return this;
    }    

    /**
     * Set this criteria to include zero-valued properties in the comparison.
     * 
     * @return this
     */
    public ExampleSearchCriteria<T> includeZeroes() {
        setExcludeZeroes(false);
        return this;
    }    


    /**
     * @return the example
     */
    public T getExample() {
        return example;
    }

    /**
     * @param example the example to set
     */
    public void setExample(T example) {
        this.example = example;
    }

    /**
     * @return the matchMode
     */
    public MatchMode getMatchMode() {
        return matchMode;
    }

    /**
     * @param matchMode the matchMode to set
     */
    public void setMatchMode(MatchMode matchMode) {
        this.matchMode = matchMode;
    }

    /**
     * @return the excludeNulls
     */
    public boolean isExcludeNulls() {
        return excludeNulls;
    }

    /**
     * @param excludeNulls the excludeNulls to set
     */
    public void setExcludeNulls(boolean excludeNulls) {
        this.excludeNulls = excludeNulls;
    }

    /**
     * @return the excludeProperties
     */
    public Collection<String> getExcludeProperties() {
        return excludeProperties;
    }

    /**
     * @param excludeProperties the excludeProperties to set
     */
    public void setExcludeProperties(Collection<String> excludeProperties) {
        this.excludeProperties = excludeProperties;
    }

    /**
     * @return the excludeZeroes
     */
    public boolean isExcludeZeroes() {
        return excludeZeroes;
    }

    /**
     * @param excludeZeroes the excludeZeroes to set
     */
    public void setExcludeZeroes(boolean excludeZeroes) {
        this.excludeZeroes = excludeZeroes;
    }
}
