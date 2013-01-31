//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.application.project;

import java.util.SortedSet;
import java.util.TreeSet;

/**
 * Simple bean class to hold the results of uploading or unpacking a set of files.
 * 
 * @author dkokotov
 */
public class FileProcessingResult {
    private int count;
    private final SortedSet<String> conflictingFiles = new TreeSet<String>();

    /**
     * @return the number of files successfully processed
     */
    public int getCount() {
        return count;
    }

    /**
     * Increment the count of successfully processed files by 1.
     */
    public void incrementCount() {
        this.count++;
    }
    
    /**
     * Increment the count of successfully processed files by the given amount.
     * @param by amount to increment by.
     */
    public void incrementCount(int by) {
        this.count += by;
    }

    /**
     * @return the names of files that could not be successfully processed because they had
     * conflicts with files already existing in the project.
     */
    public SortedSet<String> getConflictingFiles() {
        return conflictingFiles;
    }

    /**
     * Add the given file names to the set of conflicting file names.
     * @param filesToAdd the file names to add
     */
    public void addConflictingFiles(SortedSet<String> filesToAdd) {
        this.conflictingFiles.addAll(filesToAdd);
    }
    
    /**
     * Add the given file name to the set of conflicting file names.
     * @param conflictingFile the file name to add
     */
    public void addConflictingFile(String conflictingFile) {
        this.conflictingFiles.add(conflictingFile);
    }

}
