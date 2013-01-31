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
import gov.nih.nci.caarray.domain.project.AbstractFactorValue;
import gov.nih.nci.caarray.domain.project.Experiment;
import gov.nih.nci.caarray.domain.project.ExperimentContact;
import gov.nih.nci.caarray.domain.project.Factor;
import gov.nih.nci.caarray.domain.project.Project;
import gov.nih.nci.caarray.domain.sample.Extract;
import gov.nih.nci.caarray.domain.sample.LabeledExtract;
import gov.nih.nci.caarray.domain.sample.Sample;
import gov.nih.nci.caarray.domain.sample.Source;
import gov.nih.nci.caarray.domain.vocabulary.Term;
import gov.nih.nci.caarray.magetab.MageTabDocumentSet;
import gov.nih.nci.caarray.magetab.MageTabFileSet;
import gov.nih.nci.caarray.magetab.MageTabParser;
import gov.nih.nci.caarray.magetab.MageTabParsingException;
import gov.nih.nci.caarray.validation.FileValidationResult;
import gov.nih.nci.caarray.validation.InvalidDataException;
import gov.nih.nci.caarray.validation.ValidationMessage;
import gov.nih.nci.caarray.validation.ValidationResult;
import gov.nih.nci.caarray.validation.ValidationMessage.Type;

import java.io.File;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;

/**
 * Responsible for importing parsed MAGE-TAB data into caArray.
 */
@SuppressWarnings("PMD.TooManyMethods")
class MageTabImporter {

    private static final Logger LOG = Logger.getLogger(MageTabImporter.class);

    private final CaArrayDaoFactory daoFactory;
    private final MageTabTranslator translator;

    MageTabImporter(MageTabTranslator translator, CaArrayDaoFactory daoFactory) {
        this.translator = translator;
        this.daoFactory = daoFactory;
    }

    MageTabDocumentSet validateFiles(Project targetProject, CaArrayFileSet fileSet) {
        LOG.info("Validating MAGE-TAB document set");
        updateFileStatus(fileSet, FileStatus.VALIDATING);
        MageTabDocumentSet documentSet = null;
        MageTabFileSet inputSet = getInputFileSet(targetProject, fileSet);
        try {
            updateFileStatus(fileSet, FileStatus.VALIDATED);
            documentSet = MageTabParser.INSTANCE.parse(inputSet);
            handleResult(fileSet, translator.validate(documentSet, fileSet));
        } catch (MageTabParsingException e) {
            updateFileStatus(fileSet, FileStatus.VALIDATION_ERRORS);
        } catch (InvalidDataException e) {
            handleResult(fileSet, e.getValidationResult());
        }
        this.daoFactory.getSearchDao().save(fileSet.getFiles());
        return documentSet;
    }

    MageTabDocumentSet selectRefFiles(Project project, CaArrayFileSet idfFileSet) {
        MageTabDocumentSet documentSet = null;
        MageTabFileSet inputSet = getInputFileSet(project, idfFileSet);
        try {

            documentSet = MageTabParser.INSTANCE.parseDataFileNames(inputSet);
            handleSelectRefFilesResult(idfFileSet, translator.validate(documentSet, idfFileSet));
        } catch (MageTabParsingException e) {
            updateFileStatus(idfFileSet, FileStatus.VALIDATION_ERRORS);
        } catch (InvalidDataException e) {
            handleResult(idfFileSet, e.getValidationResult());
        }

        return documentSet;
    }

    private void handleSelectRefFilesResult(CaArrayFileSet fileSet, ValidationResult result) {

        for (FileValidationResult fileValidationResult : result.getFileValidationResults()) {
            CaArrayFile caArrayFile = fileSet.getFile(fileValidationResult.getFile());
            if (!result.isValid()) {
                // check whether any of the validation errors are other than data file checks
                saveErrorMessages(fileValidationResult, caArrayFile);
            }
        }
    }

    private void saveErrorMessages(FileValidationResult fileValidationResult,
            CaArrayFile caArrayFile) {
        FileValidationResult newResult =
            new FileValidationResult(getFile(caArrayFile));
        for (ValidationMessage vm : fileValidationResult.getMessages()) {
            if (vm.getType().equals(Type.ERROR)
                    && !Pattern.matches(".*Array Data.*not found in the document set.*", vm.getMessage())) {
                newResult.addMessage(Type.ERROR, vm.getMessage());
            }
        }

        if (newResult.getMessages().size() > 0) {
            caArrayFile.setFileStatus(FileStatus.VALIDATION_ERRORS);
            caArrayFile.setValidationResult(newResult);
            daoFactory.getProjectDao().save(caArrayFile);
        }
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

    void importFiles(Project targetProject, CaArrayFileSet fileSet)
            throws MageTabParsingException {
        LOG.info("Importing MAGE-TAB document set");
        updateFileStatus(fileSet, FileStatus.IMPORTING);
        MageTabFileSet inputSet = getInputFileSet(targetProject, fileSet);
        MageTabDocumentSet documentSet;
        try {
            documentSet = MageTabParser.INSTANCE.parse(inputSet);
            CaArrayTranslationResult translationResult = translator.translate(documentSet, fileSet);
            save(targetProject, translationResult);
            updateFileStatus(fileSet, FileStatus.IMPORTED);
        } catch (InvalidDataException e) {
            handleResult(fileSet, e.getValidationResult());
        }
        this.daoFactory.getSearchDao().save(fileSet.getFiles());
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

    private MageTabFileSet getInputFileSet(Project project, CaArrayFileSet fileSet) {
        CaArrayFileSet fullSet = new CaArrayFileSet(fileSet);
        fullSet.addAll(Collections2.filter(project.getImportedFiles(), new Predicate<CaArrayFile>() {
            public boolean apply(CaArrayFile f) {
                return f.getFileType().isArrayData() || FileType.MAGE_TAB_DATA_MATRIX.equals(f.getFileType());
            }
        }));

        MageTabFileSet inputFileSet = new MageTabFileSet();
        for (CaArrayFile caArrayFile : fullSet.getFiles()) {
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
        }
    }

    private void mergeTranslatedData(Experiment originalExperiment, Experiment translatedExperiment) {
        originalExperiment.getArrayDesigns().addAll(translatedExperiment.getArrayDesigns());
        originalExperiment.setDate(translatedExperiment.getDate());
        originalExperiment.setDescription(translatedExperiment.getDescription());
        mergeFactors(originalExperiment, translatedExperiment);
        originalExperiment.getExperimentDesignTypes().addAll(translatedExperiment.getExperimentDesignTypes());
        originalExperiment.getNormalizationTypes().addAll(translatedExperiment.getNormalizationTypes());
        originalExperiment.getPublications().addAll(translatedExperiment.getPublications());
        originalExperiment.getQualityControlTypes().addAll(translatedExperiment.getQualityControlTypes());
        originalExperiment.getReplicateTypes().addAll(translatedExperiment.getReplicateTypes());
        mergeExperimentContacts(originalExperiment, translatedExperiment);

        originalExperiment.getHybridizations().addAll(translatedExperiment.getHybridizations());
        getCaArrayDao().save(originalExperiment.getHybridizations());
        
        for (LabeledExtract le : translatedExperiment.getLabeledExtracts()) {
            le.setExperiment(originalExperiment);
            originalExperiment.getLabeledExtracts().add(le);
        }
        getCaArrayDao().save(originalExperiment.getLabeledExtracts());
        for (Extract e : translatedExperiment.getExtracts()) {
            e.setExperiment(originalExperiment);
            originalExperiment.getExtracts().add(e);
        }
        getCaArrayDao().save(originalExperiment.getExtracts());
        for (Sample s : translatedExperiment.getSamples()) {
            s.setExperiment(originalExperiment);
            originalExperiment.getSamples().add(s);
        }
        getCaArrayDao().save(originalExperiment.getSamples());
        for (Source s : translatedExperiment.getSources()) {
            s.setExperiment(originalExperiment);
            originalExperiment.getSources().add(s);
        }
        getCaArrayDao().save(originalExperiment.getSources());

    }

    private void mergeExperimentContacts(Experiment originalExperiment, Experiment translatedExperiment) {
        for (ExperimentContact translatedEc : translatedExperiment.getExperimentContacts()) {
            List<ExperimentContact> originalExperimentContacts = originalExperiment.getExperimentContacts();
            boolean isNewEc = true;
            for (ExperimentContact originalEc : originalExperimentContacts) {
                if (originalEc.equalsBaseContact(translatedEc)) {
                    isNewEc = false;
                    break;
                }
            }
            if (isNewEc) {
                translatedEc.setExperiment(originalExperiment);
                originalExperimentContacts.add(translatedEc);
            }
        }
    }

    private void mergeFactors(Experiment originalExperiment, Experiment translatedExperiment) {
        for (Factor translatedFactor : translatedExperiment.getFactors()) {
            boolean isNewFactor = true;
            Set<Factor> originalFactors = originalExperiment.getFactors();
            for (Factor originalFactor : originalFactors) {
                if (originalFactor.getName().equals(translatedFactor.getName())) {
                    isNewFactor = false;
                    for (AbstractFactorValue newValue : translatedFactor.getFactorValues()) {
                        newValue.setFactor(originalFactor);
                    }
                    break;
                }
            }
            if (isNewFactor) {
                originalFactors.add(translatedFactor);
            }
        }
    }

    private CaArrayDao getCaArrayDao() {
        return getProjectDao();
    }

    private ProjectDao getProjectDao() {
        return daoFactory.getProjectDao();
    }

}
