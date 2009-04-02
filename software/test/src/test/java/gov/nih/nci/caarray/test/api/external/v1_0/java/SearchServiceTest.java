package gov.nih.nci.caarray.test.api.external.v1_0.java;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import gov.nih.nci.caarray.external.v1_0.AbstractCaArrayEntity;
import gov.nih.nci.caarray.external.v1_0.CaArrayEntityReference;
import gov.nih.nci.caarray.external.v1_0.array.ArrayDesign;
import gov.nih.nci.caarray.external.v1_0.array.ArrayProvider;
import gov.nih.nci.caarray.external.v1_0.data.FileType;
import gov.nih.nci.caarray.external.v1_0.experiment.Experiment;
import gov.nih.nci.caarray.external.v1_0.experiment.Organism;
import gov.nih.nci.caarray.external.v1_0.experiment.Person;
import gov.nih.nci.caarray.external.v1_0.query.BiomaterialKeywordSearchCriteria;
import gov.nih.nci.caarray.external.v1_0.query.BiomaterialSearchCriteria;
import gov.nih.nci.caarray.external.v1_0.query.ExperimentSearchCriteria;
import gov.nih.nci.caarray.external.v1_0.query.HybridizationSearchCriteria;
import gov.nih.nci.caarray.external.v1_0.query.KeywordSearchCriteria;
import gov.nih.nci.caarray.external.v1_0.sample.Biomaterial;
import gov.nih.nci.caarray.external.v1_0.sample.BiomaterialType;
import gov.nih.nci.caarray.external.v1_0.sample.Hybridization;
import gov.nih.nci.caarray.services.external.v1_0.InvalidReferenceException;
import gov.nih.nci.caarray.services.external.v1_0.NoEntityMatchingReferenceException;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;

import org.junit.Test;


/**
 * Search service Test
 * @author dkokotov
 */
public class SearchServiceTest extends AbstractExternalJavaApiTest {
    @Test
    public void testGetAllFileTypes() {
        List<FileType> allFileTypes = caArrayServer.getSearchService().getAllFileTypes(null);
        assertEquals(34, allFileTypes.size());
    }

    @Test
    public void testGetAllProviders() {
        List<ArrayProvider> allProviders = caArrayServer.getSearchService().getAllProviders(null);
        assertEquals(8, allProviders.size());
    }
    
    @Test
    public void testGetAllArrayDesigns() {
        List<ArrayDesign> allDesigns = caArrayServer.getSearchService().getAllArrayDesigns(null);
        assertEquals(3, allDesigns.size());
    }

    @Test
    public void testGetAllOrganisms() {
        List<Organism> allOrgs = caArrayServer.getSearchService().getAllOrganisms(null);
        assertEquals(33, allOrgs.size());
    }
    
    @Test
    public void testGetAllPrincipalInvestigators() {
        List<Person> allPis = caArrayServer.getSearchService().getAllPrincipalInvestigators(null);
        assertEquals(10, allPis.size());
    }

    @Test
    public void testGetByReference() {
        try {
            Experiment e = (Experiment) caArrayServer.getSearchService().getByReference(
                    new CaArrayEntityReference(
                            "URN:LSID:caarray.nci.nih.gov:gov.nih.nci.caarray.external.v1_0.experiment.Experiment:1"));
            assertEquals("fdsfds", e.getTitle());
        } catch (NoEntityMatchingReferenceException e) {
            e.printStackTrace();
            fail("Couldn't retrieve by reference: " + e);
        }
    }

    @Test
    public void testGetByReferences() {
        List<CaArrayEntityReference> refs = Arrays.asList(new CaArrayEntityReference(
                "URN:LSID:caarray.nci.nih.gov:gov.nih.nci.caarray.external.v1_0.sample.Biomaterial:1"),
                new CaArrayEntityReference(
                        "URN:LSID:caarray.nci.nih.gov:gov.nih.nci.caarray.external.v1_0.sample.Biomaterial:2"));
        try {
            List<AbstractCaArrayEntity> entities = caArrayServer.getSearchService().getByReferences(refs);
            assertEquals(2, entities.size());
            assertTrue(entities.get(0) instanceof Biomaterial);
            Biomaterial b = (Biomaterial) entities.get(0);
            assertEquals("TK6 replicate 2", b.getName());
            assertEquals(BiomaterialType.SOURCE, b.getType());
        } catch (NoEntityMatchingReferenceException e) {
            e.printStackTrace();
            fail("Couldnt retrieve by references: " + e);
        }
    }

    @Test
    public void testSearchExperimentsByKeyword() {
        KeywordSearchCriteria crit = new KeywordSearchCriteria();
        crit.setKeyword("Affymetrix");
        List<Experiment> experiments = caArrayServer.getSearchService().searchForExperimentsByKeyword(crit, null);
        assertEquals(2, experiments.size());
        assertEquals("dsfdsf", experiments.get(0).getTitle());
    }
    
    @Test
    public void testSearchExperiments() {
        ExperimentSearchCriteria crit = new ExperimentSearchCriteria();
        crit.setArrayProvider(new ArrayProvider("Affymetrix"));
        try {
            List<Experiment> experiments = caArrayServer.getSearchService().searchForExperiments(crit, null);
            assertEquals(2, experiments.size());
            assertEquals("dsfdsf", experiments.get(0).getTitle());
        } catch (InvalidReferenceException e) {
            e.printStackTrace();
            fail("Couldn't search experiments: " + e);
        }
    }

    @Test
    public void testSearchBiomaterialsByKeyword() {
        BiomaterialKeywordSearchCriteria crit = new BiomaterialKeywordSearchCriteria();
        crit.setKeyword("MDR");
        List<Biomaterial> bms = caArrayServer.getSearchService().searchForBiomaterialsByKeyword(crit, null);
        assertEquals(2, bms.size());
        assertEquals("TK6MDR1 replicate 1", bms.get(0).getName());
        assertEquals(BiomaterialType.SAMPLE, bms.get(0).getType());
        assertEquals("TK6MDR1 replicate 2", bms.get(1).getName());
        assertEquals(BiomaterialType.SAMPLE, bms.get(1).getType());
    }
    
    @Test
    public void testSearchForBiomaterials() throws InvalidReferenceException {
        BiomaterialSearchCriteria crit = new BiomaterialSearchCriteria();
        crit.setExperiment(new CaArrayEntityReference("URN:LSID:caarray.nci.nih.gov:gov.nih.nci.caarray.external.v1_0.experiment.Experiment:1"));
        crit.setTypes(EnumSet.of(BiomaterialType.SOURCE, BiomaterialType.SAMPLE));
        List<Biomaterial> bms = caArrayServer.getSearchService().searchForBiomaterials(crit, null);        
        assertEquals(12, bms.size());
    }
    
    @Test
    public void testSearchForHybridizations() throws InvalidReferenceException {
        HybridizationSearchCriteria crit = new HybridizationSearchCriteria();
        crit.setExperiment(new CaArrayEntityReference("URN:LSID:caarray.nci.nih.gov:gov.nih.nci.caarray.external.v1_0.experiment.Experiment:1"));
        List<Hybridization> hybs = caArrayServer.getSearchService().searchForHybridizations(crit, null);
        assertEquals(6, hybs.size());
    }
}
