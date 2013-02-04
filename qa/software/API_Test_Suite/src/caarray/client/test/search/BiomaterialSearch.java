//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package caarray.client.test.search;

import gov.nih.nci.caarray.external.v1_0.AbstractCaArrayEntity;
import gov.nih.nci.caarray.external.v1_0.sample.Biomaterial;
import gov.nih.nci.caarray.external.v1_0.sample.BiomaterialType;

import java.util.ArrayList;
import java.util.List;

/**
 * Bean encapsulating details of a search-by-example Biomaterial search.
 * 
 * @author vaughng
 * Jun 28, 2009
 */
public class BiomaterialSearch extends ExampleSearch
{
    private Biomaterial biomaterial;
    private List<BiomaterialType> expectedType = new ArrayList<BiomaterialType>();
    private List<String> expectedExternalId = new ArrayList<String>();
    /**
     * 
     */
    public BiomaterialSearch()
    {
        super();
    }

    /* (non-Javadoc)
     * @see caarray.client.test.ExampleSearch#getExample()
     */
    @Override
    public AbstractCaArrayEntity getExample()
    {
        return getBiomaterial();
    }

    public Biomaterial getBiomaterial()
    {
        return biomaterial;
    }

    public void setBiomaterial(Biomaterial biomaterial)
    {
        this.biomaterial = biomaterial;
    }

    public List<BiomaterialType> getExpectedType()
    {
        return expectedType;
    }

    public void addExpectedType(BiomaterialType expectedType)
    {
        this.expectedType.add(expectedType);
    }

    public List<String> getExpectedExternalId()
    {
        return expectedExternalId;
    }

    public void addExpectedExternalId(String expectedExternalId)
    {
        this.expectedExternalId.add(expectedExternalId);
    }

}
