//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package caarray.client.test.search;

import gov.nih.nci.caarray.external.v1_0.AbstractCaArrayEntity;
import gov.nih.nci.caarray.external.v1_0.data.QuantitationType;

import java.util.ArrayList;
import java.util.List;

/**
 * Bean encapsulating details of a search-by-example Quantitation Type search.
 * 
 * @author vaughng
 * Jun 28, 2009
 */
public class QuantitationTypeSearch extends ExampleSearch
{
    private QuantitationType quantitationType;
    private List<String> expectedNames = new ArrayList<String>(), expectedDataTypes = new ArrayList<String>();
    
    /**
     * 
     */
    public QuantitationTypeSearch()
    {
        super();
    }

    /* (non-Javadoc)
     * @see caarray.client.test.ExampleSearch#getExample()
     */
    @Override
    public AbstractCaArrayEntity getExample()
    {
        return getQuantitationType();
    }

    public QuantitationType getQuantitationType()
    {
        return quantitationType;
    }

    public void setQuantitationType(QuantitationType quantitationType)
    {
        this.quantitationType = quantitationType;
    }


    public List<String> getExpectedNames()
    {
        return expectedNames;
    }

    public void addExpectedName(String expectedName)
    {
        expectedNames.add(expectedName);
    }

    public List<String> getExpectedDataTypes()
    {
        return expectedDataTypes;
    }

    public void addExpectedDataType(String expectedDataType)
    {
        expectedDataTypes.add(expectedDataType);
    }

}
