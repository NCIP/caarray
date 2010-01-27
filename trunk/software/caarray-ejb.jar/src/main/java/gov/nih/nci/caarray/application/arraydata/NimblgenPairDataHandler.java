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

import gov.nih.nci.caarray.application.arraydata.nimblegen.NimblegenArrayDataTypes;
import gov.nih.nci.caarray.application.arraydata.nimblegen.NimblegenQuantitationType;
import gov.nih.nci.caarray.application.arraydesign.ArrayDesignService;
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
import gov.nih.nci.caarray.util.io.DelimitedFileReader;
import gov.nih.nci.caarray.util.io.DelimitedFileReaderFactory;
import gov.nih.nci.caarray.validation.FileValidationResult;
import gov.nih.nci.caarray.validation.ValidationMessage.Type;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

/**
 * Handles reading of nimblegen data.
 */
class NimblegenPairDataHandler extends AbstractDataFileHandler {
    private static final String LSID_AUTHORITY = "nimblegen.com";
    private static final String LSID_NAMESPACE = "PhysicalArrayDesign";
    private static final Map<String, NimblegenQuantitationType> TYPE_MAP =
        new HashMap<String, NimblegenQuantitationType>();
    private static final Logger LOG = Logger
            .getLogger(NimblegenPairDataHandler.class);
    private static final String ERROR_INDICATOR = "Error";

    private static final String SEQ_ID_HEADER = "SEQ_ID";
    private static final String PROBE_ID_HEADER = "PROBE_ID";
    private static final String CONTAINER_HEADER = "GENE_EXPR_OPTION";
    private static final String X_HEADER = "X";
    private static final String Y_HEADER = "Y";

    private static final List<String> STANDARD_HEADERS = Arrays
            .asList(new String[] {
                    SEQ_ID_HEADER, PROBE_ID_HEADER,
                    CONTAINER_HEADER, X_HEADER, Y_HEADER, });

    static {
        initializeTypeMap();
    }

    private static void initializeTypeMap() {
        for (NimblegenQuantitationType descriptor : NimblegenQuantitationType
                .values()) {
            TYPE_MAP.put(descriptor.getName(), descriptor);
        }
    }

    @Override
    ArrayDataTypeDescriptor getArrayDataTypeDescriptor(File dataFile) {
        return NimblegenArrayDataTypes.NIMBLEGEN;
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

    private QuantitationTypeDescriptor[] getQuantitationTypeDescriptors(
            DelimitedFileReader reader) throws IOException {
        return getQuantitationTypeDescriptors(getHeaders(reader));
    }

    private QuantitationTypeDescriptor[] getQuantitationTypeDescriptors(
            List<String> headers) {
        List<QuantitationTypeDescriptor> descriptorList = new ArrayList<QuantitationTypeDescriptor>();
        for (String header : headers) {
            if (TYPE_MAP.containsKey(header)) {
                descriptorList.add(TYPE_MAP.get(header));
            }
        }
        return descriptorList.toArray(new QuantitationTypeDescriptor[] {});
    }

    private List<String> getHeaders(DelimitedFileReader reader)
            throws IOException {
        reset(reader);
        while (reader.hasNextLine()) {
            List<String> values = reader.nextLine();
            if (values.get(0).startsWith("#")) {
                continue;
            }
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
            return DelimitedFileReaderFactory.INSTANCE
                    .getTabDelimitedReader(dataFile);
        } catch (IOException e) {
            throw new IllegalStateException("File " + dataFile.getName()
                    + " could not be read", e);
        }

    }

    @Override
    Logger getLog() {
        return LOG;
    }

    @Override
    void loadData(DataSet dataSet, List<QuantitationType> types, File file,
            ArrayDesignService arrayDesignService) {
        DelimitedFileReader reader = getReader(file);
        try {
            prepareColumns(dataSet, types, getNumberOfDataRows(reader));
            if (dataSet.getDesignElementList() == null) {
                loadDesignElementList(dataSet, reader, arrayDesignService);
            }
            Set<QuantitationTypeDescriptor> descriptorSet = getDescriptorSet(types);
            for (HybridizationData hybridizationData : dataSet
                    .getHybridizationDataList()) {
                loadData(hybridizationData, descriptorSet, reader);
            }
        } catch (IOException e) {
            throw new IllegalStateException(READ_FILE_ERROR_MESSAGE, e);
        } finally {
            reader.close();
        }
    }

    private void loadDesignElementList(DataSet dataSet,
            DelimitedFileReader reader, ArrayDesignService arrayDesignService)
            throws IOException {
        DesignElementList probeList = new DesignElementList();
        probeList.setDesignElementTypeEnum(DesignElementType.PHYSICAL_PROBE);
        dataSet.setDesignElementList(probeList);
        ArrayDesignDetails designDetails = getArrayDesign(arrayDesignService,
                reader).getDesignDetails();
        ProbeLookup probeLookup = new ProbeLookup(designDetails.getProbes());
        List<String> headers = getHeaders(reader);
        int seqIdIndex = headers.indexOf(SEQ_ID_HEADER);
        int probeIdIndex = headers.indexOf(PROBE_ID_HEADER);
        int containerIndex = headers.indexOf(CONTAINER_HEADER);
        while (reader.hasNextLine()) {
            List<String> values = reader.nextLine();
            String probeId = values.get(probeIdIndex);
            String sequenceId = values.get(seqIdIndex);
            String container = values.get(containerIndex);
            String probeName = container + "|" + sequenceId + "|" + probeId;
            probeList.getDesignElements().add(probeLookup.getProbe(probeName));
        }
    }

    private Set<QuantitationTypeDescriptor> getDescriptorSet(
            List<QuantitationType> types) {
        Set<QuantitationTypeDescriptor> descriptors = new HashSet<QuantitationTypeDescriptor>();
        for (QuantitationType type : types) {
            descriptors.add(TYPE_MAP.get(type.getName()));
        }
        return descriptors;
    }

    private void loadData(HybridizationData hybridizationData,
            Set<QuantitationTypeDescriptor> descriptors,
            DelimitedFileReader reader) throws IOException {
        List<String> headers = getHeaders(reader);
        int rowIndex = 0;
        while (reader.hasNextLine()) {
            List<String> values = reader.nextLine();
            loadData(hybridizationData, descriptors, values, headers,
                    rowIndex++);
        }
    }

    private void loadData(HybridizationData hybridizationData,
            Set<QuantitationTypeDescriptor> descriptors, List<String> values,
            List<String> headers, int rowIndex) {
        for (int valueIndex = 0; valueIndex < values.size(); valueIndex++) {
            QuantitationTypeDescriptor valueType = TYPE_MAP.get(headers
                    .get(valueIndex));
            if (descriptors.contains(valueType)) {
                setValue(hybridizationData.getColumn(valueType), rowIndex,
                        values.get(valueIndex));
            }
        }
    }

    private int getNumberOfDataRows(DelimitedFileReader reader)
            throws IOException {
        int numberOfDataRows = 0;
        getHeaders(reader);
        while (reader.hasNextLine()) {
            reader.nextLine();
            numberOfDataRows++;
        }
        return numberOfDataRows;
    }

    @Override
    void validate(CaArrayFile caArrayFile, File file,
            MageTabDocumentSet mTabSet, FileValidationResult result,
            ArrayDesignService arrayDesignService) {
        DelimitedFileReader reader = getReader(file);
        try {
            validateHeader(reader, result);
            if (result.isValid()) {
                validateData(reader, result);
            }
        } catch (IOException e) {
            throw new IllegalStateException(READ_FILE_ERROR_MESSAGE, e);
        } finally {
            reader.close();
        }
    }

    private Map<String, QuantitationTypeDescriptor> getHeaderToDescriptorMap(
            List<String> headers) {
        Map<String, QuantitationTypeDescriptor> headerDescriptorMap = new HashMap<String, QuantitationTypeDescriptor>();
        for (String header : headers) {
            headerDescriptorMap.put(header, TYPE_MAP.get(header));
        }
        return headerDescriptorMap;
    }

    private void validateData(DelimitedFileReader reader,
            FileValidationResult result) throws IOException {
        List<String> headers = getHeaders(reader);
        Map<String, QuantitationTypeDescriptor> headerToDescriptorMap = getHeaderToDescriptorMap(headers);
        while (reader.hasNextLine()) {
            List<String> values = reader.nextLine();
            validateValues(values, headers, headerToDescriptorMap, result,
                    reader.getCurrentLineNumber());
        }
    }

    private void validateValues(List<String> values, List<String> headers,
            Map<String, QuantitationTypeDescriptor> headerToDescriptorMap,
            FileValidationResult result, int line) {
        for (int columnIndex = 0; columnIndex < values.size(); columnIndex++) {
            if (isQuantitation(headers.get(columnIndex), headerToDescriptorMap)) {
                validateQuantitation(values.get(columnIndex),
                        headerToDescriptorMap.get(headers.get(columnIndex)),
                        result, line, columnIndex + 1);
            } else if (isStandardColumn(headers.get(columnIndex))) {
                validateStandardColumn(values.get(columnIndex), headers
                        .get(columnIndex), result, line, columnIndex + 1);
            }
        }
    }

    private boolean isQuantitation(String header,
            Map<String, QuantitationTypeDescriptor> headerToDescriptorMap) {
        return headerToDescriptorMap.get(header) != null;
    }

    private void validateQuantitation(String value,
            QuantitationTypeDescriptor descriptor, FileValidationResult result,
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
            throw new IllegalArgumentException("Invalid data type: "
                    + descriptor.getDataType());
        }
    }

    private void validateBoolean(String value, FileValidationResult result,
            int line, int column) {
        if (!"0".equals(value) && !"1".equals(value)) {
            result.addMessage(Type.ERROR, "Invalid boolean value: " + value
                    + ". Legal values are 0 or 1.", line, column);
        }
    }

    private void validateDouble(String value, FileValidationResult result,
            int line, int column) {
        if (!ERROR_INDICATOR.equals(value)) {
            try {
                Double.parseDouble(value);
            } catch (NumberFormatException e) {
                result.addMessage(Type.ERROR, "Invalid double value: " + value
                        + ". Must be a valid floating point number.", line,
                        column);
            }
        }
    }

    private void validateFloat(String value, FileValidationResult result,
            int line, int column) {
        if (!ERROR_INDICATOR.equals(value)) {
            try {
                Float.parseFloat(value);
            } catch (NumberFormatException e) {
                result.addMessage(Type.ERROR, "Invalid float value: " + value
                        + ". Must be a valid floating point number.", line,
                        column);
            }
        }
    }

    private void validateInteger(String value, FileValidationResult result,
            int line, int column) {
        try {
            Integer.parseInt(value);
        } catch (NumberFormatException e) {
            result.addMessage(Type.ERROR, "Invalid integer value: " + value
                    + ". Must be a valid integer.", line, column);
        }
    }

    private void validateLong(String value, FileValidationResult result,
            int line, int column) {
        try {
            Integer.parseInt(value);
        } catch (NumberFormatException e) {
            result.addMessage(Type.ERROR, "Invalid integer value: " + value
                    + ". Must be a valid integer.", line, column);
        }
    }

    private void validateShort(String value, FileValidationResult result,
            int line, int column) {
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

    private void validateStandardColumn(String value, String header,
            FileValidationResult result, int line, int column) {
        if (X_HEADER.equals(header)) {
            validateInteger(value, result, line, column);
        } else if (Y_HEADER.equals(header)) {
            validateInteger(value, result, line, column);
        }
    }

    private void validateHeader(DelimitedFileReader reader,
            FileValidationResult result) throws IOException {
        if (getHeaders(reader) == null) {
            result.addMessage(Type.ERROR,
                    "The Pair file doesn't contain a valid header line.");
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

    /**
     * {@inheritDoc}
     */
    @Override
    public ArrayDesign getArrayDesign(ArrayDesignService arrayDesignService,
            File file) {
        DelimitedFileReader reader = getReader(file);
        try {
            return getArrayDesign(arrayDesignService, reader);
        } catch (IOException e) {
            throw new IllegalStateException(READ_FILE_ERROR_MESSAGE, e);
        } finally {
            reader.close();
        }
    }

    private Map<String, String> getHeaderMetadata(DelimitedFileReader reader)
            throws IOException {
        reset(reader);
        Map<String, String> result = new HashMap<String, String>();
        List<String> line = reader.nextLine();
        line.set(0, line.get(0).substring(2));
        for (String value : line) {
            String[] v = value.split("=");
            if (v.length < 2) {
                continue;
            }
            result.put(v[0], v[1]);
        }
        return result;
    }

    private ArrayDesign getArrayDesign(ArrayDesignService arrayDesignService,
            DelimitedFileReader reader) throws IOException {
        Map<String, String> metadata = getHeaderMetadata(reader);
        String designName = metadata.get("designname");
        return arrayDesignService.getArrayDesign(LSID_AUTHORITY,
                LSID_NAMESPACE, designName);
    }

}
