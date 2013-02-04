//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package gov.nih.nci.caarray.application.project;

import javax.ejb.ApplicationException;

import org.apache.commons.lang.ArrayUtils;

/**
 * Indicates that an attempt was made to save a project whose state is not internally consistent.
 * @author dkokotov
 */
@ApplicationException(rollback = true)
public class InconsistentProjectStateException extends Exception {
    private static final long serialVersionUID = 3582622697786140397L;

    /**
     * An enum of the different reasons that are possible for a project to be in an inconsistent state.
     * @author dkokotov
     */
    public static enum Reason {
        /**
         * Indicates that the user-specified set of array designs does not include all of the array designs
         * actually linked from the hybridizations belonging to the experiment. Arguments array
         * should consist of the names of the array designs that are still linked from hybs but 
         * are no longer in the user-specified set.
         */
        INCONSISTENT_ARRAY_DESIGNS,
        
        /**
         * Indicates that the selected array designs do no match the experiment's selected manufacturer
         * and/or assay type.  The argument array is not used with this Reason.
         */
        ARRAY_DESIGNS_DONT_MATCH_MANUF_OR_TYPE,
        
        /**
         * Indicates that the project has files that are currently importing.
         */
        IMPORTING_FILES;
    }

    private final Reason reason;
    private final Object[] arguments;
    
    /**
     * Creates a new exception with given reason for inconsistency.
     * @param reason the explanation of what the inconsistency was
     * @param arguments additional information about the inconsistency. the content of this array
     * will vary depending on the reason.
     */
    public InconsistentProjectStateException(Reason reason, Object... arguments) {
        this.reason = reason;
        this.arguments = arguments;
    }

    /**
     * @return the reason for the inconsistency
     */
    public Reason getReason() {
        return reason;
    }

    /**
     * @return the arguments, interpreted depending on the reason.
     */
    public Object[] getArguments() {
        return ArrayUtils.clone(arguments);
    }
}
