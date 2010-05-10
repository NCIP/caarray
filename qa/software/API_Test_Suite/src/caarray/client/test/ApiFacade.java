/**
 * 
 */
package caarray.client.test;

import gov.nih.nci.caarray.external.v1_0.AbstractCaArrayEntity;
import gov.nih.nci.caarray.external.v1_0.CaArrayEntityReference;
import gov.nih.nci.caarray.external.v1_0.array.ArrayProvider;
import gov.nih.nci.caarray.external.v1_0.array.AssayType;
import gov.nih.nci.caarray.external.v1_0.data.ArrayDataType;
import gov.nih.nci.caarray.external.v1_0.data.DataSet;
import gov.nih.nci.caarray.external.v1_0.data.File;
import gov.nih.nci.caarray.external.v1_0.data.MageTabFileSet;
import gov.nih.nci.caarray.external.v1_0.data.QuantitationType;
import gov.nih.nci.caarray.external.v1_0.experiment.Experiment;
import gov.nih.nci.caarray.external.v1_0.experiment.Organism;
import gov.nih.nci.caarray.external.v1_0.experiment.Person;
import gov.nih.nci.caarray.external.v1_0.query.AnnotationSetRequest;
import gov.nih.nci.caarray.external.v1_0.query.BiomaterialKeywordSearchCriteria;
import gov.nih.nci.caarray.external.v1_0.query.BiomaterialSearchCriteria;
import gov.nih.nci.caarray.external.v1_0.query.DataSetRequest;
import gov.nih.nci.caarray.external.v1_0.query.ExampleSearchCriteria;
import gov.nih.nci.caarray.external.v1_0.query.ExperimentSearchCriteria;
import gov.nih.nci.caarray.external.v1_0.query.FileSearchCriteria;
import gov.nih.nci.caarray.external.v1_0.query.HybridizationSearchCriteria;
import gov.nih.nci.caarray.external.v1_0.query.KeywordSearchCriteria;
import gov.nih.nci.caarray.external.v1_0.query.LimitOffset;
import gov.nih.nci.caarray.external.v1_0.query.QuantitationTypeSearchCriteria;
import gov.nih.nci.caarray.external.v1_0.query.SearchResult;
import gov.nih.nci.caarray.external.v1_0.sample.AnnotationSet;
import gov.nih.nci.caarray.external.v1_0.sample.Biomaterial;
import gov.nih.nci.caarray.external.v1_0.sample.Hybridization;
import gov.nih.nci.caarray.external.v1_0.vocabulary.Category;
import gov.nih.nci.caarray.external.v1_0.vocabulary.Term;

import java.util.List;

/**
 * Hides the details of the API being used by providing
 * generic search and retrieval methods. Methods correspond directly to API methods.
 * Used for transparent testing of one or both APIs.
 * 
 * @author vaughng
 *
 */
public interface ApiFacade
{

    public void connect() throws Exception;
    
    public <T extends AbstractCaArrayEntity> SearchResult<T> searchByExample(String api, 
            ExampleSearchCriteria<T> criteria, LimitOffset offset) throws Exception;
    
    public <T extends AbstractCaArrayEntity> List<T> searchByExampleUtils(String api, 
            ExampleSearchCriteria<T> criteria) throws Exception;
    
    public SearchResult<? extends AbstractCaArrayEntity> searchForExperiments(String api, ExperimentSearchCriteria criteria, 
            LimitOffset offset, boolean login) throws Exception;
    
    public SearchResult<Experiment> searchForExperimentByKeyword(String api, KeywordSearchCriteria criteria, LimitOffset limitOffset) throws Exception;

    public SearchResult<? extends AbstractCaArrayEntity> searchForBiomaterials(String api, BiomaterialSearchCriteria criteria,
            LimitOffset offset) throws Exception;
    
    public SearchResult<Biomaterial> searchForBiomaterialByKeyword(String api, BiomaterialKeywordSearchCriteria criteria, LimitOffset limitOffset) throws Exception;

    public SearchResult<? extends AbstractCaArrayEntity> searchForFiles(String api, FileSearchCriteria criteria,
            LimitOffset offset) throws Exception;
    
    public SearchResult<? extends AbstractCaArrayEntity> searchForHybridizations(String api, HybridizationSearchCriteria criteria,
            LimitOffset offset) throws Exception;
    
    public List<QuantitationType> searchForQuantitationTypes(String api, QuantitationTypeSearchCriteria criteria,
            LimitOffset offset) throws Exception;
    
    public List<Experiment> experimentsByCriteriaSearchUtils(String api, ExperimentSearchCriteria criteria) throws Exception;
    
    public List<Experiment> experimentsByKeywordSearchUtils(String api, KeywordSearchCriteria criteria) throws Exception;
    
    public List<Biomaterial> biomaterialsByCriteriaSearchUtils(String api, BiomaterialSearchCriteria criteria) throws Exception;
    
    public List<Biomaterial> biomaterialsByKeywordSearchUtils(String api, BiomaterialKeywordSearchCriteria criteria) throws Exception;
    
    public List<File> filesByCriteriaSearchUtils(String api, FileSearchCriteria criteria) throws Exception;
    
    public List<Experiment> enumerateExperiments(String api, ExperimentSearchCriteria criteria) throws Exception;

    public List<Experiment> enumerateExperimentsByKeyword(String api, KeywordSearchCriteria criteria) throws Exception;
    
    public List<Biomaterial> enumerateBiomaterials(String api, BiomaterialSearchCriteria criteria) throws Exception;

    public List<Biomaterial> enumerateBiomaterialsByKeyword(String api, BiomaterialKeywordSearchCriteria criteria) throws Exception;
    
    public List<Hybridization> enumerateHybridizations(String api, HybridizationSearchCriteria criteria) throws Exception;
    
    public List<File> enumerateFiles(String api, FileSearchCriteria criteria) throws Exception;
    
    public List<? extends AbstractCaArrayEntity> enumerateByExample(String api, 
            ExampleSearchCriteria<? extends AbstractCaArrayEntity> criteria, Class<? extends AbstractCaArrayEntity> clazz) throws Exception;
    
    public List<File> getFilesByName(String api, List<String> fileNames, String experimentName) throws Exception;
    
    public List<Hybridization> hybridizationsByCriteriaSearchUtils(String api, HybridizationSearchCriteria criteria) throws Exception;
     
    public List<Person> getAllPrincipalInvestigators(String api) throws Exception;
    
    public List<Term> getTermsForCategory(String api, CaArrayEntityReference categoryRef, String valuePrefix) throws Exception;
    
    public CaArrayEntityReference getCategoryReference(String api, String categoryName) throws Exception;
    
    public ArrayProvider getArrayProvider(String api, String providerName) throws Exception;
    
    public Organism getOrganism(String api, String scientificName, String commonName) throws Exception;
    
    public Hybridization getHybridization(String api, String name) throws Exception;
    
    public ArrayDataType getArrayDataType(String api, String name) throws Exception;

    public Biomaterial getBiomaterial(String api, String name) throws Exception;
    
    public QuantitationType getQuantitationType(String api, String name) throws Exception;
    
    public AssayType getAssayType(String api, String type) throws Exception;
    
    public Experiment getExperiment(String api, String title) throws Exception;
    
    public List<Category> getAllCharacteristicCategories(String api, CaArrayEntityReference reference) throws Exception;
    
    public AnnotationSet getAnnotationSet(String api, AnnotationSetRequest annotationSetRequest) throws Exception;
    
    public DataSet getDataSet(String api, DataSetRequest dataSetRequest) throws Exception;
    
    public Integer getFileContents(String api, List<CaArrayEntityReference> fileReferences, boolean compressed) throws Exception;
    
    public Long getFileContentsZip(String api, List<CaArrayEntityReference> fileReferences, boolean compressed) throws Exception;
    
    public Integer copyFileContentsUtils(String api, List<CaArrayEntityReference> fileReferences, boolean compressed) throws Exception;
    
    public Integer copyFileContentsZipUtils(String api, List<CaArrayEntityReference> fileReferences, boolean compressed) throws Exception;
    
    public MageTabFileSet getMageTabExport(String api, CaArrayEntityReference experimentReference) throws Exception;
    
    public Long copyMageTabZipToOutputStream(String api, CaArrayEntityReference experimentReference, boolean compressed) throws Exception;
    
    public Long copyMageTabZipApiUtils(String api, CaArrayEntityReference experimentReference, boolean compressed) throws Exception;
}
