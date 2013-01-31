//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.util.io;

import java.io.OutputStream;

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
     * Returns a writer which will write to the specified output stream and use the specified delimiter
     * and text qualifier.
     *
     * @param outputStream the output stream to write to.
     * @param delimiter the character to use to separate two values in the same row or line.
     * @param textQualifier the character to use to enclose a single value.
     *
     * @return a delimited writer
     */
    public static DelimitedWriter getWriter(OutputStream outputStream, char delimiter, char textQualifier) {
        return new JavaCsvDelimitedWriter(outputStream, delimiter, textQualifier);
    }

    /**
     * Returns a writer which will write tab-delimited double-quote-enclosed values to the specified output stream.
     *
     * @param outputStream the output stream to write to.
     *
     * @return a tab-delimited writer
     */
    public static DelimitedWriter getTabDelimitedWriter(OutputStream outputStream) {
        return new JavaCsvDelimitedWriter(outputStream);
    }
}
