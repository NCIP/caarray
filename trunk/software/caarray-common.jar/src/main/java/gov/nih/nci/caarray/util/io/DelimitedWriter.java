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
 * Interface to write text files with delimiters (e.g. CSV or tab delmited files).
 *
 * @author Rashmi Srinivasa
 */
public interface DelimitedWriter {
    /**
     * Writes a line comprised of String values separated by the delimiter.
     *
     * @param valueList the list of Strings to be written as a single line.
     * @throws IOException if the line could not be written.
     */
    void writeLine(List<String> valueList) throws IOException;

    /**
     * Closes the writer.
     */
    void close();

}
