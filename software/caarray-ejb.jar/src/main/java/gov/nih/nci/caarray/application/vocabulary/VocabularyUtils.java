//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.application.vocabulary;

import gov.nih.nci.caarray.application.ServiceLocatorFactory;
import gov.nih.nci.caarray.domain.project.ExperimentOntology;
import gov.nih.nci.caarray.domain.project.ExperimentOntologyCategory;
import gov.nih.nci.caarray.domain.vocabulary.Category;
import gov.nih.nci.caarray.domain.vocabulary.Term;
import gov.nih.nci.caarray.domain.vocabulary.TermSource;

import java.util.Set;

/**
 * Utility class for working with the vocabulary service. Primarily has methods for working with standard term sources
 * and categories.
 * 
 * @author dkokotov
 */
public final class VocabularyUtils {

    private VocabularyUtils() {
        // utility class
    }
    
    /**
     * Retrieve the category corresponding to the given ExperimentOntologyCategory constant.
     * @param category an ExperimentOntologyCategory constant describing a category
     * @return the category, or null if none exists
     */
    public static Category getCategory(ExperimentOntologyCategory category) {
      TermSource ts = getTermSource(category.getOntology());
      return ServiceLocatorFactory.getVocabularyService().getCategory(ts, category.getCategoryName());
    }

    /**
     * Retrieve the term source corresponding to the given ExperimentOntology constant.
     * @param ontology an ExperimentOntology constant describing a TermSource
     * @return the term source, or null if none exists
     */
    public static TermSource getTermSource(ExperimentOntology ontology) {
        return ServiceLocatorFactory.getVocabularyService()
                .getSource(ontology.getOntologyName(), ontology.getVersion());
    }

    /**
     * Retrieve the term with given value from the MGED Ontology term source.
     * @param value value of the term to retrieve
     * @return the term, or null if the term does not exist in the MGED Ontology term source
     */
    public static Term getMOTerm(String value) {
        return getTerm(ExperimentOntology.MGED_ONTOLOGY, value);
    }

    /**
     * Retrieve the term with given value from the term source corresponding to given ExperimentOntology constant.
     * @param value value of the term to retrieve
     * @param ontology an ExperimentOntology constant describing a TermSource
     * @return the term, or null if the term does not exist in the term source
     */
    public static Term getTerm(ExperimentOntology ontology, String value) {
        TermSource ts = getTermSource(ontology);
        return ServiceLocatorFactory.getVocabularyService().getTerm(ts, value);
    }

    /**
     * Retrieve the set of terms belonging to the category corresponding to the given ExperimentOntologyCategory
     * constant.
     * @param category an ExperimentOntologyCategory constant describing a category
     * @return the Set of Terms belonging to this category or its subcategories
     */
    public static Set<Term> getTermsFromCategory(ExperimentOntologyCategory category) {
      return ServiceLocatorFactory.getVocabularyService().getTerms(getCategory(category));
    }

}
