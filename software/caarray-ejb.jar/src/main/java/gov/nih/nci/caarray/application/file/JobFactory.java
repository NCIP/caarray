//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package gov.nih.nci.caarray.application.file;

import gov.nih.nci.caarray.application.arraydata.DataImportOptions;
import gov.nih.nci.caarray.domain.array.ArrayDesign;
import gov.nih.nci.caarray.domain.file.CaArrayFileSet;
import gov.nih.nci.caarray.domain.project.Project;

/**
 * Creates jobs.
 * @author jscott
 *
 */
public interface JobFactory {

    /**
     * Create a new job for importing an array design.
     * @param user the user id of the user who requested the job
     * @param arrayDesign the array design to import
     * @return the new job
     */
    AbstractFileManagementJob createArrayDesignFileImportJob(String user, ArrayDesign arrayDesign);
    
    /**
     * Create a new job for importing project data files.
     * @param user the user id of the user who requested the job
     * @param project the project containing the data files
     * @param fileSet the data files to import
     * @param dataImportOptions options to apply to the import job
     * @return the new job
     */
    ProjectFilesImportJob createProjectFilesImportJob(String user, Project project, CaArrayFileSet fileSet,
            DataImportOptions dataImportOptions);
    
    /**
     * Create a new job for importing project data files using the splitting job.
     * @param user the user id of the user who requested the job
     * @param project the project containing the data files
     * @param fileSet the data files to import
     * @param dataImportOptions options to apply to the import job
     * @param submitter job submitter for associated sub-jobs
     * @return the new job
     */
    ProjectFilesSplitJob createProjectFilesSplitJob(String user, Project project, CaArrayFileSet fileSet,
            DataImportOptions dataImportOptions, FileManagementJobSubmitter submitter);
    
    /**
     * Create a new job for validating project files.
     * @param user the user id of the user who requested the job
     * @param project the project containing the files
     * @param fileSet the files to validate
     * @return the new job
     */
    ProjectFilesValidationJob createProjectFilesValidationJob(String user, Project project, CaArrayFileSet fileSet);
    
    /**
     * Create a new job for reparsing previously imported but not parsed files.
     * @param user the user id of the user who requested the job
     * @param project the project containing the files
     * @param fileSet the files to reparse
     * @return the new job
     */
    ProjectFilesReparseJob createProjectFilesReparseJob(String user, Project project, CaArrayFileSet fileSet);
}
