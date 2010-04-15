/**
 * The software subject to this notice and license includes both human readable
 * source code form and machine readable, binary, object code form. The caArray
 * Software was developed in conjunction with the National Cancer Institute
 * (NCI) by NCI employees and 5AM Solutions, Inc. (5AM). To the extent
 * government employees are authors, any rights in such works shall be subject
 * to Title 17 of the United States Code, section 105.
 *
 * This caArray Software License (the License) is between NCI and You. You (or
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
 * its rights in the caArray Software to (i) use, install, access, operate,
 * execute, copy, modify, translate, market, publicly display, publicly perform,
 * and prepare derivative works of the caArray Software; (ii) distribute and
 * have distributed to and by third parties the caArray Software and any
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

import edu.georgetown.pir.Organism;
import gov.nih.nci.caarray.domain.project.Experiment;
import gov.nih.nci.caarray.domain.sample.AbstractCharacteristic;
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
     * Searches for categories of characteristics in the system whose names match the given keyword.
     *
     * @param experiment if not null, restrict the search to characteristics of biomaterials in the given 
     * experiment
     * @param characteristicClass only return categories of characteristics of this type
     * @param keyword if not null, then categories whose names start with this string are included in the results.
     * @return a list of matching categories
     */
    List<Category> searchForCharacteristicCategory(Experiment experiment,
            Class<? extends AbstractCharacteristic> characteristicClass, String keyword);
}
