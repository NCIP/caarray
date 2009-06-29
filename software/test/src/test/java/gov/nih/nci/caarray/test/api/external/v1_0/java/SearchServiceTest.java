package gov.nih.nci.caarray.test.api.external.v1_0.java;

import gov.nih.nci.caarray.external.v1_0.vocabulary.Term;
import gov.nih.nci.caarray.services.external.v1_0.search.SearchService;
import org.junit.Before;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import gov.nih.nci.caarray.external.v1_0.AbstractCaArrayEntity;
import gov.nih.nci.caarray.external.v1_0.CaArrayEntityReference;
import gov.nih.nci.caarray.external.v1_0.data.DataFile;
import gov.nih.nci.caarray.external.v1_0.data.DataType;
import gov.nih.nci.caarray.external.v1_0.data.FileTypeCategory;
import gov.nih.nci.caarray.external.v1_0.data.QuantitationType;
import gov.nih.nci.caarray.external.v1_0.experiment.Experiment;
import gov.nih.nci.caarray.external.v1_0.experiment.Person;
import gov.nih.nci.caarray.external.v1_0.query.BiomaterialKeywordSearchCriteria;
import gov.nih.nci.caarray.external.v1_0.query.BiomaterialSearchCriteria;
import gov.nih.nci.caarray.external.v1_0.query.ExperimentSearchCriteria;
import gov.nih.nci.caarray.external.v1_0.query.FileSearchCriteria;
import gov.nih.nci.caarray.external.v1_0.query.HybridizationSearchCriteria;
import gov.nih.nci.caarray.external.v1_0.query.KeywordSearchCriteria;
import gov.nih.nci.caarray.external.v1_0.query.QuantitationTypeSearchCriteria;
import gov.nih.nci.caarray.external.v1_0.query.SearchResult;
import gov.nih.nci.caarray.external.v1_0.sample.Biomaterial;
import gov.nih.nci.caarray.external.v1_0.sample.BiomaterialType;
import gov.nih.nci.caarray.external.v1_0.sample.Hybridization;
import gov.nih.nci.caarray.external.v1_0.vocabulary.Category;
import gov.nih.nci.caarray.services.external.v1_0.InvalidReferenceException;
import gov.nih.nci.caarray.services.external.v1_0.NoEntityMatchingReferenceException;
import gov.nih.nci.caarray.services.external.v1_0.UnsupportedCategoryException;

import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;

import org.junit.Test;


/**
 * Search service Test
 * @author dkokotov
 */
public class SearchServiceTest extends AbstractExternalJavaApiTest {

    private SearchService service;

    @Before
    public void initService() {
        service = caArrayServer.getSearchService();
    }

    @Test
    public void testGetAllPrincipalInvestigators() {
        logForSilverCompatibility(TEST_NAME, "testGetAllPrincipalInvestigators");
        List<Person> allPis = service.getAllPrincipalInvestigators();
        assertEquals(3, allPis.size());
    }

    /////////////////////////////////////////////////////////////

    @Test(expected = InvalidReferenceException.class)
    public void testGetAllCharacteristicCategories_NonEx() throws InvalidReferenceException {
        logForSilverCompatibility(TEST_NAME, "testGetAllCharacteristicCategories_NonEx");
        String qtid = "URN:LSID:gov.nih.nci.caarray.external.v1_0.data.QuantitationType:1";
        service.getAllCharacteristicCategories(new CaArrayEntityReference(qtid));
    }

    @Test(expected = InvalidReferenceException.class)
    public void testGetAllCharacteristicCategories_Bad() throws InvalidReferenceException {
        logForSilverCompatibility(TEST_NAME, "testGetAllCharacteristicCategories_Bad");
        String qtid = "foo";
        service.getAllCharacteristicCategories(new CaArrayEntityReference(qtid));
    }

    @Test
    public void testGetAllCharacteristicCategories_All() throws InvalidReferenceException {
        logForSilverCompatibility(TEST_NAME, "testGetAllCharacteristicCategories_All");
        List<Category> l = service.getAllCharacteristicCategories(null);
        assertEquals(9, l.size());
    }

    @Test
    public void testGetAllCharacteristicCategories_Ex() throws InvalidReferenceException {
        logForSilverCompatibility(TEST_NAME, "testGetAllCharacteristicCategories_Ex");
        String type = gov.nih.nci.caarray.external.v1_0.experiment.Experiment.class.getName();
        String eid = "URN:LSID:" + type + ":1";
        CaArrayEntityReference ref = new CaArrayEntityReference(eid);
        List<Category> l = service.getAllCharacteristicCategories(ref);
        assertEquals(9, l.size());
    }

    /////////////////////////////////////////////////////

    @Test(expected = InvalidReferenceException.class)
    public void testGetTermsForCategory_NonCategory() throws InvalidReferenceException {
        logForSilverCompatibility(TEST_NAME, "testGetTermsForCategory_NonCategory");
        String qtid = "URN:LSID:gov.nih.nci.caarray.external.v1_0.data.QuantitationType:1";
        service.getTermsForCategory(new CaArrayEntityReference(qtid), null);
    }

    @Test(expected = InvalidReferenceException.class)
    public void testGetTermsForCategory_Null() throws InvalidReferenceException {
        logForSilverCompatibility(TEST_NAME, "testGetTermsForCategory_Null");
        String qtid = "URN:LSID:gov.nih.nci.caarray.external.v1_0.data.QuantitationType:1";
        service.getTermsForCategory(null, null);
    }

    @Test
    public void testGetTermsForCategory_Category() throws InvalidReferenceException {
        logForSilverCompatibility(TEST_NAME, "testGetTermsForCategory_Category");
        String cid = "URN:LSID:"+Category.class.getName()+":231"; // DerivedBioAssayType
        List<Term> l = service.getTermsForCategory(new CaArrayEntityReference(cid), null);
        assertEquals(5, l.size());
    }

    @Test
    public void testGetTermsForCategory_CategoryAndPrefix() throws InvalidReferenceException {
        logForSilverCompatibility(TEST_NAME, "testGetTermsForCategory_CategoryAndPrefix");
        String cid = "URN:LSID:"+Category.class.getName()+":231"; // DerivedBioAssayType
        String prefix = "a";
        List<Term> l = service.getTermsForCategory(new CaArrayEntityReference(cid), prefix);
        assertEquals(3, l.size());
        for (Term t : l) {
            assertTrue(t.getValue().startsWith(prefix));
        }
    }

    ////////////////////////////////////////////////////

    @Test
    public void testsearchForBiomaterials_All() throws InvalidReferenceException, UnsupportedCategoryException {
        logForSilverCompatibility(TEST_NAME, "testsearchForBiomaterials_All");

        BiomaterialSearchCriteria bisc = new BiomaterialSearchCriteria();
        SearchResult<Biomaterial> sr = service.searchForBiomaterials(bisc, null);
        assertTrue(sr.getResults().size() < 200);
        assertEquals(50, sr.getResults().size());
    }

    /**
     * Search for biomaterials satisfying the given search criteria.
     *
     * @param criteria the search criteria
     * @param pagingParams paging parameters
     * @return the subset of the biomaterials matching the given criteria, subject to the paging params.
     * @throws InvalidReferenceException if there is no experiment with given reference
     * @throws UnsupportedCategoryException if the search criteria includes an annotation criterion with a category
     *             other that disease state, cell type, material type, tissue site.
     * /
    SearchResult<Biomaterial> searchForBiomaterials(BiomaterialSearchCriteria criteria, LimitOffset pagingParams)
            throws InvalidReferenceException, UnsupportedCategoryException;
*/
    ////////////////////////////////////////////////////

    @Test
    public void testGetByReference_Experiment() {
        try {
            Experiment e = (Experiment) service.getByReference(
                    new CaArrayEntityReference(
                            "URN:LSID:caarray.nci.nih.gov:gov.nih.nci.caarray.external.v1_0.experiment.Experiment:1"));
            assertEquals("fdsfds", e.getTitle());
        } catch (NoEntityMatchingReferenceException e) {
            e.printStackTrace();
            fail("Couldn't retrieve by reference: " + e);
        }

        CaArrayEntityReference badRef = new CaArrayEntityReference(
                "URN:LSID:caarray.nci.nih.gov:gov.nih.nci.caarray.external.v1_0.experiment.Experiment:10");
        try {
            service.getByReference(badRef);
            fail("Expected a NoEntityMatchingReferenceException");
        } catch (NoEntityMatchingReferenceException e) {
            assertEquals(badRef, e.getReference());
        }

        badRef = new CaArrayEntityReference(
                "URN:LSID:caarray.nci.nih.gov:gov.nih.nci.caarray.external.v1_0.experiment.BfdsfdsVBD:11");
        try {
            service.getByReference(badRef);
            fail("Expected a NoEntityMatchingReferenceException");
        } catch (NoEntityMatchingReferenceException e) {
            assertEquals(badRef, e.getReference());
        }
    }

    /////////////////////////////////////////////////

    @Test
    public void testGetByReferences() {
        List<CaArrayEntityReference> refs = Arrays.asList(new CaArrayEntityReference(
                "URN:LSID:caarray.nci.nih.gov:gov.nih.nci.caarray.external.v1_0.sample.Biomaterial:1"),
                new CaArrayEntityReference(
                        "URN:LSID:caarray.nci.nih.gov:gov.nih.nci.caarray.external.v1_0.sample.Biomaterial:2"));
        try {
            List<AbstractCaArrayEntity> entities = service.getByReferences(refs);
            assertEquals(2, entities.size());
            assertTrue(entities.get(0) instanceof Biomaterial);
            Biomaterial b = (Biomaterial) entities.get(0);
            assertEquals("TK6 replicate 2", b.getName());
            assertEquals(BiomaterialType.SOURCE, b.getType());
        } catch (NoEntityMatchingReferenceException e) {
            e.printStackTrace();
            fail("Couldnt retrieve by references: " + e);
        }

        CaArrayEntityReference badRef = new CaArrayEntityReference(
        "URN:LSID:caarray.nci.nih.gov:gov.nih.nci.caarray.external.v1_0.experiment.Experiment:10");

        try {
            service.getByReferences(Collections.singletonList(badRef));
            fail("Expected a NoEntityMatchingReferenceException");
        } catch (NoEntityMatchingReferenceException e) {
            assertEquals(badRef, e.getReference());
        }

        badRef = new CaArrayEntityReference(
                "URN:LSID:caarray.nci.nih.gov:gov.nih.nci.caarray.external.v1_0.experiment.BfdsfdsVBD:11");
        try {
            service.getByReferences(Collections.singletonList(badRef));
            fail("Expected a NoEntityMatchingReferenceException");
        } catch (NoEntityMatchingReferenceException e) {
            assertEquals(badRef, e.getReference());
        }

    }

    /////////////////////////////////////////////////

    @Test
    public void testSearchExperimentsByKeyword() {
        KeywordSearchCriteria crit = new KeywordSearchCriteria();
        crit.setKeyword("Affymetrix");
        List<Experiment> experiments = service.searchForExperimentsByKeyword(crit, null).getResults();
        assertEquals(2, experiments.size());
        assertEquals("dsfdsf", experiments.get(0).getTitle());

        crit.setKeyword("ffsdf");
        experiments = service.searchForExperimentsByKeyword(crit, null).getResults();
        assertEquals(1, experiments.size());
        assertEquals("ffsdf", experiments.get(0).getTitle());
    }

    /////////////////////////////////////////////

    @Test
    public void testSearchExperiments() {
        ExperimentSearchCriteria crit = new ExperimentSearchCriteria();
        crit.setTitle("dsfdsf");
        try {
            List<Experiment> experiments = service.searchForExperiments(crit, null).getResults();
            assertEquals(2, experiments.size());
            assertEquals("dsfdsf", experiments.get(0).getTitle());
        } catch (InvalidReferenceException e) {
            e.printStackTrace();
            fail("Couldn't search experiments: " + e);
        } catch (UnsupportedCategoryException e) {
            e.printStackTrace();
            fail("Couldn't search experiments: " + e);
        }

        crit = new ExperimentSearchCriteria();
        crit.setPublicIdentifier("admin-00001");
        try {
            List<Experiment> experiments = service.searchForExperiments(crit, null).getResults();
            assertEquals(3, experiments.size());
            assertEquals("dsfdsf", experiments.get(0).getTitle());
        } catch (InvalidReferenceException e) {
            e.printStackTrace();
            fail("Couldn't search experiments: " + e);
        } catch (UnsupportedCategoryException e) {
            e.printStackTrace();
            fail("Couldn't search experiments: " + e);
        }

        crit = new ExperimentSearchCriteria();
        crit.setOrganism(new CaArrayEntityReference(
                "URN:LSID:caarray.nci.nih.gov:gov.nih.nci.caarray.external.v1_0.experiment.Organism:5"));
        crit.setPublicIdentifier("admin-00001");
        try {
            List<Experiment> experiments = service.searchForExperiments(crit, null).getResults();
            assertEquals(1, experiments.size());
            assertEquals("fdsfds", experiments.get(0).getTitle());
        } catch (InvalidReferenceException e) {
            e.printStackTrace();
            fail("Couldn't search experiments: " + e);
        } catch (UnsupportedCategoryException e) {
            e.printStackTrace();
            fail("Couldn't search experiments: " + e);
        }
    }

    @Test
    public void testSearchBiomaterialsByKeyword() {
        BiomaterialKeywordSearchCriteria crit = new BiomaterialKeywordSearchCriteria();
        crit.setKeyword("MDR");
        List<Biomaterial> bms = service.searchForBiomaterialsByKeyword(crit, null).getResults();
        assertEquals(2, bms.size());
        assertEquals("TK6MDR1 replicate 1", bms.get(0).getName());
        assertEquals(BiomaterialType.SAMPLE, bms.get(0).getType());
        assertEquals("TK6MDR1 replicate 2", bms.get(1).getName());
        assertEquals(BiomaterialType.SAMPLE, bms.get(1).getType());
    }

    @Test
    public void testSearchForBiomaterials() throws InvalidReferenceException, UnsupportedCategoryException {
        BiomaterialSearchCriteria crit = new BiomaterialSearchCriteria();
        crit.setExperiment(new CaArrayEntityReference("URN:LSID:caarray.nci.nih.gov:gov.nih.nci.caarray.external.v1_0.experiment.Experiment:1"));
        crit.setTypes(EnumSet.of(BiomaterialType.SOURCE, BiomaterialType.SAMPLE));
        List<Biomaterial> bms = service.searchForBiomaterials(crit, null).getResults();
        assertEquals(12, bms.size());
    }

    @Test
    public void testSearchForHybridizations() throws InvalidReferenceException {
        HybridizationSearchCriteria crit = new HybridizationSearchCriteria();
        crit.setExperiment(new CaArrayEntityReference("URN:LSID:caarray.nci.nih.gov:gov.nih.nci.caarray.external.v1_0.experiment.Experiment:1"));
        List<Hybridization> hybs = service.searchForHybridizations(crit, null).getResults();
        assertEquals(6, hybs.size());
    }

    @Test
    public void testSearchForQuantitationTypes() throws InvalidReferenceException {
        QuantitationTypeSearchCriteria crit = new QuantitationTypeSearchCriteria();
        crit.setHybridization(new CaArrayEntityReference(
                "URN:LSID:caarray.nci.nih.gov:gov.nih.nci.caarray.external.v1_0.sample.Hybridization:1"));
        List<QuantitationType> types = service.searchForQuantitationTypes(crit);
        assertEquals(7, types.size());
        assertEquals("CELIntensity", types.get(0).getName());
        assertEquals(DataType.FLOAT, types.get(0).getDataType());
    }

    @Test
    public void testSearchForFiles() throws InvalidReferenceException {
        FileSearchCriteria crit = new FileSearchCriteria();
        crit.setExperiment(new CaArrayEntityReference(
                "URN:LSID:caarray.nci.nih.gov:gov.nih.nci.caarray.external.v1_0.experiment.Experiment:1"));
        crit.setCategories(EnumSet.of(FileTypeCategory.RAW));
        List<DataFile> files = service.searchForFiles(crit, null).getResults();
        assertEquals(12, files.size());
        assertEquals("H_TK6 MDR1 replicate 1.CEL", files.get(0).getName());
    }
 }
