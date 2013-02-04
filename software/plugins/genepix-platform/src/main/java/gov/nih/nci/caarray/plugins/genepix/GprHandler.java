//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package gov.nih.nci.caarray.plugins.genepix;

import gov.nih.nci.caarray.dao.ArrayDao;
import gov.nih.nci.caarray.dao.SearchDao;
import gov.nih.nci.caarray.dataStorage.DataStorageFacade;
import gov.nih.nci.caarray.domain.AbstractCaArrayEntity;
import gov.nih.nci.caarray.domain.LSID;
import gov.nih.nci.caarray.domain.array.ArrayDesign;
import gov.nih.nci.caarray.domain.data.ArrayDataTypeDescriptor;
import gov.nih.nci.caarray.domain.data.DataSet;
import gov.nih.nci.caarray.domain.data.HybridizationData;
import gov.nih.nci.caarray.domain.data.QuantitationType;
import gov.nih.nci.caarray.domain.data.QuantitationTypeDescriptor;
import gov.nih.nci.caarray.domain.file.FileCategory;
import gov.nih.nci.caarray.domain.file.FileType;
import gov.nih.nci.caarray.magetab.MageTabDocumentSet;
import gov.nih.nci.caarray.platforms.AbstractDataFileHandler;
import gov.nih.nci.caarray.platforms.DesignElementBuilder;
import gov.nih.nci.caarray.platforms.ProbeNamesValidator;
import gov.nih.nci.caarray.platforms.ValueParser;
import gov.nih.nci.caarray.validation.FileValidationResult;
import gov.nih.nci.caarray.validation.ValidationMessage.Type;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;

import com.fiveamsolutions.nci.commons.util.io.DelimitedFileReader;
import com.fiveamsolutions.nci.commons.util.io.DelimitedFileReaderFactoryImpl;
import com.google.common.collect.Sets;
import com.google.inject.Inject;

/**
 * Validates and reads data from all versions of Genepix GPR data files.
 */
@SuppressWarnings({"PMD.CyclomaticComplexity", "PMD.ExcessiveClassLength", "PMD.TooManyMethods" })
public final class GprHandler extends AbstractDataFileHandler {
    /**
     * 
     */
    private static final int BATCH_SIZE = 1000;
    private static final String LSID_AUTHORITY = AbstractCaArrayEntity.CAARRAY_LSID_AUTHORITY;
    private static final String LSID_NAMESPACE = AbstractCaArrayEntity.CAARRAY_LSID_NAMESPACE;

    private static final int REQUIRED_INITIAL_ROW_HEADER_LENGTH = 3;
    private static final String GAL_FILE_HEADER = "GalFile";
    private static final String ROW_HEADER = "Row";
    private static final String COLUMN_HEADER = "Column";
    private static final String BLOCK_HEADER = "Block";
    private static final String NAME_HEADER = "Name";
    private static final String ID_HEADER = "ID";
    private static final String X_HEADER = "X";
    private static final String Y_HEADER = "Y";
    private static final String DIA_HEADER = "Dia.";
    private static final String ERROR_INDICATOR = "Error";

    private static final Map<String, QuantitationTypeDescriptor> NAME_TO_TYPE_MAP;
    private static final Set<String> STANDARD_HEADERS;

    static {
        final Map<String, QuantitationTypeDescriptor> tmpMap = new HashMap<String, QuantitationTypeDescriptor>();
        for (final QuantitationTypeDescriptor descriptor : GenepixQuantitationType.values()) {
            tmpMap.put(descriptor.getName(), descriptor);
        }
        tmpMap.put("Log Ratio", GenepixQuantitationType.LOG_RATIO_635_532);
        tmpMap.put("Mean of Ratios", GenepixQuantitationType.MEAN_OF_RATIOS_635_532);
        tmpMap.put("Median of Ratios", GenepixQuantitationType.MEDIAN_OF_RATIOS_635_532);
        tmpMap.put("Ratio of Means", GenepixQuantitationType.RATIO_OF_MEANS_635_532);
        tmpMap.put("Ratio of Medians", GenepixQuantitationType.RATIO_OF_MEDIANS_635_532);
        tmpMap.put("Ratios SD", GenepixQuantitationType.RATIOS_SD_635_532);
        tmpMap.put("Rgn R²", GenepixQuantitationType.RGN_R2_635_532);
        tmpMap.put("Rgn R² (635/532)", GenepixQuantitationType.RGN_R2_635_532);
        tmpMap.put("Rgn R\uFFFD", GenepixQuantitationType.RGN_R2_635_532);
        tmpMap.put("Rgn R\uFFFD (635/532)", GenepixQuantitationType.RGN_R2_635_532);
        tmpMap.put("Rgn Ratio", GenepixQuantitationType.RGN_RATIO_635_532);
        tmpMap.put("Sum of Means", GenepixQuantitationType.SUM_OF_MEANS_635_532);
        tmpMap.put("Sum of Medians", GenepixQuantitationType.SUM_OF_MEDIANS_635_532);
        tmpMap.put("Rgn R² (Ratio/2)", GenepixQuantitationType.RGN_R2_RATIO_2);
        tmpMap.put("Rgn R\uFFFD (Ratio/2)", GenepixQuantitationType.RGN_R2_RATIO_2);
        tmpMap.put("Rgn R² (Ratio/3)", GenepixQuantitationType.RGN_R2_RATIO_3);
        tmpMap.put("Rgn R\uFFFD (Ratio/3)", GenepixQuantitationType.RGN_R2_RATIO_3);
        NAME_TO_TYPE_MAP = Collections.unmodifiableMap(tmpMap);

        final Set<String> tmpSet = new HashSet<String>();
        tmpSet.add(ROW_HEADER);
        tmpSet.add(COLUMN_HEADER);
        tmpSet.add(BLOCK_HEADER);
        tmpSet.add(NAME_HEADER);
        tmpSet.add(ID_HEADER);
        tmpSet.add(X_HEADER);
        tmpSet.add(Y_HEADER);
        tmpSet.add(DIA_HEADER);
        STANDARD_HEADERS = Collections.unmodifiableSet(tmpSet);
    }

    /**
     * File Type for GPR data files.
     */
    public static final FileType GPR_FILE_TYPE = new FileType("GENEPIX_GPR", FileCategory.DERIVED_DATA, true, "GPR");
    static final Set<FileType> SUPPORTED_TYPES = Sets.newHashSet(GPR_FILE_TYPE);

    private final ValueParser valueParser = new GenepixValueParser();
    private final ArrayDao arrayDao;
    private final SearchDao searchDao;

    /**
     * @param dataStorageFacade {@link DataStorageFacade} to use
     * @param arrayDao {@link ArrayDao} to use
     * @param searchDao {@link SearchDao} to use
     */
    @Inject
    GprHandler(DataStorageFacade dataStorageFacade, ArrayDao arrayDao, SearchDao searchDao) {
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
        return GenepixArrayDataTypes.GENEPIX_EXPRESSION;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public QuantitationTypeDescriptor[] getQuantitationTypeDescriptors() {
        final DelimitedFileReader reader = getReader(getFile());
        try {
            return getQuantitationTypeDescriptors(getColumnHeaders(reader));
        } catch (final IOException e) {
            throw new IllegalStateException(READ_FILE_ERROR_MESSAGE, e);
        } finally {
            reader.close();
        }
    }

    private QuantitationTypeDescriptor[] getQuantitationTypeDescriptors(List<String> headers) {
        final List<QuantitationTypeDescriptor> descriptorList = new ArrayList<QuantitationTypeDescriptor>();
        for (final String header : headers) {
            if (NAME_TO_TYPE_MAP.containsKey(header)) {
                descriptorList.add(NAME_TO_TYPE_MAP.get(header));
            }
        }
        return descriptorList.toArray(new QuantitationTypeDescriptor[] {});
    }

    private Map<String, QuantitationTypeDescriptor> getHeaderToDescriptorMap(List<String> headers) {
        final Map<String, QuantitationTypeDescriptor> headerDescriptorMap =
            new HashMap<String, QuantitationTypeDescriptor>();
        for (final String header : headers) {
            headerDescriptorMap.put(header, NAME_TO_TYPE_MAP.get(header));
        }
        return headerDescriptorMap;
    }

    // returns the column headers in the file, and positions reader at start of data
    private List<String> getColumnHeaders(DelimitedFileReader reader) throws IOException {
        reset(reader);
        while (reader.hasNextLine()) {
            final List<String> values = reader.nextLine();
            if (areColumnHeaders(values)) {
                return values;
            }
        }
        throw new IOException("Could not find column headers in file " + getFile().getName());
    }

    @SuppressWarnings("PMD.PositionLiteralsFirstInComparisons")
    // bogus warning from PMD
    private
    boolean areColumnHeaders(List<String> values) {
        return values.size() > REQUIRED_INITIAL_ROW_HEADER_LENGTH && values.contains(BLOCK_HEADER)
        && values.contains(COLUMN_HEADER) && values.contains(ROW_HEADER);
    }

    private void reset(DelimitedFileReader reader) {
        try {
            reader.reset();
        } catch (final IOException e) {
            throw new IllegalStateException(READ_FILE_ERROR_MESSAGE, e);
        }
    }

    private DelimitedFileReader getReader(File file) {
        try {
            return new DelimitedFileReaderFactoryImpl().createTabDelimitedFileReader(file);
        } catch (final IOException e) {
            throw new IllegalStateException("Couldn't open file " + file.getName(), e);
        }
    }

    private Map<String, String[]> getHeaders(DelimitedFileReader reader) throws IOException {
        reset(reader);
        final Map<String, String[]> headers = new HashMap<String, String[]>();
        List<String> values = reader.nextLine();
        while (reader.hasNextLine() && !areColumnHeaders(values)) {
            if (isHeaderLine(values)) {
                addHeader(headers, values);
            }
            values = reader.nextLine();
        }
        return headers;
    }

    private boolean isHeaderLine(List<String> values) {
        return !values.isEmpty() && values.get(0).contains("=");
    }

    private void addHeader(Map<String, String[]> headers, List<String> values) {
        final String[] parts = values.get(0).split("=");
        final String header = parts[0];
        if (parts.length > 1) {
            final String[] headerValues = parts[1].split("\t");
            headers.put(header, headerValues);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void loadData(DataSet dataSet, List<QuantitationType> types, ArrayDesign design) {
        final DelimitedFileReader reader = getReader(getFile());
        try {
            dataSet.prepareColumns(types, getNumberOfDataRows(reader));
            if (dataSet.getDesignElementList() == null) {
                loadDesignElementList(dataSet, reader, design);
            }
            final Set<QuantitationTypeDescriptor> descriptorSet = getDescriptorSet(types);
            for (final HybridizationData hybridizationData : dataSet.getHybridizationDataList()) {
                loadData(hybridizationData, descriptorSet, reader);
            }
        } catch (final IOException e) {
            throw new IllegalStateException(READ_FILE_ERROR_MESSAGE, e);
        } finally {
            reader.close();
        }
    }

    private void loadDesignElementList(DataSet dataSet, DelimitedFileReader reader, ArrayDesign design)
    throws IOException {
        final DesignElementBuilder builder = new DesignElementBuilder(dataSet, design, this.arrayDao, this.searchDao);
        final List<String> headers = getColumnHeaders(reader); // resets reader to start of data
        final int idIndex = headers.indexOf(ID_HEADER);
        while (reader.hasNextLine()) {
            final List<String> values = reader.nextLine();
            final String probeName = values.get(idIndex);
            builder.addProbe(probeName);
        }
        builder.finish();
    }

    private Set<QuantitationTypeDescriptor> getDescriptorSet(List<QuantitationType> types) {
        final Set<QuantitationTypeDescriptor> descriptors = new HashSet<QuantitationTypeDescriptor>();
        for (final QuantitationType type : types) {
            descriptors.add(NAME_TO_TYPE_MAP.get(type.getName()));
        }
        return descriptors;
    }

    private void loadData(HybridizationData hybridizationData, Set<QuantitationTypeDescriptor> descriptors,
            DelimitedFileReader reader) throws IOException {
        final List<String> headers = getColumnHeaders(reader); // resets reader to start of data
        int rowIndex = 0;
        while (reader.hasNextLine()) {
            final List<String> values = reader.nextLine();
            loadData(hybridizationData, descriptors, values, headers, rowIndex++);
        }
    }

    private void loadData(HybridizationData hybridizationData, Set<QuantitationTypeDescriptor> descriptors,
            List<String> values, List<String> headers, int rowIndex) {
        for (int valueIndex = 0; valueIndex < values.size(); valueIndex++) {
            final QuantitationTypeDescriptor valueType = NAME_TO_TYPE_MAP.get(headers.get(valueIndex));
            if (descriptors.contains(valueType)) {
                this.valueParser.setValue(hybridizationData.getColumn(valueType), rowIndex, values.get(valueIndex));
            }
        }
    }

    private int getNumberOfDataRows(DelimitedFileReader reader) throws IOException {
        int numberOfDataRows = 0;
        getColumnHeaders(reader);
        while (reader.hasNextLine()) {
            reader.nextLine();
            numberOfDataRows++;
        }
        return numberOfDataRows;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void validate(MageTabDocumentSet mTabSet, FileValidationResult result, ArrayDesign design) {
        final DelimitedFileReader reader = getReader(getFile());
        try {
            validateHeader(reader, result);
            if (result.isValid()) {
                validateData(reader, result, design);
            }
        } catch (final IOException e) {
            throw new IllegalStateException(READ_FILE_ERROR_MESSAGE, e);
        } finally {
            reader.close();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean requiresMageTab() {
        return true;
    }

    private void validateData(DelimitedFileReader reader, FileValidationResult result, ArrayDesign arrayDesign)
    throws IOException {
        validateProbeNames(reader, arrayDesign, result);
        final List<String> headers = getColumnHeaders(reader);
        final Map<String, QuantitationTypeDescriptor> headerToDescriptorMap = getHeaderToDescriptorMap(headers);
        while (reader.hasNextLine()) {
            final List<String> values = reader.nextLine();
            if (values.size() != headers.size()) {
                result.addMessage(Type.ERROR, "Invalid number of values in data row, expected " + headers.size()
                        + " but contained " + values.size(), reader.getCurrentLineNumber(), 0);
            } else {
                validateValues(values, headers, headerToDescriptorMap, result, reader.getCurrentLineNumber());
            }
        }
    }

    private void validateProbeNames(final DelimitedFileReader delimitedFileReader, final ArrayDesign arrayDesign,
            final FileValidationResult fileValidationResult) throws IOException {
        final ProbeNamesValidator probeNamesValidator = new ProbeNamesValidator(this.arrayDao, arrayDesign);
        final List<String> probeNamesBatch = new ArrayList<String>(BATCH_SIZE);
        int probeCounter = 0;
        final List<String> headers = getColumnHeaders(delimitedFileReader); // resets reader to start of data
        final int idIndex = headers.indexOf(ID_HEADER);
        while (delimitedFileReader.hasNextLine()) {
            final List<String> values = delimitedFileReader.nextLine();
            final String probeName = values.get(idIndex);
            probeNamesBatch.add(probeName);
            probeCounter++;
            if (0 == probeCounter % BATCH_SIZE) {
                probeNamesValidator.validateProbeNames(fileValidationResult, probeNamesBatch);
                probeNamesBatch.clear();
            }
        }
        if (!probeNamesBatch.isEmpty()) {
            probeNamesValidator.validateProbeNames(fileValidationResult, probeNamesBatch);
        }
    }

    private void validateValues(List<String> values, List<String> headers,
            Map<String, QuantitationTypeDescriptor> headerToDescriptorMap, FileValidationResult result, int line) {
        for (int columnIndex = 0; columnIndex < values.size(); columnIndex++) {
            if (isQuantitation(headers.get(columnIndex), headerToDescriptorMap)) {
                validateQuantitation(values.get(columnIndex), headerToDescriptorMap.get(headers.get(columnIndex)),
                        result, line, columnIndex + 1);
            } else if (isStandardColumn(headers.get(columnIndex))) {
                validateStandardColumn(values.get(columnIndex), headers.get(columnIndex), result, line,
                        columnIndex + 1);
            }
        }
    }

    private boolean isQuantitation(String header, Map<String, QuantitationTypeDescriptor> headerToDescriptorMap) {
        return headerToDescriptorMap.get(header) != null;
    }

    private void validateQuantitation(String value, QuantitationTypeDescriptor descriptor, FileValidationResult result,
            int line, int column) {
        switch (descriptor.getDataType()) {
        case BOOLEAN:
            validateBoolean(value, result, line, column);
            break;
        case DOUBLE:
            validateDouble(value, result, line, column);
            break;
        case FLOAT:
            validateFloat(value, result, line, column);
            break;
        case INTEGER:
            validateInteger(value, result, line, column);
            break;
        case LONG:
            validateLong(value, result, line, column);
            break;
        case SHORT:
            validateShort(value, result, line, column);
            break;
        case STRING:
            break; // all values are legal
        default:
            throw new IllegalArgumentException("Invalid data type: " + descriptor.getDataType());
        }
    }

    private void validateBoolean(String value, FileValidationResult result, int line, int column) {
        if (!"0".equals(value) && !"1".equals(value)) {
            result.addMessage(Type.ERROR, "Invalid boolean value: " + value + ". Legal values are 0 or 1.", line,
                    column);
        }
    }

    private void validateDouble(String value, FileValidationResult result, int line, int column) {
        if (!ERROR_INDICATOR.equals(value)) {
            try {
                Double.parseDouble(value);
            } catch (final NumberFormatException e) {
                result.addMessage(Type.ERROR, "Invalid double value: " + value
                        + ". Must be a valid floating point number.", line, column);
            }
        }
    }

    private void validateFloat(String value, FileValidationResult result, int line, int column) {
        if (!ERROR_INDICATOR.equals(value)) {
            try {
                Float.parseFloat(value);
            } catch (final NumberFormatException e) {
                result.addMessage(Type.ERROR, "Invalid float value: " + value
                        + ". Must be a valid floating point number.", line, column);
            }
        }
    }

    private void validateInteger(String value, FileValidationResult result, int line, int column) {
        try {
            Integer.parseInt(value);
        } catch (final NumberFormatException e) {
            result.addMessage(Type.ERROR, "Invalid integer value: " + value + ". Must be a valid integer.", line,
                    column);
        }
    }

    private void validateLong(String value, FileValidationResult result, int line, int column) {
        try {
            Integer.parseInt(value);
        } catch (final NumberFormatException e) {
            result.addMessage(Type.ERROR, "Invalid integer value: " + value + ". Must be a valid integer.", line,
                    column);
        }
    }

    private void validateShort(String value, FileValidationResult result, int line, int column) {
        try {
            Integer.parseInt(value);
        } catch (final NumberFormatException e) {
            result.addMessage(Type.ERROR, "Invalid integer value: " + value + ". Must be a valid integer.", line,
                    column);
        }
    }

    private boolean isStandardColumn(String header) {
        return STANDARD_HEADERS.contains(header);
    }

    @SuppressWarnings("PMD.CyclomaticComplexity")
    private void validateStandardColumn(String value, String header, FileValidationResult result, int line,
            int column) {
        if (BLOCK_HEADER.equals(header)) {
            validateInteger(value, result, line, column);
        } else if (COLUMN_HEADER.equals(header)) {
            validateInteger(value, result, line, column);
        } else if (DIA_HEADER.equals(header)) {
            validateInteger(value, result, line, column);
        } else if (ROW_HEADER.equals(header)) {
            validateInteger(value, result, line, column);
        } else if (X_HEADER.equals(header)) {
            validateInteger(value, result, line, column);
        } else if (Y_HEADER.equals(header)) {
            validateInteger(value, result, line, column);
        }
    }

    private void validateHeader(DelimitedFileReader reader, FileValidationResult result) throws IOException {
        validateAtfLine(reader, result);
        readAndValidateCountLine(reader, result);
        if (getColumnHeaders(reader) == null) {
            result.addMessage(Type.ERROR, "The GPR file doesn't contain a valid header line.");
        } else {
            validateHasGalFile(reader, result);
        }
    }

    private void readAndValidateCountLine(DelimitedFileReader reader, FileValidationResult result) throws IOException {
        final String errorMessage =
            "GPR file must contain two tab-separated integer values on the second line "
            + "corresponding to the number of optional header records and data field columns";
        final List<String> values = reader.nextLine();
        if (values.size() != 2) {
            result.addMessage(Type.ERROR, errorMessage);
            return;
        }
        try {
            Integer.parseInt(values.get(0).trim());
        } catch (final NumberFormatException e) {
            result.addMessage(Type.ERROR, errorMessage);
        }
        try {
            Integer.parseInt(values.get(1).trim());
        } catch (final NumberFormatException e) {
            result.addMessage(Type.ERROR, errorMessage);
        }
    }

    private void validateAtfLine(DelimitedFileReader reader, FileValidationResult result) throws IOException {
        final List<String> values = reader.nextLine();
        if (values.size() != 2 || !"ATF".equalsIgnoreCase(values.get(0)) || !values.get(1).startsWith("1")) {
            result.addMessage(Type.ERROR, "GPR file didn't start with \"ATF 1.0\" as required by ATF format");
        }
    }

    private void validateHasGalFile(DelimitedFileReader reader, FileValidationResult result) throws IOException {
        if (getGalFile(reader) == null) {
            result.addMessage(Type.ERROR, "This file doesn't contain the required header entry \"GalFile\"");
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<LSID> getReferencedArrayDesignCandidateIds() {
        final DelimitedFileReader reader = getReader(getFile());
        try {
            return Collections.singletonList(getReferencedArrayDesignLsid(reader));
        } catch (final IOException e) {
            throw new IllegalStateException(READ_FILE_ERROR_MESSAGE, e);
        } finally {
            reader.close();
        }
    }

    private LSID getReferencedArrayDesignLsid(DelimitedFileReader reader) throws IOException {
        final String galFile = getGalFile(reader);
        final String galName = FilenameUtils.getBaseName(galFile);
        return new LSID(LSID_AUTHORITY, LSID_NAMESPACE, galName);
    }

    private String getGalFile(DelimitedFileReader reader) throws IOException {
        final Map<String, String[]> headers = getHeaders(reader);
        final String[] galFileHeader = headers.get(GAL_FILE_HEADER);
        if (galFileHeader == null || galFileHeader.length == 0 || StringUtils.isEmpty(galFileHeader[0])) {
            return null;
        } else {
            return galFileHeader[0].trim();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean parsesData() {
        return true;
    }

}
