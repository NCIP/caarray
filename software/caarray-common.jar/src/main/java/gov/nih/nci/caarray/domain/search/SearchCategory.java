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
package gov.nih.nci.caarray.domain.search;

import gov.nih.nci.caarray.domain.ResourceBasedEnum;
import gov.nih.nci.caarray.domain.project.Experiment;

import org.apache.commons.lang.ArrayUtils;

/**
 * @author Winston Cheng
 */
@SuppressWarnings("PMD.AvoidDuplicateLiterals")
public enum SearchCategory implements ResourceBasedEnum {
    /**
     * Experiment title.
     */
    // NOTE: Experiment title is explicitly set as the first enum here so it shows up first in the UI
    EXPERIMENT_TITLE("search.category.experimentTitle", new String[] {"p.experiment e"}, "e.title"),

    /**
     * Experiment ID.
     */
    EXPERIMENT_ID("search.category.experimentId", new String[] {"p.experiment e"}, "e.publicIdentifier"),

    /**
     * Experiment title.
     */
    EXPERIMENT_DESCRIPTION("search.category.experimentDescription", new String[] {"p.experiment e"}, "e.description"),

    /**
     * Array provider.
     */
    ARRAY_PROVIDER("search.category.arrayProvider", new String[] {"p.experiment e", "e.manufacturer m"}, "m.name"),

    /**
     * Array design.
     */
    ARRAY_DESIGN("search.category.arrayDesign", new String[] {"p.experiment e", "e.arrayDesigns a"}, "a.name"),

    /**
     * Organism.
     */
    ORGANISM("search.category.organism", new String[] {"p.experiment e"}, new String[] {"e.organism.commonName",
            "e.organism.scientificName"}),
    /**
     * Sample.
     */
    SAMPLE("search.category.sample", new String[] {"p.experiment e", "e.samples s"}, "s.name"),
    /**
     * Pubmed ID.
     */
    PUBMED_ID("search.category.pubMedId", new String[] {"p.experiment e", "e.publications pub"}, "pub.pubMedId"),
    /**
     * Publication Author.
     */
    PUBLICATION_AUTHOR("search.category.publicationAuthor", new String[] {"p.experiment e", "e.publications pub"},
            "pub.authors"),
    /**
     * Disease state.
     */
    DISEASE_STATE("search.category.diseaseState", new String[] {"p.experiment e"}, ArrayUtils.EMPTY_STRING_ARRAY) {
        /**
         * {@inheritDoc}
         */
        public String getWhereClause() {
            // the subselect is faster when searching by all categories because otherwise we get a huge number of rows
            // with all the joins due to multiple many-to-many associations
            // it would have been desirable to take this approach for the SAMPLE search category as well, but we cannot
            // because of HHH-530, which would cause the security filters to not be applied
            return "e.id in (select exp.id from " + Experiment.class.getName() + " exp left join exp.sources src "
                    + "left join src.diseaseState ds where ds.value like :keyword)";
       }
   };

    private final String resourceKey;
    private final String[] joins;
    private final String[] searchFields;

    @SuppressWarnings("PMD.ArrayIsStoredDirectly")
    SearchCategory(String resourceKey, String[] joins, String... searchFields) {
        this.resourceKey = resourceKey;
        this.joins = joins;
        this.searchFields = searchFields;
   }

    /**
     * @return the resource key that should be used to retrieve a label for this SearchCategory in the UI
     */
    public String getResourceKey() {
        return this.resourceKey;
   }

    /**
     * These are the fields to join against in the HQL query. Is null if no join is necessary.
     *
     * @return the fields to join against
     */
    @SuppressWarnings("PMD.MethodReturnsInternalArray")
    public String[] getJoins() {
        return this.joins;
   }

    /**
     * @return the where subclause for this search category. this method assumes that the subclause will be wrapped in
     *         parenthesis before being added to the overall where clause of a query.
     */
    public String getWhereClause() {
        StringBuilder sb = new StringBuilder();
        int j = 0;
        for (String field : this.searchFields) {
            if (j++ > 0) {
                sb.append(" OR ");
           }
            sb.append(field).append(" LIKE :keyword");
       }
        return sb.toString();
   }
}
