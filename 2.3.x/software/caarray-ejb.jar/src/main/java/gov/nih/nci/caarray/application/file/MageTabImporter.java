//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.application.file;

import gov.nih.nci.caarray.application.fileaccess.TemporaryFileCacheLocator;
import gov.nih.nci.caarray.application.translation.CaArrayTranslationResult;
import gov.nih.nci.caarray.application.translation.magetab.MageTabTranslator;
import gov.nih.nci.caarray.dao.CaArrayDao;
import gov.nih.nci.caarray.dao.CaArrayDaoFactory;
import gov.nih.nci.caarray.dao.ProjectDao;
import gov.nih.nci.caarray.domain.file.CaArrayFile;
import gov.nih.nci.caarray.domain.file.CaArrayFileSet;
import gov.nih.nci.caarray.domain.file.FileStatus;
import gov.nih.nci.caarray.domain.file.FileType;
import gov.nih.nci.caarray.domain.project.Experiment;
import gov.nih.nci.caarray.domain.project.ExperimentContact;
import gov.nih.nci.caarray.domain.project.Project;
import gov.nih.nci.caarray.domain.vocabulary.Term;
import gov.nih.nci.caarray.magetab.MageTabDocumentSet;
import gov.nih.nci.caarray.magetab.MageTabFileSet;
import gov.nih.nci.caarray.magetab.MageTabParser;
import gov.nih.nci.caarray.magetab.MageTabParsingException;
import gov.nih.nci.caarray.validation.FileValidationResult;
import gov.nih.nci.caarray.validation.InvalidDataException;
import gov.nih.nci.caarray.validation.ValidationResult;

import java.io.File;

import org.apache.log4j.Logger;

/**
 * Responsible for importing parsed MAGE-TAB data into caArray.
 */
class MageTabImporter {

    private static final Logger LOG = Logger.getLogger(MageTabImporter.class);

    private final CaArrayDaoFactory daoFactory;
    private final MageTabTranslator translator;

    MageTabImporter(MageTabTranslator translator, CaArrayDaoFactory daoFactory) {
        this.translator = translator;
        this.daoFactory = daoFactory;
    }

    MageTabDocumentSet validateFiles(CaArrayFileSet fileSet) {
        LOG.info("Validating MAGE-TAB document set");
        MageTabDocumentSet documentSet = null;
        updateFileStatus(fileSet, FileStatus.VALIDATING);
        MageTabFileSet inputSet = getInputFileSet(fileSet);
        try {
            updateFileStatus(fileSet, FileStatus.VALIDATED);
            handleResult(fileSet, MageTabParser.INSTANCE.validate(inputSet));
            if (!fileSet.statusesContains(FileStatus.VALIDATION_ERRORS)) {
                documentSet = MageTabParser.INSTANCE.parse(inputSet);
                handleResult(fileSet, translator.validate(documentSet, fileSet));
            }
        } catch (MageTabParsingException e) {
            updateFileStatus(fileSet, FileStatus.VALIDATION_ERRORS);
        } catch (InvalidDataException e) {
            handleInvalidMageTab(fileSet, e);
        }
        this.daoFactory.getSearchDao().save(fileSet.getFiles());
        return documentSet;
    }

    private void handleResult(CaArrayFileSet fileSet, ValidationResult result) {
        for (FileValidationResult fileValidationResult : result.getFileValidationResults()) {
            CaArrayFile caArrayFile = fileSet.getFile(fileValidationResult.getFile());
            if (!result.isValid()) {
                caArrayFile.setFileStatus(FileStatus.VALIDATION_ERRORS);
            } else {
                caArrayFile.setFileStatus(FileStatus.VALIDATED);
            }
            caArrayFile.setValidationResult(fileValidationResult);
            daoFactory.getProjectDao().save(caArrayFile);
        }
    }

    void importFiles(Project targetProject, CaArrayFileSet fileSet) throws MageTabParsingException {
        LOG.info("Importing MAGE-TAB document set");
        updateFileStatus(fileSet, FileStatus.IMPORTING);
        MageTabFileSet inputSet = getInputFileSet(fileSet);
        MageTabDocumentSet documentSet;
        try {
            documentSet = MageTabParser.INSTANCE.parse(inputSet);
            CaArrayTranslationResult translationResult = translator.translate(documentSet, fileSet);
            save(targetProject, translationResult);
            updateFileStatus(fileSet, FileStatus.IMPORTED);
        } catch (InvalidDataException e) {
            handleInvalidMageTab(fileSet, e);
        }
        this.daoFactory.getSearchDao().save(fileSet.getFiles());
    }

    private void handleInvalidMageTab(CaArrayFileSet fileSet, InvalidDataException e) {
        ValidationResult validationResult = e.getValidationResult();
        for (CaArrayFile caArrayFile : fileSet.getFiles()) {
            File file = getFile(caArrayFile);
            FileValidationResult fileValidationResult = validationResult.getFileValidationResult(file);
            if (fileValidationResult != null) {
                handleValidationResult(caArrayFile, fileValidationResult);
            }
        }
    }

    private void handleValidationResult(CaArrayFile caArrayFile, FileValidationResult fileValidationResult) {
        if (fileValidationResult.isValid()) {
            caArrayFile.setFileStatus(FileStatus.VALIDATED);
        } else {
            caArrayFile.setFileStatus(FileStatus.VALIDATION_ERRORS);
        }
        caArrayFile.setValidationResult(fileValidationResult);
        daoFactory.getProjectDao().save(caArrayFile);
    }

    private void updateFileStatus(CaArrayFileSet fileSet, FileStatus status) {
        CaArrayFileSet mageTabFileSet = new CaArrayFileSet(fileSet);
        for (CaArrayFile file : fileSet.getFiles()) {
            if (!isMageTabFile(file)) {
                mageTabFileSet.getFiles().remove(file);
            }
        }
        mageTabFileSet.updateStatus(status);
    }

    private boolean isMageTabFile(CaArrayFile file) {
        return FileType.MAGE_TAB_ADF.equals(file.getFileType())
        || FileType.MAGE_TAB_DATA_MATRIX.equals(file.getFileType())
        || FileType.MAGE_TAB_IDF.equals(file.getFileType())
        || FileType.MAGE_TAB_SDRF.equals(file.getFileType());
    }

    private MageTabFileSet getInputFileSet(CaArrayFileSet fileSet) {
        MageTabFileSet inputFileSet = new MageTabFileSet();
        for (CaArrayFile caArrayFile : fileSet.getFiles()) {
            addInputFile(inputFileSet, caArrayFile);
        }
        return inputFileSet;
    }

    private void addInputFile(MageTabFileSet inputFileSet, CaArrayFile caArrayFile) {
        FileType type = caArrayFile.getFileType();
        if (FileType.MAGE_TAB_IDF.equals(type)) {
            inputFileSet.addIdf(getFile(caArrayFile));
        } else if (FileType.MAGE_TAB_SDRF.equals(type)) {
            inputFileSet.addSdrf(getFile(caArrayFile));
        } else if (FileType.MAGE_TAB_ADF.equals(type)) {
            inputFileSet.addAdf(getFile(caArrayFile));
        } else if (FileType.MAGE_TAB_DATA_MATRIX.equals(type)) {
            inputFileSet.addDataMatrix(getFile(caArrayFile));
        } else {
            inputFileSet.addNativeData(getFile(caArrayFile));
        }
    }

    private File getFile(CaArrayFile caArrayFile) {
        return TemporaryFileCacheLocator.getTemporaryFileCache().getFile(caArrayFile);
    }

    private void save(Project targetProject, CaArrayTranslationResult translationResult) {
        saveTerms(translationResult);
        saveArrayDesigns(translationResult);
        saveInvestigations(targetProject, translationResult);
    }

    private void saveTerms(CaArrayTranslationResult translationResult) {
        for (Term term : translationResult.getTerms()) {
            getCaArrayDao().save(term);
        }
    }

    private void saveArrayDesigns(CaArrayTranslationResult translationResult) {
        getCaArrayDao().save(translationResult.getArrayDesigns());
    }

    private void saveInvestigations(Project targetProject, CaArrayTranslationResult translationResult) {
        // DEVELOPER NOTE: currently, importing multiple IDFs in a single import is not supported, and will
        // be flagged as a validation error. hence we can assume that only a single investigation is present in the
        // translation result. In the future we may support multiple experiments per projects, and therefore
        // multi-IDF import. Hence, continue to allow for this in the object model.
        if (!translationResult.getInvestigations().isEmpty()) {
            mergeTranslatedData(targetProject.getExperiment(), translationResult.getInvestigations().iterator().next());
            getProjectDao().save(targetProject);
        }
    }

    private void mergeTranslatedData(Experiment originalExperiment, Experiment translatedExperiment) {
        originalExperiment.getArrayDesigns().addAll(translatedExperiment.getArrayDesigns());
        originalExperiment.setDate(translatedExperiment.getDate());
        originalExperiment.setDescription(translatedExperiment.getDescription());
        originalExperiment.getExtracts().addAll(translatedExperiment.getExtracts());
        originalExperiment.getFactors().addAll(translatedExperiment.getFactors());
        originalExperiment.getHybridizations().addAll(translatedExperiment.getHybridizations());
        originalExperiment.getLabeledExtracts().addAll(translatedExperiment.getLabeledExtracts());
        originalExperiment.getExperimentDesignTypes().addAll(translatedExperiment.getExperimentDesignTypes());
        originalExperiment.getNormalizationTypes().addAll(translatedExperiment.getNormalizationTypes());
        originalExperiment.getPublications().addAll(translatedExperiment.getPublications());
        originalExperiment.getQualityControlTypes().addAll(translatedExperiment.getQualityControlTypes());
        originalExperiment.getReplicateTypes().addAll(translatedExperiment.getReplicateTypes());
        originalExperiment.getSamples().addAll(translatedExperiment.getSamples());
        originalExperiment.getSources().addAll(translatedExperiment.getSources());
        originalExperiment.getExperimentContacts().addAll(translatedExperiment.getExperimentContacts());
        for (ExperimentContact ec : translatedExperiment.getExperimentContacts()) {
            ec.setExperiment(originalExperiment);
        }
    }

    private CaArrayDao getCaArrayDao() {
        return getProjectDao();
    }

    private ProjectDao getProjectDao() {
        return daoFactory.getProjectDao();
    }

}
