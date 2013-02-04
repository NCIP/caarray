//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package gov.nih.nci.caarray.plugins.agilent;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.fiveamsolutions.nci.commons.util.io.DelimitedFileReader;
import com.fiveamsolutions.nci.commons.util.io.DelimitedFileReaderFactoryImpl;

/**
 * Parses tab-delimited Agilent raw text format.
 * 
 * @author jscott
 */
class AgilentTextParser {

    private final DelimitedFileReader reader;
    private List<String> fields;
    private Map<String, Integer> columnIndexMap;
    private String sectionName;

    /**
     * @param reader the input to parse
     * @throws
     */
    AgilentTextParser(final File file) {
        try {
            this.reader =
                    new DelimitedFileReaderFactoryImpl().createTabDelimitedReader(new EndOfLineCorrectingReader(file));
        } catch (final IOException e) {
            throw new ParseException("Could not open tab delimited reader", e);
        }
    }

    /**
     * @return
     */
    boolean hasNext() {
        return this.reader.hasNextLine();
    }

    /**
     * 
     */
    void next() {
        readLine();

        if ("TYPE".equalsIgnoreCase(this.fields.get(0))) {
            readLine();

            this.sectionName = this.fields.get(0);

            this.columnIndexMap = new HashMap<String, Integer>();
            final int numberOfFields = this.fields.size();
            for (int i = 1; i < numberOfFields; i++) {
                this.columnIndexMap.put(this.fields.get(i).toLowerCase(), i);
            }

            readLine();
        }

        if (!"DATA".equalsIgnoreCase(this.fields.get(0))) {
            throw new ParseException("Could not parse file (expected \"DATA\" line.");
        }
    }

    /**
     * @param string
     * @return
     */
    String getStringValue(String columnName) {
        final Integer columnIndex = this.columnIndexMap.get(columnName.toLowerCase(Locale.ENGLISH));
        if (columnIndex == null) {
            return null;
        }
        return this.fields.get(columnIndex.intValue());
    }

    /**
     * @param string
     * @return
     */
    int getIntValue(String columnName) {
        final String v = getStringValue(columnName);
        if (v == null) {
            return 0;
        }
        return Integer.parseInt(v);

    }

    /**
     * @param string
     * @return
     */
    float getFloatValue(String columnName) {
        final String v = getStringValue(columnName);
        if (v == null) {
            return 0.0F;
        }
        return Float.parseFloat(v);
    }

    /**
     * @param columnName name of column.
     * @return true only if the column's value is "1".
     */
    boolean getBooleanValue(String columnName) {
        return "1".equals(getStringValue(columnName));
    }

    /**
     * @return
     */
    String getSectionName() {
        return this.sectionName;
    }

    /**
     * @return
     */
    Collection<String> getColumnNames() {
        return this.columnIndexMap.keySet();
    }

    /**
     * @return current line number.
     */
    int getCurrentLineNumber() {
        return this.reader.getCurrentLineNumber();
    }

    /**
     * @return index of named column, or -1 is not found.
     */
    int getColumnIndex(String columnName) {
        final Integer columnIndex = this.columnIndexMap.get(columnName.toLowerCase(Locale.ENGLISH));
        return columnIndex == null ? -1 : columnIndex.intValue();
    }

    private void readLine() {
        try {
            List<String> newFields;
            do {
                newFields = this.reader.nextLine();
            } while (this.reader.hasNextLine() && newFields.get(0).charAt(0) == ('*'));

            this.fields = newFields;

        } catch (final IOException e) {
            throw new ParseException("Could not read line.", e);
        }
    }

    /**
     * Closes the file.
     */
    public void close() {
        this.reader.close();
    }

    /**
     * Indicates an exception occurred during parsing or the file is malformed.
     * 
     * @author jscott
     * 
     */
    class ParseException extends RuntimeException {
        private static final long serialVersionUID = 1L;

        ParseException(String reason) {
            super(reason);
        }

        ParseException(String reason, Throwable cause) {
            super(reason, cause);
        }
    }
}
