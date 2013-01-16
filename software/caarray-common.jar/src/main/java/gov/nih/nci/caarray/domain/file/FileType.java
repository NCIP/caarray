//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================

package gov.nih.nci.caarray.domain.file;

import java.util.Arrays;
import java.util.Set;

import org.apache.commons.lang.builder.CompareToBuilder;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.google.inject.internal.Sets;

/**
 * Describes a type of file that can be uploaded to caArray. This is not a persistent bean - instances of this class are
 * registered with FileTypeRegistry by platform plugins which know how to parse particular file types.
 */
public class FileType implements Comparable<FileType> {
    private String name;
    private Set<String> extensions = Sets.newHashSet();
    private FileCategory category;
    private boolean parsed;
    private boolean dataMatrix;

    /**
     * create an uninitialized file type. mostly for dozer and other tooling frameworks, should avoid using in client
     * code.
     */
    public FileType() {
        // no-op
    }

    /**
     * Constructs a file type with given properties.
     * 
     * @param name type name. Names should be unique across all types registered in the type registry
     * @param extensions the extensions associated with this type. files whose names end in these extensions should be
     *            considered to have this type.
     * @param category the category to which files of this type belong.
     * @param parsed whether this type can be parsed
     */
    public FileType(String name, FileCategory category, boolean parsed, String... extensions) {
        this.name = name;
        this.category = category;
        this.parsed = parsed;
        this.extensions.addAll(Arrays.asList(extensions));
    }

    /**
     * Constructs a file type with given properties.
     * 
     * @param name type name. Names should be unique across all types registered in the type registry
     * @param extensions the extensions associated with this type. files whose names end in these extensions should be
     *            considered to have this type.
     * @param category the category to which files of this type belong.
     * @param parsed whether this type can be parsed
     * @param dataMatrix whether files of this type are data matrices
     */
    public FileType(String name, FileCategory category, boolean parsed, boolean dataMatrix, String... extensions) {
        this(name, category, parsed, extensions);
        this.dataMatrix = dataMatrix;
    }

    /**
     * @return whether this is a data matrix type
     */
    public boolean isDataMatrix() {
        return this.dataMatrix;
    }

    /**
     * @param dataMatrix the dataMatrix to set
     */
    public void setDataMatrix(boolean dataMatrix) {
        this.dataMatrix = dataMatrix;
    }

    /**
     * @return the extensions associated with this type
     */
    public Set<String> getExtensions() {
        return this.extensions;
    }

    /**
     * @param extensions the extensions to set
     */
    public void setExtensions(Set<String> extensions) {
        this.extensions = extensions;
    }

    /**
     * @return the category to which files of this type belong
     */
    public FileCategory getCategory() {
        return this.category;
    }

    /**
     * @param category the category to set
     */
    public void setCategory(FileCategory category) {
        this.category = category;
    }

    /**
     * @return the name of the type
     */
    public String getName() {
        return this.name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return whether files of this type can be parsed
     */
    public boolean isParsed() {
        return this.parsed;
    }

    /**
     * @param parsed the parsed to set
     */
    public void setParsed(boolean parsed) {
        this.parsed = parsed;
    }

    /**
     * @return true if this file type is an array design.
     */
    public boolean isArrayDesign() {
        return this.category == FileCategory.ARRAY_DESIGN;
    }

    /**
     * @return true if the system supports parsing this array design format.
     */
    public boolean isParseableArrayDesign() {
        return isArrayDesign() && isParsed();
    }

    /**
     * @return true if the system supports parsing this data format.
     */
    public boolean isParseableData() {
        return isArrayData() && isParsed();
    }

    /**
     * @return true if the file type is used for derived array data.
     */
    public boolean isDerivedArrayData() {
        return this.category == FileCategory.DERIVED_DATA;
    }

    /**
     * @return true if the file type is used for derived array data.
     */
    public boolean isRawArrayData() {
        return this.category == FileCategory.RAW_DATA;
    }

    /**
     * @return true if the file type is used for mage tab annotations.
     */
    public boolean isMageTab() {
        return this.category == FileCategory.MAGE_TAB;
    }

    /**
     * @return true if this file type is array data.
     */
    public boolean isArrayData() {
        return isRawArrayData() || isDerivedArrayData();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof FileType)) {
            return false;
        }
        final FileType other = (FileType) obj;
        return new EqualsBuilder().append(this.name, other.name).isEquals();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(this.name).toHashCode();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int compareTo(FileType ft) {
        return new CompareToBuilder().append(this.name, ft.name).toComparison();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
