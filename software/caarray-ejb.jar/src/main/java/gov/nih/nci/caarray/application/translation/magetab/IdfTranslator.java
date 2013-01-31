//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.application.translation.magetab;

import gov.nih.nci.caarray.dao.CaArrayDaoFactory;
import gov.nih.nci.caarray.domain.contact.Address;
import gov.nih.nci.caarray.domain.contact.Organization;
import gov.nih.nci.caarray.domain.project.Experiment;
import gov.nih.nci.caarray.domain.project.ExperimentContact;
import gov.nih.nci.caarray.domain.project.Factor;
import gov.nih.nci.caarray.domain.vocabulary.Term;
import gov.nih.nci.caarray.magetab.MageTabDocumentSet;
import gov.nih.nci.caarray.magetab.idf.ExperimentalFactor;
import gov.nih.nci.caarray.magetab.idf.IdfDocument;
import gov.nih.nci.caarray.magetab.idf.Person;
import gov.nih.nci.caarray.magetab.idf.Publication;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;

/**
 * Translates entities found in and IDF document.
 */
final class IdfTranslator extends AbstractTranslator {

    private static final int LARGE_TEXT_FIELD_LENGTH = 2000;
    private static final Logger LOG = Logger.getLogger(IdfTranslator.class);

    IdfTranslator(MageTabDocumentSet documentSet, MageTabTranslationResult translationResult,
            CaArrayDaoFactory daoFactory) {
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

    private void translate(gov.nih.nci.caarray.magetab.idf.Investigation idfInvestigation) {
        Experiment investigation = new Experiment();
        translateInvestigationSummary(idfInvestigation, investigation);
        translateTerms(idfInvestigation, investigation);
        translatePublications(idfInvestigation, investigation);
        translateFactors(idfInvestigation, investigation);
        translateContacts(idfInvestigation, investigation);
        getTranslationResult().addInvestigation(investigation);
    }

    private void translateInvestigationSummary(gov.nih.nci.caarray.magetab.idf.Investigation idfInvestigation,
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

    private void translateTerms(gov.nih.nci.caarray.magetab.idf.Investigation idfInvestigation,
            Experiment investigation) {
        investigation.getExperimentDesignTypes().addAll(getTerms(idfInvestigation.getDesigns()));
        investigation.getNormalizationTypes().addAll(getTerms(idfInvestigation.getNormalizationTypes()));
        investigation.getReplicateTypes().addAll(getTerms(idfInvestigation.getReplicateTypes()));
        investigation.getQualityControlTypes().addAll(getTerms(idfInvestigation.getQualityControlTypes()));
    }

    private void translatePublications(gov.nih.nci.caarray.magetab.idf.Investigation idfInvestigation,
            Experiment investigation) {
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

    private void translateFactors(gov.nih.nci.caarray.magetab.idf.Investigation idfInvestigation,
            Experiment investigation) {
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

    private void translateContacts(gov.nih.nci.caarray.magetab.idf.Investigation idfInvestigation,
            Experiment investigation) {
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
}
