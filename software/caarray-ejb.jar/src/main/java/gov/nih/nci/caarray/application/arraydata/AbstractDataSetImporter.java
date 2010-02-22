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

import gov.nih.nci.caarray.application.ServiceLocatorFactory;
import gov.nih.nci.caarray.application.arraydesign.ArrayDesignService;
import gov.nih.nci.caarray.application.fileaccess.TemporaryFileCacheLocator;
import gov.nih.nci.caarray.dao.ArrayDao;
import gov.nih.nci.caarray.dao.CaArrayDaoFactory;
import gov.nih.nci.caarray.domain.array.Array;
import gov.nih.nci.caarray.domain.array.ArrayDesign;
import gov.nih.nci.caarray.domain.data.AbstractArrayData;
import gov.nih.nci.caarray.domain.data.ArrayDataType;
import gov.nih.nci.caarray.domain.data.ArrayDataTypeDescriptor;
import gov.nih.nci.caarray.domain.data.DataSet;
import gov.nih.nci.caarray.domain.data.QuantitationType;
import gov.nih.nci.caarray.domain.data.QuantitationTypeDescriptor;
import gov.nih.nci.caarray.domain.file.CaArrayFile;
import gov.nih.nci.caarray.domain.file.FileType;
import gov.nih.nci.caarray.domain.hybridization.Hybridization;
import gov.nih.nci.caarray.domain.project.AbstractExperimentDesignNode;
import gov.nih.nci.caarray.domain.project.Experiment;
import gov.nih.nci.caarray.domain.project.ExperimentDesignNodeType;
import gov.nih.nci.caarray.domain.sample.AbstractBioMaterial;
import gov.nih.nci.caarray.domain.sample.Extract;
import gov.nih.nci.caarray.domain.sample.LabeledExtract;
import gov.nih.nci.caarray.domain.sample.Sample;
import gov.nih.nci.caarray.domain.sample.Source;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

/**
 * Handles import of array data by creating the associated <code>DataSet</code> and <code>AbstractDataColumn</code>
 * instances.
 * @param <ARRAYDATA> the class of the AbstractArrayData subclass that this importer instance handles
 */
@SuppressWarnings("PMD.CyclomaticComplexity")
abstract class AbstractDataSetImporter<ARRAYDATA extends AbstractArrayData> {
    private static final Logger LOG = Logger.getLogger(AbstractDataSetImporter.class);

    private final CaArrayDaoFactory daoFactory;
    private AbstractDataFileHandler dataFileHandler;
    private final CaArrayFile caArrayFile;
    private final Class<ARRAYDATA> arrayDataClass;
    private ARRAYDATA arrayData;
    private final DataImportOptions dataImportOptions;

    AbstractDataSetImporter(CaArrayFile caArrayFile, CaArrayDaoFactory daoFactory, Class<ARRAYDATA> arrayDataClass,
            DataImportOptions dataImportOptions) {
        this.caArrayFile = caArrayFile;
        this.daoFactory = daoFactory;
        this.arrayDataClass = arrayDataClass;
        this.dataImportOptions = dataImportOptions;
    }

    AbstractArrayData importData(boolean createAnnnotation) {
        lookupOrCreateArrayData(createAnnnotation);
        setArrayDataType();
        getArrayData().setDataSet(new DataSet());
        addHybridizationDatas();
        addColumns();
        getArrayData().getDataFile().setFileStatus(getDataFileHandler().getImportedStatus());
        if (StringUtils.isBlank(getArrayData().getName())) {
            getArrayData().setName(getCaArrayFile().getName());
        }
        return getArrayData();
    }

    private void setArrayDataType() {
        if (getArrayData().getType() == null) {
            getArrayData().setType(getArrayDataType(getDataFileHandler().getArrayDataTypeDescriptor(getFile())));
            getArrayDao().save(getArrayData());
        }
    }

    private void lookupOrCreateArrayData(boolean createAnnnotation) {
        lookupArrayData();
        if (getArrayData() == null) {
            createArrayData(createAnnnotation);
        } else {
            for (Hybridization h : getArrayData().getHybridizations()) {
                ensureArrayDesignSetForHyb(h);
            }
        }
    }

    private void ensureArrayDesignSetForHyb(Hybridization h)  {
        // if array was not set for a hybridization via mage-tab, try to look it up
        // from data file or experiment
        if (h.getArray() == null) {
            h.setArray(new Array());
        }
        if (h.getArray().getDesign() == null) {
            ArrayDesign ad = getArrayDesignFromFileOrExperiment();
            if (ad != null) {
                h.getArray().setDesign(ad);
            }
        }
    }

    abstract void lookupArrayData();

    @SuppressWarnings("PMD.CyclomaticComplexity")
    void createArrayData(boolean createAnnnotation) {
        try {
            arrayData = arrayDataClass.newInstance();
        } catch (InstantiationException e) {
            throw new IllegalStateException("Could not instantiate array data class", e);
        } catch (IllegalAccessException e) {
            throw new IllegalStateException("Could not instantiate array data class", e);
        }
        arrayData.setDataFile(getCaArrayFile());
        File dataFile = getFile();

        List<Hybridization> hybs = null;
        switch (this.dataImportOptions.getTargetAnnotationOption()) {
        case ASSOCIATE_TO_NODES:
            if (this.dataImportOptions.getTargetNodeType() == ExperimentDesignNodeType.HYBRIDIZATION) {
                hybs = this.daoFactory.getSearchDao().retrieveByIds(Hybridization.class,
                        this.dataImportOptions.getTargetNodeIds());
                break;
            }
            // intentional fallthrough - for target nodes other than hybs
        case AUTOCREATE_PER_FILE:
            hybs = lookupOrCreateHybridizations(getDataFileHandler().getHybridizationNames(dataFile),
                    createAnnnotation);
            break;
        case AUTOCREATE_SINGLE:
            hybs = Collections.singletonList(lookupOrCreateHybridization(this.dataImportOptions.getNewAnnotationName(),
                    createAnnnotation));
            break;
        default:
            throw new IllegalStateException("Unsupported annotation option: "
                    + this.dataImportOptions.getTargetAnnotationOption());
        }

        for (Hybridization hybridization : hybs) {
            associateToHybridization(hybridization);
        }
        getArrayDao().save(arrayData);
    }

    abstract void associateToHybridization(Hybridization hyb);

    abstract void addHybridizationDatas();

    private void addColumns() {
        List<QuantitationType> quantitationTypes = getQuantitationTypes();
        for (QuantitationType type : quantitationTypes) {
            getDataSet().addQuantitationType(type);
        }
    }

    final DataSet getDataSet() {
        return getArrayData().getDataSet();
    }

    final QuantitationType getQuantitationType(QuantitationTypeDescriptor descriptor) {
        return getArrayDao().getQuantitationType(descriptor);
    }

    private List<QuantitationType> getQuantitationTypes() {
        List<QuantitationType> quantitationTypes = new ArrayList<QuantitationType>();
        for (QuantitationTypeDescriptor descriptor : getDataFileHandler().getQuantitationTypeDescriptors(getFile())) {
            QuantitationType quantitationType = getQuantitationType(descriptor);
            if (quantitationType == null) {
                LOG.info("Reloading QuantitationTypes.  Descriptor was: " + descriptor);
                new TypeRegistrationManager(getArrayDao()).registerNewTypes();
                quantitationType = getQuantitationType(descriptor);
            }
            quantitationTypes.add(quantitationType);
        }
        return quantitationTypes;
    }

    File getFile() {
        return TemporaryFileCacheLocator.getTemporaryFileCache().getFile(getCaArrayFile());
    }

    final CaArrayFile getCaArrayFile() {
        return caArrayFile;
    }

    final Experiment getExperiment() {
        return getCaArrayFile().getProject().getExperiment();
    }

    private CaArrayDaoFactory getDaoFactory() {
        return daoFactory;
    }

    final ArrayDao getArrayDao() {
        return getDaoFactory().getArrayDao();
    }

    static AbstractDataSetImporter<? extends AbstractArrayData> create(CaArrayFile caArrayFile,
            CaArrayDaoFactory daoFactory, DataImportOptions dataImportOptions) {
        if (caArrayFile == null) {
            throw new IllegalArgumentException("arrayData was null");
        }
        FileType fileType = caArrayFile.getFileType();
        if (fileType.isRawArrayData()) {
            return new RawArrayDataImporter(caArrayFile, daoFactory, dataImportOptions);
        } else if (fileType.isDerivedArrayData()) {
            return new DerivedArrayDataImporter(caArrayFile, daoFactory, dataImportOptions);
        } else {
            throw new IllegalArgumentException("The file " + caArrayFile.getName()
                    + " does not contain array data. The file type is " + caArrayFile.getFileType().name());
        }
    }

    AbstractDataFileHandler getDataFileHandler() {
        if (dataFileHandler == null) {
            dataFileHandler = ArrayDataHandlerFactory.getInstance().getHandler(getCaArrayFile().getFileType());
        }
        return dataFileHandler;
    }

    ArrayDataType getArrayDataType(ArrayDataTypeDescriptor descriptor) {
        return getArrayDao().getArrayDataType(descriptor);
    }

    private Hybridization lookupHybridization(String hybridizationName) {
        Experiment experiment = getCaArrayFile().getProject().getExperiment();
        return experiment.getHybridizationByName(hybridizationName);
    }

    private Hybridization createHybridization(String hybridizationName) {
        Hybridization hybridization = new Hybridization();
        hybridization.setName(hybridizationName);
        Array array = new Array();
        ArrayDesign ad = getArrayDesignFromFileOrExperiment();
        if (ad != null) {
            array.setDesign(ad);
            hybridization.setArray(array);
        } 
        getExperiment().getHybridizations().add(hybridization);
        return hybridization;
    }

    private ArrayDesign getArrayDesignFromFileOrExperiment() {
        ArrayDesign ad = getDataFileHandler().getArrayDesign(getArrayDesignService(), getFile());
        if (ad == null) {
            ad = findArrayDesignFromExperiment(getExperiment());
        }
        return ad;
    }

    static ArrayDesign findArrayDesignFromExperiment(Experiment exp) {
        Set<ArrayDesign> experimentDesigns = exp.getArrayDesigns();
        if (experimentDesigns.size() == 1) {
            return experimentDesigns.iterator().next();
        }
        return null;
    }

    Hybridization lookupOrCreateHybridization(String hybridizationName, boolean createAnnotation) {
        Hybridization hybridization = lookupHybridization(hybridizationName);
        if (hybridization == null) {
            hybridization = createHybridization(hybridizationName);
            if (createAnnotation) {
                createAnnotation(hybridization);
            }
        }
        return hybridization;
    }

    List<Hybridization> lookupOrCreateHybridizations(List<String> hybridizationNames, boolean createAnnotation) {
        List<Hybridization> hybs = new ArrayList<Hybridization>();
        for (String hybName : hybridizationNames) {
            hybs.add(lookupOrCreateHybridization(hybName, createAnnotation));
        }
        return hybs;
    }

    void createAnnotation(Hybridization hybridization) {
        switch (this.dataImportOptions.getTargetAnnotationOption()) {
        case ASSOCIATE_TO_NODES:
            AbstractExperimentDesignNode newChainStart = hybridization;
            if (this.dataImportOptions.getTargetNodeType() != ExperimentDesignNodeType.LABELED_EXTRACT) {
                newChainStart = createAnnotationChain(hybridization, this.dataImportOptions.getTargetNodeType()
                        .getSuccessorType(), hybridization.getName());
            }
            for (Long targetId : this.dataImportOptions.getTargetNodeIds()) {
                AbstractBioMaterial target = this.daoFactory.getSearchDao().retrieve(AbstractBioMaterial.class,
                        targetId);
                target.addDirectSuccessor(newChainStart);
            }
            break;
        case AUTOCREATE_PER_FILE:
            // intentional fallthrough
        case AUTOCREATE_SINGLE:
            List<String> sampleNames = getDataFileHandler().getSampleNames(getFile(), hybridization.getName());
            for (String sampleName : sampleNames) {
                createAnnotationChain(hybridization, ExperimentDesignNodeType.SOURCE, sampleName);
            }
            break;
        default:
            throw new IllegalStateException("Unsupported annotation option: "
                    + this.dataImportOptions.getTargetAnnotationOption());
        }
    }

    /**
     * Create a new annotation chain from the given hybridization to the given starting node type. A new annotation node
     * of the given type is created, and all intermediate annotation nodes are created as well and linked.
     * @param hybridization
     * @param chainStartNodeType
     * @param newAnnotationName
     * @return
     */
    private AbstractBioMaterial createAnnotationChain(Hybridization hybridization,
            ExperimentDesignNodeType chainStartNodeType, String newAnnotationName) {
        Experiment experiment = getCaArrayFile().getProject().getExperiment();

        switch (chainStartNodeType) {
        case SOURCE:
            Source source = experiment.getSourceByName(newAnnotationName);
            if (source == null) {
                source = new Source();
                source.setName(newAnnotationName);
                experiment.getSources().add(source);
                source.setExperiment(experiment);
            }
            fillInAnnotationChain(hybridization, source, newAnnotationName);
            return source;
        case SAMPLE:
            Sample sample = experiment.getSampleByName(newAnnotationName);
            if (sample == null) {
                sample = new Sample();
                sample.setName(newAnnotationName);
                experiment.getSamples().add(sample);
                sample.setExperiment(experiment);
            }
            fillInAnnotationChain(hybridization, sample, newAnnotationName);
            return sample;
        case EXTRACT:
            Extract extract = experiment.getExtractByName(newAnnotationName);
            if (extract == null) {
                extract = new Extract();
                extract.setName(newAnnotationName);
                experiment.getExtracts().add(extract);
                extract.setExperiment(experiment);
            }
            fillInAnnotationChain(hybridization, extract, newAnnotationName);
            return extract;
        case LABELED_EXTRACT:
            LabeledExtract labeledExtract = experiment.getLabeledExtractByName(newAnnotationName);
            if (labeledExtract == null) {
                labeledExtract = new LabeledExtract();
                labeledExtract.setName(newAnnotationName);
                experiment.getLabeledExtracts().add(labeledExtract);
                labeledExtract.setExperiment(experiment);
            }
            fillInAnnotationChain(hybridization, labeledExtract, newAnnotationName);
            return labeledExtract;
        default:
            throw new IllegalStateException("Unsupported node type:" + chainStartNodeType);
        }
    }

    /**
     * Fill in the annotation chain from the given hybridization to the given target biomaterial, creating
     * the intermediate biomaterials.
     *
     * @param hybridization the hybridization for which to create the chain
     * @param target the target biomaterial to which the chain should link. A new biomaterial is created for each step
     *            in the chain between the hybridization and this target.
     * @param newAnnotationName the name to be given to each newly created biomaterial in the chain
     */
    private void fillInAnnotationChain(Hybridization hybridization, AbstractBioMaterial target,
            String newAnnotationName) {
        ExperimentDesignNodeType nextNodeType = target.getNodeType().getSuccessorType();
        if (nextNodeType == ExperimentDesignNodeType.HYBRIDIZATION) {
            target.addDirectSuccessor(hybridization);
        } else {
            AbstractBioMaterial nextNode = createAnnotationChain(hybridization, nextNodeType, newAnnotationName);
            target.addDirectSuccessor(nextNode);
        }
    }

    private ArrayDesignService getArrayDesignService() {
        return (ArrayDesignService) ServiceLocatorFactory.getLocator().lookup(ArrayDesignService.JNDI_NAME);
    }

    /**
     * @param arrayData the arrayData to set
     */
    protected void setArrayData(ARRAYDATA arrayData) {
        this.arrayData = arrayData;
    }

    /**
     * @return
     */
    protected ARRAYDATA getArrayData() {
        return this.arrayData;
    }
}
