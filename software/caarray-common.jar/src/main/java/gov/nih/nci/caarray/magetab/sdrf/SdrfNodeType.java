//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.magetab.sdrf;

/**
 * The different types of graph nodes supported in the SDRF format.
 */
public enum SdrfNodeType {

    /**
     * Source.
     */
    SOURCE(true),

    /**
     * Sample.
     */
    SAMPLE(true),

    /**
     * Extract.
     */
    EXTRACT(true),

    /**
     * LabeledExtract.
     */
    LABELED_EXTRACT(true),

    /**
     * Hybridization.
     */
    HYBRIDIZATION(true),

    /**
     * Scan.
     */
    SCAN(true),

    /**
     * Normalization.
     */
    NORMALIZATION(true),

    /**
     * ArrrayDataFile.
     */
    ARRAY_DATA_FILE(true),

    /**
     * ArrrayDataMatrix.
     */
    ARRAY_DATA_MATRIX(true),

    /**
     * DerivedArrrayDataFile.
     */
    DERIVED_ARRAY_DATA_FILE(true),

    /**
     * DerivedArrrayDataMatrix.
     */
    DERIVED_ARRAY_DATA_MATRIX(true),

    /**
     * Image.
     */
    IMAGE(true),    

    /**
     * ArrayDesign.
     */
    ARRAY_DESIGN(false),
    
    /**
     * comment.
     */
    COMMENT(false);    

    private boolean name = false;
    
    
    /**
     * @param name whether this node type is a name node type.
     */
    private SdrfNodeType(boolean name) {
        this.name = name;
    }

    /**
     * @return whether this node type is a "name" node type, ie it names an entity. Name nodes must be unique within a
     * source-hyb-data chain
     */
    public boolean isName() {
        return name;
    }
}
