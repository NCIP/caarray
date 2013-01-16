//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.domain.file;

import gov.nih.nci.caarray.domain.project.Project;
import gov.nih.nci.caarray.validation.FileValidationResult;
import gov.nih.nci.caarray.validation.ValidationMessage;

import java.io.File;
import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.google.common.base.Preconditions;

/**
 * Provides functionality to handle multiple <code>CaArrayFiles</code> as a single set.
 */
@SuppressWarnings("PMD.CyclomaticComplexity")
// Complex rules for checking aggregate file status
public class CaArrayFileSet implements Serializable {

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
        this.projectId = p.getId();
    }

    /**
     * @param projectId Id of the project that this file set is associated with
     */
    public CaArrayFileSet(Long projectId) {
        if (projectId == null) {
            throw new IllegalArgumentException("Project Id must be non-null");
        }
        this.projectId = projectId;
    }

    /**
     * Construct a new file set, with the same files and associated project.
     *
     * @param baseFileSet file set on which to base the new file set
     */
    public CaArrayFileSet(CaArrayFileSet baseFileSet) {
        this.projectId = baseFileSet.projectId;
        this.files.addAll(baseFileSet.getFiles());
    }

    /**
     * @return related project system identifier
     */
    public Long getProjectId() {
        return this.projectId;
    }

    /**
     * File set. Only files <em>not</em> associated with a project can be added.
     */
    public CaArrayFileSet() {
        this.projectId = null;
    }

    /**
     * Adds a file to the set.
     *
     * @param file the file to add
     */
    public void add(CaArrayFile file) {
        final Long fileProjectId = file.getProject() == null ? null : file.getProject().getId();
        if (!ObjectUtils.equals(fileProjectId, this.getProjectId())) {
            throw new IllegalArgumentException(
                    String.format("file's project's (%s) and fileset project (%s) not the same", 
                            fileProjectId, this.getProjectId()));
        }
        this.files.add(file);
    }

    /**
     * Returns the contained files.
     *
     * @return the files.
     */
    public Set<CaArrayFile> getFiles() {
        return this.files;
    }

    /**
     * Get a subset of CaArrayFile objects with file type specified.
     *
     * @param ft File type must not be null
     * @return set of CaArrayFile objects
     */
    public Set<CaArrayFile> getFilesByType(FileType ft) {
        Preconditions.checkNotNull(ft);
        final Set<CaArrayFile> sdrfFiles = new HashSet<CaArrayFile>();
        for (final CaArrayFile fileIt : this.files) {
            if (ft.equals(fileIt.getFileType())) {
                sdrfFiles.add(fileIt);
            }
        }
        return sdrfFiles;
    }

    /**
     * Get a subset of CaArrayFile objects that are array data file types.
     *
     * @return set of CaArrayFile objects
     */
    public Set<CaArrayFile> getArrayDataFiles() {
        final Set<CaArrayFile> dataFiles = new HashSet<CaArrayFile>();
        for (final CaArrayFile fileIt : this.files) {
            if (fileIt.getFileType().isArrayData()) {
                dataFiles.add(fileIt);
            }
        }
        return dataFiles;
    }

    /**
     * Returns the aggregate status of the file set.
     *
     * @return status of the set.
     */
    public FileStatus getStatus() {
        if (!this.files.isEmpty() && allStatusesEqual(this.files.iterator().next().getFileStatus())) {
            return this.files.iterator().next().getFileStatus();
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
        final Set<FileStatus> fileSetStatuses = new HashSet<FileStatus>();
        for (final CaArrayFile file : this.files) {
            fileSetStatuses.add(file.getFileStatus());
        }
        for (final FileStatus status : statuses) {
            fileSetStatuses.remove(status);
        }
        return fileSetStatuses.isEmpty();
    }

    /**
     * tells if the status is in the set.
     *
     * @param status the status.
     * @return true if it is in the set.
     */
    private boolean statusesContains(FileStatus status) {
        for (final CaArrayFile file : this.files) {
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
        for (final CaArrayFile file : fileCollection) {
            this.files.add(file);
        }
    }

    /**
     * Update the status of each file in this file set to the given status.
     *
     * @param status the new status which each file in this set should have.
     */
    public void updateStatus(FileStatus status) {
        for (final CaArrayFile file : this.files) {
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
     * Returns the <code>CaArrayFile</code> in the set corresponding to the given file object or null if no match.
     *
     * @param file get <code>CaArrayFile</code> for this file.
     * @return the matching <code>CaArrayFile</code>.
     */
    public CaArrayFile getFile(File file) {
        for (final CaArrayFile caArrayFile : this.files) {
            if (isWrapperFor(caArrayFile, file)) {
                return caArrayFile;
            }
        }
        return null;
    }

    /**
     * Returns true if this <code>CaArrayFile</code> wraps access to the given physical file.
     *
     * @param file check if this is the wrapped file
     * @return true if this is the wrapped file.
     */
    boolean isWrapperFor(CaArrayFile caArrayFile, File file) {
        return file != null && caArrayFile.getName() != null && caArrayFile.getName().equals(file.getName());
    }

    /**
     * Returns the <code>CaArrayFile</code> in the set with the given name or null if no match.
     *
     * @param name the name of the file to find
     * @return the <code>CaArrayFile</code> with given name, or null if no such file exists in this set.
     */
    public CaArrayFile getFile(String name) {
        for (final CaArrayFile caArrayFile : getFiles()) {
            if (name.equals(caArrayFile.getName())) {
                return caArrayFile;
            }
        }
        return null;
    }

    /**
     * Validation messages on files that have parents are copies to the respective
     * parents.  The parents do not have to be in the set.
     */
    public void pullUpValidationMessages() {
        for (CaArrayFile file : getFiles()) {
            pullUpSingleFileValidation(file);
        }
    }

    private void pullUpSingleFileValidation(CaArrayFile file) {
        if (needToPullUpFrom(file)) {
            CaArrayFile parent = file.getParent();
            FileValidationResult childResults = file.getValidationResult();
            FileValidationResult parentResults = getOrCreateValdationResults(parent);
            for (ValidationMessage message : childResults.getMessages()) {
                parentResults.addMessage(new ValidationMessage(message));
            }
        }
    }

    private boolean needToPullUpFrom(CaArrayFile file) {
        return file.getParent() != null
                && file.getValidationResult() != null
                && !file.getValidationResult().getMessages().isEmpty();
    }

    private FileValidationResult getOrCreateValdationResults(CaArrayFile parent) {
        if (parent.getValidationResult() == null) {
            parent.setValidationResult(new FileValidationResult());
        }
        return parent.getValidationResult();
    }

    /**
     * Returns a value to indicate if the fileset has been validated or not.
     * @return a boolean value to indicate if the fileset has been validated or not.
     */
    public boolean isValidated() {
        return getStatus().equals(FileStatus.VALIDATED) || getStatus().equals(FileStatus.VALIDATED_NOT_PARSED);
    }
}
