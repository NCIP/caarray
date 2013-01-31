//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.services.external.v1_0.search;

import gov.nih.nci.caarray.external.v1_0.AbstractCaArrayEntity;
import gov.nih.nci.caarray.external.v1_0.query.LimitOffset;
import gov.nih.nci.caarray.external.v1_0.query.SearchResult;
import gov.nih.nci.caarray.services.external.v1_0.InvalidInputException;
import gov.nih.nci.caarray.services.external.v1_0.search.SearchApiUtils.WrapperException;

import java.util.ArrayList;
import java.util.List;

import com.google.common.base.Function;

/**
 * Represents a particular API search - somewhat equivalent to a Hibernate Criteria or Query. Exposes generic
 * methods for listing or iterating over the results of the search.
 * 
 * @param <T> the type of the result entity
 * @author dkokotov
 */
public class Search<T extends AbstractCaArrayEntity> implements Iterable<T> {
    private final Function<LimitOffset, SearchResult<T>> searchFunction;
    
    /**
     * @param searchFunction a function that accepts a PagingParams and performs the search.
     */
    public Search(Function<LimitOffset, SearchResult<T>> searchFunction) {
        this.searchFunction = searchFunction;
    }
    
    /**
     * Retrieve all results for this search. 
     * @return the full list of results for this search.
     * @throws InvalidInputException if executing the underlying search service method results in this exception
     */
    public List<T> list() throws InvalidInputException {
        return getAllSearchResults();
    }   

    /**
     * Get an iterator over all results for this search. 
     * @return an iterator over the full list of results for this search.
     */
    public SearchResultIterator<T> iterator() {
        return new SearchResultIterator<T>(searchFunction);
    }   

    /**
     * Get an iterator over all results for this search, starting at given index. 
     * @param startIndex the index (0-based) of the result to start with.
     * @return an iterator over the results, starting with the result at the given index.
     */
    public SearchResultIterator<T> iterator(int startIndex) {
        return new SearchResultIterator<T>(searchFunction, startIndex);
    }   
    
    private List<T> getAllSearchResults() throws InvalidInputException {
        List<T> allResults = new ArrayList<T>();
        try {
            SearchResult<T> oneResult = searchFunction.apply(null);
            allResults.addAll(oneResult.getResults());
            
            if (oneResult.isFullResult()) {
                return allResults;
            } 

            LimitOffset pagingParams = 
                new LimitOffset(oneResult.getResults().size(), oneResult.getMaxAllowedResults());
            while (oneResult.getResults().size() == pagingParams.getLimit()) {
                oneResult = searchFunction.apply(pagingParams);
                allResults.addAll(oneResult.getResults());
                pagingParams.setOffset(oneResult.getFirstResultOffset() + oneResult.getResults().size());
            }
        } catch (WrapperException e) {
            throw (InvalidInputException) e.getCause();
        }
        
        return allResults;
    }
}
