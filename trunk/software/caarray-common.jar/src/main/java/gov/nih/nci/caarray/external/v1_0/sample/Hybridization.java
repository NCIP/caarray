//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.external.v1_0.sample;

import gov.nih.nci.caarray.external.v1_0.array.ArrayDesign;
import gov.nih.nci.caarray.external.v1_0.factor.FactorValue;

import java.util.HashSet;
import java.util.Set;

/**
 * a Hybridization is an experiment graph node that represents the product of hybridizing extracted genetic material
 * with the probes on an array.
 * 
 * @author dkokotov
 */
public class Hybridization extends AbstractExperimentGraphNode {
    private static final long serialVersionUID = 1L;
    
    private Set<FactorValue> factorValues = new HashSet<FactorValue>();
    private ArrayDesign arrayDesign;

    /**
     * @return the set of factor values associated with this hybridization.
     */
    public Set<FactorValue> getFactorValues() {
        return factorValues;
    }

    /**
     * @param factorValues the set of factor values associated with this hybridization.
     */
    public void setFactorValues(Set<FactorValue> factorValues) {
        this.factorValues = factorValues;
    }

    /**
     * @return the array design for the array to which this hybridization represents the act of hybridizing.
     */
    public ArrayDesign getArrayDesign() {
        return arrayDesign;
    }

    /**
     * @param arrayDesign the array design for the array associated with this hybridization.
     */
    public void setArrayDesign(ArrayDesign arrayDesign) {
        this.arrayDesign = arrayDesign;
    }
}
