//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.util.io;

import java.io.File;
import java.io.IOException;

/**
 * Base class for all <code>DelimitedFileReader</code> factory classes.
 */
public abstract class AbstractDelimitedFileReaderFactory implements DelimitedFileReaderFactory {
    
    private static final char TAB = '\t';
    private static final char QUOTE = '"';
    private static final char COMMA = ',';
    
    /**
     * Returns a reader for the specified field separator and field delimiter. As an 
     * example, a standard CSV file would use the comma (',') character as a separator
     * and the quote character ('"') as the delimiter. Note that the delimiter is not
     * required for fields in the file (CSV example line: 123,456,"some text",789.
     * 
     * @param file the file to read.
     * @param separator the field separator.
     * @param delimiter the field delimiter.
     * @return the reader.
     * @throws IOException if the file couldn't be opened for reading
     */
    public abstract DelimitedFileReader getReader(File file, char separator, char delimiter) throws IOException;
    
    /**
     * Returns a reader for standard CSV files.
     * 
     * @param file the CSV file
     * @return the reader.
     * @throws IOException if the file couldn't be opened for reading
     */
    public final DelimitedFileReader getCsvReader(File file) throws IOException {
        return getReader(file, COMMA, QUOTE);
    }
    
    /**
     * Returns a reader for standard tab-delimited files.
     * 
     * @param file the tab-delimited file
     * @return the reader.
     * @throws IOException if the file couldn't be opened for reading
     */
    public final DelimitedFileReader getTabDelimitedReader(File file) throws IOException {
        return getReader(file, TAB, QUOTE);
    }

}
