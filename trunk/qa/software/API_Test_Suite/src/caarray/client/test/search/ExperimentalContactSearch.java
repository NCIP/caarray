//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package caarray.client.test.search;

import gov.nih.nci.caarray.external.v1_0.AbstractCaArrayEntity;
import gov.nih.nci.caarray.external.v1_0.experiment.ExperimentalContact;

/**
 * @author vaughng
 * Jul 10, 2009
 */
public class ExperimentalContactSearch extends ExampleSearch
{

    private ExperimentalContact experimentalContact;
    
    /**
     * 
     */
    public ExperimentalContactSearch()
    {
        super();
    }

    /* (non-Javadoc)
     * @see caarray.client.test.search.ExampleSearch#getExample()
     */
    @Override
    public AbstractCaArrayEntity getExample()
    {
        return getExperimentalContact();
    }

    public ExperimentalContact getExperimentalContact()
    {
        return experimentalContact;
    }

    public void setExperimentalContact(ExperimentalContact experimentalContact)
    {
        this.experimentalContact = experimentalContact;
    }

}
