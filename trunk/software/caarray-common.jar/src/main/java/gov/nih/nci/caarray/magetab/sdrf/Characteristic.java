//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.magetab.sdrf;

import gov.nih.nci.caarray.magetab.OntologyTerm;
import gov.nih.nci.caarray.magetab.Unitable;

import java.io.Serializable;

/**
 * A characteristic of a bio-material.
 */
public final class Characteristic implements Serializable, Unitable {

    private static final long serialVersionUID = 2872802845635671179L;

    private String category;
    private String value;
    private OntologyTerm term;
    private OntologyTerm unit;

    /**
     * @return the term containing the value of this characteristic
     */
    public OntologyTerm getTerm() {
        return term;
    }

    /**
     * @param term the term to set
     */
    public void setTerm(OntologyTerm term) {
        this.term = term;
    }

    /**
     * @return the unit of this characteristic (optional)
     */
    public OntologyTerm getUnit() {
        return unit;
    }

    /**
     * @param unit the unit to set
     */
    public void setUnit(OntologyTerm unit) {
        this.unit = unit;
    }

    /**
     * @return the explicit value of this characteristic. this is used if no term source is specified for
     * a characteristic. otherwise, the value will be contained in the term property
     */
    public String getValue() {
        return value;
    }    

    /**
     * @param value the value to set
     */
    public void setValue(String value) {
        this.value = value;
    }
    
    /**
     * @return either the direct value or the term value of this characteristic, whichever is present.
     */
    public String getTermOrDirectValue() {
        return this.term == null ? this.value : this.term.getValue();
    }

    /**
     * @return the category of this characteristic
     */
    public String getCategory() {
        return category;
    }

    /**
     * @param category The category to set
     */
    public void setCategory(String category) {
        this.category = category;
    }

}
