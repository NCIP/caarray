//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.validation;

import static org.junit.Assert.assertEquals;
import gov.nih.nci.caarray.AbstractCaarrayTest;

import org.junit.Test;

public class InvalidDataFileExceptionTest extends AbstractCaarrayTest {

    @Test
    public final void testGetFileValidationResult() {
        final FileValidationResult result = new FileValidationResult();
        final InvalidDataFileException exception = new InvalidDataFileException(result);
        assertEquals(result, exception.getFileValidationResult());
    }

}
