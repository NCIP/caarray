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

import org.junit.Test;

public class InvalidDataFileExceptionTest extends AbstractCaarrayTest {

    @Test
    public final void testGetFileValidationResult() {
        FileValidationResult result = new FileValidationResult(new File("."));
        InvalidDataFileException exception = new InvalidDataFileException(result);
        assertEquals(result, exception.getFileValidationResult());
    }

}
