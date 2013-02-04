//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package gov.nih.nci.caarray.external.v1_0.query;

import gov.nih.nci.caarray.external.v1_0.AbstractCaArrayEntity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * SearchResult represents the result of performing a search.
 * 
 * @param <T> the class of result objects this SearchResult encapsulates.
 * @author dkokotov
 */
public class SearchResult<T extends AbstractCaArrayEntity> implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private final List<T> results = new ArrayList<T>();
    private int maxAllowedResults;
    private int firstResultOffset;

    /**
     * No-op constructor (for tooling).
     */
    public SearchResult() {
        // empty
    }

    /**
     * Create a new SearchResult with given results and max # of results.
     * 
     * @param results the list of results.
     * @param maxAllowedResults the maximum number of results that the service will return for the original search in a
     *            single API invocation.
     * @param firstResultOffset the offset (0-based) of the first result in this SearchResult relative to the overall
     *            result set of the original query.
     */
    public SearchResult(List<T> results, int maxAllowedResults, int firstResultOffset) {
        this.results.addAll(results);
        this.maxAllowedResults = maxAllowedResults;
        this.firstResultOffset = firstResultOffset;
    }

    /**
     * @return the results
     */
    public List<T> getResults() {
        return results;
    }

    /**
     * @return the maxAllowedResults
     */
    public int getMaxAllowedResults() {
        return maxAllowedResults;
    }

    /**
     * @param maxAllowedResults the maxAllowedResults to set
     */
    public void setMaxAllowedResults(int maxAllowedResults) {
        this.maxAllowedResults = maxAllowedResults;
    }    
    
    /**
     * @return the offset (0-based) of the first result in this SearchResult relative to the overall
     *            result set of the original query.
     */
    public int getFirstResultOffset() {
        return firstResultOffset;
    }

    /**
     * @param firstResultOffset the offset (0-based) of the first result in this SearchResult relative to the overall
     *            result set of the original query.
     */
    public void setFirstResultOffset(int firstResultOffset) {
        this.firstResultOffset = firstResultOffset;
    }

    /**
     * @return true if this represents a full result, meaning that the results returned are all of the results
     * requested; false if the actual number of results was greater than the maximum number of results the
     * service can return, and therefore this result is a subset of the available results.
     */
    public boolean isFullResult() {
        return this.maxAllowedResults < 0 || this.results.size() < this.maxAllowedResults;
    }
}
