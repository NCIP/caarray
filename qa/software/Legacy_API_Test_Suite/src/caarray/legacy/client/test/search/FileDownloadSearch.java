//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
package caarray.legacy.client.test.search;

import gov.nih.nci.caarray.domain.AbstractCaArrayObject;
import gov.nih.nci.caarray.domain.array.ArrayDesign;
import gov.nih.nci.caarray.domain.file.CaArrayFile;

/**
 * @author vaughng
 * Aug 14, 2009
 */
public class FileDownloadSearch extends ExampleSearch
{

    private CaArrayFile file;
    private ArrayDesign arrayDesign;
    private Integer expectedBytes = null, minBytes = null;
    
    /* (non-Javadoc)
     * @see caarray.legacy.client.test.search.ExampleSearch#getExample()
     */
    @Override
    public AbstractCaArrayObject getExample()
    {
        // TODO Auto-generated method stub
        return null;
    }
    /**
     * @return the file
     */
    public CaArrayFile getFile()
    {
        return file;
    }
    /**
     * @param file the file to set
     */
    public void setFile(CaArrayFile file)
    {
        this.file = file;
    }
    /**
     * @return the arrayDesign
     */
    public ArrayDesign getArrayDesign()
    {
        return arrayDesign;
    }
    /**
     * @param arrayDesign the arrayDesign to set
     */
    public void setArrayDesign(ArrayDesign arrayDesign)
    {
        this.arrayDesign = arrayDesign;
    }
    /**
     * @return the expectedBytes
     */
    public Integer getExpectedBytes()
    {
        return expectedBytes;
    }
    /**
     * @param expectedBytes the expectedBytes to set
     */
    public void setExpectedBytes(Integer expectedBytes)
    {
        this.expectedBytes = expectedBytes;
    }
    /**
     * @return the minBytes
     */
    public Integer getMinBytes()
    {
        return minBytes;
    }
    /**
     * @param minBytes the minBytes to set
     */
    public void setMinBytes(Integer minBytes)
    {
        this.minBytes = minBytes;
    }
}
