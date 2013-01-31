//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.external.v1_0.value;

import gov.nih.nci.caarray.external.v1_0.vocabulary.Term;

/**
 * A TermValue is a value defined by a term within a controlled vocabulary.
 * 
 * @author dkokotov
 */
public class TermValue extends AbstractValue {
    private static final long serialVersionUID = 1L;


    private Term term;

    /**
     * @return the term
     */
    public Term getTerm() {
        return term;
    }

    /**
     * @param term the term to set
     */
    public void setTerm(Term term) {
        this.term = term;
    }
}
