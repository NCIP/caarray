//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.application.arraydesign;

import gov.nih.nci.caarray.dao.ArrayDao;
import gov.nih.nci.caarray.dao.SearchDao;
import gov.nih.nci.caarray.domain.array.ArrayDesign;
import gov.nih.nci.caarray.domain.file.CaArrayFile;
import gov.nih.nci.caarray.domain.file.FileStatus;
import gov.nih.nci.caarray.platforms.spi.DesignFileHandler;
import gov.nih.nci.caarray.platforms.spi.PlatformFileReadException;
import gov.nih.nci.caarray.validation.FileValidationResult;
import gov.nih.nci.caarray.validation.InvalidNumberOfArgsException;
import gov.nih.nci.caarray.validation.ValidationMessage.Type;
import gov.nih.nci.caarray.validation.ValidationResult;

import java.util.HashSet;
import java.util.Set;

import com.google.inject.Inject;

/**
 * Helper class for performing various array design operations. Services as a facade for platform-specific
 * functionality.
 * 
 * @author dkokotov
 */
@SuppressWarnings({ "PMD.CyclomaticComplexity" })
final class ArrayDesignPlatformFacade {
    private final Set<DesignFileHandler> handlers;
    private final ArrayDao arrayDao;
    private final SearchDao searchDao;

    /**
     * @param handlers
     */
    @Inject
    ArrayDesignPlatformFacade(ArrayDao arrayDao, SearchDao searchDao, Set<DesignFileHandler> handlers) {
        this.handlers = new HashSet<DesignFileHandler>(handlers);
        this.arrayDao = arrayDao;
        this.searchDao = searchDao;
    }

    private DesignFileHandler getHandler(Set<CaArrayFile> designFiles) throws PlatformFileReadException {
        for (final DesignFileHandler handler : this.handlers) {
            try {
                if (handler.openFiles(designFiles)) {
                    return handler;
                }
            } catch (final PlatformFileReadException e) {
                handler.closeFiles();
                throw e;
            }
        }
        throw new InvalidNumberOfArgsException(InvalidNumberOfArgsException.UNSUPPORTED_ARRAY_DESIGN);
    }

    @SuppressWarnings({ "PMD.CyclomaticComplexity", "PMD.NPathComplexity", "PMD.ExcessiveMethodLength" })
    ValidationResult validateDesignFiles(Set<CaArrayFile> designFiles) {
        final ValidationResult result = new ValidationResult();
        for (final CaArrayFile designFile : designFiles) {
            final FileValidationResult fileResult = new FileValidationResult();
            if (designFile.getType() == null) {
                designFile.setValidationResult(fileResult);
                fileResult.addMessage(Type.ERROR,
                        "Array design file type was unrecognized, please select a file format");
                result.addFile(designFile.getName(), fileResult);
            } else if (!designFile.getFileType().isArrayDesign()) {
                designFile.setValidationResult(fileResult);
                fileResult.addMessage(Type.ERROR, "File type " + designFile.getFileType().getName()
                        + " is not an array design type.");
                result.addFile(designFile.getName(), fileResult);
            }

        }

        FileStatus validatedStatus = FileStatus.VALIDATED;
        if (result.isValid()) {
            DesignFileHandler handler = null;
            try {
                handler = getHandler(designFiles);
                if (!handler.parsesData()) {
                    validatedStatus = FileStatus.VALIDATED_NOT_PARSED;
                }
                handler.validate(result);
            } catch (final PlatformFileReadException e) {
                final CaArrayFile firstDesignFile = designFiles.iterator().next();
                FileValidationResult fileResult = firstDesignFile.getValidationResult();
                if (fileResult == null) {
                    fileResult = new FileValidationResult();
                    firstDesignFile.setValidationResult(fileResult);
                }
                fileResult.addMessage(Type.ERROR, "Unable to read file");
                result.addFile(firstDesignFile.getName(), fileResult);
            } finally {
                if (handler != null) {
                    handler.closeFiles();
                }
            }
        }

        final FileStatus status = result.isValid() ? validatedStatus : FileStatus.VALIDATION_ERRORS;
        for (final CaArrayFile designFile : designFiles) {
            designFile.setFileStatus(status);
            this.arrayDao.save(designFile);
        }
        this.arrayDao.flushSession();

        return result;
    }

    @SuppressWarnings("PMD.AvoidReassigningParameters")
    void importDesignDetails(ArrayDesign arrayDesign) {
        DesignFileHandler handler = null;
        try {
            handler = getHandler(arrayDesign.getDesignFiles());
            handler.createDesignDetails(arrayDesign);
        } catch (final PlatformFileReadException e) {
            throw new IllegalStateException("Error importing design", e);
        } finally {
            if (handler != null) {
                handler.closeFiles();
            }
        }

        // the handler cleared the session, so we need to merge before we update the status
        // See hibernate bug http://opensource.atlassian.com/projects/hibernate/browse/HHH-511
        // When we upgrade to hibernate 3.2.4+, we can remove the call to merge.
        //commenting out as part of merging 2.4.1.x to trunk. Step 3.
        /*
        arrayDesign = (ArrayDesign) this.arrayDao.mergeObject(arrayDesign);
        arrayDesign.getDesignFileSet().updateStatus(
                arrayDesign.getDesignFiles().iterator().next().getFileType().isParsed() ? FileStatus.IMPORTED
                        : FileStatus.IMPORTED_NOT_PARSED);
         */
        arrayDesign = searchDao.retrieve(ArrayDesign.class, arrayDesign.getId());
        arrayDesign.getDesignFileSet().updateStatus(
                handler.parsesData() ? FileStatus.IMPORTED : FileStatus.IMPORTED_NOT_PARSED);
        this.arrayDao.save(arrayDesign);
    }

    void importDesign(ArrayDesign arrayDesign) {
        DesignFileHandler handler = null;
        try {
            handler = getHandler(arrayDesign.getDesignFiles());
            handler.load(arrayDesign);
        } catch (final PlatformFileReadException e) {
            throw new IllegalStateException("Error importing design", e);
        } finally {
            if (handler != null) {
                handler.closeFiles();
            }
        }
    }
}
