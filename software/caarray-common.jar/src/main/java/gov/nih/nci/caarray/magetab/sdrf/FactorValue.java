//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
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
    private OntologyTerm term;

    void addToSdrfList(SdrfDocument document) {
        document.getAllFactorValues().add(this);
    }

    /**
     * @return the explicit value of this factor value. this is used if no term source is specified for
     * a factor value. otherwise, the value will be contained in the term property
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
     * @return the unit for this factor value (optional)
     */
    public OntologyTerm getUnit() {
        return unit;
    }

    /**
     *
     * @param unit the unit
     */
    public void setUnit(OntologyTerm unit) {
        this.unit = unit;
    }

    /**
     * @return the term value of this factor value
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
}
