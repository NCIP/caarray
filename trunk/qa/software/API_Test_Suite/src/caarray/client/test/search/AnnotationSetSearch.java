/**
 * 
 */
package caarray.client.test.search;

import java.util.ArrayList;
import java.util.List;

import gov.nih.nci.caarray.external.v1_0.query.AnnotationSetRequest;

/**
 * @author vaughng
 * Jul 13, 2009
 */
public class AnnotationSetSearch extends CriteriaSearch
{

    private AnnotationSetRequest annotationSetRequest;
    private List<String> expectedCategories = new ArrayList<String>();
    
    /**
     * 
     */
    public AnnotationSetSearch()
    {
        super();
    }

    public AnnotationSetRequest getAnnotationSetRequest()
    {
        return annotationSetRequest;
    }

    public void setAnnotationSetRequest(AnnotationSetRequest annotationSetRequest)
    {
        this.annotationSetRequest = annotationSetRequest;
    }

    public List<String> getExpectedCategories()
    {
        return expectedCategories;
    }
    
    public void addExpectedCategory(String category)
    {
        expectedCategories.add(category);
    }

}
