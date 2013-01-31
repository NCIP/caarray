//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package caarray.client.test.search;

import java.util.ArrayList;
import java.util.List;

import gov.nih.nci.caarray.external.v1_0.AbstractCaArrayEntity;
import gov.nih.nci.caarray.external.v1_0.vocabulary.TermSource;

/**
 * Bean encapsulating details of a search-by-example Term Source search.
 * 
 * @author vaughng
 * Jun 28, 2009
 */
public class TermSourceSearch extends ExampleSearch
{
    private TermSource termSource;
    private List<String> expectedNames = new ArrayList<String>(), expectedUrls = new ArrayList<String>();
    
    /**
     * 
     */
    public TermSourceSearch()
    {
        super();
    }

    /* (non-Javadoc)
     * @see caarray.client.test.ExampleSearch#getExample()
     */
    @Override
    public AbstractCaArrayEntity getExample()
    {
        return getTermSource();
    }

    public TermSource getTermSource()
    {
        return termSource;
    }

    public void setTermSource(TermSource termSource)
    {
        this.termSource = termSource;
    }

    public List<String> getExpectedNames()
    {
        return expectedNames;
    }
    
    public void addExpectedName(String expectedName)
    {
        expectedNames.add(expectedName);
    }

    public List<String> getExpectedUrls()
    {
        return expectedUrls;
    }
    
    public void addExpectedUrl(String expectedUrl)
    {
        expectedUrls.add(expectedUrl);
    }

}
