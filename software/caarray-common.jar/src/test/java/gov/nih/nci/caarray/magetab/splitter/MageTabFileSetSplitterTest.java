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
import gov.nih.nci.caarray.magetab.MageTabFileSet;
import gov.nih.nci.caarray.magetab.io.FileRef;
import gov.nih.nci.caarray.magetab.io.JavaIOFileRef;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

/**
 * Tests for the file set splitter.
 * 
 * @author tparnell
 */
public class MageTabFileSetSplitterTest {

    private static MageTabFileSetSplitter splitter;
    private Set<File> tmpFiles;
    
    /**
     * Set up static resources.
     */
    @BeforeClass
    public static void setupClass() {
        splitter = new MageTabFileSetSplitterImpl();
    }
    
    /**
     * Set up test-specific resources.
     */
    @Before
    public void setup() {
        tmpFiles = new HashSet<File>();
    }
    
    /**
     * Verify null input is handled gracefully.
     */
    @Test
    public void nullInput() {
        Set<MageTabFileSet> result = splitter.split(null);
        assertNull(result);
    }
    
    /**
     * Verify empty input yields a single file set, with no files.
     */
    @Test
    public void emptyInput() {
        MageTabFileSet input = new MageTabFileSet();
        Set<MageTabFileSet> result = splitter.split(input);
        assertSingleFileSetSize(result, 0);
    }
    
    /**
     * Verify that the input file set comes back unchanged if there are no SDRFs present.
     */
    @Test
    public void noSDRFs() {
        MageTabFileSet input = new MageTabFileSet();
        input.addAdf(generateFileRef());
        input.addDataMatrix(generateFileRef());
        input.addIdf(generateFileRef());
        input.addNativeData(generateFileRef());
        Set<MageTabFileSet> result = splitter.split(input);
        assertSingleFileSetSize(result, 4);
    }

    /**
     * Single line SDRF.
     */
    @Ignore(value = "TODO: This is the next test I am currently writing")
    @Test
    public void singleLineSdrf() {
        MageTabFileSet input = new MageTabFileSet();
        FileRef fileRef = generateFileRef();
        // TODO: actually have this be a valid SDRF file
        input.addSdrf(fileRef);
        Set<MageTabFileSet> result = splitter.split(input);
        assertSingleFileSetSize(result, 1);
    }

    private void assertSingleFileSetSize(Set<MageTabFileSet> fileSets, int expectedFiles) {
        assertNotNull(fileSets);
        assertEquals(1, fileSets.size());
        MageTabFileSet fs = fileSets.iterator().next();
        assertEquals(expectedFiles, fs.getAllFiles().size());
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
