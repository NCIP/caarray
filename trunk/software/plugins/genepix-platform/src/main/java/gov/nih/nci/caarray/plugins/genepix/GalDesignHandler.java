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
public final class GalDesignHandler extends AbstractDesignFileHandler {
    private static final String LSID_AUTHORITY = AbstractCaArrayEntity.CAARRAY_LSID_AUTHORITY;
    private static final String LSID_NAMESPACE = AbstractCaArrayEntity.CAARRAY_LSID_NAMESPACE;

    private static final String BLOCK_HEADER = "Block";
    private static final String COLUMN_HEADER = "Column";
    private static final String ROW_HEADER = "Row";
    private static final String ID_HEADER = "ID";
    private static final List<String> REQUIRED_DATA_COLUMN_HEADERS = Arrays.asList(new String[] {BLOCK_HEADER,
            COLUMN_HEADER, ROW_HEADER, ID_HEADER });
    private static final short DEFAULT_BLOCK_ROW_NUM = 1;

    /**
     * File Type for Genepix GAL array design.
     */
    public static final FileType GAL_FILE_TYPE = new FileType("GENEPIX_GAL", FileCategory.ARRAY_DESIGN, true, "GAL");
    static final Set<FileType> SUPPORTED_TYPES = Sets.newHashSet(GAL_FILE_TYPE);

    private final Map<String, Integer> headerToPositionMap = new HashMap<String, Integer>();

    private CaArrayFile designFile;
    private File fileOnDisk;
    private DelimitedFileReader reader;

    /**
     * @param sessionTransactionManager {@link SessionTransactionManager} to use
     * @param dataStorageFacade {@link DataStorageFacade} to use
     * @param arrayDao {@link ArrayDao} to use
     * @param searchDao {@link SearchDao} to use
     */
    @Inject
    GalDesignHandler(SessionTransactionManager sessionTransactionManager, DataStorageFacade dataStorageFacade,
            ArrayDao arrayDao, SearchDao searchDao) {
        super(sessionTransactionManager, dataStorageFacade, arrayDao, searchDao);
    }

    /**
     * {@inheritDoc}
     */
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
    public boolean parsesData() {
         return true;
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
        }
    }

    private void addToDetails(ArrayDesignDetails details, ProbeGroup group, List<String> values) {
        final Feature feature = new Feature(details);
        feature.setBlockColumn(getBlockColumn(values));
        feature.setBlockRow(DEFAULT_BLOCK_ROW_NUM);
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
        return blockNumber;
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
            if (validateHeader(fileResult)) {
                validateDataRows(fileResult);
            }
        } catch (final IOException e) {
            result.addMessage(this.designFile.getName(), Type.ERROR, "Could not read file: " + e);
        }
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

    private boolean validateHeader(FileValidationResult result) throws IOException {
        this.reader.reset();
        if (!this.reader.hasNextLine()) {
            result.addMessage(Type.ERROR, "The GAL file is empty");
            return false;
        }
        List<String> values = this.reader.nextLine();
        if (values.size() < 2 || !"ATF".equals(values.get(0))) {
            result.addMessage(Type.ERROR, "The GAL file doesn't begin with the required header (ATF\t1.0).");
            return false;
        }
        while (this.reader.hasNextLine()) {
            values = this.reader.nextLine();
            if (!values.isEmpty() && (isDataHeaderLine(values))) {
                return true;
            }
        }
        result.addMessage(Type.ERROR, "The GAL file has no data header line of the format Block Row Column ID");
        return false;
    }
}
