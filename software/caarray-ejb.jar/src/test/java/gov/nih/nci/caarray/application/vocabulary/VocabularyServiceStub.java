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
package gov.nih.nci.caarray.application.vocabulary;

import edu.georgetown.pir.Organism;
import gov.nih.nci.caarray.domain.project.ExperimentOntology;
import gov.nih.nci.caarray.domain.protocol.Protocol;
import gov.nih.nci.caarray.domain.sample.AbstractCharacteristic;
import gov.nih.nci.caarray.domain.vocabulary.Category;
import gov.nih.nci.caarray.domain.vocabulary.Term;
import gov.nih.nci.caarray.domain.vocabulary.TermSource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Basic stub for tests.
 */
@SuppressWarnings("PMD.AvoidDuplicateLiterals")
public class VocabularyServiceStub implements VocabularyService {

    private long id;
    private Map<List, Protocol> protocols = new HashMap<List, Protocol>();
    private Map<List, TermSource> sources = new HashMap<List, TermSource>();

    public Set<Term> getTerms(Category category) {
        return getTerms(category, null);
    }

    public Set<Term> getTerms(Category category, String value) {
        Set<Term> terms = new HashSet<Term>();
        TermSource source = getSource(ExperimentOntology.MGED_ONTOLOGY.getOntologyName(),
                ExperimentOntology.MGED_ONTOLOGY.getVersion());
        for (int i = 0; i < 10; i++) {
            Term term = createTerm(source, category, "term" + i);
            terms.add(term);
        }
        return terms;
    }

    /**
     * {@inheritDoc}
     */
    public TermSource getSource(String name, String version) {
        List key = Arrays.asList(name, version);
        TermSource source = sources.get(key);
        if (source == null) {
            source = new TermSource();
            source.setName(name);
            source.setVersion(version);
            source.setId(id++);
            sources.put(key, source);
        }        
        return source;
    }

    /**
     * {@inheritDoc}
     */
    public TermSource getSourceByUrl(String url, String version) {
        TermSource source = new TermSource();
        source.setUrl(url);
        source.setVersion(version);
        source.setName("Name for: " + url);
        return source;
    }

    /**
     * {@inheritDoc}
     */
    public Set<TermSource> getSources(String name) {
        TermSource ts = getSource(name, null);
        Set<TermSource> result = new HashSet<TermSource>();
        result.add(ts);
        return result;
    }

    /**
     * {@inheritDoc}
     */
    public Set<TermSource> getSourcesByUrl(String url) {
        TermSource ts = getSourceByUrl(url, null);
        ts.setName("Name for: " + url);
        Set<TermSource> result = new HashSet<TermSource>();
        result.add(ts);
        return result;
    }

    public Term getTerm(TermSource source, String value) {
        Term term = new Term();
        term.setSource(source);
        term.setValue(value);
        Category cat = new Category();
        cat.setSource(source);
        cat.setName("Category for " + value);
        term.setCategory(cat);
        return term;
    }

    @SuppressWarnings("deprecation")
    public Term getTerm(Long id) {
        Term term = new Term();
        term.setId(id);
        return term;
    }

    public Organism getOrganism(Long id) {
        Organism org = new Organism();
        org.setId(id);
        return org;
    }

    public Organism getOrganism(TermSource source, String scientificName) {
        Organism org = new Organism();
        org.setTermSource(source);
        org.setScientificName(scientificName);
        return org;
    }

    public List<Organism> getOrganisms() {
        List<Organism> orgs = new ArrayList<Organism>();
        Organism o1 = new Organism();
        o1.setId(1L);
        o1.setScientificName("Mizouse");
        orgs.add(o1);
        return orgs;
    }

    public Category createCategory(TermSource source, String categoryName) {
        Category category = new Category();
        category.setSource(source);
        category.setName(categoryName);
        return category;
    }

    public Term createTerm(TermSource source, Category category, String value) {
        Term term = new Term();
        term.setSource(source);
        term.setValue(value);
        term.setCategory(category);
        return term;
    }

    public Category getCategory(TermSource source, String categoryName) {
        Category category = new Category();
        category.setName(categoryName);
        return category;
    }

    public TermSource createSource(String name, String url, String version) {
        TermSource ts = getSource(name, version);
        ts.setUrl(url);
        return ts;
    }

    public void saveTerm(Term term) {
        // do nothing
    }

    public List<TermSource> getAllSources() {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public List<Protocol> getProtocolsByProtocolType(Term type, String name) {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public Protocol getProtocol(String name, TermSource source) {
        List key = Arrays.asList(name, source);
        Protocol p = protocols.get(key);
        if (p == null) {
            p = new Protocol(name, null, source);
            p.setId(id++);
            protocols.put(key, p);
        }
        
        return p;
    }

    public Term findTermInAllTermSourceVersions(TermSource termSource, String value) {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public <T extends AbstractCharacteristic> List<Category> searchForCharacteristicCategory(
            Class<T> characteristicClass, String keyword) {
        return Collections.emptyList();
    }    

    /**
     * {@inheritDoc}
     */
    public List<Organism> searchForOrganismNames(String keyword) {
        return new ArrayList<Organism>();
    }


}












