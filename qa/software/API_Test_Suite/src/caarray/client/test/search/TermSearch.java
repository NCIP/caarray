//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
package caarray.client.test.search;

import gov.nih.nci.caarray.external.v1_0.AbstractCaArrayEntity;
import gov.nih.nci.caarray.external.v1_0.vocabulary.Term;

/**
 * Bean encapsulating details of a search-by-example Term search.
 * 
 * @author vaughng
 * Jun 28, 2009
 */
public class TermSearch extends ExampleSearch
{
    private Term term;
    
    /**
     * 
     */
    public TermSearch()
    {
        super();
    }

    /* (non-Javadoc)
     * @see caarray.client.test.ExampleSearch#getExample()
     */
    @Override
    public AbstractCaArrayEntity getExample()
    {
        return getTerm();
    }

    public Term getTerm()
    {
        return term;
    }

    public void setTerm(Term term)
    {
        this.term = term;
    }

}
