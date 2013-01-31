//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.dao;

import edu.georgetown.pir.Organism;
import gov.nih.nci.caarray.domain.vocabulary.Category;
import gov.nih.nci.caarray.domain.vocabulary.Term;
import gov.nih.nci.caarray.domain.vocabulary.TermSource;

import java.util.List;
import java.util.Set;

/**
 * DAO for classes in the <code>gov.nih.nci.caarray.domain.vocabulary</code> package.
 *
 * @author ETavela
 */
public interface VocabularyDao extends CaArrayDao {

    /**
     * Gets all the <code>Terms</code> belonging to the given category only (and not its subcategories).
     *
     * @param category the category for which to retrieve terms
     * @return <code>List&lt;Term></code> of all terms belonging directly to the given category, or an empty
     * <code>List</code> if no matches.
     */
    Set<Term> getTerms(Category category);

    /**
     * Gets all the <code>Terms</code> in the given category and all sub-categories (where a category's
     * subcategories are the transitive closure of its children property), optionally retrieving only
     * terms whose value starts with the given prefix.
     *
     * @param category the category for which to retrieve terms
     * @param valuePrefix if not null, only return terms whose value starts with this.
     * @return all matching terms or an empty <code>Set</code> if no matches.
     */
    Set<Term> getTermsRecursive(Category category, String valuePrefix);

    /**
     * Get the term with given value in the given term source.
     * @param source the source the term must have.
     * @param value the value the term must have (case insensitive)
     * @return the term matching the above, or null if no matches
     */
    Term getTerm(TermSource source, String value);

    /**
     * Returns the <code>Category</code> with the given name from the given term source or null if none exists.
     *
     * @param termSource source the term source to look in
     * @param name the name of the <code>Category</code> to look for
     * @return the matching <code>Category</code> or null if none exist.
     */
    Category getCategory(TermSource termSource, String name);

    /**
     * Returns the Term with given id.
     * @param id id of Term to retrieve
     * @return the Term with given id or null if none found
     */
    Term getTermById(Long id);

    /**
     * Get the organism with given name in the given term source.
     * @param source the source the organism must have.
     * @param scientificName the scientific name the organism must have (case insensitive)
     * @return the organism matching the above, or null if no matches
     */
    Organism getOrganism(TermSource source, String scientificName);

    /**
     * Given a term value and a term source, searches for a term with that value in
     * all term sources in the database which are considered
     * to be other versions of the same term source. Two TermSources are considered
     * to be versions of the same term source if they have the same name or the same url.
     * @param termSource the term source whose other versions to retrieve. If any matches exist,
     * returns the match from the term source with the latest (using alphabetical ordering) version
     * @param value value of the term to find (case insensitive)
     * @return a Term with given value from the Term Source with latest version from among all term sources
     * with the same version as the given source; null if no matching terms are found
     */
    Term findTermInAllTermSourceVersions(TermSource termSource, String value);

    /**
     * Performs a query for abstract characteristic categories by text prefix matching for the given keyword.
     *
     * @param keyword text to search for
     * @return a list of matching experiments
     */
    List<Category> searchForCharacteristicCategory(String keyword);
}
