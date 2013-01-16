//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.application.file;

import gov.nih.nci.caarray.application.arraydata.DataImportOptions;
import gov.nih.nci.caarray.domain.array.ArrayDesign;
import gov.nih.nci.caarray.domain.file.CaArrayFile;
import gov.nih.nci.caarray.domain.file.CaArrayFileSet;
import gov.nih.nci.caarray.domain.project.Project;
import gov.nih.nci.caarray.validation.InvalidDataFileException;

import java.util.List;

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

    /**
     * Re-import and parse an array design which had previously been imported without parsing, but for which a parser is
     * now available.
     * 
     * @param arrayDesignId id of the array design to re-import
     * 
     * @throws InvalidDataFileException if the array design files are not valid according to the parser
     * @throws IllegalAccessException if the array design is not eligible for re=import. An array design can only be
     *             reimported if currently it has a status of IMPORTED_NOT_PARSED, but there is now a parser available
     *             for its design files.
     */
    void reimportAndParseArrayDesign(Long arrayDesignId) throws InvalidDataFileException, IllegalAccessException;

    /**
     * Re-import and parse data files which had previously been imported without parsing, but for which a parser is
     * now available.
     * 
     * @param targetProject the project which the files are a part of
     * @param fileSet the set of files to reimport
     */
    void reimportAndParseProjectFiles(Project targetProject, CaArrayFileSet fileSet);
    
    /**
     * Takes a idf file(s), and all files associated to the project,
     * finds the sdrf file associated with the idf from the list,
     * and parses the sdrf to find all files that are referenced in
     * the sdrf and returns their names.
     *
     * @param idfFile the CaArrayFile which holds the IDF document.
     * @param project the project with the file set which contain the sdrfs
     * @return list of selected file names.
     */
    List<String> findIdfRefFileNames(CaArrayFile idfFile, Project project);
}
