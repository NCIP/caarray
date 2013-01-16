//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.dao;

import edu.georgetown.pir.Organism;
import gov.nih.nci.caarray.domain.hybridization.Hybridization;
import gov.nih.nci.caarray.domain.project.Project;
import gov.nih.nci.caarray.domain.search.BrowseCategory;
import gov.nih.nci.caarray.util.CaArrayHibernateHelper;
import gov.nih.nci.security.authorization.domainobjects.User;

import java.util.List;

import org.hibernate.Query;

import com.fiveamsolutions.nci.commons.data.search.PageSortParams;
import com.fiveamsolutions.nci.commons.data.search.SortCriterion;
import com.google.inject.Inject;

/**
 * @author Winston Cheng
 */
@SuppressWarnings("PMD.CyclomaticComplexity")
public class BrowseDaoImpl extends AbstractCaArrayDaoImpl implements BrowseDao {
    private final SearchDao searchDao;
    
    /**
     * @param searchDao the SearchDao dependency
     * @param hibernateHelper the CaArrayHibernateHelper dependency
     */
    @Inject 
    public BrowseDaoImpl(SearchDao searchDao, CaArrayHibernateHelper hibernateHelper) {
        super(hibernateHelper);
        this.searchDao = searchDao;
    }

    /**
     * {@inheritDoc}
     */
    public int browseCount(BrowseCategory cat, Number fieldId) {
        Query q = browseQuery(true, null, cat, fieldId);
        return ((Number) q.uniqueResult()).intValue();
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public List<Project> browseList(PageSortParams<Project> params, BrowseCategory cat, Number fieldId) {
        Query q = browseQuery(false, params, cat, fieldId);
        q.setFirstResult(params.getIndex());
        q.setMaxResults(params.getPageSize());
        return q.list();
    }

    /**
     * {@inheritDoc}
     */
    public int countByBrowseCategory(BrowseCategory cat) {
        StringBuffer sb = new StringBuffer();
        sb.append("SELECT COUNT(DISTINCT ")
          .append(cat.getField())
          .append(") FROM ")
          .append(Project.class.getName())
          .append(" p");
        if (cat.getJoin() != null) {
            sb.append(" JOIN ").append(cat.getJoin());
        }
        Query q = getCurrentSession().createQuery(sb.toString());
        return ((Number) q.uniqueResult()).intValue();
    }

    /**
     * {@inheritDoc}
     */
    public int hybridizationCount() {
        String queryStr = "SELECT COUNT(DISTINCT h) FROM " + Hybridization.class.getName() + " h";
        Query q = getCurrentSession().createQuery(queryStr);
        return ((Number) q.uniqueResult()).intValue();
    }

    /**
     * {@inheritDoc}
     */
    public int userCount() {
        String queryStr = "SELECT COUNT(DISTINCT u) FROM " + User.class.getName() + " u";
        Query q = getCurrentSession().createQuery(queryStr);
        return ((Number) q.uniqueResult()).intValue();
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public List<Object[]> tabList(BrowseCategory cat) {
        // The query is built dynamically, but is not vulnerable to SQL injection because
        // the fields are pulled from an enum.
        String queryStr = null;
        if (BrowseCategory.EXPERIMENTS.equals(cat)) {
            queryStr = "SELECT 'Experiments',0,COUNT(p) FROM " + Project.class.getName() + " p";
        } else if (BrowseCategory.ORGANISMS.equals(cat)) {
            queryStr = "SELECT o.scientificName, o.id, count(o.scientificName) FROM " + Project.class.getName()
            + " p JOIN p.experiment.organism o GROUP BY o.scientificName";
        } else if (BrowseCategory.ARRAY_PROVIDERS.equals(cat)) {
            queryStr = "SELECT m.name, m.id, count(p) FROM " + Project.class.getName()
            + " p JOIN p.experiment.manufacturer m GROUP BY m";
        } else if (BrowseCategory.ARRAY_DESIGNS.equals(cat)) {
            queryStr = "SELECT a.name, a.id, count(p) FROM " + Project.class.getName()
            + " p JOIN p.experiment.arrayDesigns a GROUP BY a";
        } else {
            return null;
        }
        return getCurrentSession().createQuery(queryStr).list();
    }


    @SuppressWarnings({"PMD.CyclomaticComplexity", "PMD.ExcessiveMethodLength", "PMD.NPathComplexity", "deprecation" })
    private Query browseQuery(boolean count, PageSortParams<Project> params, BrowseCategory cat, Number fieldId) {
        // The query is built dynamically, but is not vulnerable to SQL injection
        // because the fields are pulled from an enum.
        SortCriterion<Project> sortCrit = params != null ? params.getSortCriterion() : null;
        boolean allProjects = BrowseCategory.EXPERIMENTS.equals(cat);
        StringBuffer sb = new StringBuffer();

        if (count) {
            sb.append("SELECT COUNT(DISTINCT p)");
        } else {
            sb.append("SELECT DISTINCT p");
        }
        sb.append(" FROM ").append(Project.class.getName()).append(" p");
        if (!count) {
            sb.append(" left join fetch p.experiment e left join fetch e.organism");
        }
        if (cat.getJoin() != null) {
            sb.append(" JOIN ").append(cat.getJoin());
        }
        if (!allProjects) {
            sb.append(" WHERE ").append(cat.getField());
            sb.append((BrowseCategory.ORGANISMS.equals(cat)?".scientificName = :name" : ".id = :id"));
        }
        if (!count && sortCrit != null) {
            sb.append(" ORDER BY p.").append(sortCrit.getOrderField());
            if (params.isDesc()) {
                sb.append(" desc");
            }
        }
        Query q = getCurrentSession().createQuery(sb.toString());
        if (!allProjects) {
            if (cat.equals(BrowseCategory.ORGANISMS)) {
                Organism org = searchDao.retrieveUnsecured(Organism.class, fieldId.longValue());
                q.setParameter("name", org.getScientificName());
            } else {
                q.setParameter("id", fieldId);
            }
        }
        return q;
    }
}
