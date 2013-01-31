//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.domain.search;


/**
 * @author mshestopalov
 *
 */

@SuppressWarnings("PMD.AvoidDuplicateLiterals")
public enum SearchSourceCategory implements BiomaterialSearchCategory {

    /**
     * Disease State.
     */
    SAMPLE_DISEASE_STATE ("search.category.diseaseState",
            new String[]{"s.diseaseState sds"},
            new String[]{"sds.value"}),

    /**
     * Tissue Site.
     */
    SAMPLE_TISSUE_SITE ("search.category.tissueSite",
            new String[]{"s.tissueSite ts"},
            new String[]{"ts.value"}),

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
            new String[]{"s.materialType sms"},
            new String[]{"sms.value"}),

    /**
     * Cell Type.
     */
    SAMPLE_CELL_TYPE ("search.category.cellType",
             new String[]{"s.cellType scs"},
             new String[]{"scs.value"}),

     /**
      * Source Provider.
      */
    SAMPLE_PROVIDER ("search.category.sourceProvider",
            new String[]{"s.providers ps"},
            new String[]{"ps.name"});


    private final String resourceKey;
    private final String[] joins;
    private final String[] searchFields;

    @SuppressWarnings("PMD.ArrayIsStoredDirectly")
    SearchSourceCategory(String resourceKey, String[] joins, String... searchFields) {
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



}
