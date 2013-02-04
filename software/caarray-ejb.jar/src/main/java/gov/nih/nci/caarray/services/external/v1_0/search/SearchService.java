//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package gov.nih.nci.caarray.services.external.v1_0.search;

import gov.nih.nci.caarray.external.v1_0.AbstractCaArrayEntity;
import gov.nih.nci.caarray.external.v1_0.CaArrayEntityReference;
import gov.nih.nci.caarray.external.v1_0.data.File;
import gov.nih.nci.caarray.external.v1_0.data.QuantitationType;
import gov.nih.nci.caarray.external.v1_0.experiment.Experiment;
import gov.nih.nci.caarray.external.v1_0.experiment.Person;
import gov.nih.nci.caarray.external.v1_0.query.AnnotationSetRequest;
import gov.nih.nci.caarray.external.v1_0.query.BiomaterialKeywordSearchCriteria;
import gov.nih.nci.caarray.external.v1_0.query.BiomaterialSearchCriteria;
import gov.nih.nci.caarray.external.v1_0.query.ExampleSearchCriteria;
import gov.nih.nci.caarray.external.v1_0.query.ExperimentSearchCriteria;
import gov.nih.nci.caarray.external.v1_0.query.FileSearchCriteria;
import gov.nih.nci.caarray.external.v1_0.query.HybridizationSearchCriteria;
import gov.nih.nci.caarray.external.v1_0.query.KeywordSearchCriteria;
import gov.nih.nci.caarray.external.v1_0.query.LimitOffset;
import gov.nih.nci.caarray.external.v1_0.query.QuantitationTypeSearchCriteria;
import gov.nih.nci.caarray.external.v1_0.query.SearchResult;
import gov.nih.nci.caarray.external.v1_0.sample.AnnotationSet;
import gov.nih.nci.caarray.external.v1_0.sample.Biomaterial;
import gov.nih.nci.caarray.external.v1_0.sample.Hybridization;
import gov.nih.nci.caarray.external.v1_0.vocabulary.Category;
import gov.nih.nci.caarray.external.v1_0.vocabulary.Term;
import gov.nih.nci.caarray.services.external.v1_0.InvalidInputException;
import gov.nih.nci.caarray.services.external.v1_0.InvalidReferenceException;
import gov.nih.nci.caarray.services.external.v1_0.UnsupportedCategoryException;

import java.util.List;

import javax.ejb.Remote;

/**
 * Remote service for search and data enumeration. Used by the grid service, and can also be used directly by EJB
 * clients.
 * 
 * Several methods in this service accept a LimitOffset parameter to allow the client to request a subset of the results
 * that would otherwise be matched by the provided criteria. For these methods, there may also be a maximum number of
 * results that the system is willing to return for that query, regardless of the limit requested by the client. This
 * maximum is not specified in the method definition (and varies between the methods), but will be be indicated in the
 * return value. The actual number of results returned for these methods will then be the smaller of { maximum system
 * threshold, limit requested by client in the LimitOffset parameter, actual number of results available (taking into
 * account the offset specified)
 * 
 * @author dkokotov
 */
@Remote
public interface SearchService {
    /**
     * The JNDI name to look up this Remote EJB under.
     */
    String JNDI_NAME = "caarray/external/v1_0/SearchServiceBean";

    /**
     * Retrieve list of Person entities that are Principal Investigators on at least one experiment in the system. A
     * Person is considered a Principal Investigator if he/she is an experiment contact on an experiment with a set of
     * roles that includes the "investigator" term from the MGED ontology.
     * 
     * @return the list of Person entities that are principal investigators on at least one experiment in the system.
     */
    List<Person> getAllPrincipalInvestigators();

    /**
     * Retrieve the list of all categories of characteristics, either in the entire system, or for given experiment.
     * This list always includes the following "standard" categories:
     * <ul>
     * <li>MGED Ontology : OrganismPart
     * <li>MGED Ontology : DiseaseState
     * <li>MGED Ontology : MaterialType
     * <li>MGED Ontology : CellType
     * <li>MGED Ontology : LabelCompound
     * <li>caArray Ontology : ExternalId
     * </ul>
     * In addition if an experiment specified, then it includes all categories from any characteristics belonging to any
     * of the biomaterials in that experiment. If an experiment is not specified, then it includes all categories from
     * any characteristics belonging to any of the biomaterials in the entire system.
     * 
     * @param experimentRef if not null, then only categories of characteristics of biomaterials in the given experiment
     *            are returned, otherwise categories of all characteristics in the system are returned.
     * @return the list of Category entities as described above.
     * @throws InvalidReferenceException if the given reference does not identify an existing experiment in the system.
     */
    List<Category> getAllCharacteristicCategories(CaArrayEntityReference experimentRef)
            throws InvalidReferenceException;

    /**
     * Retrieve the list of all terms belonging to given category in the system.
     * 
     * @param categoryRef reference identifying the category
     * @param valuePrefix if not null, only include terms whose value starts with given prefix, using case insensitive
     *            matching
     * @return the terms in the given category, possibly filtered for the given prefix
     * @throws InvalidReferenceException if the given reference does not identify an existing category in the system.
     */
    List<Term> getTermsForCategory(CaArrayEntityReference categoryRef, String valuePrefix)
            throws InvalidReferenceException;

    /**
     * Search for experiments satisfying the given search criteria.
     * 
     * @param criteria the search criteria.
     * @param limitOffset an optional parameter specifying the number of results to return, and the offset of the first
     *            result to return within the overall result set. May be left null to indicate the entire result set is
     *            requested.
     * @return a SearchResult with the matching experiments and metadata on the subset of matching results actually
     *         returned. This may be smaller than the requested number of results - see the class level Javadoc for
     *         details.
     * @throws InvalidReferenceException if any references within the given criteria are not valid, e.g. refer to
     *             entities that do not exist or are not of the correct types
     * @throws UnsupportedCategoryException if the search criteria includes an annotation criterion with a category
     *             other that disease state, cell type, material type, tissue site.
     */
    SearchResult<Experiment> searchForExperiments(ExperimentSearchCriteria criteria, LimitOffset limitOffset)
            throws InvalidReferenceException, UnsupportedCategoryException;

    /**
     * Search for experiments matching the given keyword keyword criteria. The following fields are used to match the
     * keyword
     * 
     * <ul>
     * <li>Experiment title
     * <li>Experiment description
     * <li>Experiment public identifier
     * <li>Experiment array provider name
     * <li>Experiment array designs' names
     * <li>Experiment organism's common and scientific names
     * <li>Experiment samples' names
     * <li>Experiment sources' disease state's values
     * </ul>
     * 
     * @param criteria the keyword criteria to search for.
     * @param limitOffset an optional parameter specifying the number of results to return, and the offset of the first
     *            result to return within the overall result set. May be left null to indicate the entire result set is
     *            requested.
     * @return a SearchResult with the matching experiments and metadata on the subset of matching results actually
     *         returned. This may be smaller than the requested number of results - see the class level Javadoc for
     *         details.
     */
    SearchResult<Experiment> searchForExperimentsByKeyword(KeywordSearchCriteria criteria, LimitOffset limitOffset);

    /**
     * Search for biomaterials satisfying the given search criteria.
     * 
     * @param criteria the search criteria
     * @param limitOffset an optional parameter specifying the number of results to return, and the offset of the first
     *            result to return within the overall result set. May be left null to indicate the entire result set is
     *            requested.
     * @return a SearchResult with the matching biomaterials and metadata on the subset of matching results actually
     *         returned. This may be smaller than the requested number of results - see the class level Javadoc for
     *         details.
     * @throws InvalidReferenceException if any references within the given criteria are not valid, e.g. refer to
     *             entities that do not exist or are not of the correct types
     * @throws UnsupportedCategoryException if the search criteria includes an annotation criterion with a category
     *             other that disease state, cell type, material type, tissue site.
     */
    SearchResult<Biomaterial> searchForBiomaterials(BiomaterialSearchCriteria criteria, LimitOffset limitOffset)
            throws InvalidReferenceException, UnsupportedCategoryException;

    /**
     * Search for biomaterials matching the given keyword criteria. The following fields are used to match the keyword
     * 
     * <ul>
     * <li>Biomaterial name
     * <li>Biomaterial external id
     * <li>Biomaterial disease state's value
     * <li>Biomaterial tissue site's value
     * <li>Biomaterial organism's common and scientific names
     * <li>Experiment organism's common and scientific names (if biomaterial organism is not set)
     * </ul>
     * 
     * @param criteria the keyword criteria to search for; this identifies the string to look for, and the types of
     *            biomaterials to include in the results.
     * @param limitOffset an optional parameter specifying the number of results to return, and the offset of the first
     *            result to return within the overall result set. May be left null to indicate the entire result set is
     *            requested.
     * @return a SearchResult with the matching biomaterials and metadata on the subset of matching results actually
     *         returned. This may be smaller than the requested number of results - see the class level Javadoc for
     *         details.
     */
    SearchResult<Biomaterial> searchForBiomaterialsByKeyword(BiomaterialKeywordSearchCriteria criteria,
            LimitOffset limitOffset);

    /**
     * Search for hybridizations satisfying the given search criteria.
     * 
     * @param criteria the search criteria
     * @param limitOffset an optional parameter specifying the number of results to return, and the offset of the first
     *            result to return within the overall result set. May be left null to indicate the entire result set is
     *            requested.
     * @return a SearchResult with the matching hybridizations and metadata on the subset of matching results actually
     *         returned. This may be smaller than the requested number of results - see the class level Javadoc for
     *         details.
     * @throws InvalidReferenceException if any references within the given criteria are not valid, e.g. refer to
     *             entities that do not exist or are not of the correct types
     */
    SearchResult<Hybridization> searchForHybridizations(HybridizationSearchCriteria criteria, LimitOffset limitOffset)
            throws InvalidReferenceException;

    /**
     * Search for files satisfying the given search criteria. Note that the File instances returned by this search only
     * contain file metadata; to retrieve the actual file contents, use the file retrieval methods in DataService.
     * 
     * @param criteria the search criteria.
     * @param limitOffset an optional parameter specifying the number of results to return, and the offset of the first
     *            result to return within the overall result set. May be left null to indicate the entire result set is
     *            requested.
     * @return a SearchResult with the matching hybridizations and metadata on the subset of matching results actually
     *         returned. This may be smaller than the requested number of results - see the class level Javadoc for
     *         details.
     * @throws InvalidReferenceException if any references within the given criteria are not valid, e.g. refer to
     *             entities that do not exist or are not of the correct types
     * @see DataService
     */
    SearchResult<File> searchForFiles(FileSearchCriteria criteria, LimitOffset limitOffset)
            throws InvalidReferenceException;

    /**
     * Returns a list of quantitation types satisfying the given search criteria.
     * 
     * @param criteria the search criteria. The criteria must, at a minimum, include a reference to a Hybridization.
     * @return the list of QuantitationType matching criteria.
     * @throws InvalidReferenceException if any references within the given criteria are not valid, e.g. refer to
     *             entities that do not exist or are not of the correct types
     * @throws InvalidInputException if the search criteria does not have a non-null Hybridization reference
     */
    List<QuantitationType> searchForQuantitationTypes(QuantitationTypeSearchCriteria criteria)
            throws InvalidReferenceException, InvalidInputException;

    /**
     * Search for entities based on a specified example. Searches by example use the root example entities, as well as
     * its directly associated entities, to construct the query. Entities with association chains of more than 1 link to
     * the root example entities are ignored. The ExampleSearchCriteria class also allows the caller to specify how to
     * treat empty and zero-valued properties, and how string comparisons should be done.
     * 
     * @param <T> type of the example entity
     * @param criteria the criteria specifying the example entity, as well as rules defining how candidate entities are
     *            matched against the example
     * @param limitOffset an optional parameter specifying the number of results to return, and the offset of the first
     *            result to return within the overall result set. May be left null to indicate the entire result set is
     *            requested.
     * @return a SearchResult with the matching entities and metadata on the subset of matching results actually
     *         returned. This may be smaller than the requested number of results - see the class level Javadoc for
     *         details.
     * @throws InvalidInputException if a null example is given
     */
    <T extends AbstractCaArrayEntity> SearchResult<T> searchByExample(ExampleSearchCriteria<T> criteria,
            LimitOffset limitOffset) throws InvalidInputException;

    /**
     * Returns an annotation set matching the given request. This annotation set consists of the values of
     * Characteristics with categories specified in the request across the experiment nodes (biomaterials and/or
     * hybridizations) specified in the request.
     * <p>
     * 
     * The annotation set will include an AnnotationColumn for each ExperimentGraphNode included in the request; each
     * AnnotationColumn will include an AnnotationValueSet for each Category included in the request. The
     * AnnotationValueSet for a given experiment node and characteristic category is calculated as follows:
     * 
     * <ul>
     * <li>If the node has characteristics with the category directly, then the returned set consists of the values of
     * all such characteristics</li>
     * <li>Otherwise, the returned set is given by the applying this algorithm recursively to the direct predecessors of
     * this node in the chain, and union-ing the resulting values.
     * </ul>
     * 
     * @param request the annotation set request
     * @return the annotation set.
     * @throws InvalidReferenceException if any references within the given criteria are not valid, e.g. refer to
     *             entities that do not exist or are not of the correct types
     */
    AnnotationSet getAnnotationSet(AnnotationSetRequest request) throws InvalidReferenceException;
}
