//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.services.external.v1_0.data.impl;

import gov.nih.nci.caarray.application.ConfigurationHelper;
import gov.nih.nci.caarray.application.ServiceLocatorFactory;
import gov.nih.nci.caarray.application.fileaccess.TemporaryFileCache;
import gov.nih.nci.caarray.dao.ArrayDao;
import gov.nih.nci.caarray.dataStorage.DataStorageFacade;
import gov.nih.nci.caarray.dataStorage.ParsedDataPersister;
import gov.nih.nci.caarray.domain.ConfigParamEnum;
import gov.nih.nci.caarray.domain.data.AbstractArrayData;
import gov.nih.nci.caarray.domain.data.AbstractDataColumn;
import gov.nih.nci.caarray.domain.data.DerivedArrayData;
import gov.nih.nci.caarray.domain.data.DesignElementList;
import gov.nih.nci.caarray.domain.data.HybridizationData;
import gov.nih.nci.caarray.domain.data.RawArrayData;
import gov.nih.nci.caarray.domain.file.CaArrayFile;
import gov.nih.nci.caarray.domain.file.FileType;
import gov.nih.nci.caarray.domain.file.FileTypeRegistry;
import gov.nih.nci.caarray.domain.hybridization.Hybridization;
import gov.nih.nci.caarray.domain.project.Experiment;
import gov.nih.nci.caarray.domain.search.AdHocSortCriterion;
import gov.nih.nci.caarray.external.v1_0.CaArrayEntityReference;
import gov.nih.nci.caarray.external.v1_0.data.DataSet;
import gov.nih.nci.caarray.external.v1_0.data.DesignElement;
import gov.nih.nci.caarray.external.v1_0.data.File;
import gov.nih.nci.caarray.external.v1_0.data.FileContents;
import gov.nih.nci.caarray.external.v1_0.data.FileMetadata;
import gov.nih.nci.caarray.external.v1_0.data.FileStreamableContents;
import gov.nih.nci.caarray.external.v1_0.data.MageTabFileSet;
import gov.nih.nci.caarray.external.v1_0.data.QuantitationType;
import gov.nih.nci.caarray.external.v1_0.query.DataSetRequest;
import gov.nih.nci.caarray.injection.InjectionInterceptor;
import gov.nih.nci.caarray.magetab.MageTabDocumentSet;
import gov.nih.nci.caarray.magetab.sdrf.ArrayDataFile;
import gov.nih.nci.caarray.magetab.sdrf.ArrayDataMatrixFile;
import gov.nih.nci.caarray.magetab.sdrf.DerivedArrayDataFile;
import gov.nih.nci.caarray.magetab.sdrf.DerivedArrayDataMatrixFile;
import gov.nih.nci.caarray.magetab.sdrf.SdrfDocument;
import gov.nih.nci.caarray.services.AuthorizationInterceptor;
import gov.nih.nci.caarray.services.HibernateSessionInterceptor;
import gov.nih.nci.caarray.services.StorageInterceptor;
import gov.nih.nci.caarray.services.external.v1_0.InvalidInputException;
import gov.nih.nci.caarray.services.external.v1_0.InvalidReferenceException;
import gov.nih.nci.caarray.services.external.v1_0.data.DataService;
import gov.nih.nci.caarray.services.external.v1_0.data.DataTransferException;
import gov.nih.nci.caarray.services.external.v1_0.data.InconsistentDataSetsException;
import gov.nih.nci.caarray.services.external.v1_0.impl.BaseV1_0ExternalService;
import gov.nih.nci.caarray.util.CaArrayHibernateHelper;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.security.PermitAll;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.interceptor.Interceptors;

import org.apache.commons.collections.ListUtils;
import org.apache.commons.configuration.DataConfiguration;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.jboss.ejb3.annotation.RemoteBinding;
import org.jboss.ejb3.annotation.TransactionTimeout;

import com.fiveamsolutions.nci.commons.data.search.PageSortParams;
import com.google.inject.Inject;
import com.healthmarketscience.rmiio.RemoteInputStreamServer;
import com.healthmarketscience.rmiio.SimpleRemoteInputStream;

/**
 * @author dkokotov
 * 
 */
@Stateless(name = "DataServicev1_0")
@PermitAll
@RemoteBinding(jndiBinding = DataService.JNDI_NAME)
@TransactionAttribute(TransactionAttributeType.REQUIRED)
@TransactionTimeout(DataServiceBean.TIMEOUT_SECONDS)
@Interceptors({AuthorizationInterceptor.class, StorageInterceptor.class, HibernateSessionInterceptor.class,
    InjectionInterceptor.class })
    @SuppressWarnings("PMD")
    public class DataServiceBean extends BaseV1_0ExternalService implements DataService {
    private static final Logger LOG = Logger.getLogger(DataServiceBean.class);

    static final int TIMEOUT_SECONDS = 1800;
    static final long MAX_FILE_REQUEST_SIZE = 1024 * 1024 * 1024; // 1 GB

    private CaArrayHibernateHelper hibernateHelper;
    private ArrayDao arrayDao;
    private DataStorageFacade dataStorageFacade;
    private TemporaryFileCache temporaryFileCache;
    private ParsedDataPersister parsedDataPersister;

    /**
     * {@inheritDoc}
     */
    @Override
    public DataSet getDataSet(DataSetRequest request) throws InvalidReferenceException, InvalidInputException,
    InconsistentDataSetsException {
        LOG.info("Received data retrieval request");
        checkRequest(request);
        final List<gov.nih.nci.caarray.domain.data.DataSet> dataSets = getDataSets(request);
        final gov.nih.nci.caarray.domain.data.DataSet mergedDataSet = createMergedDataSet(dataSets, request);
        LOG.info("Retrieved " + mergedDataSet.getHybridizationDataList().size() + " hybridization data elements, "
                + mergedDataSet.getQuantitationTypes().size() + " quant types");
        final DataSet externalDataSet = toExternalDataSet(mergedDataSet);
        this.hibernateHelper.getCurrentSession().clear();
        return externalDataSet;
    }

    private DataSet toExternalDataSet(gov.nih.nci.caarray.domain.data.DataSet dataSet) {
        final DataSet externalDataSet = new DataSet();
        mapCollection(dataSet.getQuantitationTypes(), externalDataSet.getQuantitationTypes(), QuantitationType.class);
        mapCollection(dataSet.getDesignElementList().getDesignElements(), externalDataSet.getDesignElements(),
                DesignElement.class);
        mapCollection(dataSet.getHybridizationDataList(), externalDataSet.getDatas(),
                gov.nih.nci.caarray.external.v1_0.data.HybridizationData.class);
        return externalDataSet;
    }

    private List<gov.nih.nci.caarray.domain.data.DataSet> getDataSets(DataSetRequest request)
    throws InvalidReferenceException {
        final List<AbstractArrayData> arrayDatas = getArrayDatas(request);
        final List<gov.nih.nci.caarray.domain.data.DataSet> dataSets =
            new ArrayList<gov.nih.nci.caarray.domain.data.DataSet>(arrayDatas.size());
        for (final AbstractArrayData data : arrayDatas) {
            dataSets.add(data.getDataSet());
        }
        return dataSets;
    }

    private gov.nih.nci.caarray.domain.data.DataSet createMergedDataSet(
            List<gov.nih.nci.caarray.domain.data.DataSet> dataSets, DataSetRequest request)
    throws InconsistentDataSetsException, InvalidReferenceException {
        final gov.nih.nci.caarray.domain.data.DataSet dataSet = new gov.nih.nci.caarray.domain.data.DataSet();
        dataSet.getQuantitationTypes().addAll(getQuantitationTypes(request));
        addDesignElementList(dataSet, dataSets);
        addHybridizationDatas(dataSet, dataSets, request);
        return dataSet;
    }

    private void addDesignElementList(gov.nih.nci.caarray.domain.data.DataSet dataSet,
            List<gov.nih.nci.caarray.domain.data.DataSet> dataSets) throws InconsistentDataSetsException {
        if (dataSets.isEmpty()) {
            dataSet.setDesignElementList(new DesignElementList());
        } else if (allDesignElementListsAreConsistent(dataSets)) {
            dataSet.setDesignElementList(dataSets.get(0).getDesignElementList());
        } else {
            throw new InconsistentDataSetsException("The DataSet requested data from inconsistent design elements");
        }
    }

    private boolean allDesignElementListsAreConsistent(List<gov.nih.nci.caarray.domain.data.DataSet> dataSets) {
        final DesignElementList firstList = dataSets.get(0).getDesignElementList();
        for (int i = 1; i < dataSets.size(); i++) {
            final DesignElementList nextList = dataSets.get(i).getDesignElementList();
            if (!ListUtils.isEqualList(firstList.getDesignElements(), nextList.getDesignElements())) {
                return false;
            }
        }
        return true;
    }

    private void addHybridizationDatas(gov.nih.nci.caarray.domain.data.DataSet dataSet,
            List<gov.nih.nci.caarray.domain.data.DataSet> dataSets, DataSetRequest request)
    throws InvalidReferenceException {
        for (final gov.nih.nci.caarray.domain.data.DataSet nextDataSet : dataSets) {
            for (final HybridizationData nextHybridizationData : nextDataSet.getHybridizationDataList()) {
                addHybridizationData(dataSet, nextHybridizationData, request);
            }
        }
    }

    private void addHybridizationData(gov.nih.nci.caarray.domain.data.DataSet dataSet,
            HybridizationData copyFromHybridizationData, DataSetRequest request) throws InvalidReferenceException {
        final HybridizationData hybridizationData = new HybridizationData();
        hybridizationData.setHybridization(copyFromHybridizationData.getHybridization());
        dataSet.getHybridizationDataList().add(hybridizationData);
        copyColumns(hybridizationData, copyFromHybridizationData, getQuantitationTypes(request));
        hybridizationData.setDataSet(dataSet);
    }

    private void copyColumns(HybridizationData hybridizationData, HybridizationData copyFromHybridizationData,
            List<gov.nih.nci.caarray.domain.data.QuantitationType> quantitationTypes) {
        for (final gov.nih.nci.caarray.domain.data.QuantitationType type : quantitationTypes) {
            AbstractDataColumn column = copyFromHybridizationData.getColumn(type);
            parsedDataPersister.loadFromStorage(column);
            hybridizationData.getColumns().add(column);
        }
    }

    private void checkRequest(DataSetRequest request) throws InvalidInputException {
        if (request == null) {
            throw new InvalidInputException("DataRetrievalRequest was null");
        } else if (request.getHybridizations().isEmpty() && request.getDataFiles().isEmpty()) {
            throw new InvalidInputException("DataRetrievalRequest didn't specify any Hybridizations or Files");
        } else if (request.getQuantitationTypes().isEmpty()) {
            throw new InvalidInputException("DataRetrievalRequest didn't specify QuantitationTypes");
        }
    }

    private List<AbstractArrayData> getArrayDatas(DataSetRequest request) throws InvalidReferenceException {
        final List<Hybridization> hybridizations = getHybridizations(request);
        final List<CaArrayFile> files = getFiles(request);
        final List<gov.nih.nci.caarray.domain.data.QuantitationType> quantitationTypes = getQuantitationTypes(request);
        final List<AbstractArrayData> arrayDatas = new ArrayList<AbstractArrayData>(hybridizations.size());

        for (final Hybridization hybridization : hybridizations) {
            addArrayDatas(arrayDatas, hybridization, quantitationTypes);
        }
        for (final CaArrayFile dataFile : files) {
            addArrayDatas(arrayDatas, dataFile, quantitationTypes);
        }
        return arrayDatas;
    }

    private void addArrayDatas(List<AbstractArrayData> arrayDatas, Hybridization hybridization,
            List<gov.nih.nci.caarray.domain.data.QuantitationType> quantitationTypes) {
        for (final RawArrayData rawArrayData : hybridization.getRawDataCollection()) {
            if (shouldAddData(arrayDatas, rawArrayData, quantitationTypes)) {
                arrayDatas.add(rawArrayData);
            }
        }
        for (final DerivedArrayData derivedArrayData : hybridization.getDerivedDataCollection()) {
            if (shouldAddData(arrayDatas, derivedArrayData, quantitationTypes)) {
                arrayDatas.add(derivedArrayData);
            }
        }
    }

    private void addArrayDatas(List<AbstractArrayData> arrayDatas, CaArrayFile dataFile,
            List<gov.nih.nci.caarray.domain.data.QuantitationType> quantitationTypes) {
        final AbstractArrayData arrayData = this.arrayDao.getArrayData(dataFile.getId());
        if (arrayData != null && shouldAddData(arrayDatas, arrayData, quantitationTypes)) {
            arrayDatas.add(arrayData);
        }
    }

    private boolean shouldAddData(List<AbstractArrayData> arrayDatas, AbstractArrayData arrayData,
            List<gov.nih.nci.caarray.domain.data.QuantitationType> quantitationTypes) {
        return arrayData != null && !arrayDatas.contains(arrayData) && containsAllTypes(arrayData, quantitationTypes);
    }

    private boolean containsAllTypes(AbstractArrayData arrayData,
            List<gov.nih.nci.caarray.domain.data.QuantitationType> quantitationTypes) {
        return arrayData.getDataSet() != null
        && arrayData.getDataSet().getQuantitationTypes().containsAll(quantitationTypes);
    }

    private List<Hybridization> getHybridizations(DataSetRequest request) throws InvalidReferenceException {
        final List<Hybridization> hybridizations = new ArrayList<Hybridization>(request.getHybridizations().size());
        for (final CaArrayEntityReference hybRef : request.getHybridizations()) {
            hybridizations.add(getRequiredByExternalId(hybRef.getId(), Hybridization.class));
        }
        return hybridizations;
    }

    private List<CaArrayFile> getFiles(DataSetRequest request) throws InvalidReferenceException {
        final List<CaArrayFile> files = new ArrayList<CaArrayFile>(request.getDataFiles().size());
        for (final CaArrayEntityReference fileRef : request.getDataFiles()) {
            files.add(getRequiredByExternalId(fileRef.getId(), CaArrayFile.class));
        }
        return files;
    }

    private List<gov.nih.nci.caarray.domain.data.QuantitationType> getQuantitationTypes(DataSetRequest request)
    throws InvalidReferenceException {
        final List<gov.nih.nci.caarray.domain.data.QuantitationType> types =
            new ArrayList<gov.nih.nci.caarray.domain.data.QuantitationType>(request.getDataFiles().size());
        for (final CaArrayEntityReference qtRef : request.getQuantitationTypes()) {
            types.add(getRequiredByExternalId(qtRef.getId(), gov.nih.nci.caarray.domain.data.QuantitationType.class));
        }
        return types;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public FileStreamableContents streamFileContents(CaArrayEntityReference fileRef, boolean compressed)
    throws InvalidReferenceException, DataTransferException {
        final CaArrayFile caarrayFile = getRequiredByExternalId(fileRef.getId(), CaArrayFile.class);
        RemoteInputStreamServer istream = null;
        final FileStreamableContents fsContents = new FileStreamableContents();
        fsContents.setMetadata(mapEntity(caarrayFile, File.class).getMetadata());
        fsContents.setCompressed(compressed);
        try {
            final InputStream is = this.dataStorageFacade.openInputStream(caarrayFile.getDataHandle(), compressed);
            final DataConfiguration config = ConfigurationHelper.getConfiguration();
            final int packetSize = config.getInt(ConfigParamEnum.FILE_RETRIEVAL_API_CHUNK_SIZE.name());
            istream = new SimpleRemoteInputStream(new BufferedInputStream(is));
            fsContents.setContentStream(istream.export());
            // after all the hard work, discard the local reference (we are passing
            // responsibility to the client)
            istream = null;

            return fsContents;
        } catch (final Exception e) {
            LOG.warn("Could not create input stream for file contents", e);
            throw new DataTransferException("Could not create input stream for file contents");
        } finally {
            // we will only close the stream here if the server fails before
            // returning an exported stream
            if (istream != null) {
                istream.close();
                // ARRAY-1958: close out temp files, if any
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MageTabFileSet exportMageTab(CaArrayEntityReference experimentRef) throws InvalidReferenceException,
    DataTransferException {
        final gov.nih.nci.caarray.domain.project.Experiment experiment =
            getRequiredByExternalId(experimentRef.getId(), gov.nih.nci.caarray.domain.project.Experiment.class);
        try {
            final MageTabFileSet mageTabSet = new MageTabFileSet();
            final MageTabDocumentSet docSet = exportToMageTab(experiment);
            mageTabSet.setIdf(toFileContents(docSet.getIdfDocuments().iterator().next().getFile().getAsFile(),
                    FileTypeRegistry.MAGE_TAB_IDF));
            mageTabSet.setSdrf(toFileContents(docSet.getSdrfDocuments().iterator().next().getFile().getAsFile(),
                    FileTypeRegistry.MAGE_TAB_SDRF));

            final List<CaArrayFile> dataFiles = getDataFilesReferencedByMageTab(docSet, experiment);
            mapCollection(dataFiles, mageTabSet.getDataFiles(), File.class);

            return mageTabSet;
        } catch (final IOException e) {
            LOG.error("Error exporting to MAGE-TAB", e);
            throw new DataTransferException("Could not generate idf/sdrf");
        }
    }

    private MageTabDocumentSet exportToMageTab(Experiment experiment) {
        final String baseFileName = experiment.getPublicIdentifier();
        final String idfFileName = baseFileName + ".idf";
        final String sdrfFileName = baseFileName + ".sdrf";
        final java.io.File idfFile = this.temporaryFileCache.createFile(idfFileName);
        final java.io.File sdrfFile = this.temporaryFileCache.createFile(sdrfFileName);
        // Translate the experiment and export to the temporary files.
        return ServiceLocatorFactory.getMageTabExporter().exportToMageTab(experiment, idfFile, sdrfFile);
    }

    private List<CaArrayFile> getDataFilesReferencedByMageTab(MageTabDocumentSet mageTab, Experiment experiment) {
        final List<String> fileNames = new ArrayList<String>();
        for (final SdrfDocument sdrfDoc : mageTab.getSdrfDocuments()) {
            for (final ArrayDataFile file : sdrfDoc.getAllArrayDataFiles()) {
                fileNames.add(file.getName());
            }
            for (final DerivedArrayDataFile file : sdrfDoc.getAllDerivedArrayDataFiles()) {
                fileNames.add(file.getName());
            }
            for (final ArrayDataMatrixFile file : sdrfDoc.getAllArrayDataMatrixFiles()) {
                fileNames.add(file.getName());
            }
            for (final DerivedArrayDataMatrixFile file : sdrfDoc.getAllDerivedArrayDataMatrixFiles()) {
                fileNames.add(file.getName());
            }
        }
        final List<CaArrayFile> dataFiles =
            ServiceLocatorFactory.getGenericDataService().pageAndFilterCollection(
                    experiment.getProject().getFiles(), "name", fileNames,
                    new PageSortParams<CaArrayFile>(-1, 0, new AdHocSortCriterion<CaArrayFile>("name"), false));
        return dataFiles;
    }

    private FileContents toFileContents(java.io.File mageTabFile, FileType fileType) throws IOException {
        final FileContents fc = new FileContents();
        fc.setContents(FileUtils.readFileToByteArray(mageTabFile));
        fc.setCompressed(false);
        final FileMetadata metadata = new FileMetadata();
        metadata.setName(mageTabFile.getName());
        metadata.setUncompressedSize(mageTabFile.length());
        metadata.setCompressedSize(-1);
        metadata.setFileType(mapEntity(fileType, gov.nih.nci.caarray.external.v1_0.data.FileType.class));
        fc.setMetadata(metadata);
        return fc;
    }

    /**
     * @param hibernateHelper the hibernateHelper to set
     */
    @Inject
    public void setHibernateHelper(CaArrayHibernateHelper hibernateHelper) {
        this.hibernateHelper = hibernateHelper;
    }

    /**
     * @param arrayDao the arrayDao to set
     */
    @Inject
    public void setArrayDao(ArrayDao arrayDao) {
        this.arrayDao = arrayDao;
    }

    /**
     * @param dataStorageFacade the dataStorageFacade to set
     */
    @Inject
    public void setDataStorageFacade(DataStorageFacade dataStorageFacade) {
        this.dataStorageFacade = dataStorageFacade;
    }

    /**
     * @param temporaryFileCache the temporaryFileCache to set
     */
    @Inject
    public void setTemporaryFileCache(TemporaryFileCache temporaryFileCache) {
        this.temporaryFileCache = temporaryFileCache;
    }

    /**
     * @param parsedDataPersister the parsedDataPersister to set
     */
    @Inject
    public void setParsedDataPersister(ParsedDataPersister parsedDataPersister) {
        this.parsedDataPersister = parsedDataPersister;
    }
}
