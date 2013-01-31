//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.dao.stub;

import edu.georgetown.pir.Organism;
import gov.nih.nci.caarray.dao.SampleDao;
import gov.nih.nci.caarray.domain.project.Experiment;
import gov.nih.nci.caarray.domain.sample.AbstractBioMaterial;
import gov.nih.nci.caarray.domain.sample.Sample;
import gov.nih.nci.caarray.domain.sample.Source;
import gov.nih.nci.caarray.domain.search.BiomaterialSearchCategory;
import gov.nih.nci.caarray.domain.search.SearchSampleCategory;
import gov.nih.nci.caarray.domain.vocabulary.Category;

import java.util.Collections;
import java.util.List;

import com.fiveamsolutions.nci.commons.data.search.PageSortParams;

/**
 *
 */
public class SampleDaoStub extends AbstractDaoStub implements SampleDao {

    /**
     * {@inheritDoc}
     */
    public int countSamplesByCharacteristicCategory(Category c, String keyword) {
        // TODO Auto-generated method stub
        return 0;
    }

    /**
     * {@inheritDoc}
     */
    public int countSourcesByCharacteristicCategory(Category c, String keyword) {
        // TODO Auto-generated method stub
        return 0;
    }

    /**
     * {@inheritDoc}
     */
    public <T extends AbstractBioMaterial> List<T> searchByCategory(PageSortParams<T> params, String keyword,
            BiomaterialSearchCategory... categories) {
        return Collections.EMPTY_LIST;
    }

    /**
     * {@inheritDoc}
     */
    public int searchCount(String keyword, BiomaterialSearchCategory... categories) {
        // TODO Auto-generated method stub
        return 0;
    }

    /**
     * {@inheritDoc}
     */
    public List<Sample> searchSamplesByCharacteristicCategory(Category c, String keyword) {
        return Collections.EMPTY_LIST;
    }

    /**
     * {@inheritDoc}
     */
    public List<Source> searchSourcesByCharacteristicCategory(Category c, String keyword) {
        return Collections.EMPTY_LIST;
    }

    /**
     * {@inheritDoc}
     */
    public List<Organism> searchForOrganismNames(String keyword) {
        return Collections.EMPTY_LIST;
    }

    /**
     * {@inheritDoc}
     */
    public List<Sample> searchSamplesByExperimentAndCategory(String keyword, Experiment e, SearchSampleCategory... c) {
        // TODO Auto-generated method stub
        return null;
    }

}
