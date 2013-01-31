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
    private final char myDelimiter;
    private final char myTextQualifier;
    private final OutputStream myOutputStream;

    /**
     * Creates a writer which will write to the specified output stream using the default delimiter and text qualifier.
     *
     * @param outStream the OutputStream to write to.
     */
    public JavaCsvDelimitedWriter(OutputStream outStream) {
        myOutputStream = outStream;
        myDelimiter = DEFAULT_DELIMITER;
        myTextQualifier = DEFAULT_TEXT_QUALIFIER;
        init();
    }

    /**
     * Creates a writer which will write to the specified output stream using the specified delimiter
     * and text qualifier.
     *
     * @param outStream the OutputStream to write to.
     * @param delimiter the character to use to delimit columns in a row or line.
     * @param textQualifier the character to use to enclose the value for a single column.
     */
    public JavaCsvDelimitedWriter(OutputStream outStream, char delimiter, char textQualifier) {
        myOutputStream = outStream;
        myDelimiter = delimiter;
        myTextQualifier = textQualifier;
        init();
    }

    private void init() {
        writer = new CsvWriter(myOutputStream, myDelimiter, Charset.forName("ISO-8859-1"));
        writer.setTextQualifier(myTextQualifier);
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
        if (writer != null) {
            writer.flush();
            writer.close();
        }
        writer = null;
    }

    /**
     * Reinitializes the writer, remembering the delimiter, text qualifier and output stream that were in use.
     */
    public void reinit() {
        close();
        init();
    }
}
