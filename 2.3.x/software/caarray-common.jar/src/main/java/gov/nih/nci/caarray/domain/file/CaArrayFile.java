//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================

package gov.nih.nci.caarray.domain.file;

import gov.nih.nci.caarray.domain.AbstractCaArrayEntity;
import gov.nih.nci.caarray.domain.MultiPartBlob;
import gov.nih.nci.caarray.domain.hybridization.Hybridization;
import gov.nih.nci.caarray.domain.project.Project;
import gov.nih.nci.caarray.security.Protectable;
import gov.nih.nci.caarray.security.ProtectableDescendent;
import gov.nih.nci.caarray.util.HibernateUtil;
import gov.nih.nci.caarray.validation.FileValidationResult;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Collections;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.lang.builder.CompareToBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.hibernate.annotations.ForeignKey;

/**
 */
@Entity
@Table(name = "caarrayfile")
@SuppressWarnings("PMD.TooManyMethods")
public class CaArrayFile extends AbstractCaArrayEntity implements Comparable<CaArrayFile>, ProtectableDescendent {
    private static final long serialVersionUID = 1234567890L;

    private String name;
    private String type;
    private String status = FileStatus.UPLOADED.name();
    private Project project;
    private FileValidationResult validationResult;
    private int uncompressedSize;
    private int compressedSize;

    // transient properties
    private transient MultiPartBlob multiPartBlob;
    private transient InputStream inputStreamToClose;
    private transient File fileToDelete;

    /**
     * Gets the name.
     *
     * @return the name
     */
    @Column(length = DEFAULT_STRING_COLUMN_SIZE)
    public String getName() {
        return this.name;
    }

    /**
     * Sets the name.
     *
     * @param name the name
     */
    public void setName(final String name) {
        this.name = name;
    }


    /**
     * @return the fileStatus
     */
    @Transient
    public FileStatus getFileStatus() {
        return getStatus() != null ? FileStatus.valueOf(getStatus()) : null;
    }

    /**
     * @param fileStatus the fileStatus to set
     */
    public void setFileStatus(FileStatus fileStatus) {
        if (fileStatus != null) {
            setStatus(fileStatus.name());
        } else {
            setStatus(null);
        }
    }

    /**
     * @return the fileType
     */
    @Transient
    public FileType getFileType() {
        return getType() != null ? FileType.valueOf(getType()) : null;
    }

    /**
     * @param fileType the fileType to set
     */
    public void setFileType(FileType fileType) {
        if (fileType != null) {
            setType(fileType.name());
        } else {
            setType(null);
        }
    }

    /**
     * @return the project
     */
    @ManyToOne
    @JoinColumn(updatable = false)
    @ForeignKey(name = "caarrayfile_project_fk")
    public Project getProject() {
        return this.project;
    }

    /**
     * @param project the project to set
     */
    public void setProject(Project project) {
        this.project = project;
    }

    /**
     * @return the uncompressed size, in bytes
     */
    public int getUncompressedSize() {
        return this.uncompressedSize;
    }

    private void setUncompressedSize(int uncompressedSize) {
        this.uncompressedSize = uncompressedSize;
    }

    /**
     * @return the compressed size, in bytes
     */
    public int getCompressedSize() {
        return this.compressedSize;
    }

    private void setCompressedSize(int compressedSize) {
        this.compressedSize = compressedSize;
    }

    /**
     * {@inheritDoc}
     */
    public int compareTo(CaArrayFile o) {
        return new CompareToBuilder()
            .append(getProject(), o.getProject())
            .append(getFileStatus(), o.getFileStatus())
            .append(getFileType(), o.getFileType())
            .append(getName(), o.getName())
            .append(getId(), o.getId())
            .toComparison();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return new ToStringBuilder(this)
            .appendSuper(super.toString())
            .append("name", this.name)
            .append("fileStatus", this.status)
            .append("type", this.type)
            .toString();
    }

    /**
     * @return the status
     */
    @Column(length = DEFAULT_STRING_COLUMN_SIZE)
    public String getStatus() {
        return this.status;
    }

    /**
     * @param status the status to set
     */
    public void setStatus(String status) {
        checkForLegalStatusValue(status);
        this.status = status;
    }

    private void checkForLegalStatusValue(String checkStatus) {
        if (checkStatus != null) {
            FileStatus.valueOf(checkStatus);
        }
    }

    /**
     * @return the type
     */
    @Column(length = DEFAULT_STRING_COLUMN_SIZE)
    public String getType() {
        return this.type;
    }

    /**
     * @param type the type to set
     */
    public void setType(String type) {
        checkForLegalTypeValue(type);
        this.type = type;
    }

    private void checkForLegalTypeValue(String checkType) {
        if (checkType != null) {
            FileType.valueOf(checkType);
        }
    }

    /**
     * @return the validationResult
     */
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @ForeignKey(name = "caarrayfile_validation_result_fk")
    public FileValidationResult getValidationResult() {
        return this.validationResult;
    }

    /**
     * @param validationResult the validationResult to set
     */
    public void setValidationResult(FileValidationResult validationResult) {
        this.validationResult = validationResult;
    }

    /**
     * Returns true if this <code>CaArrayFile</code> wraps access to the given physical
     * file.
     *
     * @param file check if this is the wrapped file
     * @return true if this is the wrapped file.
     */
    boolean isMatch(File file) {
        return file != null && getName() != null && getName().equals(file.getName());
    }

    /**
     * Writes the contents of a stream to the storage for this file's contents. Note that the
     * contents may only be set once (i.e. no overwrites are allowed).
     *
     * @param inputStream read the file contents from this input stream.
     * @throws IOException if there is a problem writing the contents.
     */
    public void writeContents(InputStream inputStream) throws IOException {
        if (this.multiPartBlob == null) {
            this.multiPartBlob = new MultiPartBlob();
            MultiPartBlob.MetaData metaData = this.multiPartBlob.writeDataCompressed(inputStream);
            setUncompressedSize(metaData.getUncompressedBytes());
            setCompressedSize(metaData.getCompressedBytes());
        } else {
            throw new IllegalStateException("Can't reset the contents of an existing CaArrayFile");
        }
    }

    /**
     * Returns an input stream to access the contents of the file.
     *
     * @return the input stream to read.
     * @throws IOException if the contents couldn't be accessed.
     */
    public InputStream readContents() throws IOException {
        return this.multiPartBlob.readCompressedContents();
    }

    /**
     * @return the multiPartBlob
     */
    @Embedded
    private MultiPartBlob getMultiPartBlob() {
        return this.multiPartBlob;
    }

    /**
     * @param multiPartBlob the multiPartBlob to set
     */
    @SuppressWarnings({"unused", "PMD.UnusedPrivateMethod" })
    private void setMultiPartBlob(MultiPartBlob multiPartBlob) {
        this.multiPartBlob = multiPartBlob;
    }

    /**
     * {@inheritDoc}
     */
    public Collection<? extends Protectable> relatedProtectables() {
        // we cheat a little bit here - we know the only modification operations allowed on files
        // are deleting and changing file type, and those are only allowed in the unimported status
        // therefore it is sufficient to only rely on the Project to which it belongs as the related
        // Protectable. If it does not belong to a project, then protections do not apply to it at all
        // (ie it is an array design file or some such)
        return getProject() != null ? Collections.singleton(getProject()) : null;
    }

    /**
     * Clear the file contents from memory.
     */
    public void clearAndEvictContents() {
        HibernateUtil.getCurrentSession().evict(this);
        if (getMultiPartBlob() != null) {
            getMultiPartBlob().clearAndEvictData();
        }
    }

    /**
     * Get the input stream that needs to be closed in postflush.
     * @return the input stream
     */
    @Transient
    public InputStream getInputStreamToClose() {
        return this.inputStreamToClose;
    }

    /**
     * Get the file the needs to be deleted in post flush.
     * @return the file
     */
    @Transient
    public File getFileToDelete() {
        return this.fileToDelete;
    }

    /**
     * Check whether file is associated with any hyb.
     * @return boolean
     */
    @Transient
    private boolean isAssocToHyb() {
        boolean associated = false;

        if (getProject() != null && getFileStatus().equals(FileStatus.IMPORTED)) {
            for (Hybridization hyb : getProject().getExperiment().getHybridizations()) {
                if (hyb.getAllDataFiles().contains(this)) {
                    associated = true;
                }
            }
        }
        return associated;
    }

    /**
     * Check whether this file is deletable.
     * @return boolean
     */
    @Transient
    public boolean isDeletable() {

        return (this.getFileStatus().isDeletable() && !this.isAssocToHyb());
    }

    /**
     * Check whether this file has status of importable.
     * @return boolean
     */
    @Transient
    public boolean isImportable() {
        return this.getFileStatus().isImportable();
    }

    /**
     * Check whether this file has status of validatable.
     * @return boolean
     */
    @Transient
    public boolean isValidatable() {
        return this.getFileStatus().isValidatable();
    }

}
