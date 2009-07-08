/**
 * 
 */
package caarray.client.test.full;

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
import gov.nih.nci.caarray.services.ServerConnectionException;

import java.rmi.RemoteException;
import java.util.List;

import org.apache.axis.types.URI.MalformedURIException;

import caarray.client.test.ApiFacade;
import caarray.client.test.TestProperties;
import caarray.client.test.grid.GridApiFacade;
import caarray.client.test.java.JavaApiFacade;

/**
 * Provides access to both java and grid APIs for methods found
 * in the ApiFacade interface. The API used is determined by an
 * argument passed by the caller.
 * 
 * @author vaughng
 *
 */
public class FullApiFacade implements ApiFacade
{

    private JavaApiFacade javaApiFacade;
    private GridApiFacade gridApiFacade;
    
    
    public FullApiFacade() throws ServerConnectionException, RemoteException, MalformedURIException
    {
        javaApiFacade = new JavaApiFacade();
        gridApiFacade = new GridApiFacade();
    }
    
    private ApiFacade getFacade(String api)
    {
        if (api.equalsIgnoreCase(TestProperties.API_JAVA))
            return javaApiFacade;
        if (api.equalsIgnoreCase(TestProperties.API_GRID))
            return gridApiFacade;
        
        System.out.println("No api specified for FullApiFacade ...");
        return null;
    }
    /* (non-Javadoc)
     * @see caarray.client.test.ApiFacade#getAllPrincipalInvestigators(java.lang.String)
     */
    public List<Person> getAllPrincipalInvestigators(String api) throws Exception
    {
        return getFacade(api).getAllPrincipalInvestigators(api);
    }

    /* (non-Javadoc)
     * @see caarray.client.test.ApiFacade#getByReference(java.lang.String, gov.nih.nci.caarray.external.v1_0.CaArrayEntityReference)
     */
    public AbstractCaArrayEntity getByReference(String api,
            CaArrayEntityReference reference) throws Exception
    {
        return getFacade(api).getByReference(api, reference);
    }

    /* (non-Javadoc)
     * @see caarray.client.test.ApiFacade#getTermsForCategory(java.lang.String, gov.nih.nci.caarray.external.v1_0.CaArrayEntityReference, java.lang.String)
     */
    public List<Term> getTermsForCategory(String api,
            CaArrayEntityReference categoryRef, String valuePrefix) throws Exception
    {
        return getFacade(api).getTermsForCategory(api, categoryRef, valuePrefix);
    }

    /* (non-Javadoc)
     * @see caarray.client.test.ApiFacade#getbyReferences(java.lang.String, java.util.List)
     */
    public List<AbstractCaArrayEntity> getByReferences(String api,
            List<CaArrayEntityReference> references) throws Exception
    {
        return getFacade(api).getByReferences(api, references);
    }

    /* (non-Javadoc)
     * @see caarray.client.test.ApiFacade#searchByExample(java.lang.String, gov.nih.nci.caarray.external.v1_0.query.ExampleSearchCriteria, gov.nih.nci.caarray.external.v1_0.query.LimitOffset)
     */
    public SearchResult<? extends AbstractCaArrayEntity> searchByExample(
            String api,
            ExampleSearchCriteria<? extends AbstractCaArrayEntity> criteria,
            LimitOffset offset) throws Exception
    {
        return getFacade(api).searchByExample(api, criteria, offset);
    }

    public CaArrayEntityReference getCategoryReference(String api,
            String categoryName) throws Exception
    {
        return getFacade(api).getCategoryReference(api, categoryName);
    }

    public ArrayProvider getArrayProvider(String api, String providerName)
            throws Exception
    {
        return getFacade(api).getArrayProvider(api, providerName);
    }

    public Organism getOrganism(String api, String scientificName,
            String commonName) throws Exception
    {
        return getFacade(api).getOrganism(api, scientificName, commonName);
    }

    public AssayType getAssayType(String api, String type) throws Exception
    {
        return getFacade(api).getAssayType(api, type);
    }

    public SearchResult<? extends AbstractCaArrayEntity> searchForExperiments(
            String api, ExperimentSearchCriteria criteria, LimitOffset offset)
            throws Exception
    {
        return getFacade(api).searchForExperiments(api, criteria, offset);
    }

    public SearchResult<Experiment> searchForExperimentByKeyword(String api,
            KeywordSearchCriteria criteria, LimitOffset limitOffset) throws Exception
    {
        return getFacade(api).searchForExperimentByKeyword(api, criteria, limitOffset);
    }

    public List<Category> getAllCharacteristicCategories(String api,
            CaArrayEntityReference reference) throws Exception
    {
        return getFacade(api).getAllCharacteristicCategories(api, reference);
    }
    
    

}
