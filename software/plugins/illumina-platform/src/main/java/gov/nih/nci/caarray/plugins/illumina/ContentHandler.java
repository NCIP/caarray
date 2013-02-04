//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================

package gov.nih.nci.caarray.plugins.illumina;

/**
 * Content handler for a tabular data stream that contains sections.
 *
 * @author gax
 */
interface ContentHandler {

    /**
     * Called when the parser start processing a document.
     */
    void startDocument();

    /**
     * Called at the begining of a section.
     * @param sectionName the name of the section w/o the leading and trailing brackets.
     * @param lineNumber the line the section starts.
     * @return true if you are interested in parsing this section.
     */
    boolean startSection(String sectionName, int lineNumber);
    
    /**
     * Process the first line in the section. May be a header if you are expecting one in the current section,
     * otherwise, you should pass on the call to {@link #parseRow(java.lang.String[])}.
     * @param values the column values in the line.
     * @param lineNumber the line number of the values.
     */
    void parseFirstRow(String[] values, int lineNumber);
    /**
     * Process a row in the current section.
     * @param values the column values in the line.
     * @param lineNumber the line number of the values.
     */
    void parseRow(String[] values, int lineNumber);

    /**
     * Called when a section ends. A section ends when a new one begins, or stream ends.
     * @param sectionName name of section that ended.
     * @param lineNumber line number where the section ends.
     * @return true if you are want the parser to keep processing subsequent sections, false if you want to stop
     * looking for other sections in the stream
     */
    boolean endSection(String sectionName, int lineNumber);

    /**
     * called at the end of the stream.
     */
    void endDocument();
}
