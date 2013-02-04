//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package gov.nih.nci.caarray.magetab.splitter;

import gov.nih.nci.caarray.magetab.io.FileRef;
import gov.nih.nci.caarray.magetab.io.JavaIOFileRef;
import gov.nih.nci.caarray.magetab.sdrf.SdrfDocument;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.apache.commons.io.FileUtils;

/**
 * @author wcheng
 */
public class SdrfSplitter {
    private static final int HEADER_ONLY = 1;
    private final FileRef file;
    private final List<String> lines = new ArrayList<String>();
    private final List<String> unusedLines = new ArrayList<String>();

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
            if (outputLines.isEmpty()) {
                // Add the header line.
                outputLines.add(curLine);
            }
            if (curLine.toLowerCase(Locale.getDefault()).indexOf(dataFileName) >= 0) {
                // Add any lines that reference this data file.
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
        if (unusedLines.size() == HEADER_ONLY) { return null; }
        File outputFile = File.createTempFile(file.getName(), ".sdrf");
        FileUtils.writeLines(outputFile, unusedLines);
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
