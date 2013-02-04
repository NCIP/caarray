//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package gov.nih.nci.caarray.magetab;

/**
 * Interface to be implemented by classes representing SDRF columns which can be followed by a Unit column. These
 * would be columns containing a numeric value, and the Unit would specify the unit for that value, as a 
 * term from an ontology.
 * 
 * @author Bill Mason
 */
public interface Unitable {
    /**
     * 
     * @return the term specifying the unit
     */
    OntologyTerm getUnit();

    /**
     * 
     * @param unit the term specifying the unit
     */
    void setUnit(OntologyTerm unit);

}
