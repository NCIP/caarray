//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.domain.project;


import gov.nih.nci.caarray.domain.AbstractCaArrayEntity;

import javax.persistence.Column;
import javax.persistence.Entity;


/**
 * Represents a single assay type associated with an Experiment or Array Design.
 */
@Entity
public class AssayType extends AbstractCaArrayEntity implements Comparable<AssayType> {

    private static final long serialVersionUID = 68829322193456517L;

    private String name;

    /**
     * @deprecated hibernate & castor only
     */
    @Deprecated
    public AssayType() {
        // hibernate & castor only
    }
    /**
     * @param name the name
     */
    public AssayType(String name) {
        this.name = name;
    }

    /**
     * Gets the name.
     * @return the name
     */
    @Column(length = DEFAULT_STRING_COLUMN_SIZE)
    public String getName() {
        return name;
    }

    /**
     * Sets the name.
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return super.toString() + ", name=" + getName();
    }
    
    /**
     * Compares assayTypes by name.
     * @param o other assayType to compare to
     * @return result of comparison
     */
    public int compareTo(AssayType o) {
        if (o == null) {
            return 1;
        }
        return this.getName().compareToIgnoreCase(o.getName());
    }
}
