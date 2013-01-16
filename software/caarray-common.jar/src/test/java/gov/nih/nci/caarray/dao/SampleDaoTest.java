//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import gov.nih.nci.caarray.domain.project.ExperimentOntologyCategory;
import gov.nih.nci.caarray.domain.sample.AbstractBioMaterial;
import gov.nih.nci.caarray.domain.sample.AbstractCharacteristic;
import gov.nih.nci.caarray.domain.sample.Extract;
import gov.nih.nci.caarray.domain.sample.Sample;
import gov.nih.nci.caarray.domain.sample.Source;
import gov.nih.nci.caarray.domain.sample.TermBasedCharacteristic;
import gov.nih.nci.caarray.domain.sample.UserDefinedCharacteristic;
import gov.nih.nci.caarray.domain.search.AdHocSortCriterion;
import gov.nih.nci.caarray.domain.search.AnnotationCriterion;
import gov.nih.nci.caarray.domain.search.BiomaterialSearchCriteria;
import gov.nih.nci.caarray.domain.search.ExternalBiomaterialSearchCategory;
import gov.nih.nci.caarray.domain.search.SampleJoinableSortCriterion;
import gov.nih.nci.caarray.domain.search.SearchSampleCategory;
import gov.nih.nci.caarray.domain.search.SearchSourceCategory;
import gov.nih.nci.caarray.domain.search.SourceJoinableSortCriterion;
import gov.nih.nci.caarray.domain.vocabulary.Category;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.hibernate.Transaction;
import org.junit.Before;
import org.junit.Test;

import com.fiveamsolutions.nci.commons.data.search.PageSortParams;

/**
 * Unit tests for the Sample DAO.
 * 
 * @author Rashmi Srinivasa
 */
@SuppressWarnings("PMD")
public class SampleDaoTest extends AbstractProjectDaoTest {
    private static TermBasedCharacteristic DUMMY_CHARACTERISTIC = new TermBasedCharacteristic();

    private static final PageSortParams<Sample> ALL_BY_NAME = new PageSortParams<Sample>(10000, 0,
            SampleJoinableSortCriterion.NAME, false);
    private static final PageSortParams<Source> ALL_SOURCE_BY_NAME = new PageSortParams<Source>(10000, 0,
            SourceJoinableSortCriterion.NAME, false);

    private SampleDao daoObject;

    @Before
    public void setUp() {
        this.daoObject = new SampleDaoImpl(this.hibernateHelper);
    }

    /**
     * Tests retrieving the <code>Sample</code> with the given id. Test encompasses save and delete of a
     * <code>Sample</code>.
     */
    @Test
    public void testGetSample() {
        Transaction tx = null;

        try {
            tx = this.hibernateHelper.beginTransaction();

            DUMMY_CHARACTERISTIC = new TermBasedCharacteristic();
            DUMMY_CHARACTERISTIC.setCategory(DUMMY_CATEGORY);
            DUMMY_CHARACTERISTIC.setTerm(DUMMY_REPLICATE_TYPE);
            DUMMY_CHARACTERISTIC.setBioMaterial(DUMMY_SAMPLE);

            DUMMY_SAMPLE.getCharacteristics().add(DUMMY_CHARACTERISTIC);

            saveSupportingObjects();

            this.daoObject.save(DUMMY_PROJECT_1);
            tx.commit();

            tx = this.hibernateHelper.beginTransaction();
            final Sample retrievedSample = this.searchDao.retrieve(Sample.class, DUMMY_SAMPLE.getId());

            if (!DUMMY_SAMPLE.equals(retrievedSample) || !compareSamples(retrievedSample, DUMMY_SAMPLE)) {
                fail("Retrieved sample is different from saved sample.");
            }
            tx.commit();
        } catch (final DAOException e) {
            this.hibernateHelper.rollbackTransaction(tx);
            fail("DAO exception during save and retrieve of sample: " + e.getMessage());
        }
    }

    /**
     * Tests retrieving the <code>Sample</code> with the given name. Test encompasses save and delete of a
     * <code>Sample</code>.
     */
    @Test
    public void testGetSampleByName() {
        Transaction tx = null;

        try {
            tx = this.hibernateHelper.beginTransaction();
            saveSupportingObjects();
            this.daoObject.save(DUMMY_PROJECT_1);
            tx.commit();

            tx = this.hibernateHelper.beginTransaction();

            final SearchSampleCategory[] categories = {SearchSampleCategory.SAMPLE_NAME };

            final int sampleCount = this.daoObject.searchCount(DUMMY_SAMPLE.getName(), Sample.class, categories);

            final List<Sample> retrievedSamples =
                    this.daoObject.searchByCategory(ALL_BY_NAME, DUMMY_SAMPLE.getName(), Sample.class, categories);

            tx.commit();

            assertEquals(sampleCount, retrievedSamples.size());
            assertEquals(1, sampleCount);
            assertEquals(DUMMY_SAMPLE, retrievedSamples.get(0));

        } catch (final DAOException e) {
            this.hibernateHelper.rollbackTransaction(tx);
            throw e;
        }
    }

    /**
     * Tests retrieving the <code>Source</code> with the given category. Test encompasses save and delete
     * <code>Source</code>.
     */
    @Test
    public void testGetSourceByCategory() {
        Transaction tx = null;

        try {
            tx = this.hibernateHelper.beginTransaction();
            saveSupportingObjects();
            this.daoObject.save(DUMMY_PROJECT_1);
            tx.commit();

            tx = this.hibernateHelper.beginTransaction();
            final SearchSourceCategory[] categories = SearchSourceCategory.values();
            final int sourceCount = this.daoObject.searchCount("", Source.class, categories);

            final List<Source> retrievedSources =
                    this.daoObject.searchByCategory(ALL_SOURCE_BY_NAME, "", Source.class, categories);

            tx.commit();

            assertEquals(sourceCount, retrievedSources.size());
            assertEquals(1, sourceCount);

        } catch (final DAOException e) {
            this.hibernateHelper.rollbackTransaction(tx);
            throw e;
        }
    }

    /**
     * Tests retrieving the <code>Sample</code> with the given category. Test encompasses save and delete.
     * <code>Source</code>.
     */
    @Test
    public void testGetSampleByCategoryNoKeyword() {
        Transaction tx = null;

        try {
            tx = this.hibernateHelper.beginTransaction();
            saveSupportingObjects();
            this.daoObject.save(DUMMY_PROJECT_1);
            tx.commit();

            tx = this.hibernateHelper.beginTransaction();
            final SearchSampleCategory[] categories = SearchSampleCategory.values();
            final int sampleCount = this.daoObject.searchCount("", Sample.class, categories);

            final List<Sample> retrievedSamples =
                    this.daoObject.searchByCategory(ALL_BY_NAME, "", Sample.class, categories);

            tx.commit();

            assertEquals(sampleCount, retrievedSamples.size());
            assertEquals(1, sampleCount);

        } catch (final DAOException e) {
            this.hibernateHelper.rollbackTransaction(tx);
            throw e;
        }
    }

    /**
     * Tests retrieving the <code>Sample</code> with the given category. Test encompasses save and delete.
     * <code>Source</code>.
     */
    @Test
    public void testGetSampleByCategory() {
        Transaction tx = null;

        try {
            tx = this.hibernateHelper.beginTransaction();
            saveSupportingObjects();
            this.daoObject.save(DUMMY_PROJECT_1);
            tx.commit();

            tx = this.hibernateHelper.beginTransaction();
            final SearchSampleCategory[] categories = SearchSampleCategory.values();
            final int sampleCount = this.daoObject.searchCount("", Sample.class, categories);

            final List<Sample> retrievedSamples =
                    this.daoObject.searchByCategory(ALL_BY_NAME, DUMMY_SAMPLE.getName(), Sample.class, categories);

            tx.commit();

            assertEquals(sampleCount, retrievedSamples.size());
            assertEquals(1, sampleCount);

        } catch (final DAOException e) {
            this.hibernateHelper.rollbackTransaction(tx);
            throw e;
        }
    }

    /**
     * Tests retrieving multiple biomaterials by keyword. Test encompasses save and delete. <code>Source</code>.
     */
    @Test
    public void testGetBiomaterialsByCategory() {
        Transaction tx = null;

        try {
            tx = this.hibernateHelper.beginTransaction();
            saveSupportingObjects();
            this.daoObject.save(DUMMY_PROJECT_1);
            tx.commit();

            tx = this.hibernateHelper.beginTransaction();

            final Set<Class<? extends AbstractBioMaterial>> classes =
                    new HashSet<Class<? extends AbstractBioMaterial>>();
            classes.add(Source.class);
            classes.add(Sample.class);
            classes.add(Extract.class);

            final List<AbstractBioMaterial> retrievedBms =
                    this.daoObject.searchByCategory(new PageSortParams<AbstractBioMaterial>(10000, 0,
                            new AdHocSortCriterion<AbstractBioMaterial>("name"), false), "Dummy", classes,
                            ExternalBiomaterialSearchCategory.NAME);

            tx.commit();

            assertEquals(3, retrievedBms.size());
            assertEquals(Extract.class, retrievedBms.get(0).getClass());
            assertEquals(DUMMY_EXTRACT.getName(), retrievedBms.get(0).getName());
            assertEquals(Sample.class, retrievedBms.get(1).getClass());
            assertEquals(DUMMY_SAMPLE.getName(), retrievedBms.get(1).getName());
            assertEquals(Source.class, retrievedBms.get(2).getClass());
            assertEquals(DUMMY_SOURCE.getName(), retrievedBms.get(2).getName());

        } catch (final DAOException e) {
            this.hibernateHelper.rollbackTransaction(tx);
            throw e;
        }
    }

    @Test
    public void testGetSourceByCharacteristicCategory() {
        Transaction tx = null;

        try {
            tx = this.hibernateHelper.beginTransaction();
            DUMMY_CHARACTERISTIC = new TermBasedCharacteristic();
            DUMMY_CHARACTERISTIC.setCategory(DUMMY_CATEGORY);
            DUMMY_CHARACTERISTIC.setTerm(DUMMY_REPLICATE_TYPE);
            DUMMY_CHARACTERISTIC.setBioMaterial(DUMMY_SOURCE);

            DUMMY_SOURCE.getCharacteristics().add(DUMMY_CHARACTERISTIC);

            saveSupportingObjects();
            this.daoObject.save(DUMMY_PROJECT_1);
            tx.commit();

            tx = this.hibernateHelper.beginTransaction();

            final List<Category> all_chars =
                    this.vocabularyDao.searchForCharacteristicCategory(null, TermBasedCharacteristic.class,
                            DUMMY_CATEGORY.getName());
            final int sourceCount =
                    this.daoObject.countSourcesByCharacteristicCategory(all_chars.get(0),
                            DUMMY_REPLICATE_TYPE.getValue());
            final PageSortParams<Source> sortByOrganism =
                    new PageSortParams<Source>(10000, 0, SourceJoinableSortCriterion.NAME, false);
            sortByOrganism.setSortCriterion(SourceJoinableSortCriterion.ORGANISM);
            final List<Source> retrievedSamples =
                    this.daoObject.searchSourcesByCharacteristicCategory(sortByOrganism, DUMMY_CATEGORY,
                            DUMMY_REPLICATE_TYPE.getValue());

            assertEquals(sourceCount, retrievedSamples.size());

            tx.commit();

            assertEquals(DUMMY_SOURCE, retrievedSamples.get(0));

        } catch (final DAOException e) {
            this.hibernateHelper.rollbackTransaction(tx);
            throw e;
        }
    }

    @Test
    public void testGetSourceByCharacteristicCategoryAll() {
        Transaction tx = null;

        try {
            tx = this.hibernateHelper.beginTransaction();
            DUMMY_CHARACTERISTIC = new TermBasedCharacteristic();
            DUMMY_CHARACTERISTIC.setCategory(DUMMY_CATEGORY);
            DUMMY_CHARACTERISTIC.setTerm(DUMMY_REPLICATE_TYPE);
            DUMMY_CHARACTERISTIC.setBioMaterial(DUMMY_SOURCE);

            DUMMY_SOURCE.getCharacteristics().add(DUMMY_CHARACTERISTIC);

            saveSupportingObjects();
            this.daoObject.save(DUMMY_PROJECT_1);
            tx.commit();

            tx = this.hibernateHelper.beginTransaction();

            final List<Category> all_chars =
                    this.vocabularyDao.searchForCharacteristicCategory(null, TermBasedCharacteristic.class,
                            DUMMY_CATEGORY.getName());
            final int sourceCount =
                    this.daoObject.countSourcesByCharacteristicCategory(all_chars.get(0),
                            DUMMY_REPLICATE_TYPE.getValue());
            final PageSortParams<Source> sortByOrganism =
                    new PageSortParams<Source>(10000, 0, SourceJoinableSortCriterion.NAME, false);
            sortByOrganism.setSortCriterion(SourceJoinableSortCriterion.ORGANISM);
            final List<Source> retrievedSamples =
                    this.daoObject.searchSourcesByCharacteristicCategory(sortByOrganism, DUMMY_CATEGORY, "");

            assertEquals(sourceCount, retrievedSamples.size());

            tx.commit();

            assertEquals(DUMMY_SOURCE, retrievedSamples.get(0));

        } catch (final DAOException e) {
            this.hibernateHelper.rollbackTransaction(tx);
            throw e;
        }
    }

    /**
     * Tests retrieving the <code>Sample</code> with the given char category and no keyword. Test encompasses save and
     * delete of a <code>Sample</code>.
     */
    @Test
    public void testGetSampleByCharacteristicCategoryAll() {
        Transaction tx = null;

        try {

            tx = this.hibernateHelper.beginTransaction();
            DUMMY_CHARACTERISTIC = new TermBasedCharacteristic();
            DUMMY_CHARACTERISTIC.setCategory(DUMMY_CATEGORY);
            DUMMY_CHARACTERISTIC.setTerm(DUMMY_REPLICATE_TYPE);
            DUMMY_CHARACTERISTIC.setBioMaterial(DUMMY_SAMPLE);

            DUMMY_SAMPLE.getCharacteristics().add(DUMMY_CHARACTERISTIC);

            saveSupportingObjects();
            this.daoObject.save(DUMMY_PROJECT_1);
            tx.commit();

            tx = this.hibernateHelper.beginTransaction();

            final List<Category> all_chars =
                    this.vocabularyDao.searchForCharacteristicCategory(null, TermBasedCharacteristic.class,
                            DUMMY_CATEGORY.getName());
            final int sampleCount = this.daoObject.countSamplesByCharacteristicCategory(all_chars.get(0), "");
            final List<Sample> retrievedSamples =
                    this.daoObject.searchSamplesByCharacteristicCategory(ALL_BY_NAME, DUMMY_CATEGORY, "");

            assertEquals(sampleCount, retrievedSamples.size());

            tx.commit();

            assertEquals(DUMMY_SAMPLE, retrievedSamples.get(0));

        } catch (final DAOException e) {
            this.hibernateHelper.rollbackTransaction(tx);
            throw e;
        }
    }

    /**
     * Tests retrieving the <code>Sample</code> with the given char category and keyword. Test encompasses save and
     * delete of a <code>Sample</code>.
     */
    @Test
    public void testGetSampleByCharacteristicCategory() {
        Transaction tx = null;

        try {
            tx = this.hibernateHelper.beginTransaction();
            DUMMY_CHARACTERISTIC = new TermBasedCharacteristic();
            DUMMY_CHARACTERISTIC.setCategory(DUMMY_CATEGORY);
            DUMMY_CHARACTERISTIC.setTerm(DUMMY_REPLICATE_TYPE);
            DUMMY_CHARACTERISTIC.setBioMaterial(DUMMY_SAMPLE);

            DUMMY_SAMPLE.getCharacteristics().add(DUMMY_CHARACTERISTIC);

            saveSupportingObjects();
            this.daoObject.save(DUMMY_PROJECT_1);
            tx.commit();

            tx = this.hibernateHelper.beginTransaction();

            final List<Category> all_chars =
                    this.vocabularyDao.searchForCharacteristicCategory(null, TermBasedCharacteristic.class,
                            DUMMY_CATEGORY.getName());
            final int sampleCount =
                    this.daoObject.countSamplesByCharacteristicCategory(all_chars.get(0),
                            DUMMY_REPLICATE_TYPE.getValue());
            final List<Sample> retrievedSamples =
                    this.daoObject.searchSamplesByCharacteristicCategory(ALL_BY_NAME, DUMMY_CATEGORY,
                            DUMMY_REPLICATE_TYPE.getValue());

            assertEquals(sampleCount, retrievedSamples.size());

            tx.commit();

            assertEquals(DUMMY_SAMPLE, retrievedSamples.get(0));

        } catch (final DAOException e) {
            this.hibernateHelper.rollbackTransaction(tx);
            throw e;
        }
    }

    /**
     * Tests retrieving the <code>Sample</code> with the given char category and keyword. Test encompasses save and
     * delete of a <code>Sample</code>.
     */
    @Test
    public void testSearchSamplesByExperimentAndArbitraryCharacteristicValue() {
        Transaction tx = null;

        try {
            tx = this.hibernateHelper.beginTransaction();
            DUMMY_SAMPLE.getCharacteristics().clear();
            TermBasedCharacteristic characteristic1 = new TermBasedCharacteristic();
            characteristic1.setCategory(DUMMY_CATEGORY);
            characteristic1.setTerm(DUMMY_REPLICATE_TYPE);
            characteristic1.setBioMaterial(DUMMY_SAMPLE);
            DUMMY_SAMPLE.getCharacteristics().add(characteristic1);

            UserDefinedCharacteristic characteristic2 = new UserDefinedCharacteristic();
            characteristic2.setCategory(DUMMY_CATEGORY);
            characteristic2.setValue(DUMMY_NORMALIZATION_TYPE.getValue());
            characteristic2.setBioMaterial(DUMMY_SAMPLE);
            DUMMY_SAMPLE.getCharacteristics().add(characteristic2);
            assertEquals(2, DUMMY_SAMPLE.getCharacteristics().size());
            
            saveSupportingObjects();
            this.daoObject.save(DUMMY_PROJECT_1);
            tx.commit();

            tx = this.hibernateHelper.beginTransaction();
            String queryParam = characteristic1.getTerm().getValue();
            List<Sample> retrievedSamples = 
                    this.daoObject.searchSamplesByExperimentAndArbitraryCharacteristicValue( 
                            queryParam, DUMMY_EXPERIMENT_1, DUMMY_CATEGORY );
            assertEquals(1, retrievedSamples.size());
            assertEquals(DUMMY_SAMPLE.getId(), retrievedSamples.get(0).getId());

            queryParam = characteristic2.getValue();
            retrievedSamples = 
                    this.daoObject.searchSamplesByExperimentAndArbitraryCharacteristicValue( 
                            queryParam, DUMMY_EXPERIMENT_1, DUMMY_CATEGORY );
            assertEquals(1, retrievedSamples.size());
            assertEquals(DUMMY_SAMPLE.getId(), retrievedSamples.get(0).getId());
            tx.commit();
        } catch (final DAOException e) {
            this.hibernateHelper.rollbackTransaction(tx);
            throw e;
        }
    }

    @Test
    public void testSearchByCriteria() {
        Transaction tx = null;

        try {
            tx = this.hibernateHelper.beginTransaction();
            DUMMY_CHARACTERISTIC = new TermBasedCharacteristic();
            DUMMY_CHARACTERISTIC.setCategory(DUMMY_CATEGORY);
            DUMMY_CHARACTERISTIC.setTerm(DUMMY_REPLICATE_TYPE);
            DUMMY_CHARACTERISTIC.setBioMaterial(DUMMY_SAMPLE);

            DUMMY_SAMPLE.getCharacteristics().add(DUMMY_CHARACTERISTIC);
            DUMMY_SAMPLE.setExternalId("123");
            DUMMY_SAMPLE.setDiseaseState(DUMMY_NORMALIZATION_TYPE);
            DUMMY_SAMPLE.setCellType(DUMMY_FACTOR_TYPE_1);

            saveSupportingObjects();
            this.daoObject.save(DUMMY_PROJECT_1);
            this.daoObject.save(DUMMY_PROJECT_2);
            this.daoObject.save(DUMMY_PROJECT_3);
            tx.commit();

            tx = this.hibernateHelper.beginTransaction();

            final PageSortParams<Source> sourceParams =
                    new PageSortParams<Source>(20, 0, new AdHocSortCriterion<Source>("name"), false);
            final PageSortParams<Sample> sampleParams =
                    new PageSortParams<Sample>(20, 0, new AdHocSortCriterion<Sample>("name"), false);
            final PageSortParams<Extract> extractParams =
                    new PageSortParams<Extract>(20, 0, new AdHocSortCriterion<Extract>("name"), false);

            BiomaterialSearchCriteria bsc = new BiomaterialSearchCriteria();
            bsc.setExperiment(DUMMY_EXPERIMENT_1);
            bsc.getBiomaterialClasses().add(Source.class);
            final List<Source> srcResults = this.daoObject.searchByCriteria(sourceParams, bsc);
            assertEquals(1, srcResults.size());
            assertEquals(DUMMY_SOURCE.getName(), srcResults.get(0).getName());

            bsc.setExperiment(null);
            bsc.getNames().add(DUMMY_EXTRACT.getName());
            bsc.getBiomaterialClasses().clear();
            bsc.getBiomaterialClasses().add(Extract.class);
            List<Extract> extResults = this.daoObject.searchByCriteria(extractParams, bsc);
            assertEquals(1, extResults.size());
            assertEquals(DUMMY_EXTRACT.getName(), extResults.get(0).getName());

            bsc.getExternalIds().add("123");
            extResults = this.daoObject.searchByCriteria(extractParams, bsc);
            assertEquals(0, extResults.size());

            bsc.getNames().clear();
            bsc.getExternalIds().add(DUMMY_SAMPLE.getExternalId());
            bsc.getBiomaterialClasses().clear();
            bsc.getBiomaterialClasses().add(Sample.class);
            List<Sample> results = this.daoObject.searchByCriteria(sampleParams, bsc);
            assertEquals(1, results.size());
            assertTrue(compareSamples(results.get(0), DUMMY_SAMPLE));

            bsc.setExperiment(DUMMY_EXPERIMENT_2);
            results = this.daoObject.searchByCriteria(sampleParams, bsc);
            assertEquals(0, results.size());

            bsc = new BiomaterialSearchCriteria();
            final Category ds = new Category();
            ds.setName(ExperimentOntologyCategory.DISEASE_STATE.getCategoryName());
            bsc.getAnnotationCriterions().add(new AnnotationCriterion(ds, DUMMY_NORMALIZATION_TYPE.getValue()));
            bsc.getBiomaterialClasses().add(Sample.class);
            results = this.daoObject.searchByCriteria(sampleParams, bsc);
            assertEquals(1, results.size());
            assertTrue(compareSamples(results.get(0), DUMMY_SAMPLE));

            bsc.getAnnotationCriterions().add(new AnnotationCriterion(ds, DUMMY_FACTOR_TYPE_2.getValue()));
            results = this.daoObject.searchByCriteria(sampleParams, bsc);
            assertEquals(1, results.size());
            assertTrue(compareSamples(results.get(0), DUMMY_SAMPLE));

            final Category ct = new Category();
            ct.setName(ExperimentOntologyCategory.CELL_TYPE.getCategoryName());
            final AnnotationCriterion ac2 = new AnnotationCriterion(ct, DUMMY_FACTOR_TYPE_1.getValue());
            bsc.getAnnotationCriterions().add(ac2);
            results = this.daoObject.searchByCriteria(sampleParams, bsc);
            assertEquals(1, results.size());
            assertTrue(compareSamples(results.get(0), DUMMY_SAMPLE));

            ac2.setValue("Fdsfdsfds");
            results = this.daoObject.searchByCriteria(sampleParams, bsc);
            assertEquals(0, results.size());

            tx.commit();
        } catch (final DAOException e) {
            this.hibernateHelper.rollbackTransaction(tx);
            throw e;
        }
    }

    /**
     * Compare 2 samples to check if they are the same.
     * 
     * @return true if the 2 samples are the same and false otherwise.
     */
    private boolean compareSamples(Sample retrievedSample, Sample dummySample) {
        if (!dummySample.getName().equals(retrievedSample.getName())) {
            return false;
        }

        final Collection<AbstractCharacteristic> characteristics = retrievedSample.getCharacteristics();
        if (characteristics.isEmpty() || characteristics.size() != 1) {
            return false;
        }

        final Iterator<AbstractCharacteristic> i = characteristics.iterator();
        final TermBasedCharacteristic retrievedCharacteristic = (TermBasedCharacteristic) i.next();
        if (!DUMMY_CHARACTERISTIC.getTerm().equals(retrievedCharacteristic.getTerm())) {
            return false;
        }

        return true;
    }
}
