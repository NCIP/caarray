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

import java.util.List;
import java.util.ArrayList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import gov.nih.nci.caarray.domain.vocabulary.Category;
import gov.nih.nci.caarray.domain.vocabulary.Term;
import gov.nih.nci.caarray.util.HibernateUtil;

/**
 * DAO for entities in the <code>gov.nih.nci.caarray.domain.vocabulary</code> package.
 *
 * @author John Pike
 */
public class VocabularyDaoImpl extends AbstractCaArrayDaoImpl implements VocabularyDao {

    private static final Log LOG = LogFactory.getLog(VocabularyDaoImpl.class);

    /**
     * Gets all the <code>Terms</code> matching the category name
     * given.
     *
     * @param categoryName get terms for this category
     * @return all matching terms or an empty <code>List</code> if no matches.
     * @exception DAOException exception
     */
    @SuppressWarnings("unchecked")
    public List<Term> getTerms(String categoryName) throws DAOException {
        Session mySession = HibernateUtil.getSessionForQueryMethod();
        List<Term> matchingTerms = new ArrayList<Term>();
        List hibernateReturnedTerms = null;

        try {
            hibernateReturnedTerms = mySession.createCriteria(Term.class).createCriteria(
                    "category").add(Restrictions.eq("name", categoryName)).list();
        } catch (HibernateException he) {
            getLog().error("Unable to retrieve terms", he);
            throw new DAOException("Unable to retrieve terms", he);
        } finally {
            HibernateUtil.returnSession(mySession);
        }

        if (hibernateReturnedTerms != null) {
            // Note: When Hibernate starts supporting Java generics, the SuppressWarnings
            // annotation should be removed.
            matchingTerms.addAll(hibernateReturnedTerms);
        }
        return matchingTerms;
    }


    /**
     * Returns the <code>Category</code> with the given name or null if none exists.
     *
     * @param name get <code>Category</code> matching this name
     * @return the <code>Category</code> or null.
     * @throws DAOException if there is a problem retrieving the <code>Category</code>.
     */
    public Category getCategory(String name) throws DAOException {
        Session mySession = HibernateUtil.getSessionForQueryMethod();
        List hibernateReturnedCategories = null;

        try {
            hibernateReturnedCategories = mySession.createCriteria(Category.class).add(
                    Restrictions.eq("name", name)).list();
        } catch (HibernateException he) {
            getLog().error("Unable to retrieve categories", he);
            throw new DAOException("Unable to retrieve categories", he);
        } finally {
            HibernateUtil.returnSession(mySession);
        }
        if ((hibernateReturnedCategories != null) && (hibernateReturnedCategories.size() >= 1)) {
            return (Category) hibernateReturnedCategories.get(0);
        } else {
            return null;
        }
    }

    @Override
    Log getLog() {
        return LOG;
    }

}
