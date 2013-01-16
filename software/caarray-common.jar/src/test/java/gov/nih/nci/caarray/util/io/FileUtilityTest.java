//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.util.io;

import gov.nih.nci.caarray.AbstractCaarrayTest;
import gov.nih.nci.caarray.magetab.io.FileRef;

import java.io.File;

import org.junit.Test;

/**
 * Test cases for file utility.
 */
public class FileUtilityTest extends AbstractCaarrayTest {

    @Test(expected = IllegalArgumentException.class)
    public void testNull() {
        FileUtility.checkFileExists((File)null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNull_FileRef() {
        FileUtility.checkFileExists((FileRef)null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNonExist() {
        FileUtility.checkFileExists(new File("idontexist.txt"));
    }

    @Test
    public void testOk() {
        FileUtility.checkFileExists(new File("."));
    }
}
