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

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import edu.georgetown.pir.Organism;
import gov.nih.nci.caarray.application.AbstractServiceTest;
import gov.nih.nci.caarray.application.fileaccess.FileAccessService;
import gov.nih.nci.caarray.application.fileaccess.FileAccessServiceStub;
import gov.nih.nci.caarray.application.vocabulary.VocabularyService;
import gov.nih.nci.caarray.application.vocabulary.VocabularyServiceStub;
import gov.nih.nci.caarray.dao.ArrayDao;
import gov.nih.nci.caarray.dao.ContactDao;
import gov.nih.nci.caarray.dao.MultipartBlobDao;
import gov.nih.nci.caarray.dao.SearchDao;
import gov.nih.nci.caarray.dao.VocabularyDao;
import gov.nih.nci.caarray.dao.stub.ArrayDaoStub;
import gov.nih.nci.caarray.dao.stub.ContactDaoStub;
import gov.nih.nci.caarray.dao.stub.DaoFactoryStub;
import gov.nih.nci.caarray.dao.stub.SearchDaoStub;
import gov.nih.nci.caarray.dao.stub.VocabularyDaoStub;
import gov.nih.nci.caarray.dataStorage.DataStorageModule;
import gov.nih.nci.caarray.dataStorage.fileSystem.FileSystemStorageModule;
import gov.nih.nci.caarray.dataStorage.spi.DataStorage;
import gov.nih.nci.caarray.domain.AbstractCaArrayEntity;
import gov.nih.nci.caarray.domain.AbstractCaArrayObject;
import gov.nih.nci.caarray.domain.LSID;
import gov.nih.nci.caarray.domain.array.ArrayDesign;
import gov.nih.nci.caarray.domain.array.ArrayDesignDetails;
import gov.nih.nci.caarray.domain.array.Feature;
import gov.nih.nci.caarray.domain.array.LogicalProbe;
import gov.nih.nci.caarray.domain.array.PhysicalProbe;
import gov.nih.nci.caarray.domain.contact.Organization;
import gov.nih.nci.caarray.domain.data.DesignElementList;
import gov.nih.nci.caarray.domain.file.CaArrayFile;
import gov.nih.nci.caarray.domain.file.FileCategory;
import gov.nih.nci.caarray.domain.file.FileStatus;
import gov.nih.nci.caarray.domain.file.FileType;
import gov.nih.nci.caarray.domain.project.AssayType;
import gov.nih.nci.caarray.domain.search.ExampleSearchCriteria;
import gov.nih.nci.caarray.domain.vocabulary.Category;
import gov.nih.nci.caarray.domain.vocabulary.Term;
import gov.nih.nci.caarray.domain.vocabulary.TermSource;
import gov.nih.nci.caarray.platforms.PlatformModule;
import gov.nih.nci.caarray.platforms.SessionTransactionManager;
import gov.nih.nci.caarray.platforms.SessionTransactionManagerNoOpImpl;
import gov.nih.nci.caarray.platforms.spi.DataFileHandler;
import gov.nih.nci.caarray.platforms.spi.DesignFileHandler;
import gov.nih.nci.caarray.platforms.spi.PlatformFileReadException;
import gov.nih.nci.caarray.staticinjection.CaArrayEjbStaticInjectionModule;
import gov.nih.nci.caarray.util.CaArrayHibernateHelper;
import gov.nih.nci.caarray.util.CaArrayHibernateHelperModule;
import gov.nih.nci.caarray.util.j2ee.ServiceLocatorStub;
import gov.nih.nci.caarray.validation.ValidationResult;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.PredicateUtils;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.hibernate.Transaction;
import org.hibernate.criterion.Order;
import org.junit.After;
import org.junit.Before;

import com.fiveamsolutions.nci.commons.data.persistent.PersistentObject;
import com.fiveamsolutions.nci.commons.data.search.PageSortParams;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.multibindings.MapBinder;
import com.google.inject.multibindings.Multibinder;
import com.google.inject.name.Names;

/**
 * Test class for ArrayDesignService subsystem.
 */
@SuppressWarnings("PMD")
public class AbstractArrayDesignServiceTest extends AbstractServiceTest {
    protected ArrayDesignService arrayDesignService;
    protected final LocalDaoFactoryStub caArrayDaoFactoryStub = new LocalDaoFactoryStub();
    protected final FileAccessServiceStub fileAccessServiceStub = new FileAccessServiceStub();
    private Transaction transaction;

    protected static Organization DUMMY_ORGANIZATION = new Organization();
    protected static Organism DUMMY_ORGANISM = new Organism();
    protected static Term DUMMY_TERM = new Term();
    protected static AssayType DUMMY_ASSAY_TYPE = new AssayType("microRNA");

    protected static FileType TEST_DESIGN_TYPE = new FileType("TEST_DESIGN_TYPE", FileCategory.ARRAY_DESIGN, true);
    protected static FileType TEST_NONDESIGN_TYPE = new FileType("TEST_NONDESIGN_TYPE", FileCategory.RAW_DATA, true);
    protected static LSID TEST_LSID = new LSID("TestAuthority", "TestNamespace", "TestId");
    protected static String TEST_DESIGN_NAME = "TestName";

    @Before
    public void setUp() {
        final Module testArrayDesignModule = new AbstractModule() {
            @Override
            protected void configure() {
                bind(ContactDao.class).toInstance(
                        AbstractArrayDesignServiceTest.this.caArrayDaoFactoryStub.getContactDao());
                bind(SearchDao.class).toInstance(
                        AbstractArrayDesignServiceTest.this.caArrayDaoFactoryStub.getSearchDao());
                bind(ArrayDao.class)
                        .toInstance(AbstractArrayDesignServiceTest.this.caArrayDaoFactoryStub.getArrayDao());
                bind(VocabularyDao.class).toInstance(
                        AbstractArrayDesignServiceTest.this.caArrayDaoFactoryStub.getVocabularyDao());
                bind(MultipartBlobDao.class).toInstance(mock(MultipartBlobDao.class));

                bind(ArrayDesignService.class).to(ArrayDesignServiceBean.class);

                final Multibinder<DesignFileHandler> designFileBinder =
                        Multibinder.newSetBinder(binder(), DesignFileHandler.class);
                designFileBinder.addBinding().to(TestDesignHandler.class);

                final DataFileHandler mockDataHandler = mock(DataFileHandler.class);
                when(mockDataHandler.getSupportedTypes()).thenReturn(Collections.singleton(TEST_NONDESIGN_TYPE));
                final Multibinder<DataFileHandler> dataFileBinder =
                        Multibinder.newSetBinder(binder(), DataFileHandler.class);
                dataFileBinder.addBinding().toInstance(mockDataHandler);

                final MapBinder<String, DataStorage> mapBinder =
                        MapBinder.newMapBinder(binder(), String.class, DataStorage.class);
                mapBinder.addBinding(FileAccessServiceStub.SCHEME).toInstance(
                        AbstractArrayDesignServiceTest.this.fileAccessServiceStub);

                bind(SessionTransactionManager.class).to(SessionTransactionManagerNoOpImpl.class);

                bind(String.class).annotatedWith(Names.named(FileSystemStorageModule.BASE_DIR_KEY)).toInstance(
                        System.getProperty("java.io.tmpdir"));
                bind(String.class).annotatedWith(Names.named(DataStorageModule.FILE_DATA_ENGINE)).toInstance(
                        "file-system");
                bind(String.class).annotatedWith(Names.named(DataStorageModule.PARSED_DATA_ENGINE)).toInstance(
                        "file-system");
            }
        };

        final PlatformModule platformModule = new PlatformModule();
        configurePlatforms(platformModule);
        final Injector injector =
                Guice.createInjector(new CaArrayEjbStaticInjectionModule(), new CaArrayHibernateHelperModule(),
                        new DataStorageModule(), platformModule, testArrayDesignModule);
        final CaArrayHibernateHelper hibernateHelper = injector.getInstance(CaArrayHibernateHelper.class);
        hibernateHelper.setFiltersEnabled(false);
        this.transaction = hibernateHelper.beginTransaction();

        this.caArrayDaoFactoryStub.clear();
        this.arrayDesignService = createArrayDesignService(injector);
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

    /**
     * Strategy method allowing subclasses to add platform implementations to the configuration
     * 
     * @param platformModule
     */
    protected void configurePlatforms(PlatformModule platformModule) {
        // no-op by default
    }

    public ArrayDesignService createArrayDesignService(final Injector injector) {
        final ArrayDesignServiceBean bean = (ArrayDesignServiceBean) injector.getInstance(ArrayDesignService.class);
        final ServiceLocatorStub locatorStub = ServiceLocatorStub.registerEmptyLocator();
        locatorStub.addLookup(FileAccessService.JNDI_NAME, this.fileAccessServiceStub);
        locatorStub.addLookup(VocabularyService.JNDI_NAME, new VocabularyServiceStub());
        return bean;
    }

    @After
    public void tearDown() {
        if (this.transaction != null && this.transaction.isActive()) {
            this.transaction.rollback();
        }
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
        this.fileAccessServiceStub.save(caArrayFile);
        arrayDesign.addDesignFile(caArrayFile);

        arrayDesign.setTechnologyType(DUMMY_TERM);
        return arrayDesign;
    }

    protected CaArrayFile getCaArrayFile(final File file, final FileType type) {
        final CaArrayFile caArrayFile = this.fileAccessServiceStub.add(file);
        caArrayFile.setFileType(type);
        return caArrayFile;
    }
        
    static class LocalObjectCache {
    	
    	private final Map<Class<?>, Map<Long, PersistentObject>> map = 
    			new HashMap<Class<?>, Map<Long, PersistentObject>>();
    	
    	private Map<Long, PersistentObject> mapByClass(Class<?> type) {
    		Map<Long, PersistentObject> current = map.get(type);
    		if (current == null) {
    			current = new HashMap<Long, PersistentObject>();
    			map.put(type, current);
    		}
    		return current;
    	}
    	
    	public void add(PersistentObject entity) {
    		Map<Long, PersistentObject> mapByType = mapByClass(entity.getClass());
    		mapByType.put(entity.getId(), entity);
    	}
    	
    	public <T extends PersistentObject> T get(Class<T> type, Long id) {
    		Map<Long, PersistentObject> mapByType = mapByClass(type);
    		return (T) mapByType.get(id);
    		
    	}
    	public <T extends PersistentObject> Collection<T> getAll(Class<T> type) {
    		Map<Long, PersistentObject> mapByType = mapByClass(type);
    		return (Collection<T>) mapByType.values();
    	}
    	
    	public <T extends PersistentObject> Set<Long> getIds(Class<T> type) {
    		Map<Long, PersistentObject> mapByType = mapByClass(type);
    		return mapByType.keySet();
    	}
    }

    public static class LocalDaoFactoryStub extends DaoFactoryStub {
        private final Map<String, AbstractCaArrayEntity> lsidEntityMap = new HashMap<String, AbstractCaArrayEntity>();
        private LocalObjectCache cache = new LocalObjectCache();
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
                public List<ArrayDesign> getArrayDesigns(final Organization provider, final Set<AssayType> assayTypes,
                        final boolean importedOnly) {
                    final List<ArrayDesign> designs = new ArrayList<ArrayDesign>();
                    for (ArrayDesign design : cache.getAll(ArrayDesign.class)) {
                        if (ObjectUtils.equals(provider, design.getProvider())
                                && (!importedOnly || design.getDesignFileSet().getStatus() == FileStatus.IMPORTED || design
                                        .getDesignFileSet().getStatus() == FileStatus.IMPORTED_NOT_PARSED)) {
                            designs.add(design);
                        }
                    }
                    return designs;
                }

                @Override
                public List<Long>
                        getLogicalProbeIds(final ArrayDesign design, final PageSortParams<LogicalProbe> params) {
                    final List<Long> ids = new ArrayList<Long>();
                    ids.addAll(cache.getIds(LogicalProbe.class));
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
                public Long save(final PersistentObject object) {
                    if (object instanceof AbstractCaArrayObject) {
                        final AbstractCaArrayObject caArrayObject = (AbstractCaArrayObject) object;
                        if (caArrayObject.getId() == null && !(caArrayObject instanceof Feature)) {
                            caArrayObject.setId(nextId++);
                        } else if (caArrayObject.getId() == null && caArrayObject instanceof Feature) {
                            caArrayObject.setId(nextFeatureId++);
                        }
                        if (caArrayObject instanceof AbstractCaArrayEntity) {
                            final AbstractCaArrayEntity caArrayEntity = (AbstractCaArrayEntity) object;
                            LocalDaoFactoryStub.this.lsidEntityMap.put(caArrayEntity.getLsid(), caArrayEntity);
                        }
                        cache.add(caArrayObject);
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
                        for (final CaArrayFile f : ad.getDesignFiles()) {
                            if (f.getId() == null) {
                                f.setId(nextId++);
                            }
                        }
                    }
                    return object.getId();
                }

                @Override
                public ArrayDesign getArrayDesign(final String lsidAuthority, final String lsidNamespace,
                        final String lsidObjectId) {
                    return (ArrayDesign) LocalDaoFactoryStub.this.lsidEntityMap.get("URN:LSID:" + lsidAuthority + ":"
                            + lsidNamespace + ":" + lsidObjectId);
                }

                @Override
                public DesignElementList getDesignElementList(final String lsidAuthority, final String lsidNamespace,
                        final String lsidObjectId) {
                    return (DesignElementList) LocalDaoFactoryStub.this.lsidEntityMap.get("URN:LSID:" + lsidAuthority
                            + ":" + lsidNamespace + ":" + lsidObjectId);
                }

                @Override
                public ArrayDesign getArrayDesign(final long id) {
                	return cache.get(ArrayDesign.class, id);
                }

                @Override
                public <T extends PersistentObject> List<T> queryEntityByExample(final T entityToMatch,
                        final Order... order) {
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
            this.lsidEntityMap.clear();
            this.cache = new LocalObjectCache();
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
                	return cache.get(entityClass, entityId);
                }

                @Override
                @SuppressWarnings("unchecked")
                public <T extends PersistentObject> T
                        retrieveUnsecured(final Class<T> entityClass, final Long entityId) {
                	return cache.get(entityClass, entityId);
                }

                @Override
                public Long save(final PersistentObject object) {
                	cache.add(object);
                    return object.getId();
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
                	List<Organization> orgs = new ArrayList<Organization>();
                	orgs.addAll(cache.getAll(Organization.class));
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
                private final List<TermSource> termSources = Collections.singletonList(this.termSource);
                private final Term millimeterTerm = new Term();

                /**
                 * {@InheritDoc}
                 */
                @Override
                @SuppressWarnings("unchecked")
                public <T extends PersistentObject> List<T> queryEntityByExample(ExampleSearchCriteria<T> criteria,
                        Order... orders) {
                    return (List<T>) this.termSources;
                }

                @Override
                public Term getTerm(TermSource source, String value) {
                    if (this.termSource.equals(source) && value.equals("mm")) {
                        return this.millimeterTerm;
                    }
                    return null;
                }
            };
        }
    }

    private static class TestDesignHandler implements DesignFileHandler {
        @Override
        public boolean openFiles(Set<CaArrayFile> designFiles) throws PlatformFileReadException {
            return designFiles.size() == 1 && designFiles.iterator().next().getFileType().equals(TEST_DESIGN_TYPE);
        }

        @Override
        public void closeFiles() {
            // no-op
        }

        @Override
        public boolean parsesData() {
             return false;
        }

        @Override
        public void load(ArrayDesign arrayDesign) throws PlatformFileReadException {
            arrayDesign.setName(TEST_DESIGN_NAME);
            arrayDesign.setLsid(TEST_LSID);
        }

        @Override
        public void createDesignDetails(ArrayDesign arrayDesign) throws PlatformFileReadException {

        }

        @Override
        public void validate(ValidationResult result) throws PlatformFileReadException {

        }

        @Override
        public Set<FileType> getSupportedTypes() {
            return Collections.singleton(TEST_DESIGN_TYPE);
        }

    }
}
