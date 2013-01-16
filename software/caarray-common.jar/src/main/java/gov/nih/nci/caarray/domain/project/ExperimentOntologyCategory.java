//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.domain.project;

/**
 * Enum of Ontology categories for various concepts used in caarray
 * The majority of these categories come from the MGED ontology.
 */
public enum ExperimentOntologyCategory {

    /**
     * Organism category, used for organisms.
     */
    ORGANISM("Organism", ExperimentOntology.MGED_ONTOLOGY),

    /**
     * OrganismPart category, used for tissue site.
     */
    ORGANISM_PART("OrganismPart", ExperimentOntology.MGED_ONTOLOGY),

    /**
     * MaterialType category, used for tissue types.
     */
    MATERIAL_TYPE("MaterialType", ExperimentOntology.MGED_ONTOLOGY),

    /**
     * CellType category, used for cell types.
     */
    CELL_TYPE("CellType", ExperimentOntology.MGED_ONTOLOGY),

    /**
     * DiseaseState category, used for diseases/conditions of an experiment.
     */
    DISEASE_STATE("DiseaseState", ExperimentOntology.MGED_ONTOLOGY),
    
    /**
     * LabelCompound category, used for the label of a LabeledExtract.
     */
    LABEL_COMPOUND("LabelCompound", ExperimentOntology.MGED_ONTOLOGY),

    /**
     * ExperimentDesignType category, for selecting type of experiment design.
     */
    EXPERIMENT_DESIGN_TYPE("ExperimentDesignType", ExperimentOntology.MGED_ONTOLOGY),

    /**
     * QualityControlDescriptionType category, for selecting type of quality control measures.
     */
    QUALITY_CONTROL_TYPE("QualityControlDescriptionType", ExperimentOntology.MGED_ONTOLOGY),

    /**
     * QualityControlDescriptionType category, for selecting type of quality control measures.
     */
    REPLICATE_TYPE("ReplicateDescriptionType", ExperimentOntology.MGED_ONTOLOGY),

    /**
     * Roles category, used for various roles in an experiment.
     */
    ROLES("Roles", ExperimentOntology.MGED_ONTOLOGY),

    /**
     * PublicationType category, used for type of Publications.
     */
    PUBLICATION_TYPE("PublicationType", ExperimentOntology.MGED_ONTOLOGY),

    /**
     * PublicationStatus category, used for status of Publications.
     */
    PUBLICATION_STATUS("PublicationStatus", ExperimentOntology.CAARRAY),

    /**
     * Clinical Diagnosis category, one of annotation fields allowed by TCGA policy.
     */
    CLINICAL_DIAGNOSIS("Clinical Diagnosis", ExperimentOntology.CAARRAY),

    /**
     * Histologic Diagnosis category, one of annotation fields allowed by TCGA policy.
     */
    HISTOLOGIC_DIAGNOSIS("Histologic Diagnosis", ExperimentOntology.CAARRAY),

    /**
     * Pathologic Status category, one of annotation fields allowed by TCGA policy.
     */
    PATHOLOGIC_STATUS("Pathologic Status", ExperimentOntology.CAARRAY),

    /**
     * Tissue Anatomic Site  category, one of annotation fields allowed by TCGA policy.
     */
    TISSUE_ANATOMIC_SITE("Tissue Anatomic Site", ExperimentOntology.CAARRAY),

    /**
     * TechnologyType category, used for array designs.
     */
    TECHNOLOGY_TYPE("TechnologyType", ExperimentOntology.MGED_ONTOLOGY),

    /**
     * ExperimentalFactorCategory category, for experimental factor types.
     */
    EXPERIMENTAL_FACTOR_CATEGORY("ExperimentalFactorCategory", ExperimentOntology.MGED_ONTOLOGY),

    /**
     * ProtocolType category, used for protocol types.
     */
    PROTOCOL_TYPE("ProtocolType", ExperimentOntology.MGED_ONTOLOGY), 
    /**
     * External Id, used for biomaterials.
     */
    EXTERNAL_ID("ExternalId", ExperimentOntology.CAARRAY),
    /**
     * External Sample Id, used for samples. This is to support legacy data sets, new data sets should use ExternalId.
     */
    EXTERNAL_SAMPLE_ID("ExternalSampleId", ExperimentOntology.CAARRAY);

    private final String categoryName;
    private final ExperimentOntology ontology;

    ExperimentOntologyCategory(String categoryName, ExperimentOntology ontology) {
        this.categoryName = categoryName;
        this.ontology = ontology;
    }

    /**
     * @return the categoryName
     */
    public String getCategoryName() {
        return this.categoryName;
    }

    /**
     * @return the ontology to which this category belongs
     */
    public ExperimentOntology getOntology() {
        return ontology;
    }
}
