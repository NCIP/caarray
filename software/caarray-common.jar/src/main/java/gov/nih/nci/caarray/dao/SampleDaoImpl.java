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

import gov.nih.nci.caarray.domain.sample.AbstractBioMaterial;
import gov.nih.nci.caarray.domain.sample.Sample;
import gov.nih.nci.caarray.domain.sample.Source;
import gov.nih.nci.caarray.domain.sample.TermBasedCharacteristic;
import gov.nih.nci.caarray.domain.search.BiomaterialSearchCategory;
import gov.nih.nci.caarray.domain.search.SearchSampleCategory;
import gov.nih.nci.caarray.domain.search.SearchSourceCategory;
import gov.nih.nci.caarray.domain.vocabulary.Category;
import gov.nih.nci.caarray.util.HibernateUtil;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.Query;

import com.fiveamsolutions.nci.commons.data.search.PageSortParams;
import com.fiveamsolutions.nci.commons.data.search.SortCriterion;

/**
 * DAO for entities in the <code>gov.nih.nci.caarray.domain.sample</code> package.
 * @author mshestopalov
 *
 */
@SuppressWarnings("PMD.CyclomaticComplexity")
public class SampleDaoImpl extends AbstractCaArrayDaoImpl implements SampleDao {

    private static final Logger LOG = Logger.getLogger(SampleDaoImpl.class);
    private static final String UNCHECKED = "unchecked";
    private static final String FROM_KEYWORD = " FROM ";
    private static final String SELECT_DISTINCT = " SELECT DISTINCT ";
    private static final String KEYWORD_SUB = "keyword";
    private static final String CATAGORY_SUB = "mycat";

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings(UNCHECKED)
    public <T extends AbstractBioMaterial>List<T>  searchByCategory(PageSortParams<T> params, String keyword,
            BiomaterialSearchCategory... categories) {
        Query q = getSearchQuery(false, params, keyword, categories);
        q.setFirstResult(params.getIndex());
        q.setMaxResults(params.getPageSize());
        return q.list();
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings(UNCHECKED)
    public List<Sample> searchSamplesByCharacteristicCategory(Category c, String keyword) {
        StringBuffer sb = new StringBuffer();

        sb.append(SELECT_DISTINCT + "s");
        sb.append(generateSamplesByCharacteristicCategoryClause());

        Query q = HibernateUtil.getCurrentSession().createQuery(sb.toString());
        q.setString(CATAGORY_SUB, c.getName());
        q.setString(KEYWORD_SUB, "%" + keyword + "%");


        return q.list();
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings(UNCHECKED)
    public List<Source> searchSourcesByCharacteristicCategory(Category c, String keyword) {
        StringBuffer sb = new StringBuffer();

        sb.append(SELECT_DISTINCT + "s");
        sb.append(generateSourcesByCharacteristicCategoryClause());

        Query q = HibernateUtil.getCurrentSession().createQuery(sb.toString());
        q.setString(CATAGORY_SUB, c.getName());
        q.setString(KEYWORD_SUB, "%" + keyword + "%");

        return q.list();
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings(UNCHECKED)
    public int countSamplesByCharacteristicCategory(Category c, String keyword) {
        StringBuffer sb = new StringBuffer();

        sb.append("SELECT count(DISTINCT s)");
        sb.append(generateSamplesByCharacteristicCategoryClause());

        Query q = HibernateUtil.getCurrentSession().createQuery(sb.toString());
        q.setString(CATAGORY_SUB, c.getName());
        q.setString(KEYWORD_SUB, "%" + keyword + "%");

        return ((Number) q.uniqueResult()).intValue();
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings(UNCHECKED)
    public int countSourcesByCharacteristicCategory(Category c, String keyword) {
        StringBuffer sb = new StringBuffer();

        sb.append("SELECT count(DISTINCT s)");
        sb.append(generateSourcesByCharacteristicCategoryClause());

        Query q = HibernateUtil.getCurrentSession().createQuery(sb.toString());
        q.setString(CATAGORY_SUB, c.getName());
        q.setString(KEYWORD_SUB, "%" + keyword + "%");

        return ((Number) q.uniqueResult()).intValue();
    }

    private String generateSamplesByCharacteristicCategoryClause() {

        String sb = FROM_KEYWORD
            + Sample.class.getName()
            + " s left join s.characteristics chr"
            + " left join chr.category cat"
            + " left join s.sources src"
            + " left join src.characteristics schr"
            + " left join schr.category scat, "
            + TermBasedCharacteristic.class.getName() + " tbs left join tbs.term t "
            + " WHERE t.value like :" + KEYWORD_SUB
            + " AND ((cat.name = :" + CATAGORY_SUB + " AND chr.id = tbs.id )"
            + " OR (scat.name = :" + CATAGORY_SUB + " AND schr.id = tbs.id ))";

        return sb;
    }

    private String generateSourcesByCharacteristicCategoryClause() {

        String sb = FROM_KEYWORD
            + Source.class.getName()
            + " s left join s.characteristics chr"
            + " left join chr.category cat, "
            + TermBasedCharacteristic.class.getName() + " tbs left join tbs.term t "
            + " WHERE t.value like :" + KEYWORD_SUB
            + " AND cat.name = :" + CATAGORY_SUB + " AND chr.id = tbs.id";

        return sb;
    }


    /**
     * {@inheritDoc}
     */
    public int searchCount(String keyword, BiomaterialSearchCategory... categories) {
        Query q = getSearchQuery(true, null, keyword, categories);
        return ((Number) q.uniqueResult()).intValue();
    }

    private Query getSearchQuery(boolean count, PageSortParams<? extends AbstractBioMaterial> params, String keyword,
            BiomaterialSearchCategory... categories) {
        SortCriterion<? extends AbstractBioMaterial> sortCrit = params != null ? params.getSortCriterion() : null;
        StringBuffer sb = new StringBuffer();
        if (count) {
            sb.append("SELECT COUNT(DISTINCT s)");
        } else {
            sb.append(SELECT_DISTINCT + "s");
        }
        sb.append(FROM_KEYWORD);
        if (categories[0] instanceof SearchSourceCategory) {
            sb.append(Source.class.getName()).append(" s");
        } else if (categories[0] instanceof SearchSampleCategory) {
            sb.append(Sample.class.getName()).append(" s");
        }

        sb.append(getJoinClause(categories));
        sb.append(getWhereClause(categories));

        if (!count && sortCrit != null) {
            sb.append(" ORDER BY s.").append(sortCrit.getOrderField());
            if (params.isDesc()) {
                sb.append(" desc");
            }
        }
        Query q = HibernateUtil.getCurrentSession().createQuery(sb.toString());
        q.setString(KEYWORD_SUB, "%" + keyword + "%");
        return q;
    }

    private static String getJoinClause(BiomaterialSearchCategory... categories) {
        LinkedHashSet<String> joins = new LinkedHashSet<String>();
        for (BiomaterialSearchCategory category : categories) {
            joins.addAll(Arrays.asList(category.getJoins()));
        }
        StringBuffer sb = new StringBuffer();
        for (String table : joins) {
            sb.append(" LEFT JOIN ").append(table);
        }
        return sb.toString();
    }

    private String getWhereClause(BiomaterialSearchCategory... categories) {
        StringBuffer sb = new StringBuffer();
        int i = 0;
        for (BiomaterialSearchCategory category : categories) {
            sb.append(i++ == 0 ? " WHERE (" : " OR (").append(category.getWhereClause()).append(')');
        }
        return sb.toString();
    }

    @Override
    Logger getLog() {
        return LOG;
    }

}
