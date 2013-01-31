//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.application.arraydata;

import gov.nih.nci.caarray.application.arraydata.illumina.IlluminaArrayDataTypes;
import gov.nih.nci.caarray.application.arraydata.illumina.IlluminaExpressionQuantitationType;
import gov.nih.nci.caarray.application.arraydata.illumina.IlluminaGenotypingQuantitationType;
import gov.nih.nci.caarray.application.arraydesign.ArrayDesignService;
import gov.nih.nci.caarray.domain.array.ArrayDesign;
import gov.nih.nci.caarray.domain.array.ArrayDesignDetails;
import gov.nih.nci.caarray.domain.data.AbstractDataColumn;
import gov.nih.nci.caarray.domain.data.ArrayDataTypeDescriptor;
import gov.nih.nci.caarray.domain.data.DataSet;
import gov.nih.nci.caarray.domain.data.DesignElementList;
import gov.nih.nci.caarray.domain.data.DesignElementType;
import gov.nih.nci.caarray.domain.data.HybridizationData;
import gov.nih.nci.caarray.domain.data.QuantitationType;
import gov.nih.nci.caarray.domain.data.QuantitationTypeDescriptor;
import gov.nih.nci.caarray.domain.file.CaArrayFile;
import gov.nih.nci.caarray.magetab.MageTabDocumentSet;
import gov.nih.nci.caarray.magetab.sdrf.Hybridization;
import gov.nih.nci.caarray.util.io.DelimitedFileReader;
import gov.nih.nci.caarray.util.io.DelimitedFileReaderFactory;
import gov.nih.nci.caarray.validation.FileValidationResult;
import gov.nih.nci.caarray.validation.ValidationMessage;
import gov.nih.nci.caarray.validation.ValidationMessage.Type;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
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
 * Handles reading of Illumina data.
 */
class IlluminaDataHandler extends AbstractDataFileHandler {
    private static final String TARGET_ID = "TargetID";
    private static final String ARRAY_CONTENT_HEADER = "Array Content";
    private static final String LSID_AUTHORITY = "illumina.com";
    private static final String LSID_NAMESPACE = "PhysicalArrayDesign";
    private static final String GROUP_ID_HEADER = "GroupID";
    private static final Map<String, IlluminaExpressionQuantitationType> EXPRESSION_TYPE_MAP =
        new HashMap<String, IlluminaExpressionQuantitationType>();
    private static final Map<String, IlluminaGenotypingQuantitationType> SNP_TYPE_MAP =
        new HashMap<String, IlluminaGenotypingQuantitationType>();
    private static final Logger LOG = Logger.getLogger(IlluminaDataHandler.class);

    static {
        initializeExpressionTypeMap();
        initializeSnpTypeMap();
    }

    private static void initializeExpressionTypeMap() {
        for (IlluminaExpressionQuantitationType descriptor : IlluminaExpressionQuantitationType.values()) {
            EXPRESSION_TYPE_MAP.put(descriptor.getName(), descriptor);
        }
    }

    private static void initializeSnpTypeMap() {
        for (IlluminaGenotypingQuantitationType descriptor : IlluminaGenotypingQuantitationType.values()) {
            SNP_TYPE_MAP.put(descriptor.getName(), descriptor);
        }
    }

    @Override
    ArrayDataTypeDescriptor getArrayDataTypeDescriptor(File dataFile) {
        DelimitedFileReader reader = getReader(dataFile);
        try {
            ArrayDataTypeDescriptor descriptor;
            if (isExpressionFile(reader)) {
                descriptor = IlluminaArrayDataTypes.ILLUMINA_EXPRESSION;
            } else if (isGenotypingFile(reader)) {
                descriptor = IlluminaArrayDataTypes.ILLUMINA_GENOTYPING;
            } else {
                throw new IllegalArgumentException("File " + dataFile.getName()
                        + " is not an Illumina genotyping or gene expression data file");
            }
            return descriptor;
        } catch (IOException e) {
            throw new IllegalStateException(AbstractDataFileHandler.READ_FILE_ERROR_MESSAGE, e);
        } finally {
            reader.close();
        }
    }

    private boolean isExpressionFile(DelimitedFileReader reader) throws IOException {
        List<QuantitationTypeDescriptor> types = getTypeDescriptors(reader);
        return Arrays.asList(IlluminaExpressionQuantitationType.values()).containsAll(types);
    }

    private boolean isGenotypingFile(DelimitedFileReader reader) throws IOException {
        List<QuantitationTypeDescriptor> types = getTypeDescriptors(reader);
        return Arrays.asList(IlluminaGenotypingQuantitationType.values()).containsAll(types);
    }

    @SuppressWarnings("PMD.PositionLiteralsFirstInComparisons") // false report from PMD
    private List<QuantitationTypeDescriptor> getTypeDescriptors(DelimitedFileReader reader) throws IOException {
        List<String> headers = getHeaders(reader);
        if (TARGET_ID.equals(headers.get(0))) {
            return getExpressionTypeDescriptors(headers);
        } else {
            return getGenotypingTypeDescriptors(headers);
        }
    }

    private List<QuantitationTypeDescriptor> getExpressionTypeDescriptors(List<String> headers) {
        List<QuantitationTypeDescriptor> descriptors = new ArrayList<QuantitationTypeDescriptor>();
        Set<String> typeNames = new HashSet<String>();
        typeNames.addAll(IlluminaExpressionQuantitationType.getTypeNames());
        for (String header : headers) {
            String typeName = header.split("-")[0];
            if (typeNames.contains(typeName)) {
                descriptors.add(IlluminaExpressionQuantitationType.valueOf(typeName.toUpperCase(Locale.getDefault())));
                typeNames.remove(typeName);
            }
        }
        return descriptors;
    }

    private boolean isRowOriented(List<String> headers) {
        return headers.contains(GROUP_ID_HEADER);
    }

    private List<QuantitationTypeDescriptor> getGenotypingTypeDescriptors(List<String> headers) {
        List<QuantitationTypeDescriptor> descriptors = new ArrayList<QuantitationTypeDescriptor>();
        Set<String> typeNames = new HashSet<String>();
        typeNames.addAll(IlluminaGenotypingQuantitationType.getTypeNames());
        for (String header : headers) {
            if (typeNames.contains(header)) {
                descriptors.add(IlluminaGenotypingQuantitationType.valueOf(header));
            }
        }
        return descriptors;
    }

    private List<String> getHeaders(DelimitedFileReader reader) throws IOException {
        reset(reader);
        while (reader.hasNextLine()) {
            List<String> values = reader.nextLine();
            if (values.size() > 1 && !StringUtils.isEmpty(values.get(1))) {
                return values;
            }
        }
        return null;
    }

    private void reset(DelimitedFileReader reader) {
        try {
            reader.reset();
        } catch (IOException e) {
            throw new IllegalStateException("File could not be reset", e);
        }
    }

    private DelimitedFileReader getReader(File dataFile) {
        try {
            return DelimitedFileReaderFactory.INSTANCE.getCsvReader(dataFile);
        } catch (IOException e) {
            throw new IllegalStateException("File " + dataFile.getName() + " could not be read", e);
        }

    }

    @Override
    Logger getLog() {
        return LOG;
    }

    @Override
    QuantitationTypeDescriptor[] getQuantitationTypeDescriptors(File file) {
        DelimitedFileReader reader = getReader(file);
        try {
            QuantitationTypeDescriptor[] descriptors =
                getTypeDescriptors(reader).toArray(new QuantitationTypeDescriptor[] {});
            return descriptors;
        } catch (IOException e) {
            throw new IllegalStateException(AbstractDataFileHandler.READ_FILE_ERROR_MESSAGE, e);
        } finally {
            reader.close();
        }
    }

    @Override
    List<String> getHybridizationNames(File dataFile) {
        DelimitedFileReader reader = getReader(dataFile);
        try {
            List<String> headers = getHeaders(reader);
            List<String> hybridizationNames;
            if (isRowOriented(headers)) {
                hybridizationNames = getSampleNamesFromGroupId(headers, reader);
            } else {
                hybridizationNames =  getSampleNamesFromHeaders(headers);
            }
            return hybridizationNames;
        } catch (IOException e) {
            throw new IllegalStateException(AbstractDataFileHandler.READ_FILE_ERROR_MESSAGE, e);
        } finally {
            reader.close();
        }
    }

    private List<String> getSampleNamesFromGroupId(List<String> headers, DelimitedFileReader reader)
    throws IOException {
        Set<String> nameSet = new HashSet<String>();
        List<String> names = new ArrayList<String>();
        int position = headers.indexOf(GROUP_ID_HEADER);
        while (reader.hasNextLine()) {
            String groupId = reader.nextLine().get(position);
            if (!nameSet.contains(groupId)) {
                nameSet.add(groupId);
                names.add(groupId);
            }
        }
        return names;
    }

    private List<String> getSampleNamesFromHeaders(List<String> headers) {
        Set<String> nameSet = new HashSet<String>();
        List<String> names = new ArrayList<String>();
        for (String header : headers) {
            String[] parts = header.split("-");
            if (parts.length == 2 && !nameSet.contains(parts[1])) {
                nameSet.add(parts[1]);
                names.add(parts[1]);
            }
        }
        return names;
    }

    @Override
    void loadData(DataSet dataSet, List<QuantitationType> types, File file, ArrayDesignService arrayDesignService) {
        DelimitedFileReader reader = getReader(file);
        try {
            List<String> headers = getHeaders(reader);
            loadData(headers, reader, dataSet, types, arrayDesignService);
        } catch (IOException e) {
            throw new IllegalStateException(AbstractDataFileHandler.READ_FILE_ERROR_MESSAGE, e);
        } finally {
            reader.close();
        }
    }

    private void loadData(List<String> headers, DelimitedFileReader reader, DataSet dataSet,
            List<QuantitationType> types, ArrayDesignService arrayDesignService) throws IOException {
        prepareColumns(dataSet, types, getNumberOfDataRows(reader));
        if (dataSet.getDesignElementList() == null) {
            loadDesignElementList(dataSet, reader, headers, arrayDesignService);
        }
        Map<String, Integer> groupIdToHybridizationDataIndexMap = getGroupIdToHybridizationDataIndexMap(headers);
        Set<QuantitationType> typeSet = new HashSet<QuantitationType>();
        typeSet.addAll(types);
        positionAtData(reader);
        int rowIndex = 0;
        while (reader.hasNextLine()) {
            List<String> values = reader.nextLine();
            for (int i = 0; i < values.size(); i++) {
                loadValue(values.get(i), headers.get(i), dataSet, typeSet, groupIdToHybridizationDataIndexMap,
                        rowIndex);
            }
            rowIndex++;
        }
    }

    private void checkSdrfHybridizations(FileValidationResult result,
            List<String> fileHybNames,
            Map<String, List<Hybridization>> sdrfHybsMap) {
        // get collection of hyb names from sdrf as strings
        for (List<Hybridization> hybList : sdrfHybsMap.values()) {
                List<String> sdrfHybStrs = new ArrayList<String>();
                for (Hybridization hyb : hybList) {
                    sdrfHybStrs.add(hyb.getName());
                }

                if (!sdrfHybStrs.containsAll(fileHybNames)) {
                    StringBuilder sb =
                        new StringBuilder("This data file contains the following hybridization names"
                                +  " that are not referenced in the SDRF document:");
                    sdrfHybStrs.removeAll(fileHybNames);
                    sb.append(StringUtils.join(sdrfHybStrs.iterator(), ','));
                    result.addMessage(Type.ERROR, sb.toString());
                }
        }
    }

    private void loadDesignElementList(DataSet dataSet, DelimitedFileReader reader, List<String> headers,
            ArrayDesignService arrayDesignService) throws IOException {
        int indexOfTargetId = headers.indexOf(TARGET_ID);
        DesignElementList probeList = new DesignElementList();
        probeList.setDesignElementTypeEnum(DesignElementType.PHYSICAL_PROBE);
        dataSet.setDesignElementList(probeList);
        ArrayDesignDetails designDetails = getArrayDesign(arrayDesignService, reader).getDesignDetails();
        ProbeLookup probeLookup = new ProbeLookup(designDetails.getLogicalProbes());
        positionAtData(reader);
        while (reader.hasNextLine()) {
            List<String> values = reader.nextLine();
            String probeName = values.get(indexOfTargetId);
            probeList.getDesignElements().add(probeLookup.getProbe(probeName));
        }
    }

    @SuppressWarnings("PMD.ExcessiveParameterList")
    private void loadValue(String value, String header, DataSet dataSet, Set<QuantitationType> typeSet,
            Map<String, Integer> groupIdToHybridizationDataIndexMap, int rowIndex) {
        String[] headerParts = header.split("-", 2);
        if (headerParts.length == 2) {
            String typeHeader = headerParts[0];
            String groupId = headerParts[1];
            int hybridizationDataIndex = groupIdToHybridizationDataIndexMap.get(groupId);
            HybridizationData hybridizationData = dataSet.getHybridizationDataList().get(hybridizationDataIndex);
            setValue(hybridizationData, typeHeader, value, typeSet, rowIndex);
        }
    }

    private void setValue(HybridizationData hybridizationData, String typeHeader, String value,
            Set<QuantitationType> typeSet, int rowIndex) {
        QuantitationTypeDescriptor typeDescriptor =
            IlluminaExpressionQuantitationType.valueOf(typeHeader.toUpperCase(Locale.getDefault()));
        AbstractDataColumn column = getColumn(hybridizationData, typeDescriptor);
        if (typeSet.contains(column.getQuantitationType())) {
            setValue(column, rowIndex, value);
        }
    }

    private AbstractDataColumn getColumn(HybridizationData hybridizationData,
            QuantitationTypeDescriptor typeDescriptor) {
        for (AbstractDataColumn column : hybridizationData.getColumns()) {
            if (column.getQuantitationType().getName().equals(typeDescriptor.getName())) {
                return column;
            }
        }
        return null;
    }

    private int getNumberOfDataRows(DelimitedFileReader reader) throws IOException {
        int numberOfDataRows = 0;
        positionAtData(reader);
        while (reader.hasNextLine()) {
            reader.nextLine();
            numberOfDataRows++;
        }
        return numberOfDataRows;
    }

    private void positionAtData(DelimitedFileReader reader) throws IOException {
        getHeaders(reader);
    }

    private Map<String, Integer> getGroupIdToHybridizationDataIndexMap(List<String> headers) {
        List<String> groupIds = getSampleNamesFromHeaders(headers);
        Map<String, Integer> map = new HashMap<String, Integer>();
        for (int i = 0; i < groupIds.size(); i++) {
            map.put(groupIds.get(i), i);
        }
        return map;
    }

    @Override
    void validate(CaArrayFile caArrayFile, File file, MageTabDocumentSet mTabSet, FileValidationResult result,
            ArrayDesignService arrayDesignService) {
        DelimitedFileReader reader = getReader(file);
        try {
            validateHeaders(reader, result);
            if (mTabSet.getSdrfDocuments() != null && !mTabSet.getSdrfDocuments().isEmpty()) {
                checkSdrfHybridizations(result, getHybridizationNames(file), mTabSet.getSdrfHybridizations());
            }

            if (result.isValid()) {
                validateData(reader, result);
            }
            result.addValidationProperties(FileValidationResult.HYB_NAME, this.getHybridizationNames(file));
        } catch (IOException e) {
            throw new IllegalStateException(AbstractDataFileHandler.READ_FILE_ERROR_MESSAGE, e);
        } finally {
            reader.close();
        }
    }

    private void validateHeaders(DelimitedFileReader reader, FileValidationResult result) throws IOException {
        validateHasArrayContentHeader(reader, result);
        List<String> headers = getHeaders(reader);
        if (headers == null) {
            result.addMessage(Type.ERROR, "No headers found");
        }
    }

    private void validateHasArrayContentHeader(DelimitedFileReader reader, FileValidationResult result)
    throws IOException {
        if (getArrayContentValue(reader) == null) {
            result.addMessage(Type.ERROR, "Data file doesn not contain required header \"Array Content\"");
        }
    }

    private String getArrayContentValue(DelimitedFileReader reader) throws IOException {
        Map<String, String[]> fileHeaders = getFileHeaders(reader);
        String[] arrayContentValues = fileHeaders.get(ARRAY_CONTENT_HEADER);
        if (arrayContentValues == null || arrayContentValues.length == 0
                || StringUtils.isEmpty(arrayContentValues[0])) {
            return null;
        } else {
            return arrayContentValues[0].trim();
        }
    }

    private Map<String, String[]> getFileHeaders(DelimitedFileReader reader) throws IOException {
        reset(reader);
        Map<String, String[]> fileHeaders = new HashMap<String, String[]>();
        while (reader.hasNextLine()) {
            List<String> values = reader.nextLine();
            if (isBlankLine(values)) {
                return fileHeaders;
            } else if (isFileHeader(values)) {
                addFileHeader(fileHeaders, values);
            }
        }
        return fileHeaders;
    }

    private boolean isBlankLine(List<String> values) {
        return values.isEmpty() || (values.size() == 1 && StringUtils.isEmpty(values.get(0)));
    }

    private boolean isFileHeader(List<String> values) {
        return !values.isEmpty() && values.get(0).contains("=");
    }

    private void addFileHeader(Map<String, String[]> fileHeaders, List<String> values) {
        String[] parts = values.get(0).split("=");
        String header = parts[0].trim();
        if (parts.length > 1) {
            String[] headerValues = parts[1].split(",");
            fileHeaders.put(header, headerValues);
        }
    }

    private void validateData(DelimitedFileReader reader, FileValidationResult result) throws IOException {
        List<String> headers = getHeaders(reader);
        positionAtData(reader);
        while (reader.hasNextLine()) {
            if (reader.nextLine().size() != headers.size()) {
                ValidationMessage message = result.addMessage(Type.ERROR, "Invalid number of values in row");
                message.setLine(reader.getCurrentLineNumber());
            }
        }
    }

    private ArrayDesign getArrayDesign(ArrayDesignService arrayDesignService, DelimitedFileReader reader)
    throws IOException {
        String illuminaFile = getArrayContentValue(reader);
        String designName = FilenameUtils.getBaseName(illuminaFile);
        return arrayDesignService.getArrayDesign(LSID_AUTHORITY, LSID_NAMESPACE, designName);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ArrayDesign getArrayDesign(ArrayDesignService arrayDesignService, File file) {
        DelimitedFileReader reader = getReader(file);
        try {
            return getArrayDesign(arrayDesignService, reader);
        } catch (IOException e) {
            throw new IllegalStateException(AbstractDataFileHandler.READ_FILE_ERROR_MESSAGE, e);
        } finally {
            reader.close();
        }
    }


}
