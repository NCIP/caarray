//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package gov.nih.nci.caarray.plugins.copynumber;

import gov.nih.nci.caarray.dao.ArrayDao;
import gov.nih.nci.caarray.dao.SearchDao;
import gov.nih.nci.caarray.dataStorage.DataStorageFacade;
import gov.nih.nci.caarray.domain.LSID;
import gov.nih.nci.caarray.domain.array.ArrayDesign;
import gov.nih.nci.caarray.domain.data.AbstractDataColumn;
import gov.nih.nci.caarray.domain.data.ArrayDataTypeDescriptor;
import gov.nih.nci.caarray.domain.data.DataSet;
import gov.nih.nci.caarray.domain.data.HybridizationData;
import gov.nih.nci.caarray.domain.data.QuantitationType;
import gov.nih.nci.caarray.domain.data.QuantitationTypeDescriptor;
import gov.nih.nci.caarray.domain.file.FileCategory;
import gov.nih.nci.caarray.domain.file.FileType;
import gov.nih.nci.caarray.magetab.MageTabDocumentSet;
import gov.nih.nci.caarray.platforms.AbstractDataFileHandler;
import gov.nih.nci.caarray.platforms.DefaultValueParser;
import gov.nih.nci.caarray.platforms.DesignElementBuilder;
import gov.nih.nci.caarray.platforms.ProbeNamesValidator;
import gov.nih.nci.caarray.platforms.ValueParser;
import gov.nih.nci.caarray.platforms.spi.PlatformFileReadException;
import gov.nih.nci.caarray.validation.FileValidationResult;
import gov.nih.nci.caarray.validation.ValidationMessage.Type;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.fiveamsolutions.nci.commons.util.io.DelimitedFileReader;
import com.fiveamsolutions.nci.commons.util.io.DelimitedFileReaderFactoryImpl;
import com.google.common.collect.Sets;
import com.google.inject.Inject;

/**
 * Handles reading of data matrix copy number data.
 * 
 * @author dharley
 * 
 */
@SuppressWarnings({"PMD.CyclomaticComplexity" })
public final class DataMatrixCopyNumberHandler extends AbstractDataFileHandler {
    private static final Logger LOG = Logger.getLogger(DataMatrixCopyNumberHandler.class);

    /**
     * Warning message when data file has more hybridizations than the split SDRF.
     */
    public static final String PARTIAL_SDRF_WARNING = "Partial SDRF import.  Verify all hybridizations in data "
            + " file are present in full SDRF.";
    
    private static final String HYBRIDIZATION_REF = "Hybridization REF";
    private static final String REPORTER_REF = "Reporter REF";
    private static final String CHROMOSOME = "Chromosome";
    private static final String POSITION = "Position";
    private static final String LOG_2_RATIO = "Log2Ratio";
    private static final int UNINITIALIZED_INDEX = -1;
    private static final int DATA_COLUMN_ORDERED_ARRAY_SIZE = 3;
    private static final int CHROMOSOME_DATA_COLUMN_ARRAY_POSITION = 0;
    private static final int POSITION_DATA_COLUMN_ARRAY_POSITION = 1;
    private static final int LOG2RATIO_DATA_COLUMN_ARRAY_POSITION = 2;
    private static final int BATCH_SIZE = 1000;

    /**
     * File Type for copy number data matrices.
     */
    public static final FileType COPY_NUMBER_FILE_TYPE = new FileType("MAGE_TAB_DATA_MATRIX_COPY_NUMBER",
            FileCategory.DERIVED_DATA, true, true, "CNDATA");

    private static final Set<FileType> SUPPORTED_TYPES = Sets.newHashSet(COPY_NUMBER_FILE_TYPE);

    private int reporterRefColumnIndex = UNINITIALIZED_INDEX;
    private int chromosomeColumnIndex = UNINITIALIZED_INDEX;
    private int positionColumnIndex = UNINITIALIZED_INDEX;
    private Map<String, Integer> hybridizationNamesToColumnIndexesMapping = null;
    private Set<Integer> hybridizationColumnIndexesBasedOnLog2RatioColumnPosition = null;
    private List<String> headersOfColumnsToBeIgnored = null;
    private boolean hasBeenInitialized = false;
    private int numberOfProbes = UNINITIALIZED_INDEX;
    private final ValueParser valueParser = new DefaultValueParser();
    private final ArrayDao arrayDao;
    private final SearchDao searchDao;

    /**
     * @param dataStorageFacade data storage facade to use
     * @param arrayDao arrayDao to use
     * @param searchDao searchDao to use
     */
    @Inject
    protected DataMatrixCopyNumberHandler(DataStorageFacade dataStorageFacade, ArrayDao arrayDao, SearchDao searchDao) {
        super(dataStorageFacade);
        this.arrayDao = arrayDao;
        this.searchDao = searchDao;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<FileType> getSupportedTypes() {
        return SUPPORTED_TYPES;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ArrayDataTypeDescriptor getArrayDataTypeDescriptor() {
        return DataMatrixCopyNumberDataTypes.MAGE_TAB_DATA_MATRIX_COPY_NUMBER;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public QuantitationTypeDescriptor[] getQuantitationTypeDescriptors() {
        return DataMatrixCopyNumberQuantitationTypes.values();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<LSID> getReferencedArrayDesignCandidateIds() throws PlatformFileReadException {
        return new ArrayList<LSID>();
    }

    private DelimitedFileReader getDelimitedFileReader() throws IOException {
        return new DelimitedFileReaderFactoryImpl().createTabDelimitedFileReader(getFile());
    }

    private void initialize() throws PlatformFileReadException {
        if (!this.hasBeenInitialized) {
            DelimitedFileReader delimitedFileReader = null;
            try {
                delimitedFileReader = getDelimitedFileReader();
                readHybridizationRefNamesAndColumnIndexes(delimitedFileReader);
                readDataColumnMappings(delimitedFileReader);
                this.hasBeenInitialized = true;
            } catch (final IOException e) {
                throw new PlatformFileReadException(getFile(), "Cannot initialize: " + e.getMessage(), e);
            } finally {
                if (null != delimitedFileReader) {
                    delimitedFileReader.close();
                }
            }
        }
    }

    private void unInitializeAndClose(DelimitedFileReader delimitedFileReader) {
        this.hasBeenInitialized = false;
        this.hybridizationNamesToColumnIndexesMapping = null;
        this.hybridizationColumnIndexesBasedOnLog2RatioColumnPosition = null;
        this.headersOfColumnsToBeIgnored = null;
        if (null != delimitedFileReader) {
            delimitedFileReader.close();
        }
    }

    private void countProbes(final DelimitedFileReader delimitedFileReader, final ArrayDesign design)
    throws IOException {
        countProbesAndOptionallyValidateProbeNames(false, delimitedFileReader, design, null);
    }

    private void validateProbeNames(final DelimitedFileReader delimitedFileReader, final ArrayDesign design,
            final FileValidationResult fileValidationResult) throws IOException {
        countProbesAndOptionallyValidateProbeNames(true, delimitedFileReader, design, fileValidationResult);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<String> getHybridizationNames() {
        List<String> hybNames = new ArrayList<String>();
        try {
            initialize();
            hybNames.addAll(hybridizationNamesToColumnIndexesMapping.keySet());
            return hybNames;
        } catch (PlatformFileReadException e) {
            LOG.error(e.getMessage(), e.getCause());
            return hybNames;
        } finally {
            unInitializeAndClose(null);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void loadData(DataSet dataSet, List<QuantitationType> types, ArrayDesign design)
    throws PlatformFileReadException {
        DelimitedFileReader delimitedFileReader = null;
        try {
            initialize();
            delimitedFileReader = getDelimitedFileReader();
            countProbes(delimitedFileReader, design);
            dataSet.prepareColumns(types, this.numberOfProbes);
            if (dataSet.getDesignElementList() == null) {
                loadDesignElementList(dataSet, delimitedFileReader, design);
            }
            final Set<QuantitationType> typeSet = new HashSet<QuantitationType>();
            typeSet.addAll(types);
            positionReaderAtStartOfData(delimitedFileReader);
            int rowIndex = 0;
            while (delimitedFileReader.hasNextLine()) {
                final List<String> values = delimitedFileReader.nextLine();
                for (HybridizationData hybridizationData : dataSet.getHybridizationDataList()) {
                    final String hybridization = hybridizationData.getHybridization().getName();
                    final AbstractDataColumn[] orderedColumns = getOrderedColumns(hybridizationData);
                    this.valueParser.setValue(orderedColumns[CHROMOSOME_DATA_COLUMN_ARRAY_POSITION], rowIndex,
                            values.get(this.chromosomeColumnIndex));
                    this.valueParser.setValue(orderedColumns[POSITION_DATA_COLUMN_ARRAY_POSITION], rowIndex,
                            values.get(this.positionColumnIndex));
                    if (this.hybridizationNamesToColumnIndexesMapping.get(hybridization) == null) {
                        throw new PlatformFileReadException(getFile(), "Log2Ratio column not found for hybridization "
                                + hybridization);
                    }
                    this.valueParser.setValue(orderedColumns[LOG2RATIO_DATA_COLUMN_ARRAY_POSITION], rowIndex,
                            values.get(this.hybridizationNamesToColumnIndexesMapping.get(hybridization).intValue()));
                }
                rowIndex++;
            }
        } catch (final IOException e) {
            throw new PlatformFileReadException(getFile(), "", e);
        } finally {
            unInitializeAndClose(delimitedFileReader);
        }
    }

    private AbstractDataColumn[] getOrderedColumns(HybridizationData hybridizationData) {
        boolean chromosomeWasFound = false;
        boolean positionWasFound = false;
        boolean log2RatioWasFound = false;
        final AbstractDataColumn[] orderedColumns = new AbstractDataColumn[DATA_COLUMN_ORDERED_ARRAY_SIZE];
        for (final AbstractDataColumn column : hybridizationData.getColumns()) {
            if (column.getQuantitationType().getName()
                    .equalsIgnoreCase(DataMatrixCopyNumberQuantitationTypes.CHROMOSOME.getName())) {
                orderedColumns[CHROMOSOME_DATA_COLUMN_ARRAY_POSITION] = column;
                chromosomeWasFound = true;
            } else if (column.getQuantitationType().getName()
                    .equalsIgnoreCase(DataMatrixCopyNumberQuantitationTypes.POSITION.getName())) {
                orderedColumns[POSITION_DATA_COLUMN_ARRAY_POSITION] = column;
                positionWasFound = true;
            } else if (column.getQuantitationType().getName()
                    .equalsIgnoreCase(DataMatrixCopyNumberQuantitationTypes.LOG_2_RATIO.getName())) {
                orderedColumns[LOG2RATIO_DATA_COLUMN_ARRAY_POSITION] = column;
                log2RatioWasFound = true;
            }
            if (chromosomeWasFound && positionWasFound && log2RatioWasFound) {
                break;
            }
        }
        return orderedColumns;
    }

    private void loadDesignElementList(DataSet dataSet, DelimitedFileReader delimitedFileReader, ArrayDesign design)
    throws IOException {
        final DesignElementBuilder builder = new DesignElementBuilder(dataSet, design, this.arrayDao, this.searchDao);
        positionReaderAtStartOfData(delimitedFileReader);
        while (delimitedFileReader.hasNextLine()) {
            final List<String> values = delimitedFileReader.nextLine();
            final String probeName = values.get(this.reporterRefColumnIndex);
            builder.addProbe(probeName);
        }
        builder.finish();
    }

    private void readHybridizationRefNamesAndColumnIndexes(DelimitedFileReader reader)
    throws PlatformFileReadException {
        this.hybridizationNamesToColumnIndexesMapping = new HashMap<String, Integer>();
        while (reader.hasNextLine()) {
            List<String> nextLineValues;
            try {
                nextLineValues = reader.nextLine();
            } catch (final IOException e) {
                throw new PlatformFileReadException(getFile(), e.getMessage(), e);
            }
            if (!nextLineValues.isEmpty() && !StringUtils.isEmpty(nextLineValues.get(0))) {
                if (!HYBRIDIZATION_REF.equalsIgnoreCase(nextLineValues.get(0))) {
                    throw new PlatformFileReadException(getFile(), "The '" + HYBRIDIZATION_REF
                            + "' section is missing or incorrectly specified.");
                }
                for (int i = 1; i < nextLineValues.size(); i++) {
                    if (!StringUtils.isEmpty(nextLineValues.get(i))) {
                        this.hybridizationNamesToColumnIndexesMapping.put(nextLineValues.get(i), Integer.valueOf(i));
                    }
                }
            }
            break;
        }
    }

    private boolean listContainsString(String targetString, List<String> listToSearchThrough) {
        for (final String string : listToSearchThrough) {
            if (targetString.equalsIgnoreCase(string)) {
                return true;
            }
        }
        return false;
    }

    private void positionReaderAtStartOfData(DelimitedFileReader delimitedFileReader) throws IOException {
        delimitedFileReader.reset();
        while (delimitedFileReader.hasNextLine()) {
            final List<String> peekLineValues = delimitedFileReader.peek();
            if ((!peekLineValues.isEmpty() && !StringUtils.isEmpty(peekLineValues.get(0)))
                    && (!HYBRIDIZATION_REF.equalsIgnoreCase(peekLineValues.get(0)) && !listContainsString(REPORTER_REF,
                            peekLineValues))) {
                break;
            } else {
                delimitedFileReader.nextLine();
            }
        }
    }

    private void countProbesAndOptionallyValidateProbeNames(final boolean shouldDoValidation,
            final DelimitedFileReader delimitedFileReader, final ArrayDesign arrayDesign,
            final FileValidationResult fileValidationResult) throws IOException {
        positionReaderAtStartOfData(delimitedFileReader);
        final ProbeNamesValidator probeNamesValidator = new ProbeNamesValidator(this.arrayDao, arrayDesign);
        final List<String> probeNamesBatch = new ArrayList<String>(BATCH_SIZE);
        this.numberOfProbes = 0;
        while (delimitedFileReader.hasNextLine()) {
            final List<String> strings = delimitedFileReader.nextLine();
            final String probeName = strings.get(this.reporterRefColumnIndex);
            this.numberOfProbes++;
            if (shouldDoValidation) {
                probeNamesBatch.add(probeName);
                if (0 == this.numberOfProbes % BATCH_SIZE) {
                    probeNamesValidator.validateProbeNames(fileValidationResult, probeNamesBatch);
                    probeNamesBatch.clear();
                }
            }
        }
        if (shouldDoValidation && !probeNamesBatch.isEmpty()) {
            probeNamesValidator.validateProbeNames(fileValidationResult, probeNamesBatch);
        }
    }

    private void readDataColumnMappings(DelimitedFileReader reader) throws IOException, PlatformFileReadException {
        this.headersOfColumnsToBeIgnored = new ArrayList<String>();
        this.hybridizationColumnIndexesBasedOnLog2RatioColumnPosition = new HashSet<Integer>();
        final List<String> columnHeaderStrings = reader.nextLine();
        for (int i = 0; i < columnHeaderStrings.size(); i++) {
            final String columnHeader = columnHeaderStrings.get(i);
            if (REPORTER_REF.equalsIgnoreCase(columnHeader)) {
                this.reporterRefColumnIndex = i;
            } else if (CHROMOSOME.equalsIgnoreCase(columnHeader)) {
                this.chromosomeColumnIndex = i;
            } else if (POSITION.equalsIgnoreCase(columnHeader)) {
                this.positionColumnIndex = i;
            } else if (LOG_2_RATIO.equalsIgnoreCase(columnHeader)) {
                this.hybridizationColumnIndexesBasedOnLog2RatioColumnPosition.add(Integer.valueOf(i));
            } else {
                this.headersOfColumnsToBeIgnored.add(columnHeader);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings({"PMD.NPathComplexity" })
    public void validate(MageTabDocumentSet mTabSet, FileValidationResult result, ArrayDesign design)
    throws PlatformFileReadException {
        initialize();
        if (this.hybridizationNamesToColumnIndexesMapping.size()
                != this.hybridizationColumnIndexesBasedOnLog2RatioColumnPosition.size()) {
            result.addMessage(Type.ERROR, "There must be a one-to-one correspondence between hybridizations and "
                    + LOG_2_RATIO + " columns.");
        } else {
            for (final String hybridizationName : this.hybridizationNamesToColumnIndexesMapping.keySet()) {
                if (!this.hybridizationColumnIndexesBasedOnLog2RatioColumnPosition
                        .contains(this.hybridizationNamesToColumnIndexesMapping.get(hybridizationName))) {
                    result.addMessage(Type.ERROR, "Detected a hybridization name to " + LOG_2_RATIO
                            + " column misalignment for the hybridization '" + hybridizationName + "'.");
                }
            }
        }
        int mageTabHybridizations = 0;
        for (List<?> curList : mTabSet.getSdrfHybridizations().values()) {
            mageTabHybridizations += curList.size();
        }
        if (this.hybridizationNamesToColumnIndexesMapping.size() != mageTabHybridizations) {
            result.addMessage(Type.WARNING, PARTIAL_SDRF_WARNING);
        }
            
        if (UNINITIALIZED_INDEX == this.reporterRefColumnIndex) {
            result.addMessage(Type.ERROR, "No '" + REPORTER_REF + "' column found.");
        }
        if (UNINITIALIZED_INDEX == this.chromosomeColumnIndex) {
            result.addMessage(Type.ERROR, "No '" + CHROMOSOME + "' column found.");
        }
        if (UNINITIALIZED_INDEX == this.positionColumnIndex) {
            result.addMessage(Type.ERROR, "No '" + POSITION + "' column found.");
        }
        for (final String columnName : this.headersOfColumnsToBeIgnored) {
            result.addMessage(Type.WARNING, "The column '" + columnName + "' will be ignored during data parsing.");
        }
        try {
            validateProbeNames(getDelimitedFileReader(), design, result);
        } catch (final IOException e) {
            throw new PlatformFileReadException(getFile(), "Cannot validate probe names: " + e.getMessage(), e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean requiresMageTab() {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean parsesData() {
        return true;
    }
}
