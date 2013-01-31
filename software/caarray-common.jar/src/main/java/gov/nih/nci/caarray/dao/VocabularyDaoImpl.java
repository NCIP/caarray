//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.dao;

import edu.georgetown.pir.Organism;
import gov.nih.nci.caarray.domain.sample.TermBasedCharacteristic;
import gov.nih.nci.caarray.domain.vocabulary.Category;
import gov.nih.nci.caarray.domain.vocabulary.Term;
import gov.nih.nci.caarray.domain.vocabulary.TermSource;
import gov.nih.nci.caarray.util.HibernateUtil;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

/**
 * DAO for entities in the <code>gov.nih.nci.caarray.domain.vocabulary</code> package.
 *
 * @author John Pike
 */
class VocabularyDaoImpl extends AbstractCaArrayDaoImpl implements VocabularyDao {

    private static final String VALUE_COLUMN_NAME = "value";
    private static final Logger LOG = Logger.getLogger(VocabularyDaoImpl.class);
    private static final String UNCHECKED = "unchecked";
    private static final String SELECT_DISTINCT = " SELECT DISTINCT ";
    private static final String KEYWORD_SUB = "keyword";
    private static final String FROM_KEYWORD = " FROM ";

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings(UNCHECKED)
    public Set<Term> getTerms(Category category) {
        Query query = HibernateUtil.getCurrentSession().createQuery(
                "select distinct t from " + Term.class.getName()
                        + " t join t.categories cat where cat = :category order by t.value asc");
        query.setEntity("category", category);
        return new LinkedHashSet<Term>(query.list());
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public Set<Term> getTermsRecursive(Category category, String valuePrefix) {
        Set<Category> searchCategories = new HashSet<Category>();
        searchCategories.add(category);
        addChildren(searchCategories, category);

        StringBuilder whereClause = new StringBuilder("cat in (:categories)");
        if (valuePrefix != null) {
            whereClause.append(" and upper(t.value) like upper(:valuePrefix)");
        }
        Query query = HibernateUtil.getCurrentSession().createQuery(
                "select distinct t from " + Term.class.getName() + " t join t.categories cat where "
                        + whereClause.toString() + " order by t.value asc");
        query.setParameterList("categories", searchCategories);
        if (valuePrefix != null) {
            query.setString("valuePrefix", valuePrefix + "%");
        }
        return new LinkedHashSet<Term>(query.list());
    }

    /**
     * {@inheritDoc}
     */
    public Term getTerm(TermSource source, String value) {
        Criteria criteria = HibernateUtil.getCurrentSession().createCriteria(Term.class);
        criteria.add(Restrictions.eq(VALUE_COLUMN_NAME, value).ignoreCase());
        criteria.add(Restrictions.eq("source", source));
        return (Term) criteria.uniqueResult();
    }

    /**
     * {@inheritDoc}
     */
    public Organism getOrganism(TermSource source, String scientificName) {
        Criteria criteria = HibernateUtil.getCurrentSession().createCriteria(Organism.class);
        criteria.add(Restrictions.eq("scientificName", scientificName).ignoreCase());
        criteria.add(Restrictions.eq("termSource", source));
        return (Organism) criteria.uniqueResult();
    }

    /**
     * Create a set of all of the descendants of the category.
     * @param searchCategories the set to build
     * @param category the category to process
     */
    private void addChildren(Collection<Category> searchCategories, Category category) {
        for (Category child : category.getChildren()) {
            searchCategories.add(child);
            addChildren(searchCategories, child);
        }
    }

    /**
     * {@inheritDoc}
     */
    public Category getCategory(TermSource termSource, String name) {
        Criteria criteria = HibernateUtil.getCurrentSession().createCriteria(Category.class);
        criteria.add(Restrictions.eq("name", name).ignoreCase());
        criteria.add(Restrictions.eq("source", termSource));
        return (Category) criteria.uniqueResult();
    }

    /**
     * {@inheritDoc}
     */
    public Term getTermById(Long id) {
        return (Term) getCurrentSession().load(Term.class, id);
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public Term findTermInAllTermSourceVersions(TermSource termSource, String value) {
        Criteria criteria = HibernateUtil.getCurrentSession().createCriteria(Term.class);
        criteria.add(Restrictions.eq("value", value).ignoreCase());
        criteria.createAlias("source", "ts");
        if (termSource.getUrl() == null) {
            criteria.add(Restrictions.eq("ts.name", termSource.getName()));
        } else {
            criteria.add(Restrictions.or(Restrictions.eq("ts.name", termSource.getName()), Restrictions.eq("ts.url",
                    termSource.getUrl())));
        }
        criteria.addOrder(Order.desc("ts.version"));
        List<Term> terms = criteria.list();
        return terms.isEmpty() ? null : terms.get(0);
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings(UNCHECKED)
    public List<Category> searchForCharacteristicCategory(String keyword) {
        StringBuffer sb = new StringBuffer();

        sb.append(SELECT_DISTINCT);
        sb.append(" c.category");
        sb.append(FROM_KEYWORD);
        sb.append(TermBasedCharacteristic.class.getName());
        sb.append(" c");
        if (keyword != null) {
            sb.append(" WHERE c.category.name like :");
            sb.append(KEYWORD_SUB);
        }
        sb.append(" ORDER BY c.category.name");

        Query q = HibernateUtil.getCurrentSession().createQuery(sb.toString());

        if (keyword != null) {
            q.setString(KEYWORD_SUB, keyword + "%");
        }

        return q.list();
    }

    @Override
    Logger getLog() {
        return LOG;
    }

}
