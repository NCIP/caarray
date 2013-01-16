//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
package caarray.client.test.search;

import gov.nih.nci.caarray.external.v1_0.query.ExperimentSearchCriteria;

import java.util.ArrayList;
import java.util.List;

/**
 * @author vaughng
 * Jul 2, 2009
 */
public class ExperimentCriteriaSearch extends CriteriaSearch
{

    private ExperimentSearchCriteria experimentSearchCriteria = null;
    private List<String> expectedTitle = new ArrayList<String>();
    private List<String> expectedAssayType = new ArrayList<String>();
    private List<String> expectedProvider = new ArrayList<String>();
    private List<String> expectedOrganismScientificName = new ArrayList<String>();
    
    
    /**
     * 
     */
    public ExperimentCriteriaSearch()
    {
        super();
    }

    public ExperimentSearchCriteria getExperimentSearchCriteria()
    {
        return experimentSearchCriteria;
    }

    public void setExperimentSearchCriteria(ExperimentSearchCriteria experimentSearchCriteria)
    {
        this.experimentSearchCriteria = experimentSearchCriteria;
    }

    public List<String> getExpectedTitle()
    {
        return expectedTitle;
    }
    
    public void addExpectedTitle(String title)
    {
        expectedTitle.add(title);
    }

    public List<String> getExpectedAssayType()
    {
        return expectedAssayType;
    }

    public void addExpectedAssayType(String assayType)
    {
        expectedAssayType.add(assayType);
    }
    
    public List<String> getExpectedProvider()
    {
        return expectedProvider;
    }

    public void addExpectedProvider(String provider)
    {
        expectedProvider.add(provider);
    }
    
    public List<String> getExpectedOrganismScientificName()
    {
        return expectedOrganismScientificName;
    }

    public void addExpectedOrganismScientificName(String name)
    {
        expectedOrganismScientificName.add(name);
    }

}
