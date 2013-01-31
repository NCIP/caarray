//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.domain.array;

import gov.nih.nci.caarray.domain.AbstractCaArrayObject;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.OneToMany;

/**
 * Groupping mechanism for Arrays.
 */
@Entity
public class ArrayGroup extends AbstractCaArrayObject {

    private static final long serialVersionUID = 697901960168637674L;

    private String name;
    private Set<Array> arrays = new HashSet<Array>();

    /**
     * @return the name of the group
     */
    public String getName() {
        return name;
    }

    /**
     * @param name new name for group
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return all arrays in this group
     */
    @OneToMany(mappedBy = "arrayGroup")
    public Set<Array> getArrays() {
        return arrays;
    }

    @SuppressWarnings({"unused", "PMD.UnusedPrivateMethod" })
    private void setArrays(Set<Array> arrays) {
        this.arrays = arrays;
    }

}
