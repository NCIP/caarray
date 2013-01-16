//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.magetab.splitter;

import static org.junit.Assert.*;
import gov.nih.nci.caarray.magetab.io.FileRef;
import gov.nih.nci.caarray.magetab.io.JavaIOFileRef;
import gov.nih.nci.caarray.test.data.magetab.MageTabDataFiles;

import java.io.File;
import java.io.IOException;
import java.util.Set;

import org.junit.Test;

import com.google.common.collect.ImmutableSet;

/**
 * Tests for the data file finder.
 * 
 * @author tparnell
 */
public class SdrfDataFileFinderTest {
    
    @Test
    public void specificationSdrf() throws IOException {
        File f = MageTabDataFiles.SPECIFICATION_EXAMPLE_SDRF;
        SdrfDataFileFinder finder = new SdrfDataFileFinderImpl();
        FileRef sdrf = new JavaIOFileRef(f);
        Set<String> referenced = finder.identifyReferencedDataFiles(sdrf);
        Set<String> expectedDataFiles = ImmutableSet.of("e-mexp-428data_v1.0.data", "H_TK6 MDR1 replicate 1.CEL", 
                "H_TK6 MDR1 replicate 2.CEL", "H_TK6 MDR1 replicate 3.CEL", "H_TK6 MDR1 replicate 4.CEL",
                "H_TK6 neo replicate 1.CEL", "H_TK6 neo replicate 2.CEL", "H_TK6 neo replicate 3.CEL", 
                "H_TK6 neo replicate 4.CEL", "H_TK6 replicate 1.CEL", "H_TK6 replicate 2.CEL", 
                "H_TK6 replicate 3.CEL", "H_TK6 replicate 4.CEL");
        assertEquals(expectedDataFiles, referenced);
    }
}
