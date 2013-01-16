//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
package gov.nih.nci.copynumberconverter;

import com.fiveamsolutions.nci.commons.util.io.DelimitedFileReader;
import com.fiveamsolutions.nci.commons.util.io.DelimitedFileReaderFactory;
import com.fiveamsolutions.nci.commons.util.io.DelimitedFileWriterFactory;
import com.fiveamsolutions.nci.commons.util.io.DelimitedFilesModule;
import com.fiveamsolutions.nci.commons.util.io.DelimitedWriter;
import com.google.inject.Guice;
import com.google.inject.Injector;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

/**
 * This class does the actual processing for copy number to MAGE-TAB data matrix conversion.
 * @author dharley
 *
 */
@SuppressWarnings({ "PMD.TooManyFields", "PMD.CyclomaticComplexity" })
public final class CopyNumberDocumentConversionProcessImpl implements CopyNumberDocumentConversionProcess {
    
    private static final String HYBRIDIZATION_REF_OUTPUT_COLUMN_HEADER = "Hybridization REF";
    private static final String REPORTER_REF_OUTPUT_COLUMN_HEADER = "Reporter REF";
    private static final String CHROMOSOME_OUTPUT_COLUMN_HEADER = "Chromosome";
    private static final String POSITION_OUTPUT_COLUMN_HEADER = "Position";
    private static final String LOG2RATIO_OUTPUT_COLUMN_HEADER = "Log2Ratio";
    private static final String NULL_STRING = "";
    
    private File inputFile;
    private File outputFile;
    private String probeNameColumnHeader;
    private int probeNameColumnIndex = -1;
    private String chromosomeIdColumnHeader;
    private int chromosomeIdColumnIndex = -1;
    private String chromosomePositionColumnHeader;
    private int chromosomePositionColumnIndex = -1;
    private String log2RatiosColumnHeader;
    private int log2RatiosColumnIndex = -1;
    private String delimiter;
    private boolean isUsingPrefix; //false in multi-hyb situation means using suffix
    private String hybridizationName;
    private int numberOfOutputColumns = 0;
    private int numberOfHybridizations = 0;
    private List<String> hybridizationNamesForCaseOfMultiples;
    private List<Integer> hybridizationColumnIndexes;
    
    /**
     * Creates a new CopyNumberDocumentConversionProcess instance.
     */
    public CopyNumberDocumentConversionProcessImpl() {
        // this is intentionally empty
    }
    
    /**
     * {@inheritDoc}
     */
    public void convert(final String inputFileString, final String outputFileString,
            final ColumnInfo columnInfo) throws ConversionException {
        this.inputFile = new File(inputFileString);
        this.outputFile = new File(outputFileString);
        this.probeNameColumnHeader = columnInfo.getProbeNameColumnHeader();
        this.chromosomeIdColumnHeader = columnInfo.getChromosomeIdColumnHeader();
        this.chromosomePositionColumnHeader = columnInfo.getChromosomePositionColumnHeader();
        this.log2RatiosColumnHeader = columnInfo.getLog2RatiosColumnHeader();
        this.delimiter = columnInfo.getDelimiter();
        this.isUsingPrefix = columnInfo.isUsingPrefix();
        this.hybridizationName = columnInfo.getHybridizationName();
        DelimitedFileReader  tabDelimitedReader = null;
        DelimitedWriter tabDelimitedWriter = null;
        try {
            Injector injector = Guice.createInjector(new DelimitedFilesModule());
            DelimitedFileReaderFactory readerFactory = injector.getInstance(DelimitedFileReaderFactory.class);
            tabDelimitedReader = readerFactory.createTabDelimitedFileReader(inputFile);
            DelimitedFileWriterFactory delimitedFileWriterFactory =
                injector.getInstance(DelimitedFileWriterFactory.class);
            tabDelimitedWriter = delimitedFileWriterFactory.createTabDelimitedWriter(outputFile);
            List<String> headerLine = tabDelimitedReader.nextLine();
            analyzeColumns(headerLine);
            writeHeader(headerLine, tabDelimitedWriter);
            while (tabDelimitedReader.hasNextLine()) {
                tabDelimitedWriter.writeLine(getNewLineOfData(tabDelimitedReader.nextLine()));
            }
        } catch (IOException e) {
            throw new ConversionException("Cannot convert: " + e.getMessage(), e);
        } finally {
            if (null != tabDelimitedReader) {
                tabDelimitedReader.close();
            }
            if (null != tabDelimitedWriter) {
                tabDelimitedWriter.close();
            }
        }
    }
    
    @SuppressWarnings({ "PMD.NPathComplexity" })
    private void analyzeColumns(final List<String> headerLine) throws ConversionException {
        if (!StringUtils.isEmpty(hybridizationName)) {
            numberOfHybridizations = 1;
            int index = 0;
            for (String columnHeader : headerLine) {
                if (columnHeader.equalsIgnoreCase(log2RatiosColumnHeader)) {
                    log2RatiosColumnIndex = index;
                    break;
                }
                index++;
            }
            if (-1 == log2RatiosColumnIndex) {
                throw new ConversionException("Cannot locate " + log2RatiosColumnHeader + " column in single " 
                        + "hybridization file " + inputFile.getAbsolutePath() + ".");
            }
        } else {
            numberOfHybridizations = processMultipleHybridizationsColumnHeaders(headerLine);
        }
        numberOfOutputColumns = numberOfHybridizations;
        int index = 0;
        for (String columnHeader : headerLine) {
            if (columnHeader.equalsIgnoreCase(probeNameColumnHeader)) {
                probeNameColumnIndex = index;
                numberOfOutputColumns++;
            } else if (columnHeader.equalsIgnoreCase(chromosomeIdColumnHeader)) {
                chromosomeIdColumnIndex = index;
                numberOfOutputColumns++;
            } else if (columnHeader.equalsIgnoreCase(chromosomePositionColumnHeader)) {
                chromosomePositionColumnIndex = index;
                numberOfOutputColumns++;
            }
            index++;
        }
        if (-1 == probeNameColumnIndex) {
            throw new ConversionException("Cannot locate " + probeNameColumnHeader + " column in "
                    + "hybridization file " + inputFile.getAbsolutePath() + ".");
        }
        if (-1 == chromosomeIdColumnIndex) {
            throw new ConversionException("Cannot locate " + chromosomeIdColumnHeader + " column in "
                    + "hybridization file " + inputFile.getAbsolutePath() + ".");
        }
        if (-1 == chromosomePositionColumnIndex) {
            throw new ConversionException("Cannot locate " + chromosomePositionColumnHeader + " column in "
                    + "hybridization file " + inputFile.getAbsolutePath() + ".");
        }
    }
    
    @SuppressWarnings({ "PMD.UseLocaleWithCaseConversions" })
    private int processMultipleHybridizationsColumnHeaders(final List<String> headerLine) throws ConversionException {
        int numberOfHybridizationsFound = 0;
        hybridizationNamesForCaseOfMultiples = new ArrayList<String>();
        hybridizationColumnIndexes = new ArrayList<Integer>();
        int index = 0;
        for (String columnHeader : headerLine) {
            if (columnHeader.toUpperCase().indexOf(log2RatiosColumnHeader.toUpperCase()) >= 0) {
                String[] splitColumnHeader = StringUtils.split(columnHeader, delimiter);
                if (splitColumnHeader.length != 2) {
                    throw new ConversionException("'" + columnHeader + "' appears to be an invalid column header "
                            + "as it did not split correctly using the specified delimiter '" + delimiter + "'.");
                } else {
                    if (isUsingPrefix) {
                        hybridizationNamesForCaseOfMultiples.add(splitColumnHeader[0]);
                    } else {
                        hybridizationNamesForCaseOfMultiples.add(splitColumnHeader[1]);
                    }
                }
                numberOfHybridizationsFound++;
                hybridizationColumnIndexes.add(Integer.valueOf(index));
            }
            index++;
        }
        return numberOfHybridizationsFound;
    }
    
    private List<String> getNewLineOfData(final List<String> rawDataLine) {
        List<String> newLineOfData = new ArrayList<String>();
        newLineOfData.add(rawDataLine.get(probeNameColumnIndex));
        if (chromosomeIdColumnIndex != -1) {
            newLineOfData.add(rawDataLine.get(chromosomeIdColumnIndex));
        }
        if (chromosomePositionColumnIndex != -1) {
            newLineOfData.add(rawDataLine.get(chromosomePositionColumnIndex));
        }
        if (numberOfHybridizations > 1) {
            for (int i = 0; i < numberOfHybridizations; i++) {
                newLineOfData.add(rawDataLine.get(hybridizationColumnIndexes.get(i).intValue()));
            }
        } else {
            newLineOfData.add(rawDataLine.get(log2RatiosColumnIndex));
        }
        return newLineOfData;
    }
    
    private void writeHeader(final List<String> inputHeaderLine, final DelimitedWriter delimitedWriter)
        throws IOException {
        List<String> firstHeaderLine = new ArrayList<String>();
        firstHeaderLine.add(HYBRIDIZATION_REF_OUTPUT_COLUMN_HEADER);
        for (int i = 0; i < (numberOfOutputColumns - (numberOfHybridizations + 1)); i++) {
            firstHeaderLine.add(NULL_STRING);
        }
        if (numberOfHybridizations > 1) {
            for (String aHybridizationName : hybridizationNamesForCaseOfMultiples) {
                firstHeaderLine.add(aHybridizationName);
            }
        } else {
            firstHeaderLine.add(hybridizationName);
        }
        delimitedWriter.writeLine(firstHeaderLine);
        firstHeaderLine = null;
        List<String> secondHeaderLine = new ArrayList<String>();
        secondHeaderLine.add(REPORTER_REF_OUTPUT_COLUMN_HEADER);
        secondHeaderLine.add(CHROMOSOME_OUTPUT_COLUMN_HEADER);
        secondHeaderLine.add(POSITION_OUTPUT_COLUMN_HEADER);
        for (int i = 0; i < numberOfHybridizations; i++) {
            secondHeaderLine.add(LOG2RATIO_OUTPUT_COLUMN_HEADER);
        }
        delimitedWriter.writeLine(secondHeaderLine);
    }

}
