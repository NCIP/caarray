//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package gov.nih.nci.copynumberconverter;

/**
 * Specifies copy number data file column mapping info.
 * @author dharley
 *
 */
public class ColumnInfo {
    
    private final String probeNameColumnHeader;
    private final String chromosomeIdColumnHeader;
    private final String chromosomePositionColumnHeader;
    private final String log2RatiosColumnHeader;
    private final String delimiter;
    private final boolean isUsingPrefix;
    private final String hybridizationName;
    
    /**
     * Wrapper object to encapsulate all data column mapping info.
     * 
     * @param probeNameColumnHeader the name of the input file column which contains probe names.
     * @param chromosomeIdColumnHeader the name of the input file column which contains chromosome IDs (can be null).
     * @param chromosomePositionColumnHeader the name of the input file column which contains chromosome positions (can
     * be null).
     * @param log2RatiosColumnHeader the name of the input file column which contains log2ration data.
     * @param delimiter the delimiter used in a multi-hybridization input file to seperate hybridization name from
     * log2RatiosColumnHeader.
     * @param isUsingPrefix indicates whether or not the multi-hybridization input file uses the hybridization name as
     * a prefix (false means it is a suffix, andvalue is ignored in single hybridization cases).
     * @param hybridizationName the name of the hybridization for single-hybridization input files (null indicates
     * multi-hybridization case)
     */
    @SuppressWarnings({ "PMD.ExcessiveParameterList" })
    public ColumnInfo(final String probeNameColumnHeader, final String chromosomeIdColumnHeader,
            final String chromosomePositionColumnHeader, final String log2RatiosColumnHeader,
            final String delimiter, final boolean isUsingPrefix, final String hybridizationName) {
        this.probeNameColumnHeader = probeNameColumnHeader;
        this.chromosomeIdColumnHeader = chromosomeIdColumnHeader;
        this.chromosomePositionColumnHeader = chromosomePositionColumnHeader;
        this.log2RatiosColumnHeader = log2RatiosColumnHeader;
        this.delimiter = delimiter;
        this.isUsingPrefix = isUsingPrefix;
        this.hybridizationName = hybridizationName;
    }

    /**
     * Gets probeNameColumnHeader.
     * @return probeNameColumnHeader the header
     */
    public String getProbeNameColumnHeader() {
        return probeNameColumnHeader;
    }

    /**
     * Gets chromosomeIdColumnHeader.
     * @return chromosomeIdColumnHeader the header
     */
    public String getChromosomeIdColumnHeader() {
        return chromosomeIdColumnHeader;
    }
    
    /**
     * Gets chromosomePositionColumnHeader.
     * @return chromosomePositionColumnHeader the header
     */
    public String getChromosomePositionColumnHeader() {
        return chromosomePositionColumnHeader;
    }

    /**
     * Gets log2RatiosColumnHeader.
     * @return log2RatiosColumnHeader the header
     */
    public String getLog2RatiosColumnHeader() {
        return log2RatiosColumnHeader;
    }

    /**
     * Gets delimiter.
     * @return delimiter the header delimiter
     */
    public String getDelimiter() {
        return delimiter;
    }

    /**
     * Gets isUsingPrefix.
     * @return isUsingPrefix if is using prefix
     */
    public boolean isUsingPrefix() {
        return isUsingPrefix;
    }

    /**
     * Gets hybridizationName.
     * @return hybridizationName the header
    
     */
    public String getHybridizationName() {
        return hybridizationName;
    }
}
