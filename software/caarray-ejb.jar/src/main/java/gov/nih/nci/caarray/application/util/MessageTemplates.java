//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.application.util;

/**
 * Stores template strings for various messages, allowing tests to be more robust.
 * @author dharley
 *
 */
public final class MessageTemplates {
    
    /**
     * The message fragment regarding an array design with specified name not in system.
     */
    public static final String NON_EXISTING_ARRAY_DESIGN_ERROR_MESSAGE_TEMPLATE = "Your reference to %s cannot be "
        + "resolved because an array design with that LSID is not in caArray.  Please import it and try again.";
    
    /**
     * The message fragment regarding a specified array design not being associated with an experiment.
     */
    public static final String ARRAY_DESIGN_NOT_ASSOCIATED_WITH_EXPERIMENT_ERROR_MESSAGE_TEMPLATE =
        "The array design %s is not currently associated with this experiment.";

}
