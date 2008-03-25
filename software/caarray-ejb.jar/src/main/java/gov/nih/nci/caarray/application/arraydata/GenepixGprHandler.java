/**
 * The software subject to this notice and license includes both human readable
 * source code form and machine readable, binary, object code form. The caArray
 * Software was developed in conjunction with the National Cancer Institute
 * (NCI) by NCI employees and 5AM Solutions, Inc. (5AM). To the extent
 * government employees are authors, any rights in such works shall be subject
 * to Title 17 of the United States Code, section 105.
 *
 * This caArray Software License (the License) is between NCI and You. You (or
 * Your) shall mean a person or an entity, and all other entities that control,
 * are controlled by, or are under common control with the entity. Control for
 * purposes of this definition means (i) the direct or indirect power to cause
 * the direction or management of such entity, whether by contract or otherwise,
 * or (ii) ownership of fifty percent (50%) or more of the outstanding shares,
 * or (iii) beneficial ownership of such entity.
 *
 * This License is granted provided that You agree to the conditions described
 * below. NCI grants You a non-exclusive, worldwide, perpetual, fully-paid-up,
 * no-charge, irrevocable, transferable and royalty-free right and license in
 * its rights in the caArray Software to (i) use, install, access, operate,
 * execute, copy, modify, translate, market, publicly display, publicly perform,
 * and prepare derivative works of the caArray Software; (ii) distribute and
 * have distributed to and by third parties the caArray Software and any
 * modifications and derivative works thereof; and (iii) sublicense the
 * foregoing rights set out in (i) and (ii) to third parties, including the
 * right to license such rights to further third parties. For sake of clarity,
 * and not by way of limitation, NCI shall have no right of accounting or right
 * of payment from You or Your sub-licensees for the rights granted under this
 * License. This License is granted at no charge to You.
 *
 * Your redistributions of the source code for the Software must retain the
 * above copyright notice, this list of conditions and the disclaimer and
 * limitation of liability of Article 6, below. Your redistributions in object
 * code form must reproduce the above copyright notice, this list of conditions
 * and the disclaimer of Article 6 in the documentation and/or other materials
 * provided with the distribution, if any.
 *
 * Your end-user documentation included with the redistribution, if any, must
 * include the following acknowledgment: This product includes software
 * developed by 5AM and the National Cancer Institute. If You do not include
 * such end-user documentation, You shall include this acknowledgment in the
 * Software itself, wherever such third-party acknowledgments normally appear.
 *
 * You may not use the names "The National Cancer Institute", "NCI", or "5AM"
 * to endorse or promote products derived from this Software. This License does
 * not authorize You to use any trademarks, service marks, trade names, logos or
 * product names of either NCI or 5AM, except as required to comply with the
 * terms of this License.
 *
 * For sake of clarity, and not by way of limitation, You may incorporate this
 * Software into Your proprietary programs and into any third party proprietary
 * programs. However, if You incorporate the Software into third party
 * proprietary programs, You agree that You are solely responsible for obtaining
 * any permission from such third parties required to incorporate the Software
 * into such third party proprietary programs and for informing Your
 * sub-licensees, including without limitation Your end-users, of their
 * obligation to secure any required permissions from such third parties before
 * incorporating the Software into such third party proprietary software
 * programs. In the event that You fail to obtain such permissions, You agree
 * to indemnify NCI for any claims against NCI by such third parties, except to
 * the extent prohibited by law, resulting from Your failure to obtain such
 * permissions.
 *
 * For sake of clarity, and not by way of limitation, You may add Your own
 * copyright statement to Your modifications and to the derivative works, and
 * You may provide additional or different license terms and conditions in Your
 * sublicenses of modifications of the Software, or any derivative works of the
 * Software as a whole, provided Your use, reproduction, and distribution of the
 * Work otherwise complies with the conditions stated in this License.
 *
 * THIS SOFTWARE IS PROVIDED "AS IS," AND ANY EXPRESSED OR IMPLIED WARRANTIES,
 * (INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY,
 * NON-INFRINGEMENT AND FITNESS FOR A PARTICULAR PURPOSE) ARE DISCLAIMED. IN NO
 * EVENT SHALL THE NATIONAL CANCER INSTITUTE, 5AM SOLUTIONS, INC. OR THEIR
 * AFFILIATES BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS;
 * OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR
 * OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
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
        tmpMap.put("Rgn R�", GenepixQuantitationType.RGN_R2_635_532);
        tmpMap.put("Rgn R� (635/532)", GenepixQuantitationType.RGN_R2_635_532);
        tmpMap.put("Rgn R\uFFFD", GenepixQuantitationType.RGN_R2_635_532);
        tmpMap.put("Rgn R\uFFFD (635/532)", GenepixQuantitationType.RGN_R2_635_532);
        tmpMap.put("Rgn Ratio", GenepixQuantitationType.RGN_RATIO_635_532);
        tmpMap.put("Sum of Means", GenepixQuantitationType.SUM_OF_MEANS_635_532);
        tmpMap.put("Sum of Medians", GenepixQuantitationType.SUM_OF_MEDIANS_635_532);
        tmpMap.put("Rgn R� (Ratio/2)", GenepixQuantitationType.RGN_R2_RATIO_2);
        tmpMap.put("Rgn R\uFFFD (Ratio/2)", GenepixQuantitationType.RGN_R2_RATIO_2);
        tmpMap.put("Rgn R� (Ratio/3)", GenepixQuantitationType.RGN_R2_RATIO_3);
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

    private List<String> getColumnHeaders(DelimitedFileReader reader) {
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
            throw new IllegalStateException("Couldn't read file", e);
        }
    }

    private QuantitationTypeDescriptor[] getQuantitationTypeDescriptors(DelimitedFileReader reader) {
        return getQuantitationTypeDescriptors(getColumnHeaders(reader));
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
        String basename = FilenameUtils.getBaseName(dataFile.getName());
        DelimitedFileReader reader = getReader(dataFile);
        try {
            Map<String, String[]> headers = getHeaders(reader);
            names.add(basename + "-635");
            names.add(basename + "-532");
            if (headers.containsKey(WAVELENGTHS_HEADER) && headers.get(WAVELENGTHS_HEADER).length > 2) {
                addThreeAndFourColorNames(names, basename, headers.get(WAVELENGTHS_HEADER));
            } else if (headers.containsKey(IMAGE_NAME_HEADER) && headers.get(IMAGE_NAME_HEADER).length > 2) {
                addThreeAndFourColorNames(names, basename, headers.get(IMAGE_NAME_HEADER));
            }
            return names;
        } finally {
            reader.close();
        }
    }

    private void addThreeAndFourColorNames(List<String> names, String basename, String[] values) {
        for (int i = 2; i < values.length; i++) {
            names.add(basename + "-" + values[i].replace(' ', '_'));
        }
    }

    private Map<String, String[]> getHeaders(DelimitedFileReader reader) {
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
        } finally {
            reader.close();
        }
    }

    private void loadDesignElementList(DataSet dataSet, DelimitedFileReader reader,
            ArrayDesignService arrayDesignService) {
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
            DelimitedFileReader reader) {
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

    private int getNumberOfDataRows(DelimitedFileReader reader) {
        int numberOfDataRows = 0;
        getColumnHeaders(reader);
        while (reader.hasNextLine()) {
            reader.nextLine();
            numberOfDataRows++;
        }
        return numberOfDataRows;
    }

    @Override
    void validate(CaArrayFile caArrayFile, File file, FileValidationResult result,
            ArrayDesignService arrayDesignService) {
        DelimitedFileReader reader = getReader(file);
        try {
            validateHeader(reader, result);
            if (result.isValid()) {
                validateData(reader, result);
            }
        } finally {
            reader.close();
        }
    }

    private void validateData(DelimitedFileReader reader, FileValidationResult result) {
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

    private void validateHeader(DelimitedFileReader reader, FileValidationResult result) {
        validateAtfLine(reader, result);
        readAndValidateCountLine(reader, result);
        if (getColumnHeaders(reader) == null) {
            result.addMessage(Type.ERROR, "The GPR file doesn't contain a valid header line.");
        } else {
            validateHasGalFile(reader, result);
        }
    }

    private void readAndValidateCountLine(DelimitedFileReader reader, FileValidationResult result) {
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

    private void validateAtfLine(DelimitedFileReader reader, FileValidationResult result) {
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

    private void validateHasGalFile(DelimitedFileReader reader, FileValidationResult result) {
        if (getGalFile(reader) == null) {
            result.addMessage(Type.ERROR, "This file doesn't contain the required header entry \"GalFile\"");
        }
    }

    @Override
    ArrayDesign getArrayDesign(ArrayDesignService arrayDesignService, File file) {
        DelimitedFileReader reader = getReader(file);
        try {
            return getArrayDesign(arrayDesignService, reader);
        } finally {
            reader.close();
        }
    }

    private ArrayDesign getArrayDesign(ArrayDesignService arrayDesignService, DelimitedFileReader reader) {
        String galFile = getGalFile(reader);
        String galName = FilenameUtils.getBaseName(galFile);
        return arrayDesignService.getArrayDesign(LSID_AUTHORITY, LSID_NAMESPACE, galName);
    }

    private String getGalFile(DelimitedFileReader reader) {
        Map<String, String[]> headers = getHeaders(reader);
        String[] galFileHeader = headers.get(GAL_FILE_HEADER);
        if (galFileHeader == null || galFileHeader.length == 0 || StringUtils.isEmpty(galFileHeader[0])) {
            return null;
        } else {
            return galFileHeader[0].trim();
        }
    }

}