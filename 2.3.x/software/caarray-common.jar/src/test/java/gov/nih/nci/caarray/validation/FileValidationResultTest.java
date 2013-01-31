//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.validation;

import static org.junit.Assert.*;

import gov.nih.nci.caarray.AbstractCaarrayTest;
import gov.nih.nci.caarray.validation.ValidationMessage.Type;

import java.io.File;

import org.junit.Test;

public class FileValidationResultTest extends AbstractCaarrayTest {

    @Test
    public final void testGetFile() {
        File file = new File(".");
        FileValidationResult result = new FileValidationResult(file);
        assertEquals(file, result.getFile());
    }

    @Test
    public final void testIsValid() {
        File file = new File(".");
        FileValidationResult result = new FileValidationResult(file);
        assertTrue(result.isValid());
        result.addMessage(Type.INFO, "");
        result.addMessage(Type.WARNING, "");
        assertTrue(result.isValid());    
        result.addMessage(Type.ERROR, "");
        assertFalse(result.isValid());    
    }

    @Test
    public final void testGetMessages() {
        File file = new File(".");
        FileValidationResult result = new FileValidationResult(file);
        result.addMessage(Type.ERROR, "message1");
        result.addMessage(Type.ERROR, "message2");
        result.addMessage(Type.ERROR, "message3");
        assertEquals(3, result.getMessages().size());
    }

    @Test
    public final void testAddMessage() {
        File file = new File(".");
        FileValidationResult result = new FileValidationResult(file);
        result.addMessage(Type.ERROR, "message1", 1, 2);
        assertEquals(1, result.getMessages().size());
        assertEquals(1, result.getMessages().get(0).getLine());
        assertEquals(2, result.getMessages().get(0).getColumn());
    }

    @Test
    public final void testToString() {
        File file = new File(".");
        FileValidationResult result = new FileValidationResult(file);
        result.addMessage(Type.ERROR, "message1");
        String messageString = result.toString();
        assertEquals(messageString, "ERROR: message1\n");
    }

}
