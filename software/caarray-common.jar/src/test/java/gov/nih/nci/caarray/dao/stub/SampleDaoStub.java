//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.dao.stub;

import edu.georgetown.pir.Organism;
import gov.nih.nci.caarray.dao.SampleDao;
import gov.nih.nci.caarray.domain.project.Experiment;
import gov.nih.nci.caarray.domain.sample.AbstractBioMaterial;
import gov.nih.nci.caarray.domain.sample.Sample;
import gov.nih.nci.caarray.domain.sample.Source;
import gov.nih.nci.caarray.domain.search.BiomaterialSearchCategory;
import gov.nih.nci.caarray.domain.search.BiomaterialSearchCriteria;
import gov.nih.nci.caarray.domain.search.SearchSampleCategory;
import gov.nih.nci.caarray.domain.vocabulary.Category;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import com.fiveamsolutions.nci.commons.data.search.PageSortParams;

/**
 *
 */
public class SampleDaoStub extends AbstractDaoStub implements SampleDao {
    /**
     * {@inheritDoc}
     */
    public int countSamplesByCharacteristicCategory(Category c, String keyword) {
        return 0;
    }

    /**
     * {@inheritDoc}
     */
    public int countSourcesByCharacteristicCategory(Category c, String keyword) {
        return 0;
    }

    /**
     * {@inheritDoc}
     */
    public <T extends AbstractBioMaterial> List<T> searchByCategory(PageSortParams<T> params, String keyword,
            Class<T> biomaterialClass, BiomaterialSearchCategory... categories) {
        return Collections.emptyList();
    }
    
    /**
     * {@inheritDoc}
     */
    public List<AbstractBioMaterial> searchByCategory(PageSortParams<AbstractBioMaterial> params, String keyword,
            Set<Class<? extends AbstractBioMaterial>> biomaterialClasses, BiomaterialSearchCategory... categories) {
        return Collections.emptyList();
    }

    /**
     * {@inheritDoc}
     */
    public int searchCount(String keyword, Class<? extends AbstractBioMaterial> biomaterialClass,
            BiomaterialSearchCategory... categories) {
        return 0;
    }

    /**
     * {@inheritDoc}
     */
    public List<Sample> searchSamplesByCharacteristicCategory(Category c, String keyword) {
        return Collections.emptyList();
    }

    /**
     * {@inheritDoc}
     */
    public List<Source> searchSourcesByCharacteristicCategory(Category c, String keyword) {
        return Collections.emptyList();
    }

    /**
     * {@inheritDoc}
     */
    public List<Organism> searchForOrganismNames(String keyword) {
        return Collections.emptyList();
    }

    /**
     * {@inheritDoc}
     */
    public List<Sample> searchSamplesByExperimentAndCategory(String keyword, Experiment e, SearchSampleCategory... c) {
        return Collections.emptyList();
    }

    @Override
    public List<Sample> searchSamplesByExperimentAndArbitraryCharacteristicValue( String keyword, Experiment e, 
            Category c ) {
        return Collections.emptyList();
    }

    public List<Sample> searchSamplesByCharacteristicCategory(
            PageSortParams<Sample> params, Category c, String keyword) {
        return Collections.emptyList();
    }

    public List<Source> searchSourcesByCharacteristicCategory(
            PageSortParams<Source> params, Category c, String keyword) {
        return Collections.emptyList();
    }

    public <T extends AbstractBioMaterial> List<T> searchByCriteria(PageSortParams<T> params,
            BiomaterialSearchCriteria criteria) {
        return Collections.emptyList();
    }

}
