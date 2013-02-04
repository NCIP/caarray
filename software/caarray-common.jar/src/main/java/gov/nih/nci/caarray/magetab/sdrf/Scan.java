//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package gov.nih.nci.caarray.magetab.sdrf;

/**
 * The act of scanning a hybridization to acquire an image.
 */
public final class Scan extends AbstractSampleDataRelationshipNode {

    private static final long serialVersionUID = -244337508880218634L;

    /**
     * {@inheritDoc}
     */
    @Override
    public SdrfNodeType getNodeType() {
        return SdrfNodeType.SCAN;
    }

    @Override
    void addToSdrfList(SdrfDocument document) {
        document.getAllScans().add(this);
    }

}
