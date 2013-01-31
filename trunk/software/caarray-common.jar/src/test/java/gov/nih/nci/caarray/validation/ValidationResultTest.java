//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.validation;

import static org.junit.Assert.*;

import gov.nih.nci.caarray.AbstractCaarrayTest;

import java.io.File;
import java.util.List;

import org.junit.Test;

public class ValidationResultTest extends AbstractCaarrayTest {

    private static final String TEST_MESSAGE = "message";

    private final File testFile = new File("path/filename.txt");

    @Test
    public void testIsValid() {
        ValidationResult result = new ValidationResult();
        assertTrue(result.isValid());
        result.addMessage(testFile , ValidationMessage.Type.INFO, TEST_MESSAGE + "1");
        assertTrue(result.isValid());
        result.addMessage(testFile , ValidationMessage.Type.WARNING, TEST_MESSAGE + "2");
        assertTrue(result.isValid());
        result.addMessage(testFile , ValidationMessage.Type.ERROR, TEST_MESSAGE + "3");
        assertFalse(result.isValid());
        result.addMessage(testFile , ValidationMessage.Type.INFO, TEST_MESSAGE + "4");
        assertFalse(result.isValid());
    }

    @Test
    public void testAddMessage() {
        ValidationResult result = new ValidationResult();
        ValidationMessage message1 = result.addMessage(testFile, ValidationMessage.Type.INFO, TEST_MESSAGE + "1");
        ValidationMessage message2 = result.addMessage(testFile, ValidationMessage.Type.WARNING, TEST_MESSAGE + "2");
        ValidationMessage message3 = result.addMessage(testFile, ValidationMessage.Type.WARNING, TEST_MESSAGE + "3");
        message2.setLine(10);
        message3.setLine(1);
        List<FileValidationResult> fileValidationResults = result.getFileValidationResults();
        assertEquals(1, fileValidationResults.size());
        assertEquals(testFile, fileValidationResults.get(0).getFile());
        assertEquals(3, fileValidationResults.get(0).getMessages().size());
        List<ValidationMessage> messages = result.getMessages();
        assertEquals(3, messages.size());
        assertEquals(ValidationMessage.Type.WARNING, messages.get(0).getType());
        assertEquals(message3.getMessage(), messages.get(0).getMessage());
        assertEquals(message2.getMessage(), messages.get(1).getMessage());
        assertEquals(message1.getMessage(), messages.get(2).getMessage());
    }

}
