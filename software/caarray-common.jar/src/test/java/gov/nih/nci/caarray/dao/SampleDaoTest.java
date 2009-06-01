/**
 * The software subject to this notice and license includes both human readable
 * source code form and machine readable, binary, object code form. The caarray-common.jar
 * Software was developed in conjunction with the National Cancer Institute
 * (NCI) by NCI employees and 5AM Solutions, Inc. (5AM). To the extent
 * government employees are authors, any rights in such works shall be subject
 * to Title 17 of the United States Code, section 105.
 *
 * This caarray-common.jar Software License (the License) is between NCI and You. You (or
 * Your) shall mean a person or an entity, and all other entities that control,
 * are controlled by, or are under common control with the entity. Control for
 * purposes of this definition means (i) the direct or indirect power to cause
 * the direction or management of such entity, whether by contract or otherwise,
 * or (ii) ownership of fifty percent (50%) or more of the outstanding shares,
 * or (iii) beneficial ownership of such entity.
 *
 * This License is granted provided that You agree to the conditions described
 * below. NCI grants You a non-exclusive, worldwide, perpetual, fully-paid-up,
 * no-charge, irrevocable, transferable and royalty-free right and license in
 * its rights in the caarray-common.jar Software to (i) use, install, access, operate,
 * execute, copy, modify, translate, market, publicly display, publicly perform,
 * and prepare derivative works of the caarray-common.jar Software; (ii) distribute and
 * have distributed to and by third parties the caarray-common.jar Software and any
 * modifications and derivative works thereof; and (iii) sublicense the
 * foregoing rights set out in (i) and (ii) to third parties, including the
 * right to license such rights to further third parties. For sake of clarity,
 * and not by way of limitation, NCI shall have no right of accounting or right
 * of payment from You or Your sub-licensees for the rights granted under this
 * License. This License is granted at no charge to You.
 *
 * Your redistributions of the source code for the Software must retain the
 * above copyright notice, this list of conditions and the disclaimer and
 * limitation of liability of Article 6, below. Your redistributions in object
 * code form must reproduce the above copyright notice, this list of conditions
 * and the disclaimer of Article 6 in the documentation and/or other materials
 * provided with the distribution, if any.
 *
 * Your end-user documentation included with the redistribution, if any, must
 * include the following acknowledgment: This product includes software
 * developed by 5AM and the National Cancer Institute. If You do not include
 * such end-user documentation, You shall include this acknowledgment in the
 * Software itself, wherever such third-party acknowledgments normally appear.
 *
 * You may not use the names "The National Cancer Institute", "NCI", or "5AM"
 * to endorse or promote products derived from this Software. This License does
 * not authorize You to use any trademarks, service marks, trade names, logos or
 * product names of either NCI or 5AM, except as required to comply with the
 * terms of this License.
 *
 * For sake of clarity, and not by way of limitation, You may incorporate this
 * Software into Your proprietary programs and into any third party proprietary
 * programs. However, if You incorporate the Software into third party
 * proprietary programs, You agree that You are solely responsible for obtaining
 * any permission from such third parties required to incorporate the Software
 * into such third party proprietary programs and for informing Your
 * sub-licensees, including without limitation Your end-users, of their
 * obligation to secure any required permissions from such third parties before
 * incorporating the Software into such third party proprietary software
 * programs. In the event that You fail to obtain such permissions, You agree
 * to indemnify NCI for any claims against NCI by such third parties, except to
 * the extent prohibited by law, resulting from Your failure to obtain such
 * permissions.
 *
 * For sake of clarity, and not by way of limitation, You may add Your own
 * copyright statement to Your modifications and to the derivative works, and
 * You may provide additional or different license terms and conditions in Your
 * sublicenses of modifications of the Software, or any derivative works of the
 * Software as a whole, provided Your use, reproduction, and distribution of the
 * Work otherwise complies with the conditions stated in this License.
 *
 * THIS SOFTWARE IS PROVIDED "AS IS," AND ANY EXPRESSED OR IMPLIED WARRANTIES,
 * (INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY,
 * NON-INFRINGEMENT AND FITNESS FOR A PARTICULAR PURPOSE) ARE DISCLAIMED. IN NO
 * EVENT SHALL THE NATIONAL CANCER INSTITUTE, 5AM SOLUTIONS, INC. OR THEIR
 * AFFILIATES BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS;
 * OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR
 * OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package gov.nih.nci.caarray.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import gov.nih.nci.caarray.domain.project.ExperimentOntologyCategory;
import gov.nih.nci.caarray.domain.sample.AbstractCharacteristic;
import gov.nih.nci.caarray.domain.sample.Extract;
import gov.nih.nci.caarray.domain.sample.Sample;
import gov.nih.nci.caarray.domain.sample.Source;
import gov.nih.nci.caarray.domain.sample.TermBasedCharacteristic;
import gov.nih.nci.caarray.domain.search.AdHocSortCriterion;
import gov.nih.nci.caarray.domain.search.AnnotationCriterion;
import gov.nih.nci.caarray.domain.search.BiomaterialSearchCriteria;
import gov.nih.nci.caarray.domain.search.SampleJoinableSortCriterion;
import gov.nih.nci.caarray.domain.search.SearchSampleCategory;
import gov.nih.nci.caarray.domain.search.SearchSourceCategory;
import gov.nih.nci.caarray.domain.search.SourceJoinableSortCriterion;
import gov.nih.nci.caarray.domain.vocabulary.Category;
import gov.nih.nci.caarray.util.HibernateUtil;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.hibernate.Transaction;
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
        new PageSortParams<Sample>(10000, 0, SampleJoinableSortCriterion.NAME, false);
    private static final PageSortParams<Source> ALL_SOURCE_BY_NAME =
        new PageSortParams<Source>(10000, 0, SourceJoinableSortCriterion.NAME, false);

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
            DAO_OBJECT.save(DUMMY_PROJECT_1);
            tx.commit();

            tx = HibernateUtil.beginTransaction();

            SearchSampleCategory[] categories = {SearchSampleCategory.SAMPLE_NAME};

            int sampleCount = DAO_SAMPLE_OBJECT.searchCount(DUMMY_SAMPLE.getName(), Sample.class, categories);

            List<Sample> retrievedSamples = DAO_SAMPLE_OBJECT.searchByCategory
                (ALL_BY_NAME, DUMMY_SAMPLE.getName(), Sample.class, categories);

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
            DAO_OBJECT.save(DUMMY_PROJECT_1);
            tx.commit();

            tx = HibernateUtil.beginTransaction();
            SearchSourceCategory[] categories = SearchSourceCategory.values();
            int sourceCount = DAO_SAMPLE_OBJECT.searchCount("", Source.class, categories);

            List<Source> retrievedSources = DAO_SAMPLE_OBJECT.searchByCategory
                (ALL_SOURCE_BY_NAME, "", Source.class, categories);

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
    public void testGetSampleByCategoryNoKeyword() {
        Transaction tx = null;

        try {
            tx = HibernateUtil.beginTransaction();
            saveSupportingObjects();
            DAO_OBJECT.save(DUMMY_PROJECT_1);
            tx.commit();

            tx = HibernateUtil.beginTransaction();
            SearchSampleCategory[] categories = SearchSampleCategory.values();
            int sampleCount = DAO_SAMPLE_OBJECT.searchCount("", Sample.class, categories);

            List<Sample> retrievedSamples = DAO_SAMPLE_OBJECT.searchByCategory
                (ALL_BY_NAME, "", Sample.class, categories);

            tx.commit();

            assertEquals(sampleCount , retrievedSamples.size());
            assertEquals(1 , sampleCount);

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
            DAO_OBJECT.save(DUMMY_PROJECT_1);
            tx.commit();

            tx = HibernateUtil.beginTransaction();
            SearchSampleCategory[] categories = SearchSampleCategory.values();
            int sampleCount = DAO_SAMPLE_OBJECT.searchCount("", Sample.class, categories);

            List<Sample> retrievedSamples = DAO_SAMPLE_OBJECT.searchByCategory
                (ALL_BY_NAME, DUMMY_SAMPLE.getName(), Sample.class, categories);

            tx.commit();

            assertEquals(sampleCount , retrievedSamples.size());
            assertEquals(1 , sampleCount);

        } catch (DAOException e) {
            HibernateUtil.rollbackTransaction(tx);
            throw e;
        }
    }


    @Test
    public void testGetSourceByCharacteristicCategory() {
        Transaction tx = null;

        try {
            tx = HibernateUtil.beginTransaction();
            DUMMY_CHARACTERISTIC = new TermBasedCharacteristic();
            DUMMY_CHARACTERISTIC.setCategory(DUMMY_CATEGORY);
            DUMMY_CHARACTERISTIC.setTerm(DUMMY_REPLICATE_TYPE);
            DUMMY_CHARACTERISTIC.setBioMaterial(DUMMY_SOURCE);

            DUMMY_SOURCE.getCharacteristics().add(DUMMY_CHARACTERISTIC);

            saveSupportingObjects();
            DAO_OBJECT.save(DUMMY_PROJECT_1);
            tx.commit();

            tx = HibernateUtil.beginTransaction();

            List<Category> all_chars = VOCABULARY_DAO.searchForCharacteristicCategory(null,
                    TermBasedCharacteristic.class, DUMMY_CATEGORY.getName());
            int sourceCount = DAO_SAMPLE_OBJECT
                .countSourcesByCharacteristicCategory(all_chars.get(0), DUMMY_REPLICATE_TYPE.getValue());
            PageSortParams<Source> sortByOrganism =
                new PageSortParams<Source>(10000, 0, SourceJoinableSortCriterion.NAME, false);
            sortByOrganism.setSortCriterion(SourceJoinableSortCriterion.ORGANISM);
            List<Source> retrievedSamples = DAO_SAMPLE_OBJECT
                .searchSourcesByCharacteristicCategory(sortByOrganism,
                        DUMMY_CATEGORY, DUMMY_REPLICATE_TYPE.getValue());

            assertEquals(sourceCount , retrievedSamples.size());

            tx.commit();

            assertEquals(DUMMY_SOURCE , retrievedSamples.get(0));


        } catch (DAOException e) {
            HibernateUtil.rollbackTransaction(tx);
            throw e;
        }
    }

    @Test
    public void testGetSourceByCharacteristicCategoryAll() {
        Transaction tx = null;

        try {
            tx = HibernateUtil.beginTransaction();
            DUMMY_CHARACTERISTIC = new TermBasedCharacteristic();
            DUMMY_CHARACTERISTIC.setCategory(DUMMY_CATEGORY);
            DUMMY_CHARACTERISTIC.setTerm(DUMMY_REPLICATE_TYPE);
            DUMMY_CHARACTERISTIC.setBioMaterial(DUMMY_SOURCE);

            DUMMY_SOURCE.getCharacteristics().add(DUMMY_CHARACTERISTIC);

            saveSupportingObjects();
            DAO_OBJECT.save(DUMMY_PROJECT_1);
            tx.commit();

            tx = HibernateUtil.beginTransaction();

            List<Category> all_chars = VOCABULARY_DAO.searchForCharacteristicCategory(null,
                    TermBasedCharacteristic.class, DUMMY_CATEGORY.getName());
            int sourceCount = DAO_SAMPLE_OBJECT
                .countSourcesByCharacteristicCategory(all_chars.get(0), DUMMY_REPLICATE_TYPE.getValue());
            PageSortParams<Source> sortByOrganism =
                new PageSortParams<Source>(10000, 0, SourceJoinableSortCriterion.NAME, false);
            sortByOrganism.setSortCriterion(SourceJoinableSortCriterion.ORGANISM);
            List<Source> retrievedSamples = DAO_SAMPLE_OBJECT
                .searchSourcesByCharacteristicCategory(sortByOrganism,
                        DUMMY_CATEGORY, "");

            assertEquals(sourceCount , retrievedSamples.size());

            tx.commit();

            assertEquals(DUMMY_SOURCE , retrievedSamples.get(0));


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
            DAO_OBJECT.save(DUMMY_PROJECT_1);
            tx.commit();

            tx = HibernateUtil.beginTransaction();

            List<Category> all_chars = VOCABULARY_DAO.searchForCharacteristicCategory(null,
                    TermBasedCharacteristic.class, DUMMY_CATEGORY.getName());
            int sampleCount = DAO_SAMPLE_OBJECT.countSamplesByCharacteristicCategory(all_chars.get(0), "");
            List<Sample> retrievedSamples = DAO_SAMPLE_OBJECT
                .searchSamplesByCharacteristicCategory(ALL_BY_NAME, DUMMY_CATEGORY, "");

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
            DAO_OBJECT.save(DUMMY_PROJECT_1);
            tx.commit();

            tx = HibernateUtil.beginTransaction();

            List<Category> all_chars = VOCABULARY_DAO.searchForCharacteristicCategory(null,
                    TermBasedCharacteristic.class, DUMMY_CATEGORY.getName());
            int sampleCount = DAO_SAMPLE_OBJECT
                .countSamplesByCharacteristicCategory(all_chars.get(0), DUMMY_REPLICATE_TYPE.getValue());
            List<Sample> retrievedSamples = DAO_SAMPLE_OBJECT
                .searchSamplesByCharacteristicCategory(ALL_BY_NAME, DUMMY_CATEGORY, DUMMY_REPLICATE_TYPE.getValue());

            assertEquals(sampleCount , retrievedSamples.size());

            tx.commit();

            assertEquals(DUMMY_SAMPLE , retrievedSamples.get(0));


        } catch (DAOException e) {
            HibernateUtil.rollbackTransaction(tx);
            throw e;
        }
    }
    
    @Test
    public void testSearchByCriteria() {        
        Transaction tx = null;

        try {
            tx = HibernateUtil.beginTransaction();
            DUMMY_CHARACTERISTIC = new TermBasedCharacteristic();
            DUMMY_CHARACTERISTIC.setCategory(DUMMY_CATEGORY);
            DUMMY_CHARACTERISTIC.setTerm(DUMMY_REPLICATE_TYPE);
            DUMMY_CHARACTERISTIC.setBioMaterial(DUMMY_SAMPLE);

            DUMMY_SAMPLE.getCharacteristics().add(DUMMY_CHARACTERISTIC);
            DUMMY_SAMPLE.setExternalSampleId("123");
            DUMMY_SAMPLE.setDiseaseState(DUMMY_NORMALIZATION_TYPE);
            
            saveSupportingObjects();
            DAO_OBJECT.save(DUMMY_PROJECT_1);
            DAO_OBJECT.save(DUMMY_PROJECT_2);
            DAO_OBJECT.save(DUMMY_PROJECT_3);
            tx.commit();

            tx = HibernateUtil.beginTransaction();

            PageSortParams<Source> sourceParams = new PageSortParams<Source>(20, 0, new AdHocSortCriterion<Source>(
                    "name"), false);
            PageSortParams<Sample> sampleParams = new PageSortParams<Sample>(20, 0, new AdHocSortCriterion<Sample>(
                    "name"), false);
            PageSortParams<Extract> extractParams = new PageSortParams<Extract>(20, 0, new AdHocSortCriterion<Extract>(
                    "name"), false);

            BiomaterialSearchCriteria bsc = new BiomaterialSearchCriteria();
            bsc.setExperiment(DUMMY_EXPERIMENT_1);
            List<Source> srcResults = DAO_SAMPLE_OBJECT.searchByCriteria(sourceParams, bsc, Source.class);
            assertEquals(1, srcResults.size());
            assertEquals(DUMMY_SOURCE.getName(), srcResults.get(0).getName());
            
            bsc.setExperiment(null);
            bsc.getNames().add(DUMMY_EXTRACT.getName());
            List<Extract> extResults = DAO_SAMPLE_OBJECT.searchByCriteria(extractParams, bsc, Extract.class);
            assertEquals(1, extResults.size());
            assertEquals(DUMMY_EXTRACT.getName(), extResults.get(0).getName());

            bsc.getExternalIds().add("123");
            extResults = DAO_SAMPLE_OBJECT.searchByCriteria(extractParams, bsc, Extract.class);
            assertEquals(0, extResults.size());

            bsc.getNames().clear();
            bsc.getExternalIds().add(DUMMY_SAMPLE.getExternalSampleId());
            List<Sample> results = DAO_SAMPLE_OBJECT.searchByCriteria(sampleParams, bsc, Sample.class);
            assertEquals(1, results.size());
            assertTrue(compareSamples(results.get(0), DUMMY_SAMPLE));
            
            bsc.setExperiment(DUMMY_EXPERIMENT_2);
            results = DAO_SAMPLE_OBJECT.searchByCriteria(sampleParams, bsc, Sample.class);
            assertEquals(0, results.size());
            
            bsc = new BiomaterialSearchCriteria();
            Category ds = new Category();
            ds.setName(ExperimentOntologyCategory.DISEASE_STATE.getCategoryName());
            bsc.getAnnotationCriterions().add(new AnnotationCriterion(ds, DUMMY_NORMALIZATION_TYPE.getValue()));
            results = DAO_SAMPLE_OBJECT.searchByCriteria(sampleParams, bsc, Sample.class);
            assertEquals(1, results.size());
            assertTrue(compareSamples(results.get(0), DUMMY_SAMPLE));            
            
            tx.commit();
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
