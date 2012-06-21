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
package gov.nih.nci.caarray.plugins.illumina;

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
import gov.nih.nci.caarray.magetab.sdrf.Hybridization;
import gov.nih.nci.caarray.platforms.AbstractDataFileHandler;
import gov.nih.nci.caarray.platforms.DefaultValueParser;
import gov.nih.nci.caarray.platforms.DesignElementBuilder;
import gov.nih.nci.caarray.platforms.ProbeNamesValidator;
import gov.nih.nci.caarray.platforms.ValueParser;
import gov.nih.nci.caarray.validation.FileValidationResult;
import gov.nih.nci.caarray.validation.ValidationMessage;
import gov.nih.nci.caarray.validation.ValidationMessage.Type;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;

import com.fiveamsolutions.nci.commons.util.io.DelimitedFileReader;
import com.fiveamsolutions.nci.commons.util.io.DelimitedFileReaderFactoryImpl;
import com.google.common.collect.Sets;
import com.google.inject.Inject;

/**
 * Handles reading of Illumina data.
 */
public class CsvDataHandler extends AbstractDataFileHandler {
    private static final String TARGET_ID = "TargetID";
    private static final String ARRAY_CONTENT_HEADER = "Array Content";
    private static final String LSID_AUTHORITY = "illumina.com";
    private static final String LSID_NAMESPACE = "PhysicalArrayDesign";
    private static final String GROUP_ID_HEADER = "GroupID";
    private static final Map<String, IlluminaExpressionQuantitationType> EXPRESSION_TYPE_MAP =
        new HashMap<String, IlluminaExpressionQuantitationType>();
    private static final Map<String, IlluminaGenotypingQuantitationType> SNP_TYPE_MAP =
        new HashMap<String, IlluminaGenotypingQuantitationType>();
    private static final int BATCH_SIZE = 1000;

    /**
     * File Type for illumina CSV data files.
     */
    public static final FileType DATA_CSV_FILE_TYPE =
        new FileType("ILLUMINA_DATA_CSV", FileCategory.DERIVED_DATA, true);
    private static final Set<FileType> SUPPORTED_TYPES = Sets.newHashSet(DATA_CSV_FILE_TYPE);

    static {
        for (final IlluminaExpressionQuantitationType descriptor : IlluminaExpressionQuantitationType.values()) {
            EXPRESSION_TYPE_MAP.put(descriptor.getName(), descriptor);
        }

        for (final IlluminaGenotypingQuantitationType descriptor : IlluminaGenotypingQuantitationType.values()) {
            SNP_TYPE_MAP.put(descriptor.getName(), descriptor);
        }
    }

    private final ValueParser valueParser = new DefaultValueParser();
    private final ArrayDao arrayDao;
    private final SearchDao searchDao;

    /**
     * 
     */
    @Inject
    CsvDataHandler(DataStorageFacade dataStorageFacade, ArrayDao arrayDao, SearchDao searchDao) {
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
        final DelimitedFileReader reader = getReader(getFile());
        try {
            ArrayDataTypeDescriptor descriptor;
            if (isExpressionFile(reader)) {
                descriptor = IlluminaArrayDataTypes.ILLUMINA_EXPRESSION;
            } else if (isGenotypingFile(reader)) {
                descriptor = IlluminaArrayDataTypes.ILLUMINA_GENOTYPING;
            } else {
                throw new IllegalArgumentException("File " + getFile().getName()
                        + " is not an Illumina genotyping or gene expression data file");
            }
            return descriptor;
        } catch (final IOException e) {
            throw new IllegalStateException(AbstractDataFileHandler.READ_FILE_ERROR_MESSAGE, e);
        } finally {
            reader.close();
        }
    }

    private boolean isExpressionFile(DelimitedFileReader reader) throws IOException {
        final List<QuantitationTypeDescriptor> types = getTypeDescriptors(reader);
        return Arrays.asList(IlluminaExpressionQuantitationType.values()).containsAll(types);
    }

    private boolean isGenotypingFile(DelimitedFileReader reader) throws IOException {
        final List<QuantitationTypeDescriptor> types = getTypeDescriptors(reader);
        return Arrays.asList(IlluminaGenotypingQuantitationType.values()).containsAll(types);
    }

    @SuppressWarnings("PMD.PositionLiteralsFirstInComparisons")
    // false report from PMD
    private
    List<QuantitationTypeDescriptor> getTypeDescriptors(DelimitedFileReader reader) throws IOException {
        final List<String> headers = getHeaders(reader);
        if (TARGET_ID.equals(headers.get(0))) {
            return getExpressionTypeDescriptors(headers);
        } else {
            return getGenotypingTypeDescriptors(headers);
        }
    }

    private List<QuantitationTypeDescriptor> getExpressionTypeDescriptors(List<String> headers) {
        final List<QuantitationTypeDescriptor> descriptors = new ArrayList<QuantitationTypeDescriptor>();
        final Set<String> typeNames = new HashSet<String>();
        typeNames.addAll(IlluminaExpressionQuantitationType.getTypeNames());
        for (final String header : headers) {
            final String typeName = header.split("-")[0];
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
        final List<QuantitationTypeDescriptor> descriptors = new ArrayList<QuantitationTypeDescriptor>();
        final Set<String> typeNames = new HashSet<String>();
        typeNames.addAll(IlluminaGenotypingQuantitationType.getTypeNames());
        for (final String header : headers) {
            if (typeNames.contains(header)) {
                descriptors.add(IlluminaGenotypingQuantitationType.valueOf(header));
            }
        }
        return descriptors;
    }

    private List<String> getHeaders(DelimitedFileReader reader) throws IOException {
        reset(reader);
        while (reader.hasNextLine()) {
            final List<String> values = reader.nextLine();
            if (values.size() > 1 && !StringUtils.isEmpty(values.get(1))) {
                return values;
            }
        }
        return null;
    }

    private void reset(DelimitedFileReader reader) {
        try {
            reader.reset();
        } catch (final IOException e) {
            throw new IllegalStateException("File could not be reset", e);
        }
    }

    private DelimitedFileReader getReader(File dataFile) {
        try {
            return new DelimitedFileReaderFactoryImpl().createCommaDelimitedFileReader(dataFile);
        } catch (final IOException e) {
            throw new IllegalStateException("File " + dataFile.getName() + " could not be read", e);
        }

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public QuantitationTypeDescriptor[] getQuantitationTypeDescriptors() {
        final DelimitedFileReader reader = getReader(getFile());
        try {
            final QuantitationTypeDescriptor[] descriptors =
                getTypeDescriptors(reader).toArray(new QuantitationTypeDescriptor[] {});
            return descriptors;
        } catch (final IOException e) {
            throw new IllegalStateException(AbstractDataFileHandler.READ_FILE_ERROR_MESSAGE, e);
        } finally {
            reader.close();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<String> getHybridizationNames() {
        final DelimitedFileReader reader = getReader(getFile());
        try {
            final List<String> headers = getHeaders(reader);
            List<String> hybridizationNames;
            if (isRowOriented(headers)) {
                hybridizationNames = getSampleNamesFromGroupId(headers, reader);
            } else {
                hybridizationNames = getSampleNamesFromHeaders(headers);
            }
            return hybridizationNames;
        } catch (final IOException e) {
            throw new IllegalStateException(AbstractDataFileHandler.READ_FILE_ERROR_MESSAGE, e);
        } finally {
            reader.close();
        }
    }

    private List<String> getSampleNamesFromGroupId(List<String> headers, DelimitedFileReader reader)
    throws IOException {
        final Set<String> nameSet = new HashSet<String>();
        final List<String> names = new ArrayList<String>();
        final int position = headers.indexOf(GROUP_ID_HEADER);
        while (reader.hasNextLine()) {
            final String groupId = reader.nextLine().get(position);
            if (!nameSet.contains(groupId)) {
                nameSet.add(groupId);
                names.add(groupId);
            }
        }
        return names;
    }

    private List<String> getSampleNamesFromHeaders(List<String> headers) {
        final Set<String> nameSet = new HashSet<String>();
        final List<String> names = new ArrayList<String>();
        for (final String header : headers) {
            final String[] parts = header.split("-");
            if (parts.length == 2 && !nameSet.contains(parts[1])) {
                nameSet.add(parts[1]);
                names.add(parts[1]);
            }
        }
        return names;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void loadData(DataSet dataSet, List<QuantitationType> types, ArrayDesign design) {
        final DelimitedFileReader reader = getReader(getFile());
        try {
            final List<String> headers = getHeaders(reader);
            loadData(headers, reader, dataSet, types, design);
        } catch (final IOException e) {
            throw new IllegalStateException(AbstractDataFileHandler.READ_FILE_ERROR_MESSAGE, e);
        } finally {
            reader.close();
        }
    }

    private void loadData(List<String> headers, DelimitedFileReader reader, DataSet dataSet,
            List<QuantitationType> types, ArrayDesign design) throws IOException {
        dataSet.prepareColumns(types, getNumberOfDataRows(reader));
        if (dataSet.getDesignElementList() == null) {
            loadDesignElementList(dataSet, reader, headers, design);
        }
        final Map<String, HybridizationData> hybridizationNameToDataMap = getHybridizationNameToDataMap(dataSet);
        final Set<QuantitationType> typeSet = new HashSet<QuantitationType>();
        typeSet.addAll(types);
        positionAtData(reader);
        int rowIndex = 0;
        while (reader.hasNextLine()) {
            final List<String> values = reader.nextLine();
            for (int i = 0; i < values.size(); i++) {
                loadValue(values.get(i), headers.get(i), dataSet, typeSet, hybridizationNameToDataMap, rowIndex);
            }
            rowIndex++;
        }
    }

    private void checkSdrfHybridizations(FileValidationResult result, List<String> fileHybNames,
            Map<String, List<Hybridization>> sdrfHybsMap) {
        // get collection of hyb names from sdrf as strings
        for (final List<Hybridization> hybList : sdrfHybsMap.values()) {
            final List<String> sdrfHybStrs = new ArrayList<String>();
            for (final Hybridization hyb : hybList) {
                sdrfHybStrs.add(hyb.getName());
            }

            if (!sdrfHybStrs.containsAll(fileHybNames)) {
                final StringBuilder sb =
                    new StringBuilder("This data file contains the following hybridization names"
                            + " that are not referenced in the SDRF document:");
                sdrfHybStrs.removeAll(fileHybNames);
                sb.append(StringUtils.join(sdrfHybStrs.iterator(), ','));
                result.addMessage(Type.ERROR, sb.toString());
            }
        }
    }

    private void loadDesignElementList(DataSet dataSet, DelimitedFileReader reader, List<String> headers,
            ArrayDesign design) throws IOException {
        final int indexOfTargetId = headers.indexOf(TARGET_ID);
        final DesignElementBuilder builder = new DesignElementBuilder(dataSet, design, this.arrayDao, this.searchDao);
        positionAtData(reader);
        while (reader.hasNextLine()) {
            final List<String> values = reader.nextLine();
            final String probeName = values.get(indexOfTargetId);
            builder.addProbe(probeName);
        }
        builder.finish();
    }

    @SuppressWarnings("PMD.ExcessiveParameterList")
    private void loadValue(String value, String header, DataSet dataSet, Set<QuantitationType> typeSet,
            Map<String, HybridizationData> hybridizationNameToDataMap, int rowIndex) {
        final String[] headerParts = header.split("-", 2);
        if (headerParts.length == 2) {
            final String typeHeader = headerParts[0];
            final String groupId = headerParts[1];
            final HybridizationData hybridizationData = hybridizationNameToDataMap.get(groupId);
            if (hybridizationData != null) {
                setValue(hybridizationData, typeHeader, value, typeSet, rowIndex);
            }
        }
    }

    private void setValue(HybridizationData hybridizationData, String typeHeader, String value,
            Set<QuantitationType> typeSet, int rowIndex) {
        final QuantitationTypeDescriptor typeDescriptor =
            IlluminaExpressionQuantitationType.valueOf(typeHeader.toUpperCase(Locale.getDefault()));
        final AbstractDataColumn column = getColumn(hybridizationData, typeDescriptor);
        if (typeSet.contains(column.getQuantitationType())) {
            this.valueParser.setValue(column, rowIndex, value);
        }
    }

    private AbstractDataColumn
    getColumn(HybridizationData hybridizationData, QuantitationTypeDescriptor typeDescriptor) {
        for (final AbstractDataColumn column : hybridizationData.getColumns()) {
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

    private Map<String, HybridizationData> getHybridizationNameToDataMap(DataSet dataset) {
        Map<String, HybridizationData> map = new HashMap<String, HybridizationData>();
        for (HybridizationData hybData : dataset.getHybridizationDataList()) {
            map.put(hybData.getHybridization().getName(), hybData);
        }
        return map;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void validate(MageTabDocumentSet mTabSet, FileValidationResult result, ArrayDesign design) {
        final DelimitedFileReader reader = getReader(getFile());
        try {
            validateHeaders(reader, result);
            final List<String> hybNames = getHybridizationNames();
            if (mTabSet.getSdrfDocuments() != null && !mTabSet.getSdrfDocuments().isEmpty()
                    && !mTabSet.hasPartialSdrf()) {
                checkSdrfHybridizations(result, hybNames, mTabSet.getSdrfHybridizations());
            }

            if (result.isValid()) {
                validateData(reader, result, design);
            }
            result.addValidationProperties(FileValidationResult.HYB_NAME, hybNames);
        } catch (final IOException e) {
            throw new IllegalStateException(AbstractDataFileHandler.READ_FILE_ERROR_MESSAGE, e);
        } finally {
            reader.close();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean requiresMageTab() {
        return false;
    }

    private void validateHeaders(DelimitedFileReader reader, FileValidationResult result) throws IOException {
        validateHasArrayContentHeader(reader, result);
        final List<String> headers = getHeaders(reader);
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
        final Map<String, String[]> fileHeaders = getFileHeaders(reader);
        final String[] arrayContentValues = fileHeaders.get(ARRAY_CONTENT_HEADER);
        if (arrayContentValues == null || arrayContentValues.length == 0
                || StringUtils.isEmpty(arrayContentValues[0])) {
            return null;
        } else {
            return arrayContentValues[0].trim();
        }
    }

    private Map<String, String[]> getFileHeaders(DelimitedFileReader reader) throws IOException {
        reset(reader);
        final Map<String, String[]> fileHeaders = new HashMap<String, String[]>();
        while (reader.hasNextLine()) {
            final List<String> values = reader.nextLine();
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
        final String[] parts = values.get(0).split("=");
        final String header = parts[0].trim();
        if (parts.length > 1) {
            final String[] headerValues = parts[1].split(",");
            fileHeaders.put(header, headerValues);
        }
    }

    private void validateData(final DelimitedFileReader reader, final FileValidationResult result,
            final ArrayDesign arrayDesign) throws IOException {
        final List<String> headers = getHeaders(reader);
        final int targetIdColumnIndex = headers.indexOf(TARGET_ID);
        positionAtData(reader);
        final ProbeNamesValidator probeNamesValidator = new ProbeNamesValidator(this.arrayDao, arrayDesign);
        final List<String> probeNamesBatch = new ArrayList<String>(BATCH_SIZE);
        int probeCounter = 0;
        while (reader.hasNextLine()) {
            final List<String> nextLineValues = reader.nextLine();
            if (nextLineValues.size() != headers.size()) {
                final ValidationMessage message = result.addMessage(Type.ERROR, "Invalid number of values in row");
                message.setLine(reader.getCurrentLineNumber());
            }
            probeNamesBatch.add(nextLineValues.get(targetIdColumnIndex));
            probeCounter++;
            if (0 == probeCounter % BATCH_SIZE) {
                probeNamesValidator.validateProbeNames(result, probeNamesBatch);
                probeNamesBatch.clear();
            }
        }
        if (!probeNamesBatch.isEmpty()) {
            probeNamesValidator.validateProbeNames(result, probeNamesBatch);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<LSID> getReferencedArrayDesignCandidateIds() {
        final DelimitedFileReader reader = getReader(getFile());
        try {
            final String illuminaFile = getArrayContentValue(reader);
            final String designName = FilenameUtils.getBaseName(illuminaFile);
            return Collections.singletonList(new LSID(LSID_AUTHORITY, LSID_NAMESPACE, designName));
        } catch (final IOException e) {
            throw new IllegalStateException(AbstractDataFileHandler.READ_FILE_ERROR_MESSAGE, e);
        } finally {
            reader.close();
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
