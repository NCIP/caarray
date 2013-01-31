//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.application.file;

import gov.nih.nci.caarray.application.arraydata.ArrayDataService;
import gov.nih.nci.caarray.application.arraydata.DataImportOptions;
import gov.nih.nci.caarray.application.fileaccess.TemporaryFileCacheLocator;
import gov.nih.nci.caarray.dao.CaArrayDaoFactory;
import gov.nih.nci.caarray.domain.file.CaArrayFile;
import gov.nih.nci.caarray.domain.file.CaArrayFileSet;
import gov.nih.nci.caarray.domain.file.FileStatus;
import gov.nih.nci.caarray.domain.file.FileType;
import gov.nih.nci.caarray.magetab.MageTabDocumentSet;
import gov.nih.nci.caarray.magetab.sdrf.AbstractSampleDataRelationshipNode;
import gov.nih.nci.caarray.validation.FileValidationResult;
import gov.nih.nci.caarray.validation.InvalidDataFileException;
import gov.nih.nci.caarray.validation.ValidationMessage.Type;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;



/**
 * Manages import of array data files.
 */
final class ArrayDataImporter {

    private static final Logger LOG = Logger.getLogger(ArrayDataImporter.class);

    private final ArrayDataService arrayDataService;
    private final CaArrayDaoFactory daoFactory;

    ArrayDataImporter(ArrayDataService arrayDataService, CaArrayDaoFactory daoFactory) {
        super();
        this.arrayDataService = arrayDataService;
        this.daoFactory = daoFactory;
    }

    public void importFiles(CaArrayFileSet fileSet, DataImportOptions dataImportOptions) {
        Set<CaArrayFile> dataFiles = fileSet.getArrayDataFiles();
        fileSet.getFiles().clear();
        int fileCount = 0;
        int totalNumberOfFiles = dataFiles.size();
        for (Iterator<CaArrayFile> fileIt = dataFiles.iterator(); fileIt.hasNext();) {
            CaArrayFile file = fileIt.next();
            LOG.info("Importing data file [" + ++fileCount + "/" + totalNumberOfFiles + "]: " + file.getName());
            importFile(file, dataImportOptions);
            fileIt.remove();
        }
    }

    private void importFile(CaArrayFile file, DataImportOptions dataImportOptions) {
        try {
            this.daoFactory.getSearchDao().refresh(file);
            this.arrayDataService.importData(file, true, dataImportOptions);
        } catch (InvalidDataFileException e) {
            file.setFileStatus(FileStatus.VALIDATION_ERRORS);
            file.setValidationResult(e.getFileValidationResult());
        }
        this.daoFactory.getProjectDao().save(file);
        TemporaryFileCacheLocator.getTemporaryFileCache().closeFile(file);
        this.daoFactory.getProjectDao().flushSession();
        this.daoFactory.getProjectDao().clearSession();
    }

    void validateFiles(CaArrayFileSet fileSet, MageTabDocumentSet mTabSet) {
        Set<CaArrayFile> dataFiles = fileSet.getArrayDataFiles();
        Set<CaArrayFile> sdrfFiles = fileSet.getFilesByType(FileType.MAGE_TAB_SDRF);
        int fileCount = 0;
        int totalNumberOfFiles = dataFiles.size();
        for (CaArrayFile file : dataFiles) {
            if (file.getFileStatus() != FileStatus.VALIDATION_ERRORS) {
                LOG.info("Validating data file [" + ++fileCount + "/" + totalNumberOfFiles + "]: " + file.getName());
                validateFile(file, mTabSet);
            }
        }
        checkSdrfHybNames(dataFiles, mTabSet, sdrfFiles);
    }

    private void validateFile(CaArrayFile file, MageTabDocumentSet mTabSet) {
        this.arrayDataService.validate(file, mTabSet);
    }

    private void checkSdrfHybNames(Set<CaArrayFile> dataFiles, MageTabDocumentSet mTabSet,
            Set<CaArrayFile> sdrfFiles) {
        if (mTabSet != null) {
        checkSdrfDataRelationshipNodeNames(dataFiles, mTabSet.getSdrfHybridizations(),
                sdrfFiles, FileValidationResult.HYB_NAME);
        }
    }

    private <T extends AbstractSampleDataRelationshipNode> List<String> getNodeValueNames(
            String key, Map<String, List<T>> values) {
        List<String> names = new ArrayList<String>();
        List<T> mageTabNodeList = values.get(key);
        for (T node : mageTabNodeList) {
            names.add(node.getName());
        }

        return names;

    }

    private <T extends AbstractSampleDataRelationshipNode>
        Map<String, List<T>>
        pruneOutDataFileNodeNames(Map<String, List<T>> sdrfNodeNames,
        List<String> dataFileNodeNames) {

        Map<String, List<T>> updatedMap = new HashMap<String, List<T>>();

        for (String key : sdrfNodeNames.keySet()) {
            List<T> updatedList = new ArrayList<T>();
            List<T> mageTabNodeList = sdrfNodeNames.get(key);

            for (T sdrfNodeName : mageTabNodeList) {
                if (!dataFileNodeNames.contains(sdrfNodeName.getName())) {
                    updatedList.add(sdrfNodeName);
                }
            }

            updatedMap.put(key, updatedList);

        }
        return updatedMap;
    }

    private <T extends AbstractSampleDataRelationshipNode>
    void checkSdrfDataRelationshipNodeNames(Set<CaArrayFile> dataFiles,
            Map<String, List<T>> values, Set<CaArrayFile> sdrfFiles, String keyName) {
        // generate list of node names from data files and compare
        // to list of node names from the sdrf

        List<String> dataFileNodeNames =
            collectDataRelationshipNodeNames(dataFiles, keyName);

        if (!dataFileNodeNames.isEmpty() && !values.isEmpty() && !values.values().isEmpty()) {

            doAddSdrfErrors(pruneOutDataFileNodeNames(values, dataFileNodeNames),
                    keyName, sdrfFiles);
        }


    }

    private List<String> collectDataRelationshipNodeNames(Set<CaArrayFile> dataFiles, String keyName) {
        List<String> dataFileRelationshipNodeNames = new ArrayList<String>();
        for (CaArrayFile caf : dataFiles) {
            if (caf.getValidationResult() != null) {
                dataFileRelationshipNodeNames
                    .addAll(getRelationshipNodeName(caf.getValidationResult(), keyName));
            }
        }

        return dataFileRelationshipNodeNames;
    }

    @SuppressWarnings("unchecked")
    private List<String> getRelationshipNodeName(FileValidationResult result, String keyName) {
        List<String> names = new ArrayList<String>();
        if (result != null && result.getValidationProperties(keyName) != null) {
            names.addAll((List<String>) result.getValidationProperties(keyName));
        }
        return names;
    }

    private <T extends AbstractSampleDataRelationshipNode> void doAddSdrfErrors(
            Map<String, List<T>> values, String materialName, Set<CaArrayFile> sdrfFiles) {

        if (!values.isEmpty() && !values.values().isEmpty()) {
            // add error to matching SDRF
            for (String mtSdrfName : values.keySet()) {
                doAddSdrfError(mtSdrfName, values, materialName, sdrfFiles);
            }
        }
    }


    private <T extends AbstractSampleDataRelationshipNode> void doAddSdrfError(
            String mtSdrfName, Map<String, List<T>> values, String materialName, Set<CaArrayFile> sdrfFiles) {
        for (CaArrayFile sdrf : sdrfFiles) {
            List<String> mtSdrfNodeNameValues = getNodeValueNames(mtSdrfName, values);
            if (mtSdrfName.equals(sdrf.getName())
                    && !mtSdrfNodeNameValues.isEmpty()) {
                sdrf.setFileStatus(FileStatus.VALIDATION_ERRORS);
                StringBuilder errorMessage = new StringBuilder(materialName);
                errorMessage.append(" ");
                errorMessage.append(mtSdrfNodeNameValues.toString());
                errorMessage.append(" were not found in data files provided.");
                if (sdrf.getValidationResult() == null) {
                    File file = TemporaryFileCacheLocator.getTemporaryFileCache().getFile(sdrf);
                    sdrf.setValidationResult(new FileValidationResult(file));
                }
                sdrf.getValidationResult()
                    .addMessage(Type.ERROR, errorMessage.toString());
                this.daoFactory.getFileDao().save(sdrf);
            }
        }
    }

}
