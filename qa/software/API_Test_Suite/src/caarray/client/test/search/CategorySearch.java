//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
package caarray.client.test.search;

import gov.nih.nci.caarray.external.v1_0.AbstractCaArrayEntity;
import gov.nih.nci.caarray.external.v1_0.vocabulary.Category;

/**
 * Bean encapsulating details of a search-by-example Category search.
 * 
 * @author vaughng
 * Jun 28, 2009
 */
public class CategorySearch extends ExampleSearch
{

    private Category category;
    /**
     * 
     */
    public CategorySearch()
    {
        super();
    }

    /* (non-Javadoc)
     * @see caarray.client.test.ExampleSearch#getExample()
     */
    @Override
    public AbstractCaArrayEntity getExample()
    {
        return getCategory();
    }

    public Category getCategory()
    {
        return category;
    }

    public void setCategory(Category category)
    {
        this.category = category;
    }

}
