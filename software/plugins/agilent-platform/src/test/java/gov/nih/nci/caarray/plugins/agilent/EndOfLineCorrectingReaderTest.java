//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.plugins.agilent;

import static org.junit.Assert.assertTrue;

import gov.nih.nci.caarray.plugins.agilent.EndOfLineCorrectingReader;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;

import org.apache.commons.io.IOUtils;
import org.junit.Test;

/**
 * @author jscott
 *
 */
public class EndOfLineCorrectingReaderTest {
    private final String crLf = "\015\012";
    private final String crCrLf = "\015\015\012";
    private String originalData = String.format("%sline two%sline three%s", crCrLf, crCrLf, crCrLf) ;
    private String expectedData = originalData.replace(crCrLf, crLf);
    
    @Test
    public void replacesCrCrLfWithCrLf() throws IOException {
        File testFile = createTestFile();
        
        EndOfLineCorrectingReader sut = new EndOfLineCorrectingReader(testFile);
        
        StringReader expectedDataReader = new StringReader(expectedData);
        
        assertTrue(IOUtils.contentEquals(expectedDataReader, sut));
    }
    
    private File createTestFile() throws IOException {
         File file = File.createTempFile("EndOfLineCorrectingReaderTest", null);
        file.deleteOnExit();
        
        final FileWriter fileWriter = new FileWriter(file);
        try {
            IOUtils.copy(new StringReader(originalData), fileWriter);
        } finally {
            fileWriter.close();
        }
        
        return file;
    }

}
