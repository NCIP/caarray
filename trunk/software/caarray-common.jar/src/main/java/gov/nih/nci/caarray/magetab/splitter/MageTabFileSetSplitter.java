/**
 *  The caArray Software License, Version 1.0
 *
 *  Copyright 2004 5AM Solutions. This software was developed in conjunction
 *  with the National Cancer Institute, and so to the extent government
 *  employees are co-authors, any rights in such works shall be subject to
 *  Title 17 of the United States Code, section 105.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions are met:
 *
 *  1. Redistributions of source code must retain the above copyright notice,
 *  this list of conditions and the disclaimer of Article 3, below.
 *  Redistributions in binary form must reproduce the above copyright notice,
 *  this list of conditions and the following disclaimer in the documentation
 *  and/or other materials provided with the distribution.
 *
 *  2. Affymetrix Pure Java run time library needs to be downloaded from
 *  (http://www.affymetrix.com/support/developer/runtime_libraries/index.affx)
 *  after agreeing to the licensing terms from the Affymetrix.
 *
 *  3. The end-user documentation included with the redistribution, if any,
 *  must include the following acknowledgment:
 *
 *  "This product includes software developed by 5AM Solutions and the National
 *  Cancer Institute (NCI).
 *
 *  If no such end-user documentation is to be included, this acknowledgment
 *  shall appear in the software itself, wherever such third-party
 *  acknowledgments normally appear.
 *
 *  4. The names "The National Cancer Institute", "NCI", and "5AM Solutions"
 *  must not be used to endorse or promote products derived from this software.
 *
 *  5. This license does not authorize the incorporation of this software into
 *  any proprietary programs. This license does not authorize the recipient to
 *  use any trademarks owned by either NCI or 5AM.
 *
 *  6. THIS SOFTWARE IS PROVIDED "AS IS," AND ANY EXPRESSED OR IMPLIED
 *  WARRANTIES, (INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
 *  MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE) ARE DISCLAIMED. IN NO
 *  EVENT SHALL THE NATIONAL CANCER INSTITUTE, SAIC, OR THEIR AFFILIATES BE
 *  LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 *  CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 *  SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 *  INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 *  CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 *  ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 *  POSSIBILITY OF SUCH DAMAGE.
 */
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
