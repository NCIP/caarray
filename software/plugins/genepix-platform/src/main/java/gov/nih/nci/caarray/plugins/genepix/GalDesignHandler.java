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
package gov.nih.nci.caarray.plugins.genepix;

import gov.nih.nci.caarray.application.util.Utils;
import gov.nih.nci.caarray.dao.ArrayDao;
import gov.nih.nci.caarray.dao.SearchDao;
import gov.nih.nci.caarray.dataStorage.DataStorageFacade;
import gov.nih.nci.caarray.domain.AbstractCaArrayEntity;
import gov.nih.nci.caarray.domain.array.ArrayDesign;
import gov.nih.nci.caarray.domain.array.ArrayDesignDetails;
import gov.nih.nci.caarray.domain.array.Feature;
import gov.nih.nci.caarray.domain.array.PhysicalProbe;
import gov.nih.nci.caarray.domain.array.ProbeGroup;
import gov.nih.nci.caarray.domain.file.CaArrayFile;
import gov.nih.nci.caarray.domain.file.FileCategory;
import gov.nih.nci.caarray.domain.file.FileType;
import gov.nih.nci.caarray.platforms.AbstractDesignFileHandler;
import gov.nih.nci.caarray.platforms.SessionTransactionManager;
import gov.nih.nci.caarray.platforms.spi.PlatformFileReadException;
import gov.nih.nci.caarray.validation.FileValidationResult;
import gov.nih.nci.caarray.validation.ValidationMessage;
import gov.nih.nci.caarray.validation.ValidationMessage.Type;
import gov.nih.nci.caarray.validation.ValidationResult;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;

import com.fiveamsolutions.nci.commons.util.io.DelimitedFileReader;
import com.fiveamsolutions.nci.commons.util.io.DelimitedFileReaderFactory;
import com.fiveamsolutions.nci.commons.util.io.DelimitedFilesModule;
import com.google.common.collect.Sets;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;

/**
 * Manages validation and loading of array designs described in the GenePix GAL format.
 */
@SuppressWarnings("PMD")
public final class GalDesignHandler extends AbstractDesignFileHandler {
    private static final String LSID_AUTHORITY = AbstractCaArrayEntity.CAARRAY_LSID_AUTHORITY;
    private static final String LSID_NAMESPACE = AbstractCaArrayEntity.CAARRAY_LSID_NAMESPACE;

    private static final int NUMBER_OF_BLOCK_INFORMATION_FIELDS = 7;
    private static final String BLOCK_HEADER = "Block";
    private static final String BLOCK_COUNT_HEADER = "BlockCount";
    private static final String BLOCK_TYPE_HEADER = "BlockType";
    private static final String COLUMN_HEADER = "Column";
    private static final String ROW_HEADER = "Row";
    private static final String ID_HEADER = "ID";
    private static final List<String> REQUIRED_DATA_COLUMN_HEADERS = Arrays.asList(new String[] {BLOCK_HEADER,
            COLUMN_HEADER, ROW_HEADER, ID_HEADER });
    private static final String BLOCK_INDICATOR = "Block";

    public static final FileType GAL_FILE_TYPE = new FileType("GENEPIX_GAL", FileCategory.ARRAY_DESIGN, true, "GAL");
    static final Set<FileType> SUPPORTED_TYPES = Sets.newHashSet(GAL_FILE_TYPE);

    private final Map<String, Integer> headerToPositionMap = new HashMap<String, Integer>();
    private final List<BlockInfo> blockInfos = new ArrayList<BlockInfo>();
    private final Map<Short, BlockInfo> numberToBlockMap = new HashMap<Short, BlockInfo>();

    private CaArrayFile designFile;
    private File fileOnDisk;
    private DelimitedFileReader reader;

    @Inject
    GalDesignHandler(SessionTransactionManager sessionTransactionManager, DataStorageFacade dataStorageFacade,
            ArrayDao arrayDao, SearchDao searchDao) {
        super(sessionTransactionManager, dataStorageFacade, arrayDao, searchDao);
    }

    @Override
    public boolean openFiles(Set<CaArrayFile> designFiles) throws PlatformFileReadException {
        if (designFiles == null || designFiles.size() != 1
                || !getSupportedTypes().contains(designFiles.iterator().next().getFileType())) {
            return false;
        }

        this.designFile = designFiles.iterator().next();
        this.fileOnDisk = getDataStorageFacade().openFile(this.designFile.getDataHandle(), false);
        try {
            final Injector injector = Guice.createInjector(new DelimitedFilesModule());
            final DelimitedFileReaderFactory readerFactory = injector.getInstance(DelimitedFileReaderFactory.class);
            this.reader = readerFactory.createTabDelimitedFileReader(this.fileOnDisk);
            return true;
        } catch (final IOException e) {
            throw new PlatformFileReadException(this.fileOnDisk, "Could not open reader for file "
                    + this.designFile.getName(), e);
        }
    }

    @Override
    public Set<FileType> getSupportedTypes() {
        return SUPPORTED_TYPES;
    }

    @Override
    public void closeFiles() {
        this.reader.close();
        getDataStorageFacade().releaseFile(this.designFile.getDataHandle(), false);
        this.reader = null;
        this.fileOnDisk = null;
        this.designFile = null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void createDesignDetails(ArrayDesign arrayDesign) throws PlatformFileReadException {
        final ArrayDesignDetails details = new ArrayDesignDetails();
        arrayDesign.setDesignDetails(details);

        try {
            final ProbeGroup group = new ProbeGroup(details);
            details.getProbeGroups().add(group);
            loadHeaderInformation();
            positionAtDataRecords();
            while (this.reader.hasNextLine()) {
                addToDetails(details, group, this.reader.nextLine());
            }
        } catch (final IOException e) {
            throw new PlatformFileReadException(this.fileOnDisk, "Couldn't read file", e);
        }

        getArrayDao().save(arrayDesign);
        getArrayDao().save(arrayDesign.getDesignDetails());
        getSessionTransactionManager().flushSession();
    }

    private void loadHeaderInformation() throws IOException {
        this.reader.reset();
        while (this.reader.hasNextLine()) {
            final List<String> values = this.reader.nextLine();
            if (isDataHeaderLine(values)) {
                loadHeaderToPositionMap(values);
                break;
            }
            handleHeaderLine(values);
        }
        setBlockColumnsAndRows();
    }

    private void setBlockColumnsAndRows() throws IOException {
        if (!this.blockInfos.isEmpty()) {
            setBlockInfoFromHeaderData();
        } else {
            setBlockInfoFromDataRows();
        }
    }

    private void setBlockInfoFromDataRows() throws IOException {
        positionAtDataRecords();
        short blockCount = 0;
        while (this.reader.hasNextLine()) {
            final short blockNumber = getBlockNumber(this.reader.nextLine());
            if (blockNumber > blockCount) {
                blockCount = blockNumber;
            }
        }
        for (short i = 1; i <= blockCount; i++) {
            final BlockInfo blockInfo = new BlockInfo(i, 0, 0);
            blockInfo.setBlockColumn(i);
            blockInfo.setBlockRow((short) 1);
            this.numberToBlockMap.put(i, blockInfo);
        }
    }

    private void setBlockInfoFromHeaderData() {
        short blockRow = 0;
        short blockColumn = 0;
        double currentYOrigin = -1;
        for (final BlockInfo blockInfo : this.blockInfos) {
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
        final String[] parts = record.split("=");
        final String name = parts[0].trim();
        final String value = parts[1].trim();
        if (isBlockInformationHeading(name)) {
            handleBlockInformation(name, value);
        }
    }

    private boolean isBlockInformationHeading(String name) {
        return name.startsWith(BLOCK_INDICATOR) && !BLOCK_COUNT_HEADER.equals(name) && !BLOCK_TYPE_HEADER.equals(name);
    }

    private void handleBlockInformation(String name, String value) {
        final String[] parts = value.split(",");
        final short blockNumber = Short.parseShort(name.substring(BLOCK_INDICATOR.length()));
        final double xOrigin = Double.parseDouble(parts[0].trim());
        final double yOrigin = Double.parseDouble(parts[1].trim());
        final BlockInfo blockInfo = new BlockInfo(blockNumber, xOrigin, yOrigin);
        this.blockInfos.add(blockInfo);
        this.numberToBlockMap.put(blockInfo.getBlockNumber(), blockInfo);
    }

    private void addToDetails(ArrayDesignDetails details, ProbeGroup group, List<String> values) {
        final Feature feature = new Feature(details);
        feature.setBlockColumn(getBlockColumn(values));
        feature.setBlockRow(getBlockRow(values));
        feature.setColumn(getColumn(values));
        feature.setRow(getRow(values));
        details.getFeatures().add(feature);
        final PhysicalProbe probe = new PhysicalProbe(details, group);
        probe.setName(getId(values));
        probe.getFeatures().add(feature);
        details.getProbes().add(probe);
    }

    private String getId(List<String> values) {
        return values.get(this.headerToPositionMap.get(ID_HEADER));
    }

    private short getBlockColumn(List<String> values) {
        final short blockNumber = getBlockNumber(values);
        return this.numberToBlockMap.get(blockNumber).getBlockColumn();
    }

    private short getBlockRow(List<String> values) {
        final short blockNumber = getBlockNumber(values);
        return this.numberToBlockMap.get(blockNumber).getBlockRow();
    }

    private short getBlockNumber(List<String> values) {
        return Short.parseShort(values.get(this.headerToPositionMap.get(BLOCK_HEADER)));
    }

    private short getColumn(List<String> values) {
        return Short.parseShort(values.get(this.headerToPositionMap.get(COLUMN_HEADER)));
    }

    private short getRow(List<String> values) {
        return Short.parseShort(values.get(this.headerToPositionMap.get(ROW_HEADER)));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void load(ArrayDesign arrayDesign) {
        arrayDesign.setName(FilenameUtils.getBaseName(this.designFile.getName()));
        arrayDesign.setLsidForEntity(LSID_AUTHORITY + ":" + LSID_NAMESPACE + ":" + arrayDesign.getName());
        try {
            arrayDesign.setNumberOfFeatures(getNumberOfFeatures());
        } catch (final IOException e) {
            throw new IllegalStateException("Couldn't read file", e);
        }
    }

    private int getNumberOfFeatures() throws IOException {
        positionAtDataRecords();
        int numberOfFeatures = 0;
        while (this.reader.hasNextLine()) {
            this.reader.nextLine();
            numberOfFeatures++;
        }
        return numberOfFeatures;
    }

    private void positionAtDataRecords() throws IOException {
        this.reader.reset();
        boolean isDataHeaderLine = false;
        List<String> values = null;
        while (this.reader.hasNextLine() && !isDataHeaderLine) {
            values = this.reader.nextLine();
            isDataHeaderLine = isDataHeaderLine(values);
        }
        if (!isDataHeaderLine) {
            throw new IllegalStateException("Invalid GAL file");
        }
    }

    private void loadHeaderToPositionMap(List<String> values) {
        for (int position = 0; position < values.size(); position++) {
            this.headerToPositionMap.put(values.get(position), position);
        }
    }

    private boolean isDataHeaderLine(List<String> values) {
        return values.containsAll(REQUIRED_DATA_COLUMN_HEADERS);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void validate(ValidationResult result) {
        final FileValidationResult fileResult = result.getOrCreateFileValidationResult(this.designFile.getName());
        this.designFile.setValidationResult(fileResult);

        try {
            validateFormat(fileResult);
            if (result.isValid()) {
                validateHeader(fileResult);
            }
            if (result.isValid()) {
                validateData(fileResult);
            }
        } catch (final IOException e) {
            result.addMessage(this.designFile.getName(), Type.ERROR, "Could not read file: " + e);
        }
    }

    private void validateFormat(FileValidationResult result) {
        validateFileNotEmpty(result);
    }

    private void validateDataRows(FileValidationResult result) throws IOException {
        this.reader.reset();
        int numberOfDataColumns = 0;
        while (this.reader.hasNextLine()) {
            final List<String> values = this.reader.nextLine();
            if (isDataHeaderLine(values)) {
                numberOfDataColumns = values.size();
                loadHeaderToPositionMap(values);
                break;
            }
        }
        while (this.reader.hasNextLine()) {
            final List<String> values = this.reader.nextLine();
            if (values.size() != numberOfDataColumns) {
                final ValidationMessage message =
                        result.addMessage(Type.ERROR, "Line " + this.reader.getCurrentLineNumber()
                                + " has an incorrect number of columns");
                message.setLine(this.reader.getCurrentLineNumber());
            } else {
                validateDataValues(values, result, this.reader.getCurrentLineNumber());
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
        final int column = this.headerToPositionMap.get(header);
        if (!Utils.isShort(values.get(column))) {
            final ValidationMessage message =
                    result.addMessage(Type.ERROR, "Illegal (non-numeric) value for field " + header + " on line "
                            + line);
            message.setLine(line);
            message.setColumn(column);
        }
    }

    private void validateColumn(List<String> values, FileValidationResult result, int line) {
        validateShortField(values, COLUMN_HEADER, result, line);
    }

    private void validateRow(List<String> values, FileValidationResult result, int line) {
        validateShortField(values, ROW_HEADER, result, line);
    }

    private void validateId(List<String> values, FileValidationResult result, int line) {
        final int column = this.headerToPositionMap.get(ID_HEADER);
        if (StringUtils.isBlank(values.get(column))) {
            final ValidationMessage message =
                    result.addMessage(Type.ERROR, "Missing value for ID field on line " + line);
            message.setLine(line);
            message.setColumn(column);
        }
    }

    private void validateFileHasRowHeader(FileValidationResult result) throws IOException {
        this.reader.reset();
        while (this.reader.hasNextLine()) {
            if (isDataHeaderLine(this.reader.nextLine())) {
                return;
            }
        }
        result.addMessage(Type.ERROR, "The GAL file has no row header line");
    }

    private void validateFileNotEmpty(FileValidationResult result) {
        if (!this.reader.hasNextLine()) {
            result.addMessage(Type.ERROR, "The GAL file is empty");
        }
    }

    private void validateHeader(FileValidationResult result) throws IOException {
        this.reader.reset();
        validateFormatHeader(result);
        if (result.isValid()) {
            validateFileHasRowHeader(result);
        }
        if (result.isValid()) {
            validateHeaderFields(result);
        }
        if (result.isValid()) {
            validateBlockInformation(result);
        }
    }

    private void validateFormatHeader(FileValidationResult result) throws IOException {
        this.reader.reset();
        List<String> values = this.reader.nextLine();
        if (values.size() < 2 || !"ATF".equals(values.get(0))) {
            result.addMessage(Type.ERROR, "The GAL file doesn't begin with the required header (ATF\t1.0).");
            return;
        }
        if (!this.reader.hasNextLine()) {
            result.addMessage(Type.ERROR, "The file is only one line long.");
            return;
        }
        values = this.reader.nextLine();
        if (values.size() < 2) {
            result.addMessage(Type.ERROR, "The second line of the GAL file must contain two numeric fields.");
            return;
        }
    }

    private void validateHeaderFields(FileValidationResult result) throws IOException {
        this.reader.reset();
        this.reader.nextLine();
        this.reader.nextLine();
        while (this.reader.hasNextLine()) {
            final List<String> values = this.reader.nextLine();
            if (isDataHeaderLine(values)) {
                return;
            }
            if (!values.get(0).contains("=")) {
                final ValidationMessage message =
                        result.addMessage(Type.ERROR,
                                "Illegal header line; headers must be of the format <name>=<value>");
                message.setLine(this.reader.getCurrentLineNumber());
            }
        }
    }

    private void validateBlockInformation(FileValidationResult result) throws IOException {
        this.reader.reset();
        while (this.reader.hasNextLine()) {
            final List<String> values = this.reader.nextLine();
            if (isDataHeaderLine(values)) {
                return;
            }
            final String name = values.get(0).split("=")[0];
            if (!values.isEmpty() && isBlockInformationHeading(name)) {
                validateBlockInformation(values.get(0), this.reader.getCurrentLineNumber(), result);
            }
        }
    }

    private void validateBlockInformation(String headerField, int line, FileValidationResult result) {
        final String[] parts = headerField.split("=");
        final String name = parts[0];
        final String information = parts[1];
        if (!Utils.isShort(name.substring(BLOCK_INDICATOR.length()))) {
            result.addMessage(Type.ERROR, "Illegal block header name: " + name);
            return;
        }
        validateBlockInformationFields(information.split(","), line, result);
    }

    private void validateBlockInformationFields(String[] values, int line, FileValidationResult result) {
        if (values.length != NUMBER_OF_BLOCK_INFORMATION_FIELDS) {
            final ValidationMessage message =
                    result.addMessage(Type.ERROR,
                            "Block information must consist of exactly 7 comma-separated numeric values");
            message.setLine(line);
            message.setColumn(1);
        }
        if (!Utils.isDouble(values[0].trim())) {
            final ValidationMessage message =
                    result.addMessage(Type.ERROR, "Block information must contain a valid xOrigin value");
            message.setLine(line);
            message.setColumn(1);
        }
        if (!Utils.isDouble(values[1].trim())) {
            final ValidationMessage message =
                    result.addMessage(Type.ERROR, "Block information must contain a valid yOrigin value");
            message.setLine(line);
            message.setColumn(2);
        }
    }

    private void validateData(FileValidationResult result) throws IOException {
        validateDataRows(result);
    }

    /**
     * Used to record location information for Genepix design blocks.
     */
    private static final class BlockInfo {
        private final short blockNumber;
        private short blockColumn;
        private short blockRow;
        private final double xOrigin;
        private final double yOrigin;

        private BlockInfo(short blockNumber, double xOrigin, double yOrigin) {
            this.blockNumber = blockNumber;
            this.xOrigin = xOrigin;
            this.yOrigin = yOrigin;
        }

        private short getBlockColumn() {
            return this.blockColumn;
        }

        private short getBlockNumber() {
            return this.blockNumber;
        }

        private short getBlockRow() {
            return this.blockRow;
        }

        @SuppressWarnings("unused")
        private double getXOrigin() {
            return this.xOrigin;
        }

        private double getYOrigin() {
            return this.yOrigin;
        }

        private void setBlockColumn(short blockColumn) {
            this.blockColumn = blockColumn;
        }

        private void setBlockRow(short blockRow) {
            this.blockRow = blockRow;
        }

    }

}
