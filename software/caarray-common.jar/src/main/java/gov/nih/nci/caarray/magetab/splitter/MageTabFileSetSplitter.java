//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package gov.nih.nci.caarray.magetab.splitter;

import gov.nih.nci.caarray.magetab.MageTabFileSet;

import java.io.IOException;
import java.util.Set;

/**
 * Splits file sets into manageable chunks, based upon SDRF rows.  This interface is only concerned with file
 * semantics and does <em>not</em> interact with other modules within caArray.  No assumptions are made about
 * whether the input file set is associated with a project, saved to the data store, etc.  This is a utility
 * interface.
 * 
 * @author tparnell
 */
public interface MageTabFileSetSplitter {

    /**
     * Accepts a (potentially) large file set and creates a set of smaller file sets with identical MAGE-TAB
     * semantics.  Identical semantics means:
     * 
     * <ul>
     * <li>Idf files are preserved.  Every returned file set will have all original IDFs.
     * <li>The set of returned items will have every row of each of original SDRFs.
     * <li>Data files are included when referenced by the split SDRF present in each file set.  Not all
     * data files will be in each split.
     * </ul>
     * 
     * In simple terms, each SDRF is split (by rows) such that the resulting set-of-sets has all the rows of
     * the original SDRFs, plus each idf file in the initial set.  Comment lines are not preserved.  Data files
     * are inculded in each split they are referenced from.
     * 
     * <p>This method is null safe.  Null input results in null output.
     * 
     * @param largeFileSet the input files to be split
     * @return collection of files
     * @throws IOException if temporary file management fails during the split
     */
    Set<MageTabFileSet> split(MageTabFileSet largeFileSet) throws IOException;
}
