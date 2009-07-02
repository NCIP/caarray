/**
 * 
 */
package caarray.client.test.java;

import gov.nih.nci.caarray.external.v1_0.AbstractCaArrayEntity;
import gov.nih.nci.caarray.external.v1_0.CaArrayEntityReference;
import gov.nih.nci.caarray.external.v1_0.experiment.Person;
import gov.nih.nci.caarray.external.v1_0.query.ExampleSearchCriteria;
import gov.nih.nci.caarray.external.v1_0.query.LimitOffset;
import gov.nih.nci.caarray.external.v1_0.query.SearchResult;
import gov.nih.nci.caarray.external.v1_0.vocabulary.Category;
import gov.nih.nci.caarray.external.v1_0.vocabulary.Term;
import gov.nih.nci.caarray.services.ServerConnectionException;
import gov.nih.nci.caarray.services.external.v1_0.CaArrayServer;
import gov.nih.nci.caarray.services.external.v1_0.search.SearchService;

import java.util.List;

import caarray.client.test.ApiFacade;
import caarray.client.test.TestProperties;

/**
 * {@link ApiFacade} implementation that used the java API.
 * 
 * @author vaughng
 *
 */
public class JavaApiFacade implements ApiFacade
{

    
    private SearchService javaSearchService = null;
    
    public JavaApiFacade() throws ServerConnectionException
    {
        String hostName = TestProperties.getJavaServerHostname();
        int port = TestProperties.getJavaServerJndiPort();
        System.out.println("Connecting to java server: " + hostName + ":" + port);
        CaArrayServer server = new CaArrayServer(hostName, port);
        server.connect();
        javaSearchService = server.getSearchService();
    }
    
    public List<Person> getAllPrincipalInvestigators(String api)
            throws Exception
    {
        return javaSearchService.getAllPrincipalInvestigators();
    }

    public AbstractCaArrayEntity getByReference(String api,
            CaArrayEntityReference reference) throws Exception
    {
        return javaSearchService.getByReference(reference);
    }

    public List<AbstractCaArrayEntity> getByReferences(String api,
            List<CaArrayEntityReference> references) throws Exception
    {
        return javaSearchService.getByReferences(references);
    }

    public List<Term> getTermsForCategory(String api,
            CaArrayEntityReference categoryRef, String valuePrefix)
            throws Exception
    {
        return javaSearchService.getTermsForCategory(categoryRef, valuePrefix);
    }

    public SearchResult<? extends AbstractCaArrayEntity> searchByExample(
            String api,
            ExampleSearchCriteria<? extends AbstractCaArrayEntity> criteria,
            LimitOffset offset) throws Exception
    {
        return javaSearchService.searchByExample(criteria, offset);
    }

    public CaArrayEntityReference getCategoryReference(String api,
            String categoryName) throws Exception
    {
        ExampleSearchCriteria<Category> criteria = new ExampleSearchCriteria<Category>();
        Category exampleCategory = new Category();
        exampleCategory.setName(categoryName);
        criteria.setExample(exampleCategory);
        SearchResult<Category> results = (SearchResult<Category>) searchByExample(
                api, criteria, null);
        List<Category> categories = results.getResults();
        if (!categories.isEmpty())
            return categories.get(0).getReference();
        return null;
    }

    
}
