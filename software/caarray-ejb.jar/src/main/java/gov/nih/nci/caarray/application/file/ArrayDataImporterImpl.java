//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package gov.nih.nci.caarray.application.file;

import gov.nih.nci.caarray.application.arraydata.ArrayDataService;
import gov.nih.nci.caarray.application.arraydata.DataImportOptions;
import gov.nih.nci.caarray.dao.FileDao;
import gov.nih.nci.caarray.dao.ProjectDao;
import gov.nih.nci.caarray.dao.SearchDao;
import gov.nih.nci.caarray.domain.file.CaArrayFile;
import gov.nih.nci.caarray.domain.file.CaArrayFileSet;
import gov.nih.nci.caarray.domain.file.FileStatus;
import gov.nih.nci.caarray.domain.file.FileTypeRegistry;
import gov.nih.nci.caarray.magetab.MageTabDocumentSet;
import gov.nih.nci.caarray.magetab.sdrf.AbstractSampleDataRelationshipNode;
import gov.nih.nci.caarray.validation.FileValidationResult;
import gov.nih.nci.caarray.validation.InvalidDataFileException;
import gov.nih.nci.caarray.validation.ValidationMessage.Type;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import com.google.inject.Inject;

/**
 * Manages import of array data files.
 */
final class ArrayDataImporterImpl implements ArrayDataImporter {
    private static final Logger LOG = Logger.getLogger(ArrayDataImporterImpl.class);

    private final ArrayDataService arrayDataService;

    private final FileDao fileDao;
    private final ProjectDao projectDao;
    private final SearchDao searchDao;

    @Inject
    ArrayDataImporterImpl(ArrayDataService arrayDataService, FileDao fileDao, ProjectDao projectDao, 
            SearchDao searchDao) {
        super();
        this.arrayDataService = arrayDataService;
        this.fileDao = fileDao;
        this.projectDao = projectDao;
        this.searchDao = searchDao;
    }

    @Override
    public void importFiles(CaArrayFileSet fileSet, DataImportOptions dataImportOptions, MageTabDocumentSet mTabSet) {
        final Set<CaArrayFile> dataFiles = fileSet.getArrayDataFiles();
        fileSet.getFiles().clear();
        int fileCount = 0;
        final int totalNumberOfFiles = dataFiles.size();
        for (final Iterator<CaArrayFile> fileIt = dataFiles.iterator(); fileIt.hasNext();) {
            final CaArrayFile file = fileIt.next();
            LOG.info("Importing data file [" + ++fileCount + "/" + totalNumberOfFiles + "]: " + file.getName());
            importFile(file, dataImportOptions, mTabSet);
            fileIt.remove();
        }
    }

    private void importFile(CaArrayFile file, DataImportOptions dataImportOptions, MageTabDocumentSet mTabSet) {
        try {
            this.searchDao.refresh(file);
            this.arrayDataService.importData(file, true, dataImportOptions, mTabSet);
        } catch (final InvalidDataFileException e) {
            file.setFileStatus(FileStatus.VALIDATION_ERRORS);
            file.setValidationResult(e.getFileValidationResult());
        }
        this.projectDao.save(file);
        this.projectDao.flushSession();
        this.projectDao.clearSession();
    }

    @Override
    public void validateFiles(CaArrayFileSet fileSet, MageTabDocumentSet mTabSet, boolean reimport) {
        final Set<CaArrayFile> dataFiles = fileSet.getArrayDataFiles();
        final Set<CaArrayFile> sdrfFiles = fileSet.getFilesByType(FileTypeRegistry.MAGE_TAB_SDRF);
        int fileCount = 0;
        final int totalNumberOfFiles = dataFiles.size();
        for (final CaArrayFile file : dataFiles) {
            if (file.getFileStatus() != FileStatus.VALIDATION_ERRORS) {
                LOG.info("Validating data file [" + ++fileCount + "/" + totalNumberOfFiles + "]: " + file.getName());
                validateFile(file, mTabSet, reimport);
            }
        }
        checkSdrfHybNames(dataFiles, mTabSet, sdrfFiles);
    }

    private void validateFile(CaArrayFile file, MageTabDocumentSet mTabSet, boolean reimport) {
        this.arrayDataService.validate(file, mTabSet, reimport);
    }

    private void checkSdrfHybNames(Set<CaArrayFile> dataFiles, MageTabDocumentSet mTabSet, Set<CaArrayFile> sdrfFiles) {
        if (mTabSet != null) {
            checkSdrfDataRelationshipNodeNames(dataFiles, mTabSet.getSdrfHybridizations(), sdrfFiles,
                    FileValidationResult.HYB_NAME);
        }
    }

    private <T extends AbstractSampleDataRelationshipNode> List<String> getNodeValueNames(String key,
            Map<String, List<T>> values) {
        final List<String> names = new ArrayList<String>();
        final List<T> mageTabNodeList = values.get(key);
        for (final T node : mageTabNodeList) {
            names.add(node.getName());
        }

        return names;
    }

    private <T extends AbstractSampleDataRelationshipNode> Map<String, List<T>> pruneOutDataFileNodeNames(
            Map<String, List<T>> sdrfNodeNames, List<String> dataFileNodeNames) {

        final Map<String, List<T>> updatedMap = new HashMap<String, List<T>>();

        for (final String key : sdrfNodeNames.keySet()) {
            final List<T> updatedList = new ArrayList<T>();
            final List<T> mageTabNodeList = sdrfNodeNames.get(key);

            for (final T sdrfNodeName : mageTabNodeList) {
                if (!dataFileNodeNames.contains(sdrfNodeName.getName())) {
                    updatedList.add(sdrfNodeName);
                }
            }

            updatedMap.put(key, updatedList);

        }
        return updatedMap;
    }

    private <T extends AbstractSampleDataRelationshipNode> void checkSdrfDataRelationshipNodeNames(
            Set<CaArrayFile> dataFiles, Map<String, List<T>> values, Set<CaArrayFile> sdrfFiles, String keyName) {
        // generate list of node names from data files and compare
        // to list of node names from the sdrf

        final List<String> dataFileNodeNames = collectDataRelationshipNodeNames(dataFiles, keyName);

        if (!dataFileNodeNames.isEmpty() && !values.isEmpty() && !values.values().isEmpty()) {

            doAddSdrfErrors(pruneOutDataFileNodeNames(values, dataFileNodeNames), keyName, sdrfFiles);
        }
    }

    private List<String> collectDataRelationshipNodeNames(Set<CaArrayFile> dataFiles, String keyName) {
        final List<String> dataFileRelationshipNodeNames = new ArrayList<String>();
        for (final CaArrayFile caf : dataFiles) {
            if (caf.getValidationResult() != null) {
                dataFileRelationshipNodeNames.addAll(getRelationshipNodeName(caf.getValidationResult(), keyName));
            }
        }

        return dataFileRelationshipNodeNames;
    }

    @SuppressWarnings("unchecked")
    private List<String> getRelationshipNodeName(FileValidationResult result, String keyName) {
        final List<String> names = new ArrayList<String>();
        if (result != null && result.getValidationProperties(keyName) != null) {
            names.addAll((List<String>) result.getValidationProperties(keyName));
        }
        return names;
    }

    private <T extends AbstractSampleDataRelationshipNode> void doAddSdrfErrors(Map<String, List<T>> values,
            String materialName, Set<CaArrayFile> sdrfFiles) {

        if (!values.isEmpty() && !values.values().isEmpty()) {
            // add error to matching SDRF
            for (final String mtSdrfName : values.keySet()) {
                doAddSdrfError(mtSdrfName, values, materialName, sdrfFiles);
            }
        }
    }

    private <T extends AbstractSampleDataRelationshipNode> void doAddSdrfError(String mtSdrfName,
            Map<String, List<T>> values, String materialName, Set<CaArrayFile> sdrfFiles) {
        for (final CaArrayFile sdrf : sdrfFiles) {
            final List<String> mtSdrfNodeNameValues = getNodeValueNames(mtSdrfName, values);
            if (mtSdrfName.equals(sdrf.getName()) && !mtSdrfNodeNameValues.isEmpty()) {
                sdrf.setFileStatus(FileStatus.VALIDATION_ERRORS);
                final StringBuilder errorMessage = new StringBuilder(materialName);
                errorMessage.append(" ");
                errorMessage.append(mtSdrfNodeNameValues.toString());
                errorMessage.append(" were not found in data files provided.");
                if (sdrf.getValidationResult() == null) {
                    sdrf.setValidationResult(new FileValidationResult());
                }
                sdrf.getValidationResult().addMessage(Type.ERROR, errorMessage.toString());
                this.fileDao.save(sdrf);
            }
        }
    }
}
