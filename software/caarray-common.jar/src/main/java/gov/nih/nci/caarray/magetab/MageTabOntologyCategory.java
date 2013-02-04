//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package gov.nih.nci.caarray.magetab;

/**
 * Ontology categories implicit in MAGE-TAB documents.
 * 
 */
public enum MageTabOntologyCategory {

    /**
     * ExperimentDesignType category.
     */
    EXPERIMENTAL_DESIGN_TYPE("ExperimentDesignType"),
    
    /**
     * ExperimentalFactorCategory category.
     */
    EXPERIMENTAL_FACTOR_CATEGORY("ExperimentalFactorCategory"),
    
    /**
     * LabelCompound category.
     */
    LABEL_COMPOUND("LabelCompound"),
    
    /**
     * MaterialType category.
     */
    MATERIAL_TYPE("MaterialType"),
    
    /**
     * NormalizationDescriptionType category.
     */
    NORMALIZATION_TYPE("NormalizationDescriptionType"),

    /**
     * Roles category.
     */
    ROLES("Roles"),

    /**
     * PublicationStatus category.
     */
    PUBLICATION_STATUS("PublicationStatus"),

    /**
     * ProtocolType category.
     */
    PROTOCOL_TYPE("ProtocolType"),
    
    /**
     * QualityControlDescriptionType category.
     */
    QUALITY_CONTROL_TYPE("QualityControlDescriptionType"),
    
    
    /**
     * ReplicateDescriptionType category.
     */
    REPLICATE_TYPE("ReplicateDescriptionType");

    private final String categoryName;

    MageTabOntologyCategory(String categoryName) {
        this.categoryName = categoryName;
    }

    /**
     * @return the categoryName
     */
    public String getCategoryName() {
        return categoryName;
    }

}
