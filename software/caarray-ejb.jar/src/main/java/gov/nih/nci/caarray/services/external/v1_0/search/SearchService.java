/**
 * The software subject to this notice and license includes both human readable
 * source code form and machine readable, binary, object code form. The caarray-ejb-jar
 * Software was developed in conjunction with the National Cancer Institute 
 * (NCI) by NCI employees and 5AM Solutions, Inc. (5AM). To the extent 
 * government employees are authors, any rights in such works shall be subject 
 * to Title 17 of the United States Code, section 105. 
 *
 * This caarray-ejb-jar Software License (the License) is between NCI and You. You (or 
 * Your) shall mean a person or an entity, and all other entities that control, 
 * are controlled by, or are under common control with the entity. Control for 
 * purposes of this definition means (i) the direct or indirect power to cause 
 * the direction or management of such entity, whether by contract or otherwise,
 * or (ii) ownership of fifty percent (50%) or more of the outstanding shares, 
 * or (iii) beneficial ownership of such entity. 
 *
 * This License is granted provided that You agree to the conditions described 
 * below. NCI grants You a non-exclusive, worldwide, perpetual, fully-paid-up, 
 * no-charge, irrevocable, transferable and royalty-free right and license in 
 * its rights in the caarray-ejb-jar Software to (i) use, install, access, operate, 
 * execute, copy, modify, translate, market, publicly display, publicly perform,
 * and prepare derivative works of the caarray-ejb-jar Software; (ii) distribute and 
 * have distributed to and by third parties the caarray-ejb-jar Software and any 
 * modifications and derivative works thereof; and (iii) sublicense the 
 * foregoing rights set out in (i) and (ii) to third parties, including the 
 * right to license such rights to further third parties. For sake of clarity, 
 * and not by way of limitation, NCI shall have no right of accounting or right 
 * of payment from You or Your sub-licensees for the rights granted under this 
 * License. This License is granted at no charge to You.
 *
 * Your redistributions of the source code for the Software must retain the 
 * above copyright notice, this list of conditions and the disclaimer and 
 * limitation of liability of Article 6, below. Your redistributions in object 
 * code form must reproduce the above copyright notice, this list of conditions 
 * and the disclaimer of Article 6 in the documentation and/or other materials 
 * provided with the distribution, if any. 
 *
 * Your end-user documentation included with the redistribution, if any, must 
 * include the following acknowledgment: This product includes software 
 * developed by 5AM and the National Cancer Institute. If You do not include 
 * such end-user documentation, You shall include this acknowledgment in the 
 * Software itself, wherever such third-party acknowledgments normally appear.
 *
 * You may not use the names "The National Cancer Institute", "NCI", or "5AM" 
 * to endorse or promote products derived from this Software. This License does 
 * not authorize You to use any trademarks, service marks, trade names, logos or
 * product names of either NCI or 5AM, except as required to comply with the 
 * terms of this License. 
 *
 * For sake of clarity, and not by way of limitation, You may incorporate this 
 * Software into Your proprietary programs and into any third party proprietary 
 * programs. However, if You incorporate the Software into third party 
 * proprietary programs, You agree that You are solely responsible for obtaining
 * any permission from such third parties required to incorporate the Software 
 * into such third party proprietary programs and for informing Your 
 * sub-licensees, including without limitation Your end-users, of their 
 * obligation to secure any required permissions from such third parties before 
 * incorporating the Software into such third party proprietary software 
 * programs. In the event that You fail to obtain such permissions, You agree 
 * to indemnify NCI for any claims against NCI by such third parties, except to 
 * the extent prohibited by law, resulting from Your failure to obtain such 
 * permissions. 
 *
 * For sake of clarity, and not by way of limitation, You may add Your own 
 * copyright statement to Your modifications and to the derivative works, and 
 * You may provide additional or different license terms and conditions in Your 
 * sublicenses of modifications of the Software, or any derivative works of the 
 * Software as a whole, provided Your use, reproduction, and distribution of the
 * Work otherwise complies with the conditions stated in this License.
 *
 * THIS SOFTWARE IS PROVIDED "AS IS," AND ANY EXPRESSED OR IMPLIED WARRANTIES, 
 * (INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY, 
 * NON-INFRINGEMENT AND FITNESS FOR A PARTICULAR PURPOSE) ARE DISCLAIMED. IN NO 
 * EVENT SHALL THE NATIONAL CANCER INSTITUTE, 5AM SOLUTIONS, INC. OR THEIR 
 * AFFILIATES BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, 
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, 
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; 
 * OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, 
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR 
 * OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF 
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package gov.nih.nci.caarray.services.external.v1_0.search;

import gov.nih.nci.caarray.external.v1_0.AbstractCaArrayEntity;
import gov.nih.nci.caarray.external.v1_0.CaArrayEntityReference;
import gov.nih.nci.caarray.external.v1_0.data.DataFile;
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
import gov.nih.nci.caarray.external.v1_0.query.PagingParams;
import gov.nih.nci.caarray.external.v1_0.query.QuantitationTypeSearchCriteria;
import gov.nih.nci.caarray.external.v1_0.query.SearchResult;
import gov.nih.nci.caarray.external.v1_0.sample.AnnotationSet;
import gov.nih.nci.caarray.external.v1_0.sample.Biomaterial;
import gov.nih.nci.caarray.external.v1_0.sample.Hybridization;
import gov.nih.nci.caarray.external.v1_0.vocabulary.Category;
import gov.nih.nci.caarray.external.v1_0.vocabulary.Term;
import gov.nih.nci.caarray.services.external.v1_0.InvalidReferenceException;
import gov.nih.nci.caarray.services.external.v1_0.NoEntityMatchingReferenceException;

import java.util.List;

import javax.ejb.Remote;

/**
 * Remote service for search and data enumeration. Used by the grid service, and can also be used directly by EJB
 * clients.
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
     * Retrieve list of Principal Inestigators in the system.
     * 
     * @return the list of Person entities that are principal investigators on at least one experiment in the system.
     */
    List<Person> getAllPrincipalInvestigators();

    /**
     * Retrieve the list of all categories of characteristics, either in the entire system, or for given experiment.
     * 
     * @param experimentRef if not null, then only categories of characteristics of biomaterials in the
     * given experiment are returned, otherwise categories of all characteristivcs in the system are returned.
     * @return the list of Category entities as described above.
     * @throws InvalidReferenceException if there is no experiment with given reference
     */
    List<Category> getAllCharacteristicCategories(CaArrayEntityReference experimentRef)
            throws InvalidReferenceException;

    /**
     * Retrieve the list of all terms belonging to given category in the system.
     * 
     * @param categoryRef reference identifying the category
     * @param valuePrefix if not null, only include terms whose value starts with given prefix, using case insensitive
     *            matching
     * @return the terms in the given category
     * @throws InvalidReferenceException if there is no category with given reference
     */
    List<Term> getTermsForCategory(CaArrayEntityReference categoryRef, String valuePrefix)
            throws InvalidReferenceException;

    /**
     * Search for biomaterials satisfying the given search criteria.
     * 
     * @param criteria the search criteria
     * @param pagingParams paging parameters
     * @return the subset of the biomaterials matching the given criteria, subject to the paging params.
     * @throws InvalidReferenceException if there is no experiment with given reference
     */
    SearchResult<Biomaterial> searchForBiomaterials(BiomaterialSearchCriteria criteria, PagingParams pagingParams)
            throws InvalidReferenceException;

    /**
     * Search for hybridizations satisfying the given search criteria.
     * 
     * @param criteria the search criteria
     * @param pagingParams paging parameters
     * @return the subset of the hybridizations matching the given criteria, subject to the paging params.
     * @throws InvalidReferenceException if there is no experiment with given reference
     */
    SearchResult<Hybridization> searchForHybridizations(HybridizationSearchCriteria criteria, PagingParams pagingParams)
            throws InvalidReferenceException;

    /**
     * Retrieves the list of entities instance of an entity identified by the given reference.
     * 
     * @param references a list of references identifying the entity to retrieve.
     * @return the instances identified by the references. this list will have the same length as the references list,
     *         and the i-th element will be the entity for the i-th reference.
     * @throws NoEntityMatchingReferenceException if there is no entity matching any reference in the list
     */
    List<AbstractCaArrayEntity> getByReferences(List<CaArrayEntityReference> references)
            throws NoEntityMatchingReferenceException;

    /**
     * Retrieves an entity identified by the given reference.
     * 
     * @param reference a reference identifying the entity to retrieve.
     * @return the entity identified by the reference.
     * @throws NoEntityMatchingReferenceException if there is no entity with given reference.
     */
    AbstractCaArrayEntity getByReference(CaArrayEntityReference reference) throws NoEntityMatchingReferenceException;

    /**
     * Returns a list of experiments satisfying the given search criteria.
     * 
     * @param criteria the search criteria.
     * @param pagingParams paging params.
     * @return the list of experiments matching criteria, subject to the paging specifications.
     * @throws InvalidReferenceException if the search criteria includes any invalid references.
     */
    SearchResult<Experiment> searchForExperiments(ExperimentSearchCriteria criteria, PagingParams pagingParams)
            throws InvalidReferenceException;

    /**
     * Returns a list of experiments matching the given keyword.
     * 
     * @param criteria the keyword criteria to search for.
     * @param pagingParams paging params.
     * @return the list of experiments matching criteria, subject to the paging specifications.
     */
    SearchResult<Experiment> searchForExperimentsByKeyword(KeywordSearchCriteria criteria, PagingParams pagingParams);

    /**
     * Returns a list of data files satisfying the given search criteria.
     * 
     * @param criteria the search criteria.
     * @param pagingParams paging params.
     * @return the list of files matching criteria, subject to the paging specifications.
     * @throws InvalidReferenceException if the search criteria includes any invalid references.
     */
    SearchResult<DataFile> searchForFiles(FileSearchCriteria criteria, PagingParams pagingParams)
            throws InvalidReferenceException;
    
    /**
     * Returns a list of biomaterials matching the given keyword. 
     * 
     * @param criteria the keyword criteria to search for.
     * @param pagingParams paging params.
     * @return the list of biomaterials matching the criteria, subject to the paging specifications.
     */
    SearchResult<Biomaterial> searchForBiomaterialsByKeyword(BiomaterialKeywordSearchCriteria criteria,
            PagingParams pagingParams);

    /**
     * Returns a list of quantitation types satisfying the given search criteria.
     * 
     * @param criteria the search criteria.
     * @return the list of quantitation types matching criteria.
     * @throws InvalidReferenceException if the search criteria includes any invalid references.
     */
    List<QuantitationType> searchForQuantitationTypes(QuantitationTypeSearchCriteria criteria)
            throws InvalidReferenceException;
    
    /**
     * Do a query by example.
     * @param <T> type of the example entity
     * @param criteria the example entity to query for
     * @param pagingParams paging params.
     * @return list of entities matching example, subject to paging params
     */
    <T extends AbstractCaArrayEntity> SearchResult<T> searchByExample(ExampleSearchCriteria<T> criteria,
            PagingParams pagingParams);
    
    /**
     * Returns an annotation set matching the given request.
     * 
     * @param request the annotation set request
     * @return the annotation set.
     * @throws InvalidReferenceException if there are any invalid references in the request
     */
    AnnotationSet getAnnotationSet(AnnotationSetRequest request) throws InvalidReferenceException;
}
