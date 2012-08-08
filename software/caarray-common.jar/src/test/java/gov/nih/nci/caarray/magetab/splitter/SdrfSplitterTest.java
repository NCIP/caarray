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
import static org.junit.Assert.fail;
import gov.nih.nci.caarray.magetab.io.FileRef;
import gov.nih.nci.caarray.magetab.io.JavaIOFileRef;
import gov.nih.nci.caarray.test.data.magetab.MageTabDataFiles;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;

import com.google.common.base.Function;
import com.google.common.collect.Lists;

/**
 * Tests for the sdrf splitter.
 * 
 * @author tparnell
 */
public class SdrfSplitterTest {
    private File sdrf;
    private List<String> sdrfLines;
    private FileRef dataFile1;
    private FileRef dataFile2;
    private int dataFile1References;
    
    @Before
    @SuppressWarnings("unchecked")
    public void setUp() throws IOException {
        sdrf = MageTabDataFiles.SPECIFICATION_EXAMPLE_SDRF;
        dataFile1 = new JavaIOFileRef(MageTabDataFiles.SPECIFICATION_EXAMPLE_DATA_FILE_1);
        dataFile2 = new JavaIOFileRef(MageTabDataFiles.SPECIFICATION_EXAMPLE_DATA_FILE_2);
        sdrfLines = FileUtils.readLines(sdrf);
        dataFile1References = 0;
        for (String curLine : sdrfLines) {
            if (curLine.contains(dataFile1.getName())) {
                dataFile1References++;
            }
        }
    }

    /**
     * Split the specification sdrf by data file,
     */
    @Test
    public void testSplitByDataFile() throws IOException {
        specificationSdrfWithTransform(null);
    }
    
    /**
     * Generate sdrf for unused rows after splitting by a couple data files.
     */
    @Test
    public void testSplitByUnusedLines() throws IOException {
        int numReferences = 0;
        for (String curLine : sdrfLines) {
            if (curLine.contains(dataFile1.getName()) || curLine.contains(dataFile2.getName())) {
                numReferences++;
            }
        }
        SdrfSplitter splitter = new SdrfSplitter(new JavaIOFileRef(sdrf));
        splitter.splitByDataFile(dataFile1);
        splitter.splitByDataFile(dataFile2);
        FileRef unused = splitter.splitByUnusedLines();
        
        @SuppressWarnings("unchecked")
        List<String> splitLines = FileUtils.readLines(unused.getAsFile());
        assertEquals(sdrfLines.size() - numReferences, splitLines.size());
        assertEquals(sdrfLines.get(0), splitLines.get(0));
    }
    
    @Test
    public void commentBeforeHeader() throws IOException {
        testComments(new int[] {0}, 
                new String[] {"# I am a comment"});
    }

    @Test
    public void commentsBeforeHeader() throws IOException {
        testComments(new int[] {0, 1}, 
                new String[] {"# I am a comment", 
                              "# I am another comment line",
                              " "});
    }
    
    @Test
    public void commentAfterHeader() throws IOException {
        testComments(new int[] {1}, 
                new String[] {"# I am a comment"});
    }
    
    @Test
    public void commentsWithLeadingWhitespace() throws IOException {
        testComments(new int[] {0, 1, 2}, 
                new String[] {" # I am a comment with leading whitespace", 
                              "\t# I am a comment with leading tab",
                              " "});
    }
    
    @Test
    public void emptyLines() throws IOException {
        testComments(new int[] {0, 2}, new String[] {"", ""});
    }
    
    private void testComments(int[] positions, String[] comments) throws IOException {
        SortedMap<Integer, String> linesToAdd = new TreeMap<Integer, String>();
        for (int i = 0; i < positions.length; ++i) {
            linesToAdd.put(positions[i], comments[i]);
        }
        
        Function<File, File> transform = getTransform(linesToAdd);
        specificationSdrfWithTransform(transform);        
    }
    
    /**
     * Checks that sdrf files that should have failed validation
     * @throws IOException
     */
    @Test(expected = IllegalArgumentException.class)
    public void onlyComments() throws IOException {
        List<String> content = Lists.newArrayList("# comment", "", "# another comment");
        File file = File.createTempFile("test", ".sdrf");
        FileUtils.writeLines(file, content);
        FileRef fileRef = new JavaIOFileRef(file);
        new SdrfSplitter(fileRef);
        fail("Should throw IllegalArgumentException");
    }
    
    /**
     * linesToAdd are added in sequential order.  This <em>modifies</em> the lines
     * in place.  So to add 2 lines before the header and 1 after, the keys should be
     * (0, 1, 3), because that will add two lines (moving the header down), then add after
     * the header.
     */
    private Function<File, File> getTransform(final SortedMap<Integer, String> linesToAdd) {
        Function<File, File> transform = new Function<File, File>() {
            @Override
            public File apply(File input) {
                try {
                    @SuppressWarnings("unchecked")
                    List<String> lines = FileUtils.readLines(input);
                    for (Integer i : linesToAdd.keySet()) {
                        lines.add(i, linesToAdd.get(i));
                    }
                    File revisedSdrf = File.createTempFile("test", ".sdrf");
                    FileUtils.writeLines(revisedSdrf, lines);
                    return revisedSdrf;
                } catch (IOException ioe) {
                    throw new RuntimeException(ioe);
                }
            }
        };
        return transform;
    }
    
    private void specificationSdrfWithTransform(Function<File, File> transform) throws IOException {
        File transformed = transform == null ? sdrf : transform.apply(sdrf);
        FileRef fileRef = new JavaIOFileRef(transformed);
        
        SdrfSplitter splitter = new SdrfSplitter(fileRef);
        FileRef splitSdrf = splitter.splitByDataFile(dataFile1);

        @SuppressWarnings("unchecked")
        List<String> splitLines = FileUtils.readLines(splitSdrf.getAsFile());
        assertEquals(dataFile1References+1, splitLines.size());
        assertEquals(sdrfLines.get(0), splitLines.get(0));
    }
    
    /**
     * Null check.
     * 
     * @throws IOException
     */
    @Test(expected = NullPointerException.class)
    public void nullFileRef() throws IOException {
        new SdrfSplitter(null);
        fail("Should throw NullPointerException");
    }
    
    /**
     * Non-existent file, but ref itself exists.
     * 
     * @throws IOException
     */
    @Test(expected = IOException.class)
    public void invalidFileRef() throws IOException {
        FileRef ref = new JavaIOFileRef(new File("idontexist.anywhere"));
        new SdrfSplitter(ref);
        fail("Should throw IOException");
    }
}
