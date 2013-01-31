//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.dao;

import gov.nih.nci.caarray.domain.project.Experiment;
import gov.nih.nci.caarray.domain.sample.AbstractBioMaterial;
import gov.nih.nci.caarray.domain.sample.Sample;
import gov.nih.nci.caarray.domain.sample.Source;
import gov.nih.nci.caarray.domain.search.BiomaterialSearchCategory;
import gov.nih.nci.caarray.domain.search.SearchSampleCategory;
import gov.nih.nci.caarray.domain.vocabulary.Category;

import java.util.List;

import com.fiveamsolutions.nci.commons.data.search.PageSortParams;


/**
 * DAO for entities in the <code>gov.nih.nci.caarray.domain.sample</code> package.
 *
 * @author mshestopalov
 *
 */
public interface SampleDao extends CaArrayDao {

    /**
     * Gets the count of search results matching the given keyword.
     * @param keyword keyword to search for
     * @param categories categories to search
     * @return number of results
     */
    int searchCount(String keyword, BiomaterialSearchCategory... categories);

    /**
     * Performs a query for all samples which contain a characteristic and category supplied.
     * @param c category
     * @param keyword text keyword
     * @return a list if samples with characteristic matching keyword and category matching c
     */
    List<Sample> searchSamplesByCharacteristicCategory(Category c, String keyword);

    /**
     * Performs a query for all samples which are part of an experiment and
     * contain a category keyword. The keyword is matched like %keyword%
     * @param c category
     * @param e experiment
     * @param keyword text keyword
     * @return a list if samples with characteristic matching keyword and category matching c
     */
    List<Sample> searchSamplesByExperimentAndCategory(String keyword, Experiment e, SearchSampleCategory... c);

    /**
     * Get number of results from query for all samples which contain a characteristic and category supplied.
     * @param c category
     * @param keyword text keyword
     * @return int count
     */
    int countSamplesByCharacteristicCategory(Category c, String keyword);

    /**
     * Performs a query for samples or sources by text matching for the given keyword.
     *
     * Note that this method currently only supports SortCriterions that are either simple properties of the target
     * class or required single-valued associations from it. If a non-required association is used in the sort
     * criterion, then any instances for which that association is null will not be included in the results (as an inner
     * join is used)
     * @param <T> child of AbstractBioMaterial
     * @param params paging and sorting parameters
     * @param keyword text to search for
     * @param categories Indicates which categories to search. Passing null will search all categories.
     * @return a list of matching experiments
     */
    <T extends AbstractBioMaterial>List<T>  searchByCategory(PageSortParams<T> params, String keyword,
            BiomaterialSearchCategory... categories);

    /**
     * Performs a query for all sources which contain a characteristic and category supplied.
     * @param c category
     * @param keyword text keyword
     * @return a list if samples with characteristic matching keyword and category matching c
     */
    List<Source> searchSourcesByCharacteristicCategory(Category c, String keyword);

    /**
     * Get number of results from query for all sources which contain a characteristic and category supplied.
     * @param c category
     * @param keyword text keyword
     * @return int count
     */
    int countSourcesByCharacteristicCategory(Category c, String keyword);

}
