//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
package caarray.client.test.search;

import gov.nih.nci.caarray.external.v1_0.AbstractCaArrayEntity;
import gov.nih.nci.caarray.external.v1_0.data.File;

/**
 * Bean encapsulating details of a search-by-example File search.
 * 
 * @author vaughng
 * Jun 28, 2009
 */
public class FileSearch extends ExampleSearch
{

    private File dataFile;
    
    /**
     * 
     */
    public FileSearch()
    {
        super();
    }

    /* (non-Javadoc)
     * @see caarray.client.test.ExampleSearch#getExample()
     */
    @Override
    public AbstractCaArrayEntity getExample()
    {
        return getDataFile();
    }

    public File getDataFile()
    {
        return dataFile;
    }

    public void setDataFile(File dataFile)
    {
        this.dataFile = dataFile;
    }

}
