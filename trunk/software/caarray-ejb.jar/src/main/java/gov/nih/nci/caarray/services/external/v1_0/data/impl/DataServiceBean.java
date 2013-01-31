//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.services.external.v1_0.data.impl;

import gov.nih.nci.caarray.application.ConfigurationHelper;
import gov.nih.nci.caarray.application.ServiceLocatorFactory;
import gov.nih.nci.caarray.application.fileaccess.TemporaryFileCache;
import gov.nih.nci.caarray.application.fileaccess.TemporaryFileCacheLocator;
import gov.nih.nci.caarray.dao.CaArrayDaoFactory;
import gov.nih.nci.caarray.domain.ConfigParamEnum;
import gov.nih.nci.caarray.domain.data.AbstractArrayData;
import gov.nih.nci.caarray.domain.data.DerivedArrayData;
import gov.nih.nci.caarray.domain.data.DesignElementList;
import gov.nih.nci.caarray.domain.data.HybridizationData;
import gov.nih.nci.caarray.domain.data.RawArrayData;
import gov.nih.nci.caarray.domain.file.CaArrayFile;
import gov.nih.nci.caarray.domain.file.FileType;
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
import gov.nih.nci.caarray.magetab.MageTabDocumentSet;
import gov.nih.nci.caarray.magetab.sdrf.ArrayDataFile;
import gov.nih.nci.caarray.magetab.sdrf.ArrayDataMatrixFile;
import gov.nih.nci.caarray.magetab.sdrf.DerivedArrayDataFile;
import gov.nih.nci.caarray.magetab.sdrf.DerivedArrayDataMatrixFile;
import gov.nih.nci.caarray.magetab.sdrf.SdrfDocument;
import gov.nih.nci.caarray.services.AuthorizationInterceptor;
import gov.nih.nci.caarray.services.HibernateSessionInterceptor;
import gov.nih.nci.caarray.services.TemporaryFileCleanupInterceptor;
import gov.nih.nci.caarray.services.external.v1_0.InvalidInputException;
import gov.nih.nci.caarray.services.external.v1_0.InvalidReferenceException;
import gov.nih.nci.caarray.services.external.v1_0.data.DataService;
import gov.nih.nci.caarray.services.external.v1_0.data.DataTransferException;
import gov.nih.nci.caarray.services.external.v1_0.data.InconsistentDataSetsException;
import gov.nih.nci.caarray.services.external.v1_0.impl.BaseV1_0ExternalService;
import gov.nih.nci.caarray.util.HibernateUtil;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
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
import org.jboss.annotation.ejb.RemoteBinding;
import org.jboss.annotation.ejb.TransactionTimeout;

import com.fiveamsolutions.nci.commons.data.search.PageSortParams;
import com.healthmarketscience.rmiio.RemoteInputStreamMonitor;
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
@Interceptors({ AuthorizationInterceptor.class, TemporaryFileCleanupInterceptor.class,
        HibernateSessionInterceptor.class })
@SuppressWarnings("PMD")
public class DataServiceBean extends BaseV1_0ExternalService implements DataService {
    private static final Logger LOG = Logger.getLogger(DataServiceBean.class);
    
    static final int TIMEOUT_SECONDS = 1800;
    static final long MAX_FILE_REQUEST_SIZE = 1024 * 1024 * 1024; // 1 GB

    private final CaArrayDaoFactory daoFactory = CaArrayDaoFactory.INSTANCE;

    /**
     * {@inheritDoc}
     */
    public DataSet getDataSet(DataSetRequest request) throws InvalidReferenceException, InvalidInputException,
            InconsistentDataSetsException {
        LOG.info("Received data retrieval request");
        checkRequest(request);
        List<gov.nih.nci.caarray.domain.data.DataSet> dataSets = getDataSets(request);
        gov.nih.nci.caarray.domain.data.DataSet mergedDataSet = createMergedDataSet(dataSets, request);
        LOG.info("Retrieved " + mergedDataSet.getHybridizationDataList().size() + " hybridization data elements, "
                + mergedDataSet.getQuantitationTypes().size() + " quant types");
        DataSet externalDataSet = toExternalDataSet(mergedDataSet);
        HibernateUtil.getCurrentSession().clear();
        return externalDataSet;
    }
    
    private DataSet toExternalDataSet(gov.nih.nci.caarray.domain.data.DataSet dataSet) {
        DataSet externalDataSet = new DataSet();
        mapCollection(dataSet.getQuantitationTypes(), externalDataSet.getQuantitationTypes(), QuantitationType.class);
        mapCollection(dataSet.getDesignElementList().getDesignElements(), externalDataSet.getDesignElements(),
                DesignElement.class);
        mapCollection(dataSet.getHybridizationDataList(), externalDataSet.getDatas(),
                gov.nih.nci.caarray.external.v1_0.data.HybridizationData.class);
        return externalDataSet;
    }

    private List<gov.nih.nci.caarray.domain.data.DataSet> getDataSets(DataSetRequest request)
            throws InvalidReferenceException {
        List<AbstractArrayData> arrayDatas = getArrayDatas(request);
        List<gov.nih.nci.caarray.domain.data.DataSet> dataSets = new ArrayList<gov.nih.nci.caarray.domain.data.DataSet>(
                arrayDatas.size());
        for (AbstractArrayData data : arrayDatas) {
            dataSets.add(ServiceLocatorFactory.getArrayDataService().getData(data, getQuantitationTypes(request)));
        }
        return dataSets;
    }

    private gov.nih.nci.caarray.domain.data.DataSet createMergedDataSet(
            List<gov.nih.nci.caarray.domain.data.DataSet> dataSets, DataSetRequest request)
            throws InconsistentDataSetsException, InvalidReferenceException {
        gov.nih.nci.caarray.domain.data.DataSet dataSet = new gov.nih.nci.caarray.domain.data.DataSet();
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
            throw new InconsistentDataSetsException("The DataSet requested data from inconsistent design elemeents");
        }
    }

    private boolean allDesignElementListsAreConsistent(List<gov.nih.nci.caarray.domain.data.DataSet> dataSets) {
        DesignElementList firstList = dataSets.get(0).getDesignElementList();
        for (int i = 1; i < dataSets.size(); i++) {
            DesignElementList nextList = dataSets.get(i).getDesignElementList();
            if (!ListUtils.isEqualList(firstList.getDesignElements(), nextList.getDesignElements())) {
                return false;
            }
        }
        return true;
    }

    private void addHybridizationDatas(gov.nih.nci.caarray.domain.data.DataSet dataSet,
            List<gov.nih.nci.caarray.domain.data.DataSet> dataSets, DataSetRequest request)
            throws InvalidReferenceException {
        for (gov.nih.nci.caarray.domain.data.DataSet nextDataSet : dataSets) {
            for (HybridizationData nextHybridizationData : nextDataSet.getHybridizationDataList()) {
                addHybridizationData(dataSet, nextHybridizationData, request);
            }
        }
    }

    private void addHybridizationData(gov.nih.nci.caarray.domain.data.DataSet dataSet,
            HybridizationData copyFromHybridizationData, DataSetRequest request) throws InvalidReferenceException {
        HybridizationData hybridizationData = new HybridizationData();
        hybridizationData.setHybridization(copyFromHybridizationData.getHybridization());
        dataSet.getHybridizationDataList().add(hybridizationData);
        copyColumns(hybridizationData, copyFromHybridizationData, getQuantitationTypes(request));
        hybridizationData.setDataSet(dataSet);
    }

    private void copyColumns(HybridizationData hybridizationData, HybridizationData copyFromHybridizationData,
            List<gov.nih.nci.caarray.domain.data.QuantitationType> quantitationTypes) {
        for (gov.nih.nci.caarray.domain.data.QuantitationType type : quantitationTypes) {
            hybridizationData.getColumns().add(copyFromHybridizationData.getColumn(type));
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
        List<Hybridization> hybridizations = getHybridizations(request);
        List<CaArrayFile> files = getFiles(request);
        List<gov.nih.nci.caarray.domain.data.QuantitationType> quantitationTypes = getQuantitationTypes(request);
        List<AbstractArrayData> arrayDatas = new ArrayList<AbstractArrayData>(hybridizations.size());

        for (Hybridization hybridization : hybridizations) {
            addArrayDatas(arrayDatas, hybridization, quantitationTypes);
        }
        for (CaArrayFile dataFile : files) {
            addArrayDatas(arrayDatas, dataFile, quantitationTypes);
        }
        return arrayDatas;
    }

    private void addArrayDatas(List<AbstractArrayData> arrayDatas, Hybridization hybridization,
            List<gov.nih.nci.caarray.domain.data.QuantitationType> quantitationTypes) {
        for (RawArrayData rawArrayData : hybridization.getRawDataCollection()) {
            if (shouldAddData(arrayDatas, rawArrayData, quantitationTypes)) {
                arrayDatas.add(rawArrayData);
            }
        }
        for (DerivedArrayData derivedArrayData : hybridization.getDerivedDataCollection()) {
            if (shouldAddData(arrayDatas, derivedArrayData, quantitationTypes)) {
                arrayDatas.add(derivedArrayData);
            }
        }
    }

    private void addArrayDatas(List<AbstractArrayData> arrayDatas, CaArrayFile dataFile,
            List<gov.nih.nci.caarray.domain.data.QuantitationType> quantitationTypes) {
        if (dataFile.getFileType().isDerivedArrayData()) {
            AbstractArrayData arrayData = daoFactory.getArrayDao().getDerivedArrayData(dataFile);
            if (arrayData != null && shouldAddData(arrayDatas, arrayData, quantitationTypes)) {
                arrayDatas.add(arrayData);
            }
        }
        if (dataFile.getFileType().isRawArrayData()) {
            AbstractArrayData arrayData = daoFactory.getArrayDao().getRawArrayData(dataFile);
            if (arrayData != null && shouldAddData(arrayDatas, arrayData, quantitationTypes)) {
                arrayDatas.add(arrayData);
            }
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
        List<Hybridization> hybridizations = new ArrayList<Hybridization>(request.getHybridizations().size());
        for (CaArrayEntityReference hybRef : request.getHybridizations()) {
            hybridizations.add(getRequiredByExternalId(hybRef.getId(), Hybridization.class));
        }
        return hybridizations;
    }

    private List<CaArrayFile> getFiles(DataSetRequest request) throws InvalidReferenceException {
        List<CaArrayFile> files = new ArrayList<CaArrayFile>(request.getDataFiles().size());
        for (CaArrayEntityReference fileRef : request.getDataFiles()) {
            files.add(getRequiredByExternalId(fileRef.getId(), CaArrayFile.class));
        }
        return files;
    }

    private List<gov.nih.nci.caarray.domain.data.QuantitationType> getQuantitationTypes(DataSetRequest request)
            throws InvalidReferenceException {
        List<gov.nih.nci.caarray.domain.data.QuantitationType> types = 
            new ArrayList<gov.nih.nci.caarray.domain.data.QuantitationType>(request.getDataFiles().size());
        for (CaArrayEntityReference qtRef : request.getQuantitationTypes()) {
            types.add(getRequiredByExternalId(qtRef.getId(), gov.nih.nci.caarray.domain.data.QuantitationType.class));
        }
        return types;
    }

    /**
     * {@inheritDoc}
     */
    public FileStreamableContents streamFileContents(CaArrayEntityReference fileRef, boolean compressed)
            throws InvalidReferenceException, DataTransferException {
        CaArrayFile caarrayFile = getRequiredByExternalId(fileRef.getId(), CaArrayFile.class);
        RemoteInputStreamServer istream = null;
        TemporaryFileCache tempFileCache = TemporaryFileCacheLocator.newTemporaryFileCache();
        FileStreamableContents fsContents =  new FileStreamableContents();
        fsContents.setMetadata(mapEntity(caarrayFile, File.class).getMetadata());
        fsContents.setCompressed(compressed);
        try {
            java.io.File file = tempFileCache.getFile(caarrayFile, !compressed);
            DataConfiguration config = ConfigurationHelper.getConfiguration();
            int packetSize = config.getInt(ConfigParamEnum.FILE_RETRIEVAL_API_CHUNK_SIZE.name());
            istream = new SimpleRemoteInputStream(new BufferedInputStream(new FileInputStream(file)),
                    new CacheClosingMonitor(tempFileCache), packetSize);
            fsContents.setContentStream(istream.export());
            // after all the hard work, discard the local reference (we are passing
            // responsibility to the client)
            istream = null;
            
            return fsContents;
        } catch (Exception e) {
            LOG.warn("Could not create input stream for file contents", e);
            throw new DataTransferException("Could not create input stream for file contents");
        } finally {
            // we will only close the stream here if the server fails before
            // returning an exported stream
            if (istream != null) {
                istream.close();
                tempFileCache.closeFiles();
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public MageTabFileSet exportMageTab(CaArrayEntityReference experimentRef) throws InvalidReferenceException,
            DataTransferException {
        gov.nih.nci.caarray.domain.project.Experiment experiment = getRequiredByExternalId(experimentRef.getId(),
                gov.nih.nci.caarray.domain.project.Experiment.class);
        try {
            MageTabFileSet mageTabSet = new MageTabFileSet();
            MageTabDocumentSet docSet = exportToMageTab(experiment);
            mageTabSet.setIdf(toFileContents(docSet.getIdfDocuments().iterator().next().getFile(),
                    FileType.MAGE_TAB_IDF));
            mageTabSet.setSdrf(toFileContents(docSet.getSdrfDocuments().iterator().next().getFile(),
                    FileType.MAGE_TAB_SDRF));

            List<CaArrayFile> dataFiles = getDataFilesReferencedByMageTab(docSet, experiment);
            mapCollection(dataFiles, mageTabSet.getDataFiles(), File.class);

            return mageTabSet;
        } catch (IOException e) {
            LOG.error("Error exporting to MAGE-TAB", e);
            throw new DataTransferException("Could not generate idf/sdrf");
        } 
    }
    
    private MageTabDocumentSet exportToMageTab(Experiment experiment) {
        TemporaryFileCache tempCache = TemporaryFileCacheLocator.getTemporaryFileCache();

        String baseFileName = experiment.getPublicIdentifier();
        String idfFileName = baseFileName + ".idf";
        String sdrfFileName = baseFileName + ".sdrf";
        java.io.File idfFile = tempCache.createFile(idfFileName);
        java.io.File sdrfFile = tempCache.createFile(sdrfFileName);

        // Translate the experiment and export to the temporary files.
        return ServiceLocatorFactory.getMageTabExporter().exportToMageTab(experiment, idfFile, sdrfFile);
    }
    
    private List<CaArrayFile> getDataFilesReferencedByMageTab(MageTabDocumentSet mageTab, Experiment experiment) {
        List<String> fileNames = new ArrayList<String>();
        for (SdrfDocument sdrfDoc : mageTab.getSdrfDocuments()) {
            for (ArrayDataFile file : sdrfDoc.getAllArrayDataFiles()) {
                fileNames.add(file.getName());
            }
            for (DerivedArrayDataFile file : sdrfDoc.getAllDerivedArrayDataFiles()) {
                fileNames.add(file.getName());
            }
            for (ArrayDataMatrixFile file : sdrfDoc.getAllArrayDataMatrixFiles()) {
                fileNames.add(file.getName());
            }
            for (DerivedArrayDataMatrixFile file : sdrfDoc.getAllDerivedArrayDataMatrixFiles()) {
                fileNames.add(file.getName());
            }
        }
        List<CaArrayFile> dataFiles = ServiceLocatorFactory.getGenericDataService().pageAndFilterCollection(
                experiment.getProject().getFiles(), "name", fileNames,
                new PageSortParams<CaArrayFile>(-1, 0, new AdHocSortCriterion<CaArrayFile>("name"), false));
        return dataFiles;
    }
    
    private FileContents toFileContents(java.io.File mageTabFile, FileType fileType) throws IOException {
        FileContents fc = new FileContents();
        fc.setContents(FileUtils.readFileToByteArray(mageTabFile));
        fc.setCompressed(false);
        FileMetadata metadata = new FileMetadata();
        metadata.setName(mageTabFile.getName());
        metadata.setUncompressedSize(mageTabFile.length());
        metadata.setCompressedSize(-1);
        metadata.setFileType(mapEntity(fileType, gov.nih.nci.caarray.external.v1_0.data.FileType.class));
        fc.setMetadata(metadata);
        return fc;
    }
    
    /**
     * Remote Stream Monitor that closes the temporary file cache associated with the files, ensuring they
     * are deleted.
     * 
     * @author dkokotov
     */
    private static class CacheClosingMonitor extends RemoteInputStreamMonitor {
        private TemporaryFileCache cache;
        
        public CacheClosingMonitor(TemporaryFileCache cache) {
            this.cache = cache;
        }
        
        public void closed(RemoteInputStreamServer stream, boolean clean) {
            cache.closeFiles();            
        }
    }
}
