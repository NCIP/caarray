package gov.nih.nci.caarray.util.io;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.List;

import gov.nih.nci.caarray.test.data.magetab.MageTabDataFiles;

import org.junit.Before;
import org.junit.Test;

public class CSVReaderDelimitedFileReaderTest {

    private final static int NUMBER_OF_LINES = 37;
    private CSVReaderDelimitedFileReader reader;

    @Before
    public void setUp() throws IOException {
        reader = new CSVReaderDelimitedFileReader(MageTabDataFiles.SPECIFICATION_EXAMPLE_IDF, '\t', '"');
    }

    @Test
    public void testNextLine() {
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


}
