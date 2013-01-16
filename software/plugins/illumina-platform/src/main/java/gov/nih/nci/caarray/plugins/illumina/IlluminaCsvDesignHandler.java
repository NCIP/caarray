//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.plugins.illumina;

import gov.nih.nci.caarray.application.util.Utils;
import gov.nih.nci.caarray.dao.ArrayDao;
import gov.nih.nci.caarray.dao.SearchDao;
import gov.nih.nci.caarray.dataStorage.DataStorageFacade;
import gov.nih.nci.caarray.domain.array.ArrayDesign;
import gov.nih.nci.caarray.domain.array.ArrayDesignDetails;
import gov.nih.nci.caarray.domain.file.CaArrayFile;
import gov.nih.nci.caarray.domain.file.FileCategory;
import gov.nih.nci.caarray.domain.file.FileType;
import gov.nih.nci.caarray.platforms.AbstractDesignFileHandler;
import gov.nih.nci.caarray.platforms.SessionTransactionManager;
import gov.nih.nci.caarray.platforms.spi.PlatformFileReadException;
import gov.nih.nci.caarray.validation.FileValidationResult;
import gov.nih.nci.caarray.validation.ValidationMessage;
import gov.nih.nci.caarray.validation.ValidationResult;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.FilenameUtils;

import com.fiveamsolutions.nci.commons.util.io.DelimitedFileReader;
import com.fiveamsolutions.nci.commons.util.io.DelimitedFileReaderFactoryImpl;
import com.google.common.collect.Sets;
import com.google.inject.Inject;

/**
 * Reads Illumina genotyping and gene expression array description files.
 */
public final class IlluminaCsvDesignHandler extends AbstractDesignFileHandler {
    static final String LSID_AUTHORITY = "illumina.com";
    static final String LSID_NAMESPACE = "PhysicalArrayDesign";
    private static final int LOGICAL_PROBE_BATCH_SIZE = 1000;

    /**
     * File Type for illumina CSV array design.
     */
    public static final FileType DESIGN_CSV_FILE_TYPE = new FileType("ILLUMINA_DESIGN_CSV", FileCategory.ARRAY_DESIGN,
            true, "CSV");
    static final Set<FileType> SUPPORTED_TYPES = Sets.newHashSet(DESIGN_CSV_FILE_TYPE);

    private CaArrayFile designFile;
    private File fileOnDisk;
    private DelimitedFileReader reader;
    private AbstractCsvDesignHelper helper;

    /**
     * 
     */
    @Inject
    IlluminaCsvDesignHandler(SessionTransactionManager sessionTransactionManager, DataStorageFacade dataStorageFacade,
            ArrayDao arrayDao, SearchDao searchDao) {
        super(sessionTransactionManager, dataStorageFacade, arrayDao, searchDao);
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
    public boolean openFiles(Set<CaArrayFile> designFiles) throws PlatformFileReadException {
        if (designFiles == null || designFiles.size() != 1
                || !SUPPORTED_TYPES.contains(designFiles.iterator().next().getFileType())) {
            return false;
        }

        this.designFile = designFiles.iterator().next();
        this.fileOnDisk = getDataStorageFacade().openFile(this.designFile.getDataHandle(), false);
        try {
            this.reader = new DelimitedFileReaderFactoryImpl().createCommaDelimitedFileReader(this.fileOnDisk);
            this.helper = createHelper();
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
    public void closeFiles() {
        if (this.reader != null) {
            this.reader.close();
            this.reader = null;
        }
        this.helper = null;
        this.fileOnDisk = null;
        if (this.designFile != null) {
            getDataStorageFacade().releaseFile(this.designFile.getDataHandle(), false);
        }
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
    public void createDesignDetails(ArrayDesign arrayDesign) {
        try {
            positionAtAnnotation();
            final ArrayDesignDetails details = new ArrayDesignDetails();
            arrayDesign.setDesignDetails(details);
            getArrayDao().save(arrayDesign);
            getArrayDao().flushSession();
            int count = 0;
            while (this.reader.hasNextLine()) {
                final List<String> values = this.reader.nextLine();
                if (this.helper.isLineFollowingAnnotation(values)) {
                    break;
                }
                getArrayDao().save(this.helper.createProbe(details, values));
                if (++count % LOGICAL_PROBE_BATCH_SIZE == 0) {
                    flushAndClearSession();
                }
            }
            flushAndClearSession();
        } catch (final IOException e) {
            throw new IllegalStateException("Couldn't read file: ", e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void validate(ValidationResult result) {
        try {
            final FileValidationResult fileResult = result.getOrCreateFileValidationResult(this.designFile.getName());
            this.designFile.setValidationResult(fileResult);

            this.reader.reset();
            if (!this.reader.hasNextLine()) {
                fileResult.addMessage(ValidationMessage.Type.ERROR, "Illumina CSV file was empty");
            }
            final List<String> headers = getHeaders();
            validateHeader(headers, fileResult);
            if (result.isValid()) {
                validateContent(fileResult, headers);
            }
        } catch (final IOException e) {
            result.addMessage(this.designFile.getName(), ValidationMessage.Type.ERROR, "Unable to read file");
        }
    }

    private void validateContent(FileValidationResult result, List<String> headers) throws IOException {
        while (this.reader.hasNextLine()) {
            final List<String> values = this.reader.nextLine();
            if (this.helper.isLineFollowingAnnotation(values)) {
                break;
            }
            if (values.size() != headers.size()) {
                final ValidationMessage error = result.addMessage(ValidationMessage.Type.ERROR,
                        "Invalid number of fields. Expected " + headers.size() + " but contained " + values.size());
                error.setLine(this.reader.getCurrentLineNumber());
            }
            this.helper.validateValues(values, result, this.reader.getCurrentLineNumber());
        }
    }

    void validateFieldLength(List<String> values, Enum header, FileValidationResult result, int lineNumber,
            int expectedLength) throws IOException {
        final int colIdx = this.helper.indexOf(header);
        final String val = values.get(colIdx);
        validateFieldLength(val, header, result, lineNumber, expectedLength, colIdx + 1);
    }

    @SuppressWarnings("PMD.ExcessiveParameterList")
    static void validateFieldLength(String value, Enum header, FileValidationResult result, int lineNumber,
            int expectedLength, int col) {
        if (value.length() != expectedLength) {
            final ValidationMessage error = result.addMessage(
                    ValidationMessage.Type.ERROR,
                    "Expected size of field for " + header.name() + " to be " + expectedLength + " but was "
                    + value.length());
            error.setLine(lineNumber);
            error.setColumn(col);
        }
    }

    void validateIntegerField(List<String> values, Enum[] headers, Enum header, FileValidationResult result,
            int lineNumber) throws IOException {
        final int colIdx = this.helper.indexOf(header);
        final String val = values.get(colIdx);
        validateIntegerField(val, header, result, lineNumber, colIdx + 1);
    }

    static void validateIntegerField(String value, Enum header, FileValidationResult result, int lineNumber, int col) {
        if (!Utils.isInteger(value)) {
            final ValidationMessage error = result.addMessage(ValidationMessage.Type.ERROR,
                    "Expected integer value for " + header.name() + ", but was " + value);
            error.setLine(lineNumber);
            error.setColumn(col);
        }
    }

    void validateLongField(List<String> values, Enum header, FileValidationResult result, int lineNumber)
    throws IOException {
        final int colIdx = this.helper.indexOf(header);
        final String val = values.get(colIdx);
        validateLongField(val, header, result, lineNumber, colIdx + 1);
    }

    static void validateLongField(String value, Enum header, FileValidationResult result, int lineNumber, int col) {
        if (!Utils.isLong(value)) {
            final ValidationMessage error = result.addMessage(ValidationMessage.Type.ERROR,
                    "Expected long integral value for " + header.name() + ", but was " + value);
            error.setLine(lineNumber);
            error.setColumn(col);
        }
    }

    private void validateHeader(List<String> headers, FileValidationResult result) throws IOException {
        final Set<? extends Enum> requiredHeaders = this.helper.getRequiredColumns();
        final Set<Enum> tmp = new HashSet<Enum>(requiredHeaders);
        for (final String v : headers) {
            for (final Enum h : requiredHeaders) {
                if (h.name().equalsIgnoreCase(v)) {
                    tmp.remove(h);
                }
            }
        }
        if (!tmp.isEmpty()) {
            result.addMessage(ValidationMessage.Type.ERROR, "Illumina CSV file didn't contain the expected columns "
                    + tmp.toString());
        }
    }

    private List<String> getHeaders() throws IOException {
        while (this.reader.hasNextLine()) {
            final List<String> values = this.reader.nextLine();
            if (this.helper.isHeaderLine(values)) {
                this.helper.initHeaderIndex(values);
                return values;
            }
        }
        return null;
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
            throw new IllegalStateException("Couldn't read file: ", e);
        }
    }

    private int getNumberOfFeatures() throws IOException {
        positionAtAnnotation();
        int numberOfFeatures = 0;
        while (this.reader.hasNextLine()) {
            if (this.helper.isLineFollowingAnnotation(this.reader.nextLine())) {
                break;
            }
            numberOfFeatures++;
        }
        return numberOfFeatures;
    }

    private void positionAtAnnotation() throws IOException {
        reset();
        while (this.reader.hasNextLine()) {
            final List<String> line = this.reader.nextLine();
            if (this.helper.isHeaderLine(line)) {
                this.helper.initHeaderIndex(line);
                return;
            }
        }
    }

    private void reset() {
        try {
            this.reader.reset();
        } catch (final IOException e) {
            throw new IllegalStateException("Couldn't reset file " + this.designFile.getName(), e);
        }
    }

    private AbstractCsvDesignHelper createHelper() throws IOException {
        final List<AbstractCsvDesignHelper> candidateHandlers = getCandidateHandlers();
        while (this.reader.hasNextLine()) {
            final List<String> values = this.reader.nextLine();
            for (final AbstractCsvDesignHelper candidateHandler : candidateHandlers) {
                if (candidateHandler.isHeaderLine(values)) {
                    return candidateHandler;
                }
            }
        }
        throw new IOException("Could not find helper for this CSV design; the headers were wrong");
    }

    private List<AbstractCsvDesignHelper> getCandidateHandlers() {
        final List<AbstractCsvDesignHelper> handlers = new ArrayList<AbstractCsvDesignHelper>();
        handlers.add(new ExpressionCsvDesignHelper());
        handlers.add(new GenotypingCsvDesignHandler());
        return handlers;
    }
}
