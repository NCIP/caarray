//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package gov.nih.nci.caarray.application.project;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import java.io.File;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

/**
 * @author wcheng
 *
 */
public class FileWrapperTest {
    private static final String DUMMY_FILE_NAME = "testFile";
    private static final long FILE_SIZE = 100L;

    @Mock
    private File dummyFile;
    
    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        when(dummyFile.length()).thenReturn(FILE_SIZE);
    }

    @Test
    public void testFields() {
        FileWrapper fileWrapper = new FileWrapper();
        fileWrapper.setCompressed(true);
        fileWrapper.setFile(dummyFile);
        fileWrapper.setFileName(DUMMY_FILE_NAME);
        fileWrapper.setTotalFileSize(FILE_SIZE);

        assertTrue(fileWrapper.isCompressed());
        assertEquals(dummyFile, fileWrapper.getFile());
        assertEquals(DUMMY_FILE_NAME, fileWrapper.getFileName());
        assertEquals(FILE_SIZE, fileWrapper.getTotalFileSize());
    }

    @Test
    public void testIdPartial() {
        FileWrapper partialFile = new FileWrapper();
        partialFile.setFile(dummyFile);
        partialFile.setTotalFileSize(2*FILE_SIZE);
        assertTrue(partialFile.isPartial());
        
        FileWrapper completeFile = new FileWrapper();
        completeFile.setFile(dummyFile);
        completeFile.setTotalFileSize(FILE_SIZE);
        assertFalse(completeFile.isPartial());
    }
}
