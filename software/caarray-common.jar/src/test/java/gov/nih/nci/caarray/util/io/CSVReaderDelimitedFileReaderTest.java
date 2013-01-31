//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.util.io;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.List;

import gov.nih.nci.caarray.AbstractCaarrayTest;
import gov.nih.nci.caarray.test.data.magetab.MageTabDataFiles;

import org.junit.Before;
import org.junit.Test;

public class CSVReaderDelimitedFileReaderTest extends AbstractCaarrayTest {

    private final static int NUMBER_OF_LINES = 37;
    private CSVReaderDelimitedFileReader reader;

    @Before
    public void setUp() throws IOException {
        reader = new CSVReaderDelimitedFileReader(MageTabDataFiles.SPECIFICATION_EXAMPLE_IDF, '\t', '"');
    }

    @Test
    public void testNextLine() throws IOException {
        assertTrue(reader.hasNextLine());
        assertEquals(0, reader.getCurrentLineNumber());
        for (int line = 1; line <= NUMBER_OF_LINES; line++) {
            assertTrue(reader.hasNextLine());
            List<String> values = reader.nextLine();
            assertFalse(values.isEmpty());
            assertNotNull(values);
            assertEquals(line, reader.getCurrentLineNumber());
        }
    }

    @Test
    public void testReset() throws IOException {
        assertEquals(0, reader.getCurrentLineNumber());
        assertEquals("Investigation Title", reader.nextLine().get(0));
        reader.nextLine();
        reader.nextLine();
        assertEquals(3, reader.getCurrentLineNumber());
        reader.reset();
        assertEquals(0, reader.getCurrentLineNumber());
        assertEquals("Investigation Title", reader.nextLine().get(0));
    }


}
