//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.util.io;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;

import com.csvreader.CsvReader;

/**
 * Implementation based off of the open source library.  This class is not thread safe.
 *
 * @author Todd Parnell
 */
final class CSVReaderDelimitedFileReader implements DelimitedFileReader {

    private static final Logger LOG = Logger.getLogger(CSVReaderDelimitedFileReader.class);

    private CsvReader reader;
    /** Next values from the <i>client's</i> perspective. */
    private List<String> nextValues;
    private final File file;
    private final char separator;
    private final char delimiter;
    private int currentLineNumber = -1;

    /**
     * Creates a new instance wrapping access to the given <code>File</code>.
     *
     * @param file the delimited file
     * @param separator the field separator
     * @param delimiter the field delimiter
     * @throws IOException if the file couldn't be opened for reading
     */
    CSVReaderDelimitedFileReader(File file, char separator, char delimiter) throws IOException {
        this.file = file;
        this.separator = separator;
        this.delimiter = delimiter;
        reset();
    }

    /**
     * {@inheritDoc}
     */
    public boolean hasNextLine()  {
        return nextValues != null;
    }

    /**
     * {@inheritDoc}
     */
    public List<String> nextLine() throws IOException {
        List<String> result = nextValues;

        try {
            if (reader.readRecord()) {
                String[] values = reader.getValues();
                nextValues = new ArrayList<String>(values.length);
                nextValues.addAll(Arrays.asList(values));
            } else {
                nextValues = null;
                reader.close();
            }
        } catch (IOException ioe) {
            LOG.error("Error during nextLine: " + ioe.getMessage(), ioe);
            nextValues = null;
            reader.close();
            throw ioe;
        }
        currentLineNumber++;
        return result;
    }

    /**
     * {@inheritDoc}
     */
    public void reset() throws IOException {
        if (reader != null) {
            reader.close();
        }
        BufferedReader br = new BufferedReader(new FileReader(file));
        reader = new CsvReader(br, separator);
        reader.setTextQualifier(delimiter);
        reader.setSkipEmptyRecords(false);
        nextLine();
        currentLineNumber = 0;
    }

    /**
     * {@inheritDoc}
     */
    public int getCurrentLineNumber() {
        return currentLineNumber;
    }

    /**
     * {@inheritDoc}
     */
    public void close() {
        if (reader != null) {
            reader.close();
        }
        reader = null;
    }
}
