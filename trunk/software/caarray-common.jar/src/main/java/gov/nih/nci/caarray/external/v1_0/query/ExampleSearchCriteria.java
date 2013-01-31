//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.external.v1_0.query;

import gov.nih.nci.caarray.external.v1_0.AbstractCaArrayEntity;

import java.io.Serializable;

/**
 * Criteria for searching by example.
 * 
 * @author dkokotov
 * @param <T> class of the example entity
 */
public class ExampleSearchCriteria<T extends AbstractCaArrayEntity> implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private T example;
    private MatchMode matchMode = MatchMode.EXACT;
    private boolean excludeNulls = true;
    private boolean excludeZeroes = false;
    
    /** 
     * Empty constructor.
     */
    public ExampleSearchCriteria() {
        // empty
    }    

    /** 
     * @param example the example
     */
    public ExampleSearchCriteria(T example) {
        this.example = example;
    }

    /** 
     * @param example the example
     * @param matchMode the match mode
     */
    public ExampleSearchCriteria(T example, MatchMode matchMode) {
        this.example = example;
        this.matchMode = matchMode;
    }

    /** 
     * @param example the example
     * @param matchMode the match mode
     * @param excludeNulls whether to exclude null properties from being used in the search
     */
    public ExampleSearchCriteria(T example, MatchMode matchMode, boolean excludeNulls) {
        this.example = example;
        this.matchMode = matchMode;
        this.excludeNulls = excludeNulls;
    }

    /** 
     * @param example the example
     * @param matchMode the match mode
     * @param excludeNulls whether to exclude null properties from being used in the search
     * @param excludeZeroes whether to exclude zero-valued properties from being used in the search
     */
    public ExampleSearchCriteria(T example, MatchMode matchMode, boolean excludeNulls, boolean excludeZeroes) {
        this.example = example;
        this.matchMode = matchMode;
        this.excludeNulls = excludeNulls;
        this.excludeZeroes = excludeNulls;
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
