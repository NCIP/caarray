//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.domain.search;

import org.apache.commons.lang.ArrayUtils;

/**
 * @author mshestopalov
 *
 */

@SuppressWarnings("PMD.AvoidDuplicateLiterals")
public enum SearchSampleCategory implements BiomaterialSearchCategory {

    /**
     * Same Name.
     */
    SAMPLE_NAME ("search.category.sample",
            ArrayUtils.EMPTY_STRING_ARRAY, "s.name"),

    /**
     * External Sample Id.
     */
    SAMPLE_EXTERNAL_ID ("search.category.externalSampleId",
            ArrayUtils.EMPTY_STRING_ARRAY, "s.externalSampleId"),

    /**
     * Disease State.
     */
    SAMPLE_DISEASE_STATE    ("search.category.diseaseState",
            new String[]{"s.sources c", "s.diseaseState sds", "c.diseaseState ds"},
            new String[]{"sds.value", "ds.value"}),

    /**
     * Tissue Site.
     */
    SAMPLE_TISSUE_SITE ("search.category.tissueSite",
            new String[]{"s.sources c", "s.tissueSite sts", "c.tissueSite ts"},
            new String[]{"sts.value", "ts.value"}),

    /**
     * Organism.
     */
    SAMPLE_ORGANISM ("search.category.organism",
            new String[]{"s.experiment e", "e.organism o"},
            new String[]{"o.commonName", "o.scientificName"}),

    /**
     * Experiment title.
     */
    SAMPLE_EXPERIMENT_TITLE ("search.category.experimentTitle",
            new String[]{"s.experiment e"},
            "e.title"),

    /**
     * Material Type.
     */
    SAMPLE_MATERIAL_TYPE ("search.category.materialType",
            new String[]{"s.sources c", "s.materialType sms", "c.materialType ms"},
            new String[]{"sms.value", "ms.value"}),

    /**
     * Cell Type.
     */
    SAMPLE_CELL_TYPE ("search.category.cellType",
            new String[]{"s.sources c", "s.cellType scs", "c.cellType cs"},
            new String[]{"scs.value", "cs.value"}),

    /**
     * Source Provider.
     */
    SAMPLE_PROVIDER ("search.category.sourceProvider",
            new String[]{"s.sources c", "c.providers ps"},
            new String[]{"ps.name"});


    private final String resourceKey;
    private final String[] joins;
    private final String[] searchFields;

    @SuppressWarnings("PMD.ArrayIsStoredDirectly")
    SearchSampleCategory(String resourceKey, String[] joins, String... searchFields) {
        this.resourceKey = resourceKey;
        this.joins = joins;
        this.searchFields = searchFields;
    }

    /**
     * {@inheritDoc}
     */
    public String getResourceKey() {
        return this.resourceKey;
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("PMD.MethodReturnsInternalArray")
    public String[] getJoins() {
        return this.joins;
    }

    /**
     * {@inheritDoc}
     */
    public String getWhereClause() {
        StringBuilder sb = new StringBuilder();
        int j = 0;
        for (String field : this.searchFields) {
            if (j++ > 0) {
                sb.append(" OR ");
            }
            sb.append(field).append(" LIKE :keyword");
        }
        return sb.toString();
    }

    /**
     * @return list of permission search categories
     */
    public static SearchSampleCategory[] getPermSearchCategories() {
        return new SearchSampleCategory[]{SAMPLE_NAME, SAMPLE_EXTERNAL_ID,
                SAMPLE_CELL_TYPE, SAMPLE_DISEASE_STATE, SAMPLE_MATERIAL_TYPE,
                SAMPLE_ORGANISM, SAMPLE_PROVIDER, SAMPLE_TISSUE_SITE};
    }


}
