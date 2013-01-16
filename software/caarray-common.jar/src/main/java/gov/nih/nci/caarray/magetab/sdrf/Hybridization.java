//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.magetab.sdrf;

import java.util.ArrayList;
import java.util.List;

/**
 * The act of hybridizing biomaterials to a microarray.
 */
public final class Hybridization extends AbstractSampleDataRelationshipNode {

    private static final long serialVersionUID = -244337508880218634L;
    private final List<FactorValue> factorValues = new ArrayList<FactorValue>();
    private ArrayDesign arrayDesign;

    /**
     * {@inheritDoc}
     */
    @Override
    public SdrfNodeType getNodeType() {
        return SdrfNodeType.HYBRIDIZATION;
    }

    @Override
    void addToSdrfList(SdrfDocument document) {
        document.getAllHybridizations().add(this);
    }

    /**
     * @return the factor values
     */
    public List<FactorValue> getFactorValues() {
        return factorValues;
    }

    /**
     * @return the array design
     */
    public ArrayDesign getArrayDesign() {
        return arrayDesign;
    }

    /**
     * @param arrayDesign the arrayDesign to be set
     */
    public void setArrayDesign(ArrayDesign arrayDesign) {
        this.arrayDesign = arrayDesign;
    }

}
