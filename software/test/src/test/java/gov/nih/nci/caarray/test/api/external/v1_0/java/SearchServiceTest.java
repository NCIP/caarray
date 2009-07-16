package gov.nih.nci.caarray.test.api.external.v1_0.java;

import gov.nih.nci.caarray.external.v1_0.vocabulary.Term;
import gov.nih.nci.caarray.services.external.v1_0.search.SearchService;
import org.junit.Before;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;
import gov.nih.nci.caarray.external.v1_0.AbstractCaArrayEntity;
import gov.nih.nci.caarray.external.v1_0.CaArrayEntityReference;
import gov.nih.nci.caarray.external.v1_0.data.DataFile;
import gov.nih.nci.caarray.external.v1_0.data.DataType;
import gov.nih.nci.caarray.external.v1_0.data.FileTypeCategory;
import gov.nih.nci.caarray.external.v1_0.data.QuantitationType;
import gov.nih.nci.caarray.external.v1_0.experiment.Experiment;
import gov.nih.nci.caarray.external.v1_0.experiment.Person;
import gov.nih.nci.caarray.external.v1_0.query.AnnotationCriterion;
import gov.nih.nci.caarray.external.v1_0.query.BiomaterialKeywordSearchCriteria;
import gov.nih.nci.caarray.external.v1_0.query.BiomaterialSearchCriteria;
import gov.nih.nci.caarray.external.v1_0.query.ExperimentSearchCriteria;
import gov.nih.nci.caarray.external.v1_0.query.FileSearchCriteria;
import gov.nih.nci.caarray.external.v1_0.query.HybridizationSearchCriteria;
import gov.nih.nci.caarray.external.v1_0.query.KeywordSearchCriteria;
import gov.nih.nci.caarray.external.v1_0.query.LimitOffset;
import gov.nih.nci.caarray.external.v1_0.query.QuantitationTypeSearchCriteria;
import gov.nih.nci.caarray.external.v1_0.query.SearchResult;
import gov.nih.nci.caarray.external.v1_0.sample.Biomaterial;
import gov.nih.nci.caarray.external.v1_0.sample.BiomaterialType;
import gov.nih.nci.caarray.external.v1_0.sample.Hybridization;
import gov.nih.nci.caarray.external.v1_0.vocabulary.Category;
import gov.nih.nci.caarray.external.v1_0.vocabulary.TermSource;
import gov.nih.nci.caarray.services.external.v1_0.InvalidReferenceException;
import gov.nih.nci.caarray.services.external.v1_0.NoEntityMatchingReferenceException;
import gov.nih.nci.caarray.services.external.v1_0.UnsupportedCategoryException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;

import java.util.Set;
import javax.ejb.EJBException;
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

        logForSilverCompatibility(TEST_OUTPUT, "test Person transmission");
        Person p = null;
        for (Person x : allPis) {
            if ("URN:LSID:caarray.nci.nih.gov:gov.nih.nci.caarray.external.v1_0.experiment.Person:10".equals(x.getId())) {
                p = x;
                break;
            }
        }
        assertNotNull(p);
        assertEquals("EmailAddress", "JEGreen@nih.gov", p.getEmailAddress());
        assertEquals("FirstName", "Jeffrey", p.getFirstName());
        assertEquals("LastName", "Green", p.getLastName());
        assertEquals("MiddleInitials", "E", p.getMiddleInitials());
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

    public void testGetTermsForCategory_Null() throws InvalidReferenceException {
        logForSilverCompatibility(TEST_NAME, "testGetTermsForCategory_Null");
        try{
            service.getTermsForCategory(null, null);
            fail("should fail");
        } catch(EJBException e) {
            assertEquals(NullPointerException.class, e.getCausedByException().getClass());
        }
    }

    @Test
    public void testGetTermsForCategory_Category() throws InvalidReferenceException {
        logForSilverCompatibility(TEST_NAME, "testGetTermsForCategory_Category");
        String cid = "URN:LSID:"+Category.class.getName()+":231"; // DerivedBioAssayType
        List<Term> l = service.getTermsForCategory(new CaArrayEntityReference(cid), null);
        assertEquals(25, l.size());

        logForSilverCompatibility(TEST_OUTPUT, "test Term transmission");
        Term term = null;
        for (Term t : l) {
            if ("URN:LSID:caarray.nci.nih.gov:gov.nih.nci.caarray.external.v1_0.vocabulary.Term:8".equals(t.getId())) {
                term = t;
                break;
            }
        }
        assertNotNull(term);
        logForSilverCompatibility(TEST_OUTPUT, "test Term transfer");
        assertEquals("MBEI", term.getValue());
        assertEquals("MO_756", term.getAccession());
        assertEquals("http://mged.sourceforge.net/ontologies/MGEDontology.php#MBEI", term.getUrl());
        TermSource s = term.getTermSource();
        assertEquals("URN:LSID:caarray.nci.nih.gov:gov.nih.nci.caarray.external.v1_0.vocabulary.TermSource:1", s.getId());
        assertEquals("MO", s.getName());
        assertEquals("1.3.1.1", s.getVersion());
        assertEquals("http://mged.sourceforge.net/ontologies/MGEDontology.php", s.getUrl());
    }

    @Test
    public void testGetTermsForCategory_CategoryAndPrefix() throws InvalidReferenceException {
        logForSilverCompatibility(TEST_NAME, "testGetTermsForCategory_CategoryAndPrefix");
        String cid = "URN:LSID:"+Category.class.getName()+":231"; // DerivedBioAssayType
        String prefix = "a";
        List<Term> l = service.getTermsForCategory(new CaArrayEntityReference(cid), prefix);
        assertEquals(6, l.size());
        for (Term t : l) {
            assertTrue("should start with " + prefix + " but value was "+t.getValue(), t.getValue().toLowerCase().startsWith(prefix));
        }
    }

    ////////////////////////////////////////////////////

    @Test
    public void testsearchForBiomaterials_All() throws InvalidReferenceException, UnsupportedCategoryException {
        logForSilverCompatibility(TEST_NAME, "testsearchForBiomaterials_All");

        BiomaterialSearchCriteria bisc = new BiomaterialSearchCriteria();
        SearchResult<Biomaterial> sr = service.searchForBiomaterials(bisc, null);
        assertTrue(sr.getResults().size() < 200);
        assertEquals(148, sr.getResults().size());

        logForSilverCompatibility(TEST_OUTPUT, "test Biomaterial transmission");
        Biomaterial b = null;
        for (Biomaterial n : sr.getResults()) {
            if (n.getId().endsWith(":1")) {
                b = n;
                break;
            }
        }
        assertNotNull(b);
        logForSilverCompatibility(TEST_OUTPUT, "test Biomaterial transfer");
        assertEquals("name", "Cy3 labeled Pr111 reference_8kNewPr111_14v1p4m11", b.getName());
        assertNull("cellType", b.getCellType());
        assertTrue("Characteristics", b.getCharacteristics().isEmpty());
        assertNull("Description", b.getDescription());
        assertNull("DiseaseState", b.getDiseaseState());
        assertEquals("URN:LSID:caarray.nci.nih.gov:gov.nih.nci.caarray.external.v1_0.experiment.Experiment:1", b.getExperiment().getId());
        assertNull("ExternalId", b.getExternalId());
        assertEquals("URN:LSID:caarray.nci.nih.gov:gov.nih.nci.caarray.external.v1_0.vocabulary.Term:152", b.getMaterialType().getTerm().getId());
        assertNull("MaterialType", b.getMaterialType().getUnit());
        assertNull("organism", b.getOrganism());
        assertNull("", b.getTissueSite());
        assertEquals(BiomaterialType.LABELED_EXTRACT, b.getType());
    }

    @Test
    public void testsearchForBiomaterials_Limit() throws InvalidReferenceException, UnsupportedCategoryException {
        logForSilverCompatibility(TEST_NAME, "testsearchForBiomaterials_Limit");

        BiomaterialSearchCriteria bisc = new BiomaterialSearchCriteria();
        SearchResult<Biomaterial> sr = service.searchForBiomaterials(bisc, null);
        assertTrue(sr.isFullResult());
        int all = sr.getResults().size();
        int chunk = all/2 + 10; // somewhere near past the middle
        // first batch
        LimitOffset off = new LimitOffset(chunk, 0);
        sr = service.searchForBiomaterials(bisc, off);
        assertEquals(off.getOffset(), sr.getFirstResultOffset());
        all -= sr.getResults().size();
        List<String> ids = getIds(sr.getResults());
        assertEquals(chunk, ids.size());
        // second batch
        off.setOffset(sr.getResults().size());
        sr = service.searchForBiomaterials(bisc, off);
        all -= sr.getResults().size();
        assertEquals(0, all);

        // check we didnt get the same ones in the second chunk
        ids.removeAll(getIds(sr.getResults()));
        assertEquals(chunk, ids.size());

    }

    private static List<String> getIds(List<? extends AbstractCaArrayEntity> entities) {
        ArrayList<String> ids = new ArrayList<String>(entities.size());
        for (AbstractCaArrayEntity a : entities) {
            ids.add(a.getId());
        }
        return ids;
    }

    @Test(expected = UnsupportedCategoryException.class)
    public void testsearchForBiomaterials_BadCategory() throws InvalidReferenceException, UnsupportedCategoryException {
        logForSilverCompatibility(TEST_NAME, "testsearchForBiomaterials_BadCategory");
        BiomaterialSearchCriteria bisc = new BiomaterialSearchCriteria();
        CaArrayEntityReference c = new CaArrayEntityReference("URN:LSID:caarray.nci.nih.gov:"+Category.class.getName()+":1");//CancerSite

        AnnotationCriterion a = new AnnotationCriterion(c, null);
        bisc.getAnnotationCriterions().add(a);
        service.searchForBiomaterials(bisc, null);
        fail("CancerSite not allowed as criterion");
        
    }

    private static final String URN_CATEGORY = "URN:LSID:caarray.nci.nih.gov:"+Category.class.getName()+":";

    @Test
    public void testsearchForBiomaterials_Category_DiseaseState() throws InvalidReferenceException, UnsupportedCategoryException {
        logForSilverCompatibility(TEST_NAME, "testsearchForBiomaterials_Category_DiseaseState");

        String value = "???";
        int count = 0;
        searchBioByCategory(97L, value, count);
    }

    @Test
    public void testsearchForBiomaterials_Category_CellType() throws InvalidReferenceException, UnsupportedCategoryException {
        logForSilverCompatibility(TEST_NAME, "testsearchForBiomaterials_Category_CellType");
        String value = "low grade prostatic intraepithelial neoplasia";
        int count = 19;
        searchBioByCategory(63L, value, count);
    }
    
    @Test
    public void testsearchForBiomaterials_Category_MaterialType() throws InvalidReferenceException, UnsupportedCategoryException {
        logForSilverCompatibility(TEST_NAME, "testsearchForBiomaterials_Category_MaterialType");
        String value = "synthetic_RNA";
        int count = 74;
        searchBioByCategory(153L, value, count);
    }

    @Test
    public void testsearchForBiomaterials_Category_OrganismPart() throws InvalidReferenceException, UnsupportedCategoryException {
        logForSilverCompatibility(TEST_NAME, "testsearchForBiomaterials_Category_OrganismPart");//tissue_site
        String value = "???";
        int count = 0;
        searchBioByCategory(178L, value, count);
    }

    private void searchBioByCategory(long categoryId, String value, int count) throws UnsupportedCategoryException, InvalidReferenceException {
        CaArrayEntityReference cat = new CaArrayEntityReference(URN_CATEGORY + categoryId);
        BiomaterialSearchCriteria bisc = new BiomaterialSearchCriteria();
        bisc.getAnnotationCriterions().add(new AnnotationCriterion(cat, value));
        SearchResult<Biomaterial> sr = service.searchForBiomaterials(bisc, null);
        assertEquals(count, sr.getResults().size());
    }

    ////////////////////////////////////////////////////

    @Test
    public void testGetByReference_Experiment() {
        logForSilverCompatibility(TEST_NAME, "testGetByReference_Experiment");
        try {
            Experiment e = (Experiment) service.getByReference(
                    new CaArrayEntityReference(
                            "URN:LSID:caarray.nci.nih.gov:gov.nih.nci.caarray.external.v1_0.experiment.Experiment:1"));
            assertEquals("test1", e.getTitle());
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
            assertEquals(badRef.getId(), e.getReference().getId());
        }

        badRef = new CaArrayEntityReference(
                "URN:LSID:caarray.nci.nih.gov:gov.nih.nci.caarray.external.v1_0.experiment.BfdsfdsVBD:11");
        try {
            service.getByReference(badRef);
            fail("Expected a NoEntityMatchingReferenceException");
        } catch (NoEntityMatchingReferenceException e) {
            assertEquals(badRef.getId(), e.getReference().getId());
        }
    }

    /////////////////////////////////////////////////

    @Test
    public void testGetByReferences() {
        logForSilverCompatibility(TEST_NAME, "testGetByReferences");
        List<CaArrayEntityReference> refs = Arrays.asList(new CaArrayEntityReference(
                "URN:LSID:caarray.nci.nih.gov:gov.nih.nci.caarray.external.v1_0.sample.Biomaterial:1"),
                new CaArrayEntityReference(
                        "URN:LSID:caarray.nci.nih.gov:gov.nih.nci.caarray.external.v1_0.sample.Biomaterial:2"));
        try {
            List<AbstractCaArrayEntity> entities = service.getByReferences(refs);
            assertEquals(2, entities.size());
            assertTrue(entities.get(0) instanceof Biomaterial);
            Biomaterial b = (Biomaterial) entities.get(0);
            assertEquals("Cy3 labeled Pr111 reference_8kNewPr111_14v1p4m11", b.getName());
            assertEquals(BiomaterialType.LABELED_EXTRACT, b.getType());
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
            assertEquals(badRef.getId(), e.getReference().getId());
        }

        badRef = new CaArrayEntityReference(
                "URN:LSID:caarray.nci.nih.gov:gov.nih.nci.caarray.external.v1_0.experiment.BfdsfdsVBD:11");
        try {
            service.getByReferences(Collections.singletonList(badRef));
            fail("Expected a NoEntityMatchingReferenceException");
        } catch (NoEntityMatchingReferenceException e) {
            assertEquals(badRef.getId(), e.getReference().getId());
        }

    }

    /////////////////////////////////////////////////

    @Test
    public void testSearchExperimentsByKeyword() {
        logForSilverCompatibility(TEST_NAME, "testSearchExperimentsByKeyword");
        KeywordSearchCriteria crit = new KeywordSearchCriteria();
        crit.setKeyword("Affymetrix");
        List<Experiment> experiments = service.searchForExperimentsByKeyword(crit, null).getResults();
        assertEquals(1, experiments.size());
        assertEquals("test2", experiments.get(0).getTitle());

        crit.setKeyword("test1");
        experiments = service.searchForExperimentsByKeyword(crit, null).getResults();
        assertEquals(1, experiments.size());
        assertEquals("test1", experiments.get(0).getTitle());
    }

    /////////////////////////////////////////////

    @Test
    public void testSearchExperiments() {
        logForSilverCompatibility(TEST_NAME, "testSearchExperiments");
        ExperimentSearchCriteria crit = new ExperimentSearchCriteria();
        crit.setTitle("test1");
        try {
            logForSilverCompatibility(TEST_OUTPUT, "title");
            List<Experiment> experiments = service.searchForExperiments(crit, null).getResults();
            assertEquals(1, experiments.size());
            assertEquals("test1", experiments.get(0).getTitle());
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
            logForSilverCompatibility(TEST_OUTPUT, "public id");
            List<Experiment> experiments = service.searchForExperiments(crit, null).getResults();
            assertEquals(1, experiments.size());
            assertEquals("test1", experiments.get(0).getTitle());
        } catch (InvalidReferenceException e) {
            e.printStackTrace();
            fail("Couldn't search experiments: " + e);
        } catch (UnsupportedCategoryException e) {
            e.printStackTrace();
            fail("Couldn't search experiments: " + e);
        }

        crit = new ExperimentSearchCriteria();
        crit.setOrganism(new CaArrayEntityReference(
                "URN:LSID:caarray.nci.nih.gov:gov.nih.nci.caarray.external.v1_0.experiment.Organism:2"));// Mus musculus
        crit.setPublicIdentifier("admin-00002");
        try {
            logForSilverCompatibility(TEST_OUTPUT, "title and Organism");
            List<Experiment> experiments = service.searchForExperiments(crit, null).getResults();
            assertEquals(1, experiments.size());
            assertEquals("test2", experiments.get(0).getTitle());
        } catch (InvalidReferenceException e) {
            e.printStackTrace();
            fail("Couldn't search experiments: " + e);
        } catch (UnsupportedCategoryException e) {
            e.printStackTrace();
            fail("Couldn't search experiments: " + e);
        }
    }

    //////////////////////////
    
    @Test
    public void testSearchBiomaterialsByKeyword() {
        logForSilverCompatibility(TEST_NAME, "testSearchBiomaterialsByKeyword");
        BiomaterialKeywordSearchCriteria crit = new BiomaterialKeywordSearchCriteria();
        crit.setKeyword("8knew");
        List<Biomaterial> bms = service.searchForBiomaterialsByKeyword(crit, null).getResults();
        assertEquals(64, bms.size());

        for (Biomaterial b : bms) {
            assertTrue("unexpected name " + b.getName(), b.getName().toLowerCase().contains("8knew"));
        }
    }

    ///////////////////////////

    @Test
    public void testSearchForBiomaterials() throws InvalidReferenceException, UnsupportedCategoryException {
        logForSilverCompatibility(TEST_NAME, "testSearchForBiomaterials");
        BiomaterialSearchCriteria crit = new BiomaterialSearchCriteria();
        crit.setExperiment(new CaArrayEntityReference("URN:LSID:caarray.nci.nih.gov:gov.nih.nci.caarray.external.v1_0.experiment.Experiment:1"));
        crit.setTypes(EnumSet.of(BiomaterialType.SOURCE, BiomaterialType.SAMPLE));
        List<Biomaterial> bms = service.searchForBiomaterials(crit, null).getResults();
        assertEquals(74, bms.size());
    }

    @Test
    public void testSearchForHybridizations() throws InvalidReferenceException {
        logForSilverCompatibility(TEST_NAME, "testSearchForHybridizations");
        HybridizationSearchCriteria crit = new HybridizationSearchCriteria();
        crit.setExperiment(new CaArrayEntityReference("URN:LSID:caarray.nci.nih.gov:gov.nih.nci.caarray.external.v1_0.experiment.Experiment:1"));
        List<Hybridization> hybs = service.searchForHybridizations(crit, null).getResults();
        assertEquals(19, hybs.size());
        Hybridization h = null;

        logForSilverCompatibility(TEST_OUTPUT, "test Hybridization transmission");
        for (Hybridization x : hybs) {
            if ("URN:LSID:caarray.nci.nih.gov:gov.nih.nci.caarray.external.v1_0.sample.Hybridization:1".equals(x.getId())) {
                h = x;
                break;
            }
        }
        assertNotNull(h);
        assertEquals("ArrayDesign.Name", "Mm-Incyte-v1px_16Bx24Cx23R", h.getArrayDesign().getName());
        assertEquals("experiment.id", "URN:LSID:caarray.nci.nih.gov:gov.nih.nci.caarray.external.v1_0.experiment.Experiment:1", h.getExperiment().getId());
        assertEquals("factorValues.size", 2, h.getFactorValues().size());
        assertEquals("name", "gov.nih.nci.ncicb.caarray:Hybridization:1015897590131481:1", h.getName());
    }

    @Test
    public void testSearchForQuantitationTypes() throws InvalidReferenceException {
        logForSilverCompatibility(TEST_NAME, "testSearchForQuantitationTypes");
        QuantitationTypeSearchCriteria crit = new QuantitationTypeSearchCriteria();
        crit.setHybridization(new CaArrayEntityReference(
                "URN:LSID:caarray.nci.nih.gov:gov.nih.nci.caarray.external.v1_0.sample.Hybridization:1"));
        List<QuantitationType> types = service.searchForQuantitationTypes(crit);
        assertEquals(38, types.size());
        QuantitationType qt = null;
        for (QuantitationType t : types) {
            if ("URN:LSID:caarray.nci.nih.gov:gov.nih.nci.caarray.external.v1_0.data.QuantitationType:59".equals(t.getId())) {
                qt = t;
                break;
            }
        }
        assertEquals("% > B532+1SD", qt.getName());
        assertEquals(DataType.INTEGER, qt.getDataType());
    }

    ///////////////////////////////
    
    @Test
    public void testSearchForFiles() throws InvalidReferenceException {
        logForSilverCompatibility(TEST_NAME, "testSearchForFiles");
        FileSearchCriteria crit = new FileSearchCriteria();
        crit.setExperiment(new CaArrayEntityReference(
                "URN:LSID:caarray.nci.nih.gov:gov.nih.nci.caarray.external.v1_0.experiment.Experiment:1"));
        crit.setCategories(EnumSet.of(FileTypeCategory.DERIVED));
        List<DataFile> files = service.searchForFiles(crit, null).getResults();
        assertEquals(19, files.size());

        logForSilverCompatibility(TEST_OUTPUT, "test DataFile transmission");
        DataFile df = null;
        for (DataFile f : files) {
            if ("URN:LSID:caarray.nci.nih.gov:gov.nih.nci.caarray.external.v1_0.data.DataFile:2".equals(f.getId())) {
                df = f;
                break;
            }
        }
        assertNotNull(df);
        assertEquals("fileType.name", "GENEPIX_GPR", df.getFileType().getName());
        assertEquals("CompressedSize", 682885, df.getCompressedSize());
        assertEquals("UncompressedSize", 1771373, df.getUncompressedSize());        
    }

    // ExperimentGraphNodes, Extention, Type
    @Test
    public void testSearchForFiles_Experiment() throws InvalidReferenceException {
        logForSilverCompatibility(TEST_NAME, "testSearchForFiles_Experiment");
        FileSearchCriteria crit = new FileSearchCriteria();
        crit.setExperiment(new CaArrayEntityReference(
                "URN:LSID:caarray.nci.nih.gov:gov.nih.nci.caarray.external.v1_0.experiment.Experiment:1"));
        List<DataFile> files = service.searchForFiles(crit, null).getResults();
        assertEquals(19, files.size());
    }

    @Test
    public void testSearchForFiles_ExperimentGraphNodes() throws InvalidReferenceException {
        logForSilverCompatibility(TEST_NAME, "testSearchForFiles_ExperimentGraphNodes");
        Set<CaArrayEntityReference> nodes = new HashSet<CaArrayEntityReference>(4);
        nodes.add(new CaArrayEntityReference(
                "URN:LSID:caarray.nci.nih.gov:gov.nih.nci.caarray.external.v1_0.sample.Hybridization:1"));//Hybridization
        nodes.add(new CaArrayEntityReference(
                "URN:LSID:caarray.nci.nih.gov:gov.nih.nci.caarray.external.v1_0.sample.Biomaterial:74"));//Extract
        nodes.add(new CaArrayEntityReference(
                "URN:LSID:caarray.nci.nih.gov:gov.nih.nci.caarray.external.v1_0.sample.Biomaterial:11"));//Labled Extract
        nodes.add(new CaArrayEntityReference(
                "URN:LSID:caarray.nci.nih.gov:gov.nih.nci.caarray.external.v1_0.sample.Biomaterial:97"));//Sample
        nodes.add(new CaArrayEntityReference(
                "URN:LSID:caarray.nci.nih.gov:gov.nih.nci.caarray.external.v1_0.sample.Biomaterial:112"));//Source
        FileSearchCriteria crit = new FileSearchCriteria();
        crit.setExperimentGraphNodes(nodes);
        List<DataFile> files = service.searchForFiles(crit, null).getResults();
        List<String> expectedNames = Arrays.asList(
                "8kReversNew17_111_4601_m83.gpr",
                "8kNew111_14v1p4m12.gpr",
                "8kNewPr111_14v1p4m13.gpr",
                "8kNew111_17m12701m36.gpr",
                "8kNew111_14_4601_m84.gpr");
        assertEquals(expectedNames.size(), files.size());
        
        for (DataFile f : files) {
            assertTrue(f.getName() + " not found ", expectedNames.remove(f.getName()));
        }
        assertTrue(expectedNames.toString(), expectedNames.isEmpty());
    }


 }
