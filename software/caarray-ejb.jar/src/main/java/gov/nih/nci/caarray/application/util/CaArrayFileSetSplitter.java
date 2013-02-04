//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package gov.nih.nci.caarray.application.util;

import gov.nih.nci.caarray.domain.file.CaArrayFileSet;

import java.io.IOException;
import java.util.Set;

/**
 * Splits a large CaArrayFileSet into smaller manageable chunks.
 *
 * @author kkanchinadam
 */
public interface CaArrayFileSetSplitter {

    /**
     * Accepts a (potentially) large file set and creates a set of smaller file sets, with the appropriate
     * CaArrayFile parent/child relationships between SDRF files.
     *
     * <p>This method is null safe.  Null input results in null output.
     *
     * @param largeFileSet the input files to be split
     * @return collection of files
     * @throws IOException if temporary file management fails during the split
     */
    Set<CaArrayFileSet> split(CaArrayFileSet largeFileSet) throws IOException;

}
