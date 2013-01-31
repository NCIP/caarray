//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.magetab.idf;

import gov.nih.nci.caarray.magetab.OntologyTerm;

import java.io.Serializable;

/**
 * A factor that is varied in an investigation design.
 */
public final class ExperimentalFactor implements Serializable {

    private static final long serialVersionUID = 5356087397773635534L;

    private String name;
    private OntologyTerm type;

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the type
     */
    public OntologyTerm getType() {
        return type;
    }

    /**
     * @param type the type to set
     */
    public void setType(OntologyTerm type) {
        this.type = type;
    }

}
