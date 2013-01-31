//======================================================================================
// Copyright 5AM Solutions Inc, Yale University, Lawrence Berkeley National Laboratory
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/caRPLA/LICENSE for details.
//======================================================================================
package gov.nih.nci.caarray.application.translation.rplatab;

import gov.nih.nci.caarray.business.vocabulary.VocabularyService;
import gov.nih.nci.caarray.dao.CaArrayDaoFactory;
import gov.nih.nci.caarray.domain.project.ExperimentOntology;
import gov.nih.nci.caarray.domain.vocabulary.Category;
import gov.nih.nci.caarray.domain.vocabulary.Term;
import gov.nih.nci.caarray.domain.vocabulary.TermSource;
import gov.nih.nci.caarray.magetab.MageTabDocumentSet;
import gov.nih.nci.caarray.magetab.OntologyTerm;
import gov.nih.nci.carpla.rplatab.RplaTabDocumentSet;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.log4j.Logger;

// carplanotes
// Changed superclass. and ctor to accommodate an RplaTabDocumentSet and
// RplaTabTranslationResult.
// I use gov.nih.nci.caarray.magetab.OntologyTerm in the rplatab code, so no
// need for other changes.

@SuppressWarnings("PMD")
final class TermTranslator extends RplaTabAbstractTranslator {

	private static final Logger			LOG			= Logger.getLogger(TermTranslator.class);

	private final VocabularyService		service;
	private final Map<TermKey, Term>	termCache	= new HashMap<TermKey, Term>();

	TermTranslator(	RplaTabDocumentSet documentSet,
					RplaTabTranslationResult translationResult,
					VocabularyService service,
					CaArrayDaoFactory daoFactory) {
		super(documentSet, translationResult, daoFactory);
		this.service = service;
	}

	@Override
	void translate () {

		LOG.info("rpladocumentset had number of terms:" + getDocumentSet()	.getTerms()
																			.size());

		for (OntologyTerm ontologyTerm : getDocumentSet().getTerms()) {

			translateTerm(ontologyTerm);
		}
	}

	private void translateTerm ( OntologyTerm ontologyTerm) {
		LOG.info(ontologyTerm.getValue());
		TermSource source = getSource(ontologyTerm.getTermSource());
		Category category = getOrCreateCategory(ontologyTerm.getCategory());
		Term term = getOrCreateTerm(source, category, ontologyTerm.getValue());
		getTranslationResult().addTerm(ontologyTerm, term);
	}

	private TermSource getSource ( gov.nih.nci.caarray.magetab.TermSource mageTabSource)
	{
		if (mageTabSource == null || StringUtils.isBlank(mageTabSource.getName())) {
			return this.service.getSource(	ExperimentOntology.CAARRAY.getOntologyName(),
											ExperimentOntology.CAARRAY.getVersion());
		}
		// if the source is present then it must have been translated
		TermSource ts = getTranslationResult().getSource(mageTabSource);
		if (ts == null) {
			throw new IllegalStateException("A term is referencing an untranslated term source: " + mageTabSource);
		}
		return ts;
	}

	private Category getOrCreateCategory ( String categoryName) {
		return getOrCreateCategory(	this.service,
									this.getTranslationResult(),
									categoryName);
	}

	static Category getOrCreateCategory (	VocabularyService vocabService,
											RplaTabTranslationResult translationResult,
											String categoryName)
	{
		TermSource mo = vocabService.getSource(	ExperimentOntology.MGED_ONTOLOGY.getOntologyName(),
												ExperimentOntology.MGED_ONTOLOGY.getVersion());
		TermSource userDef = vocabService.getSource(ExperimentOntology.CAARRAY.getOntologyName(),
													ExperimentOntology.CAARRAY.getVersion());
		Category category = vocabService.getCategory(mo, categoryName);
		if (category == null) {
			category = vocabService.getCategory(userDef, categoryName);
		}
		if (category == null) {
			category = translationResult.getCategory(categoryName);
		}
		if (category == null) {
			category = new Category();
			category.setName(categoryName);
			category.setSource(userDef);
			translationResult.addCategory(categoryName, category);
		}
		return category;
	}

	private Term getOrCreateTerm (	TermSource source,
									Category category,
									String value)

	{
		LOG.info(source.getName() + "\t" + value);
		Term term = null;
		if (source.getId() != null) {
			term = this.service.getTerm(source, value);
		}
		if (term == null) {
			term = this.service.findTermInAllTermSourceVersions(source, value);
		}
		if (term == null) {
			term = getTermFromCache(value, source);
		}
		if (term == null) {
			term = new Term();
			term.setSource(source);
			term.setValue(value);
			addTermToCache(value, source, term);
		}
		term.getCategories().add(category);
		return term;
	}

	private Term getTermFromCache ( String value,
									gov.nih.nci.caarray.domain.vocabulary.TermSource ts)
	{
		return termCache.get(new TermKey(value, ts));
	}

	void addTermToCache (	String value,
							gov.nih.nci.caarray.domain.vocabulary.TermSource ts,
							Term term)
	{
		termCache.put(new TermKey(value, ts), term);
	}

	@Override
	Logger getLog () {
		return LOG;
	}

	/**
	 * Key class for looking up terms in the cache by the Term natural key.
	 */
	private static final class TermKey {
		private final String											value;
		private final gov.nih.nci.caarray.domain.vocabulary.TermSource	termSource;

		public TermKey(	String name,
						gov.nih.nci.caarray.domain.vocabulary.TermSource termSource) {
			this.value = name;
			this.termSource = termSource;
		}

		/**
		 * @return the name
		 */
		public String getValue () {
			return value;
		}

		/**
		 * @return the termSource
		 */
		public gov.nih.nci.caarray.domain.vocabulary.TermSource getTermSource ()
		{
			return termSource;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Object#hashCode()
		 */
		@Override
		public int hashCode () {
			return new HashCodeBuilder().append(value)
										.append(termSource)
										.toHashCode();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Object#equals(java.lang.Object)
		 */
		@Override
		public boolean equals ( Object obj) {
			if (!(obj instanceof TermKey)) {
				return false;
			}
			if (this == obj) {
				return true;
			}
			TermKey tk = (TermKey) obj;
			return new EqualsBuilder()	.append(this.value, tk.value)
										.append(this.termSource, tk.termSource)
										.isEquals();
		}
	}
}
