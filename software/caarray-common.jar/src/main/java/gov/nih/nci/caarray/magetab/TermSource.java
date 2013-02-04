//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package gov.nih.nci.caarray.magetab;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

/**
 * A repository of controlled vocabulary terms. Must have a non-null, non-empty name
 */
public final class TermSource {

    private String name;
    private String file;
    private String version;

    /**
     * Create new TermSource with given name.
     * @param name the repository name; must not be blank or null
     */
    public TermSource(String name) {
        if (StringUtils.isBlank(name)) {
            throw new IllegalArgumentException("Term source name must not be blank");
        }
        this.name = name;
    }

    /**
     * Create new TermSource with given name, url and version.
     * @param name the repository name 
     * @param file the url (called file in MAGE TAB terminology)
     * @param version the version;
     */
    public TermSource(String name, String file, String version) {
        this(name);
        this.file = file;
        this.version = version;
    }

    /**
     * @return the file
     */
    public String getFile() {
        return this.file;
    }

    /**
     * @param file the file to set
     */
    public void setFile(String file) {
        this.file = file;
    }

    /**
     * @return the name
     */
    public String getName() {
        return this.name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        if (StringUtils.isBlank(name)) {
            throw new IllegalArgumentException("Term source name must not be blank");
        }
        this.name = name;
    }

    /**
     * @return the version
     */
    public String getVersion() {
        return this.version;
    }

    /**
     * @param version the version to set
     */
    public void setVersion(String version) {
        this.version = version;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof TermSource)) {
            return false;
        }
        
        if (obj == this) {
            return true;
        }
        
        TermSource ts = (TermSource) obj;
        return new EqualsBuilder().append(this.getName(), ts.getName()).append(this.getFile(), ts.getFile()).append(
                this.getVersion(), ts.getVersion()).isEquals();
    }    
    
    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(this.getName()).append(this.getFile()).append(this.getVersion())
                .toHashCode();
    }
}
