//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.platforms;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import gov.nih.nci.caarray.AbstractCaarrayTest;
import gov.nih.nci.caarray.application.arraydata.ArrayDataService;
import gov.nih.nci.caarray.application.arraydata.ArrayDataServiceBean;
import gov.nih.nci.caarray.application.arraydesign.ArrayDesignService;
import gov.nih.nci.caarray.application.arraydesign.ArrayDesignServiceBean;
import gov.nih.nci.caarray.application.fileaccess.FileAccessService;
import gov.nih.nci.caarray.application.fileaccess.FileAccessServiceStub;
import gov.nih.nci.caarray.application.vocabulary.VocabularyService;
import gov.nih.nci.caarray.application.vocabulary.VocabularyServiceStub;
import gov.nih.nci.caarray.dao.ArrayDao;
import gov.nih.nci.caarray.dao.ContactDao;
import gov.nih.nci.caarray.dao.MultipartBlobDao;
import gov.nih.nci.caarray.dao.SearchDao;
import gov.nih.nci.caarray.dao.stub.ArrayDaoStub;
import gov.nih.nci.caarray.dao.stub.ContactDaoStub;
import gov.nih.nci.caarray.dao.stub.DaoFactoryStub;
import gov.nih.nci.caarray.dao.stub.SearchDaoStub;
import gov.nih.nci.caarray.dataStorage.DataStorageModule;
import gov.nih.nci.caarray.dataStorage.fileSystem.FileSystemStorageModule;
import gov.nih.nci.caarray.dataStorage.spi.DataStorage;
import gov.nih.nci.caarray.domain.array.Array;
import gov.nih.nci.caarray.domain.array.ArrayDesign;
import gov.nih.nci.caarray.domain.array.ArrayDesignDetails;
import gov.nih.nci.caarray.domain.array.Feature;
import gov.nih.nci.caarray.domain.data.AbstractArrayData;
import gov.nih.nci.caarray.domain.data.ArrayDataType;
import gov.nih.nci.caarray.domain.data.ArrayDataTypeDescriptor;
import gov.nih.nci.caarray.domain.data.DataSet;
import gov.nih.nci.caarray.domain.data.DesignElementList;
import gov.nih.nci.caarray.domain.data.DesignElementReference;
import gov.nih.nci.caarray.domain.data.QuantitationType;
import gov.nih.nci.caarray.domain.data.QuantitationTypeDescriptor;
import gov.nih.nci.caarray.domain.file.CaArrayFile;
import gov.nih.nci.caarray.domain.file.FileStatus;
import gov.nih.nci.caarray.domain.file.FileType;
import gov.nih.nci.caarray.domain.hybridization.Hybridization;
import gov.nih.nci.caarray.domain.project.Experiment;
import gov.nih.nci.caarray.domain.project.Project;
import gov.nih.nci.caarray.jobqueue.JobQueue;
import gov.nih.nci.caarray.magetab.MageTabDocumentSet;
import gov.nih.nci.caarray.magetab.MageTabFileSet;
import gov.nih.nci.caarray.magetab.MageTabParserImplementation;
import gov.nih.nci.caarray.magetab.io.JavaIOFileRef;
import gov.nih.nci.caarray.staticinjection.CaArrayEjbStaticInjectionModule;
import gov.nih.nci.caarray.test.data.arraydesign.AffymetrixArrayDesignFiles;
import gov.nih.nci.caarray.util.CaArrayHibernateHelperModule;
import gov.nih.nci.caarray.util.UsernameHolder;
import gov.nih.nci.caarray.util.j2ee.ServiceLocatorStub;
import gov.nih.nci.caarray.validation.FileValidationResult;
import gov.nih.nci.caarray.validation.ValidationMessage;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;

import com.fiveamsolutions.nci.commons.data.persistent.PersistentObject;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.multibindings.MapBinder;
import com.google.inject.name.Names;

/**
 * @author dkokotov
 * 
 */
public abstract class AbstractHandlerTest extends AbstractCaarrayTest {
    protected ArrayDataService arrayDataService;
    protected FileAccessServiceStub fileAccessServiceStub = new FileAccessServiceStub();
    protected LocalDaoFactoryStub daoFactoryStub = new LocalDaoFactoryStub();
    protected LocalSearchDaoStub searchDaoStub = new LocalSearchDaoStub();
    public static final String PROBE_WAS_NOT_FOUND_IN_ARRAY_DESIGN_FRAGMENT = "' was not found in array design '";
    protected long fileIdCounter = 1;

    @Before
    public void setUp() throws Exception {
        final ServiceLocatorStub locatorStub = ServiceLocatorStub.registerEmptyLocator();
        locatorStub.addLookup(FileAccessService.JNDI_NAME, this.fileAccessServiceStub);
        locatorStub.addLookup(VocabularyService.JNDI_NAME, new VocabularyServiceStub());

        final Module testModule = new AbstractModule() {
            @Override
            protected void configure() {
                bind(UsernameHolder.class).toInstance(mock(UsernameHolder.class));
                bind(JobQueue.class).toInstance(mock(JobQueue.class));
                bind(ContactDao.class).toInstance(new ContactDaoStub());
                bind(SearchDao.class).toInstance(AbstractHandlerTest.this.daoFactoryStub.getSearchDao());
                bind(ArrayDao.class).toInstance(AbstractHandlerTest.this.daoFactoryStub.getArrayDao());

                bind(ArrayDesignService.class).to(ArrayDesignServiceBean.class);
                bind(ArrayDataService.class).to(ArrayDataServiceBean.class);

                bind(MultipartBlobDao.class).toInstance(mock(MultipartBlobDao.class));

                final MapBinder<String, DataStorage> mapBinder =
                        MapBinder.newMapBinder(binder(), String.class, DataStorage.class);
                mapBinder.addBinding(FileAccessServiceStub.SCHEME).toInstance(
                        AbstractHandlerTest.this.fileAccessServiceStub);

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
                        new DataStorageModule(), platformModule, testModule);

        locatorStub.addLookup(ArrayDesignService.JNDI_NAME, injector.getInstance(ArrayDesignService.class));

        this.arrayDataService = injector.getInstance(ArrayDataService.class);
        this.fileAccessServiceStub.add(AffymetrixArrayDesignFiles.TEST3_CDF);
        this.fileAccessServiceStub.add(AffymetrixArrayDesignFiles.TEN_K_CDF);
    }

    /**
     * Strategy method allowing subclasses to add platform implementations to the configuration
     * 
     * @param platformModule
     */
    protected void configurePlatforms(PlatformModule platformModule) {
        // no-op by default
    }

    protected MageTabDocumentSet genMageTabDocSet(List<File> fl) {
        final MageTabFileSet mTabFiles = new MageTabFileSet();
        for (final File f : fl) {
            if (f.getName().contains(".idf")) {
                mTabFiles.addIdf(new JavaIOFileRef(f));
            } else if (f.getName().contains(".sdrf")) {
                mTabFiles.addSdrf(new JavaIOFileRef(f));
            } else {
                mTabFiles.addNativeData(new JavaIOFileRef(f));
            }
        }
        final MageTabDocumentSet mTabSet =
                new MageTabDocumentSet(mTabFiles, MageTabParserImplementation.CAARRAY_VALIDATION_SET);
        return mTabSet;
    }

    protected void testValidFile(CaArrayFile caArrayFile, MageTabDocumentSet mTabSet,
            boolean probeNameValidationErrorsAreAcceptable) {
        assertEquals(FileStatus.UPLOADED, caArrayFile.getFileStatus());
        this.arrayDataService.validate(caArrayFile, mTabSet, false);
        if (FileStatus.VALIDATION_ERRORS.equals(caArrayFile.getFileStatus())) {
            System.out.println(caArrayFile.getValidationResult());
        }
        if (probeNameValidationErrorsAreAcceptable) {
            assertTrue(
                    "The file is only allowed to have probe name validation errors, but other errors were found.",
                    onlyAcceptableValidationErrorsArePresent(caArrayFile.getValidationResult(),
                            new String[] {" was not found in array design '" }));
        } else {
            assertEquals(FileStatus.VALIDATED, caArrayFile.getFileStatus());
        }
    }

    protected void testInvalidFile(CaArrayFile caArrayFile, MageTabDocumentSet mTabSet,
            final String[] acceptableErrorMessageFragments) {
        assertEquals(FileStatus.UPLOADED, caArrayFile.getFileStatus());
        this.arrayDataService.validate(caArrayFile, mTabSet, false);
        assertEquals(FileStatus.VALIDATION_ERRORS, caArrayFile.getFileStatus());
        assertTrue(
                "The file should should have additional validation errors.",
                !onlyAcceptableValidationErrorsArePresent(caArrayFile.getValidationResult(),
                        acceptableErrorMessageFragments));
    }

    private static boolean onlyAcceptableValidationErrorsArePresent(final FileValidationResult fileValidationResult,
            String[] acceptableErrorMessageFragments) {
        boolean onlyAcceptableValidationErrorsArePresent = true;
        for (final ValidationMessage validationMessage : fileValidationResult.getMessages(ValidationMessage.Type.ERROR)) {
            if (null == acceptableErrorMessageFragments || 0 == acceptableErrorMessageFragments.length) {
                onlyAcceptableValidationErrorsArePresent = false;
                break;
            } else {
                for (final String acceptableErrorMessageFragment : acceptableErrorMessageFragments) {
                    if (!validationMessage.getMessage().contains(acceptableErrorMessageFragment)) {
                        onlyAcceptableValidationErrorsArePresent = false;
                        break;
                    }
                }
            }
        }
        return onlyAcceptableValidationErrorsArePresent;
    }

    protected void checkAnnotation(CaArrayFile dataFile, int numberOfSamples) {
        final Experiment experiment = dataFile.getProject().getExperiment();
        assertEquals(numberOfSamples, experiment.getSources().size());
        assertEquals(numberOfSamples, experiment.getSamples().size());
        assertEquals(numberOfSamples, experiment.getExtracts().size());
        assertEquals(numberOfSamples, experiment.getLabeledExtracts().size());
    }

    protected void checkColumnTypes(DataSet dataSet, QuantitationTypeDescriptor[] descriptors) {
        for (int i = 0; i < descriptors.length; i++) {
            checkType(descriptors[i], dataSet.getQuantitationTypes().get(i));
            checkType(descriptors[i], dataSet.getHybridizationDataList().get(0).getColumns().get(i)
                    .getQuantitationType());
        }
    }

    private void checkType(QuantitationTypeDescriptor typeDescriptor, QuantitationType type) {
        assertEquals(typeDescriptor.getName(), type.getName());
    }

    protected Hybridization createHybridization(File design, FileType type) {
        final ArrayDesign arrayDesign = new ArrayDesign();
        final CaArrayFile designFile = this.fileAccessServiceStub.add(design);
        designFile.setFileType(type);
        arrayDesign.addDesignFile(designFile);
        final Array array = new Array();
        array.setDesign(arrayDesign);
        final Hybridization hybridization = new Hybridization();
        hybridization.setArray(array);
        return hybridization;
    }

    @SuppressWarnings("deprecation")
    protected CaArrayFile getDataCaArrayFile(File file, FileType type) {
        final CaArrayFile caArrayFile = this.fileAccessServiceStub.add(file);
        caArrayFile.setId(this.fileIdCounter++);
        caArrayFile.setFileType(type);
        caArrayFile.setProject(new Project());
        caArrayFile.getProject().setExperiment(new Experiment());
        return caArrayFile;
    }

    /**
     * Subclasses should override to create array designs as needed
     * 
     * @param lsidAuthority
     * @param lsidNamespace
     * @param lsidObjectId
     * @return
     */
    protected abstract ArrayDesign createArrayDesign(String lsidAuthority, String lsidNamespace, String lsidObjectId);

    @SuppressWarnings("deprecation")
    protected ArrayDesign createArrayDesign(String lsidObjectId, int rows, int columns, File designFile) {
        final ArrayDesign arrayDesign = new ArrayDesign();
        CaArrayFile f;
        if (designFile != null) {
            f = this.fileAccessServiceStub.add(designFile);
        } else {
            f = new CaArrayFile();
        }
        f.setFileStatus(FileStatus.IMPORTED);
        arrayDesign.addDesignFile(f);

        final ArrayDesignDetails details = new ArrayDesignDetails();
        for (short row = 0; row < rows; row++) {
            for (short column = 0; column < columns; column++) {
                final Feature feature = new Feature();
                feature.setRow(row);
                feature.setColumn(column);
                details.getFeatures().add(feature);
            }
        }
        arrayDesign.setNumberOfFeatures(details.getFeatures().size());
        arrayDesign.setDesignDetails(details);

        return arrayDesign;
    }

    private final class LocalSearchDaoStub extends SearchDaoStub {
        private final Map<Long, PersistentObject> objMap = new HashMap<Long, PersistentObject>();

        @Override
        public Long save(PersistentObject caArrayEntity) {
            super.save(caArrayEntity);
            this.objMap.put(caArrayEntity.getId(), caArrayEntity);
            return caArrayEntity.getId();
        }

        @SuppressWarnings("unchecked")
        @Override
        public <T extends PersistentObject> T retrieve(Class<T> entityClass, Long entityId) {
            final Object candidate = this.objMap.get(entityId);
            if (candidate == null) {
                return null;
            } else {
                return (T) (entityClass.isInstance(candidate) ? candidate : null);
            }
        }
    }

    public final class LocalDaoFactoryStub extends DaoFactoryStub {
        private final Map<String, ArrayDesign> arrayDesignMap = new HashMap<String, ArrayDesign>();

        private final Map<ArrayDataTypeDescriptor, ArrayDataType> dataTypeMap =
                new HashMap<ArrayDataTypeDescriptor, ArrayDataType>();

        private final Map<QuantitationTypeDescriptor, QuantitationType> quantitationTypeMap =
                new HashMap<QuantitationTypeDescriptor, QuantitationType>();

        private final Map<Long, AbstractArrayData> fileDataMap = new HashMap<Long, AbstractArrayData>();

        @Override
        public SearchDao getSearchDao() {
            return AbstractHandlerTest.this.searchDaoStub;
        }

        @Override
        public ArrayDao getArrayDao() {
            return new ArrayDaoStub() {

                @Override
                public ArrayDesign getArrayDesign(String lsidAuthority, String lsidNamespace, String lsidObjectId) {
                    if (LocalDaoFactoryStub.this.arrayDesignMap.containsKey(lsidObjectId)) {
                        return LocalDaoFactoryStub.this.arrayDesignMap.get(lsidObjectId);
                    } else {
                        final ArrayDesign design = createArrayDesign(lsidAuthority, lsidNamespace, lsidObjectId);
                        LocalDaoFactoryStub.this.arrayDesignMap.put(lsidObjectId, design);
                        return design;
                    }
                }

                @Override
                public ArrayDataType getArrayDataType(ArrayDataTypeDescriptor descriptor) {
                    if (LocalDaoFactoryStub.this.dataTypeMap.containsKey(descriptor)) {
                        return LocalDaoFactoryStub.this.dataTypeMap.get(descriptor);
                    }
                    final ArrayDataType arrayDataType = new ArrayDataType();
                    arrayDataType.setName(descriptor.getName());
                    arrayDataType.setVersion(descriptor.getVersion());
                    addQuantitationTypes(arrayDataType, descriptor);
                    LocalDaoFactoryStub.this.dataTypeMap.put(descriptor, arrayDataType);
                    return arrayDataType;
                }

                private void addQuantitationTypes(ArrayDataType arrayDataType, ArrayDataTypeDescriptor descriptor) {
                    for (final QuantitationTypeDescriptor quantitationTypeDescriptor : descriptor
                            .getQuantitationTypes()) {
                        arrayDataType.getQuantitationTypes().add(getQuantitationType(quantitationTypeDescriptor));
                    }
                }

                @Override
                public QuantitationType getQuantitationType(QuantitationTypeDescriptor descriptor) {
                    if (LocalDaoFactoryStub.this.quantitationTypeMap.containsKey(descriptor)) {
                        return LocalDaoFactoryStub.this.quantitationTypeMap.get(descriptor);
                    }
                    final QuantitationType quantitationType = new QuantitationType();
                    quantitationType.setName(descriptor.getName());
                    quantitationType.setTypeClass(descriptor.getDataType().getTypeClass());
                    LocalDaoFactoryStub.this.quantitationTypeMap.put(descriptor, quantitationType);
                    return quantitationType;
                }

                @Override
                public AbstractArrayData getArrayData(Long fileId) {
                    return LocalDaoFactoryStub.this.fileDataMap.get(fileId);
                }

                @Override
                public Long save(PersistentObject caArrayEntity) {
                    if (caArrayEntity instanceof AbstractArrayData) {
                        addData((AbstractArrayData) caArrayEntity);
                    } else if (caArrayEntity instanceof DesignElementReference) {
                        final DesignElementReference reference = (DesignElementReference) caArrayEntity;
                        final DesignElementList designElementList = reference.getDesignElementList();
                        designElementList.getDesignElementReferences().add(reference);
                        designElementList.getDesignElements().add(reference.getDesignElement());
                    }
                    return caArrayEntity.getId();
                }

                @Override
                public DesignElementList getDesignElementList(String lsidAuthority, String lsidNamespace,
                        String lsidObjectId) {
                    final DesignElementList list = new DesignElementList();
                    list.setLsidForEntity(lsidAuthority + ":" + lsidNamespace + ":" + lsidObjectId);
                    return list;
                }

            };
        }

        public void addData(AbstractArrayData arrayData) {
            this.fileDataMap.put(arrayData.getDataFile().getId(), arrayData);
        }
    }
}
