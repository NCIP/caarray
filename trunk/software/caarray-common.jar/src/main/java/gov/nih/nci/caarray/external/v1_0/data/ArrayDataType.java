//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.external.v1_0.data;

import gov.nih.nci.caarray.external.v1_0.AbstractCaArrayEntity;

import java.util.HashSet;
import java.util.Set;

/**
 * ArrayDataType represents a type of array. Different array types have different possible
 * quantities (as represented by <code>QuantitationType</code>s) that can be measured by them.
 * 
 * @author dkokotov
 */
public class ArrayDataType extends AbstractCaArrayEntity {
    private static final long serialVersionUID = 1L;

    private String name;
    private String version;
    private Set<QuantitationType> quantitationTypes = new HashSet<QuantitationType>();

    /**
     * @return the name of this array type
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the version of this array type
     */
    public String getVersion() {
        return version;
    }

    /**
     * @param version the version to set
     */
    public void setVersion(String version) {
        this.version = version;
    }

    /**
     * @return the quantities that can be measured by arrays of this type. Note that individual arrays may only measure
     *         a subset of this.
     */
    public Set<QuantitationType> getQuantitationTypes() {
        return quantitationTypes;
    }

    /**
     * @param quantitationTypes the quantitationTypes to set
     */
    public void setQuantitationTypes(Set<QuantitationType> quantitationTypes) {
        this.quantitationTypes = quantitationTypes;
    }

}
