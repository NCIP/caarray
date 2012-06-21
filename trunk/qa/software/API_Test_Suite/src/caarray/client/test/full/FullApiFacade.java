/**
 * 
 */
package caarray.client.test.full;

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
    
    public void connect() throws Exception
    {
        javaApiFacade.connect();
        gridApiFacade.connect();
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
     * @see caarray.client.test.ApiFacade#getTermsForCategory(java.lang.String, gov.nih.nci.caarray.external.v1_0.CaArrayEntityReference, java.lang.String)
     */
    public List<Term> getTermsForCategory(String api,
            CaArrayEntityReference categoryRef, String valuePrefix) throws Exception
    {
        return getFacade(api).getTermsForCategory(api, categoryRef, valuePrefix);
    }

    /* (non-Javadoc)
     * @see caarray.client.test.ApiFacade#searchByExample(java.lang.String, gov.nih.nci.caarray.external.v1_0.query.ExampleSearchCriteria, gov.nih.nci.caarray.external.v1_0.query.LimitOffset)
     */
    public <T extends AbstractCaArrayEntity> SearchResult<T> searchByExample(
            String api,
            ExampleSearchCriteria<T> criteria,
            LimitOffset offset) throws Exception
    {
        return getFacade(api).searchByExample(api, criteria, offset);
    }

    
    /* (non-Javadoc)
     * @see caarray.client.test.ApiFacade#searchByExampleUtils(java.lang.String, gov.nih.nci.caarray.external.v1_0.query.ExampleSearchCriteria, gov.nih.nci.caarray.external.v1_0.query.LimitOffset)
     */
    public <T extends AbstractCaArrayEntity> List<T> searchByExampleUtils(
            String api, ExampleSearchCriteria<T> criteria)
            throws Exception
    {
        return getFacade(api).searchByExampleUtils(api, criteria);
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
            String api, ExperimentSearchCriteria criteria, LimitOffset offset, boolean login)
            throws Exception
    {
        return getFacade(api).searchForExperiments(api, criteria, offset, login);
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

    public Experiment getExperiment(String api, String title) throws Exception
    {
        return getFacade(api).getExperiment(api, title);
    }

    public SearchResult<Biomaterial> searchForBiomaterialByKeyword(String api,
            BiomaterialKeywordSearchCriteria criteria, LimitOffset limitOffset)
            throws Exception
    {
        return getFacade(api).searchForBiomaterialByKeyword(api, criteria, limitOffset);
    }

    public SearchResult<? extends AbstractCaArrayEntity> searchForBiomaterials(
            String api, BiomaterialSearchCriteria criteria, LimitOffset offset)
            throws Exception
    {
        return getFacade(api).searchForBiomaterials(api, criteria, offset);
    }

    public List<Experiment> experimentsByCriteriaSearchUtils(String api,
            ExperimentSearchCriteria criteria) throws Exception
    {
        return getFacade(api).experimentsByCriteriaSearchUtils(api, criteria);
    }

    public List<Biomaterial> biomaterialsByCriteriaSearchUtils(String api,
            BiomaterialSearchCriteria criteria) throws Exception
    {
        return getFacade(api).biomaterialsByCriteriaSearchUtils(api, criteria);
    }

    public List<Biomaterial> biomaterialsByKeywordSearchUtils(String api,
            BiomaterialKeywordSearchCriteria criteria) throws Exception
    {
        return getFacade(api).biomaterialsByKeywordSearchUtils(api, criteria);
    }

    public List<Experiment> experimentsByKeywordSearchUtils(String api,
            KeywordSearchCriteria criteria) throws Exception
    {
        return getFacade(api).experimentsByKeywordSearchUtils(api, criteria);
    }

    public List<File> filesByCriteriaSearchUtils(String api,
            FileSearchCriteria criteria) throws Exception
    {
        return getFacade(api).filesByCriteriaSearchUtils(api, criteria);
    }

    public List<Hybridization> hybridizationsByCriteriaSearchUtils(String api,
            HybridizationSearchCriteria criteria) throws Exception
    {
        return getFacade(api).hybridizationsByCriteriaSearchUtils(api, criteria);
    }


    public SearchResult<? extends AbstractCaArrayEntity> searchForFiles(
            String api, FileSearchCriteria criteria, LimitOffset offset)
            throws Exception
    {
        return getFacade(api).searchForFiles(api, criteria, offset);
    }

    public SearchResult<? extends AbstractCaArrayEntity> searchForHybridizations(
            String api, HybridizationSearchCriteria criteria, LimitOffset offset)
            throws Exception
    {
        return getFacade(api).searchForHybridizations(api, criteria, offset);
    }

    public List<QuantitationType> searchForQuantitationTypes(
            String api, QuantitationTypeSearchCriteria criteria,
            LimitOffset offset) throws Exception
    {
        return getFacade(api).searchForQuantitationTypes(api, criteria, offset);
    }

    public AnnotationSet getAnnotationSet(String api,
            AnnotationSetRequest annotationSetRequest) throws Exception
    {
        return getFacade(api).getAnnotationSet(api, annotationSetRequest);
    }

    public Hybridization getHybridization(String api, String name)
            throws Exception
    {
        return getFacade(api).getHybridization(api, name);
    }

    public ArrayDataType getArrayDataType(String api, String name)
    throws Exception
    {
    	return getFacade(api).getArrayDataType(api, name);
    }

    public Biomaterial getBiomaterial(String api, String name) throws Exception
    {
        return getFacade(api).getBiomaterial(api, name);
    }

    public List<File> getFilesByName(String api, List<String> fileNames,
            String experimentName) throws Exception
    {
        return getFacade(api).getFilesByName(api, fileNames, experimentName);
    }

    public DataSet getDataSet(String api, DataSetRequest dataSetRequest)
            throws Exception
    {
        return getFacade(api).getDataSet(api, dataSetRequest);
    }

    public QuantitationType getQuantitationType(String api, String name)
            throws Exception
    {
        return getFacade(api).getQuantitationType(api, name);
    }

    public Integer getFileContents(String api,
            List<CaArrayEntityReference> fileReferences, boolean compressed)
            throws Exception
    {
        return getFacade(api).getFileContents(api, fileReferences, compressed);
    }

    public Long getFileContentsZip(String api,
            List<CaArrayEntityReference> fileReferences, boolean compressed)
            throws Exception
    {
        return getFacade(api).getFileContentsZip(api, fileReferences, compressed);
    }

    public Integer copyFileContentsUtils(String api,
            List<CaArrayEntityReference> fileReferences, boolean compressed)
            throws Exception
    {
       return getFacade(api).copyFileContentsUtils(api, fileReferences, compressed);
    }

    public Integer copyFileContentsZipUtils(String api,
            List<CaArrayEntityReference> fileReferences, boolean compressed)
            throws Exception
    {
        return getFacade(api).copyFileContentsZipUtils(api, fileReferences, compressed);
    }

    public List<Biomaterial> enumerateBiomaterials(String api,
            BiomaterialSearchCriteria criteria) throws Exception
    {
        return getFacade(api).enumerateBiomaterials(api, criteria);
    }

    public List<Biomaterial> enumerateBiomaterialsByKeyword(String api,
            BiomaterialKeywordSearchCriteria criteria) throws Exception
    {
        return getFacade(api).enumerateBiomaterialsByKeyword(api, criteria);
    }

    public List<? extends AbstractCaArrayEntity> enumerateByExample(String api,
            ExampleSearchCriteria<? extends AbstractCaArrayEntity> criteria, Class<? extends AbstractCaArrayEntity> clazz)
            throws Exception
    {
        return getFacade(api).enumerateByExample(api, criteria, clazz);
    }
    
    

    public List<Experiment> enumerateExperiments(String api,
            ExperimentSearchCriteria criteria) throws Exception
    {
        return getFacade(api).enumerateExperiments(api, criteria);
    }

    public List<Experiment> enumerateExperimentsByKeyword(String api,
            KeywordSearchCriteria criteria) throws Exception
    {
        return getFacade(api).enumerateExperimentsByKeyword(api, criteria);
    }

    public List<File> enumerateFiles(String api, FileSearchCriteria criteria)
            throws Exception
    {
        return getFacade(api).enumerateFiles(api, criteria);
    }

    public List<Hybridization> enumerateHybridizations(String api,
            HybridizationSearchCriteria criteria) throws Exception
    {
        return getFacade(api).enumerateHybridizations(api, criteria);
    }

    /* (non-Javadoc)
     * @see caarray.client.test.ApiFacade#copyMageTabZipToOutputStream(java.lang.String, gov.nih.nci.caarray.external.v1_0.CaArrayEntityReference, boolean)
     */
    public Long copyMageTabZipToOutputStream(String api,
            CaArrayEntityReference experimentReference, boolean compressed)
            throws Exception
    {
        return getFacade(api).copyMageTabZipToOutputStream(api, experimentReference, compressed);
    }

    /* (non-Javadoc)
     * @see caarray.client.test.ApiFacade#getMageTabExport(java.lang.String, gov.nih.nci.caarray.external.v1_0.CaArrayEntityReference)
     */
    public MageTabFileSet getMageTabExport(String api,
            CaArrayEntityReference experimentReference) throws Exception
    {
        return getFacade(api).getMageTabExport(api, experimentReference);
    }

    /* (non-Javadoc)
     * @see caarray.client.test.ApiFacade#copyMageTabZipApiUtils(java.lang.String, gov.nih.nci.caarray.external.v1_0.CaArrayEntityReference, boolean)
     */
    public Long copyMageTabZipApiUtils(String api,
            CaArrayEntityReference experimentReference, boolean compressed)
            throws Exception
    {
        return getFacade(api).copyMageTabZipApiUtils(api, experimentReference, compressed);
    }

}
