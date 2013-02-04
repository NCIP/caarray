//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package gov.nih.nci.caarray.application.arraydata;

import gov.nih.nci.caarray.dao.ArrayDao;
import gov.nih.nci.caarray.domain.array.ArrayDesign;
import gov.nih.nci.caarray.domain.file.CaArrayFile;
import gov.nih.nci.caarray.domain.file.FileStatus;
import gov.nih.nci.caarray.domain.project.Experiment;
import gov.nih.nci.caarray.magetab.MageTabDocumentSet;
import gov.nih.nci.caarray.platforms.spi.DataFileHandler;
import gov.nih.nci.caarray.platforms.spi.PlatformFileReadException;
import gov.nih.nci.caarray.platforms.unparsed.FallbackUnparsedDataHandler;
import gov.nih.nci.caarray.validation.FileValidationResult;
import gov.nih.nci.caarray.validation.ValidationMessage.Type;

import java.util.Set;

import org.apache.log4j.Logger;

import com.google.inject.Inject;
import com.google.inject.Provider;

/**
 * Helper class for validating array data files.
 * 
 * @author dkokotov
 */
@SuppressWarnings("PMD.CyclomaticComplexity")
final class DataFileValidator extends AbstractArrayDataUtility {
    private static final Logger LOG = Logger.getLogger(DataFileValidator.class);

    @Inject
    DataFileValidator(ArrayDao arrayDao, Set<DataFileHandler> handlers,
            Provider<FallbackUnparsedDataHandler> fallbackHandlerProvider) {
        super(arrayDao, handlers, fallbackHandlerProvider);
    }

    void validate(CaArrayFile caArrayFile, MageTabDocumentSet mTabSet, boolean reimport) {
        DataFileHandler handler = null;
        try {
            final FileValidationResult result = new FileValidationResult();
            
            try {
                handler = findAndSetupHandler(caArrayFile, mTabSet);
                assert handler != null : "findAndSetupHandler must never return null";
                if (!reimport && handler.requiresMageTab()) {
                    validateMageTabPresent(mTabSet, result);
                }
                if (result.isValid()) {
                    final ArrayDesign design = getArrayDesign(caArrayFile, handler);
                    if (design != null && design.isUnparsedAndReimportable()) {
                        result.addMessage(Type.ERROR, "Associated array design " + design.getName()
                                + " must be re-parsed");
                    } else {
                        handler.validate(mTabSet, result, design);
                        if (result.isValid()) {
                            validateArrayDesignInExperiment(caArrayFile, result, handler);
                        }
                    }
                }
            } catch (final PlatformFileReadException e) {
                LOG.error("Error obtaining a data handler for validating data file", e);
                result.addMessage(Type.ERROR, "File is not a valid file of type " + caArrayFile.getFileType() + ": "
                        + e.getMessage());
            } catch (final RuntimeException e) {
                LOG.error("Unexpected RuntimeException validating data file", e);
                result.addMessage(Type.ERROR, "Unexpected error validating data file: " + e.getMessage());
            }
            caArrayFile.setValidationResult(result);
            if (result.isValid()) {
                caArrayFile.setFileStatus(
                        handler.parsesData() ? FileStatus.VALIDATED : FileStatus.VALIDATED_NOT_PARSED);
            } else {
                caArrayFile.setFileStatus(FileStatus.VALIDATION_ERRORS);
            }
            getArrayDao().save(caArrayFile);
        } finally {
            if (handler != null) {
                handler.closeFiles();
            }
        }
    }

    private void validateMageTabPresent(MageTabDocumentSet mTabSet, FileValidationResult result) {
        if (mTabSet == null || mTabSet.getIdfDocuments().isEmpty() || mTabSet.getSdrfDocuments().isEmpty()) {
            result.addMessage(Type.ERROR, "An IDF and SDRF must be provided for this data file type.");
        }
    }

    private void validateArrayDesignInExperiment(CaArrayFile caArrayFile, FileValidationResult result,
            DataFileHandler handler) throws PlatformFileReadException {
        final ArrayDesign design = getArrayDesign(caArrayFile, handler);
        if (design == null) {
            Experiment experiment = caArrayFile.getProject().getExperiment();
            if (experiment.getArrayDesigns().size() > 1) {
                result.addMessage(Type.ERROR, "This experiment has multiple array designs. "
                        + "Please explicitly associate this data file with the appropriate array design in the sdrf");
            } else if (caArrayFile.getFileType().isParseableData()) {
                result.addMessage(Type.ERROR, "The array design referenced by this data file could not be found.");
            }
        } else if (!caArrayFile.getProject().getExperiment().getArrayDesigns().contains(design)) {
            result.addMessage(Type.ERROR, "The array design referenced by this data file (" + design.getName()
                    + ") is not associated with this experiment");
        }
    }
}
