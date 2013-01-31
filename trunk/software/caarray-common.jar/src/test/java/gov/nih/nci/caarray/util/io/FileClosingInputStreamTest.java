//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.util.io;

import static org.junit.Assert.assertTrue;

import gov.nih.nci.caarray.AbstractCaarrayTest;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.junit.Test;

/**
 * Test cases for the file closing input stream.
 */
public class FileClosingInputStreamTest extends AbstractCaarrayTest {

    @Test
    public void testStream() throws IOException {
        File f = java.io.File.createTempFile("pre", ".txt");
        f.createNewFile();
        // write a bunch of bytes to it
        OutputStream os = new FileOutputStream(f);
        for (int i = 0; i < 10000; ++i) {
            os.write(i);
        }
        os.close();

        InputStream is = new FileClosingInputStream(new BufferedInputStream(new FileInputStream(f)), f);
        assertTrue(is.available() > 0);
        assertTrue(is.markSupported());
        // exercise the api
        is.mark(10);
        is.read(new byte[5]);
        is.read(new byte[5], 0, 4);
        is.reset();
        is.skip(5);
        is.read();
        is.read();
        assertTrue(f.exists());
        is.close(); // here's what we really care about
        assertTrue(!f.exists());
    }
}
