/**
 *  The caArray Software License, Version 1.0
 *
 *  Copyright 2004 5AM Solutions. This software was developed in conjunction
 *  with the National Cancer Institute, and so to the extent government
 *  employees are co-authors, any rights in such works shall be subject to
 *  Title 17 of the United States Code, section 105.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions are met:
 *
 *  1. Redistributions of source code must retain the above copyright notice,
 *  this list of conditions and the disclaimer of Article 3, below.
 *  Redistributions in binary form must reproduce the above copyright notice,
 *  this list of conditions and the following disclaimer in the documentation
 *  and/or other materials provided with the distribution.
 *
 *  2. Affymetrix Pure Java run time library needs to be downloaded from
 *  (http://www.affymetrix.com/support/developer/runtime_libraries/index.affx)
 *  after agreeing to the licensing terms from the Affymetrix.
 *
 *  3. The end-user documentation included with the redistribution, if any,
 *  must include the following acknowledgment:
 *
 *  "This product includes software developed by 5AM Solutions and the National
 *  Cancer Institute (NCI).
 *
 *  If no such end-user documentation is to be included, this acknowledgment
 *  shall appear in the software itself, wherever such third-party
 *  acknowledgments normally appear.
 *
 *  4. The names "The National Cancer Institute", "NCI", and "5AM Solutions"
 *  must not be used to endorse or promote products derived from this software.
 *
 *  5. This license does not authorize the incorporation of this software into
 *  any proprietary programs. This license does not authorize the recipient to
 *  use any trademarks owned by either NCI or 5AM.
 *
 *  6. THIS SOFTWARE IS PROVIDED "AS IS," AND ANY EXPRESSED OR IMPLIED
 *  WARRANTIES, (INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
 *  MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE) ARE DISCLAIMED. IN NO
 *  EVENT SHALL THE NATIONAL CANCER INSTITUTE, SAIC, OR THEIR AFFILIATES BE
 *  LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 *  CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 *  SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 *  INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 *  CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 *  ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 *  POSSIBILITY OF SUCH DAMAGE.
 */
package gov.nih.nci.caarray.magetab.splitter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import gov.nih.nci.caarray.magetab.MageTabFileSet;
import gov.nih.nci.caarray.magetab.io.FileRef;
import gov.nih.nci.caarray.magetab.io.JavaIOFileRef;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

/**
 * Tests for the file set splitter.
 * 
 * @author tparnell
 */
public class MageTabFileSetSplitterTest {

    private MageTabFileSetSplitter splitter;
    private Set<File> tmpFiles;
    private SdrfSplitter sdrfSplitter;
    private SdrfDataFileFinder sdrfDataFileFinder;
        
    /**
     * Set up test-specific resources.
     */
    @Before
    public void setup() {
        tmpFiles = new HashSet<File>();
        sdrfSplitter = mock(SdrfSplitter.class);
        sdrfDataFileFinder = mock(SdrfDataFileFinder.class);
        splitter = new MageTabFileSetSplitterImpl(sdrfSplitter, sdrfDataFileFinder);
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
        assertSplit(1, 1);
        assertSplit(1, 5);
    }
    
    @Test
    public void multipleSDRFs() throws IOException {
        assertSplit(2, 7);
        assertSplit(3, 7);
        assertSplit(5, 9);
    }
    
    @Test
    public void singleDataFileReferenced() throws IOException {
        MageTabFileSet fileSet = setupFileSet(1, 1, 4);
        mockSplitter(1);
        when(sdrfDataFileFinder.identifyReferencedDataFiles(any(FileRef.class)))
            .thenReturn(Collections.singleton(fileSet.getDataMatrixFiles().iterator().next().getName()));
        Set<MageTabFileSet> result = splitter.split(fileSet);
        assertMultiFileSetSizes(result, 1, 3);
    }
    
    private void useUnityDataFileFinder(final MageTabFileSet fileSet) throws IOException {
        when(sdrfDataFileFinder.identifyReferencedDataFiles(any(FileRef.class))).thenAnswer(new Answer<Set<String>>() {
            @Override
            public Set<String> answer(InvocationOnMock invocation) throws Throwable {
                Set<String> result = new HashSet<String>();
                for (FileRef ref : fileSet.getAllFiles()) {
                    result.add(ref.getName());
                }
                return result;
            }
        });
    }

    private void mockSplitter(int splitsPerSdrf) {
        Set<FileRef> fakedSplits = new HashSet<FileRef>();
        for (int i = 0; i < splitsPerSdrf; ++i) {
            fakedSplits.add(generateFileRef());
        }
        try {
            when(sdrfSplitter.split(any(FileRef.class))).thenReturn(fakedSplits);
        } catch (IOException e) {
            // shouldn't be able to happen
            throw new RuntimeException(e);
        }
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
        input.addSdrf(generateFileRef());
        IOException expectedException = new IOException();
        try {
            when(sdrfSplitter.split(any(FileRef.class))).thenThrow(expectedException);
        } catch (IOException e) {
            // shouldn't be able to happen
            throw new RuntimeException(e);
        }
        
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
    
    
    private void assertSplit(int numSdrfs, int splitsPerSdrf) throws IOException {
        MageTabFileSet input = setupFileSet(1, numSdrfs, 2);
        mockSplitter(splitsPerSdrf);
        useUnityDataFileFinder(input);
        
        Set<MageTabFileSet> result = splitter.split(input);
        // There should always be 5 files in the result:
        // - dataMatrix, idf, nativeData, and *1* sdrf
        assertMultiFileSetSizes(result, splitsPerSdrf * numSdrfs, 4);
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
