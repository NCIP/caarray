//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.application.vocabulary;

import edu.georgetown.pir.Organism;
import gov.nih.nci.caarray.application.ExceptionLoggingInterceptor;
import gov.nih.nci.caarray.dao.ProtocolDao;
import gov.nih.nci.caarray.dao.SearchDao;
import gov.nih.nci.caarray.dao.VocabularyDao;
import gov.nih.nci.caarray.domain.protocol.Protocol;
import gov.nih.nci.caarray.domain.sample.AbstractCharacteristic;
import gov.nih.nci.caarray.domain.search.ExampleSearchCriteria;
import gov.nih.nci.caarray.domain.vocabulary.Category;
import gov.nih.nci.caarray.domain.vocabulary.Term;
import gov.nih.nci.caarray.domain.vocabulary.TermSource;
import gov.nih.nci.caarray.injection.InjectionInterceptor;
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

import org.hibernate.criterion.Order;

import com.google.inject.Inject;

/**
 * Entry point into implementation of the vocabulary service subsystem.
 */
@Local(VocabularyService.class)
@Stateless
@Interceptors({ExceptionLoggingInterceptor.class, InjectionInterceptor.class })
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class VocabularyServiceBean implements VocabularyService {
    private static final String VERSION_FIELD = "version";

    private SearchDao searchDao;
    private ProtocolDao protocolDao;
    private VocabularyDao vocabularyDao;

    /**
     * 
     * {@inheritDoc}
     */
    @Override
    public Set<Term> getTerms(final Category category) {
        return getTerms(category, null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<Term> getTerms(final Category category, String value) {
        if (category == null) {
            throw new IllegalArgumentException("Category is null");
        }
        return this.vocabularyDao.getTermsRecursive(category, value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Term getTerm(TermSource source, String value) {
        return this.vocabularyDao.getTerm(source, value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Organism getOrganism(TermSource source, String scientificName) {
        return this.vocabularyDao.getOrganism(source, scientificName);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Organism> getOrganisms() {
        return this.searchDao.retrieveAll(Organism.class, Order.asc("scientificName"));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TermSource getSource(String name, String version) {
        final TermSource querySource = new TermSource();
        querySource.setName(name);
        querySource.setVersion(version);
        return CaArrayUtils.uniqueResult(this.vocabularyDao.queryEntityByExample(
                ExampleSearchCriteria.forEntity(querySource).includeNulls().excludeProperties("url"),
                Order.desc(VERSION_FIELD)));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<TermSource> getSources(String name) {
        final TermSource querySource = new TermSource();
        querySource.setName(name);
        return new LinkedHashSet<TermSource>(this.vocabularyDao.queryEntityByExample(querySource,
                Order.desc(VERSION_FIELD)));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TermSource getSourceByUrl(String url, String version) {
        final TermSource querySource = new TermSource();
        querySource.setUrl(url);
        querySource.setVersion(version);
        return CaArrayUtils.uniqueResult(this.vocabularyDao.queryEntityByExample(
                ExampleSearchCriteria.forEntity(querySource).includeNulls().excludeProperties("name"),
                Order.desc(VERSION_FIELD)));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<TermSource> getSourcesByUrl(String url) {
        final TermSource querySource = new TermSource();
        querySource.setUrl(url);
        return new LinkedHashSet<TermSource>(this.vocabularyDao.queryEntityByExample(querySource,
                Order.desc(VERSION_FIELD)));
    }

    /**
     * 
     * {@inheritDoc}
     */
    @Override
    public List<TermSource> getAllSources() {
        return this.searchDao.retrieveAll(TermSource.class, Order.asc("name"));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Category getCategory(TermSource source, String categoryName) {
        return this.vocabularyDao.getCategory(source, categoryName);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Term getTerm(Long id) {
        return this.vocabularyDao.getTermById(id);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Protocol> getProtocolsByProtocolType(Term type, String name) {
        if (type == null) {
            return new ArrayList<Protocol>();
        }
        return this.protocolDao.getProtocols(type, name);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Protocol getProtocol(String name, TermSource source) {
        return this.protocolDao.getProtocol(name, source);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void saveTerm(Term term) {
        this.vocabularyDao.save(term);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Term findTermInAllTermSourceVersions(TermSource termSource, String value) {
        return this.vocabularyDao.findTermInAllTermSourceVersions(termSource, value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T extends AbstractCharacteristic> List<Category> searchForCharacteristicCategory(
            Class<T> characteristicClass, String keyword) {
        return this.vocabularyDao.searchForCharacteristicCategory(null, characteristicClass, keyword);
    }

    /**
     * @param searchDao the searchDao to set
     */
    @Inject
    public void setSearchDao(SearchDao searchDao) {
        this.searchDao = searchDao;
    }

    /**
     * @param protocolDao the protocolDao to set
     */
    @Inject
    public void setProtocolDao(ProtocolDao protocolDao) {
        this.protocolDao = protocolDao;
    }

    /**
     * @param vocabularyDao the vocabularyDao to set
     */
    @Inject
    public void setVocabularyDao(VocabularyDao vocabularyDao) {
        this.vocabularyDao = vocabularyDao;
    }
}
