//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
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
