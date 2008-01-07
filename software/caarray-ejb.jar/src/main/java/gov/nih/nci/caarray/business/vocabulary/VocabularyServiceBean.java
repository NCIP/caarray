/**
 * The software subject to this notice and license includes both human readable
 * source code form and machine readable, binary, object code form. The caArray
 * Software was developed in conjunction with the National Cancer Institute
 * (NCI) by NCI employees and 5AM Solutions, Inc. (5AM). To the extent
 * government employees are authors, any rights in such works shall be subject
 * to Title 17 of the United States Code, section 105.
 *
 * This caArray Software License (the License) is between NCI and You. You (or
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
 * its rights in the caArray Software to (i) use, install, access, operate,
 * execute, copy, modify, translate, market, publicly display, publicly perform,
 * and prepare derivative works of the caArray Software; (ii) distribute and
 * have distributed to and by third parties the caArray Software and any
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
package gov.nih.nci.caarray.business.vocabulary;

import edu.georgetown.pir.Organism;
import gov.nih.nci.caarray.application.ExceptionLoggingInterceptor;
import gov.nih.nci.caarray.dao.CaArrayDaoFactory;
import gov.nih.nci.caarray.dao.OrganismDao;
import gov.nih.nci.caarray.dao.VocabularyDao;
import gov.nih.nci.caarray.domain.project.Experiment;
import gov.nih.nci.caarray.domain.protocol.Protocol;
import gov.nih.nci.caarray.domain.vocabulary.Category;
import gov.nih.nci.caarray.domain.vocabulary.Term;
import gov.nih.nci.caarray.domain.vocabulary.TermSource;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.interceptor.Interceptors;

import org.hibernate.criterion.Order;

/**
 * Entry point into implementation of the vocabulary service subsystem.
 */
@Local(VocabularyService.class)
@Stateless
@Interceptors(ExceptionLoggingInterceptor.class)
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class VocabularyServiceBean implements VocabularyService {

    private CaArrayDaoFactory daoFactory = CaArrayDaoFactory.INSTANCE;

    /**
     * Creates a new instance.
     */
    public VocabularyServiceBean() {
        super();
    }

    /**
     *
     * {@inheritDoc}
     */
    public Set<Term> getTerms(final String categoryName) {
        return getTerms(categoryName, null);
    }

    /**
     * {@inheritDoc}
     */
    public Set<Term> getTerms(final String categoryName, String value) {
        if (categoryName == null) {
            throw new IllegalArgumentException("CategoryName is null");
        }
        return getVocabularyDao().getTermsRecursive(categoryName, value);
    }

    /**
     * {@inheritDoc}
     */
    public List<Organism> getOrganisms() {
        return getOrganismDao().getAllOrganisms();
    }

    /**
     * {@inheritDoc}
     */
    public TermSource getSource(String name) {
        TermSource querySource = new TermSource();
        querySource.setName(name);
        return uniqueResult(getVocabularyDao().queryEntityByExample(querySource));
    }

    /**
     *
     * {@inheritDoc}
     */
    public List<TermSource> getAllSources() {
        return getVocabularyDao().queryEntityByExample(new TermSource(), Order.asc("name"));
    }

    /**
     * {@inheritDoc}
     */
    public Category getCategory(TermSource source, String categoryName) {
        Category queryCategory = new Category();
        queryCategory.setName(categoryName);
        return uniqueResult(getVocabularyDao().queryEntityByExample(queryCategory));
    }

    /**
     * {@inheritDoc}
     */
    public Term getTerm(TermSource source, Category category, String value) {
        return uniqueResult(getVocabularyDao().getTerms(source, category, value));
    }

    /**
     * {@inheritDoc}
     */
    public Term getTerm(Long id) {
        return getVocabularyDao().getTermById(id);
    }

    /**
     * {@inheritDoc}
     */
    public List<Term> getCellTypesForExperiment(Experiment experiment) {
        return getVocabularyDao().getCellTypesForExperiment(experiment);
    }

    /**
     * {@inheritDoc}
     */
    public List<Term> getDiseaseStatesForExperiment(Experiment experiment) {
        return getVocabularyDao().getDiseaseStatesForExperiment(experiment);
    }

    /**
     * {@inheritDoc}
     */
    public List<Term> getMaterialTypesForExperiment(Experiment experiment) {
        return getVocabularyDao().getMaterialTypesForExperiment(experiment);
    }

    /**
     * {@inheritDoc}
     */
    public List<Term> getTissueSitesForExperiment(Experiment experiment) {
        return getVocabularyDao().getTissueSitesForExperiment(experiment);
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("deprecation")
    public List<Protocol> getProtocolByProtocolType(Term type) {
        if (type == null) {
            return new ArrayList<Protocol>();
        }
        Protocol p = new Protocol();
        p.setType(type);
        return getVocabularyDao().queryEntityAndAssociationsByExample(p);
    }

    /**
     * {@inheritDoc}
     */
    public Protocol getProtocol(String name, Term type, TermSource source) {
        return this.daoFactory.getProtocolDao().getProtocol(name, type, source);
    }

    /**
     * {@inheritDoc}
     */
    public Organism getOrganism(Long id) {
        return getOrganismDao().getOrganism(id);
    }

    /**
     * {@inheritDoc}
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void saveTerm(Term term) {
        getVocabularyDao().save(term);
    }

    /**
     * Method to take a get a unique result from a set and return it or null.
     * @param <T> the type of the returned object
     * @param results the set of results returned from a query
     * @return the first result in the set or null
     */
    private <T> T uniqueResult(Collection<T> results) {
        return results.isEmpty() ? null : results.iterator().next();
    }

    /**
     *
     * @return VocabularyDao
     */
    protected VocabularyDao getVocabularyDao() {
        return this.daoFactory.getVocabularyDao();
    }

    /**
     * @return OrganismDao
     */
    private OrganismDao getOrganismDao() {
        return this.daoFactory.getOrganismDao();
    }

    final CaArrayDaoFactory getDaoFactory() {
        return this.daoFactory;
    }

    final void setDaoFactory(CaArrayDaoFactory daoFactory) {
        this.daoFactory = daoFactory;
    }
}
