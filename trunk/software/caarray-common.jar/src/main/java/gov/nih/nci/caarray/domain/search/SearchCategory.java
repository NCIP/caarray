//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.domain.search;

import gov.nih.nci.caarray.domain.ResourceBasedEnum;
import gov.nih.nci.caarray.domain.project.Experiment;

import org.apache.commons.lang.ArrayUtils;

/**
 * @author Winston Cheng
 */
@SuppressWarnings("PMD.AvoidDuplicateLiterals")
public enum SearchCategory implements ResourceBasedEnum {
    /**
     * Experiment title.
     */
    // NOTE: Experiment title is explicitly set as the first enum here so it shows up first in the UI
    EXPERIMENT_TITLE("search.category.experimentTitle", new String[] {"p.experiment e"}, "e.title"),

    /**
     * Experiment ID.
     */
    EXPERIMENT_ID("search.category.experimentId", new String[] {"p.experiment e"}, "e.publicIdentifier"),

    /**
     * Experiment title.
     */
    EXPERIMENT_DESCRIPTION("search.category.experimentDescription", new String[] {"p.experiment e"}, "e.description"),

    /**
     * Array provider.
     */
    ARRAY_PROVIDER("search.category.arrayProvider", new String[] {"p.experiment e", "e.manufacturer m"}, "m.name"),

    /**
     * Array design.
     */
    ARRAY_DESIGN("search.category.arrayDesign", new String[] {"p.experiment e", "e.arrayDesigns a"}, "a.name"),

    /**
     * Organism.
     */
    ORGANISM("search.category.organism", new String[] {"p.experiment e"}, new String[] {"e.organism.commonName",
            "e.organism.scientificName"}),
    /**
     * Sample.
     */
    SAMPLE("search.category.sample", new String[] {"p.experiment e", "e.samples s"}, "s.name"),

    /**
     * Disease state.
     */
    DISEASE_STATE("search.category.diseaseState", new String[] {"p.experiment e"}, ArrayUtils.EMPTY_STRING_ARRAY) {
        /**
         * {@inheritDoc}
         */
        public String getWhereClause() {
            // the subselect is faster when searching by all categories because otherwise we get a huge number of rows
            // with all the joins due to multiple many-to-many associations
            // it would have been desirable to take this approach for the SAMPLE search category as well, but we cannot
            // because of HHH-530, which would cause the security filters to not be applied
            return "e.id in (select exp.id from " + Experiment.class.getName() + " exp left join exp.sources src "
                    + "left join src.diseaseState ds where ds.value like :keyword)";
       }
   };

    private final String resourceKey;
    private final String[] joins;
    private final String[] searchFields;

    @SuppressWarnings("PMD.ArrayIsStoredDirectly")
    SearchCategory(String resourceKey, String[] joins, String... searchFields) {
        this.resourceKey = resourceKey;
        this.joins = joins;
        this.searchFields = searchFields;
   }

    /**
     * @return the resource key that should be used to retrieve a label for this SearchCategory in the UI
     */
    public String getResourceKey() {
        return this.resourceKey;
   }

    /**
     * These are the fields to join against in the HQL query. Is null if no join is necessary.
     *
     * @return the fields to join against
     */
    @SuppressWarnings("PMD.MethodReturnsInternalArray")
    public String[] getJoins() {
        return this.joins;
   }

    /**
     * @return the where subclause for this search category. this method assumes that the subclause will be wrapped in
     *         parenthesis before being added to the overall where clause of a query.
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
