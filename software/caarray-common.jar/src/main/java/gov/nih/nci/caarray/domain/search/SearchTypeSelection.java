//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.domain.search;

import gov.nih.nci.caarray.domain.ResourceBasedEnum;

/**
 * @author mshestopalov
 *
 */
@SuppressWarnings("PMD.AvoidDuplicateLiterals")
public enum SearchTypeSelection implements ResourceBasedEnum {

    /**
     * Search by experiment.
     */
    SEARCH_BY_EXPERIMENT("search.type.experiment"),

    /**
     * Search by sample.
     */
    SEARCH_BY_SAMPLE("search.type.sample");


    private final String resourceKey;

    @SuppressWarnings("PMD.ArrayIsStoredDirectly")
    SearchTypeSelection(String resourceKey) {
        this.resourceKey = resourceKey;
    }

    /**
     * @return the resource key that should be used to retrieve a label
     * for this SearchCategory in the UI
     */
    public String getResourceKey() {
        return this.resourceKey;
    }


}

