//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.application.arraydata;

import gov.nih.nci.caarray.application.arraydesign.ArrayDesignService;
import gov.nih.nci.caarray.application.fileaccess.TemporaryFileCacheLocator;
import gov.nih.nci.caarray.dao.ArrayDao;
import gov.nih.nci.caarray.dao.CaArrayDaoFactory;
import gov.nih.nci.caarray.domain.array.Array;
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
import gov.nih.nci.caarray.util.j2ee.ServiceLocatorFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
        Experiment experiment = getCaArrayFile().getProject().getExperiment();
        Hybridization hybridization = new Hybridization();
        hybridization.setName(hybridizationName);
        Array array = new Array();
        array.setDesign(getDataFileHandler().getArrayDesign(getArrayDesignService(), getFile()));
        hybridization.setArray(array);
        experiment.getHybridizations().add(hybridization);
        return hybridization;
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
            Source source = new Source();
            source.setName(newAnnotationName);
            experiment.getSources().add(source);
            fillInAnnotationChain(hybridization, source, newAnnotationName);
            return source;
        case SAMPLE:
            Sample sample = new Sample();
            sample.setName(newAnnotationName);
            experiment.getSamples().add(sample);
            fillInAnnotationChain(hybridization, sample, newAnnotationName);
            return sample;
        case EXTRACT:
            Extract extract = new Extract();
            extract.setName(newAnnotationName);
            experiment.getExtracts().add(extract);
            fillInAnnotationChain(hybridization, extract, newAnnotationName);
            return extract;
        case LABELED_EXTRACT:
            LabeledExtract labeledExtract = new LabeledExtract();
            labeledExtract.setName(newAnnotationName);
            experiment.getLabeledExtracts().add(labeledExtract);
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
