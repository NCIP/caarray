/**
 * The software subject to this notice and license includes both human readable
 * source code form and machine readable, binary, object code form. The caArray2-branch
 * Software was developed in conjunction with the National Cancer Institute 
 * (NCI) by NCI employees and 5AM Solutions, Inc. (5AM). To the extent 
 * government employees are authors, any rights in such works shall be subject 
 * to Title 17 of the United States Code, section 105. 
 *
 * This caArray2-branch Software License (the License) is between NCI and You. You (or 
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
 * its rights in the caArray2-branch Software to (i) use, install, access, operate, 
 * execute, copy, modify, translate, market, publicly display, publicly perform,
 * and prepare derivative works of the caArray2-branch Software; (ii) distribute and 
 * have distributed to and by third parties the caArray2-branch Software and any 
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
package gov.nih.nci.caarray.platforms;

import static org.junit.Assert.assertEquals;
import gov.nih.nci.caarray.AbstractCaarrayTest;
import gov.nih.nci.caarray.application.arraydata.ArrayDataService;
import gov.nih.nci.caarray.application.arraydata.ArrayDataServiceBean;
import gov.nih.nci.caarray.application.arraydesign.ArrayDesignModule;
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
import gov.nih.nci.caarray.dao.stub.ArrayDaoStub;
import gov.nih.nci.caarray.dao.stub.ContactDaoStub;
import gov.nih.nci.caarray.dao.stub.DaoFactoryStub;
import gov.nih.nci.caarray.dao.stub.SearchDaoStub;
import gov.nih.nci.caarray.domain.array.Array;
import gov.nih.nci.caarray.domain.array.ArrayDesign;
import gov.nih.nci.caarray.domain.array.ArrayDesignDetails;
import gov.nih.nci.caarray.domain.array.Feature;
import gov.nih.nci.caarray.domain.data.AbstractArrayData;
import gov.nih.nci.caarray.domain.data.ArrayDataType;
import gov.nih.nci.caarray.domain.data.ArrayDataTypeDescriptor;
import gov.nih.nci.caarray.domain.data.DataSet;
import gov.nih.nci.caarray.domain.data.DesignElementList;
import gov.nih.nci.caarray.domain.data.QuantitationType;
import gov.nih.nci.caarray.domain.data.QuantitationTypeDescriptor;
import gov.nih.nci.caarray.domain.file.CaArrayFile;
import gov.nih.nci.caarray.domain.file.FileStatus;
import gov.nih.nci.caarray.domain.file.FileType;
import gov.nih.nci.caarray.domain.hybridization.Hybridization;
import gov.nih.nci.caarray.domain.project.Experiment;
import gov.nih.nci.caarray.domain.project.Project;
import gov.nih.nci.caarray.magetab.MageTabDocumentSet;
import gov.nih.nci.caarray.magetab.MageTabFileSet;
import gov.nih.nci.caarray.magetab.MageTabParserImplementation;
import gov.nih.nci.caarray.magetab.io.JavaIOFileRef;
import gov.nih.nci.caarray.staticinjection.CaArrayEjbStaticInjectionModule;
import gov.nih.nci.caarray.test.data.arraydesign.AffymetrixArrayDesignFiles;
import gov.nih.nci.caarray.util.CaArrayHibernateHelperModule;
import gov.nih.nci.caarray.util.j2ee.ServiceLocatorStub;

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
import com.google.inject.util.Modules;

/**
 * @author dkokotov
 *
 */
public abstract class AbstractHandlerTest extends AbstractCaarrayTest {    
    protected ArrayDataService arrayDataService;
    protected FileAccessServiceStub fileAccessServiceStub = new FileAccessServiceStub();
    protected LocalDaoFactoryStub daoFactoryStub = new LocalDaoFactoryStub();
    protected LocalSearchDaoStub searchDaoStub = new LocalSearchDaoStub();
    
    protected long fileIdCounter = 1;

    @Before
    public void setUp() throws Exception {
        ServiceLocatorStub locatorStub = ServiceLocatorStub.registerEmptyLocator();
        locatorStub.addLookup(FileAccessService.JNDI_NAME, this.fileAccessServiceStub);
        locatorStub.addLookup(VocabularyService.JNDI_NAME, new VocabularyServiceStub());

        final Module testModule = Modules.override(new ArrayDesignModule()).with(new AbstractModule() {
            @Override
            protected void configure() {
                bind(ContactDao.class).toInstance(new ContactDaoStub());
                bind(SearchDao.class).toInstance(daoFactoryStub.getSearchDao());
                bind(ArrayDao.class).toInstance(daoFactoryStub.getArrayDao());
                
                bind(ArrayDesignService.class).to(ArrayDesignServiceBean.class);
                bind(ArrayDataService.class).to(ArrayDataServiceBean.class);
            }
        });
        
        final Injector injector = Guice.createInjector(new CaArrayEjbStaticInjectionModule(),
                new CaArrayHibernateHelperModule(), testModule);
               
        locatorStub.addLookup(ArrayDesignService.JNDI_NAME,
                (ArrayDesignServiceBean) injector.getInstance(ArrayDesignService.class));
        
        this.arrayDataService = injector.getInstance(ArrayDataService.class);
        TemporaryFileCacheLocator.setTemporaryFileCacheFactory(new TemporaryFileCacheStubFactory(this.fileAccessServiceStub));
        TemporaryFileCacheLocator.resetTemporaryFileCache();
        fileAccessServiceStub.add(AffymetrixArrayDesignFiles.TEST3_CDF);
        fileAccessServiceStub.add(AffymetrixArrayDesignFiles.TEN_K_CDF);
    }
    
    protected MageTabDocumentSet genMageTabDocSet(List<File> fl) {
        MageTabFileSet mTabFiles = new MageTabFileSet();
        for (File f : fl) {
            if (f.getName().contains(".idf")) {
                mTabFiles.addIdf(new JavaIOFileRef(f));
            } else if (f.getName().contains(".sdrf")) {
                mTabFiles.addSdrf(new JavaIOFileRef(f));
            } else {
                mTabFiles.addNativeData(new JavaIOFileRef(f));
            }
        }
        MageTabDocumentSet mTabSet = new MageTabDocumentSet(mTabFiles, MageTabParserImplementation.CAARRAY_VALIDATION_SET);
        return mTabSet;
    }

    protected void testValidFile(CaArrayFile caArrayFile, MageTabDocumentSet mTabSet) {
        assertEquals(FileStatus.UPLOADED, caArrayFile.getFileStatus());
        this.arrayDataService.validate(caArrayFile, mTabSet);
        if (FileStatus.VALIDATION_ERRORS.equals(caArrayFile.getFileStatus())) {
            System.out.println(caArrayFile.getValidationResult());
        }
        assertEquals(FileStatus.VALIDATED, caArrayFile.getFileStatus());
    }

    protected void testInvalidFile(CaArrayFile caArrayFile, MageTabDocumentSet mTabSet) {
        assertEquals(FileStatus.UPLOADED, caArrayFile.getFileStatus());
        this.arrayDataService.validate(caArrayFile, mTabSet);
        assertEquals(FileStatus.VALIDATION_ERRORS, caArrayFile.getFileStatus());
    }

    protected void checkAnnotation(CaArrayFile dataFile, int numberOfSamples) {
        Experiment experiment = dataFile.getProject().getExperiment();
        assertEquals(numberOfSamples, experiment.getSources().size());
        assertEquals(numberOfSamples, experiment.getSamples().size());
        assertEquals(numberOfSamples, experiment.getExtracts().size());
        assertEquals(numberOfSamples, experiment.getLabeledExtracts().size());
    }

    protected void checkColumnTypes(DataSet dataSet, QuantitationTypeDescriptor[] descriptors) {
        for (int i = 0; i < descriptors.length; i++) {
            checkType(descriptors[i], dataSet.getQuantitationTypes().get(i));
            checkType(descriptors[i], dataSet.getHybridizationDataList().get(0).getColumns().get(i).getQuantitationType());
        }
    }
    
    private void checkType(QuantitationTypeDescriptor typeDescriptor, QuantitationType type) {
        assertEquals(typeDescriptor.getName(), type.getName());
    }

    protected Hybridization createHybridization(File design, FileType type) {
        ArrayDesign arrayDesign = new ArrayDesign();
        CaArrayFile designFile = this.fileAccessServiceStub.add(design);
        designFile.setFileType(type);
        arrayDesign.addDesignFile(designFile);
        Array array = new Array();
        array.setDesign(arrayDesign);
        Hybridization hybridization = new Hybridization();
        hybridization.setArray(array);
        return hybridization;
    }
    
    @SuppressWarnings("deprecation")
    protected CaArrayFile getDataCaArrayFile(File file, FileType type) {
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
    protected abstract ArrayDesign createArrayDesign(String lsidAuthority, String lsidNamespace, String lsidObjectId);

    @SuppressWarnings("deprecation")
    protected ArrayDesign createArrayDesign(String lsidObjectId, int rows, int columns, File designFile) {
        ArrayDesign arrayDesign = new ArrayDesign();
        if (designFile != null) {
            arrayDesign.addDesignFile(fileAccessServiceStub.add(designFile));
        }
        ArrayDesignDetails details = new ArrayDesignDetails();
        for (short row = 0; row < rows; row++) {
            for (short column = 0; column < columns; column++) {
                Feature feature = new Feature();
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
        private Map<String, ArrayDesign> arrayDesignMap = new HashMap<String, ArrayDesign>();

        private final Map<ArrayDataTypeDescriptor, ArrayDataType> dataTypeMap =
            new HashMap<ArrayDataTypeDescriptor, ArrayDataType>();

        private final Map<QuantitationTypeDescriptor, QuantitationType> quantitationTypeMap =
            new HashMap<QuantitationTypeDescriptor, QuantitationType>();

        private final Map<Long, AbstractArrayData> fileDataMap = new HashMap<Long, AbstractArrayData>();

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

        public void addData(AbstractArrayData arrayData) {
            this.fileDataMap.put(arrayData.getDataFile().getId(), arrayData);
        }
    }
}
