/**
 * 
 */
package caarray.client.test;

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
import gov.nih.nci.caarray.external.v1_0.vocabulary.Term;

import java.util.List;

/**
 * Hides the details of the API being used by providing
 * generic search and retrieval methods.
 * Used for transparent testing of one or both APIs.
 * 
 * @author vaughng
 *
 */
public interface ApiFacade
{

    public SearchResult<? extends AbstractCaArrayEntity> searchByExample(String api, 
            ExampleSearchCriteria<? extends AbstractCaArrayEntity> criteria, LimitOffset offset) throws Exception;
    
    public SearchResult<? extends AbstractCaArrayEntity> searchForExperiments(String api, ExperimentSearchCriteria criteria, 
            LimitOffset offset) throws Exception;
    
    public List<Person> getAllPrincipalInvestigators(String api) throws Exception;
    
    public List<Term> getTermsForCategory(String api, CaArrayEntityReference categoryRef, String valuePrefix) throws Exception;
    
    public AbstractCaArrayEntity getByReference(String api, CaArrayEntityReference reference) throws Exception;
    
    public List<AbstractCaArrayEntity> getByReferences(String api, List<CaArrayEntityReference> references) throws Exception;
    
    public CaArrayEntityReference getCategoryReference(String api, String categoryName) throws Exception;
    
    public ArrayProvider getArrayProvider(String api, String providerName) throws Exception;
    
    public Organism getOrganism(String api, String scientificName, String commonName) throws Exception;
    
    public AssayType getAssayType(String api, String type) throws Exception;
    
    public SearchResult<Experiment> searchForExperimentByKeyword(String api, KeywordSearchCriteria criteria, LimitOffset limitOffset) throws Exception;
}
