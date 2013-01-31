//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.util.io;

import java.io.IOException;
import java.util.List;

/**
 * Interface to read text files with delimiters and separators (e.g. CSV or tab delimited files).
 */
public interface DelimitedFileReader {

    /**
     * Returns true if there are more lines to be read in the file.
     *
     * @return true if more lines, false if at end of file.
     */
    boolean hasNextLine();

    /**
     * Returns the contents of the next line.
     *
     * @return the contents.
     * @throws IOException of there was a problem reading the next line.
     */
    List<String> nextLine() throws IOException;

    /**
     * Returns the line number of the current line read, i.e. from the last call
     * to <code>nextLine()</code>. If no line has been read yet or reader has been reset, returns 0.
     *
     * @return the current line number.
     */
    int getCurrentLineNumber();

    /**
     * Resets the reader to the start of the file.
     *
     * @throws IOException if the reader couldn't be reset.
     */
    void reset() throws IOException;

    /**
     * Closes the reader.
     */
    void close();

}
