//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================

package gov.nih.nci.caarray.domain.file;

import gov.nih.nci.caarray.domain.AbstractCaArrayEntity;
import gov.nih.nci.caarray.domain.AbstractCaArrayObject;
import gov.nih.nci.caarray.domain.project.Project;
import gov.nih.nci.caarray.security.Protectable;
import gov.nih.nci.caarray.security.ProtectableDescendent;
import gov.nih.nci.caarray.validation.FileValidationResult;
import gov.nih.nci.caarray.validation.UniqueConstraint;
import gov.nih.nci.caarray.validation.UniqueConstraintField;

import java.net.URI;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.lang.builder.CompareToBuilder;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Index;
import org.hibernate.annotations.Type;
import org.hibernate.validator.NotNull;

import com.google.common.base.Preconditions;
import com.google.inject.Inject;

/**
 * This class represents metadata about a file managed by caArray.
 */
@Entity
@BatchSize(size = AbstractCaArrayObject.DEFAULT_BATCH_SIZE)
@UniqueConstraint(fields = { @UniqueConstraintField(name = "name"), @UniqueConstraintField(name = "project"),
        @UniqueConstraintField(name = "uniqueNameProject") }, message = "{caarrayfile.uniqueConstraint}")
@Table(name = "caarrayfile")
@SuppressWarnings("PMD.TooManyMethods")
public class CaArrayFile extends AbstractCaArrayEntity implements Comparable<CaArrayFile>, ProtectableDescendent {
    private static final long serialVersionUID = 1234567890L;

    private String name;
    private String type;
    private String status;
    private Project project;
    private FileValidationResult validationResult;
    private long uncompressedSize;
    private long compressedSize;
    private long partialSize;
    private URI dataHandle;
    private CaArrayFile parent;
    private Set<CaArrayFile> children = new HashSet<CaArrayFile>();
    private Boolean uniqueNameProject;

    @Inject
    private static transient FileTypeRegistry typeRegistry;

    /**
     * Default blank constructor.
     */
    public CaArrayFile() {
        super();
        uniqueNameProject = true;
    }

    /**
     * Creates a CaArrayFile with a parent.
     * @param parent the parent file.
     */
    public CaArrayFile(CaArrayFile parent) {
        super();
        this.parent = parent;
        if (parent == null) {
            uniqueNameProject = true;
        }
    }

    /**
     * Gets the name.
     *
     * @return the name
     */
    @Column(length = DEFAULT_STRING_COLUMN_SIZE)
    @Index(name = "idx_name")
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
        propigateStatusToParent();
    }

    /**
     * @return the fileType
     */
    @Transient
    public FileType getFileType() {
        return getType() != null ? typeRegistry.getTypeByName(getType()) : null;
    }

    /**
     * @param fileType the fileType to set
     */
    public void setFileType(FileType fileType) {
        if (fileType != null) {
            setType(fileType.getName());
        } else {
            setType(null);
        }
    }

    /**
     * Indicates if the CaArrayFile has children (child CaArrayFile's) or not.
     *
     * @return a boolean flag that indicates if the CaArrayFile has children or not.
     */
    @Transient
    public boolean hasChildren() {
        return !getChildren().isEmpty();
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
     * @return the parent
     */
    @ManyToOne
    @JoinColumn(name = "parent_id", nullable = true, updatable = false)
    @ForeignKey(name = "caarrayfile_parent_fk")
    public CaArrayFile getParent() {
        return parent;
    }

    /**
     * @param parent the parent to set
     */
    @SuppressWarnings("unused")
    private void setParent(CaArrayFile parent) {
        this.parent = parent;
    }

    /**
     * @return whether or not this is a partial file. This is indicated by the record containing a parent or not.
     */
    @Transient
    public boolean isPartial() {
        return getParent() != null;
    }

    /**
     * @return the children
     */
    @OneToMany(mappedBy = "parent", fetch = FetchType.LAZY)
    @Cascade({ org.hibernate.annotations.CascadeType.ALL })
    public Set<CaArrayFile> getChildren() {
        return children;
    }

    /**
     * @param children the children to set
     */
    @SuppressWarnings("unused")
    private void setChildren(Set<CaArrayFile> children) {
        this.children = children;
    }

    /**
     * @return the uncompressed size, in bytes
     */
    public long getUncompressedSize() {
        return this.uncompressedSize;
    }

    /**
     * This method should generally not be called directly, as file size is calculated when data is written to the file.
     * It is left public to support use in query by example and tooling relying on JavaBean property conventions
     *
     * @param uncompressedSize the uncompressed size of the file, in bytes
     */
    public void setUncompressedSize(long uncompressedSize) {
        this.uncompressedSize = uncompressedSize;
    }

    /**
     * @return the compressed size, in bytes
     */
    public long getCompressedSize() {
        return this.compressedSize;
    }

    /**
     * This method should generally not be called directly, as file size is calculated when data is written to the file.
     * It is left public to support use in query by example and tooling relying on JavaBean property conventions
     *
     * @param compressedSize the compressed size of the file, in bytes
     */
    public void setCompressedSize(long compressedSize) {
        this.compressedSize = compressedSize;
    }

    /**
     * @return the partialSize
     */
    public long getPartialSize() {
        return partialSize;
    }

    /**
     * @param partialSize the partialSize to set
     */
    public void setPartialSize(long partialSize) {
        this.partialSize = partialSize;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int compareTo(CaArrayFile o) {
        if (this == o) {
            return 0;
        }

        return new CompareToBuilder().append(getProject(), o.getProject()).append(getFileStatus(), o.getFileStatus())
                .append(getFileType(), o.getFileType()).append(getName(), o.getName()).append(getId(), o.getId())
                .toComparison();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return super.toString() + ", name=" + getName();
    }

    /**
     * @return the status
     */
    @Column(length = DEFAULT_STRING_COLUMN_SIZE)
    @NotNull
    public String getStatus() {
        return this.status;
    }

    private void setStatus(String status) {
        checkForLegalStatusValue(status);
        this.status = status;
    }

    private void propigateStatusToParent() {
        if (thisIsOnlyChild()) {
            parent.setStatus(status);
        }
    }

    private boolean thisIsOnlyChild() {
        return parent != null && parent.getChildren().size() == 1;
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
        Preconditions.checkArgument(checkType == null || typeRegistry.getTypeByName(checkType) != null);
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
     * {@inheritDoc}
     */
    @Override
    public Collection<? extends Protectable> relatedProtectables() {
        // we cheat a little bit here - we know the only modification operations allowed on files
        // are deleting and changing file type, and those are only allowed in the unimported status
        // therefore it is sufficient to only rely on the Project to which it belongs as the related
        // Protectable. If it does not belong to a project, then protections do not apply to it at all
        // (ie it is an array design file or some such)
        return getProject() != null ? Collections.singleton(getProject()) : null;
    }

    /**
     * Check whether this file is eligible to be imported.
     *
     *
     * @return boolean
     */
    @Transient
    public boolean isImportable() {
        return this.getFileStatus().isImportable();
    }

    /**
     * Check whether this file is eligible to be validated.
     *
     *
     * @return boolean
     */
    @Transient
    public boolean isValidatable() {
        return this.getFileStatus().isValidatable();
    }

    /**
     * Check whether this is a file that was previously imported but not parsed, but now can be imported and parsed (due
     * to a parsing FileHandler being implemented for it).
     *
     * @return true if the file can be re-imported and parsed, false otherwise.
     */
    @Transient
    public boolean isUnparsedAndReimportable() {
        return getFileStatus() == FileStatus.IMPORTED_NOT_PARSED
                && (getFileType().isParseableArrayDesign() || getFileType().isParseableData());
    }

    /**
     * @return the dataHandle
     */
    @Column(length = DEFAULT_STRING_COLUMN_SIZE)
    @NotNull
    @Index(name = "idx_handle")
    @Type(type = "uri")
    public URI getDataHandle() {
        return this.dataHandle;
    }

    /**
     * @param dataHandle the dataHandle to set
     */
    public void setDataHandle(URI dataHandle) {
        this.dataHandle = dataHandle;
    }

    /**
     * Adds a new child CaArrayFile.
     *
     * @param child the child CaArrayFile to add.
     */
    public void addChild(CaArrayFile child) {
        getChildren().add(child);
    }

    /**
     * Removes a child CaArrayFile.
     *
     * @param child the child CaArrayFile to remove.
     */
    public void removeChild(CaArrayFile child) {
        getChildren().remove(child);
    }

    /**
     * @return a non-null value if this file is unique by name and project.
     */
    @SuppressWarnings("unused")
    private Boolean getUniqueNameProject() {
        return uniqueNameProject;
    }

    /**
     * @param uniqueNameProject set to a non-null value to make this file unique by name and project.
     */
    @SuppressWarnings("unused")
    private void setUniqueNameProject(Boolean uniqueNameProject) {
        this.uniqueNameProject = uniqueNameProject;
    }
}
