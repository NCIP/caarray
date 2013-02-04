//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package caarray.client.test.search;

import gov.nih.nci.caarray.external.v1_0.AbstractCaArrayEntity;
import gov.nih.nci.caarray.external.v1_0.array.AssayType;

import java.util.ArrayList;
import java.util.List;

/**
 * Bean encapsulating details of a search-by-example AssayType search.
 * 
 * @author vaughng
 * Jun 28, 2009
 */
public class AssayTypeSearch extends ExampleSearch
{

    private AssayType assayType;
    private List<String> expectedNames = new ArrayList<String>();
    
    /**
     * 
     */
    public AssayTypeSearch()
    {
        super();
    }

    /* (non-Javadoc)
     * @see caarray.client.test.ExampleSearch#getExample()
     */
    @Override
    public AbstractCaArrayEntity getExample()
    {
        return getAssayType();
    }

    public AssayType getAssayType()
    {
        return assayType;
    }

    public void setAssayType(AssayType assayType)
    {
        this.assayType = assayType;
    }

    public List<String> getExpectedNames()
    {
        return expectedNames;
    }

    public void addExpectedName(String expectedName)
    {
        this.expectedNames.add(expectedName);
    }

}
