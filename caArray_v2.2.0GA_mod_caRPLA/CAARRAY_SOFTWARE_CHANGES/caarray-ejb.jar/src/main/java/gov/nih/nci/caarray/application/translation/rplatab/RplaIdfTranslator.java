//======================================================================================
// Copyright 5AM Solutions Inc, Yale University, Lawrence Berkeley National Laboratory
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/caRPLA/LICENSE for details.
//======================================================================================
package gov.nih.nci.caarray.application.translation.rplatab;

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
import gov.nih.nci.carpla.domain.antibody.Antibody;
import gov.nih.nci.carpla.rplatab.RplaTabDocumentSet;
import gov.nih.nci.carpla.rplatab.files.RplaIdfFile;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

public class RplaIdfTranslator extends RplaTabAbstractTranslator {

	private static final int	LARGE_TEXT_FIELD_LENGTH	= 2000;
	private static final Logger	LOG						= Logger.getLogger(RplaIdfTranslator.class);

	// ###########################################################
	RplaIdfTranslator(	RplaTabDocumentSet documentSet,
						RplaTabTranslationResult translationResult,
						CaArrayDaoFactory daoFactory) {
		super(documentSet, translationResult, daoFactory);
	}

	/**
	 * Translates the investigation from each IDF document into a caArray
	 * <code>Investigation</code> entity.
	 */
	// ###########################################################
	@Override
	void translate () {

		// for (RplaIdfFile rplaIdfDocument : getDocumentSet().getRplaIdfFile())
		// {
		// translate(idfDocument.getInvestigation());
		// }

		translate(getDocumentSet());

	}

	// ###########################################################
	private void translate ( RplaTabDocumentSet rset) {
		// Experiment investigation = new Experiment();
		// translateInvestigationSummary(idfInvestigation, investigation);
		// translateTerms(idfInvestigation, investigation);
		// translatePublications(idfInvestigation, investigation);
		// translateFactors(idfInvestigation, investigation);
		// translateContacts(idfInvestigation, investigation);
		// getTranslationResult().addInvestigation(investigation);

		Experiment investigation = new Experiment();
		translateInvestigationSummary(rset, investigation);
		translateTerms(rset, investigation);
		translateAntibodies(rset, investigation);
		translatePublications(rset, investigation);
		translateFactors(rset, investigation);
		translateContacts(rset, investigation);
		getTranslationResult().addInvestigation(investigation);

	}

	private void translateAntibodies (	RplaTabDocumentSet rset,
										Experiment investigation)
	{

		investigation.getAntibodies().addAll(getAntibodies(rset));
	}

	// ###########################################################
	// private void translateInvestigationSummary (
	// gov.nih.nci.caarray.magetab.idf.Investigation idfInvestigation,
	// Experiment investigation)

	private void translateInvestigationSummary (	RplaTabDocumentSet rset,
													Experiment investigation)

	{
		// investigation.setTitle(idfInvestigation.getTitle());
		// // WC: temporary fix. this should be constrained on the db.
		// String desc = idfInvestigation.getDescription();
		// if (desc.length() > LARGE_TEXT_FIELD_LENGTH) {
		// desc = desc.substring(0, LARGE_TEXT_FIELD_LENGTH);
		// }
		// investigation.setDescription(desc);
		// investigation.setDateOfExperiment(idfInvestigation.getDateOfExperiment());
		// investigation.setPublicReleaseDate(idfInvestigation.getPublicReleaseDate());
		//		

		investigation.setTitle(rset.getInvestigationTitle());
		// WC: temporary fix. this should be constrained on the db.
		String desc = rset.getDescription();
		if (desc.length() > LARGE_TEXT_FIELD_LENGTH) {
			desc = desc.substring(0, LARGE_TEXT_FIELD_LENGTH);
		}
		investigation.setDescription(desc);
		investigation.setDate(rset.getDateOfExperiment());
		investigation.setPublicReleaseDate(rset.getPublicReleaseDate());

	}

	// ###########################################################
	// private void translateTerms (
	// gov.nih.nci.caarray.magetab.idf.Investigation idfInvestigation,
	// Experiment investigation)

	private void translateTerms (	RplaTabDocumentSet rset,
									Experiment investigation)

	{
		// investigation .getExperimentDesignTypes()
		// .addAll(getTerms(idfInvestigation.getDesigns()));
		// investigation .getNormalizationTypes()
		// .addAll(getTerms(idfInvestigation.getNormalizationTypes()));
		// investigation .getReplicateTypes()
		// .addAll(getTerms(idfInvestigation.getReplicateTypes()));
		// investigation .getQualityControlTypes()
		// .addAll(getTerms(idfInvestigation.getQualityControlTypes()));

		investigation	.getExperimentDesignTypes()
						.addAll(getTerms(rset.getExperimentalDesigns()));

		LOG.info("carplatodo");

		// investigation .getNormalizationTypes()
		// .addAll(getTerms(rset.getNormalizationTypes()));
		// investigation .getReplicateTypes()
		// .addAll(getTerms(rset.getReplicateTypes()));
		// investigation .getQualityControlTypes()
		// .addAll(getTerms(rset.getQualityControlTypes()));

	}

	// ###########################################################
	// private void translatePublications (
	// gov.nih.nci.caarray.magetab.idf.Investigation idfInvestigation,
	// Experiment investigation)

	private void translatePublications (	RplaTabDocumentSet rset,
											Experiment investigation)

	{
		// List<gov.nih.nci.caarray.domain.publication.Publication> publications
		// = new
		// ArrayList<gov.nih.nci.caarray.domain.publication.Publication>();
		// List<Publication> idfPublications =
		// idfInvestigation.getPublications();
		// Iterator<Publication> iterator = idfPublications.iterator();
		// while (iterator.hasNext()) {
		// Publication idfPublication = iterator.next();
		// gov.nih.nci.caarray.domain.publication.Publication publication = new
		// gov.nih.nci.caarray.domain.publication.Publication();
		// publication.setTitle(idfPublication.getTitle());
		// publication.setAuthors(idfPublication.getAuthorList());
		// publication.setDoi(idfPublication.getDoi());
		// publication.setPubMedId(idfPublication.getPubMedId());
		// Term statusTerm = getTerm(idfPublication.getStatus());
		// publication.setStatus(statusTerm);
		// publications.add(publication);
		// }
		// investigation.getPublications().addAll(publications);

		List<gov.nih.nci.caarray.domain.publication.Publication> publications = new ArrayList<gov.nih.nci.caarray.domain.publication.Publication>();
		List<Publication> idfPublications = rset.getPublications();
		Iterator<Publication> iterator = idfPublications.iterator();
		while (iterator.hasNext()) {
			Publication idfPublication = iterator.next();
			gov.nih.nci.caarray.domain.publication.Publication publication = new gov.nih.nci.caarray.domain.publication.Publication();
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

	private Collection<Antibody> getAntibodies ( RplaTabDocumentSet rset)

	{
		Set<Antibody> abset = new HashSet<Antibody>();
		for (gov.nih.nci.carpla.rplatab.model.Antibody ab : rset.getAntibodies()) {

			Antibody domain_ab = new Antibody();
			domain_ab.setName(ab.getName());
			domain_ab.setCatalogId(ab.getCatalogId());
			domain_ab.setComment(ab.getComment());
			domain_ab.setEpitope(ab.getEpitope());
			domain_ab.setImmunogen(ab.getImmunogen());
			domain_ab.setLotId(ab.getLotId());
			abset.add(domain_ab);
		}
		return abset;
	}

	// ###########################################################
	// private void translateFactors (
	// gov.nih.nci.caarray.magetab.idf.Investigation idfInvestigation,
	// Experiment investigation)
	//	
	private void translateFactors ( RplaTabDocumentSet rset,
									Experiment investigation)

	{
		// List<Factor> factors = new ArrayList<Factor>();
		// List<ExperimentalFactor> idfFactors = idfInvestigation.getFactors();
		// Iterator<ExperimentalFactor> iterator = idfFactors.iterator();
		// while (iterator.hasNext()) {
		// ExperimentalFactor idfFactor = iterator.next();
		// Factor factor = new Factor();
		// factor.setName(idfFactor.getName());
		// Term typeTerm = getTerm(idfFactor.getType());
		// factor.setType(typeTerm);
		// factors.add(factor);
		// getTranslationResult().addFactor(idfFactor, factor);
		// }
		// investigation.getFactors().addAll(factors);

		List<Factor> factors = new ArrayList<Factor>();
		Map<String, ExperimentalFactor> expFactors = rset.getExperimentalFactors();

		Iterator<ExperimentalFactor> itie = expFactors.values().iterator();
		while (itie.hasNext()) {
			ExperimentalFactor idfFactor = itie.next();
			Factor factor = new Factor();
			factor.setName(idfFactor.getName());
			LOG.info("about to set factor with type=" + idfFactor	.getType()
																	.getValue());
			Term typeTerm = getTerm(idfFactor.getType());
			LOG.info("typeTerm=" + typeTerm.getValue());
			factor.setType(typeTerm);
			factors.add(factor);
			getTranslationResult().addFactor(idfFactor, factor);
		}
		investigation.getFactors().addAll(factors);

	}

	// ###########################################################
	// private void translateContacts (
	// gov.nih.nci.caarray.magetab.idf.Investigation idfInvestigation,
	// Experiment investigation)

	private void translateContacts (	RplaTabDocumentSet rset,
										Experiment investigation)

	{
		// List<ExperimentContact> contacts = new
		// ArrayList<ExperimentContact>();
		// List<Person> idfPersons = idfInvestigation.getPersons();
		// Iterator<Person> iterator = idfPersons.iterator();
		// while (iterator.hasNext()) {
		// Person idfPerson = iterator.next();
		// gov.nih.nci.caarray.domain.contact.Person person = new
		// gov.nih.nci.caarray.domain.contact.Person();
		// person.setFirstName(idfPerson.getFirstName());
		// person.setLastName(idfPerson.getLastName());
		// person.setMiddleInitials(idfPerson.getMidInitials());
		// Organization affiliatedOrg =
		// getOrCreateOrganization(idfPerson.getAffiliation());
		// if (affiliatedOrg != null) {
		// person.getAffiliations().add(affiliatedOrg);
		// }
		// person.setEmail(idfPerson.getEmail());
		// person.setFax(idfPerson.getFax());
		// person.setPhone(idfPerson.getPhone());
		// Address address = new Address();
		// // TODO Parse the address before putting it in the Address object.
		// address.setStreet1(idfPerson.getAddress());
		// person.setAddress(address);
		// ExperimentContact contact = new ExperimentContact();
		// contact.setContact(person);
		// Collection<Term> roleTerms = getTerms(idfPerson.getRoles());
		// contact.getRoles().addAll(roleTerms);
		// contacts.add(contact);
		// }
		// investigation.getExperimentContacts().addAll(contacts);

		LOG.info("carplatodo contacts");
		// List<ExperimentContact> contacts = new
		// ArrayList<ExperimentContact>();
		// List<Person> idfPersons = rset.getPersons();
		// Iterator<Person> iterator = idfPersons.iterator();
		// while (iterator.hasNext()) {
		// Person idfPerson = iterator.next();
		// gov.nih.nci.caarray.domain.contact.Person person = new
		// gov.nih.nci.caarray.domain.contact.Person();
		// person.setFirstName(idfPerson.getFirstName());
		// person.setLastName(idfPerson.getLastName());
		// person.setMiddleInitials(idfPerson.getMidInitials());
		// Organization affiliatedOrg =
		// getOrCreateOrganization(idfPerson.getAffiliation());
		// if (affiliatedOrg != null) {
		// person.getAffiliations().add(affiliatedOrg);
		// }
		// person.setEmail(idfPerson.getEmail());
		// person.setFax(idfPerson.getFax());
		// person.setPhone(idfPerson.getPhone());
		// Address address = new Address();
		// // TODO Parse the address before putting it in the Address object.
		// address.setStreet1(idfPerson.getAddress());
		// person.setAddress(address);
		// ExperimentContact contact = new ExperimentContact();
		// contact.setContact(person);
		// Collection<Term> roleTerms = getTerms(idfPerson.getRoles());
		// contact.getRoles().addAll(roleTerms);
		// contacts.add(contact);
		// }
		// investigation.getExperimentContacts().addAll(contacts);

	}

	@Override
	Logger getLog () {
		return LOG;
	}
}
