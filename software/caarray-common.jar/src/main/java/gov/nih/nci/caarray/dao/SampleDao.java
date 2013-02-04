//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package gov.nih.nci.caarray.dao;

import gov.nih.nci.caarray.domain.project.Experiment;
import gov.nih.nci.caarray.domain.sample.AbstractBioMaterial;
import gov.nih.nci.caarray.domain.sample.Sample;
import gov.nih.nci.caarray.domain.sample.Source;
import gov.nih.nci.caarray.domain.search.BiomaterialSearchCategory;
import gov.nih.nci.caarray.domain.search.BiomaterialSearchCriteria;
import gov.nih.nci.caarray.domain.search.SearchSampleCategory;
import gov.nih.nci.caarray.domain.vocabulary.Category;

import java.util.List;
import java.util.Set;

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
     * @param biomaterialClass the AbstractBioMaterial subclass whose instances to search
     * @return number of results
     */
    int searchCount(String keyword, Class<? extends AbstractBioMaterial> biomaterialClass,
            BiomaterialSearchCategory... categories);

    /**
     * Performs a query for all samples which contain a characteristic and category supplied.
     * @param params sort params
     * @param c category
     * @param keyword text keyword
     * @return a list if samples with characteristic matching keyword and category matching c
     */
    List<Sample> searchSamplesByCharacteristicCategory(PageSortParams<Sample> params, Category c, String keyword);

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
     * Performs a query for all samples which are related to an experiment and contain a value match in given arbitrary 
     * characteristic category. The keyword is matched like %keyword%
     * 
     * @param keyword text keyword
     * @param e experiment
     * @param c arbitrary characteristic category to perform search within
     * @return a list of samples matching this arbitrary characteristic value
     */
    List<Sample> searchSamplesByExperimentAndArbitraryCharacteristicValue(String keyword, Experiment e, Category c);

    /**
     * Get number of results from query for all samples which contain a characteristic and category supplied.
     * @param c category
     * @param keyword text keyword
     * @return int count
     */
    int countSamplesByCharacteristicCategory(Category c, String keyword);

    /**
     * Performs a query for biomaterials by text matching for the given keyword. This query method supports
     * searching a single type of biomaterial.
     *
     * Note that this method currently only supports SortCriterions that are either simple properties of the target
     * class or required single-valued associations from it. If a non-required association is used in the sort
     * criterion, then any instances for which that association is null will not be included in the results (as an inner
     * join is used)
     * @param <T> child of AbstractBioMaterial
     * @param params paging and sorting parameters
     * @param keyword text to search for
     * @param biomaterialClass the AbstractBioMaterial subclass whose instances to search
     * @param categories Indicates which categories to search. Passing null will search all categories.
     * @return a list of matching biomaterials of type biomaterialSubclass
     */
    <T extends AbstractBioMaterial> List<T> searchByCategory(PageSortParams<T> params, String keyword,
            Class<T> biomaterialClass, BiomaterialSearchCategory... categories);

    /**
     * Performs a query for biomaterials by text matching for the given keyword. This query method supports searching
     * across multiple types of biomaterials.
     * 
     * Note that this method currently only supports SortCriterions that are either simple properties of the target
     * class or required single-valued associations from it. If a non-required association is used in the sort
     * criterion, then any instances for which that association is null will not be included in the results (as an inner
     * join is used)
     * 
     * @param params paging and sorting parameters
     * @param keyword text to search for
     * @param biomaterialClasses the AbstractBioMaterial subclasses to include in the search. If this is an empty, then
     *            this method will return an empty list.
     * @param categories Indicates which categories to search. Passing null will search all categories.
     * @return a list of matching biomaterials
     */
    List<AbstractBioMaterial> searchByCategory(PageSortParams<AbstractBioMaterial> params, String keyword,
            Set<Class<? extends AbstractBioMaterial>> biomaterialClasses, BiomaterialSearchCategory... categories);

    /**
     * Performs a query for all sources which contain a characteristic and category supplied.
     * @param params sort params
     * @param c category
     * @param keyword text keyword
     * @return a list if samples with characteristic matching keyword and category matching c
     */
    List<Source> searchSourcesByCharacteristicCategory(PageSortParams<Source> params, Category c, String keyword);

    /**
     * Get number of results from query for all sources which contain a characteristic and category supplied.
     * @param c category
     * @param keyword text keyword
     * @return int count
     */
    int countSourcesByCharacteristicCategory(Category c, String keyword);
    
    /**
     * Performs a query for biomaterials based on given criteria.
     * 
     * @param params paging and sorting parameters
     * @param criteria the criteria for the search
     * @param <T> the type of biomaterials to search for
     * @return a list of matching biomaterials of the given type.
     */
    <T extends AbstractBioMaterial> List<T> searchByCriteria(PageSortParams<T> params,
            BiomaterialSearchCriteria criteria);

}
