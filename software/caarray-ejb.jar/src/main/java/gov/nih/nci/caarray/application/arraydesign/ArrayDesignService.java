//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
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
     * Validates an array design file in preparation for import.
     *
     * @param designFile the native file to validate
     * @return the validation result.
     */
    ValidationResult validateDesign(CaArrayFile designFile);

    /**
     * Validates an array design files in preparation for import.
     *
     * @param designFiles the native files to validate
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
     * @return the list of all providers in the system.
     */
    List<Organization> getAllProviders();

    /**
     * Returns the list of Organizations that are a provider for at least
     * one ArrayDesign in the system.
     * @return the List&lt;Organization&gt; of Organizations where for each
     * organization in the list there exists at least one ArrayDesign for which
     * that Organization is the provider
     */
    List<Organization> getArrayDesignProviders();

    /**
     * Returns the list of imported ArrayDesigns with the given provider.
     *
     * @param provider the provider
     * @return the List&lt;ArrayDesign&gt; of the array designs whose provider is the given provider and
     * whose array design details have been successfully imported
     */
    List<ArrayDesign> getImportedArrayDesignsForProvider(Organization provider);

    /**
     * Returns the list of ArrayDesigns with the given provider and assay type.
     *
     * @param provider the provider
     * @param assayType the assay type
     * @return the List&lt;ArrayDesign&gt; of the array designs whose provider is the given provider
     */
    List<ArrayDesign> getImportedArrayDesigns(Organization provider, AssayType assayType);

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
     * Saves an array design.
     *
     * @param arrayDesign the array design to save
     * @throws IllegalAccessException if trying to modify locked fields on an array design or if the array design file
     *         is currently being imported
     * @throws InvalidDataFileException if array is duplicate
     */
    void saveArrayDesign(ArrayDesign arrayDesign) throws IllegalAccessException, InvalidDataFileException;

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
     * @throws ArrayDesignDeleteException TODO
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
}
