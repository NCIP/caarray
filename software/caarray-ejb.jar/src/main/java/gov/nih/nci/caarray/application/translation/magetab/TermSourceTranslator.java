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
import gov.nih.nci.caarray.domain.vocabulary.TermSource;
import gov.nih.nci.caarray.magetab.MageTabDocumentSet;

import java.util.Comparator;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

/**
 * Translates MAGE-TAB <code>TermSources</code> to caArray <code>TermSources</code>.
 */
@SuppressWarnings("PMD.CyclomaticComplexity")
final class TermSourceTranslator extends AbstractTranslator {

    private static final Logger LOG = Logger.getLogger(TermSourceTranslator.class);

    private final VocabularyService vocabularyService;

    TermSourceTranslator(MageTabDocumentSet documentSet, MageTabTranslationResult translationResult,
            VocabularyService vocabularyService, CaArrayDaoFactory daoFatory) {
        super(documentSet, translationResult, daoFatory);
        this.vocabularyService = vocabularyService;
    }

    @Override
    void translate() {
        for (gov.nih.nci.caarray.magetab.TermSource termSource : getDocumentSet().getTermSources()) {
            translate(termSource);
        }
    }

    private void translate(gov.nih.nci.caarray.magetab.TermSource termSource) {
        TermSource source = lookupSource(termSource);
        getTranslationResult().addSource(termSource, source);
    }

    private TermSource lookupSource(gov.nih.nci.caarray.magetab.TermSource termSource) {
        boolean hasFile = termSource.getFile() != null;
        boolean hasVersion = termSource.getVersion() != null;
        if (hasFile && hasVersion) {
            return lookupSourceByNameUrlAndVersion(termSource.getName(), termSource.getFile(), termSource
                    .getVersion());
        } else if (hasFile && !hasVersion) {
            return lookupSourceByUrl(termSource.getName(), termSource.getFile());
        } else if (!hasFile && hasVersion) {
            return lookupSourceByNameAndVersion(termSource.getName(), termSource.getVersion());
        } else {
            return lookupSourceByNameOnly(termSource.getName());
        }
    }

    /**
     * @param name
     * @param file
     * @param version
     * @return
     */
    private TermSource lookupSourceByNameUrlAndVersion(String name, String url, String version) {
        TermSource match = vocabularyService.getSourceByUrl(url, version);
        if (match != null) {
            if (null == match.getName()) {
                match.setName(name);
            }
            return match;
        } else {
            TermSource result = lookupSourceByNameAndVersion(name, version);
            result.setUrl(url);
            return result;
        }
    }

    /**
     * @param name
     * @param file
     * @return
     */
    private TermSource lookupSourceByUrl(String name, String url) {
        Set<TermSource> matches = vocabularyService.getSourcesByUrl(url);
        if (!matches.isEmpty()) {
            return getBestMatch(matches);
        } else {
            TermSource newSource = new TermSource();
            newSource.setName(name);
            newSource.setUrl(url);
            return newSource;
        }
    }

    /**
     * @param name
     * @param version
     * @return
     */
    private TermSource lookupSourceByNameAndVersion(String name, String version) {
        TermSource match = vocabularyService.getSource(name, version);
        if (match != null) {
            return match;
        } else {
            TermSource newSource = new TermSource();
            newSource.setName(name);
            newSource.setVersion(version);
            return newSource;
        }
    }

    /**
     * @param name
     * @return
     */
    private TermSource lookupSourceByNameOnly(String name) {
        Set<TermSource> matches = vocabularyService.getSources(name);
        if (!matches.isEmpty()) {
            return getBestMatch(matches);
        } else {
            TermSource newSource = new TermSource();
            newSource.setName(name);
            return newSource;
        }
    }

    /**
     * @param matches
     * @return
     */
    private TermSource getBestMatch(Set<TermSource> matches) {
        TreeSet<TermSource> sorted = new TreeSet<TermSource>(new TermSourceVersionComparator());
        sorted.addAll(matches);
        return sorted.first();
    }

    @Override
    Logger getLog() {
        return LOG;
    }

    /**
     * Compares term sources by their version, such that empty / null versions come first, and otherwise uses inverse
     * alphabetical ordering.
     *
     * @author dkokotov@vecna.com
     */
    private static class TermSourceVersionComparator implements Comparator<TermSource> {
        /**
         * {@inheritDoc}
         */
        public int compare(TermSource ts1, TermSource ts2) {
            if (StringUtils.isEmpty(ts1.getVersion())) {
                return StringUtils.isEmpty(ts2.getVersion()) ? 0 : -1;
            }
            if (StringUtils.isEmpty(ts2.getVersion())) {
                return 1;
            }
            return ts1.getVersion().compareToIgnoreCase(ts2.getVersion()) * -1;
        }
    }
}
