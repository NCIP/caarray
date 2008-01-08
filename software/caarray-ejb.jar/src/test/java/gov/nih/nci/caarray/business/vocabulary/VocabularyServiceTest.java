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

import static org.junit.Assert.assertTrue;
import gov.nih.nci.caarray.dao.DAOException;
import gov.nih.nci.caarray.dao.VocabularyDao;
import gov.nih.nci.caarray.dao.stub.DaoFactoryStub;
import gov.nih.nci.caarray.dao.stub.VocabularyDaoStub;
import gov.nih.nci.caarray.domain.project.ExperimentOntology;
import gov.nih.nci.caarray.domain.project.ExperimentOntologyCategory;
import gov.nih.nci.caarray.domain.vocabulary.Category;
import gov.nih.nci.caarray.domain.vocabulary.Term;
import gov.nih.nci.caarray.domain.vocabulary.TermSource;

import java.util.HashSet;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

/**
 * @author John Pike
 * 
 */
@SuppressWarnings("PMD")
public class VocabularyServiceTest {

    private static final int NUM_PROT_TYPES = 37;
    private VocabularyService vocabularyService;
    private final LocalDaoFactoryStub daoFactoryStub = new LocalDaoFactoryStub();

    // //TEST METHODS///////////////////////////////////

    @Before
    public void setUpService() {
        VocabularyServiceBean vsBean = new VocabularyServiceBean();
        vsBean.setDaoFactory(this.daoFactoryStub);
        vocabularyService = vsBean;
    }

    /**
     * Test to ensure that 37 entries are returned for the categoryName "ProtocolType". Test method for
     * {@link gov.nih.nci.caarray.business.vocabulary.VocabularyServiceBean#getTerms (java.lang.String)}.
     */
    @Test
    public void testGetTerms() {
        Set<Term> terms = new HashSet<Term>();
        try {
            TermSource mo = vocabularyService.getSource(ExperimentOntology.MGED_ONTOLOGY.getOntologyName(),
                    ExperimentOntology.MGED_ONTOLOGY.getVersion());
            Category protocolType = vocabularyService.getCategory(mo, ExperimentOntologyCategory.PROTOCOL_TYPE.getCategoryName());
            terms = vocabularyService.getTerms(protocolType);
            assertTrue(!terms.isEmpty());
            assertTrue(terms.size() == NUM_PROT_TYPES);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Test to ensure IllegalArgumentException is thrown if a null arg is passed to "getTerms()" method
     */
    @Test(expected = IllegalArgumentException.class)
    public void getTermsNullCategory() {
        vocabularyService.getTerms(null);
    }

    // ////// INNER CLASS TEST STUBS///////////////////////
    private static class LocalDaoFactoryStub extends DaoFactoryStub {
        LocalVocabularyDaoStub vocabularyDao = new LocalVocabularyDaoStub();

        /**
         * {@inheritDoc}
         */
        @Override
        public VocabularyDao getVocabularyDao() {
            return vocabularyDao;
        }
    }

    private static class LocalVocabularyDaoStub extends VocabularyDaoStub {
        @Override
        public Set<Term> getTerms(Category category) throws DAOException {
            return new HashSet<Term>();
        }

        @Override
        public Set<Term> getTermsRecursive(Category category, String value) {
            throw new DAOException("This is a test exception");
        }
    }
}
