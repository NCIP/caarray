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
import gov.nih.nci.caarray.util.UsernameHolder;

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
public class SampleDaoTest  extends AbstractProjectDaoTest {

    private static TermBasedCharacteristic DUMMY_CHARACTERISTIC = new TermBasedCharacteristic();

    private static final SampleDao DAO_SAMPLE_OBJECT = CaArrayDaoFactory.INSTANCE.getSampleDao();
    private static final PageSortParams<Sample> ALL_BY_NAME =
        new PageSortParams<Sample>(10000, 0, SampleSortCriterion.NAME, false);
    private static final PageSortParams<Source> ALL_SOURCE_BY_NAME =
        new PageSortParams<Source>(10000, 0, SourceSortCriterion.NAME, false);

    /**
     * Tests retrieving the <code>Sample</code> with the given id. Test encompasses save and delete of a
     * <code>Sample</code>.
     */
    @Test
    public void testGetSample() {
        Transaction tx = null;

        try {
            tx = HibernateUtil.beginTransaction();


            DUMMY_CHARACTERISTIC = new TermBasedCharacteristic();
            DUMMY_CHARACTERISTIC.setCategory(DUMMY_CATEGORY);
            DUMMY_CHARACTERISTIC.setTerm(DUMMY_REPLICATE_TYPE);
            DUMMY_CHARACTERISTIC.setBioMaterial(DUMMY_SAMPLE);

            DUMMY_SAMPLE.getCharacteristics().add(DUMMY_CHARACTERISTIC);

            saveSupportingObjects();

            int size = DAO_OBJECT.getProjectCountForCurrentUser(false);
            DAO_OBJECT.save(DUMMY_PROJECT_1);
            tx.commit();

            tx = HibernateUtil.beginTransaction();
            Sample retrievedSample = SEARCH_DAO.retrieve(Sample.class, DUMMY_SAMPLE.getId());

            if (!DUMMY_SAMPLE.equals(retrievedSample)
                    || !compareSamples(retrievedSample, DUMMY_SAMPLE)) {
                fail("Retrieved sample is different from saved sample.");
            }
            tx.commit();
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
    public void testGetSampleByName() {
        Transaction tx = null;

        try {
            tx = HibernateUtil.beginTransaction();
            saveSupportingObjects();
            int size = DAO_OBJECT.getProjectCountForCurrentUser(false);
            DAO_OBJECT.save(DUMMY_PROJECT_1);
            tx.commit();

            tx = HibernateUtil.beginTransaction();

            SearchSampleCategory[] categories = {SearchSampleCategory.SAMPLE_NAME};

            int sampleCount = DAO_SAMPLE_OBJECT.searchCount(DUMMY_SAMPLE.getName(), categories);

            List<Sample> retrievedSamples = DAO_SAMPLE_OBJECT.searchByCategory
                (ALL_BY_NAME, DUMMY_SAMPLE.getName(), categories);

            tx.commit();

            assertEquals(sampleCount , retrievedSamples.size());
            assertEquals(1, sampleCount);
            assertEquals(DUMMY_SAMPLE , retrievedSamples.get(0));

        } catch (DAOException e) {
            HibernateUtil.rollbackTransaction(tx);
            throw e;
        }
    }


    /**
     * Tests retrieving the <code>Source</code> with the given category.
     * Test encompasses save and delete
     * <code>Source</code>.
     */
    @Test
    public void testGetSourceByCategory() {
        Transaction tx = null;

        try {
            tx = HibernateUtil.beginTransaction();
            saveSupportingObjects();
            int size = DAO_OBJECT.getProjectCountForCurrentUser(false);
            DAO_OBJECT.save(DUMMY_PROJECT_1);
            tx.commit();

            tx = HibernateUtil.beginTransaction();
            SearchSourceCategory[] categories = SearchSourceCategory.values();
            int sourceCount = DAO_SAMPLE_OBJECT.searchCount("", categories);

            List<Source> retrievedSources = DAO_SAMPLE_OBJECT.searchByCategory
                (ALL_SOURCE_BY_NAME, "", categories);

            tx.commit();

            assertEquals(sourceCount , retrievedSources.size());
            assertEquals(1 , sourceCount);

        } catch (DAOException e) {
            HibernateUtil.rollbackTransaction(tx);
            throw e;
        }
    }

    /**
     * Tests retrieving the <code>Sample</code> with the given category.
     * Test encompasses save and delete.
     * <code>Source</code>.
     */
    @Test
    public void testGetSampleByCategory() {
        Transaction tx = null;

        try {
            tx = HibernateUtil.beginTransaction();
            saveSupportingObjects();
            int size = DAO_OBJECT.getProjectCountForCurrentUser(false);
            DAO_OBJECT.save(DUMMY_PROJECT_1);
            tx.commit();

            tx = HibernateUtil.beginTransaction();
            SearchSampleCategory[] categories = SearchSampleCategory.values();
            int sampleCount = DAO_SAMPLE_OBJECT.searchCount("", categories);

            List<Source> retrievedSamples = DAO_SAMPLE_OBJECT.searchByCategory
                (ALL_SOURCE_BY_NAME, "", categories);

            tx.commit();

            assertEquals(sampleCount , retrievedSamples.size());
            assertEquals(1 , sampleCount);

        } catch (DAOException e) {
            HibernateUtil.rollbackTransaction(tx);
            throw e;
        }
    }


    /**
     * Tests retrieving the <code>Sample</code> with the given char category and no keyword.
     * Test encompasses save and delete of a
     * <code>Sample</code>.
     */
    @Test
    public void testGetSampleByCharacteristicCategoryAll() {
        Transaction tx = null;

        try {
            tx = HibernateUtil.beginTransaction();
            DUMMY_CHARACTERISTIC = new TermBasedCharacteristic();
            DUMMY_CHARACTERISTIC.setCategory(DUMMY_CATEGORY);
            DUMMY_CHARACTERISTIC.setTerm(DUMMY_REPLICATE_TYPE);
            DUMMY_CHARACTERISTIC.setBioMaterial(DUMMY_SAMPLE);

            DUMMY_SAMPLE.getCharacteristics().add(DUMMY_CHARACTERISTIC);

            saveSupportingObjects();
            int size = DAO_OBJECT.getProjectCountForCurrentUser(false);
            DAO_OBJECT.save(DUMMY_PROJECT_1);
            tx.commit();

            tx = HibernateUtil.beginTransaction();

            List<Category> all_chars = VOCABULARY_DAO.searchForCharacteristicCategory(DUMMY_CATEGORY.getName());
            int sampleCount = DAO_SAMPLE_OBJECT.countSamplesByCharacteristicCategory(all_chars.get(0), "");
            List<Sample> retrievedSamples = DAO_SAMPLE_OBJECT
                .searchSamplesByCharacteristicCategory(DUMMY_CATEGORY, "");

            assertEquals(sampleCount , retrievedSamples.size());

            tx.commit();

            assertEquals(DUMMY_SAMPLE , retrievedSamples.get(0));


        } catch (DAOException e) {
            HibernateUtil.rollbackTransaction(tx);
            throw e;
        }
    }

    /**
     * Tests retrieving the <code>Sample</code> with the given char category and keyword.
     * Test encompasses save and delete of a
     * <code>Sample</code>.
     */
    @Test
    public void testGetSampleByCharacteristicCategory() {
        Transaction tx = null;

        try {
            tx = HibernateUtil.beginTransaction();
            DUMMY_CHARACTERISTIC = new TermBasedCharacteristic();
            DUMMY_CHARACTERISTIC.setCategory(DUMMY_CATEGORY);
            DUMMY_CHARACTERISTIC.setTerm(DUMMY_REPLICATE_TYPE);
            DUMMY_CHARACTERISTIC.setBioMaterial(DUMMY_SAMPLE);

            DUMMY_SAMPLE.getCharacteristics().add(DUMMY_CHARACTERISTIC);

            saveSupportingObjects();
            int size = DAO_OBJECT.getProjectCountForCurrentUser(false);
            DAO_OBJECT.save(DUMMY_PROJECT_1);
            tx.commit();

            tx = HibernateUtil.beginTransaction();

            List<Category> all_chars = VOCABULARY_DAO.searchForCharacteristicCategory(DUMMY_CATEGORY.getName());
            int sampleCount = DAO_SAMPLE_OBJECT
                .countSamplesByCharacteristicCategory(all_chars.get(0), DUMMY_REPLICATE_TYPE.getValue());
            List<Sample> retrievedSamples = DAO_SAMPLE_OBJECT
                .searchSamplesByCharacteristicCategory(DUMMY_CATEGORY, DUMMY_REPLICATE_TYPE.getValue());

            assertEquals(sampleCount , retrievedSamples.size());

            tx.commit();

            assertEquals(DUMMY_SAMPLE , retrievedSamples.get(0));


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
