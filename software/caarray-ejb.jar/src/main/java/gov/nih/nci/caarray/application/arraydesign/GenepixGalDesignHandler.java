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
package gov.nih.nci.caarray.application.arraydesign;

import gov.nih.nci.caarray.business.vocabulary.VocabularyService;
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
