//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.application.project;

import javax.ejb.ApplicationException;

/**
 * Indicates that an operation was attempted on a proposal that is prohibited by its current state.
 */
@ApplicationException(rollback = true)
public class ProposalWorkflowException extends Exception {
    private static final long serialVersionUID = 3582622697786140397L;

    /**
     * Creates a new ProposalWorkflowException with given message and cause.
     * 
     * @param message the message
     * @param cause the nested exception that caused the error, if any
     */
    public ProposalWorkflowException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Creates a new ProposalWorkflowException with given message and no cause.
     * 
     * @param message the message
     */
    public ProposalWorkflowException(String message) {
        super(message);
    }

    /**
     * Create a new ProposalWorkflowException with default message and given cause.
     * 
     * @param cause the nested exception that caused the error, if any
     */
    public ProposalWorkflowException(Throwable cause) {
        super(cause);
    }

    /**
     * Creates a new ProposalWorkflowException with default message and no cause.
     */
    public ProposalWorkflowException() {
        super();
    }

}
