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

import gov.nih.nci.caarray.domain.sample.AbstractBioMaterial;
import gov.nih.nci.caarray.domain.sample.Extract;
import gov.nih.nci.caarray.domain.sample.LabeledExtract;
import gov.nih.nci.caarray.domain.sample.Sample;
import gov.nih.nci.caarray.domain.sample.Source;

import java.util.EnumSet;
import java.util.Set;
import org.apache.commons.lang.ArrayUtils;

/**
 * Search criteria for the external API search for biomaterials by keyword.
 * 
 * @author dkokotov
 */
@SuppressWarnings("PMD.AvoidDuplicateLiterals")
public enum ExternalBiomaterialSearchCategory implements BiomaterialSearchCategory {
    /**
     * Name.
     */
    NAME("search.category.sample", ArrayUtils.EMPTY_STRING_ARRAY, "this.name"),

    /**
     * External Id (Samples only).
     */
    EXTERNAL_ID("search.category.externalSampleId", ArrayUtils.EMPTY_STRING_ARRAY, "this.externalSampleId"),

    /**
     * Disease State.
     */
    DISEASE_STATE("search.category.diseaseState", new String[] {"this.diseaseState sds" },
            new String[] {"sds.value" }),

    /**
     * Tissue Site.
     */
    TISSUE_SITE("search.category.tissueSite", new String[] {"this.tissueSite sts" }, new String[] {"sts.value" }),

    /**
     * Organism.
     */
    ORGANISM("search.category.organism", new String[] {"this.organism so", "this.experiment e", "e.organism eo" },
            ArrayUtils.EMPTY_STRING_ARRAY) {

        /**
         * {@inheritDoc}
         */
        public String getWhereClause() {
            return "(so IS NOT NULL AND" + " (so.commonName like :keyword OR so.scientificName like :keyword)) OR"
                    + " (eo IS NOT NULL AND" + " (eo.commonName like :keyword OR eo.scientificName like :keyword))";

        }
    },

    /**
     * Experiment title.
     */
    EXPERIMENT_TITLE("search.category.experimentTitle", new String[] {"this.experiment e" }, "e.title"),

    /**
     * Material Type.
     */
    MATERIAL_TYPE("search.category.materialType", new String[] {"this.materialType sms" },
            "sms.value"),

    /**
     * Cell Type.
     */
    CELL_TYPE("search.category.cellType", new String[] {"this.cellType scs" }, "scs.value"),
    
    /**
     * Provider (Sources only).
     */
   PROVIDER("search.category.sourceProvider", new String[] {"this.providers sps" }, "sps.name");

    /**
     * Categories applicable for sources.
     */
    public static final Set<ExternalBiomaterialSearchCategory> SOURCE_CATEGORIES = EnumSet.complementOf(EnumSet
            .of(EXTERNAL_ID));
    /**
     * Categories applicable for samples.
     */
    public static final Set<ExternalBiomaterialSearchCategory> SAMPLE_CATEGORIES = EnumSet.complementOf(EnumSet
            .of(PROVIDER));
    /**
     * Categories applicable for extracts.
     */
    public static final Set<ExternalBiomaterialSearchCategory> EXTRACT_CATEGORIES = EnumSet.complementOf(EnumSet
            .of(EXTERNAL_ID, PROVIDER));
    /**
     * Categories applicable for labeled extracts.
     */
    public static final Set<ExternalBiomaterialSearchCategory> LABELED_EXTRACT_CATEGORIES = EnumSet
            .complementOf(EnumSet.of(EXTERNAL_ID, PROVIDER));

    private final String resourceKey;
    private final String[] joins;
    private final String[] searchFields;

    @SuppressWarnings("PMD.ArrayIsStoredDirectly")
    ExternalBiomaterialSearchCategory(String resourceKey, String[] joins, String... searchFields) {
        this.resourceKey = resourceKey;
        this.joins = joins;
        this.searchFields = searchFields;
    }

    /**
     * {@inheritDoc}
     */
    public String getResourceKey() {
        return this.resourceKey;
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("PMD.MethodReturnsInternalArray")
    public String[] getJoins() {
        return this.joins;
    }

    /**
     * {@inheritDoc}
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

    /**
     * returns the categories applicable for given biomaterial class.
     * 
     * @param bmClass the biomaterial class
     * @return the applicable categories
     */
    public static ExternalBiomaterialSearchCategory[] valuesFor(Class<? extends AbstractBioMaterial> bmClass) {
        Set<ExternalBiomaterialSearchCategory> categories = null;
        if (Source.class.equals(bmClass)) {
            categories = SOURCE_CATEGORIES;
        } else if (Sample.class.equals(bmClass)) {
            categories = SAMPLE_CATEGORIES;
        } else if (Extract.class.equals(bmClass)) {
            categories = EXTRACT_CATEGORIES;
        } else if (LabeledExtract.class.equals(bmClass)) {
            categories = LABELED_EXTRACT_CATEGORIES;
        } else {
            throw new IllegalArgumentException("Unknown biomaterial class: " + bmClass);
        }
        return categories.toArray(new ExternalBiomaterialSearchCategory[categories.size()]);
    }
}
