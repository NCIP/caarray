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
