/**
 * 
 */
package caarray.client.test.search;

import gov.nih.nci.caarray.external.v1_0.query.FileSearchCriteria;

/**
 * @author vaughng
 * Jul 15, 2009
 */
public class FileCriteriaSearch extends CriteriaSearch
{

    private FileSearchCriteria searchCriteria;
    
    /**
     * 
     */
    public FileCriteriaSearch()
    {
        // TODO Auto-generated constructor stub
    }

    public FileSearchCriteria getFileSearchCriteria()
    {
        return searchCriteria;
    }

    public void setFileSearchCriteria(FileSearchCriteria fileSearchCriteria)
    {
        this.searchCriteria = fileSearchCriteria;
    }

}
