//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.validation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import gov.nih.nci.caarray.AbstractCaarrayTest;
import gov.nih.nci.caarray.validation.ValidationMessage.Type;

import org.junit.Test;

public class FileValidationResultTest extends AbstractCaarrayTest {

    @Test
    public final void testIsValid() {
        final FileValidationResult result = new FileValidationResult();
        assertTrue(result.isValid());
        result.addMessage(Type.INFO, "");
        result.addMessage(Type.WARNING, "");
        assertTrue(result.isValid());
        result.addMessage(Type.ERROR, "");
        assertFalse(result.isValid());
    }

    @Test
    public final void testGetMessages() {
        final FileValidationResult result = new FileValidationResult();
        result.addMessage(Type.ERROR, "message1");
        result.addMessage(Type.ERROR, "message2");
        result.addMessage(Type.ERROR, "message3");
        assertEquals(3, result.getMessages().size());
    }

    @Test
    public final void testAddMessage() {
        final FileValidationResult result = new FileValidationResult();
        result.addMessage(Type.ERROR, "message1", 1, 2);
        assertEquals(1, result.getMessages().size());
        assertEquals(1, result.getMessages().get(0).getLine());
        assertEquals(2, result.getMessages().get(0).getColumn());
    }

    @Test
    public final void testToString() {
        final FileValidationResult result = new FileValidationResult();
        result.addMessage(Type.ERROR, "message1");
        final String messageString = result.toString();
        assertEquals(messageString, "ERROR: message1\n");
    }

}
