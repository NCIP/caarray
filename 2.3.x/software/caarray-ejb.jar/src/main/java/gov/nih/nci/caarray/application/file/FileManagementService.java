//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.application.file;

import gov.nih.nci.caarray.application.arraydata.DataImportOptions;
import gov.nih.nci.caarray.domain.array.ArrayDesign;
import gov.nih.nci.caarray.domain.file.CaArrayFileSet;
import gov.nih.nci.caarray.domain.project.Project;
import gov.nih.nci.caarray.validation.InvalidDataFileException;

/**
 * Provides file storage, validation and import services to the caArray application.
 * Interface to the FileManagement subsystem.
 */
public interface FileManagementService {

    /**
     * The default JNDI name to use to lookup <code>ProjectManagementService</code>.
     */
    String JNDI_NAME = "caarray/FileManagementServiceBean/local";

    /**
     * Validates the files provided.
     *
     * @param project the project the files belong to.
     * @param fileSet the files to validate.
     */
    void validateFiles(Project project, CaArrayFileSet fileSet);

    /**
     * Imports the files provided into the project given.
     *
     * @param fileSet the files to import.
     * @param targetProject entities will be added to this project (if appropriate).
     * @param dataImportOptions options to control auto creation of annotation chains
     */
    void importFiles(Project targetProject, CaArrayFileSet fileSet, DataImportOptions dataImportOptions);

    /**
     * Associates files with the project as supplemental data or informational files.
     *
     * @param fileSet the files to add as supplemental.
     * @param targetProject files will be added to this project.
     */
    void addSupplementalFiles(Project targetProject, CaArrayFileSet fileSet);

    /**
     * Validates and adds/edits the array design (if valid) without adding design details.
     *
     * @param arrayDesign the array design object
     * @param designFiles the file(s) describing the array design.
     * @throws InvalidDataFileException if the design files are invalid for import.
     * @throws IllegalAccessException if trying to modify locked fields on an array design
     */
    void saveArrayDesign(ArrayDesign arrayDesign, CaArrayFileSet designFiles) throws InvalidDataFileException,
            IllegalAccessException;

    /**
     * Imports array design information from the file provided into an <code>ArrayDesign</code> asynchronously.
     *
     * @param arrayDesign the array design object
     */
    void importArrayDesignDetails(ArrayDesign arrayDesign);

}
