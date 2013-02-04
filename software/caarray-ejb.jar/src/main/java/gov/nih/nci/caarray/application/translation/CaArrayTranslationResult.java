//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package gov.nih.nci.caarray.application.translation;

import gov.nih.nci.caarray.domain.array.ArrayDesign;
import gov.nih.nci.caarray.domain.project.Experiment;
import gov.nih.nci.caarray.domain.vocabulary.Term;

import java.io.Serializable;
import java.util.Collection;

/**
 * Contains the result of translating other formats to the caArray domain model.
 */
public interface CaArrayTranslationResult extends Serializable {

    /**
     * Returns the <code>Term</code> objects translated from
     * another format.
     * 
     * @return the translated <code>Terms</code>
     */
    Collection<Term> getTerms();

    /**
     * Returns the <code>Investigation</code> objects (with contents) translated from
     * another format.
     * 
     * @return the translated <code>Investigations</code>
     */
    Collection<Experiment> getInvestigations();

    /**
     * Returns the <code>Investigation</code> objects (with contents) translated from
     * another format.
     * 
     * @return the translated <code>Investigations</code>
     */
    Collection<ArrayDesign> getArrayDesigns();
    
}
