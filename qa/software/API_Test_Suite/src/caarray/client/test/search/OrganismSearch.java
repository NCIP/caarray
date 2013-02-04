//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package caarray.client.test.search;

import gov.nih.nci.caarray.external.v1_0.AbstractCaArrayEntity;
import gov.nih.nci.caarray.external.v1_0.experiment.Organism;

import java.util.ArrayList;
import java.util.List;

/**
 * Bean encapsulating details of a search-by-example Organism search.
 * 
 * @author vaughng
 * Jun 28, 2009
 */
public class OrganismSearch extends ExampleSearch
{

    private Organism organism;
    private List<String> expectedCommonNames = new ArrayList<String>(), expectedScientificNames = new ArrayList<String>();
    
    /**
     * 
     */
    public OrganismSearch()
    {
        super();
    }

    /* (non-Javadoc)
     * @see caarray.client.test.ExampleSearch#getExample()
     */
    @Override
    public AbstractCaArrayEntity getExample()
    {
        return getOrganism();
    }

    public Organism getOrganism()
    {
        return organism;
    }

    public void setOrganism(Organism organism)
    {
        this.organism = organism;
    }

    public List<String> getExpectedCommonNames()
    {
        return expectedCommonNames;
    }

    public void addExpectedCommonName(String expectedCommonName)
    {
        expectedCommonNames.add(expectedCommonName);
    }

    public List<String> getExpectedScientificNames()
    {
        return expectedScientificNames;
    }

    public void addExpectedScientificName(String expectedScientificName)
    {
        expectedScientificNames.add(expectedScientificName);
    }

    public Integer getMinResults()
    {
        return minResults;
    }

    public void setMinResults(Integer minResults)
    {
        this.minResults = minResults;
    }

}
