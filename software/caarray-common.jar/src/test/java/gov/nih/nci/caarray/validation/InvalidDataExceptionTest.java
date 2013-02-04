//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package gov.nih.nci.caarray.validation;

import static org.junit.Assert.*;
import gov.nih.nci.caarray.AbstractCaarrayTest;

import org.junit.Test;

public class InvalidDataExceptionTest extends AbstractCaarrayTest {

    @Test
    public final void testGetValidationResult() {
        ValidationResult result = new ValidationResult();
        InvalidDataException exception = new InvalidDataException(result);
        assertEquals(result, exception.getValidationResult());
    }

}
