//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package gov.nih.nci.caarray.magetab.splitter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;
import gov.nih.nci.caarray.magetab.MageTabFileSet;
import gov.nih.nci.caarray.magetab.io.FileRef;
import gov.nih.nci.caarray.magetab.io.JavaIOFileRef;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests for the file set splitter.
 * 
 * @author tparnell
 */
public class MageTabFileSetSplitterTest {

    private MageTabFileSetSplitter splitter;
    private Set<File> tmpFiles;
        
    /**
     * Set up test-specific resources.
     */
    @Before
    public void setup() {
        tmpFiles = new HashSet<File>();
        splitter = new MageTabFileSetSplitterImpl();
    }
    
    @Test
    public void nullInput() throws IOException {
        Set<MageTabFileSet> result = splitter.split(null);
        assertNull(result);
    }
    
    @Test
    public void emptyInput() throws IOException {
        MageTabFileSet input = new MageTabFileSet();
        Set<MageTabFileSet> result = splitter.split(input);
        assertSingleFileSetSize(result, 0);
    }
    
    @Test
    public void noSDRFs() throws IOException {
        MageTabFileSet input = new MageTabFileSet();
        input.addAdf(generateFileRef());
        input.addDataMatrix(generateFileRef());
        input.addIdf(generateFileRef());
        input.addNativeData(generateFileRef());
        Set<MageTabFileSet> result = splitter.split(input);
        assertSingleFileSetSize(result, 4);
    }

    @Test
    public void oneSDRF() throws IOException {
        assertSplit(1, 2);
    }
    
    @Test
    public void multipleSDRFs() throws IOException {
        assertSplit(2, 6);
    }

    private MageTabFileSet setupFileSet(int numIdfs, int numSdrfs, int numDataFiles) {
        MageTabFileSet result = new MageTabFileSet();
        for (int i = 0; i < numIdfs; ++i) {
            result.addIdf(generateFileRef());
        }
        for (int i = 0; i < numSdrfs; ++i) {
            result.addSdrf(generateFileRef());
        }
        for (int i = 0; i < numDataFiles; ++i) {
            result.addDataMatrix(generateFileRef());
        }
        
        return result;
    }

    @Test 
    public void ioExceptionPropagated() {
        MageTabFileSet input = new MageTabFileSet();
        input.addIdf(generateFileRef());
        input.addSdrf(generateFileRef());
        IOException expectedException = new IOException();
        
        try {
            splitter.split(input);
        } catch (IOException actualException) {
            assertEquals(expectedException, actualException);
        }

    }
    
    @Test
    public void multiIdfNoSplit() throws IOException {
        MageTabFileSet input = new MageTabFileSet();
        input.addSdrf(generateFileRef());
        input.addIdf(generateFileRef());
        input.addIdf(generateFileRef());
        Set<MageTabFileSet> split = splitter.split(input);
        assertSingleFileSetSize(split, 3);
    }
    
    
    private void assertSplit(int numSdrfs, int numDataFiles) throws IOException {
        MageTabFileSet input = setupFileSet(1, numSdrfs, numDataFiles);
        
        Set<MageTabFileSet> result = splitter.split(input);
        // There should always be 3 files in each result fileset:
        // - 1 idf, *1* sdrf, and *1* data file
        assertMultiFileSetSizes(result, numDataFiles * numSdrfs, 3);
    }
    
    private void assertSingleFileSetSize(Set<MageTabFileSet> fileSets, int expectedFiles) {
        assertMultiFileSetSizes(fileSets, 1, expectedFiles);
    }
    
    private void assertMultiFileSetSizes(Set<MageTabFileSet> fileSets, int expectedSets, int expectedFilesPerSet) {
        assertNotNull(fileSets);
        assertEquals(expectedSets, fileSets.size());
        for (MageTabFileSet fs : fileSets) {
            assertEquals(expectedFilesPerSet, fs.getAllFiles().size());
        }
    }
    
    private FileRef generateFileRef() {
        File f = null;
        try {
            f = File.createTempFile("fileRef", ".tmp");
            tmpFiles.add(f);
            FileUtils.writeStringToFile(f, "dummy header");
        } catch (IOException e) {
            fail(e.getMessage());
        }
        FileRef result = new JavaIOFileRef(f);
        return result;
    }
    
    /**
     * Tear down test-specific resources.
     */
    @After
    public void cleanup() {
        for (File f : tmpFiles) {
            f.delete();
        }
    }
}
