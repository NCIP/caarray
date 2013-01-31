//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.application.translation.magetab;

import gov.nih.nci.caarray.dao.CaArrayDaoFactory;
import gov.nih.nci.caarray.dao.ProjectDao;
import gov.nih.nci.caarray.domain.contact.Organization;
import gov.nih.nci.caarray.domain.file.CaArrayFileSet;
import gov.nih.nci.caarray.domain.vocabulary.Term;
import gov.nih.nci.caarray.magetab.MageTabDocumentSet;
import gov.nih.nci.caarray.magetab.OntologyTerm;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

/**
 * Base class for translators.
 */
abstract class AbstractTranslator {

    private final MageTabDocumentSet documentSet;
    private final CaArrayFileSet fileSet;
    private final MageTabTranslationResult translationResult;
    private final CaArrayDaoFactory daoFactory;
    private final Map<String, Organization> importedOrganizations = new HashMap<String, Organization>();

    AbstractTranslator(MageTabDocumentSet documentSet, CaArrayFileSet fileSet,
            MageTabTranslationResult translationResult, CaArrayDaoFactory daoFactory) {
        this.documentSet = documentSet;
        this.fileSet = fileSet;
        this.translationResult = translationResult;
        this.daoFactory = daoFactory;
    }

    AbstractTranslator(MageTabDocumentSet documentSet, MageTabTranslationResult translationResult,
            CaArrayDaoFactory daoFactory) {
        this.documentSet = documentSet;
        this.fileSet = null;
        this.translationResult = translationResult;
        this.daoFactory = daoFactory;
    }

    CaArrayDaoFactory getDaoFactory() {
        return daoFactory;
    }

    MageTabDocumentSet getDocumentSet() {
        return documentSet;
    }

    CaArrayFileSet getFileSet() {
        return fileSet;
    }

    MageTabTranslationResult getTranslationResult() {
        return translationResult;
    }

    Collection<Term> getTerms(List<OntologyTerm> ontologyTerms) {
        HashSet<Term> terms = new HashSet<Term>(ontologyTerms.size());
        for (OntologyTerm ontologyTerm : ontologyTerms) {
            terms.add(getTerm(ontologyTerm));
        }
        return terms;
    }

    Term getTerm(OntologyTerm ontologyTerm) {
        if (ontologyTerm == null) {
            return null;
        }
        return translationResult.getTerm(ontologyTerm);
    }

    abstract void translate();

    abstract Logger getLog();

    /**
     * Creates or retrieves the org with the given name.
     * @param name the name.
     * @return the org.
     */
    protected Organization getOrCreateOrganization(String name) {
        Organization org = importedOrganizations.get(name);

        if (org == null && StringUtils.isNotBlank(name)) {
            Organization entityToMatch = new Organization();
            entityToMatch.setName(name);

            List<Organization> matchingEntities = getProjectDao().queryEntityByExample(entityToMatch);
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

    ProjectDao getProjectDao() {
        return getDaoFactory().getProjectDao();
    }

}
