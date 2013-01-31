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
import gov.nih.nci.caarray.magetab.MageTabDocumentSet;
import gov.nih.nci.caarray.validation.InvalidDataFileException;

import java.util.HashSet;
import java.util.Iterator;
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
        Set<CaArrayFile> dataFiles = getDataFiles(fileSet);
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

    private Set<CaArrayFile> getDataFiles(CaArrayFileSet fileSet) {
        Set<CaArrayFile> dataFiles = new HashSet<CaArrayFile>();
        for (Iterator<CaArrayFile> fileIt = fileSet.getFiles().iterator(); fileIt.hasNext();) {
            CaArrayFile file = fileIt.next();
            if (isDataFile(file)) {
                dataFiles.add(file);
            }
        }
        return dataFiles;
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

    private boolean isDataFile(CaArrayFile file) {
        return file.getFileType().isArrayData();
    }

    void validateFiles(CaArrayFileSet fileSet, MageTabDocumentSet mTabSet) {
        Set<CaArrayFile> dataFiles = getDataFiles(fileSet);
        int fileCount = 0;
        int totalNumberOfFiles = dataFiles.size();
        for (CaArrayFile file : dataFiles) {
            LOG.info("Validating data file [" + ++fileCount + "/" + totalNumberOfFiles + "]: " + file.getName());
            validateFile(file, mTabSet);
        }
    }


    private void validateFile(CaArrayFile file, MageTabDocumentSet mTabSet) {
        this.arrayDataService.validate(file, mTabSet);
    }
}
