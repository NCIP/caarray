//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
package caarray.client.test.search;

import gov.nih.nci.caarray.external.v1_0.AbstractCaArrayEntity;
import gov.nih.nci.caarray.external.v1_0.data.FileType;

import java.util.ArrayList;
import java.util.List;

/**
 * Bean encapsulating details of a search-by-example FileType search.
 * 
 * @author vaughng
 * Jun 28, 2009
 */
public class FileTypeSearch extends ExampleSearch
{
    private FileType fileType;
    private List<String> expectedNames = new ArrayList<String>(), expectedCategories = new ArrayList<String>();
    
    /**
     * 
     */
    public FileTypeSearch()
    {
        super();
    }

    @Override
    public AbstractCaArrayEntity getExample()
    {
        return getFileType();
    }

    public FileType getFileType()
    {
        return fileType;
    }

    public void setFileType(FileType fileType)
    {
        this.fileType = fileType;
    }

    public List<String> getExpectedNames()
    {
        return expectedNames;
    }

    public void addExpectedName(String expectedName)
    {
        expectedNames.add(expectedName);
    }

    public List<String> getExpectedCategories()
    {
        return expectedCategories;
    }

    public void addExpectedCategory(String expectedCategory)
    {
        expectedCategories.add(expectedCategory);
    }

}
