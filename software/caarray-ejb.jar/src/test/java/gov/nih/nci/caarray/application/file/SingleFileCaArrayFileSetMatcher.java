//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.application.file;

import gov.nih.nci.caarray.domain.file.CaArrayFile;
import gov.nih.nci.caarray.domain.file.CaArrayFileSet;

import org.mockito.ArgumentMatcher;

/**
 * Matches file sets that contain a single file, by name.
 */
public class SingleFileCaArrayFileSetMatcher extends ArgumentMatcher<CaArrayFileSet> {
    
    private final CaArrayFile expected;

    /**
     * Constructor with expected file.
     * 
     * @param expected expected file during match
     */
    public SingleFileCaArrayFileSetMatcher(CaArrayFile expected) {
        this.expected = expected;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean matches(Object argument) {
        CaArrayFileSet fileSet = (CaArrayFileSet) argument;
        return fileSet.getFiles().size() == 1 && fileSet.getFiles().contains(expected);
    }
}
