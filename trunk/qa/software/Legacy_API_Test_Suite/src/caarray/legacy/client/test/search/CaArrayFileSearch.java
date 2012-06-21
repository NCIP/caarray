/**
 * 
 */
package caarray.legacy.client.test.search;

import gov.nih.nci.caarray.domain.AbstractCaArrayObject;
import gov.nih.nci.caarray.domain.file.CaArrayFile;

/**
 * @author vaughng
 * Aug 12, 2009
 */
public class CaArrayFileSearch extends ExampleSearch
{
    private CaArrayFile caArrayFile;
    
    /**
     * 
     */
    public CaArrayFileSearch()
    {
       super();
    }

    /* (non-Javadoc)
     * @see caarray.legacy.client.test.search.ExampleSearch#getExample()
     */
    @Override
    public AbstractCaArrayObject getExample()
    {
        return getCaArrayFile();
    }

    /**
     * @return the caArrayFile
     */
    public CaArrayFile getCaArrayFile()
    {
        return caArrayFile;
    }

    /**
     * @param caArrayFile the caArrayFile to set
     */
    public void setCaArrayFile(CaArrayFile caArrayFile)
    {
        this.caArrayFile = caArrayFile;
    }

}
