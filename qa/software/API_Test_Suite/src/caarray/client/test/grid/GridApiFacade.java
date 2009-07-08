/**
 * 
 */
package caarray.client.test.grid;

import gov.nih.nci.caarray.external.v1_0.AbstractCaArrayEntity;
import gov.nih.nci.caarray.external.v1_0.CaArrayEntityReference;
import gov.nih.nci.caarray.external.v1_0.array.ArrayProvider;
import gov.nih.nci.caarray.external.v1_0.array.AssayType;
import gov.nih.nci.caarray.external.v1_0.experiment.Experiment;
import gov.nih.nci.caarray.external.v1_0.experiment.Organism;
import gov.nih.nci.caarray.external.v1_0.experiment.Person;
import gov.nih.nci.caarray.external.v1_0.query.ExampleSearchCriteria;
import gov.nih.nci.caarray.external.v1_0.query.ExperimentSearchCriteria;
import gov.nih.nci.caarray.external.v1_0.query.KeywordSearchCriteria;
import gov.nih.nci.caarray.external.v1_0.query.LimitOffset;
import gov.nih.nci.caarray.external.v1_0.query.SearchResult;
import gov.nih.nci.caarray.external.v1_0.vocabulary.Category;
import gov.nih.nci.caarray.external.v1_0.vocabulary.Term;
import gov.nih.nci.caarray.services.external.v1_0.UnsupportedCategoryException;
import gov.nih.nci.caarray.services.external.v1_0.grid.client.CaArraySvc_v1_0Client;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

import org.apache.axis.types.URI.MalformedURIException;

import caarray.client.test.ApiFacade;
import caarray.client.test.TestProperties;

/**
 * {@link ApiFacade} implementation that used the grid API.
 * 
 * @author vaughng
 *
 */
public class GridApiFacade implements ApiFacade
{

    private CaArraySvc_v1_0Client gridClient = null;
    
    public GridApiFacade() throws RemoteException, MalformedURIException
    {
        String gridUrl = TestProperties.getGridServiceUrl();
        System.out.println("Connecting to grid service: " + gridUrl);
        gridClient = new CaArraySvc_v1_0Client(gridUrl);
    }
    /* (non-Javadoc)
     * @see caarray.client.test.ApiFacade#getAllPrincipalInvestigators(java.lang.String)
     */
    public List<Person> getAllPrincipalInvestigators(String api) throws Exception
    {
        List<Person> resultsList = new ArrayList<Person>();
        Person[] results = gridClient.getAllPrincipalInvestigators();
        if (results != null)
        {
            for (Person person : results)
                resultsList.add(person);
        }
        return resultsList;
    }

    /* (non-Javadoc)
     * @see caarray.client.test.ApiFacade#getByReference(java.lang.String, gov.nih.nci.caarray.external.v1_0.CaArrayEntityReference)
     */
    public AbstractCaArrayEntity getByReference(String api,
            CaArrayEntityReference reference) throws Exception
    {
        return gridClient.getByReference(reference);
    }

    /* (non-Javadoc)
     * @see caarray.client.test.ApiFacade#getTermsForCategory(java.lang.String, gov.nih.nci.caarray.external.v1_0.CaArrayEntityReference, java.lang.String)
     */
    public List<Term> getTermsForCategory(String api,
            CaArrayEntityReference categoryRef, String valuePrefix) throws Exception
    {
        List<Term> resultsList = new ArrayList<Term>();
        Term[] result = gridClient.getTermsForCategory(categoryRef, null);
        if (result != null)
        {
            for (Term term : result)
            {
                resultsList.add(term);  
            }
        } 
        return resultsList;
    }

    /* (non-Javadoc)
     * @see caarray.client.test.ApiFacade#getbyReferences(java.lang.String, java.util.List)
     */
    public List<AbstractCaArrayEntity> getByReferences(String api,
            List<CaArrayEntityReference> references) throws Exception
    {
        CaArrayEntityReference refs[] = new CaArrayEntityReference[references.size()];
        for (int i = 0; i < references.size(); i++)
        {
            refs[i] = references.get(i);
        }
        AbstractCaArrayEntity[] results = gridClient.getByReferences(refs);
        List<AbstractCaArrayEntity> resultsList = new ArrayList<AbstractCaArrayEntity>();
        for (AbstractCaArrayEntity result : results)
        {
            resultsList.add(result);
        }
        return resultsList;
    }

    /* (non-Javadoc)
     * @see caarray.client.test.ApiFacade#searchByExample(java.lang.String, gov.nih.nci.caarray.external.v1_0.query.ExampleSearchCriteria, gov.nih.nci.caarray.external.v1_0.query.LimitOffset)
     */
    public SearchResult<? extends AbstractCaArrayEntity> searchByExample(
            String api,
            ExampleSearchCriteria<? extends AbstractCaArrayEntity> criteria,
            LimitOffset offset) throws Exception
    {
        return gridClient.searchByExample(criteria, offset);
    }
    public CaArrayEntityReference getCategoryReference(String api,
            String categoryName) throws Exception
    {
        ExampleSearchCriteria<Category> criteria = new ExampleSearchCriteria<Category>();
        Category exampleCategory = new Category();
        exampleCategory.setName(categoryName);
        criteria.setExample(exampleCategory);
        try
        {
            SearchResult<Category> results = (SearchResult<Category>) searchByExample(
                    api, criteria, null);
            List<Category> categories = results.getResults();
            if (!categories.isEmpty())
                return categories.get(0).getReference();
        }
        catch (UnsupportedCategoryException e)
        {
            System.out.println("Unsupported category: " + e.getMessage());
        }
        
        return null;
    }
    public ArrayProvider getArrayProvider(String api, String providerName)
            throws Exception
    {
        ArrayProvider provider = new ArrayProvider();
        provider.setName(providerName);
        ExampleSearchCriteria<ArrayProvider> providerCriteria = new ExampleSearchCriteria<ArrayProvider>();
        providerCriteria.setExample(provider);
        SearchResult<ArrayProvider> results = (SearchResult<ArrayProvider>)searchByExample(api, providerCriteria, null);
        if (!results.getResults().isEmpty())
            return results.getResults().get(0);
        return null;
    }
    public Organism getOrganism(String api, String scientificName,
            String commonName) throws Exception
    {
        ExampleSearchCriteria<Organism> organismCriteria = new ExampleSearchCriteria<Organism>();
        Organism exampleOrganism = new Organism();
        exampleOrganism.setCommonName(commonName);
        exampleOrganism.setScientificName(scientificName);
        organismCriteria.setExample(exampleOrganism);
        SearchResult<Organism> organisms = (SearchResult<Organism>)searchByExample(api,
                organismCriteria, null);
        if (!organisms.getResults().isEmpty())
            return organisms.getResults().get(0);
        return null;
    }
    public AssayType getAssayType(String api, String type) throws Exception
    {
        ExampleSearchCriteria<AssayType> assayCriteria = new ExampleSearchCriteria<AssayType>();
        AssayType assay = new AssayType();
        assay.setName(type);
        assayCriteria.setExample(assay);
        SearchResult<AssayType> assays = (SearchResult<AssayType>)searchByExample(api,
                assayCriteria, null);
        if (!assays.getResults().isEmpty())
            return assays.getResults().get(0);
        return null;
    }
    public SearchResult<? extends AbstractCaArrayEntity> searchForExperiments(
            String api, ExperimentSearchCriteria criteria, LimitOffset offset)
            throws Exception
    {
        return gridClient.searchForExperiments(criteria, offset);
    }
    public SearchResult<Experiment> searchForExperimentByKeyword(String api,
            KeywordSearchCriteria criteria, LimitOffset limitOffset) throws Exception
    {
        return gridClient.searchForExperimentsByKeyword(criteria, limitOffset);
    }
    public List<Category> getAllCharacteristicCategories(String api,
            CaArrayEntityReference reference) throws Exception
    {
        List<Category> resultsList = new ArrayList<Category>();
        Category[] categories = gridClient.getAllCharacteristicCategories(reference);
        if (categories != null)
        {
            for (Category category : categories)
            {
                resultsList.add(category);
            }
        }
        return resultsList;
    }
    
    

}
