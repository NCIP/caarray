/**
 * 
 */
package caarray.client.test.grid;

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
import gov.nih.nci.caarray.services.external.v1_0.UnsupportedCategoryException;
import gov.nih.nci.caarray.services.external.v1_0.grid.client.CaArraySvc_v1_0Client;
import gov.nih.nci.caarray.services.external.v1_0.grid.client.GridDataApiUtils;
import gov.nih.nci.caarray.services.external.v1_0.grid.client.GridSearchApiUtils;
import gov.nih.nci.caarray.services.external.v1_0.search.Search;
import gov.nih.nci.cagrid.enumeration.stubs.response.EnumerationResponseContainer;
import gov.nih.nci.cagrid.wsenum.utils.EnumerationResponseHelper;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import javax.xml.soap.SOAPElement;

import org.apache.axis.types.URI.MalformedURIException;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.output.CountingOutputStream;
import org.apache.commons.io.output.NullOutputStream;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cagrid.transfer.context.client.TransferServiceContextClient;
import org.cagrid.transfer.context.client.helper.TransferClientHelper;
import org.cagrid.transfer.context.stubs.types.TransferServiceContextReference;
import org.globus.ws.enumeration.ClientEnumIterator;
import org.globus.wsrf.encoding.ObjectDeserializer;

import caarray.client.test.ApiFacade;
import caarray.client.test.TestProperties;
import caarray.client.test.full.FullTest;

/**
 * {@link ApiFacade} implementation that uses the grid API.
 * 
 * @author vaughng
 *
 */
public class GridApiFacade implements ApiFacade
{
    private static final Log log = LogFactory.getLog(GridApiFacade.class);
    
    private CaArraySvc_v1_0Client gridClient = null;
    GridSearchApiUtils apiUtils = null;
    GridDataApiUtils dataApiUtils = null;
    
    public GridApiFacade() throws RemoteException, MalformedURIException
    {}
    
    public void connect() throws Exception
    {
        String gridUrl = TestProperties.getGridServiceUrl();
        System.out.println("Connecting to grid service: " + gridUrl);
        gridClient = new CaArraySvc_v1_0Client(gridUrl);
        apiUtils = new GridSearchApiUtils(gridClient);
        dataApiUtils = new GridDataApiUtils(gridClient);
    
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
     * @see caarray.client.test.ApiFacade#searchByExample(java.lang.String, gov.nih.nci.caarray.external.v1_0.query.ExampleSearchCriteria, gov.nih.nci.caarray.external.v1_0.query.LimitOffset)
     */
    public <T extends AbstractCaArrayEntity> SearchResult<T> searchByExample(
            String api,
            ExampleSearchCriteria<T> criteria,
            LimitOffset offset) throws Exception
    {
        return gridClient.searchByExample(criteria, offset);
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
            log.error("Exception encountered:",e);
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
            String api, ExperimentSearchCriteria criteria, LimitOffset offset, boolean login)
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
        return gridClient.searchForBiomaterialsByKeyword(criteria, limitOffset);
    }
    public SearchResult<? extends AbstractCaArrayEntity> searchForBiomaterials(
            String api, BiomaterialSearchCriteria criteria, LimitOffset offset)
            throws Exception
    {
        return gridClient.searchForBiomaterials(criteria, offset);
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
        for (Iterator<File> resultsIter = results.iterator(); resultsIter.hasNext();)
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
        return gridClient.searchForFiles(criteria, offset);
    }
    public SearchResult<? extends AbstractCaArrayEntity> searchForHybridizations(
            String api, HybridizationSearchCriteria criteria, LimitOffset offset)
            throws Exception
    {
        return gridClient.searchForHybridizations(criteria, offset);
    }
    public List<QuantitationType> searchForQuantitationTypes(
            String api, QuantitationTypeSearchCriteria criteria,
            LimitOffset offset) throws Exception
    {
        QuantitationType[] types = gridClient.searchForQuantitationTypes(criteria);
        List<QuantitationType> resultsList = new ArrayList<QuantitationType>();
        if (types != null)
        {
            for (QuantitationType type : types)
            {
                resultsList.add(type);
            }
        }
        return resultsList;
    }
    public AnnotationSet getAnnotationSet(String api,
            AnnotationSetRequest annotationSetRequest) throws Exception
    {
        return gridClient.getAnnotationSet(annotationSetRequest);
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
        return gridClient.getDataSet(dataSetRequest);
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
        InputStream stream = null;
        if (fileReferences.size() == 1)
        {
            TransferServiceContextReference serviceContextRef = gridClient.getFileContentsTransfer(fileReferences.get(0), compressed);
            TransferServiceContextClient transferClient = new TransferServiceContextClient(serviceContextRef.getEndpointReference());
            stream = TransferClientHelper.getData(transferClient.getDataTransferDescriptor());
            total += IOUtils.toByteArray(stream).length;
        }
        else
        {
            return copyFileContentsUtils(api, fileReferences, compressed);
        }
        
        return total;      
    }
    
    public Long getFileContentsZip(String api,
            List<CaArrayEntityReference> fileReferences, boolean compressed)
            throws Exception
    {
        /*CountingOutputStream ostream = new CountingOutputStream(new NullOutputStream());
        dataApiUtils.copyFileContentsZipToOutputStream(fileReferences, ostream);
        return ostream.getByteCount();*/
        return new Long(copyFileContentsUtils(api, fileReferences, compressed));
    }
    
    public Integer copyFileContentsUtils(String api,
            List<CaArrayEntityReference> fileReferences, boolean compressed)
            throws Exception
    {
        if (fileReferences.isEmpty())
            return 0;
        
        int total = 0;
        for (int i = 0; i < fileReferences.size(); i++)
        {
          ByteArrayOutputStream stream = new ByteArrayOutputStream();  
            
          dataApiUtils.copyFileContentsToOutputStream(fileReferences.get(i), compressed, stream);
          total += stream.size();
        }
       return total;
    }
    
    public Integer copyFileContentsZipUtils(String api,
            List<CaArrayEntityReference> fileReferences, boolean compressed)
            throws Exception
    {
        return copyFileContentsUtils(api, fileReferences, compressed);
    }
    public List<Biomaterial> enumerateBiomaterials(String api,
            BiomaterialSearchCriteria criteria) throws Exception
    {
        EnumerationResponseContainer results = gridClient.enumerateBiomaterials(criteria);
        ClientEnumIterator iter = EnumerationResponseHelper.createClientIterator(results,CaArraySvc_v1_0Client.class
                .getResourceAsStream("client-config.wsdd"));
        List<Biomaterial> resultsList = new ArrayList<Biomaterial>();
        
        while (iter.hasNext()) {
            try {
                SOAPElement elem = (SOAPElement) iter.next();
                if (elem != null) {
                    Biomaterial biomaterial = (Biomaterial) ObjectDeserializer.toObject(elem, Biomaterial.class);
                    resultsList.add(biomaterial);
                }
            } catch (NoSuchElementException e) {
                break;
            }
        }
        return resultsList;
    }
    
    public List<Biomaterial> enumerateBiomaterialsByKeyword(String api,
            BiomaterialKeywordSearchCriteria criteria) throws Exception
    {
        EnumerationResponseContainer results = gridClient.enumerateBiomaterialsByKeyword(criteria);
        ClientEnumIterator iter = EnumerationResponseHelper.createClientIterator(results,CaArraySvc_v1_0Client.class
                .getResourceAsStream("client-config.wsdd"));
        List<Biomaterial> resultsList = new ArrayList<Biomaterial>();
        
        while (iter.hasNext()) {
            try {
                SOAPElement elem = (SOAPElement) iter.next();
                if (elem != null) {
                    Biomaterial biomaterial = (Biomaterial) ObjectDeserializer.toObject(elem, Biomaterial.class);
                    resultsList.add(biomaterial);
                }
            } catch (NoSuchElementException e) {
                //break;
            }
        }
        return resultsList;
    }
    public List<? extends AbstractCaArrayEntity> enumerateByExample(String api,
            ExampleSearchCriteria<? extends AbstractCaArrayEntity> criteria, Class<? extends AbstractCaArrayEntity> clazz)
            throws Exception
    {
        EnumerationResponseContainer results = gridClient.enumerateByExample(criteria);
        ClientEnumIterator iter = EnumerationResponseHelper.createClientIterator(results,CaArraySvc_v1_0Client.class
                .getResourceAsStream("client-config.wsdd"));
        List<AbstractCaArrayEntity> resultsList = new ArrayList<AbstractCaArrayEntity>();
        
        while (iter.hasNext()) {
            try {
                SOAPElement elem = (SOAPElement) iter.next();
                if (elem != null) {
                    AbstractCaArrayEntity entity = (AbstractCaArrayEntity) ObjectDeserializer.toObject(elem, clazz);
                    resultsList.add(entity);
                }
            } catch (NoSuchElementException e) {
                //break;
            }
        }
        return resultsList;
    
    }
    public List<Experiment> enumerateExperiments(String api,
            ExperimentSearchCriteria criteria) throws Exception
    {
        EnumerationResponseContainer results = gridClient.enumerateExperiments(criteria);
        ClientEnumIterator iter = EnumerationResponseHelper.createClientIterator(results,CaArraySvc_v1_0Client.class
                .getResourceAsStream("client-config.wsdd"));
        List<Experiment> resultsList = new ArrayList<Experiment>();
        
        while (iter.hasNext()) {
            try {
                SOAPElement elem = (SOAPElement) iter.next();
                if (elem != null) {
                    Experiment entity = (Experiment) ObjectDeserializer.toObject(elem, Experiment.class);
                    resultsList.add(entity);
                }
            } catch (NoSuchElementException e) {
                //break;
            }
        }
        return resultsList;
    
    }
    public List<Experiment> enumerateExperimentsByKeyword(String api,
            KeywordSearchCriteria criteria) throws Exception
    {
        EnumerationResponseContainer results = gridClient.enumerateExperimentsByKeyword(criteria);
        ClientEnumIterator iter = EnumerationResponseHelper.createClientIterator(results,CaArraySvc_v1_0Client.class
                .getResourceAsStream("client-config.wsdd"));
        List<Experiment> resultsList = new ArrayList<Experiment>();
        
        while (iter.hasNext()) {
            try {
                SOAPElement elem = (SOAPElement) iter.next();
                if (elem != null) {
                    Experiment entity = (Experiment) ObjectDeserializer.toObject(elem, Experiment.class);
                    resultsList.add(entity);
                }
            } catch (NoSuchElementException e) {
                //break;
            }
        }
        return resultsList;
    
    }
    public List<File> enumerateFiles(String api, FileSearchCriteria criteria)
            throws Exception
    {
        EnumerationResponseContainer results = gridClient.enumerateFiles(criteria);
        ClientEnumIterator iter = EnumerationResponseHelper.createClientIterator(results,CaArraySvc_v1_0Client.class
                .getResourceAsStream("client-config.wsdd"));
        List<File> resultsList = new ArrayList<File>();
        
        while (iter.hasNext()) {
            try {
                SOAPElement elem = (SOAPElement) iter.next();
                if (elem != null) {
                    File entity = (File) ObjectDeserializer.toObject(elem, File.class);
                    resultsList.add(entity);
                }
            } catch (NoSuchElementException e) {
                //break;
            }
        }
        return resultsList;
    
    }
    public List<Hybridization> enumerateHybridizations(String api,
            HybridizationSearchCriteria criteria) throws Exception
    {
        EnumerationResponseContainer results = gridClient.enumerateHybridizations(criteria);
        ClientEnumIterator iter = EnumerationResponseHelper.createClientIterator(results,CaArraySvc_v1_0Client.class
                .getResourceAsStream("client-config.wsdd"));
        List<Hybridization> resultsList = new ArrayList<Hybridization>();
        
        while (iter.hasNext()) {
            try {
                SOAPElement elem = (SOAPElement) iter.next();
                if (elem != null) {
                    Hybridization entity = (Hybridization) ObjectDeserializer.toObject(elem, Hybridization.class);
                    resultsList.add(entity);
                }
            } catch (NoSuchElementException e) {
                break;
            }
        }
        return resultsList;
    
    }

    /* (non-Javadoc)
     * @see caarray.client.test.ApiFacade#copyMageTabZipToOutputStream(java.lang.String, gov.nih.nci.caarray.external.v1_0.CaArrayEntityReference, boolean)
     */
    public Long copyMageTabZipToOutputStream(String api,
            CaArrayEntityReference experimentReference, boolean compressed)
            throws Exception
    {
        return copyMageTabZipApiUtils(api, experimentReference, compressed);
    }

    /* (non-Javadoc)
     * @see caarray.client.test.ApiFacade#getMageTabExport(java.lang.String, gov.nih.nci.caarray.external.v1_0.CaArrayEntityReference)
     */
    public MageTabFileSet getMageTabExport(String api,
            CaArrayEntityReference experimentReference) throws Exception
    {
        return gridClient.getMageTabExport(experimentReference);
    }

    /* (non-Javadoc)
     * @see caarray.client.test.ApiFacade#copyMageTabZipApiUtils(java.lang.String, gov.nih.nci.caarray.external.v1_0.CaArrayEntityReference, boolean)
     */
    public Long copyMageTabZipApiUtils(String api,
            CaArrayEntityReference experimentReference, boolean compressed)
            throws Exception
    {
        CountingOutputStream stream = new CountingOutputStream(new NullOutputStream());
        dataApiUtils.copyMageTabZipToOutputStream(experimentReference, stream);
        return stream.getByteCount();
    }

}
