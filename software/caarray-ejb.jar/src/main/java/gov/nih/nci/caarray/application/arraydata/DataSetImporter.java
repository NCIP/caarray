//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.application.arraydata;

import gov.nih.nci.caarray.dao.ArrayDao;
import gov.nih.nci.caarray.dao.SearchDao;
import gov.nih.nci.caarray.domain.array.Array;
import gov.nih.nci.caarray.domain.array.ArrayDesign;
import gov.nih.nci.caarray.domain.data.AbstractArrayData;
import gov.nih.nci.caarray.domain.data.DataSet;
import gov.nih.nci.caarray.domain.data.DerivedArrayData;
import gov.nih.nci.caarray.domain.data.QuantitationType;
import gov.nih.nci.caarray.domain.data.QuantitationTypeDescriptor;
import gov.nih.nci.caarray.domain.data.RawArrayData;
import gov.nih.nci.caarray.domain.file.CaArrayFile;
import gov.nih.nci.caarray.domain.file.FileStatus;
import gov.nih.nci.caarray.domain.hybridization.Hybridization;
import gov.nih.nci.caarray.domain.project.AbstractExperimentDesignNode;
import gov.nih.nci.caarray.domain.project.Experiment;
import gov.nih.nci.caarray.domain.project.ExperimentDesignNodeType;
import gov.nih.nci.caarray.domain.sample.AbstractBioMaterial;
import gov.nih.nci.caarray.domain.sample.Extract;
import gov.nih.nci.caarray.domain.sample.LabeledExtract;
import gov.nih.nci.caarray.domain.sample.Sample;
import gov.nih.nci.caarray.domain.sample.Source;
import gov.nih.nci.caarray.magetab.MageTabDocumentSet;
import gov.nih.nci.caarray.platforms.spi.DataFileHandler;
import gov.nih.nci.caarray.platforms.spi.PlatformFileReadException;
import gov.nih.nci.caarray.platforms.unparsed.FallbackUnparsedDataHandler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import com.google.inject.Inject;
import com.google.inject.Provider;

/**
 * Helper class for creating and initializing ArrayDatas associated with a data file, and potentially auto-generating
 * related annotations.
 * 
 * This does not actually load the AbstractDataColumns with array data values; this is done by the DataSetLoader
 * 
 * @author dkokotov
 */
@SuppressWarnings({"PMD.CyclomaticComplexity", "PMD.TooManyMethods" })
class DataSetImporter extends AbstractArrayDataUtility {
    private final SearchDao searchDao;

    @Inject
    DataSetImporter(ArrayDao arrayDao, SearchDao searchDao, Set<DataFileHandler> handlers,
            Provider<FallbackUnparsedDataHandler> fallbackHandlerProvider) {
        super(arrayDao, handlers, fallbackHandlerProvider);
        this.searchDao = searchDao;
    }

    AbstractArrayData importData(CaArrayFile caArrayFile, DataImportOptions dataImportOptions, 
            boolean createAnnnotation, MageTabDocumentSet mTabSet) {
        DataFileHandler handler = null;
        try {
            handler = findAndSetupHandler(caArrayFile, mTabSet);
            assert handler != null : "findAndSetupHandler must never return null";
            final Helper helper = new Helper(caArrayFile, dataImportOptions, handler);
            return helper.importData(createAnnnotation);
        } catch (final PlatformFileReadException e) {
            throw new IllegalArgumentException("Error obtaining a handler for file " + caArrayFile.getName(), e);
        } finally {
            if (handler != null) {
                handler.closeFiles();
            }
        }
    }

    /**
     * Helper class for the import, just so that the various parameters can be held as instance variables.
     * 
     * @author dkokotov
     */
    private final class Helper {
        private final CaArrayFile caArrayFile;
        private AbstractArrayData arrayData;
        private DataImportOptions dataImportOptions;
        private final DataFileHandler handler;

        Helper(CaArrayFile caArrayFile, DataImportOptions dataImportOptions, DataFileHandler handler) {
            if (caArrayFile == null) {
                throw new IllegalArgumentException("arrayData was null");
            }
            if (!caArrayFile.getFileType().isRawArrayData() && !caArrayFile.getFileType().isDerivedArrayData()) {
                throw new IllegalArgumentException("The file " + caArrayFile.getName()
                        + " does not contain array data. The file type is " + caArrayFile.getType());
            }
            this.dataImportOptions = dataImportOptions;
            this.caArrayFile = caArrayFile;
            this.handler = handler;
        }

        AbstractArrayData importData(boolean createAnnnotation) throws PlatformFileReadException {
            if (dataImportOptions != null) {
                caArrayFile.getProject().setImportDescription(dataImportOptions.getImportDescription());
            }
            lookupOrCreateArrayData(createAnnnotation);
            updateLastModifiedData();
            if (StringUtils.isBlank(this.arrayData.getName())) {
                this.arrayData.setName(this.caArrayFile.getName());
            }

            setArrayDataType();
            this.arrayData.setDataSet(new DataSet());
            addHybridizationDatas();
            addColumns();

            this.arrayData.getDataFile().setFileStatus(
                    this.handler.parsesData() ? FileStatus.IMPORTED : FileStatus.IMPORTED_NOT_PARSED);
            
            return this.arrayData;
        }

        private void setArrayDataType() {
            this.arrayData.setType(getArrayDao().getArrayDataType(this.handler.getArrayDataTypeDescriptor()));
            getArrayDao().save(this.arrayData);
        }

        private void lookupOrCreateArrayData(boolean createAnnnotation) throws PlatformFileReadException {
            this.arrayData = getArrayDao().getArrayData(this.caArrayFile.getId());
            if (this.arrayData == null) {
                createArrayData(createAnnnotation);
            } else {
                if (this.arrayData.getDataSet() != null) {
                    getArrayDao().remove(this.arrayData.getDataSet());
                    this.arrayData.setDataSet(null);
                    getArrayDao().save(this.arrayData);
                }
                for (final Hybridization h : this.arrayData.getHybridizations()) {
                    ensureArrayDesignSetForHyb(h);
                }
            }
        }

        private void ensureArrayDesignSetForHyb(Hybridization h) throws PlatformFileReadException {
            // if array was not set for a hybridization via mage-tab, try to look it up
            // from data file or experiment
            if (h.getArray() == null) {
                h.setArray(new Array());
            }
            if (h.getArray().getDesign() == null) {
                final ArrayDesign ad = getArrayDesign(this.caArrayFile, this.handler);
                if (ad != null) {
                    h.getArray().setDesign(ad);
                }
            }
        }

        @SuppressWarnings("PMD.CyclomaticComplexity")
        private void createArrayData(boolean createAnnnotation) throws PlatformFileReadException {
            this.arrayData =
                    this.caArrayFile.getFileType().isRawArrayData() ? new RawArrayData() : new DerivedArrayData();
            this.arrayData.setDataFile(this.caArrayFile);

            List<Hybridization> hybs = null;
            if (dataImportOptions.getTargetAnnotationOption() == null) {
                // Default import option is autocreate per file.
                this.dataImportOptions = DataImportOptions.getAutoCreatePerFileOptions();
            }
            switch (this.dataImportOptions.getTargetAnnotationOption()) {
            case ASSOCIATE_TO_NODES:
                if (this.dataImportOptions.getTargetNodeType() == ExperimentDesignNodeType.HYBRIDIZATION) {
                    hybs =
                            DataSetImporter.this.searchDao.retrieveByIds(Hybridization.class,
                                    this.dataImportOptions.getTargetNodeIds());
                    break;
                }
                // intentional fallthrough - for target nodes other than hybs
            case AUTOCREATE_PER_FILE:
                hybs = lookupOrCreateHybridizations(this.handler.getHybridizationNames(), createAnnnotation);
                break;
            case AUTOCREATE_SINGLE:
                hybs =
                        Collections.singletonList(lookupOrCreateHybridization(
                                this.dataImportOptions.getNewAnnotationName(), createAnnnotation));
                break;
            default:
                throw new IllegalStateException("Unsupported annotation option: "
                        + this.dataImportOptions.getTargetAnnotationOption());
            }

            for (final Hybridization hybridization : hybs) {
                associateToHybridization(hybridization);
            }

            getArrayDao().save(this.arrayData);
            getArrayDao().flushSession();
        }

        private void associateToHybridization(Hybridization hyb) {
            this.arrayData.addHybridization(hyb);
            hyb.addArrayData(this.arrayData);
        }

        private void addHybridizationDatas() {
            for (final Hybridization hybridization : this.arrayData.getHybridizations()) {
                this.arrayData.getDataSet().addHybridizationData(hybridization);
            }
        }

        private void addColumns() {
            final List<QuantitationType> quantitationTypes = getQuantitationTypes();
            for (final QuantitationType type : quantitationTypes) {
                this.arrayData.getDataSet().addQuantitationType(type);
            }
        }
        
        private void updateLastModifiedData() {
            Date date = new Date();
            for (final Hybridization hybridization : this.arrayData.getHybridizations()) {
                hybridization.propagateLastModifiedDataTime(date);
            }
            caArrayFile.getProject().getExperiment().setLastDataModificationDate(date);
        }

        private List<QuantitationType> getQuantitationTypes() {
            final List<QuantitationType> quantitationTypes = new ArrayList<QuantitationType>();
            for (final QuantitationTypeDescriptor descriptor : this.handler.getQuantitationTypeDescriptors()) {
                final QuantitationType quantitationType = getArrayDao().getQuantitationType(descriptor);
                quantitationTypes.add(quantitationType);
            }
            return quantitationTypes;
        }

        private Hybridization lookupHybridization(String hybridizationName) {
            final Experiment experiment = this.caArrayFile.getProject().getExperiment();
            return experiment.getHybridizationByName(hybridizationName);
        }

        private Hybridization createHybridization(String hybridizationName) throws PlatformFileReadException {
            final Hybridization hybridization = new Hybridization();
            hybridization.setName(hybridizationName);
            final ArrayDesign ad = getArrayDesign(this.caArrayFile, this.handler);
            if (ad != null) {
                final Array array = new Array();
                array.setDesign(ad);
                hybridization.setArray(array);
            }
            this.caArrayFile.getProject().getExperiment().getHybridizations().add(hybridization);
            hybridization.setExperiment(this.caArrayFile.getProject().getExperiment());
            return hybridization;
        }

        private Hybridization lookupOrCreateHybridization(String hybridizationName, boolean createAnnotation)
                throws PlatformFileReadException {
            Hybridization hybridization = lookupHybridization(hybridizationName);
            if (hybridization == null) {
                hybridization = createHybridization(hybridizationName);
                if (createAnnotation) {
                    createAnnotation(hybridization);
                }
            }
            return hybridization;
        }

        private List<Hybridization> lookupOrCreateHybridizations(List<String> hybridizationNames,
                boolean createAnnotation) throws PlatformFileReadException {
            final List<Hybridization> hybs = new ArrayList<Hybridization>();
            for (final String hybName : hybridizationNames) {
                hybs.add(lookupOrCreateHybridization(hybName, createAnnotation));
            }
            return hybs;
        }

        private void createAnnotation(Hybridization hybridization) {
            switch (this.dataImportOptions.getTargetAnnotationOption()) {
            case ASSOCIATE_TO_NODES:
                AbstractExperimentDesignNode newChainStart = hybridization;
                if (this.dataImportOptions.getTargetNodeType() != ExperimentDesignNodeType.LABELED_EXTRACT) {
                    newChainStart =
                            createAnnotationChain(hybridization, this.dataImportOptions.getTargetNodeType()
                                    .getSuccessorType(), hybridization.getName());
                }
                for (final Long targetId : this.dataImportOptions.getTargetNodeIds()) {
                    final AbstractBioMaterial target =
                            DataSetImporter.this.searchDao.retrieve(AbstractBioMaterial.class, targetId);
                    target.addDirectSuccessor(newChainStart);
                }
                break;
            case AUTOCREATE_PER_FILE:
                // intentional fallthrough
            case AUTOCREATE_SINGLE:
                final List<String> sampleNames = this.handler.getSampleNames(hybridization.getName());
                for (final String sampleName : sampleNames) {
                    createAnnotationChain(hybridization, ExperimentDesignNodeType.SOURCE, sampleName);
                }
                break;
            default:
                throw new IllegalStateException("Unsupported annotation option: "
                        + this.dataImportOptions.getTargetAnnotationOption());
            }
        }

        /**
         * Create a new annotation chain from the given hybridization to the given starting node type. A new annotation
         * node of the given type is created, and all intermediate annotation nodes are created as well and linked.
         * 
         * @param hybridization
         * @param chainStartNodeType
         * @param newAnnotationName
         * @return
         */
        private AbstractBioMaterial createAnnotationChain(Hybridization hybridization,
                ExperimentDesignNodeType chainStartNodeType, String newAnnotationName) {
            final Experiment experiment = this.caArrayFile.getProject().getExperiment();

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
         * Fill in the annotation chain from the given hybridization to the given target biomaterial, creating the
         * intermediate biomaterials.
         * 
         * @param hybridization the hybridization for which to create the chain
         * @param target the target biomaterial to which the chain should link. A new biomaterial is created for each
         *            step in the chain between the hybridization and this target.
         * @param newAnnotationName the name to be given to each newly created biomaterial in the chain
         */
        private void fillInAnnotationChain(Hybridization hybridization, AbstractBioMaterial target,
                String newAnnotationName) {
            final ExperimentDesignNodeType nextNodeType = target.getNodeType().getSuccessorType();
            if (nextNodeType == ExperimentDesignNodeType.HYBRIDIZATION) {
                target.addDirectSuccessor(hybridization);
            } else {
                final AbstractBioMaterial nextNode =
                        createAnnotationChain(hybridization, nextNodeType, newAnnotationName);
                target.addDirectSuccessor(nextNode);
            }
        }
    }
}
