//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
package caarray.client.test.search;

import gov.nih.nci.caarray.external.v1_0.AbstractCaArrayEntity;
import gov.nih.nci.caarray.external.v1_0.array.ArrayDesign;

/**
 * Bean encapsulating details of a search-by-example ArrayDesign search.
 * 
 * @author vaughng
 * Jun 27, 2009
 */
public class ArrayDesignSearch extends ExampleSearch
{

    private String name = null, associatedProvider = null, expectedProvider = null, expectedOrganism = null;
    private ArrayDesign arrayDesign;
    
    /**
     * 
     */
    public ArrayDesignSearch()
    {
        super();
    }
    public String getName()
    {
        return name;
    }
    public void setName(String name)
    {
        this.name = name;
    }
    public String getAssociatedProvider()
    {
        return associatedProvider;
    }
    public void setAssociatedProvider(String associatedProvider)
    {
        this.associatedProvider = associatedProvider;
    }
    public String getExpectedProvider()
    {
        return expectedProvider;
    }
    public void setExpectedProvider(String expectedProvider)
    {
        this.expectedProvider = expectedProvider;
    }
    public String getExpectedOrganism()
    {
        return expectedOrganism;
    }
    public void setExpectedOrganism(String expectedOrganism)
    {
        this.expectedOrganism = expectedOrganism;
    }
    
    public ArrayDesign getArrayDesign()
    {
        return arrayDesign;
    }
    public void setArrayDesign(ArrayDesign arrayDesign)
    {
        this.arrayDesign = arrayDesign;
    }
    @Override
    public AbstractCaArrayEntity getExample()
    {
        return getArrayDesign();
    }

    
}
