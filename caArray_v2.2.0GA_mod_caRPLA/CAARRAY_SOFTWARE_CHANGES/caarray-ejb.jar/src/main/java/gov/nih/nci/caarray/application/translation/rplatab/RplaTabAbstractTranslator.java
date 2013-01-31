//======================================================================================
// Copyright 5AM Solutions Inc, Yale University, Lawrence Berkeley National Laboratory
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/caRPLA/LICENSE for details.
//======================================================================================
package gov.nih.nci.caarray.application.translation.rplatab;

import gov.nih.nci.caarray.dao.CaArrayDaoFactory;
import gov.nih.nci.caarray.dao.ProjectDao;
import gov.nih.nci.caarray.domain.contact.Organization;
import gov.nih.nci.caarray.domain.file.CaArrayFile;
import gov.nih.nci.caarray.domain.file.CaArrayFileSet;
import gov.nih.nci.caarray.domain.vocabulary.Term;
import gov.nih.nci.caarray.magetab.MageTabDocumentSet;
import gov.nih.nci.caarray.magetab.OntologyTerm;
import gov.nih.nci.carpla.rplatab.RplaTabDocumentSet;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

// carplanotes
// I can't use AbstractTranslator as it uses MageTabDocumentSet, and
// MageTabTranslationResult
// No changes to code except for replacing those classes named above.

abstract class RplaTabAbstractTranslator {

	private final RplaTabDocumentSet		documentSet;
	private final CaArrayFileSet			fileSet;
	private final RplaTabTranslationResult	translationResult;
	private final CaArrayDaoFactory			daoFactory;
	private final Map<String, Organization>	importedOrganizations	= new HashMap<String, Organization>();

	private static final Logger				LOG						= Logger.getLogger(RplaTabAbstractTranslator.class);

	RplaTabAbstractTranslator(	RplaTabDocumentSet documentSet,
								CaArrayFileSet fileSet,
								RplaTabTranslationResult translationResult,
								CaArrayDaoFactory daoFactory) {
		this.documentSet = documentSet;
		this.fileSet = fileSet;
		this.translationResult = translationResult;
		this.daoFactory = daoFactory;
	}

	RplaTabAbstractTranslator(	RplaTabDocumentSet documentSet,
								RplaTabTranslationResult translationResult,
								CaArrayDaoFactory daoFactory) {
		this.documentSet = documentSet;
		this.fileSet = null;
		this.translationResult = translationResult;
		this.daoFactory = daoFactory;
	}

	CaArrayDaoFactory getDaoFactory () {
		return daoFactory;
	}

	RplaTabDocumentSet getDocumentSet () {
		return documentSet;
	}

	CaArrayFileSet getFileSet () {
		return fileSet;
	}

	CaArrayFile getFile ( String name) {
		if (fileSet == null) {
			return null;
		}
		Set<CaArrayFile> files = fileSet.getFiles();
		Iterator<CaArrayFile> i = files.iterator();
		while (i.hasNext()) {
			CaArrayFile caArrayFile = i.next();
			if (name.equals(caArrayFile.getName())) {
				return caArrayFile;
			}
		}
		return null;
	}

	RplaTabTranslationResult getTranslationResult () {
		return translationResult;
	}

	Collection<Term> getTerms ( List<OntologyTerm> ontologyTerms) {
		HashSet<Term> terms = new HashSet<Term>(ontologyTerms.size());
		for (OntologyTerm ontologyTerm : ontologyTerms) {
			terms.add(getTerm(ontologyTerm));
		}
		return terms;
	}

	Term getTerm ( OntologyTerm ontologyTerm) {
		if (ontologyTerm == null) {
			LOG.info("the ontology term was null");
			return null;
		}
		return translationResult.getTerm(ontologyTerm);
	}

	abstract void translate ();

	abstract Logger getLog ();

	/**
	 * Creates or retrieves the org witht he given name.
	 * 
	 * @param name
	 *            the name.
	 * @return the org.
	 */
	protected Organization getOrCreateOrganization ( String name) {

		Organization org = importedOrganizations.get(name);

		if (org == null && StringUtils.isNotBlank(name)) {
			Organization entityToMatch = new Organization();
			entityToMatch.setName(name);

			List<Organization> matchingEntities = getProjectDao()	.queryEntityAndAssociationsByExample(entityToMatch);
			if (matchingEntities.isEmpty()) {
				importedOrganizations.put(name, entityToMatch);
				org = entityToMatch;
			} else {
				// use existing object in database.
				org = matchingEntities.get(0);
			}
		}

		return org;
	}

	ProjectDao getProjectDao () {
		return getDaoFactory().getProjectDao();
	}

}
