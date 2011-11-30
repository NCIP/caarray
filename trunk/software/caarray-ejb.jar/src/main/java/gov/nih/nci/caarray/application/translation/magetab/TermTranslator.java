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
package gov.nih.nci.caarray.application.translation.magetab;

import gov.nih.nci.caarray.application.vocabulary.VocabularyService;
import gov.nih.nci.caarray.dao.CaArrayDaoFactory;
import gov.nih.nci.caarray.domain.project.ExperimentOntology;
import gov.nih.nci.caarray.domain.vocabulary.Category;
import gov.nih.nci.caarray.domain.vocabulary.Term;
import gov.nih.nci.caarray.domain.vocabulary.TermSource;
import gov.nih.nci.caarray.magetab.MageTabDocumentSet;
import gov.nih.nci.caarray.magetab.OntologyTerm;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.log4j.Logger;

/**
 * Translates MAGE-TAB <code>OntologyTerms</code> to caArray <code>Terms</code>.
 */
@SuppressWarnings("PMD")
final class TermTranslator extends AbstractTranslator {

    private static final Logger LOG = Logger.getLogger(TermTranslator.class);

    private final VocabularyService service;
    private final Map<TermKey, Term> termCache = new HashMap<TermKey, Term>();

    TermTranslator(MageTabDocumentSet documentSet, MageTabTranslationResult translationResult,
            VocabularyService service, CaArrayDaoFactory daoFactory) {
        super(documentSet, translationResult, daoFactory);
        this.service = service;
    }

    @Override
    void translate() {
        for (OntologyTerm ontologyTerm : getDocumentSet().getTerms()) {
            translateTerm(ontologyTerm);
        }
    }

    void translateTerm(OntologyTerm ontologyTerm) {
        TermSource source = getSource(ontologyTerm.getTermSource());
        Category category = getOrCreateCategory(ontologyTerm.getCategory());
        Term term = getOrCreateTerm(source, category, ontologyTerm.getValue());
        getTranslationResult().addTerm(ontologyTerm, term);
    }

    private TermSource getSource(gov.nih.nci.caarray.magetab.TermSource mageTabSource) {
        if (mageTabSource == null || StringUtils.isBlank(mageTabSource.getName())) {
            return this.service.getSource(ExperimentOntology.CAARRAY.getOntologyName(), ExperimentOntology.CAARRAY
                    .getVersion());
        }
        // if the source is present then it must have been translated
        TermSource ts = getTranslationResult().getSource(mageTabSource);
        if (ts == null) {
            throw new IllegalStateException("A term is referencing an untranslated term source: "
                    + mageTabSource.getName());
        }
        return ts;
    }

    private Category getOrCreateCategory(String categoryName) {
        return getOrCreateCategory(this.service, this.getTranslationResult(), categoryName);
    }

    static Category getOrCreateCategory(VocabularyService vocabService, MageTabTranslationResult translationResult,
            String categoryName) {
        if (categoryName == null) {
            return null;
        }
        
        TermSource mo = vocabService.getSource(ExperimentOntology.MGED_ONTOLOGY.getOntologyName(),
                ExperimentOntology.MGED_ONTOLOGY.getVersion());
        TermSource userDef = vocabService.getSource(ExperimentOntology.CAARRAY.getOntologyName(),
                ExperimentOntology.CAARRAY.getVersion());
        Category category = vocabService.getCategory(mo, categoryName);
        if (category == null) {
            category = vocabService.getCategory(userDef, categoryName);
        }
        if (category == null) {
            category = translationResult.getCategory(categoryName);
        }
        if (category == null) {
            category = new Category();
            category.setName(categoryName);
            category.setSource(userDef);
            translationResult.addCategory(categoryName, category);
        }
        return category;
    }

    private Term getOrCreateTerm(TermSource source, Category category, String value) {
        Term term = null;
        if (source.getId() != null) {
            term = this.service.getTerm(source, value);
        }
        if (term == null) {
            term = this.service.findTermInAllTermSourceVersions(source, value);
        }
        if (term == null) {
            term = getTermFromCache(value, source);
        }
        if (term == null) {
            term = new Term();
            term.setSource(source);
            term.setValue(value);
            addTermToCache(value, source, term);
        }
        if (category != null) {
            term.getCategories().add(category);            
        }
        return term;
    }

    private Term getTermFromCache(String value, gov.nih.nci.caarray.domain.vocabulary.TermSource ts) {
        return termCache.get(new TermKey(value, ts));
    }

    private void addTermToCache(String value, gov.nih.nci.caarray.domain.vocabulary.TermSource ts, Term term) {
        termCache.put(new TermKey(value, ts), term);
    }

    @Override
    Logger getLog() {
        return LOG;
    }

    /**
     * Key class for looking up terms in the cache by the Term natural key.
     */
    private static final class TermKey {
        private final String value;
        private final gov.nih.nci.caarray.domain.vocabulary.TermSource termSource;

        public TermKey(String name, gov.nih.nci.caarray.domain.vocabulary.TermSource termSource) {
            this.value = name;
            this.termSource = termSource;
        }

        /**
         * @return the name
         */
        public String getValue() {
            return value;
        }

        /**
         * @return the termSource
         */
        public gov.nih.nci.caarray.domain.vocabulary.TermSource getTermSource() {
            return termSource;
        }

        /*
         * (non-Javadoc)
         *
         * @see java.lang.Object#hashCode()
         */
        @Override
        public int hashCode() {
            return new HashCodeBuilder().append(StringUtils.upperCase(value)).append(termSource).toHashCode();
        }

        /*
         * (non-Javadoc)
         *
         * @see java.lang.Object#equals(java.lang.Object)
         */
        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof TermKey)) {
                return false;
            }
            if (this == obj) {
                return true;
            }
            TermKey tk = (TermKey) obj;
            return new EqualsBuilder().append(StringUtils.upperCase(this.value), StringUtils.upperCase(tk.value))
                    .append(this.termSource, tk.termSource).isEquals();
        }
    }
}
