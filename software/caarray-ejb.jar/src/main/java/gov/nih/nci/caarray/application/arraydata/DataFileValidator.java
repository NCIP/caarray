//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.application.arraydata;

import gov.nih.nci.caarray.application.arraydesign.ArrayDesignService;
import gov.nih.nci.caarray.application.fileaccess.TemporaryFileCacheLocator;
import gov.nih.nci.caarray.dao.CaArrayDaoFactory;
import gov.nih.nci.caarray.domain.file.CaArrayFile;
import gov.nih.nci.caarray.domain.file.FileStatus;
import gov.nih.nci.caarray.magetab.MageTabDocumentSet;
import gov.nih.nci.caarray.validation.FileValidationResult;

import java.io.File;

/**
 * Responsible for validation of arbitrary array data files.
 */
final class DataFileValidator {

    private final CaArrayFile arrayDataFile;
    private final CaArrayDaoFactory daoFactory;
    private final ArrayDesignService arrayDesignService;
    private final MageTabDocumentSet mTabSet;

    DataFileValidator(CaArrayFile arrayDataFile, MageTabDocumentSet mTabSet,
            CaArrayDaoFactory daoFactory, ArrayDesignService arrayDesignService) {
        this.arrayDataFile = arrayDataFile;
        this.daoFactory = daoFactory;
        this.arrayDesignService = arrayDesignService;
        this.mTabSet = mTabSet;
    }

    void validate() {
        AbstractDataFileHandler handler =
            ArrayDataHandlerFactory.getInstance().getHandler(getArrayDataFile().getFileType());
        File file = TemporaryFileCacheLocator.getTemporaryFileCache().getFile(arrayDataFile);
        FileValidationResult result = handler.validate(arrayDataFile, file, mTabSet, arrayDesignService);
        getArrayDataFile().setValidationResult(result);
        if (result.isValid()) {
            getArrayDataFile().setFileStatus(handler.getValidatedStatus());
        } else {
            getArrayDataFile().setFileStatus(FileStatus.VALIDATION_ERRORS);
        }
        getDaoFactory().getArrayDao().save(arrayDataFile);
    }

    private CaArrayFile getArrayDataFile() {
        return arrayDataFile;
    }

    private CaArrayDaoFactory getDaoFactory() {
        return daoFactory;
    }
}
