//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package gov.nih.nci.caarray.external.v1_0.query;

import java.io.Serializable;

/**
 * Bean for specifying paging and sorting constraints for a query.
 * 
 * @author dkokotov
 */
public class LimitOffset implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private int limit;
    private int offset;

    /**
     * Constructor.
     * 
     * @param limit the maximum number of results to return. A negative number indicates no limit. 
     * @param offset the index (0-based) of the first result to return.  
     */
    public LimitOffset(int limit, int offset) {
        this.limit = limit;
        this.offset = offset;
    }
    
    /**
     * Empty constructor, for serialization.
     */
    public LimitOffset() {
        // noop
    }

    /**
     * @return the maximum number of results to return. A negative number indicates no limit.
     */
    public int getLimit() {
        return limit;
    }

    /**
     * @param limit the maximum number of results to return. A negative number indicates no limit. 
     */
    public void setLimit(int limit) {
        this.limit = limit;
    }

    /**
     * @return the index (0-based) of the first result to return.
     */
    public int getOffset() {
        return offset;
    }

    /**
     * @param offset the index (0-based) of the first result to return.
     */
    public void setOffset(int offset) {
        this.offset = offset;
    }
}
