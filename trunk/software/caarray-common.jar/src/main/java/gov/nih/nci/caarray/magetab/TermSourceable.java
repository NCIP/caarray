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
public interface TermSourceable {
    /**
     * 
     * @return TermSource a term source
     */
    TermSource getTermSource();

    /**
     * 
     * @param termSource termsources
     */
    void setTermSource(TermSource termSource);
}
