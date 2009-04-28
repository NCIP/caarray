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
import gov.nih.nci.caarray.domain.sample.TermBasedCharacteristic;
import gov.nih.nci.caarray.domain.search.BiomaterialSearchCategory;
import gov.nih.nci.caarray.domain.search.JoinableSortCriterion;
import gov.nih.nci.caarray.domain.search.SearchSampleCategory;
import gov.nih.nci.caarray.domain.search.SearchSourceCategory;
import gov.nih.nci.caarray.domain.vocabulary.Category;
import gov.nih.nci.caarray.util.HibernateUtil;

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hibernate.Query;

import com.fiveamsolutions.nci.commons.data.search.PageSortParams;
import com.fiveamsolutions.nci.commons.util.HibernateHelper;

/**
 * DAO for entities in the <code>gov.nih.nci.caarray.domain.sample</code> package.
 * @author mshestopalov
 *
 */
@SuppressWarnings("PMD.CyclomaticComplexity")
public class SampleDaoImpl extends AbstractCaArrayDaoImpl implements SampleDao {
    private static final String UNCHECKED = "unchecked";
    private static final String FROM_KEYWORD = " FROM ";
    private static final String SELECT_DISTINCT = " SELECT DISTINCT ";
    private static final String KEYWORD_SUB = "keyword";
    private static final String CATAGORY_SUB = "mycat";
    private static final String ORDER_BY = " ORDER BY ";
    private static final String LEFT_JOIN = " LEFT JOIN ";

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings(UNCHECKED)
    public <T extends AbstractBioMaterial>List<T>  searchByCategory(PageSortParams<T> params, String keyword,
            BiomaterialSearchCategory... categories) {
        Query q = getSearchQuery(false, params, keyword, categories);
        q.setFirstResult(params.getIndex());
        if (params.getPageSize() > 0) {
            q.setMaxResults(params.getPageSize());
        }
        return q.list();
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings(UNCHECKED)
     public List<Source> searchSourcesByCharacteristicCategory(PageSortParams<Source> params,
             Category c, String keyword) {

        StringBuffer sb = new StringBuffer();

        sb.append(SELECT_DISTINCT + "s");
        sb.append(generateSourcesByCharacteristicCategoryClause(false, params, keyword));

        Query q = HibernateUtil.getCurrentSession().createQuery(sb.toString());
        q.setString(CATAGORY_SUB, c.getName());

        if (keyword != null && !keyword.equals("")) {
            q.setString(KEYWORD_SUB, "%" + keyword + "%");
        }

        q.setFirstResult(params.getIndex());
        if (params.getPageSize() > 0) {
            q.setMaxResults(params.getPageSize());
        }
        return q.list();
    }

    /**
     * {@inheritDoc}
     */
    public int countSourcesByCharacteristicCategory(Category c, String keyword) {
        StringBuffer sb = new StringBuffer();

        sb.append("SELECT count(DISTINCT s)");
        sb.append(generateSourcesByCharacteristicCategoryClause(true, null, keyword));

        Query q = HibernateUtil.getCurrentSession().createQuery(sb.toString());
        q.setString(CATAGORY_SUB, c.getName());
        if (keyword != null && !keyword.equals("")) {
            q.setString(KEYWORD_SUB, "%" + keyword + "%");
        }

        return ((Number) q.uniqueResult()).intValue();
    }

    /**
     * {@inheritDoc}
     */
    public int countSamplesByCharacteristicCategory(Category c, String keyword) {
        Query q = createQueryForSamplesByCharacteristicCategory(true, "SELECT count(DISTINCT s)", null, c, keyword);

        if (q == null) {
            return 0;
        }

        return  ((Number) q.uniqueResult()).intValue();

    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings(UNCHECKED)
    public List<Sample> searchSamplesByCharacteristicCategory(PageSortParams<Sample> params,
            Category c, String keyword) {
        Query q = createQueryForSamplesByCharacteristicCategory(false, SELECT_DISTINCT + "s", params, c, keyword);
        q.setFirstResult(params.getIndex());
        if (params.getPageSize() > 0) {
            q.setMaxResults(params.getPageSize());
        }
        return q.list();
    }

    @SuppressWarnings(UNCHECKED)
    private Query createQueryForSamplesByCharacteristicCategory(boolean count,
            String selectClause, PageSortParams<Sample> params, Category c, String keyword) {

        Query returnVal = null;
        if (keyword != null && !keyword.equals("")) {

            StringBuffer tbs = new StringBuffer();
            tbs.append("SELECT DISTINCT tbs");
            tbs.append(generateTermByKeywordClause());
            Query tbsq = HibernateUtil.getCurrentSession().createQuery(tbs.toString());
            tbsq.setString(KEYWORD_SUB, "%" + keyword + "%");
            List<TermBasedCharacteristic> tbsList = tbsq.list();

            if (!tbsList.isEmpty()) {

                Map<String, List<? extends Serializable>> idBlocks =
                    new HashMap<String, List<? extends Serializable>>();
                StringBuffer sb = new StringBuffer();
                sb.append(selectClause);
                sb.append(generateSamplesByCharacteristicCategoryClause(count, tbsList, params, idBlocks));
                returnVal = HibernateUtil.getCurrentSession().createQuery(sb.toString());
                returnVal.setString(CATAGORY_SUB, c.getName());
                HibernateHelper.bindInClauseParameters(returnVal, idBlocks);
            }

        } else {

            StringBuffer sb = new StringBuffer();
            sb.append(selectClause);
            sb.append(generateSamplesByCharacteristicCategoryClause(count, null, params, null));
            returnVal = HibernateUtil.getCurrentSession().createQuery(sb.toString());
            returnVal.setString(CATAGORY_SUB, c.getName());
        }

        return returnVal;
    }

    private String generateSamplesByCharacteristicCategoryClause(boolean count, List<? extends Serializable> tbsList,
            PageSortParams<Sample> params, Map<String, List<? extends Serializable>> idBlocks) {

        // need to break this up into blocks of 500 to get around bug
        // http://opensource.atlassian.com/projects/hibernate/browse/HHH-2166
        JoinableSortCriterion<Sample> sortCrit =
            params != null ? (JoinableSortCriterion<Sample>) params.getSortCriterion() : null;
        StringBuffer sb = new StringBuffer();
        sb.append(FROM_KEYWORD);
        sb.append(Sample.class.getName());
        sb.append(" s ");
        sb.append(getJoinClause(count, sortCrit, "s.characteristics chr",
                "chr.category cat", "s.sources src", "src.characteristics schr",
                "schr.category scat"));

        if (tbsList != null) {
            sb.append(" WHERE (cat.name = :" + CATAGORY_SUB
                + " AND " + HibernateHelper.buildInClause(tbsList, "chr", idBlocks) + " )"
                + " OR (scat.name = :" + CATAGORY_SUB
                + " AND " + HibernateHelper.buildInClause(tbsList, "schr", idBlocks) + " )");
        } else {
            sb.append(" WHERE cat.name = :" + CATAGORY_SUB
                + " OR scat.name = :" + CATAGORY_SUB);
        }

        if (!count && sortCrit != null) {
            sb.append(ORDER_BY).append(getOrderByField(sortCrit));
            if (params.isDesc()) {
                sb.append(" desc");
            }
        }

        return sb.toString();
    }

    private String generateTermByKeywordClause() {
        StringBuffer sb = new StringBuffer();
        sb.append(FROM_KEYWORD);
        sb.append(TermBasedCharacteristic.class.getName());
        sb.append(" tbs " + LEFT_JOIN + " tbs.term t WHERE t.value like :" + KEYWORD_SUB);

        return sb.toString();

    }


    private String generateSourcesByCharacteristicCategoryClause(boolean count, PageSortParams<Source> params,
            String keyword) {

        JoinableSortCriterion<Source> sortCrit =
            params != null ? (JoinableSortCriterion<Source>) params.getSortCriterion() : null;
        StringBuffer sb = new StringBuffer();
        sb.append(FROM_KEYWORD);
        sb.append(Source.class.getName());
        sb.append(" s ");
        sb.append(getJoinClause(count, sortCrit, "s.characteristics chr", "chr.category cat"));
        sb.append(", ");
        sb.append(TermBasedCharacteristic.class.getName() + " tbs " + LEFT_JOIN + " tbs.term t "
            + " WHERE cat.name = :" + CATAGORY_SUB + " AND chr.id = tbs.id");

        if (keyword != null && !keyword.equals("")) {
            sb.append(" AND t.value like :" + KEYWORD_SUB);
        }

        if (!count && sortCrit != null) {
            sb.append(ORDER_BY).append(getOrderByField(sortCrit));
            if (params.isDesc()) {
                sb.append(" desc");
            }
        }

        return sb.toString();
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings(UNCHECKED)
    public List<Sample> searchSamplesByExperimentAndCategory(String keyword, Experiment e, SearchSampleCategory... c) {
        StringBuffer sb = new StringBuffer();

        sb.append(SELECT_DISTINCT + "s");
        sb.append(FROM_KEYWORD + Sample.class.getName() + " s");
        sb.append(getJoinClause(false, null, c));
        sb.append(getWhereClause(c));
        sb.append(" AND s.experiment = :exp" + ORDER_BY + "s.name");
        Query q = HibernateUtil.getCurrentSession().createQuery(sb.toString());
        q.setEntity("exp", e);
        q.setString(KEYWORD_SUB, "%" + keyword + "%");

        return q.list();
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
        JoinableSortCriterion<? extends AbstractBioMaterial> sortCrit =
            params != null ? (JoinableSortCriterion<? extends AbstractBioMaterial>) params.getSortCriterion() : null;
        StringBuffer sb = new StringBuffer();
        if (count) {
            sb.append("SELECT COUNT(DISTINCT s)");
        } else {
            sb.append(SELECT_DISTINCT + "s");
        }

        sb.append(generateSearchClause(count, sortCrit, categories));

        if (!count && sortCrit != null) {
            sb.append(ORDER_BY).append(getOrderByField(sortCrit));
            if (params.isDesc()) {
                sb.append(" desc");
            }
        }
        Query q = HibernateUtil.getCurrentSession().createQuery(sb.toString());
        q.setString(KEYWORD_SUB, "%" + keyword + "%");
        return q;
    }

    private String generateSearchClause(boolean count, JoinableSortCriterion<? extends AbstractBioMaterial> sortCrit,
            BiomaterialSearchCategory... categories) {

        StringBuffer sb = new StringBuffer();
        sb.append(FROM_KEYWORD);
        if (categories[0] instanceof SearchSourceCategory) {
            sb.append(Source.class.getName());
        } else if (categories[0] instanceof SearchSampleCategory) {
            sb.append(Sample.class.getName());
        }

        sb.append(" s");
        sb.append(getJoinClause(count, sortCrit, categories));

        sb.append(getWhereClause(categories));
        return sb.toString();

    }

    private static String getJoinClause(boolean count, JoinableSortCriterion<? extends AbstractBioMaterial> sortCrit,
            String...joinTables) {
        LinkedHashSet<String> joins = new LinkedHashSet<String>();

        joins.addAll(Arrays.asList(joinTables));

        return generateJoinClause(count, sortCrit, joins);
    }

    private static String getJoinClause(boolean count, JoinableSortCriterion<? extends AbstractBioMaterial> sortCrit,
            BiomaterialSearchCategory... categories) {
        LinkedHashSet<String> joins = new LinkedHashSet<String>();

        for (BiomaterialSearchCategory category : categories) {
            joins.addAll(Arrays.asList(category.getJoins()));
        }

        return generateJoinClause(count, sortCrit, joins);
    }

    private static String generateJoinClause(boolean count,
            JoinableSortCriterion<? extends AbstractBioMaterial> sortCrit, Set<String> joins) {
        String[] orderByJoins = null;

        if (!count && sortCrit != null) {
            orderByJoins = sortCrit.getJoins();
        }

        Set<String> completeJoinsList = new LinkedHashSet<String>();
        completeJoinsList.addAll(joins);

        if (orderByJoins != null) {
            completeJoinsList.addAll(Arrays.asList(orderByJoins));
        }

        StringBuffer sb = new StringBuffer();
        for (String joinTable : completeJoinsList) {
            sb.append(LEFT_JOIN).append(joinTable.replace("this", "s"));
        }

        return sb.toString();

    }

    private static String getOrderByField(JoinableSortCriterion<? extends AbstractBioMaterial> sortCrit) {
        String searchByField = sortCrit.getOrderField();
        return searchByField.replace("this", "s");
    }

    private String getWhereClause(BiomaterialSearchCategory... categories) {
        StringBuffer sb = new StringBuffer();
        int i = 0;
        for (BiomaterialSearchCategory category : categories) {
            sb.append(i++ == 0 ? " WHERE (" : " OR (")
                .append(category.getWhereClause().replace("this", "s")).append(')');
        }
        return sb.toString();
    }
}
