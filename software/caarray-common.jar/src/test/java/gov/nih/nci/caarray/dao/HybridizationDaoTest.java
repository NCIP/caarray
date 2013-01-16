//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.dao;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.hibernate.Transaction;
import org.junit.Before;
import org.junit.Test;

import com.fiveamsolutions.nci.commons.data.search.PageSortParams;

import edu.georgetown.pir.Organism;
import gov.nih.nci.caarray.domain.contact.Organization;
import gov.nih.nci.caarray.domain.hybridization.Hybridization;
import gov.nih.nci.caarray.domain.project.Experiment;
import gov.nih.nci.caarray.domain.project.Project;
import gov.nih.nci.caarray.domain.sample.Extract;
import gov.nih.nci.caarray.domain.sample.LabeledExtract;
import gov.nih.nci.caarray.domain.sample.Sample;
import gov.nih.nci.caarray.domain.sample.Source;
import gov.nih.nci.caarray.domain.search.AdHocSortCriterion;
import gov.nih.nci.caarray.domain.search.HybridizationSearchCriteria;
import gov.nih.nci.caarray.domain.vocabulary.Category;
import gov.nih.nci.caarray.domain.vocabulary.Term;
import gov.nih.nci.caarray.domain.vocabulary.TermSource;

/**
 * @author Scott Miller
 * 
 */
public class HybridizationDaoTest extends AbstractDaoTest {
    // Experiment
    private static Organism DUMMY_ORGANISM = new Organism();
    private static Organization DUMMY_PROVIDER = new Organization();
    private static Project DUMMY_PROJECT_1 = new Project();
    private static Experiment DUMMY_EXPERIMENT_1 = new Experiment();
    private static TermSource DUMMY_TERM_SOURCE = new TermSource();
    private static Category DUMMY_CATEGORY = new Category();

    // Annotations
    private static Term DUMMY_REPLICATE_TYPE = new Term();
    private static Term DUMMY_NORMALIZATION_TYPE = new Term();
    private static Term DUMMY_QUALITY_CTRL_TYPE = new Term();

    private HybridizationDao daoObject;
    private VocabularyDao vocabularyDao;

    /**
     * Define the dummy objects that will be used by the tests.
     */
    @Before
    public void setup() {
        this.daoObject = new HybridizationDaoImpl(this.hibernateHelper);
        this.vocabularyDao = new VocabularyDaoImpl(this.hibernateHelper);

        // Experiment
        DUMMY_ORGANISM = new Organism();
        DUMMY_PROVIDER = new Organization();
        DUMMY_PROJECT_1 = new Project();
        DUMMY_EXPERIMENT_1 = new Experiment();
        DUMMY_TERM_SOURCE = new TermSource();
        DUMMY_CATEGORY = new Category();

        // Annotations
        DUMMY_REPLICATE_TYPE = new Term();
        DUMMY_NORMALIZATION_TYPE = new Term();
        DUMMY_QUALITY_CTRL_TYPE = new Term();

        // Initialize all the dummy objects needed for the tests.
        initializeProjects();
    }

    /**
     * Initialize the dummy <code>Project</code> objects.
     */
    private static void initializeProjects() {
        setExperimentSummary();
        DUMMY_TERM_SOURCE.setName("Dummy MGED Ontology");
        DUMMY_TERM_SOURCE.setUrl("test url");
        DUMMY_CATEGORY.setName("Dummy Category");
        DUMMY_CATEGORY.setSource(DUMMY_TERM_SOURCE);
        DUMMY_ORGANISM.setScientificName("Foo");
        DUMMY_ORGANISM.setTermSource(DUMMY_TERM_SOURCE);
        setExperimentAnnotations();
        DUMMY_PROJECT_1.setExperiment(DUMMY_EXPERIMENT_1);
        DUMMY_EXPERIMENT_1.setOrganism(DUMMY_ORGANISM);
        DUMMY_EXPERIMENT_1.setManufacturer(DUMMY_PROVIDER);
    }

    private static void setExperimentSummary() {
        DUMMY_EXPERIMENT_1.setTitle("DummyExperiment1");
        DUMMY_EXPERIMENT_1.setDescription("DummyExperiment1Desc");
        final Date currDate = new Date();
        DUMMY_EXPERIMENT_1.setDate(currDate);
        DUMMY_EXPERIMENT_1.setPublicReleaseDate(currDate);
        DUMMY_EXPERIMENT_1.setDesignDescription("Working on it");
    }

    private static void setExperimentAnnotations() {
        DUMMY_REPLICATE_TYPE.setValue("Dummy Replicate Type");
        DUMMY_REPLICATE_TYPE.setSource(DUMMY_TERM_SOURCE);
        DUMMY_REPLICATE_TYPE.setCategory(DUMMY_CATEGORY);
        DUMMY_NORMALIZATION_TYPE.setValue("Dummy Normalization Type");
        DUMMY_NORMALIZATION_TYPE.setSource(DUMMY_TERM_SOURCE);
        DUMMY_NORMALIZATION_TYPE.setCategory(DUMMY_CATEGORY);
        DUMMY_QUALITY_CTRL_TYPE.setValue("Dummy Quality Control Type");
        DUMMY_QUALITY_CTRL_TYPE.setSource(DUMMY_TERM_SOURCE);
        DUMMY_QUALITY_CTRL_TYPE.setCategory(DUMMY_CATEGORY);
        DUMMY_EXPERIMENT_1.getReplicateTypes().add(DUMMY_REPLICATE_TYPE);
        DUMMY_EXPERIMENT_1.getNormalizationTypes().add(DUMMY_NORMALIZATION_TYPE);
        DUMMY_EXPERIMENT_1.getQualityControlTypes().add(DUMMY_QUALITY_CTRL_TYPE);
    }

    private void saveSupportingObjects() {
        DUMMY_REPLICATE_TYPE.setValue("Dummy Replicate Type");
        DUMMY_REPLICATE_TYPE.setSource(DUMMY_TERM_SOURCE);
        DUMMY_REPLICATE_TYPE.setCategory(DUMMY_CATEGORY);
        DUMMY_NORMALIZATION_TYPE.setValue("Dummy Normalization Type");
        DUMMY_NORMALIZATION_TYPE.setSource(DUMMY_TERM_SOURCE);
        DUMMY_NORMALIZATION_TYPE.setCategory(DUMMY_CATEGORY);
        DUMMY_QUALITY_CTRL_TYPE.setValue("Dummy Quality Control Type");
        DUMMY_QUALITY_CTRL_TYPE.setSource(DUMMY_TERM_SOURCE);
        DUMMY_QUALITY_CTRL_TYPE.setCategory(DUMMY_CATEGORY);
        this.vocabularyDao.save(DUMMY_REPLICATE_TYPE);
        this.vocabularyDao.save(DUMMY_QUALITY_CTRL_TYPE);
        this.vocabularyDao.save(DUMMY_NORMALIZATION_TYPE);
        this.daoObject.save(DUMMY_PROJECT_1);
    }

    @Test
    public void testSearchByCriteria() throws Exception {
        Transaction tx = this.hibernateHelper.beginTransaction();

        saveSupportingObjects();

        final Hybridization h1 = new Hybridization();
        h1.setName("h1");
        DUMMY_EXPERIMENT_1.getHybridizations().add(h1);
        h1.setExperiment(DUMMY_EXPERIMENT_1);

        final Hybridization h2 = new Hybridization();
        h2.setName("h2");
        DUMMY_EXPERIMENT_1.getHybridizations().add(h2);
        h2.setExperiment(DUMMY_EXPERIMENT_1);

        final Source so1 = new Source();
        so1.setName("source");
        DUMMY_EXPERIMENT_1.getSources().add(so1);
        so1.setExperiment(DUMMY_EXPERIMENT_1);
        final Sample sa1 = new Sample();
        sa1.setName("sample");
        so1.getSamples().add(sa1);
        sa1.getSources().add(so1);
        DUMMY_EXPERIMENT_1.getSamples().add(sa1);
        sa1.setExperiment(DUMMY_EXPERIMENT_1);
        final Extract ex1 = new Extract();
        ex1.setName("extract1");
        sa1.getExtracts().add(ex1);
        ex1.getSamples().add(sa1);
        DUMMY_EXPERIMENT_1.getExtracts().add(ex1);
        ex1.setExperiment(DUMMY_EXPERIMENT_1);
        final Extract ex2 = new Extract();
        ex2.setName("extract2");
        sa1.getExtracts().add(ex2);
        ex2.getSamples().add(sa1);
        DUMMY_EXPERIMENT_1.getExtracts().add(ex2);
        ex2.setExperiment(DUMMY_EXPERIMENT_1);
        final LabeledExtract le1 = new LabeledExtract();
        le1.setName("LE1");
        ex1.getLabeledExtracts().add(le1);
        le1.getExtracts().add(ex1);
        ex2.getLabeledExtracts().add(le1);
        le1.getExtracts().add(ex2);
        le1.getHybridizations().add(h1);
        h1.getLabeledExtracts().add(le1);
        DUMMY_EXPERIMENT_1.getLabeledExtracts().add(le1);
        le1.setExperiment(DUMMY_EXPERIMENT_1);
        final LabeledExtract le2 = new LabeledExtract();
        le2.setName("LE2");
        ex2.getLabeledExtracts().add(le2);
        le2.getExtracts().add(ex2);
        le2.getHybridizations().add(h2);
        h2.getLabeledExtracts().add(le2);
        DUMMY_EXPERIMENT_1.getLabeledExtracts().add(le2);
        le2.setExperiment(DUMMY_EXPERIMENT_1);
        final LabeledExtract le3 = new LabeledExtract();
        le3.setName("LE3");
        le3.getHybridizations().add(h1);
        h1.getLabeledExtracts().add(le3);
        le3.getHybridizations().add(h2);
        h2.getLabeledExtracts().add(le3);
        DUMMY_EXPERIMENT_1.getLabeledExtracts().add(le3);
        le3.setExperiment(DUMMY_EXPERIMENT_1);

        final LabeledExtract le4 = new LabeledExtract();
        le4.setName("LE4");
        DUMMY_EXPERIMENT_1.getLabeledExtracts().add(le4);
        le4.setExperiment(DUMMY_EXPERIMENT_1);

        tx.commit();

        tx = this.hibernateHelper.beginTransaction();
        final Experiment e =
                CaArrayDaoFactory.INSTANCE.getSearchDao().retrieve(Experiment.class, DUMMY_EXPERIMENT_1.getId());

        final PageSortParams<Hybridization> params =
                new PageSortParams<Hybridization>(5, 0, new AdHocSortCriterion<Hybridization>("name"), false);
        final HybridizationSearchCriteria criteria = new HybridizationSearchCriteria();
        criteria.setExperiment(e);

        List<Hybridization> hybs = this.daoObject.searchByCriteria(params, criteria);
        assertEquals(2, hybs.size());
        assertEquals(h1.getName(), hybs.get(0).getName());
        assertEquals(h2.getName(), hybs.get(1).getName());

        criteria.getNames().addAll(Arrays.asList("h1"));
        hybs = this.daoObject.searchByCriteria(params, criteria);
        assertEquals(1, hybs.size());
        assertEquals(h1.getName(), hybs.get(0).getName());

        criteria.setExperiment(null);
        hybs = this.daoObject.searchByCriteria(params, criteria);
        assertEquals(1, hybs.size());
        assertEquals(h1.getName(), hybs.get(0).getName());

        criteria.getBiomaterials().add(le3);
        hybs = this.daoObject.searchByCriteria(params, criteria);
        assertEquals(1, hybs.size());
        assertEquals(h1.getName(), hybs.get(0).getName());

        criteria.getNames().clear();
        hybs = this.daoObject.searchByCriteria(params, criteria);
        assertEquals(2, hybs.size());
        assertEquals(h1.getName(), hybs.get(0).getName());
        assertEquals(h2.getName(), hybs.get(1).getName());

        criteria.getBiomaterials().clear();
        criteria.getBiomaterials().add(le4);
        hybs = this.daoObject.searchByCriteria(params, criteria);
        assertEquals(0, hybs.size());

        tx.commit();
    }
}
