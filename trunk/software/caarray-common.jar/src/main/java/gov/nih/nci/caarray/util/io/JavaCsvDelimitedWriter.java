//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.util.io;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.List;

import org.apache.log4j.Logger;

import com.csvreader.CsvWriter;
import java.io.OutputStreamWriter;
import java.io.Writer;

/**
 * A delimited writer that uses the JavaCSV API.
 *
 * @author Rashmi Srinivasa
 */
public class JavaCsvDelimitedWriter implements DelimitedWriter {
    private static final Logger LOG = Logger.getLogger(JavaCsvDelimitedWriter.class);

    private static final char DEFAULT_DELIMITER = '\t';
    private static final char DEFAULT_TEXT_QUALIFIER = '"';

    private CsvWriter writer;

    /**
     * Creates a writer which will write to the specified output stream using the default delimiter, text qualifier,
     * and charset.
     *
     * @param outStream the OutputStream to write to.
     * @param encoding character encoding
     * @since 3.2.1
     */
    public JavaCsvDelimitedWriter(OutputStream outStream, Charset encoding) {
        this(new OutputStreamWriter(outStream, encoding));
    }

    /**
     * Creates a writer which will write to the specified character stream using the default delimiter
     * and text qualifier.
     *
     * @param writer writer to write to.
     * @since 3.2.1
     */
    public JavaCsvDelimitedWriter(Writer writer) {
        this(writer, DEFAULT_DELIMITER, DEFAULT_TEXT_QUALIFIER);
    }

    /**
     * Creates a writer which will write to the specified output stream using the specified delimiter, text qualifier
     * , and charset.
     *
     * @param outStream byte stream the write to.
     * @param encoding charset for the character writer.
     * @param delimiter the character to use to delimit columns in a row or line.
     * @param textQualifier the character to use to enclose the value for a single column.
     * @since 3.2.1
     */
    public JavaCsvDelimitedWriter(OutputStream outStream, Charset encoding, char delimiter, char textQualifier) {
        this(new OutputStreamWriter(outStream, encoding), delimiter, textQualifier);
    }
    
    /**
     * Creates a writer which will write to the specified character stream using the specified delimiter
     * and text qualifier.
     *
     * @param writer  the writer.
     * @param delimiter the character to use to delimit columns in a row or line.
     * @param textQualifier the character to use to enclose the value for a single column.
     */
    public JavaCsvDelimitedWriter(Writer writer, char delimiter, char textQualifier) {
        this.writer = new CsvWriter(writer, delimiter);
        this.writer.setTextQualifier(textQualifier);
    }
    
    /**
     * Writes a line comprised of String values enclosed in the text qualifier and separated by the delimiter.
     *
     * @param valueList a List of String values to be written to a single line or row.
     * @throws IOException if line could not be written.
     */
    public void writeLine(List<String> valueList) throws IOException {
        String[] values = valueList.toArray(new String[valueList.size()]);
        try {
            writer.writeRecord(values);
        } catch (IOException e) {
            LOG.error("Error while writing line: ", e);
            close();
            throw e;
        }
    }

    /**
     * Closes the writer.
     */
    public void close() {
        writer.flush();
        writer.close();
    }
}
