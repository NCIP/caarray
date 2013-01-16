//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
package caarray.client.test.search;

import gov.nih.nci.caarray.external.v1_0.AbstractCaArrayEntity;
import gov.nih.nci.caarray.external.v1_0.data.ArrayDataType;

/**
 * Simple bean encapsulating the details of an ArrayDataType search-by-example.
 * 
 * @author vaughng 
 * Jun 26, 2009
 */
public class ArrayDataTypeSearch extends ExampleSearch
{

    private ArrayDataType arrayDataType;
    private Integer expectedQuantitations = null;

    public ArrayDataTypeSearch()
    {
        super();
    }

    public void setArrayDataType(ArrayDataType arrayDataType)
    {
        this.arrayDataType = arrayDataType;
    }

    public Integer getExpectedQuantitations()
    {
        return expectedQuantitations;
    }

    public void setExpectedQuantitations(Integer expectedQuantitations)
    {
        this.expectedQuantitations = expectedQuantitations;
    }

    public ArrayDataType getArrayDataType()
    {
        return arrayDataType;
    }

    @Override
    public AbstractCaArrayEntity getExample()
    {
        return getArrayDataType();
    }

}
