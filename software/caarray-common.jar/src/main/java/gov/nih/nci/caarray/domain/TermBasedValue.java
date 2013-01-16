//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.domain;

import gov.nih.nci.caarray.domain.vocabulary.Term;

/**
 * A UnitableValue with a controlled vocabulary term value.
 * 
 * @author dkokotov
 */
public interface TermBasedValue extends UnitableValue {
    /**
     * @return the value
     */
    Term getTerm();

    /**
     * @param term the value to set
     */
    void setTerm(Term term);
}
