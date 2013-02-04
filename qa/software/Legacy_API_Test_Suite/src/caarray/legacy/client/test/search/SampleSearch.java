//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package caarray.legacy.client.test.search;

import gov.nih.nci.caarray.domain.AbstractCaArrayObject;
import gov.nih.nci.caarray.domain.sample.Sample;

/**
 * @author vaughng
 * Aug 12, 2009
 */
public class SampleSearch extends ExampleSearch
{

    private Sample sample;
    private String expectedExternalId = null;
    
    public SampleSearch(){}
    
    
    /* (non-Javadoc)
     * @see caarray.legacy.client.test.search.ExampleSearch#getExample()
     */
    @Override
    public AbstractCaArrayObject getExample()
    {
        return getSample();
    }


    /**
     * @return the sample
     */
    public Sample getSample()
    {
        return sample;
    }


    /**
     * @param sample the sample to set
     */
    public void setSample(Sample sample)
    {
        this.sample = sample;
    }


    /**
     * @return the expectedExternalId
     */
    public String getExpectedExternalId()
    {
        return expectedExternalId;
    }


    /**
     * @param expectedExternalId the expectedExternalId to set
     */
    public void setExpectedExternalId(String expectedExternalId)
    {
        this.expectedExternalId = expectedExternalId;
    }

}
