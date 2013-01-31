//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.application.arraydesign;

import gov.nih.nci.caarray.application.fileaccess.TemporaryFileCacheLocator;
import gov.nih.nci.caarray.business.vocabulary.VocabularyService;
import gov.nih.nci.caarray.dao.ArrayDao;
import gov.nih.nci.caarray.dao.CaArrayDaoFactory;
import gov.nih.nci.caarray.dao.SearchDao;
import gov.nih.nci.caarray.domain.array.ArrayDesign;
import gov.nih.nci.caarray.domain.file.CaArrayFile;
import gov.nih.nci.caarray.domain.file.FileStatus;
import gov.nih.nci.caarray.validation.ValidationResult;
import gov.nih.nci.caarray.validation.ValidationMessage.Type;

import java.io.File;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import org.apache.log4j.Logger;

/**
 * Base class for all design handlers.
 */
abstract class AbstractArrayDesignHandler {

    private final Set<CaArrayFile> designFiles = new HashSet<CaArrayFile>();
    private final VocabularyService vocabularyService;
    private final CaArrayDaoFactory daoFactory;

    AbstractArrayDesignHandler(VocabularyService vocabularyService,
            CaArrayDaoFactory daoFactory, CaArrayFile designFiles) {
        this.designFiles.add(designFiles);
        this.vocabularyService = vocabularyService;
        this.daoFactory = daoFactory;
    }

    AbstractArrayDesignHandler(VocabularyService vocabularyService,
            CaArrayDaoFactory daoFactory, Set<CaArrayFile> designFiles) {
        this.designFiles.addAll(designFiles);
        this.vocabularyService = vocabularyService;
        this.daoFactory = daoFactory;
    }

    final Set<CaArrayFile> getDesignFiles() {
        return designFiles;
    }

    final CaArrayFile getDesignFile() {
        return designFiles.isEmpty() ? null : designFiles.iterator().next();
    }

    final VocabularyService getVocabularyService() {
        return vocabularyService;
    }

    final File getFile() {
        return TemporaryFileCacheLocator.getTemporaryFileCache().getFile(
                getDesignFile());
    }

    final File getFile(CaArrayFile file) {
        return TemporaryFileCacheLocator.getTemporaryFileCache().getFile(file);
    }

    final CaArrayFile getDesignFile(String suffix) {
        for (CaArrayFile designFile : designFiles) {
            if (designFile.getName().toLowerCase(Locale.getDefault()).endsWith(
                    suffix.toLowerCase(Locale.getDefault()))) {
                return designFile;
            }
        }
        return null;
    }

    final File getFile(String suffix) {
        CaArrayFile file = getDesignFile(suffix);
        if (file != null) {
            return TemporaryFileCacheLocator.getTemporaryFileCache().getFile(
                    file);
        } else {
            return null;
        }
    }

    abstract void load(ArrayDesign arrayDesign);

    abstract void createDesignDetails(ArrayDesign arrayDesign);

    final ValidationResult validate() {
        ValidationResult result = new ValidationResult();
        try {
            validate(result);
        } catch (RuntimeException e) {
            getLog().error("Unexpected error validating file", e);
            result.addMessage(getFile(), Type.ERROR,
                    "Unexpected error validating file: " + e.getMessage());
        }
        return result;
    }

    abstract Logger getLog();

    abstract void validate(ValidationResult result);

    CaArrayDaoFactory getDaoFactory() {
        return daoFactory;
    }

    ArrayDao getArrayDao() {
        return getDaoFactory().getArrayDao();
    }

    SearchDao getSearchDao() {
        return getDaoFactory().getSearchDao();
    }

    void flushAndClearSession() {
        getArrayDao().flushSession();
        getArrayDao().clearSession();
    }

    FileStatus getValidationErrorStatus() {
        return FileStatus.VALIDATION_ERRORS;
    }

    FileStatus getValidatedStatus() {
        return FileStatus.VALIDATED;
    }

}
