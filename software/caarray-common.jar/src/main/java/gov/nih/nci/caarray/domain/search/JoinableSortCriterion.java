package gov.nih.nci.caarray.domain.search;

import com.fiveamsolutions.nci.commons.data.search.SortCriterion;
/**
 * SortCriterion where the orderBy field must be used with the joins field because the orderBy field
 * depends on an alias established in the joins.
 * @author mshestopalov
 *
 * @param <AbstractBiomaterial>
 */
public interface JoinableSortCriterion<AbstractBiomaterial> extends SortCriterion<AbstractBiomaterial> {


    /**
     * Get list of join tables for this search order.
     * @return join tables
     */
    String[] getJoins();

}
