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
package gov.nih.nci.caarray.application.arraydata;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import gov.nih.nci.caarray.application.AbstractServiceTest;
import gov.nih.nci.caarray.application.TemporaryCacheFileManager;
import gov.nih.nci.caarray.application.arraydesign.ArrayDesignService;
import gov.nih.nci.caarray.application.arraydesign.ArrayDesignServiceBean;
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
import gov.nih.nci.caarray.domain.array.ArrayDesign;
import gov.nih.nci.caarray.domain.data.AbstractArrayData;
import gov.nih.nci.caarray.domain.data.ArrayDataType;
import gov.nih.nci.caarray.domain.data.ArrayDataTypeDescriptor;
import gov.nih.nci.caarray.domain.data.DataType;
import gov.nih.nci.caarray.domain.data.DesignElementList;
import gov.nih.nci.caarray.domain.data.QuantitationType;
import gov.nih.nci.caarray.domain.data.QuantitationTypeDescriptor;
import gov.nih.nci.caarray.domain.data.RawArrayData;
import gov.nih.nci.caarray.domain.file.CaArrayFile;
import gov.nih.nci.caarray.domain.file.FileStatus;
import gov.nih.nci.caarray.domain.file.FileType;
import gov.nih.nci.caarray.domain.hybridization.Hybridization;
import gov.nih.nci.caarray.domain.project.Experiment;
import gov.nih.nci.caarray.domain.project.ExperimentDesignNodeType;
import gov.nih.nci.caarray.domain.project.Project;
import gov.nih.nci.caarray.domain.sample.Source;
import gov.nih.nci.caarray.magetab.MageTabDocumentSet;
import gov.nih.nci.caarray.magetab.MageTabParsingException;
import gov.nih.nci.caarray.platforms.FileManager;
import gov.nih.nci.caarray.platforms.LocalSessionTransactionManager;
import gov.nih.nci.caarray.platforms.PlatformModule;
import gov.nih.nci.caarray.platforms.SessionTransactionManager;
import gov.nih.nci.caarray.staticinjection.CaArrayEjbStaticInjectionModule;
import gov.nih.nci.caarray.test.data.arraydata.AffymetrixArrayDataFiles;
import gov.nih.nci.caarray.test.data.arraydata.AgilentArrayDataFiles;
import gov.nih.nci.caarray.test.data.arraydata.NimblegenArrayDataFiles;
import gov.nih.nci.caarray.test.data.arraydesign.AffymetrixArrayDesignFiles;
import gov.nih.nci.caarray.test.data.magetab.MageTabDataFiles;
import gov.nih.nci.caarray.util.CaArrayHibernateHelperModule;
import gov.nih.nci.caarray.util.j2ee.ServiceLocatorStub;
import gov.nih.nci.caarray.validation.FileValidationResult;
import gov.nih.nci.caarray.validation.InvalidDataFileException;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import com.fiveamsolutions.nci.commons.data.persistent.PersistentObject;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.multibindings.Multibinder;


/**
 * Tests the ArrayDataService subsystem
 */
@SuppressWarnings("PMD")
public class ArrayDataServiceTest extends AbstractServiceTest {
    private static final String AFFY_TEST3_LSID_OBJECT_ID = "Test3";
    private static final String HG_FOCUS_LSID_OBJECT_ID = "HG-Focus";
    private static final String AGILENT_ACGH_LSID_OBJECT_ID = "Agilent-aCGH";
    
    private static final DataImportOptions DEFAULT_IMPORT_OPTIONS = DataImportOptions.getAutoCreatePerFileOptions();

    public ArrayDataService arrayDataService;
    FileAccessServiceStub fileAccessServiceStub = new FileAccessServiceStub();
    LocalDaoFactoryStub daoFactoryStub = new LocalDaoFactoryStub();
    LocalSearchDaoStub searchDaoStub = new LocalSearchDaoStub();
    VocabularyServiceStub vocabularyServiceStub = new VocabularyServiceStub();
    
    private long fileIdCounter = 1;

    @Before
    public void setUp() throws Exception {
        ServiceLocatorStub locatorStub = ServiceLocatorStub.registerEmptyLocator();
        locatorStub.addLookup(FileAccessService.JNDI_NAME, this.fileAccessServiceStub);
        locatorStub.addLookup(VocabularyService.JNDI_NAME, vocabularyServiceStub);

        final Module testModule = new AbstractModule() {
            @Override
            protected void configure() {
                bind(ContactDao.class).toInstance(new ContactDaoStub());
                bind(SearchDao.class).toInstance(daoFactoryStub.getSearchDao());
                bind(ArrayDao.class).toInstance(daoFactoryStub.getArrayDao());
                bind(VocabularyService.class).toInstance(vocabularyServiceStub);
                bind(FileManager.class).toInstance(new TemporaryCacheFileManager());
                bind(VocabularyDao.class).toInstance(new VocabularyDaoStub());
                bind(SessionTransactionManager.class).to(LocalSessionTransactionManager.class).asEagerSingleton();
                
                Multibinder<ArrayDataTypeDescriptor> arrayDataDescriptorBinder = Multibinder.newSetBinder(binder(),
                        ArrayDataTypeDescriptor.class);       
                arrayDataDescriptorBinder.addBinding().toInstance(TestArrayDescriptor.INSTANCE);
                
                install(new PlatformModule());
                bind(ArrayDesignService.class).to(ArrayDesignServiceBean.class);
                bind(ArrayDataService.class).to(ArrayDataServiceBean.class);
           }
        };
        
        final Injector injector = Guice.createInjector(new CaArrayEjbStaticInjectionModule(), new CaArrayHibernateHelperModule(),
                testModule);
      
        final ArrayDesignServiceBean arrayDesignServiceBean =
            (ArrayDesignServiceBean) injector.getInstance(ArrayDesignService.class);

        locatorStub.addLookup(ArrayDesignService.JNDI_NAME, arrayDesignServiceBean);
        
        final ArrayDataServiceBean arrayDataServiceBean = (ArrayDataServiceBean) injector.getInstance(ArrayDataService.class);

        this.arrayDataService = arrayDataServiceBean;
        TemporaryFileCacheLocator.setTemporaryFileCacheFactory(new TemporaryFileCacheStubFactory(this.fileAccessServiceStub));
        TemporaryFileCacheLocator.resetTemporaryFileCache();
        fileAccessServiceStub.add(AffymetrixArrayDesignFiles.TEST3_CDF);
        fileAccessServiceStub.add(AffymetrixArrayDesignFiles.TEN_K_CDF);
    }
    
    @Test
    public void testInitialize() {
        this.arrayDataService.initialize();
        assertTrue(this.daoFactoryStub.dataTypeMap.containsKey(TestArrayDescriptor.INSTANCE));
        assertTrue(this.daoFactoryStub.quantitationTypeMap.keySet().contains(TestQuantitationDescriptor.INSTANCE));
    }

    @Test
    public void testImportRawAndDerivedSameName() throws InvalidDataFileException {
        // tests that imports of raw and derived data files with same base name go
        // to the same hybridization chain
        CaArrayFile focusCel = getCelCaArrayFile(AffymetrixArrayDataFiles.HG_FOCUS_CEL, HG_FOCUS_LSID_OBJECT_ID);
        CaArrayFile focusCalvinCel = getCelCaArrayFile(AffymetrixArrayDataFiles.HG_FOCUS_CALVIN_CEL, HG_FOCUS_LSID_OBJECT_ID);
        CaArrayFile focusChp = getChpCaArrayFile(AffymetrixArrayDataFiles.HG_FOCUS_CHP, HG_FOCUS_LSID_OBJECT_ID);
        CaArrayFile focusCalvinChp = getChpCaArrayFile(AffymetrixArrayDataFiles.HG_FOCUS_CALVIN_CHP, HG_FOCUS_LSID_OBJECT_ID);
        focusCalvinCel.setProject(focusCel.getProject());
        focusChp.setProject(focusCel.getProject());
        focusCalvinChp.setProject(focusCel.getProject());
        this.arrayDataService.importData(focusCel, true, DEFAULT_IMPORT_OPTIONS);
        this.arrayDataService.importData(focusCalvinCel, true, DEFAULT_IMPORT_OPTIONS);
        this.arrayDataService.importData(focusChp, true, DEFAULT_IMPORT_OPTIONS);
        this.arrayDataService.importData(focusCalvinChp, true, DEFAULT_IMPORT_OPTIONS);
        checkAnnotation(focusCel, 2);
        Experiment exp = focusCel.getProject().getExperiment();
        assertEquals(2, exp.getHybridizations().size());
        for (Hybridization h : exp.getHybridizations()) {
            assertEquals(1, h.getRawDataCollection().size());
            assertEquals(1, h.getDerivedDataCollection().size());
            if (h.getRawDataCollection().iterator().next().getDataFile().equals(focusCel)) {
                assertEquals(focusChp, h.getDerivedDataCollection().iterator().next().getDataFile());
            } else if (h.getRawDataCollection().iterator().next().getDataFile().equals(focusCalvinCel)) {
                assertEquals(focusCalvinChp, h.getDerivedDataCollection().iterator().next().getDataFile());
            } else {
                fail("Expected hybridization to be linked to either focus or calvin focus CEL");
            }
        }
    }

    @Test
    public void testImportSingleAnnotationChain() throws InvalidDataFileException {
        // tests the import of multiple files to single annotation chain
        CaArrayFile focusCel = getCelCaArrayFile(AffymetrixArrayDataFiles.HG_FOCUS_CEL, HG_FOCUS_LSID_OBJECT_ID);
        CaArrayFile focusCalvinCel = getCelCaArrayFile(AffymetrixArrayDataFiles.HG_FOCUS_CALVIN_CEL, HG_FOCUS_LSID_OBJECT_ID);
        CaArrayFile focusChp = getChpCaArrayFile(AffymetrixArrayDataFiles.HG_FOCUS_CHP, HG_FOCUS_LSID_OBJECT_ID);
        CaArrayFile focusCalvinChp = getChpCaArrayFile(AffymetrixArrayDataFiles.HG_FOCUS_CALVIN_CHP, HG_FOCUS_LSID_OBJECT_ID);
        focusCalvinCel.setProject(focusCel.getProject());
        focusChp.setProject(focusCel.getProject());
        focusCalvinChp.setProject(focusCel.getProject());
        DataImportOptions options = DataImportOptions.getAutoCreateSingleOptions("TEST_NAME");
        this.arrayDataService.importData(focusCel, true, options);
        this.arrayDataService.importData(focusCalvinCel, true, options);
        this.arrayDataService.importData(focusChp, true, options);
        this.arrayDataService.importData(focusCalvinChp, true, options);
        checkAnnotation(focusCel, 1);
        Experiment exp = focusCel.getProject().getExperiment();
        assertEquals(1, exp.getHybridizations().size());
        Hybridization h = exp.getHybridizations().iterator().next();
        assertEquals(2, h.getRawDataCollection().size());
        assertEquals(2, h.getDerivedDataCollection().size());
    }

    @SuppressWarnings("deprecation")
    @Test
    public void testImportToTargetSources() throws InvalidDataFileException {
        // tests the import of multiple files to single annotation chain
        CaArrayFile focusCel = getCelCaArrayFile(AffymetrixArrayDataFiles.HG_FOCUS_CEL, HG_FOCUS_LSID_OBJECT_ID);
        CaArrayFile focusCalvinCel = getCelCaArrayFile(AffymetrixArrayDataFiles.HG_FOCUS_CALVIN_CEL, HG_FOCUS_LSID_OBJECT_ID);
        CaArrayFile focusChp = getChpCaArrayFile(AffymetrixArrayDataFiles.HG_FOCUS_CHP, HG_FOCUS_LSID_OBJECT_ID);
        CaArrayFile focusCalvinChp = getChpCaArrayFile(AffymetrixArrayDataFiles.HG_FOCUS_CALVIN_CHP, HG_FOCUS_LSID_OBJECT_ID);
        focusCalvinCel.setProject(focusCel.getProject());
        focusChp.setProject(focusCel.getProject());
        focusCalvinChp.setProject(focusCel.getProject());

        Source targetSrc1 = new Source();
        targetSrc1.setId(1L);
        targetSrc1.setName("targetSrc1");
        targetSrc1.setExperiment(focusCel.getProject().getExperiment());
        searchDaoStub.save(targetSrc1);
        Source targetSrc2 = new Source();
        targetSrc2.setName("targetSrc2");
        targetSrc2.setId(2L);
        searchDaoStub.save(targetSrc2);
        targetSrc2.setExperiment(focusCel.getProject().getExperiment());
        focusCel.getProject().getExperiment().getSources().addAll(Arrays.asList(targetSrc1, targetSrc2));

        DataImportOptions options = DataImportOptions.getAssociateToBiomaterialsOptions(
                ExperimentDesignNodeType.SOURCE, Arrays.asList(targetSrc1.getId(), targetSrc2.getId()));
        this.arrayDataService.importData(focusCel, true, options);
        this.arrayDataService.importData(focusCalvinCel, true, options);
        this.arrayDataService.importData(focusChp, true, options);
        this.arrayDataService.importData(focusCalvinChp, true, options);
        Experiment exp = focusCel.getProject().getExperiment();
        assertEquals(2, exp.getSources().size());
        assertEquals(2, exp.getSamples().size());
        assertEquals(2, exp.getHybridizations().size());
        assertEquals(2, targetSrc1.getSamples().size());
        assertEquals(2, targetSrc2.getSamples().size());
    }
    

    @Test
    public void testCreateAnnotation() throws InvalidDataFileException {
        testExistingAnnotationNotOverwritten();
    }

    private void testExistingAnnotationNotOverwritten() throws InvalidDataFileException {
        CaArrayFile celFile = getCelCaArrayFile(AffymetrixArrayDataFiles.TEST3_CEL, AFFY_TEST3_LSID_OBJECT_ID);
        RawArrayData celData = new RawArrayData();
        Hybridization hybridization = new Hybridization();
        celData.addHybridization(hybridization);
        hybridization.addArrayData(celData);
        celData.setDataFile(celFile);
        assertNull(celData.getType());
        this.daoFactoryStub.getArrayDao().save(celData);
        this.arrayDataService.importData(celFile, true, DEFAULT_IMPORT_OPTIONS);
        assertNotNull(celData.getType());
        assertEquals(celData, this.daoFactoryStub.getArrayDao().getArrayData(celFile.getId()));
        assertEquals(hybridization, celData.getHybridizations().iterator().next());
    }

    @Test
    public void testAgilentRawTextAcghRejectedIfNoMageTab() throws InvalidDataFileException {
        CaArrayFile expFile = getAgilentRawTextCaArrayFile(AgilentArrayDataFiles.TEST_ACGH_RAW_TEXT, AGILENT_ACGH_LSID_OBJECT_ID);
        MageTabDocumentSet mTabSet = null;
        FileValidationResult validationResult = this.arrayDataService.validate(expFile, mTabSet);
        assertFalse(validationResult.isValid());
    }

    @Test
    public void testUnsupportedDataFile() throws InvalidDataFileException {
        CaArrayFile expFile = getDataCaArrayFile(MageTabDataFiles.UNSUPPORTED_DATA_EXAMPLE_EXP, FileType.AFFYMETRIX_EXP);
        this.arrayDataService.importData(expFile, true, DEFAULT_IMPORT_OPTIONS);
        assertEquals(FileStatus.IMPORTED_NOT_PARSED, expFile.getFileStatus());
    }
    
    @Test
    public void testNimblegenUnparsedDataFiles() throws InvalidDataFileException, MageTabParsingException {
        // tests that imports of nimblegen raw and derived data files are successful
        
        // test derived data...
        CaArrayFile dummyDerivedDataFile =
            getDataCaArrayFile(NimblegenArrayDataFiles.DERIVED_TXT_DATA_FILE, FileType.NIMBLEGEN_DERIVED_TXT);
        arrayDataService.importData(dummyDerivedDataFile, true, DEFAULT_IMPORT_OPTIONS);
        assertEquals("The number of hybridizations is incorrect.", 1, dummyDerivedDataFile.getProject().getExperiment().getHybridizations().size());
        assertNotNull("The number of hybridizations is incorrect.", dummyDerivedDataFile.getProject().getExperiment().getHybridizationByName("dummy_nimblegen_derived_data"));
        
        // test raw data
        CaArrayFile dummyRawDataFile =
            getDataCaArrayFile(NimblegenArrayDataFiles.RAW_TXT_DATA_FILE, FileType.NIMBLEGEN_RAW_TXT);
        arrayDataService.importData(dummyRawDataFile, true, DEFAULT_IMPORT_OPTIONS);
        assertEquals("The number of hybridizations is incorrect.", 1, dummyRawDataFile.getProject().getExperiment().getHybridizations().size());
        assertNotNull("The number of hybridizations is incorrect.", dummyRawDataFile.getProject().getExperiment().getHybridizationByName("dummy_nimblegen_raw_data"));
    }

    private void checkAnnotation(CaArrayFile dataFile, int numberOfSamples) {
        Experiment experiment = dataFile.getProject().getExperiment();
        assertEquals(numberOfSamples, experiment.getSources().size());
        assertEquals(numberOfSamples, experiment.getSamples().size());
        assertEquals(numberOfSamples, experiment.getExtracts().size());
        assertEquals(numberOfSamples, experiment.getLabeledExtracts().size());
    }

    private CaArrayFile getChpCaArrayFile(File chp, String lsidObjectId) {
        CaArrayFile caArrayFile = getDataCaArrayFile(chp, FileType.AFFYMETRIX_CHP);
        ArrayDesign arrayDesign = daoFactoryStub.getArrayDao().getArrayDesign(null, null, lsidObjectId);
        caArrayFile.getProject().getExperiment().getArrayDesigns().add(arrayDesign);
        return caArrayFile;
    }

    private CaArrayFile getCelCaArrayFile(File cel, String lsidObjectId) {
        CaArrayFile caArrayFile = getDataCaArrayFile(cel, FileType.AFFYMETRIX_CEL);
        ArrayDesign arrayDesign = daoFactoryStub.getArrayDao().getArrayDesign(null, null, lsidObjectId);
        caArrayFile.getProject().getExperiment().getArrayDesigns().add(arrayDesign);
        return caArrayFile;
    }

    private CaArrayFile getAgilentRawTextCaArrayFile(File cel, String lsidObjectId) {
        CaArrayFile caArrayFile = getDataCaArrayFile(cel, FileType.AGILENT_RAW_TXT);
        ArrayDesign arrayDesign = daoFactoryStub.getArrayDao().getArrayDesign(null, null, lsidObjectId);
        caArrayFile.getProject().getExperiment().getArrayDesigns().add(arrayDesign);
        return caArrayFile;
    }

    @SuppressWarnings("deprecation")
    public CaArrayFile getDataCaArrayFile(File file, FileType type) {
        CaArrayFile caArrayFile = this.fileAccessServiceStub.add(file);
        caArrayFile.setId(fileIdCounter++);
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
    protected ArrayDesign createArrayDesign(String lsidAuthority, String lsidNamespace, String lsidObjectId) {
        return new ArrayDesign();
    }


    private final class LocalSearchDaoStub extends SearchDaoStub {
        private Map<Long, PersistentObject> objMap = new HashMap<Long, PersistentObject>();

        @Override
        public void save(PersistentObject caArrayEntity) {
            super.save(caArrayEntity);
            objMap.put(caArrayEntity.getId(), caArrayEntity);
        }

        @SuppressWarnings("unchecked")
        @Override
        public <T extends PersistentObject> T retrieve(Class<T> entityClass, Long entityId) {
            Object candidate = objMap.get(entityId);
            if (candidate == null) {
                return null;
            } else {
                return (T) (entityClass.isInstance(candidate) ? candidate : null);
            }
        }
    }

    public final class LocalDaoFactoryStub extends DaoFactoryStub {

        private final Map<ArrayDataTypeDescriptor, ArrayDataType> dataTypeMap =
            new HashMap<ArrayDataTypeDescriptor, ArrayDataType>();

        private final Map<QuantitationTypeDescriptor, QuantitationType> quantitationTypeMap =
            new HashMap<QuantitationTypeDescriptor, QuantitationType>();

        private final Map<Long, AbstractArrayData> fileDataMap = new HashMap<Long, AbstractArrayData>();

        private Map<String, ArrayDesign> arrayDesignMap = new HashMap<String, ArrayDesign>();

        @Override
        public SearchDao getSearchDao() {
            return searchDaoStub;
        }

        @Override
        public ArrayDao getArrayDao() {
            return new ArrayDaoStub() {

                @Override
                public ArrayDesign getArrayDesign(String lsidAuthority, String lsidNamespace, String lsidObjectId) {
                    if (arrayDesignMap.containsKey(lsidObjectId)) {
                        return arrayDesignMap.get(lsidObjectId);
                    } else {
                        ArrayDesign design = createArrayDesign(lsidAuthority, lsidNamespace, lsidObjectId);
                        arrayDesignMap.put(lsidObjectId, design);
                        return design;
                    }
                }

                @Override
                public ArrayDataType getArrayDataType(ArrayDataTypeDescriptor descriptor) {
                    if (LocalDaoFactoryStub.this.dataTypeMap.containsKey(descriptor)) {
                        return LocalDaoFactoryStub.this.dataTypeMap.get(descriptor);
                    }
                    ArrayDataType arrayDataType = new ArrayDataType();
                    arrayDataType.setName(descriptor.getName());
                    arrayDataType.setVersion(descriptor.getVersion());
                    addQuantitationTypes(arrayDataType, descriptor);
                    LocalDaoFactoryStub.this.dataTypeMap.put(descriptor, arrayDataType);
                    return arrayDataType;
                }

                private void addQuantitationTypes(ArrayDataType arrayDataType, ArrayDataTypeDescriptor descriptor) {
                    for (QuantitationTypeDescriptor quantitationTypeDescriptor : descriptor.getQuantitationTypes()) {
                        arrayDataType.getQuantitationTypes().add(getQuantitationType(quantitationTypeDescriptor));
                    }
                }

                @Override
                public QuantitationType getQuantitationType(QuantitationTypeDescriptor descriptor) {
                    if (LocalDaoFactoryStub.this.quantitationTypeMap.containsKey(descriptor)) {
                        return LocalDaoFactoryStub.this.quantitationTypeMap.get(descriptor);
                    }
                    QuantitationType quantitationType = new QuantitationType();
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
                public void save(PersistentObject caArrayEntity) {
                    if (caArrayEntity instanceof AbstractArrayData) {
                        addData((AbstractArrayData) caArrayEntity);
                    }
                }

                @Override
                public DesignElementList getDesignElementList(String lsidAuthority, String lsidNamespace, String lsidObjectId) {
                    DesignElementList list = new DesignElementList();
                    list.setLsidForEntity(lsidAuthority + ":" + lsidNamespace + ":" + lsidObjectId);
                    return list;
                }

            };
        }

        void addData(AbstractArrayData arrayData) {
            this.fileDataMap.put(arrayData.getDataFile().getId(), arrayData);
        }
    }

    /**
     * Descriptor for data formats that aren't supported.
     */
    private static class TestArrayDescriptor implements ArrayDataTypeDescriptor {        
        private static ArrayDataTypeDescriptor INSTANCE = new TestArrayDescriptor();
        
        /**
         * {@inheritDoc}
         */
        public String getName() {
            return "Test";
        }

        /**
         * {@inheritDoc}
         */
        public List<QuantitationTypeDescriptor> getQuantitationTypes() {
            return Collections.singletonList(TestQuantitationDescriptor.INSTANCE);
        }

        /**
         * {@inheritDoc}
         */
        public String getVersion() {
            return null;
        }

        /**
         * {@inheritDoc}
         */
        public boolean isEquivalent(ArrayDataType arrayDataType) {
            return this.equals("Name".equals(arrayDataType.getName()));
        }
    }
    
    private static class TestQuantitationDescriptor implements QuantitationTypeDescriptor {
        private static QuantitationTypeDescriptor INSTANCE = new TestQuantitationDescriptor();
        
        public DataType getDataType() {
            return DataType.BOOLEAN;
        }
        
        public String getName() {
            return "TestQT";
        }
    }
}
