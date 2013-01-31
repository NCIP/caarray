//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.magetab.sdrf;

/**
 * Processed array data in native format.
 */
public final class DerivedArrayDataFile extends AbstractNativeFileReference {

    private static final long serialVersionUID = -244337508880218634L;

    /**
     * {@inheritDoc}
     */
    @Override
    public SdrfNodeType getNodeType() {
        return SdrfNodeType.DERIVED_ARRAY_DATA_FILE;
    }

    @Override
    void addToSdrfList(SdrfDocument document) {
        document.getAllDerivedArrayDataFiles().add(this);
    }

}
