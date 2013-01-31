//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.util.io;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertEquals;
import gov.nih.nci.caarray.AbstractCaarrayTest;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.junit.Before;
import org.junit.Test;

/**
 * Tests the JavaCSV delimited writer.
 *
 * @author Rashmi Srinivasa
 */
public class JavaCsvDelimitedWriterTest extends AbstractCaarrayTest {
    private JavaCsvDelimitedWriter writer;
    private static final String MY_DELIMITER = "\t";
    private final ByteArrayOutputStream outStream = new ByteArrayOutputStream();;

    @Before
    public void setUp() throws IOException {
        // Create a writer with the default delimiter and text qualifier.
        writer = new JavaCsvDelimitedWriter(outStream, Charset.forName("UTF-8"));
    }

    @Test
    public void testWriteLine() throws IOException {
        // Write 2 lines using the writer.
        writeTwoLines();

        // Read 2 lines and ensure they are the same as what was written.
        InputStream inStream = new ByteArrayInputStream(outStream.toByteArray());
        BufferedReader reader = new BufferedReader(new InputStreamReader(inStream, Charset.forName("ISO-8859-1")));

        verifyLineOne(reader);
        verifyLineTwo(reader);

        String line3String = reader.readLine();
        assertNull(line3String);
    }

    private void verifyLineTwo(BufferedReader reader) throws IOException {
        StringTokenizer tokenizer;
        String line2String = reader.readLine();
        assertNotNull(line2String);
        tokenizer = new StringTokenizer(line2String, MY_DELIMITER);
        assertEquals(3, tokenizer.countTokens());
        assertEquals("Row2Col1", tokenizer.nextToken());
        assertEquals("Row2 and Col2", tokenizer.nextToken());
        assertEquals("Row2Col3", tokenizer.nextToken());
    }

    private void verifyLineOne(BufferedReader reader) throws IOException {
        String line1String = reader.readLine();
        assertNotNull(line1String);
        StringTokenizer tokenizer = new StringTokenizer(line1String, MY_DELIMITER);
        assertEquals(2, tokenizer.countTokens());
        assertEquals("Row1Col1", tokenizer.nextToken());
        assertEquals("Row1Col2", tokenizer.nextToken());
    }

    private void writeTwoLines() throws IOException {
        List<String> line1 = new ArrayList<String>();
        line1.add("Row1Col1");
        line1.add("Row1Col2");
        List<String> line2 = new ArrayList<String>();
        line2.add("Row2Col1");
        line2.add("Row2 and Col2");
        line2.add("Row2Col3");
        writer.writeLine(line1);
        writer.writeLine(line2);
        writer.close();
    }
}
