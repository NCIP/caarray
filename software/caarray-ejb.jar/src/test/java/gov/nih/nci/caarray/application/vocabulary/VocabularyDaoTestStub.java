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
package gov.nih.nci.caarray.application.vocabulary;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import gov.nih.nci.caarray.dao.DAOException;
import gov.nih.nci.caarray.dao.VocabularyDao;
import gov.nih.nci.caarray.domain.AbstractCaArrayEntity;
import gov.nih.nci.caarray.domain.vocabulary.Term;
import gov.nih.nci.caarray.domain.vocabulary.Category;

/**
 * @author John Pike
 *
 */
public class VocabularyDaoTestStub implements VocabularyDao {

    /* (non-Javadoc)
     * @see gov.nih.nci.caarray.dao.VocabularyDao#getTerms(java.lang.String)
     */
    public List<Term> getTerms(String categoryName) throws DAOException {
        // TODO Auto-generated method stub
        return new ArrayList<Term>();
    }

    public void removeTerms(List<Term> caArrayEntities) throws DAOException {
        // TODO Auto-generated method stub

    }


    /* (non-Javadoc)
     * @see gov.nih.nci.caarray.dao.VocabularyDao#getCategory(java.lang.String)
     */
    public Category getCategory(String name) throws DAOException {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see gov.nih.nci.caarray.dao.CaArrayDao#save(gov.nih.nci.caarray.domain.AbstractCaArrayEntity)
     */
    public void save(AbstractCaArrayEntity caArrayEntity) throws DAOException {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see gov.nih.nci.caarray.dao.CaArrayDao#save(java.util.Collection)
     */
    public void save(Collection<? extends AbstractCaArrayEntity> caArrayEntities) throws DAOException {
        // TODO Auto-generated method stub

    }

    /**
     * Returns the list of <code>AbstractCaArrayEntity</code> matching the given entity,
     * or null if none exists.
     *
     * @param entityToMatch get <code>AbstractCaArrayEntity</code> objects matching this entity
     * @return the List of <code>AbstractCaArrayEntity</code> objects, or an empty List.
     * @throws DAOException if the list of matching entities could not be retrieved.
     */
    public List<AbstractCaArrayEntity> queryEntityByExample(AbstractCaArrayEntity entityToMatch) throws DAOException {
        // TODO Auto-generated method stub
        return new ArrayList<AbstractCaArrayEntity>();
    }

    public List<AbstractCaArrayEntity> queryEntityAndAssociationsByExample(AbstractCaArrayEntity entityToMatch) throws DAOException {
        // TODO Auto-generated method stub
        return new ArrayList<AbstractCaArrayEntity>();
    }

    /**
     * Deletes the entity from persistent storage.
     *
     * @param caArrayEntity the entity to be deleted.
     * @throws DAOException if unable to delete the entity.
     */
    public void remove(AbstractCaArrayEntity caArrayEntity) throws DAOException {
        // TODO Auto-generated method stub

    }

    /**
     * Returns the <code>AbstractCaArrayEntity</code> matching the given id,
     * or null if none exists.
     *
     * @param entityToMatch get <code>AbstractCaArrayEntity</code> objects matching this id.
     * @return the retrieved <code>AbstractCaArrayEntity</code> or null.
     * @throws DAOException if matching entities could not be retrieved.
     */
    public AbstractCaArrayEntity queryEntityById(AbstractCaArrayEntity entityToMatch) throws DAOException {
        return null;
    }

    /** (non-Javadoc)
     * @see gov.nih.nci.caarray.dao.VocabularyDao#getTermsRecursive(java.lang.String)
     */
    public Set<Term> getTermsRecursive(String categoryName) throws DAOException {
        return null;
    }
}
