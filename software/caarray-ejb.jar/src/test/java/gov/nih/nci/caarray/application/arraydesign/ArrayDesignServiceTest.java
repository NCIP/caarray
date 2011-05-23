/**
 * The software subject to this notice and license includes both human readable
 * source code form and machine readable, binary, object code form. The caArray
 * Software was developed in conjunction with the National Cancer Institute
 * (NCI) by NCI employees and 5AM Solutions, Inc. (5AM). To the extent
 * government employees are authors, any rights in such works shall be subject
 * to Title 17 of the United States Code, section 105.
 *
 * This caArray Software License (the License) is between NCI and You. You (or
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
 * its rights in the caArray Software to (i) use, install, access, operate,
 * execute, copy, modify, translate, market, publicly display, publicly perform,
 * and prepare derivative works of the caArray Software; (ii) distribute and
 * have distributed to and by third parties the caArray Software and any
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
package gov.nih.nci.caarray.application.arraydesign;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import edu.georgetown.pir.Organism;
import gov.nih.nci.caarray.application.AbstractServiceTest;
import gov.nih.nci.caarray.application.fileaccess.FileAccessService;
import gov.nih.nci.caarray.application.fileaccess.FileAccessServiceStub;
import gov.nih.nci.caarray.application.fileaccess.TemporaryFileCacheLocator;
import gov.nih.nci.caarray.application.fileaccess.TemporaryFileCacheStubFactory;
import gov.nih.nci.caarray.application.vocabulary.VocabularyService;
import gov.nih.nci.caarray.application.vocabulary.VocabularyServiceStub;
import gov.nih.nci.caarray.dao.ArrayDao;
import gov.nih.nci.caarray.dao.ContactDao;
import gov.nih.nci.caarray.dao.SearchDao;
import gov.nih.nci.caarray.dao.VocabularyDao;
import gov.nih.nci.caarray.dao.stub.ArrayDaoStub;
import gov.nih.nci.caarray.dao.stub.ContactDaoStub;
import gov.nih.nci.caarray.dao.stub.DaoFactoryStub;
import gov.nih.nci.caarray.dao.stub.SearchDaoStub;
import gov.nih.nci.caarray.dao.stub.VocabularyDaoStub;
import gov.nih.nci.caarray.domain.AbstractCaArrayEntity;
import gov.nih.nci.caarray.domain.AbstractCaArrayObject;
import gov.nih.nci.caarray.domain.array.ArrayDesign;
import gov.nih.nci.caarray.domain.array.ArrayDesignDetails;
import gov.nih.nci.caarray.domain.array.Feature;
import gov.nih.nci.caarray.domain.array.LogicalProbe;
import gov.nih.nci.caarray.domain.array.PhysicalProbe;
import gov.nih.nci.caarray.domain.array.SNPProbeAnnotation;
import gov.nih.nci.caarray.domain.contact.Organization;
import gov.nih.nci.caarray.domain.data.DesignElementList;
import gov.nih.nci.caarray.domain.file.CaArrayFile;
import gov.nih.nci.caarray.domain.file.FileStatus;
import gov.nih.nci.caarray.domain.file.FileType;
import gov.nih.nci.caarray.domain.project.AssayType;
import gov.nih.nci.caarray.domain.search.ExampleSearchCriteria;
import gov.nih.nci.caarray.domain.vocabulary.Category;
import gov.nih.nci.caarray.domain.vocabulary.Term;
import gov.nih.nci.caarray.domain.vocabulary.TermSource;
import gov.nih.nci.caarray.jobqueue.JobQueue;
import gov.nih.nci.caarray.platforms.spi.PlatformFileReadException;
import gov.nih.nci.caarray.staticinjection.CaArrayEjbStaticInjectionModule;
import gov.nih.nci.caarray.test.data.arraydata.AffymetrixArrayDataFiles;
import gov.nih.nci.caarray.test.data.arraydesign.AffymetrixArrayDesignFiles;
import gov.nih.nci.caarray.test.data.arraydesign.AgilentArrayDesignFiles;
import gov.nih.nci.caarray.test.data.arraydesign.GenepixArrayDesignFiles;
import gov.nih.nci.caarray.test.data.arraydesign.IlluminaArrayDesignFiles;
import gov.nih.nci.caarray.test.data.arraydesign.NimblegenArrayDesignFiles;
import gov.nih.nci.caarray.test.data.magetab.MageTabDataFiles;
import gov.nih.nci.caarray.util.CaArrayHibernateHelper;
import gov.nih.nci.caarray.util.CaArrayHibernateHelperModule;
import gov.nih.nci.caarray.util.UsernameHolder;
import gov.nih.nci.caarray.util.j2ee.ServiceLocatorStub;
import gov.nih.nci.caarray.validation.ValidationResult;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.PredicateUtils;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.hibernate.Transaction;
import org.hibernate.criterion.Order;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.fiveamsolutions.nci.commons.data.persistent.PersistentObject;
import com.fiveamsolutions.nci.commons.data.search.PageSortParams;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.util.Modules;

/**
 * Test class for ArrayDesignService subsystem.
 */
@SuppressWarnings("PMD")
public class ArrayDesignServiceTest extends AbstractServiceTest {
    private ArrayDesignService arrayDesignService;
    private final LocalDaoFactoryStub caArrayDaoFactoryStub = new LocalDaoFactoryStub();
    private final FileAccessServiceStub fileAccessServiceStub = new FileAccessServiceStub();
    private Transaction transaction;

    private static Organization DUMMY_ORGANIZATION = new Organization();
    private static Organism DUMMY_ORGANISM = new Organism();
    private static Term DUMMY_TERM = new Term();
    private static AssayType DUMMY_ASSAY_TYPE = new AssayType("microRNA");

    @Before
    public void setUp() {
        final Module testArrayDesignModule = Modules.override(new ArrayDesignModule()).with(new AbstractModule() {
            @Override
            protected void configure() {
                bind(UsernameHolder.class).toInstance(mock(UsernameHolder.class));
                bind(JobQueue.class).toInstance(mock(JobQueue.class));
                bind(ContactDao.class).toInstance(caArrayDaoFactoryStub.getContactDao());
                bind(SearchDao.class).toInstance(caArrayDaoFactoryStub.getSearchDao());
                bind(ArrayDao.class).toInstance(caArrayDaoFactoryStub.getArrayDao());
                bind(VocabularyDao.class).toInstance(caArrayDaoFactoryStub.getVocabularyDao());
                
                bind(ArrayDesignService.class).to(ArrayDesignServiceBean.class);
            }
        });
        
        Injector injector = Guice.createInjector(new CaArrayEjbStaticInjectionModule(), new CaArrayHibernateHelperModule(),
                testArrayDesignModule);
        CaArrayHibernateHelper hibernateHelper = injector.getInstance(CaArrayHibernateHelper.class);;
        hibernateHelper.setFiltersEnabled(false);
        transaction = hibernateHelper.beginTransaction();

        caArrayDaoFactoryStub.clear();
        arrayDesignService = createArrayDesignService(injector);
        DUMMY_ORGANIZATION.setName("DummyOrganization");
        DUMMY_ORGANISM.setScientificName("Homo sapiens");
        final TermSource ts = new TermSource();
        ts.setName("TS 1");
        final Category cat = new Category();
        cat.setName("catName");
        cat.setSource(ts);
        DUMMY_ORGANISM.setTermSource(ts);
        DUMMY_TERM.setValue("testval");
        DUMMY_TERM.setCategory(cat);
        DUMMY_TERM.setSource(ts);
    }

    public ArrayDesignService createArrayDesignService(final Injector injector) {       
        final ArrayDesignServiceBean bean = (ArrayDesignServiceBean) injector.getInstance(ArrayDesignService.class);
        final ServiceLocatorStub locatorStub = ServiceLocatorStub.registerEmptyLocator();
        locatorStub.addLookup(FileAccessService.JNDI_NAME, fileAccessServiceStub);
        locatorStub.addLookup(VocabularyService.JNDI_NAME, new VocabularyServiceStub());
        TemporaryFileCacheLocator.setTemporaryFileCacheFactory(new TemporaryFileCacheStubFactory(fileAccessServiceStub));
        TemporaryFileCacheLocator.resetTemporaryFileCache();
        return bean;
    }

    @After
    public void tearDown() {
        if (transaction != null && transaction.isActive()) {
            transaction.rollback();
        }
    }


    @Test
    public void testSaveArrayDesign() throws Exception {
        final ArrayDesign design = createDesign(null, null, null,
                getAffymetrixCdfCaArrayFile(AffymetrixArrayDesignFiles.TEST3_CDF));
        arrayDesignService.importDesign(design);
        assertEquals("Test3", design.getName());
        assertEquals("Affymetrix.com", design.getLsidAuthority());
        assertEquals("PhysicalArrayDesign", design.getLsidNamespace());
        assertEquals("Test3", design.getLsidObjectId());
        assertNull(design.getDescription());

        design.setDescription("new description");
        arrayDesignService.saveArrayDesign(design);
        final ArrayDesign updatedDesign = arrayDesignService.getArrayDesign(design.getId());
        assertEquals("Test3", updatedDesign.getName());
        assertEquals("Affymetrix.com", updatedDesign.getLsidAuthority());
        assertEquals("PhysicalArrayDesign", updatedDesign.getLsidNamespace());
        assertEquals("Test3", updatedDesign.getLsidObjectId());
        assertEquals("new description", updatedDesign.getDescription());
    }
    
    @Test
    @SuppressWarnings("deprecation")
    public void testSaveLockedArrayDesignAllowedFields() throws Exception {
        ArrayDesign design = createDesign(null, null, null,
                getAffymetrixCdfCaArrayFile(AffymetrixArrayDesignFiles.TEST3_CDF));
        // "lock" this design by setting the ID to 2 but can still update description of locked designs
        design.setId(2L);
        caArrayDaoFactoryStub.getArrayDao().save(design);
        arrayDesignService.importDesign(design);
        
        design.setDescription("another description");
        design = arrayDesignService.saveArrayDesign(design);
        assertEquals(2L, design.getId().longValue());        
        final ArrayDesign updatedLockedDesign = arrayDesignService.getArrayDesign(design.getId());
        assertEquals("Test3", updatedLockedDesign.getName());
        assertEquals("Affymetrix.com", updatedLockedDesign.getLsidAuthority());
        assertEquals("PhysicalArrayDesign", updatedLockedDesign.getLsidNamespace());
        assertEquals("Test3", updatedLockedDesign.getLsidObjectId());
        assertEquals("another description", updatedLockedDesign.getDescription());        
    }

    @Test(expected=IllegalAccessException.class)
    public void testSaveArrayDesignWhileImporting() throws Exception {
        final ArrayDesign design = createDesign(null, null, null,
                getAffymetrixCdfCaArrayFile(AffymetrixArrayDesignFiles.TEST3_CDF));
        arrayDesignService.importDesign(design);
        assertEquals("Test3", design.getName());
        assertEquals("Affymetrix.com", design.getLsidAuthority());
        assertEquals("PhysicalArrayDesign", design.getLsidNamespace());
        assertEquals("Test3", design.getLsidObjectId());
        assertNull(design.getDescription());

        design.getFirstDesignFile().setFileStatus(FileStatus.IMPORTING);
        design.setName("new name");
        arrayDesignService.saveArrayDesign(design);
    }

    @Test(expected=IllegalAccessException.class)
    @SuppressWarnings("deprecation")
    public void testSaveArrayDesignLockedProviderChangeOrganization() throws Exception {
        ArrayDesign design = createDesign(null, null, null,
                getAffymetrixCdfCaArrayFile(AffymetrixArrayDesignFiles.TEST3_CDF));
        // array designs with ID == 2 are considered locked by the test stub
        design.setId(2L);
        arrayDesignService.importDesign(design);

        // since the test DB is in memory, we have to instantiate a new copy of this design to alter it
        design = createDesign(null, null, null, getAffymetrixCdfCaArrayFile(AffymetrixArrayDesignFiles.TEST3_CDF));
        design.setId(2L);
        design.setProvider(new Organization());
        arrayDesignService.saveArrayDesign(design);
    }

    @Test(expected=IllegalAccessException.class)
    @SuppressWarnings("deprecation")
    public void testSaveArrayDesignLockedProviderChangeAssayType() throws Exception {
        ArrayDesign design = createDesign(null, null, null,
                getAffymetrixCdfCaArrayFile(AffymetrixArrayDesignFiles.TEST3_CDF));
        // array designs with ID == 2 are considered locked by the test stub
        design.setId(2L);
        arrayDesignService.importDesign(design);

        // since the test DB is in memory, we have to instantiate a new copy of this design to alter it
        design = createDesign(null, null, null, getAffymetrixCdfCaArrayFile(AffymetrixArrayDesignFiles.TEST3_CDF));
        design.setId(2L);
        final SortedSet <AssayType>assayTypes = new TreeSet<AssayType>();
        assayTypes.add(DUMMY_ASSAY_TYPE);
        design.setAssayTypes(assayTypes);
        arrayDesignService.saveArrayDesign(design);
    }

    @Test(expected=IllegalAccessException.class)
    @SuppressWarnings("deprecation")
    public void testSaveArrayDesignLockedProviderChangeDesignFile() throws Exception {
        ArrayDesign design = createDesign(null, null, null,
                getAffymetrixCdfCaArrayFile(AffymetrixArrayDesignFiles.TEST3_CDF));
        // array designs with ID == 2 are considered locked by the test stub
        design.setId(2L);
        arrayDesignService.importDesign(design);

        // since the test DB is in memory, we have to instantiate a new copy of this design to alter it
        design = createDesign(null, null, null, getAffymetrixCdfCaArrayFile(AffymetrixArrayDesignFiles.TEST3_CDF));
        design.setId(2L);
        design.getDesignFiles().clear();
        design.addDesignFile(new CaArrayFile());
        arrayDesignService.saveArrayDesign(design);
    }

    @Test
    public void testImportDesignDetails_Genepix() {
        final ArrayDesign design = new ArrayDesign();
        arrayDesignService.importDesignDetails(design);
        assertNull(design.getNumberOfFeatures());

        design.addDesignFile(getGenepixCaArrayFile(GenepixArrayDesignFiles.DEMO_GAL));
        arrayDesignService.importDesign(design);
        arrayDesignService.importDesignDetails(design);
        checkGenepixDesign(design, "Demo", 8064, 4, 4);
        design.getDesignFiles().clear();
        design.addDesignFile(getGenepixCaArrayFile(GenepixArrayDesignFiles.MEEBO));
        arrayDesignService.importDesign(design);
        arrayDesignService.importDesignDetails(design);
        checkGenepixDesign(design, "MEEBO", 38880, 16, 13);
    }

    private void checkGenepixDesign(final ArrayDesign design, final String expectedName, final int expectedNumberOfFeatures,
            final int largestExpectedBlockColumn, final int largestExpectedBlockRow) {
        assertEquals(expectedName, design.getName());
        assertEquals(expectedNumberOfFeatures, design.getNumberOfFeatures().intValue());
        assertEquals(expectedNumberOfFeatures, design.getDesignDetails().getFeatures().size());
        assertEquals(expectedNumberOfFeatures, design.getDesignDetails().getProbes().size());
        final Iterator<PhysicalProbe> probeIt = design.getDesignDetails().getProbes().iterator();
        while (probeIt.hasNext()) {
            final PhysicalProbe probe = probeIt.next();
            assertFalse(StringUtils.isBlank(probe.getName()));
            assertEquals(1, probe.getFeatures().size());
            final Feature feature = probe.getFeatures().iterator().next();
            assertTrue(feature.getBlockColumn() > 0);
            assertTrue(feature.getBlockRow() > 0);
            assertTrue(feature.getBlockColumn() <= largestExpectedBlockColumn);
            assertTrue(feature.getBlockRow() <= largestExpectedBlockRow);
            assertTrue(feature.getColumn() > 0);
            assertTrue(feature.getRow() > 0);
        }
    }

    @Test
    public void testImportDesign_ArrayDesign() {
        final ArrayDesign design = new ArrayDesign();
        arrayDesignService.importDesign(design);
        assertNull(design.getName());

        design.addDesignFile(getAffymetrixCdfCaArrayFile(AffymetrixArrayDesignFiles.TEST3_CDF));
        arrayDesignService.importDesign(design);
        assertEquals("Test3", design.getName());
        assertEquals("Affymetrix.com", design.getLsidAuthority());
        assertEquals("PhysicalArrayDesign", design.getLsidNamespace());
        assertEquals("Test3", design.getLsidObjectId());
    }

    @Test
    public void testImportDesign_AffymetrixTest3() {
        final CaArrayFile designFile = getAffymetrixCdfCaArrayFile(AffymetrixArrayDesignFiles.TEST3_CDF);
        final ArrayDesign arrayDesign = new ArrayDesign();
        arrayDesign.addDesignFile(designFile);
        arrayDesignService.importDesign(arrayDesign);
        assertEquals("Test3", arrayDesign.getName());
        assertEquals("Affymetrix.com", arrayDesign.getLsidAuthority());
        assertEquals("PhysicalArrayDesign", arrayDesign.getLsidNamespace());
        assertEquals("Test3", arrayDesign.getLsidObjectId());
        assertEquals(15876, arrayDesign.getNumberOfFeatures().intValue());
    }

    @Test
    public void testImportDesignDetails_AffymetrixTest3() throws PlatformFileReadException {
        final String name = "Test3";
        final File testFile = AffymetrixArrayDesignFiles.TEST3_CDF;
        final int expectedFeatureCount = 15876;
        testImportDesignDetails(name, testFile, expectedFeatureCount);
    }

    @Test
    public void testImportDesignDetails_AffymetrixAgcc2() throws PlatformFileReadException {
        final String name = "AGCC_2.x_Test_Truncated";
        final File testFile = AffymetrixArrayDesignFiles.AGCC_2_X_TEST_CDF;
        final int expectedFeatureCount = 553536;
        testImportDesignDetails(name, testFile, expectedFeatureCount);
    }

    @Test
    public void testImportDesignDetails_AffymetrixAgcc3() throws PlatformFileReadException {
        final String name = "AGCC_3.x_Test_Truncated";
        final File testFile = AffymetrixArrayDesignFiles.AGCC_3_X_TEST_CDF;
        final int expectedFeatureCount = 553536;
        testImportDesignDetails(name, testFile, expectedFeatureCount);
    }

    @Test
    public void testImportDesignDetails_AffymetrixAgccGcos() throws PlatformFileReadException {
        final String name = "AGCC_GCOS_Test_Truncated";
        final File testFile = AffymetrixArrayDesignFiles.AGCC_GCOS_TEST_CDF;
        final int expectedFeatureCount = 553536;
        testImportDesignDetails(name, testFile, expectedFeatureCount);
    }

    @SuppressWarnings("deprecation")
    private void testImportDesignDetails(final String name, final File testFile, final int expectedFeatureCount) {
        final ArrayDesign design = new ArrayDesign();
        design.setId(0L);
        design.addDesignFile(getAffymetrixCdfCaArrayFile(testFile));
        arrayDesignService.importDesign(design);
        arrayDesignService.importDesignDetails(design);
        assertEquals(name, design.getName());
        assertEquals("Affymetrix.com", design.getLsidAuthority());
        assertEquals("PhysicalArrayDesign", design.getLsidNamespace());
        assertEquals(name, design.getLsidObjectId());
        assertEquals(expectedFeatureCount, design.getNumberOfFeatures().intValue());
    }

    @Test
    public void testImportDesign_AffymetrixMapping10K() {
        final CaArrayFile designFile = getAffymetrixCdfCaArrayFile(AffymetrixArrayDesignFiles.TEN_K_CDF);
        final ArrayDesign arrayDesign = new ArrayDesign();
        arrayDesign.addDesignFile(designFile);
        arrayDesignService.importDesign(arrayDesign);
        assertEquals("Mapping10K_Xba131-xda", arrayDesign.getName());
        assertEquals("Affymetrix.com", arrayDesign.getLsidAuthority());
        assertEquals("PhysicalArrayDesign", arrayDesign.getLsidNamespace());
        assertEquals("Mapping10K_Xba131-xda", arrayDesign.getLsidObjectId());
        assertEquals(506944, arrayDesign.getNumberOfFeatures().intValue());
    }

    @Test
    public void testImportDesign_AffymetrixHuEx() {
        final CaArrayFile clfDesignFile = getCaArrayFile(AffymetrixArrayDesignFiles.HUEX_TEST_CLF);
        final CaArrayFile pgfDesignFile = getCaArrayFile(AffymetrixArrayDesignFiles.HUEX_TEST_PGF);
        final ArrayDesign arrayDesign = new ArrayDesign();
        arrayDesign.addDesignFile(clfDesignFile);
        arrayDesign.addDesignFile(pgfDesignFile);
        arrayDesignService.importDesign(arrayDesign);
        assertEquals("HuEx-1_0-st-v1-test", arrayDesign.getName());
        assertEquals("Affymetrix.com", arrayDesign.getLsidAuthority());
        assertEquals("PhysicalArrayDesign", arrayDesign.getLsidNamespace());
        assertEquals("HuEx-1_0-st-v1-test", arrayDesign.getLsidObjectId());
        assertEquals(1024, arrayDesign.getNumberOfFeatures().intValue());
    }

    @Test
    @SuppressWarnings("deprecation")
    public void testImportDesignDetails_AffymetrixHuEx() throws PlatformFileReadException {
        final CaArrayFile clfDesignFile = getCaArrayFile(AffymetrixArrayDesignFiles.HUEX_TEST_CLF);
        final CaArrayFile pgfDesignFile = getCaArrayFile(AffymetrixArrayDesignFiles.HUEX_TEST_PGF);
        final ArrayDesign design = new ArrayDesign();
        design.setId(0L);
        design.addDesignFile(clfDesignFile);
        design.addDesignFile(pgfDesignFile);
        arrayDesignService.importDesign(design);
        arrayDesignService.importDesignDetails(design);
        final String arrayDesignName = "HuEx-1_0-st-v1-test";
        assertEquals(arrayDesignName, design.getName());
        assertEquals("Affymetrix.com", design.getLsidAuthority());
        assertEquals("PhysicalArrayDesign", design.getLsidNamespace());
        assertEquals(arrayDesignName, design.getLsidObjectId());
        assertEquals(1024, design.getNumberOfFeatures().intValue());

        assertEquals(94, design.getDesignDetails().getLogicalProbes().size());
        assertEquals(364, design.getDesignDetails().getProbes().size());
        assertEquals(1024, design.getDesignDetails().getFeatures().size());

        for (final PhysicalProbe probe : design.getDesignDetails().getProbes()) {
            assertTrue(probe.getFeatures().size() > 0);
            assertEquals(design.getDesignDetails(), probe.getArrayDesignDetails());
            for (final Feature abstractFeature : probe.getFeatures()) {
                final Feature feature = abstractFeature;
                assertTrue(feature.getColumn() >= 0 && feature.getColumn() < 32);
                assertTrue(feature.getRow() >= 0 && feature.getRow() < 32);
            }
        }

        for (final LogicalProbe logicalProbe : design.getDesignDetails().getLogicalProbes()) {
            assertEquals(design.getDesignDetails(), logicalProbe.getArrayDesignDetails());
            assertTrue(logicalProbe.getProbes().size() > 0);
            for (final PhysicalProbe physicalProbe : logicalProbe.getProbes()) {
                assertEquals(design.getDesignDetails(), physicalProbe.getArrayDesignDetails());
                assertTrue(physicalProbe.getFeatures().size() > 0);
                for (final Feature abstractFeature : physicalProbe.getFeatures()) {
                    final Feature feature = abstractFeature;
                    assertEquals(design.getDesignDetails(), feature.getArrayDesignDetails());
                    assertTrue(feature.getColumn() >= 0 && feature.getColumn() < 32);
                    assertTrue(feature.getRow() >= 0 && feature.getRow() < 32);
                }
            }
        }
    }

    @Test
    public void testImportDesign_IlluminaHumanWG6() {
        final CaArrayFile designFile = getIlluminaCaArrayFile(IlluminaArrayDesignFiles.HUMAN_WG6_CSV);
        final ArrayDesign arrayDesign = new ArrayDesign();
        arrayDesign.addDesignFile(designFile);
        arrayDesignService.importDesign(arrayDesign);
        assertEquals("Human_WG-6", arrayDesign.getName());
        assertEquals("illumina.com", arrayDesign.getLsidAuthority());
        assertEquals("PhysicalArrayDesign", arrayDesign.getLsidNamespace());
        assertEquals("Human_WG-6", arrayDesign.getLsidObjectId());
        assertEquals(47296, arrayDesign.getNumberOfFeatures().intValue());
    }

    @Test
    public void testImportDesignDetails_IlluminaHumanWG6() {
        final CaArrayFile designFile = getIlluminaCaArrayFile(IlluminaArrayDesignFiles.HUMAN_WG6_CSV);
        final ArrayDesign arrayDesign = new ArrayDesign();
        arrayDesign.addDesignFile(designFile);
        arrayDesignService.importDesign(arrayDesign);
        arrayDesignService.importDesignDetails(arrayDesign);
        assertEquals(47296, arrayDesign.getDesignDetails().getProbes().size());
    }

    @Test
    public void testImportDesign_IlluminaHumanHap300() {
        final CaArrayFile designFile = getIlluminaCaArrayFile(IlluminaArrayDesignFiles.HUMAN_HAP_300_CSV);
        final ArrayDesign arrayDesign = new ArrayDesign();
        arrayDesign.addDesignFile(designFile);
        arrayDesignService.importDesign(arrayDesign);
        assertEquals("HumanHap300v2_A", arrayDesign.getName());
        assertEquals("illumina.com", arrayDesign.getLsidAuthority());
        assertEquals("PhysicalArrayDesign", arrayDesign.getLsidNamespace());
        assertEquals("HumanHap300v2_A", arrayDesign.getLsidObjectId());
        assertEquals(318237, arrayDesign.getNumberOfFeatures().intValue());
    }

    @Test
    public void testImportDesignDetails_IlluminaHumanHap300() {
        final CaArrayFile designFile = getIlluminaCaArrayFile(IlluminaArrayDesignFiles.HUMAN_HAP_300_CSV);
        final ArrayDesign arrayDesign = new ArrayDesign();
        arrayDesign.addDesignFile(designFile);
        arrayDesignService.importDesign(arrayDesign);
        arrayDesignService.importDesignDetails(arrayDesign);
        assertEquals(318237, arrayDesign.getDesignDetails().getProbes().size());
        for (final LogicalProbe probe : arrayDesign.getDesignDetails().getLogicalProbes()) {
            assertNotNull(probe);
            assertNotNull(probe.getAnnotation());
            final SNPProbeAnnotation annotation = (SNPProbeAnnotation) probe.getAnnotation();
            assertNotNull(annotation.getDbSNPId());
            assertNotNull(annotation.getDbSNPVersion());
            assertNotNull(annotation.getAlleleA());
            assertNotNull(annotation.getAlleleB());
            assertNotNull(annotation.getPhysicalPosition());
            assertEquals(probe.getName(), annotation.getDbSNPId());
            assertEquals(arrayDesign.getDesignDetails(), probe.getArrayDesignDetails());
        }
    }

    @Test
    public void testImportDesign_NimblegenTestExpression() throws Exception {
        CaArrayFile designFile = getNimblegenCaArrayFile(NimblegenArrayDesignFiles.SHORT_EXPRESSION_DESIGN);
        ArrayDesign design = new ArrayDesign();
        design.addDesignFile(designFile);
        arrayDesignService.importDesign(design);
        arrayDesignService.importDesignDetails(design);
        assertEquals("2006-08-03_HG18_60mer_expr-short", design.getName());
        assertEquals("nimblegen.com", design.getLsidAuthority());
        assertEquals("PhysicalArrayDesign", design.getLsidNamespace());
        assertEquals("2006-08-03_HG18_60mer_expr-short", design.getLsidObjectId());
        assertEquals(FileStatus.IMPORTED, design.getDesignFileSet().getStatus());
        assertEquals(99, design.getDesignDetails().getProbes().size());
        assertEquals(98, design.getDesignDetails().getLogicalProbes().size());
    }

    @Test
    public void testImportDesign_NimblegenTestCGH() throws Exception {
        CaArrayFile designFile = getNimblegenCaArrayFile(NimblegenArrayDesignFiles.SHORT_CGH_DESIGN);
        ArrayDesign design = new ArrayDesign();
        design.addDesignFile(designFile);
        arrayDesignService.importDesign(design);
        arrayDesignService.importDesignDetails(design);
        assertEquals("090210_HG18_WG_CGH_v3.1_HX3-short", design.getName());
        assertEquals("nimblegen.com", design.getLsidAuthority());
        assertEquals("PhysicalArrayDesign", design.getLsidNamespace());
        assertEquals("090210_HG18_WG_CGH_v3.1_HX3-short", design.getLsidObjectId());
        assertEquals(FileStatus.IMPORTED, design.getDesignFileSet().getStatus());
        assertEquals(4999, design.getDesignDetails().getProbes().size());
        assertEquals(41, design.getDesignDetails().getLogicalProbes().size());
    }

    @Test
    public void testImportDesign_AgilentGelm() throws Exception {
        CaArrayFile designFile = getAgilentGelmCaArrayFile(AgilentArrayDesignFiles.TEST_ACGH_XML);
        ArrayDesign design = new ArrayDesign();
        design.addDesignFile(designFile);

        arrayDesignService.importDesign(design);

        assertEquals("022522_D_F_20090107", design.getName());
        assertEquals("Agilent.com", design.getLsidAuthority());
        assertEquals("PhysicalArrayDesign", design.getLsidNamespace());
        assertEquals("022522_D_F_20090107", design.getLsidObjectId());

        arrayDesignService.importDesignDetails(design);

        assertEquals(180880, design.getNumberOfFeatures().intValue());
        assertEquals(180880, design.getDesignDetails().getFeatures().size());
        assertEquals(177071, design.getDesignDetails().getProbes().size());
        assertEquals(0, design.getDesignDetails().getLogicalProbes().size());

        assertEquals(FileStatus.IMPORTED, design.getDesignFileSet().getStatus());
        //this will be addressed with GForge issue:
        //[#29984] need to fix incorrect probe groups return in ArrayDesignServiceTest.testImportDesign_AgilentGelm() test case.
        //assertEquals("The number of probe groups in incorrect.", 2, design.getDesignDetails().getProbeGroups().size());
    }

    @Test
    public void testImportDesign_AgilentGeneExpression() throws Exception {
        CaArrayFile designFile = getAgilentGelmCaArrayFile(AgilentArrayDesignFiles.TEST_GENE_EXPRESSION_1_XML);
        ArrayDesign design = new ArrayDesign();
        design.addDesignFile(designFile);

        arrayDesignService.importDesign(design);

        assertEquals("015354_D_20061130", design.getName());
        assertEquals("Agilent.com", design.getLsidAuthority());
        assertEquals("PhysicalArrayDesign", design.getLsidNamespace());
        assertEquals("015354_D_20061130", design.getLsidObjectId());

        arrayDesignService.importDesignDetails(design);

        assertEquals(45220, design.getNumberOfFeatures().intValue());
        assertEquals(45220, design.getDesignDetails().getFeatures().size());
        assertEquals(21536, design.getDesignDetails().getProbes().size());
        assertEquals(0, design.getDesignDetails().getLogicalProbes().size());

        assertEquals(FileStatus.IMPORTED, design.getDesignFileSet().getStatus());
    }

    @Test
    public void testImportDesign_AgilentMiRNA() throws Exception {
        CaArrayFile designFile = getAgilentGelmCaArrayFile(AgilentArrayDesignFiles.TEST_MIRNA_1_XML);
        ArrayDesign design = new ArrayDesign();
        design.addDesignFile(designFile);

        arrayDesignService.importDesign(design);

        assertEquals("Human_miRNA_Microarray_3.0", design.getName());
        assertEquals("Agilent.com", design.getLsidAuthority());
        assertEquals("PhysicalArrayDesign", design.getLsidNamespace());
        assertEquals("Human_miRNA_Microarray_3.0", design.getLsidObjectId());

        arrayDesignService.importDesignDetails(design);

        assertEquals(15744, design.getNumberOfFeatures().intValue());
        assertEquals(15744, design.getDesignDetails().getFeatures().size());
        assertEquals(2735, design.getDesignDetails().getProbes().size());
        assertEquals(0, design.getDesignDetails().getLogicalProbes().size());

        assertEquals(FileStatus.IMPORTED, design.getDesignFileSet().getStatus());
    }

    @Test
    public void testImportDesign_UnsupportedVendors() {
        // The specific file doesn't matter, because the type will determine how the file is handled
        // Once these file types can be properly parsed, they should be pulled out into their own tests
        CaArrayFile designFile = getCaArrayFile(IlluminaArrayDesignFiles.HUMAN_HAP_300_CSV, FileType.AGILENT_CSV);
        ArrayDesign arrayDesign = createDesign(null, null, null, designFile);
        arrayDesignService.importDesign(arrayDesign);
        assertEquals("HumanHap300v2_A", arrayDesign.getName());
        assertEquals("Agilent.com", arrayDesign.getLsidAuthority());
        assertEquals("PhysicalArrayDesign", arrayDesign.getLsidNamespace());
        assertEquals("HumanHap300v2_A", arrayDesign.getLsidObjectId());

        designFile = getCaArrayFile(IlluminaArrayDesignFiles.HUMAN_HAP_300_CSV, FileType.IMAGENE_TPL);
        arrayDesign = createDesign(null, null, null, designFile);
        arrayDesignService.importDesign(arrayDesign);
        assertEquals("HumanHap300v2_A", arrayDesign.getName());
        assertEquals("caarray.nci.nih.gov", arrayDesign.getLsidAuthority());
        assertEquals("domain", arrayDesign.getLsidNamespace());
        assertEquals("HumanHap300v2_A", arrayDesign.getLsidObjectId());

        designFile = getCaArrayFile(IlluminaArrayDesignFiles.HUMAN_HAP_300_CSV, FileType.UCSF_SPOT_SPT);
        arrayDesign = createDesign(null, null, null, designFile);
        arrayDesignService.importDesign(arrayDesign);
        assertEquals("HumanHap300v2_A", arrayDesign.getName());
        assertEquals("caarray.nci.nih.gov", arrayDesign.getLsidAuthority());
        assertEquals("domain", arrayDesign.getLsidNamespace());
        assertEquals("HumanHap300v2_A", arrayDesign.getLsidObjectId());

        designFile = getCaArrayFile(MageTabDataFiles.SPECIFICATION_EXAMPLE_ADF, FileType.MAGE_TAB_ADF);
        arrayDesign = createDesign(null, null, null, designFile);
        arrayDesignService.importDesign(arrayDesign);
        assertEquals("a-mexp-58f_excerpt_v1.0", arrayDesign.getName());
        assertEquals("caarray.nci.nih.gov", arrayDesign.getLsidAuthority());
        assertEquals("domain", arrayDesign.getLsidNamespace());
        assertEquals("a-mexp-58f_excerpt_v1.0", arrayDesign.getLsidObjectId());
    }

    @Test
    public void testValidateDesign_AffymetrixTest3() {
        final CaArrayFile designFile = getAffymetrixCdfCaArrayFile(AffymetrixArrayDesignFiles.TEST3_CDF);
        final ValidationResult result = arrayDesignService.validateDesign(Collections.singleton(designFile));
        assertTrue(result.isValid());
    }

    @Test
    public void testValidateDesign_AffymetrixHG_U133_Plus2() {
        final CaArrayFile designFile = getAffymetrixCdfCaArrayFile(AffymetrixArrayDesignFiles.HG_U133_PLUS_2_CDF);
        final ValidationResult result = arrayDesignService.validateDesign(Collections.singleton(designFile));
        assertTrue(result.isValid());
    }

    @Test
    public void testValidateDesign_AffymetrixHuEx() {
        final CaArrayFile pgfDesignFile = getCaArrayFile(AffymetrixArrayDesignFiles.HUEX_1_0_PGF);
        final CaArrayFile clfDesignFile = getCaArrayFile(AffymetrixArrayDesignFiles.HUEX_1_0_CLF);
        final Set<CaArrayFile> designFiles = new HashSet<CaArrayFile>();
        designFiles.add(pgfDesignFile);
        designFiles.add(clfDesignFile);
        final ValidationResult result = arrayDesignService.validateDesign(designFiles);
        assertTrue(result.isValid());
    }

    @Test
    public void testValidateDesign_Genepix() {
        CaArrayFile designFile = getGenepixCaArrayFile(GenepixArrayDesignFiles.DEMO_GAL);
        ValidationResult result = arrayDesignService.validateDesign(Collections.singleton(designFile));
        assertTrue(result.toString(), result.isValid());
        designFile = getGenepixCaArrayFile(GenepixArrayDesignFiles.TWO_K_GAL);
        result = arrayDesignService.validateDesign(Collections.singleton(designFile));
        assertTrue(result.toString(), result.isValid());
        designFile = getGenepixCaArrayFile(GenepixArrayDesignFiles.MEEBO);
        result = arrayDesignService.validateDesign(Collections.singleton(designFile));
        assertTrue(result.toString(), result.isValid());
    }

    @Test
    public void testValidateDesign_IlluminaHumanWG6() {
        CaArrayFile designFile = getIlluminaCaArrayFile(IlluminaArrayDesignFiles.HUMAN_WG6_CSV);
        ValidationResult result = arrayDesignService.validateDesign(Collections.singleton(designFile));
        assertTrue(result.isValid());

        designFile = getIlluminaCaArrayFile(IlluminaArrayDesignFiles.HUMAN_WG6_CSV_INVALID_HEADERS);

        result = arrayDesignService.validateDesign(Collections.singleton(designFile));
        assertFalse(result.isValid());
        assertEquals(1, result.getFileValidationResults().size());
        assertEquals("Unable to read file", result.getFileValidationResults().get(0).getMessages().get(0).getMessage());
        assertEquals("Unable to read file", designFile.getValidationResult().getMessages().get(0).getMessage());

        designFile = getIlluminaCaArrayFile(IlluminaArrayDesignFiles.HUMAN_WG6_CSV_INVALID_CONTENT);
        result = arrayDesignService.validateDesign(Collections.singleton(designFile));
        assertFalse(result.isValid());


        designFile = getIlluminaCaArrayFile(AffymetrixArrayDataFiles.TEST3_CEL);
        result = arrayDesignService.validateDesign(Collections.singleton(designFile));
        assertFalse(result.isValid());
    }

    @Test
    public void testValidateDesign_IlluminaHumanHap300() {
        final CaArrayFile designFile = getIlluminaCaArrayFile(IlluminaArrayDesignFiles.HUMAN_HAP_300_CSV);
        final ValidationResult result = arrayDesignService.validateDesign(Collections.singleton(designFile));
        assertTrue(result.isValid());
    }

    @Test
    public void testValidateDesign_InvalidFileType() {
        CaArrayFile invalidDesignFile = getCaArrayFile(AffymetrixArrayDataFiles.TEST3_CEL, FileType.AFFYMETRIX_CEL);
        ValidationResult result = arrayDesignService.validateDesign(Collections.singleton(invalidDesignFile));
        assertFalse(result.isValid());
        invalidDesignFile = getCaArrayFile(AffymetrixArrayDataFiles.TEST3_CEL, FileType.AGILENT_CSV);
        result = arrayDesignService.validateDesign(Collections.singleton(invalidDesignFile));
        assertTrue(result.isValid());
        assertEquals(FileStatus.VALIDATED_NOT_PARSED, invalidDesignFile.getFileStatus());
        invalidDesignFile = getCaArrayFile(AffymetrixArrayDataFiles.TEST3_CEL, null);
        result = arrayDesignService.validateDesign(Collections.singleton(invalidDesignFile));
        assertFalse(result.isValid());
    }

    @Test
    public void testValidateDesign_AgilentGelm() {
        final CaArrayFile designFile = getAgilentGelmCaArrayFile(AgilentArrayDesignFiles.TEST_ACGH_XML);
        final ValidationResult result = arrayDesignService.validateDesign(Collections.singleton(designFile));
        assertTrue(result.isValid());
    }

    @Test
    public void testDuplicateArrayDesign() {
        final ArrayDesign design = createDesign(null, null, null,
                getAffymetrixCdfCaArrayFile(AffymetrixArrayDesignFiles.TEST3_CDF));
        arrayDesignService.importDesign(design);
        @SuppressWarnings("unused")
        final
        CaArrayFile designFile = getAffymetrixCdfCaArrayFile(AffymetrixArrayDesignFiles.TEST3_CDF);
        ValidationResult result = arrayDesignService.validateDesign(design);
        assertFalse(result.isValid());
        assertTrue(result.getMessages().iterator().next().getMessage().contains("has already been imported"));

        final ArrayDesign design2 = createDesign(null, null, null, getCaArrayFile(AffymetrixArrayDesignFiles.TEST3_CDF,
                FileType.UCSF_SPOT_SPT));
        arrayDesignService.importDesign(design2);
        result = arrayDesignService.validateDesign(design2);
        assertFalse(result.isValid());
        assertTrue(result.getMessages().iterator().next().getMessage().contains("design already exists with the name"));
    }

    public ArrayDesign createDesign(Organization provider, Organism organism, SortedSet<AssayType> assayTypes,
            CaArrayFile caArrayFile) {
        final ArrayDesign arrayDesign = new ArrayDesign();
        arrayDesign.setName("Dummy Design");

        if (provider == null) {
            provider = DUMMY_ORGANIZATION;
        }
        arrayDesign.setProvider(provider);

        if (organism == null) {
            organism = DUMMY_ORGANISM;
        }
        arrayDesign.setOrganism(organism);

        if (assayTypes == null) {
            assayTypes = new TreeSet<AssayType>();
            assayTypes.add(DUMMY_ASSAY_TYPE);
        }
        arrayDesign.setAssayTypes(assayTypes);

        if (caArrayFile == null) {
            caArrayFile = new CaArrayFile();
        }
        fileAccessServiceStub.save(caArrayFile);
        arrayDesign.addDesignFile(caArrayFile);

        arrayDesign.setTechnologyType(DUMMY_TERM);
        return arrayDesign;
    }


    private CaArrayFile getGenepixCaArrayFile(final File file) {
        return getCaArrayFile(file, FileType.GENEPIX_GAL);
    }

    private CaArrayFile getAffymetrixCdfCaArrayFile(final File file) {
        return getCaArrayFile(file, FileType.AFFYMETRIX_CDF);
    }

    private CaArrayFile getIlluminaCaArrayFile(final File file) {
        return getCaArrayFile(file, FileType.ILLUMINA_DESIGN_CSV);
    }

    private CaArrayFile getNimblegenCaArrayFile(File file) {
        return getCaArrayFile(file, FileType.NIMBLEGEN_NDF);
    }

    private CaArrayFile getAgilentGelmCaArrayFile(File file) {
        return getCaArrayFile(file, FileType.AGILENT_XML);
    }

    /**
     * Sets file type based on extension.
     */
    private CaArrayFile getCaArrayFile(final File file) {
        final CaArrayFile caArrayFile = fileAccessServiceStub.add(file);
        return caArrayFile;
    }

    private CaArrayFile getCaArrayFile(final File file, final FileType type) {
        final CaArrayFile caArrayFile = fileAccessServiceStub.add(file);
        caArrayFile.setFileType(type);
        return caArrayFile;
    }

    @Test
    public void testOrganizations() {
        assertEquals(0, arrayDesignService.getAllProviders().size());
        final Organization o = new Organization();
        o.setName("Foo");
        o.setProvider(true);
        caArrayDaoFactoryStub.getSearchDao().save(o);
        assertEquals(1, arrayDesignService.getAllProviders().size());
        assertEquals("Foo", arrayDesignService.getAllProviders().get(0).getName());
    }

    public static class LocalDaoFactoryStub extends DaoFactoryStub {
        private final Map<String, AbstractCaArrayEntity> lsidEntityMap = new HashMap<String, AbstractCaArrayEntity>();
        private final Map<Class, Map<Long, PersistentObject>> classKeyedOfPersistentObjectsMap = new HashMap<Class, Map<Long, PersistentObject>>();
        private static long nextId = 1;
        private static long nextFeatureId = 1;

        @Override
        public ArrayDao getArrayDao() {
            return new ArrayDaoStub() {
                @Override
                public Map<String, Long> getLogicalProbeNamesToIds(final ArrayDesign design, final List<String> names) {
                    final Map<String, Long> map = new HashMap<String, Long>();
                    for (final LogicalProbe lp : design.getDesignDetails().getLogicalProbes()) {
                        if (names.contains(lp.getName())) {
                            map.put(lp.getName(), lp.getId());
                        }
                    }
                    return map;
                }

                @Override
                public List<ArrayDesign> getArrayDesigns(final Organization provider, final Set<AssayType> assayTypes, final boolean importedOnly) {
                    final List<ArrayDesign> designs = new ArrayList<ArrayDesign>();
                    Map<Long, PersistentObject> persistentObjectsMap = classKeyedOfPersistentObjectsMap.get(ArrayDesign.class);
                    if (null != persistentObjectsMap) {
                        for (final PersistentObject entity : persistentObjectsMap.values()) {
                            final ArrayDesign design = (ArrayDesign) entity;
                            if (ObjectUtils.equals(provider, design.getProvider())
                                    && (!importedOnly
                                            || design.getDesignFileSet().getStatus() == FileStatus.IMPORTED
                                            || design.getDesignFileSet().getStatus() == FileStatus.IMPORTED_NOT_PARSED)) {
                                designs.add(design);
                            }
                        }
                    }
                    return designs;
                }

                @Override
                public List<Long> getLogicalProbeIds(final ArrayDesign design, final PageSortParams<LogicalProbe> params) {
                    final List<Long> ids = new ArrayList<Long>();
                    Map<Long, PersistentObject> persistentObjectsMap = classKeyedOfPersistentObjectsMap.get(LogicalProbe.class);
                    if (null != persistentObjectsMap) {
                        for (final Entry<Long, PersistentObject> entry : persistentObjectsMap.entrySet()) {
                            ids.add(entry.getKey());
                        }
                    }
                    Collections.sort(ids);
                    final int startIndex = params.getIndex();
                    final int toIndex = Math.min(ids.size(), startIndex + params.getPageSize());
                    if (startIndex > ids.size()) {
                        return new ArrayList<Long>();
                    }
                    return ids.subList(startIndex, toIndex);
                }

                @SuppressWarnings("deprecation")
                @Override
                public void save(final PersistentObject object) {
                    if (object instanceof AbstractCaArrayObject) {
                        final AbstractCaArrayObject caArrayObject = (AbstractCaArrayObject) object;
                        if (caArrayObject.getId() == null && !(caArrayObject instanceof Feature)) {
                            caArrayObject.setId(nextId++);
                        } else if (caArrayObject.getId() == null && caArrayObject instanceof Feature) {
                            caArrayObject.setId(nextFeatureId++);
                        }
                        if (caArrayObject instanceof AbstractCaArrayEntity) {
                            final AbstractCaArrayEntity caArrayEntity = (AbstractCaArrayEntity) object;
                            lsidEntityMap.put(caArrayEntity.getLsid(), caArrayEntity);
                        }
                        Map<Long, PersistentObject> persistentObjectsMap = classKeyedOfPersistentObjectsMap.get(caArrayObject.getClass());
                        if (null == persistentObjectsMap) {
                            persistentObjectsMap = new HashMap<Long, PersistentObject>();
                            classKeyedOfPersistentObjectsMap.put(caArrayObject.getClass(), persistentObjectsMap);
                        }
                        persistentObjectsMap.put(caArrayObject.getId(), caArrayObject);
                    }
                    // manually create reverse association automatically created by database fk relationship
                    if (object instanceof LogicalProbe) {
                        final LogicalProbe probe = (LogicalProbe) object;
                        probe.getArrayDesignDetails().getLogicalProbes().add(probe);
                    } else if (object instanceof PhysicalProbe) {
                        final PhysicalProbe probe = (PhysicalProbe) object;
                        probe.getArrayDesignDetails().getProbes().add(probe);
                    } else if (object instanceof Feature) {
                        final Feature feature = (Feature) object;
                        feature.getArrayDesignDetails().getFeatures().add(feature);
                    } else if (object instanceof ArrayDesign) {
                        final ArrayDesign ad = (ArrayDesign) object;
                        for (CaArrayFile f : ad.getDesignFiles()) {
                            if (f.getId() == null) {
                                f.setId(nextId++);                                
                            }
                        }
                    }
                }

                @Override
                public ArrayDesign getArrayDesign(final String lsidAuthority, final String lsidNamespace, final String lsidObjectId) {
                    return (ArrayDesign)
                    lsidEntityMap.get("URN:LSID:" + lsidAuthority + ":" + lsidNamespace + ":" + lsidObjectId);
                }

                @Override
                public DesignElementList getDesignElementList(final String lsidAuthority, final String lsidNamespace, final String lsidObjectId) {
                    return (DesignElementList)
                    lsidEntityMap.get("URN:LSID:" + lsidAuthority + ":" + lsidNamespace + ":" + lsidObjectId);
                }

                @Override
                public ArrayDesign getArrayDesign(final long id) {
                    Map<Long, PersistentObject> persistentObjectsMap = classKeyedOfPersistentObjectsMap.get(ArrayDesign.class);
                    ArrayDesign arrayDesign = null;
                    if (null != persistentObjectsMap) {
                        arrayDesign = (ArrayDesign) persistentObjectsMap.get(id);
                    }
                    return arrayDesign;
                }

                @Override
                public <T extends PersistentObject> List<T> queryEntityByExample(final T entityToMatch, final Order... order) {
                    final List<T> entities = new ArrayList<T>();
                    entities.add(entityToMatch);
                    return entities;
                }

                @Override
                public boolean isArrayDesignLocked(final Long id) {
                    return id.equals(2L);
                }

                @Override
                public Long getFirstFeatureId(final ArrayDesignDetails designDetails) {
                    return NumberUtils.LONG_ONE;
                }

                @Override
                public void createFeatures(final int rows, final int cols, final ArrayDesignDetails designDetails) {
                    for (int y = 0; y < rows; y++) {
                        for (int x = 0; x < cols; x++) {
                            final Feature feature = new Feature(designDetails);
                            feature.setColumn((short) x);
                            feature.setRow((short) y);
                            getArrayDao().save(feature);
                        }
                    }
                }

            };
        }

        public void clear() {
            lsidEntityMap.clear();
            classKeyedOfPersistentObjectsMap.clear();
            nextId = 0;
            nextFeatureId = 1;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public SearchDao getSearchDao() {
            return new SearchDaoStub() {
                @Override
                @SuppressWarnings("unchecked")
                public <T extends PersistentObject> T retrieve(final Class<T> entityClass, final Long entityId) {
                    Map<Long, PersistentObject> persistentObjectsMap = classKeyedOfPersistentObjectsMap.get(entityClass);
                    PersistentObject persistentObject = null;
                    if (null != persistentObjectsMap) {
                        persistentObject = persistentObjectsMap.get(entityId);
                    }
                    return (T) persistentObject;
                }

                @Override
                @SuppressWarnings("unchecked")
                public <T extends PersistentObject> T retrieveUnsecured(final Class<T> entityClass, final Long entityId) {
                    Map<Long, PersistentObject> persistentObjectsMap = classKeyedOfPersistentObjectsMap.get(entityClass);
                    PersistentObject persistentObject = null;
                    if (null != persistentObjectsMap) {
                        persistentObject = persistentObjectsMap.get(entityId);
                    }
                    return (T) persistentObject;
                }

                @Override
                public void save(final PersistentObject object) {
                    Map<Long, PersistentObject> persistentObjectsMap = classKeyedOfPersistentObjectsMap.get(object.getClass());
                    if (null == persistentObjectsMap) {
                        persistentObjectsMap = new HashMap<Long, PersistentObject>();
                        classKeyedOfPersistentObjectsMap.put(object.getClass(), persistentObjectsMap);
                    }
                    persistentObjectsMap.put(object.getId(), object);
                }
            };
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public ContactDao getContactDao() {
            return new ContactDaoStub() {
                /**
                 * {@inheritDoc}
                 */
                @Override
                public List<Organization> getAllProviders() {
                    Map<Long, PersistentObject> persistentObjectsMap = classKeyedOfPersistentObjectsMap.get(Organization.class);                 
                    final List<Organization> orgs = new ArrayList<Organization>();
                    if (null != persistentObjectsMap) {
                        CollectionUtils.addAll(orgs, persistentObjectsMap.values().iterator());
                    }
                    return orgs;
                }
            };
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public VocabularyDao getVocabularyDao() {
            return new VocabularyDaoStub() {
                private final TermSource termSource = new TermSource();
                private final List<TermSource> termSources = Collections.singletonList(termSource);
                private final Term millimeterTerm = new Term();


                /**
                 * {@InheritDoc}
                 */
                @SuppressWarnings("unchecked")
                public <T extends PersistentObject> List<T> queryEntityByExample(ExampleSearchCriteria<T> criteria,
                        Order... orders) {
                    return (List<T>) termSources;
                }

                public Term getTerm(TermSource source, String value) {
                    if (termSource.equals(source) && value.equals("mm")) {
                        return millimeterTerm;
                    }
                    return null;
                }
            };
        }
    }
}
