//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package gov.nih.nci.caarray.dao;

import gov.nih.nci.caarray.domain.array.ArrayDesign;
import gov.nih.nci.caarray.domain.array.ArrayDesignDetails;
import gov.nih.nci.caarray.domain.array.LogicalProbe;
import gov.nih.nci.caarray.domain.array.PhysicalProbe;
import gov.nih.nci.caarray.domain.contact.Organization;
import gov.nih.nci.caarray.domain.data.AbstractArrayData;
import gov.nih.nci.caarray.domain.data.ArrayDataType;
import gov.nih.nci.caarray.domain.data.ArrayDataTypeDescriptor;
import gov.nih.nci.caarray.domain.data.DesignElementList;
import gov.nih.nci.caarray.domain.data.QuantitationType;
import gov.nih.nci.caarray.domain.data.QuantitationTypeDescriptor;
import gov.nih.nci.caarray.domain.project.AssayType;
import gov.nih.nci.caarray.domain.search.QuantitationTypeSearchCriteria;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.fiveamsolutions.nci.commons.data.search.PageSortParams;

/**
 * DAO for entities in the <code>gov.nih.nci.caarray.domain.array</code> package.
 * 
 * @author Rashmi Srinivasa
 */
public interface ArrayDao extends CaArrayDao {
    /**
     * Returns the <code>ArrayDesign</code> with the id given.
     * 
     * @param id get <code>ArrayDesign</code> matching this id
     * @return the <code>ArrayDesign</code>.
     */
    ArrayDesign getArrayDesign(long id);

    /**
     * Returns the entity matching the LSID given.
     * 
     * @param lsidAuthority the LSID authority
     * @param lsidNamespace the LSID namespace
     * @param lsidObjectId the LSID object ID
     * @return the matching design or null.
     */
    ArrayDesign getArrayDesign(String lsidAuthority, String lsidNamespace, String lsidObjectId);

    /**
     * Returns the list of ArrayDesigns with at least one of the given assay types and the provider. Either the provider
     * or the list of assayTypes is required. If the list of assay types is null or empty, the function will return the
     * list of array designs for the given provider. If the provider is null, the function will return the list of array
     * designs associated with any of the assay types passed in.
     * 
     * @param provider the provider is optional if the list of assayTypes is provided
     * @param assayTypes the list of assay types is optional if provider is specified
     * @param importedOnly whether to only return ArrayDesigns which have finished importing (ie whose corresponding
     *            design file has a status of IMPORTED). if true only those array designs are returned, otherwise, all
     *            array designs are.
     * @return the List&lt;ArrayDesign&gt; of the array designs with the given provider and assay type
     */
    List<ArrayDesign> getArrayDesigns(Organization provider, Set<AssayType> assayTypes, boolean importedOnly);

    /**
     * Returns the array data type corresponding to the given descriptor, if one exists.
     * 
     * @param descriptor search for the matching type
     * @return the matching type, or null if not in the database.
     */
    ArrayDataType getArrayDataType(ArrayDataTypeDescriptor descriptor);

    /**
     * Returns the array data object for the file with given id.
     * 
     * @param fileId id of file for which to retrieve data
     * @return the associated data.
     */
    AbstractArrayData getArrayData(Long fileId);

    /**
     * Returns the quantitation type corresponding to the given descriptor, if one exists.
     * 
     * @param descriptor search for the matching type
     * @return the matching type, or null if not in the database.
     */
    QuantitationType getQuantitationType(QuantitationTypeDescriptor descriptor);

    /**
     * Returns true if an array design is associated with an existing experiment. The provider, type, and design file
     * cannot be modified on a locked array design.
     * 
     * @param id array design id
     * @return true if the array design is locked
     */
    boolean isArrayDesignLocked(Long id);

    /**
     * Returns the design element list matching the LSID given.
     * 
     * @param lsidAuthority the LSID authority
     * @param lsidNamespace the LSID namespace
     * @param lsidObjectId the LSID object ID
     * @return the matching design list or null.
     */
    DesignElementList getDesignElementList(String lsidAuthority, String lsidNamespace, String lsidObjectId);

    /**
     * Return a mapping of logical probe names to ids for the given list of probe names.
     * 
     * @param design probes must belong to this design
     * @param names the names of the probes for which to get ids
     * @return the Map&lt;String, Long> of probe names to ids for the given names
     */
    Map<String, Long> getLogicalProbeNamesToIds(ArrayDesign design, List<String> names);

    /**
     * Returns a list of IDs of logical probes in a given array design, in batches.
     * 
     * @param design probes must belong to this design
     * @param params paging parameters (sorting parameters are ignored)
     * @return a list of matching IDs
     */
    List<Long> getLogicalProbeIds(ArrayDesign design, PageSortParams<LogicalProbe> params);

    /**
     * Select named Physical Probes from a array design, if found.
     * 
     * @param design owner design.
     * @param names the names of probes of interest.
     * @return a potentially incomplete list of the probes of interest.
     */
    List<PhysicalProbe> getPhysicalProbeByNames(ArrayDesign design, List<String> names);

    /**
     * Returns a set of names of physical probes in a given array design.
     * @param design probes must belong to this design
     * @return a set of probe names
     */
    Set<String> getPhysicalProbeNames(ArrayDesign design);

    /**
     * Save a batch of design element entries in a design element list. The entries are put in the list starting at a
     * given index.
     * 
     * @param designElementList the design element list to which the entries belong (must already be persistent)
     * @param startIndex the starting index in the list at which the entries should be saved
     * @param logicalProbeIds the ids of the design elements which should be added to the list starting at given index
     */
    void
            createDesignElementListEntries(DesignElementList designElementList, int startIndex,
                    List<Long> logicalProbeIds);

    /**
     * Delete array design detail.
     * 
     * @param design array design associated with the arrayDesignDetail that will be deleted.
     */
    void deleteArrayDesignDetails(ArrayDesign design);

    /**
     * Create all features for an array design, rows * cols total features will be created. It is assumed that there is
     * only one block.
     * 
     * @param rows number of rows in the design
     * @param cols number of columns in the design
     * @param designDetails array design details in which to create the features
     */
    void createFeatures(int rows, int cols, ArrayDesignDetails designDetails);

    /**
     * Get the ID of the first feature associated with the given design details (the lowest ID number).
     * 
     * @param designDetails array design details to find features for
     * @return lowest ID associated with the design details
     */
    Long getFirstFeatureId(ArrayDesignDetails designDetails);

    /**
     * Gets the array designs associated with an ArrayDesignDetails object. Most array design details are only
     * associated with one array design, but some (such as those imported from PGF/CLF files) are associated with
     * several.
     * 
     * @param arrayDesignDetails the array design details by which to look up array designs
     * @return the ArrayDesigns associated with the given details
     */
    List<ArrayDesign> getArrayDesigns(ArrayDesignDetails arrayDesignDetails);

    /**
     * Performs a query for quantitation types by the given criteria.
     * 
     * @param params paging and sorting parameters
     * @param criteria the criteria for the search
     * @return a list of matching quantitation types
     */
    List<QuantitationType> searchForQuantitationTypes(PageSortParams<QuantitationType> params,
            QuantitationTypeSearchCriteria criteria);

    /**
     * Get array designs that have files that were uploaded but not parsed by earlier caArray versions, but can now be
     * parsed.
     * 
     * @return designs with unparsed - now parsable data file.
     * @since 2.4.0
     */
    List<ArrayDesign> getArrayDesignsWithReImportable();

    /**
     * Get the data handles for all parsed data in the persistent store.
     * 
     * @return a List of URIs for the data handles corresponding to the data underlying all AbstractDataColumns in the
     *         system
     */
    List<URI> getAllParsedDataHandles();
}
