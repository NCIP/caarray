//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package gov.nih.nci.caarray.domain.file;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.Collections;
import java.util.List;

import gov.nih.nci.caarray.test.data.magetab.MageTabDataFiles;
import gov.nih.nci.caarray.validation.FileValidationResult;
import gov.nih.nci.caarray.validation.ValidationMessage;

import org.junit.Test;

public class CaArrayFileSetTest {

    @Test
    public void testGetFile() {
        CaArrayFile caArrayFile = new CaArrayFile();
        caArrayFile.setName(MageTabDataFiles.SPECIFICATION_EXAMPLE_IDF.getName());
        CaArrayFile caArrayFile2 = new CaArrayFile();
        caArrayFile2.setName(MageTabDataFiles.SPECIFICATION_EXAMPLE_SDRF.getName());
        CaArrayFileSet fileSet = new CaArrayFileSet();
        fileSet.add(caArrayFile);
        fileSet.add(caArrayFile2);
        fileSet.add(new CaArrayFile());
        assertEquals(caArrayFile, fileSet.getFile(MageTabDataFiles.SPECIFICATION_EXAMPLE_IDF));
        assertEquals(caArrayFile2, fileSet.getFile(MageTabDataFiles.SPECIFICATION_EXAMPLE_SDRF));
        assertNull(fileSet.getFile(MageTabDataFiles.SPECIFICATION_ERROR_EXAMPLE_IDF));
    }

    @Test
    public void testGetStatus() {
        CaArrayFile caArrayFile = new CaArrayFile();
        CaArrayFile caArrayFile2 = new CaArrayFile();
        CaArrayFileSet fileSet = new CaArrayFileSet();
        fileSet.add(caArrayFile);
        fileSet.add(caArrayFile2);

        caArrayFile.setFileStatus(FileStatus.IMPORTED);
        caArrayFile2.setFileStatus(FileStatus.IMPORTED);
        assertEquals(FileStatus.IMPORTED, fileSet.getStatus());

        caArrayFile.setFileStatus(FileStatus.IMPORTED);
        caArrayFile2.setFileStatus(FileStatus.IMPORTED_NOT_PARSED);
        assertEquals(FileStatus.IMPORTED, fileSet.getStatus());

        caArrayFile.setFileStatus(FileStatus.IMPORTING);
        caArrayFile2.setFileStatus(FileStatus.IMPORTED);
        assertEquals(FileStatus.IMPORTING, fileSet.getStatus());

        caArrayFile.setFileStatus(FileStatus.IMPORTED_NOT_PARSED);
        caArrayFile2.setFileStatus(FileStatus.IMPORTED_NOT_PARSED);
        assertEquals(FileStatus.IMPORTED_NOT_PARSED, fileSet.getStatus());

        caArrayFile.setFileStatus(FileStatus.VALIDATED);
        caArrayFile2.setFileStatus(FileStatus.VALIDATING);
        assertEquals(FileStatus.VALIDATING, fileSet.getStatus());

        caArrayFile.setFileStatus(FileStatus.VALIDATED);
        caArrayFile2.setFileStatus(FileStatus.VALIDATED);
        assertEquals(FileStatus.VALIDATED, fileSet.getStatus());

        caArrayFile.setFileStatus(FileStatus.VALIDATED);
        caArrayFile2.setFileStatus(FileStatus.VALIDATED_NOT_PARSED);
        assertEquals(FileStatus.VALIDATED, fileSet.getStatus());

        caArrayFile.setFileStatus(FileStatus.VALIDATED_NOT_PARSED);
        caArrayFile2.setFileStatus(FileStatus.VALIDATED_NOT_PARSED);
        assertEquals(FileStatus.VALIDATED_NOT_PARSED, fileSet.getStatus());
    }

    @Test
    public void pullUpSingleChildMessages() {
        FileValidationResult parentMessages = new FileValidationResult();
        CaArrayFile parent = mock(CaArrayFile.class);
        when(parent.getValidationResult()).thenReturn(parentMessages);
        
        validatePullUpToParent(parent);        
    }
    
    @Test
    public void nullParentValidationMessages() {
        CaArrayFile parent = new CaArrayFile();    
        validatePullUpToParent(parent);
    }
    
    @Test
    public void pullUpNullChildrenMessages() {
        noChildrenMessage(false);
    }
    
    @Test
    public void pullUpNoChildrenMessages() {
        noChildrenMessage(true);
    }
    
    private void noChildrenMessage(boolean resultsAsEmpty) {
        CaArrayFile parent = mock(CaArrayFile.class);
        CaArrayFile child = mock(CaArrayFile.class);
        when(child.getParent()).thenReturn(parent);
        if (resultsAsEmpty) {
            FileValidationResult validationResult = mock(FileValidationResult.class);
            when(child.getValidationResult()).thenReturn(validationResult);
        }
        CaArrayFileSet fileSet = new CaArrayFileSet();
        fileSet.add(child);
        fileSet.pullUpValidationMessages();
        
        // parent should be completely untouched
        verifyNoMoreInteractions(parent);
    }
    
    private void validatePullUpToParent(CaArrayFile parent) {
        ValidationMessage message = mock(ValidationMessage.class);
        List<ValidationMessage> childMessages = Collections.singletonList(message);
                
        FileValidationResult childValidationResult = mock(FileValidationResult.class);
        when(childValidationResult.getMessages()).thenReturn(childMessages);
        
        CaArrayFile child = mock(CaArrayFile.class);
        when(child.getValidationResult()).thenReturn(childValidationResult);
        when(child.getParent()).thenReturn(parent);
        
        CaArrayFileSet fileSet = new CaArrayFileSet();
        fileSet.add(child);
        fileSet.pullUpValidationMessages();
        
        assertMessagePulledUp(parent, message);
    }
    
    
    private void assertMessagePulledUp(CaArrayFile parent, ValidationMessage childMessage) {
        assertNotNull(parent.getValidationResult());
        assertEquals(1, parent.getValidationResult().getMessages().size());
        ValidationMessage validationMessage = parent.getValidationResult().getMessages().get(0);
        assertFalse(childMessage.equals(validationMessage)); // needs to be different object ...
        assertTrue(childMessage.compareTo(validationMessage) == 0); // ... with same business fields
    }
}
