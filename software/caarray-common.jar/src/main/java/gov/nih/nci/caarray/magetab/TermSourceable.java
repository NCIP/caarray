//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package gov.nih.nci.caarray.magetab;

/**
 * Interface to be implemented by classes representing SDRF columns which can be followed by a TermSource column. 
 * These would be columns specifying an item coming from an ontology, and the TermSource column would specify
 * the ontology.
 * 
 * @author Bill Mason
 */
public interface TermSourceable {
    /**
     * 
     * @return the term source specifying the ontology for the preceding column.
     */
    TermSource getTermSource();

    /**
     * 
     * @param termSource the term source specifying the ontology for the preceding column.
     */
    void setTermSource(TermSource termSource);
}
