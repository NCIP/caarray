//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.magetab;

/**
 * @author Bill Mason
 *
 */
public interface Unitable {
    /**
     * 
     * @return OntologyTerm term
     */
    OntologyTerm getUnit();

    /**
     * 
     * @param unit OntologyTerm
     */
    void setUnit(OntologyTerm unit);

}
