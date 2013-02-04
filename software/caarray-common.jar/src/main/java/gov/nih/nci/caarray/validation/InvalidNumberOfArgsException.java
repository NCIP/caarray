//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package gov.nih.nci.caarray.validation;

import java.util.ArrayList;
import java.util.List;

/**
 * Tracks reason for importer failure.
 * @author mshestopalov
 */
public final class InvalidNumberOfArgsException extends RuntimeException {
    /**
     * Incorrect number of design files passed in.
     */
    public static final String NUMBER_OF_ARGS = "number.of.args";
    
    /**
     * Unsupported array design type.
     */
    public static final String UNSUPPORTED_ARRAY_DESIGN = "unsupportedFile";

    private List<String> arguments = new ArrayList<String>();
    private static final long serialVersionUID = 107348410227852110L;

    /**
     * Constructor for exceptions w/out args.
     * @param message message
     */
    public InvalidNumberOfArgsException(String message) {
        super(message);
    }

    /**
     * Constructor for exceptions w/ args.
     * @param message message
     * @param args arguments
     */
    public InvalidNumberOfArgsException(String message, List<String> args) {
        super(message);
        arguments = args;
    }

    /**
     * Get list or arguments.
     * @return list of arguments
     */
    public List<String> getArguments() {
        return arguments;
    }

    /**
     * Add argument to the list.
     * @param arg arguments
     */
    public void addArg(String arg) {
        arguments.add(arg);
    }


}
