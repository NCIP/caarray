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

import gov.nih.nci.caarray.magetab.io.FileRef;
import gov.nih.nci.caarray.magetab.io.JavaIOFileRef;
import gov.nih.nci.caarray.magetab.sdrf.SdrfDocument;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.apache.commons.io.FileUtils;

/**
 * @author wcheng
 */
public class SdrfSplitter {
    private final FileRef file;
    private final List<String> lines = new ArrayList<String>();
    private final Set<String> unusedLines = new HashSet<String>();

    SdrfSplitter(FileRef sdrf) throws IOException {
        file = sdrf;
        splitIntoLines();
        unusedLines.addAll(lines);
    }
    
    /**
     * Creates a small sdrf file which contains only rows that reference the given data file.
     * This method also tracks and updates which rows of the original sdrf are still unused.
     * 
     * @param dataFile data file to find references to in the sdrf
     * @return small sdrf that has all rows that reference dataFile
     * @throws IOException if writing to new files fails or any other io error
     */
    public FileRef splitByDataFile(FileRef dataFile) throws IOException {
        List<String> outputLines = new ArrayList<String>();
        String dataFileName = dataFile.getName().toLowerCase(Locale.getDefault());
        for (String curLine : lines) {
            if (outputLines.isEmpty() || curLine.toLowerCase(Locale.getDefault()).indexOf(dataFileName) >= 0) {
                outputLines.add(curLine);
                unusedLines.remove(curLine);
            }
        }
        File outputFile = File.createTempFile(file.getName(), ".sdrf");
        FileUtils.writeLines(outputFile, outputLines);
        return new JavaIOFileRef(outputFile);
    }

    /**
     * Creates a small sdrf file which contains only rows that have not been split out by calls to
     * {@link #splitByDataFile(FileRef)}.
     * Returns null if there are no unused rows.
     * 
     * @return small sdrf consisting of unused rows
     * @throws IOException if writing to new files fails or any other io error
     */
    public FileRef splitByUnusedLines() throws IOException {
        if (unusedLines.isEmpty()) { return null; }
        List<String> outputLines = new ArrayList<String>();
        outputLines.add(lines.get(0));
        outputLines.addAll(unusedLines);
        File outputFile = File.createTempFile(file.getName(), ".sdrf");
        FileUtils.writeLines(outputFile, outputLines);
        return new JavaIOFileRef(outputFile);
    }
    
    private void splitIntoLines() throws IOException {
        @SuppressWarnings("unchecked")
        List<String> inputLines = FileUtils.readLines(file.getAsFile());
        for (String curLine : inputLines) {
            if (curLine != null && !isCommentLine(curLine)) {
                lines.add(curLine);
            }
        }
        if (lines.isEmpty()) {
            throw new IllegalArgumentException("Could not find header row in sdrf file.  Was it validated?");
        }
    }
    
    private boolean isCommentLine(String line) {
        String trimmedLine = line.trim();
        return trimmedLine.isEmpty() || trimmedLine.startsWith(SdrfDocument.COMMENT_CHARACTER);
    }
}
