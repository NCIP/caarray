/**
 *  The caArray Software License, Version 1.0
 *
 *  Copyright 2004 5AM Solutions. This software was developed in conjunction
 *  with the National Cancer Institute, and so to the extent government
 *  employees are co-authors, any rights in such works shall be subject to
 *  Title 17 of the United States Code, section 105.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions are met:
 *
 *  1. Redistributions of source code must retain the above copyright notice,
 *  this list of conditions and the disclaimer of Article 3, below.
 *  Redistributions in binary form must reproduce the above copyright notice,
 *  this list of conditions and the following disclaimer in the documentation
 *  and/or other materials provided with the distribution.
 *
 *  2. Affymetrix Pure Java run time library needs to be downloaded from
 *  (http://www.affymetrix.com/support/developer/runtime_libraries/index.affx)
 *  after agreeing to the licensing terms from the Affymetrix.
 *
 *  3. The end-user documentation included with the redistribution, if any,
 *  must include the following acknowledgment:
 *
 *  "This product includes software developed by 5AM Solutions and the National
 *  Cancer Institute (NCI).
 *
 *  If no such end-user documentation is to be included, this acknowledgment
 *  shall appear in the software itself, wherever such third-party
 *  acknowledgments normally appear.
 *
 *  4. The names "The National Cancer Institute", "NCI", and "5AM Solutions"
 *  must not be used to endorse or promote products derived from this software.
 *
 *  5. This license does not authorize the incorporation of this software into
 *  any proprietary programs. This license does not authorize the recipient to
 *  use any trademarks owned by either NCI or 5AM.
 *
 *  6. THIS SOFTWARE IS PROVIDED "AS IS," AND ANY EXPRESSED OR IMPLIED
 *  WARRANTIES, (INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
 *  MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE) ARE DISCLAIMED. IN NO
 *  EVENT SHALL THE NATIONAL CANCER INSTITUTE, SAIC, OR THEIR AFFILIATES BE
 *  LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 *  CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 *  SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 *  INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 *  CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 *  ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 *  POSSIBILITY OF SUCH DAMAGE.
 */
package gov.nih.nci.caarray.dao;

import edu.georgetown.pir.Organism;
import gov.nih.nci.caarray.domain.vocabulary.Category;
import gov.nih.nci.caarray.domain.vocabulary.Term;
import gov.nih.nci.caarray.domain.vocabulary.TermSource;
import gov.nih.nci.caarray.util.HibernateUtil;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.Query;
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
        criteria.add(Restrictions.eq("termSource", termSource));
        return (Category) criteria.uniqueResult();
    }

    /**
     * {@inheritDoc}
     */
    public Term getTermById(Long id) {
        return (Term) getCurrentSession().load(Term.class, id);
    }

    @Override
    Logger getLog() {
        return LOG;
    }

}
