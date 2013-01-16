//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.domain.project;

/**
 * Indicates the type of a <code>Job</code>.
 * @author jscott
 *
 */
public enum JobType {
    /**
     * Indicates a data file import job.
     */
    DATA_FILE_IMPORT("Data File Import"),
    /**
     * Indicates a data file validation job.
     */
    DATA_FILE_VALIDATION("Data File Validation"),
    /**
     * Indicates a design file import job.
     */
    DESIGN_FILE_IMPORT("Design File Import"),
    /**
     * Indicates a data file reparse job.
     */
    DATA_FILE_REPARSE("Data File Reparse"), 
    /**
     * Indicates a data file split job.
     */
    DATA_FILE_SPLIT("Data File Split");
    
    private final String displayName;

    JobType(String displayName) {
        this.displayName = displayName;
    }
    
    /**
     * @return File status display name.
     */
    public String getDisplayValue() {
        return displayName;
    }
    
    /**
     * Determine if the job is related to an experiment or not.
     * @return true if array design job, else false. 
     */
    public boolean getArrayDesignJob() {
        return this.equals(JobType.DESIGN_FILE_IMPORT);
    }

}
