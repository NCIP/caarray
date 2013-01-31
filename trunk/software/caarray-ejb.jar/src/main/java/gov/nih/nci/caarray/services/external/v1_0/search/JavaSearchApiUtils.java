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
import gov.nih.nci.caarray.services.external.v1_0.InvalidInputException;

/**
 * Utility class that allows for easier return of all results or iteration of results from a search service.
 * 
 * @author dkokotov
 */
public class JavaSearchApiUtils extends AbstractSearchApiUtils implements SearchApiUtils {
    private final SearchService searchService;
        
    /**
     * Constructor.
     * 
     * @param searchService the SearchService EJB to use to do the API calls.
     */
    public JavaSearchApiUtils(SearchService searchService) {
        this.searchService = searchService;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Function<LimitOffset, SearchResult<Experiment>> getSearchExperimentsByCriteriaFunction(
            final ExperimentSearchCriteria criteria) {
        return new Function<LimitOffset, SearchResult<Experiment>>() {
            public SearchResult<Experiment> apply(LimitOffset from) {
                try {
                    return searchService.searchForExperiments(criteria, from);
                } catch (InvalidInputException e) {
                    throw new WrapperException(e);
                }
            }
        };
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Function<LimitOffset, SearchResult<Experiment>> getSearchExperimentsByKeywordFunction(
            final KeywordSearchCriteria criteria) {
        return new Function<LimitOffset, SearchResult<Experiment>>() {
            public SearchResult<Experiment> apply(LimitOffset from) {
                return searchService.searchForExperimentsByKeyword(criteria, from);
            }
        };
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Function<LimitOffset, SearchResult<Biomaterial>> getSearchBiomaterialsByCriteriaFunction(
            final BiomaterialSearchCriteria criteria) {
        return new Function<LimitOffset, SearchResult<Biomaterial>>() {
            public SearchResult<Biomaterial> apply(LimitOffset from) {
                try {
                    return searchService.searchForBiomaterials(criteria, from);
                } catch (InvalidInputException e) {
                    throw new WrapperException(e);
                }
            }
        };
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Function<LimitOffset, SearchResult<Biomaterial>> getSearchBiomaterialsByKeywordFunction(
            final BiomaterialKeywordSearchCriteria criteria) {
        return new Function<LimitOffset, SearchResult<Biomaterial>>() {
            public SearchResult<Biomaterial> apply(LimitOffset from) {
                return searchService.searchForBiomaterialsByKeyword(criteria, from);
            }
        };
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Function<LimitOffset, SearchResult<File>> getSearchFilesFunction(
            final FileSearchCriteria criteria) {
        return new Function<LimitOffset, SearchResult<File>>() {
            public SearchResult<File> apply(LimitOffset from) {
                try {
                    return searchService.searchForFiles(criteria, from);
                } catch (InvalidInputException e) {
                    throw new WrapperException(e);
                }
            }
        };
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Function<LimitOffset, SearchResult<Hybridization>> getSearchHybridizationsFunction(
            final HybridizationSearchCriteria criteria) {
        return new Function<LimitOffset, SearchResult<Hybridization>>() {
            public SearchResult<Hybridization> apply(LimitOffset from) {
                try {
                    return searchService.searchForHybridizations(criteria, from);
                } catch (InvalidInputException e) {
                    throw new WrapperException(e);
                }
            }
        };
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected <T extends AbstractCaArrayEntity> Function<LimitOffset, SearchResult<T>> getSearchByExampleFunction(
            final ExampleSearchCriteria<T> criteria) {
        return new Function<LimitOffset, SearchResult<T>>() {
            public SearchResult<T> apply(LimitOffset from) {
                try {
                    return searchService.searchByExample(criteria, from);
                } catch (InvalidInputException e) {
                    throw new WrapperException(e);
                }
            }
        };
    }
}
