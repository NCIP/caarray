//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package gov.nih.nci.caarray.application.file;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import gov.nih.nci.caarray.domain.array.ArrayDesign;
import gov.nih.nci.caarray.domain.file.CaArrayFile;
import gov.nih.nci.caarray.domain.file.FileType;
import gov.nih.nci.caarray.domain.project.Project;
import gov.nih.nci.caarray.validation.FileValidationResult;
import gov.nih.nci.caarray.validation.ValidationMessage;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Transaction;

public abstract class AbstractFileValidationIntegrationTest extends AbstractFileManagementServiceIntegrationTest {
    protected static final class FileFileTypeWrapper {
        final File file;
        final FileType fileType;

        public FileFileTypeWrapper(final File file, final FileType fileType) {
            this.file = file;
            this.fileType = fileType;
        }

    }

    protected void doValidation(FileFileTypeWrapper[] datafiles, FileFileTypeWrapper arrayDesign,
            final FileType[] invalidFileTypes, final List<String[]> expectedErrorsForFileTypes) throws Exception {
        List<String[]> expectedWarnings = new ArrayList<String[]>(expectedErrorsForFileTypes.size());
        for (int i = 0; i < expectedErrorsForFileTypes.size(); ++i) {
            expectedWarnings.add(new String[] {});
        }
        
        doValidation(datafiles, arrayDesign, invalidFileTypes, expectedErrorsForFileTypes, expectedWarnings);
    }
    
    protected void doValidation(FileFileTypeWrapper[] datafiles, FileFileTypeWrapper arrayDesign,
            final FileType[] invalidFileTypes, final List<String[]> expectedErrorsForFileTypes,
            final List<String[]> expectedWarningsForFileTypes) throws Exception {
        final Map<File, FileType> files = new HashMap<File, FileType>();
        for (final FileFileTypeWrapper datafileWrapper : datafiles) {
            files.put(datafileWrapper.file, datafileWrapper.fileType);
        }
        final ArrayDesign design = importArrayDesign(arrayDesign.file, arrayDesign.fileType);
        addDesignToExperiment(design);
        uploadAndValidateFiles(files);
        final Transaction tx = this.hibernateHelper.beginTransaction();
        final Project project = getTestProject();
        int expectedErrorFileCounter = 0;
        for (final CaArrayFile caArrayFile : project.getFiles()) {
            if (isIn(caArrayFile.getFileType(), invalidFileTypes)) {
                validateFileHasExpectedValidationMessages(caArrayFile, ValidationMessage.Type.ERROR,
                        expectedErrorsForFileTypes.get(expectedErrorFileCounter));
                validateFileHasExpectedValidationMessages(caArrayFile, ValidationMessage.Type.WARNING,
                        expectedWarningsForFileTypes.get(expectedErrorFileCounter));
                expectedErrorFileCounter++;
            }
        }
        tx.commit();
    }

    private boolean isIn(final FileType targetFileType, final FileType[] fileTypesToCheck) {
        for (final FileType fileType : fileTypesToCheck) {
            if (fileType.equals(targetFileType)) {
                return true;
            }
        }
        return false;
    }

    private void validateFileHasExpectedValidationMessages(final CaArrayFile file, ValidationMessage.Type type, 
            final String[] expectedMessages) {
        final FileValidationResult validationResult = file.getValidationResult();
        int validationSize = (validationResult == null) ? 0 : validationResult.getMessages(type).size();
        assertEquals("Wrong number of validation messages for file=" + file.getName() + ", validation message type=" 
                + type + ", messages=" + validationResult, 
                expectedMessages.length,
                validationSize);
        final List<String> validationMessagesAsList = getValidationMessagesAsList(validationResult, type);
        for (final String expectedMessage : expectedMessages) {
            assertTrue("The expected message '" + expectedMessage + "' was not found.",
                    validationMessagesAsList.contains(expectedMessage));
        }
    }

    private List<String> getValidationMessagesAsList(final FileValidationResult validationResult, 
            ValidationMessage.Type type) {
        final List<String> validationMessagesAsList = new ArrayList<String>();
        if (validationResult != null) {
            for (final ValidationMessage validationMessage : validationResult.getMessages(type)) {
                validationMessagesAsList.add(validationMessage.getMessage());
            }
        }
        return validationMessagesAsList;
    }
}
