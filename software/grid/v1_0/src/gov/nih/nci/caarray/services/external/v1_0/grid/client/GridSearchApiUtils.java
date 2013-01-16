//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.services.external.v1_0.grid.client;

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
import gov.nih.nci.caarray.services.external.v1_0.grid.stubs.types.InvalidInputFault;
import gov.nih.nci.caarray.services.external.v1_0.search.AbstractSearchApiUtils;
import gov.nih.nci.caarray.services.external.v1_0.search.SearchApiUtils;

import java.rmi.RemoteException;

import com.google.common.base.Function;

/**
 * Utility class that allows for easier return of all results or iteration of results from a search service.
 * 
 * @author dkokotov
 */
public class GridSearchApiUtils extends AbstractSearchApiUtils implements SearchApiUtils {
    private final CaArraySvc_v1_0Client client;

    /**
     * @param client the CaArraySvc_v1_0 client proxy to use for API calls
     */
    public GridSearchApiUtils(CaArraySvc_v1_0Client client) {
        this.client = client;
    }
        
    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    protected Function<LimitOffset, SearchResult<Experiment>> getSearchExperimentsByCriteriaFunction(
            final ExperimentSearchCriteria criteria) {
        return new Function<LimitOffset, SearchResult<Experiment>>() {
            public SearchResult<Experiment> apply(LimitOffset from) {
                try {
                    return client.searchForExperiments(criteria, from);
                } catch (InvalidInputFault f) {
                    throw new WrapperException(GridApiUtils.translateFault(f));
                } catch (RemoteException e) {
                    throw new RuntimeException("Error executing API method", e);
                }
            }
        };
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    protected Function<LimitOffset, SearchResult<Experiment>> getSearchExperimentsByKeywordFunction(
            final KeywordSearchCriteria criteria) {
        return new Function<LimitOffset, SearchResult<Experiment>>() {
            public SearchResult<Experiment> apply(LimitOffset from) {
                try {
                    return client.searchForExperimentsByKeyword(criteria, from);
                } catch (RemoteException e) {
                    throw new RuntimeException("Error executing API method", e);
                }
            }
        };
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    protected Function<LimitOffset, SearchResult<Biomaterial>> getSearchBiomaterialsByCriteriaFunction(
            final BiomaterialSearchCriteria criteria) {
        return new Function<LimitOffset, SearchResult<Biomaterial>>() {
            public SearchResult<Biomaterial> apply(LimitOffset from) {
                try {
                    return client.searchForBiomaterials(criteria, from);
                } catch (InvalidInputFault f) {
                    throw new WrapperException(GridApiUtils.translateFault(f));
                } catch (RemoteException e) {
                    throw new RuntimeException("Error executing API method", e);
                }
            }
        };
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    protected Function<LimitOffset, SearchResult<Biomaterial>> getSearchBiomaterialsByKeywordFunction(
            final BiomaterialKeywordSearchCriteria criteria) {
        return new Function<LimitOffset, SearchResult<Biomaterial>>() {
            public SearchResult<Biomaterial> apply(LimitOffset from) {
                try {
                    return client.searchForBiomaterialsByKeyword(criteria, from);                    
                } catch (RemoteException e) {
                    throw new RuntimeException("Error executing API method", e);
                }
            }
        };
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    protected Function<LimitOffset, SearchResult<File>> getSearchFilesFunction(
            final FileSearchCriteria criteria) {
        return new Function<LimitOffset, SearchResult<File>>() {
            public SearchResult<File> apply(LimitOffset from) {
                try {
                    return client.searchForFiles(criteria, from);
                } catch (InvalidInputFault f) {
                    throw new WrapperException(GridApiUtils.translateFault(f));
                } catch (RemoteException e) {
                    throw new RuntimeException("Error executing API method", e);
                }
            }
        };
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    protected Function<LimitOffset, SearchResult<Hybridization>> getSearchHybridizationsFunction(
            final HybridizationSearchCriteria criteria) {
        return new Function<LimitOffset, SearchResult<Hybridization>>() {
            public SearchResult<Hybridization> apply(LimitOffset from) {
                try {
                    return client.searchForHybridizations(criteria, from);
                } catch (InvalidInputFault f) {
                    throw new WrapperException(GridApiUtils.translateFault(f));
                } catch (RemoteException e) {
                    throw new RuntimeException("Error executing API method", e);
                }
            }
        };
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    protected <T extends AbstractCaArrayEntity> Function<LimitOffset, SearchResult<T>> getSearchByExampleFunction(
            final ExampleSearchCriteria<T> criteria) {
        return new Function<LimitOffset, SearchResult<T>>() {
            public SearchResult<T> apply(LimitOffset from) {
                try {
                    return client.searchByExample(criteria, from);                    
                } catch (InvalidInputFault f) {
                    throw new WrapperException(GridApiUtils.translateFault(f));
                } catch (RemoteException e) {
                    throw new RuntimeException("Error executing API method", e);
                }
            }
        };
    }
}
