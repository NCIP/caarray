//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.util.io;

import java.io.OutputStream;
import java.io.Writer;
import java.nio.charset.Charset;

/**
 * A factory that creates a delimited writer.
 *
 * @author Rashmi Srinivasa
 */
public final class DelimitedWriterFactory {
    private DelimitedWriterFactory() {
        // empty private constructor
    }

    /**
     * Returns a writer which will write tab-delimited double-quote-enclosed values to the specified writer.
     *
     * @param writer  the character stream to write to.
     * @return a tab-delimited writer
     * @since 3.2.1
     */
    public static DelimitedWriter getTabDelimitedWriter(Writer writer) {
        return new JavaCsvDelimitedWriter(writer);
    }

    /**
     * Returns a writer which will write tab-delimited double-quote-enclosed values to the specified output stream.
     *
     * @param outputStream the output stream to write to.
     * @param encoding encoding charset for the writer.
     * @return a tab-delimited writer
     * @since 3.2.1
     */
    public static DelimitedWriter getTabDelimitedWriter(OutputStream outputStream, Charset encoding) {
        return new JavaCsvDelimitedWriter(outputStream, encoding);
    }
}
