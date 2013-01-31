//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.services.external.v1_0.search;

import gov.nih.nci.caarray.external.v1_0.AbstractCaArrayEntity;
import gov.nih.nci.caarray.external.v1_0.data.File;
import gov.nih.nci.caarray.external.v1_0.experiment.Experiment;
import gov.nih.nci.caarray.external.v1_0.query.BiomaterialKeywordSearchCriteria;
import gov.nih.nci.caarray.external.v1_0.query.BiomaterialSearchCriteria;
import gov.nih.nci.caarray.external.v1_0.query.ExampleSearchCriteria;
import gov.nih.nci.caarray.external.v1_0.query.ExperimentSearchCriteria;
import gov.nih.nci.caarray.external.v1_0.query.FileSearchCriteria;
import gov.nih.nci.caarray.external.v1_0.query.HybridizationSearchCriteria;
import gov.nih.nci.caarray.external.v1_0.query.KeywordSearchCriteria;
import gov.nih.nci.caarray.external.v1_0.query.LimitOffset;
import gov.nih.nci.caarray.external.v1_0.query.SearchResult;
import gov.nih.nci.caarray.external.v1_0.sample.Biomaterial;
import gov.nih.nci.caarray.external.v1_0.sample.Hybridization;

import com.google.common.base.Function;

/**
 * Base implementation of the SearchApiUtils interface.
 * 
 * @author dkokotov
 */
public abstract class AbstractSearchApiUtils implements SearchApiUtils {
    /**
     * {@inheritDoc}
     */
    public Search<Experiment> experimentsByCriteria(ExperimentSearchCriteria criteria) {
        return new Search<Experiment>(getSearchExperimentsByCriteriaFunction(criteria));
    }

    /**
     * {@inheritDoc}
     */
    public Search<Experiment> experimentsByKeyword(KeywordSearchCriteria criteria) {
        return new Search<Experiment>(getSearchExperimentsByKeywordFunction(criteria));
    }

    /**
     * {@inheritDoc}
     */
    public Search<Biomaterial> biomaterialsByCriteria(BiomaterialSearchCriteria criteria) {
        return new Search<Biomaterial>(getSearchBiomaterialsByCriteriaFunction(criteria));
    }

    /**
     * {@inheritDoc}
     */
    public Search<Biomaterial> biomaterialsByKeyword(BiomaterialKeywordSearchCriteria criteria) {
        return new Search<Biomaterial>(getSearchBiomaterialsByKeywordFunction(criteria));
    }

    /**
     * {@inheritDoc}
     */
    public Search<File> filesByCriteria(FileSearchCriteria criteria) {
        return new Search<File>(getSearchFilesFunction(criteria));
    }

    /**
     * {@inheritDoc}
     */
    public Search<Hybridization> hybridizationsByCriteria(HybridizationSearchCriteria criteria) {
        return new Search<Hybridization>(getSearchHybridizationsFunction(criteria));
    }

    /**
     * {@inheritDoc}
     */
    public <T extends AbstractCaArrayEntity> Search<T> byExample(ExampleSearchCriteria<T> criteria) {
        return new Search<T>(getSearchByExampleFunction(criteria));
    }
    
    /**
     * Get the function for searching by example using given criteria.
     * 
     * @param <T> type of entity for example search 
     * @param criteria the example search criteria
     * @return a Function that will execute the search by example using given criteria based on a LimitOffset
     * parameter specifying the subset of results to return.
     */
    protected abstract <T extends AbstractCaArrayEntity> Function<LimitOffset, SearchResult<T>> 
    getSearchByExampleFunction(final ExampleSearchCriteria<T> criteria);

    /**
     * Get the function for searching for hybridizations using given criteria.
     * 
     * @param criteria the search criteria
     * @return a Function that will execute the search using given criteria based on a LimitOffset
     * parameter specifying the subset of results to return.
     */
    protected abstract Function<LimitOffset, SearchResult<Hybridization>> getSearchHybridizationsFunction(
            final HybridizationSearchCriteria criteria);

    /**
     * Get the function for searching for files using given criteria.
     * 
     * @param criteria the search criteria
     * @return a Function that will execute the search using given criteria based on a LimitOffset
     * parameter specifying the subset of results to return.
     */
    protected abstract Function<LimitOffset, SearchResult<File>> getSearchFilesFunction(
            final FileSearchCriteria criteria);

    /**
     * Get the function for searching for biomaterials by keyword using given criteria.
     * 
     * @param criteria the keyword search criteria
     * @return a Function that will execute the search using given criteria based on a LimitOffset
     * parameter specifying the subset of results to return.
     */
    protected abstract Function<LimitOffset, SearchResult<Biomaterial>> getSearchBiomaterialsByKeywordFunction(
            final BiomaterialKeywordSearchCriteria criteria);

    /**
     * Get the function for searching for biomaterials using given criteria.
     * 
     * @param criteria the search criteria
     * @return a Function that will execute the search using given criteria based on a LimitOffset
     * parameter specifying the subset of results to return.
     */
    protected abstract Function<LimitOffset, SearchResult<Biomaterial>> getSearchBiomaterialsByCriteriaFunction(
            final BiomaterialSearchCriteria criteria);

    /**
     * Get the function for searching for experiments by keyword using given criteria.
     * 
     * @param criteria the keyword search criteria
     * @return a Function that will execute the search using given criteria based on a LimitOffset
     * parameter specifying the subset of results to return.
     */
   protected abstract Function<LimitOffset, SearchResult<Experiment>> getSearchExperimentsByKeywordFunction(
            final KeywordSearchCriteria criteria);

   /**
    * Get the function for searching for experiments using given criteria.
    * 
    * @param criteria the search criteria
    * @return a Function that will execute the search using given criteria based on a LimitOffset
    * parameter specifying the subset of results to return.
    */
    protected abstract Function<LimitOffset, SearchResult<Experiment>> getSearchExperimentsByCriteriaFunction(
            final ExperimentSearchCriteria criteria);
}
