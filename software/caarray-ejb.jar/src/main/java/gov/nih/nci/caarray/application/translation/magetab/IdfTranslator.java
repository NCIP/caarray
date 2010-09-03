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

import gov.nih.nci.caarray.dao.CaArrayDaoFactory;
import gov.nih.nci.caarray.domain.contact.Address;
import gov.nih.nci.caarray.domain.contact.Organization;
import gov.nih.nci.caarray.domain.project.Experiment;
import gov.nih.nci.caarray.domain.project.ExperimentContact;
import gov.nih.nci.caarray.domain.project.ExperimentOntology;
import gov.nih.nci.caarray.domain.project.Factor;
import gov.nih.nci.caarray.domain.vocabulary.Term;
import gov.nih.nci.caarray.magetab.MageTabDocumentSet;
import gov.nih.nci.caarray.magetab.TermSource;
import gov.nih.nci.caarray.magetab.idf.ExperimentalFactor;
import gov.nih.nci.caarray.magetab.idf.IdfDocument;
import gov.nih.nci.caarray.magetab.idf.Person;
import gov.nih.nci.caarray.magetab.idf.Publication;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.log4j.Logger;

/**
 * Translates entities found in and IDF document.
 */
final class IdfTranslator extends AbstractTranslator {

    private static final int LARGE_TEXT_FIELD_LENGTH = 2000;
    private static final Logger LOG = Logger.getLogger(IdfTranslator.class);
    private static final String UNVERSIONED_TERM_SOURCE = "*unversioned*";

    IdfTranslator(final MageTabDocumentSet documentSet, final MageTabTranslationResult translationResult,
            final CaArrayDaoFactory daoFactory) {
        super(documentSet, translationResult, daoFactory);
    }

    /**
     * Translates the investigation from each IDF document into a caArray <code>Investigation</code> entity.
     */
    @Override
    void translate() {
        for (IdfDocument idfDocument : getDocumentSet().getIdfDocuments()) {
            translate(idfDocument.getInvestigation());
        }
    }

    void validate() {
        Set<IdfDocument> idfDocumentsSet = getDocumentSet().getIdfDocuments();
        if (!idfDocumentsSet.isEmpty()) {
            IdfDocument idfDocument = idfDocumentsSet.iterator().next();
            validateTermSources(idfDocument);
            validateProtocolTypes(idfDocument);
            String investigationDescription = idfDocument.getInvestigation().getDescription();
            if (null != investigationDescription && investigationDescription.length() > LARGE_TEXT_FIELD_LENGTH) {
                idfDocument.addErrorMessage("The experiment description length of "
                        + investigationDescription.length() + " is greater than the allowed length of "
                        + LARGE_TEXT_FIELD_LENGTH + ".");
            }
        }
    }

    private void translate(final gov.nih.nci.caarray.magetab.idf.Investigation idfInvestigation) {
        Experiment investigation = new Experiment();
        translateInvestigationSummary(idfInvestigation, investigation);
        translateTerms(idfInvestigation, investigation);
        translatePublications(idfInvestigation, investigation);
        translateFactors(idfInvestigation, investigation);
        translateContacts(idfInvestigation, investigation);
        getTranslationResult().addInvestigation(investigation);
    }

    private void validateTermSources(final IdfDocument idfDocument) {
        Set<String> termSourceNamesSet = new HashSet<String>();
        Set<TermSourceKey> termSourceKeysByUrlSet = new HashSet<TermSourceKey>();
        List<TermSource> termSources = idfDocument.getDocTermSources();
        for (TermSource termSource : termSources) {
            String termSourceName = termSource.getName();
            String termSourceUrl = termSource.getFile();
            String termSourceVersion = termSource.getVersion();
            if (StringUtils.isBlank(termSourceVersion)) {
                termSourceVersion = UNVERSIONED_TERM_SOURCE;
            }
            TermSourceKey urlTermSourceKey = new TermSourceKey(termSourceUrl, termSourceVersion);
            if (termSourceNamesSet.contains(termSourceName)) { // cannot have same name
                idfDocument.addErrorMessage("Duplicate term source name '" + termSourceName + "'.");
            } else {
                termSourceNamesSet.add(termSourceName);
            }
            if (termSourceKeysByUrlSet.contains(urlTermSourceKey)) { // cannot have same URL *and* version
                idfDocument.addErrorMessage("Redundant term source named '" + termSourceName
                    + "'. Term sources cannot have the same URL unless they have different "
                    + "versions, even if their names are different.");
            } else {
                termSourceKeysByUrlSet.add(urlTermSourceKey);
            }
        }
    }
    
    private void validateProtocolTypes(final IdfDocument idfDocument) {
        for (gov.nih.nci.caarray.magetab.Protocol protocol : idfDocument.getInvestigation().getProtocols()) {
            if (null != protocol.getType() && null != protocol.getType().getTermSource()
                    && !ExperimentOntology.MGED_ONTOLOGY.getOntologyName().equals(
                            protocol.getType().getTermSource().getName())) {
                idfDocument.addErrorMessage("The Protocol Type '" + protocol.getType().getValue()
                        + "'is associated with an invalid Term Source REF '"
                        + protocol.getType().getTermSource().getName() + "'. All Protocol Types must come from the '"
                        + ExperimentOntology.MGED_ONTOLOGY.getOntologyName() + "' Term Source.");
            }
        }
    }

    private void translateInvestigationSummary(final gov.nih.nci.caarray.magetab.idf.Investigation idfInvestigation,
            Experiment investigation) {
        investigation.setTitle(idfInvestigation.getTitle());
        // WC: temporary fix.  this should be constrained on the db.
        String desc = idfInvestigation.getDescription();
        if (desc != null && desc.length() > LARGE_TEXT_FIELD_LENGTH) {
            desc = desc.substring(0, LARGE_TEXT_FIELD_LENGTH);
        }
        investigation.setDescription(desc);
        investigation.setDate(idfInvestigation.getDateOfExperiment());
        investigation.setPublicReleaseDate(idfInvestigation.getPublicReleaseDate());
    }

    private void translateTerms(final gov.nih.nci.caarray.magetab.idf.Investigation idfInvestigation,
            final Experiment investigation) {
        investigation.getExperimentDesignTypes().addAll(getTerms(idfInvestigation.getDesigns()));
        investigation.getNormalizationTypes().addAll(getTerms(idfInvestigation.getNormalizationTypes()));
        investigation.getReplicateTypes().addAll(getTerms(idfInvestigation.getReplicateTypes()));
        investigation.getQualityControlTypes().addAll(getTerms(idfInvestigation.getQualityControlTypes()));
    }

    private void translatePublications(final gov.nih.nci.caarray.magetab.idf.Investigation idfInvestigation,
            final Experiment investigation) {
        List<gov.nih.nci.caarray.domain.publication.Publication> publications =
            new ArrayList<gov.nih.nci.caarray.domain.publication.Publication>();
        List<Publication> idfPublications = idfInvestigation.getPublications();
        Iterator<Publication> iterator = idfPublications.iterator();
        while (iterator.hasNext()) {
            Publication idfPublication = iterator.next();
            gov.nih.nci.caarray.domain.publication.Publication publication =
                new gov.nih.nci.caarray.domain.publication.Publication();
            publication.setTitle(idfPublication.getTitle());
            publication.setAuthors(idfPublication.getAuthorList());
            publication.setDoi(idfPublication.getDoi());
            publication.setPubMedId(idfPublication.getPubMedId());
            Term statusTerm = getTerm(idfPublication.getStatus());
            publication.setStatus(statusTerm);
            publications.add(publication);
        }
        investigation.getPublications().addAll(publications);
    }

    private void translateFactors(final gov.nih.nci.caarray.magetab.idf.Investigation idfInvestigation,
            final Experiment investigation) {
        List<Factor> factors = new ArrayList<Factor>();
        List<ExperimentalFactor> idfFactors = idfInvestigation.getFactors();
        Iterator<ExperimentalFactor> iterator = idfFactors.iterator();
        while (iterator.hasNext()) {
            ExperimentalFactor idfFactor = iterator.next();
            Factor factor = new Factor();
            factor.setName(idfFactor.getName());
            Term typeTerm = getTerm(idfFactor.getType());
            factor.setType(typeTerm);
            factors.add(factor);
            getTranslationResult().addFactor(idfFactor, factor);
        }
        investigation.getFactors().addAll(factors);
    }

    private void translateContacts(final gov.nih.nci.caarray.magetab.idf.Investigation idfInvestigation,
            final Experiment investigation) {
        List<ExperimentContact> contacts = new ArrayList<ExperimentContact>();
        List<Person> idfPersons = idfInvestigation.getPersons();
        Iterator<Person> iterator = idfPersons.iterator();
        while (iterator.hasNext()) {
            Person idfPerson = iterator.next();
            gov.nih.nci.caarray.domain.contact.Person person = new gov.nih.nci.caarray.domain.contact.Person();
            person.setFirstName(idfPerson.getFirstName());
            person.setLastName(idfPerson.getLastName());
            person.setMiddleInitials(idfPerson.getMidInitials());
            Organization affiliatedOrg = getOrCreateOrganization(idfPerson.getAffiliation());
            if (affiliatedOrg != null) {
                person.getAffiliations().add(affiliatedOrg);
            }
            person.setEmail(idfPerson.getEmail());
            person.setFax(idfPerson.getFax());
            person.setPhone(idfPerson.getPhone());
            Address address = new Address();
            // TODO Parse the address before putting it in the Address object.
            address.setStreet1(idfPerson.getAddress());
            person.setAddress(address);
            ExperimentContact contact = new ExperimentContact();
            contact.setContact(person);
            Collection<Term> roleTerms = getTerms(idfPerson.getRoles());
            contact.getRoles().addAll(roleTerms);
            contacts.add(contact);
        }
        investigation.getExperimentContacts().addAll(contacts);
    }

    @Override
    Logger getLog() {
        return LOG;
    }

    /**
     * Key class for looking up term sources in the cache by the Term Source natural keys.
     */
    private static final class TermSourceKey {

        private final String uniqueKey;
        private final String version;

        public TermSourceKey(final String uniqueKey, final String version) {
            this.uniqueKey = uniqueKey;
            this.version = version;
        }

        /**
         * @return the name
         */
        public String getUniqueKey() {
            return uniqueKey;
        }

        /**
         * @return the version
         */
        public String getVersion() {
            return version;
        }

        /*
         * (non-Javadoc)
         *
         * @see java.lang.Object#hashCode()
         */
        @Override
        public int hashCode() {
            return HashCodeBuilder.reflectionHashCode(this);
        }

        /*
         * (non-Javadoc)
         *
         * @see java.lang.Object#equals(java.lang.Object)
         */
        @Override
        public boolean equals(final Object obj) {
            return EqualsBuilder.reflectionEquals(this, obj);
        }
    }
}
