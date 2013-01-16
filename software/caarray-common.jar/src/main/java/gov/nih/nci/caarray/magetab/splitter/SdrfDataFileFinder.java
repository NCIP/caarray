//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.magetab.splitter;

import gov.nih.nci.caarray.magetab.io.FileRef;

import java.io.IOException;
import java.util.Set;

/**
 * Identifies the referenced data files within a given Sdrf file, by file name.
 * 
 * @author tparnell
 */
public interface SdrfDataFileFinder {

    /**
     * Accepts a (valid) sdrf file and returns the names of all data files referenced.
     * 
     * @param sdrf input sdrf files
     * @return set of named files referenced by the sdrf
     * @throws IOException if there's trouble accessing the input file
     */
    Set<String> identifyReferencedDataFiles(FileRef sdrf) throws IOException;
}
