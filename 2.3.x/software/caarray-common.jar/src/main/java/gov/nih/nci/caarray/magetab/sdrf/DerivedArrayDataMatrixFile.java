//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.magetab.sdrf;

/**
 * Reference to a der9ved array data matrix file. Column name in SDRF is Derived Array Data Matrix File.
 */
public class DerivedArrayDataMatrixFile extends AbstractDataMatrixReference {

    private static final long serialVersionUID = -8339463718689183851L;

    @Override
    void addToSdrfList(SdrfDocument document) {
        document.getAllDerivedArrayDataMatrixFiles().add(this);
    }

    /**
     * @return the node type.
     */
    @Override
    public SdrfNodeType getNodeType() {
        return SdrfNodeType.DERIVED_ARRAY_DATA_MATRIX;
    }

}
