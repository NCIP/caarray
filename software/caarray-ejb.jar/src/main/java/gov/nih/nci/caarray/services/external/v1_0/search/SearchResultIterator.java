//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package gov.nih.nci.caarray.services.external.v1_0.search;

import gov.nih.nci.caarray.external.v1_0.AbstractCaArrayEntity;
import gov.nih.nci.caarray.external.v1_0.query.LimitOffset;
import gov.nih.nci.caarray.external.v1_0.query.SearchResult;

import java.util.Iterator;
import java.util.NoSuchElementException;

import com.google.common.base.Function;

/**
 * Generic iterator for iterating through all of the results for an API search. Will automatically take care
 * of retrieving results in batches until all the results have been obtained.
 * 
 * @param <E> the type of the result objects, and the element type for this iterator.
 * @author dkokotov
 */
public class SearchResultIterator<E extends AbstractCaArrayEntity> implements Iterator<E> {
    private SearchResult<E> currentResult;
    private LimitOffset currentPage;
    private int currentResultIndex;
    private Function<LimitOffset, SearchResult<E>> searchFunction;

    
    /**
     * Constructor for starting the iteration with the first element.
     * 
     * @param searchFunction the search function
     */
    public SearchResultIterator(Function<LimitOffset, SearchResult<E>> searchFunction) {
        this(searchFunction, 0);
    }
    
    /**
     * Constructor for starting the iteration with the element at given index.
     * 
     * @param searchFunction the search function
     * @param startIndex the index of the element to start at (0-based).
     */
    public SearchResultIterator(Function<LimitOffset, SearchResult<E>> searchFunction, int startIndex) {
        this.searchFunction = searchFunction;
        this.currentPage = new LimitOffset(-1, startIndex);
    }

    /**
     * {@inheritDoc}
     * 
     * Note that this method can cause the next batch of results to be retrieved, and thus take a long time to execute.
     */
    public boolean hasNext() {
        if (currentResult == null || currentResultIndex == currentResult.getResults().size()
                && !currentResult.isFullResult()) {
            getNext();
        }
        return currentResultIndex < currentResult.getResults().size();
    }
    
    /**
     * @return true if this iterator has more elements and has the next element available locally, e.g. calling next()
     * will not result in a remote API call; false otherwise.
     */
    public boolean hasNextLocally() {
        return currentResult != null && currentResultIndex < currentResult.getResults().size();
    }

    /**
     * {@inheritDoc}
     */
    public E next() {
        if (!hasNext()) {
            throw new NoSuchElementException();            
        }
     
        return currentResult.getResults().get(currentResultIndex++);
    }

    private void getNext() {
        if (currentResult != null) {
            currentPage.setOffset(currentResult.getFirstResultOffset() + currentResult.getResults().size());
        }
        currentResult = searchFunction.apply(currentPage);
        currentResultIndex = 0;
    }

    /**
     * {@inheritDoc}
     * 
     * This iterator does not support element removal, thus this method will always throw NotImplementedException.
     */
    public void remove() {
        throw new UnsupportedOperationException("Remove not supported");
    }
}
