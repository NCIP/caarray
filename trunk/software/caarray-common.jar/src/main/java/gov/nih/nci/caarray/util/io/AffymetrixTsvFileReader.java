//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.util.io;

import gov.nih.nci.caarray.util.CaArrayUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.log4j.Logger;

/**
 * A reader for Affymetrix TSV (tab separated values) files. Only v1 and v2 of the format are supported at this time.
 * A sample file:
 *
 * <pre>
 * #%chip_type=HuEx-1_0-st-v2
 * #%chip_type=HuEx-1_0-st-v1
 * #%chip_type=HuEx-1_0-st-ta1
 * #%lib_set_name=HuEx-1_0-st
 * #%lib_set_version=r2
 * #%create_date=Tue Sep 19 15:18:05 PDT 2006
 * #%guid=0000008635-1158704285-0183259307-0389325148-0127012107
 * #%pgf_format_version=1.0
 * #%header0=probeset_id   type
 * #%header1=  atom_id
 * #%header2=      probe_id    type    gc_count    probe_length    interrogation_position  probe_sequence
 * 2590411 main
 *     1
 *         5402769 pm:st   12  25  13  CGAAGTTGTTCATTTCCCCGAAGAC
 *     2
 *         4684894 pm:st   13  25  13  ATGAGGTCACGACGGTAGGACTAAC
 *     3
 *         3869021 pm:st   11  25  13  AGGAGTACAGGGTAAGATATGGTCT
 *     4
 *         3774604 pm:st   14  25  13  CCCCGAAGACCCTAAGATGAGGTCA
 * ...
 * </pre>
 *
 * "#%" indicates header lines, "#" indicates comments, and all else is considered data. See
 * http://www.affymetrix.com/support/developer/powertools/changelog/file-format-tsv.html for the specification.
 *
 * @author Steve Lustbader
 */
@SuppressWarnings({ "PMD.TooManyMethods" })
public class AffymetrixTsvFileReader {

    private static final Logger LOG = Logger.getLogger(AffymetrixTsvFileReader.class);

    private static final String FILE_HEADER_MARKER = "#%";
    private static final String COMMENT_MARKER = "#";
    private static final String COLUMN_HEADER_PREFIX = "header";
    private static final int READ_AHEAD_LIMIT = 1000;

    /**
     * Maps header key -> header value; if the key is listed multiple times, there will be multiple values.
     */
    private final Map<String, List<String>> fileHeaders = new HashMap<String, List<String>>();
    /**
     * Lists of column headers by level; the header0 values are the first element, header1 values are next, etc.
     */
    private final List<List<String>> columnHeaders = new ArrayList<List<String>>();
    private final File tsvFile;
    private BufferedReader fileReader;
    private final String fieldSeparator;
    private String currentLine;
    private Record currentRecord;
    private int recordCount = 0;
    private boolean dequoteData = true;

    /**
     * Construct a new TSV file using the given file.  Assumes tab is the field delimiter and quotes are automatically
     * stripped from data.
     * @param file File object for the TSV file
     * @throws FileNotFoundException if the file does not exist, is a directory rather than a regular file, or for some
     * other reason cannot be opened for reading.
     */
    public AffymetrixTsvFileReader(File file) throws FileNotFoundException {
        this.tsvFile = file;
        this.fileReader = new BufferedReader(new FileReader(tsvFile));
        this.fieldSeparator = "\t";
    }

    /**
     * Construct a new TSV file using the given file.
     * @param file File object for the TSV file
     * @param fieldSeparator field separator to use (eg, tab or comma)
     * @param dequoteData whether to strip quotes (single or double) around data
     * @throws FileNotFoundException if the file does not exist, is a directory rather than a regular file, or for some
     * other reason cannot be opened for reading.
     */
    public AffymetrixTsvFileReader(File file, String fieldSeparator, boolean dequoteData) throws FileNotFoundException {
        this.tsvFile = file;
        this.fileReader = new BufferedReader(new FileReader(tsvFile));
        this.fieldSeparator = fieldSeparator;
        this.dequoteData = dequoteData;
    }

    /**
     * Resets the reader to the start of the file.
     * @throws IOException if the reader couldn't be reset
     */
    public void reset() throws IOException {
        if (fileReader != null) {
            fileReader.close();
        }

        fileReader = new BufferedReader(new FileReader(tsvFile));
    }

    /**
     * Closes the reader.
     * @throws IOException if the reader couldn't be closed
     */
    public void close() throws IOException {
        if (fileReader != null) {
            fileReader.close();
        }
        fileReader = null;
    }

    /**
     * Load and parse just the headers in the TSV file, stopping before the data section.
     * @throws IOException on error reading the headers from the file
     */
    @SuppressWarnings("PMD.ExcessiveMethodLength")
    public void loadHeaders() throws IOException {
        reset();
        fileReader.mark(READ_AHEAD_LIMIT);
        currentLine = fileReader.readLine();
        boolean isHeader = false;
        boolean isComment = false;
        boolean isData = false;
        boolean foundData = false;
        while (currentLine != null) {
            String trimmedLine = currentLine.trim();
            isHeader = trimmedLine.indexOf(FILE_HEADER_MARKER) == 0;
            isComment = trimmedLine.indexOf(COMMENT_MARKER) == 0 || (foundData && isHeader);
            isData = !isHeader && !isComment;
            if (isData && columnHeaders.isEmpty()) {
                // v1 files may not have header# lines, so the first line of data is considered to be the column headers
                isData = false;
                isHeader = true;
            }
            foundData = foundData || isData;

            if (isData) {
                // go back to the previous line so readNextDataLine() will read the first data line
                fileReader.reset();
                break;
            } else if (isHeader) {
                parseHeaderLine();
            }
            fileReader.mark(READ_AHEAD_LIMIT);
            currentLine = fileReader.readLine();
        }
    }

    /**
     * Read the next data line from the file.  If the headers haven't been loaded yet, they will be before the
     * next data line is read.
     * @return the next data record
     * @throws IOException on error reading from the file
     */
    public Record readNextDataLine() throws IOException {
        if (columnHeaders.isEmpty()) {
            loadHeaders();
        }
        String trimmedLine;
        do {
            currentLine = fileReader.readLine();
            trimmedLine = StringUtils.trim(currentLine);
        } while ("".equals(currentLine) || StringUtils.indexOf(trimmedLine, FILE_HEADER_MARKER) > -1
                || StringUtils.indexOf(trimmedLine, COMMENT_MARKER) > -1);

        return parseDataLine();
    }

    private void parseHeaderLine() {
        String trimmedLine = currentLine.trim();
        int fileHeaderMarkerIndex = trimmedLine.indexOf(FILE_HEADER_MARKER);
        if (fileHeaderMarkerIndex < 0) {
            addColumnHeaders(currentLine, 0, dequoteData);
        } else {
            int equalsIndex = trimmedLine.indexOf('=');
            if (equalsIndex > fileHeaderMarkerIndex && equalsIndex < trimmedLine.length() - 1) {
                String headerName = trimmedLine.substring(fileHeaderMarkerIndex + 2, equalsIndex).trim();
                String headerValue = StringUtils.stripEnd(trimmedLine.substring(equalsIndex + 1), null);
                if (headerName.startsWith(COLUMN_HEADER_PREFIX)) {
                    int headerNum = Integer.valueOf(headerName.substring(6));
                    addColumnHeaders(headerValue, headerNum, false);
                } else {
                    List<String> values = fileHeaders.get(headerName);
                    if (values == null) {
                        values = new ArrayList<String>();
                        fileHeaders.put(headerName, values);
                    }
                    values.add(headerValue.trim());
                }
            }
        }
    }

    private void addColumnHeaders(String unparsedColumnHeaders, int headerNum, boolean dequoteColumnNames) {
        String[] columnNames = unparsedColumnHeaders.split(fieldSeparator);
        if (dequoteColumnNames) {
            for (int i = 0; i < columnNames.length; i++) {
                //  erroneous PMD string buffer warning
                columnNames[i] = CaArrayUtils.dequoteString(columnNames[i]); // NOPMD
            }
        }
        List<String> headersList = Arrays.asList((String[]) ArrayUtils.subarray(columnNames, headerNum,
                columnNames.length));
        checkHeaderDepth(headerNum, columnNames);
        columnHeaders.add(headerNum, headersList);
    }

    private void checkHeaderDepth(int headerNum, String[] headers) {
        for (int i = 0; i < headerNum; i++) {
            if (!headers[i].equals("")) {
                LOG.warn("Header line \"" + currentLine + "\" in file " + tsvFile.getName()
                        + " is not indented properly.");
                break;
            }
        }
    }

    private Record parseDataLine() {
        if (StringUtils.isEmpty(currentLine)) {
            return null;
        }
        String[] values = currentLine.split(fieldSeparator);
        int recordLevel = getRecordLevel(values);
        int previousLevel = currentRecord != null ? currentRecord.getRecordLevel() : 0;
        checkRecordLevel(currentLine, recordLevel, previousLevel);

        // PMD erroneously reports a UseStringBufferForStringAppends error here
        values = (String[]) ArrayUtils.subarray(values, recordLevel, values.length); // NOPMD
        Record record = new Record(recordLevel);
        List<String> headers = columnHeaders.get(recordLevel);
        for (int i = 0; i < headers.size(); i++) {
            String header = headers.get(i);
            addDataToRecord(i < values.length ? values[i] : "", record, header);
        }

        currentRecord = record;
        recordCount++;
        return record;
    }

    private void addDataToRecord(String value, Record record, String header) {
        String valueToAdd = value;
        if (dequoteData) {
            valueToAdd = CaArrayUtils.dequoteString(value);  // NOPMD - erroneous string buffer warning
        }
        record.put(header, valueToAdd);
    }

    /**
     * Makes sure the current record level is consistent with the previous level.
     * @throws IllegalStateException if the current level is invalid
     */
    private void checkRecordLevel(String line, int curentRecordLevel, int previousRecordLevel) {
        if (curentRecordLevel - previousRecordLevel > 1 || (currentRecord == null && curentRecordLevel > 0)
                || columnHeaders.size() < curentRecordLevel) {
            throw new IllegalStateException("line \"" + line + "\" is not indented properly in file "
                    + tsvFile.getName());
        }
    }

    /**
     * @return the indentation level of a data record, starting at 0
     */
    private int getRecordLevel(String[] values) {
        int recordLevel = 0;
        for (int i = 0; i < values.length; i++) {
            if (values[i].equals("")) {
                recordLevel++;
            } else {
                break;
            }
        }
        return recordLevel;
    }

    /**
     * @return the fileHeaders
     */
    public Map<String, List<String>> getFileHeaders() {
        return fileHeaders;
    }

    /**
     * Get the value(s) of a header.
     * @param headerName name of header to look up
     * @return the value(s) of that header or null if not set
     */
    public List<String> getFileHeader(String headerName) {
        return fileHeaders.get(headerName);
    }

    /**
     * Get the value of a header as a String when there is only one value for the header.
     * @param headerName name of header to look up
     * @return the value of the header or null if there isn't exactly one value
     */
    public String getFileHeaderAsString(String headerName) {
        List<String> headerVal = fileHeaders.get(headerName);
        String headerValString = null;
        if (headerVal != null && headerVal.size() == 1) {
            headerValString = headerVal.get(0);
        }
        return headerValString;
    }

    /**
     * Get the value of a header as an Integer when there is only one value for the header.
     * @param headerName name of header to look up
     * @return the value of the header or null if there isn't exactly one value or the value cannot be converted
     * to an Integer
     */
    public Integer getFileHeaderAsInteger(String headerName) {
        String fileHeaderAsString = getFileHeaderAsString(headerName);
        try {
            return NumberUtils.createInteger(fileHeaderAsString);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /**
     * Gets the total number of file headers, including any repeated headers.
     *
     * @return count of file headers
     */
    public int getFileHeaderCount() {
        int count = 0;
        for (List<String> headerVal : fileHeaders.values()) {
            count += headerVal.size();
        }
        return count;
    }

    /**
     * @return the columnHeaders
     */
    public List<List<String>> getColumnHeaders() {
        return columnHeaders;
    }

    /**
     * @return the recordCount
     */
    public int getRecordCount() {
        return recordCount;
    }

    /**
     * @return the tsvFile
     */
    public File getFile() {
        return tsvFile;
    }

    /**
     * An individual data record of a TSV file - usually a full data line.
     * @author Steve Lustbader
     */
    public class Record {
        // maps column header -> column value
        private final Map<String, String> values = new HashMap<String, String>();
        private final int recordLevel;

        /**
         * Create a new record at the given level.
         * @param level level of new record within the dataset.
         */
        public Record(int level) {
            recordLevel = level;
        }

        /**
         * Set the value for a given header in this record.
         * @param header column header
         * @param value value to set
         */
        public void put(String header, String value) {
            values.put(header, value);
        }

        /**
         * Get the value for a header in this record.
         * @param header header for which to retrieve the value
         * @return the value
         */
        public String get(String header) {
            return values.get(header);
        }

        /**
         * @return the values
         */
        public Map<String, String> getValues() {
            return values;
        }

        /**
         * Get the depth of the record (starting at 0), indicating which column headers apply.
         * @return the recordLevel
         */
        public int getRecordLevel() {
            return recordLevel;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public String toString() {
            return values.toString();
        }

    }

}
