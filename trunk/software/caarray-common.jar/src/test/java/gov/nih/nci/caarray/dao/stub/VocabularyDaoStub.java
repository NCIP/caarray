//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.dao.stub;

import edu.georgetown.pir.Organism;
import gov.nih.nci.caarray.dao.VocabularyDao;
import gov.nih.nci.caarray.domain.project.Experiment;
import gov.nih.nci.caarray.domain.sample.AbstractCharacteristic;
import gov.nih.nci.caarray.domain.vocabulary.Category;
import gov.nih.nci.caarray.domain.vocabulary.Term;
import gov.nih.nci.caarray.domain.vocabulary.TermSource;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 *
 */
public class VocabularyDaoStub extends AbstractDaoStub implements VocabularyDao {

    /**
     * {@inheritDoc}
     */
    public Category getCategory(TermSource source, String name) {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public Set<Term> getTerms(Category category) {
        return new HashSet<Term>();
    }
    /**
     * {@inheritDoc}
     */
    public Set<Term> getTermsRecursive(Category category, String value) {
        return new HashSet<Term>();
    }

    /**
     * {@inheritDoc}
     */
    public void removeTerms(List<Term> termList) {
        // no-op
    }

    /**
     * {@inheritDoc}
     */
    public Term getTermById(Long id) {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public Term getTerm(TermSource source, String value) {
        return null;
    }

    public Organism getOrganism(TermSource source, String scientificName) {
        return null;
    }

    public Term findTermInAllTermSourceVersions(TermSource termSource, String value) {
        return null;
    }

    /* (non-Javadoc)
     * @see gov.nih.nci.caarray.dao.SampleDao#searchForCharacteristicCategory(java.lang.String)
     */
    public List<Category> searchForCharacteristicCategory(Experiment e,
            Class<? extends AbstractCharacteristic> characteristicClass, String keyword) {
        return Collections.emptyList();
    }
}
