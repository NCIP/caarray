//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.application.translation.magetab;

import gov.nih.nci.caarray.application.vocabulary.VocabularyService;
import gov.nih.nci.caarray.dao.CaArrayDaoFactory;
import gov.nih.nci.caarray.domain.vocabulary.TermSource;
import gov.nih.nci.caarray.magetab.MageTabDocumentSet;

import java.util.Comparator;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

/**
 * Translates MAGE-TAB <code>TermSources</code> to caArray <code>TermSources</code>.
 */
@SuppressWarnings("PMD.CyclomaticComplexity")
final class TermSourceTranslator extends AbstractTranslator {

    private static final Logger LOG = Logger.getLogger(TermSourceTranslator.class);

    private final VocabularyService vocabularyService;

    TermSourceTranslator(MageTabDocumentSet documentSet, MageTabTranslationResult translationResult,
            VocabularyService vocabularyService, CaArrayDaoFactory daoFatory) {
        super(documentSet, translationResult, daoFatory);
        this.vocabularyService = vocabularyService;
    }

    @Override
    void translate() {
        for (gov.nih.nci.caarray.magetab.TermSource termSource : getDocumentSet().getTermSources()) {
            translate(termSource);
        }
    }

    private void translate(gov.nih.nci.caarray.magetab.TermSource termSource) {
        TermSource source = lookupSource(termSource);
        getTranslationResult().addSource(termSource, source);
    }

    private TermSource lookupSource(gov.nih.nci.caarray.magetab.TermSource termSource) {
        boolean hasFile = termSource.getFile() != null;
        boolean hasVersion = termSource.getVersion() != null;
        if (hasFile && hasVersion) {
            return lookupSourceByNameUrlAndVersion(termSource.getName(), termSource.getFile(), termSource
                    .getVersion());
        } else if (hasFile && !hasVersion) {
            return lookupSourceByNameAndUrl(termSource.getName(), termSource.getFile());
        } else if (!hasFile && hasVersion) {
            return lookupSourceByNameAndVersion(termSource.getName(), termSource.getVersion());
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
    private TermSource lookupSourceByNameUrlAndVersion(String name, String url, String version) {
        TermSource match = vocabularyService.getSourceByUrl(url, version);
        if (match != null) {
            match.setName(name);
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
    private TermSource lookupSourceByNameAndUrl(String name, String url) {
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
    private TermSource lookupSourceByNameAndVersion(String name, String version) {
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
    private TermSource lookupSourceByNameOnly(String name) {
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
    private TermSource getBestMatch(Set<TermSource> matches) {
        TreeSet<TermSource> sorted = new TreeSet<TermSource>(new TermSourceVersionComparator());
        sorted.addAll(matches);
        return sorted.first();
    }

    @Override
    Logger getLog() {
        return LOG;
    }

    /**
     * Compares term sources by their version, such that empty / null versions come first, and otherwise uses inverse
     * alphabetical ordering.
     *
     * @author dkokotov@vecna.com
     */
    private static class TermSourceVersionComparator implements Comparator<TermSource> {
        /**
         * {@inheritDoc}
         */
        public int compare(TermSource ts1, TermSource ts2) {
            if (StringUtils.isEmpty(ts1.getVersion())) {
                return StringUtils.isEmpty(ts2.getVersion()) ? 0 : -1;
            }
            if (StringUtils.isEmpty(ts2.getVersion())) {
                return 1;
            }
            return ts1.getVersion().compareToIgnoreCase(ts2.getVersion()) * -1;
        }
    }
}
