//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.plugins.illumina;

import gov.nih.nci.caarray.validation.FileValidationResult;
import gov.nih.nci.caarray.validation.ValidationMessage.Type;

/**
 *  Allows diffent ways of hadeling error and other messages generated dunring the parsing and verification of the data
 *  file.
 * @author gax
 * @since 2.4.0
 */
public interface MessageHandler {

    /**
     * called when an error is detected while parsing the header.
     * @param msg error message.
     * @param line the responsible line.
     * @param col the responsible column.
     */
    void error(String msg, int line, int col);

    /**
     * called when an warning is detected while parsing the header.
     * @param msg warning message.
     * @param line the responsible line.
     * @param col the responsible column.
     */
    void warn(String msg, int line, int col);

    /**
     * called when an info is detected while parsing the header.
     * @param msg info message.
     * @param line the responsible line.
     * @param col the responsible column.
     */
    void info(String msg, int line, int col);

    /**
     * Throws an IllegalStateException on error, does nothing otherwise.
     */
    public static class DefaultMessageHandler implements MessageHandler {

        /**
         * {@inheritDoc}
         */
        public void error(String msg, int line, int col) {
            throw new IllegalStateException(msg);
        }

        /**
         * {@inheritDoc}
         */
        public void warn(String msg, int line, int col) {
            //
        }

        /**
         * {@inheritDoc}
         */
        public void info(String msg, int line, int col) {
            //
        }
    }

    /**
     * Collects validation messages.
     */
    public static class ValidationMessageHander implements MessageHandler {

        private final FileValidationResult result;

        /**
         * @param result where to collect validation messages.
         */
        public ValidationMessageHander(FileValidationResult result) {
            this.result = result;
        }

        /**
         * {@inheritDoc}
         */
        
        public void error(String msg, int line, int col) {
            result.addMessage(Type.ERROR, msg, line, col);
        }

        /**
         * {@inheritDoc}
         */
        public void warn(String msg, int line, int col) {
            result.addMessage(Type.WARNING, msg, line, col);
        }

        /**
         * {@inheritDoc}
         */
        public void info(String msg, int line, int col) {
            result.addMessage(Type.INFO, msg, line, col);
        }
    }

}
