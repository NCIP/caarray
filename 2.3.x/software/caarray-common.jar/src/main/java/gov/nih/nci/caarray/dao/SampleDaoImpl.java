//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
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
