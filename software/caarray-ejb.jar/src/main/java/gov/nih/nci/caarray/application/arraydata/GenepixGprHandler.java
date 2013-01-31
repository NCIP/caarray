//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.application.arraydata;

import gov.nih.nci.caarray.application.arraydata.genepix.GenepixArrayDataTypes;
import gov.nih.nci.caarray.application.arraydata.genepix.GenepixQuantitationType;
import gov.nih.nci.caarray.application.arraydesign.ArrayDesignService;
import gov.nih.nci.caarray.domain.AbstractCaArrayEntity;
import gov.nih.nci.caarray.domain.array.ArrayDesign;
import gov.nih.nci.caarray.domain.array.ArrayDesignDetails;
import gov.nih.nci.caarray.domain.data.ArrayDataTypeDescriptor;
import gov.nih.nci.caarray.domain.data.DataSet;
import gov.nih.nci.caarray.domain.data.DesignElementList;
import gov.nih.nci.caarray.domain.data.DesignElementType;
import gov.nih.nci.caarray.domain.data.HybridizationData;
import gov.nih.nci.caarray.domain.data.QuantitationType;
import gov.nih.nci.caarray.domain.data.QuantitationTypeDescriptor;
import gov.nih.nci.caarray.domain.file.CaArrayFile;
import gov.nih.nci.caarray.magetab.MageTabDocumentSet;
import gov.nih.nci.caarray.magetab.sdrf.Sample;
import gov.nih.nci.caarray.util.io.DelimitedFileReader;
import gov.nih.nci.caarray.util.io.DelimitedFileReaderFactory;
import gov.nih.nci.caarray.validation.FileValidationResult;
import gov.nih.nci.caarray.validation.ValidationMessage.Type;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

/**
 * Validates and reads data from all versions of Genepix GPR data files.
 */
@SuppressWarnings({ "PMD.CyclomaticComplexity", "PMD.ExcessiveClassLength" })
final class GenepixGprHandler extends AbstractDataFileHandler {
    private static final String LSID_AUTHORITY = AbstractCaArrayEntity.CAARRAY_LSID_AUTHORITY;
    private static final String LSID_NAMESPACE = AbstractCaArrayEntity.CAARRAY_LSID_NAMESPACE;

    private static final int REQUIRED_INITIAL_ROW_HEADER_LENGTH = 3;
    private static final String WAVELENGTHS_HEADER = "Wavelengths";
    private static final String IMAGE_NAME_HEADER = "ImageName";
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
    private static final String NOT_APPLICATBLE_INDICATOR = "n/a";

    private static final Logger LOG = Logger.getLogger(GenepixGprHandler.class);
    private static final Map<String, QuantitationTypeDescriptor> NAME_TO_TYPE_MAP;
    private static final Set<String> STANDARD_HEADERS;

    static {
        Map<String, QuantitationTypeDescriptor> tmpMap = new HashMap<String, QuantitationTypeDescriptor>();
        for (QuantitationTypeDescriptor descriptor : GenepixQuantitationType.values()) {
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

        Set<String> tmpSet = new HashSet<String>();
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

    @Override
    ArrayDataTypeDescriptor getArrayDataTypeDescriptor(File dataFile) {
        return GenepixArrayDataTypes.GENEPIX_EXPRESSION;
    }

    @Override
    Logger getLog() {
        return LOG;
    }

    @Override
    QuantitationTypeDescriptor[] getQuantitationTypeDescriptors(File file) {
        DelimitedFileReader reader = getReader(file);
        try {
            return getQuantitationTypeDescriptors(reader);
        } catch (IOException e) {
            throw new IllegalStateException(READ_FILE_ERROR_MESSAGE, e);
        } finally {
            reader.close();
        }
    }

    private QuantitationTypeDescriptor[] getQuantitationTypeDescriptors(List<String> headers) {
        List<QuantitationTypeDescriptor> descriptorList = new ArrayList<QuantitationTypeDescriptor>();
        for (String header : headers) {
            if (NAME_TO_TYPE_MAP.containsKey(header)) {
                descriptorList.add(NAME_TO_TYPE_MAP.get(header));
            }
        }
        return descriptorList.toArray(new QuantitationTypeDescriptor[] {});
    }

    private Map<String, QuantitationTypeDescriptor> getHeaderToDescriptorMap(List<String> headers) {
        Map<String, QuantitationTypeDescriptor> headerDescriptorMap = new HashMap<String, QuantitationTypeDescriptor>();
        for (String header : headers) {
            headerDescriptorMap.put(header, NAME_TO_TYPE_MAP.get(header));
        }
        return headerDescriptorMap;
    }

    private List<String> getColumnHeaders(DelimitedFileReader reader) throws IOException {
        reset(reader);
        while (reader.hasNextLine()) {
            List<String> values = reader.nextLine();
            if (areColumnHeaders(values)) {
                return values;
            }
        }
        return null;
    }

    @SuppressWarnings("PMD.PositionLiteralsFirstInComparisons") // PMD check gives false positive
    private boolean areColumnHeaders(List<String> values) {
        return values.size() > REQUIRED_INITIAL_ROW_HEADER_LENGTH
        && BLOCK_HEADER.equals(values.get(0))
        && COLUMN_HEADER.equals(values.get(1))
        && ROW_HEADER.equals(values.get(2));
    }

    private void reset(DelimitedFileReader reader) {
        try {
            reader.reset();
        } catch (IOException e) {
            throw new IllegalStateException(READ_FILE_ERROR_MESSAGE, e);
        }
    }

    private QuantitationTypeDescriptor[] getQuantitationTypeDescriptors(DelimitedFileReader reader) throws IOException {
        return getQuantitationTypeDescriptors(getColumnHeaders(reader));
    }

    private void checkSdrfSamples(FileValidationResult result, List<String> fileSampleNames, Set<Sample> sdrfSamples) {
        // get collection of sample names from sdrf as strings
        List<String> sdrfHybStrs = new ArrayList<String>();
        for (Sample sam : sdrfSamples) {
            sdrfHybStrs.add(sam.getName());
        }

        if (!sdrfHybStrs.containsAll(fileSampleNames)) {
            result.addMessage(Type.ERROR, "Data file contains Sample names not referenced in the Sdrf document.");
        }
    }

    private DelimitedFileReader getReader(File file) {
        try {
            return DelimitedFileReaderFactory.INSTANCE.getTabDelimitedReader(file);
        } catch (IOException e) {
            throw new IllegalStateException("Couldn't open file " + file.getName(), e);
        }
    }

    @Override
    List<String> getSampleNames(File dataFile, String hybridizationName) {
        List<String> names = new ArrayList<String>();
        DelimitedFileReader reader = getReader(dataFile);
        try {
            Map<String, String[]> headers = getHeaders(reader);
            names.add("635");
            names.add("532");
            if (headers.containsKey(WAVELENGTHS_HEADER) && headers.get(WAVELENGTHS_HEADER).length > 2) {
                addThreeAndFourColorNames(names, headers.get(WAVELENGTHS_HEADER));
            } else if (headers.containsKey(IMAGE_NAME_HEADER) && headers.get(IMAGE_NAME_HEADER).length > 2) {
                addThreeAndFourColorNames(names, headers.get(IMAGE_NAME_HEADER));
            }
            return names;
        } catch (IOException e) {
            throw new IllegalStateException(READ_FILE_ERROR_MESSAGE, e);
        } finally {
            reader.close();
        }
    }

    private void addThreeAndFourColorNames(List<String> names, String[] values) {
        for (int i = 2; i < values.length; i++) {
            if (!values[i].trim().toLowerCase(Locale.getDefault()).equals(NOT_APPLICATBLE_INDICATOR)) {
                names.add(values[i].replace(' ', '_'));
            }
        }
    }

    private Map<String, String[]> getHeaders(DelimitedFileReader reader) throws IOException {
        reset(reader);
        Map<String, String[]> headers = new HashMap<String, String[]>();
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
        String[] parts = values.get(0).split("=");
        String header = parts[0];
        if (parts.length > 1) {
            String[] headerValues = parts[1].split("\t");
            headers.put(header, headerValues);
        }
    }

    @Override
    void loadData(DataSet dataSet, List<QuantitationType> types, File file, ArrayDesignService arrayDesignService) {
        DelimitedFileReader reader = getReader(file);
        try {
            prepareColumns(dataSet, types, getNumberOfDataRows(reader));
            if (dataSet.getDesignElementList() == null) {
                loadDesignElementList(dataSet, reader, arrayDesignService);
            }
            Set<QuantitationTypeDescriptor> descriptorSet = getDescriptorSet(types);
            for (HybridizationData hybridizationData : dataSet.getHybridizationDataList()) {
                loadData(hybridizationData, descriptorSet, reader);
            }
        } catch (IOException e) {
            throw new IllegalStateException(READ_FILE_ERROR_MESSAGE, e);
        } finally {
            reader.close();
        }
    }

    private void loadDesignElementList(DataSet dataSet, DelimitedFileReader reader,
            ArrayDesignService arrayDesignService) throws IOException {
        DesignElementList probeList = new DesignElementList();
        probeList.setDesignElementTypeEnum(DesignElementType.PHYSICAL_PROBE);
        dataSet.setDesignElementList(probeList);
        ArrayDesignDetails designDetails = getArrayDesign(arrayDesignService, reader).getDesignDetails();
        ProbeLookup probeLookup = new ProbeLookup(designDetails.getProbes());
        List<String> headers = getColumnHeaders(reader);
        int idIndex = headers.indexOf(ID_HEADER);
        while (reader.hasNextLine()) {
            List<String> values = reader.nextLine();
            String probeName = values.get(idIndex);
            probeList.getDesignElements().add(probeLookup.getProbe(probeName));
        }
    }

    private Set<QuantitationTypeDescriptor> getDescriptorSet(List<QuantitationType> types) {
        Set<QuantitationTypeDescriptor> descriptors = new HashSet<QuantitationTypeDescriptor>();
        for (QuantitationType type : types) {
            descriptors.add(NAME_TO_TYPE_MAP.get(type.getName()));
        }
        return descriptors;
    }

    private void loadData(HybridizationData hybridizationData, Set<QuantitationTypeDescriptor> descriptors,
            DelimitedFileReader reader) throws IOException {
        List<String> headers = getColumnHeaders(reader);
        int rowIndex = 0;
        while (reader.hasNextLine()) {
            List<String> values = reader.nextLine();
            loadData(hybridizationData, descriptors, values, headers, rowIndex++);
        }
    }

    private void loadData(HybridizationData hybridizationData, Set<QuantitationTypeDescriptor> descriptors,
            List<String> values, List<String> headers, int rowIndex) {
        for (int valueIndex = 0; valueIndex < values.size(); valueIndex++) {
            QuantitationTypeDescriptor valueType = NAME_TO_TYPE_MAP.get(headers.get(valueIndex));
            if (descriptors.contains(valueType)) {
               setValue(hybridizationData.getColumn(valueType), rowIndex, values.get(valueIndex));
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

    @Override
    void validate(CaArrayFile caArrayFile, File file, MageTabDocumentSet mTabSet, FileValidationResult result,
            ArrayDesignService arrayDesignService) {
        DelimitedFileReader reader = getReader(file);
        try {
            validateHeader(reader, result);
            if (mTabSet.getSdrfDocuments() != null && !mTabSet.getSdrfDocuments().isEmpty()) {
                checkSdrfSamples(result, getSampleNames(file, null), mTabSet.getSdrfSamples());
            }

            if (result.isValid()) {
                validateData(reader, result);
            }
        } catch (IOException e) {
            throw new IllegalStateException(READ_FILE_ERROR_MESSAGE, e);
        } finally {
            reader.close();
        }
    }

    private void validateData(DelimitedFileReader reader, FileValidationResult result) throws IOException {
        List<String> headers = getColumnHeaders(reader);
        Map<String, QuantitationTypeDescriptor> headerToDescriptorMap = getHeaderToDescriptorMap(headers);
        while (reader.hasNextLine()) {
            List<String> values = reader.nextLine();
            if (values.size() != headers.size()) {
                result.addMessage(Type.ERROR, "Invalid number of values in data row, expected "
                        + headers.size() + " but contained " + values.size(), reader.getCurrentLineNumber(), 0);
            } else {
                validateValues(values, headers, headerToDescriptorMap, result, reader.getCurrentLineNumber());
            }
        }
    }

    private void validateValues(List<String> values, List<String> headers,
            Map<String, QuantitationTypeDescriptor> headerToDescriptorMap, FileValidationResult result, int line) {
        for (int columnIndex = 0; columnIndex < values.size(); columnIndex++) {
            if (isQuantitation(headers.get(columnIndex), headerToDescriptorMap)) {
                validateQuantitation(values.get(columnIndex),
                        headerToDescriptorMap.get(headers.get(columnIndex)), result, line, columnIndex + 1);
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
        case CHARACTER:
            validateCharacter(value, result, line, column);
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
            result.addMessage(Type.ERROR,
                    "Invalid boolean value: " + value + ". Legal values are 0 or 1.", line, column);
        }
    }

    private void validateCharacter(String value, FileValidationResult result, int line, int column) {
        if (value.length() != 1) {
            result.addMessage(
                    Type.ERROR, "Invalid character value: " + value + ". Must be exactly one character.", line, column);
        }
    }

    private void validateDouble(String value, FileValidationResult result, int line, int column) {
        if (!ERROR_INDICATOR.equals(value)) {
            try {
                Double.parseDouble(value);
            } catch (NumberFormatException e) {
                result.addMessage(Type.ERROR, "Invalid double value: " + value
                        + ". Must be a valid floating point number.", line, column);
            }
        }
    }

    private void validateFloat(String value, FileValidationResult result, int line, int column) {
        if (!ERROR_INDICATOR.equals(value)) {
            try {
                Float.parseFloat(value);
            } catch (NumberFormatException e) {
                result.addMessage(Type.ERROR, "Invalid float value: " + value
                        + ". Must be a valid floating point number.", line, column);
            }
        }
    }

    private void validateInteger(String value, FileValidationResult result, int line, int column) {
        try {
            Integer.parseInt(value);
        } catch (NumberFormatException e) {
            result.addMessage(Type.ERROR, "Invalid integer value: " + value
                    + ". Must be a valid integer.", line, column);
        }
    }

    private void validateLong(String value, FileValidationResult result, int line, int column) {
        try {
            Integer.parseInt(value);
        } catch (NumberFormatException e) {
            result.addMessage(Type.ERROR, "Invalid integer value: " + value
                    + ". Must be a valid integer.", line, column);
        }
    }

    private void validateShort(String value, FileValidationResult result, int line, int column) {
        try {
            Integer.parseInt(value);
        } catch (NumberFormatException e) {
            result.addMessage(Type.ERROR, "Invalid integer value: " + value
                    + ". Must be a valid integer.", line, column);
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
        String errorMessage = "GPR file must contain two tab-separated integer values on the second line "
            + "corresponding to the number of optional header records and data field columns";
        List<String> values = reader.nextLine();
        if (values.size() != 2) {
            result.addMessage(Type.ERROR, errorMessage);
            return;
        }
        try {
            Integer.parseInt(values.get(0).trim());
        } catch (NumberFormatException e) {
            result.addMessage(Type.ERROR, errorMessage);
        }
        try {
            Integer.parseInt(values.get(1).trim());
        } catch (NumberFormatException e) {
            result.addMessage(Type.ERROR, errorMessage);
        }
    }

    private void validateAtfLine(DelimitedFileReader reader, FileValidationResult result) throws IOException {
        List<String> values = reader.nextLine();
        if (values.size() != 2 || !"ATF".equalsIgnoreCase(values.get(0)) || !values.get(1).startsWith("1")) {
            result.addMessage(Type.ERROR, "GPR file didn't start with \"ATF 1.0\" as required by ATF format");
        }
    }

    @Override
    boolean parseBoolean(String value) {
        return !"0".equals(value);
    }

    @Override
    float parseFloat(String value) {
        if ("Error".equals(value)) {
            return Float.NaN;
        } else {
            return super.parseFloat(value);
        }
    }

    private void validateHasGalFile(DelimitedFileReader reader, FileValidationResult result) throws IOException {
        if (getGalFile(reader) == null) {
            result.addMessage(Type.ERROR, "This file doesn't contain the required header entry \"GalFile\"");
        }
    }

    @Override
    ArrayDesign getArrayDesign(ArrayDesignService arrayDesignService, File file) {
        DelimitedFileReader reader = getReader(file);
        try {
            return getArrayDesign(arrayDesignService, reader);
        } catch (IOException e) {
            throw new IllegalStateException(READ_FILE_ERROR_MESSAGE, e);
        } finally {
            reader.close();
        }
    }

    private ArrayDesign getArrayDesign(ArrayDesignService arrayDesignService, DelimitedFileReader reader)
    throws IOException {
        String galFile = getGalFile(reader);
        String galName = FilenameUtils.getBaseName(galFile);
        return arrayDesignService.getArrayDesign(LSID_AUTHORITY, LSID_NAMESPACE, galName);
    }

    private String getGalFile(DelimitedFileReader reader) throws IOException {
        Map<String, String[]> headers = getHeaders(reader);
        String[] galFileHeader = headers.get(GAL_FILE_HEADER);
        if (galFileHeader == null || galFileHeader.length == 0 || StringUtils.isEmpty(galFileHeader[0])) {
            return null;
        } else {
            return galFileHeader[0].trim();
        }
    }

}
