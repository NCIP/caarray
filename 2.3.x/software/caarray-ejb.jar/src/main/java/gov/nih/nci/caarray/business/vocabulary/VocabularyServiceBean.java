//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.business.vocabulary;

import edu.georgetown.pir.Organism;
import gov.nih.nci.caarray.application.ExceptionLoggingInterceptor;
import gov.nih.nci.caarray.dao.CaArrayDaoFactory;
import gov.nih.nci.caarray.dao.OrganismDao;
import gov.nih.nci.caarray.dao.VocabularyDao;
import gov.nih.nci.caarray.domain.protocol.Protocol;
import gov.nih.nci.caarray.domain.vocabulary.Category;
import gov.nih.nci.caarray.domain.vocabulary.Term;
import gov.nih.nci.caarray.domain.vocabulary.TermSource;
import gov.nih.nci.caarray.util.CaArrayUtils;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.interceptor.Interceptors;

import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;

/**
 * Entry point into implementation of the vocabulary service subsystem.
 */
@Local(VocabularyService.class)
@Stateless
@Interceptors(ExceptionLoggingInterceptor.class)
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class VocabularyServiceBean implements VocabularyService {
    private static final String VERSION_FIELD = "version";
    private CaArrayDaoFactory daoFactory = CaArrayDaoFactory.INSTANCE;

    /**
     *
     * {@inheritDoc}
     */
    public Set<Term> getTerms(final Category category) {
        return getTerms(category, null);
    }

    /**
     * {@inheritDoc}
     */
    public Set<Term> getTerms(final Category category, String value) {
        if (category == null) {
            throw new IllegalArgumentException("Category is null");
        }
        return getVocabularyDao().getTermsRecursive(category, value);
    }

    /**
     * {@inheritDoc}
     */
    public Term getTerm(TermSource source, String value) {
        return getVocabularyDao().getTerm(source, value);
    }

    /**
     * {@inheritDoc}
     */
    public Organism getOrganism(TermSource source, String scientificName) {
        return getVocabularyDao().getOrganism(source, scientificName);
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
    public TermSource getSource(String name, String version) {
        TermSource querySource = new TermSource();
        querySource.setName(name);
        querySource.setVersion(version);
        return CaArrayUtils.uniqueResult(getVocabularyDao().queryEntityByExample(querySource, MatchMode.EXACT, false,
                new String[] {"url" }, Order.desc(VERSION_FIELD)));
    }

    /**
     * {@inheritDoc}
     */
    public Set<TermSource> getSources(String name) {
        TermSource querySource = new TermSource();
        querySource.setName(name);
        return new LinkedHashSet<TermSource>(getVocabularyDao().queryEntityByExample(querySource,
                Order.desc(VERSION_FIELD)));
    }

    /**
     * {@inheritDoc}
     */
    public TermSource getSourceByUrl(String url, String version) {
        TermSource querySource = new TermSource();
        querySource.setUrl(url);
        querySource.setVersion(version);
        return CaArrayUtils.uniqueResult(getVocabularyDao().queryEntityByExample(querySource, MatchMode.EXACT, false,
                new String[] {"name" }, Order.desc(VERSION_FIELD)));
    }

    /**
     * {@inheritDoc}
     */
    public Set<TermSource> getSourcesByUrl(String url) {
        TermSource querySource = new TermSource();
        querySource.setUrl(url);
        return new LinkedHashSet<TermSource>(getVocabularyDao().queryEntityByExample(querySource,
                Order.desc(VERSION_FIELD)));
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
        return getVocabularyDao().getCategory(source, categoryName);
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
    public List<Protocol> getProtocolsByProtocolType(Term type, String name) {
        if (type == null) {
            return new ArrayList<Protocol>();
        }
        return this.daoFactory.getProtocolDao().getProtocols(type, name);
    }

    /**
     * {@inheritDoc}
     */
    public Protocol getProtocol(String name, TermSource source) {
        return this.daoFactory.getProtocolDao().getProtocol(name, source);
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
     * {@inheritDoc}
     */
    public Term findTermInAllTermSourceVersions(TermSource termSource, String value) {
        return getVocabularyDao().findTermInAllTermSourceVersions(termSource, value);
    }

    /**
     * {@inheritDoc}
     */
    public List<Category> searchForCharacteristicCategory(String keyword) {
        return getVocabularyDao().searchForCharacteristicCategory(keyword);
    }

    /**
     * {@inheritDoc}
     */
    public List<Organism> searchForOrganismNames(String keyword) {
        return getOrganismDao().searchForOrganismNames(keyword);
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
