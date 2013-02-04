//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package gov.nih.nci.caarray.magetab.sdrf;

/**
 * A data processing step that reduces noise in microarray data.
 */
public final class Normalization extends AbstractSampleDataRelationshipNode {

    private static final long serialVersionUID = -244337508880218634L;

    /**
     * {@inheritDoc}
     */
    @Override
    public SdrfNodeType getNodeType() {
        return SdrfNodeType.NORMALIZATION;
    }

    @Override
    void addToSdrfList(SdrfDocument document) {
        document.getAllNormalizations().add(this);
    }

}
