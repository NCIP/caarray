/**
 * 
 */
package caarray.client.test.suite;

import gov.nih.nci.caarray.external.v1_0.CaArrayEntityReference;
import gov.nih.nci.caarray.external.v1_0.data.File;
import gov.nih.nci.caarray.external.v1_0.data.MageTabFileSet;
import gov.nih.nci.caarray.external.v1_0.experiment.Experiment;
import gov.nih.nci.caarray.external.v1_0.query.ExperimentSearchCriteria;
import gov.nih.nci.caarray.external.v1_0.query.FileSearchCriteria;
import gov.nih.nci.caarray.external.v1_0.sample.Hybridization;

import java.util.ArrayList;
import java.util.List;

import caarray.client.test.ApiFacade;
import caarray.client.test.TestProperties;
import caarray.client.test.TestResult;
import caarray.client.test.search.CriteriaSearch;
import caarray.client.test.search.FileContentsSearch;
import caarray.client.test.search.TestBean;

/**
 * @author vaughng
 * Jul 14, 2009
 */
public class FileContentsTestSuite extends SearchByCriteriaTestSuite
{

    private static final String CONFIG_FILE = TestProperties.CONFIG_DIR
            + java.io.File.separator + "FileContents.csv";

                       

    private static final String FILE_REF = "File Reference";
    private static final String FILE = "File Name";
    private static final String FILE_EXPERIMENT = "File Experiment";
    private static final String FILE_EXPERIMENT_ID= "File Experiment Id";
    private static final String EXPERIMENT_REF= "Experiment Reference";
    private static final String HYB= "Hybridization";
    private static final String MULTI_FILE_NUM= "Multi File Num";
    private static final String MULTI_FILE_TYPE= "Multi File Type";
    private static final String COMPRESSED = "Compressed";
    private static final String ZIP = "Zip";   
    private static final String EXPECTED_BYTES = "Expected Bytes";
    private static final String MIN_BYTES = "Min Bytes";
    private static final String MAX_BYTES = "Max Bytes";
    private static final String IDF_BYTES = "Expected IDF Bytes";
    private static final String SDRF_BYTES = "Expected SDRF Bytes";
    private static final String NUM_FILE = "Expected File Num";
    private static final String MAGE = "Mage Tab";

    private static final String[] COLUMN_HEADERS = new String[] { TEST_CASE,
            API, FILE_REF, API_UTILS_SEARCH, HYB,
            FILE, FILE_EXPERIMENT, FILE_EXPERIMENT_ID, EXPERIMENT_REF,MULTI_FILE_NUM, MULTI_FILE_TYPE, COMPRESSED, ZIP, EXPECTED_BYTES, MIN_BYTES, MAX_BYTES,
            MAX_TIME, IDF_BYTES, SDRF_BYTES, NUM_FILE, MAGE};
    /**
     * @param apiFacade
     */
    public FileContentsTestSuite(ApiFacade apiFacade)
    {
        super(apiFacade);
    }

    /* (non-Javadoc)
     * @see caarray.client.test.suite.SearchByCriteriaTestSuite#evaluateResults(java.lang.Object, caarray.client.test.search.CriteriaSearch, caarray.client.test.TestResult)
     */
    @Override
    protected void evaluateResults(Object resultsList, TestBean search,
            TestResult testResult)
    {
        FileContentsSearch fileSearch = (FileContentsSearch)search;
        int size = 0;
        int idfBytes = 0;
        int sdrfBytes = 0;
        int numFiles = 0;
        if (resultsList == null)
            size = 0;
        else if (resultsList instanceof byte[])
            size = ((byte[])resultsList).length;
        else if (resultsList instanceof byte[][])
        {
            byte[][] bytes = (byte[][]) resultsList;
            for (byte[] result : bytes)
            {
                if (result != null)
                    size += result.length;
            }
        }
        else if (resultsList instanceof Integer)
        {
            size = ((Integer)resultsList).intValue();
        }
        else if (resultsList instanceof Long)
        {
            size = (int)((Long)resultsList).longValue();
        }
        else if (resultsList instanceof MageTabFileSet)
        {
            MageTabFileSet fileSet = (MageTabFileSet)resultsList;
            if (fileSet.getIdf() != null && fileSet.getIdf().getContents() != null)
                idfBytes = fileSet.getIdf().getContents().length;
            
            if (fileSet.getSdrf() != null && fileSet.getSdrf().getContents() != null)
                sdrfBytes = fileSet.getSdrf().getContents().length;
            
            if (fileSet.getDataFiles() != null)
                numFiles = fileSet.getDataFiles().size();
            
            size = idfBytes + sdrfBytes;
        }
        
        if (fileSearch.getExpectedBytes() != null)
        {
            
            if (size != fileSearch.getExpectedBytes())
            {
                String errorMessage = "Failed with unexpected number of bytes, expected: "
                        + fileSearch.getExpectedBytes()
                        + ", actual number of bytes: " + size;
                
                setTestResultFailure(testResult, fileSearch, errorMessage);
            }
            else
            {
                String detail = "Found expected number of bytes: "
                        + size;
                testResult.addDetail(detail);
            }
        }
        if (fileSearch.getMinBytes() != null)
        {
            
            if (size < fileSearch.getMinBytes())
            {
                String errorMessage = "Failed with unexpected number of bytes, expected minimum: "
                        + fileSearch.getMinBytes()
                        + ", actual number of bytes: " + size;
                setTestResultFailure(testResult, fileSearch, errorMessage);
            }
            else
            {
                String detail = "Found expected minimum bytes: "
                        + size;
                testResult.addDetail(detail);
            }
        }
        if (fileSearch.getMaxBytes() != null)
        {
            
            if (size > fileSearch.getMaxBytes())
            {
                String errorMessage = "Failed with unexpected number of bytes, expected max: "
                        + fileSearch.getMaxBytes()
                        + ", actual number of bytes: " + size;
                setTestResultFailure(testResult, fileSearch, errorMessage);
            }
            else
            {
                String detail = "Found expected max bytes: "
                        + size;
                testResult.addDetail(detail);
            }
        }
        if (fileSearch.getMaxTime() != null)
        {
            
            if (testResult.getElapsedTime() > fileSearch.getMaxTime())
            {
                String errorMessage = "Search did not complete in expected time, expected: "
                        + fileSearch.getMaxTime()
                        + ", actual time: " + testResult.getElapsedTime();
                setTestResultFailure(testResult, fileSearch, errorMessage);
            }
            else
            {
                String detail = "Search completed in expected time: "
                        + testResult.getElapsedTime();
                testResult.addDetail(detail);
            }
        }
        if (fileSearch.getIdfBytes() != null)
        {
            
            if (idfBytes != fileSearch.getIdfBytes())
            {
                String errorMessage = "Failed with unexpected number of IDF bytes, expected: "
                        + fileSearch.getIdfBytes()
                        + ", actual number of bytes: " + idfBytes;
                setTestResultFailure(testResult, fileSearch, errorMessage);
            }
            else
            {
                String detail = "Found expected IDF bytes: "
                        + idfBytes;
                testResult.addDetail(detail);
            }
        }
        if (fileSearch.getSdrfBytes() != null)
        {
            
            if (sdrfBytes != fileSearch.getSdrfBytes())
            {
                String errorMessage = "Failed with unexpected number of SDRF bytes, expected: "
                        + fileSearch.getSdrfBytes()
                        + ", actual number of bytes: " + sdrfBytes;
                setTestResultFailure(testResult, fileSearch, errorMessage);
            }
            else
            {
                String detail = "Found expected SDRF bytes: "
                        + sdrfBytes;
                testResult.addDetail(detail);
            }
        }
        if (fileSearch.getNumMageTabFiles() != null)
        {
            
            if (numFiles > fileSearch.getNumMageTabFiles())
            {
                String errorMessage = "Failed with unexpected number of files, expected: "
                        + fileSearch.getNumMageTabFiles()
                        + ", actual number of files: " + numFiles;
                setTestResultFailure(testResult, fileSearch, errorMessage);
            }
            else
            {
                String detail = "Found expected number of files: "
                        + numFiles;
                testResult.addDetail(detail);
            }
        }
        
    }

    /* (non-Javadoc)
     * @see caarray.client.test.suite.SearchByCriteriaTestSuite#executeSearch(caarray.client.test.search.CriteriaSearch, caarray.client.test.TestResult)
     */
    @Override
    protected Object executeSearch(CriteriaSearch search, TestResult testResult)
            throws Exception
    {
        System.out.println("test:"+search.getTestCase());
        FileContentsSearch fileSearch = (FileContentsSearch)search;
        try
        {
            
            
            if (fileSearch.isMage())
            {
                if (fileSearch.isZip())
                {
                    if (fileSearch.isApiUtilsSearch())
                        return apiFacade.copyMageTabZipApiUtils(search.getApi(), fileSearch.getExperimentRef(), fileSearch.isCompressed());
                    
                    return apiFacade.copyMageTabZipToOutputStream(search.getApi(), fileSearch.getExperimentRef(), fileSearch.isCompressed());
                }
                    
                
                return apiFacade.getMageTabExport(search.getApi(), fileSearch.getExperimentRef());
            }
            if (fileSearch.isApiUtilsSearch())
            {
                if (fileSearch.isZip())
                    return apiFacade.copyFileContentsZipUtils(search.getApi(), fileSearch.getFileReferences(), fileSearch.isCompressed());
                
                return apiFacade.copyFileContentsUtils(search.getApi(), fileSearch.getFileReferences(), fileSearch.isCompressed());
            }
            if (fileSearch.isZip())
                return apiFacade.getFileContentsZip(search.getApi(), fileSearch.getFileReferences(), fileSearch.isCompressed());
            
            return apiFacade.getFileContents(search.getApi(), fileSearch.getFileReferences(), fileSearch.isCompressed());
        }
        catch (Throwable e)
        {
            System.out.println("Error encountered retrieving file contents set: " + e.getClass() + (e.getMessage() != null ? e.getMessage() : ""));
            testResult.addDetail("Exception encountered retrieving file contents set: " + e.getClass() + (e.getMessage() != null ? e.getMessage() : ""));
            log.error("Exception encountered:",e);
        } 
        return null;
    }

    /* (non-Javadoc)
     * @see caarray.client.test.suite.SearchByCriteriaTestSuite#getCriteriaSearch()
     */
    @Override
    protected CriteriaSearch getCriteriaSearch()
    {
        return new FileContentsSearch();
    }

    /* (non-Javadoc)
     * @see caarray.client.test.suite.SearchByCriteriaTestSuite#populateAdditionalSearchValues(java.lang.String[], caarray.client.test.search.CriteriaSearch)
     */
    @Override
    protected void populateAdditionalSearchValues(String[] input,
            TestBean criteriaSearch) throws Exception
    {
        FileContentsSearch search = (FileContentsSearch)criteriaSearch;
        String experimentName = search.getExperimentName();
        if (headerIndexMap.get(FILE) < input.length && !input[headerIndexMap.get(FILE)].equals(""))
        {
            String name = input[headerIndexMap.get(FILE)].trim();
            if (name.startsWith(VAR_START))
                name = getVariableValue(name);
            List<String> names = new ArrayList<String>();
            names.add(name);
            List<File> files = apiFacade.getFilesByName(search.getApi(), names, experimentName);
            for (File file : files)
            {
                search.addFileReference(file.getReference());
            }
        }
            
        if (headerIndexMap.get(FILE_REF) < input.length
                && !input[headerIndexMap.get(FILE_REF)].equals(""))
        {
            String ref = input[headerIndexMap.get(FILE_REF)].trim();
            CaArrayEntityReference reference ;
            if (ref.equals(NULL_VAR))
                reference = null;          
            else if (ref.startsWith(VAR_START))
                reference = new CaArrayEntityReference(getVariableValue(ref));
            else
                reference = new CaArrayEntityReference(ref);
            
            search.addFileReference(reference);
        }
    }

    /* (non-Javadoc)
     * @see caarray.client.test.suite.SearchByCriteriaTestSuite#populateSearch(java.lang.String[], caarray.client.test.search.CriteriaSearch)
     */
    @Override
    protected void populateSearch(String[] input, TestBean criteriaSearch)
            throws Exception
    {
        FileContentsSearch search = (FileContentsSearch)criteriaSearch;
        
        if (headerIndexMap.get(API) < input.length
                && !input[headerIndexMap.get(API)].equals(""))
        {
            search.setApi(input[headerIndexMap.get(API)].trim());
        }
        if (headerIndexMap.get(TEST_CASE) < input.length
                && !input[headerIndexMap.get(TEST_CASE)].equals(""))
            search.setTestCase(Float.parseFloat(input[headerIndexMap.get(TEST_CASE)]
                    .trim()));
        if (headerIndexMap.get(EXPECTED_BYTES) < input.length
                && !input[headerIndexMap.get(EXPECTED_BYTES)].equals(""))
            search.setExpectedBytes(Integer
                    .parseInt(input[headerIndexMap.get(EXPECTED_BYTES)].trim()));
        if (headerIndexMap.get(MIN_BYTES) < input.length
                && !input[headerIndexMap.get(MIN_BYTES)].equals(""))
            search.setMinBytes(Integer
                    .parseInt(input[headerIndexMap.get(MIN_BYTES)].trim()));
        if (headerIndexMap.get(MAX_BYTES) < input.length
                && !input[headerIndexMap.get(MAX_BYTES)].equals(""))
            search.setMaxBytes(Integer
                    .parseInt(input[headerIndexMap.get(MAX_BYTES)].trim())); 
        if (headerIndexMap.get(IDF_BYTES) < input.length
                && !input[headerIndexMap.get(IDF_BYTES)].equals(""))
            search.setIdfBytes(Integer
                    .parseInt(input[headerIndexMap.get(IDF_BYTES)].trim()));    
        if (headerIndexMap.get(SDRF_BYTES) < input.length
                && !input[headerIndexMap.get(SDRF_BYTES)].equals(""))
            search.setSdrfBytes(Integer
                    .parseInt(input[headerIndexMap.get(SDRF_BYTES)].trim()));   
        if (headerIndexMap.get(NUM_FILE) < input.length
                && !input[headerIndexMap.get(NUM_FILE)].equals(""))
            search.setNumMageTabFiles(Integer
                    .parseInt(input[headerIndexMap.get(NUM_FILE)].trim()));  
        if (headerIndexMap.get(COMPRESSED) < input.length
                && !input[headerIndexMap.get(COMPRESSED)].equals(""))
            search.setCompressed(Boolean.parseBoolean(input[headerIndexMap.get(COMPRESSED)].trim()));
        if (headerIndexMap.get(ZIP) < input.length
                && !input[headerIndexMap.get(ZIP)].equals(""))
            search.setZip(Boolean.parseBoolean(input[headerIndexMap.get(ZIP)].trim()));
        if (headerIndexMap.get(API_UTILS_SEARCH) < input.length
                && !input[headerIndexMap.get(API_UTILS_SEARCH)].equals(""))
            search.setApiUtilsSearch(Boolean.parseBoolean(input[headerIndexMap.get(API_UTILS_SEARCH)].trim()));
        if (headerIndexMap.get(MAX_TIME) < input.length
                && !input[headerIndexMap.get(MAX_TIME)].equals(""))
            search.setMaxTime(Long
                    .parseLong(input[headerIndexMap.get(MAX_TIME)].trim()));
                
        if (headerIndexMap.get(FILE_REF) < input.length
                && !input[headerIndexMap.get(FILE_REF)].equals(""))
        {
            String ref = input[headerIndexMap.get(FILE_REF)].trim();
            CaArrayEntityReference reference ;
            if (ref.equals(NULL_VAR))
                reference = null;          
            else if (ref.startsWith(VAR_START))
                reference = new CaArrayEntityReference(getVariableValue(ref));
            else
                reference = new CaArrayEntityReference(ref);
            
            search.addFileReference(reference);
        }
            
        String experimentName = null;
        CaArrayEntityReference experimentReference = null;
        if (headerIndexMap.get(FILE_EXPERIMENT) < input.length && !input[headerIndexMap.get(FILE_EXPERIMENT)].equals(""))
        {
            experimentName = input[headerIndexMap.get(FILE_EXPERIMENT)].trim();
            search.setExperimentName(experimentName);
        }
        if (headerIndexMap.get(EXPERIMENT_REF) < input.length && !input[headerIndexMap.get(EXPERIMENT_REF)].equals(""))
        {
            String ref = input[headerIndexMap.get(EXPERIMENT_REF)].trim();
            if (ref.startsWith(VAR_START))
                ref = getVariableValue(ref);
            search.setExperimentRef(new CaArrayEntityReference(ref));
        }
        if (headerIndexMap.get(FILE_EXPERIMENT_ID) < input.length && !input[headerIndexMap.get(FILE_EXPERIMENT_ID)].equals(""))
        {
            String id = input[headerIndexMap.get(FILE_EXPERIMENT_ID)].trim();
            ExperimentSearchCriteria crit = new ExperimentSearchCriteria();
            crit.setPublicIdentifier(id);
            List<Experiment> results = apiFacade.experimentsByCriteriaSearchUtils(search.getApi(), crit);
            if (!results.isEmpty())
            {
                experimentName = results.get(0).getTitle();
                experimentReference = results.get(0).getReference();
                search.setExperimentRef(experimentReference);
                search.setExperimentName(experimentName);
            }
                
        }
        if (headerIndexMap.get(FILE) < input.length && !input[headerIndexMap.get(FILE)].equals(""))
        {
            String name = input[headerIndexMap.get(FILE)].trim();
            if (name.startsWith(VAR_START))
                name = getVariableValue(name);
            List<String> names = new ArrayList<String>();
            names.add(name);
            try
            {
                List<File> files = apiFacade.getFilesByName(search.getApi(), names, experimentName);
                for (File file : files)
                {
                    search.addFileReference(file.getReference());
                }
            } 
            catch (Exception e)
            {
                System.out.println("Exception occured retrieving file for FileContents test: " + search.getTestCase());
                log.error("Exception encountered:",e);
            }
            
        }
        if (headerIndexMap.get(HYB) < input.length && !input[headerIndexMap.get(HYB)].equals(""))
        {
            Hybridization hyb = apiFacade.getHybridization(search.getApi(), input[headerIndexMap.get(HYB)].trim());
            if (hyb != null && hyb.getArrayDesign() != null)
            {
                for (File file : hyb.getArrayDesign().getFiles())
                {
                    search.addFileReference(file.getReference());
                }
            }
        }
        if (headerIndexMap.get(MAGE) < input.length && !input[headerIndexMap.get(MAGE)].equals(""))
        {
            search.setMage(Boolean.parseBoolean(input[headerIndexMap.get(MAGE)].trim()));
            if (search.getExperimentRef() == null && experimentName != null)
            {
                Experiment exp = apiFacade.getExperiment(search.getApi(), experimentName);
                if (exp != null)
                {
                    search.setExperimentRef(exp.getReference());
                }
            }
        }
            
        if (headerIndexMap.get(MULTI_FILE_NUM) < input.length && !input[headerIndexMap.get(MULTI_FILE_NUM)].equals(""))
        {
            int num = Integer.parseInt(input[headerIndexMap.get(MULTI_FILE_NUM)].trim());
            String extension = input[headerIndexMap.get(MULTI_FILE_TYPE)].trim();
            FileSearchCriteria crit = new FileSearchCriteria();
            crit.setExtension(extension);
            crit.setExperiment(experimentReference);
            List<File> files = apiFacade.filesByCriteriaSearchUtils(search.getApi(), crit);
            for (int i = 0; i < files.size() && i < num; i++)
            {
                search.addFileReference(files.get(i).getReference());
            }
            
        }
    }

    /* (non-Javadoc)
     * @see caarray.client.test.suite.ConfigurableTestSuite#getColumnHeaders()
     */
    @Override
    protected String[] getColumnHeaders()
    {
        return COLUMN_HEADERS;
    }

    /* (non-Javadoc)
     * @see caarray.client.test.suite.ConfigurableTestSuite#getConfigFilename()
     */
    @Override
    protected String getConfigFilename()
    {
        return CONFIG_FILE;
    }

    /* (non-Javadoc)
     * @see caarray.client.test.suite.ConfigurableTestSuite#getType()
     */
    @Override
    protected String getType()
    {
        return "File Contents";
    }

}
