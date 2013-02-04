//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package gov.nih.nci.caarray.application.file;

import gov.nih.nci.caarray.application.translation.CaArrayTranslationResult;
import gov.nih.nci.caarray.application.translation.magetab.MageTabTranslator;
import gov.nih.nci.caarray.dao.ProjectDao;
import gov.nih.nci.caarray.dao.SearchDao;
import gov.nih.nci.caarray.dataStorage.DataStorageFacade;
import gov.nih.nci.caarray.domain.file.CaArrayFile;
import gov.nih.nci.caarray.domain.file.CaArrayFileSet;
import gov.nih.nci.caarray.domain.file.FileCategory;
import gov.nih.nci.caarray.domain.file.FileStatus;
import gov.nih.nci.caarray.domain.file.FileType;
import gov.nih.nci.caarray.domain.file.FileTypeRegistry;
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
import gov.nih.nci.caarray.magetab.io.FileRef;
import gov.nih.nci.caarray.validation.FileValidationResult;
import gov.nih.nci.caarray.validation.InvalidDataException;
import gov.nih.nci.caarray.validation.ValidationMessage;
import gov.nih.nci.caarray.validation.ValidationMessage.Type;
import gov.nih.nci.caarray.validation.ValidationResult;

import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.inject.Inject;

/**
 * Responsible for importing parsed MAGE-TAB data into caArray.
 */
@SuppressWarnings("PMD.TooManyMethods")
class MageTabImporterImpl implements MageTabImporter {

    private static final Logger LOG = Logger.getLogger(MageTabImporter.class);

    private final MageTabTranslator translator;
    private final SearchDao searchDao;
    private final ProjectDao projectDao;
    private final DataStorageFacade dataStorageFacade;

    @Inject
    MageTabImporterImpl(MageTabTranslator translator, SearchDao searchDao, ProjectDao projectDao,
            DataStorageFacade dataStorageFacade) {
        this.translator = translator;
        this.searchDao = searchDao;
        this.projectDao = projectDao;
        this.dataStorageFacade = dataStorageFacade;
    }

    public MageTabDocumentSet validateFiles(Project targetProject, CaArrayFileSet fileSet) {
        LOG.info("Validating MAGE-TAB document set");
        updateFileStatus(fileSet, FileStatus.VALIDATING);
        MageTabDocumentSet documentSet = null;
        final MageTabFileSet inputSet = getInputFileSet(targetProject, fileSet);
        try {
            updateFileStatus(fileSet, FileStatus.VALIDATED);
            documentSet = MageTabParser.INSTANCE.parse(inputSet);
            handleResult(fileSet, this.translator.validate(documentSet, fileSet));
        } catch (final MageTabParsingException e) {
            updateFileStatus(fileSet, FileStatus.VALIDATION_ERRORS);
        } catch (final InvalidDataException e) {
            handleResult(fileSet, e.getValidationResult());
        }
        this.searchDao.save(fileSet.getFiles());
        return documentSet;
    }

    public MageTabDocumentSet selectRefFiles(Project project, CaArrayFileSet idfFileSet) {
        MageTabDocumentSet documentSet = null;
        final MageTabFileSet inputSet = getInputFileSet(project, idfFileSet);
        try {

            documentSet = MageTabParser.INSTANCE.parseDataFileNames(inputSet);
            handleSelectRefFilesResult(idfFileSet, this.translator.validate(documentSet, idfFileSet));
        } catch (final MageTabParsingException e) {
            updateFileStatus(idfFileSet, FileStatus.VALIDATION_ERRORS);
        } catch (final InvalidDataException e) {
            handleResult(idfFileSet, e.getValidationResult());
        }

        return documentSet;
    }

    private void handleSelectRefFilesResult(CaArrayFileSet fileSet, ValidationResult result) {
        for (final String fileName : result.getFileNames()) {
            final CaArrayFile caArrayFile = fileSet.getFile(fileName);
            final FileValidationResult fileResult = result.getFileValidationResult(fileName);
            if (!fileResult.isValid()) {
                // check whether any of the validation errors are other than data file checks
                saveErrorMessages(fileResult, caArrayFile);
            }
        }
    }

    private void saveErrorMessages(FileValidationResult fileValidationResult, CaArrayFile caArrayFile) {
        final FileValidationResult newResult = new FileValidationResult();
        for (final ValidationMessage vm : fileValidationResult.getMessages()) {
            if (vm.getType().equals(Type.ERROR)
                    && !Pattern.matches(".*Array Data.*not found in the document set.*", vm.getMessage())) {
                newResult.addMessage(Type.ERROR, vm.getMessage());
            }
        }

        if (newResult.getMessages().size() > 0) {
            caArrayFile.setFileStatus(FileStatus.VALIDATION_ERRORS);
            caArrayFile.setValidationResult(newResult);
            this.projectDao.save(caArrayFile);
        }
    }

    private void handleResult(CaArrayFileSet fileSet, ValidationResult result) {
        for (final String fileName : result.getFileNames()) {
            final CaArrayFile caArrayFile = fileSet.getFile(fileName);
            final FileValidationResult fileResult = result.getFileValidationResult(fileName);
            if (!fileResult.isValid()) {
                caArrayFile.setFileStatus(FileStatus.VALIDATION_ERRORS);
            } else {
                caArrayFile.setFileStatus(FileStatus.VALIDATED);
            }
            caArrayFile.setValidationResult(fileResult);
            this.projectDao.save(caArrayFile);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MageTabDocumentSet importFiles(Project targetProject, CaArrayFileSet fileSet)
            throws MageTabParsingException {
        LOG.info("Importing MAGE-TAB document set");
        updateFileStatus(fileSet, FileStatus.IMPORTING);
        final MageTabFileSet inputSet = getInputFileSet(targetProject, fileSet);
        MageTabDocumentSet documentSet = null;
        try {
            documentSet = MageTabParser.INSTANCE.parse(inputSet);
            final CaArrayTranslationResult translationResult = this.translator.translate(documentSet, fileSet);
            save(targetProject, translationResult);
            updateFileStatus(fileSet, FileStatus.IMPORTED);
        } catch (final InvalidDataException e) {
            handleResult(fileSet, e.getValidationResult());
        }
        this.searchDao.save(fileSet.getFiles());
        return documentSet;
    }

    private void updateFileStatus(CaArrayFileSet fileSet, FileStatus status) {
        final CaArrayFileSet mageTabFileSet = new CaArrayFileSet(fileSet);
        for (final CaArrayFile file : fileSet.getFiles()) {
            if (!isMageTabFile(file)) {
                mageTabFileSet.getFiles().remove(file);
            }
        }
        mageTabFileSet.updateStatus(status);
    }

    private boolean isMageTabFile(CaArrayFile file) {
        return FileCategory.MAGE_TAB == file.getFileType().getCategory();
    }

    private MageTabFileSet getInputFileSet(Project project, CaArrayFileSet fileSet) {
        final CaArrayFileSet fullSet = new CaArrayFileSet(fileSet);
        fullSet.addAll(Collections2.filter(project.getImportedFiles(), new Predicate<CaArrayFile>() {
            @Override
            public boolean apply(CaArrayFile f) {
                return f.getFileType().isArrayData();
            }
        }));

        final MageTabFileSet inputFileSet = new MageTabFileSet();
        for (final CaArrayFile caArrayFile : fullSet.getFiles()) {
            addInputFile(inputFileSet, caArrayFile);
        }

        return inputFileSet;
    }

    private void addInputFile(MageTabFileSet inputFileSet, CaArrayFile caArrayFile) {
        final FileType type = caArrayFile.getFileType();
        final FileRef fileRef = new CaArrayFileRef(caArrayFile, this.dataStorageFacade);
        if (FileTypeRegistry.MAGE_TAB_IDF.equals(type)) {
            inputFileSet.addIdf(fileRef);
        } else if (FileTypeRegistry.MAGE_TAB_SDRF.equals(type)) {
            inputFileSet.addSdrf(fileRef);
        } else if (type.isDataMatrix()) {
            inputFileSet.addDataMatrix(fileRef);
        } else {
            inputFileSet.addNativeData(fileRef);
        }
    }

    private void save(Project targetProject, CaArrayTranslationResult translationResult) {
        saveTerms(translationResult);
        saveArrayDesigns(translationResult);
        saveInvestigations(targetProject, translationResult);
    }

    private void saveTerms(CaArrayTranslationResult translationResult) {
        for (final Term term : translationResult.getTerms()) {
            this.projectDao.save(term);
        }
    }

    private void saveArrayDesigns(CaArrayTranslationResult translationResult) {
        this.projectDao.save(translationResult.getArrayDesigns());
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
        this.projectDao.save(originalExperiment.getHybridizations());

        for (final LabeledExtract le : translatedExperiment.getLabeledExtracts()) {
            le.setExperiment(originalExperiment);
            originalExperiment.getLabeledExtracts().add(le);
        }
        this.projectDao.save(originalExperiment.getLabeledExtracts());
        for (final Extract e : translatedExperiment.getExtracts()) {
            e.setExperiment(originalExperiment);
            originalExperiment.getExtracts().add(e);
        }
        this.projectDao.save(originalExperiment.getExtracts());
        for (final Sample s : translatedExperiment.getSamples()) {
            s.setExperiment(originalExperiment);
            originalExperiment.getSamples().add(s);
        }
        this.projectDao.save(originalExperiment.getSamples());
        for (final Source s : translatedExperiment.getSources()) {
            s.setExperiment(originalExperiment);
            originalExperiment.getSources().add(s);
        }
        this.projectDao.save(originalExperiment.getSources());

    }

    private void mergeExperimentContacts(Experiment originalExperiment, Experiment translatedExperiment) {
        for (final ExperimentContact translatedEc : translatedExperiment.getExperimentContacts()) {
            final List<ExperimentContact> originalExperimentContacts = originalExperiment.getExperimentContacts();
            boolean isNewEc = true;
            for (final ExperimentContact originalEc : originalExperimentContacts) {
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
        for (final Factor translatedFactor : translatedExperiment.getFactors()) {
            boolean isNewFactor = true;
            final Set<Factor> originalFactors = originalExperiment.getFactors();
            for (final Factor originalFactor : originalFactors) {
                if (originalFactor.getName().equals(translatedFactor.getName())) {
                    isNewFactor = false;
                    for (final AbstractFactorValue newValue : translatedFactor.getFactorValues()) {
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
}
