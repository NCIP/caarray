//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.application.vocabulary;

import edu.georgetown.pir.Organism;
import gov.nih.nci.caarray.domain.protocol.Protocol;
import gov.nih.nci.caarray.domain.sample.AbstractCharacteristic;
import gov.nih.nci.caarray.domain.vocabulary.Category;
import gov.nih.nci.caarray.domain.vocabulary.Term;
import gov.nih.nci.caarray.domain.vocabulary.TermSource;

import java.util.List;
import java.util.Set;

/**
 * Interface to the controlled vocabulary lookup service.
 */
public interface VocabularyService {
    /**
     * The default JNDI name to use to lookup <code>VocabularyService</code>.
     */
    String JNDI_NAME = "caarray/VocabularyServiceBean/local";

    /**
     * The name of the protocol type term for unknown protocol types.
     */
    String UNKNOWN_PROTOCOL_TYPE_NAME = "unknown_protocol_type";

    /**
     * Returns all terms that belong to the given category (including all subcategories).
     *
     * @param category the category for which to return terms
     * @return the Set of Terms belonging to the given categories or any of its subcategories
     * (where its set of subcategories is the transitive closure of the children property of Category)
     */
    Set<Term> getTerms(Category category);

    /**
     * Returns all terms that belong to the given category  (including all
     * subcategories) and whose value starts with the given value.
     *
     * @param category the category for which to return terms
     * @param value the value to search on
     * @return the Set of Terms belonging to the given categories or any of its subcategories
     * (where its set of subcategories is the transitive closure of the children property of Category),
     * and whose value starts with one of the given terms
     */
    Set<Term> getTerms(Category category, String value);

    /**
     * Returns all Organisms.
     *
     * @return the List&lt;Organism&gt; in the system
     */
    List<Organism> getOrganisms();

    /**
     * Returns the requested term source, if it exists.
     *
     * @param name name of the source
     * @param version version to retrieve. If null, then will retrieve the term source with given name and no version
     * @return the matching source, or null if no matching source exists.
     */
    TermSource getSource(String name, String version);

    /**
     * Returns the term sources with given name.
     *
     * @param name name of the sources to return
     * @return the matching sources, or empty set if no matching sources exists.
     */
    Set<TermSource> getSources(String name);

    /**
     * Returns the requested term source, if it exists.
     *
     * @param url url of the source
     * @param version version to retrieve. If null, then will retrieve the term source with given url and no version
     * @return the matching source, or null if no matching source exists.
     */
    TermSource getSourceByUrl(String url, String version);

    /**
     * Returns the term sources with given url.
     *
     * @param url url of the sources to return
     * @return the matching sources, or empty set if no matching sources exists.
     */
    Set<TermSource> getSourcesByUrl(String url);

    /**
     * Returns the category with the matching name for the given source.
     *
     * @param source the source
     * @param categoryName the name of the category
     * @return the category
     */
    Category getCategory(TermSource source, String categoryName);

    /**
     * Returns the term with the given value from the given term source if it exists, or null if it does not.
     *
     * @param source the source to which the term should belong
     * @param value value of the term (case insensitive)
     * @return the matching term or null if no term with that value exists
     */
    Term getTerm(TermSource source, String value);

    /**
     * Returns the term with the given id.
     *
     * @param id the id of the desired term
     * @return the term with given id or null if none found.
     */
    Term getTerm(Long id);

    /**
     * Get the organism with given name in the given term source.
     * @param source the source the organism must have.
     * @param scientificName the scientific name the organism must have (case insensitive)
     * @return the organism matching the above, or null if no matches
     */
    Organism getOrganism(TermSource source, String scientificName);

    /**
     * Method to save the term.
     * @param term the term
     */
    void saveTerm(Term term);

    /**
     * Retrieve all term sources.
     * @return the sources.
     */
    List<TermSource> getAllSources();

    /**
     * Get the list of protocols with the given type with names matching the given name (case-insensitive).
     * @param type the type.
     * @param name name  to match (case-insensitive)
     * @return the list of protocols.
     */
    List<Protocol> getProtocolsByProtocolType(Term type, String name);

    /**
     * Get a protocol based off of the fields in its unique constraint.
     * @param name the name of the protocol.
     * @param source the source.
     * @return the protocol, or null if none found.
     */
    Protocol getProtocol(String name, TermSource source);

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
     * Searches for categories of characteristics in the system whose names match the given keyword.
     * 
     * @param keyword if not null, then categories whose names start with this string are included in the results.
     * @param characteristicClass only return categories of characteristics of this type
     * @param <T> the characteristic type
     * @return a list of matching categories
     */
    <T extends AbstractCharacteristic> List<Category> searchForCharacteristicCategory(Class<T> characteristicClass,
            String keyword);
}
