//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.magetab.sdrf;

import gov.nih.nci.caarray.magetab.data.DataMatrix;

/**
 * Base class for data matrix references.
 */
public abstract class AbstractDataMatrixReference extends AbstractSampleDataRelationshipNode {

    private DataMatrix dataMatrix;
    
    AbstractDataMatrixReference() {
        super();
    }

    /**
     * @return the dataMatrix
     */
    public final DataMatrix getDataMatrix() {
        return dataMatrix;
    }

    /**
     * @param dataMatrix the dataMatrix to set
     */
    public final void setDataMatrix(DataMatrix dataMatrix) {
        this.dataMatrix = dataMatrix;
    }

}
