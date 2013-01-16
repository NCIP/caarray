//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.magetab.sdrf;

/**
 * Reference to a raw array data matrix file. Column name in SDRF is Array Data Matrix File.
 */
public final class ArrayDataMatrixFile extends AbstractDataMatrixReference {

    private static final long serialVersionUID = -5200861196061030762L;

    @Override
    void addToSdrfList(SdrfDocument document) {
        document.getAllArrayDataMatrixFiles().add(this);
    }

    /**
     * @return the node type.
     */
    @Override
    public SdrfNodeType getNodeType() {
        return SdrfNodeType.ARRAY_DATA_MATRIX;
    }

}
