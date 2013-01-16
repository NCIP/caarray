//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.magetab;

import static org.junit.Assert.assertTrue;
import gov.nih.nci.caarray.magetab.io.JavaIOFileRef;
import gov.nih.nci.caarray.magetab.sdrf.SdrfDocument;
import gov.nih.nci.caarray.test.data.magetab.MageTabDataFiles;
import gov.nih.nci.caarray.validation.InvalidDataException;
import gov.nih.nci.caarray.validation.ValidationMessage;
import gov.nih.nci.caarray.validation.ValidationResult;

import org.junit.Test;

public class SdrfDocumentTest {

    /**
     * Want clean info message, not error or NPE.
     */
    @Test
    public void parseSdrfNoIdf() throws InvalidDataException, MageTabParsingException {
        JavaIOFileRef sdrf = new JavaIOFileRef(MageTabDataFiles.SPECIFICATION_EXAMPLE_SDRF);
        MageTabFileSet inputFileSet = new MageTabFileSet();
        inputFileSet.addSdrf(sdrf);
        MageTabDocumentSet documentSet = new MageTabDocumentSet(inputFileSet);

        SdrfDocument sdrfDocument = new SdrfDocument(documentSet, sdrf);
        sdrfDocument.parseNoIdfCheck();
        
        ValidationResult validationResult = documentSet.getValidationResult();
        assertFactorInfoMessage(validationResult);
    }
    
    private void assertFactorInfoMessage(ValidationResult validationResult) {
        boolean foundFactorInfoMessage = false;
        for (ValidationMessage message : validationResult.getMessages()) {
            if (ValidationMessage.Type.INFO.equals(message.getType()) 
                    && message.getMessage().startsWith("Factor parsing disabled")) {
                foundFactorInfoMessage = true;
            }
        }
        assertTrue("No factor parsing message.  Validation result: " + validationResult, foundFactorInfoMessage);
    }
}
