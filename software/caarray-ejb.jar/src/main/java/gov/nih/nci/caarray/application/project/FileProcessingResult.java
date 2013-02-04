//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package gov.nih.nci.caarray.application.project;

import java.util.SortedSet;

import com.google.common.collect.Sets;

/**
 * Simple bean class to hold the results of uploading or unpacking a set of files.
 */
public class FileProcessingResult {
    private final SortedSet<String> conflictingFiles = Sets.newTreeSet();
    private final SortedSet<String> successfullyProcessedFiles = Sets.newTreeSet();
    private boolean partialUpload;

    /**
     * @return the number of files successfully processed
     */
    public int getCount() {
        return successfullyProcessedFiles.size();
    }

    /**
     * @return the set of files that were processed.
     */
    public SortedSet<String> getSuccessfullyProcessedFiles() {
        return successfullyProcessedFiles;
    }

    /**
     * @param fileName the name of the file that was successfully processed.
     */
    public void addSuccessfulFile(String fileName) {
        successfullyProcessedFiles.add(fileName);
    }

    /**
     * @return the names of files that could not be successfully processed because they had
     * conflicts with files already existing in the project.
     */
    public SortedSet<String> getConflictingFiles() {
        return conflictingFiles;
    }

    /**
     * Add the given file name to the set of conflicting file names.
     * @param conflictingFile the file name to add
     */
    public void addConflictingFile(String conflictingFile) {
        this.conflictingFiles.add(conflictingFile);
    }

    /**
     * @return the partialUpload
     */
    public boolean isPartialUpload() {
        return partialUpload;
    }

    /**
     * @param partialUpload the partialUpload to set
     */
    public void setPartialUpload(boolean partialUpload) {
        this.partialUpload = partialUpload;
    }

}
