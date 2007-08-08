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
package gov.nih.nci.caarray.business.vocabulary;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import gov.nih.nci.caarray.dao.DAOException;
import gov.nih.nci.caarray.dao.VocabularyDao;
import gov.nih.nci.caarray.dao.stub.DaoFactoryStub;
import gov.nih.nci.caarray.domain.AbstractCaArrayEntity;
import gov.nih.nci.caarray.domain.vocabulary.Category;
import gov.nih.nci.caarray.domain.vocabulary.Term;
import gov.nih.nci.system.applicationservice.ApplicationService;

import org.junit.Test;

/**
 * @author John Pike
 *
 */
@SuppressWarnings("PMD")
public class VocabularyServiceTest {

    private static final int NUM_PROT_TYPES = 37;


////TEST METHODS///////////////////////////////////

    /**
     * Test to ensure that 37 entries are returned for the categoryName "ProtocolType".
     * Test method for {@link gov.nih.nci.caarray.business.vocabulary.VocabularyServiceBean#getTerms
     * (java.lang.String)}.
    */
    @Test
    public void getTermsProtocolType() {
        VocabularyService vocab = new MockVocabularyServiceBean();
        List<Term> terms = new ArrayList<Term>();
        try {
             terms =  vocab.getTerms("ProtocolType");
             assertTrue(!terms.isEmpty());
             assertTrue(terms.size() == NUM_PROT_TYPES);
        } catch (Exception e) {
             System.out.println(e.getMessage());
        }
    }

    /**
     * Test to ensure IllegalArgumentException is thrown if a null arg
     * is passed to "getTerms()" method
     * @throws VocabularyServiceException
     */
    @Test(expected=IllegalArgumentException.class)
    public void getTermsNullCategory() throws VocabularyServiceException {
        VocabularyService vocab = new MockVocabularyServiceBean();
        vocab.getTerms(null);
    }


    /**  Test to ensure that no results are found for the category "Foo",
     *   and that this will return an empty list.
     * Test method for {@link gov.nih.nci.caarray.business.vocabulary.VocabularyServiceBean#getTerms
     * (java.lang.String)}.
     */
    @Test
    public void getTermsBadTerm() {
        VocabularyService vocab = new MockVocabularyServiceBean();
        List<Term> terms = new ArrayList<Term>();
        try {
             terms =  vocab.getTerms("Foo");
        } catch (VocabularyServiceException e) {

        }
        assertTrue(terms.isEmpty());
    }

    /**
     * Test to ensure that when the EVS service goes haywire, the getTerms() method will create
     * a VocabServiceException
     * Test method for {@link gov.nih.nci.caarray.business.vocabulary.VocabularyServiceBean#getTerms
     * (java.lang.String)}.
     */
    @Test(expected=VocabularyServiceException.class)
    public void getTermsEVSException() throws VocabularyServiceException {
        VocabularyService vocab = new MockVSBeanForEVSException();
        vocab.getTerms("ProtocolType");

    }

    @Test(expected=DAOException.class)
    public void testDAOException() throws DAOException {
        VocabularyDao vocabDao = new MockVocabularyDaoForException();
        vocabDao.getTerms("ProtocolType");
    }


//////// INNER CLASS TEST STUBS///////////////////////
    /**
     * Put all of my inner stub classes here
     * @author John Pike
     *
     */
    class MockEVSUtility extends EVSUtility {

        MockEVSUtility() {
            super(new DaoFactoryStub());
        }

        @Override
        public ApplicationService getApplicationInstance() {
            return ApplicationService.getRemoteInstance("http://yahoo.com");
        }
    }

    class MockVSBeanForEVSException extends VocabularyServiceBean {
        @Override
        public VocabularyDao getVocabularyDao() {
            return new MockVocabularyDao();
        }
        @Override
        EVSUtility getEVSUtility() {
            return new MockEVSUtility();
        }
    }
    class MockVocabularyServiceBean extends VocabularyServiceBean {
        @Override
        public VocabularyDao getVocabularyDao() {
            return new MockVocabularyDao();
        }
    }
    public class MockVocabularyDao extends VocabularyDaoTestStub {


        @Override
        public List<Term> getTerms(String categoryName) throws DAOException {
            return new ArrayList<Term>();
        }
        @Override
        public Set<Term> getTermsRecursive(String categoryName) throws DAOException {
            throw new DAOException("This is a test exception");
        }

        @Override
        public List<AbstractCaArrayEntity> queryEntityByExample(AbstractCaArrayEntity entityToMatch) throws DAOException {
            return new ArrayList<AbstractCaArrayEntity>();
        }
        @Override
        public List<AbstractCaArrayEntity> queryEntityAndAssociationsByExample(AbstractCaArrayEntity entityToMatch) throws DAOException {
            return new ArrayList<AbstractCaArrayEntity>();
        }
     }
    public class MockVocabularyDaoForException implements VocabularyDao {

        public List<Term> getTerms(String categoryName) throws DAOException {
            throw new DAOException("This is a test exception");
        }
        public Set<Term> getTermsRecursive(String categoryName) throws DAOException {
            throw new DAOException("This is a test exception");
        }
        public Category getCategory(String name) throws DAOException {
            throw new DAOException("This is a test exception");
        }
        public void save(AbstractCaArrayEntity caArrayEntity) throws DAOException {
        }
        public void save(Collection<? extends AbstractCaArrayEntity> caArrayEntities) throws DAOException {
        }
        public <T extends AbstractCaArrayEntity> List<T> queryEntityByExample(T entityToMatch) throws DAOException {
            return new ArrayList<T>();
        }
        public <T extends AbstractCaArrayEntity> List<T> queryEntityAndAssociationsByExample(T entityToMatch) throws DAOException {
            return new ArrayList<T>();
        }
        public <T extends AbstractCaArrayEntity> T queryEntityById(T entityToMatch) throws DAOException {
            return null;
        }
        public void remove(AbstractCaArrayEntity caArrayEntity) throws DAOException {
        }
        public void removeTerms(List<Term> entityList) throws DAOException {
        }

    }
}
