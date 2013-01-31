//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.application.fileaccess;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import gov.nih.nci.caarray.AbstractCaarrayTest;

import java.io.File;
import java.io.IOException;

import org.junit.Test;


/**
 * Test FileCleanThread
 */
public class FileCleanupThreadTest extends AbstractCaarrayTest {

    private static final String EXTENSION = ".ext";

    @Test
    public void testRun() throws IOException, FileAccessException {
        File tmpFile1 = File.createTempFile("fileCleanTestFile1", EXTENSION);
        File tmpFile2 = File.createTempFile("fileCleanTestFile2", EXTENSION);
        File tmpFile3 = File.createTempFile("fileCleanTestFile3", EXTENSION);
        FileCleanupThread.getInstance().addFile(tmpFile1);
        FileCleanupThread.getInstance().addFile(tmpFile2);
        FileCleanupThread.getInstance().addFile(tmpFile3);

        assertTrue(tmpFile1.exists());
        assertTrue(tmpFile2.exists());
        assertTrue(tmpFile3.exists());
        FileCleanupThread.getInstance().run();
        assertFalse(tmpFile1.exists());
        assertFalse(tmpFile2.exists());
        assertFalse(tmpFile3.exists());

        File tmpFile4 = File.createTempFile("fileCleanTestFile4", EXTENSION);
        FileCleanupThread.getInstance().addFile(tmpFile4);
        assertTrue(tmpFile4.delete());
        FileCleanupThread.getInstance().run();
        assertFalse(tmpFile4.exists());
    }

}
