//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package caarray.legacy.client.test.search;

import gov.nih.nci.caarray.domain.AbstractCaArrayObject;
import gov.nih.nci.caarray.domain.project.AssayType;

/**
 * @author vaughng
 * Aug 12, 2009
 */
public class AssayTypeSearch extends ExampleSearch
{

    private AssayType assayType;
    /**
     * 
     */
    public AssayTypeSearch()
    {
        super();
    }

    /* (non-Javadoc)
     * @see caarray.legacy.client.test.search.ExampleSearch#getExample()
     */
    @Override
    public AbstractCaArrayObject getExample()
    {
       return getAssayType();
    }

    /**
     * @return the assayType
     */
    public AssayType getAssayType()
    {
        return assayType;
    }

    /**
     * @param assayType the assayType to set
     */
    public void setAssayType(AssayType assayType)
    {
        this.assayType = assayType;
    }

}
