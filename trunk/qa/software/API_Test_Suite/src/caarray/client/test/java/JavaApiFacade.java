//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package caarray.client.test.java;

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
import gov.nih.nci.caarray.services.external.v1_0.CaArrayServer;
import gov.nih.nci.caarray.services.external.v1_0.data.DataService;
import gov.nih.nci.caarray.services.external.v1_0.data.JavaDataApiUtils;
import gov.nih.nci.caarray.services.external.v1_0.search.JavaSearchApiUtils;
import gov.nih.nci.caarray.services.external.v1_0.search.Search;
import gov.nih.nci.caarray.services.external.v1_0.search.SearchResultIterator;
import gov.nih.nci.caarray.services.external.v1_0.search.SearchService;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.security.auth.login.FailedLoginException;

import org.apache.commons.io.output.CountingOutputStream;
import org.apache.commons.io.output.NullOutputStream;

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
    
    private SearchService javaSearchService = null, loginSearchService = null;
    private DataService dataService;
    private JavaSearchApiUtils apiUtils;
    private JavaDataApiUtils dataApiUtils;
    
    
    public JavaApiFacade()
    {}
    
    public void connect() throws Exception
    {

        String hostName = TestProperties.getJavaServerHostname();
        int port = TestProperties.getJavaServerJndiPort();
        System.out.println("Connecting to java server: " + hostName + ":" + port);
        CaArrayServer server = new CaArrayServer(hostName, port);
        server.connect();
        javaSearchService = server.getSearchService();
        dataService = server.getDataService();
        apiUtils = new JavaSearchApiUtils(javaSearchService);
        dataApiUtils = new JavaDataApiUtils(dataService);   
    }
    
    private void connectWithLogin() throws ServerConnectionException, FailedLoginException
    {
        String hostName = TestProperties.getJavaServerHostname();
        int port = TestProperties.getJavaServerJndiPort();
        System.out.println("Connecting to java server: " + hostName + ":" + port);
        CaArrayServer server = new CaArrayServer(hostName, port);
        server.connect("caarrayuser","Pass#1234");
        loginSearchService = server.getSearchService();
    }
    
    public List<Person> getAllPrincipalInvestigators(String api)
            throws Exception
    {
        return javaSearchService.getAllPrincipalInvestigators();
    }

    public List<Term> getTermsForCategory(String api,
            CaArrayEntityReference categoryRef, String valuePrefix)
            throws Exception
    {
        return javaSearchService.getTermsForCategory(categoryRef, valuePrefix);
    }

    public <T extends AbstractCaArrayEntity> SearchResult<T> searchByExample(
            String api,
            ExampleSearchCriteria<T> criteria,
            LimitOffset offset) throws Exception
    {
        return javaSearchService.searchByExample(criteria, offset);
    }

    /* (non-Javadoc)
     * @see caarray.client.test.ApiFacade#searchByExampleUtils(java.lang.String, gov.nih.nci.caarray.external.v1_0.query.ExampleSearchCriteria, gov.nih.nci.caarray.external.v1_0.query.LimitOffset)
     */
    public <T extends AbstractCaArrayEntity> List<T> searchByExampleUtils(
            String api, ExampleSearchCriteria<T> criteria)
            throws Exception
    {
        Search<T> search = apiUtils.byExample(criteria);
        List<T> resultsList = new ArrayList<T>();
        for (Iterator<T> resultsIter = search.iterator(); resultsIter.hasNext();)
        {
            resultsList.add(resultsIter.next());
        }
        return resultsList;
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

    public ArrayProvider getArrayProvider(String api, String providerName)
            throws Exception
    {
        ArrayProvider provider = new ArrayProvider();
        provider.setName(providerName);
        ExampleSearchCriteria<ArrayProvider> providerCriteria = new ExampleSearchCriteria<ArrayProvider>();
        providerCriteria.setExample(provider);
        SearchResult<? extends AbstractCaArrayEntity> results = searchByExample(api, providerCriteria, null);
        if (!results.getResults().isEmpty())
            return (ArrayProvider)results.getResults().get(0);
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
        SearchResult<? extends AbstractCaArrayEntity> organisms = searchByExample(api,
                organismCriteria, null);
        if (!organisms.getResults().isEmpty())
            return (Organism)organisms.getResults().get(0);
        return null;
    }

    public AssayType getAssayType(String api, String type) throws Exception
    {
        ExampleSearchCriteria<AssayType> assayCriteria = new ExampleSearchCriteria<AssayType>();
        AssayType assay = new AssayType();
        assay.setName(type);
        assayCriteria.setExample(assay);
        SearchResult<? extends AbstractCaArrayEntity> assays = searchByExample(api,
                assayCriteria, null);
        if (!assays.getResults().isEmpty())
            return (AssayType)assays.getResults().get(0);
        return null;
    }

    public SearchResult<? extends AbstractCaArrayEntity> searchForExperiments(
            String api, ExperimentSearchCriteria criteria, LimitOffset offset, boolean login)
            throws Exception
    {
        if (login)
        {
            if (loginSearchService == null)
            {
                connectWithLogin();
            }
            return loginSearchService.searchForExperiments(criteria, offset);
        }
        return javaSearchService.searchForExperiments(criteria, offset);
    }

    public SearchResult<Experiment> searchForExperimentByKeyword(String api,
            KeywordSearchCriteria criteria, LimitOffset limitOffset)
            throws Exception
    {
        return javaSearchService.searchForExperimentsByKeyword(criteria, limitOffset);
    }

    public List<Category> getAllCharacteristicCategories(String api,
            CaArrayEntityReference reference) throws Exception
    {
        return javaSearchService.getAllCharacteristicCategories(reference);
    }

    public Experiment getExperiment(String api, String title) throws Exception
    {
        ExampleSearchCriteria<Experiment> experimentCriteria = new ExampleSearchCriteria<Experiment>();
        Experiment experiment = new Experiment();
        experiment.setTitle(title);
        experimentCriteria.setExample(experiment);
        SearchResult<Experiment> experiments = (SearchResult<Experiment>)searchByExample(api,
                experimentCriteria, null);
        if (!experiments.getResults().isEmpty())
            return experiments.getResults().get(0);
        return null;
    }
    

    public SearchResult<Biomaterial> searchForBiomaterialByKeyword(String api,
            BiomaterialKeywordSearchCriteria criteria, LimitOffset limitOffset)
            throws Exception
    {
        return javaSearchService.searchForBiomaterialsByKeyword(criteria, limitOffset);
    }

    public SearchResult<? extends AbstractCaArrayEntity> searchForBiomaterials(
            String api, BiomaterialSearchCriteria criteria, LimitOffset offset)
            throws Exception
    {
        return javaSearchService.searchForBiomaterials(criteria, offset);
    }

    public List<Experiment> experimentsByCriteriaSearchUtils(String api,
            ExperimentSearchCriteria criteria) throws Exception
    {
        Search<Experiment> results = apiUtils.experimentsByCriteria(criteria);
        List<Experiment> resultsList = new ArrayList<Experiment>();
        for (Iterator<Experiment> resultsIter = results.iterator(); resultsIter.hasNext();)
        {
            resultsList.add(resultsIter.next());
        }
        return resultsList;
    }

    public List<Biomaterial> biomaterialsByCriteriaSearchUtils(String api,
            BiomaterialSearchCriteria criteria) throws Exception
    {
        Search<Biomaterial> results = apiUtils.biomaterialsByCriteria(criteria);
        List<Biomaterial> resultsList = new ArrayList<Biomaterial>();
        for (Iterator<Biomaterial> resultsIter = results.iterator(); resultsIter.hasNext();)
        {
            resultsList.add(resultsIter.next());
        }
        return resultsList;
    }

    public List<Biomaterial> biomaterialsByKeywordSearchUtils(String api,
            BiomaterialKeywordSearchCriteria criteria) throws Exception
    {
        Search<Biomaterial> results = apiUtils.biomaterialsByKeyword(criteria);
        List<Biomaterial> resultsList = new ArrayList<Biomaterial>();
        for (Iterator<Biomaterial> resultsIter = results.iterator(); resultsIter.hasNext();)
        {
            resultsList.add(resultsIter.next());
        }
        return resultsList;
    }

    public List<Experiment> experimentsByKeywordSearchUtils(String api,
            KeywordSearchCriteria criteria) throws Exception
    {
        Search<Experiment> results = apiUtils.experimentsByKeyword(criteria);
        List<Experiment> resultsList = new ArrayList<Experiment>();
        for (Iterator<Experiment> resultsIter = results.iterator(); resultsIter.hasNext();)
        {
            resultsList.add(resultsIter.next());
        }
        return resultsList;
    }

    public List<File> filesByCriteriaSearchUtils(String api,
            FileSearchCriteria criteria) throws Exception
    {
        Search<File> results = apiUtils.filesByCriteria(criteria);
        List<File> resultsList = new ArrayList<File>();
        SearchResultIterator<File> resultsIter = results.iterator();
        while ( resultsIter.hasNext())
        {
            resultsList.add(resultsIter.next());
        }
        return resultsList;
    }

    public List<Hybridization> hybridizationsByCriteriaSearchUtils(String api,
            HybridizationSearchCriteria criteria) throws Exception
    {
        Search<Hybridization> results = apiUtils.hybridizationsByCriteria(criteria);
        List<Hybridization> resultsList = new ArrayList<Hybridization>();
        for (Iterator<Hybridization> resultsIter = results.iterator(); resultsIter.hasNext();)
        {
            resultsList.add(resultsIter.next());
        }
        return resultsList;
    }

    public SearchResult<? extends AbstractCaArrayEntity> searchForFiles(
            String api, FileSearchCriteria criteria, LimitOffset offset)
            throws Exception
    {
        return javaSearchService.searchForFiles(criteria, offset);
    }

    public SearchResult<? extends AbstractCaArrayEntity> searchForHybridizations(
            String api, HybridizationSearchCriteria criteria, LimitOffset offset)
            throws Exception
    {
        return javaSearchService.searchForHybridizations(criteria, offset);
    }

    public List<QuantitationType> searchForQuantitationTypes(String api,
            QuantitationTypeSearchCriteria criteria, LimitOffset offset)
            throws Exception
    {
    	System.out.println(javaSearchService.searchForQuantitationTypes(criteria));
        return javaSearchService.searchForQuantitationTypes(criteria);
    }

    public AnnotationSet getAnnotationSet(String api,
            AnnotationSetRequest annotationSetRequest) throws Exception
    {
        return javaSearchService.getAnnotationSet(annotationSetRequest);
    }

    public Hybridization getHybridization(String api, String name)
            throws Exception
    {
        ExampleSearchCriteria<Hybridization> crit = new ExampleSearchCriteria<Hybridization>();
        Hybridization hyb = new Hybridization();
        hyb.setName(name);
        crit.setExample(hyb);
        SearchResult<Hybridization> hybs = (SearchResult<Hybridization>)searchByExample(api,
                crit, null);
        if (!hybs.getResults().isEmpty())
            return hybs.getResults().get(0);
        return null;
    }

    public ArrayDataType getArrayDataType(String api, String name)
    throws Exception
    {
    	ExampleSearchCriteria<ArrayDataType> crit = new ExampleSearchCriteria<ArrayDataType>();
    	ArrayDataType adt = new ArrayDataType();
    	adt.setName(name);
    	crit.setExample(adt);
    	SearchResult<ArrayDataType> adts = (SearchResult<ArrayDataType>)searchByExample(api,
    			crit, null);
    	if (!adts.getResults().isEmpty())
    		return adts.getResults().get(0);
    	return null;
    }

    public Biomaterial getBiomaterial(String api, String name) throws Exception
    {
        ExampleSearchCriteria<Biomaterial> crit = new ExampleSearchCriteria<Biomaterial>();
        Biomaterial bio = new Biomaterial();
        bio.setName(name);
        crit.setExample(bio);
        SearchResult<Biomaterial> bios = (SearchResult<Biomaterial>)searchByExample(api,
                crit, null);
        if (!bios.getResults().isEmpty())
            return bios.getResults().get(0);
        return null;
    }

    public List<File> getFilesByName(String api, List<String> fileNames,
            String experimentName) throws Exception
    {
        List<File> resultsList = new ArrayList<File>();
        FileSearchCriteria crit = new FileSearchCriteria();
        Experiment experiment = getExperiment(api, experimentName);
        if (experiment != null)
        {
            crit.setExperiment(experiment.getReference());
            List<File> files = filesByCriteriaSearchUtils(api, crit);
            for (File file : files)
            {
                if (fileNames.contains(file.getMetadata().getName()))
                {
                    resultsList.add(file);
                }
            }
        }
        return resultsList;
    }

    public DataSet getDataSet(String api, DataSetRequest dataSetRequest)
            throws Exception
    {
        return dataService.getDataSet(dataSetRequest);
    }

    public QuantitationType getQuantitationType(String api, String name)
            throws Exception
    {
        ExampleSearchCriteria<QuantitationType> crit = new ExampleSearchCriteria<QuantitationType>();
        QuantitationType quant = new QuantitationType();
        quant.setName(name);
        crit.setExample(quant);
        SearchResult<QuantitationType> quants = (SearchResult<QuantitationType>)searchByExample(api,
                crit, null);
        if (!quants.getResults().isEmpty())
            return quants.getResults().get(0);
        return null;
    }

    public Integer getFileContents(String api,
            List<CaArrayEntityReference> fileReferences, boolean compressed)
            throws Exception
    {
        if (fileReferences.isEmpty())
            return 0;
        int total = 0;
        
        for (int i = 0; i < fileReferences.size(); i++)
        {
            ByteArrayOutputStream outStream = new ByteArrayOutputStream();
            dataApiUtils.copyFileContentsToOutputStream(fileReferences.get(i), compressed, outStream);
            total += outStream.size();
        }
        return total;    
    }

    public Integer getFileContentsZip(String api,
            List<CaArrayEntityReference> fileReferences, boolean compressed)
            throws Exception
    {
        /*FileDownloadRequest request = new FileDownloadRequest();
        request.setFiles(fileReferences);
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        dataApiUtils.copyFileContentsZipToOutputStream(request, compressed, outStream);
        return outStream.toByteArray();*/
        return copyFileContentsZipUtils(api, fileReferences, compressed);
    }

    public Integer copyFileContentsUtils(String api,
            List<CaArrayEntityReference> fileReferences, boolean compressed)
            throws Exception
    {
        return getFileContents(api, fileReferences, compressed);
    }

    public Integer copyFileContentsZipUtils(String api,
            List<CaArrayEntityReference> fileReferences, boolean compressed)
            throws Exception
    {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        dataApiUtils.copyFileContentsZipToOutputStream(fileReferences, out);
        return out.size();
    }

    public List<Biomaterial> enumerateBiomaterials(String api,
            BiomaterialSearchCriteria criteria) throws Exception
    {
        return biomaterialsByCriteriaSearchUtils(api, criteria);
    }

    public List<Biomaterial> enumerateBiomaterialsByKeyword(String api,
            BiomaterialKeywordSearchCriteria criteria) throws Exception
    {
        return biomaterialsByKeywordSearchUtils(api, criteria);
    }

    public List<? extends AbstractCaArrayEntity> enumerateByExample(String api,
            ExampleSearchCriteria<? extends AbstractCaArrayEntity> criteria, Class<? extends AbstractCaArrayEntity> clazz)
            throws Exception
    {
        return apiUtils.byExample(criteria).list();
    }

    public List<Experiment> enumerateExperiments(String api,
            ExperimentSearchCriteria criteria) throws Exception
    {
        return experimentsByCriteriaSearchUtils(api, criteria);
    }

    public List<Experiment> enumerateExperimentsByKeyword(String api,
            KeywordSearchCriteria criteria) throws Exception
    {
        return experimentsByKeywordSearchUtils(api, criteria);
    }

    public List<File> enumerateFiles(String api, FileSearchCriteria criteria)
            throws Exception
    {
        return filesByCriteriaSearchUtils(api, criteria);
    }

    public List<Hybridization> enumerateHybridizations(String api,
            HybridizationSearchCriteria criteria) throws Exception
    {
        return hybridizationsByCriteriaSearchUtils(api, criteria);
    }

    /* (non-Javadoc)
     * @see caarray.client.test.ApiFacade#copyMageTabZipToOutputStream(java.lang.String, gov.nih.nci.caarray.external.v1_0.CaArrayEntityReference, boolean)
     */
    public Long copyMageTabZipToOutputStream(String api,
            CaArrayEntityReference experimentReference, boolean compressed)
            throws Exception
    {
        CountingOutputStream outStream = new CountingOutputStream(new NullOutputStream());
        dataApiUtils.copyMageTabZipToOutputStream(experimentReference,  outStream);
        return outStream.getByteCount();
    }

    /* (non-Javadoc)
     * @see caarray.client.test.ApiFacade#getMageTabExport(java.lang.String, gov.nih.nci.caarray.external.v1_0.CaArrayEntityReference)
     */
    public MageTabFileSet getMageTabExport(String api,
            CaArrayEntityReference experimentReference) throws Exception
    {
        return dataService.exportMageTab(experimentReference);
    }

    /* (non-Javadoc)
     * @see caarray.client.test.ApiFacade#copyMageTabZipApiUtils(java.lang.String, gov.nih.nci.caarray.external.v1_0.CaArrayEntityReference, boolean)
     */
    public Long copyMageTabZipApiUtils(String api,
            CaArrayEntityReference experimentReference, boolean compressed)
            throws Exception
    {
        return copyMageTabZipToOutputStream(api, experimentReference, compressed);
    }   
    
    
}
