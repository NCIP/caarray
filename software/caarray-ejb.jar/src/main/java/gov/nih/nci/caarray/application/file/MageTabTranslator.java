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
/**
 * Copyright 2007 NCICB. This software was developed in conjunction with the National
 * Cancer Institute, and so to the extent government employees are co-authors, any
 * rights in such works shall be subject to Title 17 of the United States Code,
 * section 105.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the disclaimer of Article 3, below. Redistributions in
 * binary form must reproduce the above copyright notice, this list of conditions
 * and the following disclaimer in the documentation and/or other materials
 * provided with the distribution.
 *
 * 2. Affymetrix Pure Java run time library needs to be downloaded from
 * (http://www.affymetrix.com/support/developer/runtime_libraries/index.affx)
 * after agreeing to the licensing terms from the Affymetrix.
 *
 * 3. The end-user documentation included with the redistribution, if any, must
 * include the following acknowledgment:
 *
 * "This product includes software developed by 5AM Solutions (5AM)
 * and the National Cancer Institute (NCI)."
 *
 * If no such end-user documentation is to be included, this acknowledgment shall
 * appear in the software itself, wherever such third-party acknowledgments
 * normally appear.
 *
 * 4. The names "The National Cancer Institute", "NCI", and "5AM" must not be used to
 * endorse or promote products derived from this software.
 *
 * 5. This license does not authorize the incorporation of this software into any
 * proprietary programs. This license does not authorize the recipient to use any
 * trademarks owned by either NCI or 5AM.
 *
 * 6. THIS SOFTWARE IS PROVIDED "AS IS," AND ANY EXPRESSED OR IMPLIED WARRANTIES,
 * (INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE) ARE DISCLAIMED. IN NO EVENT SHALL THE NATIONAL
 * CANCER INSTITUTE, SAIC, OR THEIR AFFILIATES BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS;
 * OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY
 * OF SUCH DAMAGE.
 */

package gov.nih.nci.caarray.application.file;

import gov.nih.nci.caarray.dao.CaArrayDaoFactory;
import gov.nih.nci.caarray.dao.DAOException;
import gov.nih.nci.caarray.dao.VocabularyDao;
import gov.nih.nci.caarray.domain.AbstractCaArrayEntity;
import gov.nih.nci.caarray.domain.contact.Address;
import gov.nih.nci.caarray.domain.contact.Organization;
import gov.nih.nci.caarray.domain.project.Factor;
import gov.nih.nci.caarray.domain.project.Investigation;
import gov.nih.nci.caarray.domain.project.InvestigationContact;
import gov.nih.nci.caarray.domain.vocabulary.Source;
import gov.nih.nci.caarray.domain.vocabulary.Term;
import gov.nih.nci.caarray.magetab.ExperimentalFactor;
import gov.nih.nci.caarray.magetab.IdfDocument;
import gov.nih.nci.caarray.magetab.Person;
import gov.nih.nci.caarray.magetab.Publication;
import gov.nih.nci.caarray.magetab.Semantic;
import gov.nih.nci.caarray.magetab.TermSource;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Translates a set of MAGE-TAB objects into caArray domain objects.
 * It currently works on IdfDocument objects, but will eventually take
 * an object graph of all transient MAGE-TAB objects.
 *
 * @author Rashmi Srinivasa
 */
public final class MageTabTranslator {
    private static final Log LOG = LogFactory.getLog(MageTabTranslator.class);

    private final IdfDocument document;

    // Getting Vocabulary DAO because there is no generic DAO that lets you do deep-query by example.
    private static final VocabularyDao DAO_OBJECT = CaArrayDaoFactory.INSTANCE.getVocabularyDao();

    /**
     * Instantiates a translator with an <code>IdfDocument</code> to translate.
     *
     * @param idfDoc the MAGE-TAB objects in the IDF document.
     */
    public MageTabTranslator(IdfDocument idfDoc) {
        document = idfDoc;
    }

    /**
     * Takes an <code>IdfDocument</code> and converts the entities contained
     * in it into a list of caArray entities which will eventually be persisted.
     *
     * @return the List of caArray entities contained in the IDF document.
     */
    @SuppressWarnings("unchecked")
    public List<AbstractCaArrayEntity> getCaArrayEntities() {
        Investigation investigation = translateInvestigationSummary();
        List<Source> sources = translateSources();
        List<gov.nih.nci.caarray.domain.publication.Publication> publications = translatePublications();
        investigation.getPublications().addAll(publications);
        List<Factor> factors = translateFactors();
        investigation.getFactors().addAll(factors);

        //TODO Where are experimental designs modelled in the caArray domain model?
        //List<Term> experimentalDesigns = document.getExperimentalDesigns();
        List<InvestigationContact> contacts = translatePersons();
        investigation.getInvestigationContacts().addAll(contacts);
        List<Term> replicateTypes = translateTypes(document.getReplicates());
        investigation.getReplicateTypes().addAll(replicateTypes);
        List<Term> normalizationTypes = translateTypes(document.getNormalizations());
        investigation.getNormalizationTypes().addAll(normalizationTypes);
        List<Term> qualityControlTypes = translateTypes(document.getQualityControls());
        investigation.getQualityControlTypes().addAll(qualityControlTypes);
        //TODO Decide what to do with "Comment" elements in MAGE-TAB.

        List<AbstractCaArrayEntity> caArrayEntityList = new ArrayList<AbstractCaArrayEntity>();
        caArrayEntityList.add(investigation);
        //TODO Should we return Term Sources, Protocols etc. which are not referenced by the experiment?
        caArrayEntityList.addAll(sources);
        return caArrayEntityList;
    }

    /**
     * Reads the investigation summary information from the <code>IdfDocument</code>
     * and stores it into the caArray <code>Investigation</code> entity.
     *
     * @return a caArray <code>Investigation</code> entity with summary information.
     */
    private Investigation translateInvestigationSummary() {
        Investigation investigation = new Investigation();
        investigation.setTitle(document.getTitle());
        investigation.setDescription(document.getExperimentDescription());
        investigation.setDateOfExperiment(document.getExperimentDate());
        investigation.setPublicReleaseDate(document.getPublicReleaseDate());
        return investigation;
    }

    /**
     * Reads the term source information from the <code>IdfDocument</code>
     * and stores it into caArray <code>Source</code> entities.
     *
     * @return the List of caArray term sources contained in the IDF document.
    */
   private List<Source> translateSources() {
       List<Source> sources = new ArrayList<Source>();
       List<TermSource> idfSources = document.getTermSources();
       Iterator<TermSource> iterator = idfSources.iterator();
       while (iterator.hasNext()) {
           TermSource idfSource = iterator.next();
           Source source = new Source();
           source.setName(idfSource.getName());
           source.setUrl(idfSource.getFile().toString());
           source = (Source) replaceIfExists(source);
           sources.add(source);
       }
       return sources;
   }

   /**
     * Reads the term source information from the <code>IdfDocument</code> and stores it into caArray
     * <code>Source</code> entities.
     *
     * @return the List of caArray publications contained in the IDF document.
     */
    private List<gov.nih.nci.caarray.domain.publication.Publication> translatePublications() {
        List<gov.nih.nci.caarray.domain.publication.Publication> publications =
            new ArrayList<gov.nih.nci.caarray.domain.publication.Publication>();
        List<Publication> idfPublications = document.getPublications();
        Iterator<Publication> iterator = idfPublications.iterator();
        while (iterator.hasNext()) {
            Publication idfPublication = iterator.next();
            gov.nih.nci.caarray.domain.publication.Publication publication =
                new gov.nih.nci.caarray.domain.publication.Publication();
            publication.setTitle(idfPublication.getTitle());
            publication.setAuthors(idfPublication.getAuthorList());
            publication.setDoi(idfPublication.getDoi());
            publication.setPubMedId(Integer.toString(idfPublication.getPubmedId()));
            Term statusTerm = semanticToTerm(idfPublication.getStatus());
            publication.setStatus(statusTerm);
            publication = (gov.nih.nci.caarray.domain.publication.Publication) replaceIfExists(publication);
            publications.add(publication);
        }
        return publications;
    }

    /**
     * Reads the experimental factors information from the <code>IdfDocument</code> and stores it into caArray
     * <code>Factor</code> entities.
     *
     * @return the List of caArray experimental factors contained in the IDF document.
     */
    private List<Factor> translateFactors() {
        List<Factor> factors = new ArrayList<Factor>();
        List<ExperimentalFactor> idfFactors = document.getExperimentalFactors();
        Iterator<ExperimentalFactor> iterator = idfFactors.iterator();
        while (iterator.hasNext()) {
            ExperimentalFactor idfFactor = iterator.next();
            Factor factor = new Factor();
            factor.setName(idfFactor.getName());
            Term typeTerm = semanticToTerm(idfFactor.getType());
            factor.setType(typeTerm);
            factors.add(factor);
        }
        return factors;
    }

    /**
     * Reads the persons from the <code>IdfDocument</code> and stores it into caArray
     * <code>InvestigationContact</code> entities.
     *
     * @return the List of caArray investigation contacts contained in the IDF document.
     */
    @SuppressWarnings("unchecked")
    private List<InvestigationContact> translatePersons() {
        List<InvestigationContact> contacts = new ArrayList<InvestigationContact>();
        List<Person> idfPersons = document.getPersons();
        Iterator<Person> iterator = idfPersons.iterator();
        while (iterator.hasNext()) {
            Person idfPerson = iterator.next();
            gov.nih.nci.caarray.domain.contact.Person person = new gov.nih.nci.caarray.domain.contact.Person();
            person.setFirstName(idfPerson.getFirstName());
            person.setLastName(idfPerson.getLastName());
            person.setMiddleInitials(idfPerson.getMiddleInit());
            Organization affiliatedOrg = new Organization();
            affiliatedOrg.setName(idfPerson.getAffiliation());
            person.getAffiliations().add(affiliatedOrg);
            person.setEmail(idfPerson.getEmail());
            person.setFax(idfPerson.getFax());
            person.setPhone(idfPerson.getPhone());
            Address address = new Address();
            // TODO Do some address parsing before putting it in the Address object.
            address.setStreetAddress1(idfPerson.getAddress());
            person.setAddress(address);
            person = (gov.nih.nci.caarray.domain.contact.Person) replaceIfExists(person);
            InvestigationContact contact = new InvestigationContact();
            contact.setContact(person);
            Term roleTerm = semanticToTerm(idfPerson.getRole());
            contact.getRoles().add(roleTerm);
            contacts.add(contact);
        }
        return contacts;
    }

    /**
     * Reads the types (replicate types, normalization types or quality control types)
     * from the <code>IdfDocument</code> and stores it into caArray <code>Term</code>
     * entities.
     *
     * @param idfTypes the List of types (replicate etc.) in the IDF document.
     * @return the List of caArray type Terms contained in the IDF document.
     */
    private List<Term> translateTypes(List<Semantic> idfTypes) {
        List<Term> types = new ArrayList<Term>();
        Iterator<Semantic> iterator = idfTypes.iterator();
        while (iterator.hasNext()) {
            Semantic idfType = iterator.next();
            Term typeTerm = semanticToTerm(idfType);
            types.add(typeTerm);
        }
        return types;
    }

    /**
     * Converts an IDF <code>Semantic</code> to a caArray <code>Term</code>.
     *
     * @param idfSemantic an IDF <code>Semantic</code> (a term and its source).
     * @return a caArray <code>Term</code> object.
     */
    private Term semanticToTerm(Semantic idfSemantic) {
        TermSource idfSource = idfSemantic.getSource();
        Source source = new Source();
        source.setName(idfSource.getName());
        source.setUrl(idfSource.getFile().toString());
        Term term = idfSemantic.getType();
        term.setSource(source);
        term = (Term) replaceIfExists(term);
        return term;
    }

    /**
     * Checks database to see if a matching caArray entity already exists.
     * If a matching entity exists, it is returned. If no match is found, or if there
     * is an error while searching the database, the new entity is returned
     * without any modification. Searches database for attributes and one level of associations.
     *
     * @param entityToMatch the caArray entity to match.
     * @return a matching caArray that already exists in the database.
     */
    private AbstractCaArrayEntity replaceIfExists(AbstractCaArrayEntity entityToMatch) {
        try {
        List<AbstractCaArrayEntity> matchingEntities =
            DAO_OBJECT.queryEntityAndAssociationsByExample(entityToMatch);
        if (matchingEntities.size() == 1) {
            // Exactly one match; use existing object in database.
            return matchingEntities.get(0);
        } else {
            // Either no matches, or ambiguous match; return original entity.
            return entityToMatch;
        }
        } catch (DAOException e) {
            LOG.error("Error while searching database.", e);
        }

        // Error searching database; return original entity.
        return entityToMatch;
    }
}
