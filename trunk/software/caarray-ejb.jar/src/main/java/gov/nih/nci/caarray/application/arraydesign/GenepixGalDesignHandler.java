//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.application.arraydesign;

import gov.nih.nci.caarray.application.vocabulary.VocabularyService;
import gov.nih.nci.caarray.dao.CaArrayDaoFactory;
import gov.nih.nci.caarray.domain.AbstractCaArrayEntity;
import gov.nih.nci.caarray.domain.array.ArrayDesign;
import gov.nih.nci.caarray.domain.array.ArrayDesignDetails;
import gov.nih.nci.caarray.domain.array.Feature;
import gov.nih.nci.caarray.domain.array.PhysicalProbe;
import gov.nih.nci.caarray.domain.array.ProbeGroup;
import gov.nih.nci.caarray.domain.file.CaArrayFile;
import gov.nih.nci.caarray.util.io.DelimitedFileReader;
import gov.nih.nci.caarray.util.io.DelimitedFileReaderFactory;
import gov.nih.nci.caarray.validation.FileValidationResult;
import gov.nih.nci.caarray.validation.ValidationMessage;
import gov.nih.nci.caarray.validation.ValidationResult;
import gov.nih.nci.caarray.validation.ValidationMessage.Type;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

/**
 * Manages validation and loading of array designs described in the GenePix GAL
 * format.
 */
@SuppressWarnings("PMD")
final class GenepixGalDesignHandler extends AbstractArrayDesignHandler {

    private static final String LSID_AUTHORITY = AbstractCaArrayEntity.CAARRAY_LSID_AUTHORITY;
    private static final String LSID_NAMESPACE = AbstractCaArrayEntity.CAARRAY_LSID_NAMESPACE;

    private static final int NUMBER_OF_BLOCK_INFORMATION_FIELDS = 7;
    private static final Logger LOG = Logger.getLogger(GenepixGalDesignHandler.class);
    private static final String BLOCK_HEADER = "Block";
    private static final String BLOCK_COUNT_HEADER = "BlockCount";
    private static final String BLOCK_TYPE_HEADER = "BlockType";
    private static final String COLUMN_HEADER = "Column";
    private static final String ROW_HEADER = "Row";
    private static final String ID_HEADER = "ID";
    private static final List<String> REQUIRED_DATA_COLUMN_HEADERS = Arrays.asList(
            new String[] {BLOCK_HEADER, COLUMN_HEADER, ROW_HEADER, ID_HEADER});
    private static final String BLOCK_INDICATOR = "Block";

    private final Map<String, Integer> headerToPositionMap = new HashMap<String, Integer>();
    private final List<BlockInfo> blockInfos = new ArrayList<BlockInfo>();
    private final Map<Short, BlockInfo> numberToBlockMap = new HashMap<Short, BlockInfo>();

    GenepixGalDesignHandler(VocabularyService vocabularyService, CaArrayDaoFactory daoFactory, CaArrayFile designFile) {
        super(vocabularyService, daoFactory, designFile);
    }

    @Override
    Logger getLog() {
        return LOG;
    }

    @Override
    void createDesignDetails(ArrayDesign arrayDesign) {
        ArrayDesignDetails details = new ArrayDesignDetails();
        arrayDesign.setDesignDetails(details);
        try {
            loadDesignDetails(details);
        } catch (IOException e) {
            throw new IllegalStateException("Couldn't read file", e);
        }
        getArrayDao().save(arrayDesign);
        getArrayDao().save(arrayDesign.getDesignDetails());
        getArrayDao().flushSession();
    }

    private void loadDesignDetails(ArrayDesignDetails details) throws IOException {
        ProbeGroup group = new ProbeGroup(details);
        details.getProbeGroups().add(group);
        DelimitedFileReader reader = getReader();
        try {
            loadHeaderInformation(reader);
            positionAtDataRecords(reader);
            while (reader.hasNextLine()) {
                addToDetails(details, group, reader.nextLine());
            }
        } finally {
            reader.close();
        }
    }

    private void loadHeaderInformation(DelimitedFileReader reader) throws IOException {
        while (reader.hasNextLine()) {
            List<String> values = reader.nextLine();
            if (isDataHeaderLine(values)) {
                loadHeaderToPositionMap(values);
                break;
            }
            handleHeaderLine(values);
        }
        setBlockColumnsAndRows(reader);
    }

    private void setBlockColumnsAndRows(DelimitedFileReader reader) throws IOException {
        if (!blockInfos.isEmpty()) {
            setBlockInfoFromHeaderData();
        } else {
            setBlockInfoFromDataRows(reader);
        }
    }

    private void setBlockInfoFromDataRows(DelimitedFileReader reader) throws IOException {
        positionAtDataRecords(reader);
        short blockCount = 0;
        while (reader.hasNextLine()) {
            short blockNumber = getBlockNumber(reader.nextLine());
            if (blockNumber > blockCount) {
                blockCount = blockNumber;
            }
        }
        for (short i = 1; i <= blockCount; i++) {
            BlockInfo blockInfo = new BlockInfo(i, 0, 0);
            blockInfo.setBlockColumn(i);
            blockInfo.setBlockRow((short) 1);
            numberToBlockMap.put(i, blockInfo);
        }
    }

    private void setBlockInfoFromHeaderData() {
        short blockRow = 0;
        short blockColumn = 0;
        int currentYOrigin = -1;
        for (BlockInfo blockInfo : blockInfos) {
            if (blockInfo.getYOrigin() > currentYOrigin) {
                blockColumn = 0;
                currentYOrigin = blockInfo.getYOrigin();
                blockRow++;
            }
            blockColumn++;
            blockInfo.setBlockColumn(blockColumn);
            blockInfo.setBlockRow(blockRow);
        }
    }

    private void handleHeaderLine(List<String> values) {
        if (values.get(0).contains("=")) {
            handleHeaderRecord(values.get(0));
        }
    }

    private void handleHeaderRecord(String record) {
        String[] parts = record.split("=");
        String name = parts[0].trim();
        String value = parts[1].trim();
        if (isBlockInformationHeading(name)) {
            handleBlockInformation(name, value);
        }
    }

    private boolean isBlockInformationHeading(String name) {
        return name.startsWith(BLOCK_INDICATOR) && !BLOCK_COUNT_HEADER.equals(name) && !BLOCK_TYPE_HEADER.equals(name);
    }

    private void handleBlockInformation(String name, String value) {
        String[] parts = value.split(",");
        short blockNumber = Short.parseShort(name.substring(BLOCK_INDICATOR.length()));
        int xOrigin = Integer.parseInt(parts[0].trim());
        int yOrigin = Integer.parseInt(parts[1].trim());
        BlockInfo blockInfo = new BlockInfo(blockNumber, xOrigin, yOrigin);
        blockInfos.add(blockInfo);
        numberToBlockMap.put(blockInfo.getBlockNumber(), blockInfo);
    }

    private void addToDetails(ArrayDesignDetails details, ProbeGroup group, List<String> values) {
        Feature feature = new Feature(details);
        feature.setBlockColumn(getBlockColumn(values));
        feature.setBlockRow(getBlockRow(values));
        feature.setColumn(getColumn(values));
        feature.setRow(getRow(values));
        details.getFeatures().add(feature);
        PhysicalProbe probe = new PhysicalProbe(details, group);
        probe.setName(getId(values));
        probe.getFeatures().add(feature);
        details.getProbes().add(probe);
    }

    private String getId(List<String> values) {
        return values.get(headerToPositionMap.get(ID_HEADER));
    }

    private short getBlockColumn(List<String> values) {
        short blockNumber = getBlockNumber(values);
        return numberToBlockMap.get(blockNumber).getBlockColumn();
    }

    private short getBlockRow(List<String> values) {
        short blockNumber = getBlockNumber(values);
        return numberToBlockMap.get(blockNumber).getBlockRow();
    }

    private short getBlockNumber(List<String> values) {
        return Short.parseShort(values.get(headerToPositionMap.get(BLOCK_HEADER)));
    }

    private short getColumn(List<String> values) {
        return Short.parseShort(values.get(headerToPositionMap.get(COLUMN_HEADER)));
    }

    private short getRow(List<String> values) {
        return Short.parseShort(values.get(headerToPositionMap.get(ROW_HEADER)));
    }

    @Override
    void load(ArrayDesign arrayDesign) {
        arrayDesign.setName(FilenameUtils.getBaseName(getDesignFile().getName()));
        arrayDesign.setLsidForEntity(LSID_AUTHORITY + ":" + LSID_NAMESPACE + ":" + arrayDesign.getName());
        try {
            arrayDesign.setNumberOfFeatures(getNumberOfFeatures());
        } catch (IOException e) {
            throw new IllegalStateException("Couldn't read file", e);
        }
    }

    private int getNumberOfFeatures() throws IOException {
        DelimitedFileReader reader = getReader();
        try {
            positionAtDataRecords(reader);
            int numberOfFeatures = 0;
            while (reader.hasNextLine()) {
                reader.nextLine();
                numberOfFeatures++;
            }
            return numberOfFeatures;
        } finally {
            reader.close();
        }
    }

    private void positionAtDataRecords(DelimitedFileReader reader) throws IOException {
        reset(reader);
        boolean isDataHeaderLine = false;
        List<String> values = null;
        while (reader.hasNextLine() && !isDataHeaderLine) {
            values = reader.nextLine();
            isDataHeaderLine = isDataHeaderLine(values);
        }
        if (!isDataHeaderLine) {
            throw new IllegalStateException("Invalid GAL file");
        }
    }

    private void loadHeaderToPositionMap(List<String> values) {
        for (int position = 0; position < values.size(); position++) {
            headerToPositionMap.put(values.get(position), position);
        }
    }

    private boolean isDataHeaderLine(List<String> values) {
        return values.containsAll(REQUIRED_DATA_COLUMN_HEADERS);
    }

    private DelimitedFileReader getReader() {
        try {
            return DelimitedFileReaderFactory.INSTANCE.getTabDelimitedReader(getFile());
        } catch (IOException e) {
            throw new IllegalStateException("Couldn't open file " + getFile().getName(), e);
        }
    }

    @Override
    void validate(ValidationResult result) {
        DelimitedFileReader reader = getReader();
        FileValidationResult fileResult = result.getFileValidationResult(getFile());
        if (fileResult == null) {
            fileResult = new FileValidationResult(getFile());
            result.addFile(getFile(), fileResult);
        }
        try {
            validateFormat(reader, fileResult);
            if (result.isValid()) {
                validateHeader(reader, fileResult);
            }
            if (result.isValid()) {
                validateData(reader, fileResult);
            }
        } catch (IOException e) {
            result.addMessage(getFile(), Type.ERROR, "Could not read file: " + e);
        } finally {
            reader.close();
        }
    }

    private void validateFormat(DelimitedFileReader reader, FileValidationResult result) {
        validateFileNotEmpty(reader, result);
    }

    private void validateDataRows(DelimitedFileReader reader, FileValidationResult result) throws IOException {
        reset(reader);
        int numberOfDataColumns = 0;
        while (reader.hasNextLine()) {
            List<String> values = reader.nextLine();
            if (isDataHeaderLine(values)) {
                numberOfDataColumns = values.size();
                loadHeaderToPositionMap(values);
                break;
            }
        }
        while (reader.hasNextLine()) {
            List<String> values = reader.nextLine();
            if (values.size() != numberOfDataColumns) {
                ValidationMessage message = result.addMessage(Type.ERROR, "Line "
                        + reader.getCurrentLineNumber() + " has an incorrect number of columns");
                message.setLine(reader.getCurrentLineNumber());
            } else {
                validateDataValues(values, result, reader.getCurrentLineNumber());
            }
        }
    }

    private void validateDataValues(List<String> values, FileValidationResult result, int line) {
        validateBlockNumber(values, result, line);
        validateColumn(values, result, line);
        validateRow(values, result, line);
        validateId(values, result, line);
    }

    private void validateBlockNumber(List<String> values, FileValidationResult result, int line) {
        validateShortField(values, BLOCK_HEADER, result, line);
    }

    private void validateShortField(List<String> values, String header, FileValidationResult result, int line) {
        int column = headerToPositionMap.get(header);
        if (!isShort(values.get(column))) {
            ValidationMessage message =
                result.addMessage(Type.ERROR, "Illegal (non-numeric) value for field " + header + " on line " + line);
            message.setLine(line);
            message.setColumn(column);
        }
    }

    private boolean isShort(String value) {
        try {
            Short.parseShort(value);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private boolean isInteger(String value) {
        try {
            Integer.parseInt(value);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private void validateColumn(List<String> values, FileValidationResult result, int line) {
        validateShortField(values, COLUMN_HEADER, result, line);
    }

    private void validateRow(List<String> values, FileValidationResult result, int line) {
        validateShortField(values, ROW_HEADER, result, line);
    }

    private void validateId(List<String> values, FileValidationResult result, int line) {
        int column = headerToPositionMap.get(ID_HEADER);
        if (StringUtils.isBlank(values.get(column))) {
            ValidationMessage message =
                result.addMessage(Type.ERROR, "Missing value for ID field on line " + line);
            message.setLine(line);
            message.setColumn(column);
        }
    }

    private void validateFileHasRowHeader(DelimitedFileReader reader, FileValidationResult result) throws IOException {
        reset(reader);
        while (reader.hasNextLine()) {
            if (isDataHeaderLine(reader.nextLine())) {
                return;
            }
        }
        result.addMessage(Type.ERROR, "The GAL file has no row header line");
    }

    private void validateFileNotEmpty(DelimitedFileReader reader, FileValidationResult result) {
        if (!reader.hasNextLine()) {
            result.addMessage(Type.ERROR, "The GAL file is empty");
        }
    }

    private void validateHeader(DelimitedFileReader reader, FileValidationResult result) throws IOException {
        reset(reader);
        validateFormatHeader(reader, result);
        if (result.isValid()) {
            validateFileHasRowHeader(reader, result);
        }
        if (result.isValid()) {
            validateHeaderFields(reader, result);
        }
        if (result.isValid()) {
            validateBlockInformation(reader, result);
        }
    }

    private void validateFormatHeader(DelimitedFileReader reader, FileValidationResult result) throws IOException {
        reset(reader);
        List<String> values = reader.nextLine();
        if (values.size() < 2 || !"ATF".equals(values.get(0))) {
            result.addMessage(Type.ERROR, "The GAL file doesn't begin with the required header (ATF\t1.0).");
            return;
        }
        if (!reader.hasNextLine()) {
            result.addMessage(Type.ERROR, "The file is only one line long.");
            return;
        }
        values = reader.nextLine();
        if (values.size() < 2) {
            result.addMessage(Type.ERROR, "The second line of the GAL file must contain two numeric fields.");
            return;
        }
    }

    private void validateHeaderFields(DelimitedFileReader reader, FileValidationResult result) throws IOException {
        reset(reader);
        reader.nextLine();
        reader.nextLine();
        while (reader.hasNextLine()) {
            List<String> values = reader.nextLine();
            if (isDataHeaderLine(values)) {
                return;
            }
            if (!values.get(0).contains("=")) {
                ValidationMessage message =
                    result.addMessage(Type.ERROR, "Illegal header line; headers must be of the format <name>=<value>");
                message.setLine(reader.getCurrentLineNumber());
            }
        }
    }

    private void validateBlockInformation(DelimitedFileReader reader, FileValidationResult result) throws IOException {
        reset(reader);
        while (reader.hasNextLine()) {
            List<String> values = reader.nextLine();
            if (isDataHeaderLine(values)) {
                return;
            }
            String name = values.get(0).split("=")[0];
            if (!values.isEmpty() && isBlockInformationHeading(name)) {
                validateBlockInformation(values.get(0), reader.getCurrentLineNumber(), result);
            }
        }
    }

    private void validateBlockInformation(String headerField, int line, FileValidationResult result) {
        String[] parts = headerField.split("=");
        String name = parts[0];
        String information = parts[1];
        if (!isShort(name.substring(BLOCK_INDICATOR.length()))) {
            result.addMessage(Type.ERROR, "Illegal block header name: " + name);
            return;
        }
        validateBlockInformationFields(information.split(","), line, result);
    }

    private void validateBlockInformationFields(String[] values, int line, FileValidationResult result) {
        if (values.length != NUMBER_OF_BLOCK_INFORMATION_FIELDS
                || !isInteger(values[0].trim()) || !isInteger(values[1].trim())) {
            ValidationMessage message =
                result.addMessage(Type.ERROR,
                        "Block information must consist of exactly 7 comma-separated numeric values");
            message.setLine(line);
            message.setColumn(1);
        }
    }

    private void reset(DelimitedFileReader reader) {
        try {
            reader.reset();
        } catch (IOException e) {
            throw new IllegalStateException("Couldn't reset reader", e);
        }
    }

    private void validateData(DelimitedFileReader reader, FileValidationResult result) throws IOException {
        validateDataRows(reader, result);
    }

    /**
     * Used to record location information for Genepix design blocks.
     */
    private static final class BlockInfo {

        private final short blockNumber;
        private short blockColumn;
        private short blockRow;
        private final int xOrigin;
        private final int yOrigin;

        BlockInfo(short blockNumber, int xOrigin, int yOrigin) {
            this.blockNumber = blockNumber;
            this.xOrigin = xOrigin;
            this.yOrigin = yOrigin;
        }

        private short getBlockColumn() {
            return blockColumn;
        }

        private short getBlockNumber() {
            return blockNumber;
        }

        private short getBlockRow() {
            return blockRow;
        }

        int getXOrigin() {
            return xOrigin;
        }

        int getYOrigin() {
            return yOrigin;
        }

        void setBlockColumn(short blockColumn) {
            this.blockColumn = blockColumn;
        }

        void setBlockRow(short blockRow) {
            this.blockRow = blockRow;
        }

    }

}
