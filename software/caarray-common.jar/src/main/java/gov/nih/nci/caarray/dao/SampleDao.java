/**
 * The software subject to this notice and license includes both human readable
 * source code form and machine readable, binary, object code form. The caarray-common-jar
 * Software was developed in conjunction with the National Cancer Institute
 * (NCI) by NCI employees and 5AM Solutions, Inc. (5AM). To the extent
 * government employees are authors, any rights in such works shall be subject
 * to Title 17 of the United States Code, section 105.
 *
 * This caarray-common-jar Software License (the License) is between NCI and You. You (or
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
 * its rights in the caarray-common-jar Software to (i) use, install, access, operate,
 * execute, copy, modify, translate, market, publicly display, publicly perform,
 * and prepare derivative works of the caarray-common-jar Software; (ii) distribute and
 * have distributed to and by third parties the caarray-common-jar Software and any
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
package gov.nih.nci.caarray.dao;

import gov.nih.nci.caarray.domain.project.Experiment;
import gov.nih.nci.caarray.domain.sample.AbstractBioMaterial;
import gov.nih.nci.caarray.domain.sample.Sample;
import gov.nih.nci.caarray.domain.sample.Source;
import gov.nih.nci.caarray.domain.search.BiomaterialSearchCategory;
import gov.nih.nci.caarray.domain.search.BiomaterialSearchCriteria;
import gov.nih.nci.caarray.domain.search.SearchSampleCategory;
import gov.nih.nci.caarray.domain.vocabulary.Category;

import java.util.List;
import java.util.Set;

import com.fiveamsolutions.nci.commons.data.search.PageSortParams;


/**
 * DAO for entities in the <code>gov.nih.nci.caarray.domain.sample</code> package.
 *
 * @author mshestopalov
 *
 */
public interface SampleDao extends CaArrayDao {

    /**
     * Gets the count of search results matching the given keyword.
     * @param keyword keyword to search for
     * @param categories categories to search
     * @param biomaterialClass the AbstractBioMaterial subclass whose instances to search
     * @return number of results
     */
    int searchCount(String keyword, Class<? extends AbstractBioMaterial> biomaterialClass,
            BiomaterialSearchCategory... categories);

    /**
     * Performs a query for all samples which contain a characteristic and category supplied.
     * @param params sort params
     * @param c category
     * @param keyword text keyword
     * @return a list if samples with characteristic matching keyword and category matching c
     */
    List<Sample> searchSamplesByCharacteristicCategory(PageSortParams<Sample> params, Category c, String keyword);

    /**
     * Performs a query for all samples which are part of an experiment and
     * contain a category keyword. The keyword is matched like %keyword%
     * @param c category
     * @param e experiment
     * @param keyword text keyword
     * @return a list if samples with characteristic matching keyword and category matching c
     */
    List<Sample> searchSamplesByExperimentAndCategory(String keyword, Experiment e, SearchSampleCategory... c);

    /**
     * Get number of results from query for all samples which contain a characteristic and category supplied.
     * @param c category
     * @param keyword text keyword
     * @return int count
     */
    int countSamplesByCharacteristicCategory(Category c, String keyword);

    /**
     * Performs a query for biomaterials by text matching for the given keyword. This query method supports
     * searching a single type of biomaterial.
     *
     * Note that this method currently only supports SortCriterions that are either simple properties of the target
     * class or required single-valued associations from it. If a non-required association is used in the sort
     * criterion, then any instances for which that association is null will not be included in the results (as an inner
     * join is used)
     * @param <T> child of AbstractBioMaterial
     * @param params paging and sorting parameters
     * @param keyword text to search for
     * @param biomaterialClass the AbstractBioMaterial subclass whose instances to search
     * @param categories Indicates which categories to search. Passing null will search all categories.
     * @return a list of matching biomaterials of type biomaterialSubclass
     */
    <T extends AbstractBioMaterial> List<T> searchByCategory(PageSortParams<T> params, String keyword,
            Class<T> biomaterialClass, BiomaterialSearchCategory... categories);

    /**
     * Performs a query for biomaterials by text matching for the given keyword. This query method supports searching
     * across multiple types of biomaterials.
     * 
     * Note that this method currently only supports SortCriterions that are either simple properties of the target
     * class or required single-valued associations from it. If a non-required association is used in the sort
     * criterion, then any instances for which that association is null will not be included in the results (as an inner
     * join is used)
     * 
     * @param params paging and sorting parameters
     * @param keyword text to search for
     * @param biomaterialClasses the AbstractBioMaterial subclasses to include in the search. If this is an empty, then
     *            this method will return an empty list.
     * @param categories Indicates which categories to search. Passing null will search all categories.
     * @return a list of matching biomaterials
     */
    List<AbstractBioMaterial> searchByCategory(PageSortParams<AbstractBioMaterial> params, String keyword,
            Set<Class<? extends AbstractBioMaterial>> biomaterialClasses, BiomaterialSearchCategory... categories);

    /**
     * Performs a query for all sources which contain a characteristic and category supplied.
     * @param params sort params
     * @param c category
     * @param keyword text keyword
     * @return a list if samples with characteristic matching keyword and category matching c
     */
    List<Source> searchSourcesByCharacteristicCategory(PageSortParams<Source> params, Category c, String keyword);

    /**
     * Get number of results from query for all sources which contain a characteristic and category supplied.
     * @param c category
     * @param keyword text keyword
     * @return int count
     */
    int countSourcesByCharacteristicCategory(Category c, String keyword);
    
    /**
     * Performs a query for biomaterials based on given criteria.
     * 
     * @param params paging and sorting parameters
     * @param criteria the criteria for the search
     * @param <T> the type of biomaterials to search for
     * @return a list of matching biomaterials of the given type.
     */
    <T extends AbstractBioMaterial> List<T> searchByCriteria(PageSortParams<T> params,
            BiomaterialSearchCriteria criteria);
}
