//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.domain.file;

import gov.nih.nci.caarray.domain.project.Project;

import java.io.File;
import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * Provides functionality to handle multiple <code>CaArrayFiles</code> as a single set.
 */
@SuppressWarnings("PMD.CyclomaticComplexity") // Complex rules for checking aggregate file status
public final class CaArrayFileSet implements Serializable {

    private static final long serialVersionUID = -831461553674445009L;
    private final Set<CaArrayFile> files = new HashSet<CaArrayFile>();
    private final Long projectId;

    /**
     * @param p project this file set is associated with
     */
    public CaArrayFileSet(Project p) {
        if (p == null) {
            throw new IllegalArgumentException("Project must be non-null");
        }
        projectId = p.getId();
    }

    /**
     * Construct a new file set, with the same files and associated project.
     * @param baseFileSet file set on which to base the new file set
     */
    public CaArrayFileSet(CaArrayFileSet baseFileSet) {
        projectId = baseFileSet.projectId;
        files.addAll(baseFileSet.getFiles());
    }

    /**
     * @return related project system identifier
     */
    public Long getProjectId() {
        return projectId;
    }

    /**
     * File set.  Only files <em>not</em> associated with a project can be added.
     */
    public CaArrayFileSet() {
        projectId = null;
    }

    /**
     * Adds a file to the set.
     *
     * @param file the file to add
     */
    public void add(CaArrayFile file) {
        Long fileProjectId = file.getProject() == null ? null : file.getProject().getId();
        if (!ObjectUtils.equals(fileProjectId, projectId)) {
            throw new IllegalArgumentException("file's project and fileset project not the same");
        }
        files.add(file);
    }

    /**
     * Returns the contained files.
     *
     * @return the files.
     */
    public Set<CaArrayFile> getFiles() {
        return files;
    }

    /**
     * Returns the aggregate status of the file set.
     *
     * @return status of the set.
     */
    public FileStatus getStatus() {
        if (!files.isEmpty() && allStatusesEqual()) {
            return files.iterator().next().getFileStatus();
        } else if (allStatusesEqual(FileStatus.IMPORTED, FileStatus.IMPORTED_NOT_PARSED)) {
            return FileStatus.IMPORTED;
        } else if (allStatusesEqual(FileStatus.VALIDATED, FileStatus.VALIDATED_NOT_PARSED)) {
            return FileStatus.VALIDATED;
        } else if (statusesContains(FileStatus.IMPORTING)) {
            return FileStatus.IMPORTING;
        } else if (statusesContains(FileStatus.VALIDATING)) {
            return FileStatus.VALIDATING;
        } else if (statusesContains(FileStatus.VALIDATION_ERRORS)) {
            return FileStatus.VALIDATION_ERRORS;
        } else if (statusesContains(FileStatus.IMPORT_FAILED)) {
            return FileStatus.IMPORT_FAILED;
        } else {
            return FileStatus.UPLOADED;
        }
    }

    /**
     * @return true if the status of all the files is one of the given statuses
     */
    @SuppressWarnings("PMD.UnusedPrivateMethod")
    private boolean allStatusesEqual(FileStatus... statuses) {
        Set<FileStatus> fileSetStatuses = new HashSet<FileStatus>();
        for (CaArrayFile file : files) {
            fileSetStatuses.add(file.getFileStatus());
        }
        for (FileStatus status : statuses) {
            fileSetStatuses.remove(status);
        }
        return fileSetStatuses.isEmpty();
    }

    /**
     * @return true if the status of all the files is the same
     */
    private boolean allStatusesEqual() {
        Set<FileStatus> fileSetStatuses = new HashSet<FileStatus>();
        for (CaArrayFile file : files) {
            fileSetStatuses.add(file.getFileStatus());
        }
        return fileSetStatuses.size() == 1;
    }

    /**
     * tells if the status is in the set.
     * @param status the status.
     * @return true if it is in the set.
     */
    public boolean statusesContains(FileStatus status) {
        for (CaArrayFile file : files) {
            if (status.equals(file.getFileStatus())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Adds a collection of files to this set.
     *
     * @param fileCollection files to add.
     */
    public void addAll(Collection<CaArrayFile> fileCollection) {
        for (CaArrayFile file : fileCollection) {
            files.add(file);
        }
    }

    /**
     * Update the status of each file in this file set to the given status.
     * @param status the new status which each file in this set should have.
     */
    public void updateStatus(FileStatus status) {
        for (CaArrayFile file : files) {
            file.setFileStatus(status);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    /**
     * Returns the <code>CaArrayFile</code> in the set corresponding to the
     * given file object or null if no match.
     *
     * @param file get <code>CaArrayFile</code> for this file.
     * @return the matching <code>CaArrayFile</code>.
     */
    public CaArrayFile getFile(File file) {
        for (CaArrayFile caArrayFile : files) {
            if (caArrayFile.isMatch(file)) {
                return caArrayFile;
            }
        }
        return null;
    }
    
    /**
     * @return the combined compressed size of the files in this fileset
     */
    public long getTotalCompressedSize() {
        long size = 0;
        for (CaArrayFile file : files) {
            size += file.getCompressedSize();
        }
        return size;
    }

    /**
     * @return the combined uncompressed size of the files in this fileset
     */
    public long getTotalUncompressedSize() {
        long size = 0;
        for (CaArrayFile file : files) {
            size += file.getUncompressedSize();
        }
        return size;
    }
}
