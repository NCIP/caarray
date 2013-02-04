//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================

package gov.nih.nci.caarray.plugins.illumina;

import com.csvreader.CsvReader;


import java.io.IOException;
import java.io.Reader;

/**
 *
 * @author gax
 */
class SectionParser {
    private final CsvReader reader;

    /**
     * @param r the tabular data stream to parse.
     * @param delim delimitor.
     */
    public SectionParser(Reader r, char delim) {
        reader = new CsvReader(r, delim);
        reader.setTextQualifier('"');
    }

    /**
     * @param handler content handler that should receive events.
     * @throws IOException if the stream fails.
     */
    public void parse(ContentHandler handler) throws IOException {
        String currentSection = null;
        boolean keepGoing = true;
        boolean processSection = false;
        boolean isFirstLine = true;
        int lineNumber = 0;
        handler.startDocument();
        while (keepGoing && reader.readRecord()) {
            String[] line = reader.getValues();
            lineNumber++;
            if (isSectionHeader(line)) {
                String section = parseSectionName(line);
                if (currentSection != null) {
                    keepGoing = handler.endSection(currentSection, lineNumber);
                }
                currentSection = section;
                if (keepGoing) {
                     processSection = handler.startSection(currentSection, lineNumber);
                     isFirstLine = true;
                }
            } else if (processSection) {
                if (isFirstLine) {
                    handler.parseFirstRow(line, lineNumber);
                    isFirstLine = false;
                } else {
                    handler.parseRow(line, lineNumber);
                }
            }
        }
        if (currentSection != null) {
            handler.endSection(currentSection, lineNumber);
        }
        handler.endDocument();
    }

    /**
     * @param line row.
     * @return true if this line looks like a section marker.
     */
    protected boolean isSectionHeader(String[] line) {
        return line.length == 1 && line[0].length() > 2 && line[0].startsWith("[") && line[0].endsWith("]");
    }

    /**
     * called after a line has been identified as a section header.
     * @param line row.
     * @return the section marker line w/o "[" and "]" on each end.
     */
    protected String parseSectionName(String[] line) {
        String name = line[0];
        return name.substring(1, name.length() - 1);
    }
}
