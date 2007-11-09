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

import gov.nih.nci.caarray.application.arraydata.illumina.IlluminaArrayDataTypes;
import gov.nih.nci.caarray.application.arraydata.illumina.IlluminaExpressionQuantitationType;
import gov.nih.nci.caarray.application.arraydata.illumina.IlluminaGenotypingQuantitationType;
import gov.nih.nci.caarray.domain.data.AbstractDataColumn;
import gov.nih.nci.caarray.domain.data.ArrayDataTypeDescriptor;
import gov.nih.nci.caarray.domain.data.DataSet;
import gov.nih.nci.caarray.domain.data.HybridizationData;
import gov.nih.nci.caarray.domain.data.QuantitationType;
import gov.nih.nci.caarray.domain.data.QuantitationTypeDescriptor;
import gov.nih.nci.caarray.domain.file.CaArrayFile;
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

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Handles reading of Illumina data.
 */
class IlluminaDataHandler extends AbstractDataFileHandler {

    private static final String GROUP_ID_HEADER = "GroupID";
    private static final Map<String, IlluminaExpressionQuantitationType> EXPRESSION_TYPE_MAP =
        new HashMap<String, IlluminaExpressionQuantitationType>();
    private static final Map<String, IlluminaGenotypingQuantitationType> SNP_TYPE_MAP =
        new HashMap<String, IlluminaGenotypingQuantitationType>();
    private static final Log LOG = LogFactory.getLog(IlluminaDataHandler.class);

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
        if (isExpressionFile(reader)) {
            return IlluminaArrayDataTypes.ILLUMINA_EXPRESSION;
        } else if (isGenotypingFile(reader)) {
            return IlluminaArrayDataTypes.ILLUMINA_GENOTYPING;
        } else {
            throw new IllegalArgumentException("File " + dataFile.getName()
                    + " is not an Illumina genotyping or gene expression data file");
        }
    }

    private boolean isExpressionFile(DelimitedFileReader reader) {
        List<QuantitationTypeDescriptor> types = getTypeDescriptors(reader);
        return Arrays.asList(IlluminaExpressionQuantitationType.values()).containsAll(types);
    }

    private boolean isGenotypingFile(DelimitedFileReader reader) {
        List<QuantitationTypeDescriptor> types = getTypeDescriptors(reader);
        return Arrays.asList(IlluminaGenotypingQuantitationType.values()).containsAll(types);
    }

    private List<QuantitationTypeDescriptor> getTypeDescriptors(DelimitedFileReader reader) {
        List<String> headers = getHeaders(reader);
        if ("TargetID".equals(headers.get(0))) {
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

    private List<String> getHeaders(DelimitedFileReader reader) {
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
    Log getLog() {
        return LOG;
    }

    @Override
    QuantitationTypeDescriptor[] getQuantitationTypeDescriptors(File file) {
        return getTypeDescriptors(getReader(file)).toArray(new QuantitationTypeDescriptor[] {});
    }

    @Override
    List<String> getHybridizationNames(File dataFile) {
        DelimitedFileReader reader = getReader(dataFile);
        List<String> headers = getHeaders(reader);
        if (isRowOriented(headers)) {
            return getSampleNamesFromGroupId(headers, reader);
        } else {
            return getSampleNamesFromHeaders(headers);
        }
    }

    private List<String> getSampleNamesFromGroupId(List<String> headers, DelimitedFileReader reader) {
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
    void loadData(DataSet dataSet, List<QuantitationType> types, File file) {
        DelimitedFileReader reader = getReader(file);
        List<String> headers = getHeaders(reader);
        loadData(headers, reader, dataSet, types);
    }

    private void loadData(List<String> headers, DelimitedFileReader reader, DataSet dataSet, 
            List<QuantitationType> types) {
        prepareColumns(dataSet, types, getNumberOfDataRows(reader));
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

    private int getNumberOfDataRows(DelimitedFileReader reader) {
        int numberOfDataRows = 0;
        positionAtData(reader);
        while (reader.hasNextLine()) {
            reader.nextLine();
            numberOfDataRows++;
        }
        return numberOfDataRows;
    }

    private void positionAtData(DelimitedFileReader reader) {
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
    void validate(CaArrayFile caArrayFile, File file, FileValidationResult result) {
        DelimitedFileReader reader = getReader(file);
        validateHeaders(reader, result);
        if (result.isValid()) {
            validateData(reader, result);
        }
    }

    private void validateHeaders(DelimitedFileReader reader, FileValidationResult result) {
        List<String> headers = getHeaders(reader);
        if (headers == null) {
            result.addMessage(Type.ERROR, "No headers found");
        }
    }

    private void validateData(DelimitedFileReader reader, FileValidationResult result) {
        List<String> headers = getHeaders(reader);
        positionAtData(reader);
        while (reader.hasNextLine()) {
            if (reader.nextLine().size() != headers.size()) {
                ValidationMessage message = result.addMessage(Type.ERROR, "Invalid number of values in row");
                message.setLine(reader.getCurrentLineNumber());
            }
        }
    }


}
