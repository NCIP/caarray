//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================

package gov.nih.nci.caarray.plugins.illumina;

import gov.nih.nci.caarray.validation.FileValidationResult;

import java.util.List;

/**
 * Parse tabular data. NOTE: The parsers are generally for a single pass through the data file, and cannot be reused or
 * reset after one pass.
 * 
 * @author gax
 * @since 2.4.0
 */
abstract class AbstractParser {
    private final MessageHandler messages;

    /**
     * @param messages where the parser should send error or validation messages.
     */
    protected AbstractParser(MessageHandler messages) {
        this.messages = messages;
    }

    /**
     * @param result where the paser should collect validation messages (using
     *            {@link MessageHandler.ValidationMessageHander})
     */
    protected AbstractParser(FileValidationResult result) {
        this(new MessageHandler.ValidationMessageHander(result));
    }

    /**
     * Default ctor.
     * 
     * @see MessageHandler.DefaultMessageHandler
     */
    protected AbstractParser() {
        this(new MessageHandler.DefaultMessageHandler());
    }

    /**
     * Parse one row of the data table.
     * 
     * @param row the content of the row.
     * @param lineNum the line number in the file.
     * @return true when the line was parsed correctly, or the rest of the file can be read.
     */
    abstract boolean parse(List<String> row, int lineNum);

    /**
     * called when an error is detected while parsing the header.
     * 
     * @param msg error message.
     * @param line the responsible line.
     * @param col the responsible column.
     */
    protected void error(String msg, int line, int col) {
        this.messages.error(msg, line, col);
    }

    /**
     * called when an warning is detected while parsing the header.
     * 
     * @param msg warning message.
     * @param line the responsible line.
     * @param col the responsible column.
     */
    protected void warn(String msg, int line, int col) {
        this.messages.warn(msg, line, col);
    }

    /**
     * called when an info is detected while parsing the header.
     * 
     * @param msg info message.
     * @param line the responsible line.
     * @param col the responsible column.
     */
    protected void info(String msg, int line, int col) {
        this.messages.info(msg, line, col);
    }

}
