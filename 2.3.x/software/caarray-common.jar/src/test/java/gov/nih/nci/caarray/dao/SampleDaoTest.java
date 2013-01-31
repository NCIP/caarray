//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import gov.nih.nci.caarray.domain.sample.AbstractCharacteristic;
import gov.nih.nci.caarray.domain.sample.Sample;
import gov.nih.nci.caarray.domain.sample.Source;
import gov.nih.nci.caarray.domain.sample.TermBasedCharacteristic;
import gov.nih.nci.caarray.domain.search.SampleSortCriterion;
import gov.nih.nci.caarray.domain.search.SearchSampleCategory;
import gov.nih.nci.caarray.domain.search.SearchSourceCategory;
import gov.nih.nci.caarray.domain.search.SourceSortCriterion;
import gov.nih.nci.caarray.domain.vocabulary.Category;
import gov.nih.nci.caarray.domain.vocabulary.Term;
import gov.nih.nci.caarray.domain.vocabulary.TermSource;
import gov.nih.nci.caarray.util.HibernateUtil;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

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
public class SampleDaoTest  extends AbstractDaoTest {
    private static Sample DUMMY_SAMPLE_1 = new Sample();
    private static TermSource DUMMY_SOURCE = new TermSource();
    private static Source SEARCH_SOURCE = new Source();
    private static Category DUMMY_CATEGORY = new Category();
    private static Term DUMMY_MATERIAL_TYPE = new Term();
    private static TermBasedCharacteristic DUMMY_CHARACTERISTIC = new TermBasedCharacteristic();

    private static final SearchDao DAO_OBJECT = CaArrayDaoFactory.INSTANCE.getSearchDao();
    private static final SampleDao DAO_SAMPLE_OBJECT = CaArrayDaoFactory.INSTANCE.getSampleDao();
    private static final VocabularyDao DAO_VOCAB_OBJECT = CaArrayDaoFactory.INSTANCE.getVocabularyDao();
    private static final PageSortParams<Sample> ALL_BY_NAME =
        new PageSortParams<Sample>(10000, 0, SampleSortCriterion.NAME, false);
    private static final PageSortParams<Source> ALL_SOURCE_BY_NAME =
        new PageSortParams<Source>(10000, 0, SourceSortCriterion.NAME, false);
    /**
     * Define the dummy objects that will be used by the tests.
     */
    @Before
    public void setUpBeforeClass() {
        DUMMY_SAMPLE_1 = new Sample();
        DUMMY_SAMPLE_1.setName("DummySample1");
        DUMMY_SAMPLE_1.setDescription("DummySample1Desc");

        DUMMY_SOURCE = new TermSource();
        DUMMY_SOURCE.setName("Dummy Source");
        DUMMY_SOURCE.setUrl("Dummy URL");

        DUMMY_CATEGORY = new Category();
        DUMMY_CATEGORY.setName("Dummy Category");
        DUMMY_CATEGORY.setSource(DUMMY_SOURCE);

        DUMMY_MATERIAL_TYPE = new Term();
        DUMMY_MATERIAL_TYPE.setValue("Dummy Material Type");
        DUMMY_MATERIAL_TYPE.setSource(DUMMY_SOURCE);
        DUMMY_MATERIAL_TYPE.setCategory(DUMMY_CATEGORY);
        DUMMY_SAMPLE_1.setMaterialType(DUMMY_MATERIAL_TYPE);

        SEARCH_SOURCE = new Source();
        SEARCH_SOURCE.setName("Dummy Search Source");
        SEARCH_SOURCE.setMaterialType(DUMMY_MATERIAL_TYPE);

        DUMMY_CHARACTERISTIC = new TermBasedCharacteristic();
        DUMMY_CHARACTERISTIC.setCategory(DUMMY_CATEGORY);
        DUMMY_CHARACTERISTIC.setTerm(DUMMY_MATERIAL_TYPE);
        DUMMY_CHARACTERISTIC.setBioMaterial(DUMMY_SAMPLE_1);
        Transaction tx = null;
        try {
            tx = HibernateUtil.beginTransaction();
            DAO_OBJECT.save(DUMMY_SAMPLE_1);
            tx.commit();
        } catch (DAOException e) {
            HibernateUtil.rollbackTransaction(tx);
            fail("Error setting up test data: " + e.getMessage());
        }
    }

    /**
     * Tests retrieving the <code>Sample</code> with the given id. Test encompasses save and delete of a
     * <code>Sample</code>.
     */
    @Test
    public void testGetSample() {
        Transaction tx = null;

        try {
            tx = HibernateUtil.beginTransaction();
            DAO_OBJECT.save(DUMMY_SAMPLE_1);
            Sample retrievedSample = DAO_OBJECT.retrieve(Sample.class, DUMMY_SAMPLE_1.getId());
            tx.commit();
            if (DUMMY_SAMPLE_1.equals(retrievedSample)) {
                if (compareSamples(retrievedSample, DUMMY_SAMPLE_1)) {
                    // The retrieved sample is the same as the saved sample. Test passed.
                    assertTrue(true);
                }
            } else {
                fail("Retrieved sample is different from saved sample.");
            }
        } catch (DAOException e) {
            HibernateUtil.rollbackTransaction(tx);
            fail("DAO exception during save and retrieve of sample: " + e.getMessage());
        }
    }

    /**
     * Tests retrieving the <code>Sample</code> with the given name. Test encompasses save and delete of a
     * <code>Sample</code>.
     */
    @Test
    public void testGetSampleByCategory() {
        Transaction tx = null;

        try {
            tx = HibernateUtil.beginTransaction();
            DAO_OBJECT.save(DUMMY_SAMPLE_1);

            SearchSampleCategory[] categories = {SearchSampleCategory.SAMPLE_NAME};

            int sampleCount = DAO_SAMPLE_OBJECT.searchCount(DUMMY_SAMPLE_1.getName(), categories);

            List<Sample> retrievedSamples = DAO_SAMPLE_OBJECT.searchByCategory
                (ALL_BY_NAME, DUMMY_SAMPLE_1.getName(), categories);

            tx.commit();

            assertEquals(sampleCount , retrievedSamples.size());
            assertEquals(DUMMY_SAMPLE_1 , retrievedSamples.get(0));

        } catch (DAOException e) {
            HibernateUtil.rollbackTransaction(tx);
            throw e;
        }
    }


    /**
     * Tests retrieving the <code>Source</code> with the given name. Test encompasses save and delete of a
     * <code>Source</code>.
     */
    @Test
    public void testGetSourceByCategory() {
        Transaction tx = null;

        try {
            tx = HibernateUtil.beginTransaction();
            DAO_OBJECT.save(DUMMY_MATERIAL_TYPE);
            DAO_OBJECT.save(DUMMY_SOURCE);
            DAO_OBJECT.save(DUMMY_CATEGORY);
            DAO_OBJECT.save(DUMMY_CHARACTERISTIC);
            DAO_OBJECT.save(SEARCH_SOURCE);
            tx.commit();

            tx = HibernateUtil.beginTransaction();
            SearchSourceCategory[] categories = {SearchSourceCategory.SAMPLE_MATERIAL_TYPE};
            int sourceCount = DAO_SAMPLE_OBJECT.searchCount(DUMMY_MATERIAL_TYPE.getValue(), categories);

            List<Source> retrievedSources = DAO_SAMPLE_OBJECT.searchByCategory
                (ALL_SOURCE_BY_NAME, DUMMY_MATERIAL_TYPE.getValue(), categories);

            tx.commit();

            assertEquals(sourceCount , retrievedSources.size());

        } catch (DAOException e) {
            HibernateUtil.rollbackTransaction(tx);
            throw e;
        }
    }


    /**
     * Tests retrieving the <code>Sample</code> with the given name. Test encompasses save and delete of a
     * <code>Sample</code>.
     */
    @Test
    public void testGetSampleByCharacteristicCategory() {
        Transaction tx = null;

        try {
            tx = HibernateUtil.beginTransaction();
            DAO_OBJECT.save(DUMMY_CATEGORY);
            DAO_OBJECT.save(DUMMY_CHARACTERISTIC);
            DAO_OBJECT.save(DUMMY_SAMPLE_1);
            tx.commit();

            tx = HibernateUtil.beginTransaction();

            List<Category> all_chars = DAO_VOCAB_OBJECT.searchForCharacteristicCategory(DUMMY_CATEGORY.getName());
            int sampleCount = DAO_SAMPLE_OBJECT.countSamplesByCharacteristicCategory(all_chars.get(0), DUMMY_MATERIAL_TYPE.getValue());
            List<Sample> retrievedSamples = DAO_SAMPLE_OBJECT
                .searchSamplesByCharacteristicCategory(DUMMY_CATEGORY, "Dummy Material Type");

            assertEquals(sampleCount , retrievedSamples.size());

            tx.commit();

            assertEquals(DUMMY_SAMPLE_1 , retrievedSamples.get(0));


        } catch (DAOException e) {
            HibernateUtil.rollbackTransaction(tx);
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
        Term retrievedMaterialType = retrievedSample.getMaterialType();
        if (!DUMMY_MATERIAL_TYPE.getValue().equals(retrievedMaterialType.getValue())) {
            return false;
        }
        Collection<AbstractCharacteristic> characteristics = retrievedSample.getCharacteristics();
        if (characteristics.isEmpty() || characteristics.size() != 1) {
            return false;
        }
        Iterator<AbstractCharacteristic> i = characteristics.iterator();
        TermBasedCharacteristic retrievedCharacteristic = (TermBasedCharacteristic) i.next();
        if (!DUMMY_CHARACTERISTIC.getTerm().equals(retrievedCharacteristic.getTerm())) {
            return false;
        }
        return true;
    }
}
