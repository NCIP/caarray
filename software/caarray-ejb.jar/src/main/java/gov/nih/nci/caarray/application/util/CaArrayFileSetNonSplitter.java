//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.application.util;

import gov.nih.nci.caarray.domain.file.CaArrayFileSet;

import java.io.IOException;
import java.util.Set;

import com.google.common.collect.ImmutableSet;

/**
 * Degenerate implementation that doesn't actually split, but rather returns
 * the input back as the only member of the set.
 * 
 * @author tparnell
 */
public class CaArrayFileSetNonSplitter implements CaArrayFileSetSplitter {

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<CaArrayFileSet> split(CaArrayFileSet largeFileSet)
            throws IOException {
        return ImmutableSet.of(largeFileSet);
    }

}
