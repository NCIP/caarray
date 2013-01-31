//======================================================================================
// Copyright 5AM Solutions Inc, Yale University, Lawrence Berkeley National Laboratory
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/caRPLA/LICENSE for details.
//======================================================================================
package gov.nih.nci.caarray.application.translation.rplatab;

import gov.nih.nci.caarray.business.vocabulary.VocabularyService;
import gov.nih.nci.caarray.dao.CaArrayDaoFactory;
import gov.nih.nci.caarray.domain.vocabulary.TermSource;
import gov.nih.nci.caarray.magetab.MageTabDocumentSet;
import gov.nih.nci.carpla.rplatab.RplaTabDocumentSet;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.log4j.Logger;

//carplanotes
//Changed superclass. and ctor to accommodate an RplaTabDocumentSet and RplaTabTranslationResult.
//I use gov.nih.nci.caarray.magetab.TermSource in the rplatab code, so no need for other changes.

@SuppressWarnings("PMD.CyclomaticComplexity")
final class TermSourceTranslator extends RplaTabAbstractTranslator {

	private static final Logger						LOG					= Logger.getLogger(TermSourceTranslator.class);

	private final VocabularyService					vocabularyService;
	private final Map<TermSourceKey, TermSource>	termSourceByName	= new HashMap<TermSourceKey, TermSource>();
	private final Map<TermSourceKey, TermSource>	termSourceByUrl		= new HashMap<TermSourceKey, TermSource>();

	TermSourceTranslator(	RplaTabDocumentSet documentSet,
							RplaTabTranslationResult translationResult,
							VocabularyService vocabularyService,
							CaArrayDaoFactory daoFatory) {
		super(documentSet, translationResult, daoFatory);
		this.vocabularyService = vocabularyService;
	}

	@Override
	void translate () {

		for (gov.nih.nci.caarray.magetab.TermSource termSource : getDocumentSet()	.getTermSources()) {
			translate(termSource);
		}
	}

	private void translate ( gov.nih.nci.caarray.magetab.TermSource termSource)
	{
		// first, check that we haven

		LOG.info(termSource.getName());
		TermSource source = lookupSource(termSource);
		// check that this does not match (via unique constraints) one of the
		// sources we've already created.
		TermSourceKey nameKey = new TermSourceKey(	source.getName(),
													source.getVersion());
		TermSourceKey urlKey = new TermSourceKey(	source.getUrl(),
													source.getVersion());
		if (!termSourceByName.containsKey(nameKey) && (StringUtils.isBlank(source.getUrl()) || !termSourceByUrl.containsKey(urlKey))) {
			getTranslationResult().addSource(termSource, source);
			termSourceByName.put(nameKey, source);
			if (!StringUtils.isBlank(source.getUrl())) {
				termSourceByUrl.put(urlKey, source);
			}
		}
	}

	private TermSource lookupSource ( gov.nih.nci.caarray.magetab.TermSource termSource)
	{
		boolean hasFile = termSource.getFile() != null;
		boolean hasVersion = termSource.getVersion() != null;
		if (hasFile && hasVersion) {
			return lookupSourceByNameUrlAndVersion(	termSource.getName(),
													termSource.getFile(),
													termSource.getVersion());
		} else if (hasFile && !hasVersion) {
			return lookupSourceByNameAndUrl(termSource.getName(),
											termSource.getFile());
		} else if (!hasFile && hasVersion) {
			return lookupSourceByNameAndVersion(termSource.getName(),
												termSource.getVersion());
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
	private TermSource lookupSourceByNameUrlAndVersion (	String name,
															String url,
															String version)
	{
		TermSource match = vocabularyService.getSourceByUrl(url, version);
		if (match != null) {
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
	private TermSource lookupSourceByNameAndUrl ( String name, String url) {
		Set<TermSource> matches = vocabularyService.getSourcesByUrl(url);
		if (!matches.isEmpty()) {
			return getBestMatch(matches);
		} else {
			TermSource result = lookupSourceByNameOnly(name);
			result.setUrl(url);
			return result;
		}
	}

	/**
	 * @param name
	 * @param version
	 * @return
	 */
	private TermSource lookupSourceByNameAndVersion (	String name,
														String version)
	{
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
	private TermSource lookupSourceByNameOnly ( String name) {
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
	private TermSource getBestMatch ( Set<TermSource> matches) {
		TreeSet<TermSource> sorted = new TreeSet<TermSource>(new TermSourceVersionComparator());
		sorted.addAll(matches);
		return sorted.first();
	}

	@Override
	Logger getLog () {
		return LOG;
	}

	/**
	 * Compares term sources by their version, such that empty / null versions
	 * come first, and otherwise uses inverse alphabetical ordering.
	 * 
	 * @author dkokotov@vecna.com
	 */
	private static class TermSourceVersionComparator
													implements
													Comparator<TermSource> {
		/**
		 * {@inheritDoc}
		 */
		public int compare ( TermSource ts1, TermSource ts2) {
			if (StringUtils.isEmpty(ts1.getVersion())) {
				return StringUtils.isEmpty(ts2.getVersion()) ? 0 : -1;
			}
			if (StringUtils.isEmpty(ts2.getVersion())) {
				return 1;
			}
			return ts1.getVersion().compareToIgnoreCase(ts2.getVersion()) * -1;
		}
	}

	/**
	 * Key class for looking up term sources in the cache by the Term Source
	 * natural keys.
	 */
	private static final class TermSourceKey {
		private final String	name;
		private final String	version;

		public TermSourceKey(String name, String version) {
			this.name = name;
			this.version = version;
		}

		/**
		 * @return the name
		 */
		public String getName () {
			return name;
		}

		/**
		 * @return the version
		 */
		public String getVersion () {
			return version;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Object#hashCode()
		 */
		@Override
		public int hashCode () {
			return HashCodeBuilder.reflectionHashCode(this);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Object#equals(java.lang.Object)
		 */
		@Override
		public boolean equals ( Object obj) {
			if (!(obj instanceof TermSourceKey)) {
				return false;
			}
			if (this == obj) {
				return true;
			}
			return EqualsBuilder.reflectionEquals(this, obj);
		}
	}
}
