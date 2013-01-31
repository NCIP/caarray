//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.magetab.sdrf;

import gov.nih.nci.caarray.magetab.OntologyTerm;
import gov.nih.nci.caarray.magetab.Unitable;
import gov.nih.nci.caarray.magetab.idf.ExperimentalFactor;

import java.io.Serializable;

/**
 * 
 */
public class FactorValue implements Serializable, Unitable {
    private static final long serialVersionUID = 28728045635671179L;
    private String value;
    private ExperimentalFactor factor;
    private OntologyTerm unit;

    void addToSdrfList(SdrfDocument document) {
        document.getAllFactorValues().add(this);
    }

    /**
     * @return the value
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
     * @return ExperimentalFactor
     */
    public ExperimentalFactor getFactor() {
        return factor;
    }

    /**
     * @param factor the factor to set
     */
    public void setFactor(ExperimentalFactor factor) {
        this.factor = factor;
    }

    /**
     * 
     * @return OntologyTerm get the term
     */
    public OntologyTerm getUnit() {
        return unit;
    }

    /**
     * 
     * @param unit the OntologyTerm
     */
    public void setUnit(OntologyTerm unit) {
        this.unit = unit;
    }

}
