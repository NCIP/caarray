//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package gov.nih.nci.caarray.magetab.sdrf;

/**
 * A microarray image acquired after a hybridization.
 */
public final class Image extends AbstractNativeFileReference {

    private static final long serialVersionUID = -244337508880218634L;

    /**
     * {@inheritDoc}
     */
    @Override
    public SdrfNodeType getNodeType() {
        return SdrfNodeType.IMAGE;
    }

    @Override
    void addToSdrfList(SdrfDocument document) {
        document.getAllImages().add(this);
    }

}
