//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.domain.project;


/**
 * Enum of Ontologies for various concepts used by Experiment.
 * These correspond to TermSource instances
 */
public enum ExperimentOntology {
    /**
     * MGED Ontology.
     */
    MGED_ONTOLOGY("MO", "1.3.1.1"), // NOPMD PMD thinks the version is a hard-coded IP address

    /**
     * CAArray Local (aka User-defined) Ontology.
     */
    CAARRAY("caArray", "2.0");

    private final String ontologyName;
    private final String version;

    ExperimentOntology(String ontologyName, String version) {
        this.ontologyName = ontologyName;
        this.version = version;
    }

    /**
     * @return the ontologyName
     */
    public String getOntologyName() {
        return this.ontologyName;
    }

    /**
     * @return the ontology version
     */
    public String getVersion() {
        return version;
    }
}
