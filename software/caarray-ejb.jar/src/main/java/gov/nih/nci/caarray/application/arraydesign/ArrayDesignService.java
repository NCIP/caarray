//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.application.arraydesign;

import gov.nih.nci.caarray.domain.array.ArrayDesign;
import gov.nih.nci.caarray.domain.contact.Organization;
import gov.nih.nci.caarray.domain.data.DesignElementList;
import gov.nih.nci.caarray.domain.file.CaArrayFile;
import gov.nih.nci.caarray.domain.project.AssayType;
import gov.nih.nci.caarray.validation.InvalidDataFileException;
import gov.nih.nci.caarray.validation.ValidationResult;

import java.util.List;
import java.util.Set;

/**
 * Provides array design detail parsing, storage, and retrieval functionality. Interface to the
 * ArrayDesign subsystem.
 */
public interface ArrayDesignService {
    /**
     * The default JNDI name to use to lookup <code>ProjectManagementService</code>.
     */
    String JNDI_NAME = "caarray/ArrayDesignServiceBean/local";

    /**
     * Validates an array design in preparation for save.
     *
     * @param design the design to validate
     * @return the validation result.
     */
    ValidationResult validateDesign(ArrayDesign design);

    /**
     * Validates a set of array design files.
     * 
     * @param designFiles the array design files to validate. These should be a set of files representing a single array
     *            design. Typically only a single file is expected, but some design formats (e.g. Affymetrix PGF/CLF)
     *            consist of multiple files
     * @return the validation result.
     */
    ValidationResult validateDesign(Set<CaArrayFile> designFiles);

    /**
     * Imports top-level attributes for an existing array design from the associated
     * <code>designFile</code>, validating the design file and updating attributes
     * if valid.
     *
     * @param arrayDesign the design to import details for.
     */
    void importDesign(ArrayDesign arrayDesign);

    /**
     * Imports details for an existing array design from associated files(s) including
     * <code>designFile</code> and <code>annotationFile</code>.
     *
     * @param arrayDesign the design to import details for.
     */
    void importDesignDetails(ArrayDesign arrayDesign);

    /**
     * @return the list of all Organizations that are array providers in the system.
     */
    List<Organization> getAllProviders();

    /**
     * Returns the list of ArrayDesigns with at least one of the given assay types and the provider.  Either the
     * provider or the list of assayTypes is required.  If the list of assay types is null or empty, the function
     * will return the list of array designs for the given provider.  If the provider is null, the function will
     * return the list of array designs associated with any of the assay types passed in.
     * @param provider the provider is optional if the list of assayTypes is provided
     * @param assayTypes the list of assay types is optional if provider is specified
     * @return the List&lt;ArrayDesign&gt; of the array designs with the given provider and assay type
     */
    List<ArrayDesign> getImportedArrayDesigns(Organization provider, Set<AssayType> assayTypes);

    /**
     * Returns the <code>ArrayDesign</code> with the id given.
     *
     * @param id get <code>ArrayDesign</code> matching this id
     * @return the <code>ArrayDesign</code>.
     */
    ArrayDesign getArrayDesign(Long id);

    /**
     * Returns the list of all ArrayDesigns.
     * @return the List&lt;ArrayDesign&gt; of array designs
     */
    List<ArrayDesign> getArrayDesigns();

    /**
     * Retrieves an <code>ArrayDesign</code> by LSID.
     *
     * @param lsidAuthority the LSID authority
     * @param lsidNamespace the LSID namespace
     * @param lsidObjectId the LSID object ID
     * @return the matching design or null.
     */
    ArrayDesign getArrayDesign(String lsidAuthority, String lsidNamespace, String lsidObjectId);

    /**
     * Returns true if an array design is locked, meaning that it is associated
     * with an existing experiment.
     * @param id the array design id
     * @return true if the array design is locked
     */
    boolean isArrayDesignLocked(Long id);

    /**
     * Saves an array design. May be called with either a new ArrayDesign not yet in the data store, or an already
     * existing design to be updated.
     * 
     * @param arrayDesign the array design to save
     * @return the persisted array design. This may not be the same object instance as passed in.
     * @throws IllegalAccessException if trying to modify locked fields on an array design or if the array design file
     *             is currently being imported
     * @throws InvalidDataFileException if array is duplicate
     */
    ArrayDesign saveArrayDesign(ArrayDesign arrayDesign) throws IllegalAccessException, InvalidDataFileException;

    /**
     * Checks for duplicate array design.
     *
     * @param arrayDesign the array design to check
     * @return duplicate
     */
    boolean isDuplicate(ArrayDesign arrayDesign);


    /**
     * Deletes an array design. Will not delete designs that are locked or in an
     * importing state.
     *
     * @param arrayDesign
     *            the array design to delete
     * @throws ArrayDesignDeleteException if the array design could not be deleted
     */
    void deleteArrayDesign(ArrayDesign arrayDesign) throws ArrayDesignDeleteException;

    /**
     * Retrieves an existing <code>DesignElementList</code> for an array design by LSID.
     *
     * @param lsidAuthority the LSID authority
     * @param lsidNamespace the LSID namespace
     * @param lsidObjectId the LSID object ID
     * @return the matching design element list or null.
     */
    DesignElementList getDesignElementList(String lsidAuthority, String lsidNamespace, String lsidObjectId);

    /**
     * Get array designs that have files that were uploaded but not parsed by earlier caArray versions,
     * but can now be parsed.
     * @return designs with unparsed - now parsable data file.
     * @since 2.4.0
     */
    List<ArrayDesign> getArrayDesignsWithReImportableFiles();

}
